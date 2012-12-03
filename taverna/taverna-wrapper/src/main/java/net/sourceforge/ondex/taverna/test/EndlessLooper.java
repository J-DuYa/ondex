package net.sourceforge.ondex.taverna.test;

/**
 * This is a test class for testing the ProcessRunner
 * 
 * Loops forever, giving an occasional sign of live by printing out a counter;
 * <p>
 * Has to be interupted from outside, but that is the expected behaviour as it was written to test destroying.
 * 
 * @author Christian
 */
public class EndlessLooper {
    static int ignoreMe;
    
    public static void main(String[] args) {
        int counter = 0;
        do {
            for (int i = 0; i < 10000 * counter; i++){
                ignoreMe = i;
            }
            counter ++;
            System.out.println (counter);
        } while (true);
    }
}
