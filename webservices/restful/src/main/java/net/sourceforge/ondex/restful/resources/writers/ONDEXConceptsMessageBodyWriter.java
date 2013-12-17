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

import net.sourceforge.ondex.core.ONDEXConcept;
import net.sourceforge.ondex.export.oxl.Export;
import net.sourceforge.ondex.restful.util.WstxOutputFactoryProvider;

import org.codehaus.stax2.XMLStreamWriter2;

import com.ctc.wstx.io.CharsetNames;
import com.sun.jersey.spi.resource.Singleton;

/**
 * Passes XML encoding of a list of concepts to output stream or simply HTML.
 * 
 * @author taubertj
 * 
 */
@Provider
@Singleton
@Produces({ MediaType.APPLICATION_XML, MediaType.TEXT_HTML })
public class ONDEXConceptsMessageBodyWriter extends Export implements
		MessageBodyWriter<Set<ONDEXConcept>> {

	public ONDEXConceptsMessageBodyWriter() throws JAXBException {
		super();
	}

	/**
	 * Used to construct the URI linking services.
	 */
	@Context
	private UriInfo ui;

	@Override
	public long getSize(Set<ONDEXConcept> view, Class<?> clazz, Type type,
			Annotation[] anno, MediaType mediatype) {
		return -1;
	}

	@Override
	public boolean isWriteable(Class<?> clazz, Type type, Annotation[] anno,
			MediaType mediatype) {
		// accept all subclasses of Set
		return Set.class.isAssignableFrom(clazz)
				&& ONDEXConcept.class
						.isAssignableFrom((Class<?>) ((ParameterizedType) type)
								.getActualTypeArguments()[0]);
	}

	@Override
	public void writeTo(Set<ONDEXConcept> view, Class<?> clazz, Type type,
			Annotation[] anno, MediaType mediatype,
			MultivaluedMap<String, Object> map, OutputStream outstream)
			throws IOException, WebApplicationException {

		// make sure there is a view
		if (view == null)
			throw new WebApplicationException(Response.Status.NOT_FOUND);

		// return XML encoding
		if (mediatype.toString().equals(MediaType.APPLICATION_XML)) {
			try {
				// new XML writer for output stream
				XMLStreamWriter2 xmlWriteStream = (XMLStreamWriter2) WstxOutputFactoryProvider.xmlw
						.createXMLStreamWriter(outstream, CharsetNames.CS_UTF8);

				// enable legacy mode for fully expanded meta data
				setLegacyMode(true);

				// export list of concepts
				buildConcepts(xmlWriteStream, view);

				// flush out all data
				xmlWriteStream.flush();
			} catch (XMLStreamException e) {
				throw new WebApplicationException(e,
						Response.Status.INTERNAL_SERVER_ERROR);
			} catch (JAXBException e) {
				throw new WebApplicationException(e,
						Response.Status.INTERNAL_SERVER_ERROR);
			}
		}

		// return HTML encoding
		else if (mediatype.toString().equals(MediaType.TEXT_HTML)) {
			String path = ui.getAbsolutePath().getPath();
			if (path.contains("query/"))
				path = path.replace("query/", "");
			if (path.endsWith("/"))
				path = path.substring(0, path.length() - 1);
			if (!path.endsWith("concepts"))
				path = path.substring(0, path.indexOf("concepts") + 8);

			// simply write HTML code
			OutputStreamWriter writer = new OutputStreamWriter(outstream);
			writer.write("<h2>Concepts</h2>\n");
			writer.write("<table>\n");
			writer.write("<tr><th>id</th><th>pid</th></tr>\n");
			int count = 0;
			for (ONDEXConcept concept : view) {
				// display only first 100
				if (count > 100)
					break;
				writer.write("<tr><td><a href=\"");
				writer.write(path + "/" + String.valueOf(concept.getId()));
				writer.write("\">");
				writer.write(String.valueOf(concept.getId()));
				writer.write("</a>");
				writer.write("</td><td>");
				writer.write(concept.getPID());
				writer.write("</td></tr>\n");
				count++;
			}
			writer.write("</table>\n");
			if (count > 100)
				writer.write("Result returned more than 100 elements, showing only the first 100.<br>\n");
			writer.write("<a href=\"");
			writer.write(path.substring(0, path.lastIndexOf("/")));
			writer.write("\">up</a>");
			writer.flush();
		}
	}
}
