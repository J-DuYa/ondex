package net.sourceforge.ondex.emolecules.graph;

import java.io.IOException;
import java.io.Serializable;
import java.util.Iterator;
import net.sourceforge.ondex.emolecules.io.RelationsTypes;
import net.sourceforge.ondex.emolecules.io.Smile;
import net.sourceforge.ondex.emolecules.io.SmilesIteratorFactory;
import org.neo4j.graphdb.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author grzebyta
 */
public class GraphService implements Serializable {

    private static final long serialVersionUID = 8156652691906253293L;
    private Logger log = LoggerFactory.getLogger(getClass());
    private GraphManager gm;
    private SmilesIteratorFactory smifac;

    public GraphService(Configuration conf) throws IOException {
        log.debug("configure service");

        // build smiterator
        smifac = new SmilesIteratorFactory(conf.getInputFilePath());

        // build Neo4j graph manager
        gm = GraphManager.instantiate(conf);
    }

    public void run() {
        log.info("run main parsing");

        Iterator<Smile> smiterator = smifac.iterator();
        while (smiterator.hasNext()) {
            Smile smi = smiterator.next();

            // buid current smi Node
            Node current = gm.createNode(smi.getId());
            current.setProperty("smile", smi.getSmile());

            // get parent node
            Node parent = gm.createNode(smi.getParent());

            // put parent to child
            current.createRelationshipTo(parent, RelationsTypes.HAS_PARENT);
        }
    }

    public GraphManager getGraphManager() {
        return gm;
    }
}
