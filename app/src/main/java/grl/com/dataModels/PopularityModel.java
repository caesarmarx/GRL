package grl.com.dataModels;

import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONObject;

import grl.com.configuratoin.GlobalVars;

/**
 * Created by Administrator on 6/7/2016.
 */
public class PopularityModel {

    @SerializedName("twitter_id")
    public String twitterId;

    @SerializedName("user_id")
    public String userId;

    @SerializedName("user_name")
    public String userName;

    @SerializedName("user_photo")
    public String userPhoto;

    @SerializedName("est_name")
    public String estName;

    @SerializedName("photo_path")
    public String photoPath;

    @SerializedName("describe_content")
    public String describeContent;

    @SerializedName("publish_date")
    public Long publishDate;

    @SerializedName("praise_total")
    public int praiseTotal;

    @SerializedName("suremans")
    public JSONArray sureMans;

    @SerializedName("praise_list")
    public JSONArray praiseList;

    @SerializedName("estimate_total")
    public int estimateTotal;

    @SerializedName("estimate_list")
    public JSONArray estimateList;

    public PopularityModel() {
        this.twitterId = "";
        this.userId = "";
        this.userName = "";
        this.userPhoto = "";
        this.estName = "";
        this.photoPath = "";
        this.describeContent = "";
        this.publishDate = Long.valueOf(0);
        this.praiseTotal = 0;
        this.estimateTotal = 0;
    }

    public void parseFromJson(JSONObject jsonObj) {
        this.twitterId = GlobalVars.getIdFromJson(jsonObj, "twitter_id");
        this.userId = GlobalVars.getIdFromJson(jsonObj, "user_id");
        this.userName = GlobalVars.getStringFromJson(jsonObj, "user_name");
        this.userPhoto = GlobalVars.getStringFromJson(jsonObj, "user_photo");
        this.photoPath = GlobalVars.getStringFromJson(jsonObj, "photo_path");
        this.estName = GlobalVars.getStringFromJson(jsonObj, "est_name");
        this.describeContent = GlobalVars.getStringFromJson(jsonObj, "describe_content");
        this.publishDate = GlobalVars.getDateFromJson(jsonObj, "publish_date");
        this.praiseTotal = GlobalVars.getIntFromJson(jsonObj, "praise_total");
        this.sureMans = GlobalVars.getJSONArrayFromJson(jsonObj, "suremans");
        this.praiseList = GlobalVars.getJSONArrayFromJson(jsonObj, "praise_list");
        this.estimateTotal = GlobalVars.getIntFromJson(jsonObj, "estimate_total");
        this.estimateList = GlobalVars.getJSONArrayFromJson(jsonObj, "estimate_list");
    }
}
