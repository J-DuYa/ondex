package net.sourceforge.ondex.restful.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;

import net.sf.ehcache.constructs.blocking.CacheEntryFactory;
import net.sourceforge.ondex.core.ONDEXGraph;
import net.sourceforge.ondex.core.memory.MemoryONDEXGraph;
import net.sourceforge.ondex.parser.oxl.ConceptMetaDataParser;
import net.sourceforge.ondex.parser.oxl.ConceptParser;
import net.sourceforge.ondex.parser.oxl.GeneralMetaDataParser;
import net.sourceforge.ondex.parser.oxl.RelationMetaDataParser;
import net.sourceforge.ondex.parser.oxl.RelationParser;
import net.sourceforge.ondex.parser.oxl.XmlParser;
import net.sourceforge.ondex.restful.resources.ONDEXEntryPoint;
import net.sourceforge.ondex.tools.ziptools.ZipEndings;

import org.codehaus.stax2.XMLInputFactory2;
import org.codehaus.stax2.XMLStreamReader2;

import com.ctc.wstx.io.CharsetNames;

/**
 * Implements CacheEntryFactory to automatically load ONDEX graphs from disk.
 * 
 * @author taubertj
 * 
 */
public class ONDEXGraphLoader implements CacheEntryFactory {

	/**
	 * Creates an ONDEXGraph by loading its content from file.
	 * 
	 * @param arg0
	 *            is the integer index id of graph to load
	 */
	@Override
	public Object createEntry(Object arg0) throws Exception {

		// all graphs index by id and value of ONDEXGraphKey
		ONDEXGraphKey key = ONDEXEntryPoint.graphs.get(arg0);
		if (key == null)
			return null;

		// new ONDEXGraph named by file
		ONDEXGraph aog = new MemoryONDEXGraph(key.getFile());

		// load from file
		File file = new File(key.getFile());

		System.setProperty("javax.xml.stream.XMLInputFactory",
				"com.ctc.wstx.stax.WstxInputFactory");
		XMLInputFactory2 xmlInput = (XMLInputFactory2) XMLInputFactory2
				.newInstance();
		xmlInput.configureForSpeed();

		int detectedEnding = ZipEndings.getPostfix(file);

		InputStream inStream = null;

		try {
			switch (detectedEnding) {

			case ZipEndings.GZ:
				inStream = new GZIPInputStream(new FileInputStream(file));
				System.out.println("Detected GZIP file");
				break;
			case ZipEndings.OXL:
				inStream = new GZIPInputStream(new FileInputStream(file));
				System.out.println("Detected OXL file");
				break;
			case ZipEndings.XML:
				inStream = new FileInputStream(file);
				System.out.println("Detected Uncompressed file");
				break;
			default:
				System.err.println("Unsupported filetype");
				return null;
			}

			if (inStream != null) {

				// configure Parser
				XMLStreamReader2 xmlr = (XMLStreamReader2) xmlInput
						.createXMLStreamReader(inStream, CharsetNames.CS_UTF8);

				// start parsing
				XmlParser parser = new XmlParser();

				// this is a little hack to be able to browse meta data file
				parser.registerParser("cv",
						new ConceptMetaDataParser(aog, "cv"));
				parser.registerParser("unit", new GeneralMetaDataParser(aog,
						"unit"));
				parser.registerParser("attrname", new GeneralMetaDataParser(
						aog, "attrname"));
				parser.registerParser("evidences", new GeneralMetaDataParser(
						aog, "evidences"));
				parser.registerParser("cc",
						new ConceptMetaDataParser(aog, "cc"));
				parser.registerParser("relation_type",
						new RelationMetaDataParser(aog, "relation_type"));
				parser.registerParser("relationtypeset",
						new RelationMetaDataParser(aog, "relationtypeset"));

				// hashtable for id mapping old to new concept ids
				Map<Integer, Integer> table = new HashMap<Integer, Integer>();
				Map<Integer, Set<Integer>> context = new HashMap<Integer, Set<Integer>>();

				// register the parser for concepts and relations
				parser.registerParser("concept", new ConceptParser(aog, table,
						context));
				parser.registerParser("relation",
						new RelationParser(aog, table));
				parser.parse(xmlr);
				ConceptParser.syncContext(aog, table, context);

				// close reader
				xmlr.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
			
		return aog;
	}

}
