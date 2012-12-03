package net.sourceforge.ondex.ovtk2lite;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.xml.bind.JAXBException;

import net.sourceforge.ondex.core.ONDEXConcept;
import net.sourceforge.ondex.core.ONDEXGraph;
import net.sourceforge.ondex.core.ONDEXRelation;
import net.sourceforge.ondex.ovtk2.config.Config;
import net.sourceforge.ondex.ovtk2.config.OVTK2PluginLoader;
import net.sourceforge.ondex.ovtk2.graph.ONDEXEdgeColors.EdgeColorSelection;
import net.sourceforge.ondex.ovtk2.graph.ONDEXNodeDrawPaint.NodeDrawPaintSelection;
import net.sourceforge.ondex.ovtk2.graph.ONDEXNodeFillPaint.NodeFillPaintSelection;
import net.sourceforge.ondex.ovtk2.graph.ONDEXNodeShapes.NodeShapeSelection;
import net.sourceforge.ondex.ovtk2.io.CytoscapeImporter;
import net.sourceforge.ondex.ovtk2.io.NWBImport;
import net.sourceforge.ondex.ovtk2.io.OVTK2IO;
import net.sourceforge.ondex.ovtk2.io.OXLExport;
import net.sourceforge.ondex.ovtk2.io.OXLImport;
import net.sourceforge.ondex.ovtk2.io.PajekImport;
import net.sourceforge.ondex.ovtk2.io.WizardImport;
import net.sourceforge.ondex.ovtk2.io.importwizard.ConfigTool;
import net.sourceforge.ondex.ovtk2.io.importwizard.ImportWizard;
import net.sourceforge.ondex.ovtk2.ui.OVTK2PropertiesAggregator;
import net.sourceforge.ondex.ovtk2.ui.dialog.DialogExport;
import net.sourceforge.ondex.ovtk2.ui.menu.actions.AppearanceMenuAction;
import net.sourceforge.ondex.ovtk2.util.CustomFileFilter;
import net.sourceforge.ondex.ovtk2.util.ImageWriterUtil;
import net.sourceforge.ondex.ovtk2.util.LayoutNeighbours;
import net.sourceforge.ondex.ovtk2.util.OVTKProgressMonitor;
import net.sourceforge.ondex.tools.threading.monitoring.IndeterminateProcessAdapter;

public class LiteMenuBar extends JMenuBar {

	/**
	 * Sorts array alphabetically via lookup values in map
	 * 
	 * @author canevetc
	 * 
	 */
	class MapSorter implements Comparator<String> {

		private Map<String, String> map;

		public MapSorter(Map<String, String> map) {
			this.map = map;
		}

		@Override
		public int compare(String o1, String o2) {
			return map.get(o1).compareTo(map.get(o2));
		}

	}

	/**
	 * generated
	 */
	private static final long serialVersionUID = 584135978539168304L;

	// wrapped OVTK2PropertiesAggregator
	private Main main;

	/**
	 * Initialises the menu bar for the applet
	 * 
	 */
	public LiteMenuBar(Main main) {
		super();

		this.main = main;

		this.add(initFile());

		this.add(initView());

		this.add(initAppearance());

		this.add(initTools());
	}

	/**
	 * Imports a given file, choosing importer by extension
	 * 
	 * @param file
	 * @param ext2io
	 */
	private void importFile(final File file, final Map<String, OVTK2IO> ext2io) {
		final String ext = file.getName()
				.substring(file.getName().lastIndexOf(".") + 1).toLowerCase();

		// get existing graph
		final ONDEXGraph aog = main.getViewer().getONDEXJUNGGraph();
		final Set<ONDEXConcept> oldConcepts = new HashSet<ONDEXConcept>(main
				.getViewer().getONDEXJUNGGraph().getConcepts());
		final Set<ONDEXRelation> oldRelations = new HashSet<ONDEXRelation>(main
				.getViewer().getONDEXJUNGGraph().getRelations());

		// check extension
		if (ext2io.containsKey(ext)) {
			IndeterminateProcessAdapter p = new IndeterminateProcessAdapter() {
				public void task() {

					// new generic import
					OVTK2IO io = ext2io.get(ext);
					io.setGraph(aog);

					try {
						// start import process
						io.start(file);

						updateViewer(oldConcepts, oldRelations);
					} catch (Exception e) {
						e.printStackTrace();
						JOptionPane.showMessageDialog(main, e.getMessage(),
								"Error while loading file",
								JOptionPane.ERROR_MESSAGE);
					}
				}
			};
			// start processing and monitoring
			p.start();
			OVTKProgressMonitor.start(main.getListener()
					.getContentsDisplayFrame(), "Importing " + file, p);

		}

		// extension not found
		else {
			JOptionPane.showMessageDialog(main,
					"No importer for this file extension available: " + ext,
					"No importer matching", JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Setup the Appearance menu
	 * 
	 * @param viewer
	 * @return
	 */
	private JMenu initAppearance() {
		// appearance menu
		JMenu appearance = new JMenu("Appearance");

		// layout sub-menu
		JMenu layout = new JMenu(Config.language.getProperty("Menu.Layout"));
		appearance.add(layout);
		populateLayoutMenu(layout);

		// labels sub-menu
		JMenu labels = new JMenu(
				Config.language.getProperty("Menu.Appearance.Labels"));
		appearance.add(labels);

		JCheckBoxMenuItem nodelabels = new JCheckBoxMenuItem(
				Config.language.getProperty("Menu.Appearance.ConceptLabels"));
		nodelabels.setActionCommand("nodelabels");
		nodelabels.addActionListener(main.getListener());
		labels.add(nodelabels);

		JCheckBoxMenuItem edgelabels = new JCheckBoxMenuItem(
				Config.language.getProperty("Menu.Appearance.RelationLabels"));
		edgelabels.setActionCommand("edgelabels");
		edgelabels.addActionListener(main.getListener());
		labels.add(edgelabels);

		JCheckBoxMenuItem bothlabels = new JCheckBoxMenuItem(
				Config.language.getProperty("Menu.Appearance.BothLabels"));
		bothlabels.setActionCommand("bothlabels");
		bothlabels.addActionListener(main.getListener());
		labels.add(bothlabels);

		// sub-menu for loading visual attributes on the graph
		JMenu load = new JMenu(
				Config.language.getProperty("Menu.Appearance.Load"));
		appearance.add(load);

		JMenuItem loadAll = new JMenuItem(
				Config.language.getProperty("Menu.Appearance.LoadAll"));
		loadAll.setActionCommand(AppearanceMenuAction.LOADAPPEARANCE);
		loadAll.addActionListener(main.getListener());
		load.add(loadAll);

		JCheckBoxMenuItem loadNodeColor = new JCheckBoxMenuItem(
				Config.language
						.getProperty("Menu.Appearance.LoadConceptColours"));
		loadNodeColor.setActionCommand(AppearanceMenuAction.NODECOLOR);
		loadNodeColor.addActionListener(main.getListener());
		load.add(loadNodeColor);

		JCheckBoxMenuItem loadNodeShape = new JCheckBoxMenuItem(
				Config.language
						.getProperty("Menu.Appearance.LoadConceptShapes"));
		loadNodeShape.setActionCommand(AppearanceMenuAction.NODESHAPE);
		loadNodeShape.addActionListener(main.getListener());
		load.add(loadNodeShape);

		JCheckBoxMenuItem loadEdgeColor = new JCheckBoxMenuItem(
				Config.language
						.getProperty("Menu.Appearance.LoadRelationColours"));
		loadEdgeColor.setActionCommand(AppearanceMenuAction.EDGECOLOR);
		loadEdgeColor.addActionListener(main.getListener());
		load.add(loadEdgeColor);

		JCheckBoxMenuItem loadEdgeSize = new JCheckBoxMenuItem(
				Config.language
						.getProperty("Menu.Appearance.LoadRelationWidths"));
		loadEdgeSize.setActionCommand(AppearanceMenuAction.EDGESIZE);
		loadEdgeSize.addActionListener(main.getListener());
		load.add(loadEdgeSize);

		JMenuItem save = new JMenuItem(
				Config.language.getProperty("Menu.Appearance.Save"));
		save.setActionCommand(AppearanceMenuAction.SAVEAPPEARANCE);
		save.addActionListener(main.getListener());
		appearance.add(save);

		// toggle anti-aliased painting
		JCheckBoxMenuItem antialiased = new JCheckBoxMenuItem(
				Config.language.getProperty("Menu.Appearance.SmoothRelations"));
		antialiased.setActionCommand("antialiased");
		antialiased.addActionListener(main.getListener());
		appearance.add(antialiased);

		JCheckBoxMenuItem showMouseOver = new JCheckBoxMenuItem(
				Config.language.getProperty("Menu.Appearance.ShowMouseOver"));
		showMouseOver.setActionCommand(AppearanceMenuAction.SHOWMOUSEOVER);
		showMouseOver.addActionListener(main.getListener());
		appearance.add(showMouseOver);

		// make everything visible
		JMenuItem showAll = new JMenuItem("Show all");
		showAll.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				main.getViewer().getONDEXJUNGGraph().setEverythingVisible();
				main.getViewer().getVisualizationViewer().repaint();
			}
		});
		appearance.add(showAll);

		// hide everything
		JMenuItem hideAll = new JMenuItem("Hide all");
		hideAll.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// hide relations
				main.getViewer()
						.getONDEXJUNGGraph()
						.setVisibility(
								main.getViewer().getONDEXJUNGGraph()
										.getRelations(), false);
				// hide concepts
				main.getViewer()
						.getONDEXJUNGGraph()
						.setVisibility(
								main.getViewer().getONDEXJUNGGraph()
										.getConcepts(), false);

				main.getViewer().getVisualizationViewer().repaint();
			}
		});
		appearance.add(hideAll);

		// relayout graph
		JMenuItem relayout = new JMenuItem("Relayout");
		relayout.setActionCommand("relayout");
		relayout.addActionListener(main.getListener());
		appearance.add(relayout);

		return appearance;
	}

	/**
	 * Setup the File menu
	 * 
	 * @param viewer
	 * @return
	 */
	private JMenu initFile() {
		// file menu
		JMenu file = new JMenu("File");

		// open graph
		JMenuItem open = new JMenuItem("Open");
		open.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// ask user what file to open
				File file = showOpenDialog(new String[] { "oxl", "gz", "xml" });
				if (file != null)
					openFile(file);
			}
		});
		file.add(open);

		// save graph
		JMenuItem save = new JMenuItem("Save");
		save.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// ask user which file to save to
				File file = showSaveDialog(new String[] { "oxl", "xml", "gz" });
				if (file != null)
					saveFile(file);
			}
		});
		file.add(save);

		JMenuItem m_import = new JMenuItem("Import");
		m_import.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent ae) {

				// get extensions of importer plugins
				Map<String, OVTK2IO> ext2io = new Hashtable<String, OVTK2IO>();

				OVTK2IO io = new CytoscapeImporter(null);
				ext2io.put(io.getExt(), io);

				io = new NWBImport(true);
				ext2io.put(io.getExt(), io);

				io = new PajekImport();
				ext2io.put(io.getExt(), io);

				// show file open dialog
				File file = showOpenDialog(ext2io.keySet().toArray(
						new String[0]));
				if (file != null) {
					importFile(file, ext2io);
				}
			}
		});
		file.add(m_import);

		// export graph
		JMenuItem export = new JMenuItem("Export");
		export.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				File dir = (Config.lastSavedFile == null) ? new File(System
						.getProperty("user.dir")) : new File(
						Config.lastSavedFile);
				DialogExport chooser = new DialogExport(dir);
				// chooser.addFormat("graphml");

				int i = chooser.showSaveDialog(main);
				if (i == JFileChooser.APPROVE_OPTION) {
					File file = chooser.getFile();
					Config.lastSavedFile = file.getAbsolutePath();

					ImageWriterUtil<ONDEXConcept, ONDEXRelation> iw = new ImageWriterUtil<ONDEXConcept, ONDEXRelation>(
							main.getViewer().getVisualizationViewer());

					iw.writeImage(file, chooser.getSelectedFormat(),
							chooser.getScaleFactor());
				}
			}
		});
		file.add(export);

		// use config.xml to enable/disable import wizard
		if (Boolean.parseBoolean(Config.config
				.getProperty("ImportWizard.Enable"))) {

			JMenuItem impo = new JMenuItem(
					Config.language.getProperty("Menu.File.ImportWizard"));
			impo.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					// show IW
					final JFrame frame = new JFrame("Import Wizard");

					final ImportWizard iw = new ImportWizard(null, ConfigTool
							.loadFromFile(Config.ovtkDir + "/iw_config.xml"),
							frame);
					frame.pack();
					frame.setVisible(true);

					// busy waiting for ImportWizard to close
					Thread thread = new Thread() {
						public void run() {
							synchronized (this) {
								while (frame.isVisible()) {
									try {
										Thread.sleep(100);
									} catch (InterruptedException e) {
										// ignore
									}
								}
								Vector<Object> r = iw.getReturnContent();
								if (r.size() > 0) {

									// get current viewer
									main.getViewer().getONDEXJUNGGraph()
											.updateLastState();

									JFrame importFrame = new JFrame(
											"Value Mapping");

									// start loading of data
									new WizardImport(main.getViewer()
											.getONDEXJUNGGraph(), r,
											importFrame);
								}
							}
						}
					};
					thread.start();
				}
			});
			file.add(impo);
		}

		return file;
	}

	/**
	 * Populates the tools menu.
	 * 
	 * @return JMenu "Tools"
	 */
	private JMenu initTools() {
		JMenu tools = new JMenu(Config.language.getProperty("Menu.Tools"));

		// annotator sub-menu
		JMenu anno = new JMenu(Config.language.getProperty("Menu.Annotator"));
		tools.add(anno);
		populateAnnoMenu(anno);

		// selecting concepts and relations sub-menu
		JMenu selecting = new JMenu(
				Config.language.getProperty("Menu.Selecting"));
		tools.add(selecting);
		populateSelectingMenu(selecting);

		// merge concepts function
		JMenuItem merge = new JMenuItem(
				Config.language.getProperty("Menu.Edit.Merge"));
		merge.setActionCommand("merge");
		merge.addActionListener(main.getListener());
		tools.add(merge);

		// delete hidden items
		JMenuItem sync = new JMenuItem(
				Config.language.getProperty("Menu.Edit.DeleteHidden"));
		sync.setActionCommand("sync");
		sync.addActionListener(main.getListener());
		tools.add(sync);

		return tools;
	}

	/**
	 * Setup the View menu
	 * 
	 * @param viewer
	 * @return
	 */
	private JMenu initView() {
		// tools menu
		JMenu view = new JMenu("View");

		// shows the item info panel
		JCheckBoxMenuItem contentsdisplay = new JCheckBoxMenuItem(
				Config.language.getProperty("Menu.View.ItemInfo"));
		contentsdisplay.setActionCommand("contentsdisplay");
		contentsdisplay.addActionListener(main.getListener());
		view.add(contentsdisplay);

		// toggles search bar visibility
		JCheckBoxMenuItem showSearch = new JCheckBoxMenuItem("Show search bar");
		showSearch.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				main.getSearchBox()
						.setVisible(!main.getSearchBox().isVisible());
				main.invalidate();
			}
		});
		view.add(showSearch);

		return view;
	}

	/**
	 * Opens an ondex graph from the specified file and loads it in a new viewer
	 * 
	 * @param file
	 *            a valid file to open
	 */
	public void openFile(File file) {
		if (file.exists() && file.canRead()) {

			// notify user with loading message
			final JDialog dialog = new JDialog(main.getListener()
					.getContentsDisplayFrame(), "Loading...", true);
			BoxLayout layout = new BoxLayout(dialog.getContentPane(),
					BoxLayout.PAGE_AXIS);
			dialog.getContentPane().setLayout(layout);
			dialog.getContentPane().add(
					new JLabel("Loading file, please wait..."));

			// import knows about its progress
			final OXLImport imp = new OXLImport(main.getViewer()
					.getONDEXJUNGGraph(), file);
			final Set<ONDEXConcept> oldConcepts = new HashSet<ONDEXConcept>(
					main.getViewer().getONDEXJUNGGraph().getConcepts());
			final Set<ONDEXRelation> oldRelations = new HashSet<ONDEXRelation>(
					main.getViewer().getONDEXJUNGGraph().getRelations());

			// wrap into a process
			Thread t = new Thread() {
				public void run() {
					try {
						// start OXL import process
						imp.start();

						updateViewer(oldConcepts, oldRelations);

						dialog.setVisible(false);
					} catch (Exception e) {
						JOptionPane.showMessageDialog(main, e.getMessage(),
								"Error while loading file",
								JOptionPane.ERROR_MESSAGE);
					}
				}
			};
			// start processing and monitoring
			t.start();

			// add cancel button
			JButton cancel = new JButton("Cancel");
			cancel.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					imp.setCancelled(true);
				}
			});
			dialog.getContentPane().add(cancel);
			dialog.pack();
			dialog.setVisible(true);
			dialog.toFront();
		} else {
			JOptionPane.showMessageDialog(main,
					Config.language.getProperty("Dialog.File.NotFound"),
					Config.language.getProperty("Dialog.File.NotFoundTitle"),
					JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Populates the annotation menu.
	 * 
	 * @param anno
	 *            JMenu "Annotator submenu"
	 */
	private void populateAnnoMenu(JMenu anno) {

		Set<String> exceptions = new HashSet<String>();
		exceptions
				.add("net.sourceforge.ondex.ovtk2.annotator.scalecolorconcept.ScaleColorConceptAnnotator");
		exceptions
				.add("net.sourceforge.ondex.ovtk2.annotator.scaleconcept.ScaleConceptAnnotator");
		exceptions
				.add("net.sourceforge.ondex.ovtk2.annotator.colorcategory.ColorCategoryAnnotator");
		exceptions
				.add("net.sourceforge.ondex.ovtk2.annotator.scalecolorrelation.ScaleColorRelationAnnotator");
		exceptions
				.add("net.sourceforge.ondex.ovtk2.annotator.shapeconcept.ShapeConceptAnnotator");

		// add annotators from config.xml file
		ArrayList<String> entries = new ArrayList<String>();
		Enumeration<?> enu = Config.config.propertyNames();
		while (enu.hasMoreElements()) {
			String name = (String) enu.nextElement();
			if (name.startsWith("Menu.Annotator.")) {
				entries.add(name);
			}
		}

		// order annotators by name
		String[] ordered = entries.toArray(new String[entries.size()]);

		HashMap<String, String> realNameToDisplayName = new HashMap<String, String>();
		for (String name : ordered) {
			String display = Config.language.getProperty("Name." + name);
			realNameToDisplayName.put(name, display);
		}

		Arrays.sort(ordered, new MapSorter(realNameToDisplayName));

		JMenu more = new JMenu(
				Config.language.getProperty("Menu.Annotator.More"));

		for (String name : ordered) {
			String clazz = Config.config.getProperty(name);
			try {
				if (!OVTK2PluginLoader.getInstance().getAnnotatorClassNames()
						.contains(clazz)) {
					if (!exceptions.contains(clazz))
						continue;
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace(); // TODO: log?
			}

			String display = realNameToDisplayName.get(name);
			JMenuItem item = new JMenuItem(display);
			item.setActionCommand(name);
			item.addActionListener(main.getListener());
			if (!exceptions.contains(clazz)) {
				more.add(item);
			} else {
				anno.add(item);
			}
		}

		anno.add(more);
	}

	/**
	 * Populates the layout menu.
	 * 
	 * @param layout
	 *            JMenu "Layouts submenu"
	 */
	private void populateLayoutMenu(JMenu layout) {

		Set<String> exceptions = new HashSet<String>();
		exceptions
				.add("net.sourceforge.ondex.ovtk2.layout.ConceptClassCircleLayout");
		exceptions.add("net.sourceforge.ondex.ovtk2.layout.StaticLayout");
		exceptions.add("net.sourceforge.ondex.ovtk2.layout.GEMLayout");

		// add layouter from config.xml file
		ArrayList<String> entries = new ArrayList<String>();
		Enumeration<?> enu = Config.config.propertyNames();
		while (enu.hasMoreElements()) {
			String name = (String) enu.nextElement();
			if (name.startsWith("Menu.Layout.")) {
				entries.add(name);
			}
		}

		// order layouts by name
		String[] ordered = entries.toArray(new String[entries.size()]);

		HashMap<String, String> realNameToDisplayName = new HashMap<String, String>();
		for (String name : ordered) {
			String display = Config.language.getProperty("Name." + name);
			realNameToDisplayName.put(name, display);
		}

		Arrays.sort(ordered, new MapSorter(realNameToDisplayName));

		JMenu more = new JMenu(Config.language.getProperty("Menu.Layout.More"));

		for (String name : ordered) {
			String clazz = Config.config.getProperty(name);
			try {
				if (!OVTK2PluginLoader.getInstance().getLayoutClassNames()
						.contains(clazz)) {
					// exception case for included layouts
					if (!exceptions.contains(clazz))
						continue;
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace(); // TODO: log?
			}

			String display = Config.language.getProperty("Name." + name);
			JMenuItem item = new JMenuItem(display);
			item.setActionCommand(name);
			item.addActionListener(main.getListener());
			if (!clazz
					.equals("net.sourceforge.ondex.ovtk2.layout.ConceptClassCircleLayout")
					&& !clazz
							.equals("net.sourceforge.ondex.ovtk2.layout.GEMLayout")) {
				more.add(item);
			} else {
				layout.add(item);
			}
		}

		layout.add(more);

		JCheckBoxMenuItem options = new JCheckBoxMenuItem(
				Config.language.getProperty("Menu.Layout.Options"));
		options.setActionCommand("options");
		options.addActionListener(main.getListener());
		layout.add(options);

	}

	/**
	 * Populates the selecting menu.
	 * 
	 * @param selecting
	 *            JMenu "Selecting Concepts/Relations submenu"
	 */
	private void populateSelectingMenu(JMenu selecting) {

		JMenuItem allnodes = new JMenuItem(
				Config.language.getProperty("Menu.Selecting.SelectAllNodes"));
		allnodes.setActionCommand("allnodes");
		allnodes.addActionListener(main.getListener());
		selecting.add(allnodes);

		JMenuItem alledges = new JMenuItem(
				Config.language.getProperty("Menu.Selecting.SelectAllEdges"));
		alledges.setActionCommand("alledges");
		alledges.addActionListener(main.getListener());
		selecting.add(alledges);

		JMenuItem inversenodes = new JMenuItem(
				Config.language
						.getProperty("Menu.Selecting.InvertSelectionNodes"));
		inversenodes.setActionCommand("inversenodes");
		inversenodes.addActionListener(main.getListener());
		selecting.add(inversenodes);

		JMenuItem inverseedges = new JMenuItem(
				Config.language
						.getProperty("Menu.Selecting.InvertSelectionEdges"));
		inverseedges.setActionCommand("inverseedges");
		inverseedges.addActionListener(main.getListener());
		selecting.add(inverseedges);
	}

	/**
	 * Saves an ondex graph to the specified file and from the given graph
	 * 
	 * @param file
	 *            a valid file to open
	 * @throws JAXBException
	 */
	public void saveFile(File file) {

		// warn user before overwriting
		if (file.exists()
				&& Boolean.parseBoolean(Config.config
						.getProperty("Overwrite.Set"))) {
			int answer = JOptionPane.showConfirmDialog(main,
					Config.language.getProperty("Dialog.Save.Warning.Text"),
					Config.language.getProperty("Dialog.Save.Warning.Title"),
					JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
			if (answer == JOptionPane.NO_OPTION)
				return;
		}

		// notify user with saving message
		final JDialog dialog = new JDialog(main.getListener()
				.getContentsDisplayFrame(), "Saving...", true);
		BoxLayout layout = new BoxLayout(dialog.getContentPane(),
				BoxLayout.PAGE_AXIS);
		dialog.getContentPane().setLayout(layout);
		dialog.getContentPane().add(new JLabel("Saving file, please wait..."));

		// export knows about its progress
		final OXLExport exp = new OXLExport(main.getViewer()
				.getONDEXJUNGGraph(), file);

		// wrap into a process
		Thread t = new Thread() {
			public void run() {
				try {
					// start OXL export process
					exp.start();
					dialog.setVisible(false);
				} catch (Exception e) {
					JOptionPane.showMessageDialog(main, e.getMessage(),
							"Error while saving file",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		};
		// start processing and monitoring
		t.start();

		// add cancel button
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				exp.setCancelled(true);
			}
		});
		dialog.getContentPane().add(cancel);
		dialog.pack();
		dialog.setVisible(true);
		dialog.toFront();
	}

	/**
	 * Shows a file open dialog with the given filter file extensions.
	 * 
	 * @param extensions
	 *            used to filter filenames
	 * @return File
	 */
	public File showOpenDialog(String[] extensions) {
		File dir = (Config.lastOpenedFile == null) ? new File(
				System.getProperty("user.dir")) : new File(
				Config.lastOpenedFile);
		JFileChooser fc = new JFileChooser(dir);
		fc.addChoosableFileFilter(new CustomFileFilter(extensions));
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);

		// in response to a button click:
		int returnVal = fc.showOpenDialog(main);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			Config.lastOpenedFile = file.getAbsolutePath();
			if (Config.lastSavedFile == null)
				Config.lastSavedFile = Config.lastOpenedFile;
			System.out.println("Opening: " + file.getName() + ".");
			return file;
		}
		System.out.println("Open command cancelled by user.");
		return null;
	}

	/**
	 * Shows a file save dialog with the given filter file extensions.
	 * 
	 * @param extensions
	 *            used to filter filenames
	 * @return File
	 */
	public File showSaveDialog(String[] extensions) {
		File dir = (Config.lastSavedFile == null) ? new File(
				System.getProperty("user.dir"))
				: new File(Config.lastSavedFile);
		JFileChooser fc = new JFileChooser(dir);
		fc.addChoosableFileFilter(new CustomFileFilter(extensions));

		// in response to a button click:
		int returnVal = fc.showSaveDialog(main);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			Config.lastSavedFile = file.getAbsolutePath();
			System.out.println("Saving: " + file.getName() + ".");
			return file;
		}
		System.out.println("Save command cancelled by user.");
		return null;
	}

	/**
	 * Updates menu bar to represent state of activeViewer.
	 * 
	 */
	public void updateMenuBar() {

		OVTK2PropertiesAggregator viewer = main.getViewer();

		for (int i = 0; i < this.getMenuCount(); i++) {
			JMenu menu = this.getMenu(i);
			LinkedList<Component> components = new LinkedList<Component>();
			components.addAll(Arrays.asList(menu.getMenuComponents()));
			while (!components.isEmpty()) {
				Component c = components.pop();
				if (c instanceof JCheckBoxMenuItem) {
					JCheckBoxMenuItem item = (JCheckBoxMenuItem) c;
					String cmd = item.getActionCommand();
					if (cmd.equals("antialiased")) {
						item.setSelected(viewer.isAntiAliased());
					} else if (cmd.equals("nodelabels")) {
						item.setSelected(viewer.isShowNodeLabels());
					} else if (cmd.equals("edgelabels")) {
						item.setSelected(viewer.isShowEdgeLabels());
					} else if (cmd.equals("bothlabels")) {
						item.setSelected(viewer.isShowEdgeLabels()
								&& viewer.isShowNodeLabels());
					} else if (cmd.equals(AppearanceMenuAction.EDGECOLOR)) {
						item.setSelected(viewer.getEdgeColors()
								.getEdgeColorSelection() == EdgeColorSelection.MANUAL);
					} else if (cmd.equals(AppearanceMenuAction.EDGESIZE)) {
						item.setSelected(viewer.getEdgeStrokes()
								.getEdgeSizeTransformer() != null);
					} else if (cmd.equals(AppearanceMenuAction.NODECOLOR)) {
						item.setSelected(viewer.getNodeColors()
								.getFillPaintSelection() == NodeFillPaintSelection.MANUAL);
					} else if (cmd.equals(AppearanceMenuAction.NODESHAPE)) {
						item.setSelected(viewer.getNodeShapes()
								.getNodeShapeSelection() == NodeShapeSelection.MANUAL);
					}
				}

				// enqueue for breadth first search
				if (c instanceof JMenu) {
					menu = (JMenu) c;
					components.addAll(Arrays.asList(menu.getMenuComponents()));
				}
			}
		}

		for (int i = 0; i < this.getMenuCount(); i++) {
			JMenu menu = this.getMenu(i);
			LinkedList<Component> components = new LinkedList<Component>();
			components.addAll(Arrays.asList(menu.getMenuComponents()));
			while (!components.isEmpty()) {
				Component c = components.pop();
				if (c instanceof JCheckBoxMenuItem) {
					JCheckBoxMenuItem item = (JCheckBoxMenuItem) c;
					String cmd = item.getActionCommand();
					if (cmd.equals("options")) {
						item.setSelected(main.getListener().isOptionsShown());
					} else if (cmd.equals("contentsdisplay")) {
						item.setSelected(main.getListener()
								.isContentsDisplayShown());
					}
				}

				// enqueue for breadth first search
				if (c instanceof JMenu) {
					menu = (JMenu) c;
					components.addAll(Arrays.asList(menu.getMenuComponents()));
				}
			}
		}
	}

	/**
	 * Update the visibility for newly added concepts and relations.
	 * 
	 * @param oldConcepts
	 * @param oldRelations
	 */
	private void updateViewer(Set<ONDEXConcept> oldConcepts,
			Set<ONDEXRelation> oldRelations) {

		// update viewer
		Set<ONDEXConcept> newConcepts = new HashSet<ONDEXConcept>(main
				.getViewer().getONDEXJUNGGraph().getConcepts());
		newConcepts.removeAll(oldConcepts);
		System.out.println("New concepts: " + newConcepts.size());
		Set<ONDEXRelation> newRelations = new HashSet<ONDEXRelation>(main
				.getViewer().getONDEXJUNGGraph().getRelations());
		newRelations.removeAll(oldRelations);
		System.out.println("New relations: " + newRelations.size());

		// set visibility
		main.getViewer().getONDEXJUNGGraph().setVisibility(newConcepts, true);
		main.getViewer().getONDEXJUNGGraph().setVisibility(newRelations, true);
		main.getViewer().getNodeColors()
				.setFillPaintSelection(NodeFillPaintSelection.CONCEPTCLASS);
		main.getViewer().getNodeDrawPaint()
				.setDrawPaintSelection(NodeDrawPaintSelection.NONE);
		main.getViewer().getNodeShapes()
				.setNodeShapeSelection(NodeShapeSelection.NONE);

		// update visual mappings
		for (ONDEXConcept c : newConcepts) {
			main.getViewer().getNodeColors().updateColor(c);
			main.getViewer().getNodeDrawPaint().updateColor(c);
			main.getViewer().getNodeLabels().updateLabel(c);
			main.getViewer().getNodeShapes().updateShape(c);
		}
		main.getViewer().getEdgeColors()
				.setEdgeColorSelection(EdgeColorSelection.RELATIONTYPE);
		for (ONDEXRelation r : newRelations) {
			main.getViewer().getEdgeColors().updateColor(r);
			main.getViewer().getEdgeLabels().updateLabel(r);
		}

		// layout nodes
		LayoutNeighbours.layoutNodes(main.getViewer().getVisualizationViewer(),
				null, newConcepts);
		main.getViewer().center();
	}
}
