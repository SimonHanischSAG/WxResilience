package wx.resilience.pub.util;

// -----( IS Java Code Template v1.2

import com.wm.data.*;
import com.wm.util.Values;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
// --- <<IS-START-IMPORTS>> ---
import java.io.File;
import com.softwareag.util.IDataMap;
// --- <<IS-END-IMPORTS>> ---

public final class file

{
	// ---( internal utility methods )---

	final static file _instance = new file();

	static file _newInstance() { return new file(); }

	static file _cast(Object o) { return (file)o; }

	// ---( server methods )---




	public static final void checkFileExistence (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(checkFileExistence)>> ---
		// @sigtype java 3.5
		// [i] field:0:required fileName
		// [o] field:0:required exists
		// [o] field:0:required isDirectory
		IDataMap pipeMap = new IDataMap(pipeline);
		String filename = pipeMap.getAsString("fileName" );
		
		File file = new File(filename);
				
		pipeMap.put("exists", file.exists() ? "true" : "false" );
		pipeMap.put("isDirectory", file.isDirectory() ? "true" : "false" );
			
		// --- <<IS-END>> ---

                
	}
}

