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
	
	private static String shortenType(String type){
		type = type.replaceAll("^ZWPD", "Z");
		type = type.replaceAll("^ZWP", "Z");
		type = type.replaceAll("^WPD", "");
		type = type.replaceAll("^WP", "");
		type = type.replaceAll("_", "");
		type = type.replaceAll("1", "");
		type = type.replaceAll("2", "");
		type = type.replaceAll("3", "");
		type = type.replaceAll("4", "");
		type = type.replaceAll("5", "");
		type = type.replaceAll("6", "");
		type = type.replaceAll("7", "");
		type = type.replaceAll("8", "");
		type = type.replaceAll("9", "");
		type = type.replaceAll("0", "");
		type = type.toLowerCase();
		return type;
	}
	
	private static String join(String[] list, String joinString) {
	   if (list == null || list.length == 0) {
		   return "";
	   }
	   
	   StringBuilder sb = new StringBuilder();	   
	   boolean first = true;
	   for (String item : list) {
	      if (first) {
	         first = false;
	      }
	      else {
	         sb.append(joinString);
	      }
	      sb.append(item);
	   }
	   
	   return sb.toString();
	}	
	
	private static String createMesType(String IDocType){
		IDocType = IDocType.replaceAll("1", "");
		IDocType = IDocType.replaceAll("2", "");
		IDocType = IDocType.replaceAll("3", "");
		IDocType = IDocType.replaceAll("4", "");
		IDocType = IDocType.replaceAll("5", "");
		IDocType = IDocType.replaceAll("6", "");
		IDocType = IDocType.replaceAll("7", "");
		IDocType = IDocType.replaceAll("8", "");
		IDocType = IDocType.replaceAll("9", "");
		IDocType = IDocType.replaceAll("0", "");
		return IDocType;
	}
		
	
	private static boolean isrfc(IData doc){
		boolean isrfc = false;
		if ( doc != null)
		{
			IDataCursor docCursor = doc.getCursor();
			if (IDataUtil.getString( docCursor, "$rfcname" )!=null){
				isrfc = true;
			}
			docCursor.destroy();
		}
		return isrfc;
	}
			
	// --- <<IS-END-SHARED>> ---
}

