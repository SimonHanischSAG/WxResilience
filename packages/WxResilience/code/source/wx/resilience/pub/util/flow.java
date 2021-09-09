package wx.resilience.pub.util;

// -----( IS Java Code Template v1.2

import com.wm.data.*;
import com.wm.util.Values;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
// --- <<IS-START-IMPORTS>> ---
import com.wm.app.b2b.server.ns.Namespace;
import com.softwareag.util.IDataMap;
import com.wm.app.b2b.server.*;
import com.wm.lang.flow.FlowElement;
import com.wm.lang.flow.FlowState;
import com.wm.lang.ns.NSName;
import com.wm.lang.ns.NSService;
import java.util.Stack;
// --- <<IS-END-IMPORTS>> ---

public final class flow

{
	// ---( internal utility methods )---

	final static flow _instance = new flow();

	static flow _newInstance() { return new flow(); }

	static flow _cast(Object o) { return (flow)o; }

	// ---( server methods )---

	public static final void getCallingService(IData pipeline) throws ServiceException {
		IDataCursor cursor = pipeline.getCursor();
		String level = IDataUtil.getString(cursor, "level");
		int lev;
		try {
			lev = Integer.parseInt(level);
		} catch (Exception e) {
			lev = 2;
		}
		NSService currentSvc = Service.getServiceEntry();
		Stack<?> callStack = InvokeState.getCurrentState().getCallStack();
		int index = callStack.indexOf(currentSvc);

		if (index >= 0 && lev <= index && lev >= 0) {
			IDataUtil.put(cursor, "callingServiceName", ((NSService) callStack.elementAt(index - lev)).toString());
		}
		cursor.destroy();
	}
	
	// --- <<IS-START-SHARED>> ---
	

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

