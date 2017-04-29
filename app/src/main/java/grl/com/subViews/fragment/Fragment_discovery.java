package grl.com.subViews.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import grl.com.activities.discovery.crash.CrashActivity;
import grl.com.activities.discovery.order.MyOrderActivity;
import grl.com.activities.discovery.popularity.PopularityMainActivity;
import grl.com.configuratoin.Utils;
import grl.wangu.com.grl.R;

/**
 * Created by Administrator on 6/7/2016.
 */
public class Fragment_discovery extends Fragment implements View.OnClickListener{
    Activity ctx;
    View layout;

    private TextView orderView;
    private TextView popularView;
    private TextView crashView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (layout == null) {
            ctx = this.getActivity();
            layout = ctx.getLayoutInflater().inflate(R.layout.fragment_discovery,
                    null);
            initViews();
            initData();
            setOnListener();
        } else {
            ViewGroup parent = (ViewGroup) layout.getParent();
            if (parent != null) {
                parent.removeView(layout);
            }
        }
        return layout;
    }
    private void initViews() {
        // TODO Auto-generated method stub
        orderView = (TextView)layout.findViewById(R.id.tv_order);
        popularView = (TextView)layout.findViewById(R.id.tv_cheer);
        crashView = (TextView)layout.findViewById(R.id.tv_crash);
    }

    private void setOnListener() {
        // TODO Auto-generated method stub
        orderView.setOnClickListener(this);
        popularView.setOnClickListener(this);
        crashView.setOnClickListener(this);
    }

    private void initData() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_order:                                           // 나의 령보기 단추
                Utils.start_Activity(ctx, MyOrderActivity.class);
                break;
            case R.id.tv_cheer:                                            // 나의 인기보기 단추
                Utils.start_Activity(ctx, PopularityMainActivity.class);
                break;
            case R.id.tv_crash:                                             // 부딪치기 단추
                Utils.start_Activity(ctx, CrashActivity.class);
                break;
        }
    }

}
