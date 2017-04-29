package grl.com.adapters.energy.fgroup;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;

import grl.com.configuratoin.GlobalVars;
import grl.wangu.com.grl.R;

/**
 * Created by Administrator on 6/9/2016.
 */
public class FGroupHorizontalAdapter extends RecyclerView.Adapter<FGroupHorizontalAdapter.MyViewHolder>{

    Activity context;
    JsonArray myList;
    JsonArray exitUsers;

    public FGroupHorizontalAdapter(Activity context) {
        this.context = context;
        this.myList = new JsonArray();
        this.exitUsers = new JsonArray();
    }

    public void notifyData(JSONArray jsonArray) {
        this.myList = (JsonArray) new JsonParser().parse(jsonArray.toString());
        notifyDataSetChanged();
    }

    public JsonArray getExitUsers() {
        return this.exitUsers;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_my_fgroup_setting_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final JsonObject obj = this.myList.get(position).getAsJsonObject();
        String userName = obj.get("user_name").getAsString();
        String userPhoto = obj.get("user_photo").getAsString();
        holder.userName.setText(userName);
        GlobalVars.loadImage(holder.userPhoto, userPhoto);

        // 항목을 설정하였을 때의 처리 진행
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.isSelected = !holder.isSelected;
                String userId = obj.get("user_id").getAsString();
                JsonObject element = new JsonObject();
                element.addProperty("user_id", userId);
                if(holder.isSelected) {                 // 선택되였다면
                    exitUsers.add(element);
                    holder.view.setBackgroundColor(context.getResources().getColor(android.R.color.holo_red_light));
                } else {                                // 선택해제되였다면
                    exitUsers.remove(element);
                    holder.view.setBackgroundColor(context.getResources().getColor(android.R.color.white));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.myList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private final ImageView userPhoto;
        private final TextView userName;
        private final View view;
        private Boolean isSelected;
        public MyViewHolder(View itemView) {
            super(itemView);

            userPhoto = (ImageView) itemView.findViewById(R.id.img_member_photo);
            userName = (TextView) itemView.findViewById(R.id.txt_member_name);
            view = itemView;
            isSelected = false;
        }
    }
}
