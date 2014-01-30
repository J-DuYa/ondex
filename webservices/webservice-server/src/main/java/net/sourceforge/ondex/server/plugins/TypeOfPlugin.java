package net.sourceforge.ondex.server.plugins;

/**
 *
 * @author Christian Brenninkmeijer
 */
public enum TypeOfPlugin{
    FILTER("Filter","filter","Filter","WSFilterResult","Base"),
    FILTERJOB("Filter","filter","FilterUsingJob","String","JobBase"),
    PARSER("Parser","parser","Parser","String","Base"),
    PARSERJOB("Parser","parser","ParserUsingJob","String","JobBase"),
    TRANSFORMER("Transformer","transformer","Transformer","String","Base"),
    TRANSFORMERJOB("Transformer","transformer","TransformerUsingJob","String","JobBase"),
    EXPORT("Export","export","Export","String","Base"),
    EXPORTJOB("Export","export","ExportUsingJob","WSExportResult","JobBase"),
    MAPPING("Mapping","mapping","Mapping","String","Base"),
    MAPPINGJOB("Mapping","mapping","MappingUsingJob","String","JobBase");

    private String capital;
    private String lower;
    private String name;
    private String returnType;
    private String base;

    private TypeOfPlugin(String c, String l, String n, String r, String b){
        capital = c;
        lower = l;
        name = n;
        returnType = r;
        base = b;
    }

    public String getCapital(){
        return capital;
    }

    public String getLower(){
        return lower;
    }

    public String getName(){
        return name;
    }

    public String getReturnType(){
        return returnType;
    }

    public String getBase(){
        return base;
    }
}
