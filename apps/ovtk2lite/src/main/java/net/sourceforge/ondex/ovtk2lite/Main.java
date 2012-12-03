package net.sourceforge.ondex.ovtk2lite;

import java.awt.Color;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JApplet;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.text.html.parser.ParserDelegator;

import net.sourceforge.ondex.core.base.AbstractAttribute;
import net.sourceforge.ondex.ovtk2.ui.OVTK2Desktop;
import net.sourceforge.ondex.ovtk2.ui.toolbars.MenuGraphSearchBox;
import net.sourceforge.ondex.ovtk2.util.AppearanceSynchronizer;

/**
 * Applet to display ONDEX graphs either from file or from web service.
 * 
 * @author taubertj
 */
public class Main extends JApplet {

	// toggle huge debug messages
	static final boolean DEBUG = false;

	private static final long serialVersionUID = 1L;

	/**
	 * Must be shutdown manually (see below)
	 */
	private final ExecutorService exec = Executors.newSingleThreadExecutor();

	// handle all lite actions
	private LiteActionListener listener;

	// search box
	private MenuGraphSearchBox searchBox;

	// loading progress indicator
	private final JLabel statusLabel = new JLabel("Loading...");

	// this is the graph display
	private LiteViewer viewer;

	/**
	 * Configure with optional parameters
	 */
	void configure() {
		viewer.setShowNodeLabels(getParameter("nodes.labels") != null
				&& Boolean.parseBoolean(getParameter("nodes.labels")));

		viewer.setShowEdgeLabels(getParameter("edges.labels") != null
				&& Boolean.parseBoolean(getParameter("edges.labels")));

		viewer.setAntiAliased(getParameter("antialiased") != null
				&& Boolean.parseBoolean(getParameter("antialiased")));

		if (getParameter("loadappearance") != null
				&& Boolean.parseBoolean(getParameter("loadappearance"))) {
			AppearanceSynchronizer.loadAppearance(listener, viewer);
		}

		((LiteMenuBar) getJMenuBar()).updateMenuBar();
	}

	/**
	 * Applet cleanup
	 */
	@Override
	public void destroy() {
		exec.shutdownNow();
		// Explicitly shutdown core EXECUTORS
		if (AbstractAttribute.COMPRESSOR != null) {
			AbstractAttribute.COMPRESSOR.shutdownNow();
			AbstractAttribute.COMPRESSOR = null;
		}
	}

	/**
	 * @return the listener
	 */
	public LiteActionListener getListener() {
		return listener;
	}

	@Override
	public String[][] getParameterInfo() {
		String[][] info = {
				// Parameter Name, Kind of Value, Description
				{ "ondex.dir", "URL", "might use relative path 'data'" },
				{ "ovtk.dir", "URL", "might use relative path 'config'" },
				{ "layout", "String",
						"default: 'net.sourceforge.ondex.ovtk2.layout.GEMLayout'" },
				{
						"filename",
						"URL",
						"path to OXL file, has to be reachable from applet, relative to code base, can be empty" },
				{
						"xgmml",
						"URL",
						"path to XGMML file, has to be reachable from applet, relative to code base, can be empty" },
				{
						"webservice",
						"URL",
						"URL of webservice, e.g. http://rpc274.cs.man.ac.uk:8080/ondex/services/ondex-graph?wsdl" },
				{
						"graphname",
						"String",
						"name of graph, see webservice, if multiple graphs have same name, selection is arbitrary" },
				{ "nodes.labels", "boolean", "true/false show node labels" },
				{ "edges.labels", "boolean", "true/false show edge labels" },
				{ "antialiased", "boolean",
						"true/false use antialiased painting" },
				{ "loadappearance", "boolean",
						"load appearance attributes from graph" }, };
		return info;
	}

	/**
	 * @return the searchBox
	 */
	public MenuGraphSearchBox getSearchBox() {
		return searchBox;
	}

	/**
	 * @return the statusLabel
	 */
	public JLabel getStatusLabel() {
		return statusLabel;
	}

	/**
	 * @return the viewer
	 */
	public LiteViewer getViewer() {
		return viewer;
	}

	@Override
	public void init() {
		setLayout(new java.awt.GridLayout(1, 1));
		JPanel panel = new JPanel();
		panel.setBackground(Color.WHITE);
		panel.add(statusLabel);
		add(panel);

		this.listener = new LiteActionListener(this);

		// fix according to
		// http://kr.forums.oracle.com/forums/thread.jspa?threadID=1997861
		new ParserDelegator();
		exec.submit(new InitGraphWorker(this));

		OVTK2Desktop.setDesktopResources(new LiteDesktopResources(this));
	}

	/**
	 * @param searchBox
	 *            the searchBox to set
	 */
	public void setSearchBox(MenuGraphSearchBox searchBox) {
		this.searchBox = searchBox;
	}

	/**
	 * @param viewer
	 *            the viewer to set
	 */
	public void setViewer(LiteViewer viewer) {
		this.viewer = viewer;
	}
}
