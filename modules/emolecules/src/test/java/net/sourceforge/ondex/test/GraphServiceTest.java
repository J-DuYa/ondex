package net.sourceforge.ondex.test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.sourceforge.ondex.emolecules.graph.Configuration;
import net.sourceforge.ondex.emolecules.graph.GraphService;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.helpers.collection.IteratorUtil;
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
        gs.run();
        GraphDatabaseService gdb = gs.getGraphManager().getDatabase();
        Assert.assertNotNull("database service musn't be null", gdb);
        
        List<Node> allNodes = getAllNodes(gdb);
        Assert.assertTrue("empty list is wrong result. found: " + allNodes.size()
                ,allNodes.size() > 0);
        log.info("number of nodes: " + allNodes.size());
        log.info("node id: " + allNodes.get(0).getId());
        
        gdb.shutdown();
    }
    
    protected List<Node> getAllNodes(GraphDatabaseService gs) {
        log.debug("get all nodes");
        
        String q = "START n=node(*) RETURN n;";
        ExecutionEngine ee = new ExecutionEngine(gs);
        ExecutionResult result = ee.execute(q);
        
        Iterator<Node> resIter = result.columnAs("n");
        log.debug("list of columns: " + result.columns());
        
        ArrayList<Node> toReturn = new ArrayList<Node>(0);
        IteratorUtil.addToCollection(resIter, toReturn);
        
        for (Node node :toReturn) {
            log.debug("\tnode: " + node);
        }
        
        return toReturn;
    }
    
            
    
}
