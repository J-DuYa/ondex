package net.sourceforge.ondex.wsapi.result;

import net.sourceforge.ondex.core.ONDEXGraph;

/**
 * The core of the ONDEX backend that contains all Concepts and Relations as
 * well as additional data like CVs or ConceptClasses.
 * 
 * @author David Withers
 */
public class WSGraph {

	/**
	 * The ID of the Graph
	 */
	private Long id;

	/**
	 * The name of the graph
	 */
	private String name;

	public WSGraph() {
	}

	public WSGraph(ONDEXGraph graph) {
		id = graph.getSID();
		name = graph.getName();
	}

	/**
	 * Returns the id.
	 * 
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Sets the id.
	 * 
	 * @param id
	 *            the new id
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Returns the name.
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name.
	 * 
	 * @param name
	 *            the new name
	 */
	public void setName(String name) {
		this.name = name;
	}

}
