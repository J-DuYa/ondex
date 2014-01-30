package net.sourceforge.ondex.taverna.test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * This class is used to write an Executeworkflow.bat for the latest taverna.
 * It is here purly for safe storage and therefor has a hard coded path.
 * 
 * @author Christian
 */
public class MakeBat {
    
    public static String NEW_LINE = System.getProperty("line.separator");

    public static void main(String[] args) throws IOException {
        File file = new File ("D:\\taverna\\commandLine\\taverna-commandline-3.0-SNAPSHOT\\bin\\executeworkflow.bat");
        FileWriter writer = new FileWriter(file);
        writer.append("REM Taverna startup script");
        writer.append(NEW_LINE);
        writer.append(NEW_LINE);
        writer.append("REM This contains a special Vertical tab character");
        writer.append(NEW_LINE);
        writer.append("REM Do not edit with an editor that will remove that character");
        writer.append(NEW_LINE);
        writer.append(NEW_LINE);
        writer.append("REM distribution directory");
        writer.append(NEW_LINE);
        writer.append("set TAVERNA_HOME=%~dp0");
        writer.append(NEW_LINE);
        writer.append(NEW_LINE);
        writer.append("REM Convert arguement into Taverna format to handle spaces");
        writer.append(NEW_LINE);
        writer.append("set VMARGS=");
        writer.append(NEW_LINE);
        writer.append("set /a COUNT=0");
        writer.append(NEW_LINE);
        writer.append(":Loop");
        writer.append(NEW_LINE);
        writer.append("    if [%1]==[] goto Continue");
        writer.append(NEW_LINE);
        writer.append("    set TEMP=%1");
        writer.append(NEW_LINE);
        writer.append("    set TEMP=%TEMP: =");
        writer.append((char)11);
        writer.append("%");
        writer.append(NEW_LINE);
        writer.append("    set VMARGS=%VMARGS%-Dtaverna.commandline.arg.%COUNT%=%TEMP% ");
        writer.append(NEW_LINE);
        writer.append("    set /a COUNT = COUNT +1");
        writer.append(NEW_LINE);
        writer.append("    shift");
        writer.append(NEW_LINE);
        writer.append("    goto Loop");
        writer.append(NEW_LINE);
        writer.append(":Continue");
        writer.append(NEW_LINE);
        writer.append(NEW_LINE);
        writer.append("set VMARGS=%VMARGS%-Dtaverna.commandline.args=%COUNT%");
        writer.append(NEW_LINE);
        writer.append("set VMARGS=%VMARGS% -Xmx300m -XX:MaxPermSize=140m");
        writer.append(NEW_LINE);
        writer.append("REM strip out any quotes and then quote the whole thing");
        writer.append(NEW_LINE);
        writer.append("set vmargs=\"%vmargs:\"=%\"");
        writer.append(NEW_LINE);
        writer.append("");
        writer.append(NEW_LINE);
        writer.append("java -jar \"%TAVERNA_HOME%pax-runner-1.7.0.jar\" ");
        writer.append("--vmOptions=%vmargs% ");
        writer.append("--cp=\"%TAVERNA_HOME%..\\config\" ");
        writer.append("--args=\"file:%TAVERNA_HOME%..\\config\\runner.args\" ");
        writer.append("--workingDirectory=\"%TAVERNA_HOME%..\\runner\" ");
        writer.append(" scan-dir:\"%TAVERNA_HOME%..\\lib\"");
        writer.append("");
        writer.append(NEW_LINE);
        writer.close();
    }
    
}



 

