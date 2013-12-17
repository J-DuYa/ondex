package net.sourceforge.ondex.web;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;
import java.util.Set;

import javax.swing.undo.StateEdit;

import net.sourceforge.ondex.core.ONDEXConcept;
import net.sourceforge.ondex.core.ONDEXRelation;
import net.sourceforge.ondex.ovtk2.config.Config;
import net.sourceforge.ondex.ovtk2.graph.VisibilityUndo;
import edu.uci.ics.jung.visualization.picking.PickedState;

/**
 * Separate Key listener events into its own class
 * 
 * @author taubertj
 * 
 */
public class LiteKeyListener implements KeyListener {

	// parent applet
	LiteViewer viewer;

	/**
	 * Requires the applet class
	 * 
	 * @param main
	 */
	public LiteKeyListener(LiteViewer viewer) {
		this.viewer = viewer;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int modifiersEx = e.getModifiersEx();
		if (modifiersEx == 128) {
			// CTRL + c
			if (e.getKeyChar() == 3) {
				// centre view
				viewer.center();
			}
			// CTRL + m
			else if (e.getKeyChar() == 13) {
				// show everything or just small network
				viewer.setUseEntitySizes(!viewer.isUseEntitySizes());
				viewer.useVisibleAttribute(viewer.isUseEntitySizes());
			}

			// undo and selection
			else if (KeyEvent.getKeyText(e.getKeyCode()).equalsIgnoreCase("z")) {
				if (viewer.getUndoManager().canUndo()) {
					viewer.getUndoManager().undo();
					viewer.getVisualizationViewer().repaint();
				}
			} else if (KeyEvent.getKeyText(e.getKeyCode())
					.equalsIgnoreCase("y")) {
				if (viewer.getUndoManager().canRedo()) {
					viewer.getUndoManager().redo();
					viewer.getVisualizationViewer().repaint();
				}
			} else if (KeyEvent.getKeyText(e.getKeyCode())
					.equalsIgnoreCase("a")) {
				PickedState<ONDEXConcept> pickState = viewer
						.getVisualizationViewer().getPickedVertexState();
				for (ONDEXConcept n : viewer.getONDEXJUNGGraph().getVertices())
					pickState.pick(n, true);
			}

			// hiding and complement
			else if (KeyEvent.getKeyText(e.getKeyCode()).equalsIgnoreCase("h")) {
				StateEdit edit = new StateEdit(new VisibilityUndo(
						viewer.getONDEXJUNGGraph()),
						Config.language.getProperty("Undo.HideSelection"));
				viewer.getUndoManager().addEdit(edit);

				// hide edges first
				for (ONDEXRelation ondexEdge : viewer.getPickedEdges()) {
					viewer.getONDEXJUNGGraph().setVisibility(ondexEdge, false);
				}

				// hide nodes next
				for (ONDEXConcept ondexNode : viewer.getPickedNodes()) {
					viewer.getONDEXJUNGGraph().setVisibility(ondexNode, false);
				}

				// update viewer
				viewer.getVisualizationViewer().getModel().fireStateChanged();
				edit.end();
			} else if (KeyEvent.getKeyText(e.getKeyCode())
					.equalsIgnoreCase("g")) {

				StateEdit edit = new StateEdit(new VisibilityUndo(
						viewer.getONDEXJUNGGraph()),
						Config.language.getProperty("Undo.RemoveComplement"));
				viewer.getUndoManager().addEdit(edit);

				Set<ONDEXConcept> allnodes = new HashSet<ONDEXConcept>(viewer
						.getONDEXJUNGGraph().getVertices());
				allnodes.removeAll(viewer.getPickedNodes());
				for (ONDEXConcept allnode : allnodes) {
					viewer.getONDEXJUNGGraph().setVisibility(allnode, false);
				}

				// update viewer
				viewer.getVisualizationViewer().getModel().fireStateChanged();
				edit.end();
			}
		}

		// Numpad -
		else if (e.getKeyCode() == 109) {
			// zoom out
			viewer.getScaler().scale(viewer.getVisualizationViewer(), 0.9f,
					viewer.getVisualizationViewer().getCenter());
		}
		// Numpad +
		else if (e.getKeyCode() == 107) {
			// zoom in
			viewer.getScaler().scale(viewer.getVisualizationViewer(), 1.1f,
					viewer.getVisualizationViewer().getCenter());
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {

	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

}
