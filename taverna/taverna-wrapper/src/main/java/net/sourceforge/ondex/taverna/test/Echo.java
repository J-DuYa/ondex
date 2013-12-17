package net.sourceforge.ondex.taverna.test;

/**
 * Simple test class which just writes out the parameters it has recieved.
 * 
 * @author Christian
 */
public class Echo {
    public static void main(String[] args) {
        System.out.println("args as length "+ args.length);
        for (int i = 0; i< args.length; i++){
            System.out.println(i + " " + args[i]);
            String replaced = args[i].replace((char)11,' ');
            System.out.println("\"" + replaced + "\"");            
            //for (int j = 0; j<args[i].length(); j++){
            //    System.out.println ("  " + args[i].codePointAt(j));
            //}
        }
    }
}
