package grl.com.activities.discovery.popularity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.koushikdutta.async.http.BasicNameValuePair;

import org.json.JSONArray;
import org.json.JSONObject;

import grl.com.activities.energy.tGroup.challenge.ChallengeDetailActivity;
import grl.com.adapters.order.UserListAdapter;
import grl.com.adapters.popularity.PopularityChallengeAdapter;
import grl.com.adapters.popularity.StateExampleListAdapter;
import grl.com.adapters.popularity.StatePKListAdapter;
import grl.com.adapters.popularity.StateSureListAdapter;
import grl.com.configuratoin.Constant;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.SelfInfoModel;
import grl.com.configuratoin.Utils;
import grl.com.dataModels.ChallengeModel;
import grl.com.dataModels.TwitterRelModel;
import grl.com.dataModels.UserModel;
import grl.com.httpRequestTask.popularity.ExampleAcceptTask;
import grl.com.httpRequestTask.popularity.ExampleListTask;
import grl.com.httpRequestTask.popularity.PKAcceptTask;
import grl.com.httpRequestTask.popularity.PopularityAllRequestTask;
import grl.com.network.HttpCallback;
import grl.wangu.com.grl.R;

public class PopularStateActivity extends Activity implements View.OnClickListener,
                                            PopularityChallengeAdapter.OnItemOnClickListener,
                                            StateSureListAdapter.OnItemOnClickListener{

    private String estName;
    private String userId;

    RecyclerView pkListView;
    StatePKListAdapter pkListAdapter;

    RecyclerView exampleListView;
    StateExampleListAdapter exampleListAdapter;

    RecyclerView sureListView;
    StateSureListAdapter sureListAdapter;

    RecyclerView challengeListView;
    PopularityChallengeAdapter challengeAdapter;

    TextView noPkView;
    TextView noExamView;
    TextView noSureView;
    TextView noChallengeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popular_state);
        Intent intent = getIntent();
        estName = intent.getStringExtra("est_name");
        userId = SelfInfoModel.userID;

        getViewByID();
        setOnListener();
        initNavBar();
        initializeData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    public void getViewByID () {
        pkListView = (RecyclerView)findViewById(R.id.pk_list);
        exampleListView = (RecyclerView)findViewById(R.id.example_list);
        sureListView = (RecyclerView)findViewById(R.id.sure_list);
        challengeListView = (RecyclerView)findViewById(R.id.challenge_list);

        noPkView = (TextView)findViewById(R.id.tv_no_request);
        noExamView = (TextView)findViewById(R.id.tv_no_example);
        noSureView = (TextView)findViewById(R.id.tv_no_sure);
        noChallengeView = (TextView)findViewById(R.id.tv_no_challenge);

    }

    public void setOnListener() {
        findViewById(R.id.img_back).setOnClickListener(this);
        findViewById(R.id.txt_left).setOnClickListener(this);

    }

    public void initNavBar() {
        ((ImageView)findViewById(R.id.img_back)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_left)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_left)).setText(this.getString(R.string.user_nav_back));
        ((TextView)findViewById(R.id.txt_title)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_title)).setText(this.getString(R.string.menu_example_list));
    }

    public void initializeData () {
        exampleListAdapter = new StateExampleListAdapter(this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        exampleListView.setLayoutManager(mLayoutManager);
        exampleListView.setItemAnimator(new DefaultItemAnimator());
        exampleListView.setAdapter(exampleListAdapter);


        pkListAdapter = new StatePKListAdapter(this);
        RecyclerView.LayoutManager mLayoutManager1 = new LinearLayoutManager(this);
        pkListView.setLayoutManager(mLayoutManager1);
        pkListView.setItemAnimator(new DefaultItemAnimator());
        pkListView.setAdapter(pkListAdapter);

        sureListAdapter = new StateSureListAdapter(this);
        RecyclerView.LayoutManager mLayoutManager2 = new LinearLayoutManager(this);
        sureListView.setLayoutManager(mLayoutManager2);
        sureListView.setItemAnimator(new DefaultItemAnimator());
        sureListView.setAdapter(sureListAdapter);
        sureListAdapter.setItemOnClickListener(this);

        challengeAdapter = new PopularityChallengeAdapter(this);
        RecyclerView.LayoutManager mLayoutManager3 = new LinearLayoutManager(this);
        challengeListView.setLayoutManager(mLayoutManager3);
        challengeListView.setItemAnimator(new DefaultItemAnimator());
        challengeListView.setAdapter(challengeAdapter);
        challengeAdapter.setItemOnClickListener(this);

    }

    public void refresh() {
        new PopularityAllRequestTask(this, userId, estName, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object response) {
                if(!flag || response == null) {                 //failure
                    GlobalVars.showErrAlert(PopularStateActivity.this);
                    return;
                }

                pkListAdapter.myList.clear();;
                pkListAdapter.notifyDataSetChanged();
                exampleListAdapter.myList.clear();;
                exampleListAdapter.notifyDataSetChanged();
                sureListAdapter.myList.clear();;
                sureListAdapter.notifyDataSetChanged();
                challengeAdapter.myList.clear();;
                challengeAdapter.notifyDataSetChanged();


                try {
                    JSONObject result = (JSONObject) response;

                    JSONArray pkJsonArray = result.getJSONArray("pk_list");
                    JSONArray exampleJsonArray = result.getJSONArray("exam_list");
                    JSONArray sureJsonArray = result.getJSONArray("sure_list");
                    JSONArray challengeJsonArray = result.getJSONArray("challenge_list");

                    for (int i = 0; i < pkJsonArray.length(); i++) {
                        JSONObject object = pkJsonArray.getJSONObject(i);
                        TwitterRelModel model = new TwitterRelModel();
                        model.parseFromJson(object);
                        pkListAdapter.myList.add(model);
                    }

                    for (int i = 0; i < exampleJsonArray.length(); i++) {
                        JSONObject object = exampleJsonArray.getJSONObject(i);
                        TwitterRelModel model = new TwitterRelModel();
                        model.parseFromJson(object);
                        exampleListAdapter.myList.add(model);
                    }

                    for (int i = 0; i < sureJsonArray.length(); i++) {
                        JSONObject object = sureJsonArray.getJSONObject(i);
                        sureListAdapter.myList.add(object);
                    }

                    for (int i = 0; i < challengeJsonArray.length(); i++) {
                        JSONObject object = challengeJsonArray.getJSONObject(i);
                        ChallengeModel model = new ChallengeModel();
                        model.parseFromJson(object);
                        model.challengeType = GlobalVars.getIntFromJson(object, "challenge_type");
                        challengeAdapter.myList.add(model);
                    }

                } catch (Exception e) {
                    Log.e("MYAPP", "exception", e);
                }
                exampleListAdapter.notifyDataSetChanged();
                if (exampleListAdapter.myList.size() == 0)
                    noExamView.setVisibility(View.VISIBLE);
                else
                    noExamView.setVisibility(View.GONE);
                pkListAdapter.notifyDataSetChanged();
                if (pkListAdapter.myList.size() == 0)
                    noPkView.setVisibility(View.VISIBLE);
                else
                    noPkView.setVisibility(View.GONE);
                sureListAdapter.notifyDataSetChanged();
                if (sureListAdapter.myList.size() == 0)
                    noSureView.setVisibility(View.VISIBLE);
                else
                    noSureView.setVisibility(View.GONE);
                if (challengeAdapter.myList.size() == 0)
                    noChallengeView.setVisibility(View.VISIBLE);
                else
                    noChallengeView.setVisibility(View.GONE);

                findViewById(R.id.entire_view).invalidate();

            }
        });
    }
    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.img_back:                     // 되올이 단추를 누른 경우
            case R.id.txt_left:
                Utils.finish(this);
                break;
        }
    }

    public void pkClickAction(final int index) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.menu_pk));
        builder.setPositiveButton(getString(R.string.dlg_accept_btn), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sendPKAceept(index, 1);
            }
        });
        builder.setNegativeButton(getString(R.string.dlg_decline_btn), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sendPKAceept(index, 0);
            }
        });

        builder.show();
    }
    public void exampleClickAction(final int index) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.menu_pk));
        builder.setPositiveButton(getString(R.string.dlg_accept_btn), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sendExampleAceept(index, 1);
            }
        });
        builder.setNegativeButton(getString(R.string.dlg_decline_btn), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sendExampleAceept(index, 0);
            }
        });

        builder.show();
    }

    public void sendPKAceept(int index,int value) {
        TwitterRelModel model = pkListAdapter.myList.get(index);
        int position = 0;
        new PKAcceptTask(this, userId, model.userID, estName, value, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object response) {
                if(!flag || response == null) {                 //failure
                    GlobalVars.showErrAlert(PopularStateActivity.this);
                    return;
                }

                try {
                    JSONObject result = (JSONObject) response;
                    Boolean success = GlobalVars.getBooleanFromJson(result, "result");
                    if (success) {
//                        finish();
                    } else {
                        GlobalVars.showErrAlert(PopularStateActivity.this);
                    }

                } catch (Exception e) {

                }
                refresh();
            }
        });
    }

    public void sendExampleAceept(int index, int value) {
        TwitterRelModel model = exampleListAdapter.myList.get(index);
        new ExampleAcceptTask(this, userId, model.userID, estName, model.index, value, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object response) {
                if(!flag || response == null) {                 //failure
                    GlobalVars.showErrAlert(PopularStateActivity.this);
                    return;
                }

                try {
                    JSONObject result = (JSONObject) response;
                    Boolean success = GlobalVars.getBooleanFromJson(result, "result");
                    if (success) {
//                        finish();
                    } else {
                        GlobalVars.showErrAlert(PopularStateActivity.this);
                    }

                } catch (Exception e) {

                }
                refresh();
            }
        });
    }

    @Override
    public void onChallengeClick(int position) {
        // Challenge Click
        ChallengeModel model = challengeAdapter.myList.get(position);
        if (model.challengeState == 0) {
            Utils.start_Activity(this, ChallengeDetailActivity.class,
                    new BasicNameValuePair(Constant.CHALLENGE_ID, model.challengeId),
                    new BasicNameValuePair(Constant.TEACHER_ID, model.fromUserId),
                    new BasicNameValuePair("showType", String.valueOf(model.challengeType)));
        } else {
            Utils.start_Activity(this, ChallengeDetailActivity.class,
                    new BasicNameValuePair(Constant.CHALLENGE_ID, model.challengeId),
                    new BasicNameValuePair(Constant.TEACHER_ID, model.toUserId),
                    new BasicNameValuePair("showType", String.valueOf(model.challengeType)));
        }

    }

    @Override
    public void onSureClick(int position) {
        JSONObject object = sureListAdapter.myList.get(position);
        Utils.start_Activity(PopularStateActivity.this, PopularityDetailActivity.class,
                new BasicNameValuePair("twitter_id", GlobalVars.getStringFromJson(object, "twitter_id")),
                new BasicNameValuePair("user_name", GlobalVars.getStringFromJson(object, "user_name")),
                new BasicNameValuePair("user_photo", GlobalVars.getStringFromJson(object, "user_photo")),
                new BasicNameValuePair("describe_content", GlobalVars.getStringFromJson(object, "describe_content")),
                new BasicNameValuePair("photo_path", GlobalVars.getStringFromJson(object, "photo_path")));
    }
}
