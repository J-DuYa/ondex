/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sourceforge.ondex.server.plugins;

import java.lang.annotation.Annotation;
import java.util.Set;

import net.sourceforge.ondex.ONDEXPlugin;
import net.sourceforge.ondex.annotations.Authors;
import net.sourceforge.ondex.annotations.Custodians;
import net.sourceforge.ondex.annotations.DataURL;
import net.sourceforge.ondex.annotations.DatabaseTarget;
import net.sourceforge.ondex.annotations.Documentation;
import net.sourceforge.ondex.annotations.Status;
import net.sourceforge.ondex.annotations.metadata.AttributeNameRequired;
import net.sourceforge.ondex.annotations.metadata.DataSourceRequired;
import net.sourceforge.ondex.annotations.metadata.ConceptClassRequired;
import net.sourceforge.ondex.annotations.metadata.EvidenceTypeRequired;
import net.sourceforge.ondex.annotations.metadata.RelationTypeRequired;
import net.sourceforge.ondex.args.ArgumentDefinition;
import net.sourceforge.ondex.wsapi.exceptions.CaughtException;
import net.sourceforge.ondex.wsapi.exceptions.WebserviceException;
import org.apache.log4j.Logger;

/**
 *
 * @author christian
 */
public class AnnotationsReport {

    private static final Logger logger = Logger.getLogger(AnnotationsReport.class);

    public static String NEW_LINE = System.getProperty("line.separator");

    private static int authorsCount = 0;

    private static int custodiansCount = 0;

    private static int dataUrlCount = 0;

    private static int databaseTargetCount = 0;

    private static int documentationCount = 0;

    private static int statusCount = 0;

    private static int attributeNameRequiredCount = 0;

    private static int cvRequiredCount = 0;

    private static int conceptClassRequiredCount = 0;

    private static int evidenceTypeRequiredCount = 0;

    private static int relationTypeRequiredCount = 0;

    private static int inputFileCount = 0;
    private static int inputDirCount = 0;
    private static int outputFileCount = 0;
    private static int outputDirCount = 0;
    private static int fileOtherCount = 0;
    private static int fileMultipleCount = 0;

    private static int noAnnotation = 0;

    private static int pluginCount = 0;

    private static ONDEXPlugin getPlugin(String name, TypeOfPlugin pluginType)
            throws WebserviceException{
        PluginFinder pluginFinder = PluginFinder.getInstance();
        switch (pluginType){
            case FILTER: {
                return pluginFinder.getFilter(name);
            } case PARSER: {
                return pluginFinder.getParser(name);
            }case TRANSFORMER: {
                return pluginFinder.getTransformer(name);
            }case EXPORT: {
                return pluginFinder.getExport(name);
            }case MAPPING: {
                return pluginFinder.getMapping(name);
            } default: {
                throw new WebserviceException("Unexpected type in getPlugins",logger);
            }
        }
    }

    private static void reportAnnotation(StringBuffer buffer, Annotation annotation){
        if (annotation instanceof Authors){
            Authors authors = (Authors)annotation;
            buffer.append("Authors: ");
            //buffer.append(authors.authors());
            authorsCount++;
            return;
        }
        if (annotation instanceof Custodians){
            Custodians custodians  = (Custodians)annotation;
            buffer.append("Custodians: ");
            //buffer.append(custodians.custodians());
            custodiansCount++;
            return;
        }
        if (annotation instanceof DataURL){
            DataURL dataUrl  = (DataURL)annotation;
            buffer.append("DataURL: ");
            //buffer.append(dataUrl.name());
            //buffer.append(" ");
            //buffer.append(dataUrl.urls());
            dataUrlCount++;
            return;
        }
        if (annotation instanceof DatabaseTarget){
            DatabaseTarget databaseTarget  = (DatabaseTarget)annotation;
            buffer.append("DatabaseTarget: ");
            //buffer.append(databaseTarget.name());
            //buffer.append(" ");
            //buffer.append(databaseTarget.url());
            databaseTargetCount++;
            return;
        }
        if (annotation instanceof Documentation){
            buffer.append("Documentation: ");
            documentationCount++;
            return;
        }
        if (annotation instanceof Status){
            Status status = (Status)annotation;
            buffer.append("Status: ");
            statusCount++;
            return;
        }
        if (annotation instanceof AttributeNameRequired){
            buffer.append("AttributeNameRequired: ");
            attributeNameRequiredCount++;
            return;
        }
        if (annotation instanceof DataSourceRequired){
            buffer.append("AttributeNameRequired: ");
            cvRequiredCount++;
            return;
        }
        if (annotation instanceof ConceptClassRequired){
            ConceptClassRequired conceptClassRequired = (ConceptClassRequired)annotation;
            buffer.append("ConceptClassRequired: ");
            //buffer.append(conceptClassRequired.ids().toString());
            //buffer.append(" ");
            //buffer.append(status.description());
            conceptClassRequiredCount++;
            return;
        }
        if (annotation instanceof EvidenceTypeRequired){
            buffer.append("EvidenceTypeRequired: ");
            evidenceTypeRequiredCount++;
            return;
        }
        if (annotation instanceof RelationTypeRequired){
            buffer.append("RelationTypeRequired: ");
            relationTypeRequiredCount++;
            return;
        }
        throw new AssertionError ("Unexpected Annotation type "+annotation);
    }

    public static void reportPlugin(StringBuffer buffer, String name, TypeOfPlugin pluginType)
            throws WebserviceException{
        logger.info("reporting "+name+" "+pluginType.name());
        ONDEXPlugin plugin = getPlugin(name, pluginType);
        //ogger.info("found "+plugin.getName());
        buffer.append(name);
        buffer.append(": ");
        buffer.append(plugin.getName());
        Class theClass = plugin.getClass();
        pluginCount++;
        ArgumentDefinition<?>[] defintions = plugin.getArgumentDefinitions();
        TypeOfFile type = AutoCreate.getFileType(defintions);
        if (type != TypeOfFile.NONE){
           buffer.append("[FileArgumentDefinition: ");
           buffer.append(type.name());
           buffer.append("] ");
           switch (type){
                case INPUT_FILE: {
                    inputFileCount++;
                    break;
                } case INPUT_DIRECTORY: {
                    inputDirCount++;
                    break;
                } case EXPORT_FILE: {
                    outputFileCount++;
                    break;
                } case EXPORT_DIRECTORY: {
                    outputDirCount++;
                    break;
                } case MULTIPLE: {
                    fileMultipleCount++;
                    break;
                } 
            }
        }
        Annotation[] annotations = theClass.getAnnotations();
        //ogger.info("Got annotations");
        if (annotations.length == 0){
            noAnnotation++;
        } else {
            buffer.append("(");
            for(Annotation annotation: annotations){
                try{
                    reportAnnotation(buffer, annotation);
                } catch (Exception e){
                    throw new CaughtException ("reporting annotation ",e,logger);
                }
            }
            buffer.append(")");
        }
        buffer.append(NEW_LINE);
    }

    public static String report() throws WebserviceException{
    
        authorsCount = 0;
        custodiansCount = 0;
        dataUrlCount = 0;
        databaseTargetCount = 0;
        documentationCount = 0;
        statusCount = 0;
        attributeNameRequiredCount = 0;
        cvRequiredCount = 0;
        conceptClassRequiredCount = 0;
        evidenceTypeRequiredCount = 0;
        relationTypeRequiredCount = 0;
        inputFileCount = 0;
        inputDirCount = 0;
        outputFileCount = 0;
        outputDirCount = 0;
        fileOtherCount = 0;
        fileMultipleCount = 0;
        noAnnotation = 0;
        pluginCount = 0;

        StringBuffer buffer = new StringBuffer();
        for (TypeOfPlugin type: TypeOfPlugin.values()){
            buffer.append("****** ");
            buffer.append(type.name());
            buffer.append(" ******");
            buffer.append(NEW_LINE);
            Set<String> plugins = AutoCreate.getPlugins(type);
            for (String plugin: plugins){
                reportPlugin(buffer,plugin, type);
            }
            buffer.append(NEW_LINE);
    }
        buffer.append("authorsCount = ");
        buffer.append(authorsCount);
        buffer.append(NEW_LINE);

        buffer.append("custodiansCount = ");
        buffer.append(custodiansCount);
        buffer.append(NEW_LINE);

        buffer.append("dataUrlCount = ");
        buffer.append(dataUrlCount);
        buffer.append(NEW_LINE);

        buffer.append("databaseTargetCount = ");
        buffer.append(databaseTargetCount);
        buffer.append(NEW_LINE);

        buffer.append("documentationCount = ");
        buffer.append(documentationCount);
        buffer.append(NEW_LINE);

        buffer.append("statusCount = ");
        buffer.append(statusCount);
        buffer.append(NEW_LINE);

        buffer.append("attributeNameRequiredCount = ");
        buffer.append(attributeNameRequiredCount);
        buffer.append(NEW_LINE);

        buffer.append("cvRequiredCount = ");
        buffer.append(cvRequiredCount);
        buffer.append(NEW_LINE);

        buffer.append("conceptClassRequiredCount = ");
        buffer.append(conceptClassRequiredCount);
        buffer.append(NEW_LINE);

        buffer.append("evidenceTypeRequiredCount = ");
        buffer.append(evidenceTypeRequiredCount);
        buffer.append(NEW_LINE);

        buffer.append("relationTypeRequiredCount = ");
        buffer.append(relationTypeRequiredCount);
        buffer.append(NEW_LINE);

        buffer.append("noAnnotation = ");
        buffer.append(noAnnotation);
        buffer.append(NEW_LINE);

        buffer.append("inputFileCount = ");
        buffer.append(inputFileCount);
        buffer.append(NEW_LINE);

        buffer.append("inputDirCount = ");
        buffer.append(inputDirCount);
        buffer.append(NEW_LINE);

        buffer.append("outputFileCount = ");
        buffer.append(outputFileCount);
        buffer.append(NEW_LINE);

        buffer.append("outputDirCount = ");
        buffer.append(outputDirCount);
        buffer.append(NEW_LINE);

        buffer.append("fileOtherCount = ");
        buffer.append(fileOtherCount);
        buffer.append(NEW_LINE);

        buffer.append("fileMultipleCount = ");
        buffer.append(fileMultipleCount);
        buffer.append(NEW_LINE);
        
        buffer.append("pluginCount = ");
        buffer.append(pluginCount);
        buffer.append(NEW_LINE);

        return buffer.toString();
    }
}

