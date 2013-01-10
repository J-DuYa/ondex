package net.sourceforge.ondex.emolecules.graph;

import java.io.IOException;
import java.io.Serializable;
import java.util.Iterator;
import net.sourceforge.ondex.emolecules.cdk.CdkCalculator;
import net.sourceforge.ondex.emolecules.io.Smile;
import net.sourceforge.ondex.emolecules.io.SmilesIteratorFactory;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author grzebyta
 */
public class GraphLoadingService implements Serializable {

    private static final long serialVersionUID = 8156652691906253293L;
    private Logger log = LoggerFactory.getLogger(getClass());
    private GraphManager gm;
    private SmilesIteratorFactory smifac;
    private Index<Node> chemicalIdIndex;
    private Index<Node> chemicalSmileIndex;
    
    private CdkCalculator calc;

    public GraphLoadingService(Configuration conf) throws IOException {
        log.debug("configure service");

        // build smiterator
        smifac = new SmilesIteratorFactory(conf.getInputFilePath());

        // build Neo4j graph manager
        gm = GraphManager.instantiate(conf);
        
        // create chemical index
        chemicalIdIndex = gm.getDatabase().index().forNodes("chemicalId");
        chemicalSmileIndex = gm.getDatabase().index().forNodes("chemicalSmile");
        
        // instantiate calculator
        calc = new CdkCalculator();
    }

    public void run() {
        log.info("run main parsing");

        Iterator<Smile> smiterator = smifac.iterator();
        Transaction tx = gm.getDatabase().beginTx();

        try {
            while (smiterator.hasNext()) {
                Smile smi = smiterator.next();
                log.debug("found smiles: " + smi);

                // buid current smi Node
                Node current = gm.createChemicalNode(smi.getId());
                String smile = smi.getSmile();
                
                current.setProperty("smile", smile);
                current.setProperty("m", calc.getMolecularMass(smile));
                
                chemicalIdIndex.add(current, "id", smi.getId());
                chemicalSmileIndex.add(current, "smile", smile);

                // get parent node
                Node parent = gm.createChemicalNode(smi.getParent());
                chemicalIdIndex.add(parent, "id", smi.getParent());
                

                // put parent to child
                current.createRelationshipTo(parent, RelationsTypes.HAS_PARENT);
            }
            
            tx.success();
        } catch (Exception e) {
            tx.failure();
        } finally {
            tx.finish();
        }
    }

    public GraphManager getGraphManager() {
        return gm;
    }
}
