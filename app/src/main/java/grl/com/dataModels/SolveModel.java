package grl.com.dataModels;

import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

import grl.com.configuratoin.GlobalVars;

/**
 * Created by Administrator on 6/7/2016.
 */
public class SolveModel {

    @SerializedName("order_id")
    public String orderId;

    @SerializedName("content_id")
    public String contentId;

    @SerializedName("user_id")
    public String userId;

    @SerializedName("user_name")
    public String userName;

    @SerializedName("user_photo")
    public String userPhoto;

    @SerializedName("grl_number")
    public String grlNumber;

    @SerializedName("org_user_id")
    public String orgUserId;

    @SerializedName("ord_type")
    public String orderType;

    @SerializedName("ord_content")
    public String orderContent;

    @SerializedName("order_result")
    public String orderResult;

    @SerializedName("time_start")
    public Long timeStart;

    @SerializedName("time_end")
    public Long timeEnd;

    @SerializedName("order_status")
    public Integer orderStatus;

    @SerializedName("ord_budget")
    public Integer orderBudget;


    public SolveModel() {
        this.orderId = "";
        this.contentId = "";
        this.userId = "";
        this.userName = "";
        this.userPhoto = "";
        this.grlNumber = "";
        this.orgUserId = "";
        this.orderContent = "";
        this.orderResult = "";
        this.orderStatus = 0;
        this.orderBudget = 0;
        this.timeStart = Long.valueOf(0);
        this.timeEnd = Long.valueOf(0);
    }

    public void parseFromJson(JSONObject jsonObject) {
        try {
            this.orderId = GlobalVars.getIdFromJson(jsonObject, "order_id");
            this.contentId = GlobalVars.getIdFromJson(jsonObject, "content_id");
            this.userId = GlobalVars.getIdFromJson(jsonObject, "user_id");
            this.userName = GlobalVars.getStringFromJson(jsonObject, "user_name");
            this.userPhoto = GlobalVars.getStringFromJson(jsonObject, "user_photo");
            this.grlNumber = GlobalVars.getStringFromJson(jsonObject, "grl_order_id");
            this.orgUserId = GlobalVars.getIdFromJson(jsonObject, "org_user_id");
            this.orderType = GlobalVars.getStringFromJson(jsonObject, "ord_type");
            this.orderContent = GlobalVars.getStringFromJson(jsonObject, "ord_content");
            this.timeStart = GlobalVars.getDateFromJson(jsonObject, "time_start");
            this.timeEnd = GlobalVars.getDateFromJson(jsonObject, "time_end");
            this.orderStatus = GlobalVars.getIntFromJson(jsonObject, "order_status");
            this.orderResult = GlobalVars.getStringFromJson(jsonObject, "order_result");

        } catch (Exception ex) {

        }
    }
}
