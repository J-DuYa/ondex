/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sourceforge.ondex.server.executor;

import java.util.Random;
import java.util.concurrent.Callable;
import net.sourceforge.ondex.server.exceptions.JobException;
import org.apache.log4j.Logger;

/**
 *
 * @author christian
 */
public class TestJob implements Callable {

    private static Random random;

    private static final Logger logger = Logger.getLogger(TestJob.class);

    @Override
    public Object call() throws JobException {
        try {
            int val = random.nextInt(5);
            if (val == 0) {
                throw new JobException("Test fail", logger);
            }
            Thread.sleep(val * 1000);
            return val;
        } catch (InterruptedException ex) {
            return -1;
        }
    }

}
