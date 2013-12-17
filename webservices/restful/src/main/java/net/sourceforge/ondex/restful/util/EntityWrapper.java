package net.sourceforge.ondex.restful.util;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Simple wrapper class for entity id, from and to.
 * 
 * @author taubertj
 * 
 */
@XmlRootElement
@XmlType(propOrder = { "id", "from", "to" })
public class EntityWrapper implements Comparable<EntityWrapper> {

	/**
	 * wrapped relation id
	 */
	private Integer id = Integer.valueOf(0);

	/**
	 * wrapped from concept id
	 */
	private Integer from = Integer.valueOf(0);

	/**
	 * wrapped to concept id
	 */
	private Integer to = Integer.valueOf(0);

	@Override
	public int compareTo(EntityWrapper o) {
		return o.id.compareTo(id);
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getFrom() {
		return from;
	}

	public void setFrom(Integer from) {
		this.from = from;
	}

	public Integer getTo() {
		return to;
	}

	public void setTo(Integer to) {
		this.to = to;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof EntityWrapper)
			return id.equals(((EntityWrapper) obj).id);
		return false;
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public String toString() {
		return "Entity ID: " + id + " " + from + " " + to;
	}
}
