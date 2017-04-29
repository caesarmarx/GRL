package grl.com.activities.discovery;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.http.BasicNameValuePair;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import grl.com.activities.common.ContentShowActivity;
import grl.com.configuratoin.Constant;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.SelfInfoModel;
import grl.com.configuratoin.Utils;
import grl.com.httpRequestTask.discovery.profile.UserProfileEnableTask;
import grl.com.httpRequestTask.discovery.profile.UserProfileGetTask;
import grl.com.network.HttpCallback;
import grl.wangu.com.grl.R;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.LineChartView;

/**
 * Created by Administrator on 7/1/2016.
 */
public class DisciplePriceActiviy extends Activity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    // view
    TextView tvPriveValue;
    Switch switchStatus;
    LineChartView chartView;

    // task
    UserProfileGetTask userProfileGetTask;
    UserProfileEnableTask userProfileEnableTask;

    // response
    int priceEnable;
    JsonArray resultData;

    // require
    String strBackTitle;
    String userID;

    // value
    private LineChartData data;
    private int numberOfLines = 1;
    private boolean hasAxes = true;
    private boolean hasAxesNames = true;
    private boolean hasLines = true;
    private boolean hasPoints = true;
    private lecho.lib.hellocharts.model.ValueShape shape = lecho.lib.hellocharts.model.ValueShape.CIRCLE;
    private boolean isFilled = false;
    private boolean hasLabels = false;
    private boolean isCubic = false;
    private boolean hasLabelForSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disciple_price);

        getParamsFromIntent();
        getViewByID();
        initNavBar();
        initializeData();
        setOnListener();
    }

    @Override
    protected void onDestroy() {
        userProfileEnableTask = new UserProfileEnableTask(this, userID, priceEnable, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object Response) throws JSONException {
                if(!flag || Response == null) {
                    GlobalVars.showErrAlert(DisciplePriceActiviy.this);
                    return;
                }
                JsonObject result = (JsonObject) Response;
                Boolean res_flag = result.get("result").getAsBoolean();
                if(res_flag) {                  // success

                } else {                        // failure

                }
            }
        });
        super.onDestroy();
    }

    public void getParamsFromIntent () {
        Intent intent = getIntent();
        strBackTitle = intent.getStringExtra(Constant.BACK_TITLE_ID);
        userID = intent.getStringExtra("user_id");
    }

    public void getViewByID () {
        tvPriveValue = (TextView) findViewById(R.id.tvPriceValue);
        switchStatus = (Switch) findViewById(R.id.switch_price);
        chartView = (LineChartView) findViewById(R.id.lineChart);
    }

    public void initNavBar () {
        findViewById(R.id.img_back).setVisibility(View.VISIBLE);
        findViewById(R.id.txt_left).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_left)).setText(strBackTitle);
        ((TextView)findViewById(R.id.txt_title)).setText(getString(R.string.user_price_title));
    }

    public void initializeData () {
        // init views
        if(!userID.equals(SelfInfoModel.userID)) {                              // 다른 사용자인 경우
            findViewById(R.id.layoutFirst).setVisibility(View.GONE);
            findViewById(R.id.tvHelp).setVisibility(View.GONE);
            findViewById(R.id.layout_second).setVisibility(View.GONE);
            findViewById(R.id.layout_switch).setVisibility(View.GONE);
        }

        // init data
        priceEnable = 0;
        resultData = new JsonArray();

        // send request
        userProfileGetTask = new UserProfileGetTask(this, userID, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object Response) throws JSONException {
                if(!flag || Response == null) {
                    GlobalVars.showErrAlert(DisciplePriceActiviy.this);
                    return;
                }
                try {
                    JsonObject result = (JsonObject) Response;
                    resultData = result.get("disciple_price").getAsJsonArray();
                    priceEnable = result.get("pay_enable").getAsInt();
                    initView();
                } catch (Exception e) {}
            }
        });
    }

    public void initChartView (){
        chartView.setInteractive(false);

        generateData();

        // Disable viewport recalculations, see toggleCubic() method for more info.
        chartView.setViewportCalculationEnabled(false);
    }
    public void initView () {
        float price = 0;
        try {
            if(resultData.size() > 0) {
                JsonObject temp = resultData.get(resultData.size() - 1).getAsJsonObject();
                price = temp.get("price").getAsFloat();
            }
        } catch (Exception e) {}
        String priceVal = String.valueOf(price) + "金条/月";
        tvPriveValue.setText(priceVal);
        switch(priceEnable) {
            case 0:
                switchStatus.setChecked(false);
                break;
            case 1:
                switchStatus.setChecked(true);
                break;
            default:
                break;
        }

        // chart view init
        chartView.postDelayed(new Runnable() {
            @Override
            public void run() {
//                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) chartView.getLayoutParams();
//                params.height = params.width / 2;
//                chartView.setLayoutParams(params);

                initChartView();
            }
        }, 1);
    }

    public void setOnListener () {
        findViewById(R.id.txt_left).setOnClickListener(this);
        findViewById(R.id.img_back).setOnClickListener(this);

        findViewById(R.id.tvHelp).setOnClickListener(this);
        switchStatus.setOnCheckedChangeListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_left:                  // 되돌이단추를 누를 때의 처리 진행
            case R.id.img_left:
                Utils.finish(this);
                break;
            case R.id.tvHelp:                    // 설명 보기
                String helpTitle = getString(R.string.user_price_title) + getString(R.string.disciple_price_help);
                Utils.start_Activity(this, ContentShowActivity.class,
                        new BasicNameValuePair(Constant.BACK_TITLE_ID, getString(R.string.user_price_title)),
                        new BasicNameValuePair(Constant.TITLE_ID, helpTitle),
                        new BasicNameValuePair(Constant.CONTENT_VIEW_KEY, Constant.PRICE_HELP));
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked) {
            priceEnable = 1;
        } else {
            priceEnable = 0;
        }
    }

    private void generateData() {

        // get data from server
        if(resultData.size() == 0) {
            return;
        }
        List<AxisValue> axisVal = new ArrayList<AxisValue>();
        List<Float> prices = new ArrayList<Float>();
        for(int i = resultData.size() - 1; i >= 0; i --) {
            JsonObject jsonObject = resultData.get(i).getAsJsonObject();
            String time = GlobalVars.getDateStringFromMongoDate(jsonObject.get("date").getAsJsonObject(), "MM/dd");
            AxisValue axis = new AxisValue(i + 1);
            axis.setLabel(time);
            axisVal.add(axis);

            float price = jsonObject.get("price").getAsFloat();
            prices.add(price);

            if(prices.size() > 11)                  // 1년분자료를 얻는다.
                break;
        }

        AxisValue axisValue = new AxisValue(0);
        axisValue.setLabel("");
        axisVal.add(0, axisValue);

        List<Line> lines = new ArrayList<Line>();
        for (int i = 0; i < numberOfLines; ++i) {

            List<PointValue> values = new ArrayList<PointValue>();
            for (int j = 0; j < prices.size(); ++j) {
                values.add(new PointValue(j + 1, prices.get(j)));
            }

            // 초기점 추가
            values.add(0, new PointValue(0, 0));

            Line line = new Line(values);
            line.setColor(ChartUtils.COLORS[i]);
            line.setShape(shape);
            line.setCubic(isCubic);
            line.setFilled(isFilled);
            line.setHasLabels(hasLabels);
            line.setHasLabelsOnlyForSelected(hasLabelForSelected);
            line.setHasLines(hasLines);
            line.setHasPoints(hasPoints);
//            if (pointsHaveDifferentColor){
//                line.setPointColor(ChartUtils.COLORS[(i + 1) % ChartUtils.COLORS.length]);
//            }
            lines.add(line);
        }

        data = new LineChartData(lines);

        if (hasAxes) {
            Axis axisX = new Axis();
            Axis axisY = new Axis().setHasLines(true);
            if (hasAxesNames) {
                axisX.setName("");
                axisY.setName("");

                axisX.setAutoGenerated(false);
                axisX.setValues(axisVal);
            }
            data.setAxisXBottom(axisX);
            data.setAxisYLeft(axisY);
        } else {
            data.setAxisXBottom(null);
            data.setAxisYLeft(null);
        }
        data.setBaseValue(Float.NEGATIVE_INFINITY);
        chartView.setLineChartData(data);
    }

    public enum ValueShape {
        CIRCLE, SQUARE, DIAMOND
    }

}
