package net.sourceforge.ondex.ovtk2lite;

import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import javax.swing.JPopupMenu;

import net.sourceforge.ondex.core.ONDEXConcept;
import net.sourceforge.ondex.core.ONDEXRelation;
import net.sourceforge.ondex.ovtk2.ui.OVTK2PropertiesAggregator;
import net.sourceforge.ondex.ovtk2.ui.mouse.OVTK2GraphMouse;
import net.sourceforge.ondex.ovtk2.ui.mouse.OVTK2PickingMousePlugin;
import net.sourceforge.ondex.ovtk2.ui.popup.PopupVertexEdgeMenuMousePlugin;
import net.sourceforge.ondex.ovtk2lite.popup.EdgeMenu;
import net.sourceforge.ondex.ovtk2lite.popup.VertexMenu;
import edu.uci.ics.jung.algorithms.layout.GraphElementAccessor;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.control.ScalingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.ViewScalingControl;

/**
 * Specific mouse behaviour for ovtk2lite
 * 
 * @author taubertj
 * 
 */
public class LiteDefaultModalGraphMouse extends
		DefaultModalGraphMouse<ONDEXConcept, ONDEXRelation> implements
		OVTK2GraphMouse {

	private Main main;

	private PopupVertexEdgeMenuMousePlugin<ONDEXConcept, ONDEXRelation> myPlugin = null;

	boolean restoreMode = false;

	private ScalingControl scaler = new CrossoverScalingControl();

	private OVTK2PropertiesAggregator viewer;

	/**
	 * Requires the applet class
	 * 
	 * @param main
	 */
	public LiteDefaultModalGraphMouse(Main main) {
		super();
		this.main = main;
		this.viewer = main.getViewer();

		// picking mouse producer with mouse over highlighting
		this.pickingPlugin = new OVTK2PickingMousePlugin(
				viewer.getVisualizationViewer());

		// Trying out our new popup menu mouse producer...
		this.myPlugin = new PopupVertexEdgeMenuMousePlugin<ONDEXConcept, ONDEXRelation>();

		// Add some popup menus for the edges and vertices to our mouse
		// producer.
		JPopupMenu edgeMenu = new EdgeMenu(viewer);
		JPopupMenu vertexMenu = new VertexMenu(main);
		myPlugin.setEdgePopup(edgeMenu);
		myPlugin.setVertexPopup(vertexMenu);

		this.add(myPlugin); // Add our new producer to the mouse

		// zoom behaviour like google maps
		this.remove(scalingPlugin);
		this.scaler = new CrossoverScalingControl();
		this.scalingPlugin = new ScalingGraphMousePlugin(scaler, 0, out, in);
		this.add(scalingPlugin);
	}
	
	@Override
	public OVTK2PickingMousePlugin getOVTK2PickingMousePlugin() {
		if (this.pickingPlugin instanceof OVTK2PickingMousePlugin)
			return (OVTK2PickingMousePlugin) this.pickingPlugin;
		else
			return null;
	}
	
	/**
	 * @return the scaler
	 */
	@Override
	public ScalingControl getScaler() {
		return scaler;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		super.mouseClicked(e);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (e.isShiftDown()) {
			super.mousePressed(e);
			return;
		}
		if (e.getSource() instanceof VisualizationViewer<?, ?>) {
			if (super.mode == Mode.TRANSFORMING
					|| super.mode == Mode.ANNOTATING) {
				super.mousePressed(e);
				return;
			}
			restoreMode = true;
			Point2D p = e.getPoint();
			// is pick support available
			GraphElementAccessor<ONDEXConcept, ONDEXRelation> pickSupport = viewer
					.getVisualizationViewer().getPickSupport();
			if (pickSupport != null
					&& (pickSupport.getEdge(viewer.getVisualizationViewer()
							.getGraphLayout(), p.getX(), p.getY()) == null && pickSupport
							.getVertex(viewer.getVisualizationViewer()
									.getGraphLayout(), p.getX(), p.getY()) == null)) {
				viewer.getVisualizationViewer().getPickedVertexState().clear();
				viewer.getVisualizationViewer().getPickedEdgeState().clear();
				((ModalGraphMouse) viewer.getVisualizationViewer()
						.getGraphMouse()).setMode(Mode.TRANSFORMING);
			} else if (pickSupport != null) {
				if (main.getListener().getContentsDisplayFrame().isVisible()
						&& e.getButton() == MouseEvent.BUTTON1) {
					main.getListener().getContentsDisplay().refresh();
					main.getListener().getContentsDisplayFrame().toFront();
				}
			}
		}
		super.mousePressed(e);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		super.mouseReleased(e);
		if (restoreMode == true) {
			restoreMode = false;
			if (e.getSource() instanceof VisualizationViewer<?, ?>) {
				VisualizationViewer<?, ?> vv = (VisualizationViewer<?, ?>) e
						.getSource();
				((ModalGraphMouse) vv.getGraphMouse()).setMode(Mode.PICKING);
			}
		}
	}

	/**
	 * @param scaler the scaler to set
	 */
	@Override
	public void setScaler(ScalingControl scaler) {
		this.scaler = scaler;
	}

	/**
	 * Sets view scaling enabled or disabled.
	 * 
	 * @param enabled
	 */
	public void setViewScaling(boolean enabled) {
		if (enabled) {
			// only use view scaling
			this.remove(scalingPlugin);
			this.scaler = new ViewScalingControl();
			this.scalingPlugin = new ScalingGraphMousePlugin(scaler, 0, out, in);
			this.add(scalingPlugin);
		} else {
			// cross over scaling
			this.remove(scalingPlugin);
			this.scaler = new CrossoverScalingControl();
			this.scalingPlugin = new ScalingGraphMousePlugin(scaler, 0, out, in);
			this.add(scalingPlugin);
		}
	}
}
