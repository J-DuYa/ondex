package net.sourceforge.ondex.wsapi;

import net.sourceforge.ondex.core.*;
import net.sourceforge.ondex.marshal.Marshaller;
import net.sourceforge.ondex.wsapi.exceptions.CaughtException;
import net.sourceforge.ondex.wsapi.exceptions.WebserviceException;
import net.sourceforge.ondex.wsapi.plugins.export.ExportOXL;
import net.sourceforge.ondex.wsapi.plugins.parser.ParserOXL;
import org.apache.log4j.Logger;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.sourceforge.ondex.wsapi.result.*;

/**
 * @author Christian Brenninkmeijer
 */
public class ONDEXapiWS {

    private WebServiceEngine engine = WebServiceEngine.getWebServiceEngine();

    private static final Marshaller marshaller = Marshaller.getMarshaller();

    /*@WebMethod(exclude = true)
    public void setOndexDir(String ondexDir) throws WebserviceException {
        try{
            engine.setOndexDir(ondexDir);
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }*/

    private static final Logger logger = Logger.getLogger(ONDEXapiWS.class);

    // **** ONDEXGraph Methods ****

    /**
     * See net.sourceforge.ondex.core.ONDEXGraph method with the same name
     */
    @WebResult(name = "conceptId")
    @WebMethod(exclude = false)
    public Integer createConcept(
            @WebParam(name = "graphId") Long graphId,
            @WebParam(name = "parserId") String parserId,
            @WebParam(name = "annotation") String annotation,
            @WebParam(name = "description") String description,
            @WebParam(name = "elementOfDataSourceId") String elementOfDataSourceId,
            @WebParam(name = "ofTypeConceptClassId") String ofTypeConceptClassId,
            @WebParam(name = "evidenceTypeIdList") List<String> evidenceTypeIdList)
            throws WebserviceException {
        try{
            return engine.createConcept(graphId, parserId, annotation, description,
                    elementOfDataSourceId, ofTypeConceptClassId, evidenceTypeIdList);
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * See net.sourceforge.ondex.core.ONDEXGraph method with the same name
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
        try{
            return engine.createRelation(graphId, fromConceptId, toConceptId,
                    relationTypeId, evidenceTypeIdList);
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * See net.sourceforge.ondex.core.ONDEXGraph method with the same name
     */
    @WebMethod(exclude = false)
    @WebResult(name = "success")
    public boolean deleteConcept(@WebParam(name = "graphId") Long graphId,
                                 @WebParam(name = "conceptId") Integer conceptId)
            throws WebserviceException {
        try{
            logger.info("In deleteConcept "+graphId+", "+conceptId);
            boolean result = engine.deleteConcept(graphId, conceptId);
            logger.info("result = "+result);
            return result;
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * See net.sourceforge.ondex.core.ONDEXGraph method with the same name
     */
    @WebMethod(exclude = false)
    @WebResult(name = "success")
    public boolean deleteRelation(@WebParam(name = "graphId") Long graphId,
                                  @WebParam(name = "relationId") Integer relationId)
            throws WebserviceException {
        try{
            return engine.deleteRelation(graphId, relationId);
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * See net.sourceforge.ondex.core.ONDEXGraph method with the same name
     */
    @WebMethod(exclude = false)
    @WebResult(name = "success")
    public boolean deleteRelationOfType(
            @WebParam(name = "graphId") Long graphId,
            @WebParam(name = "fromConceptId") Integer fromConceptId,
            @WebParam(name = "toConceptId") Integer toConceptId,
            @WebParam(name = "relationTypeId") String relationTypeId)
            throws WebserviceException {
        try{
            return engine.deleteRelationOfType(graphId, fromConceptId,
                    toConceptId, relationTypeId);
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * See net.sourceforge.ondex.core.ONDEXGraph method with the same name
     */
    @WebResult(name = "WSConcept")
    @WebMethod(exclude = false)
    public WSConcept getConcept(@WebParam(name = "graphId") Long graphId,
                                @WebParam(name = "conceptId") Integer conceptId)
            throws WebserviceException {
        try{
            ONDEXConcept result = engine.getConcept(graphId, conceptId);
            if (result == null) {
                return null;
            }
            return new WSConcept(result);
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * See net.sourceforge.ondex.core.ONDEXGraph method with the same name
     */
    @WebResult(name = "WSConcepts")
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
     * See net.sourceforge.ondex.core.ONDEXGraph method with the same name
     */
    @WebResult(name = "WSConcepts")
    @WebMethod(exclude = false)
    public List<WSConcept> getConceptsOfAttributeName(
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
     * See net.sourceforge.ondex.core.ONDEXGraph method with the same name
     */
    @WebResult(name = "WSConcepts")
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
     * See net.sourceforge.ondex.core.ONDEXGraph method with the same name
     */
    @WebResult(name = "WSConcepts")
    @WebMethod(exclude = false)
    public List<WSConcept> getConceptsOfTag(
            @WebParam(name = "graphId") Long graphId,
            @WebParam(name = "conceptId") Integer conceptId)
            throws WebserviceException {
        try{
            List<WSConcept> list = new ArrayList<WSConcept>();
            for (ONDEXConcept c : engine.getConceptsOfTag(graphId, conceptId)) {
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
     * See net.sourceforge.ondex.core.ONDEXGraph method with the same name
     */
    @WebResult(name = "WSConcepts")
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
     * See net.sourceforge.ondex.core.ONDEXGraph method with the same name
     */
    @WebResult(name = "WSConcepts")
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
     * See net.sourceforge.ondex.core.ONDEXGraph method with the same name
     */
    @WebResult(name = "WSConcepts")
    @WebMethod(exclude = false)
    public List<WSConcept> getTagsGraph(@WebParam(name = "graphId") Long graphId)
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
     * See net.sourceforge.ondex.core.ONDEXGraph method with the same name
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
     * See net.sourceforge.ondex.core.ONDEXGraph method with the same name
     */
    @WebResult(name = "WSRelation")
    @WebMethod(exclude = false)
    public WSRelation getRelation(@WebParam(name = "graphId") Long graphId,
                                  @WebParam(name = "relationId") Integer relationId)
            throws WebserviceException {
        try{
            ONDEXRelation result = engine.getRelation(graphId, relationId);
            if (result == null) {
                return null;
            }
            return new WSRelation(result);
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * See net.sourceforge.ondex.core.ONDEXGraph method with the same name
     */
    @WebResult(name = "WSRelation")
    @WebMethod(exclude = false)
    public WSRelation getRelationOfType(
            @WebParam(name = "graphId") Long graphId,
            @WebParam(name = "fromConceptId") Integer fromConceptId,
            @WebParam(name = "toConceptId") Integer toConceptId,
            @WebParam(name = "relationTypeId") String relationTypeId)
            throws WebserviceException {
        try{
            ONDEXRelation result = engine.getRelationOfType(graphId, fromConceptId,
                    toConceptId, relationTypeId);
            if (result == null) {
                return null;
            }
            return new WSRelation(result);
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

     /**
     * See net.sourceforge.ondex.core.ONDEXGraph method with the same name
     */
    @WebResult(name = "WSRelations")
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
     * See net.sourceforge.ondex.core.ONDEXGraph method with the same name
     */
    @WebResult(name = "WSRelations")
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
     * See net.sourceforge.ondex.core.ONDEXGraph method with the same name
     */
    @WebResult(name = "WSRelations")
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
     * See net.sourceforge.ondex.core.ONDEXGraph method with the same name
     */
    @WebResult(name = "WSRelations")
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

    /**
     * See net.sourceforge.ondex.core.ONDEXGraph method with the same name
     */
    @WebResult(name = "WSRelations")
    @WebMethod(exclude = false)
    public List<WSRelation> getRelationsOfTag(
            @WebParam(name = "graphId") Long graphId,
            @WebParam(name = "tagConceptId") Integer tagConceptId)
            throws WebserviceException {
        try{
            List<WSRelation> list = new ArrayList<WSRelation>();
            for (ONDEXRelation r : engine.getRelationsOfTag(graphId, tagConceptId)) {
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
     * See net.sourceforge.ondex.core.ONDEXGraph method with the same name
     */
    @WebResult(name = "WSRelations")
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
     * See net.sourceforge.ondex.core.ONDEXGraph method with the same name
     */
    @WebResult(name = "WSRelations")
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
     * See net.sourceforge.ondex.core.ONDEXGraph method with the same name
     */
    @WebResult(name = "WSRelations")
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
     * See net.sourceforge.ondex.core.ONDEXGraph method with the same name
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

    // **** ONDEX Concept Methods ******

    /**
     * See net.sourceforge.ondex.core.ONDEXConcept method with the same name
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
     * See net.sourceforge.ondex.core.ONDEXConcept method with the same name
     */
    @WebResult(name = "attributeNameId")
    @WebMethod(exclude = false)
    public String createConceptAttribute(@WebParam(name = "graphId") Long graphId,
                                   @WebParam(name = "conceptId") Integer conceptId,
                                   @WebParam(name = "attributeNameId") String attributeNameId,
                                   @WebParam(name = "valueAsXML") String xml,
                                   @WebParam(name = "doIndex") boolean doIndex)
            throws WebserviceException {
        try{
            //ogger.info("createConceptAttribute");
            //ogger.info(xml);
            Object value = marshaller.fromXML(xml);
            //if (value != null){
                //ogger.info(value);
                //ogger.info(value.getClass());
            //} else{
                //ogger.info("NULL!!!");
            //}
            return engine.createConceptAttribute(graphId, conceptId, attributeNameId,
                    value, doIndex);
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * See net.sourceforge.ondex.core.ONDEXConcept method with the same name
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
     * See net.sourceforge.ondex.core.ONDEXConcept method with the same name
     */
    @WebMethod(exclude = false)
    @WebResult(name = "success")
    public boolean deleteConceptAccession(
            @WebParam(name = "graphId") Long graphId,
            @WebParam(name = "conceptId") Integer conceptId,
            @WebParam(name = "accession") String accession,
            @WebParam(name = "elementOfDataSourceId") String elementOfDataSourceId)
            throws WebserviceException {
        try{
            return engine.deleteConceptAccession(graphId, conceptId, accession,
                    elementOfDataSourceId);
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * See net.sourceforge.ondex.core.ONDEXConcept method with the same name
     */
    @WebMethod(exclude = false)
    @WebResult(name = "success")
    public boolean deleteConceptAttribute(@WebParam(name = "graphId") Long graphId,
                                    @WebParam(name = "conceptId") Integer conceptId,
                                    @WebParam(name = "attributeNameId") String attributeNameId)
            throws WebserviceException {
        try{
            //logger.info("Request to delete attribute with name " + attributeNameId + " from concept " + conceptId);
            return engine.deleteConceptAttribute(graphId, conceptId, attributeNameId);
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * See net.sourceforge.ondex.core.ONDEXConcept method with the same name
     */
    @WebMethod(exclude = false)
    @WebResult(name = "success")
    public boolean deleteConceptName(@WebParam(name = "graphId") Long graphId,
                                     @WebParam(name = "conceptId") Integer conceptId,
                                     @WebParam(name = "name") String name)
            throws WebserviceException {
        try{
            return engine.deleteConceptName(graphId, conceptId, name);
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * See net.sourceforge.ondex.core.ONDEXConcept method with the same name
     */
    @WebResult(name = "annotation")
    @WebMethod(exclude = false)
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
     * See net.sourceforge.ondex.core.ONDEXConcept method with the same name
     */
    @WebResult(name = "WSConceptAccession")
    @WebMethod(exclude = false)
    public WSConceptAccession getConceptAccession(
            @WebParam(name = "graphId") Long graphId,
            @WebParam(name = "conceptId") Integer conceptId,
            @WebParam(name = "accession") String accession,
            @WebParam(name = "elementOfDataSourceId") String elementOfDataSourceId)
            throws WebserviceException {
        try{
            ConceptAccession result = engine.getConceptAccession(graphId,
                    conceptId, accession, elementOfDataSourceId);
            if (result == null) {
                return null;
            }
            return new WSConceptAccession(result);
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * See net.sourceforge.ondex.core.ONDEXConcept method with the same name
     */
    @WebResult(name = "WSConceptAccessions")
    @WebMethod(exclude = false)
    public List<WSConceptAccession> getConceptAccessions(
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
     * See net.sourceforge.ondex.core.ONDEXConcept method with the same name
     */
    @WebResult(name = "WSAttributes")
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
     * See net.sourceforge.ondex.core.ONDEXConcept method with the same name
     */
    @WebResult(name = "WSAttribute")
    @WebMethod(exclude = false)
    public WSAttribute getConceptAttribute(@WebParam(name = "graphId") Long graphId,
                               @WebParam(name = "conceptId") Integer conceptId,
                               @WebParam(name = "attributeNameId") String attributeNameId)
            throws WebserviceException {
        try{
            Attribute attribute = engine.getConceptAttribute(graphId, conceptId, attributeNameId);
            if (attribute == null){
                return null;
            }
            return new WSAttribute(attribute);
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * See net.sourceforge.ondex.core.ONDEXConcept method with the same name
     */
    @WebResult(name = "WSConceptName")
    @WebMethod(exclude = false)
    public WSConceptName getConceptName(
            @WebParam(name = "graphId") Long graphId,
            @WebParam(name = "conceptId") Integer conceptId)
            throws WebserviceException {
        try{
            ConceptName result = engine.getConceptName(graphId, conceptId);
            if (result == null) {
                return null;
            }
            return new WSConceptName(result);
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * See net.sourceforge.ondex.core.ONDEXConcept method with the same name
     */
    @WebResult(name = "WSConceptName")
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
     * See net.sourceforge.ondex.core.ONDEXConcept method with the same name
     */
    @WebResult(name = "WSConceptNames")
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
     * See net.sourceforge.ondex.core.ONDEXConcept method with the same name
     */
    @WebResult(name = "descriptiom")
    @WebMethod(exclude = false)
    public String getDescription(@WebParam(name = "graphId") Long graphId,
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
     * See net.sourceforge.ondex.core.ONDEXConcept method with the same name
     */
    @WebResult(name = "WSDataSource")
    @WebMethod(exclude = false)
    public WSDataSource getElementOf(@WebParam(name = "graphId") Long graphId,
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
     * See net.sourceforge.ondex.core.ONDEXConcept method with the same name
     */
    @WebResult(name = "WSConceptClass")
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
     * See net.sourceforge.ondex.core.ONDEXConcept method with the same name
     */
    @WebResult(name = "pid")
    @WebMethod(exclude = false)
    public String getPID(@WebParam(name = "graphId") Long graphId,
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
     * See net.sourceforge.ondex.core.ONDEXConcept method with the same name
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
     * See net.sourceforge.ondex.core.ONDEXConcept method with the same name
     */
    @WebMethod(exclude = false)
    @WebResult(name = "success")
    public String setAnnotation(@WebParam(name = "graphId") Long graphId,
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
     * See net.sourceforge.ondex.core.ONDEXConcept method with the same name
     */
    @WebMethod(exclude = false)
    public void setDescription(@WebParam(name = "graphId") Long graphId,
                                 @WebParam(name = "conceptId") Integer conceptId,
                                 @WebParam(name = "description") String description)
            throws WebserviceException {
        try{
            engine.setDescription(graphId, conceptId, description);
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * See net.sourceforge.ondex.core.ONDEXConcept method with the same name
     */
    @WebMethod(exclude = false)
    public void setPID(@WebParam(name = "graphId") Long graphId,
                       @WebParam(name = "conceptId") Integer conceptId,
                       @WebParam(name = "pid") String pid)
            throws WebserviceException {
        try{
            engine.setPID(graphId, conceptId, pid);
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * See net.sourceforge.ondex.core.ONDEXConcept method with the same name
     */
    @WebMethod(exclude = false)
    public void addTagConcept(@WebParam(name = "graphId") Long graphId,
                                    @WebParam(name = "conceptId") Integer conceptId,
                                    @WebParam(name = "tagId") Integer tagId)
            throws WebserviceException {
        try{
            engine.addTagConcept(graphId, conceptId, tagId);
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * See net.sourceforge.ondex.core.ONDEXConcept method with the same name
     */
    @WebMethod(exclude = false)
    public void addEvidenceTypeConcept(
            @WebParam(name = "graphId") Long graphId,
            @WebParam(name = "conceptId") Integer conceptId,
            @WebParam(name = "evidenceTypeId") String evidenceTypeId)
            throws WebserviceException {
        try{
            engine.addEvidenceTypeConcept(graphId, conceptId, evidenceTypeId);
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * See net.sourceforge.ondex.core.ONDEXConcept method with the same name
     */
    @WebResult(name = "WSConcepts")
    @WebMethod(exclude = false)
    public List<WSConcept> getTagsConcept(
            @WebParam(name = "graphId") Long graphId,
            @WebParam(name = "conceptId") Integer conceptId)
            throws WebserviceException {
        try{
            List<WSConcept> list = new ArrayList<WSConcept>();
            for (ONDEXConcept c : engine.getTagsConcept(graphId, conceptId)) {
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
     * See net.sourceforge.ondex.core.ONDEXConcept method with the same name
     */
    @WebResult(name = "WSEvidenceTypes")
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
     * See net.sourceforge.ondex.core.ONDEXConcept method with the same name
     */
    @WebMethod(exclude = false)
    @WebResult(name = "success")
    public boolean removeTagConcept(
            @WebParam(name = "graphId") Long graphId,
            @WebParam(name = "conceptId") Integer conceptId,
            @WebParam(name = "tagId") Integer tagId)
            throws WebserviceException {
        try{
            return engine.removeTagConcept(graphId, conceptId, tagId);
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * See net.sourceforge.ondex.core.ONDEXConcept method with the same name
     */
    @WebMethod(exclude = false)
    @WebResult(name = "success")
    public boolean removeEvidenceTypeConcept(
            @WebParam(name = "graphId") Long graphId,
            @WebParam(name = "conceptId") Integer conceptId,
            @WebParam(name = "evidenceTypeId") String evidenceTypeId)
            throws WebserviceException {
        try{
            return engine.removeEvidenceTypeConcept(graphId, conceptId,
                    evidenceTypeId);
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    // **** ONDEX Relation Methods *****

    /**
     * See net.sourceforge.ondex.core.ONDEXRelation method with the same name
     */
    @WebResult(name = "attributeNameId")
    @WebMethod(exclude = false)
    public String createRelationAttribute(@WebParam(name = "graphId") Long graphId,
                                    @WebParam(name = "relationId") Integer relationId,
                                    @WebParam(name = "attributeNameId") String attributeNameId,
                                    @WebParam(name = "valueAsXML") String xml,
                                    @WebParam(name = "doIndex") boolean doIndex)
            throws WebserviceException {
        try{
            //ogger.info("Going to create Relation Attribute "+attributeNameId +" doIndex ="+doIndex);
            Object value = marshaller.fromXML(xml);
            return engine.createRelationAttribute(graphId, relationId, attributeNameId,
                    value, doIndex);
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * See net.sourceforge.ondex.core.ONDEXRelation method with the same name
     */
    @WebMethod(exclude = false)
    @WebResult(name = "success")
    public boolean deleteRelationAttribute(@WebParam(name = "graphId") Long graphId,
                                     @WebParam(name = "relationId") Integer relationId,
                                     @WebParam(name = "attributeNameId") String attributeNameId)
            throws WebserviceException {
        try{
            //ogger.info("deleting "+graphId+" "+relationId+" "+attributeNameId);
            boolean result = engine.deleteRelationAttribute(graphId, relationId, attributeNameId);
            //ogger.info(result);
            return result;
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * See net.sourceforge.ondex.core.ONDEXRelation method with the same name
     */
    @WebMethod(exclude = false)
    @WebResult(name = "WSConcept")
    public WSConcept getFromConcept(@WebParam(name = "graphId") Long graphId,
                                    @WebParam(name = "relationId") Integer relationId)
            throws WebserviceException {
        try{
            return engine.getFromConcept(graphId, relationId);
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * See net.sourceforge.ondex.core.ONDEXRelation method with the same name
     */
    @WebMethod(exclude = false)
    @WebResult(name = "WSRelationKey")
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
     * See net.sourceforge.ondex.core.ONDEXRelation method with the same name
     */
    @WebMethod(exclude = false)
    @WebResult(name = "WSRelationType")
    public WSRelationType getOfTypeRelation(
            @WebParam(name = "graphId") Long graphId,
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
     * See net.sourceforge.ondex.core.ONDEXRelation method with the same name
     */
    @WebMethod(exclude = false)
    @WebResult(name = "WSAttribute")
    public WSAttribute getRelationAttribute(
            @WebParam(name = "graphId") Long graphId,
            @WebParam(name = "relationId") Integer relationId,
            @WebParam(name = "attributeNameId") String attributeNameId)
            throws WebserviceException {
        try{
            Attribute result = engine.getRelationAttribute(graphId, relationId,
                    attributeNameId);
            if (result == null) {
                //ogger.info("null attribute for "+graphId+" "+relationId+" "+attributeNameId);
                return null;
            }
            //ogger.info("got attribute "+result.getOfType().getId()+" "+result.isDoIndex());
            return new WSAttribute(result);
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * See net.sourceforge.ondex.core.ONDEXRelation method with the same name
     */
    @WebMethod(exclude = false)
    @WebResult(name = "WSAttributes")
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
     * See net.sourceforge.ondex.core.ONDEXRelation method with the same name
     */
    @WebResult(name = "WSConcept")
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
     * See net.sourceforge.ondex.core.ONDEXRelation method with the same name
     */
    @WebMethod(exclude = false)
    @WebResult(name = "inheritedFrom")
    public boolean inheritedFromRelation(
            @WebParam(name = "graphId") Long graphId,
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
     * See net.sourceforge.ondex.core.ONDEXRelation method with the same name
     */
    @WebMethod(exclude = false)
    public void addTagRelation(@WebParam(name = "graphId") Long graphId,
                                     @WebParam(name = "relationId") Integer relationId,
                                     @WebParam(name = "tagId") Integer tagId)
            throws WebserviceException {
        try{
            engine.addTagRelation(graphId, relationId, tagId);
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * See net.sourceforge.ondex.core.ONDEXRelation method with the same name
     */
    @WebMethod(exclude = false)
    public void addEvidenceTypeRelation(
            @WebParam(name = "graphId") Long graphId,
            @WebParam(name = "relationId") Integer relationId,
            @WebParam(name = "evidenceTypeId") String evidenceTypeId)
            throws WebserviceException {
        try{
            engine.addEvidenceTypeRelation(graphId, relationId,evidenceTypeId);
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * See net.sourceforge.ondex.core.ONDEXRelation method with the same name
     */
    @WebResult(name = "WSConcepts")
    @WebMethod(exclude = false)
    public List<WSConcept> getTagsRelation(
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
     * See net.sourceforge.ondex.core.ONDEXRelation method with the same name
     */
    @WebResult(name = "WSEvidenceTypes")
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
     * See net.sourceforge.ondex.core.ONDEXRelation method with the same name
     */
    @WebMethod(exclude = false)
    @WebResult(name = "success")
    public boolean removeTagRelation(
            @WebParam(name = "graphId") Long graphId,
            @WebParam(name = "relationId") Integer relationId,
            @WebParam(name = "tagId") Integer tagId)
            throws WebserviceException {
        try{
            return engine.removeTagRelation(graphId, relationId, tagId);
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * See net.sourceforge.ondex.core.ONDEXRelation method with the same name
     */
    @WebMethod(exclude = false)
    @WebResult(name = "success")
    public boolean removeEvidenceTypeRelation(
            @WebParam(name = "graphId") Long graphId,
            @WebParam(name = "relationId") Integer relationId,
            @WebParam(name = "evidenceTypeId") String evidenceTypeId)
            throws WebserviceException {
        try{
            return engine.removeEvidenceTypeRelation(graphId, relationId, evidenceTypeId);
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    // **** Manger methods

    /**
     * See net.sourceforge.ondex.Webservice2.ONDEXServiceWS method with the same
     * name
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
     * See net.sourceforge.ondex.Webservice2.ONDEXServiceWS method with the same
     * name
     */
    @WebResult(name = "graphId")
    @WebMethod(exclude = false)
    public Long createGraph(@WebParam(name = "name") String name)
            throws WebserviceException  {
        try{
            long graphId = engine.createGraph(name);
            logger.info("passing back "+graphId);
            return graphId;
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * See net.sourceforge.ondex.Webservice2.ONDEXServiceWS method with the same
     * name
     */
    @WebResult(name = "graphId")
    @WebMethod(exclude = false)
    public long createMemoryGraph(@WebParam(name = "name") String name)
            throws WebserviceException {
        try{
            long graphId = engine.createMemoryGraph(name);
            logger.info("passing back "+graphId);
            return graphId;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    @WebResult(name = "graphId")
    @WebMethod(exclude = false)
    public long createTestGraph()
            throws WebserviceException {
        try{
            long graphId = engine.createTestGraph();
            logger.info("passing back "+graphId);
            return graphId;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * See net.sourceforge.ondex.Webservice2.ONDEXServiceWS method with the same
     * name
     */
    @WebMethod(exclude = false)
    @WebResult(name = "success")
    public String deleteGraph(@WebParam(name = "graphId") Long graphId)
            throws WebserviceException {
        try{
            logger.info("deleteGraph called on "+graphId);
            return engine.deleteGraph(graphId);
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * See net.sourceforge.ondex.Webservice2.ONDEXServiceWS method with the same
     * name
     */
    @WebResult(name = "oxl")
    @WebMethod(exclude = false)
    public String exportGraphLite(@WebParam(name = "graphId") Long graphId)
            throws WebserviceException {
        try{
            ExportOXL exportOXL = new ExportOXL();
            String result = exportOXL.oxlExport(
                null, //@WebParam(name = "ExcludeConceptsOfConceptClass")java.lang.String[] ExcludeConceptsOfConceptClass,
                null, //@WebParam(name = "ExcludeRelationsOfRelationType")java.lang.String[] ExcludeRelationsOfRelationType,
                null, //@WebParam(name = "ExcludeAttributeWithAttribute")java.lang.String[] ExcludeAttributeWithName,
                null, //@WebParam(name = "IncludeOnlyAttributeAttribute")java.lang.String[] IncludeAttributesOfName,
                null, //@WebParam(name = "IncludeOnlyConceptClass")java.lang.String[] IncludeOnlyConceptClass,
                null, //@WebParam(name = "IncludeOnlyRelationType")java.lang.String[] IncludeOnlyRelationType,
                null, //@WebParam(name = "pretty")java.lang.Boolean pretty,
                null, //@WebParam(name = "ExportIsolatedConcepts")java.lang.Boolean ExportIsolatedConcepts,
                "raw", //@WebParam(name = "GZip")java.lang.Boolean GZip,
                graphId); //@WebParam(name = "graphId") Long graphId)
            return result;
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * See net.sourceforge.ondex.Webservice2.ONDEXServiceWS method with the same
     * name
     */
    @WebResult(name = "oxl")
    @WebMethod(exclude = false)
    public String exportGraph(@WebParam(name = "ExcludeConceptsOfConceptClass")java.lang.String[] ExcludeConceptsOfConceptClass,
           @WebParam(name = "ExcludeRelationsOfRelationType")java.lang.String[] ExcludeRelationsOfRelationType,
           @WebParam(name = "ExcludeAttributeWithName")java.lang.String[] ExcludeAttributeWithName,
           @WebParam(name = "IncludeAttributesOfName")java.lang.String[] IncludeAttributesOfName,
           @WebParam(name = "IncludeOnlyConceptClass")java.lang.String[] IncludeOnlyConceptClass,
           @WebParam(name = "IncludeOnlyRelationType")java.lang.String[] IncludeOnlyRelationType,
           @WebParam(name = "pretty")java.lang.Boolean pretty,
           @WebParam(name = "ExportIsolatedConcepts")java.lang.Boolean ExportIsolatedConcepts,
     ////paramter GZip ignored as compression done by webservice.
           @WebParam(name = "compression") java.lang.String compression,
           @WebParam(name = "graphId") Long graphId)
           throws WebserviceException {
        try{
            ExportOXL exportOXL = new ExportOXL();
            String result = exportOXL.oxlExport(
                ExcludeConceptsOfConceptClass,
                ExcludeRelationsOfRelationType,
                ExcludeAttributeWithName,
                IncludeAttributesOfName,
                IncludeOnlyConceptClass,
                IncludeOnlyRelationType,
                pretty,
                ExportIsolatedConcepts,
                "false", //@WebParam(name = "GZip")java.lang.Boolean GZip,
                graphId);
                return result;
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * See net.sourceforge.ondex.Webservice2.ONDEXServiceWS method with the same
     * name
     */
    @WebResult(name = "WSGraph")
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
     * See net.sourceforge.ondex.Webservice2.ONDEXServiceWS method with the same
     * name
     */
    @WebResult(name = "WSGraphs")
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
     * See net.sourceforge.ondex.Webservice2.ONDEXServiceWS method with the same
     * name
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
     * See net.sourceforge.ondex.Webservice2.ONDEXServiceWS method with the same
     * name
     */
    @WebResult(name = "report")
    @WebMethod(exclude = false)
    public String importGraphLite(@WebParam(name = "graphId") Long graphId,
                            @WebParam(name = "xml") String xml)
                            throws WebserviceException {
        try{
            //return engine.importGraph(graphId, xml);
            ParserOXL parserOXL = new ParserOXL();
            byte[] oxlByteArray = null;
            return parserOXL.oxlParser(null, xml, oxlByteArray, graphId);
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * See net.sourceforge.ondex.Webservice2.ONDEXServiceWS method with the same
     * name
     */
    @WebResult(name = "report")
    @WebMethod(exclude = false)
    public String importGraph(@WebParam(name = "oxlString") String oxlString,
           @WebParam(name = "oxlByteArray") byte[] oxlByteArray,
           @WebParam(name = "IgnoreAttribute")java.lang.String[] IgnoreAttribute,
           @WebParam(name = "graphId") Long graphId)
            throws WebserviceException {
        try{
            //return engine.importGraph(graphId, xml);
            ParserOXL parserOXL = new ParserOXL();
            return parserOXL.oxlParser(IgnoreAttribute, oxlString, oxlByteArray, graphId);
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    // **** MetaData Methdoss ****

    /**
     * See net.sourceforge.ondex.core.MetaData method with the same name
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
     * See net.sourceforge.ondex.core.MetaData method with the same name
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
     * See net.sourceforge.ondex.core.MetaData method with the same name
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
     * See net.sourceforge.ondex.core.MetaData method with the same name
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
     * See net.sourceforge.ondex.core.MetaData method with the same name
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
     * See net.sourceforge.ondex.core.MetaData method with the same name
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
     * See net.sourceforge.ondex.core.MetaData method with the same name
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
            @WebParam(name = "specialisationOfAttributeNameId") String specialisationOfAttributeNameId)
            throws WebserviceException {
        //ogger.info("createAttributeName");
        //ogger.info(atttributeNameId);
        //ogger.info(unitId);
        try{
            String result = engine.createAttributeName(graphId, atttributeNameId, fullname,
                description, unitId, datatype, specialisationOfAttributeNameId);
            //ogger.info(result);
            return result;
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * See net.sourceforge.ondex.core.MetaData method with the same name
     */
    @WebResult(name = "conceptClassId")
    @WebMethod(exclude = false)
     public String createConceptClass(
            @WebParam(name = "graphId") Long graphId,
            @WebParam(name = "conceptClassId") String conceptClassId,
            @WebParam(name = "fullname") String fullname,
            @WebParam(name = "description") String description,
            @WebParam(name = "specialisationOfConceptClassId") String specialisationOfConceptClassId)
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
     * See net.sourceforge.ondex.core.MetaData method with the same name
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
     * See net.sourceforge.ondex.core.MetaData method with the same name
     */
    @WebResult(name = "evidenceTypeId")
    @WebMethod(exclude = false)
    public String createEvidenceType(@WebParam(name = "graphId") Long graphId,
                                     @WebParam(name = "evidenceTypeId") String evidenceTypeId,
                                     @WebParam(name = "fullname") String fullname,
                                     @WebParam(name = "description") String description)
            throws WebserviceException {
        try{
            return engine.createEvidenceType(graphId, evidenceTypeId, fullname,
                    description);
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * See net.sourceforge.ondex.core.MetaData method with the same name
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
            return engine.createRelationType(graphId, relationTypeId, fullname,
                    description, inverseName, isAntisymmetric, isReflexive,
                    isSymmetric, isTransitiv, specialisationOfRelationTypeId);
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * See net.sourceforge.ondex.core.MetaData method with the same name
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
     * See net.sourceforge.ondex.core.MetaData method with the same name
     */
    @WebMethod(exclude = false)
    @WebResult(name = "success")
    public boolean deleteAttributeName(@WebParam(name = "graphId") Long graphId,
                                       @WebParam(name = "attributeNameId") String attributeNameId)
            throws WebserviceException {
        try{
            return engine.deleteAttributeName(graphId, attributeNameId);
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * See net.sourceforge.ondex.core.MetaData method with the same name
     */
    @WebMethod(exclude = false)
    @WebResult(name = "success")
    public boolean deleteConceptClass(@WebParam(name = "graphId") Long graphId,
                                      @WebParam(name = "ConceptClassId") String ConceptClassId)
            throws WebserviceException {
        try{
            return engine.deleteConceptClass(graphId, ConceptClassId);
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * See net.sourceforge.ondex.core.MetaData method with the same name
     */
    @WebMethod(exclude = false)
    @WebResult(name = "success")
    public boolean deleteDataSource(@WebParam(name = "graphId") Long graphId,
                            @WebParam(name = "dataSourceId") String dataSourceId)
            throws WebserviceException {
        try{
            return engine.deleteDataSource(graphId, dataSourceId);
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }


    /**
     * See net.sourceforge.ondex.core.MetaData method with the same name
     */
    @WebMethod(exclude = false)
    @WebResult(name = "success")
    public boolean deleteEvidenceType(@WebParam(name = "graphId") Long graphId,
                                      @WebParam(name = "evidenceTypeId") String evidenceTypeId)
            throws WebserviceException {
        try{
            return engine.deleteEvidenceType(graphId, evidenceTypeId);
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * See net.sourceforge.ondex.core.MetaData method with the same name
     */
    @WebMethod(exclude = false)
    @WebResult(name = "success")
    public boolean deleteRelationType(@WebParam(name = "graphId") Long graphId,
                                      @WebParam(name = "relationTypeId") String relationTypeId)
            throws WebserviceException {
        try{
            return engine.deleteRelationType(graphId, relationTypeId);
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * See net.sourceforge.ondex.core.MetaData method with the same name
     */
    @WebMethod(exclude = false)
    @WebResult(name = "success")
    public boolean deleteUnit(@WebParam(name = "graphId") Long graphId,
                              @WebParam(name = "unitId") String unitId)
            throws WebserviceException {
        try{
            return engine.deleteUnit(graphId, unitId);
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * See net.sourceforge.ondex.core.MetaData method with the same name
     */
    @WebResult(name = "WSAttributeName")
    @WebMethod(exclude = false)
    public WSAttributeName getAttributeName(
            @WebParam(name = "graphId") Long graphId,
            @WebParam(name = "attributeNameId") String attributeNameId)
            throws WebserviceException {
        try{
            AttributeName result = engine.getAttributeName(graphId, attributeNameId);
            if (result == null) {
                return null;
            }
            return new WSAttributeName(result);
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * See net.sourceforge.ondex.core.MetaData method with the same name
     */
    @WebResult(name = "WSAttributeNames")
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
     * See net.sourceforge.ondex.core.MetaData method with the same name
     */
    @WebResult(name = "WSConceptClass")
    @WebMethod(exclude = false)
    public WSConceptClass getConceptClass(
            @WebParam(name = "graphId") Long graphId,
            @WebParam(name = "conceptClassId") String conceptClassId)
            throws WebserviceException {
        try{
            ConceptClass result = engine.getConceptClass(graphId, conceptClassId);
            if (result == null) {
                return null;
            }
            return new WSConceptClass(result);
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * See net.sourceforge.ondex.core.MetaData method with the same name
     */
    @WebResult(name = "WSConceptClasses")
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
     * See net.sourceforge.ondex.core.MetaData method with the same name
     */
    @WebResult(name = "WSDataSource")
    @WebMethod(exclude = false)
    public WSDataSource getDataSource(@WebParam(name = "graphId") Long graphId,
                      @WebParam(name = "dataSourceId") String dataSourceId)
            throws WebserviceException {
        try{
            DataSource result = engine.getDataSource(graphId, dataSourceId);
            if (result == null) {
                return null;
            }
            return new WSDataSource(result);
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * See net.sourceforge.ondex.core.MetaData method with the same name
     */
    @WebResult(name = "WSDataSources")
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
     * See net.sourceforge.ondex.core.MetaData method with the same name
     */
    @WebResult(name = "WSEvidenceType")
    @WebMethod(exclude = false)
    public WSEvidenceType getEvidenceType(
            @WebParam(name = "graphId") Long graphId,
            @WebParam(name = "evidenceTypeId") String evidenceTypeId)
            throws WebserviceException {
        try{
            EvidenceType result = engine.getEvidenceType(graphId, evidenceTypeId);
            if (result == null) {
                return null;
            }
            return new WSEvidenceType(result);
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * See net.sourceforge.ondex.core.MetaData method with the same name
     */
    @WebResult(name = "WSEvidenceTypes")
    @WebMethod(exclude = false)
    public List<WSEvidenceType> getEvidenceTypes(
            @WebParam(name = "graphId") Long graphId)
            throws WebserviceException {
        try{
            return engine.getEvidenceTypes(graphId);
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * See net.sourceforge.ondex.core.MetaData method with the same name
     */
    @WebResult(name = "WSRelationType")
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
     * See net.sourceforge.ondex.core.MetaData method with the same name
     */
    @WebResult(name = "WSRelationTypes")
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
     * See net.sourceforge.ondex.core.MetaData method with the same name
     */
    @WebResult(name = "WSUnit")
    @WebMethod(exclude = false)
    public WSUnit getUnit(@WebParam(name = "graphId") Long graphId,
                          @WebParam(name = "unitId") String unitId)
            throws WebserviceException {
        try{
            Unit result = engine.getUnit(graphId, unitId);
            if (result == null) {
                return null;
            }
            return new WSUnit(result);
        } catch (WebserviceException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException (e, logger);
        }
    }

    /**
     * See net.sourceforge.ondex.core.MetaData method with the same name
     */
    @WebResult(name = "WSUnits")
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

}
