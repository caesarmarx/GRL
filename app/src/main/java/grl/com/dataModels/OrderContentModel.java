package grl.com.dataModels;

import android.util.Log;

import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

import grl.com.configuratoin.GlobalVars;

/**
 * Created by Administrator on 6/7/2016.
 */
public class OrderContentModel {
    @SerializedName("content_id")
    public String contentId;

    @SerializedName("ord_content_id")
    public String ord_content_id;

    @SerializedName("user_id")
    public String userID;

    @SerializedName("user_name")
    public String userName;

    @SerializedName("user_photo")
    public String userPhoto;

    @SerializedName("ord_type")
    public String ordType;

    @SerializedName("ord_content")
    public String ordContent;

    @SerializedName("ord_sound")
    public String ordSound;

    @SerializedName("time_start")
    public Long timeStart;

    @SerializedName("ord_budget")
    public Integer ordBudget;


    public OrderContentModel() {
        this.contentId = "";
        this.ord_content_id = "";
        this.userID = "";
        this.userName = "";
        this.userPhoto = "";
        this.ordType = "";
        this.ordContent = "";
        this.ordSound = "";
        this.ordBudget = 0;
    }

    public void parseFromJson(JSONObject jsonObject) {
        try {
            this.contentId = GlobalVars.getIdFromJson(jsonObject, "content_id");
            this.ord_content_id = GlobalVars.getIdFromJson(jsonObject, "ord_content_id");
            this.userID = GlobalVars.getIdFromJson(jsonObject, "user_id");
            this.userName = GlobalVars.getStringFromJson(jsonObject, "user_name");
            this.userPhoto = GlobalVars.getStringFromJson(jsonObject, "user_photo");
            this.ordType = GlobalVars.getStringFromJson(jsonObject, "ord_type");
            this.ordContent = GlobalVars.getStringFromJson(jsonObject, "ord_content");
            this.ordSound = GlobalVars.getStringFromJson(jsonObject, "ord_sound");
            this.timeStart = GlobalVars.getDateFromJson(jsonObject, "time_start");
            this.ordBudget = GlobalVars.getIntFromJson(jsonObject, "ord_budget");
        } catch (Exception ex) {
            Log.e("Json Parse", ex.getMessage());
        }
    }
}
