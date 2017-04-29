package grl.com.subViews.fragment;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.speech.VoiceRecognitionService;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import grl.com.activities.MainActivity;
import grl.com.adapters.chat.VoicePlayClickListener;
import grl.com.adapters.order.UserListAdapter;
import grl.com.configuratoin.CommonUtils;
import grl.com.configuratoin.Constant;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.MyAudioRecorder;
import grl.com.configuratoin.SelfInfoModel;
import grl.com.dataModels.OrderModel;
import grl.com.dataModels.UserModel;
import grl.com.httpRequestTask.FileUploadTask;
import grl.com.httpRequestTask.order.GiveOrderTask;
import grl.com.httpRequestTask.order.NearUserTask;
import grl.com.httpRequestTask.order.OrderReactTask;
import grl.com.network.HttpCallback;
import grl.com.subViews.animation.ArcTranslateAnimation;
import grl.com.subViews.dialogues.OrderBudgetDialog;
import grl.com.subViews.dialogues.OrderSkillDialog;
import grl.com.subViews.dialogues.OrderUserDialog;
import grl.com.subViews.edittext.OrderEditText;
import grl.com.subViews.star.StarShowView;
import grl.wangu.com.grl.R;

/**
 * Created by Administrator on 6/7/2016.
 */
public class Fragment_order extends Fragment implements View.OnClickListener, RecognitionListener {

    private final int DIALOG_USER_REQ_CODE = 1;
    private final int DIALOG_BUDGET_REQ_CODE = 2;
    private final int DIALOG_SKILL_REQ_CODE = 3;

    // Order Submit Filter

    private String orderType;
    private String orderContent;

    private String skill;

    private String budget;

    private String gender;
    private String area;
    private String phoneNumber;
    private Integer minDistance;
    private Integer maxDistance;
    private Integer direction;
    private double latitude;
    private double longitude;

    private int submitTime;

    private String voicePath = "";

    private String orderContentId = "";

    private MainActivity ctx;
    private View layout;
    private WindowManager mWindowManager;
    private Button showReactBtn;

    InputMethodManager manager;

    android.support.v7.app.ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private RecyclerView mUserList;
    private LinearLayout mDrawView;
    private TextView mInfoView;
    private UserListAdapter userListAdapter;

    private Button soundModeBtn;
    private Button keyboardModeBtn;

    private Button orderLogoBtn;
    private ImageView orderLogoSearch;
    private ImageView orderLogoBackground;
    private ImageView orderSearchLine;
    private ImageView orderStarBackground;

    private TextView submitDateView;
    private TextView submitTimeView;

    private TextView budgetView;

    private Button showUserBtn;
    private Button showBudgetBtn;
    private Button soundBtn;

//    private Button showSkillBtn;

    private FrameLayout submitView;
    private TextView speakBtnLayout;

    private RelativeLayout editTextLayout;
    private OrderEditText contentField;

    private OrderSkillDialog skillDialog;
    private OrderBudgetDialog budgetDialog;
    private OrderUserDialog userDialog;

    private boolean bOrderSubmit = false;
    public StarShowView starView;

    private ArrayList<UserModel> reactUsers;
    private ArrayList<UserModel> nearByUsers;

    // Press to Speak
    private MyAudioRecorder recorder;
    private AnimationDrawable animationDrawable;
    private View recordingContainer;
    private ImageView micImage;
    private TextView recordingHint;
    private PowerManager.WakeLock wakeLock;

    private android.os.Handler micImageHandler = new android.os.Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
        }
    };

    RefreshTask refreshTask;
    private int refreshCount = 0;

    public Fragment_order() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (layout == null) {
            ctx = (MainActivity) this.getActivity();
            layout = ctx.getLayoutInflater().inflate(R.layout.fragment_order,
                    null);
            mWindowManager = (WindowManager) ctx
                    .getSystemService(Context.WINDOW_SERVICE);
            getViewByID();
            getSubmitView();
            initViews();
        } else {
            ViewGroup parent = (ViewGroup) layout.getParent();
            if (parent != null) {
                parent.removeView(layout);
            }
        }

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(ctx, new ComponentName(ctx , VoiceRecognitionService.class));

        speechRecognizer.setRecognitionListener(this);


        return layout;
    }

    @Override
    public void onPause() {
        speechRecognizer.destroy();
        super.onPause();
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        ClockRotateListen listener = new ClockRotateListen();
        orderLogoBackground.setOnTouchListener(listener);

        int centerX = (int)(orderLogoBackground.getWidth() / 2);
        int centerY = (int)(orderLogoBackground.getHeight() / 2);
        listener.setCenter(new Point(centerX, centerY));
    }

    public void getViewByID () {
        mDrawerLayout = (DrawerLayout)layout.findViewById(R.id.drawer_layout);
        mDrawView = (LinearLayout)layout.findViewById(R.id.drawer_view);
        mUserList = (RecyclerView) layout.findViewById(R.id.order_react_view);
        mInfoView = (TextView)layout.findViewById(R.id.order_react_info);
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        manager = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        showReactBtn = (Button) layout.findViewById(R.id.order_react_show);
        showReactBtn.setOnClickListener(this);

        orderLogoBtn = (Button)layout.findViewById(R.id.order_logo_btn);
        orderLogoBtn.setOnClickListener(this);
        orderLogoSearch = (ImageView)layout.findViewById(R.id.order_logo_search);
        orderLogoBackground = (ImageView)layout.findViewById(R.id.order_logo_background);
        orderSearchLine = (ImageView)layout.findViewById(R.id.order_search_line);
        orderStarBackground = (ImageView)layout.findViewById(R.id.order_star_background);

        budgetView = (TextView)layout.findViewById(R.id.tv_budget);

        submitDateView = (TextView) layout.findViewById(R.id.tv_submit_date);
        submitTimeView = (TextView)layout.findViewById(R.id.tv_submit_time);

        starView = (StarShowView)layout.findViewById(R.id.star_view);
//        starView.horizontalView = (HScroll) layout.findViewById(R.id.hs_star);
//        starView.layout = (RelativeLayout)layout.findViewById(R.id.rl_star_layout);
        starView.initView();

        // Record Sound
        recordingContainer = layout.findViewById(R.id.view_talk);
        micImage = (ImageView) layout.findViewById(R.id.mic_image);
        animationDrawable = (AnimationDrawable) micImage.getBackground();
        animationDrawable.setOneShot(false);
        recordingHint = (TextView) layout.findViewById(R.id.recording_hint);


    }

    private void getSubmitView() {
        submitView = (FrameLayout)ctx.findViewById(R.id.submit_view);
        soundModeBtn = (Button)ctx.findViewById(R.id.btn_set_mode_voice);
        soundModeBtn.setOnClickListener(this);
        keyboardModeBtn = (Button)ctx.findViewById(R.id.btn_set_mode_keyboard);
        keyboardModeBtn.setOnClickListener(this);

        speakBtnLayout = (TextView)ctx.findViewById(R.id.btn_press_to_speak);
//        speakView = (TextView)ctx.findViewById(R.id.btn_press_view);

        editTextLayout = (RelativeLayout)ctx.findViewById(R.id.edittext_layout);
        contentField = (OrderEditText)ctx.findViewById(R.id.edittext_content);
        contentField.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    submitOrderAction();
                    return true;
                }
                return false;
            }
        });

        showUserBtn = (Button)ctx.findViewById(R.id.order_show_user_btn);
        showUserBtn.setOnClickListener(this);
//        showSkillBtn = (Button)layout.findViewById(R.id.order_show_skill_btn);
//        showSkillBtn.setOnClickListener(this);
        showBudgetBtn = (Button)ctx.findViewById(R.id.order_show_budget_btn);
        showBudgetBtn.setOnClickListener(this);

        soundBtn = (Button)ctx.findViewById(R.id.btn_set_mode_voice);
        soundBtn.setOnClickListener(this);

        speakBtnLayout.setOnTouchListener(new PressToSpeakListen());
    }

    private void initViews() {
        // Init Adapter - React
        userListAdapter = new UserListAdapter(ctx);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(ctx);
        mUserList.setLayoutManager(mLayoutManager);
        mUserList.setItemAnimator(new DefaultItemAnimator());
        mUserList.setAdapter(userListAdapter);

        showBudgetBtn.setOnClickListener(this);

        skillDialog = OrderSkillDialog.newInstance();
        budgetDialog = OrderBudgetDialog.newInstance();
        userDialog = OrderUserDialog.newInstance();
        userDialog.setParent(getActivity());

        nearByUsers = new ArrayList<UserModel>();
        reactUsers = new ArrayList<UserModel>();

//        voiceRecorder = new VoiceRecorder(ctx, micImageHandler);
        recorder = new MyAudioRecorder(ctx);
        animationDrawable = (AnimationDrawable) micImage.getBackground();
        animationDrawable.setOneShot(false);
        wakeLock = ((PowerManager) ctx.getSystemService(Context.POWER_SERVICE))
                .newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "demo");

        refreshTask = new RefreshTask();
        Timer myTimer = new Timer();
        myTimer.schedule(refreshTask, 1000, 10000);

        resetFilter();

        refreshClock();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.order_react_show:
                showReactView();
                break;
            case R.id.order_logo_btn:
                showSubmitView();
                break;
            case R.id.order_show_user_btn:
                showOrderUser();
                break;
            case R.id.order_show_budget_btn:
                showOrderBudget();
                break;
            case R.id.btn_set_mode_keyboard:
                setModeKeyboard();
                break;
            case R.id.btn_set_mode_voice:
                setModeVoice();
                break;
        }
    }

    void showReactView() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                DrawerLayout drawer = (DrawerLayout) layout.findViewById(R.id.drawer_layout);
                if (!drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.openDrawer(mDrawView);
                }
            }
        }, 300);

    }

    // Show / Hide Submit View
    public void showSubmitView() {
        if (submitView.getVisibility() == View.VISIBLE) {
            hideKeyboard();
            submitView.setVisibility(View.GONE);
            ctx.findViewById(R.id.layout_bar).setVisibility(View.VISIBLE);
        } else {
            starView.goToCenter();
            submitView.setVisibility(View.VISIBLE);
            ctx.findViewById(R.id.layout_bar).setVisibility(View.INVISIBLE);
        }
        setBudgetView();
    }

    private void setBudgetView() {
        if (submitView.getVisibility() == View.VISIBLE)
            budgetView.setVisibility(View.VISIBLE);
        else
            budgetView.setVisibility(View.INVISIBLE);
        if (budget.isEmpty() || budget.compareTo("0") == 0) {
            budgetView.setText("");
            budgetView.setText("");
            budgetView.setVisibility(View.INVISIBLE);
        } else {
            budgetView.setText(String.format("%s¥", budget));
        }

    }

    // Sound / KeyBoard Mode

    public void setModeKeyboard() {
        editTextLayout.setVisibility(View.VISIBLE);
        keyboardModeBtn.setVisibility(View.GONE);
        soundModeBtn.setVisibility(View.VISIBLE);
        contentField.requestFocus();
        speakBtnLayout.setVisibility(View.GONE);
        if (TextUtils.isEmpty(contentField.getText())) {

        }
    }

    public void setModeVoice() {
        hideKeyboard();
        editTextLayout.setVisibility(View.GONE);
        soundModeBtn.setVisibility(View.GONE);
        keyboardModeBtn.setVisibility(View.VISIBLE);
        speakBtnLayout.setVisibility(View.VISIBLE);
    }

    // Order Submit Filter Show

    public void showOrderUser() {
        if (userDialog.isShowing())
            return;
        userDialog.setShowsDialog(true);
        userDialog.setTargetFragment(this, DIALOG_USER_REQ_CODE);
        userDialog.show(getActivity().getSupportFragmentManager(), "");
        userDialog.setData(gender, area, phoneNumber, minDistance, maxDistance, latitude, longitude, direction);
    }

    public void showOrderBudget() {
        if (budgetDialog.isShowing())
            return;
        budgetDialog.setShowsDialog(true);
        budgetDialog.setTargetFragment(this, DIALOG_BUDGET_REQ_CODE);
        budgetDialog.show(getActivity().getSupportFragmentManager(), "");
        budgetDialog.setData(budget);
    }

    public void showOrderSkill() {
        skillDialog.setShowsDialog(true);
        skillDialog.setData(skill);
        skillDialog.setTargetFragment(this, DIALOG_SKILL_REQ_CODE);
        skillDialog.show(getActivity().getSupportFragmentManager(), "");
    }

    private void hideKeyboard() {
        if (getActivity().getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getActivity().getCurrentFocus() != null)
                manager.hideSoftInputFromWindow(getActivity().getCurrentFocus()
                        .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(resultCode == 1) // 1 is an arbitrary number, can be any int
        {
            switch (requestCode) {
                case DIALOG_USER_REQ_CODE:
                    gender = data.getStringExtra(Constant.ORDER_GENDER);
                    area = data.getStringExtra(Constant.ORDER_AREA);
                    phoneNumber = data.getStringExtra(Constant.ORDER_NUMBER);
                    minDistance = data.getIntExtra(Constant.ORDER_MIN_DISTANCE, 0);
                    maxDistance = data.getIntExtra(Constant.ORDER_MAX_DISTANCE, 0);
                    direction = data.getIntExtra(Constant.ORDER_DIRECTION, -1);
                    latitude = data.getDoubleExtra(Constant.ORDER_LATITUDE, 0);
                    longitude = data.getDoubleExtra(Constant.ORDER_LONGITUDE, 0);
                    break;
                case DIALOG_BUDGET_REQ_CODE:
                    budget = data.getStringExtra(Constant.ORDER_BUDGET);
                    setBudgetView();
                    break;
                case DIALOG_SKILL_REQ_CODE:
                    break;
            }
        }

    }

    class RefreshTask extends TimerTask {
        public void run() {
            refreshCount = (refreshCount + 1) % 6;
            GlobalVars.hideWaitDialog();
            ctx.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    runRotateAnimation(5, orderSearchLine.getWidth() / 2, orderSearchLine.getHeight() / 2);
                }
            });

            if (refreshCount != 0)
                return;

            if (bOrderSubmit == false ) {

                new NearUserTask(ctx, new HttpCallback() {
                    @Override
                    public void onResponse(Boolean flag, Object response) {
                        if(!flag || response == null) {                 //failure
                            GlobalVars.showErrAlert(ctx);
                            return;
                        }

                        try {
                            JSONArray result = (JSONArray) response;
                            nearByUsers.clear();
                            for (int i = 0; i < result.length(); i++) {
                                JSONObject object = result.getJSONObject(i);
                                UserModel model = new UserModel();
                                model.parseFromJson(object);
                                model.state = "";
//                                if (GlobalVars.getIntFromJson(object, "state") == 1) {
//                                    model.state = "接受";
//                                } else {
//                                    model.state = "等候";
//                                }
                                nearByUsers.add(model);
                            }
                        } catch (Exception ex) {

                        }
                        starView.refreshData(nearByUsers, orderLogoSearch.getWidth());
                        starView.setnShowType(0);
                        userListAdapter.myList = nearByUsers;
                        userListAdapter.notifyDataSetChanged();
                    }
                });
                return;
            } else {

                new OrderReactTask(ctx, orderContentId, new HttpCallback() {
                    @Override
                    public void onResponse(Boolean flag, Object response) {
                        if(!flag || response == null) {                 //failure
                            GlobalVars.showErrAlert(ctx);
                            return;
                        }

                        try {
                            JSONObject result = (JSONObject) response;
                            reactUsers.clear();
                            long timeStart = GlobalVars.getDateFromJson(result, "time_start");
                            int offset = GlobalVars.getIntFromJson(result, "submit_time");
                            Date date = new Date(timeStart * 1000 + offset * 1000);
                            String dateStr = GlobalVars.getDateStringFromDate(date, "yyyy-MM-dd hh:mm");
                            String locationStr = SelfInfoModel.posArea.get("province") + " " +
                                                    SelfInfoModel.posArea.get("province") + " " +
                                                    SelfInfoModel.posArea.get("district");
                            // Test Code
                            mInfoView.setText(dateStr + "\n" + locationStr);

                            JSONArray users = result.getJSONArray("users");
                            for (int i = 0; i < users.length(); i++) {
                                JSONObject object = users.getJSONObject(i);
                                UserModel model = new UserModel();
                                model.parseFromJson(object);
                                model.state = "";
                                if (model.orderStatus >= OrderModel.ORDER_ACCEPT_STATE)
                                    model.state = "已抢";
//                                if (GlobalVars.getIntFromJson(object, "state") == 1) {
//                                    model.state = "接受";
//                                } else {
//                                    model.state = "等候";
//                                }
                                reactUsers.add(model);
                            }
                        } catch (Exception ex) {

                        }
                        starView.refreshData(reactUsers, orderLogoSearch.getWidth());
                        starView.setnShowType(1);
                        userListAdapter.myList = reactUsers;
                        userListAdapter.notifyDataSetChanged();
                        mInfoView.setVisibility(View.VISIBLE);

                    }
                });
            }
        }
    }

    class PressToSpeakListen implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    animationDrawable.start();
                    if (!CommonUtils.isExitsSdcard()) {
                        String st4 = getResources().getString(
                                R.string.Send_voice_need_sdcard_support);
                        Toast.makeText(ctx, st4, Toast.LENGTH_SHORT)
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
                                .setText(getString(R.string.move_up_to_cancel));
                        recordingHint.setBackgroundColor(Color.TRANSPARENT);
//                        voiceRecorder.startRecording(null, "", ctx);
                        recorder.beginRecording();
                    } catch (Exception e) {
                        e.printStackTrace();
                        v.setPressed(false);
                        if (wakeLock.isHeld())
                            wakeLock.release();
//                        if (voiceRecorder != null)
//                            voiceRecorder.discardRecording();
                        if(recorder != null)
                            recorder.endRecording();
                        recordingContainer.setVisibility(View.INVISIBLE);
                        Toast.makeText(ctx, R.string.recoding_fail,
                                Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    return true;
                case MotionEvent.ACTION_MOVE: {
                    if (event.getY() < 0) {
                        recordingHint
                                .setText(getString(R.string.release_to_cancel));
                        recordingHint
                                .setBackgroundResource(R.drawable.recording_text_hint_bg);
                    } else {
                        recordingHint
                                .setText(getString(R.string.move_up_to_cancel));
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
//                        voiceRecorder.discardRecording();
                        recorder.endRecording();
                    } else {
                        // stop recording and send voice file
                        String st1 = getResources().getString(
                                R.string.Recording_without_permission);
                        String st2 = getResources().getString(
                                R.string.The_recording_time_is_too_short);
                        String st3 = getResources().getString(
                                R.string.send_failure_please);
                        try {
                            final String recodFilePath = recorder.endRecording();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    startRecognition(recodFilePath);
                                }
                            }, 100);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(ctx, st3,
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                    return true;
                default:
                    recordingContainer.setVisibility(View.INVISIBLE);
//                    if (voiceRecorder != null)
//                        voiceRecorder.discardRecording();
                    if(recorder != null)
                        recorder.endRecording();
                    return false;
            }
        }
    }

    class ClockRotateListen implements View.OnTouchListener {
        private double oldAngle;
        private double calcAngle;
        private boolean bTouch = false;
        private Point center;

        public void setCenter(Point point) {
            center = point;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    bTouch = true;
                    calcAngle = oldAngle = getAngle(new Point((int)event.getX(), (int)event.getY()));
                    return true;
                case MotionEvent.ACTION_MOVE: {
                    if (bTouch == false) return true;
                    double angle = getAngle(new Point((int)event.getX(), (int)event.getY()));
                    double TIME_UNIT = Math.PI * 2 / 6 / 5;
                    if (Math.abs(angle - calcAngle) > TIME_UNIT) {
                        Log.d("New Angle",String.valueOf(angle));
                        Log.d("Calc Angle",String.valueOf(calcAngle));
                        int timeChange = -(int)(getAngleOffset(calcAngle, angle) / TIME_UNIT) * 60 * 10;
                        submitTime += timeChange;
                        calcAngle = angle;
                        refreshClock();
                    }
                    if (angle > 0 && angle < Math.PI / 2)
                        orderLogoSearch.animate().rotation((float)(-angle/ Math.PI * 180)).setDuration(0).start();
                    else if (angle > Math.PI / 2 && angle < Math.PI)
                        orderLogoSearch.animate().rotation((float)(-angle/ Math.PI * 180)).setDuration(0).start();
                    else if (angle > Math.PI && angle < Math.PI)
                        orderLogoSearch.animate().rotation((float)(angle/ Math.PI * 180)).setDuration(0).start();
                    else
                        orderLogoSearch.animate().rotation((float)(-angle/ Math.PI * 180)).setDuration(0).start();
                    oldAngle = angle;
                    return true;
                }
                case MotionEvent.ACTION_UP:
                    bTouch = false;
                    return true;
                default:
                    return false;
            }
        }

        private double getAngle(Point point) {
            double offsetX = point.x - center.x;
            double offsetY = point.y - center.y;
            double length = Math.sqrt(offsetX * offsetX + offsetY * offsetY);
            double angle = Math.acos(offsetX / length);
            if (point.y > center.y) {
                angle = 2 * Math.PI - angle;
            }
            return angle;
        }

        private double getAngleOffset(double startAngle, double endAngle) {
            double offset = endAngle - startAngle;
            double offsetInvert = 2 * Math.PI - Math.abs(offset);
            if (Math.abs(offset) < Math.abs(offsetInvert)) {
                return -offset;
            } else {
                if (offset > 0)
                    return Math.abs(offsetInvert);
                else
                    return -Math.abs(offsetInvert);
            }
        }
    }

    private void refreshClock() {
        Date date = new Date();
        if (submitTime != 0)
            date.setTime(date.getTime() + submitTime * 1000);

        submitDateView.setText(String.format("穿%s越", GlobalVars.getDateStringFromDate(date, "yyyy MM dd")));
        submitTimeView.setText(GlobalVars.getDateStringFromDate(date, "HH:mm"));
    }

    private void runRotateAnimation(int duration, int cx, int cy) {
        orderSearchLine.setDrawingCacheEnabled(true);
//        RotateAnimation rAnim = new RotateAnimation(0.0F, 359.0F, Animation.RELATIVE_TO_SELF, 0.5F, Animation.RELATIVE_TO_SELF, 0.5F);
//        rAnim.setInterpolator(new LinearInterpolator());
//        rAnim.setRepeatCount(1);
//        rAnim.setDuration(5000);
//        orderSearchLine.startAnimation(rAnim);

        orderSearchLine.setVisibility(View.VISIBLE);

        orderSearchLine.setDrawingCacheEnabled(true);
        Animation startRotateAnimation = AnimationUtils.loadAnimation(ctx.getApplicationContext(), R.anim.rotate_animation);
        orderSearchLine.startAnimation(startRotateAnimation);

        startRotateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationEnd(Animation arg0) {
                orderSearchLine.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationStart(Animation animation) {

            }
        });

//        orderStarBackground.setVisibility(View.VISIBLE);
//        ArcTranslateAnimation arcAnimation = new ArcTranslateAnimation(
//                0, orderStarBackground.getWidth(), 0, orderStarBackground.getHeight());
//        arcAnimation
//                .setInterpolator(new LinearInterpolator());
//        arcAnimation.setDuration(5000);
//        arcAnimation.setFillAfter(true);
//
//        orderStarBackground.startAnimation(arcAnimation);
//        arcAnimation.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationEnd(Animation arg0) {
//                orderStarBackground.setVisibility(View.INVISIBLE);
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//
//            }
//
//            @Override
//            public void onAnimationStart(Animation animation) {
//
//            }
//        });

    }

    private void submitOrderAction() {
        orderType = contentField.getOrderType();
        orderContent = contentField.getOrderCcontent();
        if (orderContent.isEmpty())
            return;
        if (voicePath.isEmpty()) {
            orderSubmit("");
        } else {
            new FileUploadTask(ctx, voicePath, Constant.MSG_IMAGE_TYPE, new HttpCallback() {

                @Override
                public void onResponse(Boolean flag, Object response) {

                    if(!flag || response == null) {                 //failure
                        GlobalVars.showErrAlert(ctx);
                        return;
                    }
                    try {
                        JSONObject result = (JSONObject) response;
                        int success = result.getInt("upload_result");
                        if(success == 1) {
                            String filePath = result.getString("file_path");
                            orderSubmit(filePath);
                        } else {
                            GlobalVars.showErrAlert(ctx);
                            return;
                        }
                    } catch (Exception e) {

                    }
                }
            });
        }
    }

    private void orderSubmit(String path) {
        bOrderSubmit = true;

        reactUsers.clear();
        starView.refreshData(reactUsers, orderLogoSearch.getWidth());
        starView.goToCenter();
        userListAdapter.myList = reactUsers;
        userListAdapter.notifyDataSetChanged();


        new GiveOrderTask(ctx, path, orderType, orderContent, skill, gender, area, phoneNumber,
                budget, submitTime, latitude, longitude,
                minDistance, maxDistance, direction, new HttpCallback() {

            @Override
            public void onResponse(Boolean flag, Object response) {

                if(!flag || response == null) {                 //failure
                    GlobalVars.showErrAlert(ctx);
                    return;
                }
                try {
                    JSONObject result = (JSONObject) response;
                    boolean success = GlobalVars.getBooleanFromJson(result, "give_result");
                    if(success) {
                        GlobalVars.playSoundPool(ctx, R.raw.order_send);
                        orderContentId = GlobalVars.getStringFromJson(result, "order_content_id");
                        resetFilter();
                        Toast.makeText(ctx, ctx.getResources().getString(R.string.submit_order_ok), Toast.LENGTH_SHORT)
                                .show();
                    } else {
                        Toast.makeText(ctx, ctx.getResources().getString(R.string.submit_order_fail), Toast.LENGTH_SHORT)
                                .show();
                        return;
                    }
                } catch (Exception e) {
                    Toast.makeText(ctx, ctx.getResources().getString(R.string.submit_order_fail), Toast.LENGTH_SHORT)
                            .show();
                }
                GlobalVars.showWaitDialog(ctx);
                refreshTask.run();

            }
        });
    }

    private void resetFilter() {
        voicePath = "";
        orderType = orderContent = "";
        skill = "";
        gender = area = "-1";
        phoneNumber = "";
        budget = "0";
        submitTime = 0;
        latitude = longitude = 0;
        minDistance = maxDistance = 0;
        direction = -1;
        contentField.setOrderValue(orderType, orderContent);
    }


    public OrderUserDialog getUserDialog() {
        return userDialog;
    }

    public void setUserDialog(OrderUserDialog userDialog) {
        this.userDialog = userDialog;
    }

    /**
     * voice recognition
     * @param path
     */
    public void startRecognition(String path) {

        GlobalVars.showWaitDialog(ctx);

        Intent intent = new Intent();
        bindParams(intent, path);
        speechEndTime = -1;
        speechRecognizer.startListening(intent);
    }

    public void bindParams(Intent intent, String path) {
//        path = "sdcard/app.wav";
        intent.putExtra(EXTRA_INFILE, path);

    }

    @Override
    public void onReadyForSpeech(Bundle params) {

    }

    @Override
    public void onBeginningOfSpeech() {

    }

    @Override
    public void onRmsChanged(float rmsdB) {

    }

    @Override
    public void onBufferReceived(byte[] buffer) {

    }

    @Override
    public void onEndOfSpeech() {

    }

    @Override
    public void onError(int error) {
        StringBuilder sb = new StringBuilder();
        switch (error) {
            case SpeechRecognizer.ERROR_AUDIO:
                sb.append("音频问题");
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                sb.append("没有语音输入");
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                sb.append("其它客户端错误");
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                sb.append("权限不足");
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                sb.append("网络问题");
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                sb.append("没有匹配的识别结果");
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                sb.append("引擎忙");
                break;
            case SpeechRecognizer.ERROR_SERVER:
                sb.append("服务端错误");
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                sb.append("连接超时");
                break;
        }
        sb.append(":" + error);

        Toast.makeText(ctx, "识别失败：" + sb.toString(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResults(Bundle results) {
        GlobalVars.hideWaitDialog();
        ArrayList<String> nbest = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        String strResult =  Arrays.toString(nbest.toArray(new String[nbest.size()]));
        if (strResult.length() < 6) {
            return;
        }
        strResult = strResult.substring(1, strResult.length() - 1);
        orderType = strResult.substring(0, 6);
        orderContent = strResult.substring(6);
        submitOrderAction();
    }

    @Override
    public void onPartialResults(Bundle partialResults) {

    }

    @Override
    public void onEvent(int eventType, Bundle params) {

    }

    private long speechEndTime = -1;
    public static final String EXTRA_INFILE = "infile";
    private SpeechRecognizer speechRecognizer;



}
