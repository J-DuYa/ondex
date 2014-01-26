/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sourceforge.ondex.ws_tester.inputs;

import java.util.ArrayList;
import net.sourceforge.ondex.webservice.client.parser.ArrayOfString;

/**
 *
 * @author christian
 */
public class ParserArrayOfString extends ArrayOfString{

    public ParserArrayOfString (){
        string = new ArrayList<String>();
    }
}
