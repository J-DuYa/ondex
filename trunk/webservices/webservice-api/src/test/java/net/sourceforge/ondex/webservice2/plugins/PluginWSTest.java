/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sourceforge.ondex.webservice2.plugins;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


/**
 *
 * @author christian
 */
public class PluginWSTest {

    @Before
    public void setUp() {}

    @After
    public void tearDown() throws Exception {}

/*
            XStream xstream = new XStream();

        System.out.println("test start");
        WSGDS test = new WSGDS();
        Object inner = new ArrayList();
        test.setValue(inner);
        test.setTypeOf(inner.getClass().toString());
        System.out.println(test);
        System.out.println(inner);
        String xml = xstream.toXML(inner);
        System.out.println(xml);
        xml = xstream.toXML(test);
        System.out.println(xml);
        Object test2 = xstream.fromXML(xml);
        System.out.println(test2);
        WSGDS w2 = (WSGDS)test2;
        Object in2 = w2.getValue();
        System.out.println(in2);
        System.out.println(in2.getClass().toString());
        System.out.println("test done");
*/
    
    @Test
    public void testPluginWS() throws Exception {
        //Echo01.copyXML();
        //Properties properties = System.getProperties();
        //Set<String> names = properties.stringPropertyNames();
        //for (String name:names){
        //    System.out.println(name);
        //}
        //Config.ondexDir = "c:\tmp";
        //ParserAuto ap = new ParserAuto();
        //ap.oxlParser("THis is a test", null, null, new Long(1));
        //net.sourceforge.ondex.mapping.sequence2pfam.Mapping mapping = new net.sourceforge.ondex.mapping.sequence2pfam.Mapping();
        //ystem.out.println("normal call worked");
        //String fullName = "net.sourceforge.ondex.mapping.sequence2pfam.Mapping";
        //ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        //Class theClass = classLoader.loadClass(fullName);
        //ystem.out.println("got class "+theClass);
        //Object mapping1 = theClass.newInstance();
        //ystem.out.println("created object");
        //ONDEXMapping mapping2 = (ONDEXMapping) theClass.newInstance();
        //ystem.out.println("mapping2");
        //ystem.out.println(mapping2);
        //llegalSymbolException exception = new IllegalSymbolException("test");
        
        //UUID[] jobIds = new UUID[10];
        //WebserviceJob test;
        //for (int i = 0; i<10; i++) {
        //    test = new TestWebserviceJob();
        //    System.out.println(test.getJobId());
        //    jobIds[i] = test.getJobId();
        // }
        //for (int i = 0; i<10; i++) {
        //    try {
        //        System.out.println(ExecutorRegister.getRegister().get(jobIds[i]));
        //    } catch (JobException e){
        //        System.out.println(e);
        //    }
        //}
        // System.out.println("Start test");
        //PluginWS pluginws = new PluginWS();
        //pluginws.UriToFile("ftp://ftp.genome.jp/pub/kegg/genes/");
        //System.out.println("Done test");
        //FTPTest.run();
    }
}
