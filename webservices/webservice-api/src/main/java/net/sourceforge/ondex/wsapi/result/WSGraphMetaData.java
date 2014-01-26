package net.sourceforge.ondex.wsapi.result;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author christian
 */
public class WSGraphMetaData {

    private List<ConceptClass> conceptClasses;

    private List<DataSource> dataSources;

    private List<RelationType> relationTypes;

    private List<EvidenceType> evidenceTypes;

    public WSGraphMetaData(){
        //TEST
        conceptClasses = new ArrayList<ConceptClass>();
        dataSources = new ArrayList<DataSource>();
        relationTypes = new ArrayList<RelationType>();
        evidenceTypes = new ArrayList<EvidenceType>();
    }

    public void addConceptClass(net.sourceforge.ondex.core.ConceptClass conceptClass, int count){
        ConceptClass temp = new ConceptClass(conceptClass.getId(),conceptClass.getFullname(),count);
        conceptClasses.add(temp);
    }

    public void addDataSource(net.sourceforge.ondex.core.DataSource dataSource, int count){
        DataSource temp = new DataSource(dataSource.getId(),dataSource.getFullname(),count);
        dataSources.add(temp);
    }

    public void addRelationType(net.sourceforge.ondex.core.RelationType relationType, int count){
        RelationType temp = new RelationType(relationType.getId(),relationType.getFullname(),count);
        relationTypes.add(temp);
    }

    public void addEvidenceType(net.sourceforge.ondex.core.EvidenceType evidenceType, int count){
        EvidenceType temp = new EvidenceType(evidenceType.getId(),evidenceType.getFullname(),count);
        evidenceTypes.add(temp);
    }

    public void setConceptClasses(List<ConceptClass> conceptClasses){
        this.conceptClasses = conceptClasses;
    }

    public List<ConceptClass> getConceptClasses(){
        return conceptClasses;
    }
    
    public void setDataSource(List<DataSource> dataSources){
        this.dataSources = dataSources;
    }

    public List<DataSource> getDataSources(){
        return dataSources;
    }

    public void setRelationTypes(List<RelationType> relationType){
        this.relationTypes = relationTypes;
    }

    public List<RelationType> getRelationTypes(){
        return relationTypes;
    }

    public void setEvidenceTypes(List<EvidenceType> evidenceTypes){
        this.evidenceTypes = evidenceTypes;
    }

    public List<EvidenceType> getEvidenceTypes(){
        return evidenceTypes;
    }

    public class ConceptClass{

        public ConceptClass(){};

        private ConceptClass(String id, String name, int count){
            this.id = id;
            this.name = name;
            this.count = count;
        }

        private String id;

        private String name;

        private int count;

        public String getId(){
            return id;
        }
        public void setId(String id){
            this.id = id;
        }
        public String getName(){
            return name;
        }
        public void setName(String name){
            this.name = name;
        }
        public int getCount(){
            return count;
        }
        public void setCount(int count){
            this.count = count;
        }

    }

    public class DataSource{

        public DataSource(){};

        private DataSource(String id, String name, int count){
            this.id = id;
            this.name = name;
            this.count = count;
        }

        private String id;

        private String name;

        private int count;

        public String getId(){
            return id;
        }
        public void setId(String id){
            this.id = id;
        }
        public String getName(){
            return name;
        }
        public void setName(String name){
            this.name = name;
        }
        public int getCount(){
            return count;
        }
        public void setCount(int count){
            this.count = count;
        }

    }

    public class RelationType{

        public RelationType(){};

        private RelationType(String id, String name, int count){
            this.id = id;
            this.name = name;
            this.count = count;
        }

        private String id;

        private String name;

        private int count;

        public String getId(){
            return id;
        }
        public void setId(String id){
            this.id = id;
        }
        public String getName(){
            return name;
        }
        public void setName(String name){
            this.name = name;
        }
        public int getCount(){
            return count;
        }
        public void setCount(int count){
            this.count = count;
        }

    }

    public class EvidenceType{

        public EvidenceType(){};

        private EvidenceType(String id, String name, int count){
            this.id = id;
            this.name = name;
            this.count = count;
        }

        private String id;

        private String name;

        private int count;

        public String getId(){
            return id;
        }
        public void setId(String id){
            this.id = id;
        }
        public String getName(){
            return name;
        }
        public void setName(String name){
            this.name = name;
        }
        public int getCount(){
            return count;
        }
        public void setCount(int count){
            this.count = count;
        }

    }
}
