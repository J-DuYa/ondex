/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sourceforge.ondex.taverna.wrapper;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author Christian
 */
public class ChangeListenerStub implements ChangeListener{

    Object lastSource;
    
    public void stateChanged(ChangeEvent e) {
        lastSource = e.getSource();
    }
    
}
