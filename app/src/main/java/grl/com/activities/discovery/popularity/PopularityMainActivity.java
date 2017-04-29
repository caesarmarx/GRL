package grl.com.activities.discovery.popularity;

import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.koushikdutta.async.http.BasicNameValuePair;

import grl.com.activities.discovery.DiscoverySettingActivity;
import grl.com.activities.order.UserRankActivity;
import grl.com.activities.search.GpsSearchActivity;
import grl.com.configuratoin.Utils;
import grl.com.subViews.dialogues.ActionItem;
import grl.com.subViews.dialogues.TitlePopup;
import grl.com.subViews.dialogues.TitlePopup.OnItemOnClickListener;
import grl.com.subViews.fragment.Fragment_chat;
import grl.com.subViews.fragment.Fragment_discovery;
import grl.com.subViews.fragment.Fragment_energy;
import grl.com.subViews.fragment.Fragment_order;
import grl.com.subViews.fragment.popularity.Fragment_my_popularity;
import grl.com.subViews.fragment.popularity.Fragment_teacher_popularity;
import grl.wangu.com.grl.R;

/**
 * Created by Administrator on 6/7/2016.
 */
public class PopularityMainActivity extends FragmentActivity implements View.OnClickListener{


    private TitlePopup titlePopup;

    //tab bars
    private Fragment[] fragments;
    private Fragment_teacher_popularity fragmentTeacher;
    private Fragment_my_popularity fragmentMyPopulrity;

    private ImageView rightImage;
    //tab bar views
    private RadioButton[] radioButtons;

    public String estName;

    private int index;
    private int currentTabIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popularity_main);
        getViewByID();
        initNavBar();
        initPopWindow();
        setOnListener();
        initTabView();
    }

    public void getViewByID () {
        rightImage = (ImageView)findViewById(R.id.img_right);
        rightImage.setImageResource(R.drawable.info_btn);
        rightImage.setVisibility(View.INVISIBLE);

    }

    public void initNavBar() {
        ((ImageView)findViewById(R.id.img_back)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_left)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_left)).setText(this.getString(R.string.user_nav_back));
        ((TextView)findViewById(R.id.txt_title)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_title)).setText(this.getString(R.string.popularity));
    }

    public void setOnListener() {
        findViewById(R.id.img_back).setOnClickListener(this);
        findViewById(R.id.txt_left).setOnClickListener(this);
        rightImage.setOnClickListener(this);
    }

    private void initPopWindow() {
        // 实例化标题栏弹窗
        titlePopup = new TitlePopup(this, LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT, false);
        titlePopup.setItemOnClickListener(onitemClick);

        titlePopup.addAction(new ActionItem(this, getResources().getString(R.string.menu_request_pk)));
        titlePopup.addAction(new ActionItem(this, getResources().getString(R.string.menu_example_list)));
        titlePopup.addAction(new ActionItem(this, getResources().getString(R.string.menu_exmaple_to_me)));
        titlePopup.addAction(new ActionItem(this, getResources().getString(R.string.menu_popular_rank)));
        titlePopup.addAction(new ActionItem(this, getResources().getString(R.string.menu_popular_state)));
    }

    private void initTabView() {
        fragmentTeacher = new Fragment_teacher_popularity();
        fragmentMyPopulrity = new Fragment_my_popularity();
        fragments = new Fragment[] {fragmentTeacher, fragmentMyPopulrity};

        radioButtons = new RadioButton[2];
        radioButtons[0] = (RadioButton)findViewById(R.id.btn_teacher_popularity);
        radioButtons[0] = (RadioButton)findViewById(R.id.btn_my_popularity);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, fragmentTeacher)
                .add(R.id.fragment_container, fragmentMyPopulrity)
                .hide(fragmentMyPopulrity).show(fragmentTeacher).commit();

    }

    public void onTabClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_teacher_popularity:
                rightImage.setVisibility(View.INVISIBLE);
                index = 0;
                break;
            case R.id.btn_my_popularity:
                rightImage.setVisibility(View.VISIBLE);
                index = 1;
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
        currentTabIndex = index;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.img_back:                     // 되올이 단추를 누른 경우
            case R.id.txt_left:
                Utils.finish(this);
                break;
            case R.id.img_right:
                titlePopup.show(findViewById(R.id.top_bar));
                break;
        }
    }

    private OnItemOnClickListener onitemClick = new OnItemOnClickListener() {

        @Override
        public void onItemClick(ActionItem item, int position) {
        // mLoadingDialog.show();
        switch (position) {
            case 0: // PK 요청
                Utils.start_Activity(PopularityMainActivity.this, RequestPKActivity.class,
                        new BasicNameValuePair("est_name", estName));
                break;
            case 1:// 榜样
                Utils.start_Activity(PopularityMainActivity.this, ExampleActivity.class,
                        new BasicNameValuePair("est_name", estName));
                break;
            case 2:// 榜粉
                Utils.start_Activity(PopularityMainActivity.this, MyExampleActivity.class,
                        new BasicNameValuePair("est_name", estName));
                break;
            case 3:// 贵人党
                Utils.start_Activity(PopularityMainActivity.this, PopularUserRankActivity.class,
                        new BasicNameValuePair("est_name", estName));
                break;
            case 4:// 状态
                Utils.start_Activity(PopularityMainActivity.this, PopularStateActivity.class,
                        new BasicNameValuePair("est_name", estName));
                break;
            default:
                break;
        }
        }
    };
}
