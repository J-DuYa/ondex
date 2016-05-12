package net.sourceforge.ondex.parser.uniprot.sink;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author singha
 * @created 05/02/2016
 * @modified 19/02/2016
 */
public class Disease {

	private String diseaseID;
	private String diseaseName;
	private String diseaseAcronym;
	private String diseaseEvidence;
	private String diseaseDescription;
	private String mimID;
	private String diseaseText;
	private List<DbLink> references= new ArrayList<DbLink> ();

	public String getDiseaseID() {
		return diseaseID;
	}
	public void setDiseaseID(String diseaseID) {
		this.diseaseID = diseaseID;
	}

	public String getDiseaseName() {
		return diseaseName;
	}
	public void setDiseaseName(String diseaseName) {
		this.diseaseName = diseaseName;
	}

	public String getDiseaseAcronym() {
		return diseaseAcronym;
	}
	public void setDiseaseAcronym(String diseaseAcronym) {
		this.diseaseAcronym = diseaseAcronym;
	}

	public String getDiseaseEvidence() {
		return diseaseEvidence;
	}
	public void setDiseaseEvidence(String diseaseEvidence) {
		this.diseaseEvidence = diseaseEvidence;
	}

	public String getDiseaseDescription() {
		return diseaseDescription;
	}
	public void setDiseaseDescription(String diseaseDescription) {
		this.diseaseDescription = diseaseDescription;
	}

	public String getMimID() {
		return mimID;
	}
	public void setMimID(String mimID) {
		this.mimID = mimID;
	}

	public String getDiseaseText() {
		return diseaseText;
	}
	public void setDiseaseText(String diseaseText) {
		this.diseaseText = diseaseText;
	}

	public String toString(){
		return this.diseaseID;
	}

	public void addReference(DbLink dbLink){
		this.references.add(dbLink);
	}

	public List<DbLink> getReferences() {
		return references;
	}

}
