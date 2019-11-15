package com.example.batterylevel;

import android.os.Build;
import android.util.Base64;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import okhttp3.Request;

public class Utils {
    static void addHttpHeaders(Request.Builder builder, String apiKey, String userId, String clientId) {
        long timestamp = System.currentTimeMillis() / 1000;
        String hashTokenPayload = apiKey + timestamp + userId;

        builder.addHeader("appVersion", BuildConfig.VERSION_NAME);
        builder.addHeader("os", "ANDROID");
        builder.addHeader("appId", BuildConfig.APPLICATION_ID);
        builder.addHeader("deviceModel", Build.MODEL);
        builder.addHeader("sdkVersion", "1.0");
        builder.addHeader("clientId", clientId);
        builder.addHeader("timestamp", "" + timestamp);
        builder.addHeader("hashToken", generateHashToken(hashTokenPayload));
    }

    private static String generateHashToken(String payload) {
        try {
            String signatureMethod = "SHA-512";
            MessageDigest messageDigest = MessageDigest.getInstance(signatureMethod);
            byte[] signatureBytes = messageDigest.digest(payload.getBytes());
            return Base64.encodeToString(signatureBytes, Base64.NO_WRAP);
        } catch (NoSuchAlgorithmException ignored) {
            return "";
        }
    }
}
