package net.sourceforge.ondex.taverna.baclava;

/**
 * Interface to obtain the required information from a Baclava file.
 * 
 * Abstracts away from the actual implementation so it can be done either by an XMLBased hack or by proper Tavernacode.
 * 
 * @author Christian
 */
public interface Baclava {
    
    /**
     * Checks to see if the Baclava file includes data with this key.
     * 
     * @param key
     * @return True if and only if their is data with this key.
     */
    public boolean hasValue(String key);
}
