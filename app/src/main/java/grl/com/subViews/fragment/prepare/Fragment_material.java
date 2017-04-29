package grl.com.subViews.fragment.prepare;


import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import grl.com.activities.energy.tGroup.TGroupPrepareActivity;
import grl.com.configuratoin.Constant;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.SelfInfoModel;
import grl.com.httpRequestTask.FileUploadTask;
import grl.com.httpRequestTask.tGroup.LessonMaterialGetTask;
import grl.com.httpRequestTask.tGroup.LessonMaterialSetTask;
import grl.com.httpRequestTask.tGroup.LessonTimeGetTask;
import grl.com.network.HttpCallback;
import grl.wangu.com.grl.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_material extends Fragment implements View.OnClickListener {
    private TGroupPrepareActivity ctx;
    private View layout;

    EditText pathEdit;
    Button uploadBtn;
    Button downloadBtn;

    String teacherId;
    String  coursePath = "";

    public static final int REQUEST_PIC_SEL = 0;

    public Fragment_material() {
        // Required empty public constructor
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (layout == null) {
            ctx = (TGroupPrepareActivity)this.getActivity();
            layout = ctx.getLayoutInflater().inflate(R.layout.fragment_prepare_material,
                    null);
            teacherId = ctx.teacherId;
            getViewByID();
            setOnListener();
            initializeData();
        } else {
            ViewGroup parent = (ViewGroup) layout.getParent();
            if (parent != null) {
                parent.removeView(layout);
            }
        }
        return layout;
    }

    public void getViewByID () {
        pathEdit = (EditText)layout.findViewById(R.id.edit_file_url);
        uploadBtn = (Button)layout.findViewById(R.id.btn_upload_material);
        downloadBtn = (Button)layout.findViewById(R.id.btn_download_material);
    }

    public void setOnListener() {
        uploadBtn.setOnClickListener(this);
        downloadBtn.setOnClickListener(this);
    }

    public void initializeData () {
        refresh();
        pathEdit.setEnabled(false);
        if (teacherId.compareTo(SelfInfoModel.userID) != 0) {
            uploadBtn.setEnabled(false);
        }
    }

    public void refresh() {
        new LessonMaterialGetTask(ctx, teacherId, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object response) {
                if (!flag || response == null) {                 //failure
                    GlobalVars.showErrAlert(ctx);
                    return;
                }

                try {
                    JSONObject result = (JSONObject) response;
                    coursePath = GlobalVars.getStringFromJson(result, "course_path");
                } catch (Exception e) {
                    coursePath = "";
                    Log.e("MYAPP", "exception", e);
                }
                pathEdit.setText(coursePath);
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btn_upload_material:
                selectPictureFromLocal();
                break;
            case R.id.btn_download_material:
                download();
                break;

        }
    }

    private void selectPictureFromLocal() {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            ctx.startActivityForResult(intent, REQUEST_PIC_SEL);
        } else {
            intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            ctx.startActivityForResult(intent, REQUEST_PIC_SEL);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) { // 清空消息

            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = ctx.getContentResolver().query(
                    selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String filePath = cursor.getString(columnIndex);
            cursor.close();

            savePath(filePath);

        }
    }

    public void savePath(String path) {

        new FileUploadTask(ctx, path, Constant.MSG_IMAGE_TYPE, new HttpCallback() {

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
                        coursePath = GlobalVars.SEVER_ADDR + "/" + filePath;
                        new LessonMaterialSetTask(ctx, teacherId, coursePath, new HttpCallback() {
                            @Override
                            public void onResponse(Boolean flag, Object response) {
                                if (!flag || response == null) {                 //failure
                                    GlobalVars.showErrAlert(ctx);
                                    return;
                                }
                                try {
                                    JSONObject result = (JSONObject) response;
                                    Boolean success = GlobalVars.getBooleanFromJson(result, "result");
                                    if (success) {
                                        Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.save_success), Toast.LENGTH_SHORT)
                                                .show();
                                        refresh();
                                    } else {
                                        GlobalVars.showErrAlert(ctx);
                                    }
                                } catch (Exception e) {
                                    Log.e("MYAPP", "exception", e);
                                }
                            }
                        });

                    } else {
                        GlobalVars.showErrAlert(ctx);
                        return;
                    }
                } catch (Exception e) {

                }

            }
        });


    }

    public void download() {
        if (coursePath == null)
            return;
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(coursePath));
        startActivity(browserIntent);
    }
}