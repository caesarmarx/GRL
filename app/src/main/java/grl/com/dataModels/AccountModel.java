package grl.com.dataModels;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 7/7/2016.
 */
public class AccountModel {
    @SerializedName("item_id")
    public String itemID;

    @SerializedName("item_path")
    public String itemImage;

    @SerializedName("item_name")
    public String itemName;

    @SerializedName("item_price_unit")
    public String itemPriceUnit;

    @SerializedName("item_price")
    public int itemPrice;

    @SerializedName("item_cnt")
    public int count;

    @SerializedName("cyber_type")
    public int itemType;
}
