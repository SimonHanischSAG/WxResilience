package wx.resilience.impl;

// -----( IS Java Code Template v1.2

import com.wm.data.*;
import com.wm.util.Values;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
// --- <<IS-START-IMPORTS>> ---
import com.wm.util.JournalLogger;
import java.util.Map.Entry;
import java.util.Set;
import com.softwareag.util.IDataMap;
import com.wm.app.b2b.server.InvokeState;
import com.wm.app.b2b.server.ServerAPI;
import com.wm.data.IData;
import com.wm.data.IDataCursor;
import com.wm.data.IDataUtil;
// --- <<IS-END-IMPORTS>> ---

public final class resilience

{
	// ---( internal utility methods )---

	final static resilience _instance = new resilience();

	static resilience _newInstance() { return new resilience(); }

	static resilience _cast(Object o) { return (resilience)o; }

	// ---( server methods )---




	public static final void getContextID (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(getContextID)>> ---
		// @sigtype java 3.5
		// [o] field:0:required contextID
		IDataMap pipeMap = new IDataMap(pipeline);
		String[] auditContext = ServerAPI.getAuditContext();
		/*for (String string : auditContext) {
			debugLogInfo(string);
		}*/
		if (auditContext != null && auditContext.length >= 2) {
			pipeMap.put("contextID", auditContext[1]);
		}
			
		// --- <<IS-END>> ---

                
	}

	// --- <<IS-START-SHARED>> ---
	private static final String LOG_FUNCTION = "WxResilience";
	
	private static void debugLogInfo(String message) {
	    JournalLogger.log(4,  JournalLogger.FAC_FLOW_SVC, JournalLogger.INFO, LOG_FUNCTION, message);
	}	
	// --- <<IS-END-SHARED>> ---
}

