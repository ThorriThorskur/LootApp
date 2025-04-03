package is.hbv501g.lootapp.models;

import com.google.gson.annotations.SerializedName;

public class Card {
    private String id;
    private String name;

    @SerializedName("mana_cost")
    private String mana_cost;

    @SerializedName("type_line")
    private String type_line;

    @SerializedName("oracle_text")
    private String oracle_text;

    private String usd;

    @SerializedName("usd_foil")
    private String usd_foil;

    @SerializedName(value = "imageUrl", alternate = {"image_url"})
    private String imageUrl;

    @SerializedName("set_name")
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
        return imageUrl;
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

    public void setName(String name) {
        this.name = name;
    }

    public void setTypeLine(String type_line) {
        this.type_line = type_line;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

}
