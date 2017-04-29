package grl.com.subViews.dialogues.planet;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import grl.wangu.com.grl.R;

/**
 * Created by Administrator on 6/28/2016.
 */
public class PlanetSettingDialog extends AlertDialog.Builder{

    // view
    EditText etUserName;
    EditText etUserPhone;

    // value
    Context context;
    String userName;
    String userPhone;
    View dialoglayout;

    public PlanetSettingDialog(Activity context) {
        super(context);

        this.context = context;
        LayoutInflater inflater = context.getLayoutInflater();
        this.dialoglayout = inflater.inflate(R.layout.planet_setting_dialog, null);
        this.setView(dialoglayout);

        this.getViewByID();
        this.initializeData();
        this.setOnListener();
    }


    public void setData(String userName , String userPhone) {
        this.userName = userName;
        this.userPhone = userPhone;
        initializeData();
    }

    public void getViewByID () {
        etUserName = (EditText) dialoglayout.findViewById(R.id.etUserName);
        etUserPhone = (EditText) dialoglayout.findViewById(R.id.etUserPhone);
    }

    public void initializeData () {
        // init views
        etUserName.setText(userName);
        etUserPhone.setText(userPhone);
    }

    public void setOnListener () {
    }
}
