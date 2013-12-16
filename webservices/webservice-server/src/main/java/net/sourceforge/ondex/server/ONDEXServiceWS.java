package net.sourceforge.ondex.server;

import net.sourceforge.ondex.core.ONDEXConcept;
import net.sourceforge.ondex.core.ONDEXRelation;

import org.apache.log4j.Logger;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import net.sourceforge.ondex.core.Attribute;
import net.sourceforge.ondex.wsapi.exceptions.CaughtException;
import net.sourceforge.ondex.wsapi.exceptions.WebserviceException;
import net.sourceforge.ondex.server.plugins.parser.ParserAuto;
import net.sourceforge.ondex.wsapi.WebServiceEngine;
import net.sourceforge.ondex.wsapi.result.WSConceptAccession;
import net.sourceforge.ondex.wsapi.result.WSAttribute;
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

/**
 * @author Christian Brenninkmeijer
 */
public class ONDEXServiceWS {

    WebServiceEngine engine = WebServiceEngine.getWebServiceEngine();

    private static final Logger logger = Logger.getLogger(ONDEXServiceWS.class);

    /**
     * Startup method.
     *
     * This method is for the startup only and should not be called.
     * @param ondexDir
     * @throws WebserviceException
     */
    @WebMethod(exclude=true)
    public void setOndexDir(String ondexDir)
            throws WebserviceException {
        try {
            engine.setOndexDir(ondexDir);
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Creates a new {@link WSConcept} and adds it to the Graph.
     *
     * @param graphId              the ID of the Graph
     * @param annotation           the annotation for the {@link WSConcept} (Optional)
     * @param description          the description of the {@link WSConcept} (Optional)
     * @param elementOfDataSourceId        the ID of the {@link WSDataSource} for the elementOf property
     * @param ofTypeConceptClassId the ID of the {@link WSConceptClass} for the ofType property
     * @param evidenceTypeIdList   a list of {@link WSEvidenceType} IDs. Must contain at least
     *                             one EvidenceTypeID
     * @return the ID of the new {@link WSConcept}
     * @throws WebserviceException
     */
    @WebResult(name = "conceptId")
    @WebMethod(exclude = false)
    public Integer createConcept(
            @WebParam(name = "graphId") Long graphId,
            @WebParam(name = "annotation") String annotation,
            @WebParam(name = "description") String description,
            @WebParam(name = "elementOfDataSourceId") String elementOfDataSourceId,
            @WebParam(name = "ofTypeConceptClassId") String ofTypeConceptClassId,
            @WebParam(name = "evidenceTypeIdList") List<String> evidenceTypeIdList)
            throws WebserviceException {
        try {
            return engine.createConcept(graphId, "", annotation, description,
                    elementOfDataSourceId, ofTypeConceptClassId, evidenceTypeIdList);
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Creates a new {@link WSRelation} and adds it to the Graph.
     *
     * @param graphId            the ID of the Graph
     * @param fromConceptId      the ID of the 'from' {@link WSConcept}
     * @param toConceptId        the ID of the 'from' {@link WSConcept}
     * @param relationTypeId     the ID of the {@link WSRelationType}
     * @param evidenceTypeIdList a list of {@link WSEvidenceType} IDs. Must contain at least
     *                           one {@link WSEvidenceType} ID
     * @return the ID of the new {@link WSRelation}
     * @throws WebserviceException
     * @derpricated
     */
    @WebResult(name = "relationId")
    @WebMethod(exclude = false)
    public Integer createRelation(
            @WebParam(name = "graphId") Long graphId,
            @WebParam(name = "fromConceptId") Integer fromConceptId,
            @WebParam(name = "toConceptId") Integer toConceptId,
            @WebParam(name = "relationTypeId") String relationTypeId,
            @WebParam(name = "evidenceTypeIdList") List<String> evidenceTypeIdList)
            throws WebserviceException {
        try {
            return engine.createRelation(graphId, fromConceptId, toConceptId,
                    relationTypeId, evidenceTypeIdList);
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
     }

    /**
     * Removes a {@link WSConcept} from the Graph.
     *
     * @param graphId   the ID of the Graph
     * @param conceptId the ID of the {@link WSConcept} to remove
     * @return ""\Successfully removed+ X\" or "X not found"
     *         where X is the id of the item being deleted.
     * @throws WebserviceException
     */
    @WebMethod(exclude = false)
    @WebResult(name = "success")
    public String deleteConcept(@WebParam(name = "graphId") Long graphId,
                                @WebParam(name = "conceptId") Integer conceptId)
            throws WebserviceException {
        try {
            boolean result = engine.deleteConcept(graphId, conceptId);
            if (result) {
                return "Successfully removed " + conceptId;
            } else {
                return conceptId + "not found.";
            }
       } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
     }

    /**
     * Removes a {@link WSRelation} from the Graph.
     *
     * @param graphId    the ID of the Graph
     * @param relationId the ID of the {@link WSRelation} to remove
     * @return ""\Successfully removed+ X\" or "X not found"
     *         where X is the id of the item being deleted.
     * @throws WebserviceException
     */
    @WebMethod(exclude = false)
    @WebResult(name = "success")
    public String deleteRelation(@WebParam(name = "graphId") Long graphId,
                                 @WebParam(name = "relationId") Integer relationId)
            throws WebserviceException {
        try{
            boolean result = engine.deleteRelation(graphId, relationId);
            if (result) {
                return "Successfully removed " + relationId;
            } else {
                return relationId + "not found.";
            }
       } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
     }

    /**
     * Removes a {@link WSRelation} from the Graph. The {@link WSRelation} to
     * remove is specified by its to and from {@link WSConcept}s,
     * {@link WSEvidenceType}.
     *
     * @param graphId        the ID of the Graph
     * @param fromConceptId  the ID of the 'from' {@link WSConcept}
     * @param toConceptId    the ID of the 'to' {@link WSConcept}
     * @param relationTypeId the ID of the {@link WSEvidenceType}
     * @return ""\Successfully removed+ X\" or "X not found"
     *         where X is the id of the item being deleted.
     * @throws WebserviceException
     */
    @WebMethod(exclude = false)
    @WebResult(name = "success")
    public String deleteRelationOfType(
            @WebParam(name = "graphId") Long graphId,
            @WebParam(name = "fromConceptId") Integer fromConceptId,
            @WebParam(name = "toConceptId") Integer toConceptId,
            @WebParam(name = "relationTypeId") String relationTypeId)
            throws WebserviceException {
        try{
            boolean result = engine.deleteRelationOfType(graphId, fromConceptId,
                    toConceptId, relationTypeId);
            if (result) {
                return "Successfully removed " + relationTypeId;
            } else {
                return relationTypeId + "not found.";
            }
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
     }

    /**
     * Returns the {@link WSConcept} with the specified ID.
     *
     * @param graphId   the ID of the Graph
     * @param conceptId the ID of the {@link WSConcept} to return
     * @return the {@link WSConcept} with the specified ID
     * @throws WebserviceException
     */
    @WebResult(name = "concept")
    @WebMethod(exclude = false)
    public WSConcept getConcept(@WebParam(name = "graphId") Long graphId,
                                @WebParam(name = "conceptId") Integer conceptId)
            throws WebserviceException {
        try{
            return new WSConcept(engine.getConcept(graphId, conceptId));
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Returns all the {@link WSConcept}s in the Graph
     *
     * @param graphId the ID of the Graph
     * @return all the {@link WSConcept}s in the Graph
     * @throws WebserviceException
     */
    @WebResult(name = "concepts")
    @WebMethod(exclude = false)
    public List<WSConcept> getConcepts(@WebParam(name = "graphId") Long graphId)
            throws WebserviceException {
        try{
            List<WSConcept> list = new ArrayList<WSConcept>();
            for (ONDEXConcept c : engine.getConcepts(graphId)) {
                list.add(new WSConcept(c));
            }
            return list;
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Returns the number of {@link WSConcept}s in the Graph
     *
     * @param graphId the ID of the Graph
     * @return The number of {@link WSConcept}s in the Graph
     * @throws WebserviceException
     */
    @WebResult(name = "number")
    @WebMethod(exclude = false)
    public int getNumberOfConcepts(@WebParam(name = "graphId") Long graphId)
            throws WebserviceException {
        try{
            return engine.getNumberOfConcepts(graphId);
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Returns all the id of the concepts in the Graph.
     *
     * @param graphId the ID of the Graph
     * @return all the id of the concepts in the Graph
     * @throws WebserviceException
     */
    @WebResult(name = "conceptIds")
    @WebMethod(exclude = false)
    public List<Integer> getConceptIds(@WebParam(name = "graphId") Long graphId)
            throws WebserviceException {
        try{
            List<Integer> list = new ArrayList<Integer>();
            for (ONDEXConcept c : engine.getConcepts(graphId)) {
                list.add(c.getId());
            }
            return list;
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Returns all the {@link WSConcept}s with specified {@link WSAttributeName}.
     *
     * @param graphId         the ID of the Graph
     * @param attributeNameId the ID of the {@link WSAttributeName}
     * @return all the {@link WSConcept}s with specified {@link WSAttributeName}
     * @throws WebserviceException
     */
    @WebResult(name = "concepts")
    @WebMethod(exclude = false)
    public List<WSConcept> getConceptIdsOfAttributeName(
            @WebParam(name = "graphId") Long graphId,
            @WebParam(name = "attributeNameId") String attributeNameId)
            throws WebserviceException {
        try{
            List<WSConcept> list = new ArrayList<WSConcept>();
            for (ONDEXConcept c : engine.getConceptsOfAttributeName(graphId, attributeNameId)) {
                list.add(new WSConcept(c));
            }
            return list;
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Returns List of all ids of the {@link WSConcept}s with specified {@link WSAttributeName}.
     *
     * @param graphId         the ID of the Graph
     * @param attributeNameId the ID of the {@link WSAttributeName}
     * @return List of all ids of the {@link WSConcept}s with specified {@link WSAttributeName}
     * @throws WebserviceException
     */
    @WebResult(name = "concepts")
    @WebMethod(exclude = false)
    public List<Integer> getConceptsOfAttributeName(
            @WebParam(name = "graphId") Long graphId,
            @WebParam(name = "attributeNameId") String attributeNameId)
            throws WebserviceException {
        try{
            List<Integer> list = new ArrayList<Integer>();
            for (ONDEXConcept c : engine.getConceptsOfAttributeName(graphId, attributeNameId)) {
                list.add(new Integer(c.getId()));
            }
            return list;
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Returns all the {@link WSConcept}s with specified {@link WSConceptClass}.
     *
     * @param graphId        the ID of the Graph
     * @param conceptClassId the ID of the {@link WSConceptClass}
     * @return all the {@link WSConcept}s with specified {@link WSConceptClass}
     * @throws WebserviceException
     */
    @WebResult(name = "concepts")
    @WebMethod(exclude = false)
    public List<WSConcept> getConceptsOfConceptClass(
            @WebParam(name = "graphId") Long graphId,
            @WebParam(name = "conceptClassId") String conceptClassId)
            throws WebserviceException {
        try{
            List<WSConcept> list = new ArrayList<WSConcept>();
            for (ONDEXConcept c : engine.getConceptsOfConceptClass(graphId, conceptClassId)) {
                list.add(new WSConcept(c));
            }
            return list;
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Returns a list of all ids of the {@link WSConcept}s with specified {@link WSConceptClass}.
     *
     * @param graphId        the ID of the Graph
     * @param conceptClassId the ID of the {@link WSConceptClass}
     * @return List of all ids of the {@link WSConcept}s with specified {@link WSConceptClass}
     * @throws WebserviceException
     */
    @WebResult(name = "conceptIds")
    @WebMethod(exclude = false)
    public List<Integer> getConceptIdsOfConceptClass(
            @WebParam(name = "graphId") Long graphId,
            @WebParam(name = "conceptClassId") String conceptClassId)
            throws WebserviceException {
        try{
            List<Integer> list = new ArrayList<Integer>();
            for (ONDEXConcept c : engine.getConceptsOfConceptClass(graphId, conceptClassId)) {
                list.add(c.getId());
            }
            return list;
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Returns all the {@link WSConcept}s with specified tag {@link WSConcept}.
     * {@link WSConcept}.
     *
     * @param graphId      the ID of the Graph
     * @param tagConceptId the ID of the tag {@link WSConcept}
     * @return all the {@link WSConcept}s with specified tag {@link WSConcept}
     * @throws WebserviceException
     */
    @WebResult(name = "concepts")
    @WebMethod(exclude = false)
    public List<WSConcept> getConceptsOfTag(
            @WebParam(name = "graphId") Long graphId,
            @WebParam(name = "tagConceptId") Integer tagConceptId)
            throws WebserviceException {
        try{
            List<WSConcept> list = new ArrayList<WSConcept>();
            for (ONDEXConcept c : engine.getConceptsOfTag(graphId, tagConceptId)) {
                list.add(new WSConcept(c));
            }
            return list;
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Returns a list of all the Ids of the {@link WSConcept}s with specified tag
     * {@link WSConcept}.
     *
     * @param graphId      the ID of the Graph
     * @param tagConceptId the ID of the tag {@link WSConcept}
     * @return List of all the Ids of the {@link WSConcept}s with specified tag
     *         {@link WSConcept}
     * @throws WebserviceException
     */
    @WebResult(name = "conceptIds")
    @WebMethod(exclude = false)
    public List<Integer> getConceptIdsOfTag(
            @WebParam(name = "graphId") Long graphId,
            @WebParam(name = "tagConceptId") Integer tagConceptId)
            throws WebserviceException {
        try{
            List<Integer> list = new ArrayList<Integer>();
            for (ONDEXConcept c : engine.getConceptsOfTag(graphId, tagConceptId)) {
                list.add(c.getId());
            }
            return list;
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Returns all the {@link WSConcept}s with specified {@link WSDataSource}.
     *
     * @param graphId the ID of the Graph
     * @param dataSourceId    the ID of the {@link WSDataSource}
     * @return all the {@link WSConcept}s with specified {@link WSDataSource}
     * @throws WebserviceException
     */
    @WebResult(name = "concepts")
    @WebMethod(exclude = false)
    public List<WSConcept> getConceptsOfDataSource(
            @WebParam(name = "graphId") Long graphId,
            @WebParam(name = "dataSourceId") String dataSourceId)
            throws WebserviceException {
        try{
           List<WSConcept> list = new ArrayList<WSConcept>();
            for (ONDEXConcept c : engine.getConceptsOfDataSource(graphId, dataSourceId)) {
                list.add(new WSConcept(c));
            }
            return list;
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Returns a list of Ids of all the {@link WSConcept}s with specified {@link WSDataSource}.
     *
     * @param graphId the ID of the Graph
     * @param dataSourceId    the ID of the {@link WSDataSource}
     * @return List of Ids of all the {@link WSConcept}s with specified {@link WSDataSource}
     * @throws WebserviceException
     */
    @WebResult(name = "conceptIds")
    @WebMethod(exclude = false)
    public List<Integer> getConceptIdsOfDataSource(
            @WebParam(name = "graphId") Long graphId,
            @WebParam(name = "dataSourceId") String dataSourceId)
            throws WebserviceException {
        try{
            List<Integer> list = new ArrayList<Integer>();
            for (ONDEXConcept c : engine.getConceptsOfDataSource(graphId, dataSourceId)) {
                list.add(c.getId());
            }
            return list;
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Returns all the {@link WSConcept}s with specified {@link WSEvidenceType}.
     *
     * @param graphId        the ID of the Graph
     * @param evidenceTypeId the ID of the {@link WSEvidenceType}
     * @return all the {@link WSConcept}s with specified {@link WSEvidenceType}
     * @throws WebserviceException
     */
    @WebResult(name = "concepts")
    @WebMethod(exclude = false)
    public List<WSConcept> getConceptsOfEvidenceType(
            @WebParam(name = "graphId") Long graphId,
            @WebParam(name = "evidenceTypeId") String evidenceTypeId)
            throws WebserviceException {
        try{
            List<WSConcept> list = new ArrayList<WSConcept>();
            for (ONDEXConcept c : engine.getConceptsOfEvidenceType(graphId, evidenceTypeId)) {
                list.add(new WSConcept(c));
            }
            return list;
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Returns a list of all the Ids of the Concepts with specified {@link WSEvidenceType}.
     *
     * @param graphId        the ID of the Graph
     * @param evidenceTypeId the ID of the {@link WSEvidenceType}
     * @return List of all the Ids of the Concepts with specified {@link WSEvidenceType}
     * @throws WebserviceException
     */
    @WebResult(name = "conceptIds")
    @WebMethod(exclude = false)
    public List<Integer> getConceptIdsOfEvidenceType(
            @WebParam(name = "graphId") Long graphId,
            @WebParam(name = "evidenceTypeId") String evidenceTypeId)
            throws WebserviceException {
        try{
            List<Integer> list = new ArrayList<Integer>();
            for (ONDEXConcept c : engine.getConceptsOfEvidenceType(graphId, evidenceTypeId)) {
                list.add(c.getId());
            }
            return list;
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Returns list of {@link WSConcept}s contained in the list of Concepts of
     * this graph that are tags for other Concepts/Relations.
     *
     * @param graphId the ID of the Graph
     * @return List of {@link WSConcept}s of all tags in the graph
     * @throws WebserviceException
     */
    @WebResult(name = "tags")
    @WebMethod(exclude = false)
    public List<WSConcept> getTagGraph(
            @WebParam(name = "graphId") Long graphId)
            throws WebserviceException {
        try{
            List<WSConcept> list = new ArrayList<WSConcept>();
            for (ONDEXConcept c : engine.getTagsGraph(graphId)) {
                list.add(new WSConcept(c));
            }
            return list;
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Returns A list of the ids of all tags contained in the list of Concepts of
     * this graph that are tags for other Concepts/Relations.
     *
     * @param graphId the ID of the Graph
     * @return A list of the ids of all tags in the graph
     * @throws WebserviceException
     */
    @WebResult(name = "tagIds")
    @WebMethod(exclude = false)
    public List<Integer> getTagIdsGraph(
            @WebParam(name = "graphId") Long graphId)
            throws WebserviceException {
        try{
            List<Integer> list = new ArrayList<Integer>();
            for (ONDEXConcept c : engine.getTagsGraph(graphId)) {
                list.add(c.getId());
            }
            return list;
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Returns the name of this Graph.
     *
     * @param graphId the ID of the Graph
     * @return Name of the graph
     * @throws WebserviceException
     */
    @WebResult(name = "name")
    @WebMethod(exclude = false)
    public String getName(@WebParam(name = "graphId") Long graphId)
            throws WebserviceException {
        try{
            return engine.getName(graphId);
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Returns the {@link WSRelation} with the specified ID.
     *
     * @param graphId    the ID of the Graph
     * @param relationId the ID of the {@link WSRelation} to return
     * @return the {@link WSRelation} with the specified ID
     * @throws WebserviceException
     */
    @WebResult(name = "relation")
    @WebMethod(exclude = false)
    public WSRelation getRelation(@WebParam(name = "graphId") Long graphId,
                                  @WebParam(name = "relationId") Integer relationId)
            throws WebserviceException {
        try{
            return new WSRelation(engine.getRelation(graphId, relationId));
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Returns the {@link WSRelation} with the specified {@link WSRelationType}.
     *
     * @param graphId        the ID of the Graph
     * @param fromConceptId  the ID of the 'from' {@link WSConcept}
     * @param toConceptId    the ID of the 'to' {@link WSConcept}
     * @param relationTypeId the ID of the {@link WSRelationType}
     * @return the {@link WSRelation} with the specified {@link WSRelationType}
     * @throws WebserviceException
     */
    @WebResult(name = "relation")
    @WebMethod(exclude = false)
    public WSRelation getRelationOfType(
            @WebParam(name = "graphId") Long graphId,
            @WebParam(name = "fromConceptId") Integer fromConceptId,
            @WebParam(name = "toConceptId") Integer toConceptId,
            @WebParam(name = "relationTypeId") String relationTypeId)
            throws WebserviceException {
        try{
            return new WSRelation(engine.getRelationOfType(graphId, fromConceptId,
                    toConceptId, relationTypeId));
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Returns all the {@link WSRelation}s in the Graph.
     *
     * @param graphId the ID of the Graph
     * @return all the {@link WSRelation}s in the Graph
     * @throws WebserviceException
     */
    @WebResult(name = "relations")
    @WebMethod(exclude = false)
    public List<WSRelation> getRelations(
            @WebParam(name = "graphId") Long graphId)
            throws WebserviceException {
        try{
            List<WSRelation> list = new ArrayList<WSRelation>();
            for (ONDEXRelation r : engine.getRelations(graphId)) {
                list.add(new WSRelation(r));
            }
            return list;
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Returns the number of {@link WSRelation}s in the Graph.
     *
     * @param graphId the ID of the Graph
     * @return The number of  {@link WSRelation}s in the Graph
     * @throws WebserviceException
     */
    @WebResult(name = "number")
    @WebMethod(exclude = false)
    public int getNumberOfRelations(
            @WebParam(name = "graphId") Long graphId)
            throws WebserviceException {
        try{
            return engine.getNumberOfRelations(graphId);
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Returns a list of all the Ids of the {@link WSRelation}s in the Graph.
     *
     * @param graphId the ID of the Graph
     * @return List of all the Ids of the {@link WSRelation}s in the Graph
     * @throws WebserviceException
     */
    @WebResult(name = "relationIds")
    @WebMethod(exclude = false)
    public List<Integer> getRelationIds(
            @WebParam(name = "graphId") Long graphId)
            throws WebserviceException {
        try{
            List<Integer> list = new ArrayList<Integer>();
            for (ONDEXRelation r : engine.getRelations(graphId)) {
                list.add(r.getId());
            }
            return list;
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Returns all the {@link WSRelation}s with specified
     * {@link WSAttributeName}.
     *
     * @param graphId         the ID of the Graph
     * @param attributeNameId the ID of the {@link WSAttributeName}
     * @return all the {@link WSRelation}s with specified
     *         {@link WSAttributeName}
     * @throws WebserviceException
     */
    @WebResult(name = "relations")
    @WebMethod(exclude = false)
    public List<WSRelation> getRelationsOfAttributeName(
            @WebParam(name = "graphId") Long graphId,
            @WebParam(name = "attributeNameId") String attributeNameId)
            throws WebserviceException {
        try{
            List<WSRelation> list = new ArrayList<WSRelation>();
            for (ONDEXRelation r : engine.getRelationsOfAttributeName(graphId, attributeNameId)) {
                list.add(new WSRelation(r));
            }
            return list;
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Returns a list of all the Ids of the {@link WSRelation}s with specified
     * {@link WSAttributeName}.
     *
     * @param graphId         the ID of the Graph
     * @param attributeNameId the ID of the {@link WSAttributeName}
     * @return List of all the Ids of the {@link WSRelation}s with specified
     *         {@link WSAttributeName}
     * @throws WebserviceException
     */
    @WebResult(name = "relationIds")
    @WebMethod(exclude = false)
    public List<Integer> getRelationIdsOfAttributeName(
            @WebParam(name = "graphId") Long graphId,
            @WebParam(name = "attributeNameId") String attributeNameId)
            throws WebserviceException {
        try{
            List<Integer> list = new ArrayList<Integer>();
            for (ONDEXRelation r : engine.getRelationsOfAttributeName(graphId, attributeNameId)) {
                list.add(r.getId());
            }
            return list;
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Returns all the {@link WSRelation}s with specified {@link WSConcept}.
     *
     * @param graphId   the ID of the Graph
     * @param conceptId the ID of the {@link WSConcept}
     * @return all the {@link WSRelation}s with specified {@link WSConcept}
     * @throws WebserviceException
     */
    @WebResult(name = "relations")
    @WebMethod(exclude = false)
    public List<WSRelation> getRelationsOfConcept(
            @WebParam(name = "graphId") Long graphId,
            @WebParam(name = "conceptId") Integer conceptId)
            throws WebserviceException {
        try{
            List<WSRelation> list = new ArrayList<WSRelation>();
            for (ONDEXRelation r : engine.getRelationsOfConcept(graphId, conceptId)) {
                list.add(new WSRelation(r));
            }
            return list;
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Returns a list of all the Ids of the {@link WSRelation}s with specified {@link WSConcept}.
     *
     * @param graphId   the ID of the Graph
     * @param conceptId the ID of the {@link WSConcept}
     * @return List of all the IDs of the {@link WSRelation}s with specified {@link WSConcept}
     * @throws WebserviceException
     */
    @WebResult(name = "relationIds")
    @WebMethod(exclude = false)
    public List<Integer> getRelationIdsOfConcept(
            @WebParam(name = "graphId") Long graphId,
            @WebParam(name = "conceptId") Integer conceptId)
            throws WebserviceException {
        try{
            List<Integer> list = new ArrayList<Integer>();
            for (ONDEXRelation r : engine.getRelationsOfConcept(graphId, conceptId)) {
                list.add(r.getId());
            }
            return list;
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Returns all the {@link WSRelation}s with specified {@link WSConceptClass}.
     *
     * @param graphId        the ID of the Graph
     * @param conceptClassId the ID of the {@link WSConceptClass}
     * @return all the {@link WSRelation}s with specified {@link WSConceptClass}
     * @throws WebserviceException
     */
    @WebResult(name = "relations")
    @WebMethod(exclude = false)
    public List<WSRelation> getRelationsOfConceptClass(
            @WebParam(name = "graphId") Long graphId,
            @WebParam(name = "conceptClassId") String conceptClassId)
            throws WebserviceException {
        try{
            List<WSRelation> list = new ArrayList<WSRelation>();
            for (ONDEXRelation r : engine.getRelationsOfConceptClass(graphId, conceptClassId)) {
                list.add(new WSRelation(r));
            }
            return list;
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**0
     * Returns a list of all the Ids of the {@link WSRelation}s with specified {@link WSConceptClass}.
     *
     * @param graphId        the ID of the Graph
     * @param conceptClassId the ID of the {@link WSConceptClass}
     * @return List of all the ids of the {@link WSRelation}s with specified {@link WSConceptClass}
     */
    @WebResult(name = "relationIds")
    @WebMethod(exclude = false)
    public List<Integer> getRelationIdsOfConceptClass(
            @WebParam(name = "graphId") Long graphId,
            @WebParam(name = "conceptClassId") String conceptClassId)
            throws WebserviceException {
        try{
            List<Integer> list = new ArrayList<Integer>();
            for (ONDEXRelation r : engine.getRelationsOfConceptClass(graphId, conceptClassId)) {
                list.add(r.hashCode()); // fixme: should this not be r.getID() rather than hashcode?
            }
            return list;
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Returns all the {@link WSRelation}s with specified tag
     * {@link WSConcept}.
     *
     * @param graphId      the ID of the Graph
     * @param tagConceptId the ID of the tag {@link WSConcept}
     * @return all the {@link WSRelation}s with specified tag
     *         {@link WSConcept}
     * @throws WebserviceException
     */
    @WebResult(name = "relations")
    @WebMethod(exclude = false)
    public List<WSRelation> getRelationsOfTag(
            @WebParam(name = "graphId") Long graphId,
            @WebParam(name = "tagConceptId") Integer tagConceptId)
            throws WebserviceException {
        try{
            List<WSRelation> list = new ArrayList<WSRelation>();
            for (ONDEXRelation r : engine.getRelationsOfConcept(graphId, tagConceptId)) {
                list.add(new WSRelation(r));
            }
            return list;
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Returns a list of all the Ids of the {@link WSRelation}s with specified tag
     * {@link WSConcept}.
     *
     * @param graphId      the ID of the Graph
     * @param tagConceptId the ID of the tag {@link WSConcept}
     * @return List of all the Ids of the {@link WSRelation}s with specified tag
     *         {@link WSConcept}
     * @throws WebserviceException
     */
    @WebResult(name = "relationIds")
    @WebMethod(exclude = false)
    public List<Integer> getRelationIdsOfTag(
            @WebParam(name = "graphId") Long graphId,
            @WebParam(name = "tagConceptId") Integer tagConceptId)
            throws WebserviceException {
        try{
            List<Integer> list = new ArrayList<Integer>();
            for (ONDEXRelation r : engine.getRelationsOfConcept(graphId, tagConceptId)) {
                list.add(r.getId());
            }
            return list;
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Returns all the {@link WSRelation}s with specified tag {@link WSDataSource}.
     *
     * @param graphId the ID of the Graph
     * @param dataSourceId    the ID of the {@link WSDataSource}
     * @return all the {@link WSRelation}s with specified {@link WSDataSource}
     * @throws WebserviceException
     */
    @WebResult(name = "relations")
    @WebMethod(exclude = false)
    public List<WSRelation> getRelationsOfDataSource(
            @WebParam(name = "graphId") Long graphId,
            @WebParam(name = "dataSourceId") String dataSourceId)
            throws WebserviceException {
        try{
            List<WSRelation> list = new ArrayList<WSRelation>();
            for (ONDEXRelation r : engine.getRelationsOfDataSource(graphId, dataSourceId)) {
                list.add(new WSRelation(r));
            }
            return list;
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Returns all the ids of the {@link WSRelation}s with specified tag {@link WSDataSource}.
     *
     * @param graphId the ID of the Graph
     * @param dataSourceId    the ID of the {@link WSDataSource}
     * @return List of all the ids of the {@link WSRelation}s with specified {@link WSDataSource}
     * @throws WebserviceException
     */
    @WebResult(name = "relationIds")
    @WebMethod(exclude = false)
    public List<Integer> getRelationIdsOfDataSource(
            @WebParam(name = "graphId") Long graphId,
            @WebParam(name = "dataSourceId") String dataSourceId)
            throws WebserviceException {
        try{
            List<Integer> list = new ArrayList<Integer>();
            for (ONDEXRelation r : engine.getRelationsOfDataSource(graphId, dataSourceId)) {
                list.add(r.getId());
            }
            return list;
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Returns all the {@link WSRelation}s with specified {@link WSEvidenceType}
     * .
     *
     * @param graphId        the ID of the Graph
     * @param evidenceTypeId the ID of the {@link WSEvidenceType}
     * @return all the {@link WSRelation}s with specified tag
     *         {@link WSEvidenceType}
     * @throws WebserviceException
     */
    @WebResult(name = "relations")
    @WebMethod(exclude = false)
    public List<WSRelation> getRelationsOfEvidenceType(
            @WebParam(name = "graphId") Long graphId,
            @WebParam(name = "evidenceTypeId") String evidenceTypeId)
            throws WebserviceException {
        try{
            List<WSRelation> list = new ArrayList<WSRelation>();
            for (ONDEXRelation r : engine.getRelationsOfEvidenceType(graphId, evidenceTypeId)) {
                list.add(new WSRelation(r));
            }
            return list;
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Returns a list of all the Ids of the {@link WSRelation}s with specified {@link WSEvidenceType}
     * .
     *
     * @param graphId        the ID of the Graph
     * @param evidenceTypeId the ID of the {@link WSEvidenceType}
     * @return List of all the Ids of the {@link WSRelation}s with specified tag
     *         {@link WSEvidenceType}
     * @throws WebserviceException
     */
    @WebResult(name = "relationIds")
    @WebMethod(exclude = false)
    public List<Integer> getRelationIdsOfEvidenceType(
            @WebParam(name = "graphId") Long graphId,
            @WebParam(name = "evidenceTypeId") String evidenceTypeId)
            throws WebserviceException {
        try{
            List<Integer> list = new ArrayList<Integer>();
            for (ONDEXRelation r : engine.getRelationsOfEvidenceType(graphId, evidenceTypeId)) {
                list.add(r.getId());
            }
            return list;
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Returns all the {@link WSRelation}s with specified {@link WSEvidenceType}.
     *
     * @param graphId        the ID of the Graph
     * @param relationTypeId the ID of the {@link WSEvidenceType}
     * @return all the {@link WSRelation}s with specified {@link WSEvidenceType}
     * @throws WebserviceException
     */
    @WebResult(name = "relations")
    @WebMethod(exclude = false)
    public List<WSRelation> getRelationsOfRelationType(
            @WebParam(name = "graphId") Long graphId,
            @WebParam(name = "relationTypeId") String relationTypeId)
            throws WebserviceException {
        try{
            List<WSRelation> list = new ArrayList<WSRelation>();
            for (ONDEXRelation r : engine.getRelationsOfRelationType(graphId, relationTypeId)) {
                list.add(new WSRelation(r));
            }
            return list;
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Returns a list of all the Ids of the {@link WSRelation}s with specified {@link WSEvidenceType}.
     *
     * @param graphId        the ID of the Graph
     * @param relationTypeId the ID of the {@link WSEvidenceType}
     * @return List of all the Ids of the {@link WSRelation}s with specified {@link WSEvidenceType}
     * @throws WebserviceException
     */
    @WebResult(name = "relationIds")
    @WebMethod(exclude = false)
    public List<Integer> getRelationIdsOfRelationType(
            @WebParam(name = "graphId") Long graphId,
            @WebParam(name = "relationTypeId") String relationTypeId)
            throws WebserviceException {
        try{
            List<Integer> list = new ArrayList<Integer>();
            for (ONDEXRelation r : engine.getRelationsOfRelationType(graphId, relationTypeId)) {
                list.add(r.getId());
            }
            return list;
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Returns whether or not this Graph is read only.
     *
     * @param graphId the ID of the Graph
     * @return true or false
     * @throws WebserviceException
     */
    @WebResult(name = "isReadOnly")
    @WebMethod(exclude = false)
    public boolean isReadOnly(@WebParam(name = "graphId") Long graphId)
            throws WebserviceException {
        try{
            return engine.isReadOnly(graphId);
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    //ONDEXGraphMetaDataWSApi Methods

    /**
     * Checks if an {@link WSAttributeName} for a given ID exists in the Graph.
     *
     * @param graphId         the ID of the Graph
     * @param attributeNameId the {@link WSAttributeName} of the Attribute
     * @return true if the {@link WSAttributeName} exists, false otherwise
     * @throws WebserviceException
     */
    @WebResult(name = "exists")
    @WebMethod(exclude = false)
    public Boolean checkAttributeName(@WebParam(name = "graphId") Long graphId,
                                      @WebParam(name = "attributeNameId") String attributeNameId)
            throws WebserviceException {
        try{
            return engine.checkAttributeName(graphId, attributeNameId);
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Checks if a {@link WSConceptClass} for a given ID exists in the Graph.
     *
     * @param graphId        the ID of the Graph
     * @param conceptClassId the ID of the {@link WSConceptClass}
     * @return true if the {@link WSConceptClass} exists, false otherwise
     * @throws WebserviceException
     */
    @WebResult(name = "exists")
    @WebMethod(exclude = false)
    public Boolean checkConceptClass(@WebParam(name = "graphId") Long graphId,
                                     @WebParam(name = "conceptClassId") String conceptClassId)
            throws WebserviceException {
        try{
            return engine.checkConceptClass(graphId, conceptClassId);
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Checks if a {@link WSDataSource} for a given ID exists in the Graph.
     *
     * @param graphId the ID of the Graph
     * @param dataSourceId    the ID of the {@link WSDataSource}
     * @return true if the {@link WSDataSource} exists, false otherwise
     * @throws WebserviceException
     */
    @WebResult(name = "exists")
    @WebMethod(exclude = false)
    public Boolean checkDataSource(@WebParam(name = "graphId") Long graphId,
                           @WebParam(name = "dataSourceId") String dataSourceId)
            throws WebserviceException {
        try{
            return engine.checkDataSource(graphId, dataSourceId);
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Checks if an {@link WSEvidenceType} for a given ID exists in the Graph.
     *
     * @param graphId        the ID of the Graph
     * @param evidenceTypeId the ID of the {@link WSEvidenceType} being added.
     * @return true if the {@link WSEvidenceType} exists, false otherwise
     * @throws WebserviceException
     */
    @WebResult(name = "exists")
    @WebMethod(exclude = false)
    public Boolean checkEvidenceType(@WebParam(name = "graphId") Long graphId,
                                     @WebParam(name = "evidenceTypeId") String evidenceTypeId)
            throws WebserviceException {
        try{
            return engine.checkEvidenceType(graphId, evidenceTypeId);
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Checks if a {@link WSRelationType} for a given ID exists in the Graph.
     *
     * @param graphId        the ID of the Graph
     * @param relationTypeId the ID of the {@link WSRelationType}
     * @return true if the {@link WSRelationType} exists, false otherwise
     * @throws WebserviceException
     */
    @WebResult(name = "exists")
    @WebMethod(exclude = false)
    public Boolean checkRelationType(@WebParam(name = "graphId") Long graphId,
                                     @WebParam(name = "relationTypeId") String relationTypeId)
            throws WebserviceException {
        try{
            return engine.checkRelationType(graphId, relationTypeId);
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Checks if a {@link WSUnit} for a given ID exists in the specified graph.
     *
     * @param graphId the ID of the Graph
     * @param unitId  the ID of the {@link WSUnit}
     * @return true if the {@link WSUnit} exists, false otherwise
     * @throws WebserviceException
     */
    @WebResult(name = "exists")
    @WebMethod(exclude = false)
    public Boolean checkUnit(@WebParam(name = "graphId") Long graphId,
                             @WebParam(name = "unitId") String unitId)
            throws WebserviceException {
        try{
            return engine.checkUnit(graphId, unitId);
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Creates a new {@link WSAttributeName} and adds it to the Graph.
     *
     * @param graphId          the ID of the Graph
     * @param atttributeNameId the ID of the new {@link WSAttributeName}
     * @param fullname         the name of the {@link WSAttributeName} (Optional)
     * @param description      the description of the {@link WSAttributeName} (Optional)
     * @param unitId           the ID of the {@link WSUnit} (Optional)
     * @param datatype         the datatype of the {@link WSAttributeName}. Must be the name
     *                         of a Java class.
     * @param specialisationOfAttributeNameId
     *                         the ID of the {@link WSAttributeName} that new
     *                         {@link WSAttributeName} is a specialization of (Optional)
     * @return the ID of the new {@link WSAttributeName}
     * @throws WebserviceException
     */
    @WebResult(name = "attributeNameId")
    @WebMethod(exclude = false)
    public String createAttributeName(
            @WebParam(name = "graphId") Long graphId,
            @WebParam(name = "atttributeNameId") String atttributeNameId,
            @WebParam(name = "fullname") String fullname,
            @WebParam(name = "description") String description,
            @WebParam(name = "unitId") String unitId,
            @WebParam(name = "datatype") String datatype,
            @WebParam(name = "specialisationOfAttributeNameId")
            String specialisationOfAttributeNameId)
            throws WebserviceException {
        try{
            return engine.createAttributeName(graphId, atttributeNameId, fullname, description,
                    unitId, datatype, specialisationOfAttributeNameId);
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Creates a new {@link WSConceptClass} and adds it to the Graph.
     *
     * @param graphId                        the ID of the Graph
     * @param conceptClassId                 the ID of the {@link WSConceptClass}
     * @param fullname                       the name of the {@link WSConceptClass} (Optional)
     * @param description                    the description of the {@link WSConceptClass} (Optional)
     * @param specialisationOfConceptClassId the ID of the {@link WSConceptClass} that this
     *                                       {@link WSConceptClass} is a specialization of (Optional)
     * @return the ID of the new {@link WSConceptClass}
     * @throws WebserviceException
     */
    @WebResult(name = "conceptClassId")
    @WebMethod(exclude = false)
    public String createConceptClass(
            @WebParam(name = "graphId") Long graphId,
            @WebParam(name = "conceptClassId") String conceptClassId,
            @WebParam(name = "fullname") String fullname,
            @WebParam(name = "description") String description,
            @WebParam(name = "specialisationOfConceptClassId")
            String specialisationOfConceptClassId)
            throws WebserviceException {
        try{
            return engine.createConceptClass(graphId, conceptClassId, fullname,
                    description, specialisationOfConceptClassId);
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Creates a new {@link WSDataSource} and adds it to the Graph.
     *
     * @param graphId     the ID of the Graph
     * @param dataSourceId        the ID of the {@link WSDataSource}
     * @param fullname    the name of the {@link WSDataSource} (Optional)
     * @param description the description of the {@link WSDataSource} (Optional)
     * @return the ID of the new {@link WSDataSource}
     * @throws WebserviceException
     */
    @WebResult(name = "dataSourceId")
    @WebMethod(exclude = false)
    public String createDataSource(@WebParam(name = "graphId") Long graphId,
                           @WebParam(name = "dataSourceId") String dataSourceId,
                           @WebParam(name = "fullname") String fullname,
                           @WebParam(name = "description") String description)
            throws WebserviceException {
        try{
            return engine.createDataSource(graphId, dataSourceId, fullname, description);
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Creates a new {@link WSEvidenceType} and adds it to the Graph.
     *
     * @param graphId        the ID of the Graph
     * @param evidenceTypeId the ID of the {@link WSEvidenceType} being added.
     * @param fullname       the name of the {@link WSEvidenceType} (Optional)
     * @param description    the description of the {@link WSEvidenceType} (Optional)
     * @return the ID of the new {@link WSEvidenceType}
     * @throws WebserviceException
     */
    @WebResult(name = "evidenceTypeId")
    @WebMethod(exclude = false)
    public String createEvidenceType(@WebParam(name = "graphId") Long graphId,
                                     @WebParam(name = "evidenceTypeId") String evidenceTypeId,
                                     @WebParam(name = "fullname") String fullname,
                                     @WebParam(name = "description") String description)
            throws WebserviceException {
        try{
            return engine.createEvidenceType(graphId, evidenceTypeId, fullname, description);
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Creates a new {@link WSRelationType} and adds it to the Graph.
     *
     * @param graphId                        the ID of the Graph
     * @param relationTypeId                 the ID of the {@link WSRelationType}
     * @param fullname                       the name of the {@link WSRelationType} (Optional)
     * @param description                    the description of the {@link WSRelationType} (Optional)
     * @param inverseName                    the inverse name of the {@link WSRelationType} (Optional)
     * @param isAntisymmetric                whether the {@link WSRelationType} is antisymmetric. The
     *                                       default is false. (Optional)
     * @param isReflexive                    whether the {@link WSRelationType} is reflexive. The default
     *                                       is false. (Optional)
     * @param isSymmetric                    whether the {@link WSRelationType} is symmetric. The default
     *                                       is false. (Optional)
     * @param isTransitiv                    whether the {@link WSRelationType} is transitive. The default
     *                                       is false. (Optional)
     * @param specialisationOfRelationTypeId id the ID of the {@link WSRelationType} that this
     *                                       {@link WSRelationType} is a specialization of (Optional)
     * @return the ID of the new {@link WSRelationType}
     * @throws WebserviceException
     */
    @WebResult(name = "relationTypeId")
    @WebMethod(exclude = false)
    public String createRelationType(
            @WebParam(name = "graphId") Long graphId,
            @WebParam(name = "relationTypeId") String relationTypeId,
            @WebParam(name = "fullname") String fullname,
            @WebParam(name = "description") String description,
            @WebParam(name = "inverseName") String inverseName,
            @WebParam(name = "isAntisymmetric") Boolean isAntisymmetric,
            @WebParam(name = "isReflexive") Boolean isReflexive,
            @WebParam(name = "isSymmetric") Boolean isSymmetric,
            @WebParam(name = "isTransitiv") Boolean isTransitiv,
            @WebParam(name = "specialisationOfRelationTypeId") String specialisationOfRelationTypeId)
            throws WebserviceException {
        try{
            return engine.createRelationType(graphId, relationTypeId, fullname, description,
                    inverseName, isAntisymmetric, isReflexive, isSymmetric,
                    isTransitiv, specialisationOfRelationTypeId);
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Creates a memory graph with the name  provide or "test" plus the current time.
     * <p/>
     * WARNING: Users of this method are responsible to both saving and deleting the graph.
     * <p/>
     * Note: Does not load the default Ondex MetaData.
     * Use initialiseMetaData (Long graphId) to load the MetaData.

     * @param name (optional) The name to assign to the graph.
     * @return Id of the test graph
     * @throws WebserviceException
     */
    @WebResult(name = "graphId")
    @WebMethod(exclude = false)
    public long createMemoryGraph(@WebParam(name = "name") String name) throws WebserviceException {
        try{
            return engine.createMemoryGraph(name);
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Creates a graph with the name "test" plus the current time.   *
     * <p/>
     * Note: Does not load the default Ondex MetaData.
     * Use initialiseMetaData (Long graphId) to load the MetaData.
     * @return Id of the test graph
     * @throws WebserviceException
     */
    @WebResult(name = "graphId")
    @WebMethod(exclude = false)
    public long createTestGraph() throws WebserviceException  {
        try{
            return engine.createTestGraph();
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Creates a new {@link WSUnit} and adds it to the Graph.
     *
     * @param graphId     the ID of the Graph
     * @param unitId      the ID of the {@link WSUnit}
     * @param fullname    the name of the {@link WSUnit} (Optional)
     * @param description the description of the {@link WSUnit} (Optional)
     * @return the ID of the new {@link WSUnit}
     * @throws WebserviceException
     */
    @WebResult(name = "unitId")
    @WebMethod(exclude = false)
    public String createUnit(@WebParam(name = "graphId") Long graphId,
                             @WebParam(name = "unitId") String unitId,
                             @WebParam(name = "fullname") String fullname,
                             @WebParam(name = "description") String description)
            throws WebserviceException {
        try{
            return engine.createUnit(graphId, unitId, fullname, description);
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Removes an {@link WSAttributeName} from the Graph.
     *
     * @param graphId         the ID of the Graph
     * @param attributeNameId the ID of the {@link WSAttributeName} to remove
     * @return ""\Successfully removed+ X\" or "X not found"
     *         where X is the id of the item being deleted.
     * @throws WebserviceException
     */
    @WebMethod(exclude = false)
    @WebResult(name = "success")
    public String deleteAttributeName(@WebParam(name = "graphId") Long graphId,
                                      @WebParam(name = "attributeNameId") String attributeNameId)
            throws WebserviceException {
        try{
            boolean result = engine.deleteAttributeName(graphId, attributeNameId);
            if (result) {
                return "Successfully removed " + attributeNameId;
            } else {
                return attributeNameId + "not found.";
            }
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Removes a {@link WSConceptClass} from the Graph.
     *
     * @param graphId        the ID of the Graph
     * @param ConceptClassId the ID of the {@link WSConceptClass} to remove
     * @return ""\Successfully removed+ X\" or "X not found"
     *         where X is the id of the item being deleted.
     * @throws WebserviceException
     */
    @WebMethod(exclude = false)
    @WebResult(name = "success")
    public String deleteConceptClass(@WebParam(name = "graphId") Long graphId,
                                     @WebParam(name = "ConceptClassId") String ConceptClassId)
            throws WebserviceException {
        try{
            boolean result = engine.deleteConceptClass(graphId, ConceptClassId);
            if (result) {
                return "Successfully removed " + ConceptClassId;
            } else {
                return ConceptClassId + "not found.";
            }
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Removes a {@link WSDataSource} from the Graph.
     *
     * @param graphId the ID of the Graph
     * @param dataSourceId    the ID of the {@link WSDataSource}
     * @return ""\Successfully removed+ X\" or "X not found"
     *         where X is the id of the item being deleted.
     * @throws WebserviceException
     */
    @WebMethod(exclude = false)
    @WebResult(name = "success")
    public String deleteDataSource(@WebParam(name = "graphId") Long graphId,
                           @WebParam(name = "dataSourceId") String dataSourceId)
            throws WebserviceException {
        try{
            boolean result = engine.deleteDataSource(graphId, dataSourceId);
            if (result) {
                return "Successfully removed " + dataSourceId;
            } else {
                return dataSourceId + "not found.";
            }
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Removes an {@link WSEvidenceType} from the Graph.
     *
     * @param graphId        the ID of the Graph
     * @param evidenceTypeId the ID of the {@link WSEvidenceType} being added.
     * @return ""\Successfully removed+ X\" or "X not found"
     *         where X is the id of the item being deleted.
     * @throws WebserviceException
     */
    @WebMethod(exclude = false)
    @WebResult(name = "success")
    public String deleteEvidenceType(@WebParam(name = "graphId") Long graphId,
                                     @WebParam(name = "evidenceTypeId") String evidenceTypeId)
            throws WebserviceException {
        try{
            boolean result = engine.deleteEvidenceType(graphId, evidenceTypeId);
            if (result) {
                return "Successfully removed " + evidenceTypeId;
            } else {
                return evidenceTypeId + "not found.";
            }
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Removes a {@link WSRelationType} from the Graph.
     *
     * @param graphId        the ID of the Graph
     * @param relationTypeId the ID of the {@link WSRelationType}
     * @return ""\Successfully removed+ X\" or "X not found"
     *         where X is the id of the item being deleted.
     * @throws WebserviceException
     */
    @WebMethod(exclude = false)
    @WebResult(name = "success")
    public String deleteRelationType(@WebParam(name = "graphId") Long graphId,
                                     @WebParam(name = "relationTypeId") String relationTypeId)
            throws WebserviceException {
        try{
            boolean result = engine.deleteRelationType(graphId, relationTypeId);
            if (result) {
                return "Successfully removed " + relationTypeId;
            } else {
                return relationTypeId + "not found.";
            }
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Removes a {@link WSUnit} from the Graph.
     *
     * @param graphId the ID of the Graph
     * @param unitId  the ID of the {@link WSUnit} to remove
     * @return ""\Successfully removed+ X\" or "X not found"
     *         where X is the id of the item being deleted.
     * @throws WebserviceException
     */
    @WebMethod(exclude = false)
    @WebResult(name = "success")
    public String deleteUnit(@WebParam(name = "graphId") Long graphId,
                             @WebParam(name = "unitId") String unitId)
            throws WebserviceException {
        try{
            boolean result = engine.deleteUnit(graphId, unitId);
            if (result) {
                return "Successfully removed " + unitId;
            } else {
                return unitId + "not found.";
            }
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Returns the {@link WSAttributeName} with the specified ID.
     *
     * @param graphId         the ID of the Graph
     * @param attributeNameId the ID of the {@link WSAttributeName} to return
     * @return the {@link WSAttributeName} with the specified ID
     * @throws WebserviceException
     */
    @WebResult(name = "attributeName")
    @WebMethod(exclude = false)
    public WSAttributeName getAttributeName(
            @WebParam(name = "graphId") Long graphId,
            @WebParam(name = "attributeNameId") String attributeNameId)
            throws WebserviceException {
        try{
            return new WSAttributeName(engine.getAttributeName(graphId, attributeNameId));
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Returns all the {@link WSAttributeName}s in the Graph.
     *
     * @param graphId the ID of the Graph
     * @return all {@link WSAttributeName}s in the Graph
     * @throws WebserviceException
     */
    @WebResult(name = "attributeNames")
    @WebMethod(exclude = false)
    public List<WSAttributeName> getAttributeNames(
            @WebParam(name = "graphId") Long graphId)
            throws WebserviceException {
        try{
            return engine.getAttributeNames(graphId);
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Returns the {@link WSConceptClass} with the specified ID.
     *
     * @param graphId        the ID of the Graph
     * @param conceptClassId the ID of the {@link WSConceptClass} to return
     * @return the {@link WSConceptClass} with the specified ID
     * @throws WebserviceException
     */
    @WebResult(name = "conceptClass")
    @WebMethod(exclude = false)
    public WSConceptClass getConceptClass(
            @WebParam(name = "graphId") Long graphId,
            @WebParam(name = "conceptClassId") String conceptClassId)
            throws WebserviceException {
        try{
            return new WSConceptClass(engine.getConceptClass(graphId, conceptClassId));
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Returns all the {@link WSConceptClass}es in the Graph.
     *
     * @param graphId the ID of the Graph
     * @return all the {@link WSConceptClass}es in the Graph
     * @throws WebserviceException
     */
    @WebResult(name = "conceptClasses")
    @WebMethod(exclude = false)
    public List<WSConceptClass> getConceptClasses(
            @WebParam(name = "graphId") Long graphId)
            throws WebserviceException {
        try{
            return engine.getConceptClasses(graphId);
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Returns the number of {@link WSConceptClass}es in the Graph.
     *
     * @param graphId the ID of the Graph
     * @return The number of {@link WSConceptClass}es in the Graph
     * @throws WebserviceException
     */
    @WebResult(name = "number")
    @WebMethod(exclude = false)
    public int getNumberOfConceptClasses(
            @WebParam(name = "graphId") Long graphId)
            throws WebserviceException {
        try{
            return engine.getNumberOfConceptClasses(graphId);
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

     /**
     * Returns the {@link WSDataSource} with the specified ID.
     *
     * @param graphId the ID of the Graph
     * @param dataSourceId    the ID of the {@link WSDataSource}
     * @return the DataSource with the specified ID
     * @throws WebserviceException
     */
    @WebResult(name = "dataSource")
    @WebMethod(exclude = false)
    public WSDataSource getDataSource(@WebParam(name = "graphId") Long graphId,
                      @WebParam(name = "dataSourceId") String dataSourceId)
            throws WebserviceException {
        try{
            return new WSDataSource(engine.getDataSource(graphId, dataSourceId));
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

     /**
     * Returns all the {@link WSDataSource}s in the Graph.
     *
     * @param graphId the ID of the Graph
     * @return all the {@link WSDataSource}s in the Graph
     * @throws WebserviceException
     */
    @WebResult(name = "dataSources")
    @WebMethod(exclude = false)
    public List<WSDataSource> getDataSources(@WebParam(name = "graphId") Long graphId)
            throws WebserviceException {
        try{
            return engine.getDataSources(graphId);
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Returns the number of {@link WSDataSource}s in the Graph.
     *
     * @param graphId the ID of the Graph
     * @return The number {@link WSDataSource}s in the Graph
     * @throws WebserviceException
     */
    @WebResult(name = "number")
    @WebMethod(exclude = false)
    public int getNumberOfDataSources(Long graphId) throws WebserviceException {
        try {
            return engine.getNumberOfDataSources(graphId);
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

     /**
     * Returns the {@link WSEvidenceType} with the specified ID.
     *
     * @param graphId        the ID of the Graph
     * @param evidenceTypeId the ID of the {@link WSEvidenceType} being added.
     * @return the {@link WSEvidenceType} with the specified ID
     * @throws WebserviceException
     */
    @WebResult(name = "evidenceType")
    @WebMethod(exclude = false)
    public WSEvidenceType getEvidenceType(
            @WebParam(name = "graphId") Long graphId,
            @WebParam(name = "evidenceTypeId") String evidenceTypeId)
            throws WebserviceException {
        try{
            return new WSEvidenceType(engine.getEvidenceType(graphId, evidenceTypeId));
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Returns all the {@link WSEvidenceType}s in the Graph.
     *
     * @param graphId the ID of the Graph
     * @return all the {@link WSEvidenceType}s in the Graph
     * @throws WebserviceException
     */
    @WebResult(name = "evidenceTypes")
    @WebMethod(exclude = false)
    public List<WSEvidenceType> getEvidenceTypes(
            @WebParam(name = "graphId") Long graphId)
            throws WebserviceException {
        try{
            logger.info("getEvidenceTypes called with " + graphId);
            return engine.getEvidenceTypes(graphId);
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Returns the number of {@link WSEvidenceType}s in the Graph.
     *
     * @param graphId the ID of the Graph
     * @return The number of {@link WSEvidenceType}s in the Graph
     * @throws WebserviceException
     */
    @WebResult(name = "number")
    @WebMethod(exclude = false)
    public int getNumberOfEvidenceTypes(
            @WebParam(name = "graphId") Long graphId)
            throws WebserviceException {
        try{
            return engine.getNumberOfEvidenceTypes(graphId);
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Returns the {@link WSRelationType} with the specified ID.
     *
     * @param graphId        the ID of the Graph
     * @param relationTypeId the ID of the {@link WSRelationType}
     * @param relationTypeId
     * @return the {@link WSRelationType} with the specified ID
     * @throws WebserviceException
     */
    @WebResult(name = "relationType")
    @WebMethod(exclude = false)
    public WSRelationType getRelationType(
            @WebParam(name = "graphId") Long graphId,
            @WebParam(name = "relationTypeId") String relationTypeId)
            throws WebserviceException {
        try{
            return engine.getRelationType(graphId, relationTypeId);
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Returns all the {@link WSRelationType}s in the Graph.
     *
     * @param graphId the ID of the Graph
     * @return all the {@link WSRelationType}s in the Graph
     * @throws WebserviceException
     */
    @WebResult(name = "relationTypes")
    @WebMethod(exclude = false)
    public List<WSRelationType> getRelationTypes(
            @WebParam(name = "graphId") Long graphId)
            throws WebserviceException {
        try{
            return engine.getRelationTypes(graphId);
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
   }

    /**
     * Returns the number of {@link WSRelationType}s in the Graph.
     *
     * @param graphId the ID of the Graph
     * @return The number of {@link WSRelationType}s in the Graph
     * @throws WebserviceException
     */
    @WebResult(name = "number")
    @WebMethod(exclude = false)
    public int getNumberOfRelationTypes(
            @WebParam(name = "graphId") Long graphId)
            throws WebserviceException {
        try{
            return engine.getNumberOfRelationTypes(graphId);
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
   }

    /**
     * Returns the {@link WSUnit} with the specified ID.
     *
     * @param graphId the ID of the Graph
     * @param unitId  the ID of {@link WSUnit}
     * @return the {@link WSUnit} with the specified ID
     * @throws WebserviceException
     */
    @WebResult(name = "unit")
    @WebMethod(exclude = false)
    public WSUnit getUnit(@WebParam(name = "graphId") Long graphId,
                          @WebParam(name = "unitId") String unitId)
            throws WebserviceException {
        try{
            return new WSUnit(engine.getUnit(graphId, unitId));
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Returns all the {@link WSUnit}s in the Graph.
     *
     * @param graphId the ID of the Graph
     * @return all the {@link WSUnit}s in the Graph
     * @throws WebserviceException
     */
    @WebResult(name = "units")
    @WebMethod(exclude = false)
    public List<WSUnit> getUnits(@WebParam(name = "graphId") Long graphId)
            throws WebserviceException {
        try{
            return engine.getUnits(graphId);
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    //**** ONDEXCncept Methods

    /**
     * Creates a new {@link WSConceptAccession} and adds it to the
     * {@link WSConcept}.
     *
     * @param graphId       the ID of the Graph
     * @param conceptId     the ID of the {@link WSConcept} to add the ConceptAccession to
     * @param accession     the accession of the {@link WSConceptAccession}
     * @param elementOfDataSourceId the ID of the {@link WSDataSource} for the elementOf property
     * @param isAmbiguous   whether the {@link WSConceptAccession} is ambiguous. The
     *                      default is true. (Optional)
     * @return the accession of the {@link WSConceptAccession}
     * @throws WebserviceException
     */
    @WebResult(name = "accession")
    @WebMethod(exclude = false)
    public String createConceptAccession(
            @WebParam(name = "graphId") Long graphId,
            @WebParam(name = "conceptId") Integer conceptId,
            @WebParam(name = "accession") String accession,
            @WebParam(name = "elementOfDataSourceId") String elementOfDataSourceId,
            @WebParam(name = "isAmbiguous") Boolean isAmbiguous)
            throws WebserviceException {
        try{
            return engine.createConceptAccession(graphId, conceptId, accession,
                    elementOfDataSourceId, isAmbiguous);
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Creates a new {@link WSAttribute} and adds it to the {@link WSConcept}.
     *
     * @param graphId         the ID of the Graph
     * @param conceptId       the ID of the {@link WSConcept} to add the Attribute to
     * @param attributeNameId the {@link WSAttributeName} of the Attribute
     * @param value           the value of the Attribute
     * @param doIndex         setting for the doIndex flag. the defaults is false.
     * @return the ID of the {@link WSAttributeName}
     * @throws WebserviceException
     */
    @WebResult(name = "attributeNameId")
    @WebMethod(exclude = false)
    public String createConceptAttribute(@WebParam(name = "graphId") Long graphId,
                                   @WebParam(name = "conceptId") Integer conceptId,
                                   @WebParam(name = "attributeNameId") String attributeNameId,
                                   @WebParam(name = "value") Object value,
                                   @WebParam(name = "doIndex") boolean doIndex)
            throws WebserviceException {
        try{
            return engine.createConceptAttribute(graphId, conceptId, attributeNameId,
                    value, doIndex);
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Creates a new {@link WSConceptName} and adds it to the {@link WSConcept}.
     *
     * @param graphId     the ID of the Graph
     * @param conceptId   the ID of the {@link WSConcept} to add the ConceptName to
     * @param name        the name of the {@link WSConceptName}
     * @param isPreferred whether the {@link WSConceptName} is preferred. The default is
     *                    false. (Optional)
     * @return the name of the {@link WSConceptName}
     * @throws GraphNotFoundException   No Graph found with the given ID.
     * @throws WebserviceException
     */
    @WebResult(name = "name")
    @WebMethod(exclude = false)
    public String createConceptName(@WebParam(name = "graphId") Long graphId,
                                    @WebParam(name = "conceptId") Integer conceptId,
                                    @WebParam(name = "name") String name,
                                    @WebParam(name = "isPreferred") Boolean isPreferred)
            throws WebserviceException {
        try{
            return engine.createConceptName(graphId, conceptId, name, isPreferred);
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Removes a {@link WSConceptAccession} from the {@link WSConcept}.
     *
     * @param graphId       the ID of the Graph
     * @param conceptId     the ID of the {@link WSConcept}
     * @param accession     the accession of the {@link WSConceptAccession}
     * @param elementOfDataSourceId the ID of the {@link WSDataSource} for the elementOf property
     * @return ""\Successfully removed+ X\" or "X not found"
     *         where X is the id of the item being deleted.
     * @throws WebserviceException
     */
    @WebMethod(exclude = false)
    @WebResult(name = "success")
    public String deleteConceptAccession(
            @WebParam(name = "graphId") Long graphId,
            @WebParam(name = "conceptId") Integer conceptId,
            @WebParam(name = "accession") String accession,
            @WebParam(name = "elementOfDataSourceId") String elementOfDataSourceId)
            throws WebserviceException {
        try{
            boolean result = engine.deleteConceptAccession(graphId, conceptId, accession,
                elementOfDataSourceId);
            if (result) {
                return "Successfully removed " + elementOfDataSourceId;
            } else {
                return elementOfDataSourceId + "not found.";
            }
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Removes a {@link WSAttribute} from the {@link WSConcept}.
     *
     * @param graphId         the ID of the Graph
     * @param conceptId       the ID of the {@link WSConcept}
     * @param attributeNameId the ID of the {@link WSAttributeName}
     * @return ""\Successfully removed+ X\" or "X not found"
     *         where X is the id of the item being deleted.
     * @throws WebserviceException
     */
    @WebMethod(exclude = false)
    @WebResult(name = "success")
    public String deleteConceptAttribute(@WebParam(name = "graphId") Long graphId,
                                   @WebParam(name = "conceptId") Integer conceptId,
                                   @WebParam(name = "attributeNameId") String attributeNameId)
            throws WebserviceException {
        try{
            boolean result = engine.deleteConceptAttribute(graphId, conceptId, attributeNameId);
            if (result) {
                return "Successfully removed " + attributeNameId;
            } else {
                return attributeNameId + "not found.";
            }
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Removes a {@link WSConceptName} from the {@link WSConcept}.
     *
     * @param graphId   the ID of the Graph
     * @param conceptId the ID of the {@link WSConcept}
     * @param name      the name of the {@link WSConceptName}
     * @return ""\Successfully removed+ X\" or "X not found"
     *         where X is the id of the item being deleted.
     * @throws WebserviceException
     */
    @WebMethod(exclude = false)
    @WebResult(name = "success")
    public String deleteConceptName(@WebParam(name = "graphId") Long graphId,
                                    @WebParam(name = "conceptId") Integer conceptId,
                                    @WebParam(name = "name") String name)
            throws WebserviceException {
        try{
            boolean result = engine.deleteConceptName(graphId, conceptId, name);
            if (result) {
                return "Successfully removed " + name;
            } else {
                return name + "not found.";
            }
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Returns the annotation of this instance of {@link WSConcept}.
     *
     * @param graphId   the ID of the Graph
     * @param conceptId the ID of the {@link WSConcept}
     * @return annotation String
     * @throws WebserviceException
     */
    @WebMethod(exclude = false)
    @WebResult(name = "success")
    public String getAnnotation(@WebParam(name = "graphId") Long graphId,
                                @WebParam(name = "conceptId") Integer conceptId)
            throws WebserviceException {
        try{
            return engine.getAnnotation(graphId, conceptId);
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Returns the {@link WSConceptAccession} with the specified accession and
     * {@link WSDataSource} from the {@link WSConcept}.
     *
     * @param graphId       the ID of the Graph
     * @param conceptId     the ID of the {@link WSConcept}
     * @param accession     the accession of the {@link WSConceptAccession}
     * @param elementOfDataSourceId the ID of the {@link WSDataSource} for the elementOf property
     * @return the {@link WSConceptAccession} with the specified accession and
     *         DataSource
     * @throws WebserviceException
     */
    @WebResult(name = "conceptAccession")
    @WebMethod(exclude = false)
    public WSConceptAccession getConceptAccession(
            @WebParam(name = "graphId") Long graphId,
            @WebParam(name = "conceptId") Integer conceptId,
            @WebParam(name = "accession") String accession,
            @WebParam(name = "elementOfDataSourceId") String elementOfDataSourceId)
            throws WebserviceException {
        try {
            return new WSConceptAccession(engine.getConceptAccession(graphId, conceptId, accession,
                    elementOfDataSourceId));
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Returns all the {@link WSConceptAccession} in the specified
     * {@link WSConcept}.
     *
     * @param graphId   the ID of the Graph
     * @param conceptId the ID of the {@link WSConcept}
     * @return all the {@link WSConceptAccession} in the specified
     *         {@link WSConcept}
     * @throws WebserviceException
     */
    @WebResult(name = "conceptAccessions")
    @WebMethod(exclude = false)
    public List<WSConceptAccession> getAllConceptAccessions(
            @WebParam(name = "graphId") Long graphId,
            @WebParam(name = "conceptId") Integer conceptId)
            throws WebserviceException {
        try{
            return engine.getConceptAccessions(graphId, conceptId);
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Returns all the {@link WSAttribute} in the specified {@link WSConcept}.
     *
     * @param graphId   the ID of the Graph
     * @param conceptId the ID of the {@link WSConcept}
     * @return all the {@link WSAttribute} in the specified {@link WSConcept}
     * @throws WebserviceException
     */
    @WebResult(name = "conceptAttributes")
    @WebMethod(exclude = false)
    public List<WSAttribute> getConceptAttributes(
            @WebParam(name = "graphId") Long graphId,
            @WebParam(name = "conceptId") Integer conceptId)
            throws WebserviceException {
        try{
            Set<Attribute> Attributes = engine.getAttributes(graphId, conceptId);
            List<WSAttribute> list = new ArrayList<WSAttribute>();
            for (Attribute attribute : Attributes) {
                list.add(new WSAttribute(attribute));
            }
            return list;
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Returns the {@link WSAttribute} with the specified
     * {@link WSAttributeName} from the {@link WSConcept}.
     *
     * @param graphId         the ID of the Graph
     * @param conceptId       the ID of the {@link WSConcept}
     * @param attributeNameId the ID of the {@link WSAttributeName}
     * @return the {@link WSAttribute} with the specified
     *         {@link WSAttributeName}
     * @throws WebserviceException
     */
    @WebResult(name = "conceptAttribute")
    @WebMethod(exclude = false)
    public WSAttribute getConceptAttribute(@WebParam(name = "graphId") Long graphId,
                               @WebParam(name = "conceptId") Integer conceptId,
                               @WebParam(name = "attributeNameId") String attributeNameId)
            throws WebserviceException {
        try{
            return new WSAttribute(engine.getConceptAttribute(graphId, conceptId, attributeNameId));
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Returns the {@link WSConceptName} with the specified name from the
     * {@link WSConcept}.
     *
     * @param graphId   the ID of the Graph
     * @param conceptId the ID of the {@link WSConcept}
     * @return the {@link WSConceptName} with the specified name
     * @throws WebserviceException
     */
    @WebResult(name = "conceptName")
    @WebMethod(exclude = false)
    public WSConceptName getConceptName(
            @WebParam(name = "graphId") Long graphId,
            @WebParam(name = "conceptId") Integer conceptId)
            throws WebserviceException {
        try{
            return new WSConceptName(engine.getConceptName(graphId, conceptId));
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Returns the {@link WSConceptName} with the specified name from the
     * {@link WSConcept}.
     *
     * @param graphId   the ID of the Graph
     * @param conceptId the ID of the {@link WSConcept}
     * @param name      the name of the {@link WSConceptName}
     * @return the {@link WSConceptName} with the specified name
     * @throws WebserviceException
     */
    @WebResult(name = "conceptName")
    @WebMethod(exclude = false)
    public WSConceptName getConceptNameWithName(
            @WebParam(name = "graphId") Long graphId,
            @WebParam(name = "conceptId") Integer conceptId,
            @WebParam(name = "name") String name)
            throws WebserviceException {
        try{
            return engine.getConceptNameWithName(graphId, conceptId, name);
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Returns all the {@link WSConceptName} in the specified {@link WSConcept}.
     *
     * @param graphId   the ID of the Graph
     * @param conceptId the ID of the {@link WSConcept}
     * @return all the {@link WSConceptName} in the specified {@link WSConcept}
     * @throws WebserviceException
     */
    @WebResult(name = "conceptNames")
    @WebMethod(exclude = false)
    public List<WSConceptName> getConceptNames(
            @WebParam(name = "graphId") Long graphId,
            @WebParam(name = "conceptId") Integer conceptId)
            throws WebserviceException {
        try{
            return engine.getConceptNames(graphId, conceptId);
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Returns the description of this instance of {@link WSConcept}.
     *
     * @param graphId   the ID of the Graph
     * @param conceptId the ID of the {@link WSConcept}
     * @return the {@link WSConceptName} with the specified name
     * @throws WebserviceException
     */
    @WebResult(name = "description")
    @WebMethod(exclude = false)
    public String getDescription(
            @WebParam(name = "graphId") Long graphId,
            @WebParam(name = "conceptId") Integer conceptId)
            throws WebserviceException {
        try{
            return engine.getDescription(graphId, conceptId);
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Returns the {@link WSDataSource}, which this {@link WSConcept} belongs to.
     *
     * @param graphId   the ID of the Graph
     * @param conceptId the ID of the {@link WSConcept}
     * @return the {@link WSConceptName} with the specified name
     * @throws WebserviceException
     */
    @WebResult(name = "elementOf")
    @WebMethod(exclude = false)
    public WSDataSource getElementOf(
            @WebParam(name = "graphId") Long graphId,
            @WebParam(name = "conceptId") Integer conceptId)
            throws WebserviceException {
        try{
            return engine.getElementOf(graphId, conceptId);
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Returns the {@link WSConceptClass} this {@link WSConcept} belongs to.
     *
     * @param graphId   the ID of the Graph
     * @param conceptId the ID of the {@link WSConcept}
     * @return the {@link WSConceptName} with the specified name
     * @throws WebserviceException
     */
    @WebResult(name = "typeOf")
    @WebMethod(exclude = false)
    public WSConceptClass getOfTypeConcept(
            @WebParam(name = "graphId") Long graphId,
            @WebParam(name = "conceptId") Integer conceptId)
            throws WebserviceException {
        try{
            return engine.getOfTypeConcept(graphId, conceptId);
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }


    /**
     * Returns a list of all OXL files saved on the server.
     *
     * @return all the file names.
     * @throws WebserviceException
     */
    @WebResult(name = "files")
    @WebMethod(exclude = false)
    public List<String> getOXLFilesAvailable() throws WebserviceException {
        try{
            return engine.getOXLFilesAvailable();
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Returns the parser id of this instance of {@link WSConcept}.
     *
     * @param graphId   the ID of the Graph
     * @param conceptId the ID of the {@link WSConcept}
     * @return the {@link WSConceptName} with the specified name
     * @throws WebserviceException
     */
    @WebResult(name = "pid")
    @WebMethod(exclude = false)
    public String getPID(
            @WebParam(name = "graphId") Long graphId,
            @WebParam(name = "conceptId") Integer conceptId)
            throws WebserviceException {
        try{
            return engine.getPID(graphId, conceptId);
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Returns whether this {@link WSConcept} is inherited from the given {@link WSConceptClass}.
     * <p/>
     * This is the case when its ofType {@link WSConceptClass}'
     * either equals the given {@link WSConceptClass} or is a transitive
     * specialisation of the given {@link WSConceptClass}.
     *
     * @param graphId        the ID of the Graph
     * @param conceptId      the ID of the {@link WSConcept}
     * @param conceptClassId the ID of the {@link WSConceptClass} for the ofType property
     * @return whether the above holds.
     * @throws WebserviceException
     */
    @WebResult(name = "inheritedFrom")
    @WebMethod(exclude = false)
    public boolean inheritedFromConcept(
            @WebParam(name = "graphId") Long graphId,
            @WebParam(name = "conceptId") Integer conceptId,
            @WebParam(name = "conceptClassId") String conceptClassId)
            throws WebserviceException {
        try{
            return engine.inheritedFromConcept(graphId, conceptId, conceptClassId);
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Sets the annotation of this instance of {@link WSConcept}.
     *
     * @param graphId    the ID of the Graph
     * @param conceptId  the ID of the {@link WSConcept}
     * @param annotation the annotation to set.
     * @return String text confirming success or throws an exception.
     * @throws WebserviceException
     */
    @WebMethod(exclude = false)
    @WebResult(name = "success")
    public String setAnnotation(
            @WebParam(name = "graphId") Long graphId,
            @WebParam(name = "conceptId") Integer conceptId,
            @WebParam(name = "annotation") String annotation)
            throws WebserviceException {
        try{
            return engine.setAnnotation(graphId, conceptId, annotation);
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Sets the description of this instance of {@link WSConcept}.
     *
     * @param graphId     the ID of the Graph
     * @param conceptId   the ID of the {@link WSConcept}
     * @param description the description to set
     * @return String text confirming success or throws an exception.
     * @throws GraphNotFoundException   No Graph found with the given ID.
     * @throws WebserviceException
     */
    @WebMethod(exclude = false)
    @WebResult(name = "success")
    public String setDescription(
            @WebParam(name = "graphId") Long graphId,
            @WebParam(name = "conceptId") Integer conceptId,
            @WebParam(name = "description") String description)
            throws WebserviceException {
        try{
            return engine.setDescription(graphId, conceptId, description);
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Sets the pid of this instance of {@link WSConcept}.
     *
     * @param graphId     the ID of the Graph
     * @param conceptId   the ID of the {@link WSConcept}
     * @param pid         the pid to set
     * @return String text confirming success or throws an exception.
     * @throws GraphNotFoundException   No Graph found with the given ID.
     * @throws WebserviceException
     */
    @WebMethod(exclude = false)
    @WebResult(name = "success")
    public String setPID(
            @WebParam(name = "graphId") Long graphId,
            @WebParam(name = "conceptId") Integer conceptId,
            @WebParam(name = "pid") String pid)
            throws WebserviceException {
        try{
            return engine.setPID(graphId, conceptId, pid);
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Adds an {@link WSConcept} to the tag of this {@link WSConcept}.
     *
     * @param graphId   the ID of the Graph
     * @param conceptId the ID of the co{@link WSConcept}ncept to add to {@link WSConcept}
     * @param tagId     the ID of the tag being added {@link WSConcept}
     * @return String text confirming success or throws an exception.
     * @throws WebserviceException
     */
    @WebMethod(exclude = false)
    @WebResult(name = "success")
    public String addTagConcept(
            @WebParam(name = "graphId") Long graphId,
            @WebParam(name = "conceptId") Integer conceptId,
            @WebParam(name = "tagId") Integer tagId)
            throws WebserviceException {
        try{
            return engine.addTagConcept(graphId, conceptId, tagId);
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Adds a given {@link WSEvidenceType} to the {@link WSConcept}.
     *
     * @param graphId        the ID of the Graph
     * @param conceptId      the ID of the {@link WSConcept} to add to {@link WSConcept}
     * @param evidenceTypeId the ID of the {@link WSEvidenceType} being added.
     * @return String text confirming success or throws an exception.
     * @throws WebserviceException
     */
    @WebMethod(exclude = false)
    @WebResult(name = "success")
    public String addEvidenceTypeConcept(
            @WebParam(name = "graphId") Long graphId,
            @WebParam(name = "conceptId") Integer conceptId,
            @WebParam(name = "evidenceTypeId") String evidenceTypeId)
            throws WebserviceException {
        try{
            return engine.addEvidenceTypeConcept(graphId, conceptId, evidenceTypeId);
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Returns the parser id of this instance of {@link WSConcept}.
     *
     * @param graphId   the ID of the Graph
     * @param conceptId the ID of the {@link WSConcept}
     * @return A List of the concepts {@link WSConcept} in the tag
     * @throws WebserviceException
     */
    @WebResult(name = "tags")
    @WebMethod(exclude = false)
    public List<WSConcept> getTagConcept(
            @WebParam(name = "graphId") Long graphId,
            @WebParam(name = "conceptId") Integer conceptId)
            throws WebserviceException {
        try{
            Set<ONDEXConcept> concepts =
                    engine.getTagsConcept(graphId, conceptId);
            List<WSConcept> list = new ArrayList<WSConcept>();
            for (ONDEXConcept c : concepts) {
                list.add(new WSConcept(c));
            }
            return list;
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Returns the parser id of this instance of {@link WSConcept}.
     *
     * @param graphId   the ID of the Graph
     * @param conceptId the ID of the {@link WSConcept}
     * @return A List of the concepts {@link WSConcept} in the tag
     * @throws WebserviceException
     */
    @WebResult(name = "tagsIds")
    @WebMethod(exclude = false)
    public List<Integer> getTagConceptIds(
            @WebParam(name = "graphId") Long graphId,
            @WebParam(name = "conceptId") Integer conceptId)
            throws WebserviceException {
        try{
            List<Integer> list = new ArrayList<Integer>();
            for (ONDEXConcept c : engine.getTagsConcept(graphId, conceptId)) {
                list.add(c.getId());
            }
            return list;
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Returns a list of all evidences for this AbstractConcept.
     *
     * @param graphId   the ID of the Graph
     * @param conceptId the ID of the {@link WSConcept}
     * @return A list of the {@link WSEvidenceType}
     * @throws WebserviceException
     */
    @WebResult(name = "evidence")
    @WebMethod(exclude = false)
    public List<WSEvidenceType> getEvidenceConcept(
            @WebParam(name = "graphId") Long graphId,
            @WebParam(name = "conceptId") Integer conceptId)
            throws WebserviceException {
        try{
            return engine.getEvidenceConcept(graphId, conceptId);
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Removes an {@link WSConcept} from the tag of this {@link WSConcept}.
     *
     * @param graphId   the ID of the Graph
     * @param conceptId the ID of the {@link WSConcept}
     * @param tagId     the ID of the tag being removed {@link WSConcept}
     * @return ""\Successfully removed+ X\" or "X not found"
     *         where X is the id of the item being deleted.
     * @throws WebserviceException
     */
    @WebMethod(exclude = false)
    @WebResult(name = "success")
    public String removeTagConcept(
            @WebParam(name = "graphId") Long graphId,
            @WebParam(name = "conceptId") Integer conceptId,
            @WebParam(name = "tagId") Integer tagId)
            throws WebserviceException {
        try{
            if (engine.removeTagConcept(graphId, conceptId, tagId)) {
                return "Successfully removed " + tagId;
            } else {
                return tagId + " not found";
            }
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Removes a given {@link WSEvidenceType} to the {@link WSConcept}.
     *
     * @param graphId        the ID of the Graph
     * @param conceptId      the ID of the {@link WSConcept}
     * @param evidenceTypeId the ID of the {@link WSEvidenceType} being added.
     * @return ""\Successfully removed+ X\" or "X not found"
     *         where X is the id of the item being deleted.
     * @throws WebserviceException
     */
    @WebMethod(exclude = false)
    @WebResult(name = "success")
    public String removeEvidenceTypeConcept(
            @WebParam(name = "graphId") Long graphId,
            @WebParam(name = "conceptId") Integer conceptId,
            @WebParam(name = "evidenceTypeId") String evidenceTypeId)
            throws WebserviceException {
        try{
            if (engine.removeEvidenceTypeConcept(graphId, conceptId, evidenceTypeId)) {
                return "Successfully removed " + evidenceTypeId;
            } else {
                return evidenceTypeId + " not found";
            }
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

//ONDEXRelation methods

    /**
     * Creates a new {@link WSAttribute} and adds it to the {@link WSRelation}
     * .
     *
     * @param graphId         the ID of the Graph
     * @param relationId      the ID of the {@link WSRelation} to add the Attribute to
     * @param attributeNameId the ID of the {@link WSAttributeName} of the Attribute
     * @param value           the value of the Attribute
     * @param doIndex        the setting for the doIndex flag. The default is false.
     * @return the ID of the {@link WSAttributeName}
     * @throws WebserviceException
     */
    @WebResult(name = "attributeNameId")
    @WebMethod(exclude = false)
    public String createRelationAttribute(@WebParam(name = "graphId") Long graphId,
                                    @WebParam(name = "relationId") Integer relationId,
                                    @WebParam(name = "attributeNameId") String attributeNameId,
                                    @WebParam(name = "value") Object value,
                                    @WebParam(name = "doIndex") boolean doIndex)
            throws WebserviceException {
        try{
            return engine.createRelationAttribute(graphId, relationId, attributeNameId,
                    value, doIndex);
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Removes a {@link WSAttribute} from a {@link WSRelation}.
     *
     * @param graphId         the ID of the Graph
     * @param relationId      the ID of the {@link WSRelation}
     * @param attributeNameId the ID of the {@link WSAttributeName}
     * @return ""\Successfully removed+ X\" or "X not found"
     *         where X is the id of the item being deleted.
     * @throws WebserviceException
     */
    @WebMethod(exclude = false)
    @WebResult(name = "success")
    public String deleteRelationAttribute(@WebParam(name = "graphId") Long graphId,
                                    @WebParam(name = "relationId") Integer relationId,
                                    @WebParam(name = "attributeNameId") String attributeNameId)
            throws WebserviceException {
        try{
            boolean result = engine.deleteRelationAttribute(graphId, relationId, attributeNameId);
            if (result) {
                return "Successfully removed " + attributeNameId;
            } else {
                return attributeNameId + "not found.";
            }
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Returns the from {@link WSConcept} of this instance of {@link WSRelation}.
     *
     * @param graphId    the ID of the Graph
     * @param relationId the ID of the {@link WSRelation}
     * @return ONDEXConcept
     * @throws WebserviceException
     */
    @WebMethod(exclude = false)
    @WebResult(name = "fromConcept")
    public WSConcept getFromConcept(@WebParam(name = "graphId") Long graphId,
                                    @WebParam(name = "relationId") Integer relationId)
            throws WebserviceException{
        try{
            return engine.getFromConcept(graphId, relationId);
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Returns the unique Key of this {@link WSRelation}.
     *
     * @param graphId    the ID of the Graph
     * @param relationId the ID of the {@link WSRelation}
     * @return {@link WSRelationKey}
     * @throws WebserviceException
     */
    @WebMethod(exclude = false)
    @WebResult(name = "relationkey")
    public WSRelationKey getKey(@WebParam(name = "graphId") Long graphId,
                                @WebParam(name = "relationId") Integer relationId)
            throws WebserviceException {
        try{
            return engine.getKey(graphId, relationId);
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Returns the {@link WSRelationType} of this instance of {@link WSRelation}.
     *
     * @param graphId    the ID of the Graph
     * @param relationId the ID of the {@link WSRelation}
     * @return RelationType for this {@link WSRelation}.
     * @throws WebserviceException
     */
    @WebMethod(exclude = false)
    @WebResult(name = "relationkey")
    public WSRelationType getOfTypeRelation(@WebParam(name = "graphId") Long graphId,
                                            @WebParam(name = "relationId") Integer relationId)
            throws WebserviceException {
        try{
            return engine.getOfTypeRelation(graphId, relationId);
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Returns the {@link WSAttribute} with the specified
     * {@link WSAttributeName} from the {@link WSRelation}.
     *
     * @param graphId         the ID of the Graph
     * @param relationId      the ID of the {@link WSRelation}
     * @param attributeNameId the ID of the {@link WSAttributeName}
     * @return the {@link WSAttribute} with the specified
     *         {@link WSAttributeName}
     * @throws WebserviceException
     */
    @WebMethod(exclude = false)
    @WebResult(name = "relationAttribute")
    public WSAttribute getRelationAttribute(
            @WebParam(name = "graphId") Long graphId,
            @WebParam(name = "relationId") Integer relationId,
            @WebParam(name = "attributeNameId") String attributeNameId)
            throws WebserviceException {
        try{
            return new WSAttribute(engine.getRelationAttribute(graphId, relationId, attributeNameId));
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Returns all the {@link WSAttribute} in the specified {@link WSRelation}
     * .
     *
     * @param graphId    the ID of the Graph
     * @param relationId the ID of the {@link WSRelation}
     * @return all the {@link WSAttribute} in the specified {@link WSRelation}
     * @throws WebserviceException
     */
    @WebMethod(exclude = false)
    @WebResult(name = "relationAttributes")
    public List<WSAttribute> getRelationAttributes(
            @WebParam(name = "graphId") Long graphId,
            @WebParam(name = "relationId") Integer relationId)
            throws WebserviceException {
        try{
            Set<Attribute> Attributes = engine.getRelationAttributes(graphId, relationId);
            List<WSAttribute> list = new ArrayList<WSAttribute>();
            for (Attribute attribute : Attributes) {
                list.add(new WSAttribute(attribute));
            }
            return list;
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Returns the to {@link WSConcept} of this instance of {@link WSRelation}.
     *
     * @param graphId    the ID of the Graph
     * @param relationId the ID of the {@link WSRelation}
     * @return ONDEXConcept
     * @throws WebserviceException
     */
    @WebResult(name = "toConcept")
    @WebMethod(exclude = false)
    public WSConcept getToConcept(@WebParam(name = "graphId") Long graphId,
                                  @WebParam(name = "relationId") Integer relationId)
            throws WebserviceException {
        try{
            return engine.getToConcept(graphId, relationId);
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Returns whether this {@link WSRelation} is inherited from the given
     * {@link WSRelationType}.
     * This is the case when its ofType rt' either equals rt or
     * is a transitive specialisation of rt.
     *
     * @param graphId        the ID of the Graph
     * @param relationId     the ID of the {@link WSRelation}
     * @param relationTypeId the ID of the {@link WSRelationType}
     * @return whether the above holds
     * @throws WebserviceException
     */
    @WebMethod(exclude = false)
    @WebResult(name = "inheritedFrom")
    public boolean inheritedFromRelation(@WebParam(name = "graphId") Long graphId,
                                         @WebParam(name = "relationId") Integer relationId,
                                         @WebParam(name = "relationTypeId") String relationTypeId)
            throws WebserviceException {
        try{
            return engine.inheritedFromRelation(graphId, relationId, relationTypeId);
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Adds an {@link WSConcept} to the tag of this {@link WSRelation}.
     *
     * @param graphId    the ID of the Graph
     * @param relationId the ID of the {@link WSRelation} to add to {@link WSRelation}
     * @param tagId      the ID of the tag being added {@link WSConcept}
     * @return String text confirming success or throws an exception.
     * @throws WebserviceException
     */
    @WebMethod(exclude = false)
    @WebResult(name = "success")
    public String addTagRelation(
            @WebParam(name = "graphId") Long graphId,
            @WebParam(name = "relationId") Integer relationId,
            @WebParam(name = "tagId") Integer tagId)
            throws WebserviceException {
        try{
            return engine.addTagRelation(graphId, relationId, tagId);
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Adds a given Evide{@link WSEvidenceType}nceType to the {@link WSConcept}.
     *
     * @param graphId        the ID of the Graph
     * @param relationId     the ID of the {@link WSRelation} to add to.
     * @param evidenceTypeId the ID of the {@link WSEvidenceType} being added.
     * @return String text confirming success or throws an exception.
     * @throws WebserviceException
     */
    @WebMethod(exclude = false)
    @WebResult(name = "success")
    public String addEvidenceTypeRelation(
            @WebParam(name = "graphId") Long graphId,
            @WebParam(name = "relationId") Integer relationId,
            @WebParam(name = "evidenceTypeId") String evidenceTypeId)
            throws WebserviceException {
        try{
            return engine.addEvidenceTypeRelation(graphId, relationId, evidenceTypeId);
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Returns A List of the concepts {@link WSConcept} in the tag.
     *
     * @param graphId    the ID of the Graph
     * @param relationId the ID of the {@link WSRelation} to add to {@link WSRelation}
     * @return A List of the concepts {@link WSConcept} in the tag
     * @throws WebserviceException
     */
    @WebResult(name = "tag")
    @WebMethod(exclude = false)
    public List<WSConcept> getTagRelation(
            @WebParam(name = "graphId") Long graphId,
            @WebParam(name = "relationId") Integer relationId)
            throws WebserviceException {
        try{
            List<WSConcept> list = new ArrayList<WSConcept>();
            for (ONDEXConcept c : engine.getTagsRelation(graphId, relationId)) {
                list.add(new WSConcept(c));
            }
            return list;
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Returns a List of the ids of the concepts {@link WSConcept} in the tag
     *
     * @param graphId    the ID of the Graph
     * @param relationId the ID of the {@link WSRelation} to get add to {@link WSRelation}
     * @return A List of the ids of the concepts {@link WSConcept} in the tag
     * @throws WebserviceException
     */
    @WebResult(name = "tagIds")
    @WebMethod(exclude = false)
    public List<Integer> getTagRelationIds(
            @WebParam(name = "graphId") Long graphId,
            @WebParam(name = "relationId") Integer relationId)
            throws WebserviceException {
        try{
            List<Integer> list = new ArrayList<Integer>();
            for (ONDEXConcept c : engine.getTagsRelation(graphId, relationId)) {
                list.add(c.getId());
            }
            return list;
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Returns a list of all evidences for this {@link WSRelation}.
     *
     * @param graphId    the ID of the Graph
     * @param relationId the ID of the {@link WSRelation} to add to {@link WSRelation}
     * @return A list of the EvidenceTypes {@link WSEvidenceType}
     * @throws WebserviceException
     */
    @WebResult(name = "evidence")
    @WebMethod(exclude = false)
    public List<WSEvidenceType> getEvidenceRelation(
            @WebParam(name = "graphId") Long graphId,
            @WebParam(name = "relationId") Integer relationId)
            throws WebserviceException {
        try{
            return engine.getEvidenceRelation(graphId, relationId);
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Removes an {@link WSConcept} from the tag of this {@link WSConcept}.
     *
     * @param graphId    the ID of the Graph
     * @param relationId the ID of the {@link WSRelation} to add to.
     * @param tagId      the id of the {@link WSConcept} being removed from the tag.
     * @return ""\Successfully removed+ X\" or "X not found"
     *         where X is the id of the item being deleted.
     * @throws WebserviceException
     */
    @WebMethod(exclude = false)
    @WebResult(name = "success")
    public String removeTagRelation(
            @WebParam(name = "graphId") Long graphId,
            @WebParam(name = "relationId") Integer relationId,
            @WebParam(name = "tagId") Integer tagId)
            throws WebserviceException {
        try{
            if (engine.removeTagRelation(graphId, relationId, tagId)) {
                return "Successfully removed " + tagId;
            } else {
                return tagId + " not found";
            }
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Removes a given {@link WSEvidenceType} form the set of {@link WSEvidenceType} of this {@link WSConcept}.
     *
     * @param graphId        the ID of the Graph
     * @param relationId     the ID of the {@link WSRelation} to add to.
     * @param evidenceTypeId the ID of the {@link WSEvidenceType} being added.
     * @return ""\Successfully removed+ X\" or "X not found"
     *         where X is the id of the item being deleted.
     * @throws WebserviceException
     */
    @WebMethod(exclude = false)
    @WebResult(name = "success")
    public String removeEvidenceTypeRelation(
            @WebParam(name = "graphId") Long graphId,
            @WebParam(name = "relationId") Integer relationId,
            @WebParam(name = "evidenceTypeId") String evidenceTypeId)
            throws WebserviceException {
        try{
            if (engine.removeEvidenceTypeRelation(graphId, relationId, evidenceTypeId)) {
                return "Successfully removed " + evidenceTypeId;
            } else {
                return evidenceTypeId + " not found";
            }
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    //ONDEXManager methods

    /**
     * Clones and existing graph.
     *
     * Especial useful for (None memory) Berkley graphs.
     * Makes a physical copy of the files on disk.
     *
     * @param graphId
     * @param name    Name of new graph. If blank or null old name plus "2".
     * @return Id of new graph.
     * @throws WebserviceException
     */
    @WebResult(name = "graphId")
    @WebMethod(exclude = false)
    public long cloneGraph(@WebParam(name = "graphId") Long graphId,
                           @WebParam(name = "name") String name)
            throws WebserviceException {
        try{
            return engine.cloneGraph(graphId, name);
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Creates a new {@link WSGraph}.
     *
     * Note: Does not load the default Ondex MetaData.
     * Use initialiseMetaData (Long graphId) to load the MetaData.
     *
     * @param name the name of the {@link WSGraph}
     * @return the ID of the new {@link WSGraph}
     * @throws IllegalNameException,
     * @throws WebserviceException
     */
    @WebResult(name = "graphId")
    @WebMethod(exclude = false)
    public Long createGraph(@WebParam(name = "name") String name)
            throws WebserviceException  {
        try{
            return engine.createGraph(name);
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Add the default Ondex MetaData to an existing graph.
     *
     * This should be called if an empty Graph is created and users want to add Copcepts, Relations ect,
     * which depend on the MetaData being present.
     * <p/>
     * It is not required before running a parser as the WebServer will call this method ebfore running any parser
     * except the OXL parser.
     * @param graphId
     * @return log of the oxl parser user.
     * @throws WebserviceException
     */
    @WebResult(name = "report")
    @WebMethod(exclude = false)
    public String initialiseMetaData (@WebParam(name = "graphId") Long graphId)
            throws WebserviceException {
        try{
            return engine.initialiseMetaData(graphId);
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Removes a {@link WSGraph} from the server. All the data in the Graph will
     * be deleted.
     *
     * @param graphId the ID of the Graph to delete
     * @return String text confirming success or throws an exception.
     * @throws WebserviceException
     */
    @WebMethod(exclude = false)
    @WebResult(name = "success")
    public String deleteGraph(@WebParam(name = "graphId") Long graphId)
            throws WebserviceException {
        try{
            return engine.deleteGraph(graphId);
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Exports a graph to oxl, with all available settings.
     *
     * @param ExcludeConceptsOfConceptClass
 	 *            (Optional. No Default.) [List]This parameter can be used to do some basic filtering on ConceptClass in the export method.
This is especially useful if graphs become to large.
     * @param ExcludeRelationsOfRelationType
 	 *            (Optional. No Default.) [List]This parameter can be used to do some basic filtering on RelationType in the export method.
This is especially useful if graphs become to large.
     * @param ExcludeAttributeWithAttribute
 	 *            (Optional. No Default.) [List]This parameter can be used to exclude a number of attributes from being written in the ondex.xml file.
When **ALL** is used all attributes are excluded and thus no Attribute values will be written.
     * @param IncludeOnlyAttributeAttribute
 	 *            (Optional. No Default.) [List]This parameter works by setting exclusive inclusions for a set of Attribute Attributes. All other Attributes Attributes not specified will be excluded.
     * @param IncludeOnlyConceptClass
 	 *            (Optional. No Default.) [List]This parameter works by setting exclusive inclusions for a set of Concept Classes. All other Concept Classes not specified will be excluded.
     * @param IncludeOnlyRelationType
 	 *            (Optional. No Default.) [List]This parameter works by setting exclusive inclusions for a set of Relation Types. All other Relation Types not specified will be excluded.
     * @param pretty
 	 *            (Optional. Defaults to :true) When this option is set the output XML is kind of pretty printed. This makes the output larger.
     * @param ExportIsolatedConcepts
 	 *            (Optional. Defaults to :true) When this is option is set, it will export also concepts without any relations.
     * @param graphId
 	 *            (REQUIRED! No Default!) The ID of the Graph
     * @return String representation of all event fired by the export
     * @throws WebserviceException
     * /
    @WebResult(name = "oxl")
    @WebMethod(exclude = false)
    public String exportGraph(@WebParam(name = "ExcludeConceptsOfConceptClass")java.lang.String[] ExcludeConceptsOfConceptClass,
           @WebParam(name = "ExcludeRelationsOfRelationType")java.lang.String[] ExcludeRelationsOfRelationType,
           @WebParam(name = "ExcludeAttributeWithAttribute")java.lang.String[] ExcludeAttributeWithAttribute,
           @WebParam(name = "IncludeOnlyAttributeAttribute")java.lang.String[] IncludeOnlyAttributeAttribute,
           @WebParam(name = "IncludeOnlyConceptClass")java.lang.String[] IncludeOnlyConceptClass,
           @WebParam(name = "IncludeOnlyRelationType")java.lang.String[] IncludeOnlyRelationType,
           @WebParam(name = "pretty")java.lang.Boolean pretty,
           @WebParam(name = "ExportIsolatedConcepts")java.lang.Boolean ExportIsolatedConcepts,
           @WebParam(name = "graphId") Long graphId)
           throws WebserviceException {
        try{
            ExportAuto exportAuto = new ExportAuto();
            String result = exportAuto.oxlExport(ExcludeConceptsOfConceptClass, ExcludeRelationsOfRelationType,
                    ExcludeAttributeWithAttribute, IncludeOnlyAttributeAttribute, IncludeOnlyConceptClass,
                    IncludeOnlyRelationType, pretty, false, ExportIsolatedConcepts, graphId);
            return result;
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }
           
    /**
     * Returns the {@link WSGraph}s with the specified name.
     *
     * @param name the name of the {@link WSGraph}s to return
     * @return the {@link WSGraph}s with the specified name
     * @throws WebserviceException
     */
    @WebResult(name = "graph")
    @WebMethod(exclude = false)
    public WSGraph getGraphOfName(@WebParam(name = "name") String name)
            throws WebserviceException {
        try{
            return engine.getGraphOfName(name);
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Returns all the {@link WSGraph}s.
     *
     * @return all the {@link WSGraph}s
     * @throws WebserviceException
     */
    @WebResult(name = "graphs")
    @WebMethod(exclude = false)
    public List<WSGraph> getGraphs() throws WebserviceException {
        try{
            return engine.getGraphs();
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Returns a list of the Berkley files and the time they where "LastModified".
     *
     * This method only applies to Berkley graphs all others are completely ignored.
     * "LastModified" is calculated by calling getLastModifed() on the directory that holds each Berkley Graph.
     *   It is unknown if the results are system dependent. (Ie wil Latex results be different to Windows results.
     * It is currently unknown if opening or even editing the graph changes the directories getLastModifed() value.
     *
     * @return And arrange of Strings one for each Berkley graoh.
     *     In the format graphId:  lastModifdedDate
     * @throws CaughtException
     */
    @WebResult(name = "graphsLastModified")
    @WebMethod(exclude = false)
    public String[] getGraphsLastModified() throws CaughtException {
        try{
            return engine.getGraphsLastModified();
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Returns the Date this graph was "lasttModified".
     *
     * For Memory Graphs this function just returns the current date.
     * <p>
     * For Berkley graphs "LastModified" is calculated by calling getLastModifed() on the directory that holds each Berkley Graph.
     *   It is unknown if the results are system dependent. (Ie wil Latex results be different to Windows results.
     * It is currently unknown if opening or even editing the graph changes the directories getLastModifed() value.
     *
     * @param graphId the ID of the Graph to get "LastModified"
     * @return result of getLastModified() on directory that holds the graph, or new Date() for a Memory Graph.
     * @throws CaughtException
     */
    @WebResult(name = "graphLastModified")
    @WebMethod(exclude = false)
    public Date getGraphLastModified(@WebParam(name = "graphId") Long graphId ) throws WebserviceException{
        try{
            return engine.getGraphLastModified(graphId);
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

     /**
     * Method to run oxl Parser.
     *
     * @param oxlString
 	 *            (Optional.) (Use this or oxlByteArray NOT BOTH.) Reader arround Ondex XML (oxl) file to parse
     *        Legal values for the String are:
     *        1) Path and Name of a File Stored on the Server.
     *        2) Valid URL pointing to the file. (Traverna New Value)
     *        3) Valid URL pointing to a gzip file (as identified by ".gz" ending.
     *                 (Traverna New Value)
     *        4) Actaul String value. Avoid using this option if data is in a URL (use 3).
     *                 (Traverna Add file location or Add URL)
     * @param oxlByteArray
 	 *            (Optional.) (Only used if oxlString is null or "".) GZip representation of Reader arround Ondex XML (oxl) file to parse
     * @param IgnoreAttribute
 	 *            (Optional. No Default.) [List]Do not parse Attribute attributes with specified AttributeName
     * @param graphId
 	 *            (REQUIRED! No Default!) The ID of the Graph
     * @return String representation of all event fired by the parser
     * @throws WebserviceException
     */
    @WebResult(name = "report")
    @WebMethod(exclude = false)
    public String importGraph(@WebParam(name = "oxlString") String oxlString,
           @WebParam(name = "oxlByteArray") byte[] oxlByteArray,
           @WebParam(name = "IgnoreAttribute")java.lang.String[] IgnoreAttribute,
           @WebParam(name = "graphId") Long graphId)
            throws WebserviceException {
        try{
            ParserAuto parserAuto = new ParserAuto();
            return parserAuto.oxlParser(IgnoreAttribute, oxlString, oxlByteArray, graphId);
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * Gets a summary of the graph's meta data {@link WSGraphMetaData}
     *
     * Warning may take a considerable time to return.
     *
     * @param graphId the ID of the Graph
     * @return the graph's meta data {@link WSGraphMetaData}
     * @throws WebserviceException
     */
    @WebResult(name = "graphMetaData")
    @WebMethod(exclude=false)
    public WSGraphMetaData getGraphMetaData(@WebParam(name = "graphId") Long graphId)
            throws WebserviceException{
        try{
            return engine.getGraphMetaData(graphId);
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }
}
