package com.example.cc.netphone;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import org.litepal.crud.DataSupport;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Search extends AppCompatActivity {

    private String[] items = new String[]{"拨打此号码", "查看详细资料"};
    private ListView listView;
    private List<String> nameList = new ArrayList<String>();
    private String contentName[];
    private String contentPhone[];
    private EditText editText;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.searchToolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
        }

        listView = (ListView) findViewById(R.id.listView);
        button = (Button) findViewById(R.id.search_button);
        editText = (EditText) findViewById(R.id.editText);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText.getText().toString().equals("")) {
                    Snackbar.make(getWindow().getDecorView(), "请输入内容", Snackbar.LENGTH_LONG).show();
                } else {
                    getData(editText.getText().toString());
                    List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
                    for (int i = 0; i < contentName.length; i++) {
                        Map<String, Object> listItem = new HashMap<String, Object>();
                        listItem.put("name", contentName[i]);
                        listItem.put("number", contentPhone[i]);
                        listItems.add(listItem);
                    }
                    MySimpleAdapter adapter = new MySimpleAdapter(Search.this, listItems, R.layout.listview_format,
                            new String[]{"name", "number"}, new int[]{R.id.before, R.id.after});
                    listView.setAdapter(adapter);
                }

            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String, String> map = (HashMap<String, String>) listView.getItemAtPosition(position);
                final String number = map.get("number");
                /**
                 *  dialog
                 */
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(Search.this);
                builder.setTitle("提示")
                        .setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        //拨打电话
                                        if (ContextCompat.checkSelfPermission(Search.this, Manifest.permission.CALL_PHONE)
                                                != PackageManager.PERMISSION_GRANTED) {
                                            ActivityCompat.requestPermissions(Search.this, new String[]{Manifest.permission.CALL_PHONE}, 1);
                                            tellPhone(number);
                                        } else {
                                            tellPhone(number);
                                        }
                                        break;
                                    //跳转
                                    case 1:
                                        Intent intent1 = new Intent(Search.this, listViewContentActivity.class);
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

    private void getData(String name) {

        List<PhoneDataBases> dataBasesPhone = DataSupport.select
                ("name", "phone1").where("name like ? or phone1 like ? ", "%"+ name + "%","%"+ name + "%").find(PhoneDataBases.class);

        for (PhoneDataBases phone : dataBasesPhone) {
            nameList.add(phone.getName());
        }
        contentName = new String[dataBasesPhone.size()];
        contentPhone = new String[dataBasesPhone.size()];
        for (int i = 0; i < contentName.length; i++) {
            String name1 = dataBasesPhone.get(i).getName();
            String phone1 = dataBasesPhone.get(i).getPhone1();
            contentName[i] = name1;
            contentPhone[i] = phone1;
        }
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
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    //拨号
    private void tellPhone(String number) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + number));
        startActivity(intent);
    }

}
