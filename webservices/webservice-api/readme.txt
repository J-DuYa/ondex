This file contains instruction on how to set the webservice up on a local machine.

Please contact Christian if anything is missing, incorrect or unclear.

1. Set config.xml (in src/main/resources) to local setup.
    make sure file owenr is tomcat6
    -use sudo chown tomcat6:tomcat6 name

a. Direcoty on server where you have stored any oxl files load.
<entry key="WebServiceEngine.oxldir">/ondex/originals</entry>

2. Set ondex.properties (in src/main/WEB_INF) to local setop

a. Set the directory you would like to write the berkey form of graphs into.
Note: due to file name changes versions from webservice Pre Feb 22 2010 are not usable
ondex.home=/ondex

3. Load any required graphs.
(Three options)
a. Add them to src/main/resources/data
    -before running webservice for very first time
    -or delete the directory ondex-db in the ondex.home directory

b. Load them from the directory WebServiceEngine.oxldir using new method.

c. Import them using existing methods

====
New features in webservice 2.
- A lot more exception checking and throwing

- All graphs now must have unique names
    getGraphOfNames - no longer exists
    getGraphOfName - now returns a single graph

- Graphs keep there id even if tomcat goes goes down and back up again.
    numbers are not the same on various servers so use getGraphOfName to get number

- Service are available both in one large service () 
    or various small service which map to the api classes
    -There is NO difference to which is called as they call the same implementating object.
