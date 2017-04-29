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

import grl.com.activities.MainActivity;
import grl.com.activities.energy.tGroup.TGroupActivity;
import grl.com.activities.energy.tGroup.TGroupDetailActivity;
import grl.com.activities.otherRel.OtherMainActivity;
import grl.com.configuratoin.Constant;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.Utils;
import grl.wangu.com.grl.R;
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;

/**
 * Created by Administrator on 6/7/2016.
 */
public class TGroupViewAdapter extends StatelessSection{

    public Activity context;
    public JSONArray myList;
    public String groupTitle;
    public String groupHeaderPhoto;
    public String tgroupID;

    public TGroupViewAdapter(Activity context) {
        super(R.layout.layout_tgroup_section, R.layout.layout_none,  R.layout.layout_tgroup_row);
        this.context = context;
        this.myList = new JSONArray();
        this.groupTitle = "";
        this.tgroupID = "";
    }

    @Override
    public int getContentItemsTotal() {
        int cnt = myList.length();
        if(cnt > 3)
            return 4;
        if(cnt == 0)
            return 1;
        return cnt;
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new MyItemViewHolder(view);
    }

    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
//        return super.getHeaderViewHolder(view);
        return  new MyHeaderViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
        super.onBindHeaderViewHolder(holder);

        MyHeaderViewHolder itemHolder = (MyHeaderViewHolder) holder;
        itemHolder.txtUserName.setText(this.groupTitle);
        if(!GlobalVars.loadImage(itemHolder.imgPhoto, groupHeaderPhoto))
            itemHolder.imgPhoto.setImageDrawable(context.getResources().getDrawable(R.drawable.default_image));

        // 해당 스승제자계를 선택할 때 의 사건처리(스승제자계창으로 이행한다.)
        itemHolder.view.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                // 사건처리 부분
                Utils.start_Activity(context, TGroupActivity.class,
                        new BasicNameValuePair(Constant.TGROUP_ID, tgroupID),
                        new BasicNameValuePair(Constant.TGROUP_NAME, groupTitle),
                        new BasicNameValuePair(Constant.TGROUP_PHOTO, groupHeaderPhoto));
            }
        });
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final MyItemViewHolder itemHolder = (MyItemViewHolder)holder;
        //bind your view here
        if(myList.length() == 0) {
            itemHolder.tvEmpty.setVisibility(View.VISIBLE);
            return;
        } else {
            itemHolder.tvEmpty.setVisibility(View.GONE);
        }
        try {
            JSONObject jsonObj = this.myList.getJSONObject(position);
            String relType = "";
            if(jsonObj.get("type") != null) {
                Integer type = jsonObj.getInt("type");
                if(type == 1 && position == 0) {
                    itemHolder.tvRelType.setVisibility(View.VISIBLE);
                } else {
                    itemHolder.tvRelType.setVisibility(View.INVISIBLE);
                }
            }
            itemHolder.tvRelative.setText(jsonObj.getString("relation_type"));
            itemHolder.tvUserName.setText(jsonObj.getString("user_name"));
            GlobalVars.loadImage(itemHolder.imgUserPhoto, jsonObj.getString("user_photo"));
//            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this.context)
//                    .discCacheFileNameGenerator(new HashCodeFileNameGenerator())
//                    .build();
//            DisplayImageOptions options = new DisplayImageOptions.Builder()
//                    .displayer(new RoundedBitmapDisplayer(20))
//                    .cacheInMemory(true)
//                    .cacheOnDisk(true)
//                    .build();
//            ImageLoader imageLoader = ImageLoader.getInstance();
//            imageLoader.init(config);
//            imageLoader.displayImage(GlobalVars.SEVER_ADDR + "/" + jsonObj.getString("user_photo"), itemHolder.imgUserPhoto);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // view more detail cell
        if(position == 3) {
            itemHolder.tvMore.setVisibility(View.VISIBLE);
        } else {
            itemHolder.tvMore.setVisibility(View.GONE);
        }
        // 항목을 선택하였을 때의 처리 진행
        itemHolder.view.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                // 제3인자창으로 이행한다.
                String strBackTitle = "";
                if(context instanceof MainActivity){
                    strBackTitle = context.getString(R.string.tab_energy_title);
                }
                try {
                    JSONObject obj = myList.getJSONObject(position);
                    String user_id = obj.getString("user_id");
                    String user_photo = obj.getString("user_photo");
                    Utils.start_Activity(context, OtherMainActivity.class,
                            new BasicNameValuePair(Constant.TITLE_ID, itemHolder.tvUserName.getText().toString()),
                            new BasicNameValuePair(Constant.BACK_TITLE_ID, strBackTitle),
                            new BasicNameValuePair("user_id", user_id),
                            new BasicNameValuePair("user_photo", user_photo));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        // 더보기 항목을 선택하였을 때의 처리진행
        itemHolder.tvMore.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                // 상세창으로 이행한다.
                Utils.start_Activity(context, TGroupDetailActivity.class, new BasicNameValuePair(Constant.TITLE_ID, itemHolder.tvUserName.getText().toString()),
                        new BasicNameValuePair("arrayData", myList.toString()));
            }
        });
    }

    // custom cell
    class MyItemViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvRelative;
        private final TextView tvRelType;
        private final TextView tvUserName;
        private final ImageView imgUserPhoto;
        private final TextView tvMore;
        private final TextView tvEmpty;
        private final View view;

        public MyItemViewHolder(View itemView) {
            super(itemView);

            tvRelative = (TextView) itemView.findViewById(R.id.txt_relative);
            tvRelType = (TextView) itemView.findViewById(R.id.rel_type);
            tvUserName = (TextView) itemView.findViewById(R.id.txt_member_name);
            imgUserPhoto = (ImageView) itemView.findViewById(R.id.user_photo);
            tvMore = (TextView) itemView.findViewById(R.id.tvMore);
            tvEmpty = (TextView) itemView.findViewById(R.id.tvEmpty);
            view = itemView;
        }
    }

    // custom Header
    class MyHeaderViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imgPhoto;
        private final TextView txtUserName;
        private final View view;
        public MyHeaderViewHolder(View itemView) {
            super(itemView);

            imgPhoto = (ImageView) itemView.findViewById(R.id.img_photo);
            txtUserName = (TextView) itemView.findViewById(R.id.txt_tgroup_section);
            view = itemView;
        }
    }
}