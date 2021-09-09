package wx.resilience.impl;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;


/** Utility class, which provides static helper methods for working with Path objects.
 */
public class Paths {
	public static void assertParentDirExists(Path pFile) {
		final Path dir = pFile.getParent();
		if (dir != null) {
			try {
				Files.createDirectories(dir);
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		}
	}

	public static void moveWithBackup(Path pSourceFile, Path pTargetFile) throws IOException {
		final String baseName = pTargetFile.getFileName().toString();
		if (Files.isRegularFile(pTargetFile)) {
			final Path backupFile = pTargetFile.getParent().resolve(baseName+".backup");
			move(pTargetFile, backupFile);
		}
		move(pSourceFile, pTargetFile);
	}
	public static void move(Path pSourceFile, Path pTargetFile) throws IOException {
		try {
			Files.move(pSourceFile, pTargetFile, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
		} catch (AtomicMoveNotSupportedException amnse) {
			Files.move(pSourceFile, pTargetFile, StandardCopyOption.REPLACE_EXISTING);
		}
		Files.deleteIfExists(pSourceFile);
	}

	/** <p>Constructs a path, which is guaranteed to be within the base directory {@code pBaseDir}.</p>
	 * <p>The use case is a web application, which is supposed to read a file within that directory.
	 * However, for security reasons, we must guarantee, that the result must not be outside.</p>
	 * @param pBaseDir The base directory, within the result is expected to live.
	 * @param pRelativePath The path of the result, relative to the base directory.
	 * @return Path of a file within the base directory.
	 * @throws IllegalArgumentException The parameter {@code pRelativePath} is an absolute file
	 *   name, and outside the base directory.
	 */
	public static Path resolve(Path pBaseDir, String pRelativePath) {
		final Path path = java.nio.file.Paths.get(pRelativePath);
		if (path.isAbsolute()) {
			if (isWithin(pBaseDir, path)) {
				return path;
			} else {
				throw new IllegalArgumentException("Invalid path: " + path
						+ " (Not within " + pBaseDir + ")");
			}
		} else {
			return pBaseDir.resolve(path);
		}
	}

	public static boolean isWithin(Path pDir, Path pPath) {
		Path p = pPath;
		while (p != null) {
			if (p.equals(pDir)) {
				return true;
			} else {
				p = p.getParent();
			}
		}
		return false;
	}

}
