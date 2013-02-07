package net.sourceforge.ondex.ovtk2.util.chemical;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import net.sourceforge.ondex.parser.chemblactivity.Parser;
import net.sourceforge.ondex.parser.chemblactivity.Parser.EXMODE;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Present the user a GUI for filtering on certian XML document values
 * 
 * @author taubertj
 * 
 */
public class SimilarityDocumentFilter extends JPanel implements ActionListener {

	/**
	 * generated
	 */
	private static final long serialVersionUID = 811435531256115678L;

	Map<String, Document> docs;

	Map<String, Set<Element>> filtered;

	EXMODE mode;

	JLabel info;

	JTextField numberField;

	public SimilarityDocumentFilter(Map<String, Document> docs, EXMODE mode) {
		super();

		this.docs = docs;
		this.filtered = new HashMap<String, Set<Element>>();
		this.mode = mode;

		initGUI();

	}

	/**
	 * Some debug output of XML
	 * 
	 * @param document
	 */
	private void debugXML(Document document) throws Exception {
		TransformerFactory tranFactory = TransformerFactory.newInstance();
		Transformer aTransformer = tranFactory.newTransformer();
		Source src = new DOMSource(document);
		Result dest = new StreamResult(System.out);
		aTransformer.transform(src, dest);
	}

	/**
	 * Transforms internal filter results into document based structure
	 * 
	 * @return
	 * @throws Exception
	 */
	public Map<String, Document> getFiltered() throws Exception {

		// prepare for assembling all elements back into a document
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();

		Map<String, Document> results = new HashMap<String, Document>();
		for (String key : filtered.keySet()) {
			Set<Element> elements = filtered.get(key);
			if (elements.size() > 0) {
				Document document = builder.newDocument();
				Element root = (Element) document.createElement("list");
				document.appendChild(root);
				for (Element e : elements) {
					Node n = document.importNode(e, true);
					root.appendChild(n);
				}
				//debugXML(document);
				results.put(key, document);
			}
		}
		return results;
	}

	private void initGUI() {

		// simple layout of page boxes
		BoxLayout layout = new BoxLayout(this, BoxLayout.PAGE_AXIS);
		this.setLayout(layout);

		int number = 0;
		for (String key : docs.keySet()) {
			Document doc = docs.get(key);

			// get matching compound elements
			Set<Element> set = extractElements(doc);
			number += set.size();
			for (Element eElement : set) {

				// if all matches add to map
				if (!filtered.containsKey(key))
					filtered.put(key, new HashSet<Element>());
				filtered.get(key).add(eElement);
			}
		}
		this.add(new JLabel("Matching documents: " + number));

		// panel for providing a matching value
		JPanel valuePanel = new JPanel(new BorderLayout());
		this.add(valuePanel);
		valuePanel.add(new JLabel("Enter cut-off (>0.9 and <1.0):"), BorderLayout.WEST);
		numberField = new JTextField(20);
		numberField.addActionListener(this);
		valuePanel.add(numberField, BorderLayout.EAST);

		// this will be the result line
		info = new JLabel("Matching documents after filter: " + number);
		this.add(info);
	}

	/**
	 * Extract all bioactivity elements from Document
	 * 
	 * @param doc
	 * @return
	 */
	private Set<Element> extractElements(Document doc) {

		// get all bioactivity elements
		Set<Element> elements = new HashSet<Element>();
		NodeList nList = doc.getElementsByTagName("compound");

		// iterate over all activities
		for (int temp = 0; temp < nList.getLength(); temp++) {

			Node nNode = nList.item(temp);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {

				Element eElement = (Element) nNode;
				elements.add(eElement);
			}
		}

		return elements;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		filtered.clear();

		for (String key : docs.keySet()) {
			Document doc = docs.get(key);

			// get matching activity elements
			Set<Element> set = extractElements(doc);

			for (Element eElement : set) {

				if (numberField.getText().trim().length() == 0) {
					// if all matches add to map
					if (!filtered.containsKey(key))
						filtered.put(key, new HashSet<Element>());
					filtered.get(key).add(eElement);
				} else {

					// parse value
					String value = Parser.getTagValue("similarity", eElement);
					if (value != null) {
						try {
							double v = Double.parseDouble(value);
							double n = Double
									.parseDouble(numberField.getText());

							// filter numerical value here
							if (v > n) {
								// if all matches add to map
								if (!filtered.containsKey(key))
									filtered.put(key, new HashSet<Element>());
								filtered.get(key).add(eElement);
							}

						} catch (NumberFormatException nfe) {

						}
					}
				}
			}
		}

		// calculate new number after filter
		int number = 0;
		for (String key : filtered.keySet()) {
			number += filtered.get(key).size();
		}
		info.setText("Matching documents after filter: " + number);
		info.revalidate();
	}

}
