package grl.com.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import grl.com.activities.consult.ChatActivity;
import grl.com.configuratoin.Utils;
import grl.com.dataModels.ConsultUserModel;
import grl.wangu.com.grl.R;

/**
 * Created by Administrator on 6/8/2016.
 */

public class ConsultUserListAdapter extends RecyclerView.Adapter<ConsultUserListAdapter.MyViewHolder> {

    public List<ConsultUserModel> myList;
    private Activity ctx;
    public ConsultUserListAdapter(Activity context) {
        this.ctx = context;
        this.myList = new ArrayList<ConsultUserModel>();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_consult_user_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        if (position == 0) {
            holder.userPhotoView.setImageResource(R.drawable.news_icon);
            holder.userNameView.setText("消息通知");
        } else {
            int index = position - 1;
            ConsultUserModel itemModel = this.myList.get(index);
            holder.userNameView.setText(itemModel.userName);
        }

        // set onClickListener
        holder.itemView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                // 해당 대화창으로 이행
                Utils.start_Activity(ctx, ChatActivity.class);
            }
        });

    }


    @Override
    public int getItemCount() {
        return this.myList.size() + 1;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private final ImageView userPhotoView;
        private final TextView userNameView;
        private final TextView lastMsgView;
        private final TextView lastDateView;
        private final TextView msgCountView;


        public MyViewHolder(View itemView) {
            super(itemView);
            userPhotoView = (ImageView) itemView.findViewById(R.id.img_user_photo);
            userNameView = (TextView) itemView.findViewById(R.id.tv_user_name);
            lastMsgView = (TextView) itemView.findViewById(R.id.tv_last_msg);
            lastDateView = (TextView) itemView.findViewById(R.id.tv_last_date);
            msgCountView = (TextView) itemView.findViewById(R.id.tv_msg_count);
        }
    }
}
