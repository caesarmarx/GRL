package grl.com.dataModels;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 6/7/2016.
 */
public class FactorModel {

    @SerializedName("est_name")
    public String estName;

    @SerializedName("est_value")
    public String estValue;

    @SerializedName("description")
    public String description;

    public FactorModel() {
        this.estName = "";
        this.estValue = "";
        this.description = "";
    }
}
