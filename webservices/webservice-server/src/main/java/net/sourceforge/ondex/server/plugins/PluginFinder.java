/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sourceforge.ondex.server.plugins;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import net.sourceforge.ondex.export.ONDEXExport;
import net.sourceforge.ondex.filter.ONDEXFilter;
import net.sourceforge.ondex.mapping.ONDEXMapping;
import net.sourceforge.ondex.parser.ONDEXParser;
import net.sourceforge.ondex.transformer.ONDEXTransformer;
import net.sourceforge.ondex.wsapi.exceptions.CaughtException;
import net.sourceforge.ondex.wsapi.exceptions.PluginNotFoundException;

import org.apache.log4j.Logger;

/**
 * 
 * @author Christian Brenninkmeijer
 */
public class PluginFinder {

	private static final Logger logger = Logger.getLogger(PluginFinder.class);

	private static Hashtable<String, String> mappings = new Hashtable<String, String>();

	private static Hashtable<String, String> filters = new Hashtable<String, String>();

	private static Hashtable<String, String> exports = new Hashtable<String, String>();

	private static Hashtable<String, String> parsers = new Hashtable<String, String>();

	private static Hashtable<String, String> transformers = new Hashtable<String, String>();

	private final static String SLASH_START = "net/sourceforge/ondex/";

	private final static String NCL_SLASH_START = "uk/ac/ncl/cs/ondex/";

	//private final static String DOT_START = "net.sourceforge.ondex.";

	//private final static String EXPORT_START = SLASH_START + "export/";

	//private final static String EXPORT_END = ".Export";

	//private final static String MAPPING_START = DOT_START + "mapping/";

	//private final static String MAPPING_END = ".Mapping";

	//private final static String FILTER_START = DOT_START + "filter/";

	//private final static String FILTER_END = ".Filter";

	//private final static String PARSER_START = DOT_START + "parser/";

	//private final static String PARSER_END = ".Parser";

	//private final static String TRANSFORMER_START = DOT_START + "transformer/";

	//private final static String TRANSFORMER_END = ".Transformer";

	private static PluginFinder instance;

	public static synchronized PluginFinder getInstance()
			throws CaughtException {
		if (instance == null) {
			instance = new PluginFinder();
		}
		return instance;
	}

	private String scrub(String oldName) {
		String newName = oldName.replace("/", ".");
		int end = newName.lastIndexOf(".class");
		return newName.substring(0, end);
	}

	private PluginFinder() throws CaughtException {
		load();
	}

	private void load() throws CaughtException {
		String webappRoot = System.getProperties().getProperty("webapp.root");
		File dir = new File(webappRoot + File.separator + "WEB-INF"
				+ File.separator + "lib");
		logger.info(dir.getAbsolutePath());
		File[] files = dir.listFiles();
		for (File file : files) {
			if (file.isFile()) {
				logger.info(file.getAbsolutePath());
				JarFile jar;
				try {
					jar = new JarFile(file);
				} catch (IOException ex) {
					throw new CaughtException("Loading loading " + file, ex,
							logger);
				}
				Enumeration<JarEntry> entries = jar.entries();
				while (entries.hasMoreElements()) {
					JarEntry jarEntry = entries.nextElement();
					String fullName = jarEntry.getName();
					loadClass(fullName);
				}
			}
		}
		// catch (Exception e) {
		// throw new CaughtException("finding Plugings ",e,logger);
		// }
	}

	private void loadClass(String fullName) throws CaughtException {
		if ((fullName.startsWith(SLASH_START) || fullName
				.startsWith(NCL_SLASH_START))
				&& fullName.endsWith(".class")
				&& !fullName.contains("$")) {
			String className = scrub(fullName);
			ClassLoader classLoader = Thread.currentThread()
					.getContextClassLoader();
			Class<?> theClass;
			try {
				theClass = classLoader.loadClass(className);
			} catch (ClassNotFoundException ex) {
				throw new CaughtException("Loading Class " + fullName, ex,
						logger);
			}
			Class<?> superClass = theClass.getSuperclass();
			while (superClass != null) {
				if (superClass.equals(ONDEXMapping.class)) {
					loadMapping(theClass, className);
				} else if (superClass.equals(ONDEXFilter.class)) {
					loadFilter(theClass, className);
				} else if (superClass.equals(ONDEXExport.class)) {
					loadExport(theClass, className);
				} else if (superClass.equals(ONDEXParser.class)) {
					loadParser(theClass, className);
				} else if (superClass.equals(ONDEXTransformer.class)) {
					loadTransformer(theClass, className);
				}
				superClass = superClass.getSuperclass();
			}
		}
	}

	private void loadMapping(Class<?> theClass, String className)
			throws CaughtException {
		try {
			try {
				ONDEXMapping mapping = (ONDEXMapping) theClass.newInstance();
				String id = mapping.getId();
				if (mappings.contains(id)) {
					logger.info("Skipping repeated " + id);
				} else {
					logger.info("Mapping: " + id + " = " + className);
					mappings.put(id, className);
				}
			} catch (InstantiationException e) {
				// Must be abstract
				logger.info("abstract");
			}
		} catch (Exception e) {
			throw new CaughtException("finding Plugings ", e, logger);
		}
	}

	private void loadFilter(Class<?> theClass, String className)
			throws CaughtException {
		try {
			try {
				ONDEXFilter filter = (ONDEXFilter) theClass.newInstance();
				String id = filter.getId();
				if (filters.contains(id)) {
					logger.info("Skipping repeated " + id);
				} else {
					logger.info("Filter: " + id + " = " + className);
					filters.put(id, className);
				}
			} catch (InstantiationException e) {
				// Must be abstract
				logger.info("abstract");
			}
		} catch (Exception e) {
			throw new CaughtException("finding Plugings ", e, logger);
		}
	}

	private void loadExport(Class<?> theClass, String className)
			throws CaughtException {
		try {
			try {
				// System.setProperty("javax.xml.stream.XMLOutputFactory",
				// "com.ctc.wstx.stax.WstxOutputFactory");
				ONDEXExport export = (ONDEXExport) theClass.newInstance();
				String id = export.getId();
				if (exports.contains(id)) {
					logger.info("Skipping repeated " + id);
				} else {
					logger.info("Export: " + id + " = " + className);
					exports.put(id, className);
				}
			} catch (InstantiationException e) {
				// Must be abstract
				logger.info("abstract");
			}
		} catch (Exception e) {
			throw new CaughtException("finding Plugings ", e, logger);
		}
	}

	private void loadParser(Class<?> theClass, String className)
			throws CaughtException {
		try {
			try {
				ONDEXParser parser = (ONDEXParser) theClass.newInstance();
				String id = parser.getId();
				if (parsers.contains(id)) {
					logger.info("Skipping repeated " + id);
				} else {
					logger.info("Parser: " + id + " = " + className);
					parsers.put(id, className);
				}
			} catch (InstantiationException e) {
				// Must be abstract
				logger.info("abstract");
			}
		} catch (Exception e) {
			throw new CaughtException("finding Plugings ", e, logger);
		}
	}

	private void loadTransformer(Class<?> theClass, String className)
			throws CaughtException {
		try {
			try {
				ONDEXTransformer transformer = (ONDEXTransformer) theClass
						.newInstance();
				String id = transformer.getId();
				if (transformers.contains(id)) {
					logger.info("Skipping repeated " + id);
				} else {
					transformers.put(id, className);
					logger.info("Transformer: " + id + " = " + className);
				}
			} catch (InstantiationException e) {
				// Must be abstract
				logger.info("abstract");
			}
		} catch (Exception e) {
			throw new CaughtException("finding Plugings ", e, logger);
		}
	}

	public Set<String> getExportNames() {
		return exports.keySet();
	}

	public Set<String> getFilterNames() {
		return filters.keySet();
	}

	public Set<String> getParserNames() {
		return parsers.keySet();
	}

	public Set<String> getMappingNames() {
		return mappings.keySet();
	}

	public Set<String> getTransformerNames() {
		return transformers.keySet();
	}

	//private String slashToDot(String name) {
	//	name = name.replace("/", ".");
	//	return name;
	//}

	public ONDEXFilter getFilter(String name) throws CaughtException,
			PluginNotFoundException {
		// ogger.info("getMapping called with "+name);
		String fullName;
		if (filters.containsKey(name)) {
			try {
				fullName = filters.get(name);
				ClassLoader classLoader = Thread.currentThread()
						.getContextClassLoader();
				Class<?> theClass = classLoader.loadClass(fullName);
				ONDEXFilter filter = (ONDEXFilter) theClass.newInstance();
				// ogger.info("mapping found: "+mapping);
				return filter;
			} catch (Exception ex) {
				throw new CaughtException(ex, logger);
			}
		} else {
			throw new PluginNotFoundException("Unable to find filter class: "
					+ name, logger);
		}
	}

	public ONDEXParser getParser(String name) throws CaughtException,
			PluginNotFoundException {
		// ogger.info("getMapping called with "+name);
		String fullName = "?";
		if (parsers.containsKey(name)) {
			try {
				fullName = parsers.get(name);
				ClassLoader classLoader = Thread.currentThread()
						.getContextClassLoader();
				Class<?> theClass = classLoader.loadClass(fullName);
				ONDEXParser parser = (ONDEXParser) theClass.newInstance();
				// ogger.info("mapping found: "+mapping);
				return parser;
			} catch (Exception ex) {
				throw new CaughtException(ex, logger);
			}
		} else {
			throw new PluginNotFoundException("Unable to find parser class: "
					+ fullName, logger);
		}
	}

	public ONDEXExport getExport(String name) throws CaughtException,
			PluginNotFoundException {
		// ogger.info("getMapping called with "+name);
		String fullName = "?";
		if (exports.containsKey(name)) {
			try {
				fullName = exports.get(name);
				// ogger.info(name);
				// ogger.info(fullName);
				System.setProperty("javax.xml.stream.XMLOutputFactory",
						"com.ctc.wstx.stax.WstxOutputFactory");
				ClassLoader classLoader = Thread.currentThread()
						.getContextClassLoader();
				Class<?> theClass = classLoader.loadClass(fullName);
				ONDEXExport export = (ONDEXExport) theClass.newInstance();
				return export;
			} catch (Exception ex) {
				throw new CaughtException(ex, logger);
			}
		} else {
			throw new PluginNotFoundException("Unable to find export class: "
					+ fullName, logger);
		}
	}

	public ONDEXMapping getMapping(String name) throws CaughtException,
			PluginNotFoundException {
		// ogger.info("getMapping called with "+name);
		String fullName = "?";
		if (mappings.containsKey(name)) {
			try {
				fullName = mappings.get(name);
				// ogger.info("fullname = "+fullName);
				ClassLoader classLoader = Thread.currentThread()
						.getContextClassLoader();
				Class<?> theClass = classLoader.loadClass(fullName);
				// ogger.info("created object");
				ONDEXMapping mapping = (ONDEXMapping) theClass.newInstance();
				// ogger.info("mapping found: "+mapping);
				return mapping;
			} catch (Exception ex) {
				// ogger.info("exception found "+ex);
				throw new CaughtException(ex, logger);
			}
		} else {
			// for (String check:mappings){
			// ogger.info(check);
			// }
			throw new PluginNotFoundException("Unable to find mapping class: "
					+ fullName, logger);
		}
	}

	public String getClassName(String name, TypeOfPlugin pluginType)
			throws CaughtException, PluginNotFoundException {
		switch (pluginType) {
		case EXPORT:
		case EXPORTJOB:
			if (exports.containsKey(name)) {
				return exports.get(name);
			} else {
				throw new PluginNotFoundException(
						"Unable to find export class: " + name, logger);
			}
		case FILTER:
		case FILTERJOB:
			if (filters.containsKey(name)) {
				return filters.get(name);
			} else {
				throw new PluginNotFoundException(
						"Unable to find filter class: " + name, logger);
			}
		case PARSER:
		case PARSERJOB:
			if (parsers.containsKey(name)) {
				return parsers.get(name);
			} else {
				throw new PluginNotFoundException(
						"Unable to find parser class: " + name, logger);
			}
		case MAPPING:
		case MAPPINGJOB:
			if (mappings.containsKey(name)) {
				return mappings.get(name);
			} else {
				throw new PluginNotFoundException(
						"Unable to find mapping class: " + name, logger);
			}
		case TRANSFORMER:
		case TRANSFORMERJOB:
			if (transformers.containsKey(name)) {
				return transformers.get(name);
			} else {
				throw new PluginNotFoundException(
						"Unable to find transformer class: " + name, logger);
			}
		}
		throw new PluginNotFoundException(
				"Unexpected TypeOfPlugin in getClassName" + pluginType, logger);
	}

	public ONDEXTransformer getTransformer(String name) throws CaughtException,
			PluginNotFoundException {
		// ogger.info("getMapping called with "+name);
		String fullName = "?";
		if (transformers.containsKey(name)) {
			try {
				fullName = transformers.get(name);
				ClassLoader classLoader = Thread.currentThread()
						.getContextClassLoader();
				Class<?> theClass = classLoader.loadClass(fullName);
				ONDEXTransformer transformer = (ONDEXTransformer) theClass
						.newInstance();
				// ogger.info("mapping found: "+mapping);
				return transformer;
			} catch (Exception ex) {
				throw new CaughtException(ex, logger);
			}
		} else {
			throw new PluginNotFoundException(
					"Unable to find transformer class: " + fullName, logger);
		}
	}

}
