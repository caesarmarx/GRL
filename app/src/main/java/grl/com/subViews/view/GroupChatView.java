package grl.com.subViews.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.EMError;
import com.justalk.cloud.lemon.MtcApi;
import com.justalk.cloud.lemon.MtcConstants;
import com.justalk.cloud.lemon.MtcIm;
import com.justalk.cloud.lemon.MtcImConstants;
import com.justalk.cloud.lemon.MtcUeDb;
import com.justalk.cloud.lemon.MtcUser;
import com.justalk.cloud.lemon.MtcUserConstants;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import grl.com.App;
import grl.com.activities.consult.ChatActivity;
import grl.com.activities.consult.FavouriteActivity;
import grl.com.activities.consult.ForwardActivity;
import grl.com.activities.map.MapSelectActivity;
import grl.com.adapters.chat.EmoticonAdapter;
import grl.com.adapters.chat.MessageAdapter;
import grl.com.adapters.chat.VoicePlayClickListener;
import grl.com.configuratoin.CommonUtils;
import grl.com.configuratoin.Constant;
import grl.com.configuratoin.FileUtils;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.SelfInfoModel;
import grl.com.configuratoin.Utils;
import grl.com.configuratoin.VoiceRecorder;
import grl.com.configuratoin.dbUtil.DBManager;
import grl.com.dataModels.ChatUserModel;
import grl.com.dataModels.ConsultUserModel;
import grl.com.dataModels.EmoticonModel;
import grl.com.dataModels.MessageModel;
import grl.com.httpRequestTask.FileUploadBackgroundTask;
import grl.com.httpRequestTask.FileUploadTask;
import grl.com.network.HttpCallback;
import grl.wangu.com.grl.R;

/**
 * Created by macdev001 on 7/10/16.
 */

public class GroupChatView extends RelativeLayout implements EmoticonAdapter.OnItemClickListener, View.OnClickListener, MessageAdapter.OnItemLongClickListener {

    private Activity mContext;

    private ArrayList<ChatUserModel> receiptUsers;

    public String GROUP_TYPE = "";
    public String GROUP_ID = "";

    private static final int REQUEST_CODE_EMPTY_HISTORY = 2;
    private static final int REQUEST_CODE_MAP = 4;
    public static final int REQUEST_CODE_CAMERA = 18;
    public static final int REQUEST_CODE_LOCAL = 19;
    public static final int REQUEST_CODE_GROUP_DETAIL = 21;
    public static final int REQUEST_CODE_ADD_TO_BLACKLIST = 25;
    public static final int REQUEST_CODE_FAVOURITE = 26;


    private View recordingContainer;
    private ImageView micImage;
    private TextView recordingHint;
    private ListView listView;
    private EditText mEditTextContent;
    private View buttonSetModeKeyboard;
    private View buttonSetModeVoice;
    private View buttonSend;
    private View buttonPressToSpeak;
    private LinearLayout emojiIconContainer;
    private LinearLayout btnContainer;

    private View more;
    private InputMethodManager manager;
    public static Activity activityInstance = null;

    // 给谁发送消息
    private VoiceRecorder voiceRecorder;
    private MessageAdapter adapter;
    private File cameraFile;    // Camera 파일 위치 보관 장소

    private TextView txt_title;
    private ImageView iv_emoticons_normal;
    private ImageView iv_emoticons_checked;
    private RelativeLayout edittext_layout;
    private Button btnMore;
    public String playMsgId;
    private AnimationDrawable animationDrawable;

    private RecyclerView emoticonView;
    private EmoticonAdapter emoticonAdapter;

    private MessageModel model;

    private String nFileType = "";
    ArrayList<MessageModel> msgList;
    String soundLength = "";

    private Handler micImageHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
        }
    };

    // TIME RECORD / EMOTICON SEND COUNT
    private int timeRecord = 0;
    private final int TIME_EST_INTERVAL = 300;
    private int estimateSent = 0;
    private boolean bReceive = false;
    private boolean bSent = false;

    private BroadcastReceiver receiver;

    private BroadcastReceiver mtcImSendingReceiver;
    private BroadcastReceiver mtcImSendOkReceiver;
    private BroadcastReceiver mtcImSendDidFailReceiver;

    public GroupChatView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = (Activity)context;

        LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.layout_group_chat, this);
        initView();
        initializeData();
        setUpView();
        setListener();

        TimeRecordTask recordTask = new TimeRecordTask();
        Timer myTimer = new Timer();
        myTimer.schedule(recordTask, 1000, 1000);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updateChatHistory();
            }
        };
        registerReceivers();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Utils.finish(mContext);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * initView
     */
    protected void initView() {

        recordingContainer = findViewById(R.id.view_talk);
        txt_title = (TextView) findViewById(R.id.txt_title);
        micImage = (ImageView) findViewById(R.id.mic_image);
        animationDrawable = (AnimationDrawable) micImage.getBackground();
        animationDrawable.setOneShot(false);
        recordingHint = (TextView) findViewById(R.id.recording_hint);
        listView = (ListView) findViewById(R.id.list);
        mEditTextContent = (EditText) findViewById(R.id.et_sendmessage);
        buttonSetModeKeyboard = findViewById(R.id.btn_set_mode_keyboard);
        edittext_layout = (RelativeLayout) findViewById(R.id.edittext_layout);
        buttonSetModeVoice = findViewById(R.id.btn_set_mode_voice);
        buttonSend = findViewById(R.id.btn_send);
        buttonPressToSpeak = findViewById(R.id.btn_press_to_speak);

        emojiIconContainer = (LinearLayout) findViewById(R.id.ll_face_container);
        emoticonView = (RecyclerView) findViewById(R.id.emotiocn_view);
        emoticonAdapter = new EmoticonAdapter(mContext);
        emoticonAdapter.setItemOnClickListener(this);
        emoticonView.setItemAnimator(new DefaultItemAnimator());
        emoticonView.setAdapter(emoticonAdapter);

        btnContainer = (LinearLayout) findViewById(R.id.ll_btn_container);
        // locationImgview = (ImageView) findViewById(R.id.btn_location);
        iv_emoticons_normal = (ImageView) findViewById(R.id.iv_emoticons_normal);
        iv_emoticons_checked = (ImageView) findViewById(R.id.iv_emoticons_checked);

        btnMore = (Button) findViewById(R.id.btn_more);
        iv_emoticons_normal.setVisibility(View.VISIBLE);
        iv_emoticons_checked.setVisibility(View.INVISIBLE);
        more = findViewById(R.id.more);
        edittext_layout.setBackgroundResource(R.drawable.input_bar_bg_normal);

        edittext_layout.requestFocus();
        voiceRecorder = new VoiceRecorder(mContext, micImageHandler);
        buttonPressToSpeak.setOnTouchListener(new PressToSpeakListen());
        mEditTextContent.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    edittext_layout
                            .setBackgroundResource(R.drawable.input_bar_bg_active);
                } else {
                    edittext_layout
                            .setBackgroundResource(R.drawable.input_bar_bg_normal);
                }

            }
        });
        mEditTextContent.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                edittext_layout
                        .setBackgroundResource(R.drawable.input_bar_bg_active);
                more.setVisibility(View.GONE);
                iv_emoticons_normal.setVisibility(View.VISIBLE);
                iv_emoticons_checked.setVisibility(View.INVISIBLE);
                emojiIconContainer.setVisibility(View.GONE);
                btnContainer.setVisibility(View.GONE);
                editClick();
            }
        });
        // 监听文字框
        mEditTextContent.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (!TextUtils.isEmpty(s)) {
                    btnMore.setVisibility(View.GONE);
                    buttonSend.setVisibility(View.VISIBLE);
                } else {
                    btnMore.setVisibility(View.VISIBLE);
                    buttonSend.setVisibility(View.GONE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void registerReceivers() {
        mtcImSendingReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                // TODO Auto-generated method stub
                String info = intent.getStringExtra(MtcApi.EXTRA_INFO);
                int cookie = intent.getIntExtra(MtcApi.EXTRA_COOKIE, -1);
                if (cookie >= msgList.size())
                    return;
                MessageModel model = msgList.get(cookie);
                try {
                    JSONObject json = (JSONObject) new JSONTokener(info).nextValue();
                    int progress = json.getInt(MtcImConstants.MtcImProgressKey);
                    model.setProgress(progress);
                    model.setMsgStatus(Constant.MSG_SEND_PROGRESS);
                    DBManager.updateMessageState(Constant.MSG_SEND_PROGRESS, model.getSqlId());
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                adapter.setDateSource(msgList);
                adapter.notifyDataSetChanged();

            }
        };

        mtcImSendOkReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                // TODO Auto-generated method stub
                int cookie = intent.getIntExtra(MtcApi.EXTRA_COOKIE, -1);
                if (cookie >= msgList.size())
                    return;
                MessageModel model = msgList.get(cookie);
                model.setMsgStatus(Constant.MSG_SEND_SUCCESS);
                DBManager.updateMessageState(Constant.MSG_SEND_SUCCESS, model.getSqlId());
                adapter.setDateSource(msgList);
                adapter.notifyDataSetChanged();
            }
        };

        mtcImSendDidFailReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                // TODO Auto-generated method stub
                int cookie = intent.getIntExtra(MtcApi.EXTRA_COOKIE, -1);
                if (cookie >= msgList.size())
                    return;
                MessageModel model = msgList.get(cookie);
                model.setMsgStatus(Constant.MSG_SEND_FAIL);
                DBManager.updateMessageState(Constant.MSG_SEND_FAIL, model.getSqlId());
                adapter.setDateSource(msgList);
                adapter.notifyDataSetChanged();
            }
        };
        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(mContext);
        broadcastManager.registerReceiver(mtcImSendingReceiver, new IntentFilter(MtcImConstants.MtcImSendingNotification));
        broadcastManager.registerReceiver(mtcImSendOkReceiver, new IntentFilter(MtcImConstants.MtcImSendOkNotification));
        broadcastManager.registerReceiver(mtcImSendDidFailReceiver, new IntentFilter(MtcImConstants.MtcImSendDidFailNotification));
    }
    private void unregisterReceivers() {
        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(mContext);
        broadcastManager.unregisterReceiver(mtcImSendingReceiver);
        broadcastManager.unregisterReceiver(mtcImSendOkReceiver);
        broadcastManager.unregisterReceiver(mtcImSendDidFailReceiver);
        mtcImSendingReceiver = null;
        mtcImSendOkReceiver = null;
        mtcImSendDidFailReceiver = null;
    }

    private void initializeData() {
        msgList = new ArrayList<MessageModel>();

        // Emoticon Create
        emoticonAdapter.myList = GlobalVars.getEmoticonList(false);
        emoticonAdapter.notifyDataSetChanged();
    }

    private void setUpView() {
        activityInstance = mContext;
        iv_emoticons_normal.setOnClickListener(this);
        iv_emoticons_checked.setOnClickListener(this);
        manager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        mContext.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        wakeLock = ((PowerManager) mContext.getSystemService(Context.POWER_SERVICE))
                .newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "demo");
        // 判断单聊还是群聊

        adapter = new MessageAdapter(mContext, "", GROUP_TYPE);
        adapter.setItemLongClickListener(this);

        // 显示消息
        listView.setAdapter(adapter);
        listView.setOnScrollListener(new ListScrollListener());
        int count = listView.getCount();
        if (count > 0) {
            listView.setSelection(count);
        }

        listView.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard();
                more.setVisibility(View.GONE);
                iv_emoticons_normal.setVisibility(View.VISIBLE);
                iv_emoticons_checked.setVisibility(View.INVISIBLE);
                emojiIconContainer.setVisibility(View.GONE);
                btnContainer.setVisibility(View.GONE);
                return false;
            }
        });

    }

    private void setListener() {
        buttonSend.setOnClickListener(this);
        findViewById(R.id.view_camera).setOnClickListener(this);
        findViewById(R.id.view_photo).setOnClickListener(this);
        findViewById(R.id.view_favourite).setOnClickListener(this);
        findViewById(R.id.view_location).setOnClickListener(this);

        buttonSetModeVoice.setOnClickListener(this);
        buttonSetModeKeyboard.setOnClickListener(this);
        btnMore.setOnClickListener(this);

    }

    /**
     * onActivityResult
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        btnContainer.setVisibility(View.GONE);
        scrollToBottom();

        if (resultCode == Activity.RESULT_OK) { // 清空消息
            if (requestCode == REQUEST_CODE_FAVOURITE) {
                int position = data.getIntExtra("Position", 0);
                sendFavouriteMessage(position);
            } else if (requestCode == REQUEST_CODE_EMPTY_HISTORY) {
                // 清空会话
                adapter.refresh();
            } else if (requestCode == REQUEST_CODE_CAMERA ||
                    requestCode == REQUEST_CODE_LOCAL) {

                if (requestCode == REQUEST_CODE_CAMERA) {
                    String resPath = cameraFile.getPath();
                    sendFile(resPath);
                } else {
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = mContext.getContentResolver().query(
                            selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String resPath = cursor.getString(columnIndex);
                    cursor.close();

                    sendFile(resPath);
                }

            } else if (requestCode == REQUEST_CODE_MAP) { // 地图
                double latitude = data.getDoubleExtra(Constant.SAVED_LATITUDE, 0);
                double longitude = data.getDoubleExtra(Constant.SAVED_LONGITUDE, 0);
                double zoom = data.getIntExtra(Constant.SAVED_ZOOM, 10);
                String path = data.getStringExtra(Constant.MAP_THUMB);
                sendLocationMsg(latitude, longitude, zoom, path);
                // 重发消息
            } else if (requestCode == REQUEST_CODE_ADD_TO_BLACKLIST) { // 移入黑名单

            } else if (msgList.size() > 0) {
                adapter.refresh();
            } else if (requestCode == REQUEST_CODE_GROUP_DETAIL) {
                adapter.refresh();
            }
        }
    }

    /**
     * 消息图标点击事件
     *
     * @param view
     */
    @Override
    public void onClick(View view) {
        hideKeyboard();
        int id = view.getId();
        switch (id) {
            case R.id.img_back:
//                Utils.finish(ChatActivity.this);
                break;

            case R.id.view_camera:
                selectPicFromCamera();// 点击照相图标
                break;
            case R.id.view_photo:
                selectPicFromLocal(); // 点击图片图标
                break;
            case R.id.view_location:
                // TODO 位置
                mContext.startActivityForResult(new Intent(mContext, MapSelectActivity.class),
                        REQUEST_CODE_MAP);
                break;
            case R.id.view_favourite:
                selectFavourite();
                break;

            case R.id.iv_emoticons_normal:
                // 点击显示表情框
                more.setVisibility(View.VISIBLE);
                iv_emoticons_normal.setVisibility(View.INVISIBLE);
                iv_emoticons_checked.setVisibility(View.VISIBLE);
                btnContainer.setVisibility(View.GONE);
                emojiIconContainer.setVisibility(View.VISIBLE);
                hideKeyboard();
                break;
            case R.id.iv_emoticons_checked:// 点击隐藏表情框
                iv_emoticons_normal.setVisibility(View.VISIBLE);
                iv_emoticons_checked.setVisibility(View.INVISIBLE);
                btnContainer.setVisibility(View.VISIBLE);
                emojiIconContainer.setVisibility(View.GONE);
                more.setVisibility(View.GONE);
                break;
            case R.id.btn_send:
                // 点击发送按钮(发文字和表情)
                String s = mEditTextContent.getText().toString();
                sendText(s);
                break;
            case R.id.btn_more:
                more();
                break;
            case R.id.btn_set_mode_voice:
                setModeVoice();
                break;
            case R.id.btn_set_mode_keyboard:
                setModeKeyboard();
                break;
            default:
                break;
        }
    }

    public void selectPicFromCamera() {
        if (!CommonUtils.isExitsSdcard()) {
            String st = getResources().getString(
                    R.string.sd_card_does_not_exist);
            Toast.makeText(mContext.getApplicationContext(), st, Toast.LENGTH_SHORT).show();
            return;
        }
        String path = FileUtils.getSendDir(mContext.getApplicationContext()) +  "IMG_CAPTURE" + ".jpg";
        FileUtils.deleteFile(path);
        cameraFile = new File(path);
        cameraFile.getParentFile().mkdirs();
        mContext.startActivityForResult(
                new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(
                        MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile)),
                REQUEST_CODE_CAMERA);
    }

    public void selectPicFromLocal() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/* video/*");
        mContext.startActivityForResult(intent, REQUEST_CODE_LOCAL);
//        }
    }

    public void selectFavourite() {
        Intent intent = new Intent(mContext, FavouriteActivity.class);
        mContext.startActivityForResult(intent, REQUEST_CODE_FAVOURITE);
    }


    /**
     *  Send Text / File
     */
    private void sendText(String content) {
        String text = mEditTextContent.getText().toString();

        if (text.length() > 0) {
            model = new MessageModel();
            model.setMsgText(text);
            model.setMsgFromMe(true);
            model.setGroupId(GROUP_ID);
            model.setMsgId(CommonUtils.getRndMsgId());
            model.setPhoto(SelfInfoModel.userPhoto);
            model.setMsgToUserId(GROUP_ID);
            model.setMsgFromUserId(SelfInfoModel.userID);
            model.setMsgType(Constant.MSG_TEXT_TYPE);
            model.setGroupType(GROUP_TYPE);
            model.setbRead(true);
            model = DBManager.insertMessage(model);

            msgList.add(model);
            adapter.setDateSource(msgList);
            adapter.notifyDataSetChanged();
            scrollToBottom();

            mEditTextContent.setText("");

            for (int i = 0; i < receiptUsers.size(); i++ ) {
                ChatUserModel userModel = receiptUsers.get(i);
                if (userModel.userID.compareTo(SelfInfoModel.userID) != 0) {
                    String receiptUserId = userModel.userID;
                    String info = String.format(Locale.getDefault(), "{\"MtcImUserDataKey\":\"%s\"}", GROUP_ID);
                    String userUri = MtcUser.Mtc_UserFormUri(MtcUserConstants.EN_MTC_USER_ID_USERNAME, receiptUserId);
                    int cookie = msgList.indexOf(model);
                    String type = String.format("%s-%s-%s", model.getGroupType(), model.getMsgType(), model.getMsgId());
                    int ret = MtcIm.Mtc_ImSendInfo(0, userUri, type, model.getMsgText(), info);
                    if (ret != MtcConstants.ZOK) {

                    }
                }

            }
        }
    }

    private void sendEmotcion(String emoticonName) {

        if (GlobalVars.isGiftEmoticon(emoticonName) >= 0) {
            if (TIME_EST_INTERVAL * (estimateSent + 1) < timeRecord) {
                int judgeType = GlobalVars.isGiftEmoticon(emoticonName);
                // Estimate Disable
            } else {
                return;
            }
        }

        model = new MessageModel();
        model.setMsgText(emoticonName);
        model.setMsgFromMe(true);
        model.setPhoto(SelfInfoModel.userPhoto);
        model.setGroupId(GROUP_ID);
        model.setMsgToUserId(GROUP_ID);
        model.setMsgFromUserId(SelfInfoModel.userID);
        model.setMsgType(Constant.MSG_EMOTICON_TYPE);
        model.setGroupType(GROUP_TYPE);
        model.setbRead(true);
        model = DBManager.insertMessage(model);

        msgList.add(model);
        adapter.setDateSource(msgList);
        adapter.notifyDataSetChanged();
        scrollToBottom();

        for (int i = 0; i < receiptUsers.size(); i++ ) {
            ChatUserModel userModel = receiptUsers.get(i);
            if (userModel.userID.compareTo(SelfInfoModel.userID) != 0) {
                String receiptUserId = userModel.userID;
                String info = String.format(Locale.getDefault(), "{\"MtcImUserDataKey\":\"%s\"}", GROUP_ID);
                String userUri = MtcUser.Mtc_UserFormUri(MtcUserConstants.EN_MTC_USER_ID_USERNAME, receiptUserId);
                int cookie = msgList.indexOf(model);
                String type = String.format("%s-%s-%s", model.getGroupType(), model.getMsgType(), model.getMsgId());
                int ret = MtcIm.Mtc_ImSendInfo(0, userUri, type, model.getMsgText(), info);
                if (ret != MtcConstants.ZOK) {
                    model.setMsgStatus("send fail!");
                    adapter.setDateSource(msgList);
                    adapter.notifyDataSetChanged();
                }
            }
        }

    }

    private void sendVoice(String filePath, String fileName, String length,
                           boolean isResend) {
        if (!(new File(filePath).exists())) {
            return;
        }
        soundLength = length;
        sendFile(filePath);

    }

    private void sendLocationMsg(double latitude, double longitude, double zoom, String imagePath) {
        model = new MessageModel();
        model.setMsgText(String.format("%f-%f-%f", latitude, longitude, zoom));
        model.setMsgFromMe(true);
        model.setMsgId(CommonUtils.getRndMsgId());
        model.setPhoto(SelfInfoModel.userPhoto);
        model.setGroupId(GROUP_ID);
        model.setMsgToUserId(GROUP_ID);
        model.setMsgFromUserId(SelfInfoModel.userID);
        model.setMsgType(Constant.MSG_LOCATION_TYPE);
        model.setGroupType(GROUP_TYPE);
        model.setbRead(true);

        String fileName = String.format("MAP-%s.png", model.getMsgId());
        String newPath = FileUtils.getSendDir(mContext.getApplicationContext()) + fileName;
        FileUtils.fileCopy(imagePath, newPath);
        model.setThumbnailUrl(newPath);
        model.setThumbnail(FileUtils.getImageThumb(newPath));
        model = DBManager.insertMessage(model);

        msgList.add(model);
        adapter.setDateSource(msgList);
        adapter.notifyDataSetChanged();
        scrollToBottom();

        mEditTextContent.setText("");

        for (int i = 0; i < receiptUsers.size(); i++ ) {
            ChatUserModel userModel = receiptUsers.get(i);
            if (userModel.userID.compareTo(SelfInfoModel.userID) != 0) {
                String receiptUserId = userModel.userID;
                String info = String.format(Locale.getDefault(), "{\"MtcImUserDataKey\":\"%s\"}", GROUP_ID);
                String userUri = MtcUser.Mtc_UserFormUri(MtcUserConstants.EN_MTC_USER_ID_USERNAME, receiptUserId);
                int cookie = msgList.indexOf(model);
                String type = String.format("%s-%s-%s", model.getGroupType(), model.getMsgType(), model.getMsgId());
                int ret = MtcIm.Mtc_ImSendInfo(0, userUri, type, model.getMsgText(), info);
                if (ret != MtcConstants.ZOK) {
                    model.setMsgStatus("send fail!");
                    adapter.setDateSource(msgList);
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }

    private void sendFile(String path) {
        String fileType = "";
        String thumbPath = "";
        if (path.toLowerCase().endsWith(".jpg") || path.toLowerCase().endsWith(".png")) {
            fileType = Constant.MSG_IMAGE_TYPE;
        }
        if (path.toLowerCase().endsWith(".mp4") || path.toLowerCase().endsWith(".mov")) {
            fileType = Constant.MSG_VIDEO_TYPE;
        }
        if (fileType.isEmpty())
            fileType = Constant.MSG_VOICE_TYPE;

        model = new MessageModel();
        model.setMsgFromMe(true);
        model.setMsgId(CommonUtils.getRndMsgId());
        model.setGroupId(GROUP_ID);
        model.setMsgToUserId(GROUP_ID);
        model.setMsgFromUserId(SelfInfoModel.userID);
        model.setMsgType(fileType);
        model.setGroupType(GROUP_TYPE);
        model.setThumbnailUrl(thumbPath);
        model.setThumbnail(FileUtils.getImageThumb(thumbPath));
        model.setbRead(true);

        if (fileType.compareTo(Constant.MSG_VOICE_TYPE) == 0) {
            model.setSoundLength(Float.valueOf(soundLength));
        }

        if ((path != null) && (path.length() > 0)) {
            int dot = path.lastIndexOf("/");
            int endDot = path.lastIndexOf(".");
            if ((dot >-1) && (endDot < (path.length() - 1))) {
                String extension = path.substring(endDot + 1);

                String fileName = path.substring(dot + 1, endDot);
                if (fileType.compareTo(Constant.MSG_VOICE_TYPE) == 0) {
                    // Voice File 인 경우
                    fileName = String.format("%s-%s-%f-%s",model.getGroupType(), model.getMsgId(), model.getSoundLength(), fileName);
                } else {
                    // Image / Video File 인 경우
                    fileName = String.format("%s-%s-%s",model.getGroupType(), model.getMsgId(), fileName);
                }

                thumbPath = "thumb" + fileName;

                // File Path
                String newPath = FileUtils.getSendDir(mContext.getApplicationContext()) + fileName + "." + extension;
                FileUtils.fileCopy(path, newPath);
                path = newPath;
                // Thumb Path
                thumbPath = FileUtils.getSendDir(mContext.getApplicationContext()) + thumbPath + "." + extension;
            }
            model.setThumbnailUrl(thumbPath);
        }

        if (fileType.compareTo(Constant.MSG_IMAGE_TYPE) == 0 || fileType.compareTo(Constant.MSG_VIDEO_TYPE) == 0) {
            Bitmap bmp = null;
            if (path.toLowerCase().endsWith(".jpg") || path.toLowerCase().endsWith(".png")) {
                bmp = FileUtils.getImageThumb(path);
            }
            if (path.toLowerCase().endsWith(".mp4") || path.toLowerCase().endsWith(".mov")) {
                bmp = FileUtils.getVideoThumb(path);
            }

            try {
                File myCaptureFile = new File(thumbPath);
                BufferedOutputStream bos = new BufferedOutputStream(
                        new FileOutputStream(myCaptureFile));
                bmp.compress(Bitmap.CompressFormat.JPEG, 80, bos);
                bos.flush();
                bos.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            model.setThumbnail(bmp);
            if (fileType.compareTo(Constant.MSG_IMAGE_TYPE) == 0) {
                // Image 압축하여 보관
                FileUtils.fileCopy(thumbPath, path);
            }
        }

        model.setFilePath(path);
        model = DBManager.insertMessage(model);

        msgList.add(model);
        adapter.setDateSource(msgList);
        adapter.notifyDataSetChanged();
        scrollToBottom();

        final int cookie = msgList.indexOf(model);

        for (int i = 0; i < receiptUsers.size(); i++ ) {
            final ChatUserModel userModel = receiptUsers.get(i);
            if (userModel.userID.compareTo(SelfInfoModel.userID) != 0) {
                new FileUploadBackgroundTask(mContext, path, Constant.MSG_IMAGE_TYPE, new HttpCallback() {

                    @Override
                    public void onResponse(Boolean flag, Object response) {

                        if(!flag || response == null) {                 //failure
                            GlobalVars.showErrAlert(mContext);
                            return;
                        }
                        try {
                            JSONObject result = (JSONObject) response;
                            int success = result.getInt("upload_result");
                            if(success == 1) {
                                String filePath = result.getString("file_path");

                                String receiptUserId = userModel.userID;
                                String info = String.format(Locale.getDefault(), "{\"MtcImUserDataKey\":\"%s\"}", GROUP_ID);
                                String userUri = MtcUser.Mtc_UserFormUri(MtcUserConstants.EN_MTC_USER_ID_USERNAME, receiptUserId);

                                String type = String.format("%s-%s-%s", model.getGroupType(), model.getMsgType(), model.getMsgId());
                                int ret = MtcIm.Mtc_ImSendInfo(0, userUri, type, filePath, info);
                                if (ret != MtcConstants.ZOK) {

                                }
                            } else {
                                GlobalVars.showErrAlert(mContext);
                                return;
                            }
                        } catch (Exception e) {

                        }

                    }
                });

            }
        }
    }

    /**
     * 转发消息
     *
     * @param forward_msg_id
     */

    /**
     *  Menu Operation Forward / Collect / Recall / Delete
     */

    private void forwardMessage(int position, long id) {
        // 转发
        Intent intent = new Intent(mContext, ForwardActivity.class);
        intent.putExtra(Constant.MSG_SQL_ID, id);
        mContext.startActivity(intent);
    }
    private void deleteMessage(int position, long id) {
        // 删除
        DBManager.removeMessageById(id);
        adapter.removeItem(position);
        adapter.notifyDataSetChanged();
    }
    private void recallMessage(int position, long id) {
        // 撤销
        MessageModel oldMessage = DBManager.getMessage(id);

        model = new MessageModel();
        model.setMsgText(oldMessage.getMsgId());
        model.setMsgFromMe(true);
        model.setMsgId(CommonUtils.getRndMsgId());
        model.setPhoto(SelfInfoModel.userPhoto);
        model.setMsgToUserId(GROUP_ID);
        model.setMsgFromUserId(SelfInfoModel.userID);
        model.setMsgType(Constant.MSG_DELETE_TYPE);
        model.setGroupType(Constant.CHAT_CONSULT);
        model.setbRead(true);

        mEditTextContent.setText("");

        for (int i = 0; i < receiptUsers.size(); i++ ) {
            final ChatUserModel userModel = receiptUsers.get(i);
            if (userModel.userID.compareTo(SelfInfoModel.userID) != 0) {
                String receiptUserId = userModel.userID;
                String info = String.format(Locale.getDefault(), "{\"MtcImUserDataKey\":\"%s\"}", GROUP_ID);
                String userUri = MtcUser.Mtc_UserFormUri(MtcUserConstants.EN_MTC_USER_ID_USERNAME, receiptUserId);
                int cookie = msgList.indexOf(model);
                String type = String.format("%s-%s-%s", model.getGroupType(), model.getMsgType(), model.getMsgId());
                int ret = MtcIm.Mtc_ImSendInfo(0, userUri, type, model.getMsgText(), info);
                if (ret != MtcConstants.ZOK) {

                }
            }
        }

        DBManager.removeMessageById(id);
        adapter.removeItem(position);
        adapter.notifyDataSetChanged();
    }
    private void favouriteMessage(int position, long id) {
        // 收藏
        MessageModel _model = adapter.getItem(position);
        DBManager.insertFavouriteMsg(_model);
        Toast.makeText(mContext, "已收藏", Toast.LENGTH_SHORT).show();
    }
    private void sendFavouriteMessage(int position) {
        MessageModel _model = DBManager.getFavouriteMsgList().get(position);
        if (_model.getMsgType().compareTo(Constant.MSG_TEXT_TYPE) == 0) {
            sendText(_model.getMsgText());
        }
        if (_model.getMsgType().compareTo(Constant.MSG_IMAGE_TYPE) == 0) {
            sendFile(_model.getFilePath());
        }
        if (_model.getMsgType().compareTo(Constant.MSG_VIDEO_TYPE) == 0) {
            sendFile(_model.getFilePath());
        }
        if (_model.getMsgType().compareTo(Constant.MSG_VOICE_TYPE) == 0) {
            sendFile(_model.getFilePath());
        }
        if (_model.getMsgType().compareTo(Constant.MSG_LOCATION_TYPE) == 0) {
            String[] strArr = _model.getMsgText().split("-");
            double latitude = Double.valueOf(strArr[0]);
            double longitude = Double.valueOf(strArr[1]);
            double zoom = Double.valueOf(strArr[2]);
            sendLocationMsg(latitude, longitude, zoom, _model.getThumbnailUrl());
        }
        if (_model.getMsgType().compareTo(Constant.MSG_EMOTICON_TYPE) == 0) {
            sendEmotcion(_model.getMsgText());
        }
        scrollToBottom();
    }

    // Keyboard / Voice Change
    public void setModeVoice() {
        hideKeyboard();
        edittext_layout.setVisibility(View.GONE);
        more.setVisibility(View.GONE);
        buttonSetModeVoice.setVisibility(View.GONE);
        buttonSetModeKeyboard.setVisibility(View.VISIBLE);
        buttonSend.setVisibility(View.GONE);
        btnMore.setVisibility(View.VISIBLE);
        buttonPressToSpeak.setVisibility(View.VISIBLE);
        iv_emoticons_normal.setVisibility(View.VISIBLE);
        iv_emoticons_checked.setVisibility(View.INVISIBLE);
        btnContainer.setVisibility(View.VISIBLE);
        emojiIconContainer.setVisibility(View.GONE);
    }

    public void setModeKeyboard() {
        edittext_layout.setVisibility(View.VISIBLE);
        more.setVisibility(View.GONE);
        buttonSetModeKeyboard.setVisibility(View.GONE);
        buttonSetModeVoice.setVisibility(View.VISIBLE);
        mEditTextContent.requestFocus();
        // buttonSend.setVisibility(View.VISIBLE);
        buttonPressToSpeak.setVisibility(View.GONE);
        if (TextUtils.isEmpty(mEditTextContent.getText())) {
            btnMore.setVisibility(View.VISIBLE);
            buttonSend.setVisibility(View.GONE);
        } else {
            btnMore.setVisibility(View.GONE);
            buttonSend.setVisibility(View.VISIBLE);
        }

    }

    /**
     * 点击清空聊天记录
     *
     * @param view
     */
    public void emptyHistory(View view) {

    }

    public void more() {
        if (more.getVisibility() == View.GONE) {
            System.out.println("more gone");
            hideKeyboard();
            more.setVisibility(View.VISIBLE);
            btnContainer.setVisibility(View.VISIBLE);
            emojiIconContainer.setVisibility(View.GONE);
        } else {
            if (emojiIconContainer.getVisibility() == View.VISIBLE) {
                emojiIconContainer.setVisibility(View.GONE);
                btnContainer.setVisibility(View.VISIBLE);
                iv_emoticons_normal.setVisibility(View.VISIBLE);
                iv_emoticons_checked.setVisibility(View.INVISIBLE);
            } else {
                more.setVisibility(View.GONE);
            }

        }

    }

    public void editClick() {
        listView.setSelection(listView.getCount() - 1);
        if (more.getVisibility() == View.VISIBLE) {
            more.setVisibility(View.GONE);
            iv_emoticons_normal.setVisibility(View.VISIBLE);
            iv_emoticons_checked.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public void onEmoticonClick(int position) {
        EmoticonModel model = emoticonAdapter.myList.get(position);
        sendEmotcion(model.emoticonName);
    }

    private PowerManager.WakeLock wakeLock;

    @Override
    public void onChatLongClick(final int position) {
        // View Long Click
        final MessageModel model = adapter.getItem(position);
        boolean bCancel = false;
        int count = 3;
        if (model.isMsgFromMe() == true) {
            if (model.getSendDate().getTime() > new Date().getTime() - 2 * 60 * 1000) {
                bCancel = true;
                count = 4;
            }
        }

        String[] strArray = new String[count];
        strArray[0] = "转发";
        strArray[1] = "收藏";
        if (bCancel == false) {
            strArray[2] = "删除";
        } else {
            strArray[2] = "撤销";
            strArray[3] = "删除";
        }

        final boolean bCancelShow = bCancel;
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setItems(strArray, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:                 //  转发
                        forwardMessage(position, model.getSqlId());
                        dialog.dismiss();
                        break;
                    case 1:                 //  收藏
                        favouriteMessage(position, model.getSqlId());
                        dialog.dismiss();
                        break;
                    case 2:
                        if (bCancelShow)    //  撤销
                            recallMessage(position, model.getSqlId());
                        else                //  删除
                            deleteMessage(position, model.getSqlId());
                        dialog.dismiss();
                        break;
                    case 3:                 //  删除
                        deleteMessage(position, model.getSqlId());
                        dialog.dismiss();
                        break;
                }
            }
        }).setCancelable(true);
        AlertDialog dialog = builder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.show();
    }

    /**
     * 按住说话listener
     *
     */
    class PressToSpeakListen implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    animationDrawable.start();
                    if (!CommonUtils.isExitsSdcard()) {
                        String st4 = getResources().getString(
                                R.string.Send_voice_need_sdcard_support);
                        Toast.makeText(mContext, st4, Toast.LENGTH_SHORT)
                                .show();
                        return false;
                    }
                    try {
                        v.setPressed(true);
                        wakeLock.acquire();
                        if (VoicePlayClickListener.isPlaying)
                            VoicePlayClickListener.currentPlayListener
                                    .stopPlayVoice();
                        recordingContainer.setVisibility(View.VISIBLE);
                        recordingHint
                                .setText(mContext.getString(R.string.move_up_to_cancel));
                        recordingHint.setBackgroundColor(Color.TRANSPARENT);
                        voiceRecorder.startRecording(null, "", mContext.getApplicationContext());
                    } catch (Exception e) {
                        e.printStackTrace();
                        v.setPressed(false);
                        if (wakeLock.isHeld())
                            wakeLock.release();
                        if (voiceRecorder != null)
                            voiceRecorder.discardRecording();
                        recordingContainer.setVisibility(View.INVISIBLE);
                        Toast.makeText(mContext, R.string.recoding_fail,
                                Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    return true;
                case MotionEvent.ACTION_MOVE: {
                    if (event.getY() < 0) {
                        recordingHint
                                .setText(mContext.getString(R.string.release_to_cancel));
                        recordingHint
                                .setBackgroundResource(R.drawable.recording_text_hint_bg);
                    } else {
                        recordingHint
                                .setText(mContext.getString(R.string.move_up_to_cancel));
                        recordingHint.setBackgroundColor(Color.TRANSPARENT);
                        animationDrawable.start();
                    }
                    return true;
                }
                case MotionEvent.ACTION_UP:
                    if (animationDrawable.isRunning()) {
                        animationDrawable.stop();
                    }
                    v.setPressed(false);
                    recordingContainer.setVisibility(View.INVISIBLE);
                    if (wakeLock.isHeld())
                        wakeLock.release();
                    if (event.getY() < 0) {
                        // discard the recorded audio.
                        voiceRecorder.discardRecording();

                    } else {
                        // stop recording and send voice file
                        String st1 = getResources().getString(
                                R.string.Recording_without_permission);
                        String st2 = getResources().getString(
                                R.string.The_recording_time_is_too_short);
                        String st3 = getResources().getString(
                                R.string.send_failure_please);
                        try {

                            int length = voiceRecorder.stopRecoding();
                            if (length > 0) {
                                sendVoice(voiceRecorder.getVoiceFilePath(),
                                        voiceRecorder
                                                .getVoiceFileName(""),
                                        Integer.toString(length), false);
                            } else if (length == EMError.INVALID_FILE) {
                                Toast.makeText(mContext.getApplicationContext(), st1,
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(mContext.getApplicationContext(), st2,
                                        Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(mContext, st3,
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                    return true;
                default:
                    recordingContainer.setVisibility(View.INVISIBLE);
                    if (voiceRecorder != null)
                        voiceRecorder.discardRecording();
                    return false;
            }
        }
    }


    public void onStart() {
        String filter = "";
        if (GROUP_TYPE.compareTo(Constant.CHAT_TGROUP) == 0)
            filter = Constant.UPDATE_FGROUP_CHAT;
        if (GROUP_TYPE.compareTo(Constant.CHAT_FGROUP) == 0)
            filter = Constant.UPDATE_TGROUP_CHAT;
        if (GROUP_TYPE.compareTo(Constant.CHAT_ORDER) == 0)
            filter = Constant.UPDATE_ORDER_CHAT;

        LocalBroadcastManager.getInstance(App.getInstance()).registerReceiver((receiver),
                new IntentFilter(filter)
        );
    }

    public void onStop() {
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(receiver);
    }

    public void onDestroy() {
        unregisterReceivers();
        activityInstance = null;

    }

    public void onResume() {
        adapter.refresh();
    }

    public void onPause() {
        if (wakeLock.isHeld())
            wakeLock.release();
        if (VoicePlayClickListener.isPlaying
                && VoicePlayClickListener.currentPlayListener != null) {
            // 停止语音播放
            VoicePlayClickListener.currentPlayListener.stopPlayVoice();
        }

        try {
            // 停止录音
            if (voiceRecorder.isRecording()) {
                voiceRecorder.discardRecording();
                recordingContainer.setVisibility(View.INVISIBLE);
            }
        } catch (Exception e) {
        }
    }

    /**
     * 隐藏软键盘
     */

    private void hideKeyboard() {
        if (mContext.getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (mContext.getCurrentFocus() != null)
                manager.hideSoftInputFromWindow(mContext.getCurrentFocus()
                        .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 返回
     *
     * @param view
     */
    public void back(View view) {


    }
    public void onBackPressed() {
        if (more.getVisibility() == View.VISIBLE) {
            more.setVisibility(View.GONE);
            iv_emoticons_normal.setVisibility(View.VISIBLE);
            iv_emoticons_checked.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * listview滑动监听listener
     *
     */
    private class ListScrollListener implements AbsListView.OnScrollListener {

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            switch (scrollState) {
                case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:

            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem,
                             int visibleItemCount, int totalItemCount) {

        }

    }

    class TimeRecordTask extends TimerTask {
        public void run() {
            // ERROR

        }
    }


    public void updateChatHistory() {
        if (SelfInfoModel.userID == null)
            return;
        String query = "GROUP_TYPE = ? and GROUP_ID = ?";
        msgList = DBManager.getMessageList(query, GROUP_TYPE, GROUP_ID);
        for (int i = 0; i < msgList.size(); i++) {
            MessageModel _model = msgList.get(i);
            if (_model.getbRead() == false) {
                // 새로 받은 Message 있음음
                bReceive = true;
            }
            if (_model.isMsgFromMe())
                _model.setPhoto(SelfInfoModel.userPhoto);
            else {

                for (int j = 0; j < receiptUsers.size(); j++) {
                    ChatUserModel userModel = receiptUsers.get(j);
                    if (userModel.userID.compareTo(_model.getMsgFromUserId()) == 0) {
                        _model.setPhoto(userModel.userPhoto);
                    }
                }
            }

            if (_model.getMsgType().compareTo(Constant.MSG_IMAGE_TYPE) == 0 ||
                    _model.getMsgType().compareTo(Constant.MSG_VIDEO_TYPE) == 0 ||
                    _model.getMsgType().compareTo(Constant.MSG_LOCATION_TYPE) == 0) {

                if (_model.getThumbnailUrl() == null || _model.getThumbnailUrl().isEmpty() == true) {
                    if (_model.getMsgType().compareTo(Constant.MSG_IMAGE_TYPE) == 0) {
                        Bitmap bmp = null;
                        bmp = FileUtils.getImageThumb(_model.getFilePath());
                        String thumbPath = "thumb" + FileUtils.getFileName(_model.getFilePath())
                                + "." + FileUtils.getExtension(_model.getFilePath());
                        thumbPath = FileUtils.getSendDir(mContext.getApplicationContext()) + thumbPath;
                        _model.setThumbnail(bmp);
                        _model.setThumbnailUrl(thumbPath);
                        DBManager.updateMessageThumbPath(thumbPath, _model.getSqlId());
                        FileUtils.saveBitmap(bmp, thumbPath);
                    }
                    if (_model.getMsgType().compareTo(Constant.MSG_VIDEO_TYPE) == 0) {
                        String thumbPath = "thumb" + FileUtils.getFileName(_model.getFilePath())
                                + "." + FileUtils.getExtension(_model.getFilePath());
                        Bitmap bmp = FileUtils.getVideoThumb(_model.getFilePath());
                        _model.setThumbnailUrl(thumbPath);
                        _model.setThumbnail(bmp);
                        DBManager.updateMessageThumbPath(thumbPath, _model.getSqlId());
                        FileUtils.saveBitmap(bmp, thumbPath);
                    }
                    if (_model.getMsgType().compareTo(Constant.MSG_LOCATION_TYPE) == 0) {

                    }
                }
                _model.setThumbnail(FileUtils.getImageThumb(_model.getThumbnailUrl()));
            }

        }
        adapter.setDateSource(msgList);
        adapter.notifyDataSetChanged();
        scrollToBottom();

    }

    private void scrollToBottom() {
        listView.setSelection(listView.getCount());
    }

    public ArrayList<ChatUserModel> getReceiptUsers() {
        return receiptUsers;
    }

    public void setReceiptUsers(ArrayList<ChatUserModel> receiptUsers) {
        this.receiptUsers = receiptUsers;
    }
}
