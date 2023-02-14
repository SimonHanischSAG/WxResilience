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
import java.util.Arrays;
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
		// [o] field:0:required message
		InvokeManager invokeManager = InvokeManager.getDefault();
		IDataMap pipeMap = new IDataMap(pipeline);
		String[] blacklist = pipeMap.getAsStringArray("invokeChain.blacklist");
		WxResilienceProcessor newProcessor = new WxResilienceProcessor(blacklist);
		String message = "";
		
		if (processor != null) {
			invokeManager.unregisterProcessor(processor);			
			invokeManager.registerProcessor(newProcessor);
			processor = newProcessor;
			message = "WxResilienceProcessor re-registered to InvokeChain using blacklist: " + Arrays.toString(blacklist);
		} else {
			invokeManager.registerProcessor(newProcessor);
			processor = newProcessor;
			message = "WxResilienceProcessor registered in InvokeChain using blacklist: " + Arrays.toString(blacklist);
		}
		logInfo(message);
		pipeMap.put("message", message);
			
			
			
		// --- <<IS-END>> ---

                
	}



	public static final void unregisterWxResilience (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(unregisterWxResilience)>> ---
		// @sigtype java 3.5
		// [o] field:0:required message
		String message = "";
		if (processor != null) {
			InvokeManager invokeManager = InvokeManager.getDefault();
			
			invokeManager.unregisterProcessor(processor);
			message = "WxResilienceProcessor unregistered from InvokeChain";
			processor = null;
		} else {
			message = "WxResilienceProcessor already unregistered from InvokeChain";
		}
		logInfo(message);
		IDataMap pipeMap = new IDataMap(pipeline);
		pipeMap.put("message", message);
			
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
	
			String baseServiceName = baseService.getNSName().getFullName();
			IDataMap pipeMap = new IDataMap(pipeline);
			
			boolean wxResilienceServiceRedirected = pipeMap.getAsBoolean(REDIRECTED);
			boolean wxResilienceServiceWrapped = pipeMap.getAsBoolean(WRAPPED);
			boolean redirected = false;
			boolean wrapped = false;
	
			if (!wxResilienceServiceWrapped) {
				// Avoid redirecting for services where the wrapper was already invoked manually
				if ("wx.resilience.pub.http:http".equals(baseServiceName)) {
					pipeMap.put(WRAPPED, true);
					wrapped = true;
				} else if ("wx.resilience.pub.http:http".equals(baseServiceName)) {
					pipeMap.put(WRAPPED, true);
					wrapped = true;
				} else if ("wx.resilience.pub.jms:send".equals(baseServiceName)) {
					pipeMap.put(WRAPPED, true);
					wrapped = true;
				} else if ("wx.resilience.pub.jms:sendWxMessage".equals(baseServiceName)) {
					pipeMap.put(WRAPPED, true);
					wrapped = true;
				} else if ("wx.resilience.pub.jms:sendWxMessageAsJson".equals(baseServiceName)) {
					pipeMap.put(WRAPPED, true);
					wrapped = true;
				} else {
					// Avoid endless redirecting
					if (!wxResilienceServiceRedirected) {
						if ("pub.jms:send".equals(baseServiceName)) {
							//logDebug("pub.jms:send redirected");
							pipeMap.put(REDIRECTED, true);
							executeService("wx.resilience.pub.jms", "send", pipeline, baseServiceName);
							pipeMap.remove(REDIRECTED);
							redirected = true;
						} else if ("pub.client:http".equals(baseServiceName)) {
							//logDebug("wx.resilience.pub.http");
							pipeMap.put(REDIRECTED, true);
							executeService("wx.resilience.pub.http", "http", pipeline, baseServiceName);
							pipeMap.remove(REDIRECTED);
							redirected = true;
						}
					}
				}
			}
			if (!redirected) {
				boolean isTop = status.isTopService();		
				boolean executeWxResilienceServices = false;
				if (isTop) {
					executeWxResilienceServices = true;
					for (String entry : invokeChainBlacklist) {
						if (baseServiceName.startsWith(entry)) {
							executeWxResilienceServices = false;
							break;
						}
					}
				}
				//if (isTop || executeWxResilienceServices)
					//logDebug(baseServiceName + ": " + isTop + "," + executeWxResilienceServices);
				// we have to make sure that the servcie invocation chain is not cut
				if (chain.hasNext()) {
					try {
						if (executeWxResilienceServices) {
							try {
								//logDebug("pipe1: " + pipeline.getClass().hashCode());
								executeService("wx.resilience.impl.invokeChainProcessor", "preProcessForTopLevelService", pipeline, baseServiceName);
								//logDebug("pipe2: " + pipeline.getClass().hashCode());
								//pipeMap = new IDataMap(pipeline);
							} catch(Exception e) {
								logError("Error in preProcessForTopLevelService: " + e);
							}
						}
		
						//logDebug("Before original Service");
						
						//logDebug("pipe3: " + pipeline.getClass().hashCode());
						((InvokeChainProcessor) chain.next()).process(chain, baseService, pipeline, status);
						//logDebug("pipe4: " + pipeline.getClass().hashCode());
						
						//logDebug("After original Service");
						
					} catch (Exception originalException) {
						//pipeMap = new IDataMap(pipeline);
						if (executeWxResilienceServices) {
							boolean wxResiliencePostProcessExecuted = pipeMap.getAsBoolean(POSTPROCESS_EXECUTED);
							//logDebug("wxResiliencePostProcessExecuted: " + wxResiliencePostProcessExecuted);
							if (!wxResiliencePostProcessExecuted) {
								logError("Handle error");
										
								IData lastError = null;
								try {
									executeService("pub.flow", "getLastError", pipeline, baseServiceName);						
									//pipeMap = new IDataMap(pipeline);
									
									lastError = pipeMap.getAsIData("lastError");
									if (lastError == null) {
										lastError = createLastErrorFromException(originalException);
										pipeMap.put("lastError", lastError);
									}
								} catch (Exception e) {
									logError("Could not call getLastError");
									lastError = createLastErrorFromException(originalException);
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
						//pipeMap = new IDataMap(pipeline);
						boolean wxResiliencePostProcessExecuted = pipeMap.getAsBoolean(POSTPROCESS_EXECUTED);
						if (!wxResiliencePostProcessExecuted) {
							if (executeWxResilienceServices) {
							//logDebug(baseServiceName + " wxResiliencePostProcessExecuted: " + wxResiliencePostProcessExecuted);
								//logDebug("pipe5: " + pipeline.getClass().hashCode());
								executeService("wx.resilience.pub.resilience", "postProcessForTopLevelService", pipeline, baseServiceName);
								//logDebug("pipe6: " + pipeline.getClass().hashCode());
								//pipeMap = new IDataMap(pipeline);
							}
							pipeMap.remove(POSTPROCESS_EXECUTED);
						}
					}
				}
			}
			if (wrapped) {
				pipeMap.remove(WRAPPED);
			}
		}
	
		private IData createLastErrorFromException(Exception originalException) {
			IData lastError;
			lastError = IDataFactory.create();
			IDataMap lastErrorMap = new IDataMap(lastError);
			Throwable causeException;
			if (originalException.getCause() != null) {
				causeException = originalException.getCause();
			} else {
				causeException = originalException;
			}
			lastErrorMap.put("error", causeException.getLocalizedMessage());
			lastErrorMap.put("errorType", causeException.getClass().getCanonicalName());
			return lastError;
		}
	}
	
	private static IData executeService(String ifc, String svc, IData pipeline, String currentServiceName) throws ServerException, ISRuntimeException {
		// Avoid endless loop in case of broken erroneous service:
		if (!(ifc + ":" + svc).equals(currentServiceName)) {
			try {
				return Service.doInvoke(ifc, svc, pipeline);
			} catch (ISRuntimeException e) {
				throw e;
			} catch (ServerException e) {
				throw e;
			} catch (Exception e) {
				throw new ServiceException(e);
			}
		} else {
			return pipeline;
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
	
		
	private static final String REDIRECTED = "@WxResilience.service.redirected@";
	private static final String WRAPPED = "@WxResilience.service.wrapped@";
	private static final String POSTPROCESS_EXECUTED = "@WxResilience.postProcess.executed@";
	
		
		
	// --- <<IS-END-SHARED>> ---
}

