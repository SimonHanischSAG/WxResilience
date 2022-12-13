package wx.resilienceDemoInvokeChain;

// -----( IS Java Code Template v1.2

import com.wm.data.*;
import com.wm.util.Values;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
// --- <<IS-START-IMPORTS>> ---
// --- <<IS-END-IMPORTS>> ---

public final class impl

{
	// ---( internal utility methods )---

	final static impl _instance = new impl();

	static impl _newInstance() { return new impl(); }

	static impl _cast(Object o) { return (impl)o; }

	// ---( server methods )---




	public static final void testJavaException (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(testJavaException)>> ---
		// @sigtype java 3.5
		throw new ServiceException("Error!");
		// --- <<IS-END>> ---

                
	}
}

