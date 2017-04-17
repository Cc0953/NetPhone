package com.example.cc.netphone;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class MyDialog extends AlertDialog {

    private Context context;
    private String[] items = new String[]{"拨打此号码","查看详细资料"};

    protected MyDialog(Context context) {
        super(context);
        this.context = context;
    }
    public void SimpleDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("提示");
        builder.setMessage("是否退出");
        setPositiveButton(builder);
        setNegativeButton(builder).create().show();
    }
    private AlertDialog.Builder setPositiveButton(AlertDialog.Builder builder) {

        return builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                System.exit(0);
            }
        });
    }

    private AlertDialog.Builder setNegativeButton(AlertDialog.Builder builder) {

        return builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
    }


}
