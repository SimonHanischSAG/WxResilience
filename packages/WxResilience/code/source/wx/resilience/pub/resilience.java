package wx.resilience.pub;

// -----( IS Java Code Template v1.2

import com.wm.data.*;
import com.wm.util.Values;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
// --- <<IS-START-IMPORTS>> ---
// --- <<IS-END-IMPORTS>> ---

public final class resilience

{
	// ---( internal utility methods )---

	final static resilience _instance = new resilience();

	static resilience _newInstance() { return new resilience(); }

	static resilience _cast(Object o) { return (resilience)o; }

	// ---( server methods )---




	public static final void throwDocumentDiscardedException (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(throwDocumentDiscardedException)>> ---
		// @sigtype java 3.5
		// [i] field:0:required message
		IDataCursor pipelineCursor = pipeline.getCursor();
		String msg = IDataUtil.getString( pipelineCursor, "message");
		pipelineCursor.destroy();
		
		if (msg == null || msg.trim().isEmpty())
		{
			msg = "The Document is discarded.";	 	
		}		
		throw new DocumentDiscardedException(msg);	 
			
		// --- <<IS-END>> ---

                
	}



	public static final void throwException (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(throwException)>> ---
		// @sigtype java 3.5
		// [i] field:0:required message
		// pipeline
		IDataCursor pipelineCursor = pipeline.getCursor();
		
		String	msg = IDataUtil.getString( pipelineCursor, "message" );
		pipelineCursor.destroy();
		
		throw new ServiceException(msg);
			
		// --- <<IS-END>> ---

                
	}

	// --- <<IS-START-SHARED>> ---
	public static final class DocumentDiscardedException extends ServiceException {
		
		public DocumentDiscardedException(String arg0) {
			super(arg0);
		}
		
		private static final long serialVersionUID = 1L;
	
	}	
		
	// --- <<IS-END-SHARED>> ---
}

