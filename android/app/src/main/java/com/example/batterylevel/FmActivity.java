package com.example.batterylevel;


import com.element.camera.ElementFaceCaptureActivity;

import android.app.Activity;
import android.os.Bundle;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Base64;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.element.camera.Capture;
import com.element.common.PermissionUtils;
import com.google.gson.Gson;

import java.net.HttpURLConnection;

import okhttp3.Response;

public class FmActivity extends ElementFaceCaptureActivity {
    @Override
    protected void onCreate(Bundle bundle) {
        getIntent().putExtra(EXTRA_ELEMENT_USER_ID, getString(R.string.user_id));
        getIntent().putExtra(EXTRA_LIVENESS_DETECTION, true);
        getIntent().putExtra(EXTRA_TUTORIAL, false);
        getIntent().putExtra(EXTRA_SECONDARY_TUTORIAL, false);

        super.onCreate(bundle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!PermissionUtils.isGranted(getBaseContext(), android.Manifest.permission.CAMERA)) {
            toastMessage("Please grant all permissions in Settings -> Apps");
            finish();
        }
    }

    @SuppressLint("PrivateResource")
    @Override
    public void onImageCaptured(@Nullable Capture[] captures, @NonNull String code) {
        Capture capture = captures[0];
        String a = Base64.encodeToString(capture.data, Base64.DEFAULT);

        if (CAPTURE_RESULT_OK.equals(code) || CAPTURE_RESULT_GAZE_OK.equals(code) || CAPTURE_STATUS_VALID_CAPTURES.equals(code)) {
            toastMessage(R.string.processing);

            if (captures != null) {
                new FmTask(faceMatchingTaskCallback, captures).execute();
            }
        } else if (CAPTURE_RESULT_NO_FACE.equals(code) || CAPTURE_RESULT_GAZE_FAILED.equals(code)) {
            showResult(getString(R.string.capture_failed), R.drawable.common_google_signin_btn_icon_dark);
        }
    }

    void recapture() {
        finish();

        overridePendingTransition(0, 0);
        Intent intent = getIntent();
        intent.setClass(getBaseContext(), getClass());
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    private void showResult(String message, int iconResId) {
        Intent intent = getIntent();
        intent.setClass(getBaseContext(), MainActivity.class);
        intent.putExtra("FM_MESSAGE", message);
        setResult(Activity.RESULT_OK, intent);
        finish();
//        FmResultFragment fragment = new FmResultFragment();
//        fragment.setData(message, iconResId);
//        fragment.show(getSupportFragmentManager(), null);
    }

    private FmTask.FaceMatchingTaskCallback faceMatchingTaskCallback = new FmTask.FaceMatchingTaskCallback() {
        @Override
        public String getUrl() {
            return getString(R.string.api_url);
        }

        @Override
        public String apiKey() {
            return getString(R.string.api_key);
        }

        @Override
        public String userId() {
            return getString(R.string.user_id);
        }

        @Override
        public String clientId() {
            return getString(R.string.client_id);
        }

        @Override
        public void onResult(Response response) throws Exception {
            if (response.code() == HttpURLConnection.HTTP_OK) {
                FmTask.FmResponse fmResponse = new Gson().fromJson(response.body().string(), FmTask.FmResponse.class);
                showResult(fmResponse.displayMessage, R.drawable.common_google_signin_btn_icon_dark);
            } else {
                FmTask.ServerMessage serverMessage = new Gson().fromJson(response.body().string(), FmTask.ServerMessage.class);
                showResult(serverMessage.message, R.drawable.common_google_signin_btn_icon_dark);
            }
        }

        @Override
        public void onException(String message) {
            showResult(message, R.drawable.common_google_signin_btn_icon_dark);
        }
    };
}
