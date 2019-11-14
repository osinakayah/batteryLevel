package com.example.batterylevel;
import com.element.camera.ElementFaceSDK;

//io.flutter.app.FlutterApplication
public class FirstBankApplication extends  io.flutter.app.FlutterApplication  {
    @Override
    public void onCreate() {
        super.onCreate();
        ElementFaceSDK.initSDK(this);
    }
}
