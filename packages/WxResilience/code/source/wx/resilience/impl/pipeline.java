package wx.resilience.impl;

// -----( IS Java Code Template v1.2

import com.wm.data.*;
import com.wm.util.Values;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
// --- <<IS-START-IMPORTS>> ---
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




	public static final void createPipelineFileName (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(createPipelineFileName)>> ---
		// @sigtype java 3.5
		// [i] field:0:required fileNamePrefix
		// [i] field:0:optional fileNameSuffix
		// [i] field:0:required serviceName
		// [i] field:0:optional addTimestamp
		// [o] field:0:required pipelineFileName
		// Input parameters
		final IDataCursor crsr = pipeline.getCursor();
		final String subFolder = IDataUtil.getString(crsr, "subFolder");
		final String fileNameSuffix = IDataUtil.getString(crsr, "fileNameSuffix");
		final String serviceName = IDataUtil.getString(crsr, "serviceName");
		if(serviceName == null) {
			throw new NullPointerException("Missing parameter: serviceName");
		}
		if(serviceName.length() == 0) {
			throw new IllegalArgumentException("Empty parameter: serviceName");
		}
		final boolean addTimestamp = IDataUtil.getBoolean(crsr, "addTimestamp", false);
		
		// Create the file name.
		final StringBuilder sb = new StringBuilder();
		sb.append("pipeline/");
		if (subFolder != null) {
			sb.append(subFolder);
			if (subFolder.length() > 0  &&  !subFolder.endsWith("/")) {
				sb.append('/');
			}
		}
		sb.append(serviceName.replace(':', '.'));
		if (fileNameSuffix != null  &&  fileNameSuffix.length() > 0) {
			sb.append('-');
			sb.append(fileNameSuffix);
		}
		if (addTimestamp) {
			long l = System.currentTimeMillis();
			synchronized(previousTimestampLock) {
				if (previousTimestamp == 0  ||  previousTimestamp < l) {
					previousTimestamp = l;
				} else {
					l = previousTimestamp++;
				}
				Instant instant = Instant.ofEpochMilli(l);
				LocalDateTime ldt = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
				sb.append('-');
				sb.append(timestampFormatter.format(ldt));
			}
		}
		sb.append(".xml");
		IDataUtil.put(crsr, "pipelineFileName", sb.toString());
		
			
			
		// --- <<IS-END>> ---

                
	}



	public static final void savePipelineToFile (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(savePipelineToFile)>> ---
		// @sigtype java 3.5
		// [i] field:0:required pipelineFileName
		// [i] field:1:optional ignoredKeys
		final IDataCursor crsr = pipeline.getCursor();
		final String pipelineFileName = IDataUtil.getString(crsr, "pipelineFileName");
		if (pipelineFileName == null) {
			throw new NullPointerException("Missing parameter: pipelineFileName");
		}
		if (pipelineFileName.length() == 0) {
			throw new NullPointerException("Empty parameter: pipelineFileName");
		}
		String[] ignoredKeys = IDataUtil.getStringArray(crsr, "ignoredKeys");
		final Map<String,Object> map = new HashMap<>();
		final List<String> keys = new ArrayList<>();
		if (ignoredKeys != null) {
			keys.addAll(Arrays.asList(ignoredKeys));
		}
		keys.add("pipelineFileName");
		keys.add("ignoredKeys");
		for (String s : keys) {
			map.put(s, IDataUtil.get(crsr, s));
			IDataUtil.remove(crsr, s);
		}
		final Path path = Paths.get(pipelineFileName);
		final Path dir = path.getParent();
		try {
			Files.createDirectories(dir);
			try (OutputStream os = Files.newOutputStream(path);
				 BufferedOutputStream bos = new BufferedOutputStream(os)) {
				new IDataXMLCoder().encode(bos, pipeline);
			}
		} catch (IOException e) {
			throw new ServiceException(e);
		}
		// Restore the pipeline contents
		for (Map.Entry<String,Object> en : map.entrySet()) {
			final Object v = en.getValue();
			if (v != null) {
				IDataUtil.put(crsr, en.getKey(), v);
			}
		}			
		// --- <<IS-END>> ---

                
	}

	// --- <<IS-START-SHARED>> ---
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
			if (object instanceof Hashtable) {
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
	
	public static void log(String message) {
		// input
		IData input = IDataFactory.create();
		IDataCursor inputCursor = input.getCursor();
		IDataUtil.put(inputCursor, "message", message);
		inputCursor.destroy();
	
		try {
			Service.doInvoke("pub.flow", "debugLog", input);
		} catch (Exception e) {
		}
	}	
		
		
		
	// --- <<IS-END-SHARED>> ---
}

