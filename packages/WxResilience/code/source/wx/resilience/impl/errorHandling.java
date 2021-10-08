package wx.resilience.impl;

// -----( IS Java Code Template v1.2

import com.wm.data.*;
import com.wm.util.Values;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
// --- <<IS-START-IMPORTS>> ---
import com.softwareag.util.IDataMap;
import com.wm.app.audit.IAuditRuntime;
import wx.resilience.impl.ErrorInfo;
import wx.resilience.impl.SummarizedExceptionHandlingHandler;
import wx.resilience.impl.SummarizedExceptionHandlingHandler.Creator;
import wx.resilience.impl.Exceptions;
import wx.resilience.impl.Paths;
import com.wm.lang.ns.NSName;
import java.io.File;
import java.io.IOException;
import java.util.*;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;
import org.xml.sax.SAXException;
import com.wm.app.b2b.server.*;
import java.io.*;
import java.lang.reflect.UndeclaredThrowableException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import com.wm.lang.ns.NSService;
import java.util.function.Supplier;
// --- <<IS-END-IMPORTS>> ---

public final class errorHandling

{
	// ---( internal utility methods )---

	final static errorHandling _instance = new errorHandling();

	static errorHandling _newInstance() { return new errorHandling(); }

	static errorHandling _cast(Object o) { return (errorHandling)o; }

	// ---( server methods )---




	public static final void assembleExceptionHandlings (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(assembleExceptionHandlings)>> ---
		// @sigtype java 3.5
		Log.logInfo("Scanning all packages for ExceptionHandling.xml files");
		createSummarizedExceptionHandling();
	
		// --- <<IS-END>> ---

                
	}



	public static final void auditLogCompleteForTopLevelService (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(auditLogCompleteForTopLevelService)>> ---
		// @sigtype java 3.5
		InvokeState state = InvokeState.getCurrentState();
		if (state == null) {
			return;
		}
		IAuditRuntime auditRuntime = state.getAuditRuntime();
		if (auditRuntime == null) {
			return;
		}
		String[] stack = auditRuntime.getContextStack();
		if (stack == null || stack.length < 1) {
			return;
		}
		String[] stackWithoutCurrentContext = new String[1];
		stackWithoutCurrentContext[0] = stack[0];
		auditRuntime.setContextStack(stackWithoutCurrentContext);
		NSService topService = getCurrentTopLevelService();
		if (topService == null) {
			return;
		}
		AuditLogManager.auditLog(topService, pipeline, 2);
		auditRuntime.setContextStack(stack);
		
			
		// --- <<IS-END>> ---

                
	}



	public static final void getCallingService (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(getCallingService)>> ---
		// @sigtype java 3.5
		// [i] field:0:optional level
		// [o] record:0:required document
		// [o] field:0:required attributeName
		// [o] object:0:optional attributeValue
		IDataCursor cursor = pipeline.getCursor();
		String level = IDataUtil.getString(cursor, "level");
		int lev; 
		try {
			lev = Integer.parseInt(level);
		} 
		catch (Exception e) {
			lev = 2;
		}
		NSService currentSvc = Service.getServiceEntry();
		Stack<?> callStack = InvokeState.getCurrentState().getCallStack();
		int index = callStack.indexOf(currentSvc);
		if (index > 1 && lev <= index) {
			IDataUtil.put(cursor, "callingServiceName", ((NSService) callStack.elementAt(index - lev)).toString());
		}
		cursor.destroy();
		// --- <<IS-END>> ---

                
	}



	public static final void getDocumentAttribute (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(getDocumentAttribute)>> ---
		// @sigtype java 3.5
		// [i] record:0:required document
		// [i] field:0:required attributeName
		// [o] object:0:optional attributeValue
		final IDataMap map = new IDataMap(pipeline);
		final String attributeName = map.getAsString("attributeName");
		if (attributeName == null) {
			throw new NullPointerException("Missing parameter: attributeName");
		}
		if (attributeName.length() == 0) {
			throw new IllegalArgumentException("Empty parameter: attributeName");
		}
		final IData document = map.getAsIData("document");
		if (document == null) {
			throw new NullPointerException("Missing parameter: document");
		}
		final Object attributeValue = new IDataMap(document).get(attributeName);
		if (attributeValue != null) {
			map.put("attributeValue", attributeValue);
		}		
		// --- <<IS-END>> ---

                
	}



	public static final void handleErrorSvc (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(handleErrorSvc)>> ---
		// @sigtype java 3.5
		// [i] recref:0:required exceptionInfo pub.event:exceptionInfo
		// [i] field:0:optional genericErrorMessage
		// [i] record:0:optional businessObject
		// [o] field:0:required errorToBeThrown
		// [o] field:0:optional errorCode
		Log.write("handleErrorSvc: -->");
		IDataCursor pipelineCursor = pipeline.getCursor();
		
		// *******************************************************************
		// -----------------INITIAL CHECKS AND PREPARATIONS-------------------
		// *******************************************************************
		
		IData MetaData = IDataUtil.getIData(pipelineCursor, METADATA_ID);
		String messageUID = getMessageUID(MetaData);
		Log.write("handleErrorSvc: messageUID=" + messageUID);
		
		// Extract inputs from pipeline and check for null values
		IData businessObject = IDataUtil.getIData(pipelineCursor, BUSINESS_DOC_ID);
		Log.logDebug("handleErrorSvc: businessObject=" + businessObject);
		/*if (businessObject == null) {
			logMessageFromCatalog("200", "0020", new String[] {});
		}*/
		
		IData exceptionInfo = IDataUtil.getIData(pipelineCursor, ERROR_INFO_ID);
		Log.write("handleErrorSvc: exceptionInfo=" + exceptionInfo);
		if (exceptionInfo == null) {
			Log.logError("Failed to handle the exception. Error is null");
			throw new ServiceException(
					"Was not able to handle exception. Error is null");
		}
		
		String excludeServiceFromExceptionHandling = IDataUtil.getString(pipelineCursor, EXCLUDE_SERVICE);
		Log.write("handleErrorSvc: excludeServiceFromExceptionHandling=" + excludeServiceFromExceptionHandling);
		
		// ---Initialize and extract error hierarchy---
		ArrayList<IDataCursor> errorInfoHierachy = new ArrayList<IDataCursor>();
		errorInfoHierachy.add(exceptionInfo.getCursor());
		errorInfoHierachy.trimToSize();
		
		// Initialize handling status informations
		String handlingLocation = "";
		// extract error hierarchy
		ArrayList<ErrorInfo> hierachicalErrorInfo = extractErrorHierarchy(errorInfoHierachy);
		
		// check if exception handling definition has been initialized
		final Document errorHandlings = geterrorHandlings();
		Log.write("handleErrorSvc: errorHandlings=" + errorHandlings);		
		// Initialize list of handlings
		/**
		 * contains all handling, which have been found for the error and some
		 * additional informations handling.get(0) contains the abortExecution
		 * parameter ("true" or "false") handling.get(1) contains the
		 * handlingType parameter ("all" or special error type)
		 * handling.get(2...n) contains all handlings (service calls as String)
		 * */
		ExceptionHandlingInfo handlingInfo = null;
		
		// *******************************************************************
		// --------------START HIERARCHICAL ERROR PROCESSING------------------
		// *******************************************************************
		
		
		// Initialize callerType
		String callerType = getCallerType();
		Log.write("handleErrorSvc: callerType=" + callerType);
		// Initialize originalError		
		String originalErrorMessage = hierachicalErrorInfo.get(hierachicalErrorInfo.size() - 1).getError();
		Log.write("handleErrorSvc: originalErrorMessage=" + originalErrorMessage);
		// try to handle error hierarchical bottom up (from origin to root
		// error)
		// iteration over hierarchies
		//for (int hierachyLevel = 0; hierachyLevel < hierachicalErrorInfo.size(); hierachyLevel++) {
		for (int hierachyLevel = (hierachicalErrorInfo.size()-1); hierachyLevel >= 0; hierachyLevel--) {
			Log.write("handleErrorSvc: hierarchyLevel=" + hierachyLevel);
			// extract needed error informations of actually used hierarchy
			String errorType = hierachicalErrorInfo.get(hierachyLevel).getErrorType();
			Log.write("handleErrorSvc: errorType=" + errorType);
			if (errorType == null) {
				hierachicalErrorInfo.get(hierachyLevel).setErrorType("unknown");
				errorType = "unknown";
			}
		
			String service = hierachicalErrorInfo.get(hierachyLevel).getService();
			Log.write("handleErrorSvc: service=" + service);
			if (service == null) {
				hierachicalErrorInfo.get(hierachyLevel).setService("unknown");
				service = "unknown";
			}
		
			Log.logDebug("Start level " + hierachyLevel + " error handling for error type " + errorType + " at location " + service);
		
			// Initialize XPath expression
			String locationXPath = "//" + EHD_NODE_DOC_ROOT_ID;
			
		
			// extract and format location information of error
			// String "a.b.c:d" will be put into String[]{a,b,c,d}
			String[] location = new String[0];
			if (service != null && !service.equals("") && !service.equals(excludeServiceFromExceptionHandling)) {
				String[] locationTemp = service.split("\\.");
				location = new String[locationTemp.length + 1];
		
				for (int locationLength = 0; locationLength < locationTemp.length - 1; ++locationLength) {
					location[locationLength] = locationTemp[locationLength];
				}
		
				if (locationTemp[locationTemp.length - 1].contains(":")) {
					locationTemp = locationTemp[locationTemp.length - 1]
							.split(":");
					location[location.length - 2] = locationTemp[0];
					location[location.length - 1] = locationTemp[1];
				} else
					location[location.length - 2] = locationTemp[locationTemp.length - 1];
			}
		
			// *******************************************************************
			// -----------------SEARCH FOR HANDLING IN
			// LOCATIONS------------------
			// *******************************************************************
		
			// backwards recursive search over locations in exception handling
			// definition
			for (int locationLength = location.length - 1; locationLength >= 0; --locationLength) {
		
				// reset location XPath
				locationXPath = "//" + EHD_NODE_DOC_ROOT_ID;
		
				// build of location XPath
				for (int locationDepth = 0; locationDepth <= locationLength; ++locationDepth) {
					locationXPath += "/" + EHD_NODE_ERROR_LOCATION_ID + "[@"
							+ EHD_ATTRIBUTE_LOCATION_NAME_ID + "='"
							+ location[locationDepth] + "']";
				}
		
				locationXPath = locationXPath + "/" + EHD_NODE_ERROR_TYPE_ID;
				Log.write("handleErrorSvc: locationXPath=" + locationXPath);
		
				// get handling for location and error type
				handlingInfo = getHandlingsForLocation(errorHandlings,
						locationXPath, errorType, callerType, originalErrorMessage);
		
				// break loop and set up handling location, if handling has been
				// found
				if (handlingInfo != null) {
					for (int locationID = 0; locationID <= locationLength; ++locationID) {
						if (locationID == (locationLength - 1)
								&& locationLength == (location.length - 1))
							handlingLocation += location[locationID] + ":";
						else
							handlingLocation += location[locationID] + ".";
					}
					handlingLocation = handlingLocation.substring(0,
							handlingLocation.length() - 1);
		
					
					break;
				}
			}
		
			// *******************************************************************
			// -----------SEARCH FOR HANDLING IN GLOBAL ERROR
			// HANDLING------------
			// *******************************************************************
		
			// if no handling has been found by searching in all location paths,
			// look up for global error handling
			// if hierarchy is root allow to use global generic handling
			if (handlingInfo == null && hierachyLevel == 0) {
				Log.write("handleErrorSvc: Global error handling");
				locationXPath = "//" + EHD_NODE_DOC_ROOT_ID + "/"
						+ EHD_NODE_GLOBAL_ERROR_TYPE_ID;
				handlingInfo = getHandlingsForLocation(errorHandlings,
						locationXPath, errorType, callerType, originalErrorMessage);
				handlingLocation = EHD_VALUE_GLOBAL_ERROR_HANDLING_ID;
				// if hierarchy is not root deny to use global generic handling,
				// to ensure global generic handling is really the last handling
				// to use
			} 
		
			// *******************************************************************
			// ----------------------HANDLING HAS BEEN
			// FOUND----------------------
			// *******************************************************************
		
			if (handlingInfo != null) {
				Log.write("handleErrorSvc: Handling detected: " + handlingInfo.toString());
		
				// create handled error info
				IData handledErrorInfo = IDataFactory.create();
				IDataCursor handledErrorInfoCursor = handledErrorInfo
						.getCursor();
		
				int rootLevel = 0;
				IDataUtil.put(
					handledErrorInfoCursor,
					HANDLED_ERROR_INFO_ROOT_ID,
					buildCompactErrorInfo(
						hierachicalErrorInfo.get(rootLevel).getService(),
						hierachicalErrorInfo.get(rootLevel).getFlowStep()));
				
				IDataUtil.put(
					handledErrorInfoCursor,
					HANDLED_ERROR_INFO_HANDLED_ID,
					buildCompactErrorInfo(
						hierachicalErrorInfo.get(hierachyLevel).getService(),
						hierachicalErrorInfo.get(hierachyLevel).getFlowStep()));
				
				int applicationServiceLevel = getDeepestApplicationServiceLevel(hierachicalErrorInfo);
				IDataUtil.put(
					handledErrorInfoCursor,
					HANDLED_ERROR_INFO_APPLICATION_SERVICE_ID,
					buildCompactErrorInfo(
						hierachicalErrorInfo.get(applicationServiceLevel).getService(),
						hierachicalErrorInfo.get(applicationServiceLevel).getFlowStep()));
		
				int originLevel = hierachicalErrorInfo.size() - 1;
				IDataUtil.put(
					handledErrorInfoCursor,
					HANDLED_ERROR_INFO_ORIGIN_ID,
					buildCompactErrorInfo(
						hierachicalErrorInfo.get(originLevel).getService(),
						hierachicalErrorInfo.get(originLevel).getFlowStep()));
		
				// Use origin error for message and type:
				IDataUtil.put(handledErrorInfoCursor,
						ERROR_MSG_ID, hierachicalErrorInfo.get(originLevel).getError());
				IDataUtil.put(handledErrorInfoCursor,
						ERROR_TYPE_ID, hierachicalErrorInfo.get(originLevel).getErrorType());
				
				
				IDataUtil.put(handledErrorInfoCursor,
						HANDLED_ERROR_INFO_DEPTH_ID,
						hierachicalErrorInfo.size());
				IDataUtil.put(handledErrorInfoCursor,
						HANDLED_ERROR_INFO_HANDLING_LOCATION_ID,
						handlingLocation);
				IDataUtil.put(handledErrorInfoCursor,
						HANDLED_ERROR_INFO_HANDLING_TYPE_ID, handlingInfo.getType());
				IDataUtil.put(handledErrorInfoCursor,
						HANDLED_ERROR_INFO_ERROR_CODE, handlingInfo.getErrorCode());
		
				/*IDataUtil.put(handledErrorInfoCursor,
						HANDLED_ERROR_INFO_CALL_STACK, getCallStackAsJSONString(errorInfoHierachy));*/
				IDataUtil.put(handledErrorInfoCursor,
						HANDLED_ERROR_INFO_CALL_STACK, IDataUtil.getIDataArray(errorInfoHierachy.get(0), ERROR_INFO_CALL_STACK));
				IDataUtil.put(handledErrorInfoCursor,
						HANDLED_ERROR_INFO_ERROR_DUMP, IDataUtil.getString(errorInfoHierachy.get(0), ERROR_INFO_ERROR_DUMP));
				IDataUtil.put(handledErrorInfoCursor,
						HANDLED_ERROR_INFO_TIME, IDataUtil.getString(errorInfoHierachy.get(0), ERROR_INFO_TIME));
				// hier pipeline adden
				// IDataUtil.put(handledErrorInfoCursor, HANDLED_ERROR_INFO_PIPELINE, pipeline);
				// IData exceptionInfo = IDataUtil.getIData(pipelineCursor, ERROR_INFO_ID);
				IDataCursor tempPipelineCursor = exceptionInfo.getCursor();
				IData tempPipeline = IDataUtil.getIData(tempPipelineCursor, HANDLED_ERROR_INFO_PIPELINE);
				IDataUtil.put(handledErrorInfoCursor, HANDLED_ERROR_INFO_PIPELINE, tempPipeline);
				tempPipelineCursor.destroy();
		
				
				boolean breakRetryLoopForCurrentUID = Boolean.valueOf(getValueConf(BREAK_RETRY_LOOP + messageUID, PACKAGE_LCL_ERROR, "true", "", "", "", ""));
				if (breakRetryLoopForCurrentUID) {
					Log.logWarn("Endless loop for uuid " + messageUID + " broken because of wxconfig key");
				}
				Log.write("handleErrorSvc: breakRetryLoopForCurrentUID=" + breakRetryLoopForCurrentUID);
				
				// create generic value map
				// TODO: Drop genericValueMap
				HashMap<String, String> genericValueMap = createGenericValueMap(handlingInfo, breakRetryLoopForCurrentUID);
				Log.write("handleErrorSvc: genericValueMap=" + genericValueMap);
				IDataUtil.put(handledErrorInfoCursor, EHD_ATTRIBUTE_RETRY_COUNT,
						genericValueMap.get(EHD_ATTRIBUTE_RETRY_COUNT));
				
				IDataUtil.put(handledErrorInfoCursor, EHD_ATTRIBUTE_MAX_RETRY_ATTEMPTS,
						genericValueMap.get(EHD_ATTRIBUTE_MAX_RETRY_ATTEMPTS));
				IDataUtil.put(handledErrorInfoCursor, EHD_ATTRIBUTE_RETURN_VALUE_ID,
						genericValueMap.get(EHD_ATTRIBUTE_RETURN_VALUE_ID));
				
				IDataUtil.put(handledErrorInfoCursor, EHD_ATTRIBUTE_ERROR_CALLER_TYPE_ID,
						callerType);
				
				
				// create handling service Input
				IData handlingServiceInput = IDataFactory.create();
				IDataCursor serviceInputCursor = handlingServiceInput.getCursor();
		
				// put businessObject, handled error info, genericValueList
				// and genericErrorMessage into handling service input
				IDataUtil
						.put(serviceInputCursor, METADATA_ID, MetaData);
				if (businessObject != null  &&  handlingInfo.isPrintBusinessObject()) {
					IDataUtil.put(serviceInputCursor, BUSINESS_DOC_ID,
							businessObject);
					IDataUtil.put(serviceInputCursor, PRINT_BUSINESS_OBJECT_ID,
							handlingInfo.isPrintBusinessObject());
				}
				IDataUtil.put(serviceInputCursor,
						HANDLED_ERROR_INFO_ID, handledErrorInfo);
				IDataUtil.put(serviceInputCursor, GENERIC_VALUE_MAP_ID,
						genericValueMap);
				IDataUtil.put(serviceInputCursor,
						GENERIC_ERROR_MESSAGE_ID, IDataUtil.getString(
								pipelineCursor,
								GENERIC_ERROR_MESSAGE_ID));
					
				// execute all handling
				Log.write("handleErrorSvc: Ready to invoke error handlers");
				final List<String> handlingServices = handlingInfo.getHandlingServices();
				for (final String serviceName : handlingServices) {
					try {
						Log.write("handleErrorSvc: Invoking error handler, serviceName=" + serviceName);
						invokeErrorHandler(handlingServiceInput, serviceName);
						Log.logInfo("Successfully invoked handling service " + serviceName);
					} catch (ErrorHandlingException e) {
						throw e;
					} catch (Exception e) {
						Log.logError("Failed to invoke handling service " + serviceName + " with error: " + e.getMessage());
					}
				}
		
				
		
				final String errorToBeThrown = genericValueMap.get(EHD_ATTRIBUTE_RETURN_VALUE_ID);
				IDataUtil.put(pipelineCursor, EHD_ATTRIBUTE_RETURN_VALUE_ID,
						errorToBeThrown);
				
				final String errorCode = handlingInfo.getErrorCode();
				IDataUtil.put(pipelineCursor, "errorCode", handlingInfo.getErrorCode());
				
				final String handlingErrorToBeThrown = handlingInfo.getErrorToBeThrown();
				Log.write("handleErrorSvc: <-- handlingErrorToBeThrown=" + handlingErrorToBeThrown
						+ ", errorToBeThrown=" + errorToBeThrown + ", errorCode=" + errorCode);
		
				// abort recursive bottom-up looping over wrapped exceptions
				break;
			}
		}
		
		// *******************************************************************
		// ---------------------NO HANDLING HAS BEEN FOUND--------------------
		// *******************************************************************
		if (handlingInfo == null) {
			Log.write("handleErrorSvc: No handling found");
			String errorType = hierachicalErrorInfo.get(hierachicalErrorInfo.size() - 1).getErrorType();
			String service = hierachicalErrorInfo.get(hierachicalErrorInfo.size() - 1).getService();
		
			Log.logError("No error handling found! Was not able to handle error of type " + errorType + " at location " + service + " (origin error, " + (hierachicalErrorInfo.size() - 1) + "-fold wrapped)");
			pipelineCursor.destroy();
			throw new ServiceException(
					"No error handling found! \n Was not able to handle error of type '"
							+ errorType + "' at location '" + service
							+ "' (origin error, "
							+ (hierachicalErrorInfo.size() - 1)
							+ "-fold wrapped).");
		}
		
		pipelineCursor.destroy();
		Log.write("handleErrorSvc: <--");
			
			
			
			
		// --- <<IS-END>> ---

                
	}



	public static final void initializeExceptionHandlings (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(initializeExceptionHandlings)>> ---
		// @sigtype java 3.5
		Log.logInfo("Initializing error handling framework...");
		//TODO
		File eHDFile = new File(ServerAPI.getPackageConfigDir(WX_RESILIENCE) + "/" + SUMMARIZED_EXCEPTION_HANDLING_FILE);
		if (!eHDFile.isFile()) {
			createSummarizedExceptionHandling();
		}
		final URL url;
		try {
			url = eHDFile.toURI().toURL();
		} catch (IOException e) {
			throw new ServiceException(e);
		}
		final SAXBuilder builder = new SAXBuilder("org.apache.xerces.parsers.SAXParser");
		initHandlings(url, builder);
			
			
			
		// --- <<IS-END>> ---

                
	}

	// --- <<IS-START-SHARED>> ---
	@FunctionalInterface
	public static interface ExceptionHandlingListener {
		public void accept(String pServiceName, IData pPipeline);
	}
	
	@FunctionalInterface
	public static interface CallerTypeSupplier {
		public String get();
	}
	
	private static Path createSummarizedExceptionHandling() throws ServiceException {
		synchronized(createSummarizedExceptionHandlingLock) {
			return createSummarizedExceptionHandling(SUMMARIZED_EXCEPTION_HANDLING_FILE);
		}
	}
	private static Path createSummarizedExceptionHandling(String pFileName)
			throws ServiceException {
		File ISDir = ServerAPI.getServerConfigDir().getParentFile();
		File ISPackages = new java.io.File(ISDir, "packages");
		final Path configDir = ServerAPI.getPackageConfigDir(WX_RESILIENCE).toPath();
		final Path outputFile = configDir.resolve(pFileName);
		Paths.assertParentDirExists(outputFile);
	
		try {
			try (Creator creator = SummarizedExceptionHandlingHandler.newCreator(outputFile)) {
				for (File packageDir : ISPackages.listFiles()) {
					if (packageDir.isDirectory()) {
						final String packageName = packageDir.getName();
						if (packageName.startsWith("Wm")) {
							continue;
						}
						final File packageCfgDir = new File(packageDir, "config");
						if (packageCfgDir.isDirectory()) {
							final File packageEhdFile = new File(packageCfgDir, ERROR_HANDLING_XML_FILE);
							if (packageEhdFile.isFile()) {
								creator.parse(packageEhdFile, packageName);
							}
						}
					}
				}
			}
			validateXML();
		} catch (Exception e) {
			Log.logError("Error during parsing of summarizedExceptionHandlingDefinition: " + e.getMessage());
			throw Exceptions.show(e, ServiceException.class);
		}
		return outputFile;
	}
	
	private static final ExceptionHandlingListener DEFAULT_ERROR_HANDLER = new ExceptionHandlingListener() {
		public void accept(String s, IData d) {
			Log.logDebug("DefaultErrorHandler: --> " + s);
			try {
				Service.doInvoke(NSName.create(s), d);
			} catch (Exception e) {
				Log.logDebug("DefaultErrorHandler: " + e.getClass().getName() + ", " + e.getMessage());
				throw new UndeclaredThrowableException(e);
			}
			Log.logDebug("DefaultErrorHandler: <--");
		}
	};
	private static ExceptionHandlingListener getErrorHandler() {
		synchronized (exceptionHandlingListenerLock) {
			if (_exceptionHandlingListener == null) {
				return DEFAULT_ERROR_HANDLER;
			} else {
				return _exceptionHandlingListener;
			}
		}
	}
	public static void setErrorHandler(ExceptionHandlingListener pErrorHandlingListener) {
		synchronized (exceptionHandlingListenerLock) {
			_exceptionHandlingListener = pErrorHandlingListener;
		}
	}
	private static void invokeErrorHandler(IData pHandlingServiceInput,
			final String pServiceName) throws Exception {
		Log.logDebug("invokeErrorHandler: --> ");
		final ExceptionHandlingListener ehl = getErrorHandler();
		Log.logDebug("invokeErrorHandler: eh=" + ehl);
		ehl.accept(pServiceName, pHandlingServiceInput);
		Log.logDebug("invokeErrorHandler: <--");
	
		// --- <<IS-END>> ---
	}
	
	private static Document geterrorHandlings() throws ServiceException {
		synchronized(exceptionHandlingLock) {
			if (_exceptionHandling != null) {
				return _exceptionHandling;
			}
		}
		initializeExceptionHandlings(null);
		synchronized (exceptionHandlingLock) {
			if (_exceptionHandling == null) {
				throw new IllegalStateException("Unable to initialize errorHandling. Did you assemble it?");
			} else {
				return _exceptionHandling;
			}
		}
	}
	
	private static String getCallerType() {
		CallerTypeSupplier cts; 
		synchronized (callerTypeSupplierLock) {
			cts = callerTypeSupplier;
		}
		if (cts == null) {
			cts = DEFAULT_CALLER_TYPE_SUPPLIER;
		}
		return cts.get();
	}
	
	public static void setCallerTypeSupplier(CallerTypeSupplier pSupplier) {
		synchronized (callerTypeSupplierLock) {
			callerTypeSupplier = pSupplier;
		}
	}
	
	private static String getCallerTypeByCallingService() {
		IData input = null;
		String type = "";
		// output
		IData output = IDataFactory.create();
		try {
			output = Service.doInvoke( "wx.resilience.impl.caller", "getCaller", input );
			IDataMap outputMap = new IDataMap(output);
			IDataMap callerMap = new IDataMap(outputMap.getAsIData("caller"));
			return callerMap.getAsString("type");
		} catch (Exception e) {
			return type;
		}	
	}
	
	
		private static String getCallStackAsJSONString(ArrayList<IDataCursor> errorInfoHierachy) {
	
			IData[] callStack = IDataUtil.getIDataArray(errorInfoHierachy.get(0), ERROR_INFO_CALL_STACK);
			
			String jsonString = "";
			
			if (callStack != null) {
				// input
				IData input = IDataFactory.create();
				IDataCursor inputCursor = input.getCursor();
	
				// document
				IData document = IDataFactory.create();
				IDataCursor documentCursor = document.getCursor();
				IDataUtil.put(documentCursor, "callStack", callStack);
				IDataUtil.put(inputCursor, "document", document);
				IDataUtil.put(inputCursor, "prettyPrint", "false");
				inputCursor.destroy();
	
				// output
				IData output = IDataFactory.create();
				try {
					output = Service.doInvoke("pub.json", "documentToJSONString", input);
					IDataCursor outputCursor = output.getCursor();
					jsonString = IDataUtil.getString(outputCursor, "jsonString");
					outputCursor.destroy();
				} catch (Exception e) {
					// It is possible, that this service does not exists (WM8.2 environments/stores).
					// Try again with Service from WmJSON package:
					// This block can be deleted, if every environment is on WM9.0!
					try {
						output = Service.doInvoke("pub.json", "documentToJSON", input);
						IDataCursor outputCursor = output.getCursor();
						jsonString = IDataUtil.getString(outputCursor, "json");
						outputCursor.destroy();
					} catch (Exception innerEx) {
					}
				}
			}
			
			return jsonString;
		}
		
		private static HashMap<String, String> createGenericValueMap(ExceptionHandlingInfo handling, boolean breakRetryLoop) {
			HashMap<String, String> genericValueMap = new HashMap<String, String>();
	
			// evaluate errorToBeThrown:
			String errorToBeThrown = handling.getErrorToBeThrown();
			
			int retryCount = getRetryCount();
			genericValueMap.put(EHD_ATTRIBUTE_RETRY_COUNT, String.valueOf(retryCount));
	
			String maxRetryAttemptsString = handling.getMaxRetryAttempts();
			Integer maxRetryAttempts = null;
			try {
				maxRetryAttempts = Integer.valueOf(maxRetryAttemptsString);
			} catch (NumberFormatException nfe) {
			}						
			
			int maxRetryAttemptsDefault = 0;
			String maxRetryAttemptsDefaultString = getValueConf(
					MAX_RETRY_ATTEMPTS_DEFAULT, PACKAGE_LCL_ERROR, "true",
					"", "", "", "");		
			try {
				maxRetryAttemptsDefault = Integer.valueOf(maxRetryAttemptsDefaultString);
			} catch (NumberFormatException nfe) {}
	
			int maxRetryAttemptsBeforeAbort = 0;
			String maxRetryAttemptsBeforeCancelRetryString = getValueConf(
					MAX_RETRY_ATTEMPTS_BEFORE_CANCEL_RETRY, PACKAGE_LCL_ERROR, "true",
					"", "", "", "");		
			try {
				maxRetryAttemptsBeforeAbort = Integer.valueOf(maxRetryAttemptsBeforeCancelRetryString);
			} catch (NumberFormatException nfe) {}
	
			// Determine errorToBeThrown and maxRetryAttempts according to retry configuration:
			if (breakRetryLoop) {
				genericValueMap.put(EHD_ATTRIBUTE_RETURN_VALUE_ID, CONTINUE.equals(errorToBeThrown)?CONTINUE:ABORT);
				genericValueMap.put(EHD_ATTRIBUTE_MAX_RETRY_ATTEMPTS, String.valueOf(retryCount));
			} else if (maxRetryAttempts != null && maxRetryAttempts == -1 && retryCount < maxRetryAttemptsBeforeAbort) {
				genericValueMap.put(EHD_ATTRIBUTE_RETURN_VALUE_ID, RETRY);
				genericValueMap.put(EHD_ATTRIBUTE_MAX_RETRY_ATTEMPTS, maxRetryAttemptsBeforeCancelRetryString);
			} else {
				if (maxRetryAttempts != null && retryCount < maxRetryAttempts) {
					genericValueMap.put(EHD_ATTRIBUTE_RETURN_VALUE_ID, RETRY);
					genericValueMap.put(EHD_ATTRIBUTE_MAX_RETRY_ATTEMPTS, maxRetryAttemptsString);
				} else {
					 if (RETRY.equals(errorToBeThrown) && maxRetryAttempts == null && retryCount < maxRetryAttemptsDefault) {
						 genericValueMap.put(EHD_ATTRIBUTE_RETURN_VALUE_ID, RETRY);
						 genericValueMap.put(EHD_ATTRIBUTE_MAX_RETRY_ATTEMPTS, maxRetryAttemptsDefaultString);
					 } else {
						 if (CONTINUE.equals(errorToBeThrown)) {
							 genericValueMap.put(EHD_ATTRIBUTE_RETURN_VALUE_ID, CONTINUE);
						 } else {
							 // Final behavior for abort and retry: abort
							 genericValueMap.put(EHD_ATTRIBUTE_RETURN_VALUE_ID, ABORT);
						 }
						 // maxRetryAttempts is reached or was not defined (-> 0)
						 String maxRetryAttemptsResult = maxRetryAttempts != null ? maxRetryAttemptsString : "0";
						 genericValueMap.put(EHD_ATTRIBUTE_MAX_RETRY_ATTEMPTS, maxRetryAttemptsResult);
					 }
				}
			}
			return genericValueMap;
		}
		
		/**
		 * Filters the error hierarchy for the deepest service --> shows
		 * in which service of our code the error occured
		 * 
		 * @param hierachicalErrorInfo
		 * @return the level of the deepest service name
		 */
		private static int getDeepestApplicationServiceLevel(ArrayList<ErrorInfo> hierachicalErrorInfo) {
			String service = null;
			for (int level = hierachicalErrorInfo.size() - 1; level > 0; level--) {
				service = hierachicalErrorInfo.get(level).getService();
				// create of List of Flownames to ignore
				List<String> ignoreFlows = Arrays.asList("wx.resilience.pub.resilience:throwError",
						 								"wx.resilience.pub.resilience:throwErrorIfNecessary", 
														 "wx.resilience.pub.resilience:postProcessForTopLevelService");
				
				
				
				
				
				if (service != null && 
					!service.startsWith("pub.") &&
					!service.startsWith("wm.") && 
					!ignoreFlows.contains(service)
				) {
					return level;
				}
			}
	
			return 0;
		}
	
		
		/**
		 * Logs message from a catalog.
		 * 
		 * @param aFacilityKey
		 *            defines the facility in the catalog
		 * @param aMessageKey
		 *            corresponds with the message key in the catalog
		 * @param aMessageParams
		 *            if message in the catalog has parameters
		 * @throws ServiceException
		 *             thrown if the logging failed
		 */
		/*private static void logMessageFromCatalog(String aFacilityKey, String aMessageKey, String[] aMessageParams) {
			// input
			IData tInput = IDataFactory.create();
			IDataCursor inputCursor = tInput.getCursor();
			IDataUtil.put(inputCursor, "facilityKey", aFacilityKey);
			IDataUtil.put(inputCursor, "messageKey", aMessageKey);
		
			// There are some message params provided
			if (aMessageParams.length > 0) {
				IDataUtil.put(inputCursor, "messageParams", aMessageParams);
			}
			inputCursor.destroy();
		
			try {
				Service.doInvoke("wx.resilience.impl:", "logMessageFromCatalog", tInput);
			} 
			catch (Exception e) {
			}
		}*/
	
		
		private static void validateXML() throws ServiceException {
			Log.logInfo("Validating the summarized error handling");
			File xmlFile = new File(ServerAPI.getPackageConfigDir(WX_RESILIENCE) + "/" + SUMMARIZED_EXCEPTION_HANDLING_FILE);
			File schemaFile = new File(ServerAPI.getPackageConfigDir(WX_RESILIENCE) + "/" + EXCEPTION_HANDLING_XSD_FILE);
			
			try {
				// 1. Lookup a factory for the W3C XML Schema language
				SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
				// 2. Compile the schema.
				// Here the schema is loaded from a java.io.File, but you could use
				// a java.net.URL or a javax.xml.transform.Source instead.
				Schema schema = factory.newSchema(schemaFile);	
				// 3. Get a validator from the schema.
				Validator validator = schema.newValidator();	
				// 4. Parse the document you want to check.
				Source source = new StreamSource(xmlFile);	
				// 5. Check the document
				validator.validate(source);
				Log.logInfo(xmlFile.getName() + " is valid");
			} 
			catch (SAXException|IOException ex) {
				String message = xmlFile.getName() + " is not valid because \n" + ex.getMessage();
				final ServiceException se = new ServiceException(message);
				se.initCause(ex);
				throw se;
			}	
		}
	
		private static String getValueConf(String key, String wxConfigPkgName,
				String noServiceException, String substituteVariables,
				String ignoreGlobalValues, String scanAllConfigurations,
				String noVariableInterpolation) {
			String propertyValue = " ";
			// input
			IData configParam = IDataFactory.create();
			IDataCursor configParamCursor = configParam.getCursor();
			IDataUtil.put(configParamCursor, "key", key);
			IDataUtil.put(configParamCursor, "wxConfigPkgName", wxConfigPkgName);
			IDataUtil.put(configParamCursor, "noServiceException",
					noServiceException);
			configParamCursor.destroy();
		
			// output
			IData configOutput = IDataFactory.create();
			try {
				configOutput = Service.doInvoke("wx.config.pub", "getValue",
						configParam);
			} catch (Exception e) {
				return propertyValue;
			}
			IDataCursor configOutputCursor = configOutput.getCursor();
			propertyValue = IDataUtil
					.getString(configOutputCursor, "propertyValue");
			configOutputCursor.destroy();
			Log.logDebug("getValueConf for " + key + " returns: " + propertyValue);
			return propertyValue;
		}
		
		private static int getRetryCount() {
			IData input = null;
			// output
			IData output = IDataFactory.create();
			try {
				output = Service.doInvoke("pub.flow", "getRetryCount", input);
				IDataCursor outputCursor = output.getCursor();
				return Integer.parseInt(IDataUtil.getString(outputCursor, "retryCount"));
			} catch (Exception e) {
				return 0;
			}
		}
		
		/**
		 * Extracts an error hierarchy for a given error hierarchy (normally only
		 * filled by one (root) error). Uses the callStack and the deepest errorInfo to build hiearchy.
		 * 
		 * @param pErrorHierarchy
		 *            - array list of IDataCursors on pub.event:exceptionInfo
		 * @return full array list array list of ErrorInfo on
		 *         pub.event:exceptionInfo, containing all errors from root(0) to
		 *         origin (size of array list)
		 */
		private static ArrayList<ErrorInfo> extractErrorHierarchy(ArrayList<IDataCursor> pErrorHierarchy) {	
			if (pErrorHierarchy == null) {
				return null;
			}
			
			IDataCursor deepestNestedError = getDeepestNestedError(pErrorHierarchy);
			
			// get callStack of error:
			IData[] callStack = IDataUtil.getIDataArray(pErrorHierarchy.get(0), "callStack");
			ArrayList<ErrorInfo> errorInfoList = null;
			
			if (callStack != null) {
				errorInfoList = new ArrayList<ErrorInfo>(callStack.length);
				
				// use the error and errorType of the deepest errorInfo to determine the error and errorType of each level:
				String nestedErrorType = IDataUtil.getString(deepestNestedError, ERROR_INFO_ERROR_TYPE);
				String nestedError = IDataUtil.getString(deepestNestedError, ERROR_INFO_ERROR);
	
				// the callstack is ordered inverse!
				for (int i = callStack.length - 1 ; i >= 0; i--) {
					// use the service names of the callstack:
					IDataCursor callStackCursor = callStack[i].getCursor();
					String service = IDataUtil.getString(callStackCursor, ERROR_INFO_CALL_STACK_SERVICE);
					String flowStep = IDataUtil.getString(callStackCursor, ERROR_INFO_CALL_STACK_FLOW_STEP);
					if (flowStep == null || flowStep.equals("")) {
						flowStep = "n.a.";
					}
					
					errorInfoList.add(new ErrorInfo(nestedError, nestedErrorType, service, flowStep));
				}
			}
			return errorInfoList;
		}
		
		/**
		 * Gets the deepest error in the error hierarchy
		 * 
		 * @param pErrorHierarchy
		 *            - array list of IDataCursors on pub.event:exceptionInfo
		 * @return cursor of the deepest error
		 */
		private static IDataCursor getDeepestNestedError(ArrayList<IDataCursor> pErrorHierarchy) {		
			
			IDataCursor nestedError = pErrorHierarchy.get(0);
			IDataCursor currentError = null;
			IData nestedErrorIData = null;
			do {
				currentError = nestedError;
				nestedErrorIData = IDataUtil.getIData(currentError, ERROR_INFO_NESTED_ERROR_ID);
				if (nestedErrorIData != null) {
					nestedError = nestedErrorIData.getCursor();
				} else {
					nestedError = null;
				}
			} while (nestedError != null);
	
			return currentError;
	
		}	
	
		/*public static class Log {
			private static final Log INSTANCE = new Log();
			private static final boolean logging = false;
	
			public static void init() {
				if (logging) {
					INSTANCE.initWriter();
				}
			}
			public static void write(String pMessage) {
				if (logging) {
					INSTANCE.log(pMessage);
				}
			}
			public static void close() {
				if (logging) {
					INSTANCE.closeWriter();
				}
			}
			public static boolean isLogging() {
				return logging;
			}
	
			private boolean initialized;
			private void initWriter() {
				initialized = true;
			}
			private void closeWriter() {
				initialized = false;
			}
			private void log(String pMessage) {
				if (initialized) {
					// TODO:
					//Log.logTraceMessage("EHF", pMessage);
				}
			}
		}*/
		public static class ErrorHandlingException extends RuntimeException {
			private static final long serialVersionUID = -3950100741636479681L;
	
			public ErrorHandlingException(String pMsg, Throwable pCause) {
				super(pMsg, pCause);
			}
	
			public ErrorHandlingException(String pMsg) {
				super(pMsg);
			}
	
			public ErrorHandlingException(Throwable pCause) {
				super(pCause);
			}
		}
	
		public static class ExceptionHandlingInfo {
			// Old representation of ExceptionHandlingInfo (deprecated)
			private final List<String> serviceNames = new ArrayList<>();
			private final String  errorToBeThrown, type, errorCode, exceptionType, maxRetryAttempts;
			private final boolean printBusinessObject;
			public ExceptionHandlingInfo(String pErrorToBeThrown, String pType, String pErrorCode,
					 					 String pExceptionType, String pMaxRetryAttempts,
					 					 boolean pPrintBusinessObject) {
				errorToBeThrown = pErrorToBeThrown;
				type = pType;
				errorCode = pErrorCode;
				exceptionType = pExceptionType;
				maxRetryAttempts = pMaxRetryAttempts;
				printBusinessObject = pPrintBusinessObject;
			}
	
			public List<String> getHandlingServices() {
				return serviceNames;
			}
	
			public void addServiceName(String pServiceName) {
				serviceNames.add(pServiceName);
			}
	
			public String getErrorToBeThrown() {
				return errorToBeThrown;
			}
	
			public String getType() {
				return type;
			}
	
			public String getErrorCode() {
				return errorCode;
			}
	
			public String getExceptionType() {
				return exceptionType;
			}
	
			public String getMaxRetryAttempts() {
				return maxRetryAttempts;
			}
	
			public boolean isPrintBusinessObject() {
				return printBusinessObject;
			}
	
			@Override
			public String toString() {
				final StringBuilder sb = new StringBuilder();
				sb.append(super.toString());
				sb.append(": errorToBeThrown= ");
				sb.append(errorToBeThrown);
				sb.append(", type=");
				sb.append(type);
				sb.append("errorCode=");
				sb.append(errorCode);
				sb.append(", project=");
				sb.append(", exceptionType=");
				sb.append(exceptionType);
				sb.append(", maxRetryAttempts=");
				sb.append(maxRetryAttempts);
				sb.append(", printBusinessObject=");
				sb.append(printBusinessObject);
				sb.append(", serviceNames=");
				// TODO:
				//sb.append(Strings.asString(serviceNames));
				return sb.toString();
			}
			
		}
		
		/**
		 * Extract handlings for an error at a location from a given exception
		 * handling definition
		 * 
		 * @param pErrorHandling
		 *            definition as DOM document to use for extraction
		 * @param pLocationXPath
		 *            error location as XPATH
		 * @param pExceptionType
		 *            error type as String
		 * @param currentCallerType
		 *            caller type as String	
		 * @param currentErrorMessage
		 *            original error as String 
		 * @return all found handlings for the given error as an array list, empty
		 *         list in case of no found error
		 * @throws ServiceException
		 */
		@SuppressWarnings("unchecked")
		private static ExceptionHandlingInfo getHandlingsForLocation(
				Document pErrorHandling, String pLocationXPath,
				String pExceptionType, String currentCallerType, String currentErrorMessage)
				throws ServiceException {
			Log.logDebug("getHandlingsForLocation: --> " + pLocationXPath + ", " + pExceptionType + ", " + currentCallerType + ", " + currentErrorMessage);
	
			// get exception list for location
			List<Element> exceptionNodes = null;
			try {
				exceptionNodes = (List<Element>) XPath.selectNodes(pErrorHandling, pLocationXPath);
			} 
			catch (JDOMException e) {
				Log.logError("JDOM Error during extraction of error types from exception handling definition: " + e.getMessage());
				Log.logDebug("getHandlingsForLocation: <-- null (JDOMException: " + e.getMessage() + ")");
				return null;
			}
	        // check exception nodes sanity
			if (exceptionNodes == null || exceptionNodes.isEmpty()) {
				Log.logDebug("getHandlingsForLocation: <-- null (No exceptionNodes)");
				return null;
			}			
			
			// extract exceptions from list, if location exists and exception
			// definitions are contained
			// iteration over all defined exceptions in location
			ExceptionHandlingInfo exceptionHandlingInfo = null;
			Element exceptionHandlingElement = null;
			for (Element exceptionNode : exceptionNodes) {
				boolean isErrorMessageContainsCheckTrue = false;
				final String errorMessageContainsFromDefinition = exceptionNode.getAttributeValue(EHD_ATTRIBUTE_ERROR_MESSAGE_CONTAINS);
				if (errorMessageContainsFromDefinition == null ||
					(currentErrorMessage != null &&
					errorMessageContainsFromDefinition != null &&
					currentErrorMessage.contains(errorMessageContainsFromDefinition))) 
				{
					isErrorMessageContainsCheckTrue = true;
				}
	
				// Check for optional attribut errorMessageRegex:
				boolean isErrorMessageRegexCheckTrue = false;
				final String errorMessageRegexFromDefinition = exceptionNode.getAttributeValue(EHD_ATTRIBUTE_ERROR_MESSAGE_REGEX_ID);
				if (errorMessageRegexFromDefinition == null ||
					(currentErrorMessage != null &&
					errorMessageRegexFromDefinition != null &&
					currentErrorMessage.matches(errorMessageRegexFromDefinition))) 
				{
					isErrorMessageRegexCheckTrue = true;
				}
				
				// Check for optional attribut callerType:
				boolean isCallerTypeCheckTrue = false;
				final String callerTypeFromDefinition = exceptionNode.getAttributeValue(EHD_ATTRIBUTE_ERROR_CALLER_TYPE_ID);
				if (callerTypeFromDefinition == null ||
					(currentCallerType != null &&
					callerTypeFromDefinition != null &&
					currentCallerType.equals(callerTypeFromDefinition))) 
				{
					isCallerTypeCheckTrue = true;
				}
				
				// if defined exception type equals exception, set handling and
				// exit loop
				final String exceptionTypeFromDefinition = exceptionNode.getAttributeValue(EHD_ATTRIBUTE_ERROR_TYPE_ID);
				if (exceptionTypeFromDefinition.equals(pExceptionType) && isErrorMessageContainsCheckTrue && isErrorMessageRegexCheckTrue && isCallerTypeCheckTrue) {
					exceptionHandlingInfo = getRetVal(exceptionNode, pExceptionType);
					exceptionHandlingElement = exceptionNode;
					break;
				}	
				  
				// if generic handling is allowed and defined (type="all"), set (cache)
				// generic handling
				else if (exceptionTypeFromDefinition.equals(EHD_VALUE_GENERIC_ERROR_TYPE_ID) && isErrorMessageContainsCheckTrue && isErrorMessageRegexCheckTrue && isCallerTypeCheckTrue) {
					exceptionHandlingInfo = getRetVal(exceptionNode, pExceptionType);
					exceptionHandlingElement = exceptionNode;
					break;
				}
			}
	
			if (exceptionHandlingElement == null) {
				return null;
			} else {
				// check handling nodes sanity
				for (Object n : exceptionHandlingElement.getChildren(EHD_ATTRIBUTE_HANDLING)) {
						exceptionHandlingInfo.addServiceName(((Element) n).getText());
				}
	
				Log.logDebug("getHandlingsForLocation: <-- " + exceptionHandlingInfo);
				return exceptionHandlingInfo;
			}
			
		}
		
		private static ExceptionHandlingInfo getRetVal(Element exceptionNode, String exceptionType) {	
			final String errorToBeThrown = exceptionNode.getAttributeValue(EHD_ATTRIBUTE_RETURN_VALUE_ID);
			final String type = exceptionNode.getAttributeValue(EHD_ATTRIBUTE_ERROR_TYPE_ID);
			final String exceptionHandlingId = exceptionNode.getAttributeValue(EHD_ATTRIBUTE_ERROR_HANDLING_ID_ID);
			final String maxRetryAttempts = exceptionNode.getAttributeValue(EHD_ATTRIBUTE_MAX_RETRY_ATTEMPTS);
			final String printBusinessObjectStr = exceptionNode.getAttributeValue(EHD_PRINT_BUSINESS_OBJECT_ID);
			final boolean printBusinessObject = printBusinessObjectStr == null ? false : Boolean.parseBoolean(printBusinessObjectStr);
			return new ExceptionHandlingInfo(errorToBeThrown, type, exceptionHandlingId, exceptionType, maxRetryAttempts, printBusinessObject);
		}
		
	
		private static IData buildCompactErrorInfo(String pErrorLocation, String flowStep) {
			IData compactErrorInfo = IDataFactory.create();
			IDataCursor compactErrorInfoCursor = compactErrorInfo.getCursor();
	
			IDataUtil.put(compactErrorInfoCursor, COMPACT_ERROR_LOCATION_ID, pErrorLocation);
			IDataUtil.put(compactErrorInfoCursor, COMPACT_ERROR_FLOW_STEP, flowStep);
	
			compactErrorInfoCursor.destroy();
	
			return compactErrorInfo;
		}
			
		@SuppressWarnings("rawtypes")
		private static final NSService getCurrentTopLevelService() {
			NSService topLevelServiceName = null;
			InvokeState state = InvokeState.getCurrentState();
			if (state != null) {
				Stack callStack = state.getCallStack();
				if (callStack != null && callStack.size() > 0) {
					topLevelServiceName = (NSService) callStack.elementAt(0);
				}
			}
			return topLevelServiceName;
		}
		
		private static final String getMessageUID(IData metaData) {
			if(metaData != null) {
				IDataMap metaDataMap = new IDataMap(metaData);
				return metaDataMap.getAsString(UID_ID);
			}
			return null;
		}
	
		public static void initHandlings(URL pUrl, SAXBuilder pBuilder) {
			final SAXBuilder builder;
			if (pBuilder == null) {
				builder = new SAXBuilder();
			} else {
				builder = pBuilder;
			}
			final Document doc = loadHandlings(pUrl, builder);
			synchronized(exceptionHandlingLock) {
				_exceptionHandling = doc;
			}
		}
		
		private static Document loadHandlings(URL pFile, SAXBuilder pBuilder) {
			// Load exception handling definition file
			
			try {
				final Document ehd = pBuilder.build(pFile);
			
				Log.logInfo("Successfully initialized error handling");
				return ehd;
			} 
			catch (JDOMException e) {
				Log.logError("JDOM Error during initialization of error handling: " + e.getMessage());
				throw new UndeclaredThrowableException(e);
			} 
			catch (IOException e) {
				Log.logError("IO Error during initialization of error handling: " + e.getMessage());
				throw new UndeclaredThrowableException(e);
			}
		}
	
	
		private static String asString(String[] tempParts) {
			StringBuilder builder = new StringBuilder();
			for (String string : tempParts) {
				builder.append(string).append(",");
			}
			return builder.toString();
		}
	
		
		// *******************************************************************
		// ---------------------XML NODE NAME DEFINITIONS---------------------
		// *******************************************************************
		private static final String EHD_NODE_DOC_ROOT_ID = "exceptionHandling";
		private static final String EHD_NODE_GLOBAL_ERROR_TYPE_ID = "globalException";
		private static final String EHD_NODE_ERROR_LOCATION_ID = "location";
		private static final String EHD_NODE_ERROR_TYPE_ID = "exception";
	
		// *******************************************************************
		// -------------------XML NODE ATTRIBUTE DEFINITIONS------------------
		// *******************************************************************
		private static final String EHD_ATTRIBUTE_ERROR_TYPE_ID = "type";
		private static final String EHD_ATTRIBUTE_ERROR_CALLER_TYPE_ID = "callerType";		
		private static final String EHD_ATTRIBUTE_ERROR_MESSAGE_CONTAINS = "errorMessageContains";		
		private static final String EHD_ATTRIBUTE_ERROR_MESSAGE_REGEX_ID = "errorMessageRegex";		
		private static final String EHD_ATTRIBUTE_LOCATION_NAME_ID = "name";
		private static final String EHD_ATTRIBUTE_RETURN_VALUE_ID = "errorToBeThrown";
		private static final String EHD_ATTRIBUTE_ERROR_HANDLING_ID_ID = "exceptionHandlingId";
		private static final String EHD_ATTRIBUTE_HANDLING= "handling";
		private static final String EHD_ATTRIBUTE_MAX_RETRY_ATTEMPTS = "maxRetryAttempts";
		private static final String EHD_ATTRIBUTE_RETRY_COUNT = "retryCount";
		private static final String EHD_PRINT_BUSINESS_OBJECT_ID = "printBusinessObject";
	
		// *******************************************************************
		// ---------------------XML NODE VALUE DEFINITIONS--------------------
		// *******************************************************************
		private static final String EHD_VALUE_GENERIC_ERROR_TYPE_ID = "all";
		private static final String EHD_VALUE_GLOBAL_ERROR_HANDLING_ID = "global";
	
		// *******************************************************************
		// ------------------INPUT ERROR FIELD ID DEFINITIONS-----------------
		// *******************************************************************
		private static final String ERROR_INFO_ID = "exceptionInfo";
		private static final String EXCLUDE_SERVICE = "@WxResilience.errorHandling.excludeServiceFromErrorHandling@";
		
		private static final String ERROR_INFO_ERROR = "error";
		private static final String ERROR_INFO_ERROR_TYPE = "errorType";
		private static final String ERROR_INFO_CALL_STACK_SERVICE = "service";
		private static final String ERROR_INFO_CALL_STACK_FLOW_STEP = "flowStep";
		private static final String ERROR_INFO_CALL_STACK = "callStack";
		private static final String ERROR_INFO_ERROR_DUMP = "errorDump";
		private static final String ERROR_INFO_TIME = "time";
		private static final String ERROR_INFO_NESTED_ERROR_ID = "nestedErrorInfo";
	
		private static final String GENERIC_VALUE_MAP_ID = "genericValueMap";
		private static final String GENERIC_ERROR_MESSAGE_ID = "genericErrorMessage";
	
		// *******************************************************************
		// --------------OUTPUT HANDLED ERROR FIELD ID DEFINITIONS------------
		// *******************************************************************
		private static final String HANDLED_ERROR_INFO_ID = "handledErrorInfo";
		
		private static final String HANDLED_ERROR_INFO_ROOT_ID = "topServiceError";
		private static final String HANDLED_ERROR_INFO_HANDLED_ID = "handledServiceError";
		private static final String HANDLED_ERROR_INFO_APPLICATION_SERVICE_ID = "applicationServiceError";
		private static final String HANDLED_ERROR_INFO_ORIGIN_ID = "rootServiceError";
	
		private static final String HANDLED_ERROR_INFO_HANDLING_LOCATION_ID = "handlingLocation";
		private static final String HANDLED_ERROR_INFO_HANDLING_TYPE_ID = "handlingType";
		private static final String HANDLED_ERROR_INFO_DEPTH_ID = "errorDepth";
		private static final String HANDLED_ERROR_INFO_ERROR_CODE = "errorCode";
		private static final String HANDLED_ERROR_INFO_CALL_STACK = "callStack";
		private static final String HANDLED_ERROR_INFO_ERROR_DUMP = "errorDump";
		private static final String HANDLED_ERROR_INFO_TIME = "time";
		private static final String HANDLED_ERROR_INFO_PIPELINE = "pipeline";
	
		private static final String ERROR_MSG_ID = "errorMessage";
		private static final String COMPACT_ERROR_LOCATION_ID = "errorLocation";
		private static final String COMPACT_ERROR_FLOW_STEP = "errorFlowStep";
		private static final String ERROR_TYPE_ID = "errorType";
	
		// *******************************************************************
		// -------------------GENERIC DOC FIELD ID DEFINITIONS----------------
		// *******************************************************************
		private static final String BUSINESS_DOC_ID = "businessObject";
		private static final String PRINT_BUSINESS_OBJECT_ID = "printBusinessObject";
	
		// *******************************************************************
		// --------------------EXCEPTION HANDLING DEFINITION------------------
		// *******************************************************************
		private static final String METADATA_ID = "metaData";
		private static final String UID_ID = "uuid";
	
		// *******************************************************************
		// --------------------EXCEPTION HANDLING DEFINITION------------------
		// *******************************************************************
		private static Document _exceptionHandling;
		private static final Object exceptionHandlingLock = new Object();
		private static ExceptionHandlingListener _exceptionHandlingListener;
		private static CallerTypeSupplier DEFAULT_CALLER_TYPE_SUPPLIER = new CallerTypeSupplier() {
			@Override
			public String get() {
				return getCallerTypeByCallingService();
			}
		};
		private static CallerTypeSupplier callerTypeSupplier = null;
		private static final Object callerTypeSupplierLock = new Object();
		private static final Object exceptionHandlingListenerLock = new Object();
		private static final Object createSummarizedExceptionHandlingLock = new Object();
		// *******************************************************************
		// --------------------INPUT GET VALUE DEFINITION---------------------
		// *******************************************************************
		private static final String MAX_RETRY_ATTEMPTS_DEFAULT = "maxRetryAttemptsDefault";
		private static final String MAX_RETRY_ATTEMPTS_BEFORE_CANCEL_RETRY = "maxRetryAttemptsBeforeCancelRetry";
		private static final String PACKAGE_LCL_ERROR = "WxResilience";
		private static final String BREAK_RETRY_LOOP = "break.retry.loop.for.";
		// *******************************************************************
		// --------------------ERROR VALUE DEFINITION---------------------
		// *******************************************************************
		private static final String RETRY = "TRANSIENT";
		private static final String ABORT = "FATAL";
		private static final String CONTINUE = "NONE";	
		
		private static final String SUMMARIZED_EXCEPTION_HANDLING_FILE = "ExceptionHandlingSummarized.xml";
		private static final String EXCEPTION_HANDLING_XSD_FILE = "ExceptionHandling.xsd";
		private static final String WX_RESILIENCE = "WxResilience";
		private static final String ERROR_HANDLING_XML_FILE = "ExceptionHandling.xml";
	
	
		
		
		
		
		
	// --- <<IS-END-SHARED>> ---
}

