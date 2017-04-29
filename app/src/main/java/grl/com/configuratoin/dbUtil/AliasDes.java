package grl.com.configuratoin.dbUtil;

import com.orm.SugarRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 7/2/2016.
 */
public class AliasDes extends SugarRecord {
    private String userId;
    private String alias;
    private String mobileNum;
    private String desText;
    private String desImage;

    public AliasDes(){}

    public AliasDes(String userId) {
        this.userId = userId;
        this.alias = "";
        this.mobileNum = "";
        this.desImage = "";
        this.desText = "";
    }

    public void setMobileList(List<String> mobile) {
        String mobileTemp = "";
        for(int i = 0 ; i < mobile.size(); i ++) {
            mobileTemp = mobileTemp + mobile.get(i) + ", ";
        }
        this.mobileNum = mobileTemp;
    }

    public List<String> getMobileLst () {
        List<String> result = new ArrayList<String>();
        String[] strLst = this.mobileNum.split(", ");
        for(int i = 0; i < strLst.length; i ++) {
            if(strLst[i].equals(""))
                continue;
            result.add(strLst[i]);
        }
        return  result;
    }
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getMobileNum() {
        return mobileNum;
    }

    public void setMobileNum(String mobileNum) {
        this.mobileNum = mobileNum;
    }

    public String getDesText() {
        return desText;
    }


    public void setDesText(String desText) {
        this.desText = desText;
    }

    public String getDesImage() {
        return desImage;
    }

    public void setDesImage(String desImage) {
        this.desImage = desImage;
    }
}
