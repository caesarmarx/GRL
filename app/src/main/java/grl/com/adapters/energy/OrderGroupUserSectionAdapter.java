package grl.com.adapters.energy;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.async.http.BasicNameValuePair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import grl.com.activities.otherRel.OtherMainActivity;
import grl.com.configuratoin.Constant;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.Utils;
import grl.wangu.com.grl.R;
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;

/**
 * Created by Administrator on 6/8/2016.
 */
public class OrderGroupUserSectionAdapter extends StatelessSection {

    public JSONArray myList;
    private Activity context;
    private Integer sectionType;
    public OrderGroupUserSectionAdapter(Activity context, Integer type) {
        super(R.layout.layout_order_group_section, R.layout.layout_none, R.layout.layout_default_row);
        this.context = context;
        this.sectionType = type;
        this.myList = new JSONArray();
    }

    @Override
    public int getContentItemsTotal() {
        Integer cnt = this.myList.length();
        if(cnt == 0)
            cnt = 1;
        return cnt;
    }

    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
        return new MySectionViewHolder(view);
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new MyItemViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
        super.onBindHeaderViewHolder(holder);

        MySectionViewHolder sectionView = (MySectionViewHolder) holder;
        String strTitle = "";
        switch (this.sectionType) {
            case 0:
                strTitle = context.getString(R.string.order_group_first_section_title);
                break;
            case 1:
                strTitle = context.getString(R.string.order_group_second_section_title);
                break;
            case 2:
                strTitle = context.getString(R.string.order_group_third_section_title);
                break;
        }
        sectionView.sectionTitle.setText(strTitle);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        final MyItemViewHolder itemHolder = (MyItemViewHolder) holder;
        String userID = "";
        String userPhoto = "";
        // bind your view here
        if(this.myList.length() == 0){
            itemHolder.emptyData(true);
            return;
        } else {
            itemHolder.emptyData(false);
        }
        try {
            JSONObject jsonObject = this.myList.getJSONObject(position);
            userID = jsonObject.getString("user_id");
            userPhoto = jsonObject.getString("user_photo");
            itemHolder.userName.setText(jsonObject.getString("user_name"));
            GlobalVars.loadImage(itemHolder.userPhoto, userPhoto);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String finalUserID = userID;
        final String finalUserPhoto = userPhoto;
        itemHolder.view.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                // 항목을 선택한 경우 3인자창으로 이행한다.
                Utils.start_Activity(context, OtherMainActivity.class, new BasicNameValuePair(Constant.BACK_TITLE_ID, context.getString(R.string.order_plate_title)),
                        new BasicNameValuePair(Constant.TITLE_ID, itemHolder.userName.getText().toString()),
                        new BasicNameValuePair("user_id", finalUserID),
                        new BasicNameValuePair("user_photo", finalUserPhoto));
            }
        });

        // 경계선 겹치는 문제 처리 진행
        if(this.sectionType == 0 || this.sectionType == 1) {
            if(position == this.myList.length() - 1) {
                itemHolder.viewBorder.setVisibility(View.GONE);
            } else {
                itemHolder.viewBorder.setVisibility(View.VISIBLE);
            }
        }
    }

    class MyItemViewHolder extends RecyclerView.ViewHolder {

        private final TextView userName;
        private final ImageView userPhoto;
        private final TextView tvEmpty;
        private final View viewBorder;
        private final View view;

        public MyItemViewHolder(View itemView) {
            super(itemView);

            userName = (TextView) itemView.findViewById(R.id.txt_user_name);
            userPhoto = (ImageView) itemView.findViewById(R.id.img_user_photo);
            tvEmpty = (TextView) itemView.findViewById(R.id.tvEmpty);
            viewBorder = (View) itemView.findViewById(R.id.view_border);
            view = itemView;
        }

        public void emptyData(Boolean flag) {
            if(flag) {
                userName.setVisibility(View.INVISIBLE);
                userPhoto.setVisibility(View.INVISIBLE);
                tvEmpty.setVisibility(View.VISIBLE);
            } else {
                userName.setVisibility(View.VISIBLE);
                userPhoto.setVisibility(View.VISIBLE);
                tvEmpty.setVisibility(View.GONE);
            }
        }
    }

    class MySectionViewHolder extends RecyclerView.ViewHolder {
        private final TextView sectionTitle;

        public MySectionViewHolder(View itemView) {
            super(itemView);

            sectionTitle = (TextView) itemView.findViewById(R.id.section_title);
        }
    }
}
