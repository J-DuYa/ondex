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

import net.sourceforge.ondex.core.ONDEXGraphMetaData;
import net.sourceforge.ondex.export.oxl.Export;
import net.sourceforge.ondex.restful.util.WstxOutputFactoryProvider;

import org.codehaus.stax2.XMLStreamWriter2;

import com.ctc.wstx.io.CharsetNames;
import com.sun.jersey.spi.resource.Singleton;

/**
 * Passes XML encoding of meta data to output stream or simply HTML.
 * 
 * @author taubertj
 * 
 */
@Provider
@Singleton
@Produces({ MediaType.APPLICATION_XML, MediaType.TEXT_HTML })
public class ONDEXGraphMetaDataMessageBodyWriter extends Export implements
		MessageBodyWriter<ONDEXGraphMetaData> {

	public ONDEXGraphMetaDataMessageBodyWriter() throws JAXBException {
		super();
	}

	/**
	 * Used to construct the URI linking services.
	 */
	@Context
	private UriInfo ui;

	@Override
	public long getSize(ONDEXGraphMetaData graph, Class<?> clazz, Type type,
			Annotation[] anno, MediaType mediatype) {
		return -1;
	}

	@Override
	public boolean isWriteable(Class<?> clazz, Type type, Annotation[] anno,
			MediaType mediatype) {
		// accept all subclasses of ONDEXGraphMetaData
		return ONDEXGraphMetaData.class.isAssignableFrom(clazz);
	}

	@Override
	public void writeTo(ONDEXGraphMetaData meta, Class<?> clazz, Type type,
			Annotation[] anno, MediaType mediatype,
			MultivaluedMap<String, Object> map, OutputStream outstream)
			throws IOException, WebApplicationException {

		// make sure there is meta data
		if (meta == null)
			throw new WebApplicationException(Response.Status.NOT_FOUND);

		// return XML encoding
		if (mediatype.toString().equals(MediaType.APPLICATION_XML)) {
			try {
				// new XML writer for output stream
				XMLStreamWriter2 xmlWriteStream = (XMLStreamWriter2) WstxOutputFactoryProvider.xmlw
						.createXMLStreamWriter(outstream, CharsetNames.CS_UTF8);

				// enable legacy mode for fully expanded meta data
				setLegacyMode(true);

				// export complete meta data
				buildMetaDataDocument(xmlWriteStream, meta);

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
			writer.write("<h2>Meta data:</h2>\n");
			writer.write("<table><tr><th>type</th><th>count</th></tr>\n");

			// link attribute names
			writer.write("<tr><td><a href=\"");
			writer.write(path + "/attributenames\">");
			writer.write("AttributeName</a></td><td>");
			writer.write(meta.getAttributeNames().size() + "");
			writer.write("</td></tr>\n");

			// link cvs
			writer.write("<tr><td><a href=\"");
			writer.write(path + "/datasources\">");
			writer.write("DataSource</a></td><td>");
			writer.write(meta.getDataSources().size() + "");
			writer.write("</td></tr>\n");

			// link concept classes
			writer.write("<tr><td><a href=\"");
			writer.write(path + "/conceptclasses\">");
			writer.write("ConceptClass</a></td><td>");
			writer.write(meta.getConceptClasses().size() + "");
			writer.write("</td></tr>\n");

			// link evidence types
			writer.write("<tr><td><a href=\"");
			writer.write(path + "/evidencetypes\">");
			writer.write("EvidenceType</a></td><td>");
			writer.write(meta.getEvidenceTypes().size() + "");
			writer.write("</td></tr>\n");

			// link relation types
			writer.write("<tr><td><a href=\"");
			writer.write(path + "/relationtypes\">");
			writer.write("RelationType</a></td><td>");
			writer.write(meta.getRelationTypes().size() + "");
			writer.write("</td></tr>\n");

			// link units
			writer.write("<tr><td><a href=\"");
			writer.write(path + "/units\">");
			writer.write("Unit</a></td><td>");
			writer.write(meta.getUnits().size() + "");
			writer.write("</td></tr>\n");
			writer.write("</table>\n");

			// link up
			writer.write("<a href=\"");
			writer.write(path.substring(0, path.lastIndexOf("/")));
			writer.write("\">up</a>");
			writer.flush();
		}
	}
}
