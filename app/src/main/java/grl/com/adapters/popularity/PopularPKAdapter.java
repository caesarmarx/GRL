package grl.com.adapters.popularity;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import grl.com.dataModels.UserModel;
import grl.wangu.com.grl.R;

/**
 * Created by Administrator on 6/8/2016.
 */

public class PopularPKAdapter extends RecyclerView.Adapter<PopularPKAdapter.MyViewHolder> {

    public List<UserModel> myList;
    private Activity ctx;
    public PopularPKAdapter(Activity context) {
        this.ctx = context;
        this.myList = new ArrayList<UserModel>();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_popularity_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        UserModel model = this.myList.get(position);

        holder.userNameView.setText(model.userName);

        // set onClickListener
        holder.itemView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {


            }
        });

    }


    @Override
    public int getItemCount() {
        if (myList == null)
            return 0;
        return this.myList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private final ImageView userPhotoView;
        private final TextView userNameView;
        private final LinearLayout photoLayout;
        private final TextView descriptionView;
        private final TextView timeView;
        private final ImageView imageView;
        private final ImageView operationView;

        public MyViewHolder(View itemView) {
            super(itemView);
            userPhotoView = (ImageView) itemView.findViewById(R.id.img_user_photo);
            userNameView = (TextView) itemView.findViewById(R.id.tv_user_name);
            descriptionView = (TextView) itemView.findViewById(R.id.tv_description);
            photoLayout = (LinearLayout) itemView.findViewById(R.id.layout_photo);
            imageView = (ImageView) itemView.findViewById(R.id.img_photo);
            timeView = (TextView) itemView.findViewById(R.id.tv_time);
            operationView = (ImageView) itemView.findViewById(R.id.img_operation);
        }
    }
}
