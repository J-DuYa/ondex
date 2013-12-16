package net.sourceforge.ondex.restful.resources.writers;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

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

import net.sourceforge.ondex.core.ConceptAccession;
import net.sourceforge.ondex.core.DataSource;
import net.sourceforge.ondex.export.oxl.Export;
import net.sourceforge.ondex.restful.util.WstxOutputFactoryProvider;

import org.codehaus.stax2.XMLStreamWriter2;

import com.ctc.wstx.io.CharsetNames;
import com.sun.jersey.spi.resource.Singleton;

/**
 * Passes XML encoding of a ConceptAccession to output stream or simply HTML.
 * 
 * @author taubertj
 * 
 */
@Provider
@Singleton
@Produces({ MediaType.APPLICATION_XML, MediaType.TEXT_HTML })
public class ConceptAccessionMessageBodyWriter extends Export implements
		MessageBodyWriter<ConceptAccession> {

	public ConceptAccessionMessageBodyWriter() throws JAXBException {
		super();
	}

	/**
	 * Used to construct the URI linking services.
	 */
	@Context
	private UriInfo ui;

	@Override
	public long getSize(ConceptAccession conceptname, Class<?> clazz,
			Type type, Annotation[] anno, MediaType mediatype) {
		return -1;
	}

	@Override
	public boolean isWriteable(Class<?> clazz, Type type, Annotation[] anno,
			MediaType mediatype) {
		// accept all subclasses of ConceptAccession
		return ConceptAccession.class.isAssignableFrom(clazz);
	}

	@Override
	public void writeTo(ConceptAccession conceptaccession, Class<?> clazz,
			Type type, Annotation[] anno, MediaType mediatype,
			MultivaluedMap<String, Object> map, OutputStream outstream)
			throws IOException, WebApplicationException {

		// make sure there is a ConceptAccession
		if (conceptaccession == null)
			throw new WebApplicationException(Response.Status.NOT_FOUND);

		// return XML encoding
		if (mediatype.toString().equals(MediaType.APPLICATION_XML)) {
			try {
				// new XML writer for output stream
				XMLStreamWriter2 xmlWriteStream = (XMLStreamWriter2) WstxOutputFactoryProvider.xmlw
						.createXMLStreamWriter(outstream, CharsetNames.CS_UTF8);

				// enable legacy mode for fully expanded meta data
				setLegacyMode(true);

				// export ConceptAccession
				buildConceptAccession(xmlWriteStream, conceptaccession);

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
			String meta = path.substring(0,
					path.indexOf("/", path.indexOf("graphs/") + 7));
			meta = meta + "/metadata";

			// simply write HTML code
			OutputStreamWriter writer = new OutputStreamWriter(outstream);
			writer.write("<h2>ConceptAccession</h2>\n");
			writer.write("<table>\n");
			writer.write("<tr><td>accession</td><td>");
			writer.write(conceptaccession.getAccession());
			writer.write("</td></tr>\n");

			// link CVs
			DataSource dataSource = conceptaccession.getElementOf();
			writer.write("<tr><td>element of</td><td><a href=\"");
			writer.write(meta + "/datasources/" + dataSource.getId());
			writer.write("\">");
			writer.write(dataSource.getId());
			writer.write("</a></td></tr>\n");

			writer.write("<tr><td>ambiguous</td><td>");
			writer.write(conceptaccession.isAmbiguous() + "</td></tr>\n");

			writer.write("</table>\n");
			writer.write("<a href=\"");
			path = path.substring(0, path.lastIndexOf("/"));
			path = path.substring(0, path.lastIndexOf("/"));
			writer.write(path);
			writer.write("\">up</a>");
			writer.flush();
		}
	}
}
