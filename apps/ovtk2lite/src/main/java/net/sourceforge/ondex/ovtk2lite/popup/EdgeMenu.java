package net.sourceforge.ondex.ovtk2lite.popup;

import java.util.Set;

import javax.swing.JMenu;
import javax.swing.JPopupMenu;

import net.sourceforge.ondex.core.ONDEXConcept;
import net.sourceforge.ondex.core.ONDEXRelation;
import net.sourceforge.ondex.ovtk2.config.Config;
import net.sourceforge.ondex.ovtk2.ui.OVTK2PropertiesAggregator;
import net.sourceforge.ondex.ovtk2.ui.popup.EdgeMenuListener;
import net.sourceforge.ondex.ovtk2.ui.popup.EntityMenuItem;
import net.sourceforge.ondex.ovtk2.ui.popup.items.ChangeEdgeSizeItem;
import net.sourceforge.ondex.ovtk2.ui.popup.items.HideEdgeEvidenceTypeItem;
import net.sourceforge.ondex.ovtk2.ui.popup.items.HideEdgeItem;
import net.sourceforge.ondex.ovtk2.ui.popup.items.HideEdgeLabelItem;
import net.sourceforge.ondex.ovtk2.ui.popup.items.HideEdgeRelationTypeItem;
import net.sourceforge.ondex.ovtk2.ui.popup.items.HideEdgeSameTagItem;
import net.sourceforge.ondex.ovtk2.ui.popup.items.ShowEdgeLabelItem;
import net.sourceforge.ondex.ovtk2.ui.popup.items.ShowEdgeSameTagItem;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.picking.PickedState;

/**
 * Menu shown on right click on edges.
 * 
 * @author taubertj
 * 
 */
public class EdgeMenu extends JPopupMenu implements
		EdgeMenuListener<ONDEXConcept, ONDEXRelation> {

	OVTK2PropertiesAggregator aggregator;

	JMenu change, hide, show;

	/**
	 * generated
	 */
	private static final long serialVersionUID = -579398778319440138L;

	public EdgeMenu(OVTK2PropertiesAggregator aggregator) {
		this.aggregator = aggregator;
	}

	@Override
	public void setEdgeAndView(ONDEXRelation edge,
			VisualizationViewer<ONDEXConcept, ONDEXRelation> visComp) {
		removeAll();

		// new change menu
		change = new JMenu(
				Config.language.getProperty("Viewer.EdgeMenu.ChangeBy"));

		// new hide menu
		hide = new JMenu(Config.language.getProperty("Viewer.EdgeMenu.HideBy"));

		// new show menu
		show = new JMenu(Config.language.getProperty("Viewer.EdgeMenu.ShowBy"));

		// make sure edge is picked
		PickedState<ONDEXRelation> pickedEdges = aggregator
				.getVisualizationViewer().getPickedEdgeState();
		if (!pickedEdges.isPicked(edge))
			pickedEdges.pick(edge, true);
		Set<ONDEXRelation> set = pickedEdges.getPicked();

		// adds items to menu
		addItem(new ChangeEdgeSizeItem(), set);
		addItem(new HideEdgeEvidenceTypeItem(), set);
		addItem(new HideEdgeItem(), set);
		addItem(new HideEdgeLabelItem(), set);
		addItem(new HideEdgeRelationTypeItem(), set);
		addItem(new HideEdgeSameTagItem(), set);
		addItem(new ShowEdgeLabelItem(), set);
		addItem(new ShowEdgeSameTagItem(), set);

		// add non-empty menus to popup
		if (change.getSubElements().length > 0)
			add(change);
		if (hide.getSubElements().length > 0)
			add(hide);
		if (show.getSubElements().length > 0)
			add(show);

	}

	/**
	 * Adds an EntityMenuItem to the correct menu if it accepts graph selection
	 * 
	 * @param item
	 * @param set
	 */
	private void addItem(EntityMenuItem<ONDEXRelation> item,
			Set<ONDEXRelation> set) {
		item.init(aggregator, set);
		if (item.accepts()) {
			switch (item.getCategory()) {
			case HIDE:
				hide.add(item.getItem());
				break;
			case SHOW:
				show.add(item.getItem());
				break;
			case CHANGE:
				change.add(item.getItem());
				break;
			}
		}
	}

}
