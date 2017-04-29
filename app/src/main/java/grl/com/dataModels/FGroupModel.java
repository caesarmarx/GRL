package grl.com.dataModels;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 6/8/2016.
 */
public class FGroupModel {
    @SerializedName("group_id")
    public String groupID;

    @SerializedName("group_name")
    public String groupName;

    public String groupDetail;

    public FGroupModel() {
        this.groupID = "";
        this.groupName = "";

        this.groupDetail = "";
    }
}
