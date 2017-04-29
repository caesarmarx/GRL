package grl.com.dataModels;

import android.util.Log;

import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import grl.com.configuratoin.GlobalVars;

/**
 * Created by Administrator on 6/7/2016.
 */
public class OrderModel {

    public static final int ORDER_INIT_STATE = 0;
    public static final int ORDER_ACCEPT_STATE = 1;
    public static final int ORDER_DISCUSS_STATE = 2;
    public static final int ORDER_SOLVE_STATE = 3;
    public static final int ORDER_SELECT_STATE = 4;
    public static final int ORDER_CANCEL_STATE = 5;
    public static final int ORDER_RESEND_STATE = 6;

    @SerializedName("order_id")
    public String orderId;

    @SerializedName("content_id")
    public String contentId;

    @SerializedName("from_userid")
    public String fromUserId;

    @SerializedName("grl_order_id")
    public String grlNumber;

    @SerializedName("org_user_id")
    public String orgUserId;

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


    public ArrayList<SolveModel> solutions;

    public OrderModel() {
        this.orderId = "";
        this.contentId = "";
        this.fromUserId = "";
        this.grlNumber = "";
        this.orgUserId = "";
        this.orderContent = "";
        this.orderResult = "";
        this.orderStatus = 0;
        this.orderBudget = 0;
        this.timeStart = Long.valueOf(0);
        this.timeEnd = Long.valueOf(0);
        this.solutions = null;
    }

    public void parseFromJson(JSONObject jsonObject) {

        try {
            this.orderId = GlobalVars.getIdFromJson(jsonObject, "order_id");
            this.contentId = GlobalVars.getIdFromJson(jsonObject, "content_id");
            this.fromUserId = GlobalVars.getIdFromJson(jsonObject, "from_userid");
            this.grlNumber = GlobalVars.getStringFromJson(jsonObject, "grl_order_id");
            this.orderContent = GlobalVars.getIdFromJson(jsonObject, "ord_content");
            this.timeStart = GlobalVars.getDateFromJson(jsonObject, "time_start");
            this.timeEnd = GlobalVars.getDateFromJson(jsonObject, "time_end");
            this.orderStatus = GlobalVars.getIntFromJson(jsonObject, "order_status");
            this.orderResult = GlobalVars.getStringFromJson(jsonObject, "order_result");
            this.orderBudget = GlobalVars.getIntFromJson(jsonObject, "ord_budget");
            if (jsonObject.has("solution")) {
                if (this.solutions != null)
                    this.solutions.clear();
                else
                    this.solutions = new ArrayList<SolveModel>();
                JSONArray jsonSolutions = jsonObject.getJSONArray("solution");
                for (int i = 0; i < jsonSolutions.length(); i++) {
                    JSONObject jsonSolution = jsonSolutions.getJSONObject(i);
                    SolveModel model = new SolveModel();
                    model.parseFromJson(jsonSolution);
                    this.solutions.add(model);
                }
            } else {
                this.solutions = null;
            }
        } catch (Exception ex) {
            Log.e("Json Parse", ex.getMessage());

        }
    }
}
