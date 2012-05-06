package items;

import etc.Price;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: gron
 * Date: 5/5/12
 * Time: 9:02 PM
 */
public class Item {

    private int    id;
    private String title;
    private String color;
    private String img;
    private String type;
    private int    minLevel;
    private Price  price;
    private String fightType;
    private String lifeTime;
    private boolean              notTransferable = false;
    private Map<String, Integer> strength        = new HashMap<String, Integer>();
    private List<String>         magicType       = new ArrayList<String>();
    private Map<String, Integer> conditions      = new HashMap<String, Integer>();
    private List<String>         additionalInfo  = new ArrayList<String>();

    public Item(int id) {
        this.id = id;
    }

    public void addMinimumStrength(int minimumStrength) {
        this.strength.put("minimum", new Integer(minimumStrength));
    }

    public void addMaximumStrength(int maximumStrength) {
        this.strength.put("maximum", new Integer(maximumStrength));
    }

    public void resetStrength() {
        this.strength.clear();
    }

    public void addMagicType(String magicType) {
        this.magicType.add(magicType);
    }

    public void addCondition(String type, Integer value) {
        this.conditions.put(type, value);
    }

    public void addAdditionalInfo(String info) {
        this.additionalInfo.add(info);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getMinLevel() {
        return minLevel;
    }

    public void setMinLevel(int minLevel) {
        this.minLevel = minLevel;
    }

    public Price getPrice() {
        return price;
    }

    public void setPrice(Price price) {
        this.price = price;
    }

    public String getFightType() {
        return fightType;
    }

    public void setFightType(String fightType) {
        this.fightType = fightType;
    }

    public String getLifeTime() {
        return lifeTime;
    }

    public void setLifeTime(String lifeTime) {
        this.lifeTime = lifeTime;
    }

    public boolean isNotTransferable() {
        return notTransferable;
    }

    public void setNotTransferable(boolean notTransferable) {
        this.notTransferable = notTransferable;
    }

    public Map<String, Integer> getStrength() {
        return strength;
    }

    public void setStrength(Map<String, Integer> strength) {
        this.strength = strength;
    }

    public List<String> getMagicType() {
        return magicType;
    }

    public void setMagicType(List<String> magicType) {
        this.magicType = magicType;
    }

    public Map<String, Integer> getConditions() {
        return conditions;
    }

    public void setConditions(Map<String, Integer> conditions) {
        this.conditions = conditions;
    }

    public List<String> getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(List<String> additionalInfo) {
        this.additionalInfo = additionalInfo;
    }
}