package grl.com.adapters.energy;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import grl.com.dataModels.FGroupMemberModel;
import grl.wangu.com.grl.R;

/**
 * Created by Administrator on 6/9/2016.
 */
public class FGroupHorizontalAdapter extends RecyclerView.Adapter<FGroupHorizontalAdapter.MyViewHolder>{

    public List<FGroupMemberModel> myList;
    private Activity context;

    public FGroupHorizontalAdapter(Activity context) {
        this.context = context;
        this.myList = new ArrayList<FGroupMemberModel>();
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_my_fgroup_setting_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        FGroupMemberModel itemModel = this.myList.get(position);
        holder.userName.setText(itemModel.userName);
    }

    @Override
    public int getItemCount() {
        return this.myList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private final ImageView userPhoto;
        private final TextView userName;
        public MyViewHolder(View itemView) {
            super(itemView);

            userPhoto = (ImageView) itemView.findViewById(R.id.img_member_photo);
            userName = (TextView) itemView.findViewById(R.id.txt_member_name);
        }
    }
}
