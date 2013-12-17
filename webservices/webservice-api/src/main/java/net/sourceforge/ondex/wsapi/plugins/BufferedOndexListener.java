/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sourceforge.ondex.wsapi.plugins;

import net.sourceforge.ondex.event.ONDEXEvent;
import net.sourceforge.ondex.event.ONDEXListener;
import net.sourceforge.ondex.event.type.EventType;

import org.apache.log4j.Logger;

/**
 *
 * @author christian
 */
public class BufferedOndexListener implements ONDEXListener
{

    public static String NEW_LINE = System.getProperty("line.separator");

    protected StringBuffer buffer = new StringBuffer();

    protected Logger logger;

    public BufferedOndexListener(Logger logger){
        this.logger= logger;
    }
   
    @Override
    public void eventOccurred(ONDEXEvent e) {
        EventType eventType = e.getEventType();
        buffer.append(eventType.getCompleteMessage());
        buffer.append(NEW_LINE);
        logger.info("event: "+eventType.getCompleteMessage());
    }

    public String getCompleteEventHistory(){
        return buffer.toString();
    }


}
