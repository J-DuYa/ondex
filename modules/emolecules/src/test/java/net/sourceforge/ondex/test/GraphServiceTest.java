package net.sourceforge.ondex.test;

import net.sourceforge.ondex.emolecules.graph.Configuration;
import net.sourceforge.ondex.emolecules.graph.GraphService;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.neo4j.graphdb.GraphDatabaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author grzebyta
 */
@RunWith(JUnit4.class)
public class GraphServiceTest {
    
    private static Logger log = LoggerFactory.getLogger(GraphServiceTest.class);
    private static Configuration conf;
    
    public static final String TEST_DATA_FILE = "target/test-classes/version.smi.gz";
    public static final String INDEX_GRAPH_DIR = "target/";
    
    @BeforeClass
    public static void init() throws Exception {
        log.info("build configuration");
        conf = new Configuration()
                .setIndexDirectoryPath(INDEX_GRAPH_DIR, true)
                .setInputFilePath(TEST_DATA_FILE);
    }
    
    @Test
    public void isConfigValid() throws Exception {
        log.info("is configValid test");
        
        log.debug("is configuration valid: " + conf.isValid());
        log.info("\tindex path: " + conf.getIndexDirectoryPath().getCanonicalPath());
        log.info("\tindex path: " + conf.getInputFilePath().getCanonicalPath());
        Assert.assertTrue("configuration should be valid", conf.isValid());
    }
    
    @Test
    public void testGraph() throws Exception {
        log.info("build test graph");
        
        GraphService gs = new GraphService(conf);
        GraphDatabaseService gdb = gs.getGraphManager().getDatabase();
        
        Assert.assertNotNull("database service musn't be null", gdb);
        gdb.shutdown();
    }
    
}
