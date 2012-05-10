package core;

import items.Item;
import items.ItemMapper;
import items.ItemParser;
import items.ParserCallable;
import org.apache.http.ParseException;
import org.apache.http.conn.HttpHostConnectException;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

public class MainClass {
    final static boolean DEBUG = true;

    private static Connection connection;
    private static ItemMapper itemMapper;

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
        Item item = null;
        try {
            item = parser.start(id);
            Log.sLog("End process.");
        } catch (HttpHostConnectException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            Log.sLog("End process, empty page for item " + Integer.toString(id));
        }

        if(item != null){
            MainClass.saveItemToDB(item);
        }
    }

    /**
     * Parse all items which IDs between > start and <= end
     *
     * @param start
     * @param end
     */
    private static void parseItemsBetween(int start, int end) {
        ExecutorService executor = Executors.newFixedThreadPool(10);
        Set<Future<Item>> itemsFuture = new HashSet<Future<Item>>();
        List<Item> items = new ArrayList<Item>();
        for (int i = start; i <= end; ++i) {
            Callable<Item> c = new ParserCallable(i);
            Future<Item> itemFuture = executor.submit(c);
            itemsFuture.add(itemFuture);
        }
        int addedItems = 0;
        for (Future<Item> item : itemsFuture) {
            try {
                Item parsedItem = item.get();
                if (parsedItem != null) {
                    // add Item to DB
                    if(item != null){
                        MainClass.saveItemToDB(parsedItem);
                        ++addedItems;
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        executor.shutdown();
        Log.sLog("End work get " + Integer.toString(addedItems) + " elements ");
    }

    /**
     * Save item to DB
     *
     * @param item
     */
    private static void saveItemToDB(Item item){
        if(connection == null){
            try {
                Class.forName("com.mysql.jdbc.Driver").newInstance();
                connection = DriverManager.getConnection("jdbc:mysql://localhost/test?user=root&password=&useUnicode=true&characterEncoding=utf-8");
                connection.setAutoCommit(false);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if(itemMapper == null){
            itemMapper = new ItemMapper();
            itemMapper.setConnection(connection);
        }

        itemMapper.add(item);
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

        if(connection != null){
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}