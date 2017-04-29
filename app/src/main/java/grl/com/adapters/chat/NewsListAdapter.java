package grl.com.adapters.chat;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import grl.com.configuratoin.GlobalVars;
import grl.com.dataModels.EmoticonModel;
import grl.wangu.com.grl.R;

/**
 * Created by Administrator on 6/8/2016.
 */

public class NewsListAdapter extends RecyclerView.Adapter<NewsListAdapter.MyViewHolder> {

    public JSONArray myList;
    private Activity ctx;

    public NewsListAdapter(Activity context) {
        this.ctx = context;
        this.myList = new JSONArray();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_user_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        try {
            JSONObject object = this.myList.getJSONObject(position);
            String description = "";
            String userName = GlobalVars.getStringFromJson(object, "user_name");
            if (!GlobalVars.getStringFromJson(object, "fgroup_id").isEmpty()) {
                description = String.format("%s 邀请你加入他的贵人圈等", userName);
            }
            if (GlobalVars.getIntFromJson(object, "request_disciple") == 0) {
                description = String.format("%s 拜你为徒弟", userName);
            }
            if (GlobalVars.getIntFromJson(object, "request_teacher") == 0) {
                description = String.format("%s 拜你为师父", userName);
            }
            if (GlobalVars.getIntFromJson(object, "request_grl") == 0) {
                description = String.format("%s 做贵人等提醒", userName);
            }
            if (description.isEmpty())
                description = userName;

            holder.textView.setText(description);
            String photoName = GlobalVars.getStringFromJson(object, "user_photo");
            GlobalVars.loadImage(holder.imageView, photoName);
        } catch (Exception ex) {

        }
        // set onClickListener
        holder.itemView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                // 해당 대화창으로 이행

            }
        });

    }

    @Override
    public int getItemCount() {
        return this.myList.length();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imageView;
        private final TextView textView;


        public MyViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.img_user_photo);
            textView = (TextView) itemView.findViewById(R.id.tv_user_name);
        }
    }

}
