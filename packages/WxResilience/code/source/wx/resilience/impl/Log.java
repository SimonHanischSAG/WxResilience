package wx.resilience.impl;

import com.softwareag.util.IDataMap;
import com.wm.app.b2b.server.Service;
import com.wm.data.IData;
import com.wm.data.IDataCursor;
import com.wm.data.IDataFactory;
import com.wm.data.IDataUtil;

public class Log {

	public static void logInfo(String message) {
		IData input = IDataFactory.create();
		IDataMap inputMap = new IDataMap(input);
		inputMap.put("message", message);
		inputMap.put("level", "Info");
		
		try {
			Service.doInvoke("wx.resilience.impl", "log", input);
		} 
		catch (Exception e) {
		}
	}
	
	public static void logWarn(String message) {
		IData input = IDataFactory.create();
		IDataMap inputMap = new IDataMap(input);
		inputMap.put("message", message);
		inputMap.put("level", "Warn");
		
		try {
			Service.doInvoke("wx.resilience.impl", "log", input);
		} 
		catch (Exception e) {
		}
	}
	
	public static void logError(String message) {
		IData input = IDataFactory.create();
		IDataMap inputMap = new IDataMap(input);
		inputMap.put("message", message);
		inputMap.put("level", "Error");
		
		try {
			Service.doInvoke("wx.resilience.impl", "log", input);
		} 
		catch (Exception e) {
		}
	}

	public static void logTrace(String message) {
		IData input = IDataFactory.create();
		IDataMap inputMap = new IDataMap(input);
		inputMap.put("message", message);
		inputMap.put("level", "Trace");
		
		try {
			Service.doInvoke("wx.resilience.impl", "log", input);
		} 
		catch (Exception e) {
		}
	}
	
	public static void write(String message) {
		logDebug(message);
	}
	
	public static void logDebug(String message) {
		IData input = IDataFactory.create();
		IDataMap inputMap = new IDataMap(input);
		inputMap.put("message", message);
		inputMap.put("level", "Debug");
		
		try {
			Service.doInvoke("wx.resilience.impl", "log", input);
		} 
		catch (Exception e) {
		}
	}
	
	public static void logFatal(String message) {
		IData input = IDataFactory.create();
		IDataMap inputMap = new IDataMap(input);
		inputMap.put("message", message);
		inputMap.put("level", "Fatal");
		
		try {
			Service.doInvoke("wx.resilience.impl", "log", input);
		} 
		catch (Exception e) {
		}
	}
	
}
