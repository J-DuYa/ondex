package net.sourceforge.ondex.restful.resources.readers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;

import net.sourceforge.ondex.core.ONDEXGraph;
import net.sourceforge.ondex.core.ONDEXRelation;
import net.sourceforge.ondex.restful.resources.ONDEXEntryPoint;

import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.spi.resource.Singleton;

/**
 * Returns a relation for a plain id.
 * 
 * @author taubertj
 * 
 */
@Provider
@Singleton
@Consumes(MediaType.TEXT_PLAIN)
public class ONDEXRelationMessageBodyReader implements
		MessageBodyReader<ONDEXRelation> {

	/**
	 * Important to keep track of parameters in path.
	 */
	private final @Context
	HttpContext hc;

	/**
	 * Sets the HttpContext for this instance.
	 * 
	 * @param hc
	 *            HttpContext
	 */
	public ONDEXRelationMessageBodyReader(@Context HttpContext hc) {
		this.hc = hc;
	}

	@Override
	public boolean isReadable(Class<?> clazz, Type type, Annotation[] anno,
			MediaType mediatype) {
		// accept all subclasses of ONDEXRelation
		return ONDEXRelation.class.isAssignableFrom(clazz);
	}

	@Override
	public ONDEXRelation readFrom(Class<ONDEXRelation> clazz, Type type,
			Annotation[] anno, MediaType mediatype,
			MultivaluedMap<String, String> map, InputStream instream)
			throws IOException, WebApplicationException {

		// get graph from cache
		String graphid = hc.getUriInfo().getPathParameters()
				.getFirst("graphid");
		ONDEXGraph graph = (ONDEXGraph) ONDEXEntryPoint.cache.get(
				Integer.valueOf(graphid)).getObjectValue();

		// read relation id from stream
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				instream));
		Integer id = Integer.valueOf(reader.readLine());

		// return corresponding relation
		return graph.getRelation(id);
	}

}
