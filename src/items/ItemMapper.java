package items;

import core.Log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: gron
 * Date: 5/9/12
 * Time: 4:53 PM
 */
public class ItemMapper {
    public final static String ITEM_TABLE_NAME            = "items";
    public final static String ITEM_TABLE_ADDITIONAL_INFO = "items_additional_info";
    public final static String ITEM_TABLE_CONDITIONS      = "items_conditions";
    public final static String ITEM_TABLE_MAGIC_TYPES     = "items_magic_types";

    private Connection connection;

    /**
     * Add item to DB
     *
     * @param item
     */
    public void add(Item item) {
        try {
            String preSqlQuery = "SELECT id FROM " + ITEM_TABLE_NAME + " WHERE id=?";
            PreparedStatement preStatement = connection.prepareStatement(preSqlQuery);
            preStatement.setInt(1, item.getId());
            ResultSet rs = preStatement.executeQuery();
            while(rs.next()){
                return;
            }

            String sqlQuery = "INSERT INTO " + ITEM_TABLE_NAME +
                    "(" +
                    "`id`,`title`,`color`,`img`,`type`,`min_level`,`max_level`,`strength_min`,`strength_max`," +
                    "`price_gold`,`price_silver`,`price_bronze`,`fight_type`,`life_time`,`not_transferable`," +
                    "`has_additional_info`,`has_conditions`,`has_magic_types`" +
                    ") " +
                    "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement statement = connection.prepareStatement(sqlQuery);
            statement.setInt(1, item.getId());
            statement.setString(2, item.getTitle());
            statement.setString(3, item.getColor());
            statement.setString(4, item.getImg());
            statement.setString(5, item.getType());
            statement.setInt(6, item.getMinLevel());
            statement.setInt(7, item.getMaxLevel());
            statement.setInt(8, item.getStrength().get("minimum"));
            statement.setInt(9, item.getStrength().get("maximum"));
            statement.setFloat(10, item.getPrice().getGold());
            statement.setFloat(11, item.getPrice().getSilver());
            statement.setFloat(12, item.getPrice().getBronze());
            statement.setString(13, item.getFightType());
            statement.setString(14, item.getLifeTime());
            statement.setBoolean(15, item.isNotTransferable());
            statement.setBoolean(16, item.getAdditionalInfo().isEmpty());
            statement.setBoolean(17, item.getConditions().isEmpty());
            statement.setBoolean(18, item.getMagicType().isEmpty());
            statement.execute();

            // save additional data to another tables
            addItemConditions(item);
            addItemAdditionalInfo(item);
            addItemMagicStyle(item);

            connection.commit();
        } catch (Exception e) {
            try {
                connection.rollback();
                Log.sLog("Rollback for item " + item.getId());
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    /**
     * Add additional info to DB
     *
     * @param item
     * @throws SQLException
     */
    private void addItemAdditionalInfo(Item item) throws SQLException {
        String sqlQuery = "INSERT INTO " + ITEM_TABLE_ADDITIONAL_INFO +
                "(`id_item`,`additional_info`)" +
                "VALUES(?,?)";
        for (String info : item.getAdditionalInfo()) {
            PreparedStatement statement = connection.prepareStatement(sqlQuery);
            statement.setInt(1, item.getId());
            statement.setString(2, info);
            statement.execute();
        }
    }

    /**
     * Add item conditons to DB
     *
     * @param item
     * @throws SQLException
     */
    private void addItemConditions(Item item) throws SQLException {
        String sqlQuery = "INSERT INTO " + ITEM_TABLE_CONDITIONS +
                "(`id_item`,`condition_name`,`condition_value`)" +
                "VALUES(?,?,?)";
        Set set = item.getConditions().entrySet();
        Iterator iterator = set.iterator();
        while (iterator.hasNext()) {
            Map.Entry condition = (Map.Entry) iterator.next();
            PreparedStatement statement = connection.prepareStatement(sqlQuery);
            statement.setInt(1, item.getId());
            statement.setString(2, condition.getKey().toString());
            statement.setFloat(3, Float.parseFloat(condition.getValue().toString()));
            statement.execute();
        }
    }

    /**
     * Add info about magic type of object to DB
     *
     * @param item
     * @throws SQLException
     */
    private void addItemMagicStyle(Item item) throws SQLException {
        String sqlQuery = "INSERT INTO " + ITEM_TABLE_MAGIC_TYPES +
                "(`id_item`,`magic_type`)" +
                "VALUES(?,?)";
        for (String magicType : item.getMagicType()) {
            PreparedStatement statement = connection.prepareStatement(sqlQuery);
            statement.setInt(1, item.getId());
            statement.setString(2, magicType);
            statement.execute();
        }
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }
}
