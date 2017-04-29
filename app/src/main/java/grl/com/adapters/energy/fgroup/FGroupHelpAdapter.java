package grl.com.adapters.energy.fgroup;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.koushikdutta.async.http.BasicNameValuePair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import grl.com.activities.common.ContentShowActivity;
import grl.com.activities.energy.fGroup.FGroupHelpActivity;
import grl.com.configuratoin.Constant;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.SelfInfoModel;
import grl.com.configuratoin.Utils;
import grl.com.httpRequestTask.friendGroup.FriendHelpSolveTask;
import grl.com.listeners.ChoiceFriendListener;
import grl.com.network.HttpCallback;
import grl.com.subViews.dialogues.fGroup.FriendListDialog;
import grl.wangu.com.grl.R;

/**
 * Created by Administrator on 6/24/2016.
 */
public class FGroupHelpAdapter extends RecyclerView.Adapter<FGroupHelpAdapter.MyViewHolder>{
    FGroupHelpActivity context;
    JSONArray myList;
    JSONArray friendList;

    // tasks
    FriendHelpSolveTask friendHelpSolveTask;

    public FGroupHelpAdapter (Activity context) {
        this.context = (FGroupHelpActivity)context;
        this.myList = new JSONArray();
    }

    public void notifyData  (JSONArray helpLst, JSONArray friendLst) {
        this.myList = helpLst;
        this.friendList = friendLst;
        notifyDataSetChanged();
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_fgroup_help_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        if(myList.length() == 0) {
            holder.showEmptyAlert(true);
        } else {
            holder.showEmptyAlert(false);
        }
        try {
            final JSONObject info = this.myList.getJSONObject(position);
            String requesterName = info.getString("from_username") + " 的求助";
            holder.requesterUserName.setText(requesterName);
            String solverID = info.getString("solver_id");
            if(solverID.equals("")) {                               // 방조자가 선정되지 않은 경우
                holder.solverUserName.setText("@");
                holder.solverUserName.setTextColor(context.getResources().getColor(android.R.color.black));
            } else {                                                // 방조자가 선정된 경우
                String helperName = "@" + info.getString("solver_username");
                holder.solverUserName.setText(helperName);
                holder.solverUserName.setTextColor(context.getResources().getColor(R.color.light_gray_color));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // set listener
        holder.requesterUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 도움내용을 보려고할 때의 처리 진행
                try {
                    JSONObject jsonObject = myList.getJSONObject(position);
                    Utils.start_Activity(context, ContentShowActivity.class,
                            new BasicNameValuePair(Constant.BACK_TITLE_ID, context.getString(R.string.fgroup_help_title)),
                            new BasicNameValuePair(Constant.TITLE_ID, holder.requesterUserName.getText().toString()),
                            new BasicNameValuePair(Constant.CONTENT_VIEW_KEY, jsonObject.getString("req_content")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        holder.solverUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 방조자를 선택하려할때의 처리 진행
                FriendListDialog dialog = new FriendListDialog(context, friendList, new ChoiceFriendListener() {
                    @Override
                    public void selectFriend(String userID, String userName) {
                        // 방조자를 선택하였을 때의 처리 진행
                        try {
                            JSONObject jsonObject = myList.getJSONObject(position);
                            if(userID.equals(""))
                                return;
                            JSONObject params = new JSONObject();
                            params.put("help_id", jsonObject.getString("id"));
                            params.put("solver_id", userID);
                            params.put("solver_username", userName);
                            friendHelpSolveTask = new FriendHelpSolveTask(context, params, new HttpCallback() {
                                @Override
                                public void onResponse(Boolean flag, Object Response) throws JSONException {
                                    if(!flag || Response == null) {
                                        GlobalVars.showErrAlert(context);
                                        return;
                                    }
                                    context.initializeData();
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                });
                try {
                    JSONObject jsonObject = myList.getJSONObject(position);
                    String fromUserID = jsonObject.getString("from_userid");
                    String solverID = jsonObject.getString("solver_id");
                    // 본인이 발령한 령에 대하여서만 해령자를 선택할수있다.
                    if(!SelfInfoModel.userID.equals(fromUserID) || !solverID.equals(""))
                        return;

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                dialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        int cnt = this.myList.length();
        if(cnt == 0)
            cnt = 1;
        return cnt;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private final TextView requesterUserName;
        private final TextView solverUserName;
        private final TextView viewEmpty;
        private final View viewBorder;
        private final LinearLayout layoutInfo;
        public MyViewHolder(View itemView) {
            super(itemView);

            requesterUserName = (TextView) itemView.findViewById(R.id.tv_help_request);
            solverUserName = (TextView) itemView.findViewById(R.id.tv_helper_name);
            viewEmpty = (TextView) itemView.findViewById(R.id.tvEmpty);
            viewBorder = (View) itemView.findViewById(R.id.layout_border);
            layoutInfo = (LinearLayout) itemView.findViewById(R.id.layout_user_info);
        }

        public void showEmptyAlert(Boolean flag) {
            if(flag) {
                layoutInfo.setVisibility(View.INVISIBLE);
                viewEmpty.setVisibility(View.VISIBLE);
            } else {
                layoutInfo.setVisibility(View.VISIBLE);
                viewEmpty.setVisibility(View.GONE);
            }
        }
    }
}
