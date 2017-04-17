package com.example.cc.netphone;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class listViewContentActivity extends AppCompatActivity {

    private TextView name, department, phone1, phone2, phone3;
    List<String> nameList = new ArrayList<String>();
    List<String> departmentList = new ArrayList<String>();
    List<String> phone1List = new ArrayList<String>();
    List<String> phone2List = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview_content);
        Toolbar toolbar = (Toolbar) findViewById(R.id.listContentToolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
        }
        name = (TextView) findViewById(R.id.name_listContent);
        department = (TextView) findViewById(R.id.department_listContent);
        phone1 = (TextView) findViewById(R.id.phone1_listContent);
        phone2 = (TextView) findViewById(R.id.phone2_listContent);
        phone3 = (TextView) findViewById(R.id.phone3_listContent);

        Intent intent = getIntent();
        phone1.setText(intent.getStringExtra("phone"));

        //通过phone1 查询name,department,phone2,phone3
        findWithPhone();
        onClick();
    }

    private void findWithPhone() {
        String getPhone = phone1.getText().toString();
        List<PhoneDataBases> dataBases = DataSupport.where("phone1==?", getPhone).find(PhoneDataBases.class);
        for (PhoneDataBases phone : dataBases) {
            nameList.add(phone.getName());
            departmentList.add(phone.getDepartment());
            phone1List.add(phone.getPhone1());
            phone2List.add(phone.getPhone2());
        }
        for (int i = 0; i < nameList.size(); i++) {
            name.setText(dataBases.get(i).getName());
            List<DepartmentDataBases> dataBases1 =
                    DataSupport.where("DepartmentID == ?",dataBases.get(i).getDepartment()).find(DepartmentDataBases.class);
            department.setText(dataBases1.get(i).getDepartment());
            phone2.setText(dataBases.get(i).getPhone2());
            phone3.setText(dataBases.get(i).getPhone3());
        }
    }

    private void onClick() {
        phone1.setOnClickListener(listener);
        phone2.setOnClickListener(listener);
        phone3.setOnClickListener(listener);
    }

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //打电话
            switch (v.getId()) {
                case R.id.phone1_listContent:
                    tellPhone(phone1.getText().toString());
                    break;
                case R.id.phone2_listContent:
                    tellPhone(phone2.getText().toString());
                    break;
                case R.id.phone3_listContent:
                    tellPhone(phone3.getText().toString());
                    break;
            }
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 拨号
     * @param number 号码
     */
    private void tellPhone(String number) {
        if(number.length()>=6) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + number));
            startActivity(intent);
        }
        else if(number.length()<6 ){
            Snackbar.make(getWindow().getDecorView(),"所选号码为空",Snackbar.LENGTH_SHORT).show();
        }
        else {
            Snackbar.make(getWindow().getDecorView(),"所选号码为空",Snackbar.LENGTH_SHORT).show();
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
