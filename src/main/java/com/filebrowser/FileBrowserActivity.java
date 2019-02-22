package com.filebrowser;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.filebrowser.Storage.FileStorage;

import java.io.File;

public class FileBrowserActivity extends Activity implements
        View.OnClickListener, MyAdapter.FileSelectListener {
    private TextView curPathTextView;
    private String rootPath = "";
    private MyAdapter listAdapter;
    //初始化进入的目录，默认目录
    private String filePath = "";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_browser_acitivity);
        initView();

        //跟目录
        rootPath = getIntent().getStringExtra("rootPath");
        //指定文件夹
        filePath = getIntent().getStringExtra("path");



        curPathTextView.setText(filePath);
        filePath = filePath.isEmpty() ? rootPath : filePath;
        View layoutFileSelectList = findViewById(R.id.layoutFileSelectList);
        listAdapter = new MyAdapter(layoutFileSelectList, rootPath, filePath);
        listAdapter.setOnFileSelectListener(this);

        findViewById(R.id.btnSure).setOnClickListener(this);
        findViewById(R.id.btnCancel).setOnClickListener(this);
    }

    @Override
    public void finish() {
        Intent intent = new Intent();
        intent.putExtra("file", filePath);
        setResult(RESULT_OK, intent);
        super.finish();
    }

    private void initView() {
        curPathTextView = (TextView) findViewById(R.id.curPath);
    }


    @Override
    public void onFileSelect(File selectedFile) {
        filePath = selectedFile.getPath();
    }

    @Override
    public void onDirSelect(File selectedDir) {
        filePath = selectedDir.getPath();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnSure:
                finish();
                break;
            case R.id.btnCancel:
                filePath ="";
                finish();
                break;

            default:
                break;

        }
    }

}
