package net.sourceforge.ondex.web.search;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import net.sourceforge.ondex.core.ConceptClass;
import net.sourceforge.ondex.core.DataSource;
import net.sourceforge.ondex.core.ONDEXConcept;
import net.sourceforge.ondex.ovtk2.config.Config;
import net.sourceforge.ondex.ovtk2.ui.OVTK2PropertiesAggregator;
import net.sourceforge.ondex.ovtk2.ui.toolbars.MenuGraphSearchBox;
import net.sourceforge.ondex.ovtk2.ui.toolbars.MenuGraphSearchBox.MetaDataWrapper;
import net.sourceforge.ondex.ovtk2.util.IntegerStringWrapper;

/**
 * Handles search box related action events.
 * 
 * @author taubertj
 * 
 */
public class SearchBoxAction implements ActionListener {

	OVTK2PropertiesAggregator viewer;

	public SearchBoxAction(OVTK2PropertiesAggregator v) {
		this.viewer = v;
	}

	@Override
	public void actionPerformed(ActionEvent ae) {

		// skip default search term
		if (ae.getActionCommand().equals(
				Config.language.getProperty("ToolBar.Search.Filler")))
			return;

		MenuGraphSearchBox searchBox = (MenuGraphSearchBox) ae.getSource();
		String searchMode = searchBox.getSearchMode();

		// get concept class restriction, null is valid
		ConceptClass conceptClass = null;
		if (searchBox.getConceptClasses().getSelectedItem() instanceof MetaDataWrapper) {
			conceptClass = (ConceptClass) ((MetaDataWrapper) searchBox
					.getConceptClasses().getSelectedItem()).getMetaData();
		}

		// get data source restriction, null is valid
		DataSource dataSource = null;
		if (searchBox.getDataSources().getSelectedItem() instanceof MetaDataWrapper) {
			dataSource = (DataSource) ((MetaDataWrapper) searchBox
					.getDataSources().getSelectedItem()).getMetaData();
		}

		// get context restriction, null is valid
		ONDEXConcept context = null;
		if (searchBox.getTags().getSelectedItem() instanceof IntegerStringWrapper) {
			context = viewer.getONDEXJUNGGraph().getConcept(
					((IntegerStringWrapper) searchBox.getTags()
							.getSelectedItem()).getValue());
		}

		System.out.println("Search for: " + searchBox.getSearchText());

		if (searchMode.equals(Config.language
				.getProperty("ToolBar.Search.Mode.Default"))) {
			// fire up search
			DialogSearchResult results = new DialogSearchResult(viewer,
					searchBox.getSearchText(), searchBox.isRegex(),
					searchBox.isCaseSensitive(), conceptClass, dataSource,
					context);
			results.setVisible(true);
		} else if (searchMode.equals(Config.language
				.getProperty("ToolBar.Search.Mode.SMILES"))
				|| searchMode.equals(Config.language
						.getProperty("ToolBar.Search.Mode.InChI"))
				|| searchMode.equals(Config.language
						.getProperty("ToolBar.Search.Mode.InChIKey"))
				|| searchMode.equals(Config.language
						.getProperty("ToolBar.Search.Mode.ChEMBL"))) {
			// special case for chemical search
			DialogChemicalSearch results = new DialogChemicalSearch(viewer,
					searchBox.getSearchText(), conceptClass, dataSource,
					context, searchMode, searchBox.getTanimotoSimilarity(),
					searchBox.isUseChEMBL());
			results.setVisible(true);
		} else if (searchMode.equals(Config.language
				.getProperty("ToolBar.Search.Mode.UniProt"))) {
			// special case for protein search
			DialogProteinSearch results = new DialogProteinSearch(viewer,
					searchBox.getSearchText());
			results.setVisible(true);
		}
	}
}
