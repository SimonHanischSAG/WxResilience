package wx.resilience.pub;

// -----( IS Java Code Template v1.2

import com.wm.data.*;
import com.wm.util.Values;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
// --- <<IS-START-IMPORTS>> ---
import com.wm.lang.ns.NSName;
import com.softwareag.util.IDataMap;
import com.wm.app.b2b.server.ISRuntimeException;
// --- <<IS-END-IMPORTS>> ---

public final class log

{
	// ---( internal utility methods )---

	final static log _instance = new log();

	static log _newInstance() { return new log(); }

	static log _cast(Object o) { return (log)o; }

	// ---( server methods )---




	public static final void getLoggingConfig (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(getLoggingConfig)>> ---
		// @sigtype java 3.5
		IDataMap pipeMap = new IDataMap(pipeline);
		pipeMap.put("loggingServices", loggingServices);
		// --- <<IS-END>> ---

                
	}



	public static final void log (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(log)>> ---
		// @specification wx.resilience.impl.log.wrapper:logSpec
		// @sigtype java 3.5
		// strip pipeline in order to avoid overhead for next calls:
		IDataMap pipeMap = new IDataMap(pipeline);
		
		if (loggingServices == null) {
			initLogging();
		}
		
		if (loggingServices != null) {
			pipeMap.put("message", getLogIdentifier(pipeMap) + pipeMap.getAsString("message"));
			IData strippedPipeline = IDataFactory.create();
			IDataMap strippedMap = new IDataMap(strippedPipeline);
			stripPipeline(pipeMap, strippedMap);
			for (String loggingService : loggingServices) {
				NSName nsName = NSName.create(loggingService);
				try {
					//log("invoke: " + nsName.toString());
					IData strippedPipelineCloned = IDataUtil.deepClone(strippedPipeline);
					Service.doInvoke(nsName, strippedPipelineCloned);
				} catch(Exception e) {}
			}
		}
			
			
			
		// --- <<IS-END>> ---

                
	}



	public static final void resetLoggingConfig (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(resetLoggingConfig)>> ---
		// @sigtype java 3.5
		loggingServices = null;
		initLogging();
		IDataMap pipeMap = new IDataMap(pipeline);
		pipeMap.put("loggingServices", loggingServices);
			
		// --- <<IS-END>> ---

                
	}

	// --- <<IS-START-SHARED>> ---
	private static String[] loggingServices = null;
	
	private static final String[] parmNames = new String [] { 
			"message", 
			"function", 
			"level", 
			"metaData"
		};
	
	private static void initLogging() throws ServiceException {
		IData input = IDataFactory.create();
		IDataMap inputMap = new IDataMap(input);
		inputMap.put("key", "logging.service");
		inputMap.put("wxConfigPkgName", "WxResilience");
		inputMap.put("noServiceException", "true");
		try{
			IData output = Service.doInvoke( "wx.config.pub", "getValueList", input);
			IDataMap outputMap = new IDataMap(output);
			loggingServices = outputMap.getAsStringArray("propertyValueList");
			
		} catch(ISRuntimeException anISRuntimeException) {
			throw anISRuntimeException;
		} catch(ServiceException aServiceException) {
			throw aServiceException;
		} catch( Exception anException){
			throw new ServiceException(anException);
		} 
	}
	private static void stripPipeline(IDataMap pipelineMap, IDataMap strippedMap) {
		for ( String key : parmNames) {
			
			if ( pipelineMap.containsKey(key)) {
				strippedMap.put(key, pipelineMap.get(key));
			}
		}
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
	
	private static String getLogIdentifier(IDataMap pipeMap) {
		
		StringBuilder sb = new StringBuilder("");
		String interfaceId = null;
		
		// metaData
		IData metaData = pipeMap.getAsIData("metaData");
		if (metaData != null) {
			IDataMap metaDataMap = new IDataMap(metaData);
		
			// i.Sender
			String source = metaDataMap.getAsString("source");
			String destination = metaDataMap.getAsString("destination");
			if (source != null || destination != null) {
				if (source != null) {
					sb.append(source).append(ADR_DELIM);
					
				}
				
				sb.append("to").append(ADR_DELIM);
				
				/*					 process Receiver(s)					 */
				
				if (destination != null) {
					sb.append(destination);
				}	
				sb.append(" ");
			}
			
			IData[]	customProperties = metaDataMap.getAsIDataArray("customProperties");
			if (customProperties != null) {
				sb.append(BI_DELIM);
				for (int i = 0; i < customProperties.length; i++ )
				{
					IDataMap propMap = new IDataMap(customProperties[i]);
					String key = propMap.getAsString("key");
					if(key != null && key != ""){
						sb.append(key).append(KEY_DELIM);
					}
					String value = propMap.getAsString("value");
					if(value != null && value != ""){
						sb.append(value).append(BI_DELIM);
					} else {
						sb.append(BI_DELIM);
					}
				}
				sb.append(" ");
			}
		}
		
		return sb.toString();
	}
	
	private static final String ADR_DELIM = "_";
	private static final String BI_DELIM = "|";
	private static final String KEY_DELIM = ":";
		
		
		
	// --- <<IS-END-SHARED>> ---
}

