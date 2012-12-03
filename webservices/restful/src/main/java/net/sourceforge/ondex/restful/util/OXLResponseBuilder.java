package net.sourceforge.ondex.restful.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Set;
import java.util.zip.GZIPOutputStream;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import net.sourceforge.ondex.core.ONDEXConcept;
import net.sourceforge.ondex.core.ONDEXGraph;
import net.sourceforge.ondex.core.ONDEXRelation;
import net.sourceforge.ondex.export.oxl.Export;

import org.codehaus.stax2.XMLStreamWriter2;

import com.ctc.wstx.io.CharsetNames;

/**
 * Consolidate building a Response with OXL content
 * 
 * @author taubertj
 * 
 */
public class OXLResponseBuilder extends Export {

	// new OXL exporter
	static Export export = new Export();

	static {
		// enable legacy mode for fully expanded meta data
		export.setLegacyMode(true);
	}

	/**
	 * Build OXL Response for given ONDEXGraph and file name
	 * 
	 * @param name
	 *            file name
	 * @param graph
	 *            ONDEXGraph
	 * @return OXL Response
	 */
	public static Response build(String name, ONDEXGraph graph) {
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		try {

			// new XML writer for output stream
			XMLStreamWriter2 xmlWriteStream = (XMLStreamWriter2) WstxOutputFactoryProvider.xmlw
					.createXMLStreamWriter(byteStream, CharsetNames.CS_UTF8);

			// export complete graph
			export.buildDocument(xmlWriteStream, graph);

			// flush out all data
			xmlWriteStream.flush();

			// generate response
			return Response
					.ok(compress(byteStream.toByteArray()),
							MediaType.APPLICATION_OCTET_STREAM)
					.header("content-disposition",
							"attachment; filename = " + name + ".oxl").build();
		} catch (XMLStreamException e) {
			throw new WebApplicationException(e,
					Response.Status.INTERNAL_SERVER_ERROR);
		} catch (JAXBException e) {
			throw new WebApplicationException(e,
					Response.Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Build OXL Response for given lists of concepts and relations and file
	 * name
	 * 
	 * @param name
	 *            file name
	 * @param concepts
	 *            list of concepts
	 * @param relations
	 *            list of relations
	 * @return OXL Response
	 */
	public static Response build(String name, Set<ONDEXConcept> concepts,
			Set<ONDEXRelation> relations) {
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		try {

			// add tags of all concepts
			for (ONDEXConcept c : concepts.toArray(new ONDEXConcept[0])) {
				concepts.addAll(c.getTags());
			}
			
			// add tags of all relations
			for (ONDEXRelation r : relations) {
				concepts.addAll(r.getTags());
			}
			
			// new XML writer for output stream
			XMLStreamWriter2 xmlWriteStream = (XMLStreamWriter2) WstxOutputFactoryProvider.xmlw
					.createXMLStreamWriter(byteStream, CharsetNames.CS_UTF8);

			// export complete graph
			export.buildDocument(xmlWriteStream, concepts, relations);

			// flush out all data
			xmlWriteStream.flush();

			// generate response
			return Response
					.ok(compress(byteStream.toByteArray()),
							MediaType.APPLICATION_OCTET_STREAM)
					.header("content-disposition",
							"attachment; filename = " + name + ".oxl").build();
		} catch (XMLStreamException e) {
			throw new WebApplicationException(e,
					Response.Status.INTERNAL_SERVER_ERROR);
		} catch (JAXBException e) {
			throw new WebApplicationException(e,
					Response.Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * GZIP compression required for OXL.
	 * 
	 * @param array
	 *            uncompressed data
	 * @return compressed data
	 */
	private static byte[] compress(byte[] array) {
		// compress output
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		try {
			GZIPOutputStream gzipOutputStream = new GZIPOutputStream(
					byteArrayOutputStream);
			gzipOutputStream.write(array);
			gzipOutputStream.close();
		} catch (IOException e) {
			throw new WebApplicationException(e,
					Response.Status.INTERNAL_SERVER_ERROR);
		}
		return byteArrayOutputStream.toByteArray();
	}
}
