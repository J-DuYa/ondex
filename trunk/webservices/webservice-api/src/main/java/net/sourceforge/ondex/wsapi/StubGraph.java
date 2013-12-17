/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sourceforge.ondex.wsapi;

import net.sourceforge.ondex.core.*;

import java.util.Collection;
import java.util.Set;

/**
 *
 * @author christian
 */
public class StubGraph implements ONDEXGraph{
    private String name;
    private long sid;
    private String directory;
    
    public StubGraph(String name, long sid, String directory){
        this.name = name;
        this.sid = sid;
        this.directory = directory;
    }
    
    public String getDirectory(){
        return directory;
    }
    
    @Override
    public ONDEXConcept createConcept(String pid, String annotation, String description, DataSource elementOf, ConceptClass ofType, Collection<EvidenceType> evidence) {
        throw new UnsupportedOperationException("Illegal call to StubGraph. You must load graph from "+directory);
    }

    @Override
    public ONDEXRelation createRelation(ONDEXConcept fromConcept, ONDEXConcept toConcept, RelationType ofType, Collection<EvidenceType> evidence) {
        throw new UnsupportedOperationException("Illegal call to StubGraph. You must load graph from "+directory);
    }

    @Override
    public boolean deleteConcept(int id) {
        throw new UnsupportedOperationException("Illegal call to StubGraph. You must load graph from "+directory);
    }

    @Override
    public boolean deleteRelation(int id) {
        throw new UnsupportedOperationException("Illegal call to StubGraph. You must load graph from "+directory);
    }

    @Override
    public boolean deleteRelation(ONDEXConcept fromConcept, ONDEXConcept toConcept, RelationType ofType) {
        throw new UnsupportedOperationException("Illegal call to StubGraph. You must load graph from "+directory);
    }

    @Override
    public ONDEXConcept getConcept(int id) {
        throw new UnsupportedOperationException("Illegal call to StubGraph. You must load graph from "+directory);
    }

    @Override
    public Set<ONDEXConcept> getConcepts() {
        throw new UnsupportedOperationException("Illegal call to StubGraph. You must load graph from "+directory);
    }

    @Override
    public Set<ONDEXConcept> getConceptsOfAttributeName(AttributeName an) {
        throw new UnsupportedOperationException("Illegal call to StubGraph. You must load graph from "+directory);
    }

    @Override
    public Set<ONDEXConcept> getConceptsOfConceptClass(ConceptClass cc) {
        throw new UnsupportedOperationException("Illegal call to StubGraph. You must load graph from "+directory);
    }

    @Override
    public Set<ONDEXConcept> getConceptsOfTag(ONDEXConcept ac) {
        throw new UnsupportedOperationException("Illegal call to StubGraph. You must load graph from "+directory);
    }

    @Override
    public Set<ONDEXConcept> getConceptsOfDataSource(DataSource dataSource) {
        throw new UnsupportedOperationException("Illegal call to StubGraph. You must load graph from "+directory);
    }

    @Override
    public Set<ONDEXConcept> getConceptsOfEvidenceType(EvidenceType et) {
        throw new UnsupportedOperationException("Illegal call to StubGraph. You must load graph from "+directory);
    }

    @Override
    public Set<ONDEXConcept> getAllTags() {
        throw new UnsupportedOperationException("Illegal call to StubGraph. You must load graph from "+directory);
    }

    @Override
    public EntityFactory getFactory() {
        throw new UnsupportedOperationException("Illegal call to StubGraph. You must load graph from "+directory);
    }

    @Override
    public ONDEXGraphMetaData getMetaData() {
        throw new UnsupportedOperationException("Illegal call to StubGraph. You must load graph from "+directory);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ONDEXRelation getRelation(int id) {
        throw new UnsupportedOperationException("Illegal call to StubGraph. You must load graph from "+directory);
    }

    @Override
    public ONDEXRelation getRelation(ONDEXConcept fromConcept, ONDEXConcept toConcept, RelationType ofType) {
        throw new UnsupportedOperationException("Illegal call to StubGraph. You must load graph from "+directory);
    }

    @Override
    public Set<ONDEXRelation> getRelations() {
        throw new UnsupportedOperationException("Illegal call to StubGraph. You must load graph from "+directory);
    }

    @Override
    public Set<ONDEXRelation> getRelationsOfAttributeName(AttributeName an) {
        throw new UnsupportedOperationException("Illegal call to StubGraph. You must load graph from "+directory);
    }

    @Override
    public Set<ONDEXRelation> getRelationsOfConcept(ONDEXConcept concept) {
        throw new UnsupportedOperationException("Illegal call to StubGraph. You must load graph from "+directory);
    }

    @Override
    public Set<ONDEXRelation> getRelationsOfConceptClass(ConceptClass cc) {
        throw new UnsupportedOperationException("Illegal call to StubGraph. You must load graph from "+directory);
    }

    @Override
    public Set<ONDEXRelation> getRelationsOfTag(ONDEXConcept ac) {
        throw new UnsupportedOperationException("Illegal call to StubGraph. You must load graph from "+directory);
    }

    @Override
    public Set<ONDEXRelation> getRelationsOfDataSource(DataSource dataSource) {
        throw new UnsupportedOperationException("Illegal call to StubGraph. You must load graph from "+directory);
    }

    @Override
    public Set<ONDEXRelation> getRelationsOfEvidenceType(EvidenceType et) {
        throw new UnsupportedOperationException("Illegal call to StubGraph. You must load graph from "+directory);
    }

    @Override
    public Set<ONDEXRelation> getRelationsOfRelationType(RelationType rt) {
        throw new UnsupportedOperationException("Illegal call to StubGraph. You must load graph from "+directory);
    }

    @Override
    public boolean isReadOnly() {
        throw new UnsupportedOperationException("Illegal call to StubGraph. You must load graph from "+directory);
    }

    @Override
    public long getSID() {
        return sid;
    }

}
