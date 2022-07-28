package wx.resilience.pub.util;

// -----( IS Java Code Template v1.2

import com.wm.data.*;
import com.wm.util.Values;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
// --- <<IS-START-IMPORTS>> ---
import com.wm.util.JournalLogger;
import com.wm.util.coder.IDataXMLCoder;
import com.wm.app.b2b.server.InvokeState;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import javax.print.attribute.standard.Severity;
import com.wm.lang.xml.Node;
import com.softwareag.util.IDataMap;
// --- <<IS-END-IMPORTS>> ---

public final class pipeline

{
	// ---( internal utility methods )---

	final static pipeline _instance = new pipeline();

	static pipeline _newInstance() { return new pipeline(); }

	static pipeline _cast(Object o) { return (pipeline)o; }

	// ---( server methods )---




	public static final void duplicateObjectsAsDocs (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(duplicateObjectsAsDocs)>> ---
		// @sigtype java 3.5
		duplicateObjectsAsDocsImpl(pipeline);	
			
		// --- <<IS-END>> ---

                
	}



	public static final void replicateObjectsFromDocs (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(replicateObjectsFromDocs)>> ---
		// @sigtype java 3.5
		replicateObjectsFromDocsImpl(pipeline);
			
		// --- <<IS-END>> ---

                
	}

	// --- <<IS-START-SHARED>> ---
	public static final String MAPMESSAGE = "@MAPMESSAGE_AS_DOC@:";
	public static final String HASHTABLE = "@HASHTABLE_AS_DOC@:";
	public static final String STREAM = "@STREAM_AS_DOC@:";
	public static final String BYTES = "@BYTES_AS_DOC@:";
	public static final String NODE = "@NODE_AS_DOC@:";
	public static final String ARRAYLIST = "@ARRAYLIST_AS_DOC@:";
	public static final String ENTRIES = "entries";
	public static final String ENTRY = "entry";
	public static final String VALUE = "value";
	private static final String KEY = "key";
	private static final String UTF_8 = "UTF-8";
	
	// Do not access previousTimestamp without synchronization on timeStampLock!
	private static long previousTimestamp;
	private static final Object previousTimestampLock = new Object();
	private static final DateTimeFormatter timestampFormatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss.SSS");
	
	@SuppressWarnings({ "rawtypes", "unchecked", "resource" })
	public static IData duplicateObjectsAsDocsImpl(IData iData) {
		
		IDataMap iDataMap = new IDataMap(iData);
		
		Set<String> keySet = iDataMap.keySet();
		for (String key : keySet) {
			Object object = iDataMap.get(key);
			
			/*if (object instanceof MapMessage) {
				debugLogInfo(object.getClass().getCanonicalName());
				MapMessage mapMessage = (MapMessage) object;
				try {
					Enumeration names = mapMessage.getMapNames();
					IData mapMessageDoc = IDataFactory.create();
					IDataMap mapMessageDocMap = new IDataMap(mapMessageDoc);
					while (names.hasMoreElements()) {
						String enumKey = names.nextElement().toString();
						Object value = mapMessage.getObject(enumKey);
						mapMessageDocMap.put(enumKey, value);
					}
					iDataMap.put(MAPMESSAGE + key, mapMessageDoc);
					// Go on like a normal IData
					duplicateObjectsAsDocsImpl(mapMessageDoc);
				} catch (JMSException e) {
				}
			} else*/ if (object instanceof Hashtable) {
				IData hashtableDoc = IDataFactory.create();
				IDataMap hashtableMap = new IDataMap(hashtableDoc);
				Hashtable<String, Object> hashTable = (Hashtable<String, Object>) object;
				Set hashKeys = hashTable.keySet();
				IData[]	entries = new IData[hashKeys.size()];
				int i = 0;
				for (Object hashKeyObject : hashKeys) {
					String hashKey = (String) hashKeyObject;
					Object hashObject = hashTable.get(hashKey);
					entries[i] = IDataFactory.create();
					IDataMap entriyMap = new IDataMap(entries[i]);
					entriyMap.put(KEY, hashKey);
					entriyMap.put(VALUE, hashObject);
					i++;
				}
				hashtableMap.put(ENTRIES, entries);
				iDataMap.put(HASHTABLE + key, hashtableDoc);
			} else if (object instanceof InputStream) {
				InputStream stream = (InputStream) object;
				final int arraySize = 1024;
				byte[] byteArray = new byte[arraySize];
				StringBuilder builder = new StringBuilder();
				try {
					int red = 0;
					while((red = stream.read(byteArray)) > 0) {
						if (red == arraySize) {
							builder.append(new String(byteArray, UTF_8));
						} else {
							builder.append(new String(Arrays.copyOf(byteArray, red)));
						}
					}
				} catch (IOException e) {
				}
				
				iDataMap.put(STREAM + key, builder.toString());
				try {
					stream.reset();
				} catch (IOException e) {}
			} else if (object instanceof ArrayList) {
				ArrayList arrayList = (ArrayList) object;
				IData arrayListDoc = IDataFactory.create();
				IDataMap arrayListMap = new IDataMap(arrayListDoc);
				IData[]	entries = new IData[arrayList.size()];
				for (int i = 0; i < arrayList.size(); i++) {
					Object arrayListObject = arrayList.get(i);
					if (arrayListObject instanceof IData) {
						entries[i] = (IData) arrayListObject;
					}
				}
				arrayListMap.put(ENTRIES, entries);
				iDataMap.put(ARRAYLIST + key, arrayListDoc);
			} else if (object instanceof byte[]) {
				try {
					iDataMap.put(BYTES + key, new String((byte[]) object, UTF_8));
				} catch (UnsupportedEncodingException e) {
				}
			} else if (object instanceof Node) {
				iDataMap.put(NODE + key, nodeToDocument((Node) object));
			} else if (object instanceof IData) {
				duplicateObjectsAsDocsImpl((IData) object);
			} else if (object instanceof IData[]) {
				for(IData iDataOfArray : (IData[]) object) {
					duplicateObjectsAsDocsImpl(iDataOfArray);
				}
			}
		}
	
		return iData;
	}
	
	public static IData replicateObjectsFromDocsImpl(IData iData) {
		
		IDataMap iDataMap = new IDataMap(iData);
		
		Set<String> keySet = iDataMap.keySet();
		for (String key : keySet) {
			if (key.startsWith(HASHTABLE)) {
				IData hashtableDoc = (IData) iDataMap.get(key);
				IDataMap hashtableMap = new IDataMap(hashtableDoc);
				IData[] entries = hashtableMap.getAsIDataArray(ENTRIES);
				Hashtable<String, Object> hashtable = new Hashtable<String, Object>(entries.length);
				for (IData entry : entries) {
					IDataMap entryMap = new IDataMap(entry);
					String hashKey = entryMap.getAsString(KEY);
					Object valueObject = entryMap.get(VALUE);
					hashtable.put(hashKey, valueObject);
				}
				iDataMap.put(key.substring(HASHTABLE.length()), hashtable);
			} else if (key.startsWith(STREAM)) {
				String streamString = iDataMap.getAsString(key);
	
				// input
				IData input = IDataFactory.create();
				IDataMap inputMap = new IDataMap(input);
				inputMap.put("string", streamString);
				inputMap.put("encoding", UTF_8);
	
				// output
				IData output = IDataFactory.create();
				try{
					output = Service.doInvoke("pub.io", "stringToStream", input);
				} catch(Exception e){}
				IDataMap outputMap = new IDataMap(output);
				Object inputStream = outputMap.get("inputStream" );
	
				iDataMap.put(key.substring(STREAM.length()), inputStream);
			} else if (key.startsWith(ARRAYLIST)) {
				IData arrayListDoc = (IData) iDataMap.get(key);
				IDataMap arrayListMap = new IDataMap(arrayListDoc);
				IData[] entries = arrayListMap.getAsIDataArray(ENTRIES);
				ArrayList<IData> arrayList = new ArrayList<IData>(entries.length);
				for (IData entry : entries) {
					arrayList.add(entry);
				}
				iDataMap.put(key.substring(ARRAYLIST.length()), arrayList);
			} else if (key.startsWith(BYTES)) {
				String bytesString = iDataMap.getAsString(key);
				try {
					iDataMap.put(key.substring(BYTES.length()), bytesString.getBytes(UTF_8));
				} catch (UnsupportedEncodingException e) {
				}
			} else if (key.startsWith(NODE)) {
				IData nodeDoc = iDataMap.getAsIData(key);
				xmlStringToNode(documentToXmlString(nodeDoc));
				iDataMap.put(key.substring(NODE.length()), xmlStringToNode(documentToXmlString(nodeDoc)));
			} else if (iDataMap.get(key) instanceof IData) {
				replicateObjectsFromDocsImpl((IData) iDataMap.get(key));
			} else if (iDataMap.get(key) instanceof IData[]) {
				for(IData iDataOfArray : (IData[]) iDataMap.get(key)) {
					replicateObjectsFromDocsImpl(iDataOfArray);
				}
			}
		}
	
		return iData;
	}
	
	private static IData nodeToDocument(Node node) {
	
		// input
		IData input = IDataFactory.create();
		IDataCursor inputCursor = input.getCursor();
		IDataUtil.put( inputCursor, "node", node );
	
		// output
		IData 	output = IDataFactory.create();
		try{
			output = Service.doInvoke( "pub.xml", "xmlNodeToDocument", input );
		}catch( Exception e){}
		IDataCursor outputCursor = output.getCursor();
	
		// document
		IData	document = IDataUtil.getIData( outputCursor, "document" );
		outputCursor.destroy();
		
		return document;
	
	}
	
	private static String documentToXmlString(IData idata) {
	
		// input
		IData input = IDataFactory.create();
		IDataCursor inputCursor = input.getCursor();
	
		// document
		IDataUtil.put( inputCursor, "document", idata );
	
		// output
		IData 	output = IDataFactory.create();
		try{
			output = Service.doInvoke( "pub.xml", "documentToXMLString", input );
		}catch( Exception e){}
		IDataCursor outputCursor = output.getCursor();
			String	xmldata = IDataUtil.getString( outputCursor, "xmldata" );
		outputCursor.destroy();
	
		return xmldata;
	}
	
	private static Node xmlStringToNode(String xmlString) {
	
		// input
		IData input = IDataFactory.create();
		IDataCursor inputCursor = input.getCursor();
		IDataUtil.put( inputCursor, "xmldata", xmlString );
	
		// output
		IData 	output = IDataFactory.create();
		try{
			output = Service.doInvoke( "pub.xml", "xmlStringToXMLNode", input );
		}catch( Exception e){}
		IDataCursor outputCursor = output.getCursor();
			Object	node = IDataUtil.get( outputCursor, "node" );
		outputCursor.destroy();
		
		return (Node) node;
	}
	
	private static final String LOG_FUNCTION = "WxResilience";
	private static void debugLogError(String message) {
	    JournalLogger.log(4,  JournalLogger.FAC_FLOW_SVC, JournalLogger.ERROR, LOG_FUNCTION, message);
	}
	
	private static void debugLogInfo(String message) {
	    JournalLogger.log(4,  JournalLogger.FAC_FLOW_SVC, JournalLogger.INFO, LOG_FUNCTION, message);
	}
	
		
		
		
		
	// --- <<IS-END-SHARED>> ---
}

