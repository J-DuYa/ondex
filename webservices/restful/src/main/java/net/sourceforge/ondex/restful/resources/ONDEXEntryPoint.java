package net.sourceforge.ondex.restful.resources;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.constructs.blocking.SelfPopulatingCache;
import net.sourceforge.ondex.core.searchable.LuceneEnv;
import net.sourceforge.ondex.restful.LaunchApp;
import net.sourceforge.ondex.restful.util.ONDEXGraphKey;
import net.sourceforge.ondex.restful.util.ONDEXGraphLoader;

import com.sun.jersey.spi.resource.Singleton;

/**
 * Provides the entry point for service initialisation, e.g. load graphs into
 * registry.
 * 
 * @author taubertj
 * 
 */
@Path("graphs")
@Singleton
public class ONDEXEntryPoint {

	/**
	 * Used to construct the URI linking services.
	 */
	@Context
	private UriInfo ui;

	/**
	 * Cache used for MemoryONDEXGraphs
	 */
	public static SelfPopulatingCache cache = null;

	/**
	 * All graph files found in database.dir
	 */
	public static Map<Integer, ONDEXGraphKey> graphs = new TreeMap<Integer, ONDEXGraphKey>();

	/**
	 * Lucene index for corresponding graph ID, lazy filled
	 */
	public static Map<Integer, LuceneEnv> indicies = new TreeMap<Integer, LuceneEnv>();
	
	static {

		// new EHCACHE manager, singleton
		CacheManager manager = CacheManager.create();

		// 10 graphs at any time, 10 min to live each, 2 min idle allowed
		Cache memoryOnlyCache = new Cache("graphCache", 10, false, false, 600,
				120);
		manager.addCache(memoryOnlyCache);

		// decorator pattern for self populating cache
		ONDEXGraphLoader graphLoader = new ONDEXGraphLoader();
		cache = new SelfPopulatingCache(memoryOnlyCache, graphLoader);

		// list all files in database directory ending .xml
		File dir = new File(LaunchApp.DATABASEDIR);
		System.out.println("Using directory: " + dir.getAbsolutePath());
		File[] files = dir.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				if (name.endsWith(".xml") || name.endsWith(".xml.gz")
						|| name.endsWith(".oxl"))
					return true;
				return false;
			}
		});
		int i = 0;
		for (File file : files) {
			i++;
			ONDEXGraphKey key = new ONDEXGraphKey();
			key.setId(i);
			key.setFile(file.getAbsolutePath());
			graphs.put(i, key);
		}
	}

	/**
	 * Returns the list of graphs loaded in this service.
	 * 
	 * @return text/html
	 */
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String getHtml() {
		StringBuffer html = new StringBuffer("<h2>Graphs:</h2>\n");
		html.append("<table>\n");
		html.append("<tr><th>ID</th><th>Filename</th></tr>\n");
		ONDEXGraphKey[] values = graphs.values().toArray(new ONDEXGraphKey[0]);
		Arrays.sort(values);
		for (ONDEXGraphKey key : values) {
			String path = ui.getAbsolutePath().getPath();
			if (path.endsWith("/"))
				path = path.substring(0, path.length() - 1);
			html.append("<tr><td><a href=\"");
			html.append(path);
			html.append("/");
			html.append(key.getId());
			html.append("\">");
			html.append(key.getId());
			html.append("</a></td><td>");
			html.append(key.getFile());
			html.append("</td></tr>\n");
		}
		html.append("</table>\n");
		return html.toString();
	}

	/**
	 * Returns list of graphs as either XML or JSON.
	 * 
	 * @return application/xml or application/json
	 */
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public List<ONDEXGraphKey> getXmlOrJson() {
		List<ONDEXGraphKey> temp = new ArrayList<ONDEXGraphKey>();
		temp.addAll(graphs.values());
		return temp;
	}
}
