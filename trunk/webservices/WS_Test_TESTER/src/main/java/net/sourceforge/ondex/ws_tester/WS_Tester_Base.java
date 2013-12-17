package net.sourceforge.ondex.ws_tester;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import net.sourceforge.ondex.webservice.client.*;
import net.sourceforge.ondex.webservice.client.WebserviceException_Exception;

/**
 * Program for creating plugin wrappers.
 *
 * Designed only to run on Christian Machine
 * 
 * @author BrennincC based on stuff by taubertj
 */
public class WS_Tester_Base {

    protected static final String WEBSERVICE_ROOT = "c:/Users/Christian/Ondex/webservices/";
    protected static final String TEST_DATA = WEBSERVICE_ROOT + "WS_Test_TESTER/data/";
    protected static final String ONDEX_METADATA = TEST_DATA + "xml/ondex_metadata.xml";
    protected static final String RESULT_DATA = TEST_DATA + "results/";
    protected static final String INPUT_DATA = TEST_DATA + "inputs/";

    static private int UNSET = -1;
    protected int concpetClassess = UNSET;
    protected int cvs = UNSET;
    protected int relationTypes = UNSET;
    protected int evidenceTypes = UNSET;
    protected int relations = UNSET;
    protected int concepts = UNSET;

    protected static ONDEXapiWS api_service;

    protected String testName;

    public WS_Tester_Base(String name) throws MalformedURLException
    {
        if (api_service == null){
            getGraphServer();
        }
        testName = name;
 	}

    private void getGraphServer() throws MalformedURLException
    {
        URL url = new URL("http://rpc466.cs.man.ac.uk:8080/ondex/services/ONDEXapiWS");
		ONDEXapiWSService service = new ONDEXapiWSService(url);
        api_service = service.getONDEXapiWSPort();
 	}

    public void relationInfo(WSRelation relation){
        String relationType = relation.getOfType().getValue();
        String toConceptname = relation.getToConceptName().getValue();
        String fromConceptname = relation.getFromConceptName().getValue();
        System.out.println ("Relation: " + relationType + " from " + fromConceptname + " to " + fromConceptname);
    }

    public int relationsInfo(long graphId, boolean details) throws WebserviceException_Exception{
        WSRelations wsRelations = api_service.getRelations(graphId);
        List<WSRelation> relationList = wsRelations.getWSRelation();
        if (this.relations == UNSET) {
            System.out.println("        relations = "+relationList.size() + ";");
        } else if (this.relations == relationList.size()) {
            if (details) {
                System.out.println("Correct! Relations = "+relationList.size() + ";");
            }
        } else {
            System.out.println ("******* WARNING *******");
            System.out.println("**** UNEXPECTED RESULT FOR TEST " +  testName + " ******");
            System.out.println("Found! relations = "+relationList.size() + ";");
            System.out.println("Expected! " + this.relations);
            System.out.println ("******* WARNING *******");
        }
        if (details) {
            WSRelation[] relationArray = new WSRelation[0];
            relationArray = relationList.toArray(relationArray);
            for (int i = 0; i< relationArray.length; i++){
                relationInfo(relationArray[i]);
            }
        }
        return relationList.size();
    }

    public void conceptInfo(WSConcept concept){
        String elementOf = concept.getElementOf().getValue();
        String name = concept.getName().getValue();
        String type = concept.getOfType().getValue();
        String annotation = concept.getAnnotation().getValue();
        String description = concept.getDescription().getValue();
        String pid = concept.getPID().getValue();
        System.out.print("Concept: " + pid + " name " + name + " type " + type + " desc " + description + " annot " + annotation);
        System.out.println(" elementof " + elementOf);
    }

    public int conceptsInfo(long graphId, boolean details) throws WebserviceException_Exception{
        WSConcepts wsConcepts = api_service.getConcepts(graphId);
        List<WSConcept> conceptsList = wsConcepts.getWSConcept();
        if (concepts == UNSET) {
            System.out.println("        concepts = " + conceptsList.size() + ";");
        } else if (concepts == conceptsList.size()) {
            if (details) {
                System.out.println("Correct! Concepts = " + conceptsList.size() + ";");
            }
        } else {
            System.out.println ("******* WARNING *******");
            System.out.println("**** UNEXPECTED RESULT FOR TEST " +  testName + " ******");
            System.out.println("Found! Concepts = " + conceptsList.size() + ";");
            System.out.println("Expected! " + concepts);
            System.out.println ("******* WARNING *******");
        }
        if (details) {
            WSConcept[] conceptArray = new WSConcept[0];
            conceptArray = conceptsList.toArray(conceptArray);
            for (int i = 0; i< conceptArray.length; i++){
               conceptInfo(conceptArray[i]);
            }
        }
        return conceptsList.size();
    }

    public void classInfo(WSConceptClass conceptClass){
        String id = conceptClass.getId().getValue();
        String fullName = conceptClass.getFullname().getValue();
        String description = conceptClass.getDescription().getValue();
        String spec = conceptClass.getSpecialisationOf().getValue();
        System.out.println ("ConceptClass " + id + " name " + fullName + " desc " + description + " spec " + spec);
    }

    public int conceptClassesInfo(long graphId, boolean details) throws WebserviceException_Exception{
        WSConceptClasses classes = api_service.getConceptClasses(graphId);
        List<WSConceptClass> classList = classes.getWSConceptClass();
        if (concepts == UNSET) {
            System.out.println("        concpetClassess = "+ classList.size() + ";");
        } else if (concpetClassess == classList.size()) {
            if (details) {
                System.out.println("Correct! ConceptClasses = " + classList.size() + ";");
            }
        } else {
            System.out.println ("******* WARNING *******");
            System.out.println("**** UNEXPECTED RESULT FOR TEST " +  testName + " ******");
            System.out.println("Found! ConceptClasses = " + classList.size() + ";");
            System.out.println("Expected! " +  concpetClassess);
            System.out.println ("******* WARNING *******");
        }
        if (details) {
            WSConceptClass[] classArray = new WSConceptClass[0];
            classArray = classList.toArray(classArray);
            for (int i = 0; i< classArray.length; i++){
               classInfo(classArray[i]);
            }
        }
        return classList.size();
    }

    public void cvInfo(WSDataSource cv){
        String id = cv.getId().getValue();
        String fullName = cv.getFullname().getValue();
        String description = cv.getDescription().getValue();
        System.out.println ("CV " + id + " name " + fullName + " desc " + description);
    }

    public int cvsInfo(long graphId, boolean details) throws WebserviceException_Exception{
        WSDataSources wsCVs = api_service.getDataSources(graphId);
        List<WSDataSource> cvsList = wsCVs.getWSDataSource();
        if (cvs == UNSET) {
            System.out.println("        cvs = "+ cvsList.size() + ";");
        } else if (cvs == cvsList.size()) {
            if (details) {
                System.out.println("Correct! CVs = " + cvsList.size() + ";");
            }
        } else {
            System.out.println ("******* WARNING *******");
            System.out.println("**** UNEXPECTED RESULT FOR TEST " +  testName + " ******");
            System.out.println("Found! CVs = " + cvsList.size() + ";");
            System.out.println("Expected! " +  cvs);
            System.out.println ("******* WARNING *******");
        }
        if (details) {
            WSDataSource[] cvArray = new WSDataSource[0];
            cvArray = cvsList.toArray(cvArray);
            for (int i = 0; i< cvArray.length; i++){
               cvInfo(cvArray[i]);
            }
        }
        return cvsList.size();
    }

    public void evidenceTypeInfo(WSEvidenceType type){
        String id = type.getId().getValue();
        String fullName = type.getFullname().getValue();
        String description = type.getDescription().getValue();
        System.out.println("RelationType " + id + " name " + fullName + " desc " + description);
    }

    public int evidenceTypesInfo(long graphId, boolean details) throws WebserviceException_Exception{
        WSEvidenceTypes types = api_service.getEvidenceTypes(graphId);
        List<WSEvidenceType> typesList = types.getWSEvidenceType();
        if (evidenceTypes == UNSET) {
            System.out.println("        evidenceTypes = "+ typesList.size() + ";");
        } else if (evidenceTypes == typesList.size()) {
            if (details) {
                System.out.println("Correct! evidenceTypes = " + typesList.size() + ";");
            }
        } else {
            System.out.println ("******* WARNING *******");
            System.out.println("**** UNEXPECTED RESULT FOR TEST " +  testName + " ******");
            System.out.println("Found! evidenceTypes = " + typesList.size() + ";");
            System.out.println("Expected! " +  evidenceTypes);
            System.out.println ("******* WARNING *******");
        }
        if (details) {
            WSEvidenceType[] typeArray = new WSEvidenceType[0];
            typeArray = typesList.toArray(typeArray);
            for (int i = 0; i< typeArray.length; i++){
               evidenceTypeInfo(typeArray[i]);
            }
        }
        return typesList.size();
    }

    public void relationTypeInfo(WSRelationType type){
        String id = type.getId().getValue();
        String fullName = type.getFullname().getValue();
        String description = type.getDescription().getValue();
        String inverse = type.getInverseName().getValue();
        String spec = type.getSpecialisationOf().getValue();
        System.out.print("RelationType " + id + " name " + fullName + " desc " + description + " inverse " + inverse);
        System.out.println(" spec " + spec);
    }

    public int relationTypesInfo(long graphId, boolean details) throws WebserviceException_Exception{
        WSRelationTypes types = api_service.getRelationTypes(graphId);
        List<WSRelationType> typesList = types.getWSRelationType();
        if (relationTypes == UNSET) {
            System.out.println("        relationTypes = "+ typesList.size() + ";");
        } else if (relationTypes == typesList.size()) {
            if (details) {
                System.out.println("Correct! relationTypes = " + typesList.size() + ";");
            }
        } else {
            System.out.println ("******* WARNING *******");
            System.out.println("**** UNEXPECTED RESULT FOR TEST " +  testName + " ******");
            System.out.println("Found! relationTypes = " + typesList.size() + ";");
            System.out.println("Expected! " +  relationTypes);
            System.out.println ("******* WARNING *******");
        }
        if (details) {
            WSRelationType[] typeArray = new WSRelationType[0];
            typeArray = typesList.toArray(typeArray);
            for (int i = 0; i< typeArray.length; i++){
               relationTypeInfo(typeArray[i]);
            }
        }
        return typesList.size();
    }

    public void graphInfo(long graphId, boolean details) throws WebserviceException_Exception{
        conceptClassesInfo(graphId, details);
        cvsInfo(graphId, details);
        relationTypesInfo(graphId, details);
        evidenceTypesInfo(graphId, details);
        relationsInfo(graphId, details);
        conceptsInfo(graphId, details);
    }

    public void writeOXL(long graphid, String fileName) throws WebserviceException_Exception, IOException
    {
        String oxl = api_service.exportGraphLite(graphid);
        String oxlFile =  RESULT_DATA + fileName;
        BufferedWriter writer = new BufferedWriter(new FileWriter(new File(oxlFile)));
        writer.write(oxl);
        writer.close();
 	}


}
