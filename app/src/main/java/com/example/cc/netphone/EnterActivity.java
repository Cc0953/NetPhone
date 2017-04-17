package com.example.cc.netphone;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import org.litepal.crud.DataSupport;

import android.content.Intent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.json.JSONArray;

import android.os.Bundle;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;

import okhttp3.Response;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;

public class EnterActivity extends AppCompatActivity {

    private String[] DepartmentID = new String[50];
    private String[] DepartmentName = new String[50];
    private String[] DepartmentPic = new String[50];
    private static final String URL_GetDepartment = "http://114.115.208.226:8080/Telephone/selectAllDepartmentAndroid.action";
    private int getIntentInt = 0;
    private List<DepartmentDataBases> dataBases;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enter_layout);
        dataBases = DataSupport.findAll(DepartmentDataBases.class);
        if (dataBases.size() == 0) {
            Log.d("提示", "加载部门" + dataBases.size());
            getDepartmentJson();//从网络获取部门信息
        } else if (dataBases.size() > 0) {
            Log.d("提示", "部门数据已加载");
            getPermission(); //获取权限以及下载图片
        }

    }
    private void getPermission() {
        if (ContextCompat.checkSelfPermission(MyApplocation.getContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(EnterActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            getPicFromWeb();//从网络下载图片
        }
    }
    /**
     * 从Web获取图片
     */
    private void getPicFromWeb() {
        Log.d("提示", "我是下载图片");
        Intent intent = getIntent();
        getIntentInt = intent.getIntExtra("int", 0);
        String path = Environment.getExternalStorageDirectory() + "/tu" + 25 + ".png";
        File file = new File(path);
        if (file.exists() && getIntentInt == 0) {
            Log.d("提示", "图片25存在:" + path);
            //跳转
            try {
                Timer timer = new Timer();
                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        Log.d("提示", "准备跳转");
                        EnterToMain();
                    }
                };
                timer.schedule(task, 1000 * 3);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (!file.exists()||getIntentInt == 1) {
            Log.d("提示", "图片25不存在" + path);
            AddPictureData();
        }
    }

    /**
     * 从Web获取部门信息
     */
    private void getDepartmentJson() {
        MyJson.receiveHttpWithJson(URL_GetDepartment, new okhttp3.Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("提示", "加载部门1");
                String Response = response.body().string();
                parseDepartmentJsonWithJSONObject(Response);
            }

            @Override
            public void onFailure(Call call, IOException e) {
            }
        });
    }

    /**
     * 解析从Web中得到的部门Json
     */
    private void parseDepartmentJsonWithJSONObject(String response) {
        try {
            Log.d("提示", "加载部门2");
            JSONArray jsonArray = new JSONArray(response);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                DepartmentID[i] = jsonObject.getString("id");
                DepartmentName[i] = jsonObject.getString("name");
                DepartmentPic[i] = jsonObject.getString("pic");
                addDataBasesDepartment(DepartmentID[i], DepartmentName[i], DepartmentPic[i]);
            }
            Log.d("提示", "加载部门完成");
            getPermission();//获取权限以及下载图片
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 添加部门数据
     */
    private void addDataBasesDepartment(String ID, String Name, String Pic) {
        Log.d("提示", "加载部门3");
        DepartmentDataBases department = new DepartmentDataBases();
        department.setDepartmentID(ID);
        department.setDepartment(Name);
        department.setDepartmentPic(Pic);
        department.save();
    }

    private void AddPictureData() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                dataBases = DataSupport.findAll(DepartmentDataBases.class);
                Log.d("提示", "部门数量" + dataBases.size());
                downLoadPic(dataBases.size(), dataBases);
                Log.d("提示", "加载完成");
                EnterToMain();
            }
        }).start();
    }

    public void downLoadPic(int DataSize, List<DepartmentDataBases> dataBasesForPic) {
        Log.d("提示", "准备加载图片");
        for (int i = 0; i < DataSize; i++) {
            String UrlPath = "http://114.115.208.226:8080" + dataBasesForPic.get(i).getDepartmentPic();
            Bitmap bitmap;
            String PicName = "tu" + i;
            try {
                bitmap = Glide.with(EnterActivity.this).
                        load(UrlPath)
                        .asBitmap()
                        .centerCrop()
                        .into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                        .get();
                saveBitmapFile(bitmap, PicName);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 将bitmap存储为File
     *
     * @param bitmap
     * @param PicName
     */
    public void saveBitmapFile(Bitmap bitmap, String PicName) {

        String dir = Environment.getExternalStorageDirectory().getPath();
        File dirFile = new File(dir);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        File file = new File(dirFile, PicName + ".png");
        Log.d("提示", dirFile + "/" + PicName + ".png");
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 延时跳转
     */
    public void EnterToMain() {
        Log.d("提示", "我是跳转");
        Intent intent = new Intent(EnterActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getPicFromWeb();
                } else {
                    Snackbar.make(getWindow().getDecorView(), "没有相关权限", Snackbar.LENGTH_SHORT).show();
                }
        }
    }


}
