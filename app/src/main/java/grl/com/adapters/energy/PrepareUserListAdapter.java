package grl.com.adapters.energy;

import android.app.Activity;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import grl.com.configuratoin.GlobalVars;
import grl.com.dataModels.UserModel;
import grl.com.subViews.fragment.prepare.Fragment_enter;
import grl.wangu.com.grl.R;

/**
 * Created by Administrator on 6/8/2016.
 */

public class PrepareUserListAdapter extends RecyclerView.Adapter<PrepareUserListAdapter.MyViewHolder> {

    public List<UserModel> myList;
    private Activity ctx;
    private Fragment_enter parent;
    private boolean bNumberShow = false;

    private int selectedPos = 0;

    public PrepareUserListAdapter(Activity context, Fragment_enter fragment) {
        this.ctx = context;
        this.parent = fragment;
        this.myList = new ArrayList<UserModel>();
    }

    public void showNumber() {
        bNumberShow = true;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_tgroup_enter_row, parent, false);
        return new MyViewHolder(itemView);
    }

    public void toggleSelection(int pos) {
        selectedPos = pos;
        notifyDataSetChanged();
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        UserModel itemModel = this.myList.get(position);

        if (selectedPos == position) {
            holder.itemView.setActivated(true);
        } else {
            holder.itemView.setActivated(false);
        }
        holder.userNameView.setText(itemModel.userName);
        GlobalVars.loadImage(holder.userPhotoView, itemModel.userPhoto);

        // set onClickListener
        holder.itemView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                toggleSelection(position);
                parent.userClicked(position);
            }
        });

    }


    @Override
    public int getItemCount() {
        return this.myList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private final ImageView userPhotoView;
        private final TextView userNameView;

        public MyViewHolder(View itemView) {
            super(itemView);
            userPhotoView = (ImageView) itemView.findViewById(R.id.img_user_photo);
            userNameView = (TextView) itemView.findViewById(R.id.txt_user_name);
        }
    }
}
