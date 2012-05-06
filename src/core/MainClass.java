package core;

import items.ItemParser;

import java.io.IOException;

public class MainClass {
    final static boolean DEBUG = true;

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
            } catch (NumberFormatException e) {
                System.out.println("You don't send valid item ID");
                return false;
            }
        }

        return true;
    }

    public static void main(String[] args) {
        if (MainClass.checkInputArguments(args)) {
            ItemParser parser = new ItemParser();

            try {
                parser.start(Integer.parseInt(args[0]));
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}