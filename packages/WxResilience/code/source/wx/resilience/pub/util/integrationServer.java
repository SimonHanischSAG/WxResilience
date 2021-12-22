package wx.resilience.pub.util;

// -----( IS Java Code Template v1.2

import com.wm.data.*;
import com.wm.util.Values;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
// --- <<IS-START-IMPORTS>> ---
import com.wm.app.b2b.server.ListenerAdmin;
import com.wm.app.b2b.server.ServerListenerIf;
// --- <<IS-END-IMPORTS>> ---

public final class integrationServer

{
	// ---( internal utility methods )---

	final static integrationServer _instance = new integrationServer();

	static integrationServer _newInstance() { return new integrationServer(); }

	static integrationServer _cast(Object o) { return (integrationServer)o; }

	// ---( server methods )---




	public static final void getPrimaryPort (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(getPrimaryPort)>> ---
		// @sigtype java 3.5
		// [o] field:0:required result
		String serverPort = null;
		ServerListenerIf listener = ListenerAdmin.getPrimaryListener();
		if (listener != null) {
			serverPort = Integer.toString(listener.getPort());
		}
		
		// pipeline
		IDataCursor pipelineCursor = pipeline.getCursor();
		IDataUtil.put( pipelineCursor, "result", serverPort );
		pipelineCursor.destroy();
		// --- <<IS-END>> ---

                
	}
}

