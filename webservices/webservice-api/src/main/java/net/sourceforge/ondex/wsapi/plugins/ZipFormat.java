/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sourceforge.ondex.wsapi.plugins;

import org.apache.log4j.Logger;

/**
 *
 * @author christian
 */
public enum ZipFormat {
    ZIP(true, true),
    GZIP(false, true),
    TAR(true, true),
    TAR_GZIP(true, true),
    URL(false, false),
    RAW(false, false);

    private static final Logger logger = Logger.getLogger(ZipFormat.class);

    private boolean isDirectory;
    private boolean compressed;

    static ZipFormat DEFAULT = RAW;

    private ZipFormat(boolean d, boolean c){
        isDirectory = d;
        compressed = c;
    }

    public static ZipFormat parseString(String format) {
        if (format == null){
            return DEFAULT;
        }
        if (format.length() == 0){
            return DEFAULT;
        }
        String lower = format.toLowerCase();
        if (lower.endsWith("zip")) {
            return ZIP;
        }
        if (lower.endsWith("tar")) {
            return TAR;
        }
        if (lower.endsWith("tar.gz")) {
            return TAR_GZIP;
        }
        if (lower.endsWith("gz")) {
            return GZIP;
        }
        if (lower.endsWith("url")) {
            return URL;
        }
        logger.info("Unexpected ZipFormat String "+format);
        return RAW;
    }

    public static String legalValues(){
        return "\"\" in which case the output file's contents are returned as a String. (This is the default) " + 
                "In all other case the file is held on the server and only a url is returned. " +
                "\"url\" results in a url pointing to the unzipped file as returned by the plugin. " + 
                "\"zip\", \"tar\", \"tar.gz\" and \"gz\" request the WebServer to compress the returned file. " +
                "In all url and compressed url cases the resulting file is available to anyone who knows the url, " +
                "and remains avaiable until cleaned up by the adminsistrator. ";
    }

    public boolean isDirectory(){
        return isDirectory;
    }

    public boolean isCompressed(){
        return compressed;
    }
}
