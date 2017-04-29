package grl.com.activities.energy.fGroup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.Utils;
import grl.com.httpRequestTask.friendGroup.FGroupCreateTask;
import grl.com.network.HttpCallback;
import grl.wangu.com.grl.R;

/**
 * Created by Administrator on 6/8/2016.
 */
public class FriendGroupCreateActivity extends Activity implements View.OnClickListener{

    // Tasks
    FGroupCreateTask fGroupCreateTask;
    // Views
    EditText etFgroupName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_group_create);

        this.getViewByID();
        this.initNavBar();
        this.initializeData();
        this.setOnListener();
    }

    public void getViewByID () {
        etFgroupName = (EditText) findViewById(R.id.et_group_name);
    }

    public void initNavBar () {
        ((ImageView)findViewById(R.id.img_back)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_left)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_left)).setText(this.getString(R.string.friend_group_title));
        ((TextView)findViewById(R.id.txt_title)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_title)).setText(this.getString(R.string.friend_create));
    }

    public void initializeData () {
    }

    public void setOnListener () {
        findViewById(R.id.img_back).setOnClickListener(this);
        findViewById(R.id.txt_left).setOnClickListener(this);
        findViewById(R.id.btn_create).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.img_back:                     // 되올이 단추를 누른 경우
            case R.id.txt_left:
                Utils.finish(this);
                break;
            case R.id.btn_create:                   // 친구계창설단추를 누른 경우
                onCreateAction();
                break;
        }
    }

    public void onCreateAction () {
        String name = etFgroupName.getText().toString();
        if(name.equals("")) {                       // 친구계이름을 입력하지 않은 경우
            GlobalVars.showCommonAlertDialog(this, "请输入名称", "");
            return;
        }
        if(FriendGroupActivity.self.isExistingGroup(name)) { // 이미 존재하는 친구계인 경우
            GlobalVars.showCommonAlertDialog(this, "已经创建个同名贵人圈","");
            return;
        }
        // send request
        fGroupCreateTask = new FGroupCreateTask(this, name, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object Response) throws JSONException {
                if(!flag || Response == null) {
                    GlobalVars.showErrAlert(FriendGroupCreateActivity.this);
                    return;
                }
                Toast.makeText(FriendGroupCreateActivity.this, "创建成功", Toast.LENGTH_LONG)
                        .show();
                Intent intent = new Intent();
                setResult(Activity.RESULT_OK, intent);
                Utils.finish(FriendGroupCreateActivity.this);
            }
        });
    }
}
