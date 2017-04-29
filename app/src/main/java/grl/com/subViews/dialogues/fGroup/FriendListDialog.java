package grl.com.subViews.dialogues.fGroup;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import grl.com.configuratoin.GlobalVars;
import grl.com.listeners.ChoiceFriendListener;
import grl.wangu.com.grl.R;

/**
 * Created by Administrator on 6/24/2016.
 */
public class FriendListDialog extends Dialog {
    // view
    RecyclerView recyclerView;

    //values
    FriendListAdapter recyclerAdapter;
    Context context;
    JSONArray friendArray;
    ChoiceFriendListener callBack;

    public FriendListDialog(Context context, JSONArray friendArray, ChoiceFriendListener listener) {
        super(context);

        this.context = context;
        this.friendArray = friendArray;
        this.callBack = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("好友列表");
        setContentView(R.layout.layout_friend_list_view);

        this.getViewByID();
        this.initializeData();
    }

    public void getViewByID() {
        recyclerView = (RecyclerView)findViewById(R.id.recycler_view);

    }

    public void initializeData () {
        // init values
        // set up recycler view
        recyclerAdapter = new FriendListAdapter(context);
        RecyclerView.LayoutManager mLayoutManagerTop = new LinearLayoutManager(context.getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManagerTop);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(recyclerAdapter);

        recyclerAdapter.notifyData(friendArray);
    }

    public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.MyViewHolder>{

        Context context;
        JSONArray myList;

        public FriendListAdapter(Context context) {
            this.context = context;
            myList = new JSONArray();
        }

        public void notifyData(JSONArray temp) {
            myList = temp;
            notifyDataSetChanged();
        }
        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_friend_list_row, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            if(myList.length() == 0) {
                holder.showEmptyAlert(true);
                return;
            } else {
                holder.showEmptyAlert(false);
            }
            try {
                JSONObject jsonObject = myList.getJSONObject(position);
                holder.tvUserName.setText(jsonObject.getString("user_name"));
                GlobalVars.loadImage(holder.imgUserPhoto, jsonObject.getString("user_photo"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // 한명을 선택하였을 때의 처리 진행
            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String userID = "";
                    String userName = "";
                    try {
                        JSONObject jsonObject = myList.getJSONObject(position);
                        userID = jsonObject.getString("user_id");
                        userName = jsonObject.getString("user_name");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    callBack.selectFriend(userID, userName);
                    dismiss();
                }
            });
        }

        @Override
        public int getItemCount() {
            int cnt = myList.length();
            if(cnt == 0)
                cnt = 1;
            return cnt;
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            private final ImageView imgUserPhoto;
            private final TextView tvUserName;
            private final LinearLayout layoutView;
            private final TextView tvEmpty;
            private final View view;
            public MyViewHolder(View itemView) {
                super(itemView);

                imgUserPhoto = (ImageView) itemView.findViewById(R.id.img_user_photo);
                tvUserName = (TextView) itemView.findViewById(R.id.tv_user_name);
                tvEmpty = (TextView) itemView.findViewById(R.id.tvEmpty);
                layoutView = (LinearLayout) itemView.findViewById(R.id.layout_view);
                view = itemView;
            }

            public void showEmptyAlert (Boolean flag) {
                if(flag) {
                    tvEmpty.setVisibility(View.VISIBLE);
                    layoutView.setVisibility(View.INVISIBLE);
                } else {
                    tvEmpty.setVisibility(View.GONE);
                    layoutView.setVisibility(View.VISIBLE);
                }
            }
        }
    }
}
