package items;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import core.Config;
import core.Log;
import etc.Price;
import org.apache.http.ParseException;
import org.apache.http.conn.HttpHostConnectException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: gron
 * Date: 5/5/12
 * Time: 8:55 PM
 */
public class ItemParser {

    public static final String ITEM_TYPE_TD_TITLE          = "Тип предмета";
    public static final String ITEM_STRENGTH_TD_TITLE      = "Прочность предмета";
    public static final String ITEM_FIGHT_STYLE_TD_TITLE   = "Стиль боя";
    public static final String ITEM_MINIMUM_LEVEL_TD_TITLE = "Требуемый уровень";
    public static final String ITEM_PRICE_TD_TITLE         = "Цена";
    public static final String ITEM_MAGIC_STYLE_ID_TITLE   = "Школа магии";
    public static final String ITEM_LEVEL_REGEXP           = "Уровень ";

    private Item item;

    /**
     * Create web client and set client options
     *
     * @return
     */
    private WebClient _createWebClient() {
        WebClient webClient = new WebClient(BrowserVersion.FIREFOX_3_6);
        webClient.setJavaScriptEnabled(false);
        webClient.setCssEnabled(false);
        return webClient;
    }

    public Item start(int id) throws IOException, ParseException, HttpHostConnectException {
        WebClient client = _createWebClient();

        item = new Item(id);

        HtmlPage page = (HtmlPage) client.getPage(Config.ARTIFACT_URL + item.getId());

        if (page.asText().isEmpty()) {
            throw new ParseException("Empty page");
        }

        boolean parsingResult;

        parsingResult = getItemName(page);
        if (parsingResult) {
            getItemImage(page);
            getItemTopInfo(page);
            getItemBottomInfo(page);
        }
        return item;
    }

    /**
     * Parse full page and add title to item
     *
     * @param page
     * @return
     */
    private boolean getItemName(HtmlPage page) {
        List<?> tables = page.getByXPath("//table[@class='tbl-ati_brd-all']");

        try {
            HtmlTable tableItemName = (HtmlTable) tables.get(0);
            HtmlBold boldItemName = (HtmlBold) tableItemName.getByXPath("//h1/b").get(0);
            item.setTitle(boldItemName.asText());

            String color = boldItemName.getAttribute("style").replaceAll("color:", "");
            item.setColor(color);
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
        return true;
    }

    /**
     * Parse full page and add image to current item object
     *
     * @param page
     * @return
     */
    private boolean getItemImage(HtmlPage page) {
        List<?> tables = page.getByXPath("//div[@class='bg-inner-b']//table[@width='60']");

        try {
            HtmlTable tableItemImage = (HtmlTable) tables.get(0);
            item.setImg(Config.PROJECT_URL + tableItemImage.getAttribute("background"));
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
        return true;
    }

    /**
     * Parse top table information block and add data
     *
     * @param page
     * @return
     */
    private boolean getItemTopInfo(HtmlPage page) {
        List<?> tables = page.getByXPath("//div[@class='bg-inner-b']//td[contains(@class,'tbl-ati_regular')]");

        try {
            for (HtmlTableDataCell td : (List<HtmlTableDataCell>) tables) {
                String tdTitle = td.getAttribute("title");
                if (tdTitle.equalsIgnoreCase(ITEM_TYPE_TD_TITLE)) {
                    item.setType(td.asText());
                } else if (tdTitle.equalsIgnoreCase(ITEM_STRENGTH_TD_TITLE)) {
                    String strengthText = td.asText();
                    String[] strength = strengthText.split("/");
                    if (strength.length == 2) {
                        item.addMinimumStrength(Integer.parseInt(strength[0]));
                        item.addMaximumStrength(Integer.parseInt(strength[1]));
                    }
                } else if (tdTitle.equalsIgnoreCase(ITEM_FIGHT_STYLE_TD_TITLE)) {
                    item.setFightType(td.asText());
                } else if (tdTitle.equalsIgnoreCase(ITEM_MINIMUM_LEVEL_TD_TITLE)) {
                    try {
                        String levelText = td.asText().replace(ITEM_LEVEL_REGEXP, "");
                        if (levelText.indexOf("-") > 0) {
                            String[] levels = levelText.split("-");
                            item.setMinLevel(Integer.parseInt(levels[0]));
                            item.setMaxLevel(Integer.parseInt(levels[1]));
                        } else {
                            item.setMinLevel(Integer.parseInt(levelText));
                        }
                    } catch (NumberFormatException e) {
                        Log.sLog("Error in item level ID:" + item.getId());
                        e.printStackTrace();
                    }
                } else if (tdTitle.equalsIgnoreCase(ITEM_PRICE_TD_TITLE)) {
                    getPriceFromTd(td);
                } else if (tdTitle.equalsIgnoreCase(ITEM_MAGIC_STYLE_ID_TITLE)) {
                    String[] magicStyles = td.asText().split(" ");
                    for(String style : magicStyles){
                        item.addMagicType(style);
                    }
                }
            }
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
        return true;
    }

    /**
     * Parse price TD and set price to current item object
     *
     * @param td
     */
    private void getPriceFromTd(HtmlTableDataCell td) {
        // get prices array
        String[] priceValueArray = td.asText().split("  ");
        for (int i = 0; i < priceValueArray.length; ++i) {
            priceValueArray[i] = new String(priceValueArray[i]).trim();
        }
        // get currencies array
        List<String> currencyArray = new ArrayList<String>();
        List<?> spans = td.getElementsByTagName("span");
        for (HtmlSpan span : (List<HtmlSpan>) spans) {
            if (span.getAttribute("title").length() > 0) {
                currencyArray.add(span.getAttribute("title"));
            }
        }

        HashMap<String, String> prices = new HashMap<String, String>();
        int i = 0;
        for (String currency : currencyArray) {
            prices.put(currency, priceValueArray[i]);
            ++i;
        }

        item.setPrice(new Price(prices));
    }

    /**
     * Parse conditions table and set data to current item object
     *
     * @param page
     */
    private void getItemBottomInfo(HtmlPage page) {
        List<?> tables = page.getByXPath("//div[@class='bg-inner-b']//table[contains(@class,'tbl-ati_brd-all')]");

        try {
            HtmlTable table = (HtmlTable) tables.get(1);
            List<?> rows = table.getElementsByTagName("tr");
            for (HtmlTableRow row : (List<HtmlTableRow>) rows) {
                try {
                    // if we had 1 TD element - this is additional info. 2 TD elements - condition.
                    int tdCount = row.getElementsByTagName("td").size();
                    if (tdCount == 2) {
                        getConditionsFromRow(row);
                    } else if (tdCount == 1) {
                        getAdditionalInfoFromRow(row);
                    }
                } catch (IndexOutOfBoundsException e) {
                    continue;
                }
            }
        } catch (IndexOutOfBoundsException e) {

        }
    }

    /**
     * Get pair condition-value and add it to current item object
     *
     * @param row TR with conditions
     * @throws IndexOutOfBoundsException
     */
    private void getConditionsFromRow(HtmlTableRow row) throws IndexOutOfBoundsException {
        List<?> bs = row.getElementsByTagName("b");
        if (bs.size() == 2) {
            HtmlBold conditionNameBold = (HtmlBold) bs.get(0);
            HtmlBold conditionValueBold = (HtmlBold) bs.get(1);

            String conditionName = conditionNameBold.asText();
            String conditionValue = conditionValueBold.asText().replace("+", "");

            if (conditionName.matches(".*(У|у)рон")) {
                String[] damage = conditionValue.split(" .. ");
                damage[0] = new String(damage[0]).replace("+", "");
                damage[1] = new String(damage[1]).replace("+", "");
                item.addCondition(conditionName + " минимальный ", Float.parseFloat(damage[0]));
                item.addCondition(conditionName + " максимальный ", Float.parseFloat(damage[1]));
            } else {
                item.addCondition(conditionName, Float.parseFloat(conditionValue));
            }
        } else if (bs.size() == 0) {
            List<?> tds = row.getElementsByTagName("td");
            HtmlTableCell conditionTdName = (HtmlTableCell) tds.get(0);
            HtmlTableCell conditionTdValue = (HtmlTableCell) tds.get(1);

            if (conditionTdName.asText().matches("Время жизни")) {
                item.setLifeTime(conditionTdValue.asText());
            }
        }
    }

    /**
     * Get additional info and add its HTML to current item object
     *
     * @param row
     * @throws IndexOutOfBoundsException
     */
    private void getAdditionalInfoFromRow(HtmlTableRow row) throws IndexOutOfBoundsException {
        HtmlTableCell cell = (HtmlTableCell) row.getElementsByTagName("td").get(0);
        item.addAdditionalInfo(cell.asXml());
    }
}