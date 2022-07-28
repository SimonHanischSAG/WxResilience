package wx.resilience.pub.util;

// -----( IS Java Code Template v1.2

import com.wm.data.*;
import com.wm.util.Values;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
// --- <<IS-START-IMPORTS>> ---
import java.net.InetAddress;
// --- <<IS-END-IMPORTS>> ---

public final class operatingSystem

{
	// ---( internal utility methods )---

	final static operatingSystem _instance = new operatingSystem();

	static operatingSystem _newInstance() { return new operatingSystem(); }

	static operatingSystem _cast(Object o) { return (operatingSystem)o; }

	// ---( server methods )---




	public static final void getHostname (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(getHostname)>> ---
		// @sigtype java 3.5
		// [o] field:0:required result
		String hostname = null;
		// pipeline
		
		try {
			hostname = InetAddress.getLocalHost().getCanonicalHostName().toLowerCase();
		} catch (Exception e) {
		}
		
		// pipeline
		IDataCursor pipelineCursor = pipeline.getCursor();
		IDataUtil.put(pipelineCursor, "result", hostname);
		pipelineCursor.destroy();
		// --- <<IS-END>> ---

                
	}
}

