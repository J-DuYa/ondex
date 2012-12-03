package net.sourceforge.ondex.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.DateFormat;
import java.util.Date;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * Root class for the various dialog classes.
 * 
 * 
 * @author weilej and Christian
 *
 */
public abstract class AbstractDialog extends JDialog implements ActionListener {

	//####FIELDS####
	
	private JPanel topPanel, bottomPanel;
	
	private JScrollPane centerPanel;
	
	private JButton moreLess;
	
	private BufferedImage image;
	
	private Dimension minDim, maxDim;
    
    /** Operating System depenend new Line character obtained from System Property "line.separator" */
    public static String NEW_LINE = System.getProperty("line.separator");
    
	//####CONSTRUCTOR####
	
	AbstractDialog(Frame parent, String title) {
		super(parent,title);
	}

    //####METHOD STUBS ####
    abstract void addPanelDetails(StringBuilder b);
    
	//####METHODS####

	void setupGUI() {
		getContentPane().setLayout(new BoxLayout(getContentPane(),BoxLayout.PAGE_AXIS));
		
		topPanel = new JPanel(new BorderLayout());
		JPanel leftPanel = makeImgPanel();
		if (leftPanel != null) {
			topPanel.add(leftPanel,BorderLayout.WEST);
		}
		JPanel rightPanel = new JPanel(new BorderLayout());
		rightPanel.add(makeMsgPanel(),BorderLayout.CENTER);
		rightPanel.add(makeMoreLessPanel(),BorderLayout.SOUTH);
		topPanel.add(rightPanel, BorderLayout.CENTER);
		
		centerPanel = createStackPanel();
		bottomPanel = createButtonPanel();
		
		less();
		
		int w_self = getWidth()+100;
		int h_self = getHeight();
		int x,y,w,h;
		
        x = getParent().getX();
        y = getParent().getY();
        w = getParent().getWidth();
        h = getParent().getHeight();
		this.setBounds(x + (w-w_self)/2, y + (h-h_self)/2, w_self, h_self);
		
 		minDim = getSize();
		maxDim = new Dimension(getWidth(),getHeight()+100);
		
		topPanel.setMaximumSize(new Dimension(1280,topPanel.getHeight()));
		bottomPanel.setMaximumSize(bottomPanel.getSize());
		
		setVisible(true);
        Point point = this.getLocationOnScreen();
        if (point.x < 10 || point.y < 10){
            if (point.x < 10) point.x = 10;
            if (point.y < 10) point.y = 10;
            this.setLocation(point);
            this.repaint();
        }
	}
	
	private JScrollPane createStackPanel() {
		
		StringBuilder b = new StringBuilder();
		
		DateFormat df = DateFormat.getDateTimeInstance(DateFormat.FULL,DateFormat.FULL);
		String date = df.format(new Date(System.currentTimeMillis()));
		
		b.append("Date: ");
		b.append(date);
        b.append(NEW_LINE);
				
		String arch = System.getProperty("os.arch");
		String osname = System.getProperty("os.name");
		String osversion = System.getProperty("os.version");
		
		b.append("System: ");
        b.append(arch);
        b.append(" ");
        b.append(osname);
        b.append(" v");
        b.append(osversion);
        b.append(NEW_LINE);
		
        addPanelDetails(b);
		
		JTextArea area = new JTextArea();
		area.setEditable(false);
		area.setText(b.toString());
		return new JScrollPane(area);
	}
	
   
	private JPanel makeMoreLessPanel() {
		JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		moreLess = makeButton("Details >>","more");
		p.add(moreLess);
		return p;
	}
	
	JPanel createButtonPanel() {
		JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER));
		p.add(makeButton("OK","ok"));
		return p;
	}
	
	 JButton makeButton(String title, String actionCommand) {
		JButton button = new JButton(title);
		button.setActionCommand(actionCommand);
		button.addActionListener(this);
		return button;
	}
	
	abstract JPanel makeMsgPanel();
	
    abstract BufferedImage getImage();
    
	JPanel makeImgPanel() {
		String s = File.separator;
        image = getImage();
        if (image != null) {
            JPanel leftPanel = new JPanel() {
						
                private static final long serialVersionUID = 858217031036743682L;

                public void paint(Graphics g) {
                    super.paint(g);
                    Graphics2D g2 = (Graphics2D) g;
                    g2.drawImage(image, null, 10, 17);
                }			
            };
					
            Dimension d = new Dimension(image.getWidth() + 20, image.getHeight()+ 20);
            leftPanel.setMinimumSize(d);
            leftPanel.setMaximumSize(d);
            leftPanel.setPreferredSize(d);
            leftPanel.setSize(d);
            return leftPanel;
        }
		return null;
	}
	
	private void less() {
		moreLess.setText("Details >>");
		moreLess.setActionCommand("more");
		
		getContentPane().removeAll();
		getContentPane().add(topPanel);
		getContentPane().add(bottomPanel);
		
		pack();
		if (minDim != null) {
			setSize(minDim);
		}
	}
	
	private void more() {
		moreLess.setText("<< Details");
		moreLess.setActionCommand("less");
		
		getContentPane().removeAll();
		getContentPane().add(topPanel);
		getContentPane().add(centerPanel);
		getContentPane().add(bottomPanel);
		
		setSize(maxDim);
		validate();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if (cmd.equals("more")) {
			more();
		} else if (cmd.equals("less")) {
			less();
		} else if (cmd.equals("ok")) {
			dispose();
		}
		
	}
			
}
