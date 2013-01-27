package net.sourceforge.ondex.ovtk2lite;

import java.awt.BorderLayout;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import javax.jnlp.BasicService;
import javax.jnlp.ServiceManager;
import javax.jnlp.UnavailableServiceException;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import net.sourceforge.ondex.ONDEXPluginArguments;
import net.sourceforge.ondex.core.ONDEXGraph;
import net.sourceforge.ondex.core.memory.MemoryONDEXGraph;
import net.sourceforge.ondex.ovtk2.graph.ONDEXJUNGGraph;
import net.sourceforge.ondex.ovtk2.io.OXLImport;
import net.sourceforge.ondex.ovtk2.io.WebserviceImport;
import net.sourceforge.ondex.ovtk2.ui.toolbars.MenuGraphSearchBox;
import net.sourceforge.ondex.ovtk2lite.search.SearchBoxAction;
import net.sourceforge.ondex.webservice.client.WSGraph;
import net.sourceforge.ondex.webservice.client.WebserviceException_Exception;

/**
 * Wrap loading of data files into worker thread.
 * 
 * @author taubertj
 * 
 */
public class InitGraphWorker extends SwingWorker<Boolean, Void> {

	private final Main main;

	public InitGraphWorker(Main main) {
		this.main = main;
	}

	@Override
	protected Boolean doInBackground() {

		// special case if loading from webservice
		URL url = null;
		WSGraph wsgraph = null;
		if (main.getParameter("webservice") != null) {
			// get URL from parameters
			try {
				url = new URL(main.getParameter("webservice"));
			} catch (MalformedURLException e) {
				JOptionPane.showMessageDialog(main, e.getMessage(),
						"Error while connecting to webservice.",
						JOptionPane.ERROR_MESSAGE);
				return false;
			}
			if (url != null) {
				// search for specified name
				try {
					for (WSGraph g : WebserviceImport.getGraphs(url)) {
						if (g.getName()
								.getValue()
								.equalsIgnoreCase(
										main.getParameter("graphname"))) {
							wsgraph = g;
							break;
						}
					}
				} catch (WebserviceException_Exception e) {
					JOptionPane.showMessageDialog(main, e.getMessage(),
							"Error while connecting to webservice.",
							JOptionPane.ERROR_MESSAGE);
					return false;
				}
			}
		}

		// sanity checks
		if (main.getParameter("ondex.dir") == null
				|| main.getParameter("ovtk.dir") == null) {
			JOptionPane.showMessageDialog(main,
					"ondex.dir or ovtk.dir not set!");
			return false;
		}

		main.getStatusLabel().setText("Initialising config...");

		// in case applet is started via JNLP, get codebase
		URL codeBase = null;
		try {
			BasicService bs = (BasicService) ServiceManager
					.lookup("javax.jnlp.BasicService");
			codeBase = bs.getCodeBase();
		} catch (UnavailableServiceException e1) {
			e1.printStackTrace();
		}

		String ondexDir = main.getParameter("ondex.dir");
		String ovtkDir = main.getParameter("ovtk.dir");
		// in case of relative paths
		if (codeBase != null && !ondexDir.contains("://")
				&& !ovtkDir.contains("://")) {
			ondexDir = codeBase.toExternalForm() + "/" + ondexDir;
			ovtkDir = codeBase.toExternalForm() + "/" + ovtkDir;
		}

		// set data directories
		net.sourceforge.ondex.config.Config.ondexDir = ondexDir;
		net.sourceforge.ondex.ovtk2.config.Config.ovtkDir = ovtkDir;

		// important to load configuration from file!
		try {
			net.sourceforge.ondex.config.Config.loadConfig();
			net.sourceforge.ondex.ovtk2.config.Config.loadConfig(true);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(main, e.getMessage(),
					"Error while initialising config.",
					JOptionPane.ERROR_MESSAGE);
			return false;
		}

		// add menu bar to applet
		LiteMenuBar menuBar = new LiteMenuBar(main);
		main.setJMenuBar(menuBar);

		main.getStatusLabel().setText("Starting import...");

		// in-memory graph
		ONDEXGraph aog = new MemoryONDEXGraph("ONDEX Graph");

		String filename = main.getParameter("filename");
		// in case of relative paths
		if (codeBase != null && filename != null && !filename.contains("://")) {
			filename = codeBase.toExternalForm() + "/" + filename;
		}

		String xgmml = main.getParameter("xgmml");
		// in case of relative paths
		if (codeBase != null && xgmml != null && !xgmml.contains("://")) {
			xgmml = codeBase.toExternalForm() + "/" + xgmml;
		}

		if (filename != null) {
			// load from file
			try {
				OXLImport imp = new OXLImport(aog, filename, true);
				imp.start();
			} catch (Exception e) {
				JOptionPane.showMessageDialog(main, e.getMessage(),
						"Error while loading from filename.",
						JOptionPane.ERROR_MESSAGE);
				return false;
			}
		} else if (url != null && wsgraph != null) {
			// load from webservice
			try {
				new WebserviceImport(aog, url, wsgraph);
			} catch (Exception e) {
				JOptionPane.showMessageDialog(main, e.getMessage(),
						"Error while loading from webservice.",
						JOptionPane.ERROR_MESSAGE);
				return false;
			}
		} else if (xgmml != null) {
			// load from xgmml
			net.sourceforge.ondex.parser.cytoscape.Parser parser = new net.sourceforge.ondex.parser.cytoscape.Parser();
			ONDEXPluginArguments args = new ONDEXPluginArguments(
					parser.getArgumentDefinitions());
			try {
				parser.setArguments(args);
				parser.setONDEXGraph(aog);
				parser.start(xgmml);
			} catch (Exception e) {
				JOptionPane.showMessageDialog(main, e.getMessage(),
						"Error while loading from filename.",
						JOptionPane.ERROR_MESSAGE);
				return false;
			}
		}

		if (Main.DEBUG) {
			System.out.println("----- Configuration ONDEX: -----");
			Object[] keys = net.sourceforge.ondex.config.Config.properties
					.keySet().toArray();
			Arrays.sort(keys);
			for (Object key : keys) {
				System.out.println(key
						+ " = "
						+ net.sourceforge.ondex.config.Config.properties
								.get(key));
			}
			System.out.println("----- Configuration OVTK2: -----");
			keys = net.sourceforge.ondex.ovtk2.config.Config.config.keySet()
					.toArray();
			Arrays.sort(keys);
			for (Object key : keys) {
				System.out.println(key
						+ " = "
						+ net.sourceforge.ondex.ovtk2.config.Config.config
								.get(key));
			}
		}

		main.getStatusLabel().setText("Constructing viewer...");

		// check success of loading
		if (aog.getConcepts().size() > 0)
			System.out.println("Loading of " + filename != null ? filename
					: wsgraph.getName() + " successful.");

		// wrap ONDEXGraph into JUNG
		ONDEXJUNGGraph graph = new ONDEXJUNGGraph(aog);
		if (aog.getConcepts().size() > 2000 || aog.getRelations().size() > 2000) {
			int option = JOptionPane
					.showConfirmDialog(
							main,
							"The network does contain more than 2000 nodes or edges."
									+ "\nIt is recommend to not display all, but use the search function for information extraction."
									+ "\nDo you wish to start with an empty / partial display?",
							"Network size warning", JOptionPane.YES_NO_OPTION,
							JOptionPane.WARNING_MESSAGE);
			if (option == JOptionPane.NO_OPTION)
				graph.setEverythingVisible();
		} else {
			graph.setEverythingVisible();
		}

		// wrap into viewer
		LiteViewer viewer = new LiteViewer(main, graph);

		main.getStatusLabel().setText("Configuring viewer...");

		// get parameter configuration, after GraphMouse for static layout
		main.configure();

		// setup search box
		SearchBoxAction actionListener = new SearchBoxAction(viewer);
		MenuGraphSearchBox searchBox = new MenuGraphSearchBox();
		searchBox.updateRestrictions(viewer);
		searchBox.addActionListener(actionListener);
		main.setSearchBox(searchBox);

		return true;
	}

	public void done() {
		try {
			if (get()) {

				// clean loading label
				main.getContentPane().removeAll();

				// minimal size of applet
				main.setSize(main.getViewer().getPreferredSize());

				// setup layout for applet
				main.setLayout(new BorderLayout());

				// add to applet
				main.add(main.getViewer(), BorderLayout.CENTER);

				// add search box
				main.add(main.getSearchBox(), BorderLayout.SOUTH);
				main.getSearchBox().setVisible(false);

				// re-validate components
				main.validate();

				// center graph in applet
				main.getViewer().center();

				// make sure the applet gets painted properly
				main.invalidate();
				main.validate();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}
}