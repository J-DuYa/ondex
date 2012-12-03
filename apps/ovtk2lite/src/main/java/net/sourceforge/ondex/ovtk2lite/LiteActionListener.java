package net.sourceforge.ondex.ovtk2lite;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Set;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import net.sourceforge.ondex.core.ONDEXConcept;
import net.sourceforge.ondex.core.ONDEXRelation;
import net.sourceforge.ondex.ovtk2.annotator.OVTK2Annotator;
import net.sourceforge.ondex.ovtk2.annotator.colorcategory.ColorCategoryAnnotator;
import net.sourceforge.ondex.ovtk2.annotator.scaleconcept.ScaleConceptAnnotator;
import net.sourceforge.ondex.ovtk2.config.Config;
import net.sourceforge.ondex.ovtk2.config.OVTK2PluginLoader;
import net.sourceforge.ondex.ovtk2.config.PluginID;
import net.sourceforge.ondex.ovtk2.graph.ONDEXEdgeColors.EdgeColorSelection;
import net.sourceforge.ondex.ovtk2.graph.ONDEXNodeDrawPaint.NodeDrawPaintSelection;
import net.sourceforge.ondex.ovtk2.graph.ONDEXNodeFillPaint.NodeFillPaintSelection;
import net.sourceforge.ondex.ovtk2.graph.ONDEXNodeShapes.NodeShapeSelection;
import net.sourceforge.ondex.ovtk2.layout.OVTK2Layouter;
import net.sourceforge.ondex.ovtk2.ui.OVTK2PropertiesAggregator;
import net.sourceforge.ondex.ovtk2.ui.contentsdisplay.ContentsDisplay;
import net.sourceforge.ondex.ovtk2.ui.dialog.DialogMerging;
import net.sourceforge.ondex.ovtk2.ui.menu.actions.AppearanceMenuAction;
import net.sourceforge.ondex.ovtk2.ui.mouse.OVTK2GraphMouse;
import net.sourceforge.ondex.ovtk2.ui.mouse.OVTK2PickingMousePlugin;
import net.sourceforge.ondex.ovtk2.util.AppearanceSynchronizer;
import net.sourceforge.ondex.ovtk2.util.GraphSynchronizer;
import net.sourceforge.ondex.ovtk2.util.OVTKProgressMonitor;
import net.sourceforge.ondex.ovtk2.util.VisualisationUtils;
import net.sourceforge.ondex.tools.threading.monitoring.IndeterminateProcessAdapter;
import net.sourceforge.ondex.tools.threading.monitoring.Monitorable;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.visualization.picking.PickedState;

/**
 * Separate Action listener events into its own class
 * 
 * @author taubertj
 * 
 */
public class LiteActionListener implements ActionListener, ComponentListener,
		WindowListener {

	// display node and edge info
	private ContentsDisplay contentsDisplay = null;

	// contains contents display
	private final JFrame frame = new JFrame("Content info");

	// parent applet
	private final Main main;

	// layout options of active graph
	private LiteLayoutOptions options = null;

	/**
	 * Requires the applet class
	 * 
	 * @param main
	 */
	public LiteActionListener(Main main) {
		this.main = main;
		frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		frame.addComponentListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		String cmd = e.getActionCommand();
		final OVTK2PropertiesAggregator viewer = main.getViewer();

		if (cmd.equals("relayout")) {
			VisualisationUtils.relayout(viewer, frame);
		}

		// toggle anti-aliased painting
		else if (cmd.equals("antialiased")) {
			boolean selected = ((JCheckBoxMenuItem) e.getSource()).isSelected();
			viewer.setAntiAliased(selected);
		}

		// toggle node label visibility
		else if (cmd.equals("nodelabels")) {
			boolean selected = ((JCheckBoxMenuItem) e.getSource()).isSelected();
			viewer.setShowNodeLabels(selected);
		}

		// toggle edge label visibility
		else if (cmd.equals("edgelabels")) {
			boolean selected = ((JCheckBoxMenuItem) e.getSource()).isSelected();
			viewer.setShowEdgeLabels(selected);
		}

		// toggle both label visibility
		else if (cmd.equals("bothlabels")) {
			boolean selected = ((JCheckBoxMenuItem) e.getSource()).isSelected();
			viewer.setShowNodeLabels(selected);
			viewer.setShowEdgeLabels(selected);
		}

		// triggers all events to load saved appearance
		else if (cmd.equals(AppearanceMenuAction.LOADAPPEARANCE)) {
			AppearanceSynchronizer.loadAppearance(this, viewer);
		}

		// toggle node Attribute colour parsing
		else if (cmd.equals(AppearanceMenuAction.NODECOLOR)) {

			JCheckBoxMenuItem item = (JCheckBoxMenuItem) e.getSource();
			if (item.isSelected())
				AppearanceSynchronizer.loadNodeColor(
						viewer.getONDEXJUNGGraph(), viewer.getNodeColors(),
						viewer.getNodeDrawPaint());
			else {
				viewer.getNodeColors().setFillPaintSelection(
						NodeFillPaintSelection.CONCEPTCLASS);
				viewer.getNodeColors().updateAll();
				viewer.getNodeDrawPaint().setDrawPaintSelection(
						NodeDrawPaintSelection.NONE);
				viewer.getNodeDrawPaint().updateAll();
			}
			// notify model of change
			viewer.getVisualizationViewer().getModel().fireStateChanged();
		}

		// toggle node shape Attribute parsing
		else if (cmd.equals(AppearanceMenuAction.NODESHAPE)) {

			JCheckBoxMenuItem item = (JCheckBoxMenuItem) e.getSource();
			if (item.isSelected())
				AppearanceSynchronizer.loadNodeShape(
						viewer.getONDEXJUNGGraph(), viewer.getNodeShapes());
			else {
				viewer.getNodeShapes().setNodeShapeSelection(
						NodeShapeSelection.NONE);
				viewer.getNodeShapes().setNodeSizes(
						new Transformer<ONDEXConcept, Integer>() {
							@Override
							public Integer transform(ONDEXConcept input) {
								return Config.defaultNodeSize;
							}
						});
				viewer.getNodeShapes().setNodeAspectRatios(
						new Transformer<ONDEXConcept, Float>() {
							@Override
							public Float transform(ONDEXConcept input) {
								return 1.0f;
							}
						});
				viewer.getNodeShapes().updateAll();
			}
			// notify model of change
			viewer.getVisualizationViewer().getModel().fireStateChanged();
		}

		// toggle edge Attribute colour parsing
		else if (cmd.equals(AppearanceMenuAction.EDGECOLOR)) {

			JCheckBoxMenuItem item = (JCheckBoxMenuItem) e.getSource();
			if (item.isSelected())
				AppearanceSynchronizer.loadEdgeColor(
						viewer.getONDEXJUNGGraph(), viewer.getEdgeColors());
			else {
				viewer.getEdgeColors().setEdgeColorSelection(
						EdgeColorSelection.RELATIONTYPE);
				viewer.getEdgeColors().updateAll();
			}
			// notify model of change
			viewer.getVisualizationViewer().getModel().fireStateChanged();
		}

		// toggle edge size Attribute parsing
		else if (cmd.equals(AppearanceMenuAction.EDGESIZE)) {

			JCheckBoxMenuItem item = (JCheckBoxMenuItem) e.getSource();
			if (item.isSelected())
				AppearanceSynchronizer.loadEdgeSize(viewer.getONDEXJUNGGraph(),
						viewer.getEdgeStrokes());
			else
				viewer.getEdgeStrokes().setEdgeSizes(null);
			// notify model of change
			viewer.getVisualizationViewer().getModel().fireStateChanged();
		}

		// sync node positions to Attribute
		else if (cmd.equals(AppearanceMenuAction.SAVEAPPEARANCE)) {
			AppearanceSynchronizer.saveAppearance(viewer);
		}

		// toggle mouse over for current viewer
		else if (cmd.equals(AppearanceMenuAction.SHOWMOUSEOVER)) {
			if (viewer != null) {
				JCheckBoxMenuItem item = (JCheckBoxMenuItem) e.getSource();
				OVTK2GraphMouse mouse = (OVTK2GraphMouse) viewer
						.getVisualizationViewer().getGraphMouse();
				OVTK2PickingMousePlugin picking = mouse
						.getOVTK2PickingMousePlugin();
				if (picking != null)
					picking.setShowMouseOver(item.isSelected());
			}
		}

		// handle Layouter calls
		else if (cmd.startsWith("Menu.Layout.")) {

			String className = Config.config.getProperty(cmd);

			// get new instance of layout
			int index = className.lastIndexOf(".");
			String name = className.substring(index + 1, className.length());

			try {
				final OVTK2Layouter layouter_new = OVTK2PluginLoader
						.getInstance().loadLayouter(name, viewer);

				if (layouter_new instanceof Monitorable) {
					// layout knows about its progress
					Monitorable p = (Monitorable) layouter_new;
					OVTKProgressMonitor.start(frame, "Running Layout...", p);
					Thread t = new Thread() {
						public void run() {
							VisualisationUtils.runLayout(layouter_new, viewer);
							if (options != null) {
								options.setLayouter(layouter_new);
							}
						}
					};
					t.start();
				} else {
					// wrap into indefinite process
					IndeterminateProcessAdapter p = new IndeterminateProcessAdapter() {
						public void task() {
							VisualisationUtils.runLayout(layouter_new, viewer);
							if (options != null) {
								options.setLayouter(layouter_new);
							}
						}
					};

					// set layout
					OVTKProgressMonitor.start(frame, "Running Layout...", p);
					p.start();
				}

				// central handling of scaling
				LiteDefaultModalGraphMouse mouse = (LiteDefaultModalGraphMouse) viewer
						.getVisualizationViewer().getGraphMouse();
				if (name.equals("StaticLayout")) {
					// static layout should stay static
					mouse.setViewScaling(true);
				} else {
					// reset scaling control
					mouse.setViewScaling(false);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		// toggle options view
		else if (cmd.equals("options")) {
			boolean selected = ((JCheckBoxMenuItem) e.getSource()).isSelected();
			if (selected) {
				showOptions(viewer);
			} else {
				options.setVisible(false);
			}
		}

		// toggle contents display view
		else if (cmd.equals("contentsdisplay")) {
			boolean selected = ((JCheckBoxMenuItem) e.getSource()).isSelected();
			if (selected) {
				showContentsDisplay(viewer);
			} else {
				frame.setVisible(false);
			}
		}

		// select all visible nodes
		else if (cmd.equals("allnodes")) {
			if (viewer != null) {
				PickedState<ONDEXConcept> pickState = viewer
						.getVisualizationViewer().getPickedVertexState();
				for (ONDEXConcept n : viewer.getONDEXJUNGGraph().getVertices())
					pickState.pick(n, true);
			}
		}

		// select all visible edges
		else if (cmd.equals("alledges")) {
			if (viewer != null) {
				PickedState<ONDEXRelation> pickState = viewer
						.getVisualizationViewer().getPickedEdgeState();
				for (ONDEXRelation edge : viewer.getONDEXJUNGGraph().getEdges())
					pickState.pick(edge, true);
			}
		}

		// inverse node selection
		else if (cmd.equals("inversenodes")) {
			if (viewer != null) {
				PickedState<ONDEXConcept> pickState = viewer
						.getVisualizationViewer().getPickedVertexState();
				for (ONDEXConcept n : viewer.getONDEXJUNGGraph().getVertices())
					pickState.pick(n, !pickState.isPicked(n));
			}
		}

		// inverse edge selection
		else if (cmd.equals("inverseedges")) {
			if (viewer != null) {
				PickedState<ONDEXRelation> pickState = viewer
						.getVisualizationViewer().getPickedEdgeState();
				for (ONDEXRelation edge : viewer.getONDEXJUNGGraph().getEdges())
					pickState.pick(edge, !pickState.isPicked(edge));
			}
		}

		// show merge dialog
		else if (cmd.equals("merge")) {
			if (viewer != null) {
				JFrame wrapper = new JFrame("Merge concepts");
				new DialogMerging(viewer, wrapper);
				wrapper.pack();
				wrapper.setVisible(true);
				wrapper.toFront();
			}
		}

		// sync graph
		else if (cmd.equals("sync")) {
			if (viewer != null) {
				int option = JOptionPane
						.showConfirmDialog(
								frame,
								Config.language
										.getProperty("Dialog.Sync.Warning.Text"),
								Config.language
										.getProperty("Dialog.Sync.Warning.Title"),
								JOptionPane.YES_NO_OPTION,
								JOptionPane.WARNING_MESSAGE);
				if (option == JOptionPane.NO_OPTION) {
					return;
				}

				// sync graphs by deleting invisible concepts/relations
				final GraphSynchronizer gs = new GraphSynchronizer(viewer);

				java.lang.Thread t = new Thread("graph synchronization") {
					public void run() {
						gs.run();
					}
				};

				// start thread and monitoring
				t.start();
				OVTKProgressMonitor.start(frame, "Graph Synchronisation", gs);
			}
		}

		// handle annotator calls
		else if (cmd.startsWith("Menu.Annotator.")) {

			final String className = Config.config.getProperty(cmd);

			// wrap into indefinite process
			IndeterminateProcessAdapter p = new IndeterminateProcessAdapter() {
				public void task() {
					try {

						// get new instance of annotator
						int index = className.lastIndexOf(".");
						String pack = className.substring(0, index);
						pack = pack.substring(pack.lastIndexOf(".") + 1,
								pack.length());
						PluginID plid = new PluginID(pack, className.substring(
								index + 1, className.length()));
						OVTK2Annotator annotator_new = OVTK2PluginLoader
								.getInstance().loadAnnotator(plid, viewer);

						// add as frame to desktop
						JFrame annotatorFrame = new JFrame(
								annotator_new.getName());
						annotatorFrame
								.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
						annotatorFrame.setContentPane(annotator_new);
						annotatorFrame.setVisible(true);
						annotatorFrame.pack();
						annotatorFrame.addWindowListener(main.getListener());

					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			};
			p.start();
			OVTKProgressMonitor.start(frame, "Working...", p);
		}

		// update menu to reflect new state
		((LiteMenuBar) main.getJMenuBar()).updateMenuBar();
	}

	@Override
	public void componentHidden(ComponentEvent e) {
		// update menu to reflect new state
		((LiteMenuBar) main.getJMenuBar()).updateMenuBar();
	}

	@Override
	public void componentMoved(ComponentEvent e) {

	}

	@Override
	public void componentResized(ComponentEvent e) {

	}

	@Override
	public void componentShown(ComponentEvent e) {
		// update menu to reflect new state
		((LiteMenuBar) main.getJMenuBar()).updateMenuBar();
	}

	/**
	 * @return the contentsDisplay
	 */
	public ContentsDisplay getContentsDisplay() {
		return contentsDisplay;
	}

	/**
	 * @return the frame
	 */
	public JFrame getContentsDisplayFrame() {
		return frame;
	}

	/**
	 * Returns whether or not the contents display is currently visible.
	 * 
	 * @return is visible?
	 */
	public boolean isContentsDisplayShown() {
		return contentsDisplay != null && frame.isVisible();
	}

	/**
	 * Returns whether or not the options is currently visible.
	 * 
	 * @return is visible?
	 */
	public boolean isOptionsShown() {
		return options != null && options.isVisible();
	}

	/**
	 * Shows the contents display frame.
	 * 
	 * @param viewer
	 *            what viewer to show contents display for
	 */
	private void showContentsDisplay(OVTK2PropertiesAggregator viewer) {

		if (contentsDisplay == null) {
			// displays on right click
			contentsDisplay = new ContentsDisplay(viewer.getONDEXJUNGGraph(),
					new ActivatedHyperlinkListener(frame), frame);
		}

		updateContentsDisplay(viewer);

		// check if there is already a contents display
		if (!frame.isVisible())
			frame.setVisible(true);
		contentsDisplay.refresh();
		frame.toFront();
	}

	/**
	 * Shows the options frame.
	 * 
	 * @param viewer
	 *            what viewer to show options for
	 */
	private void showOptions(OVTK2PropertiesAggregator viewer) {

		// check if there is already a options
		if (options == null) {
			options = new LiteLayoutOptions(viewer);
			options.addComponentListener(this);
		} else {
			options.setViewer(viewer);
		}
		options.setVisible(true);
		options.toFront();
	}

	/**
	 * Refreshes the contents display with a different viewer.
	 * 
	 * @param viewer
	 *            viewer to set to contents display
	 */
	private void updateContentsDisplay(OVTK2PropertiesAggregator viewer) {

		LiteDefaultModalGraphMouse graphMouse = (LiteDefaultModalGraphMouse) viewer
				.getVisualizationViewer().getGraphMouse();

		// just in case we do not want to add it twice
		graphMouse.getOVTK2PickingMousePlugin().removePickingListener(
				contentsDisplay);
		graphMouse.getOVTK2PickingMousePlugin().addPickingListener(
				contentsDisplay);

		// get currently picked node
		Set<ONDEXConcept> pickedNodes = viewer.getPickedNodes();
		if (pickedNodes.size() > 0)
			contentsDisplay.showInfoFor(pickedNodes.iterator().next());
		else {
			// if no node selected, try edges
			Set<ONDEXRelation> pickedEdges = viewer.getPickedEdges();
			if (pickedEdges.size() > 0)
				contentsDisplay.showInfoFor(pickedEdges.iterator().next());
		}
	}

	@Override
	public void windowOpened(WindowEvent e) {

	}

	@Override
	public void windowClosing(WindowEvent e) {
		if (e.getSource() instanceof JFrame
				&& ((JFrame) e.getSource()).getContentPane() instanceof OVTK2Annotator) {
			OVTK2Annotator annotator = (OVTK2Annotator) ((JFrame) e.getSource())
					.getContentPane();

			// hack to close possible histogram frame
			if (annotator instanceof ScaleConceptAnnotator) {
				ScaleConceptAnnotator scale = (ScaleConceptAnnotator) annotator;
				if (scale.frame != null) {
					scale.frame.setVisible(false);
					scale.frame.dispose();
				}
			}

			// hack to close possible legend frame
			if (annotator instanceof ColorCategoryAnnotator) {
				ColorCategoryAnnotator cat = (ColorCategoryAnnotator) annotator;
				if (cat.frame != null) {
					cat.frame.setVisible(false);
					cat.frame.dispose();
				}
			}

			// if annotator hasn't been used, don't ask
			if (!annotator.hasBeenUsed())
				return;

			Object[] options = {
					Config.language.getProperty("Annotator.Save.Changes.Keep"),
					Config.language
							.getProperty("Annotator.Save.Changes.Discard") };
			int option = JOptionPane
					.showOptionDialog(
							(JFrame) e.getSource(),
							Config.language
									.getProperty("Annotator.Save.Changes.Text"),
							Config.language
									.getProperty("Annotator.Save.Changes.Title"),
							JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE, null, options,
							options[0]);

			switch (option) {
			case JOptionPane.YES_OPTION:
				// do nothing
				break;
			case JOptionPane.NO_OPTION:
				// reset to default values
				JCheckBoxMenuItem item = new JCheckBoxMenuItem();
				item.setSelected(false);
				this.actionPerformed(new ActionEvent(item,
						ActionEvent.ACTION_PERFORMED,
						AppearanceMenuAction.NODECOLOR));
				this.actionPerformed(new ActionEvent(item,
						ActionEvent.ACTION_PERFORMED,
						AppearanceMenuAction.EDGECOLOR));
				this.actionPerformed(new ActionEvent(item,
						ActionEvent.ACTION_PERFORMED,
						AppearanceMenuAction.NODESHAPE));
				this.actionPerformed(new ActionEvent(item,
						ActionEvent.ACTION_PERFORMED,
						AppearanceMenuAction.EDGESIZE));

				// reset icon transformer, might have been set by annotator
				main.getViewer().getVisualizationViewer().getRenderContext()
						.setVertexIconTransformer(null);

				// cleanup in title
				String name = main.getViewer().getTitle();
				name = name.replaceAll(" \\(.+\\)$", "");
				main.getViewer().setTitle(name);
				break;
			}

		}
	}

	@Override
	public void windowClosed(WindowEvent e) {

	}

	@Override
	public void windowIconified(WindowEvent e) {

	}

	@Override
	public void windowDeiconified(WindowEvent e) {

	}

	@Override
	public void windowActivated(WindowEvent e) {

	}

	@Override
	public void windowDeactivated(WindowEvent e) {

	}
}
