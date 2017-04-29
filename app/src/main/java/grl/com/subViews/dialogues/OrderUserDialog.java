package grl.com.subViews.dialogues;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import grl.com.activities.MainActivity;
import grl.com.activities.map.MapSelectActivity;
import grl.com.configuratoin.Constant;
import grl.com.configuratoin.GlobalVars;
import grl.wangu.com.grl.R;

/**
 * Created by macdev001 on 6/8/16.
 */

public class OrderUserDialog extends DialogFragment implements
        View.OnClickListener{

    Context mContext;

    Button okBtn;
    Button cancelBtn;

    String gender = "-1";
    String area = "";
    String number = "";
    int minDistance = 0;
    int maxDistance = 0;
    int direction = -1;
    double latitude = 0;
    double longitude = 0;
    int zoom = 0;

    RadioButton genderAllBtn;
    RadioButton genderManBtn;
    RadioButton genderWomanBtn;

    RadioButton areaAllBtn;
    RadioButton areaCityBtn;

    TextView numberView;
    EditText minDistanceEdit;
    EditText maxDistanceEdit;

    Button locationBtn;
    Button directionBtn;

    private final int DIRECTION_REQUEST = 0;
    private final int MAP_REQUEST = 1;

    public static OrderUserDialog newInstance() {
        OrderUserDialog dlg = new OrderUserDialog();
        return dlg;
    }

    public OrderUserDialog()
    {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.Full_Width_Dialog);
        setCancelable(false);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        View view = getActivity().getLayoutInflater().inflate(R.layout.layout_dialog_submit_user, new LinearLayout(getActivity()), false);

        // Build dialog
        Dialog builder = new Dialog(getActivity());
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        builder.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        builder.setContentView(view);

        okBtn = (Button)view.findViewById(R.id.btn_ok);
        okBtn.setOnClickListener(this);
        cancelBtn = (Button)view.findViewById(R.id.btn_cancel);
        cancelBtn.setOnClickListener(this);

        genderAllBtn = (RadioButton) view.findViewById(R.id.gender_all);
        genderManBtn = (RadioButton) view.findViewById(R.id.gender_man);
        genderWomanBtn = (RadioButton) view.findViewById(R.id.gender_woman);

        areaAllBtn = (RadioButton) view.findViewById(R.id.area_all);
        areaCityBtn = (RadioButton) view.findViewById(R.id.city_all);

        numberView = (TextView) view.findViewById(R.id.phone_view);
        numberView.setOnClickListener(this);

        minDistanceEdit = (EditText) view.findViewById(R.id.mindistance_edit);
        maxDistanceEdit = (EditText) view.findViewById(R.id.maxdistance_edit);

        locationBtn = (Button) view.findViewById(R.id.location_btn);
        directionBtn = (Button) view.findViewById(R.id.direction_btn);

        locationBtn.setOnClickListener(this);
        directionBtn.setOnClickListener(this);

        setController();

        return builder;
    }

    public void setParent(Context context) {
        mContext = context;
    }


    public void setData(String gender, String area, String number, int minDistance, int maxDistance, double latitude, double longitude, int direction) {
        this.gender = gender;
        this.area = area;
        this.number = number;
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
        this.latitude = latitude;
        this.longitude = longitude;
        this.direction = direction;
    }

    public void setController() {
        if (gender.compareTo("-1") == 0)
            genderAllBtn.setChecked(true);
        if (gender.compareTo("0") == 0)
            genderManBtn.setChecked(true);
        if (gender.compareTo("1") == 0)
            genderWomanBtn.setChecked(true);

        if (area.compareTo("-1") == 0)
            areaAllBtn.setChecked(true);
        if (area.compareTo("city") == 0)
            areaCityBtn.setChecked(true);

        numberView.setText(number);
        if (minDistance != 0)
            minDistanceEdit.setText(String.valueOf(minDistance));
        else
            minDistanceEdit.setText("");
        if (maxDistance != 0)
            maxDistanceEdit.setText(String.valueOf(maxDistance));
        else
            maxDistanceEdit.setText("");
    }

    public boolean isShowing() {
        if (getDialog() != null && getDialog().isShowing())
            return true;
        return false;
    }

    public void getData() {
        if (genderAllBtn.isChecked())
            gender = "-1";
        if (genderManBtn.isChecked())
            gender = "0";
        if (genderWomanBtn.isChecked())
            gender = "1";

        if (areaAllBtn.isChecked())
            area = "-1";
        if (areaCityBtn.isChecked())
            area = "city";

        number = numberView.getText().toString();
        minDistance = GlobalVars.getIntFromString((minDistanceEdit.getText().toString()));
        maxDistance = GlobalVars.getIntFromString((maxDistanceEdit.getText().toString()));

    }

    private void showContact() {
        ((MainActivity)getActivity()).startContactActivity(number);
    }

    private void showDirection() {
        ((MainActivity)getActivity()).startDirectionActivity(direction);
    }

    private void showMap() {
        ((MainActivity)getActivity()).startMapActivity(latitude, longitude, zoom);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.direction_btn:
                showDirection();
                break;
            case R.id.phone_view:
                showContact();
                break;
            case R.id.location_btn:
                showMap();
                break;
            case R.id.btn_ok:
                getData();
                Intent intent = new Intent();
                intent.putExtra(Constant.ORDER_GENDER, gender);
                intent.putExtra(Constant.ORDER_AREA, area);
                intent.putExtra(Constant.ORDER_NUMBER, number);
                intent.putExtra(Constant.ORDER_MIN_DISTANCE, minDistance);
                intent.putExtra(Constant.ORDER_MAX_DISTANCE, maxDistance);
                intent.putExtra(Constant.ORDER_DIRECTION, direction);
                intent.putExtra(Constant.ORDER_LATITUDE, latitude);
                intent.putExtra(Constant.ORDER_LONGITUDE, longitude);
                getTargetFragment().onActivityResult(getTargetRequestCode(), 1, intent);
                if (isShowing())
                    dismiss();
                break;
            case R.id.btn_cancel:
                if (isShowing())
                    dismiss();
                break;
        }
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getZoom() {
        return zoom;
    }

    public void setZoom(int zoom) {
        this.zoom = zoom;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
        numberView.setText(number);
    }
}