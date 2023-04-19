package wx.resilience.impl.log;

// -----( IS Java Code Template v1.2

import com.wm.data.*;
import com.wm.util.Values;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
// --- <<IS-START-IMPORTS>> ---
import com.wm.util.JournalLogger;
import com.softwareag.is.log.Log;
import com.softwareag.util.IDataMap;
import com.wm.app.b2b.server.ISRuntimeException;
// --- <<IS-END-IMPORTS>> ---

public final class wrapper

{
	// ---( internal utility methods )---

	final static wrapper _instance = new wrapper();

	static wrapper _newInstance() { return new wrapper(); }

	static wrapper _cast(Object o) { return (wrapper)o; }

	// ---( server methods )---




	public static final void addPrefixAndSuffix (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(addPrefixAndSuffix)>> ---
		// @sigtype java 3.5
		// [i] recref:0:required wxMetaData wx.resilience.doc:wxMetaData
		// [i] field:0:required message
		// [o] field:0:required message
		addPrefixAndSuffixImpl(new IDataMap(pipeline));
		// --- <<IS-END>> ---

                
	}



	public static final void logDebugLog (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(logDebugLog)>> ---
		// @specification wx.resilience.impl.log.wrapper:logSpec
		// @sigtype java 3.5
		IDataMap pipeMap = new IDataMap(pipeline);
		
		// Add suffixes and prefixes:
		addPrefixAndSuffixImpl(pipeMap);
		
		JournalLogger.log(4, JournalLogger.FAC_FLOW_SVC, JournalLogger.getISLevel(pipeMap.getAsString("level")), pipeMap.getAsString("function"), pipeMap.getAsString("message"));
		/*try{
			Service.doInvoke( "pub.flow", "debugLog", pipeline );
		} catch(ISRuntimeException anISRuntimeException) {
			throw anISRuntimeException;		} catch(ServiceException aServiceException) {
			throw aServiceException;
		} catch( Exception anException){
			throw new ServiceException(anException);
		} */
			
			
			
			
			
		// --- <<IS-END>> ---

                
	}



	public static final void logWxLog (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(logWxLog)>> ---
		// @specification wx.resilience.impl.log.wrapper:logSpec
		// @sigtype java 3.5
		IDataMap pipeMap = new IDataMap(pipeline);
		
		// Add suffixes and prefixes:
		addPrefixAndSuffixImpl(pipeMap);
		
		// Rename for WxLog
		renameVariable(pipeMap, "message", "text");
		renameVariable(pipeMap, "function", "logger");
		toUpper(pipeMap, "level");
		String level = pipeMap.getAsString("level");
		if ("FATAL".equals(level)) {
			pipeMap.put("level", "ERROR");
		}
		
				
		try{
			Service.doInvoke( "wx.log.services", "log", pipeline );
		} catch(ISRuntimeException anISRuntimeException) {
			throw anISRuntimeException;
		} catch(ServiceException aServiceException) {
			throw aServiceException;
		} catch( Exception anException){
			throw new ServiceException(anException);
		} 		
			
			
			
			
			
		// --- <<IS-END>> ---

                
	}



	public static final void logWxLog2 (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(logWxLog2)>> ---
		// @specification wx.resilience.impl.log.wrapper:logSpec
		// @sigtype java 3.5
		IDataMap pipeMap = new IDataMap(pipeline);
		
		// Add suffixes and prefixes:
		addPrefixAndSuffixImpl(pipeMap);
		
		// Rename for WxLog2
		renameVariable(pipeMap, "message", "logMessage");
		renameVariable(pipeMap, "function", "logger");
		String level = pipeMap.getAsString("level");
		if (level == null || level.equals("")) {
			pipeMap.put("level", "Info");
		}
		
		renameVariable(pipeMap, "level", "severity");
		toUpper(pipeMap, "severity");
		
		// output
		try{
			// currently, no output defined.
			Service.doInvoke( "wx.log.pub", "logMessage", pipeline );
		} catch(ISRuntimeException anISRuntimeException) {
			throw anISRuntimeException;
		} catch(ServiceException aServiceException) {
			throw aServiceException;
		} catch( Exception anException){
			throw new ServiceException(anException);
		} 
			
			
			
			
			
		// --- <<IS-END>> ---

                
	}



	public static final void logWxLog4j2 (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(logWxLog4j2)>> ---
		// @sigtype java 3.5
		IDataMap pipeMap = new IDataMap(pipeline);
		
		// Add suffixes and prefixes:
		addPrefixAndSuffixImpl(pipeMap);
		
		// Rename for WxLog2
		//renameVariable(pipeMap, "message", "logMessage");
		//renameVariable(pipeMap, "function", "logger");
		/*String level = pipeMap.getAsString("level");
		if (level == null || level.equals("")) {
			pipeMap.put("level", "Info");
		}
		
		renameVariable(pipeMap, "level", "severity");
		toUpper(pipeMap, "severity");*/
		
		// output
		try{
			// currently, no output defined.
			Service.doInvoke( "wx.log4j2.pub", "log", pipeline );
		} catch(ISRuntimeException anISRuntimeException) {
			throw anISRuntimeException;
		} catch(ServiceException aServiceException) {
			throw aServiceException;
		} catch( Exception anException){
			throw new ServiceException(anException);
		} 
		// --- <<IS-END>> ---

                
	}

	// --- <<IS-START-SHARED>> ---
	
	private static void renameVariable(IDataMap map, String oldName, String newName) {
		map.put(newName, map.get(oldName));
		map.remove(oldName);
	}
	
	private static void toUpper(IDataMap map, String variableName) {
		String variable = map.getAsString(variableName);
		variable = variable.toUpperCase();
		map.put(variableName, variable);
	}
	
	private static void addPrefixAndSuffixImpl(IDataMap pipeMap) {
		
		IData metaData = pipeMap.getAsIData("wxMetaData");
		if (metaData != null) {
			// prefix:
			StringBuilder builder = new StringBuilder();
			builder.append(pipeMap.getAsString("message"));
	
			IDataMap metaDataMap = new IDataMap(metaData);
	
			String correlationID = metaDataMap.getAsString("correlationID");
			String type = metaDataMap.getAsString("type");
			String source = metaDataMap.getAsString("source");
			String destination = metaDataMap.getAsString("destination");
			
			if (source != null) {
				builder.append("|source:").append(source);
			}
			if (destination != null) {
				builder.append("|destination:").append(destination);
			}
			if (type != null) {
				builder.append("|type:").append(type);
			}
			builder.append("|correlationID:").append(correlationID).append("|");
			
	
			// suffix:
			IData[] customProperties = metaDataMap.getAsIDataArray("customProperties");
			if (customProperties != null) {
				builder.append("\r\nCP:");
				for (IData property : customProperties) {
					IDataMap propertyMap = new IDataMap(property);
					String key = propertyMap.getAsString("key");
					String value = propertyMap.getAsString("value");
					builder.append("|").append(key).append(":").append(value);
				}	
				builder.append("|");
			}
			
			pipeMap.put("message", builder.toString());
		}
	}
	
	
		
		
		
		
		
		
		
		
		
		
		
	// --- <<IS-END-SHARED>> ---
}

