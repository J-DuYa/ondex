package net.sourceforge.ondex.ovtk2lite;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.beans.XMLDecoder;
import java.io.ByteArrayInputStream;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.undo.UndoManager;

import net.sourceforge.ondex.core.Attribute;
import net.sourceforge.ondex.core.AttributeName;
import net.sourceforge.ondex.core.ONDEXConcept;
import net.sourceforge.ondex.core.ONDEXEntity;
import net.sourceforge.ondex.core.ONDEXGraphMetaData;
import net.sourceforge.ondex.core.ONDEXRelation;
import net.sourceforge.ondex.ovtk2.config.Config;
import net.sourceforge.ondex.ovtk2.graph.ONDEXEdgeArrows;
import net.sourceforge.ondex.ovtk2.graph.ONDEXEdgeColors;
import net.sourceforge.ondex.ovtk2.graph.ONDEXEdgeLabels;
import net.sourceforge.ondex.ovtk2.graph.ONDEXEdgeShapes;
import net.sourceforge.ondex.ovtk2.graph.ONDEXEdgeStrokes;
import net.sourceforge.ondex.ovtk2.graph.ONDEXJUNGGraph;
import net.sourceforge.ondex.ovtk2.graph.ONDEXNodeDrawPaint;
import net.sourceforge.ondex.ovtk2.graph.ONDEXNodeFillPaint;
import net.sourceforge.ondex.ovtk2.graph.ONDEXNodeLabels;
import net.sourceforge.ondex.ovtk2.graph.ONDEXNodeShapes;
import net.sourceforge.ondex.ovtk2.graph.ONDEXNodeShapes.NodeShapeSelection;
import net.sourceforge.ondex.ovtk2.graph.custom.ONDEXBasicVertexRenderer;
import net.sourceforge.ondex.ovtk2.layout.ConceptClassCircleLayout;
import net.sourceforge.ondex.ovtk2.layout.GEMLayout;
import net.sourceforge.ondex.ovtk2.layout.OVTK2Layouter;
import net.sourceforge.ondex.ovtk2.metagraph.ONDEXMetaGraph;
import net.sourceforge.ondex.ovtk2.ui.OVTK2PropertiesAggregator;
import net.sourceforge.ondex.ovtk2.ui.OVTK2Viewer;
import net.sourceforge.ondex.ovtk2.util.AppearanceSynchronizer;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse.Mode;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;

/**
 * Viewer implementation of the applet
 * 
 * @author taubertj
 * 
 */
public class LiteViewer extends JPanel implements OVTK2PropertiesAggregator {

	/**
	 * generated
	 */
	private static final long serialVersionUID = 8721345931480078932L;

	// paint antialiased
	private boolean antiAliased = false;

	private final ONDEXEdgeArrows edgeArrows;

	private final ONDEXEdgeColors edgeColors;

	private final ONDEXEdgeLabels edgeLabels;

	private final ONDEXEdgeShapes edgeShapes;

	private final ONDEXEdgeStrokes edgeStrokes;

	// JUNG graph
	private final ONDEXJUNGGraph graph;

	// rendering hints
	private final Map<RenderingHints.Key, Object> hints = new HashMap<RenderingHints.Key, Object>();

	// JUNG synchronized ONDEXMetaGraph is null
	private final ONDEXMetaGraph meta = null;

	private final ONDEXNodeDrawPaint nodeDrawPaint;

	private final ONDEXNodeFillPaint nodeFillPaint;

	private final ONDEXNodeLabels nodeLabels;

	private final ONDEXNodeShapes nodeShapes;

	// used for scaling by toolbar buttons
	private final CrossoverScalingControl scaler = new CrossoverScalingControl();

	// main visualisation pane
	private final GraphZoomScrollPane scrollPane;

	// show edge labels
	private boolean showEdgeLabels = false;

	// show node labels
	private boolean showNodeLabels = false;

	// handles visibility undo events
	private final UndoManager undoManager = new UndoManager();

	// set node sizes
	private boolean useEntitySizes = true;

	// JUNG visualisation viewer
	private final VisualizationViewer<ONDEXConcept, ONDEXRelation> visviewer;

	/**
	 * Wraps a given graph
	 * 
	 * @param graph
	 */
	public LiteViewer(Main main, ONDEXJUNGGraph graph) {
		this.graph = graph;
		main.setViewer(this);

		// ask for menu size
		int menuHeight = main.getJMenuBar().getBounds().height;

		// prepare viewer, account for menu size
		visviewer = new VisualizationViewer<ONDEXConcept, ONDEXRelation>(
				new ConceptClassCircleLayout(this), new Dimension(
						main.getWidth(), main.getHeight() - menuHeight));
		visviewer.setBackground(Color.white);
		visviewer.setDoubleBuffered(true);
		visviewer.addKeyListener(new LiteKeyListener(this));

		main.getStatusLabel().setText("Setting graph layout...");

		// load visibility pattern first - POPLAR-4
		loadVisibility(graph);

		// set default layout
		OVTK2Layouter layouter = null;
		if (main.getParameter("layout") != null) {
			String className = main.getParameter("layout");
			try {
				Class<?> clazz = Class.forName(className);
				Constructor<?> constr = clazz.getConstructor(
						VisualizationViewer.class, ONDEXJUNGGraph.class);
				layouter = (OVTK2Layouter) constr.newInstance(visviewer, graph);
			} catch (Exception e) {
				JOptionPane
						.showMessageDialog(
								main,
								e.getMessage(),
								"Error while setting graph layout. Reverting to default.",
								JOptionPane.WARNING_MESSAGE);
				layouter = new ConceptClassCircleLayout(this);
			}
		} else {
			layouter = new GEMLayout(this);
		}
		visviewer.setGraphLayout(layouter);

		main.getStatusLabel().setText("Configuring appearance...");

		// default label position
		visviewer.getRenderer().getVertexLabelRenderer()
				.setPosition(Position.AUTO);

		// set custom vertex renderer for shape transparency
		visviewer.getRenderer().setVertexRenderer(
				new ONDEXBasicVertexRenderer(graph));

		// initialise node labels
		nodeLabels = new ONDEXNodeLabels(true);
		visviewer.getRenderContext().setVertexLabelTransformer(nodeLabels);

		// initialise edge labels
		edgeLabels = new ONDEXEdgeLabels();
		visviewer.getRenderContext().setEdgeLabelTransformer(edgeLabels);

		// initialise node shapes
		nodeShapes = new ONDEXNodeShapes();
		visviewer.getRenderContext().setVertexShapeTransformer(nodeShapes);

		// initialise edge shapes
		edgeShapes = new ONDEXEdgeShapes();
		visviewer.getRenderContext().setEdgeShapeTransformer(edgeShapes);

		// initialise node colours
		nodeFillPaint = new ONDEXNodeFillPaint(visviewer.getPickedVertexState());
		visviewer.getRenderContext().setVertexFillPaintTransformer(
				nodeFillPaint);

		// initialise node draw colors
		nodeDrawPaint = new ONDEXNodeDrawPaint();
		visviewer.getRenderContext().setVertexDrawPaintTransformer(
				nodeDrawPaint);

		// initialise edge colors
		edgeColors = new ONDEXEdgeColors(visviewer.getPickedEdgeState());
		visviewer.getRenderContext().setEdgeDrawPaintTransformer(edgeColors);

		// initialize edge strokes
		edgeStrokes = new ONDEXEdgeStrokes();
		visviewer.getRenderContext().setEdgeStrokeTransformer(edgeStrokes);

		// initialize edge arrows
		edgeArrows = new ONDEXEdgeArrows();
		visviewer.getRenderContext().setEdgeArrowPredicate(edgeArrows);

		// set anti-aliasing painting off
		Map<?, ?> temp = visviewer.getRenderingHints();

		// copying necessary because of type safety
		Iterator<?> it = temp.keySet().iterator();
		while (it.hasNext()) {
			RenderingHints.Key key = (RenderingHints.Key) it.next();
			hints.put(key, temp.get(key));
		}
		hints.put(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_OFF);
		visviewer.setRenderingHints(hints);

		// standard mouse support
		LiteDefaultModalGraphMouse graphMouse = new LiteDefaultModalGraphMouse(
				main);
		visviewer.setGraphMouse(graphMouse);
		visviewer.addKeyListener(graphMouse.getModeKeyListener());

		// zoom pane and mouse menu in the corner
		scrollPane = new GraphZoomScrollPane(visviewer);
		JMenuBar menu = new JMenuBar();
		menu.add(graphMouse.getModeMenu());
		scrollPane.setCorner(menu);

		// add to JPanel
		this.setLayout(new GridLayout(1, 1));
		this.add(scrollPane);

		// set graph mode to picking
		graphMouse.setMode(Mode.PICKING);

		// updates all entities to default rendering styles
		updateViewer(null);
	}

	/**
	 * Calculates actual bounds of a painted graph.
	 * 
	 * @return Point2D[]
	 */
	private Point2D[] calcBounds() {
		Point2D[] result = new Point2D[2];
		Point2D min = null;
		Point2D max = null;
		Layout<ONDEXConcept, ONDEXRelation> layout = visviewer.getGraphLayout();
		Iterator<ONDEXConcept> it = graph.getVertices().iterator();
		while (it.hasNext()) {
			Point2D point = layout.transform(it.next());
			if (min == null) {
				min = new Point2D.Double(0, 0);
				min.setLocation(point);
			}
			if (max == null) {
				max = new Point2D.Double(0, 0);
				max.setLocation(point);
			}
			min.setLocation(Math.min(min.getX(), point.getX()),
					Math.min(min.getY(), point.getY()));
			max.setLocation(Math.max(max.getX(), point.getX()),
					Math.max(max.getY(), point.getY()));
		}
		result[0] = min;
		result[1] = max;
		return result;
	}

	/**
	 * Centers the current graph within the window
	 */
	@Override
	public void center() {
		// reset scaling for predictive behaviour
		visviewer.getRenderContext().getMultiLayerTransformer()
				.getTransformer(Layer.LAYOUT).setToIdentity();
		visviewer.getRenderContext().getMultiLayerTransformer()
				.getTransformer(Layer.VIEW).setToIdentity();

		// place layout center in center of the view
		Point2D[] calc = calcBounds();
		Point2D min = calc[0];
		Point2D max = calc[1];

		if (min == null || max == null) {
			return; // nothing to center on
		}

		Point2D screen_center = visviewer.getCenter();
		Point2D layout_bounds = new Point2D.Double(max.getX() - min.getX(),
				max.getY() - min.getY());
		Point2D layout_center = new Point2D.Double(screen_center.getX()
				- (layout_bounds.getX() / 2) - min.getX(), screen_center.getY()
				- (layout_bounds.getY() / 2) - min.getY());
		visviewer.getRenderContext().getMultiLayerTransformer()
				.getTransformer(Layer.VIEW)
				.translate(layout_center.getX(), layout_center.getY());

		// scale graph
		Point2D scale_bounds = new Point2D.Double(visviewer.getWidth()
				/ layout_bounds.getX(), visviewer.getHeight()
				/ layout_bounds.getY());
		float scale = (float) Math
				.min(scale_bounds.getX(), scale_bounds.getY());
		scale = 0.95f * scale;
		scaler.scale(visviewer, scale, visviewer.getCenter());
	}

	@Override
	public ONDEXEdgeArrows getEdgeArrows() {
		return edgeArrows;
	}

	@Override
	public ONDEXEdgeColors getEdgeColors() {
		return edgeColors;
	}

	@Override
	public Font getEdgeFont() {
		if (graph.getAnnotations().containsKey(OVTK2Viewer.EDGEFONT)) {
			ByteArrayInputStream bis = new ByteArrayInputStream(graph
					.getAnnotations().get(OVTK2Viewer.EDGEFONT).getBytes());
			XMLDecoder decoder = new XMLDecoder(bis);
			return (Font) decoder.readObject();
		}
		return null;
	}

	@Override
	public ONDEXEdgeLabels getEdgeLabels() {
		return edgeLabels;
	}

	@Override
	public ONDEXEdgeShapes getEdgeShapes() {
		return edgeShapes;
	}

	@Override
	public ONDEXEdgeStrokes getEdgeStrokes() {
		return edgeStrokes;
	}

	/**
	 * Returns ONDEXMetaGraph, which is a JUNG graph implementation.
	 * 
	 * @return OMDEXMetaGraph
	 */
	@Override
	public ONDEXMetaGraph getMetaGraph() {
		return meta;
	}

	@Override
	public ONDEXNodeFillPaint getNodeColors() {
		return nodeFillPaint;
	}

	@Override
	public ONDEXNodeDrawPaint getNodeDrawPaint() {
		return nodeDrawPaint;
	}

	@Override
	public ONDEXNodeLabels getNodeLabels() {
		return nodeLabels;
	}

	@Override
	public ONDEXNodeShapes getNodeShapes() {
		return nodeShapes;
	}

	@Override
	public ONDEXJUNGGraph getONDEXJUNGGraph() {
		return graph;
	}

	@Override
	public Set<ONDEXRelation> getPickedEdges() {
		return visviewer.getPickedEdgeState().getPicked();
	}

	@Override
	public Set<ONDEXConcept> getPickedNodes() {
		return visviewer.getPickedVertexState().getPicked();
	}

	/**
	 * @return the scaler
	 */
	public CrossoverScalingControl getScaler() {
		return scaler;
	}

	@Override
	public String getTitle() {
		return graph.getName();
	}

	@Override
	public UndoManager getUndoManager() {
		return undoManager;
	}

	@Override
	public Font getVertexFont() {
		if (graph.getAnnotations().containsKey(OVTK2Viewer.VERTEXFONT)) {
			ByteArrayInputStream bis = new ByteArrayInputStream(graph
					.getAnnotations().get(OVTK2Viewer.VERTEXFONT).getBytes());
			XMLDecoder decoder = new XMLDecoder(bis);
			return (Font) decoder.readObject();
		}
		return null;
	}

	@Override
	public VisualizationViewer<ONDEXConcept, ONDEXRelation> getVisualizationViewer() {
		return visviewer;
	}

	/**
	 * @return the antiAliased
	 */
	@Override
	public boolean isAntiAliased() {
		return antiAliased;
	}

	@Override
	public boolean isDestroy() {
		return false;
	}

	@Override
	public boolean isRelayoutOnResize() {
		return false;
	}

	/**
	 * @return the showEdgeLabels
	 */
	@Override
	public boolean isShowEdgeLabels() {
		return showEdgeLabels;
	}

	/**
	 * @return the showNodeLabels
	 */
	@Override
	public boolean isShowNodeLabels() {
		return showNodeLabels;
	}

	/**
	 * @return the useEntitySizes
	 */
	public boolean isUseEntitySizes() {
		return useEntitySizes;
	}

	@Override
	public boolean isVisible() {
		return true;
	}

	/**
	 * Sets visibility according to attributes
	 * 
	 * @param graph
	 */
	private void loadVisibility(ONDEXJUNGGraph graph) {

		ONDEXGraphMetaData meta = graph.getMetaData();
		AttributeName attrVisible;

		// prevent whole graph from disappearing
		if ((attrVisible = meta.getAttributeName("visible")) != null
				&& graph.getConceptsOfAttributeName(attrVisible).size() > 0) {

			// load node visibility
			for (ONDEXConcept c : graph.getConcepts()) {
				Attribute attribute = c.getAttribute(attrVisible);
				if (attribute != null)
					graph.setVisibility(c,
							((Boolean) attribute.getValue()).booleanValue());
				else
					graph.setVisibility(c, false);
			}

			// load edge visibility
			for (ONDEXRelation r : graph.getRelations()) {
				Attribute attribute = r.getAttribute(attrVisible);
				if (attribute != null)
					graph.setVisibility(r,
							((Boolean) attribute.getValue()).booleanValue());
				else
					graph.setVisibility(r, false);
			}
		}
	}

	/**
	 * @param antiAliased
	 *            the antiAliased to set
	 */
	@Override
	public void setAntiAliased(boolean antiAliased) {
		this.antiAliased = antiAliased;
		if (antiAliased) {
			hints.put(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			visviewer.setRenderingHints(hints);
		} else {
			hints.put(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_OFF);
			visviewer.setRenderingHints(hints);
		}
		visviewer.getModel().fireStateChanged();
	}

	/**
	 * @param showEdgeLabels
	 *            the showEdgeLabels to set
	 */
	@Override
	public void setShowEdgeLabels(boolean showEdgeLabels) {
		this.showEdgeLabels = showEdgeLabels;
		getEdgeLabels().fillMask(showEdgeLabels);
		visviewer.getModel().fireStateChanged();
	}

	/**
	 * @param showNodeLabels
	 *            the showNodeLabels to set
	 */
	@Override
	public void setShowNodeLabels(boolean showNodeLabels) {
		this.showNodeLabels = showNodeLabels;
		getNodeLabels().fillMask(showNodeLabels);
		visviewer.getModel().fireStateChanged();
	}

	@Override
	public void setTitle(String title) {
		// ignored for now
	}

	/**
	 * @param useEntitySizes
	 *            the useEntitySizes to set
	 */
	public void setUseEntitySizes(boolean useEntitySizes) {
		this.useEntitySizes = useEntitySizes;
		useEntitySizes(useEntitySizes);
	}

	/**
	 * Update with current changes.
	 * 
	 * @param entity
	 *            ONDEXEntity or null
	 */
	@Override
	public synchronized void updateViewer(ONDEXEntity entity) {

		if (entity == null) {
			// ensure that none of these pull the entire underlying graph into
			// the GUI
			this.getNodeLabels().updateAll();
			this.getNodeColors().updateAll();
			this.getNodeDrawPaint().updateAll();
			this.getNodeShapes().updateAll();
			this.getEdgeLabels().updateAll();
			this.getEdgeColors().updateAll();
			this.getEdgeShapes().updateAll();
		} else if (entity instanceof ONDEXConcept) {
			ONDEXConcept node = (ONDEXConcept) entity;
			this.getNodeLabels().updateLabel(node);
			this.getNodeColors().updateColor(node);
			this.getNodeDrawPaint().updateColor(node);
			this.getNodeShapes().updateShape(node);
		} else if (entity instanceof ONDEXRelation) {
			ONDEXRelation edge = (ONDEXRelation) entity;
			this.getEdgeLabels().updateLabel(edge);
			this.getEdgeColors().updateColor(edge);
		}

		visviewer.getModel().fireStateChanged();
		visviewer.repaint();
	}

	/**
	 * Toggles node size mapping
	 * 
	 * @param use
	 *            use node sizes?
	 */
	private void useEntitySizes(boolean use) {
		if (use) {
			AppearanceSynchronizer.loadNodeShape(graph, getNodeShapes());
			AppearanceSynchronizer.loadEdgeSize(graph, getEdgeStrokes());
		} else {
			// set new node size function
			getNodeShapes().setNodeSizes(
					new Transformer<ONDEXConcept, Integer>() {
						@Override
						public Integer transform(ONDEXConcept input) {
							return Config.defaultNodeSize;
						}
					});
			// update all
			getNodeShapes().setNodeShapeSelection(NodeShapeSelection.NONE);
			getNodeShapes().updateAll();
			// set new edge size function
			getEdgeStrokes().setEdgeSizes(
					new Transformer<ONDEXRelation, Integer>() {
						@Override
						public Integer transform(ONDEXRelation input) {
							return Config.defaultEdgeSize;
						}
					});
		}
		visviewer.getModel().fireStateChanged();
	}

	/**
	 * Set visibility of nodes and edges if not defined as attribute
	 * 
	 * @param isVisible
	 *            visibility
	 */
	public void useVisibleAttribute(boolean isVisible) {

		ONDEXGraphMetaData meta = graph.getMetaData();
		AttributeName attrVisible;
		if ((attrVisible = meta.getAttributeName("visible")) != null) {

			// load node visibility
			for (ONDEXConcept c : graph.getConcepts()) {
				Attribute attribute = c.getAttribute(attrVisible);
				if (attribute != null)
					graph.setVisibility(c,
							((Boolean) attribute.getValue()).booleanValue());
				else
					graph.setVisibility(c, isVisible);
			}

			// load edge visibility
			for (ONDEXRelation r : graph.getRelations()) {
				Attribute attribute = r.getAttribute(attrVisible);
				if (attribute != null)
					graph.setVisibility(r,
							((Boolean) attribute.getValue()).booleanValue());
				else
					graph.setVisibility(r, isVisible);
			}
		}

		visviewer.getModel().fireStateChanged();
	}

}
