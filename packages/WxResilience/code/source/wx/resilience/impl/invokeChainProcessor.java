package wx.resilience.impl;

// -----( IS Java Code Template v1.2

import com.wm.data.*;
import com.wm.util.Values;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
// --- <<IS-START-IMPORTS>> ---
import com.wm.util.JournalLogger;
import com.wm.util.ServerException;
import com.wm.app.b2b.server.BaseService;
import com.wm.app.b2b.server.ISRuntimeException;
import com.wm.app.b2b.server.invoke.InvokeChainProcessor;
import com.wm.app.b2b.server.invoke.InvokeManager;
import com.wm.app.b2b.server.invoke.ServiceStatus;
import java.util.Iterator;
import com.softwareag.util.IDataMap;
// --- <<IS-END-IMPORTS>> ---

public final class invokeChainProcessor

{
	// ---( internal utility methods )---

	final static invokeChainProcessor _instance = new invokeChainProcessor();

	static invokeChainProcessor _newInstance() { return new invokeChainProcessor(); }

	static invokeChainProcessor _cast(Object o) { return (invokeChainProcessor)o; }

	// ---( server methods )---




	public static final void isInvokeChainProcessorRunning (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(isInvokeChainProcessorRunning)>> ---
		// @sigtype java 3.5
		// [o] field:0:required invokeChainProcessorRunning
		IDataMap pipeMap = new IDataMap(pipeline);
		if (processor != null) {
			pipeMap.put("invokeChainProcessorRunning", "true");
		} else {
			pipeMap.put("invokeChainProcessorRunning", "false");
		}
		// --- <<IS-END>> ---

                
	}



	public static final void registerWxResilience (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(registerWxResilience)>> ---
		// @sigtype java 3.5
		// [i] field:1:required invokeChain.blacklist
		if (processor == null) {
			InvokeManager invokeManager = InvokeManager.getDefault();
			IDataMap pipeMap = new IDataMap(pipeline);
			processor = new WxResilienceProcessor(pipeMap.getAsStringArray("invokeChain.blacklist"));
			invokeManager.registerProcessor(processor);
			logInfo("WxResilienceProcessor registered in InvokeChain");
		} else {
			logInfo("WxResilienceProcessor already registered in InvokeChain");
		}
		
		 		
			
			
		// --- <<IS-END>> ---

                
	}



	public static final void unregisterWxResilience (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(unregisterWxResilience)>> ---
		// @sigtype java 3.5
		if (processor != null) {
			InvokeManager invokeManager = InvokeManager.getDefault();
			
			invokeManager.unregisterProcessor(processor);
			logInfo("WxResilienceProcessor unregistered from InvokeChain");
			processor = null;
		} else {
			logInfo("WxResilienceProcessor already unregistered from InvokeChain");
		}
		// --- <<IS-END>> ---

                
	}

	// --- <<IS-START-SHARED>> ---
	
	public static WxResilienceProcessor processor = null;
	
	public static class WxResilienceProcessor implements InvokeChainProcessor {
	
		private String[] invokeChainBlacklist;
		
		public WxResilienceProcessor(String[] invokeChainBlacklist) {
			if (invokeChainBlacklist != null) {
				this.invokeChainBlacklist = invokeChainBlacklist;
			} else {
				this.invokeChainBlacklist = new String[0];
			}
		}
	
		@Override
		public void process(Iterator chain, BaseService baseService, IData pipeline, ServiceStatus status)
				throws ServerException {
	
			IDataMap pipeMap = new IDataMap(pipeline);
			String baseServiceName = "";
			boolean isTop = status.isTopService();		
			baseServiceName = baseService.getNSName().getFullName();
			
			boolean executeWxResilienceServices = false;
			if (isTop) {
				for (String entry : invokeChainBlacklist) {
					if (!baseServiceName.startsWith(entry) && !baseServiceName.startsWith("wm.")) {
						break;
					}
				}
				executeWxResilienceServices = true;
			}			
			
			// we have to make sure that the servcie invocation chain is not cut
			if (chain.hasNext()) {
				try {
					if (executeWxResilienceServices) {
						try {
							executeService("wx.resilience.pub.resilience", "preProcessForTopLevelService", pipeline, baseServiceName);
						} catch(Exception e) {
							logError("Error in preProcessForTopLevelService: " + e);
						}
					}
	
					//logDebug("Before original Service");
					
					((InvokeChainProcessor) chain.next()).process(chain, baseService, pipeline, status);
					
					//logDebug("After original Service");
					
				} catch (Exception originalException) {
					if (executeWxResilienceServices) {
						boolean wxResiliencePostProcessExecuted = pipeMap.getAsBoolean("@WxResilience.postProcess.executed@");
						//logDebug("wxResiliencePostProcessExecuted: " + wxResiliencePostProcessExecuted);
						if (!wxResiliencePostProcessExecuted) {
							logError("Handle error");
									
							IData lastError = null;
							try {
								executeService("pub.flow", "getLastError", pipeline, baseServiceName);						
								
								lastError = pipeMap.getAsIData("lastError");
								if (lastError == null) {
									lastError = IDataFactory.create();
									IDataMap lastErrorMap = new IDataMap(lastError);
									lastErrorMap.put("error", originalException.getCause());
									lastErrorMap.put("errorType", originalException.getClass().getCanonicalName());
									pipeMap.put("lastError", lastError);
								}
							} catch (Exception e) {
								logError("Could not call getLastError");
								lastError = IDataFactory.create();
								IDataMap lastErrorMap = new IDataMap(lastError);
								lastErrorMap.put("error", originalException.getLocalizedMessage());
								lastErrorMap.put("errorType", originalException.getClass().getCanonicalName());
								pipeMap.put("lastError", lastError);
							}
		
							try {
								executeService("wx.resilience.pub.resilience", "handleError", pipeline, baseServiceName);
							} catch (Exception e) {
								logError("Error in handleError: " + e);
							}
						} else {
							// Error already handled in Flow
							throw originalException;
						} 
					} else {
						//logDebug("execute");
						throw originalException;
					}
				
				} finally {
					if (executeWxResilienceServices) {
						boolean wxResiliencePostProcessExecuted = pipeMap.getAsBoolean("@WxResilience.postProcess.executed@");
						//logDebug("wxResiliencePostProcessExecuted: " + wxResiliencePostProcessExecuted);
						if (!wxResiliencePostProcessExecuted) {
							executeService("wx.resilience.pub.resilience", "postProcessForTopLevelService", pipeline, baseServiceName);
						}
					}
				}
			}
		}
	}
	
	private static void executeService(String ifc, String svc, IData pipeline, String currentServiceName) throws ServerException, ISRuntimeException {
		// Avoid endless loop in case of broken erroneous service:
		if (!(ifc + ":" + svc).equals(currentServiceName)) {
			try {
				Service.doInvoke(ifc, svc, pipeline);
			} catch (ISRuntimeException e) {
				throw e;
			} catch (ServerException e) {
				throw e;
			} catch (Exception e) {
				throw new ServiceException(e);
			}
		}
	}
	
	private static void logDebug(String message) {
		JournalLogger.log(4, JournalLogger.FAC_FLOW_SVC, JournalLogger.INFO, "WxResilienceProcessor", message);
	}
	
	private static void logInfo(String message) {
		JournalLogger.log(4, JournalLogger.FAC_FLOW_SVC, JournalLogger.INFO, "WxResilienceProcessor", message);
	}
	
	private static void logError(String message) {
		JournalLogger.log(4, JournalLogger.FAC_FLOW_SVC, JournalLogger.ERROR, "WxResilienceProcessor", message);
	}
	
		
		
		
	// --- <<IS-END-SHARED>> ---
}

