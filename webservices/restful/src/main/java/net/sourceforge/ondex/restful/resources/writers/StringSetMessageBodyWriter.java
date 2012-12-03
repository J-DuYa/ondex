package net.sourceforge.ondex.restful.resources.writers;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Set;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import net.sourceforge.ondex.restful.util.WstxOutputFactoryProvider;

import org.codehaus.stax2.XMLStreamWriter2;

import com.ctc.wstx.io.CharsetNames;
import com.sun.jersey.spi.resource.Singleton;
import com.thoughtworks.xstream.XStream;

/**
 * Passes XML encoding of a set of Strings to output stream or simply HTML.
 * 
 * @author taubertj
 * 
 */
@Provider
@Singleton
@Produces({ MediaType.APPLICATION_XML, MediaType.TEXT_HTML })
public class StringSetMessageBodyWriter implements
		MessageBodyWriter<Set<String>> {

	public StringSetMessageBodyWriter() throws JAXBException {
		super();
	}

	/**
	 * Used to construct the URI linking services.
	 */
	@Context
	private UriInfo ui;

	private static final XStream xstream = new XStream();

	@Override
	public long getSize(Set<String> set, Class<?> clazz, Type type,
			Annotation[] anno, MediaType mediatype) {
		return -1;
	}

	@Override
	public boolean isWriteable(Class<?> clazz, Type type, Annotation[] anno,
			MediaType mediatype) {
		// accept all subclasses of Set
		boolean accept = Set.class.isAssignableFrom(clazz);
		if (type instanceof ParameterizedType) {
			Object first = ((ParameterizedType) type).getActualTypeArguments()[0];
			if (first instanceof Class<?>)
				accept = accept
						&& String.class.isAssignableFrom((Class<?>) first);
			else
				accept = false;
		} else
			accept = false;
		return accept;
	}

	@Override
	public void writeTo(Set<String> set, Class<?> clazz, Type type,
			Annotation[] anno, MediaType mediatype,
			MultivaluedMap<String, Object> map, OutputStream outstream)
			throws IOException, WebApplicationException {

		// make sure there is a iterator
		if (set == null)
			throw new WebApplicationException(Response.Status.NOT_FOUND);

		// return XML encoding
		if (mediatype.toString().equals(MediaType.APPLICATION_XML)) {
			try {
				// new XML writer for output stream
				XMLStreamWriter2 xmlWriteStream = (XMLStreamWriter2) WstxOutputFactoryProvider.xmlw
						.createXMLStreamWriter(outstream, CharsetNames.CS_UTF8);

				// write XStream serialisation of set
				xmlWriteStream.writeCharacters(xstream.toXML(set));

				// flush out all data
				xmlWriteStream.flush();
			} catch (XMLStreamException e) {
				throw new WebApplicationException(e,
						Response.Status.INTERNAL_SERVER_ERROR);
			}
		}

		// return HTML encoding
		else if (mediatype.toString().equals(MediaType.TEXT_HTML)) {
			String path = ui.getAbsolutePath().getPath();
			if (path.endsWith("/"))
				path = path.substring(0, path.length() - 1);

			// simply write HTML code
			OutputStreamWriter writer = new OutputStreamWriter(outstream);
			writer.write("<h2>Values</h2>\n");
			writer.write("<table>\n");
			for (String s : set) {
				writer.write("<tr><td>");
				writer.write(s);
				writer.write("</td></tr>\n");
			}
			writer.write("</table>\n");
			writer.write("<a href=\"");
			writer.write(path.substring(0, path.lastIndexOf("/")));
			writer.write("\">up</a>");
			writer.flush();
		}
	}
}
