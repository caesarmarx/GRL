package grl.com.dataModels;

import com.google.gson.annotations.SerializedName;


/**
 * Created by Administrator on 6/7/2016.
 */

public class OrderEntireModel {
    @SerializedName("ord_content")
    public OrderContentModel contentModel;

    @SerializedName("order")
    public OrderModel orderModel;

    @SerializedName("user_info")
    public UserModel userModel;


    public OrderEntireModel() {
        this.contentModel = new OrderContentModel();
        this.userModel = new UserModel();
        this.orderModel = new OrderModel();
    }
}
