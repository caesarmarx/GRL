package grl.com.adapters.order;

import android.app.Activity;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.async.http.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

import grl.com.activities.consult.ChatActivity;
import grl.com.activities.otherRel.OtherMainActivity;
import grl.com.configuratoin.Constant;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.Utils;
import grl.com.dataModels.ConsultUserModel;
import grl.com.dataModels.UserModel;
import grl.wangu.com.grl.R;

/**
 * Created by Administrator on 6/8/2016.
 */

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.MyViewHolder> {

    public List<UserModel> myList;
    private Activity ctx;
    private boolean bNumberShow = false;
    public UserListAdapter(Activity context) {
        this.ctx = context;
        this.myList = new ArrayList<UserModel>();
    }

    public void showNumber() {
        bNumberShow = true;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_user_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        UserModel itemModel = this.myList.get(position);

        holder.numberView.setText(String.format("%d", position + 1));
        if (bNumberShow) {
            if (position <= 2)
                holder.numberView.setTextColor(Color.RED);
            else
                holder.numberView.setTextColor(Color.BLACK);
            holder.numberView.setVisibility(View.VISIBLE);
        } else
            holder.numberView.setVisibility(View.GONE);

        holder.userNameView.setText(itemModel.userName);
        GlobalVars.loadImage(holder.userPhotoView, itemModel.userPhoto);

        holder.userStateView.setText(itemModel.state); // 상태

        // set onClickListener
        holder.itemView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                // 3인자창
                UserModel itemModel = myList.get(position);
                Utils.start_Activity(ctx, OtherMainActivity.class,
                        new BasicNameValuePair(Constant.BACK_TITLE_ID, ctx.getString(R.string.user_nav_back)),
                        new BasicNameValuePair(Constant.TITLE_ID, itemModel.userName),
                        new BasicNameValuePair(Constant.USER_ID, itemModel.userID),
                        new BasicNameValuePair(Constant.USER_NAME, itemModel.userName),
                        new BasicNameValuePair(Constant.USER_PHOTO, itemModel.userPhoto));

            }
        });

    }


    @Override
    public int getItemCount() {
        return this.myList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private final TextView numberView;
        private final ImageView userPhotoView;
        private final TextView userNameView;
        private final TextView userStateView;

        public MyViewHolder(View itemView) {
            super(itemView);
            userPhotoView = (ImageView) itemView.findViewById(R.id.img_user_photo);
            userNameView = (TextView) itemView.findViewById(R.id.tv_user_name);
            numberView = (TextView) itemView.findViewById(R.id.tv_number);
            userStateView = (TextView) itemView.findViewById(R.id.tv_user_state);
        }
    }
}
