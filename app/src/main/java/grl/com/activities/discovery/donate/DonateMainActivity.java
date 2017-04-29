package grl.com.activities.discovery.donate;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import grl.com.configuratoin.Utils;
import grl.com.subViews.fragment.donate.Fragment_donate_help;
import grl.com.subViews.fragment.donate.Fragment_donate_history;
import grl.com.subViews.fragment.donate.Fragment_donate_request;
import grl.wangu.com.grl.R;

/**
 * Created by Administrator on 6/10/2016.
 */
public class DonateMainActivity extends FragmentActivity implements View.OnClickListener {

    private Button[] tabButtons;

    //tab bars
    private Fragment[] fragments;
    private Fragment_donate_help fragmentHelp;
    private Fragment_donate_history fragmentHistory;
    private Fragment_donate_request fragmentRequest;

    private int index;
    private int currentTabIndex;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate_main);

        this.getDataFromIntent();
        this.getViewByID();
        this.initNavBar();
        this.initTabView();
        this.initializeData();
        this.setOnListener();
    }

    public void getDataFromIntent (){

    }

    public void getViewByID () {

    }

    public void initNavBar () {
        ((ImageView)findViewById(R.id.img_back)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_left)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_left)).setText(this.getString(R.string.me_title));
        ((TextView)findViewById(R.id.txt_title)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_title)).setText(this.getString(R.string.donate_title));
    }

    public void initializeData () {
    }
    private void initTabView() {
        tabButtons = new Button[3];
        tabButtons[0] = (Button) findViewById(R.id.btn_donate_request);
        tabButtons[1] = (Button) findViewById(R.id.btn_donate_control);
        tabButtons[2] = (Button) findViewById(R.id.btn_donate_history);
        tabButtons[0].setSelected(true);
        tabButtons[0].setTextColor(this.getResources().getColor(R.color.tab_selected_color));

        fragmentHelp = new Fragment_donate_help();
        fragmentHistory = new Fragment_donate_history();
        fragmentRequest = new Fragment_donate_request();
        fragments = new Fragment[]{fragmentRequest, fragmentHelp, fragmentHistory};

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, fragmentRequest)
                .add(R.id.fragment_container, fragmentHelp)
                .add(R.id.fragment_container, fragmentHistory)
                .hide(fragmentHelp).hide(fragmentHistory)
                .show(fragmentRequest).commit();
    }

    public void setOnListener () {
        findViewById(R.id.img_back).setOnClickListener(this);
        findViewById(R.id.txt_left).setOnClickListener(this);
    }

    // tabBar를 클릭하였을 때의 처리진행
    public void onTabClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_donate_request:
                index = 0;
                break;
            case R.id.btn_donate_control:
                index = 1;
                break;
            case R.id.btn_donate_history:
                index = 2;
                break;
        }

        if (currentTabIndex != index) {
            FragmentTransaction trx = getSupportFragmentManager()
                    .beginTransaction();
            trx.hide(fragments[currentTabIndex]);
            if (!fragments[index].isAdded()) {
                trx.add(R.id.fragment_container, fragments[index]);
            }
            trx.show(fragments[index]).commit();
        }
        tabButtons[currentTabIndex].setSelected(false);
        // 把当前tab设为选中状态
        tabButtons[index].setSelected(true);
        tabButtons[currentTabIndex].setTextColor(this.getResources().getColor(R.color.light_dark_color));
        tabButtons[index].setTextColor(this.getResources().getColor(R.color.dark_gray_color));
        currentTabIndex = index;
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
}
