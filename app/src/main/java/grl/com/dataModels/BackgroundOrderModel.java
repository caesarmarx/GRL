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
public class BackgroundOrderModel {

    public String orderId;
    public boolean backShow;

    public ArrayList<SolveModel> solutions;

    public BackgroundOrderModel() {
        this.orderId = "";
        this.backShow = false;
    }

}
