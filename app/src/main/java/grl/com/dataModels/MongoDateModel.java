package grl.com.dataModels;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 6/8/2016.
 */
public class MongoDateModel {
    @SerializedName("sec")
    public long sec;

    @SerializedName("usec")
    public long usec;

    public MongoDateModel() {
        this.sec = 0;
        this.usec = 0;
    }
}
