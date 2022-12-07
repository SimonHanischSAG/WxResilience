package wx.resilience.impl;

// -----( IS Java Code Template v1.2

import com.wm.data.*;
import com.wm.util.Values;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
// --- <<IS-START-IMPORTS>> ---
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
		if (auditContext != null && auditContext.length > 0) {
			pipeMap.put("contextID", auditContext[0]);
		}
		// --- <<IS-END>> ---

                
	}
}

