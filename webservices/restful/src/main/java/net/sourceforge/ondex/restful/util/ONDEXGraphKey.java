package net.sourceforge.ondex.restful.util;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Keeps track of the mapping between graph id and filename.
 * 
 * @author taubertj
 * 
 */
@XmlRootElement
@XmlType(propOrder = { "id", "file" })
public class ONDEXGraphKey implements Comparable<ONDEXGraphKey> {

	/**
	 * number of cached graph
	 */
	private int id = 0;

	/**
	 * file from which graph is loaded
	 */
	private String file = "";

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ONDEXGraphKey)
			return file.equals(((ONDEXGraphKey) obj).getFile());
		return false;
	}

	@Override
	public int hashCode() {
		return file.hashCode();
	}

	@Override
	public String toString() {
		return "ONDEXGraphKey [file=" + file + ", id=" + id + "]";
	}

	@Override
	public int compareTo(ONDEXGraphKey o) {
		return id - o.id;
	}

}
