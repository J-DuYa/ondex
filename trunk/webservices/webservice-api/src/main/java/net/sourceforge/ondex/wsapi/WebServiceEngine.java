package net.sourceforge.ondex.wsapi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jws.WebService;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import net.sourceforge.ondex.config.Config;
import net.sourceforge.ondex.config.ONDEXGraphRegistry;
import net.sourceforge.ondex.core.Attribute;
import net.sourceforge.ondex.core.AttributeName;
import net.sourceforge.ondex.core.ConceptAccession;
import net.sourceforge.ondex.core.ConceptClass;
import net.sourceforge.ondex.core.ConceptName;
import net.sourceforge.ondex.core.DataSource;
import net.sourceforge.ondex.core.EvidenceType;
import net.sourceforge.ondex.core.ONDEXConcept;
import net.sourceforge.ondex.core.ONDEXGraph;
import net.sourceforge.ondex.core.ONDEXGraphMetaData;
import net.sourceforge.ondex.core.ONDEXRelation;
import net.sourceforge.ondex.core.RelationKey;
import net.sourceforge.ondex.core.RelationType;
import net.sourceforge.ondex.core.Unit;
import net.sourceforge.ondex.core.memory.MemoryONDEXGraph;
import net.sourceforge.ondex.core.persistent.BerkeleyEnv;
import net.sourceforge.ondex.export.oxl.Export;
import net.sourceforge.ondex.logging.ONDEXLogger;
import net.sourceforge.ondex.tools.DirUtils;
import net.sourceforge.ondex.wsapi.cleanup.CleanupContextListener;
import net.sourceforge.ondex.wsapi.exceptions.AttributeNameNotFoundException;
import net.sourceforge.ondex.wsapi.exceptions.AttributeNotFoundException;
import net.sourceforge.ondex.wsapi.exceptions.CaughtException;
import net.sourceforge.ondex.wsapi.exceptions.ConceptAccessionNotFoundException;
import net.sourceforge.ondex.wsapi.exceptions.ConceptClassNotFoundException;
import net.sourceforge.ondex.wsapi.exceptions.ConceptNameNotFoundException;
import net.sourceforge.ondex.wsapi.exceptions.ConceptNotFoundException;
import net.sourceforge.ondex.wsapi.exceptions.CreateException;
import net.sourceforge.ondex.wsapi.exceptions.DataSourceNotFoundException;
import net.sourceforge.ondex.wsapi.exceptions.EvidenceTypeNotFoundException;
import net.sourceforge.ondex.wsapi.exceptions.GraphNotFoundException;
import net.sourceforge.ondex.wsapi.exceptions.GraphNotImportedException;
import net.sourceforge.ondex.wsapi.exceptions.IllegalClassException;
import net.sourceforge.ondex.wsapi.exceptions.IllegalNameException;
import net.sourceforge.ondex.wsapi.exceptions.KeyNotFoundException;
import net.sourceforge.ondex.wsapi.exceptions.MetaDataNotFoundException;
import net.sourceforge.ondex.wsapi.exceptions.NullInViewException;
import net.sourceforge.ondex.wsapi.exceptions.NullViewException;
import net.sourceforge.ondex.wsapi.exceptions.ParsingException;
import net.sourceforge.ondex.wsapi.exceptions.ReadOnlyException;
import net.sourceforge.ondex.wsapi.exceptions.RelationNotFoundException;
import net.sourceforge.ondex.wsapi.exceptions.RelationTypeNotFoundException;
import net.sourceforge.ondex.wsapi.exceptions.StartUpException;
import net.sourceforge.ondex.wsapi.exceptions.UnitNotFoundException;
import net.sourceforge.ondex.wsapi.exceptions.WebserviceException;
import net.sourceforge.ondex.wsapi.plugins.PluginWS;
import net.sourceforge.ondex.wsapi.result.WSAttributeName;
import net.sourceforge.ondex.wsapi.result.WSConcept;
import net.sourceforge.ondex.wsapi.result.WSConceptAccession;
import net.sourceforge.ondex.wsapi.result.WSConceptClass;
import net.sourceforge.ondex.wsapi.result.WSConceptName;
import net.sourceforge.ondex.wsapi.result.WSDataSource;
import net.sourceforge.ondex.wsapi.result.WSEvidenceType;
import net.sourceforge.ondex.wsapi.result.WSGraph;
import net.sourceforge.ondex.wsapi.result.WSGraphMetaData;
import net.sourceforge.ondex.wsapi.result.WSRelation;
import net.sourceforge.ondex.wsapi.result.WSRelationKey;
import net.sourceforge.ondex.wsapi.result.WSRelationType;
import net.sourceforge.ondex.wsapi.result.WSUnit;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.codehaus.stax2.XMLOutputFactory2;
import org.codehaus.stax2.XMLStreamWriter2;

import com.ctc.wstx.api.WstxOutputProperties;
import com.ctc.wstx.io.CharsetNames;
import com.ctc.wstx.stax.WstxOutputFactory;

/**
 * Webservice implementation of OndexGraph.
 * <p/>
 *
 * @author David Withers
 * @author Christian Brenninkmeijer
 */
@WebService(endpointInterface = "net.sourceforge.ondex.webservice.OndexGraph")
public class WebServiceEngine { 

    private static final Logger logger = Logger.getLogger(WebServiceEngine.class);

    public static final int ERROR_ID = -1;

    private static final String DB_DIR = "ondex-db";

    private static final String DEFAULT_USER = "none";

    private static final String DEFAULT_PASSWORD = "8fe956;lt5";

    private static final String GRAPH_DIR_PREFIX = "graph";

    private static final char GRAPH_DIR_PRE_ID = '{';

    private static final char GRAPH_DIR_POST_ID = '}';

    public static String NEW_LINE = System.getProperty("line.separator");

    private File dbPath;

    private Map<Long, BerkeleyEnv> databases = new HashMap<Long, BerkeleyEnv>();

    private static WebServiceEngine webServiceEngine = null;

    private String ondexDir;

    private final boolean IGNORE_NULLS_IN_VIEWS = true;

    private WebServiceEngine() {
        logger.info("new webserviceEngine");
        System.getProperty("javax.xml.stream.XMLInputFactory");
    }

    private void readEbvEntries() throws CaughtException {
        try {
            Context env = (Context) new InitialContext().lookup("java:comp/env");
            Hashtable details = env.getEnvironment();
            Enumeration keys = details.keys();
            while (keys.hasMoreElements()) {
                String key = (String) keys.nextElement();
                if (System.getProperty(key) != null) {
                    logger.error("Found " + key + " in both System.properties" +
                            " and <ebv-entry>. Ignoring <ebv-entry>.");
                } else {
                    String value = (String) env.lookup(key);
                    logger.info("Setting System property: " + key + " to " +  value);
                    System.setProperty(key, value);
                }
            }
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
    }

    /**
     * Factory method for WebServiceEngine.
     * Ensures that all webservices use the same engine.
     *
     * @return WebServiceEngine
     */
    public static WebServiceEngine getWebServiceEngine() {
        if (webServiceEngine == null) {
            webServiceEngine = new WebServiceEngine();
        }
        return webServiceEngine;
    }

    public String addTagConcept(Long graphId, Integer conceptId,
                                Integer tagId)
            throws GraphNotFoundException, ReadOnlyException,
            ConceptNotFoundException, CaughtException {
        ONDEXGraph graph = getGraphToEdit(graphId);
        ONDEXConcept concept = getConcept(graph, conceptId);
        ONDEXConcept tag = getConcept(graph, tagId);
        try {
            concept.addTag(tag);
            commit(graphId);
            return ("Tag " + tagId + " added to Concept " + conceptId + " in graph " + graphId);
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
    }

    public String addTagRelation(Long graphId, Integer relationId,
                                 Integer tagId)
            throws GraphNotFoundException, ReadOnlyException,
            RelationNotFoundException, ConceptNotFoundException, CaughtException {
        ONDEXGraph graph = getGraphToEdit(graphId);
        ONDEXRelation relation = getRelation(graph, relationId);
        ONDEXConcept tag = getConcept(graph, tagId);
        try {
            relation.addTag(tag);
            commit(graphId);
            return ("Tag " + tagId + " added to Relation " + relationId + " in graph " + graphId);
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
    }

    public String addEvidenceTypeConcept(Long graphId, Integer conceptId,
                                         String evidenceTypeId)
            throws GraphNotFoundException, ReadOnlyException,
            ConceptNotFoundException, MetaDataNotFoundException,
            EvidenceTypeNotFoundException, CaughtException {
        ONDEXGraph graph = getGraphToEdit(graphId);
        ONDEXConcept concept = getConcept(graph, conceptId);
        EvidenceType evidenceType = getEvidenceType(graph, evidenceTypeId);
        try {
            concept.addEvidenceType(evidenceType);
            commit(graphId);
            return ("EvidenceType " + evidenceTypeId + " added to Concept " + conceptId + " in graph " + graphId);
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
    }

    public String addEvidenceTypeRelation(Long graphId, Integer relationId,
                                          String evidenceTypeId)
            throws GraphNotFoundException, ReadOnlyException,
            RelationNotFoundException, MetaDataNotFoundException,
            EvidenceTypeNotFoundException, CaughtException {
        ONDEXGraph graph = getGraphToEdit(graphId);
        ONDEXRelation relation = getRelation(graph, relationId);
        EvidenceType evidenceType = getEvidenceType(graph, evidenceTypeId);
        try {
            relation.addEvidenceType(evidenceType);
            commit(graphId);
            return ("EvidenceType " + evidenceTypeId + " added to Relation " + relationId + " in graph " + graphId);
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
    }

    public Boolean checkAttributeName(Long graphId, String id)
            throws GraphNotFoundException, MetaDataNotFoundException, CaughtException {
        ONDEXGraph graph = getGraphFromRegistry(graphId);
        ONDEXGraphMetaData metaData = getMetaData(graph);
        try {
            return metaData.checkAttributeName(id);
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
    }

    public Boolean checkConceptClass(Long graphId, String id)
            throws GraphNotFoundException, MetaDataNotFoundException, CaughtException {
        ONDEXGraph graph = getGraphFromRegistry(graphId);
        ONDEXGraphMetaData metaData = getMetaData(graph);
        try {
            return metaData.checkConceptClass(id);
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
    }

    public Boolean checkDataSource(Long graphId, String id)
            throws GraphNotFoundException, MetaDataNotFoundException, CaughtException {
        ONDEXGraph graph = getGraphFromRegistry(graphId);
        ONDEXGraphMetaData metaData = getMetaData(graph);
        try {
            return metaData.checkDataSource(id);
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
    }

    public Boolean checkEvidenceType(Long graphId, String id)
            throws GraphNotFoundException, MetaDataNotFoundException, CaughtException {
        ONDEXGraph graph = getGraphFromRegistry(graphId);
        ONDEXGraphMetaData metaData = getMetaData(graph);
        try {
            return metaData.checkEvidenceType(id);
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
    }

    public Boolean checkRelationType(Long graphId, String id)
            throws GraphNotFoundException, MetaDataNotFoundException, CaughtException {
        ONDEXGraph graph = getGraphFromRegistry(graphId);
        ONDEXGraphMetaData metaData = getMetaData(graph);
        try{
            return metaData.checkRelationType(id);
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
    }

    public Boolean checkUnit(Long graphId, String id)
            throws GraphNotFoundException, MetaDataNotFoundException, CaughtException {
        ONDEXGraph graph = getGraphFromRegistry(graphId);
        ONDEXGraphMetaData metaData = getMetaData(graph);
        try {
            return metaData.checkUnit(id);
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
    }

    /**
     * @param graphId     - Required
     * @param id          - Required
     * @param fullname    - Optional
     * @param description - Optional
     * @param unitId      - Optional
     * @param datatype    - Required
     * @param specialisationOfAttributeNameId
     *                    - Optional
     * @return
     * @throws GraphNotFoundException
     * @throws MetaDataNotFoundException
     * @throws AttributeNameNotFoundException - only if specialisationOfAttributeNameId != ""
     * @throws CreateException
     * @throws IllegalClassException
     * @throws UnitNotFoundException          - ony if unitId != null
     * @throws ReadOnlyException
     */
    public String createAttributeName(Long graphId, String id, String fullname,
                                      String description, String unitId, String datatypeSt,
                                      String specialisationOfAttributeNameId)
            throws GraphNotFoundException, MetaDataNotFoundException,
            AttributeNameNotFoundException, CreateException,
            IllegalClassException, UnitNotFoundException,
            ReadOnlyException, CaughtException {
        //ogger.info("engine.createAttributeName");
        ONDEXGraph graph = getGraphToEdit(graphId);
        ONDEXGraphMetaData metaData = getMetaData(graph);
        Unit unit = getUnitOrNull(graph, unitId);
        //ogger.info(unit);
        AttributeName specialisationOf = getAttributeNameOrNull(graph,
                specialisationOfAttributeNameId);
        AttributeName attributeName;
        try {
            //ogger.info(datatypeSt);
            Class<?> datatype = Class.forName(datatypeSt);
            //ogger.info(datatype);
            attributeName = metaData.createAttributeName(id,
                    fullname == null ? "" : fullname, description == null ? ""
                            : description, unit, datatype, specialisationOf);
            //ogger.info(attributeName);
            commit(graphId);
        } catch (ClassNotFoundException e) {
            throw new IllegalClassException("Unable to find class " + datatypeSt +
                    "  when creatingAttributeName", logger);
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
        if (attributeName == null) {
            throw new CreateException(
                    "Unable to create attribute name " + id + " " + fullname +
                            " " + description + " in graph " + graph.getName(), logger);
        }
        try{
            return new WSAttributeName(attributeName).getId();
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
    }

    public Integer createConcept(Long graphId, String parserId, String annotation,
                                 String description, String elementOfDataSourceId,
                                 String ofTypeConceptClassId, List<String> evidenceTypeIdList)
            throws GraphNotFoundException, MetaDataNotFoundException,
            ConceptClassNotFoundException, DataSourceNotFoundException,
            EvidenceTypeNotFoundException, ReadOnlyException, CaughtException {
        ONDEXGraph graph = getGraphToEdit(graphId);
        DataSource dataSource = getDataSource(graph, elementOfDataSourceId);
        ConceptClass conceptClass = getConceptClass(graph, ofTypeConceptClassId);
        if (evidenceTypeIdList.size() == 0) {
            throw new EvidenceTypeNotFoundException("Evidence Type list may not be empty.", logger);
        }
        List<EvidenceType> evidence = new ArrayList<EvidenceType>();
        try {
            for (String evidenceTypeId : evidenceTypeIdList) {
                evidence.add(getEvidenceType(graph, evidenceTypeId));
            }
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
        try {
            ONDEXConcept concept = graph.createConcept(parserId, annotation == null ? ""
                    : annotation, description == null ? "" : description, dataSource,
                    conceptClass, evidence);
            commit(graphId);
            return new WSConcept(concept).getId();
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
    }

    /**
     * @param graphId                        -Required
     * @param id                             - Required
     * @param fullname                       - Optional
     * @param description-                   Optional
     * @param specialisationOfConceptClassId - Optional
     * @return
     * @throws GraphNotFoundException
     * @throws MetaDataNotFoundException
     * @throws ConceptClassNotFoundException Only if specialisationOfConceptClassId != ""
     * @throws CreateException
     * @throws ReadOnlyException
     */
    public String createConceptClass(Long graphId, String id, String fullname,
                                     String description, String specialisationOfConceptClassId)
            throws GraphNotFoundException, MetaDataNotFoundException,
            ConceptClassNotFoundException, CreateException,
            ReadOnlyException, CaughtException {
        ONDEXGraph graph = getGraphToEdit(graphId);
        ONDEXGraphMetaData metaData = getMetaData(graph);
        ConceptClass specialisationOf =
                getConceptClassOrNull(graph, specialisationOfConceptClassId);
        ConceptClass conceptClass;
        try {
            conceptClass = metaData.createConceptClass(id,
                    fullname == null ? "" : fullname, description == null ? ""
                            : description, specialisationOf);
            commit(graphId);
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
        if (conceptClass == null) {
            throw new CreateException("Unable to create conceptClass " + id + " " + fullname +
                    " " + description + " in graph " + graph.getName(), logger);
        }
        try {
            return new WSConceptClass(conceptClass).getId();
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
    }

    public String createConceptAttribute (Long graphId, Integer conceptId,
                                   String attributeNameId, Object value, Boolean doIndex)
            throws GraphNotFoundException, AttributeNameNotFoundException,
            MetaDataNotFoundException, ReadOnlyException,
            ConceptNotFoundException, CaughtException {
        ONDEXGraph graph = getGraphToEdit(graphId);
        ONDEXConcept concept = getConcept(graph, conceptId);
        AttributeName attributeName = getAttributeName(graph, attributeNameId);
        try {
            concept.createAttribute (attributeName, value, doIndex);
            commit(graphId);
            return attributeNameId;
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
    }

    public String createConceptName(Long graphId, Integer conceptId,
                                    String name, Boolean isPreferred)
            throws GraphNotFoundException, ReadOnlyException, ConceptNotFoundException,
            CaughtException {
        ONDEXGraph graph = getGraphToEdit(graphId);
        ONDEXConcept concept = getConcept(graph, conceptId);
        try {
            concept.createConceptName(name, isPreferred == null ? false : isPreferred);
            commit(graphId);
            return name;
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
    }

    public String createConceptAccession(Long graphId, Integer conceptId,
                                         String accession, String elementOfDataSourceId, Boolean isAmbiguous)
            throws GraphNotFoundException, MetaDataNotFoundException,
            DataSourceNotFoundException, ReadOnlyException,
            ConceptNotFoundException, CaughtException {
        ONDEXGraph graph = getGraphToEdit(graphId);
        DataSource dataSource = getDataSource(graph, elementOfDataSourceId);
        ONDEXConcept concept = getConcept(graph, conceptId);
        try {
            concept.createConceptAccession(accession, dataSource, isAmbiguous == null ? true : isAmbiguous);
            commit(graphId);
            return accession;
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
    }

    /**
     * @param graphId     - required
     * @param id          -required
     * @param fullname    - Optional
     * @param description - Optional
     * @return
     * @throws GraphNotFoundException
     * @throws MetaDataNotFoundException
     * @throws CreateException
     * @throws ReadOnlyException
     */
    public String createDataSource(Long graphId, String id, String fullname,
                           String description)
            throws GraphNotFoundException, MetaDataNotFoundException,
            CreateException, ReadOnlyException, CaughtException {
        ONDEXGraph graph = getGraphToEdit(graphId);
        ONDEXGraphMetaData metaData = getMetaData(graph);
        DataSource dataSource;
        try {
            dataSource = metaData.createDataSource(id, fullname == null ? "" : fullname,
                    description == null ? "" : description);
            commit(graphId);
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
        if (dataSource == null) {
            throw new CreateException("Unable to create DataSource " + id + " " + fullname +
                    " " + description + " in graph " + graph.getName(), logger);
        }
        try {
            return new WSDataSource(dataSource).getId();
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
    }

    /**
     * @param graphId     -required
     * @param id          -required
     * @param fullname    - Optional
     * @param description
     * @return
     * @throws GraphNotFoundException
     * @throws MetaDataNotFoundException
     * @throws CreateException
     * @throws ReadOnlyException
     */
    public String createEvidenceType(Long graphId, String id, String fullname,
                                     String description)
            throws GraphNotFoundException, MetaDataNotFoundException,
            CreateException, ReadOnlyException, CaughtException {
        ONDEXGraph graph = getGraphToEdit(graphId);
        ONDEXGraphMetaData metaData = getMetaData(graph);
        EvidenceType evidenceType;
        //gger.info("creating EvidenceType with name \"" + fullname + "\"");
        try {
            evidenceType = metaData.createEvidenceType(id,
                    fullname == null ? "" : fullname, description == null ? ""
                            : description);
            commit(graphId);
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
        if (evidenceType == null) {
            throw new CreateException(
                    "Unable to create evidenceType " + id + " " + fullname +
                            " " + description + " in graph " + graph.getName(), logger);
        }
        try {
            return new WSEvidenceType(evidenceType).getId();
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
    }

    public Long createGraph(String name) 
            throws IllegalNameException, CaughtException, GraphNotFoundException,
                GraphNotFoundException  {
        return makeGraph(name);
    }

    public String initialiseMetaData(Long graphId) throws CaughtException, GraphNotFoundException, ReadOnlyException{
        ONDEXGraph graph = getGraphToEdit(graphId);
        return PluginWS.initMetaData(graph);
    }

    /**
     * @param graphId
     * @param fromConceptId
     * @param toConceptId
     * @param relationTypeId
     * @param evidenceTypeIdList
     * @return
     * @throws GraphNotFoundException
     * @throws MetaDataNotFoundException
     * @throws EvidenceTypeNotFoundException
     * @throws RelationTypeNotFoundException
     * @throws ReadOnlyException
     * @throws ConceptNotFoundException
     */
    public Integer createRelation(Long graphId, Integer fromConceptId, Integer toConceptId, String relationTypeId,
            List<String> evidenceTypeIdList)
            throws GraphNotFoundException, MetaDataNotFoundException,
            EvidenceTypeNotFoundException, RelationTypeNotFoundException,
            ReadOnlyException, ConceptNotFoundException, CaughtException {
        ONDEXGraph graph = getGraphToEdit(graphId);
        ONDEXConcept fromConcept = getConcept(graph, fromConceptId);
        ONDEXConcept toConcept = getConcept(graph, toConceptId);
        RelationType relationType = getRelationType(graph, relationTypeId);
        List<EvidenceType> evidence = new ArrayList<EvidenceType>();
        try {
            for (String evidenceTypeId : evidenceTypeIdList) {
                evidence.add(getEvidenceType(graph, evidenceTypeId));
            }
            ONDEXRelation relation = graph.createRelation(fromConcept, toConcept,
                    relationType, evidence);
            commit(graphId);
            return new WSRelation(relation).getId();
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
    }

    public String createRelationAttribute (Long graphId, Integer relationId,
                                    String attributeNameId, Object value, Boolean doIndex)
            throws GraphNotFoundException, AttributeNameNotFoundException,
            MetaDataNotFoundException, ReadOnlyException, CaughtException {
        ONDEXGraph graph = getGraphToEdit(graphId);
        ONDEXRelation relation = graph.getRelation(relationId);
        AttributeName attributeName = getAttributeName(graph, attributeNameId);
        //gger.info("creating Relation Attribute  "+attributeNameId +" doIndex ="+doIndex);
        try {
            relation.createAttribute(attributeName, value, doIndex == null ? false : doIndex);
            commit(graphId);
            ONDEXRelation relation2 = graph.getRelation(relationId);
            Attribute Attribute  = relation2.getAttribute(attributeName);
            //ogger.info("created Relation Attribute  "+ Attribute .getOfType().getId() + " doIndex ="+Attribute .isDoIndex());
            return attributeNameId;
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
    }

    /**
     * @param graphId                        -require
     * @param id                             -required
     * @param fullname                       - Optional
     * @param description                    - Optional
     * @param inverseName                    - Optional
     * @param isAntisymmetric                - Optional
     * @param isReflexive                    - Optional
     * @param isSymmetric                    - Optional
     * @param isTransitiv                    - Optional
     * @param specialisationOfRelationTypeId - Optional
     * @return
     * @throws GraphNotFoundException
     * @throws MetaDataNotFoundException
     * @throws RelationTypeNotFoundException Only if specialisationOfRelationTypeId provided
     * @throws CreateException
     * @throws ReadOnlyException
     */
    public String createRelationType(Long graphId, String id, String fullname,
                                     String description, String inverseName, Boolean isAntisymmetric,
                                     Boolean isReflexive, Boolean isSymmetric, Boolean isTransitiv,
                                     String specialisationOfRelationTypeId)
            throws GraphNotFoundException, MetaDataNotFoundException,
            RelationTypeNotFoundException, CreateException,
            ReadOnlyException, CaughtException {
        ONDEXGraph graph = getGraphToEdit(graphId);
        ONDEXGraphMetaData metaData = getMetaData(graph);
        RelationType specialisationOf = getRelationTypeOrNull(graph,
                specialisationOfRelationTypeId);
        RelationType relationType;
        try {
            relationType = metaData.createRelationType(id,
                    fullname == null ? "" : fullname, description == null ? ""
                            : description, inverseName == null ? "" : inverseName,
                    isAntisymmetric == null ? false : isAntisymmetric,
                    isReflexive == null ? false : isReflexive,
                    isSymmetric == null ? false : isSymmetric,
                    isTransitiv == null ? false : isTransitiv, specialisationOf);
            commit(graphId);
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
        if (relationType == null) {
            throw new CreateException(
                    "Unable to create relationType " + id + " " + fullname +
                            " " + description + " in graph " + graph.getName(), logger);
        }
        try{
            return new WSRelationType(relationType).getId();
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
    }

    /**
     * @param graphId     Required
     * @param id          Required
     * @param fullname    Optional
     * @param description Optional
     * @return
     * @throws GraphNotFoundException
     * @throws MetaDataNotFoundException
     * @throws CreateException
     * @throws ReadOnlyException
     */
    public String createUnit(Long graphId, String id, String fullname,
                             String description)
            throws GraphNotFoundException, MetaDataNotFoundException,
            CreateException, ReadOnlyException, CaughtException {
        ONDEXGraph graph = getGraphToEdit(graphId);
        ONDEXGraphMetaData metaData = getMetaData(graph);
        Unit unit;
        try {
            unit = metaData.createUnit(id, fullname == null ? "" : fullname,
                    description == null ? "" : description);
            commit(graphId);
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
        if (unit == null) {
            throw new CreateException(
                    "Unable to create unit " + id + " " + fullname +
                            " " + description + " in graph " + graph.getName(), logger);
        }
        try {
            return new WSUnit(unit).getId();
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
    }

    public boolean deleteAttributeName(Long graphId, String id)
            throws GraphNotFoundException, AttributeNameNotFoundException,
            MetaDataNotFoundException, ReadOnlyException, CaughtException {
        ONDEXGraph graph = getGraphToEdit(graphId);
        ONDEXGraphMetaData metaData = getMetaData(graph);
        try {
            boolean deleted = metaData.deleteAttributeName(id);
            commit(graphId);
            return deleted;
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
    }

    public boolean deleteConcept(Long graphId, Integer id)
            throws GraphNotFoundException, ReadOnlyException,
            ConceptNotFoundException, CaughtException {
        ONDEXGraph graph = getGraphToEdit(graphId);
        try {
            boolean edited = graph.deleteConcept(id);
            commit(graphId);
            return edited;
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
     }

    public boolean deleteConceptClass(Long graphId, String id)
            throws GraphNotFoundException, MetaDataNotFoundException,
            ConceptClassNotFoundException, ReadOnlyException, CaughtException {
        ONDEXGraph graph = getGraphToEdit(graphId);
        ONDEXGraphMetaData metaData = getMetaData(graph);
        try {
            boolean deleted = metaData.deleteConceptClass(id);
            commit(graphId);
            return deleted;
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
    }

    public boolean deleteConceptAttribute (Long graphId, Integer conceptId,
                                    String attributeNameId)
            throws GraphNotFoundException, AttributeNameNotFoundException,
            MetaDataNotFoundException, ReadOnlyException,
            ConceptNotFoundException, AttributeNotFoundException,
            CaughtException {
        ONDEXGraph graph = getGraphToEdit(graphId);
        ONDEXConcept concept = getConcept(graph, conceptId);
        AttributeName attributeName = getAttributeName(graph, attributeNameId);
        try {
            boolean deleted = concept.deleteAttribute(attributeName);
            commit(graphId);
            logger.info("deleted ="+deleted);
            concept = getConceptOrNull(graph, conceptId);
            logger.info(concept);
            return deleted;
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
    }

    public boolean deleteConceptName(Long graphId, Integer conceptId, String name)
            throws GraphNotFoundException, ReadOnlyException,
            ConceptNotFoundException, ConceptNameNotFoundException,
            CaughtException {
        ONDEXGraph graph = getGraphToEdit(graphId);
        ONDEXConcept concept = getConcept(graph, conceptId);
        try {
            boolean deleted = concept.deleteConceptName(name);
            commit(graphId);
            return deleted;
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
    }

    public boolean deleteConceptAccession(Long graphId, Integer conceptId,
                                          String accession, String elementOfDataSourceId)
            throws GraphNotFoundException, MetaDataNotFoundException,
            DataSourceNotFoundException, ReadOnlyException,
            ConceptNotFoundException, ConceptAccessionNotFoundException,
            CaughtException {
        ONDEXGraph graph = getGraphToEdit(graphId);
        DataSource dataSource = getDataSource(graph, elementOfDataSourceId);
        ONDEXConcept concept = getConcept(graph, conceptId);
        try {
            boolean deleted = concept.deleteConceptAccession(accession, dataSource);
            commit(graphId);
            return deleted;
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
    }

    public boolean deleteDataSource(Long graphId, String id)
            throws GraphNotFoundException, DataSourceNotFoundException,
            MetaDataNotFoundException,
            ReadOnlyException, CaughtException {
        ONDEXGraph graph = getGraphToEdit(graphId);
        ONDEXGraphMetaData metaData = getMetaData(graph);
        try {
            boolean deleted = metaData.deleteDataSource(id);
            commit(graphId);
            return deleted;
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
    }

    public boolean deleteEvidenceType(Long graphId, String id)
            throws GraphNotFoundException, EvidenceTypeNotFoundException,
            MetaDataNotFoundException,
            ReadOnlyException, CaughtException {
        ONDEXGraph graph = getGraphToEdit(graphId);
        ONDEXGraphMetaData metaData = getMetaData(graph);
        try {
            boolean deleted = metaData.deleteEvidenceType(id);
            commit(graphId);
            return deleted;
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
    }

    public String deleteGraph(Long graphId)
            throws GraphNotFoundException, ReadOnlyException, CaughtException {
        ONDEXGraph graph = getGraphToEdit(graphId);
        String name = graph.getName();
        try {
            logger.info("remove graph called on graph "+graphId);
            BerkeleyEnv env = databases.get(graphId);
            if (env != null){
                env.cleanup();
                env = null;
            }
            databases.remove(graphId);
            name = GRAPH_DIR_PREFIX + GRAPH_DIR_PRE_ID + graphId +
                    GRAPH_DIR_POST_ID + name;
            File file = new File(dbPath, name);
            logger.info("deleting graph from:"+file);
            logger.info(file.exists());
            DirUtils.deleteTree(file);
            logger.info(file.exists());
            ONDEXGraphRegistry.graphs.remove(graphId);
            Set<Long> longset = ONDEXGraphRegistry.graphs.keySet();
            logger.info(longset.toArray());
            return ("Successfully deleted graph " + graphId);
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
    }

    public boolean deleteRelation(Long graphId, Integer id)
            throws GraphNotFoundException, ReadOnlyException,
            RelationNotFoundException, CaughtException {
        ONDEXGraph graph = getGraphToEdit(graphId);
        try {
            boolean deleted = graph.deleteRelation(id);
            commit(graphId);
            return deleted;
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
    }

    public boolean deleteRelationAttribute (Long graphId, Integer relationId,
                                     String attributeNameId)
            throws GraphNotFoundException, AttributeNameNotFoundException,
            MetaDataNotFoundException, ReadOnlyException,
            RelationNotFoundException, CaughtException {
        ONDEXGraph graph = getGraphToEdit(graphId);
        ONDEXRelation relation = getRelation(graph, relationId);
        AttributeName attributeName = getAttributeName(graph, attributeNameId);
        try {
            boolean deleted = relation.deleteAttribute(attributeName);
            logger.info("deleted");
            commit(graphId);
            return deleted;
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
    }

    public boolean deleteRelationOfType(Long graphId,
                                        Integer fromConceptId, Integer toConceptId,
                                        String relationTypeId)
            throws GraphNotFoundException, MetaDataNotFoundException,
            RelationTypeNotFoundException, ReadOnlyException,
            ConceptNotFoundException, RelationNotFoundException, CaughtException {
        ONDEXGraph graph = getGraphToEdit(graphId);
        ONDEXConcept from = getConcept(graph, fromConceptId);
        ONDEXConcept to = getConcept(graph, toConceptId);
        RelationType relationType = getRelationType(graph, relationTypeId);
        try {
            boolean deleted = graph.deleteRelation(from, to, relationType);
            commit(graphId);
            return deleted;
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
    }

    public boolean deleteRelationType(Long graphId, String id)
            throws GraphNotFoundException, RelationTypeNotFoundException, MetaDataNotFoundException,
            ReadOnlyException, CaughtException {
        ONDEXGraph graph = getGraphToEdit(graphId);
        ONDEXGraphMetaData metaData = getMetaData(graph);
        try {
            boolean deleted = metaData.deleteRelationType(id);
            commit(graphId);
            return deleted;
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }    }

    public boolean deleteUnit(Long graphId, String id)
            throws GraphNotFoundException, MetaDataNotFoundException,
            UnitNotFoundException, ReadOnlyException, CaughtException {
        ONDEXGraph graph = getGraphToEdit(graphId);
        ONDEXGraphMetaData metaData = getMetaData(graph);
        try {
            boolean deleted = metaData.deleteUnit(id);
            commit(graphId);
            return deleted;
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
    }

    public String getAnnotation(Long graphId, Integer id)
            throws GraphNotFoundException, ConceptNotFoundException, CaughtException {
        ONDEXGraph graph = getGraphFromRegistry(graphId);
        ONDEXConcept concept = getConcept(graph, id);
        try {
            return concept.getAnnotation();
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
    }

    public AttributeName getAttributeName(Long graphId, String id)
            throws GraphNotFoundException, AttributeNameNotFoundException,
            MetaDataNotFoundException, CaughtException {
        ONDEXGraph graph = getGraphFromRegistry(graphId);
        ONDEXGraphMetaData metaData = getMetaData(graph);
        try {
            return metaData.getAttributeName(id);
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
    }

    public List<WSAttributeName> getAttributeNames(Long graphId)
            throws GraphNotFoundException, MetaDataNotFoundException, NullViewException, CaughtException {
        ONDEXGraph graph = getGraphFromRegistry(graphId);
        ONDEXGraphMetaData metaData = getMetaData(graph);
        List<WSAttributeName> list = new ArrayList<WSAttributeName>();
        Set<AttributeName> attributeNames;
        try {
            attributeNames = metaData.getAttributeNames();
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
        if (attributeNames == null) {
            throw new NullViewException("Unexpected null return from " +
                    "metaData.attributeNames from graph " + graph.getName(), logger);
        }
        try {
            for (AttributeName an : attributeNames) {
                list.add(new WSAttributeName(an));
            }
            return list;
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
    }

    public ONDEXConcept getConcept(Long graphId, Integer id)
            throws GraphNotFoundException, ConceptNotFoundException, CaughtException {
        ONDEXGraph graph = getGraphFromRegistry(graphId);
        try {
            return graph.getConcept(id);
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
    }

    private ONDEXConcept getConcept(ONDEXGraph graph, Integer id)
            throws ConceptNotFoundException, CaughtException {
        ONDEXConcept concept;
        try {
            concept = graph.getConcept(id);
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
        if (concept == null) {
            throw new ConceptNotFoundException(
                    "Unable to find concept " + id + " in graph "
                            + graph.getName(), logger);
        }
        return concept;
    }

    private ONDEXConcept getConceptOrNull(ONDEXGraph graph, Integer id)
            throws ConceptNotFoundException, CaughtException {
        if (id == null) {
            return null;
        }
        if (id.equals("")) {
            return null;
        }
        try {
            return getConcept(graph, id);
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
    }

    public ConceptClass getConceptClass(Long graphId, String id)
            throws GraphNotFoundException, MetaDataNotFoundException,
            ConceptClassNotFoundException, CaughtException {
        ONDEXGraph graph = getGraphFromRegistry(graphId);
        try {
            return getConceptClass(graph, id);
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
    }

    public List<WSConceptClass> getConceptClasses(Long graphId)
            throws GraphNotFoundException, MetaDataNotFoundException,
            NullViewException, CaughtException {
        ONDEXGraph graph = getGraphFromRegistry(graphId);
        ONDEXGraphMetaData metaData = getMetaData(graph);
        List<WSConceptClass> list = new ArrayList<WSConceptClass>();
        Set<ConceptClass> conceptClasses;
        try {
            conceptClasses = metaData.getConceptClasses();
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
        if (conceptClasses == null) {
            throw new NullViewException("Unexpected null return from " +
                    "metaData.conceptClasses from graph " + graph.getName(), logger);
        }
        try{
            for (ConceptClass cc : conceptClasses) {
                list.add(new WSConceptClass(cc));
            }
            return list;
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
    }

     public int getNumberOfConceptClasses(Long graphId)
            throws GraphNotFoundException, MetaDataNotFoundException,
            NullViewException, CaughtException {
        ONDEXGraph graph = getGraphFromRegistry(graphId);
        ONDEXGraphMetaData metaData = getMetaData(graph);
        Set<ConceptClass> conceptClasses;
        try {
            conceptClasses = metaData.getConceptClasses();
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
        if (conceptClasses == null) {
            throw new NullViewException("Unexpected null return from " +
                    "metaData.conceptClasses from graph " + graph.getName(), logger);
        }
        try{
            return conceptClasses.size();
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
    }

    public Attribute getConceptAttribute (Long graphId, Integer conceptId,
                      String attributeNameId)
            throws GraphNotFoundException, AttributeNameNotFoundException,
            MetaDataNotFoundException,
            ConceptNotFoundException, CaughtException {
        ONDEXGraph graph = getGraphFromRegistry(graphId);
        ONDEXConcept concept = getConcept(graph, conceptId);
        AttributeName attributeName = getAttributeName(graph, attributeNameId);
        try {
            return concept.getAttribute(attributeName);
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
    }

    public Set<Attribute> getAttributes(Long graphId, Integer conceptId)
            throws GraphNotFoundException, ConceptNotFoundException,
            NullViewException, CaughtException {
        ONDEXGraph graph = getGraphFromRegistry(graphId);
        ONDEXConcept concept = getConcept(graph, conceptId);
        Set<Attribute> conceptAttributes;
        try {
            conceptAttributes = concept.getAttributes();
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
        return conceptAttributes;
    }

    public ConceptName getConceptName(Long graphId, Integer conceptId)
            throws GraphNotFoundException, ConceptNotFoundException, CaughtException {
        ONDEXGraph graph = getGraphFromRegistry(graphId);
        ONDEXConcept concept = getConcept(graph, conceptId);
        try {
            return concept.getConceptName();
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
    }

    public WSConceptName getConceptNameWithName(Long graphId, Integer conceptId,
                                                String name) throws GraphNotFoundException, ConceptNotFoundException, CaughtException {
        ONDEXGraph graph = getGraphFromRegistry(graphId);
        ONDEXConcept concept = getConcept(graph, conceptId);
        try {
            return new WSConceptName(concept.getConceptName(name));
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
    }

    public List<WSConceptName> getConceptNames(Long graphId, Integer conceptId)
            throws GraphNotFoundException, ConceptNotFoundException,
            NullViewException, CaughtException {
        ONDEXGraph graph = getGraphFromRegistry(graphId);
        ONDEXConcept concept = getConcept(graph, conceptId);
        List<WSConceptName> list = new ArrayList<WSConceptName>();
        Set<ConceptName> conceptNames;
        try {
            conceptNames = concept.getConceptNames();
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
        if (conceptNames == null) {
            throw new NullViewException("Unexpected null return from " +
                    "concept.getConceptNames() from concept " + conceptId +
                    " from graph " + graph.getName(), logger);
        }
        try {
            for (ConceptName cn : conceptNames) {
                list.add(new WSConceptName(cn));
            }
            return list;
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
    }

    public ConceptAccession getConceptAccession(Long graphId,
                                                Integer conceptId, String accession, String elementOfDataSourceId)
            throws GraphNotFoundException, MetaDataNotFoundException,
            DataSourceNotFoundException,
            ConceptNotFoundException, CaughtException {
        ONDEXGraph graph = getGraphFromRegistry(graphId);
        DataSource dataSource = getDataSource(graph, elementOfDataSourceId);
        ONDEXConcept concept = getConcept(graph, conceptId);
        try {
            return concept.getConceptAccession(accession, dataSource);
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
    }

    public List<WSConceptAccession> getConceptAccessions(Long graphId,
                                                         Integer conceptId) throws GraphNotFoundException, ConceptNotFoundException, NullViewException, CaughtException {
        ONDEXGraph graph = getGraphFromRegistry(graphId);
        ONDEXConcept concept = getConcept(graph, conceptId);
        List<WSConceptAccession> list = new ArrayList<WSConceptAccession>();
        Set<ConceptAccession> conceptAccessions;
        try {
            conceptAccessions = concept.getConceptAccessions();
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
        if (conceptAccessions == null) {
            throw new NullViewException("Unexpected null return from " +
                    "concept.getConceptAccessions() from concept " + conceptId +
                    " from graph " + graph.getName(), logger);
        }
        try {
            for (ConceptAccession ca : conceptAccessions) {
                list.add(new WSConceptAccession(ca));
            }
            return list;
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
    }

    public Set<ONDEXConcept> getConcepts(Long graphId)
            throws GraphNotFoundException, NullViewException, CaughtException {
        ONDEXGraph graph = getGraphFromRegistry(graphId);
        logger.info("Retrieved graph " + graphId);
        Set<ONDEXConcept> concepts = null;
        try {
            concepts = graph.getConcepts();
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
        if (concepts == null) {
            throw new NullViewException("Unexpected null return from " +
                    "graph.getConcepts() from graph " + graph.getName(), logger);
        }
        return concepts;
    }

    public int getNumberOfConcepts(Long graphId)
            throws GraphNotFoundException, NullViewException, CaughtException {
        ONDEXGraph graph = getGraphFromRegistry(graphId);
        logger.info("Retrieved graph " + graphId);
        Set<ONDEXConcept> concepts = null;
        try {
            concepts = graph.getConcepts();
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
        if (concepts == null) {
            throw new NullViewException("Unexpected null return from " +
                    "graph.getConcepts() from graph " + graph.getName(), logger);
        }
        return concepts.size();
    }

    public Set<ONDEXConcept> getConceptsOfAttributeName(Long graphId,
                                                              String attributeNameId)
            throws GraphNotFoundException, AttributeNameNotFoundException,
            MetaDataNotFoundException, NullViewException, CaughtException {
        ONDEXGraph graph = getGraphFromRegistry(graphId);
        AttributeName attributeName = getAttributeName(graph, attributeNameId);
        Set<ONDEXConcept> concepts;
        try {
            concepts = graph.getConceptsOfAttributeName(attributeName);
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
        if (concepts == null) {
            throw new NullViewException("Unexpected null return from " +
                    "getConceptsOfAttributeName(attributeName)" +
                    " with attributNameID " + attributeNameId +
                    "from graph " + graph.getName(), logger);
        }
        return concepts;
    }

    public Set<ONDEXConcept> getConceptsOfConceptClass(Long graphId,
                                                             String conceptClassId)
            throws GraphNotFoundException, MetaDataNotFoundException,
            ConceptClassNotFoundException, NullViewException, CaughtException {
        ONDEXGraph graph = getGraphFromRegistry(graphId);
        ConceptClass conceptClass = getConceptClass(graph, conceptClassId);
        Set<ONDEXConcept> concepts;
        try {
            concepts = graph.getConceptsOfConceptClass(conceptClass);
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
        if (concepts == null) {
            throw new NullViewException("Unexpected null return from " +
                    "getConceptsOfConceptClass(conceptClass)" +
                    " with conceptClassId " + conceptClassId +
                    "from graph " + graph.getName(), logger);
        }
        return concepts;
    }

    public Set<ONDEXConcept> getConceptsOfTag(Long graphId,
                                                    Integer tagConceptId)
            throws GraphNotFoundException, ConceptNotFoundException,
            NullViewException, CaughtException {
        ONDEXGraph graph = getGraphFromRegistry(graphId);
        ONDEXConcept concept = getConcept(graph, tagConceptId);
        Set<ONDEXConcept> concepts;
        try {
            concepts = graph.getConceptsOfTag(concept);
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
        if (concepts == null) {
            throw new NullViewException("Unexpected null return from " +
                    "getConceptsOfTag(concept)" +
                    " with tagConceptId " + tagConceptId +
                    "from graph " + graph.getName(), logger);
        }
        return concepts;
    }

    public Set<ONDEXConcept> getConceptsOfDataSource(Long graphId, String dataSourceId)
            throws GraphNotFoundException, MetaDataNotFoundException,
            DataSourceNotFoundException,
            NullViewException, CaughtException {
        ONDEXGraph graph = getGraphFromRegistry(graphId);
        DataSource dataSource = getDataSource(graph, dataSourceId);
        Set<ONDEXConcept> concepts;
        try {
            concepts = graph.getConceptsOfDataSource(dataSource);
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
        if (concepts == null) {
            throw new NullViewException("Unexpected null return from " +
                    "getConceptsOfDataSource(dataSource)" +
                    " with dataSourceId " + dataSourceId +
                    "from graph " + graph.getName(), logger);
        }
        return concepts;
    }

    public Set<ONDEXConcept> getConceptsOfEvidenceType(Long graphId,
                                                             String evidenceTypeId)
            throws GraphNotFoundException, MetaDataNotFoundException,
            EvidenceTypeNotFoundException, NullViewException, CaughtException {
        ONDEXGraph graph = getGraphFromRegistry(graphId);
        EvidenceType evidenceType = getEvidenceType(graph, evidenceTypeId);
        Set<ONDEXConcept> concepts;
        try {
            concepts = graph.getConceptsOfEvidenceType(evidenceType);
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
        if (concepts == null) {
            throw new NullViewException("Unexpected null return from " +
                    "getConceptsOfEvidenceType(evidenceType)" +
                    " with evidenceTypeId " + evidenceTypeId +
                    "from graph " + graph.getName(), logger);
        }
        return concepts;
    }

    public Set<ONDEXConcept> getTagsConcept(Long graphId, Integer conceptId)
            throws GraphNotFoundException, ConceptNotFoundException,
            NullViewException, CaughtException {
        ONDEXGraph graph = getGraphFromRegistry(graphId);
        ONDEXConcept concept = getConcept(graph, conceptId);
        Set<ONDEXConcept> concepts;
        try {
            concepts = concept.getTags();
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
        if (concepts == null) {
            throw new NullViewException("Unexpected null return from " +
                    "concept.getTag()" +
                    " from concept " + conceptId +
                    "from graph " + graph.getName(), logger);
        }
        return concepts;
    }

    public Set<ONDEXConcept> getTagsGraph(Long graphId)
            throws GraphNotFoundException, NullViewException, CaughtException {
        ONDEXGraph graph = getGraphFromRegistry(graphId);
        Set<ONDEXConcept> concepts;
        try {
            concepts = graph.getAllTags();
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
        if (concepts == null) {
            throw new NullViewException("Unexpected null return from graph.getTag() " +
                    "from graph " + graph.getName(), logger);
        }
        return concepts;
    }

    public Set<ONDEXConcept> getTagsRelation(Long graphId, Integer relationId)
            throws GraphNotFoundException, RelationNotFoundException,
            NullViewException, CaughtException {
        ONDEXGraph graph = getGraphFromRegistry(graphId);
        ONDEXRelation relation = getRelation(graph, relationId);
        Set<ONDEXConcept> concepts;
        try {
            concepts = relation.getTags();
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
        if (concepts == null) {
            throw new NullViewException("Unexpected null return from " +
                    "relation.getTags()" +
                    " from relation " + relationId +
                    "from graph " + graph.getName(), logger);
        }
        return concepts;
    }

    public DataSource getDataSource(Long graphId, String id)
            throws GraphNotFoundException, MetaDataNotFoundException,
            DataSourceNotFoundException, CaughtException {
        ONDEXGraph graph = getGraphFromRegistry(graphId);
        ONDEXGraphMetaData metaData = getMetaData(graph);
        try {
            return metaData.getDataSource(id);
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
    }

    public List<WSDataSource> getDataSources(Long graphId)
            throws GraphNotFoundException, MetaDataNotFoundException,
            NullViewException, CaughtException {
        ONDEXGraph graph = getGraphFromRegistry(graphId);
        logger.info("Got graph " + graphId);
        ONDEXGraphMetaData metaData = getMetaData(graph);
        List<WSDataSource> list = new ArrayList<WSDataSource>();
        Set<DataSource> DataSources;
        try {
            DataSources = metaData.getDataSources();
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
        if (DataSources == null) {
            throw new NullViewException(
                    "Unexpected null return from metaData.getDataSources() " +
                            "from graph " + graph.getName(), logger);
        }
        try {
            logger.info("Got list of DataSources for graph " + graphId);
            for (DataSource dataSource : DataSources) {
                list.add(new WSDataSource(dataSource));
            }
            return list;
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
    }

    public int getNumberOfDataSources(Long graphId)
            throws GraphNotFoundException, MetaDataNotFoundException,
            NullViewException, CaughtException {
        ONDEXGraph graph = getGraphFromRegistry(graphId);
        logger.info("Got graph " + graphId);
        ONDEXGraphMetaData metaData = getMetaData(graph);
        Set<DataSource> DataSources;
        try {
            DataSources = metaData.getDataSources();
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
        if (DataSources == null) {
            throw new NullViewException(
                    "Unexpected null return from metaData.getDataSources() " +
                            "from graph " + graph.getName(), logger);
        }
        try {
            //ogger.info("Got list of DataSources for graph " + graphId);
            return DataSources.size();
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
    }

    public String getDescription(Long graphId, Integer conceptId)
            throws GraphNotFoundException, ConceptNotFoundException, CaughtException {
        ONDEXGraph graph = getGraphFromRegistry(graphId);
        try {
            ONDEXConcept concept = getConcept(graph, conceptId);
            return concept.getDescription();
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
    }

    public WSDataSource getElementOf(Long graphId, Integer conceptId)
            throws GraphNotFoundException, ConceptNotFoundException,
            DataSourceNotFoundException, CaughtException {
        ONDEXGraph graph = getGraphFromRegistry(graphId);
        ONDEXConcept concept = getConcept(graph, conceptId);
        DataSource dataSource;
        try {
            dataSource = concept.getElementOf();
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
        if (dataSource == null) {
            throw new DataSourceNotFoundException(
                    "Null return for concept.getElementOf() " +
                            "with concet " + conceptId + " from graph " + graph.getName(), logger);
        }
        return new WSDataSource(dataSource);
    }

    public List<WSEvidenceType> getEvidenceConcept(Long graphId,
                                                   Integer conceptId)
            throws GraphNotFoundException, MetaDataNotFoundException,
            ConceptNotFoundException, NullViewException, CaughtException {
        ONDEXGraph graph = getGraphFromRegistry(graphId);
        ONDEXConcept concept = getConcept(graph, conceptId);
        List<WSEvidenceType> list = new ArrayList<WSEvidenceType>();
        Set<EvidenceType> evidenceTypes;
        try {
            evidenceTypes = concept.getEvidence();
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
        if (evidenceTypes == null) {
            throw new NullViewException(
                    "Unexpected null return from concept.getEvidence() " +
                            " from conceptId " + conceptId +
                            " from graph " + graph.getName(), logger);
        }
        try {
            for (EvidenceType et : evidenceTypes) {
                list.add(new WSEvidenceType(et));
            }
            return list;
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
    }

    public List<WSEvidenceType> getEvidenceRelation(Long graphId,
                                                    Integer relationId)
            throws GraphNotFoundException, MetaDataNotFoundException,
            RelationNotFoundException, NullViewException, CaughtException {
        ONDEXGraph graph = getGraphFromRegistry(graphId);
        ONDEXRelation relation = getRelation(graph, relationId);
        List<WSEvidenceType> list = new ArrayList<WSEvidenceType>();
        Set<EvidenceType> evidenceTypes;
        try {
            evidenceTypes = relation.getEvidence();
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
        if (evidenceTypes == null) {
            throw new NullViewException(
                    "Unexpected null return from relation.getEvidence() " +
                            " from relationId " + relationId +
                            " from graph " + graph.getName(), logger);
        }
        try {
            for (EvidenceType et : evidenceTypes) {
                list.add(new WSEvidenceType(et));
            }
            return list;
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
    }

    public EvidenceType getEvidenceType(Long graphId, String id)
            throws GraphNotFoundException, MetaDataNotFoundException,
            EvidenceTypeNotFoundException, CaughtException {
        ONDEXGraph graph = getGraphFromRegistry(graphId);
        try {
            return getEvidenceType(graph, id);
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
    }

    public List<WSEvidenceType> getEvidenceTypes(Long graphId)
            throws GraphNotFoundException, MetaDataNotFoundException,
            NullViewException, NullInViewException, CaughtException {
        logger.info("getEvidenceTypes called with " + graphId);
        ONDEXGraph graph = getGraphFromRegistry(graphId);
        ONDEXGraphMetaData metaData = getMetaData(graph);
        logger.info("found metaData ");
        Set<EvidenceType> evidenceTypes;
        try {
            evidenceTypes = metaData.getEvidenceTypes();
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
        logger.info("found evidence types");
        if (evidenceTypes == null) {
            throw new NullViewException(
                    "Unexpected null return from metaData..getEvidenceTypes() " +
                            "from graph " + graph.getName(), logger);
        }
        return toList(evidenceTypes);
    }

    public int getNumberOfEvidenceTypes(Long graphId)
            throws GraphNotFoundException, MetaDataNotFoundException,
            NullViewException, NullInViewException, CaughtException {
        logger.info("getEvidenceTypes called with " + graphId);
        ONDEXGraph graph = getGraphFromRegistry(graphId);
        ONDEXGraphMetaData metaData = getMetaData(graph);
        logger.info("found metaData ");
        Set<EvidenceType> evidenceTypes;
        try {
            evidenceTypes = metaData.getEvidenceTypes();
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
        logger.info("found evidence types");
        if (evidenceTypes == null) {
            throw new NullViewException(
                    "Unexpected null return from metaData..getEvidenceTypes() " +
                            "from graph " + graph.getName(), logger);
        }
        return evidenceTypes.size();
    }

    public WSConcept getFromConcept(Long graphId, Integer relationId)
            throws ConceptNotFoundException, GraphNotFoundException,
            RelationNotFoundException, CaughtException {
        ONDEXGraph graph = getGraphFromRegistry(graphId);
        ONDEXRelation relation = getRelation(graph, relationId);
        ONDEXConcept concept;
        try {
            concept = relation.getFromConcept();
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
        if (concept == null) {
            throw new ConceptNotFoundException(
                    "Unable to find from concept " +
                            "from relation " + relationId +
                            " in graph " + graph.getName(), logger);
        }
        return new WSConcept(concept);
    }

    public WSGraph getGraph(Long graphId) throws GraphNotFoundException {
        ONDEXGraph graph = getGraphFromRegistry(graphId);
        return new WSGraph(graph);
    }

    public WSGraphMetaData getGraphMetaData(long graphid)
            throws GraphNotFoundException, MetaDataNotFoundException, CaughtException{
        ONDEXGraph graph = getGraphFromRegistry(graphid);
        WSGraphMetaData wsGraphMetaData = new WSGraphMetaData();
        ONDEXGraphMetaData metaData = getMetaData(graph);

        try{
            Set<ConceptClass> conceptClasses = metaData.getConceptClasses();
            for (ConceptClass conceptClass: conceptClasses){
                Set<ONDEXConcept> concepts = graph.getConceptsOfConceptClass(conceptClass);
                int count = concepts.size();
                wsGraphMetaData.addConceptClass(conceptClass, count);
            }

            Set<DataSource> dataSources = metaData.getDataSources();
            for (DataSource dataSource: dataSources){
                Set<ONDEXConcept> concepts = graph.getConceptsOfDataSource(dataSource);
                int count = concepts.size();
                wsGraphMetaData.addDataSource(dataSource, count);
            }

            Set<RelationType> relationTypes = metaData.getRelationTypes();
            for (RelationType relationType: relationTypes){
                Set<ONDEXRelation> relations = graph.getRelationsOfRelationType(relationType);
                int count = relations.size();
                wsGraphMetaData.addRelationType(relationType, count);
            }

            Set<EvidenceType> evidenceTypes = metaData.getEvidenceTypes();
            for (EvidenceType evidenceType: evidenceTypes){
                Set<ONDEXRelation> relations = graph.getRelationsOfEvidenceType(evidenceType);
                int count = relations.size();
                wsGraphMetaData.addEvidenceType(evidenceType, count);
            }

            return wsGraphMetaData;
        } catch (Exception e) {
            throw new CaughtException("Trying to get all metaData",e, logger);
        }

    }

    private ONDEXGraph getGraphFromRegistry(Long graphId)
            throws GraphNotFoundException {
        ONDEXGraph graph = ONDEXGraphRegistry.graphs.get(graphId);

        if (graph instanceof StubGraph) {
            File dir = new File(((StubGraph) graph).getDirectory());
            logger.info("Converting stub graph to berley graph at:" + dir.getAbsolutePath());
            try {
                loadBerley(dir);
            } catch (ParsingException ex) {
                new GraphNotFoundException("Unable to find graph with id: " + graphId, logger);
            } catch (ReadOnlyException ex) {
                new GraphNotFoundException("Unable to find graph with id: " + graphId, logger);
            }
            graph = ONDEXGraphRegistry.graphs.get(graphId);
        }
        if (graph == null) {
            GraphNotFoundException graphNotFoundException =
                    new GraphNotFoundException("Unable to find graph with id: " + graphId, logger);
            logger.error("Avaiable graphs are: ");
            Set<Long> longset = ONDEXGraphRegistry.graphs.keySet();
            for (Long key : longset) {
                logger.error(key.longValue() + " : " + ONDEXGraphRegistry.graphs.get(key));
            }
            throw graphNotFoundException;
        }
        return graph;
    }

    public ONDEXGraph getGraphToEdit(Long graphId)
            throws GraphNotFoundException, ReadOnlyException {
        ONDEXGraph graph = getGraphFromRegistry(graphId);
        if (graph.isReadOnly()) {
            ReadOnlyException readOnlyException =
                    new ReadOnlyException("Unable to change graph with id: "
                            + graphId + " it is readOnly.", logger);
            throw readOnlyException;
        }
        return graph;
    }

    public WSGraph getGraphOfName(String name) throws GraphNotFoundException, CaughtException {
        WSGraph result = getGraphOfNameOrNull(name);
        if (result != null)
            return result;
        throw new GraphNotFoundException("No graph with name " + name + " found", logger);
    }

    private WSGraph getGraphOfNameOrNull(String name) 
            throws CaughtException, GraphNotFoundException {
        if (name == null) {
            throw new GraphNotFoundException("Illegal Attempt to get graph with null name ", logger);
        }
        if (name.isEmpty()) {
            throw new GraphNotFoundException("Illegal Attempt to get graph with empty name ", logger);
        }
        try {
            Set<Long> longset = ONDEXGraphRegistry.graphs.keySet();
            for (Long key : longset) {
                ONDEXGraph graph = ONDEXGraphRegistry.graphs.get(key);
                if (name.equalsIgnoreCase(graph.getName())) {
                    return new WSGraph(graph);
                }
            }
            return null;
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
    }

    public List<WSGraph> getGraphs() throws CaughtException {
        try {
            List<WSGraph> list = new ArrayList<WSGraph>();
            Set<Long> longset = ONDEXGraphRegistry.graphs.keySet();
            for (Long key : longset) {
                ONDEXGraph graph = ONDEXGraphRegistry.graphs.get(key);
                list.add(new WSGraph(graph));
            }
            return list;
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
    }

     public Date getGraphLastModified(Long graphId)
            throws GraphNotFoundException {
        ONDEXGraph graph = ONDEXGraphRegistry.graphs.get(graphId);

        if (graph instanceof StubGraph) {
            File dir = new File(((StubGraph) graph).getDirectory());
            return new Date(dir.lastModified());
        }
        if (graph instanceof MemoryONDEXGraph){
            return new Date();
        }
        if (graph == null) {
            GraphNotFoundException graphNotFoundException =
                    new GraphNotFoundException("Unable to find graph with id: " + graphId, logger);
            logger.error("Avaiable graphs are: ");
            Set<Long> longset = ONDEXGraphRegistry.graphs.keySet();
            for (Long key : longset) {
                logger.error(key.longValue() + " : " + ONDEXGraphRegistry.graphs.get(key));
            }
            throw graphNotFoundException;
        }
        return getGraphLastModifiedByScanning(graphId);
    }

    private Date getGraphLastModifiedByScanning(Long graphId) throws GraphNotFoundException{
         if (dbPath.exists() && dbPath.listFiles().length > 0) {
            for (File file : dbPath.listFiles()) {
                if (file.isDirectory()) {
                    String dirName = file.getName();
                    if (dirName.startsWith(GRAPH_DIR_PREFIX)) {
                        String temp = dirName.substring(dirName.indexOf(GRAPH_DIR_PRE_ID) + 1,
                                dirName.indexOf(GRAPH_DIR_POST_ID));
                        long fileGraphId = Long.parseLong(temp);
                        if (fileGraphId == graphId){
                            return new Date(file.lastModified());
                        }
                    }
                }
             }
        }
        throw new GraphNotFoundException("Unable to find directory for graph with id: " + graphId, logger);
    }

    public String[] getGraphsLastModified() {
        ArrayList<String> datesAndGraphIds = new ArrayList<String>();
        for (File file : dbPath.listFiles()) {
            if (file.isDirectory()) {
                String dirName = file.getName();
                if (dirName.startsWith(GRAPH_DIR_PREFIX)) {
                    String temp = dirName.substring(dirName.indexOf(GRAPH_DIR_PRE_ID) + 1,
                            dirName.indexOf(GRAPH_DIR_POST_ID));
                    long fileGraphId = Long.parseLong(temp);
                    String dateAndGraphId = fileGraphId + ":  " + new Date(file.lastModified());
                    datesAndGraphIds.add(dateAndGraphId);
                }
             }
        }
        return datesAndGraphIds.toArray(new String[1]);
    }

    public WSRelationKey getKey(Long graphId, Integer relationId)
            throws ConceptNotFoundException, GraphNotFoundException,
            RelationNotFoundException, KeyNotFoundException, CaughtException {
        ONDEXGraph graph = getGraphFromRegistry(graphId);
        ONDEXRelation relation = getRelation(graph, relationId);
        RelationKey key;
        try {
            key = relation.getKey();
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
        if (key == null) {
            throw new KeyNotFoundException(
                    "Unable to find to key " +
                            "from relation " + relationId +
                            " in graph " + graph.getName(), logger);
        }
        return new WSRelationKey(key);
    }

    public String getName(Long graphId) throws GraphNotFoundException, CaughtException {
        ONDEXGraph graph = getGraphFromRegistry(graphId);
        try {
            return graph.getName();
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
    }

    public WSConceptClass getOfTypeConcept(Long graphId, Integer conceptId)
            throws GraphNotFoundException, ConceptNotFoundException, CaughtException {
        ONDEXGraph graph = getGraphFromRegistry(graphId);
        ONDEXConcept concept = getConcept(graph, conceptId);
        return new WSConceptClass(concept.getOfType());
    }

    public WSRelationType getOfTypeRelation(Long graphId, Integer relationId)
            throws ConceptNotFoundException, GraphNotFoundException,
            RelationNotFoundException, RelationTypeNotFoundException, CaughtException {
        ONDEXGraph graph = getGraphFromRegistry(graphId);
        ONDEXRelation relation = getRelation(graph, relationId);
        RelationType type;
        try {
            type = relation.getOfType();
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
        if (type == null) {
            throw new RelationTypeNotFoundException(
                    "Unable to get type of " +
                            "from relation " + relationId +
                            " in graph " + graph.getName(), logger);
        }
        return new WSRelationType(type);
    }

    public List<String> getOXLFilesAvailable() throws CaughtException {
        List<String> list = new ArrayList<String>();
        String oxlDir =
                Config.properties.getProperty("WebServiceEngine.oxldir");
        if (oxlDir == null) {
            logger.error("No value for WebServiceEngine.oxldir found in config.xml");
            return list;
        }
        File dataDir = new File(oxlDir);
        if (!dataDir.exists()) {
            logger.error("Unable to find WebServiceEngine.oxldirr: " +
                    dataDir.getAbsolutePath() + " found in config.xml");
            return list;
        }
        if (!dataDir.isDirectory()) {
            logger.error("WebServiceEngine.oxldir: " + dataDir.getAbsolutePath() +
                    " found in config.xml is not a directory");
            return list;
        }
        try {
            File[] files = dataDir.listFiles();
            for (File file : files) {
                list.add(file.getName());
            }
            return list;
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
    }

    public String getPID(Long graphId, Integer conceptId)
            throws GraphNotFoundException, ConceptNotFoundException, CaughtException {
        ONDEXGraph graph = getGraphFromRegistry(graphId);
        ONDEXConcept concept = getConcept(graph, conceptId);
        try {
            return concept.getPID();
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
    }

    public ONDEXRelation getRelation(Long graphId, Integer id)
            throws GraphNotFoundException, RelationNotFoundException, CaughtException {
        ONDEXGraph graph = getGraphFromRegistry(graphId);
        return graph.getRelation(id);
    }

    private ONDEXRelation getRelation(ONDEXGraph graph, Integer id)
            throws RelationNotFoundException, CaughtException {
        ONDEXRelation relation;
        try {
            relation = graph.getRelation(id);
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
        if (relation == null) {
            throw new RelationNotFoundException("Unable to find relation: " + id
                    + " in graph: " + graph.getName(), logger);
        }
        return relation;
    }

    public Attribute getRelationAttribute (Long graphId, Integer relationId,
                              String attributeNameId)
            throws GraphNotFoundException, AttributeNameNotFoundException,
            MetaDataNotFoundException,
            RelationNotFoundException, CaughtException {
        ONDEXGraph graph = getGraphFromRegistry(graphId);
        ONDEXRelation relation = getRelation(graph, relationId);
        AttributeName attributeName = getAttributeName(graph, attributeNameId);
        try {
            return relation.getAttribute(attributeName);
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
    }

    public Set<Attribute> getRelationAttributes(Long graphId, Integer relationId)
            throws GraphNotFoundException, RelationNotFoundException,
            NullViewException, CaughtException {
        ONDEXGraph graph = getGraphFromRegistry(graphId);
        ONDEXRelation relation = getRelation(graph, relationId);
        Set<Attribute> relationAttributes;
        try {
            relationAttributes = relation.getAttributes();
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
        if (relationAttributes == null) {
            throw new NullViewException(
                    "Unexpected null return from relation.getAttribute s() " +
                            " on relation " + relationId +
                            "from graph " + graph.getName(), logger);
        }
        return relationAttributes;
    }

    public ONDEXRelation getRelationOfType(Long graphId, Integer fromConceptId,
                                           Integer toConceptId, String relationTypeId)
            throws GraphNotFoundException, MetaDataNotFoundException,
            RelationTypeNotFoundException, ConceptNotFoundException,
            RelationNotFoundException, CaughtException {
        ONDEXGraph graph = getGraphFromRegistry(graphId);
        ONDEXConcept from = getConcept(graph, fromConceptId);
        ONDEXConcept to = getConcept(graph, toConceptId);
        RelationType typeSet = getRelationType(graph, relationTypeId);
        try {
            return graph.getRelation(from, to, typeSet);
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
    }

    public Set<ONDEXRelation> getRelations(Long graphId)
            throws GraphNotFoundException, NullViewException, CaughtException {
        ONDEXGraph graph = getGraphFromRegistry(graphId);
        logger.info("Got Ondex graph " + graphId);
        Set<ONDEXRelation> relations;
        try {
            relations = graph.getRelations();
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
        logger.info("Got relations");
        if (relations == null) {
            throw new NullViewException(
                    "Unexpected null return from graph.getRelations() " +
                            "from graph " + graph.getName(), logger);
        }
        return relations;
    }

     public int getNumberOfRelations(Long graphId)
            throws GraphNotFoundException, NullViewException, CaughtException {
        ONDEXGraph graph = getGraphFromRegistry(graphId);
        logger.info("Got Ondex graph " + graphId);
        Set<ONDEXRelation> relations;
        try {
            relations = graph.getRelations();
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
        logger.info("Got relations");
        if (relations == null) {
            throw new NullViewException(
                    "Unexpected null return from graph.getRelations() " +
                            "from graph " + graph.getName(), logger);
        }
        return relations.size();
    }

    public Set<ONDEXRelation> getRelationsOfAttributeName(Long graphId,
                                                                String attributeNameId)
            throws GraphNotFoundException, AttributeNameNotFoundException,
            MetaDataNotFoundException,
            NullViewException, CaughtException {
        ONDEXGraph graph = getGraphFromRegistry(graphId);
        AttributeName attributeName = getAttributeName(graph, attributeNameId);
        Set<ONDEXRelation> relations;
        try {
            relations = graph.getRelationsOfAttributeName(attributeName);
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
        if (relations == null) {
            throw new NullViewException("Unexpected null return from " +
                    "graph.getRelationsOfAttributeName(attributeName) " +
                    " with attrributeNameId " + attributeNameId +
                    " on graph " + graph.getName(), logger);
        }
        return relations;
    }

    public Set<ONDEXRelation> getRelationsOfConcept(Long graphId,
                                                          Integer conceptId)
            throws GraphNotFoundException, ConceptNotFoundException,
            NullViewException, CaughtException {
        ONDEXGraph graph = getGraphFromRegistry(graphId);
        ONDEXConcept concept = getConcept(graph, conceptId);
        Set<ONDEXRelation> relations;
        try {
            relations = graph.getRelationsOfConcept(concept);
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
        if (relations == null) {
            throw new NullViewException("Unexpected null return from " +
                    "graph.getRelationsOfConcept(concept) " +
                    " with conceptId " + conceptId +
                    " on graph " + graph.getName(), logger);
        }
        return relations;
    }

    public Set<ONDEXRelation> getRelationsOfConceptClass(Long graphId,
                                                               String conceptClassId)
            throws GraphNotFoundException, MetaDataNotFoundException,
            ConceptClassNotFoundException,
            NullViewException, CaughtException {
        ONDEXGraph graph = getGraphFromRegistry(graphId);
        ConceptClass conceptClass = getConceptClass(graph, conceptClassId);
        Set<ONDEXRelation> relations;
        try {
            relations = graph.getRelationsOfConceptClass(conceptClass);
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
        if (relations == null) {
            throw new NullViewException("Unexpected null return from " +
                    "graph.getRelationsOfConceptClass(conceptClass) " +
                    " with conceptClassId " + conceptClassId +
                    " on graph " + graph.getName(), logger);
        }
        return relations;
    }

    public Set<ONDEXRelation> getRelationsOfTag(Long graphId,
                                                      Integer tagConceptId)
            throws GraphNotFoundException, ConceptNotFoundException,
            NullViewException, CaughtException {
        ONDEXGraph graph = getGraphFromRegistry(graphId);
        ONDEXConcept concept = getConcept(graph, tagConceptId);
        Set<ONDEXRelation> relations;
        try {
            relations = graph.getRelationsOfTag(concept);
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
        if (relations == null) {
            throw new NullViewException("Unexpected null return from " +
                    "graph.getRelationsOfTag(concept) " +
                    " with tagConceptId " + tagConceptId +
                    " on graph " + graph.getName(), logger);
        }
        return relations;
    }

    public Set<ONDEXRelation> getRelationsOfDataSource(Long graphId, String dataSourceId)
            throws GraphNotFoundException, MetaDataNotFoundException,
            DataSourceNotFoundException, NullViewException, CaughtException {
        ONDEXGraph graph = getGraphFromRegistry(graphId);
        DataSource dataSource = getDataSource(graph, dataSourceId);
        Set<ONDEXRelation> relations;
        try {
            relations = graph.getRelationsOfDataSource(dataSource);
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
        if (relations == null) {
            throw new NullViewException("Unexpected null return from " +
                    "graph.getRelationsOfDataSource(dataSource) " +
                    " with dataSourceId " + dataSourceId +
                    " on graph " + graph.getName(), logger);
        }
        return relations;
    }

    public Set<ONDEXRelation> getRelationsOfEvidenceType(Long graphId,
                                                               String evidenceTypeId)
            throws GraphNotFoundException, MetaDataNotFoundException,
            EvidenceTypeNotFoundException, NullViewException, CaughtException {
        ONDEXGraph graph = getGraphFromRegistry(graphId);
        EvidenceType evidenceType = getEvidenceType(graph, evidenceTypeId);
        Set<ONDEXRelation> relations;
        try {
            relations = graph.getRelationsOfEvidenceType(evidenceType);
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
        if (relations == null) {
            throw new NullViewException("Unexpected null return from " +
                    "graph.getRelationsOfEvidenceType(evidenceType) " +
                    " with evidenceTypeId " + evidenceTypeId +
                    " on graph " + graph.getName(), logger);
        }
        return relations;
    }

    public Set<ONDEXRelation> getRelationsOfRelationType(Long graphId,
                                                               String relationTypeId)
            throws GraphNotFoundException, MetaDataNotFoundException,
            RelationTypeNotFoundException, NullViewException, CaughtException {
        ONDEXGraph graph = getGraphFromRegistry(graphId);
        RelationType relationType = getRelationType(graph, relationTypeId);
        Set<ONDEXRelation> relations;
        try {
            relations = graph.getRelationsOfRelationType(relationType);
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
        if (relations == null) {
            throw new NullViewException("Unexpected null return from " +
                    "graph.getRelationsOfRelationType(relationType) " +
                    " with relationTypeId " + relationTypeId +
                    " on graph " + graph.getName(), logger);
        }
        return relations;
    }

    public WSRelationType getRelationType(Long graphId, String id)
            throws GraphNotFoundException, MetaDataNotFoundException,
            RelationTypeNotFoundException, CaughtException {
        ONDEXGraph graph = getGraphFromRegistry(graphId);
        return new WSRelationType(getRelationType(graph, id));
    }

    public List<WSRelationType> getRelationTypes(Long graphId)
            throws GraphNotFoundException, MetaDataNotFoundException,
            NullViewException, CaughtException {
        ONDEXGraph graph = getGraphFromRegistry(graphId);
        ONDEXGraphMetaData metaData = getMetaData(graph);
        List<WSRelationType> list = new ArrayList<WSRelationType>();
        Set<RelationType> relationTypes;
        try {
            relationTypes = metaData.getRelationTypes();
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
        if (relationTypes == null) {
            throw new NullViewException("Unexpected null return from " +
                    "metaData.getRelationTypes() " +
                    " on graph " + graph.getName(), logger);
        }
        try {
            for (RelationType rt : relationTypes) {
                list.add(new WSRelationType(rt));
            }
            return list;
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
    }

    public int getNumberOfRelationTypes(Long graphId)
            throws GraphNotFoundException, MetaDataNotFoundException,
            NullViewException, CaughtException {
        ONDEXGraph graph = getGraphFromRegistry(graphId);
        ONDEXGraphMetaData metaData = getMetaData(graph);
        Set<RelationType> relationTypes;
        try {
            relationTypes = metaData.getRelationTypes();
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
        if (relationTypes == null) {
            throw new NullViewException("Unexpected null return from " +
                    "metaData.getRelationTypes() " +
                    " on graph " + graph.getName(), logger);
        }
        try {
            return relationTypes.size();
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
    }

    public long getSIDConcept(Long graphId, Integer conceptId)
            throws GraphNotFoundException, ConceptNotFoundException, CaughtException {
        ONDEXGraph graph = getGraphFromRegistry(graphId);
        ONDEXConcept concept = getConcept(graph, conceptId);
        try {
            return concept.getSID();
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
    }

    public long getSIDGraph(Long graphId) throws GraphNotFoundException {
        ONDEXGraph graph = getGraphFromRegistry(graphId);
        return graph.getSID();
    }

    public long getSIDRelation(Long graphId, Integer relationId)
            throws GraphNotFoundException, RelationNotFoundException, CaughtException {
        ONDEXGraph graph = getGraphFromRegistry(graphId);
        ONDEXRelation relation = getRelation(graph, relationId);
        try {
            return relation.getSID();
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
    }

    public WSConcept getToConcept(Long graphId, Integer relationId)
            throws ConceptNotFoundException, GraphNotFoundException,
            RelationNotFoundException, CaughtException {
        ONDEXGraph graph = getGraphFromRegistry(graphId);
        ONDEXRelation relation = getRelation(graph, relationId);
        ONDEXConcept concept;
        try {
            concept = relation.getToConcept();
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
        if (concept == null) {
            throw new ConceptNotFoundException(
                    "Unable to find to concept " +
                            "from relation " + relationId +
                            " in graph " + graph.getName(), logger);
        }
        return new WSConcept(concept);
    }

    public Unit getUnit(Long graphId, String id)
            throws GraphNotFoundException, MetaDataNotFoundException,
            UnitNotFoundException, CaughtException {
        ONDEXGraph graph = getGraphFromRegistry(graphId);
        return getUnit(graph, id);
    }

    public List<WSUnit> getUnits(Long graphId)
            throws GraphNotFoundException, MetaDataNotFoundException,
            NullViewException, CaughtException {
        ONDEXGraph graph = getGraphFromRegistry(graphId);
        ONDEXGraphMetaData metaData = getMetaData(graph);
        List<WSUnit> list = new ArrayList<WSUnit>();
        Set<Unit> units;
        try {
            units = metaData.getUnits();
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
        if (units == null) {
            throw new NullViewException("Unexpected null return from " +
                    "metaData.getUnits() " +
                    " on graph " + graph.getName(), logger);
        }
        try {
            for (Unit u : units) {
                list.add(new WSUnit(u));
            }
            return list;
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
    }

    private void handleFileNotFound(String url) {
        String small = url;
        while (small.lastIndexOf('.') > 0) {
            small = small.substring(0, small.lastIndexOf('.'));
            File test = new File(small);
            if (test.exists()) {
                return;
            }
            logger.info("Unable to find: " + small);
        }
    }

    private synchronized long makeGraph(String name) 
            throws IllegalNameException, CaughtException, GraphNotFoundException {
        Date now = new Date();
        long graphId = now.getTime();
        return makeGraph(graphId, name);
    }

    public synchronized ONDEXGraph makeGraph(String oldName, String reason)
            throws CaughtException, GraphNotFoundException  {
        Date now = new Date();
        long graphId = now.getTime();
        String name = oldName + graphId + reason;
        try {
            makeGraph(graphId, name);
            return getGraphFromRegistry(graphId);
        } catch (IllegalNameException e) {
            //Should never happen so convert
            throw new CaughtException(e, logger);
        } catch (GraphNotFoundException e) {
            //Should never happen so convert
            throw new CaughtException(e, logger);
        }
    }

    private synchronized long makeGraph(long graphId, String name)
            throws IllegalNameException, CaughtException, GraphNotFoundException {
        WSGraph result = getGraphOfNameOrNull(name);
        if (result != null) {
            throw new IllegalNameException("A graph called " + name + " already exists.", logger);
        }
        try {
            String graphID = GRAPH_DIR_PREFIX + GRAPH_DIR_PRE_ID + graphId + GRAPH_DIR_POST_ID + name;
            File graphDbDir = new File(dbPath, graphID);
            graphDbDir.mkdirs();
            BerkeleyEnv berkeleyEnv = new BerkeleyEnv(graphDbDir.getPath(), name,
                    graphId, new ONDEXLogger());

            logger.info("Making new graph " + graphId + " at: " + graphDbDir.toURI());
            ONDEXGraph graph = berkeleyEnv.getAbstractONDEXGraph();
            logger.info("Made new graph " + graphId + " at: " + graphDbDir.toURI());
            registerGraph(graphId, berkeleyEnv);

            return graphId;
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
    }

    public Long createMemoryGraph(String name) {
        if ((name == null) || name.length() == 0) {
            Date now = new Date();
            long graphId = now.getTime();
            name = "temp" + graphId;
        }
        ONDEXGraph graph = new MemoryONDEXGraph("");
        return graph.getSID();
    }

    public Long createTestGraph() throws CaughtException, GraphNotFoundException  {
        try {
            Date now = new Date();
            long graphId = now.getTime();
            String name = "test" + graphId;
            makeGraph(graphId, name);
            return graphId;
        } catch (IllegalNameException e) {
            //Should never happen so convert
            throw new CaughtException(e, logger);
        }
    }

    public boolean inheritedFromConcept(Long graphId, Integer conceptId,
                                        String conceptClassId)
            throws GraphNotFoundException, MetaDataNotFoundException, ConceptClassNotFoundException,
            ConceptNotFoundException, CaughtException {
        ONDEXGraph graph = getGraphFromRegistry(graphId);
        ConceptClass conceptClass = getConceptClass(graph, conceptClassId);
        ONDEXConcept concept = getConcept(graph, conceptId);
        try {
            return concept.inheritedFrom(conceptClass);
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
    }

    public boolean inheritedFromRelation(Long graphId, Integer relationId,
                                         String relationTypeId)
            throws GraphNotFoundException, RelationNotFoundException,
            MetaDataNotFoundException, RelationTypeNotFoundException, CaughtException {
        ONDEXGraph graph = getGraphFromRegistry(graphId);
        RelationType typeSet = getRelationType(graph, relationTypeId);
        ONDEXRelation relation = getRelation(graph, relationId);
        try {
            return relation.inheritedFrom(typeSet);
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
    }

    public boolean isReadOnly(Long graphId)
            throws GraphNotFoundException, CaughtException {
        ONDEXGraph graph = getGraphFromRegistry(graphId);
        try {
            return graph.isReadOnly();
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
    }

    public boolean isValidName(String name)
            throws IllegalNameException {
        if (name.lastIndexOf(GRAPH_DIR_PRE_ID) != -1) {
            throw new IllegalNameException("Name: " + name + " conations " + GRAPH_DIR_PRE_ID, logger);
        }
        if (name.lastIndexOf(GRAPH_DIR_POST_ID) != -1) {
            logger.error("Name: " + name + " conations " + GRAPH_DIR_POST_ID);
            return false;
        }
        return true;
    }

    public boolean removeTagConcept(Long graphId, Integer conceptId,
                                    Integer tagId)
            throws GraphNotFoundException, ReadOnlyException,
            ConceptNotFoundException, CaughtException {
        ONDEXGraph graph = getGraphToEdit(graphId);
        ONDEXConcept concept = getConcept(graph, conceptId);
        ONDEXConcept tag = getConcept(graph, tagId);
        boolean result;
        try {
            result = concept.removeTag(tag);
            commit(graphId);
            return result;
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
    }

    public boolean removeTagRelation(Long graphId, Integer relationId,
                                     Integer tagId)
            throws GraphNotFoundException, ReadOnlyException,
            ConceptNotFoundException,
            RelationNotFoundException, CaughtException {
        ONDEXGraph graph = getGraphToEdit(graphId);
        ONDEXRelation relation = getRelation(graph, relationId);
        ONDEXConcept tag = getConcept(graph, tagId);
        boolean result;
        try {
            result = relation.removeTag(tag);
            commit(graphId);
            return result;
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
    }

    public boolean removeEvidenceTypeConcept(Long graphId, Integer conceptId,
                                             String evidenceTypeId)
            throws GraphNotFoundException, ReadOnlyException,
            ConceptNotFoundException, MetaDataNotFoundException,
            EvidenceTypeNotFoundException, CaughtException {
        ONDEXGraph graph = getGraphToEdit(graphId);
        ONDEXConcept concept = getConcept(graph, conceptId);
        EvidenceType evidenceType = getEvidenceType(graph, evidenceTypeId);
        boolean result;
        try {
            result = concept.removeEvidenceType(evidenceType);
            commit(graphId);
            return result;
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
    }

    public boolean removeEvidenceTypeRelation(Long graphId, Integer relationId,
                                              String evidenceTypeId)
            throws GraphNotFoundException, ReadOnlyException,
            RelationNotFoundException, MetaDataNotFoundException,
            EvidenceTypeNotFoundException, CaughtException {
        ONDEXGraph graph = getGraphToEdit(graphId);
        ONDEXRelation relation = getRelation(graph, relationId);
        EvidenceType evidenceType = getEvidenceType(graph, evidenceTypeId);
        boolean result;
        try {
            result = relation.removeEvidenceType(evidenceType);
            commit(graphId);
            return result;
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
    }

    public String setAnnotation(Long graphId, Integer conceptId, String annotation)
            throws GraphNotFoundException, ConceptNotFoundException, CaughtException {
        ONDEXGraph graph = getGraphFromRegistry(graphId);
        ONDEXConcept concept = getConcept(graph, conceptId);
        try {
            concept.setAnnotation(annotation);
            commit(graphId);
            return ("Successfully set Annotation " + annotation + " in Concept " + conceptId +
                 " from graph " + graphId + " to \"" + concept.getAnnotation() + "\"");
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
    }

    public String setDescription(Long graphId, Integer conceptId, String description)
            throws GraphNotFoundException, ConceptNotFoundException, CaughtException {
        ONDEXGraph graph = getGraphFromRegistry(graphId);
        ONDEXConcept concept = getConcept(graph, conceptId);
        try {
            concept.setDescription(description);
            commit(graphId);
            return ("Successfully set Description " + description + " in Concept " + conceptId +
                " from graph " + graphId);
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
    }

    public String setPID(Long graphId, Integer conceptId, String pid)
            throws GraphNotFoundException, ConceptNotFoundException, CaughtException {
        ONDEXGraph graph = getGraphFromRegistry(graphId);
        ONDEXConcept concept = getConcept(graph, conceptId);
        try {
            concept.setPID(pid);
            commit(graphId);
            return ("Successfully set Pid " + pid + " in Concept " + conceptId +
                " from graph " + graphId);
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
    }

    private void readDatabases(File dbDir)
            throws ParsingException,
            GraphNotFoundException, ReadOnlyException {
        if (dbDir.exists() && dbDir.listFiles().length > 0) {
            for (File file : dbDir.listFiles()) {
                logger.info("loading " + file.getAbsolutePath());
                if (file.isDirectory()) {
                    String dirName = file.getName();
                    if (dirName.startsWith(GRAPH_DIR_PREFIX)) {
                        loadStub(file);
                    } else {
                        logger.info("ignoring directory: " + file.getAbsolutePath());
                    }
                } else {
                    logger.error(file + " is not a directory");
                }
            }
        }
    }

    private long loadStub(File file)
            throws ParsingException,
            GraphNotFoundException, ReadOnlyException {
        logger.info("loading " + file.getAbsolutePath());
        long graphId = -1;
        if (file.isDirectory()) {
            String dirName = file.getName();
            if (dirName.startsWith(GRAPH_DIR_PREFIX)) {
                String temp = dirName.substring(
                        dirName.indexOf(GRAPH_DIR_PRE_ID) + 1,
                        dirName.indexOf(GRAPH_DIR_POST_ID));
                graphId = Long.parseLong(temp);
                String graphName = dirName.substring(
                        dirName.indexOf(GRAPH_DIR_POST_ID) + 1);
                ONDEXGraph graph = new StubGraph(graphName, graphId, file.getAbsolutePath());
                ONDEXGraphRegistry.graphs.put(graphId, graph);
                logger.info("Created Stub graph: " + graphName + " " + graphId + " sid =" + graph.getSID());
            } else {
                throw new GraphNotFoundException(
                        "Unable to read " + file.getAbsolutePath(), logger);
            }
        } else {
            throw new GraphNotFoundException(
                    "Unable to read " + file.getAbsolutePath(), logger);
        }
        logger.info("setting session");
        return graphId;
    }

    private long loadBerley(File file)
            throws ParsingException,
            GraphNotFoundException, ReadOnlyException {
        logger.info("loading " + file.getAbsolutePath());
        long graphId = -1;
        if (file.isDirectory()) {
            String dirName = file.getName();
            if (dirName.startsWith(GRAPH_DIR_PREFIX)) {
                String temp = dirName.substring(
                        dirName.indexOf(GRAPH_DIR_PRE_ID) + 1,
                        dirName.indexOf(GRAPH_DIR_POST_ID));
                graphId = Long.parseLong(temp);
                String graphName = dirName.substring(
                        dirName.indexOf(GRAPH_DIR_POST_ID) + 1);
                BerkeleyEnv berkeleyEnv = new BerkeleyEnv(file.getPath(),
                        graphName, graphId, new ONDEXLogger());
                ONDEXGraph graph = berkeleyEnv.getAbstractONDEXGraph();
                registerGraph(graphId, berkeleyEnv);
                logger.info("Loaded graph: " + graphName + " " + graphId + " sid =" + graph.getSID());
            } else {
                throw new GraphNotFoundException(
                        "Unable to read " + file.getAbsolutePath(), logger);
            }
        } else {
            throw new GraphNotFoundException(
                    "Unable to read " + file.getAbsolutePath(), logger);
        }
        logger.info("setting session");
        return graphId;
    }

    private void registerGraph(long graphId, BerkeleyEnv berkeleyEnv) {
        databases.put(graphId, berkeleyEnv);
    }

    private void moveStyleSheet() {
        File styleSheet = new File(System.getProperty("webapp.root") + "WEB-INF/classes/stylesheet.css");
        if (styleSheet.exists()) {
            File parent = styleSheet.getParentFile().getParentFile().getParentFile();
            styleSheet.renameTo(new File(parent.getAbsolutePath() + File.separator + "stylesheet.css"));
        }
    }

    public void setOndexDir(String ondexDir) throws WebserviceException {
        try {
            if (ondexDir.equals(this.ondexDir)) {
                return; //Already set
            }
            logger.info("ondexDir set to " + ondexDir);
            File ondexDirFile = new File(ondexDir);
            if (!ondexDirFile.exists()) {
                if (ondexDirFile.mkdirs()) {
                    logger.info("made new directory at " + ondexDirFile.getAbsolutePath());
                } else {
                    throw new StartUpException("Unable to create directory ondexDir: "  + ondexDir, logger);
                }
            } else if (!ondexDirFile.canWrite()) {
                throw new StartUpException("Unable to write to directory ondexDir: " + ondexDir, logger);
            }
            copyResourceToDir(ondexDir, "config.xml");
            copyResourceToDir(ondexDir, "log4j.properties");

            Config.ondexDir = ondexDir;
            Config.loadConfig();

            this.ondexDir = ondexDir;
            dbPath = new File(ondexDir, DB_DIR);
            logger.info("dbPath Directory is " + dbPath.toURI());
            CleanupContextListener.setOndexGraph(this);
            readDatabases(dbPath);
            moveStyleSheet();
            logger.info("setDir done");
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e){
            throw new CaughtException(e, logger);
        }
    }

    public void cleanup() {
        logger.info("cleanup called");
        for (BerkeleyEnv database : databases.values()) {
            database.commit();
        }
    }

    public synchronized long cloneGraph(long graphId, String name)
            throws GraphNotFoundException, ReadOnlyException,
            IllegalNameException, GraphNotImportedException, ParsingException, CaughtException {
        ONDEXGraph graph = getGraphToEdit(graphId);
        if (name == null || name.equals("")) {
            name = name + "2";
        }
        isValidName(name);
        if (getGraphOfNameOrNull(name) != null) {
            throw new IllegalNameException(
                    "graph with name " + name + " already exists", logger);
        }
        String dirSt = GRAPH_DIR_PREFIX + GRAPH_DIR_PRE_ID
                + graphId + GRAPH_DIR_POST_ID + graph.getName();
        File oldDir = new File(dbPath, dirSt);
        Date now = new Date();
        long newId = now.getTime();
        dirSt = GRAPH_DIR_PREFIX + GRAPH_DIR_PRE_ID
                + graphId + GRAPH_DIR_POST_ID + graph.getName();
        if (!oldDir.isDirectory()) {
            throw new GraphNotImportedException("Unable to clone graph " +
                    graphId + " oldDir " + oldDir.getAbsolutePath() + " not does exists", logger);
        }
        //oldDir.
        dirSt = GRAPH_DIR_PREFIX + GRAPH_DIR_PRE_ID
                + newId + GRAPH_DIR_POST_ID + name;
        File newDir = new File(dbPath, dirSt);
        try {
            copy(oldDir, newDir);
        } catch (IOException ex) {
            throw new GraphNotImportedException("Unable to clone graph " + ex, logger);
        }
        return loadBerley(newDir);
    }

    public void commit(long graphId) {
        BerkeleyEnv env = databases.get(graphId);
        if (env != null) {
            env.commit();
        }
    }

    private void copy(File source, File dest) throws IOException {
        if (source.isDirectory()) {
            if (!dest.exists()) {
                dest.mkdir();
            }
            if (dest.isDirectory()) {
                File[] files = source.listFiles();
                for (File sourceSub : files) {
                    copy(sourceSub, dest);
                }
            } else {
                throw new IOException(
                        "Unable to copy directory to none directory");
            }
        } else {
            if (dest.isDirectory()) {
                dest = new File(dest, source.getName());
            }
            FileChannel in = null, out = null;
            try {
                in = new FileInputStream(source).getChannel();
                out = new FileOutputStream(dest).getChannel();

                long size = in.size();
                MappedByteBuffer buf = in.map(FileChannel.MapMode.READ_ONLY, 0, size);
                out.write(buf);
            } finally {
                if (in != null) in.close();
                if (out != null) out.close();
            }
        }
    }

    private void copyResourceToDir(String ondexDir, String resource) throws StartUpException {
        InputStream inputStream = WebServiceEngine.class.getClassLoader()
                .getResourceAsStream(resource);

        if (inputStream == null) {
            throw new StartUpException("Unable to open resource: '" + resource, logger);
        }

        FileWriter fileWriter;
        try {
            fileWriter = new FileWriter(new File(ondexDir, resource));
            IOUtils.copy(inputStream, fileWriter);
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            throw new StartUpException("Unable to copy '" + resource + "' file to "
                    + ondexDir + "' " + e.getMessage(), logger);
        }
    }

    private void write(ONDEXGraph graph, Writer writer)
            throws XMLStreamException, JAXBException {

        WstxOutputFactory xmlw = new WstxOutputFactory();
        xmlw.configureForRobustness();

        xmlw.setProperty(XMLOutputFactory2.IS_REPAIRING_NAMESPACES, false);
        xmlw.setProperty(WstxOutputProperties.P_OUTPUT_FIX_CONTENT, true);
        xmlw.setProperty(WstxOutputProperties.P_OUTPUT_VALIDATE_CONTENT, true);

        XMLStreamWriter2 xmlWriteStream = xmlw
                .createXMLStreamWriter(writer, CharsetNames.CS_UTF8);

        new Export().buildDocument(xmlWriteStream, graph);

        xmlWriteStream.flush();
        xmlWriteStream.close();

    }

    private List<WSEvidenceType> toList(
            Set<EvidenceType> evidenceTypes)
            throws NullInViewException {
        try {
            List<WSEvidenceType> list = new ArrayList<WSEvidenceType>();
            //ogger.info("size = " + list.size());
            logger.info("trace");
            for (EvidenceType evidenceType : evidenceTypes) {
                //ogger.info("evidenceType=" + evidenceType + "@");
                if (evidenceType == null) {
                    if (!IGNORE_NULLS_IN_VIEWS) {
                        throw new NullInViewException(
                                "Null in Set<EvidenceType>", logger);
                    }
                } else {
                    //ogger.info("making ws");
                    WSEvidenceType ws = new WSEvidenceType(evidenceType);
                    logger.info(ws);
                    list.add(ws);
                    //ogger.info("added");
                }
            }
            WSEvidenceType[] output = list.toArray(new WSEvidenceType[0]);
            for (WSEvidenceType et : output) {
                //ogger.info(et);
            }
            //ogger.info("returning");
            return list;
        } catch (Exception e) {
            throw new NullInViewException("Exception in toList " + e, logger);
        }
    }
    // MetaData Methods

    private ONDEXGraphMetaData getMetaData(ONDEXGraph graph) throws MetaDataNotFoundException {
        ONDEXGraphMetaData metaData = graph.getMetaData();
        if (metaData == null) {
            throw new MetaDataNotFoundException(
                    "Unable to get MetaData from graph: " + graph.getName(), logger);
        }
        return metaData;
    }

    private AttributeName getAttributeName(ONDEXGraph graph, String id)
            throws AttributeNameNotFoundException, MetaDataNotFoundException {
        ONDEXGraphMetaData metaData = getMetaData(graph);
        AttributeName attributeName = metaData.getAttributeName(id);
        if (attributeName == null) {
            throw new AttributeNameNotFoundException(
                    "Unable to get attributeName: "
                            + id + " from MetaData", graph, logger);
        }
        return attributeName;
    }

    private AttributeName getAttributeNameOrNull(ONDEXGraph graph, String id)
            throws AttributeNameNotFoundException, MetaDataNotFoundException {
        if (id == null) {
            return null;
        }
        if (id.equals("")) {
            return null;
        }
        return getAttributeName(graph, id);
    }

    private ConceptClass getConceptClass(ONDEXGraph graph, String id)
            throws MetaDataNotFoundException, ConceptClassNotFoundException, CaughtException {
        ONDEXGraphMetaData metaData = getMetaData(graph);
        ConceptClass conceptClass;
        try {
            conceptClass = metaData.getConceptClass(id);
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
        if (conceptClass == null) {
            throw new ConceptClassNotFoundException(
                    "Unable to get ConceptClass: "
                            + id + " from MetaData from graph " + graph.getName(), logger);
        }
        return conceptClass;
    }

    private ConceptClass getConceptClassOrNull(ONDEXGraph graph, String id)
            throws MetaDataNotFoundException, ConceptClassNotFoundException, CaughtException {
        if (id == null) {
            return null;
        }
        if (id.equals("")) {
            return null;
        }
        return getConceptClass(graph, id);
    }

    private DataSource getDataSource(final ONDEXGraph graph, String id)
            throws MetaDataNotFoundException, DataSourceNotFoundException, CaughtException {
        ONDEXGraphMetaData metaData = getMetaData(graph);
        DataSource dataSource;
        try {
            dataSource = metaData.getDataSource(id);
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
        if (dataSource == null) {
            throw new DataSourceNotFoundException("Unable to find DataSource " + id + " in graph "
                    + graph.getName(), logger);
        }
        return dataSource;
    }

    private EvidenceType getEvidenceType(final ONDEXGraph graph, String id)
            throws MetaDataNotFoundException, EvidenceTypeNotFoundException {
        ONDEXGraphMetaData metaData = getMetaData(graph);
        EvidenceType evidenceType = metaData.getEvidenceType(id);
        if (evidenceType == null) {
            throw new EvidenceTypeNotFoundException(
                    "Unable to find evidenceType " + id + " in graph "
                            + graph.getName(), logger);
        }
        return evidenceType;
    }

    private RelationType getRelationType(final ONDEXGraph graph, String id)
            throws MetaDataNotFoundException, RelationTypeNotFoundException, CaughtException {
        ONDEXGraphMetaData metaData = getMetaData(graph);
        RelationType relationType;
        try {
            relationType = metaData.getRelationType(id);
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
        if (relationType == null) {
            throw new RelationTypeNotFoundException(
                    "Unable to find relationType " + id + " in graph "
                            + graph.getName(), logger);
        }
        return relationType;
    }

    private RelationType getRelationTypeOrNull(final ONDEXGraph graph,
                                               String id)
            throws MetaDataNotFoundException, RelationTypeNotFoundException, CaughtException {
        if (id == null) {
            return null;
        }
        if (id.equals("")) {
            return null;
        }
        return getRelationType(graph, id);
    }

    private Unit getUnit(final ONDEXGraph graph, final String id)
            throws MetaDataNotFoundException, UnitNotFoundException, CaughtException {
        ONDEXGraphMetaData metaData = getMetaData(graph);
        Unit unit;
        try {
            unit = metaData.getUnit(id);
        } catch (Exception e) {
            throw new CaughtException(e, logger);
        }
        if (unit == null) {
            throw new UnitNotFoundException("Unable to get unit: "
                    + id + " from MetaData from MetaData from graph "
                    + graph.getName(), logger);
        }
        return unit;
    }

    private Unit getUnitOrNull(final ONDEXGraph graph, final String id)
            throws MetaDataNotFoundException, UnitNotFoundException, CaughtException {
        if (id == null) {
            return null;
        }
        if (id.equals("")) {
            return null;
        }
        return getUnit(graph, id);
    }

}
