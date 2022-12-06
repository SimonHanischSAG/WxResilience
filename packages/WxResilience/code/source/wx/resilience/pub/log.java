package wx.resilience.pub;

// -----( IS Java Code Template v1.2

import com.wm.data.*;
import com.wm.util.Values;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
// --- <<IS-START-IMPORTS>> ---
import com.wm.util.JournalLogger;
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
		// [o] field:1:required loggingServices
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
		// [o] field:1:required loggingServices
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
			"wxMetaData"
		};
	
	private static void initLogging() throws ServiceException {
		// Try to read from logging.service.env first:
		IData input = IDataFactory.create();
		IDataMap inputMap = new IDataMap(input);
		inputMap.put("key", "logging.service.env");
		inputMap.put("wxConfigPkgName", "WxResilience");
		inputMap.put("noServiceException", "true");
		try{
			IData output = Service.doInvoke( "wx.config.pub", "getValueList", input);
			IDataMap outputMap = new IDataMap(output);
			
			String[] servicesEnv = outputMap.getAsStringArray("propertyValueList");
			
			if (servicesEnv != null && servicesEnv.length > 0) {
				loggingServices = servicesEnv;
			} else {				
				inputMap.put("key", "logging.service");
				inputMap.put("wxConfigPkgName", "WxResilience");
				inputMap.put("noServiceException", "true");
				output = Service.doInvoke( "wx.config.pub", "getValueList", input);
				outputMap = new IDataMap(output);
				
				loggingServices = outputMap.getAsStringArray("propertyValueList");
			}
			
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
		
	private static final String LOG_FUNCTION = "WxResilience";
	private static void debugLogError(String message) {
	    JournalLogger.log(4,  JournalLogger.FAC_FLOW_SVC, JournalLogger.ERROR, LOG_FUNCTION, message);
	}
	
	private static void debugLogInfo(String message) {
	    JournalLogger.log(4,  JournalLogger.FAC_FLOW_SVC, JournalLogger.INFO, LOG_FUNCTION, message);
	}
	
	
	private static final String ADR_DELIM = "_";
	private static final String BI_DELIM = "|";
	private static final String KEY_DELIM = ":";
		
		
		
		
		
		
		
	// --- <<IS-END-SHARED>> ---
}

