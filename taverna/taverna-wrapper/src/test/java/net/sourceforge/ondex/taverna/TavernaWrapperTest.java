package net.sourceforge.ondex.taverna;

import java.io.File;
import javax.swing.JMenu;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Christian
 */
public class TavernaWrapperTest {
    
    String tavernaHome = System.getenv("TAVERNA_HOME");
    
    public TavernaWrapperTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    // Test of setTravernaHome method, of class TavernaWrapper.
    @Test
    public void testSetTravernaHome() throws TavernaException {
        TavernaWrapper instance = new TavernaWrapper(null);
        instance.setTavernaHome(tavernaHome);
    }

    //null MUST be allowed in String version as could come from unset config.
    @Test
    public void testSetTravernaHomeNull() throws TavernaException {
        TavernaWrapper instance = new TavernaWrapper(null);
        String nul = null;
        instance.setTavernaHome(nul);
    }

    // Test of setTavernaHome method, of class TavernaWrapper.
    @Test
    public void testSetTavernaHome()  throws TavernaException {
    	if (tavernaHome == null)
    		return;
        File newHome = new File(tavernaHome);
        TavernaWrapper instance = new TavernaWrapper(null);
        instance.setTavernaHome(newHome);
    }

    //While in the FILE version the set with NULL should fail. 
    @Test
    public void testSetTavernaHomeNull() throws TavernaException {
        TavernaWrapper instance = new TavernaWrapper(null);
        File nul = null;
        try {
            instance.setTavernaHome(nul);
            fail("NullPointerException expected");
        } catch (NullPointerException ex){
            //OK Expected
        }
    }

    // Test of setDataViewerHome method, of class TavernaWrapper.
    @Test
    public void testSetDataViewerHome() throws TavernaException {
        String dataViewerHome = System.getenv("TAVERNA_DATAVIEWER_HOME");
        TavernaWrapper instance = new TavernaWrapper(null);
        instance.setDataViewerHome(dataViewerHome);
    }

    //Null must be allowed as could come from unset config.
    @Test
    public void testSetDataViewerHomeNull() throws TavernaException {
        TavernaWrapper instance = new TavernaWrapper(null);
        instance.setDataViewerHome(null);
    }

    // Test of setRootDirectory method, of class TavernaWrapper.
    @Test
    public void testSetRootDirectory() throws TavernaException {
        File rootDir =  new File ("tempDirectory");
        if (!rootDir.exists())
        	rootDir.mkdir();
        TavernaWrapper instance = new TavernaWrapper(null);
        instance.setRootDirectory(rootDir);
    }

    //null should fail as something as gone wrong in caller
    @Test
    public void testSetRootDirectoryNull() throws TavernaException {
        TavernaWrapper instance = new TavernaWrapper(null);
        try {
            instance.setRootDirectory(null);
            fail("NullPointerException expected");
        } catch (NullPointerException ex){
            //Ok fail expected
        }
    }

    @Test
    public void testAttachMenu() throws TavernaException {
        System.out.println("attachMenu");
        JMenu menu = new JMenu("test");
        TavernaWrapper instance = new TavernaWrapper(null);
        instance.attachMenu(menu);
    }
}
