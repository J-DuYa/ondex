package net.sourceforge.ondex.server.result;

/**
 * Provides the required result from an Export.
 *
 * This includes the id of the new graph and the String output of the events
 *
 * @author Christian Brenninkmeijer
 */
public class WSExportJobResult {

        /**
	 * The url to the data
	 */
	private String result;

        private String history;

    public WSExportJobResult(String result, String history){
        this.result = result;
        this.history = history;
     }

    public String getResult(){
        return result;
    }

    public void setResult(String url){
        this.result = url;
    }

    public String getHistory(){
        return history;
    }

    public void setHistory(String history){
        this.history = history;
    }

 }
