package net.sourceforge.ondex.server.result;

import net.sourceforge.ondex.core.ONDEXGraph;
import net.sourceforge.ondex.wsapi.plugins.BufferedOndexListener;

/**
 * Provides the required result from a filter.
 *
 * This includes the id of the new graph and the String output of the events
 *
 * @author Christian Brenninkmeijer
 */
public class WSFilterResult {

    /**
	 * The ID of the new Graph
	 */
	private Long id;

	/**
	 * The history of the events.
	 */
	private String eventHistory;

    public WSFilterResult(ONDEXGraph graph, BufferedOndexListener bufferedONDEXListener){
        id = graph.getSID();
        eventHistory = bufferedONDEXListener.getCompleteEventHistory();
    }

    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }

    public String getEventHistory(){
        return eventHistory;
    }

    public void setEventHistory(String eventHistory){
        this.eventHistory = eventHistory;
    }
}
