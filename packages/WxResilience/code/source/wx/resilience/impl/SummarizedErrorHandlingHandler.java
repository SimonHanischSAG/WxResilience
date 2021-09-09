package wx.resilience.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.lang.reflect.UndeclaredThrowableException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.AttributesImpl;


/**
 * This class is responsible for the maintenance of the file
 * {@code WxResilience/config/ErrorHandlingSummarized.xml,
 * which contains the assembled information from all the files
 * {@code PKG_NAME/config/errorHandling.xml}.
 */
public class SummarizedErrorHandlingHandler {
	private static final String NS = XMLConstants.NULL_NS_URI;
	private static final String NS_XSI = "http://www.w3.org/2001/XMLSchema-instance";
	private static final String SUMMARIZED_ERROR_HANDLING_FILE = "ErrorHandlingSummarized.xml";
	private static final String EXCEPTION_HANDLING_XSD_FILE = "ErrorHandling.xsd";
	private static final String WX_RESILIENCE = "WxResilience";
	private static final String ERROR_HANDLING_XML_FILE = "ErrorHandling.xml";
	private static final String EXCEPTION_HANDLING_DEFINITION = "errorHandling";

	/** This class is used to create the summarized file.
	 * Basically, you create an instance of {@link Creator},
	 * and invoke the method {@link parse#Path} for all the
	 * files, which are being added to the summarized file.
	 */
	public static class Creator implements AutoCloseable {
		private final OutputStream out;
		private final TransformerHandler th;
		private final LexicalHandler lh;

		Creator(OutputStream pOut) {
			out = pOut;
			try {
				SAXTransformerFactory stf = (SAXTransformerFactory) TransformerFactory.newInstance();
				th = stf.newTransformerHandler();
				th.getTransformer().setOutputProperty(OutputKeys.ENCODING, "UTF-8");
				th.getTransformer().setOutputProperty(OutputKeys.INDENT, "yes");
				th.getTransformer().setOutputProperty(OutputKeys.STANDALONE, "yes");
				th.getTransformer().setOutputProperty(OutputKeys.VERSION, "1.0");
				th.setResult(new StreamResult(out));
				if (th instanceof LexicalHandler) {
					lh = (LexicalHandler) th;
				} else {
					throw new IllegalStateException("TransformerHandler doesn't implement LexicalHandler");
				}
				th.startDocument();
				th.startPrefixMapping("xsi", NS_XSI);
				final AttributesImpl attrs = new AttributesImpl();
				attrs.addAttribute(NS_XSI, "noNamespaceSchemaLocation", "xsi:noNamespaceSchemaLocation", "CDATA", EXCEPTION_HANDLING_XSD_FILE);
				final String comment = "DO NOT EDIT MANUALLY: The configuration is the generated summary over all package configurations!";
				final char[] commentChars = comment.toCharArray();
				th.startElement(NS, EXCEPTION_HANDLING_DEFINITION, EXCEPTION_HANDLING_DEFINITION, attrs);
				lh.comment(commentChars, 0, commentChars.length);
			} catch (TransformerException|SAXException e) {
				throw new UndeclaredThrowableException(e);
			}
		}

		private void parse(InputSource pSource, String pPackageName) {
			/** Create a SAX filter, which basically passes all SAX events (except for starting,
			 * and ending the root element.)
			 */
			final String comment = ">>>>>>>>>>>>>>>>> " + pPackageName + " >>>>>>>>>>>>>>>>>";
			final char[] commentChars = comment.toCharArray();
			final ContentHandler ch = new ContentHandler(){
				private int level;
				private Locator locator;
				@Override
				public void setDocumentLocator(Locator pLocator) {
					locator = pLocator;
				}
				@Override
				public void startDocument() {
					level = 0;
				}

				@Override
				public void endElement(String pUri, String pLocalName,
						String pQName) throws SAXException {
					if (--level == 0) {
						assertExceptionHandlingElement(pUri, pLocalName);
					} else {
						th.endElement(pUri, pLocalName, pQName);
					}
				}
				@Override
				public void startElement(String pUri, String pLocalName,
						String pQName, Attributes pAttrs) throws SAXException {
					if (level++ == 0) {
						assertExceptionHandlingElement(pUri, pLocalName);
					} else {
						th.startElement(pUri, pLocalName, pQName, pAttrs);
					}
				}

				protected void assertExceptionHandlingElement(String pUri, String pLocalName)
						throws SAXException {
					if (!isElementNS(NS, EXCEPTION_HANDLING_DEFINITION, pUri, pLocalName)) {
						throw new SAXParseException("Expected " + asQName(NS, EXCEPTION_HANDLING_DEFINITION)
								                    + ", got " + asQName(pUri, pLocalName), locator);
					}
				}
				protected String asQName(String pUri, String pLocalName) {
					if (pUri == null  ||  XMLConstants.NULL_NS_URI.equals(pUri)) {
						return pLocalName;
					} else {
						return "{" + pUri + "}" + pLocalName;
					}
				}
				protected boolean isElementNS(String pExpectedUri, String pExpectedLocalName,
						                      String pUri, String pLocalName) {
					if (pExpectedUri == null  ||  XMLConstants.NULL_NS_URI.equals(pExpectedUri)) {
						if (pUri == null  ||  XMLConstants.NULL_NS_URI.equals(pUri)) {
							return pExpectedLocalName.equals(pLocalName);
						} else {
							return false;
						}
					} else {
						return pExpectedUri.equals(pUri)  &&  pExpectedLocalName.equals(pLocalName);
					}
				}
				@Override
				public void characters(char[] pChars, int pOffset, int pLength)
					throws SAXException {
					if (level > 0) {
						th.characters(pChars, pOffset, pLength);
					}
				}
				@Override
				public void endDocument() throws SAXException {
					if (level != 0) {
						throw new SAXParseException("Expected level=0, got " + level, locator);
					}
				}
				@Override
				public void endPrefixMapping(String pPrefix)
						throws SAXException {
					if (level > 0) {
						th.endEntity(pPrefix);
					}
				}
				@Override
				public void ignorableWhitespace(char[] pChars, int pOffset, int pLength)
						throws SAXException {
					if (level > 0) {
						th.ignorableWhitespace(pChars, pOffset, pLength);
					}
				}
				@Override
				public void processingInstruction(String pTarget, String pData)
						throws SAXException {
					if (level > 0) {
						th.processingInstruction(pTarget, pData);
					}
				}
				@Override
				public void skippedEntity(String pName)
						throws SAXException {
					if (level > 0) {
						th.skippedEntity(pName);
					}
				}
				@Override
				public void startPrefixMapping(String pPrefix, String pUri) throws SAXException {
					if (level > 0) {
						th.startPrefixMapping(pPrefix, pUri);
					}
				}
			};
			try {
				lh.comment(commentChars, 0, commentChars.length);
				final SAXParserFactory spf = SAXParserFactory.newInstance();
				spf.setValidating(false);
				spf.setNamespaceAware(true);
				final XMLReader xr = spf.newSAXParser().getXMLReader();
				xr.setContentHandler(ch);
				xr.parse(pSource);
			} catch (SAXException|ParserConfigurationException e) {
				throw new UndeclaredThrowableException(e);
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		}

		public void parse(Path pPath, String pPackageName) {
			try (InputStream in = Files.newInputStream(pPath)) {
				final InputSource isource = new InputSource(in);
				isource.setSystemId(pPath.toString());
				parse(isource, pPackageName);
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		}

		public void parse(File pFile, String pPackageName) {
			parse(pFile.toPath(), pPackageName);
		}
		@Override
		public void close() {
			try {
				th.endElement(NS, EXCEPTION_HANDLING_DEFINITION, EXCEPTION_HANDLING_DEFINITION);
				th.endPrefixMapping("xsi");
				th.endDocument();
				out.close();
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			} catch (SAXException e) {
				throw new UndeclaredThrowableException(e);
			}
		}
	}

	public static Creator newCreator(File pFile) {
		return newCreator(pFile.toPath());
	}

	public static Creator newCreator(Path pPath) {
		try {
			OutputStream out = Files.newOutputStream(pPath);
			return new Creator(out);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
}
