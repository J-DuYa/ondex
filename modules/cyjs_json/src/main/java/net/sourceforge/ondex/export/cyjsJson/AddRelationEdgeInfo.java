package net.sourceforge.ondex.export.cyjsJson;

import net.sourceforge.ondex.core.ONDEXRelation;
import org.json.simple.JSONObject;

/**
 * Build edge json objects using their various attributes.
 * @author Ajit Singh
 * @version 09/01/15
 */
public class AddRelationEdgeInfo {
    
 public JSONObject getEdgeJson(ONDEXRelation rel) {
  JSONObject edge= new JSONObject();
  JSONObject edgeData= new JSONObject();

  String relationID= String.valueOf(rel.getId()); // relation ID.
  String sourceConcept= String.valueOf(rel.getFromConcept().getId()); // relation source ID.
  String targetConcept= String.valueOf(rel.getToConcept().getId()); // relation target ID.
  String edgeLabel= rel.getOfType().getFullname(); // relation type label.

  edgeData.put(/*"id"*/JSONAttributeNames.ID, "e"+relationID);
  edgeData.put("source", sourceConcept);
  edgeData.put("target", targetConcept);
  edgeData.put("label", edgeLabel);
  
  // Set the edge color for this Relation.
  String edgeColour= determineEdgeColour(edgeLabel);
  edgeData.put("edgeColor", edgeColour);

  edge.put("data", edgeData); // the edge's data.
  edge.put("group", "edges"); // Grouping edges together

  return edge;
 }

 private String determineEdgeColour(String edgeLbl) {
  String colour= EdgeColour.grey.toString(); // default.

  // Determine the colour for this relation based on the edge label.
  if(edgeLbl.equals(EdgeLabel.published_in.toString())) {
     colour= EdgeColour.orange.toString();
    }
  else if(edgeLbl.equals(EdgeLabel.encodes.toString())) {
     colour= EdgeColour.grey.toString();
    }
  else if(edgeLbl.equals(EdgeLabel.participates_in.toString())) {
     colour= EdgeColour.teal.toString();
    }
  else if(edgeLbl.equals(EdgeLabel.has_similar_sequence.toString())) {
     colour= EdgeColour.red.toString();
    }
  else if((edgeLbl.equals(EdgeLabel.has_protein_domain.toString())) || 
          (edgeLbl.equals(EdgeLabel.interacts_with.toString())) || 
          (edgeLbl.equals(EdgeLabel.cooccurs_with.toString()))) {
     colour= EdgeColour.blue.toString();
    }
  else if(edgeLbl.equals(EdgeLabel.located_in.toString())) {
     colour= EdgeColour.lightGreen.toString();
    }
  else if(edgeLbl.equals(EdgeLabel.has_function.toString())) {
     colour= EdgeColour.purple.toString();
    }

  return colour;
 }

}
