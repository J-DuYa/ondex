package net.sourceforge.ondex.restful.resources.injectable;

import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import net.sourceforge.ondex.core.AttributeName;
import net.sourceforge.ondex.core.ONDEXGraph;
import net.sourceforge.ondex.restful.resources.ONDEXEntryPoint;

import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.spi.inject.InjectableProvider;

/**
 * Returns a AttributeName for a plain id in path.
 * 
 * @author taubertj
 * 
 */
@Provider
public class AttributeNameInjectableProvider implements
		InjectableProvider<PathParam, Parameter> {

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
	public AttributeNameInjectableProvider(@Context HttpContext hc) {
		this.hc = hc;
	}

	@Override
	public Injectable<?> getInjectable(ComponentContext cc, PathParam pp,
			Parameter param) {

		// only accepts parameters dealing with AttributeName
		if (!AttributeName.class.isAssignableFrom(param.getParameterClass()))
			return null;

		final String name = param.getSourceName();
		return new Injectable<AttributeName>() {
			public AttributeName getValue() {

				// get graph from cache
				String graphid = hc.getUriInfo().getPathParameters()
						.getFirst("graphid");
				ONDEXGraph graph = (ONDEXGraph) ONDEXEntryPoint.cache.get(
						Integer.valueOf(graphid)).getObjectValue();

				// return AttributeName for id
				String value = hc.getUriInfo().getPathParameters()
						.getFirst(name);
				return graph.getMetaData().getAttributeName(value);
			}
		};
	}

	@Override
	public ComponentScope getScope() {
		return ComponentScope.PerRequest;
	}

}
