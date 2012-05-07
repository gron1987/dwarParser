package core;

import items.Item;
import items.ItemParser;
import items.ParserCallable;
import org.apache.http.ParseException;
import org.apache.http.conn.HttpHostConnectException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

public class MainClass {
    final static boolean DEBUG = true;

    /**
     * Check input arguments
     *
     * @param args
     * @return
     */
    private static boolean checkInputArguments(String[] args) {
        if (args.length < 1) {
            System.out.println("You don't send valid item ID");
            return false;
        } else {
            if (args[0].isEmpty()) {
                System.out.println("You don't send valid item ID");
                return false;
            }
            try {
                Integer.parseInt(args[0]);
                Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.out.println("You don't send valid item ID");
                return false;
            } catch (IndexOutOfBoundsException e) {

            }
        }

        return true;
    }

    /**
     * Parse item by it's ID
     *
     * @param id
     */
    private static void parseOneItem(int id) {
        ItemParser parser = new ItemParser();
        try {
            Item item = parser.start(id);
        } catch (HttpHostConnectException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            Log.sLog("End process, empty page for item " + Integer.toString(id));
        }
    }

    /**
     * Parse all items which IDs between > start and <= end
     *
     * @param start
     * @param end
     */
    private static void parseItemsBetween(int start, int end) {
        ExecutorService executor = Executors.newFixedThreadPool(end - start);
        Set<Future<Item>> itemsFuture = new HashSet<Future<Item>>();
        List<Item> items = new ArrayList<Item>();
        for (int i = start; i <= end; ++i) {
            Callable<Item> c = new ParserCallable(i);
            Future<Item> itemFuture = executor.submit(c);
            itemsFuture.add(itemFuture);
        }
        for (Future<Item> item : itemsFuture) {
            try {
                Item parsedItem = item.get();
                if(parsedItem != null){
                    items.add(parsedItem);
                }
            } catch (InterruptedException e) {
            } catch (ExecutionException e) {
            }
        }
        Log.sLog("End work get " + items.size() + " elements " );
    }

    public static void main(String[] args) {
        if (MainClass.checkInputArguments(args)) {
            Log.sLog("Start process");
            if (args.length == 1) {
                MainClass.parseOneItem(Integer.parseInt(args[0]));
            } else if (args.length == 2) {
                MainClass.parseItemsBetween(
                        Integer.parseInt(args[0]),
                        Integer.parseInt(args[1])
                );
            }
        }
    }
}