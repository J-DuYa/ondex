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
import net.sourceforge.ondex.core.AttributeName;
import net.sourceforge.ondex.core.ONDEXConcept;
import net.sourceforge.ondex.core.ONDEXEntity;
import net.sourceforge.ondex.core.ONDEXRelation;
import net.sourceforge.ondex.export.oxl.Export;
import net.sourceforge.ondex.export.oxl.XMLTagNames;
import net.sourceforge.ondex.restful.util.WstxOutputFactoryProvider;

import org.codehaus.stax2.XMLStreamWriter2;

import com.ctc.wstx.io.CharsetNames;
import com.sun.jersey.spi.resource.Singleton;

/**
 * Passes XML encoding of a Attribute to output stream or simply HTML.
 * 
 * @author taubertj
 */
@Provider
@Singleton
@Produces({ MediaType.APPLICATION_XML, MediaType.TEXT_HTML })
public class AttributeMessageBodyWriter extends Export implements
		MessageBodyWriter<Attribute> {

	public AttributeMessageBodyWriter() throws JAXBException {
		super();
	}

	/**
	 * Used to construct the URI linking services.
	 */
	@Context
	private UriInfo ui;

	@Override
	public long getSize(Attribute attribute, Class<?> clazz, Type type,
			Annotation[] anno, MediaType mediatype) {
		return -1;
	}

	@Override
	public boolean isWriteable(Class<?> clazz, Type type, Annotation[] anno,
			MediaType mediatype) {
		// accept all subclasses of Attribute
		return Attribute.class.isAssignableFrom(clazz);
	}

	@Override
	public void writeTo(Attribute attribute, Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, Object> httpHeaders,
			OutputStream entityStream) throws IOException,
			WebApplicationException {

		// make sure there is a Attribute
		if (attribute == null)
			throw new WebApplicationException(Response.Status.NOT_FOUND);

		// return XML encoding
		if (mediaType.toString().equals(MediaType.APPLICATION_XML)) {
			try {
				// new XML writer for output stream
				XMLStreamWriter2 xmlWriteStream = (XMLStreamWriter2) WstxOutputFactoryProvider.xmlw
						.createXMLStreamWriter(entityStream,
								CharsetNames.CS_UTF8);

				// enable legacy mode for fully expanded meta data
				setLegacyMode(true);

				// retrieve class to deal with
				Class<? extends ONDEXEntity> clazz = attribute.getOwnerClass();

				// export Attribute
				if (clazz.isAssignableFrom(ONDEXConcept.class))
					buildAttribute(xmlWriteStream, attribute,
							XMLTagNames.CONCEPTGDS);
				else if (clazz.isAssignableFrom(ONDEXRelation.class))
					buildAttribute(xmlWriteStream, attribute,
							XMLTagNames.RELATIONGDS);
				else
					throw new WebApplicationException(Response.Status.CONFLICT);

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
		else if (mediaType.toString().equals(MediaType.TEXT_HTML)) {
			String path = ui.getAbsolutePath().getPath();
			if (path.endsWith("/"))
				path = path.substring(0, path.length() - 1);
			String meta = path.substring(0,
					path.indexOf("/", path.indexOf("graphs/") + 7));
			meta = meta + "/metadata";

			AttributeName an = attribute.getOfType();

			// simply write HTML code
			OutputStreamWriter writer = new OutputStreamWriter(entityStream);
			writer.write("<h2>Attribute</h2>\n");
			writer.write("<table>\n");
			writer.write("<tr><td>attribute name</td><td><a href=\"");
			writer.write(meta + "/attributenames/" + an.getId());
			writer.write("\">");
			writer.write(an.getId());
			writer.write("</a></td></tr>\n");
			writer.write("<tr><td>datatype</td><td>");
			writer.write(an.getDataTypeAsString());
			writer.write("</td></tr>\n");
			writer.write("<tr><td>value</td><td>");
			writer.write(attribute.getValue().toString());
			writer.write("</td></tr>\n");
			writer.write("<tr><td>index</td><td>");
			writer.write(attribute.isDoIndex() + "");
			writer.write("</td></tr>\n");

			writer.write("</table>\n");
			writer.write("<a href=\"");
			writer.write(path.substring(0, path.lastIndexOf("/")));
			writer.write("\">up</a>");
			writer.flush();
		}
	}
}
