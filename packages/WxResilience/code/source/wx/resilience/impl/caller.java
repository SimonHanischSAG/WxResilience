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
import com.wm.data.IData;
import com.wm.data.IDataCursor;
import java.util.regex.Pattern;
import com.wm.data.IDataUtil;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.PatternSyntaxException;
// --- <<IS-END-IMPORTS>> ---

public final class caller

{
	// ---( internal utility methods )---

	final static caller _instance = new caller();

	static caller _newInstance() { return new caller(); }

	static caller _cast(Object o) { return (caller)o; }

	// ---( server methods )---




	public static final void findMatchesForRegex (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(findMatchesForRegex)>> ---
		// @sigtype java 3.5
		// [i] field:0:required string
		// [i] field:0:required regexp
		// [o] field:1:optional matches
		// [o] field:0:required resultType {"noMatch","matchNoGroups","matchWithGroups"}
		// [o] field:1:required groups
		final IDataCursor crsr = pipeline.getCursor();
		try {
			final String str = IDataUtil.getString(crsr, "string");
			if (str == null  ||  str.length() == 0) {
				throw new ServiceException("Missing, or empty parameter: string");
			}
			final String re = IDataUtil.getString(crsr, "regexp");
			if (re == null  ||  str.length() == 0) {
				throw new ServiceException("Missing, or empty parameter: regexp");
			}
			final Pattern p;
			try {
				p = Pattern.compile(re);
			} catch (PatternSyntaxException pse) {
				throw new ServiceException("Invalid parameter (Pattern syntax: regexp=" + re);
			}
			final Matcher m = p.matcher(str);
			if (m.find()) {
				final int cnt = m.groupCount();
				if (cnt == 0) {
					java.util.List<String> matches = new java.util.ArrayList<String>();
					matches.add(m.group());
					while(m.find()) {
						matches.add(m.group());
					}
					//	final String matchGroups = m.group();
					IDataUtil.put(crsr, "matches", matches.toArray());
				    IDataUtil.put(crsr, "resultType", "matchNoGroups");
				} else {
					
				ArrayList <String> matchedGroups = new ArrayList <String>();
				ArrayList <String> matches = new ArrayList <String>();
				matches.add(m.group(0));
				for (int i = 1;  i <= cnt;  i++) {
					matchedGroups.add(m.group(i));					
				 	}
				while (m.find()){
					matches.add(m.group(0));
					for (int i = 1;  i <= cnt;  i++) {						
						matchedGroups.add(m.group(i));						
					 	}
					}				
					IDataUtil.put(crsr, "groups", matchedGroups.toArray());
					IDataUtil.put(crsr, "matches", matches.toArray());
					IDataUtil.put(crsr, "resultType", "matchWithGroups");
				}
			} else {
				IDataUtil.put(crsr, "resultType", "noMatch");
			}		
			IDataUtil.remove(crsr, "string");
			IDataUtil.remove(crsr, "regexp");
		} catch (RuntimeException rte) {
			throw rte;
		} catch (Error e) {
			throw e;
		} catch (ServiceException se) {
			throw se;
		} catch (Throwable th) {
			throw new ServiceException(th);
		} finally {
			crsr.destroy();
		}				
			
		// --- <<IS-END>> ---

                
	}



	public static final void getCallstackAsString (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(getCallstackAsString)>> ---
		// @sigtype java 3.5
		// [o] field:0:required callStack
		IDataCursor idcPipeline = pipeline.getCursor();			
		String callStack = InvokeState.getCurrentState().getCallStackAsString() ;	
		idcPipeline.insertAfter("callStack", callStack);
		idcPipeline.destroy(); 
		// --- <<IS-END>> ---

                
	}



	public static final void getStartupSessionID (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(getStartupSessionID)>> ---
		// @sigtype java 3.5
		// [o] field:0:required sessionID
		IDataCursor idcPipeline = pipeline.getCursor();		
		String sessionID = InvokeState.getCurrentSession().getSessionID();
		idcPipeline.insertAfter("sessionID", sessionID);	
		idcPipeline.destroy(); 
		// --- <<IS-END>> ---

                
	}



	public static final void getThreadInfo (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(getThreadInfo)>> ---
		// @sigtype java 3.5
		// [o] field:0:required threadName
		IDataCursor idcPipeline = pipeline.getCursor();		
		String threadName = Thread.currentThread().getName();		
		idcPipeline.insertAfter("threadName", threadName);
		idcPipeline.destroy(); 
			
		// --- <<IS-END>> ---

                
	}
}

