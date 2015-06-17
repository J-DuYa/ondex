package net.sourceforge.ondex.export.cyjsJson;

import java.util.Set;
import net.sourceforge.ondex.core.Attribute;
import net.sourceforge.ondex.core.ONDEXConcept;
import org.json.simple.JSONObject;

/**
 * Build node json objects using their various attributes.
 * @author Ajit Singh
 * @version 15/05/15
 */
public class AddConceptNodeInfo {

 private String defaultVisibility= null;

 public AddConceptNodeInfo() {
  defaultVisibility= ElementVisibility.none.toString();
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
  if(conceptType.equals("")) {
     conceptType= ConceptType.Phenotype.toString(); // default.
    }

  // For concept Type: "SNP".
  if(conceptType.equalsIgnoreCase(ConceptType.Compound.toString())) {
     conceptType= ConceptType.SNP.toString();
    }

  String conceptShape;
  String conceptColour;
  String conceptSize= "18px"; // default.
  String conceptVisibility= defaultVisibility; // default (element, i.e., true).

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

/*  if(conceptsUsedInRelations.contains(conId)) {
     conceptVisibility= ElementVisibility.element.toString();
//     System.out.println("ConceptID: "+ conId +" , visibleDisplay: "+ conceptVisibility);
    }
  else {
     conceptVisibility= ElementVisibility.none.toString();
//     System.out.println("ConceptID: "+ conId +" , visibleDisplay: "+ conceptVisibility);
    }*/

  // Set concept visibility, concept size (height & width) & flagged status from Attributes.
  String attrID, visibility, flagged= "false";
  int con_size;
  Set<Attribute> concept_attributes= con.getAttributes(); // get all concept Attributes.
  for(Attribute attr : concept_attributes) {
      attrID= attr.getOfType().getId(); // Attribute ID.
      if(attrID.equals("")) {
         attrID= attr.getOfType().getFullname();
        }

      if(attrID.equals("visible")) { // set visibility.
         visibility= attr.getValue().toString();
         if(visibility.equals("false")) {
            conceptVisibility= ElementVisibility.none.toString();
           }
         else {
           conceptVisibility= ElementVisibility.element.toString();
          }
        }
      else if(attrID.equals("size")) { // set size.
              con_size= Integer.parseInt(attr.getValue().toString());
              if(con_size > 18 && con_size <= 30) {
                 con_size= 22;
                }
              else if(con_size > 30) {
                      con_size= 26;
                     }
//              conceptSize= attr.getValue().toString() +"px";
              conceptSize= String.valueOf(con_size) +"px";
             }

      else if(attrID.equals("flagged")) { // set flagged status.
              flagged= attr.getValue().toString(); // true
             }
     }

  nodeData.put("conceptDisplay", conceptVisibility);
  nodeData.put("conceptSize", conceptSize);
  nodeData.put("flagged", flagged);

  node.put("data", nodeData); // the node's data.
  node.put("group", "nodes"); // Grouping nodes together
 
  return node;
 }

 private String[] determineNodeColourAndShape(String conType) {
  String[] attr= new String[2];
  String shape= ConceptShape.rectangle.toString(); // default (for concept Type: 'Phenotype').
  String colour= ConceptColour.greenYellow.toString(); // default (for concept Type: 'Phenotype').

  // Determine the shape & colour attributes for this concept based on the concept type.
  if(conType.equals(ConceptType.Biological_Process.toString())) {
     shape= ConceptShape.pentagon.toString();
     colour= ConceptColour.teal.toString();
    }
  else if(conType.equals(ConceptType.Cellular_Component.toString())) {
     shape= ConceptShape.pentagon.toString();
     colour= ConceptColour.springGreen.toString();
    }
  else if(conType.equals("Protein Domain")) {
     shape= ConceptShape.pentagon.toString();
     colour= ConceptColour.lightGrey.toString();
    }
  else if(conType.equals(ConceptType.Pathway.toString())) {
     shape= ConceptShape.star.toString();
     colour= ConceptColour.springGreen.toString();
    }
  else if(conType.equals(ConceptType.Reaction.toString())) {
     shape= ConceptShape.star.toString();
     colour= ConceptColour.greenYellow.toString();
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
     shape= ConceptShape.star.toString();
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
     colour= ConceptColour.greenYellow.toString();
    }
  else if(conType.equals("Quantitative Trait Locus")) {
     shape= ConceptShape.triangle.toString();
     colour= ConceptColour.red.toString();
    }
  else if(conType.equals(ConceptType.Scaffold.toString())) {
     shape= ConceptShape.triangle.toString();
     colour= ConceptColour.blue.toString();
    }
  else if((conType.equals(ConceptType.Compound.toString())) || (conType.equals(ConceptType.SNP.toString()))) {
     shape= ConceptShape.star.toString();
     colour= ConceptColour.teal.toString();
    }
  else if(conType.equals(ConceptType.Phenotype.toString())) {
     shape= ConceptShape.rectangle.toString();
     colour= ConceptColour.greenYellow.toString();
    }
  
  // Set the determined attribute values;
  attr[0]= shape;
  attr[1]= colour;

  return attr;
 }
}
