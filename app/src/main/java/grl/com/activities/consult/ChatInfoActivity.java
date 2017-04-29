package grl.com.activities.consult;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.koushikdutta.async.http.BasicNameValuePair;

import org.json.JSONObject;

import grl.com.activities.common.ContentShowActivity;
import grl.com.configuratoin.Constant;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.SelfInfoModel;
import grl.com.configuratoin.Utils;
import grl.com.httpRequestTask.chat.ChatRuleTask;
import grl.com.network.HttpCallback;
import grl.wangu.com.grl.R;

public class ChatInfoActivity extends Activity implements View.OnClickListener {

    String targetName = "";
    String timeRule = "";
    String judgeRule = "";
    String estimateRule = "";

    TextView targetView;
    LinearLayout recordRuleLayout;
    LinearLayout judgeRuleLayout;
    LinearLayout estimateRuleLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_info);

        Intent intent = getIntent();
        targetName = intent.getStringExtra(Constant.USER_NAME);

        getViewByID();
        initNavBar();
        setOnListener();
        initializeData();
    }

    public void getViewByID () {
        targetView = (TextView)findViewById(R.id.tv_target);
        recordRuleLayout = (LinearLayout)findViewById(R.id.linear_record_rule);
        estimateRuleLayout = (LinearLayout)findViewById(R.id.linear_estimate_rule);
        judgeRuleLayout = (LinearLayout)findViewById(R.id.linear_judge_rule);
    }

    public void initNavBar() {
        ((ImageView)findViewById(R.id.img_back)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_left)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_left)).setText(this.getString(R.string.user_nav_back));
        ((TextView)findViewById(R.id.txt_title)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_title)).setText("");
        ((TextView)findViewById(R.id.txt_right)).setVisibility(View.GONE);
    }

    public void setOnListener() {
        findViewById(R.id.img_back).setOnClickListener(this);
        findViewById(R.id.txt_left).setOnClickListener(this);
        findViewById(R.id.txt_right).setOnClickListener(this);

        recordRuleLayout.setOnClickListener(this);
        estimateRuleLayout.setOnClickListener(this);
        judgeRuleLayout.setOnClickListener(this);
    }

    public void initializeData() {
        // set up adapters
        if (targetName != null || !targetName.isEmpty())
            targetView.setText(targetName);

        new ChatRuleTask(ChatInfoActivity.this, SelfInfoModel.userID, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object response) {
                if(!flag || response == null) {                 //failure
                    GlobalVars.showErrAlert(ChatInfoActivity.this);
                    return;
                }
                try {
                    JSONObject result = (JSONObject) response;

                    timeRule = GlobalVars.getStringFromJson(result, "time_rule");
                    estimateRule = GlobalVars.getStringFromJson(result, "estimate_rule");
                    judgeRule = GlobalVars.getStringFromJson(result, "judge_rule");
                } catch (Exception e) {
                    Log.e("MYAPP", "exception", e);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:                     // 되올이 단추를 누른 경우
            case R.id.txt_left:
                Utils.finish(this);
                break;
            case R.id.linear_record_rule:
                onTimeRuleClick();
                break;
            case R.id.linear_estimate_rule:
                onEstimateRuleClick();
                break;
            case R.id.linear_judge_rule:
                onJudgeRuleClick();
                break;
        }
    }

    private void onTimeRuleClick() {
        if (timeRule == null || timeRule.isEmpty())
            return;
        Utils.start_Activity(this, ContentShowActivity.class,
                new BasicNameValuePair(Constant.TITLE_ID, getString(R.string.chat_time_rule)),
                new BasicNameValuePair(Constant.BACK_TITLE_ID, getString(R.string.user_nav_back)),
                new BasicNameValuePair(Constant.CONTENT_VIEW_KEY, timeRule));
    }
    private void onEstimateRuleClick() {
        Utils.start_Activity(this, ContentShowActivity.class,
                new BasicNameValuePair(Constant.TITLE_ID, getString(R.string.chat_estimate_rule)),
                new BasicNameValuePair(Constant.BACK_TITLE_ID, getString(R.string.user_nav_back)),
                new BasicNameValuePair(Constant.CONTENT_VIEW_KEY, estimateRule));

    }
    private void onJudgeRuleClick() {
        Utils.start_Activity(this, ContentShowActivity.class,
                new BasicNameValuePair(Constant.TITLE_ID, getString(R.string.chat_judge_rule)),
                new BasicNameValuePair(Constant.BACK_TITLE_ID, getString(R.string.user_nav_back)),
                new BasicNameValuePair(Constant.CONTENT_VIEW_KEY, judgeRule));
    }
}
