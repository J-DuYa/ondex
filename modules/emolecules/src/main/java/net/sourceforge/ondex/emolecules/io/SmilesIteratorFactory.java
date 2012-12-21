package net.sourceforge.ondex.emolecules.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.log4j.Logger;

/**
 * The Smiterator (smiles iterator) factory.
 * 
 * @author grzebyta
 * @see Iterable
 */
public class SmilesIteratorFactory implements Iterable<Smile> {
    
    private LineIterator li;
    private static Logger log = Logger.getLogger(SmilesIteratorFactory.class);
    
    public SmilesIteratorFactory(File f) throws IOException {
        // convert compressed file to input stream
        GzipCompressorInputStream is =
                new GzipCompressorInputStream(new FileInputStream(f));
        try {
            this.li = IOUtils.lineIterator(is, null);
        } catch (IOException io) {
            IOUtils.closeQuietly(is);
            throw io;
        }
    }
    
    public SmilesIteratorFactory(File f, String encoding) throws IOException {
        GzipCompressorInputStream is =
                new GzipCompressorInputStream(new FileInputStream(f));
        try {
            this.li = IOUtils.lineIterator(is, encoding);
        } catch (IOException io) {
            IOUtils.closeQuietly(is);
            throw io;
        }
    }
    
    public SmilesIteratorFactory(InputStream is, String encoding) throws IOException {
        try {
            this.li = IOUtils.lineIterator(is, encoding);
        } catch (IOException io) {
            IOUtils.closeQuietly(is);
            throw io;
        }
    }
    
    public Iterator<Smile> iterator() {
        // to ommit first line
        li.nextLine();
        
        log.debug("prepare iterator");
        return new Iterator<Smile>() {
            public boolean hasNext() {
                return li.hasNext();
            }
            
            public Smile next() {
                String line = li.nextLine();
                String[] records = line.split("\\s");
                
                Smile toReturn = new Smile();
                toReturn.setSmile(records[0]);
                toReturn.setId(Long.valueOf(records[1]));
                toReturn.setParent(Long.valueOf(records[2]));
                
                return toReturn;
            }
            
            public void remove() {
                // not supported
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };
    }
}
