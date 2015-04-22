package net.sourceforge.ondex.export.json;

import java.util.Set;
import net.sourceforge.ondex.core.ONDEXConcept;
import org.json.simple.JSONObject;

/**
 * Build node json objects using their various attributes.
 * @author Ajit Singh
 * @version 26/02/15
 */
public class AddConceptNodeInfo {

 private String defaultVisibility= null;

 public AddConceptNodeInfo() {
  defaultVisibility= ConceptVisibility.element.toString();
 }

 public JSONObject getNodeJson(ONDEXConcept con, Set<Integer> conceptsUsedInRelations) {

  JSONObject node= new JSONObject();
  JSONObject nodeData= new JSONObject();
  int conId= con.getId(); // concept ID.
  String conceptID= String.valueOf(conId);
  String conceptName= " ";
  if(con.getConceptName() != null) {
     if(con.getConceptName().getName() != null) {
        conceptName= con.getConceptName().getName(); // concept name.
       }
    }
  String conceptType= con.getOfType().getFullname(); // conceptType.
  if(conceptType.equalsIgnoreCase("Compound")) {
     conceptType= "SNP";
    }
  String conceptShape;
  String conceptColour;
  String conceptVisibility= defaultVisibility; // default

  nodeData.put(JSONAttributeNames.ID, conceptID);
  nodeData.put(JSONAttributeNames.VALUE, conceptName);
  nodeData.put("conceptType", conceptType); // conceptType ("ofType").  
  nodeData.put(JSONAttributeNames.PID, con.getPID());
  nodeData.put(JSONAttributeNames.ANNOTATION, con.getAnnotation().replaceAll("(\\r|\\n)", " "));
  // Set the shape, color & visibility attributes for this Concept.
  String[] nodeAttributes= determineNodeColourAndShape(conceptType);
  conceptShape= nodeAttributes[0];
  conceptColour= nodeAttributes[1];

  nodeData.put("conceptShape", conceptShape);
  nodeData.put("conceptColor", conceptColour);

  if(conceptsUsedInRelations.contains(conId)) {
     conceptVisibility= ConceptVisibility.element.toString();
//     System.out.println("ConceptID: "+ conId +" , visibleDisplay: "+ conceptVisibility);
    }
  else {
     conceptVisibility= ConceptVisibility.none.toString();
//     System.out.println("ConceptID: "+ conId +" , visibleDisplay: "+ conceptVisibility);
    }

  nodeData.put("visibleDisplay", conceptVisibility);

  node.put("data", nodeData); // the node's data.
  node.put("group", "nodes"); // Grouping nodes together
 
  return node;
 }

 private String[] determineNodeColourAndShape(String conType) {
  String[] attr= new String[2];
  String shape= ConceptShape.triangle.toString(); // default (for concept Type: 'Gene').
  String colour= ConceptColour.cyan.toString(); // default (for concept Type: 'Gene').

  // Determine the shape & colour attributes for this concept based on the concept type.
  if(conType.equals(ConceptType.Biological_Process.toString())) {
     shape= ConceptShape.pentagon.toString();
     colour= ConceptColour.teal.toString();
    }
  else if(conType.equals(ConceptType.Cellular_Component.toString())) {
     shape= ConceptShape.pentagon.toString();
     colour= ConceptColour.lightGreen.toString();
    }
  else if(conType.equals("Protein Domain")) {
     shape= ConceptShape.pentagon.toString();
     colour= ConceptColour.grey.toString();
    }
  else if(conType.equals(ConceptType.Pathway.toString())) {
     shape= ConceptShape.heptagon.toString();
     colour= ConceptColour.lightGreen.toString();
    }
  else if(conType.equals(ConceptType.Reaction.toString())) {
     shape= ConceptShape.heptagon.toString();
     colour= ConceptColour.yellow.toString();
    }
  else if(conType.equals(ConceptType.Publication.toString())) {
     shape= ConceptShape.rectangle.toString();
     colour= ConceptColour.orange.toString();
    }
  else if(conType.equals(ConceptType.Protein.toString())) {
     shape= ConceptShape.ellipse.toString();
     colour= ConceptColour.red.toString();
    }
  else if(conType.equals(ConceptType.Enzyme.toString())) {
     shape= ConceptShape.heptagon.toString();
     colour= ConceptColour.salmon.toString();
    }
  else if(conType.equals(ConceptType.Molecular_Function.toString())) {
     shape= ConceptShape.pentagon.toString();
     colour= ConceptColour.purple.toString();
    }
  else if((conType.equals(ConceptType.Enzyme_Classification.toString())) || (conType.equals("Enzyme Classification"))) {
     shape= ConceptShape.pentagon.toString();
     colour= ConceptColour.pink.toString();
    }
  else if(conType.equals("Trait Ontology")) {
     shape= ConceptShape.pentagon.toString();
     colour= ConceptColour.yellow.toString();
    }
  else if(conType.equals("Quantitative Trait Locus")) {
     shape= ConceptShape.triangle.toString();
     colour= ConceptColour.blue.toString();
    }
  else if((conType.equals(ConceptType.Compound.toString())) || (conType.equals(ConceptType.SNP.toString()))) {
     shape= ConceptShape.heptagon.toString();
     colour= ConceptColour.teal.toString();
    }
  else if(conType.equals(ConceptType.Phenotype.toString())) {
     shape= ConceptShape.rectangle.toString();
     colour= ConceptColour.yellow.toString();
    }
  
  // Set the determined attribute values;
  attr[0]= shape;
  attr[1]= colour;

  return attr;
 }
}
