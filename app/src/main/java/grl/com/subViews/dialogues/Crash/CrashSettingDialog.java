package grl.com.subViews.dialogues.Crash;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import grl.wangu.com.grl.R;

/**
 * Created by Administrator on 6/29/2016.
 */
public class CrashSettingDialog  extends AlertDialog.Builder implements RadioGroup.OnCheckedChangeListener{

    // view
    RadioGroup genderGroup;
    RadioGroup arearGroup;

    // value
    Context context;
    View dialoglayout;
    int[] genderIDS;
    int[] areaIDs;
    int genderVal;
    int areaVal;

    public CrashSettingDialog(Activity context) {
        super(context);

        this.context = context;
        LayoutInflater inflater = context.getLayoutInflater();
        this.dialoglayout = inflater.inflate(R.layout.crash_setting_dialog, null);
        this.setView(dialoglayout);

        this.getViewByID();
        this.initializeData();
        this.setOnListener();
    }



    public void setData(int genderVal , int areaVal ) {
        this.genderVal = genderVal;
        this.areaVal = areaVal;
        initializeData();
    }


    public void getViewByID () {
        genderGroup = (RadioGroup) dialoglayout.findViewById(R.id.genderGroup);
        arearGroup = (RadioGroup) dialoglayout.findViewById(R.id.areaGroup);
    }

    public void initializeData () {
        // init value

        // init views
        ((RadioButton)genderGroup.getChildAt(genderVal)).setChecked(true);
        ((RadioButton)arearGroup.getChildAt(areaVal)).setChecked(true);
    }

    public void setOnListener () {
        genderGroup.setOnCheckedChangeListener(this);
        arearGroup.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (group.getId()) {
            case R.id.genderGroup:                  // 성별을 설정한 경우
                break;
            case R.id.areaGroup:                    // 지역을 설정한 경우
                break;
        }
    }
}
