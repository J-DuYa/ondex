The taverna-wrapper package has hidden dependencies on Taverna Command Line and Taverna Data Viewer.

Please download these from http://www.taverna.org.uk/

The command line should be available at: http://www.taverna.org.uk/download/command-line-tool/2-3/
Command Line is included in the full Taverna download so users who have already installed Taverna do not need to down load it seperately.
Please locate the executeworkflow.bat file and set the environment variable "TAVERNA_HOME" to the absolute path of that file.

The DataViewer (which is not part of the main Taverna DownLoad) should be available at: http://www.taverna.org.uk/download/associated-tools/dataviewer-tool/
Please locate the dataviewer.bat file and set the environment variable "TAVERNA_DATAVIEWER_HOME" to the absolute path of that file.

Both scripts assume that Java.exe can be found and run from any directory.

The Taverna-wrapper code can be run in two ways.
Stand Alone buy running the main method in net.sourceforge.ondex.taverna.TavernaMini
Or by including it in an application such as ONDEX OVTK2.

To use the Taverna wrapper in Ondex you additional have to.
1)Edit the OVTK2 - config/config.xml file and change the to Taverna.Enable line to 
    <entry key="Taverna.Enable">true</entry>
2) Build Taverna-wrapper module
3) Drop the taverna-wrapper.jar into the OVTK2's plugins folder

Current Limitations
1) This code is only included in the latest ONDEX 0.3.0.
2) Currently only works in Windows but Linux scripts will be added. (Sooner if there is demand)
3) There is NO interaction between data in the OVTK and the webservices.
   a) To send an ONDEX graph to webservice it has to be saved as an oxl file and parsed in by the webservice
   b) To get an ONDEX graph from the webservice is has to be Exported to oxl and loaded into the ovtk
   
   
