package com.example.batterylevel;

import android.os.AsyncTask;
import android.util.Base64;

import com.element.camera.Capture;
import com.google.gson.Gson;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FmTask  extends AsyncTask<Object, Void, Void> {
    private static final MediaType TYPE = MediaType.parse("application/json; charset=utf-8");

    private final FaceMatchingTaskCallback callback;

    private final Capture[] captures;

    private final Request.Builder builder;

    FmTask(FaceMatchingTaskCallback callback, Capture[] captures) {
        this.callback = callback;
        this.captures = captures;
        this.builder = new Request.Builder().url(callback.getUrl());
    }

    @Override
    protected Void doInBackground(Object... params) {
        OkHttpClient client = new OkHttpClient();
        Utils.addHttpHeaders(builder, callback.apiKey(), callback.userId(), callback.clientId());

        try {
            FmRequest fmRequest = new FmRequest();
            fmRequest.cardId = callback.userId();
            fmRequest.setImages(captures);

            String json = new Gson().toJson(fmRequest);
            RequestBody requestBody = RequestBody.create(TYPE, json);
            builder.post(requestBody);

            Request request = builder.build();
            Response response = client.newCall(request).execute();
            callback.onResult(response);
        } catch (Exception e) {
            callback.onException(e.getMessage());
        }
        return null;
    }

    private static class FmRequest {
        String cardId;
        // String userId;
        Image[] images;

        void setImages(Capture[] imageData) {
            images = new Image[imageData.length];
            int i = 0;
            for (Capture capture : imageData) {
                images[i] = new Image();
                images[i].index = i;
                images[i].modality = "face";
                images[i].mode = "gaze";
                images[i].data = Base64.encodeToString(capture.data, Base64.DEFAULT);
                images[i].tag = capture.tag;
                i++;
            }
        }
    }

    static class Image {
        int index;
        String modality;
        String mode;
        String data;
        String tag;
    }

    static class FmResponse {
        String displayMessage;
    }

    static class ServerMessage {
        String message;
    }

    interface FaceMatchingTaskCallback {
        String getUrl();

        String apiKey();

        String userId();

        String clientId();

        void onResult(Response response) throws Exception;

        void onException(String message);
    }
}
