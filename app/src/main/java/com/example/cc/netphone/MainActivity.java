package com.example.cc.netphone;


import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import android.support.v7.widget.Toolbar;

import org.json.JSONException;

import java.io.File;
import java.util.Collections;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import okhttp3.Response;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;

public class MainActivity extends AppCompatActivity {

    private Phone[] phone;
    private String[] departments;
    private File[] files;
    private List<Phone> phoneList = new ArrayList<>();
    private String[] phoneName = new String[600];
    private String[] Department = new String[600];
    private String[] phoneNumber = new String[600];
    private String[] phoneNumber1 = new String[600];
    private String[] phoneNumber2 = new String[600];
    private static final String URL_GetPerson = "http://114.115.208.226:8080/Telephone/selectAllPersonAndroid.action";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //加载部门数据
        AddDepartmentData();
        //加载图片
        AddDepartmentPic();
        //加载布局
        setContentView(R.layout.activity_main);
        //对数据的第一次访问处理
        FirstInquire();
        //加载标题
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        //初始化界面
        initPhone();
        //recyclerView
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView_phone);
        //GridLayoutManager
        GridLayoutManager layoutManage = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(layoutManage);
        //phoneAdapter
        PhoneAdapter phoneadapter = new PhoneAdapter(phoneList);
        recyclerView.setAdapter(phoneadapter);
    }

    private void AddDepartmentData() {
        List<DepartmentDataBases> dataBases = DataSupport.findAll(DepartmentDataBases.class);
        departments = new String[dataBases.size()];
        for (int i = 0; i < dataBases.size(); i++) {
            departments[i] = dataBases.get(i).getDepartment();
        }
    }

    private void AddDepartmentPic() {
        files = new File[departments.length];
        for (int i = 0; i < files.length; i++) {
            File file = new File(Environment.getExternalStorageDirectory().getPath() + "/tu" + i + ".png");
            files[i] = file;
        }
    }

    private void FirstInquire() {
        phone = new Phone[files.length];
        for (int i = 0; i < departments.length; i++) {
            phone[i] = new Phone(files[i], departments[i]);
        }
        List<PhoneDataBases> phoneDataBases = DataSupport.findAll(PhoneDataBases.class);
        if (phoneDataBases.size() > 0) {
            //doNothing
        } else if (phoneDataBases.size() == 0) {
            if (isNetworkAvailable(MainActivity.this)) {
                //更新数据库
                InitDataBase();
            } else {
                Snackbar.make(getWindow().getDecorView(), "首次使用，请联网后点击右上角更新数据", Snackbar.LENGTH_LONG).show();
            }
        }
    }

    /**
     * 初始化界面
     */
    private void initPhone() {
        phoneList.clear();
        Collections.addAll(phoneList, phone);
    }

    /**
     * 初始化数据库
     * bug: 在初始化之前已经添加过一次部门信息
     */
    private void InitDataBase() {
        //建立数据库
        DataSupport.deleteAll(PhoneDataBases.class);
        Connector.getDatabase();
        getPersonJson();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.upData:
                if (isNetworkAvailable(MainActivity.this)) {
                    //删除图片

                    for (int i = departments.length; i >=0; i--) {
                        File file = new File(Environment.getExternalStorageDirectory() + "/tu" + i + ".png");
                        if (file.exists()) {
                            file.delete();
                            Log.d("提示", "tu"+i+".png 删除");
                        }
                    }
                    InitDataBase();
                    DataSupport.deleteAll(DepartmentDataBases.class);//删除数据库
                    Snackbar.make(getWindow().getDecorView(), "更新过程中···请勿点击", Snackbar.LENGTH_SHORT).show();
                    final Intent intent = new Intent(MainActivity.this, EnterActivity.class);
                    Timer timer = new Timer();
                    TimerTask task = new TimerTask() {
                        @Override
                        public void run() {
                            intent.putExtra("int",1);
                            startActivity(intent);
                            MainActivity.this.finish();
                        }
                    };
                    timer.schedule(task, 1000 * 3);
                } else {
                    Snackbar.make(getWindow().getDecorView(), "无网络", Snackbar.LENGTH_LONG).show();
                }
                break;
            case R.id.Search:
                Intent intent = new Intent(MainActivity.this, Search.class);
                startActivity(intent);
                break;
        }
        return true;
    }

    /**
     * 访问web获得人员数据
     */
    public void getPersonJson() {
        MyJson.receiveHttpWithJson(URL_GetPerson, new okhttp3.Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String Response = response.body().string();

                parsePersonJsonWithJSONObject(Response);
            }

            @Override
            public void onFailure(Call call, final IOException e) {
            }
        });

    }

    /**
     * 解析从Web中得到的人员Json
     */
    private void parsePersonJsonWithJSONObject(String response) {
        try {
            JSONArray jsonArray = new JSONArray(response);
            Log.d("responsePerson", jsonArray.toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                phoneName[i] = jsonObject.getString("name");
                Department[i] = jsonObject.getString("department_id");
                phoneNumber[i] = jsonObject.getString("phone1");
                phoneNumber1[i] = jsonObject.getString("phone2");
                phoneNumber2[i] = jsonObject.getString("phone3");
                addDataBasesPerson(phoneName[i], Department[i], phoneNumber[i], phoneNumber1[i], phoneNumber2[i]);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 添加人员数据
     */
    private void addDataBasesPerson(String name, String department, String phoneNum, String phoneNum1, String phoneNum2) {
        PhoneDataBases phone = new PhoneDataBases();
        phone.setName(name);
        phone.setDepartment(department);
        phone.setPhone1(phoneNum);
        phone.setPhone2(phoneNum1);
        phone.setPhone3(phoneNum2);
        phone.save();
    }


    /**
     * 检测当的网络（WLAN、3G/2G）状态
     *
     * @param context Context
     * @return true 表示网络可用
     */

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                // 当前网络是连接的
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    // 当前所连接的网络可用
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        MyDialog dialog = new MyDialog(this);
        dialog.SimpleDialog();
        return super.onKeyDown(keyCode, event);
    }
}


