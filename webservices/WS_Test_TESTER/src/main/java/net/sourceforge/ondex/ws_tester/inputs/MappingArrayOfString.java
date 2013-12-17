/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sourceforge.ondex.ws_tester.inputs;

import java.util.ArrayList;
import net.sourceforge.ondex.webservice.client.mapping.ArrayOfString;

/**
 *
 * @author christian
 */
public class MappingArrayOfString extends ArrayOfString{

    public MappingArrayOfString (){
        string = new ArrayList<String>();
    }

    public MappingArrayOfString (ArrayList<String> input){
        string =input;
    }

    public MappingArrayOfString (String[] input){
        string = new ArrayList<String>();
        for (String value: input ){
            string.add(value);
        }
    }
}
