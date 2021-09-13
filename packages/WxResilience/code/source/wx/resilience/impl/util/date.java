package wx.resilience.impl.util;

// -----( IS Java Code Template v1.2

import com.wm.data.*;
import com.wm.util.Values;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
// --- <<IS-START-IMPORTS>> ---
import com.softwareag.util.IDataMap;
import java.util.*;
// --- <<IS-END-IMPORTS>> ---

public final class date

{
	// ---( internal utility methods )---

	final static date _instance = new date();

	static date _newInstance() { return new date(); }

	static date _cast(Object o) { return (date)o; }

	// ---( server methods )---




	public static final void calculateDuration (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(calculateDuration)>> ---
		// @sigtype java 3.5
		// [i] object:0:required start
		// [i] object:0:required end
		// [o] field:0:required duration
		IDataMap pipeMap = new IDataMap(pipeline);
		
		Date start = (Date) pipeMap.get("start");
		Date end = (Date) pipeMap.get("end");
		
		pipeMap.put("duration", String.format(Locale.US, "%.3f", (end.getTime() - start.getTime())/1000.0));
			
			
		// --- <<IS-END>> ---

                
	}
}

