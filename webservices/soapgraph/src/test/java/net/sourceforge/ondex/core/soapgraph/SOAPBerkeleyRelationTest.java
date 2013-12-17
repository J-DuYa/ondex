package net.sourceforge.ondex.core.soapgraph;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.ondex.core.ONDEXGraph;
import net.sourceforge.ondex.core.test.AbstractRelationTest;

import org.junit.After;

/**
 * 
 * @author hindlem
 * 
 */
public class SOAPBerkeleyRelationTest extends AbstractRelationTest implements
		ActionListener {

	SOAPONDEXGraph graph;

	List<ActionEvent> list;

	@Override
	public void commit() {
		// do nothing
	}

	@After
	public void tearDown() throws Exception {
		graph.delete();
		assertTrue(list.isEmpty());
	}

	@Override
	public ONDEXGraph initialize(String name) throws Exception {

		try {
			list = new ArrayList<ActionEvent>();
			graph = new SOAPONDEXGraph(
					new URL(
							"http://rpc466.cs.man.ac.uk:8080/ondex/services/ONDEXapiWS?wsdl"),
					name, true);
			graph.addActionListener(this);
			return graph;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		list.add(e);
	}
}
