package items;

import core.Log;
import org.apache.http.ParseException;
import org.apache.http.conn.HttpHostConnectException;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: gron
 * Date: 5/7/12
 * Time: 12:01 AM
 */
public class ParserCallable implements Callable {
    private int id;

    public ParserCallable(int id) {
        this.id = id;
    }

    public Item call() {
        Random generator = new Random();
        try {
            Thread.sleep(generator.nextInt(2000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ItemParser parser = new ItemParser();

        Item item = null;
        try {
            item = parser.start(id);
        } catch (HttpHostConnectException e) {
            e.printStackTrace();
            return item;
        } catch (IOException e) {
            e.printStackTrace();
            return item;
        } catch (ParseException e) {
            Log.sLog("End process, empty page for item " + Integer.toString(id));
            return item;
        }

        Log.sLog("End parse item " + Integer.toString(id));

        return item;
    }
}
