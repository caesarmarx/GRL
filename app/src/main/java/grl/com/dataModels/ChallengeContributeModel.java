package grl.com.dataModels;

import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

import grl.com.configuratoin.GlobalVars;

/**
 * Created by Administrator on 6/7/2016.
 */
public class ChallengeContributeModel {

    @SerializedName("user_id")
    public String userId;

    @SerializedName("user_name")
    public String userName;

    @SerializedName("user_photo")
    public String userPhoto;

    @SerializedName("contribute")
    public int contribute;

    @SerializedName("pay")
    public float pay;

    public ChallengeContributeModel() {
        this.userId = "";
        this.contribute = 0;
        this.contribute = 0;
    }

    public void parseFromJson(JSONObject object) {
        this.userId = GlobalVars.getIdFromJson(object, "user_id");
        this.userName = GlobalVars.getStringFromJson(object, "user_name");
        this.contribute = GlobalVars.getIntFromJson(object, "contribute");
        this.pay = GlobalVars.getIntFromJson(object, "pay");
    }
}
