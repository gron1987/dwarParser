/**
 * Created with IntelliJ IDEA.
 * User: gron
 * Date: 5/5/12
 * Time: 3:24 PM
 */

package core;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Log {
    public static boolean sLog(String message){
        if(MainClass.DEBUG){
            SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");//dd/MM/yyyy
            Date now = new Date();
            String strDate = sdfDate.format(now);

            System.out.println(message + " Time: " + strDate);
            return true;
        }else{
            return false;
        }
    }

}
