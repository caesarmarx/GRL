package grl.com.dataModels;

/**
 * Created by macdev001 on 7/8/16.
 */

public class EmoticonModel {
    public String emoticonName;
    public float emoticonValue;
    public int resId;

    public EmoticonModel() {
        emoticonName = "";
        emoticonValue = 0;
        resId = 0;
    }
    public EmoticonModel(String name, float value, int id) {
        emoticonName = name;
        emoticonValue = value;
        resId = id;
    }
}
