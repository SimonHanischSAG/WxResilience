package wx.resilience.impl;

// -----( IS Java Code Template v1.2

import com.wm.data.*;
import com.wm.util.Values;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
// --- <<IS-START-IMPORTS>> ---
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.wm.data.IData;
import com.wm.data.IDataCursor;
import com.wm.data.IDataUtil;
// --- <<IS-END-IMPORTS>> ---

public final class status

{
	// ---( internal utility methods )---

	final static status _instance = new status();

	static status _newInstance() { return new status(); }

	static status _cast(Object o) { return (status)o; }

	// ---( server methods )---




	public static final void applyWhitelist (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(applyWhitelist)>> ---
		// @sigtype java 3.5
		// [i] record:0:required doc
		// [i] field:1:optional whitelist
		// [i] field:0:optional maxSize
		// [o] record:0:optional doc
		// [o] field:0:optional size
		IDataCursor cursor = pipeline.getCursor();
		IData doc = IDataUtil.getIData(cursor, "doc");
		
		if (doc != null) {
			String[] whitelist = IDataUtil.getStringArray(cursor, "whitelist");
			String maxSize = IDataUtil.getString(cursor, "maxSize");
			
			// Map the whitelist to a list of classes. This way, any unknown classes will throw an exception
			// in the beginning.
			List<Class<?>> list = whitelist == null || whitelist.length == 0 ? null : Arrays.stream(whitelist)
					.filter(java.util.Objects::nonNull)
					.distinct()
					.map(v -> {
						try { return Class.forName(v); }
						catch (ClassNotFoundException e) { throw new RuntimeException(e); }
					})
					.collect(Collectors.toList());
			
			try {
				DocumentSize size = new DocumentSize(maxSize == null ? null : Long.parseLong(maxSize));
				applyWhitelist(null, doc, list, size);
				IDataUtil.put(cursor, "size", Long.toString(size.getSize()));
			}
			catch (DocumentSize.TooBigException e) {
				IDataUtil.put(cursor, "doc", null);
			}
		}
		cursor.destroy();
			
			
			
		// --- <<IS-END>> ---

                
	}

	// --- <<IS-START-SHARED>> ---
	/**
	 * Recursively removes all values from a document that are not whitelisted.
	 * 
	 * @param key The key of the value. Can be null.
	 * @param value The value to check if it should be removed. The root should be either a document
	 * or a document-array.
	 * @param whitelist A set of classes that should be kept. Can be null if only the default types
	 * should be kept.
	 * @param size A DocumentSize-Object to calculate the size of the document.
	 * @return Whether the value should be kept or not. The caller is responsible for removing the value.
	 * @throws wx.resilience.impl.status.applyWhitelist_SVC.DocumentSize.DocumentSizeTooBigException 
	 */
	private static boolean applyWhitelist(String key, Object value, List<Class<?>> whitelist, DocumentSize size)
			throws DocumentSize.TooBigException {
		
		if (value instanceof IData) {
			size.addKey(key);
			size.addIData();
	
			IDataCursor cursor = ((IData) value).getCursor();
			boolean delete = false;
			while (delete && cursor.delete() || cursor.next()) {
				delete = !applyWhitelist(cursor.getKey(), cursor.getValue(), whitelist, size);
			}
			cursor.destroy();
			return true;
		}
		else if (value instanceof IData[]) {
			size.addKey(key);
	
			for (IData doc : (IData[]) value) {
				applyWhitelist(null, doc, whitelist, size);
			}
			return true;			
		}
	
		boolean keep = value == null
			|| value instanceof String      && size.addString(value)
			|| value instanceof String[]    && size.addStringArray(value)
			|| value instanceof Number      && size.addNumber(value)
			|| value instanceof Number[]    && size.addNumberArray(value)
			|| value instanceof Boolean     && size.addBoolean(value)
			|| value instanceof Boolean[]   && size.addBooleanArray(value)
			|| value instanceof Character   && size.addChar(value)
			|| value instanceof Character[] && size.addCharArray(value)
			|| value instanceof Date        && size.addDate(value)
			|| value instanceof Date[]      && size.addDateArray(value)
			
			// A String Table is internally just a two dimensional String array.
			|| value instanceof String[][]  && size.addStringTable(value)
							
			// Check for primitive arrays.
			|| value.getClass().isArray() && value.getClass().getComponentType().isPrimitive() && size.addPrimitiveArray(value)
	
			// Check if the value is of any type in the provided whitelist. The value is also valid if it is
			// an array of those types.
			|| whitelist != null && whitelist.stream().anyMatch(c -> value.getClass().isArray()
					? c.isAssignableFrom(value.getClass().getComponentType())
					: c.isInstance(value)
			);
		
		if (keep) size.addKey(key);
		return keep;
	}
	
	/**
	 * This class takes care of the size-calculation. It provides different methods for adding the size of various types
	 * and may throw a TooBigException if the optional max size is exceeded.
	 */
	private static class DocumentSize {
		private static final Map<Class<?>, Integer> NUMBER_SIZES;
		private static final int IDATA_OVERHEAD_SIZE = 17;
		private static final int PAIR_OVERHEAD_SIZE = 1;
		private static final int CHAR_SIZE = 2;
		private static final int BOOL_SIZE = 1;
		private static final int DATE_SIZE = 32;
	
		private long size;
		private Long maxSize;
		
		public DocumentSize(Long maxSize) {
			this.maxSize = maxSize;
		}
		
		public long getSize() {
			return size;
		}
		
		public boolean addKey(String key) throws TooBigException  {
			if (key != null) {
				size += key.length() * CHAR_SIZE;
				size += PAIR_OVERHEAD_SIZE;
				return checkSize();
			}
			return true;
		}
		
		public boolean addIData() throws TooBigException  {
			size += IDATA_OVERHEAD_SIZE;
			return checkSize();
		}
		
		public boolean addString(Object o) throws TooBigException  {
			size += ((String) o).length() * CHAR_SIZE;
			return checkSize();
		}
		
		public boolean addStringArray(Object o) throws TooBigException  {
			for (String s : (String[]) o) {
				if (s != null) size += s.length() * CHAR_SIZE;
			}
			return checkSize();
		}
		
		public boolean addStringTable(Object o) throws TooBigException  {
			for (String[] s1 : (String[][]) o) {
				if (s1 != null) {
					for (String s2 : (String[]) s1) {
						if (s2 != null) size += s2.length() * CHAR_SIZE;
					}
				}
			}
			return checkSize();
		}
		
		public boolean addNumber(Object o) throws TooBigException  {
			size += NUMBER_SIZES.get(o.getClass());
			return checkSize();
		}
		
		public boolean addNumberArray(Object o) throws TooBigException  {
			size += NUMBER_SIZES.get(o.getClass().getComponentType()) * sizeOf((Object[]) o);
			return checkSize();
		}
		
		public boolean addBoolean(Object o) throws TooBigException  {
			size += BOOL_SIZE;
			return checkSize();
		}
		
		public boolean addBooleanArray(Object o) throws TooBigException  {
			size += BOOL_SIZE * sizeOf((Object[]) o);
			return checkSize();
		}
		
		public boolean addChar(Object o) throws TooBigException  {
			size += CHAR_SIZE;
			return checkSize();
		}
		
		public boolean addCharArray(Object o) throws TooBigException  {
			size += CHAR_SIZE * sizeOf((Object[]) o);
			return checkSize();
		}
		
		public boolean addDate(Object o) throws TooBigException  {
			size += DATE_SIZE;
			return checkSize();
		}
		
		public boolean addDateArray(Object o) throws TooBigException  {
			size += DATE_SIZE * sizeOf((Object[]) o);
			return checkSize();
		}
		
		public boolean addPrimitiveArray(Object o) throws TooBigException  {
			size += NUMBER_SIZES.get(o.getClass().getComponentType()) * Array.getLength(o);
			return checkSize();
		}
		
		private long sizeOf(Object[] array) {
			return Arrays.stream(array)
					.filter(java.util.Objects::nonNull)
					.count();
		}
		
		private boolean checkSize() throws TooBigException  {
			if (maxSize != null && size > maxSize) throw new TooBigException();
			return true;
		}
		
		static {
			NUMBER_SIZES = new HashMap<>();
			NUMBER_SIZES.put(Byte.class, 1);
			NUMBER_SIZES.put(Short.class, 2);
			NUMBER_SIZES.put(Integer.class, 4);
			NUMBER_SIZES.put(Long.class, 8);
			NUMBER_SIZES.put(Float.class, 4);
			NUMBER_SIZES.put(Double.class, 8);
			// Character not needed, since it isn't a number.
	
			NUMBER_SIZES.put(byte.class, 1);
			NUMBER_SIZES.put(short.class, 2);
			NUMBER_SIZES.put(int.class, 4);
			NUMBER_SIZES.put(long.class, 8);
			NUMBER_SIZES.put(float.class, 4);
			NUMBER_SIZES.put(double.class, 8);
			NUMBER_SIZES.put(char.class, CHAR_SIZE);
		}
		
		class TooBigException extends Exception {}
	}
	
	
		
	// --- <<IS-END-SHARED>> ---
}

