package items;

import core.Log;

import java.io.IOException;
import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: gron
 * Date: 5/7/12
 * Time: 12:01 AM
 */
public class ParserRunnable implements Runnable {
    private int id;

    public ParserRunnable(int id) {
        this.id = id;
    }

    @Override
    public void run() {
        Random generator = new Random();
        try {
            Thread.sleep(generator.nextInt(2000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ItemParser parser = new ItemParser();

        try {
            parser.start(id);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.sLog("End parse item " + Integer.toString(id));
    }
}
