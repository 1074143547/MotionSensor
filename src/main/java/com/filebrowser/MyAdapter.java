package com.filebrowser;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MyAdapter extends BaseAdapter implements View.OnClickListener, AdapterView.OnItemClickListener {
    private String rootPath;
    private LayoutInflater mInflater;
    private Bitmap mIcon3;
    private Bitmap mIcon4;
    private List<File> fileList;
    private View header;
    private View layoutReturnRoot;
    private View layoutReturnPre;
    private TextView curPathTextView;
    private String suffix = "";
    private String currentDirPath;
    private FileSelectListener listener;

    public MyAdapter(View fileSelectListView, String rootPath, String defaultPath) {
        this.rootPath = rootPath;
        Context context = fileSelectListView.getContext();
        mInflater = LayoutInflater.from(context);
        mIcon3 = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_fodler);
        mIcon4 = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_file);

        curPathTextView = (TextView) fileSelectListView.findViewById(R.id.curPath);
        header = fileSelectListView.findViewById(R.id.layoutFileListHeader);
        layoutReturnRoot = fileSelectListView.findViewById(R.id.layoutReturnRoot);
        layoutReturnPre = fileSelectListView.findViewById(R.id.layoutReturnPre);
        layoutReturnRoot.setOnClickListener(this);
        layoutReturnPre.setOnClickListener(this);
        if (defaultPath != null && !defaultPath.isEmpty()) {
            getFileDir(defaultPath);
        } else {
            getFileDir(rootPath);
        }
        ListView listView = (ListView) fileSelectListView.findViewById(R.id.list);
        listView.setAdapter(this);
        listView.setOnItemClickListener(this);
    }

    private class ViewHolder {
        TextView text;
        ImageView icon;
    }

    public interface FileSelectListener {
        void onFileSelect(File selectedFile);

        void onDirSelect(File selectedDir);
    }

    public void setOnFileSelectListener(FileSelectListener listener) {
        this.listener = listener;
    }

    /**
     * 获取所选文件路径下的所有文件，并且更新到listview中
     */
    private void getFileDir(String filePath) {
        File file = new File(filePath);
        File[] files = file.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                if (pathname.isFile()&&!suffix.isEmpty()){
                    return pathname.getName().endsWith(suffix);
                }
                return true;
            }
        });
        fileList = Arrays.asList(files);
        //按名称排序
        Collections.sort(fileList, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                if (o1.isFile() && o2.isDirectory())
                    return 1;
                if (o1.isDirectory() && o2.isFile())
                    return -1;
                return o1.getName().compareTo(o2.getName());
            }
        });

        if (header != null) {
            header.setVisibility(filePath.equals(rootPath) ? View.GONE : View.VISIBLE);
        }

        notifyDataSetChanged();

        if (curPathTextView != null) {
            curPathTextView.setText(filePath);
        }
        currentDirPath = filePath;

        if (listener!=null){
            listener.onDirSelect(file);
        }
    }


    public int getCount() {
        return fileList.size();
    }

    public Object getItem(int position) {
        return fileList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.file_item, null);
            holder = new ViewHolder();
            holder.text = (TextView) convertView.findViewById(R.id.text);
            holder.icon = (ImageView) convertView.findViewById(R.id.icon);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        File file = fileList.get(position);
        holder.text.setText(file.getName());
        if (file.isDirectory()) {
            holder.icon.setImageBitmap(mIcon3);
        } else {
            holder.icon.setImageBitmap(mIcon4);
        }
        return convertView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        File file = fileList.get(position);
        if (file.isDirectory()) {
            getFileDir(file.getPath());
        } else {
            if (listener!=null){
                listener.onFileSelect(file);
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.layoutReturnRoot) {
            getFileDir(rootPath);
        } else if (v.getId() == R.id.layoutReturnPre) {
            getFileDir(new File(currentDirPath).getParent());
        }
    }
}