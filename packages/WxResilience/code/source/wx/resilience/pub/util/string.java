package wx.resilience.pub.util;

// -----( IS Java Code Template v1.2

import com.wm.data.*;
import com.wm.util.Values;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
// --- <<IS-START-IMPORTS>> ---
import com.softwareag.util.IDataMap;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Date;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.text.SimpleDateFormat;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Collections;
import java.util.HashMap;
// --- <<IS-END-IMPORTS>> ---

public final class string

{
	// ---( internal utility methods )---

	final static string _instance = new string();

	static string _newInstance() { return new string(); }

	static string _cast(Object o) { return (string)o; }

	// ---( server methods )---



    public static final Values multipleConcat (Values in)
    {
        Values out = in;
		// --- <<IS-START(multipleConcat)>> ---
		// @sigtype java 3.0
		// [i] field:0:required inString1
		// [i] field:0:required inString2
		// [i] field:0:required inString3
		// [i] field:0:required inString4
		// [i] field:0:required inString5
		// [i] field:0:required inString6
		// [i] field:0:required inString7
		// [i] field:0:required inString8
		// [i] field:0:required inString9
		// [i] field:0:required inString10
		// [o] field:0:required value
		String str1 = checkNull(in.getString("inString1"));
		String str2 = checkNull(in.getString("inString2"));
		String str3 = checkNull(in.getString("inString3"));
		String str4 = checkNull(in.getString("inString4"));
		String str5 = checkNull(in.getString("inString5"));
		String str6 = checkNull(in.getString("inString6"));
		String str7 = checkNull(in.getString("inString7"));
		String str8 = checkNull(in.getString("inString8"));
		String str9 = checkNull(in.getString("inString9"));
		String str10 = checkNull(in.getString("inString10"));
		StringBuilder sb = new StringBuilder(); 
		
		String outStr = sb.append(str1).append(str2).append(str3).append(str4).append(str5).append(str6).append(str7).append(str8).append(str9).append(str10).toString();
		
		out.put("value", outStr);
		
		// --- <<IS-END>> ---
        return out;
                
	}



	public static final void substringNullSave (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(substringNullSave)>> ---
		// @sigtype java 3.5
		// [i] field:0:required inString
		// [i] field:0:required beginIndex
		// [i] field:0:required endIndex
		// [o] field:0:required value
		// pipeline
		IDataCursor pipelineCursor = pipeline.getCursor();
		String	inString = IDataUtil.getString( pipelineCursor, "inString" );
		String	beginIndex = IDataUtil.getString( pipelineCursor, "beginIndex" );
		String	endIndex = IDataUtil.getString( pipelineCursor, "endIndex" );
		pipelineCursor.destroy();
		
		String outString = "";
		try {
			Integer start = Integer.valueOf(beginIndex);
			Integer end = Integer.valueOf(endIndex);
			
			if (inString != null && start >= 0 && end >= 0 && start < end) {
				int stringLength = inString.length();				
				if (start < stringLength) {
					if (end <= stringLength) {
						outString = inString.substring(start, end);
					} 
					else {
						outString = inString.substring(start, stringLength);
					}					
				}
			}
		} 
		catch (NumberFormatException nfe) {
			
		}
		
		// pipeline
		IDataCursor outputCursor = pipeline.getCursor();
		IDataUtil.put( outputCursor, "value", outString );
		outputCursor.destroy();
			
			
		// --- <<IS-END>> ---

                
	}

	// --- <<IS-START-SHARED>> ---
	/**
	 * Used by "multiConcat"
	 * 
	 */
	private static String checkNull(String inputString)
	{
	  if (inputString == null)
	    return "";
	  else
	    return inputString;
	}
	
	
		
		
	// --- <<IS-END-SHARED>> ---
}

