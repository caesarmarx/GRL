package grl.com.adapters.chat;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.async.http.BasicNameValuePair;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import grl.com.activities.consult.ChatActivity;
import grl.com.configuratoin.Constant;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.Utils;
import grl.com.dataModels.ConsultUserModel;
import grl.com.dataModels.EmoticonModel;
import grl.wangu.com.grl.R;

/**
 * Created by Administrator on 6/8/2016.
 */

public class GiftListAdapter extends RecyclerView.Adapter<GiftListAdapter.MyViewHolder> {

    public JSONArray myList;
    private Activity ctx;

    public GiftListAdapter(Activity context) {
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
            holder.textView.setText(GlobalVars.getStringFromJson(object, "user_name"));
            String emoticonName = GlobalVars.getStringFromJson(object, "gift_name");
            EmoticonModel model = GlobalVars.getEmoticon(emoticonName);
            holder.imageView.setImageResource(model.resId);
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
