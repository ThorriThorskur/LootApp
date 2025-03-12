// Card.java - complete implementation
package is.hbv501g.lootapp.models;

public class Card {
    private String id;
    private String name;
    private String mana_cost;
    private String type_line;
    private String oracle_text;
    private String usd;
    private String usd_foil;
    private String image_url;
    private String set_name;
    private boolean isLegendary;
    private boolean isLand;

    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getManaCost() {
        return mana_cost;
    }

    public String getTypeLine() {
        return type_line;
    }

    public String getOracleText() {
        return oracle_text;
    }

    public String getUsd() {
        return usd;
    }

    public String getUsdFoil() {
        return usd_foil;
    }

    public String getImageUrl() {
        return image_url;
    }

    public String getSetName() {
        return set_name;
    }

    public boolean isLegendary() {
        return isLegendary;
    }

    public boolean isLand() {
        return isLand;
    }
}