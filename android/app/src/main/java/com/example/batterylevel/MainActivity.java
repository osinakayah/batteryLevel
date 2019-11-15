package com.example.batterylevel;

import android.os.Bundle;
import io.flutter.app.FlutterActivity;
import io.flutter.plugins.GeneratedPluginRegistrant;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import android.content.Intent;
import androidx.annotation.Nullable;

public class MainActivity extends FlutterActivity {
  private static final String CHANNEL = "samples.flutter.dev/battery";
  private static final int FACE_SCAN_REQUEST_CODE = 1;
  public MethodChannel.Result _result;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    GeneratedPluginRegistrant.registerWith(this);



    new MethodChannel(getFlutterView(), CHANNEL).setMethodCallHandler(new MethodCallHandler() {
      @Override
      public void onMethodCall( MethodCall call, MethodChannel.Result result) {
        if (call.method.equals("invokeFMCheck")) {
          if (_result == null) {
            _result = result;
          }
          startActivityC();
        } else {
          result.notImplemented();
        }
      }
    });
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == FACE_SCAN_REQUEST_CODE ) {
      String result = data.getStringExtra("FM_MESSAGE");
      if (result == null) {
        _result.success("I dunno y");
      }
      else {
        _result.success(result);
      }
    }

  }

  private void startActivityC(){
    final Intent intent = new Intent(this, FmActivity.class);
    startActivityForResult(intent, FACE_SCAN_REQUEST_CODE);
  }

}
