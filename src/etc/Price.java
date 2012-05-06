package etc;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: gron
 * Date: 5/5/12
 * Time: 9:03 PM
 */
public class Price {

    public static final String GOLD_NAME   = "Золотой";
    public static final String SILVER_NAME = "Серебряный";
    public static final String BRONZE_NAME = "Медный";

    private int gold;
    private int silver;
    private int bronze;

    public Price(HashMap<String,String> map){
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if(entry.getKey().equalsIgnoreCase(GOLD_NAME)){
                gold = Integer.parseInt(entry.getValue());
            }else if(entry.getKey().equalsIgnoreCase(SILVER_NAME)){
                silver = Integer.parseInt(entry.getValue());
            }else if(entry.getKey().equalsIgnoreCase(BRONZE_NAME)){
                bronze = Integer.parseInt(entry.getValue());
            }
        }
    }

    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public int getSilver() {
        return silver;
    }

    public void setSilver(int silver) {
        this.silver = silver;
    }

    public int getBronze() {
        return bronze;
    }

    public void setBronze(int bronze) {
        this.bronze = bronze;
    }
}
