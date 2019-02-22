package com.filebrowser;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.CompoundButton;
import android.widget.Switch;

import static com.filebrowser.R.id.switchButton;

/**
 * Created by Tll on 2018/11/25.
 */

public class MyService extends Service {



    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("创建服务");
    }

    @Override
    public int onStartCommand(Intent intent,int flags, int startId) {
        System.out.println("启动服务...");      //这里实现服务的核心业务



        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        System.out.println("停止服务");
        super.onDestroy();
    }
}
