package net.sourceforge.ondex.ovtk2lite.popup;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import net.sourceforge.ondex.InvalidPluginArgumentException;
import net.sourceforge.ondex.core.ConceptAccession;
import net.sourceforge.ondex.core.ONDEXConcept;
import net.sourceforge.ondex.core.ONDEXRelation;
import net.sourceforge.ondex.ovtk2.config.Config;
import net.sourceforge.ondex.ovtk2.ui.contentsdisplay.plugins.AccessionPlugin;
import net.sourceforge.ondex.ovtk2.ui.popup.EntityMenuItem;
import net.sourceforge.ondex.ovtk2.ui.popup.VertexMenuListener;
import net.sourceforge.ondex.ovtk2.ui.popup.custom.CustomPopupItem;
import net.sourceforge.ondex.ovtk2.ui.popup.items.ChangeNodeClearFlagItem;
import net.sourceforge.ondex.ovtk2.ui.popup.items.ChangeNodeColorItem;
import net.sourceforge.ondex.ovtk2.ui.popup.items.ChangeNodeSetFlagItem;
import net.sourceforge.ondex.ovtk2.ui.popup.items.ChangeNodeShapeItem;
import net.sourceforge.ondex.ovtk2.ui.popup.items.ChangeNodeSizeItem;
import net.sourceforge.ondex.ovtk2.ui.popup.items.HideNodeConceptClassItem;
import net.sourceforge.ondex.ovtk2.ui.popup.items.HideNodeDataSourceItem;
import net.sourceforge.ondex.ovtk2.ui.popup.items.HideNodeEvidenceTypeItem;
import net.sourceforge.ondex.ovtk2.ui.popup.items.HideNodeItem;
import net.sourceforge.ondex.ovtk2.ui.popup.items.HideNodeLabelItem;
import net.sourceforge.ondex.ovtk2.ui.popup.items.HideNodeSameTagItem;
import net.sourceforge.ondex.ovtk2.ui.popup.items.HideNodeTagItem;
import net.sourceforge.ondex.ovtk2.ui.popup.items.HideOtherNodesItem;
import net.sourceforge.ondex.ovtk2.ui.popup.items.LinkChEMBLAssayCompsItem;
import net.sourceforge.ondex.ovtk2.ui.popup.items.LinkChEMBLAssayTargetsItem;
import net.sourceforge.ondex.ovtk2.ui.popup.items.LinkChEMBLCompAssaysItem;
import net.sourceforge.ondex.ovtk2.ui.popup.items.LinkChEMBLCompItem;
import net.sourceforge.ondex.ovtk2.ui.popup.items.LinkChEMBLCompSimilarityItem;
import net.sourceforge.ondex.ovtk2.ui.popup.items.LinkChEMBLCompTargetsItem;
import net.sourceforge.ondex.ovtk2.ui.popup.items.LinkChEMBLTargetAssaysItem;
import net.sourceforge.ondex.ovtk2.ui.popup.items.LinkChEMBLTargetCompsItem;
import net.sourceforge.ondex.ovtk2.ui.popup.items.LinkChEMBLTargetItem;
import net.sourceforge.ondex.ovtk2.ui.popup.items.LinkUniProtItem;
import net.sourceforge.ondex.ovtk2.ui.popup.items.MergeConceptsItem;
import net.sourceforge.ondex.ovtk2.ui.popup.items.ShowNodeLabelItem;
import net.sourceforge.ondex.ovtk2.ui.popup.items.ShowNodeNeighbourhoodConceptClassItem;
import net.sourceforge.ondex.ovtk2.ui.popup.items.ShowNodeNeighbourhoodItem;
import net.sourceforge.ondex.ovtk2.ui.popup.items.ShowNodeNeighbourhoodRelationTypeItem;
import net.sourceforge.ondex.ovtk2.ui.popup.items.ShowNodeRelationsVisibleItem;
import net.sourceforge.ondex.ovtk2.ui.popup.items.ShowNodeSameTagItem;
import net.sourceforge.ondex.ovtk2.ui.popup.items.ShowNodeTagItem;
import net.sourceforge.ondex.ovtk2lite.Main;
import net.sourceforge.ondex.validator.htmlaccessionlink.Condition;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.picking.PickedState;

/**
 * Menu shown on right click on nodes.
 * 
 * @author taubertj
 * 
 */
public class VertexMenu extends JPopupMenu implements
		VertexMenuListener<ONDEXConcept, ONDEXRelation> {

	Main main;

	JMenu change, hide, show, link;

	/**
	 * generated
	 */
	private static final long serialVersionUID = -1024568069913408029L;

	public VertexMenu(Main main) {
		this.main = main;
	}

	@Override
	public void setVertexAndView(ONDEXConcept vertex,
			VisualizationViewer<ONDEXConcept, ONDEXRelation> visComp) {
		removeAll();

		// adds accesion link to menu
		addLink(vertex);

		// new change menu
		change = new JMenu(
				Config.language.getProperty("Viewer.VertexMenu.ChangeBy"));

		// new link menu
		link = new JMenu(
				Config.language.getProperty("Viewer.VertexMenu.LinkBy"));

		// new hide menu
		hide = new JMenu(
				Config.language.getProperty("Viewer.VertexMenu.HideBy"));

		// new show menu
		show = new JMenu(
				Config.language.getProperty("Viewer.VertexMenu.ShowBy"));

		// make sure vertex is picked
		PickedState<ONDEXConcept> pickedNodes = main.getViewer()
				.getVisualizationViewer().getPickedVertexState();
		if (!pickedNodes.isPicked(vertex))
			pickedNodes.pick(vertex, true);
		Set<ONDEXConcept> set = pickedNodes.getPicked();

		// adds items to menu
		addItem(new ChangeNodeClearFlagItem(), set);
		addItem(new ChangeNodeColorItem(), set);
		addItem(new ChangeNodeSetFlagItem(), set);
		addItem(new ChangeNodeShapeItem(), set);
		addItem(new ChangeNodeSizeItem(), set);
		addItem(new HideNodeConceptClassItem(), set);
		addItem(new HideNodeDataSourceItem(), set);
		addItem(new HideNodeEvidenceTypeItem(), set);
		addItem(new HideNodeItem(), set);
		addItem(new HideNodeLabelItem(), set);
		addItem(new HideNodeSameTagItem(), set);
		addItem(new HideNodeTagItem(), set);
		addItem(new HideOtherNodesItem(), set);
		addItem(new MergeConceptsItem(), set);
		addItem(new ShowNodeLabelItem(), set);
		addItem(new ShowNodeNeighbourhoodConceptClassItem(), set);
		addItem(new ShowNodeNeighbourhoodItem(), set);
		addItem(new ShowNodeNeighbourhoodRelationTypeItem(), set);
		addItem(new ShowNodeRelationsVisibleItem(), set);
		addItem(new ShowNodeSameTagItem(), set);
		addItem(new ShowNodeTagItem(), set);

		// part of experimental modules
		addItem(new LinkChEMBLAssayCompsItem(), set);
		addItem(new LinkChEMBLAssayTargetsItem(), set);
		addItem(new LinkChEMBLCompAssaysItem(), set);
		addItem(new LinkChEMBLCompItem(), set);
		addItem(new LinkChEMBLCompSimilarityItem(), set);
		addItem(new LinkChEMBLCompTargetsItem(), set);
		addItem(new LinkChEMBLTargetAssaysItem(), set);
		addItem(new LinkChEMBLTargetCompsItem(), set);
		addItem(new LinkChEMBLTargetItem(), set);
		addItem(new LinkUniProtItem(), set);

		// add non-empty menus to popup
		if (change.getSubElements().length > 0)
			add(change);
		if (link.getSubElements().length > 0)
			add(link);
		if (hide.getSubElements().length > 0)
			add(hide);
		if (show.getSubElements().length > 0)
			add(show);

		// add custom popup items
		if (Boolean.parseBoolean(Config.config
				.getProperty("PopupEditor.Enable"))) {
			CustomPopupItem menuItem = new CustomPopupItem(main.getListener()
					.getContentsDisplayFrame());
			menuItem.init(main.getViewer(), set);
			if (menuItem.accepts()) {
				addSeparator();
				add(menuItem.getItem());
			}
		}

	}

	/**
	 * Adds an EntityMenuItem to the correct menu if it accepts graph selection
	 * 
	 * @param item
	 * @param set
	 */
	private void addItem(EntityMenuItem<ONDEXConcept> item,
			Set<ONDEXConcept> set) {
		item.init(main.getViewer(), set);
		if (item.accepts()) {
			switch (item.getCategory()) {
			case HIDE:
				hide.add(item.getItem());
				break;
			case LINK:
				link.add(item.getItem());
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

	/**
	 * Constructs a click able menu entry for the relevant accession link, which
	 * is non-ambiguous and of same data source, only the first one found is
	 * taken.
	 * 
	 * @param vertex
	 */
	private void addLink(ONDEXConcept vertex) {
		// find link to display for concept
		Set<ConceptAccession> accs = vertex.getConceptAccessions();
		if (accs.size() > 0) {
			for (ConceptAccession acc : accs) {
				if (!acc.isAmbiguous()
						&& acc.getElementOf().equals(vertex.getElementOf())) {

					// this is to load possible htmlaccession file
					try {
						new AccessionPlugin(main.getViewer()
								.getONDEXJUNGGraph());
					} catch (InvalidPluginArgumentException e) {
						JOptionPane.showMessageDialog(main.getListener()
								.getContentsDisplayFrame(), e.getMessage());
					}

					// get URL for this type of accessions
					String url = AccessionPlugin.cvToURL.get(acc.getElementOf()
							.getId());
					if (AccessionPlugin.mapper != null) {
						Condition cond = new Condition(acc.getElementOf()
								.getId(), vertex.getElementOf().getId());
						String prefix = (String) AccessionPlugin.mapper
								.validate(cond);
						if (prefix != null && prefix.length() > 0) {
							url = prefix;
						}
					}

					// add in URL
					if (url != null) {
						try {
							// try to build a URI for link
							final URI uri = new URI(url + ""
									+ acc.getAccession());

							// make menu item with blue text
							JMenuItem item = new JMenuItem(acc.getElementOf()
									.getId() + ": " + acc.getAccession());
							item.setForeground(Color.BLUE);
							item.addActionListener(new ActionListener() {

								@Override
								public void actionPerformed(ActionEvent e) {
									Desktop desktop = null;
									// Before more Desktop API is used, first
									// check whether the API is supported by
									// this particular virtual machine (VM) on
									// this particular host.
									if (Desktop.isDesktopSupported()) {
										desktop = Desktop.getDesktop();

										// open href in browser
										try {
											desktop.browse(uri);
										} catch (IOException ioe) {
											JOptionPane.showMessageDialog(main
													.getListener()
													.getContentsDisplayFrame(),
													ioe.getMessage());
										}
									} else {
										JOptionPane
												.showMessageDialog(
														main.getListener()
																.getContentsDisplayFrame(),
														"Hyperlinks not supported by OS.");
									}
								}
							});

							// add to pop-up menu
							this.add(item);
						} catch (URISyntaxException e1) {
							JOptionPane
									.showMessageDialog(main.getListener()
											.getContentsDisplayFrame(), e1
											.getMessage());
						}
					}

					// only one accession so far
					break;
				}
			}
		}
	}

}
