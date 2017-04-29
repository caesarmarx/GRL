package grl.com.subViews.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.koushikdutta.async.util.HashList;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import grl.com.activities.consult.GiftActivity;
import grl.com.adapters.chat.ConsultUserListAdapter;
import grl.com.configuratoin.Constant;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.SelfInfoModel;
import grl.com.configuratoin.Utils;
import grl.com.configuratoin.dbUtil.DBManager;
import grl.com.configuratoin.dbUtil.MsgUser;
import grl.com.dataModels.ConsultUserModel;
import grl.com.dataModels.MessageModel;
import grl.com.httpRequestTask.chat.ChatInfoTask;
import grl.com.httpRequestTask.chat.UserListTask;
import grl.com.network.HttpCallback;
import grl.wangu.com.grl.R;

/**
 * Created by Administrator on 6/7/2016.
 */
public class Fragment_chat extends Fragment implements ConsultUserListAdapter.OnItemOnLongClickListener, View.OnClickListener{
    private Activity ctx;
    private View layout;

    private TextView infoView;
    private Button giftBtn;
    private RecyclerView userListView;
    private ConsultUserListAdapter userListAdapter;

    private TextView unreadMsgView;

    float price;
    int goodJudge;
    int normalJudge;
    int badJudge;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (layout == null) {
            ctx = this.getActivity();
            layout = ctx.getLayoutInflater().inflate(R.layout.fragment_chat,
                    null);
            initViews();
            initData();
            setOnListener();
        } else {
            ViewGroup parent = (ViewGroup) layout.getParent();
            if (parent != null) {
                parent.removeView(layout);
            }
        }
        return layout;
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    private void initViews() {
        // TODO Auto-generated method stub
        infoView = (TextView)layout.findViewById(R.id.today_result_view);
        giftBtn = (Button)layout.findViewById(R.id.gift_btn);

        userListAdapter = new ConsultUserListAdapter(ctx);
        userListAdapter.setItemOnClickListener(this);

        userListView = (RecyclerView)layout.findViewById(R.id.consult_user_list);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(ctx);
        userListView.setLayoutManager(mLayoutManager);
        userListView.setItemAnimator(new DefaultItemAnimator());
        userListView.setAdapter(userListAdapter);

        unreadMsgView= (TextView)ctx.findViewById(R.id.unread_chat_number);
    }

    private void setOnListener() {
        // TODO Auto-generated method stub
//        layout.findViewById(R.id.txt_pengyouquan).setOnClickListener(this);
        giftBtn.setOnClickListener(this);
      }

    private void initData() {
        // TODO Auto-generated method stub

    }

    public void refresh() {
        new ChatInfoTask(ctx, SelfInfoModel.userID, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object response) {
                if(!flag || response == null) {                 //failure
                    GlobalVars.showErrAlert(ctx);
                    return;
                }
                try {
                    JSONObject result = (JSONObject) response;
                    price = GlobalVars.getDoubleFromJson(result, "disciple_price").floatValue();
                    goodJudge = GlobalVars.getIntFromJson(result, "user_judge_good");
                    normalJudge = GlobalVars.getIntFromJson(result, "user_judge_normal");
                    badJudge = GlobalVars.getIntFromJson(result, "user_judge_bad");
                } catch (Exception e) {
                    Log.e("MYAPP", "exception", e);
                }

            }
        });
        final List<MsgUser> users = DBManager.getAllUsers(SelfInfoModel.userID);
        if (users.size() == 0) return;
//        ArrayList<String> userIds = new ArrayList<String>();
//        for (int i = 0; i < users.size(); i++) {
//            userIds.add(users.get(i).getToUserId());
//        }
        String[] userIds = new String[users.size()];
        for (int i = 0; i < users.size(); i++) {
            userIds[i] = users.get(i).getToUserId();
        }

        new UserListTask(ctx, userIds, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object response) {
                if(!flag || response == null) {                 //failure
                    GlobalVars.showErrAlert(ctx);
                    return;
                }
                userListAdapter.myList.clear();
                try {
                    JSONArray result = (JSONArray) response;
                    int total = 0;
                    int totalUnread = 0;
                    for (int i = 0; i < result.length(); i++) {
                        JSONObject object = result.getJSONObject(i);
                        ConsultUserModel model = new ConsultUserModel();
                        model.parseFromJson(object);
                        MsgUser user = DBManager.getUser(SelfInfoModel.userID, model.userID);
                        if (user != null) {
                            model.msgCount = user.getUnread();
                            totalUnread += model.msgCount;
                            MessageModel lastMsg = DBManager.getLastMessage(SelfInfoModel.userID, model.userID);
                            model.lastMsg = lastMsg.getMsgText();
                            if (lastMsg.getMsgType().compareTo(Constant.MSG_LOCATION_TYPE) == 0) {
                                model.lastMsg = getActivity().getResources().getString(R.string.msg_location_text);
                            }
                            if (lastMsg.getMsgType().compareTo(Constant.MSG_IMAGE_TYPE) == 0) {
                                model.lastMsg = getActivity().getResources().getString(R.string.msg_img_text);
                            }
                            if (lastMsg.getMsgType().compareTo(Constant.MSG_VIDEO_TYPE) == 0) {
                                model.lastMsg = getActivity().getResources().getString(R.string.msg_video_text);
                            }
                            if (lastMsg.getMsgType().compareTo(Constant.MSG_VOICE_TYPE) == 0) {
                                model.lastMsg = getActivity().getResources().getString(R.string.msg_voice_text);
                            }
                            model.lastDate = GlobalVars.getRelativeDate(lastMsg.getSendDate(), "yyyy-MM-dd");
                            total += user.getTime();
                        }
                        userListAdapter.myList.add(model);
                    }


                    if (totalUnread == 0)
                        unreadMsgView.setVisibility(View.INVISIBLE);
                    else {
                        unreadMsgView.setVisibility(View.VISIBLE);
                        unreadMsgView.setText(String.format("%d", totalUnread));
                    }

                    if (total > 0 && total < 60)
                        infoView.setText(String.format("%.1f金条/分钟  %d人1分钟问道", price, result.length()));
                    else
                        infoView.setText(String.format("%.1f金条/分钟  %d人%d分钟问道", price, result.length(), total / 60));

                } catch (Exception ex) {

                }
                userListAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onConsultLongClick(final int position) {
        String[] strArray = {"标为未读", "删除该聊天"};

        final AlertDialog.Builder builder = new AlertDialog.Builder(ctx);

        builder.setItems(strArray, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 1:
                        deleteChatHistory(position);
                        dialog.dismiss();
                        break;
                    case 0:
                        markChatHistory(position);
                        dialog.dismiss();
                        break;
                }
            }
        }).setCancelable(true);
        AlertDialog dialog = builder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.show();
    }

    private void deleteChatHistory(int position) {
        ConsultUserModel model = userListAdapter.myList.get(position);
        DBManager.removeUserHistory(SelfInfoModel.userID, model.userID);
        userListAdapter.myList.remove(position);
        userListAdapter.notifyDataSetChanged();
    }

    private void markChatHistory(int position) {
        ConsultUserModel model = userListAdapter.myList.get(position);
        DBManager.markAsRead(model.userID);
        model.msgCount = 0;
        userListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.gift_btn:
                Utils.start_Activity(ctx, GiftActivity.class);
                break;
        }
    }
}
