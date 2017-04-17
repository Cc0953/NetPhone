package com.example.cc.netphone;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Phone_content extends AppCompatActivity {

    public static final String Phone_NAME = "phoneName";
    private String[] name;
    private String[] number;
    private String[] items = new String[]{"拨打此号码", "查看详细资料"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phone_content);
        Intent intent = getIntent();
        String nameForIntent = intent.getStringExtra(Phone_NAME);
        Toolbar toolbar = (Toolbar) findViewById(R.id.content_toolbar);
        ImageView phoneImageView = (ImageView) findViewById(R.id.content_phone_image);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
        }
        CollapsingToolbarLayout ctbl = (CollapsingToolbarLayout) findViewById(R.id.content_collapsing_toolbar);
        Glide.with(this).load(R.drawable.titile).into(phoneImageView);
        ctbl.setTitle(nameForIntent);

        //获取数据库数据
        getDatabaseData(nameForIntent);
        final MyListView listView = (MyListView) findViewById(R.id.content_listview);
        List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
        List<PhoneDataBases> list = DataSupport.findAll(PhoneDataBases.class);

        for (int i = 0; i < name.length; i++) {
            Map<String, Object> listItem = new HashMap<String, Object>();
            listItem.put("name", name[i]);
            listItem.put("number", number[i]);
            listItems.add(listItem);
        }
        MySimpleAdapter adapter = new MySimpleAdapter(this, listItems, R.layout.listview_format,
                new String[]{"name", "number"}, new int[]{R.id.before, R.id.after});
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String, String> map = (HashMap<String, String>) listView.getItemAtPosition(position);
                final String number = map.get("number");
                /**
                 *  dialog
                 */
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(Phone_content.this);
                builder.setTitle("提示")
                        .setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        //拨打电话
                                        if (ContextCompat.checkSelfPermission(Phone_content.this, Manifest.permission.CALL_PHONE)
                                                != PackageManager.PERMISSION_GRANTED) {
                                            ActivityCompat.requestPermissions(Phone_content.this, new String[]{Manifest.permission.CALL_PHONE}, 1);

                                            if (number.length() >= 6) {
                                                tellPhone(number);
                                            } else {
                                                Snackbar.make(getWindow().getDecorView(), "所选号码为空", Snackbar.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            if (number.length() >= 6) {
                                                tellPhone(number);
                                            } else {
                                                Snackbar.make(getWindow().getDecorView(), "所选号码为空", Snackbar.LENGTH_SHORT).show();
                                            }
                                        }
                                        break;
                                    //跳转
                                    case 1:
                                        Intent intent1 = new Intent(Phone_content.this, listViewContentActivity.class);
                                        intent1.putExtra("phone", number);
                                        startActivity(intent1);
                                        break;
                                    default:
                                        break;
                                }
                            }

                        }).create().show();
            }
        });
    }

    private void getDatabaseData(String content) {
        try {
            DepartmentDataBases basesListDDB = DataSupport.where("department == ?", content).findFirst(DepartmentDataBases.class);
            String departmentID = basesListDDB.getDepartmentID();
            List<PhoneDataBases> basesListPDB = DataSupport.where("department == ?", departmentID).find(PhoneDataBases.class);
            name = new String[basesListPDB.size()];
            number = new String[basesListPDB.size()];
            for (int i = 0; i < name.length; i++) {
                String name1 = basesListPDB.get(i).getName();
                String phone1 = basesListPDB.get(i).getPhone1();
                name[i] = name1;
                number[i] = phone1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //拨号
    private void tellPhone(String number) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + number));
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        finish();
        return super.onKeyDown(keyCode, event);
    }
}
