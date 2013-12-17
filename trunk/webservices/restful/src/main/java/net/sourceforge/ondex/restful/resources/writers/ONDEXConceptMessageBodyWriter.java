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

import net.sourceforge.ondex.core.Attribute;
import net.sourceforge.ondex.core.ConceptClass;
import net.sourceforge.ondex.core.ConceptName;
import net.sourceforge.ondex.core.DataSource;
import net.sourceforge.ondex.core.EvidenceType;
import net.sourceforge.ondex.core.ONDEXConcept;
import net.sourceforge.ondex.export.oxl.Export;
import net.sourceforge.ondex.restful.util.WstxOutputFactoryProvider;

import org.codehaus.stax2.XMLStreamWriter2;

import com.ctc.wstx.io.CharsetNames;
import com.sun.jersey.spi.resource.Singleton;

/**
 * Passes XML encoding of a concept to output stream or simply HTML.
 * 
 * @author taubertj
 * 
 */
@Provider
@Singleton
@Produces({ MediaType.APPLICATION_XML, MediaType.TEXT_HTML })
public class ONDEXConceptMessageBodyWriter extends Export implements
		MessageBodyWriter<ONDEXConcept> {

	public ONDEXConceptMessageBodyWriter() throws JAXBException {
		super();
	}

	/**
	 * Used to construct the URI linking services.
	 */
	@Context
	private UriInfo ui;

	@Override
	public long getSize(ONDEXConcept concept, Class<?> clazz, Type type,
			Annotation[] anno, MediaType mediatype) {
		return -1;
	}

	@Override
	public boolean isWriteable(Class<?> clazz, Type type, Annotation[] anno,
			MediaType mediatype) {
		// accept all subclasses of ONDEXConcept
		return ONDEXConcept.class.isAssignableFrom(clazz);
	}

	@Override
	public void writeTo(ONDEXConcept concept, Class<?> clazz, Type type,
			Annotation[] anno, MediaType mediatype,
			MultivaluedMap<String, Object> map, OutputStream outstream)
			throws IOException, WebApplicationException {

		// make sure there is a concept
		if (concept == null)
			throw new WebApplicationException(Response.Status.NOT_FOUND);

		// return XML encoding
		if (mediatype.toString().equals(MediaType.APPLICATION_XML)) {
			try {
				// new XML writer for output stream
				XMLStreamWriter2 xmlWriteStream = (XMLStreamWriter2) WstxOutputFactoryProvider.xmlw
						.createXMLStreamWriter(outstream, CharsetNames.CS_UTF8);

				// enable legacy mode for fully expanded meta data
				setLegacyMode(true);

				// export concept
				buildConcept(xmlWriteStream, concept);

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
			if (path.endsWith("/"))
				path = path.substring(0, path.length() - 1);
			String meta = path.substring(0, path.lastIndexOf("/"));
			meta = meta.substring(0, meta.lastIndexOf("/"));
			meta = meta + "/metadata";

			// simply write HTML code
			OutputStreamWriter writer = new OutputStreamWriter(outstream);
			writer.write("<h2>Concept</h2>\n");
			writer.write("<table>\n");
			writer.write("<tr><td>id</td><td>");
			writer.write(String.valueOf(concept.getId()));
			writer.write("</td></tr>\n");
			writer.write("<tr><td>pid</td><td>");
			writer.write(concept.getPID());
			writer.write("</td></tr>\n");
			writer.write("<tr><td>annotation</td><td>");
			writer.write(concept.getAnnotation());
			writer.write("</td></tr>\n");
			writer.write("<tr><td>describtion</td><td>");
			writer.write(concept.getDescription());
			writer.write("</td></tr>\n");

			// link concept classes
			ConceptClass conceptclass = concept.getOfType();
			writer.write("<tr><td>of type</td><td><a href=\"");
			writer.write(meta + "/conceptclasses/" + conceptclass.getId());
			writer.write("\">");
			writer.write(conceptclass.getId());
			writer.write("</a></td></tr>\n");

			// link data sources
			DataSource dataSource = concept.getElementOf();
			writer.write("<tr><td>element of</td><td><a href=\"");
			writer.write(meta + "/datasources/" + dataSource.getId());
			writer.write("\">");
			writer.write(dataSource.getId());
			writer.write("</a></td></tr>\n");

			// link evidences
			writer.write("<tr><td>evidence</td><td>");
			for (EvidenceType et : concept.getEvidence()) {
				writer.write("<a href=\"");
				writer.write(meta + "/evidencetypes/" + et.getId());
				writer.write("\">");
				writer.write(et.getId());
				writer.write("</a>&nbsp;");
			}
			writer.write("</td></tr>\n");

			// link preferred name
			ConceptName name = concept.getConceptName();
			writer.write("<tr><td>preferred name</td><td>");
			if (name != null) {
				writer.write("<a href=\"");
				writer.write(path + "/conceptname\">");
				writer.write(name.getName());
				writer.write("</a>");
			}
			writer.write("</td></tr>\n");

			// link tags
			writer.write("<tr><td>tags</td><td>");
			for (ONDEXConcept c : concept.getTags()) {
				writer.write("<a href=\"");
				writer.write(path.substring(0, path.lastIndexOf("/")) + "/"
						+ c.getId());
				writer.write("\">");
				writer.write(String.valueOf(c.getId()));
				writer.write("</a>&nbsp;");
			}
			writer.write("</td></tr>\n");

			// link attributes
			writer.write("<tr><td>attributes</td><td>");
			for (Attribute a : concept.getAttributes()) {
				writer.write("<a href=\"");
				writer.write(path + "/attributes/" + a.getOfType().getId());
				writer.write("\">");
				writer.write(a.getOfType().getId());
				writer.write("</a>&nbsp;");
			}
			writer.write("</td></tr>\n");

			writer.write("</table>\n");
			writer.write("<a href=\"");
			writer.write(path.substring(0, path.lastIndexOf("/")));
			writer.write("\">up</a>");
			writer.flush();
		}
	}
}
