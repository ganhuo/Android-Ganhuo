package com.ganhuo.app;

import android.app.Application;

import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.PushService;
import com.ganhuo.app.models.Entry;

import im.fir.sdk.FIR;

public class ApplicationPoint extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FIR.init(this);
        AVObject.registerSubclass(Entry.class);
        AVOSCloud.initialize(getApplicationContext(), "id77coqgtrm74f6esc617hhqxe4bo8icg17al5998lcdxswy", "qoj2a3fo8icot1qigiemnd36zjdfir45d9qapxna6ky7l0gs");
        AVInstallation.getCurrentInstallation().saveInBackground();
        PushService.setDefaultPushCallback(this, MainActivity.class);
    }
}
