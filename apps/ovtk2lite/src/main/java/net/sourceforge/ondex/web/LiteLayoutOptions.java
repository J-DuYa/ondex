package net.sourceforge.ondex.web;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

import net.sourceforge.ondex.core.ONDEXConcept;
import net.sourceforge.ondex.core.ONDEXRelation;
import net.sourceforge.ondex.ovtk2.config.Config;
import net.sourceforge.ondex.ovtk2.layout.OVTK2Layouter;
import net.sourceforge.ondex.ovtk2.ui.OVTK2PropertiesAggregator;
import net.sourceforge.ondex.ovtk2.util.VisualisationUtils;
import edu.uci.ics.jung.visualization.layout.ObservableCachingLayout;

/**
 * Presents the layout options of current active layout.
 * 
 * @author taubertj
 * 
 */
public class LiteLayoutOptions extends JFrame implements ActionListener {

	// generated
	private static final long serialVersionUID = 5016903790709100531L;

	// current OVTK2Layouter
	private OVTK2Layouter ovtk2layouter = null;

	// preferred size of this gadget
	private Dimension preferredSize = new Dimension(280, 210);

	// current OVTK2PropertiesAggregator
	private OVTK2PropertiesAggregator viewer = null;

	/**
	 * Initialise option view on a given viewer.
	 * 
	 * @param viewer
	 *            OVTK2PropertiesAggregator
	 */
	public LiteLayoutOptions(OVTK2PropertiesAggregator viewer) {
		// set title
		super("Options");

		// dispose this on close
		this.setDefaultCloseOperation(JInternalFrame.HIDE_ON_CLOSE);

		// set layout
		this.setViewer(viewer);
	}

	public void actionPerformed(ActionEvent arg0) {
		if (ovtk2layouter != null && viewer != null) {
			VisualisationUtils.relayout(viewer, this);
		}
	}

	/**
	 * Returns current viewer.
	 * 
	 * @return OVTK2PropertiesAggregator
	 */
	public OVTK2PropertiesAggregator getViewer() {
		return viewer;
	}

	/**
	 * Sets GUI for a given OVTK2Layouter.
	 * 
	 * @param ovtk2layouter
	 *            OVTK2Layouter
	 */
	public void setLayouter(OVTK2Layouter ovtk2layouter) {
		this.ovtk2layouter = ovtk2layouter;

		JScrollPane scroll = new JScrollPane(ovtk2layouter.getOptionPanel());
		scroll.setPreferredSize(preferredSize);

		JButton button = new JButton(
				Config.language.getProperty("Options.Relayout"));
		button.addActionListener(this);

		// add to content pane
		this.getContentPane().removeAll();
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(scroll, BorderLayout.CENTER);
		this.getContentPane().add(button, BorderLayout.SOUTH);
		this.pack();
	}

	/**
	 * Sets viewer to be used for these options.
	 * 
	 * @param viewer
	 *            OVTK2PropertiesAggregator
	 */
	public void setViewer(OVTK2PropertiesAggregator viewer) {
		this.viewer = viewer;

		ObservableCachingLayout<ONDEXConcept, ONDEXRelation> layouter = (ObservableCachingLayout<ONDEXConcept, ONDEXRelation>) viewer
				.getVisualizationViewer().getGraphLayout();
		if (layouter.getDelegate() instanceof OVTK2Layouter) {
			setLayouter((OVTK2Layouter) layouter.getDelegate());
		} else {
			this.getContentPane().removeAll();
			this.getContentPane().setLayout(new GridLayout(1, 1));
			JLabel label = new JLabel(" Unsupported Layouter.");
			label.setPreferredSize(preferredSize);
			this.getContentPane().add(label);
			this.pack();
		}
	}

}