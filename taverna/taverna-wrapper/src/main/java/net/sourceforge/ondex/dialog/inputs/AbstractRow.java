package net.sourceforge.ondex.dialog.inputs;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.JButton;
import net.sourceforge.ondex.taverna.TavernaException;
import net.sourceforge.ondex.taverna.wrapper.TavernaInput;

/**
 * Abstract parent for the differtent row types in InputGui 
 * @author Christian
 */
abstract class AbstractRow extends Container implements ActionListener{
    
    AbstractRow(){
        super();
        setLayout(new GridBagLayout());
    }
    
    abstract boolean ready();
    
    abstract TavernaInput getInput() throws IOException, TavernaException;
    
    static GridBagConstraints getConstraints(int column, int row){
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.gridx = column;
        gridBagConstraints.gridy = row;
        return gridBagConstraints;
    }
        
    JButton addButton(String text, int column, int row) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setActionCommand(text);
        button.addActionListener(this);
        GridBagConstraints gridBagConstraints = getConstraints(column, row);
        add(button, gridBagConstraints);
        return button;
    }


}
