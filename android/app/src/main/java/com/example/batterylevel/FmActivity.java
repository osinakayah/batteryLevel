package com.example.batterylevel;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import okhttp3.Response;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.element.camera.Capture;
import com.element.camera.ElementFaceCaptureActivity;
import com.google.gson.Gson;

import java.net.HttpURLConnection;

import android.content.Intent;

public class FmActivity extends ElementFaceCaptureActivity {
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getIntent().putExtra(EXTRA_USER_APP_ID, getPackageName());
        getIntent().putExtra(EXTRA_ELEMENT_USER_ID, getString(R.string.user_id));



        super.onCreate(savedInstanceState);
    }

    @Override
    public void onImageCaptured(@Nullable Capture[] captures, @NonNull String code) {
        if (CAPTURE_RESULT_OK.equals(code) || CAPTURE_RESULT_GAZE_OK.equals(code) || CAPTURE_STATUS_VALID_CAPTURES.equals(code)) {
            if (progressDialog == null) {
                progressDialog = new ProgressDialog(FmActivity.this);
            }
            showProgressDialog(FmActivity.this, getString(R.string.processing));

            if (captures != null) {
                String userId = getIntent().getStringExtra(EXTRA_ELEMENT_USER_ID);
                new FmTask(faceMatchingTaskCallback).execute(userId, captures);
            }
        } else if (CAPTURE_RESULT_NO_FACE.equals(code) || CAPTURE_RESULT_GAZE_FAILED.equals(code)) {
            sendBackResult(false, getString(R.string.capture_failed));
//            showResult(getString(R.string.capture_failed), R.drawable.icon_focus);
        }
    }
    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    private void sendBackResult(final boolean wasSuccessful, final String message){

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("FM_MESSAGE", message);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
    private FmTask.FaceMatchingTaskCallback faceMatchingTaskCallback = new FmTask.FaceMatchingTaskCallback() {
        @Override
        public Context getContext() {
            return getBaseContext();
        }

        @Override
        public void onResult(Response response) throws Exception {

            if (response.code() == HttpURLConnection.HTTP_OK) {
                FmTask.FmResponse fmResponse = new Gson().fromJson(response.body().string(), FmTask.FmResponse.class);
                // showResult(fmResponse.displayMessage, R.drawable.common_google_signin_btn_icon_dark_normal);
                sendBackResult(true, fmResponse.displayMessage);
            } else {
                FmTask.ServerMessage serverMessage = new Gson().fromJson(response.body().string(), FmTask.ServerMessage.class);
                // showResult(serverMessage.message, R.drawable.icon_focus);
                sendBackResult(false, serverMessage.message);
            }
            dismissProgressDialog(FmActivity.this);
        }

        @Override
        public void onException(String message) {
            // showResult(message, R.drawable.ring_blue);
            sendBackResult(false, message);
            dismissProgressDialog(FmActivity.this);
        }
    };
    private void showProgressDialog(final Activity activity, final String msg) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progressDialog == null) {
                    progressDialog = new ProgressDialog(new ContextThemeWrapper(
                            activity, android.R.style.Theme_DeviceDefault_Light));
                }

                if (null != msg && msg.length() > 0) {
                    progressDialog.setMessage(msg);
                }

                progressDialog.setCancelable(false);
                progressDialog.show();
            }
        });
    }

    private void dismissProgressDialog(final Activity activity) {
        if (progressDialog != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialog.dismiss();
                }
            });
        }
    }
}
