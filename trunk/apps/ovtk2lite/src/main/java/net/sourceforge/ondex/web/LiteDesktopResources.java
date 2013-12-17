package net.sourceforge.ondex.web;

import javax.swing.JDesktopPane;

import net.sourceforge.ondex.logging.ONDEXLogger;
import net.sourceforge.ondex.ovtk2.ui.OVTK2MetaGraph;
import net.sourceforge.ondex.ovtk2.ui.OVTK2PropertiesAggregator;
import net.sourceforge.ondex.ovtk2.ui.OVTK2ResourceAssesor;
import net.sourceforge.ondex.ovtk2.ui.toolbars.OVTK2ToolBar;

public class LiteDesktopResources implements OVTK2ResourceAssesor {

	// core logger for graph events
	private ONDEXLogger logger = new ONDEXLogger();

	private Main main;

	public LiteDesktopResources(Main main) {
		this.main = main;
	}

	@Override
	public OVTK2PropertiesAggregator[] getAllViewers() {
		return new OVTK2PropertiesAggregator[] { main.getViewer() };
	}

	@Override
	public ONDEXLogger getLogger() {
		return logger;
	}

	@Override
	public JDesktopPane getParentPane() {
		return null;
	}

	@Override
	public OVTK2MetaGraph getSelectedMetagraph() {
		return null;
	}

	@Override
	public OVTK2PropertiesAggregator getSelectedViewer() {
		return main.getViewer();
	}

	@Override
	public OVTK2ToolBar getToolBar() {
		return null;
	}

	@Override
	public void setSelectedViewer(OVTK2PropertiesAggregator viewer) {
		// do nothing
	}

}
