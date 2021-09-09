package wx.resilience.impl;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.Objects;

public class Exceptions {
	public static RuntimeException show(Throwable pTh) {
		final Throwable th = Objects.requireNonNull(pTh, "The Throwable must not be null.");
		if (th instanceof RuntimeException) {
			throw (RuntimeException) th;
		} else if (th instanceof Error) {
			throw (Error) th;
		} else if (th instanceof IOException) {
			throw new UncheckedIOException((IOException) th);
		} else {
			throw new UndeclaredThrowableException(th);
		}
	}

	public static <E extends Exception> RuntimeException show(Throwable pTh, Class<E> pType) throws E {
		final Throwable th = Objects.requireNonNull(pTh, "The Throwable must not be null.");
		if (th instanceof RuntimeException) {
			throw (RuntimeException) th;
		} else if (th instanceof Error) {
			throw (Error) th;
		} else if (th instanceof IOException) {
			throw new UncheckedIOException((IOException) th);
		} else if (pType.isAssignableFrom(pTh.getClass())) {
			throw pType.cast(pTh);
		} else {
			throw new UndeclaredThrowableException(th);
		}
	}

}
