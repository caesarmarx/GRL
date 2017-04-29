package grl.com.activities.energy.tGroup;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import grl.com.configuratoin.Constant;
import grl.com.configuratoin.Utils;
import grl.com.subViews.fragment.prepare.Fragment_enter;
import grl.com.subViews.fragment.prepare.Fragment_material;
import grl.com.subViews.fragment.prepare.Fragment_rule;
import grl.com.subViews.fragment.prepare.Fragment_time;
import grl.wangu.com.grl.R;

/**
 * Created by Administrator on 6/7/2016.
 */
public class TGroupPrepareActivity extends FragmentActivity implements View.OnClickListener{
    ImageView imgBack;
    TextView textBack;
    TextView textTitle;

    public String teacherId;

    private Integer currentTabIndex = 0;
    //tab bars
    private Fragment[] fragments;
    private Button[] tabButtons;

    public Fragment_enter fragmentEnter;
    private Fragment_rule fragmentRule;
    private Fragment_material fragmentMaterial;
    private Fragment_time fragmentTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tgroup_prepare);

        Intent intent = getIntent();
        teacherId = intent.getStringExtra(Constant.TGROUP_ID);
        initViews();
        initTabView();

    }

    private void initViews() {
        // 设置消息页面为初始页面
        imgBack = (ImageView)findViewById(R.id.img_back);
        textBack = (TextView)findViewById(R.id.txt_left);
        textBack.setText(getResources().getText(R.string.user_nav_back));
        textTitle = (TextView)findViewById(R.id.txt_title);
        textTitle.setText(getResources().getString(R.string.tgroup_prepare_title));
        imgBack.setVisibility(View.VISIBLE);
        textBack.setVisibility(View.VISIBLE);

        imgBack.setOnClickListener(this);
        textBack.setOnClickListener(this);
    }

    private void initTabView() {
        fragmentEnter = new Fragment_enter();
        fragmentRule = new Fragment_rule();
        fragmentMaterial = new Fragment_material();
        fragmentTime = new Fragment_time();
        fragments = new Fragment[] { fragmentEnter, fragmentRule,
                fragmentMaterial, fragmentTime };
        tabButtons = new Button[4];
        tabButtons[0] = (Button)findViewById(R.id.tv_prepare_enter);
        tabButtons[1] = (Button) findViewById(R.id.tv_prepare_rule);
        tabButtons[2] = (Button)findViewById(R.id.tv_prepare_material);
        tabButtons[3] = (Button)findViewById(R.id.tv_prepare_time);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, fragmentEnter)
                .add(R.id.fragment_container, fragmentRule)
                .add(R.id.fragment_container, fragmentMaterial)
                .add(R.id.fragment_container, fragmentTime)
                .hide(fragmentRule).hide(fragmentMaterial)
                .hide(fragmentTime).show(fragmentEnter).commit();
    }

    public void onTabClicked(View view) {
        Integer index = 0;
        switch (view.getId()) {
            case R.id.tv_prepare_enter:
                index = 0;
                break;
            case R.id.tv_prepare_rule:
                index = 1;
                break;
            case R.id.tv_prepare_material:
                index = 2;
                break;
            case R.id.tv_prepare_time:
                index = 3;
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
        for (int i = 0; i < 4; i++) {
            tabButtons[i].setTextColor(getResources().getColor(R.color.light_dark_color));
        }
        tabButtons[index].setTextColor(getResources().getColor(R.color.dark_gray_color));
        currentTabIndex = index;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Fragment_material.REQUEST_PIC_SEL) {
            fragmentMaterial.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:
                Utils.finish(this);
                break;
            case R.id.txt_left:
                Utils.finish(this);
                break;
        }
    }

}
