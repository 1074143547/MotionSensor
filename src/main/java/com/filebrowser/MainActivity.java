package com.filebrowser;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Message;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RatingBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;



import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import com.filebrowser.*;
import com.filebrowser.Storage.FileStorage;
//app.xyz.lailin.Rsensor.Storage.FileStorage;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.github.mikephil.charting.charts.Chart.LOG_TAG;


public class MainActivity extends Activity {
    public boolean isSaveData=false;
    public static final int FILE_RESULT_CODE = 1;
    private Button btn_open;
    private TextView changePath;
    private String rootPath;
    private String hpath;
    public String  filePath;
    TextView time1;
    static  final int msgKey2 = 1;



    private ChartView chart;
    LineChart mLineChart;


    private List<String> accData=new ArrayList<String>();



    private boolean flag = false ;
    boolean tag = false;
    FileStorage fileStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initListener();

        fileStorage = new FileStorage("a");

        String TAG = "onclick";

        Log.i(TAG,"启动服务");
        Intent startIntent = new Intent(this,MyService.class);
        startService(startIntent);     //启动服务

        // Intent stopIntent = new Intent(this,MyService.class);
        //stopService(stopIntent);   //停止服务
        //break;

        time1 = (TextView) findViewById(R.id.tv_time1);
        new  TimeThread().start();

        mLineChart=(LineChart)findViewById(R.id.chart);

        chart=new ChartView(mLineChart);

        //加速度传感器初始化
        final Acceleration acceleration=new Acceleration();

        //文件保存
        //用filestorage类创了个新的文件夹



//获取switch btn用于开关
        final Switch switchButton=(Switch)findViewById(R.id.switchButton);

        //通过switch btn的状态设置是否监听

        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    acceleration.register();
                    toast("测试开始！");
                }else {
                    acceleration.unRegister();
                    if (isSaveData){
                        fileStorage.addData(accData);

                        java.util.Date d = new java.util.Date();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);//当前时间获取，作为txt文档的名字
                        getAlbumStorageDir(filePath);


                        copyFile(fileStorage.getFile().getPath(),hpath+"/"+sdf.format(d)+".txt");
                        toast("测试结束！文件已保存在："+hpath+"/"+sdf.format(d)+".txt");
                    }else {
                        toast("测试结束！");
                    }
                }
                switchButton.setChecked(isChecked);
            }
        });


        final Switch isSaveSwitch=(Switch) findViewById(R.id.isSave);
        isSaveSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener(){

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (flag) {
                    isSaveData = isChecked;
                }
            }
        });



    }

    private void initView() {
        btn_open = (Button) findViewById(R.id.btn_open);
        changePath = (TextView) findViewById(R.id.changePath);
    }

    private void initListener() {
        btn_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openBrowser();
            }
        });

        findViewById(R.id.btn_open1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openBrowser1();
            }
        });
    }

    private void openBrowser() {

        //rootPath = System.getenv("SECONDARY_STORAGE");
        rootPath = System.getenv("hpath");
        if (rootPath == null) {
            rootPath = Environment.getExternalStorageDirectory().toString();
        }
        if ((rootPath.equals(Environment.getExternalStorageDirectory().toString()))) {
            filePath = rootPath + "/hpath";
            Intent intent = new Intent(MainActivity.this, FileBrowserActivity.class);
            //根目录
            intent.putExtra("rootPath", rootPath);
            //进去指定文件夹
            intent.putExtra("path", filePath);
            startActivityForResult(intent, FILE_RESULT_CODE);
        }
    }

    private void openBrowser1() {

        rootPath = getSdcardPath();
        if (rootPath == null || rootPath.isEmpty()) {
            rootPath = Environment.getExternalStorageDirectory().toString();
        }
        //filePath = rootPath + "/hpath";
        Intent intent = new Intent(MainActivity.this, FileBrowserActivity.class);
        intent.putExtra("rootPath", rootPath);
       intent.putExtra("path", filePath);
        startActivityForResult(intent, FILE_RESULT_CODE);
    }

    public String getSdcardPath() {
        String sdcardPath = "";
        String[] pathArr = null;
        StorageManager storageManager = (StorageManager) getSystemService(STORAGE_SERVICE);
        try {
            Method getVolumePaths = storageManager.getClass().getMethod("getVolumePaths");
            pathArr = (String[]) getVolumePaths.invoke(storageManager);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        if (pathArr != null && pathArr.length >= 3) {
            sdcardPath = pathArr[1];
        }
        return sdcardPath;
    }

    @Override
    //protected
   //public void onActivityResult(int requestCode, int resultCode, Intent data) {
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (FILE_RESULT_CODE == requestCode) {
            Bundle bundle = null;
            if (data != null && (bundle = data.getExtras()) != null) {
                String path = bundle.getString("file","");
                if(!path.isEmpty()){
                    changePath.setText("选择路径为 : " + path);
                   flag=true;
                    hpath =path;
                }
            }
        }

    }
//之后是画图的功能
    public void restart(View v){
        accData=new ArrayList<>();
        chart.reset();
        chart=null;
        chart=new ChartView(mLineChart);
    }

    private void lineControl(Switch mSwitch, final int id){
        mSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener(){
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                chart.getDataSets().get(id).setVisible(isChecked);
            }
        });
    }

    private void toast(String string){
        Toast toast = Toast.makeText(MainActivity.this, string, Toast.LENGTH_SHORT);
        toast.show();
    }


    /**
     * 加速度传感器的相关设置，以及监听
     */
    public class Acceleration {

        /**
         * 上一次的值
         */
        private float[] prevData = new float[3];

        private float comfort = 0.0f;

        /**
         * 传感器控制
         */
        private SensorManager mSensorManager = null;

        /**
         * 传感器
         */
        private Sensor mSensor = null;

        /**
         * 构造函数，传感器初始化
         */
        Acceleration() {

            //获取系统服务（SENSOR_SERVICE）返回一个SensorManager对象
            mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

            //通过SensorManager获取相应的（加速度感应器）Sensor类型对象
            mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }

        /**
         * 注册加速度传感器
         */
        void register() {
            mSensorManager.registerListener(mSensorEventListener, mSensor, SensorManager.SENSOR_DELAY_FASTEST);
        }

        /**
         * 取消注册加速度传感器
         */
        void unRegister() {
            mSensorManager.unregisterListener(mSensorEventListener, mSensor);
        }

        /**
         * 加速度传感器监听方法
         */
        private final SensorEventListener mSensorEventListener = new SensorEventListener() {

            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                    float x = event.values[0];
                    float y = event.values[1];
                    float z = event.values[2];

                    if (prevData != null) {
                        comfort = 0f;
                        for (int i = 0; i < 3; i++) {
                            if (Math.abs(prevData[i] - event.values[i]) > comfort) {
                                comfort = Math.abs(prevData[i] - event.values[i]);
                            }
                        }
                        //setComfortRate(rate);
                    }

                    System.arraycopy(event.values, 0, prevData, 0, 3);


                /*显示左右、前后、垂直方向加速度*/
                    DecimalFormat decimalFormat = new DecimalFormat("00.00");
                 //   accData.add(decimalFormat.format(x) + "\t" + decimalFormat.format(y) + "\t" + decimalFormat.format(z));

                    long time = System.currentTimeMillis();
                    Date date = new Date(time);
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd, HH:mm:ss ");
                    //写入数据到txt文档中
                    accData.add(format.format(date)+" ,"+decimalFormat.format(x) + "," + decimalFormat.format(y) + "," + decimalFormat.format(z));

                    chart.addData(x, 0);
                    chart.addData(y, 1);
                    chart.addData(z, 2);


                }
            }



            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {


            }
        };



    }

    private File getAlbumStorageDir(String dirName) {
        // Get the directory for the user's public pictures directory.
        // File file = new File(Environment.getExternalStorageDirectory(), dirName);
        File file = new File(Environment.getExternalStorageDirectory(), dirName);
        if (!file.exists()&&!file.mkdirs()) {
            Log.e(LOG_TAG, "Directory not created");
        }
        return file;
    }

    public class TimeThread extends  Thread{
        @Override
        public void run() {
            super.run();
            do{
                try {
                    Thread.sleep(1000);
                    Message msg = new Message();
                    msg.what = msgKey2;
                    tHandler.sendMessage(msg);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }while (true);
        }
    }

    private Handler tHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case msgKey2:
                    long time = System.currentTimeMillis();
                    Date date = new Date(time);
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd, HH:mm:ss ");
                    time1.setText(format.format(date));
                    break;
                default:
                    break;
            }
        }

    };

    /**

     * 复制单个文件

     * @param oldPath String 原文件路径 如：c:/fqf.txt

     * @param newPath String 复制后路径 如：f:/fqf.txt

     * @return boolean

     */

    public void copyFile(String oldPath, String newPath) {

        try {

            int bytesum = 0;

            int byteread = 0;

            File oldfile = new File(oldPath);

            if (oldfile.exists()) { //文件存在时

                InputStream inStream = new FileInputStream(oldPath); //读入原文件

                FileOutputStream fs = new FileOutputStream(newPath);

                byte[] buffer = new byte[1444];

                int length;

                while ( (byteread = inStream.read(buffer)) != -1) {

                    bytesum += byteread; //字节数 文件大小

                    System.out.println(bytesum);

                    fs.write(buffer, 0, byteread);

                }

                inStream.close();

            }

        }

        catch (Exception e) {

            System.out.println("复制单个文件操作出错");

            e.printStackTrace();



        }

    }

}
