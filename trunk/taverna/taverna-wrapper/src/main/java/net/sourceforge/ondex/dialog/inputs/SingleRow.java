package net.sourceforge.ondex.dialog.inputs;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import net.sourceforge.ondex.dialog.ErrorDialog;
import net.sourceforge.ondex.taverna.TavernaException;
import net.sourceforge.ondex.taverna.wrapper.TavernaInput;

/**
 * Row GUI for inputs of depth 0. Single value.
 * 
 * @author Christian
 */
class SingleRow extends AbstractRow implements DocumentListener{

    private static final String STRING = "String";
    private static final String URI = "URI";
    private static final String FILE = "File contents";
    private static final String FIND_FILE = "Find File";

    private JTextField textField;
    private JRadioButton isString;
    private JRadioButton isUri;
    private JRadioButton isFile;
    private String name;
    private InputGui parentGui;
    private boolean ready;

    SingleRow (InputGui inputGui, String name) throws TavernaException{
        super();
        this.parentGui = inputGui;
        ready = false;
        textField = new JTextField(20);
        textField.getDocument().addDocumentListener(this);
        GridBagConstraints gridBagConstraints = getConstraints(0, 0);
        gridBagConstraints.weightx = 1;
        add(textField, gridBagConstraints);
        ButtonGroup group = new ButtonGroup();
        isString = addRadioButton(STRING, 1, group);
        isString.setSelected(true);
        isUri = addRadioButton( URI, 2, group);
        isFile = addRadioButton(FILE, 3,group);
        isFile.setEnabled(false);
        addButton(FIND_FILE, 4, 0);
        this.name = name;
    }

    private JRadioButton addRadioButton(String text, int column, ButtonGroup group) {
        JRadioButton button = new JRadioButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setActionCommand(text);
        button.addActionListener(this);
        GridBagConstraints gridBagConstraints = getConstraints(column, 0);
        add(button, gridBagConstraints);
        group.add(button);
        return button;
    }

	@Override
    boolean ready() {
        return ready;
    }

	@Override
    TavernaInput getInput() throws IOException, TavernaException {
        TavernaInput input = new TavernaInput(name, 0);
        if (isString.isSelected()){
            input.setStringInput(textField.getText());
        } else if (isFile.isSelected()){
            input.setSingleFileInput(new File(textField.getText()));
        } else if (isUri.isSelected()){
            input.setSingleURIInput(textField.getText());
        }
        return input;
    }

    private void notAFile(){
        isFile.setEnabled(false);
        if (isFile.isSelected()){
            isFile.setSelected(false);
            isString.setSelected(true);                    
        }
    }

    private void checkTextField(){
        if (textField.getText().isEmpty()){
            if (ready){
                ready = false;
                notAFile();
                parentGui.checkReady();
            } //else no action needed
        } else {
            File test = new File(textField.getText());
            if (test.exists()){
                isFile.setEnabled(true);
            } else {    
                notAFile();
            }
            if (!ready){
                ready = true;
                parentGui.checkReady();
            } //else no action needed
        }
    }  

    private void findFile(){
        JFileChooser chooser = new JFileChooser();
        int returnVal = chooser.showOpenDialog(parentGui.getParent());
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            textField.setText(file.getAbsolutePath());
            isFile.setEnabled(true);
            isFile.setSelected(true);
        }
    }
    
	@Override
    public void actionPerformed(ActionEvent e) {
        System.out.println(e);
        //Object source = e.getSource();
        String command = e.getActionCommand();
        try{
            if (command.equals(FIND_FILE)){
                findFile();
            }
        } catch (Exception ex) {
             ErrorDialog.show(parentGui.getParent(), ex);
        }
    }

	@Override
    public void insertUpdate(DocumentEvent e) {
        checkTextField();
    }

	@Override
    public void removeUpdate(DocumentEvent e) {
        checkTextField();
    }

	@Override
    public void changedUpdate(DocumentEvent e) {
        checkTextField();
    }
    
}
