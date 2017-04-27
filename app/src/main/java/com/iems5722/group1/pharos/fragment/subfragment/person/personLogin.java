package com.iems5722.group1.pharos.fragment.subfragment.person;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.iems5722.group1.pharos.MainActivity;
import com.iems5722.group1.pharos.R;
import com.iems5722.group1.pharos.fragment.NavigationFragment;
import com.iems5722.group1.pharos.module.favorite.FavActivity;
import com.iems5722.group1.pharos.utils.Util;

/**
 * Created by Sora on 6/4/17.
 */

public class personLogin extends AppCompatActivity {
    EditText edName;
    EditText edPwd;
    Button btnRegister;
    Button btnLogin;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_item_me_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        edName = (EditText)findViewById(R.id.et_username);
        edPwd = (EditText)findViewById(R.id.et_password);
        btnRegister = (Button)findViewById(R.id.btn_register);
        btnLogin = (Button)findViewById(R.id.btn_login);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edName.getText()!=null){
                    if(edPwd.getText()!=null){
                        LayoutInflater inflater = getLayoutInflater();
                        final View dialog = inflater.inflate(R.layout.fragment_me_pwd_dialog, null);
                        final EditText repwd = (EditText) dialog.findViewById(R.id.pwd);
                        final AlertDialog.Builder builder = new AlertDialog.Builder(personLogin.this);
                        builder.setTitle("input password again").setView(repwd);
                        builder.setCancelable(true);
                        builder.setPositiveButton("register", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String password = repwd.getText().toString();
                                if ("".equals(password.trim())){
                                    //newName.setError("用户名不能为空");
                                    AlertDialog.Builder error= new AlertDialog.Builder(personLogin.this);
                                    error.setMessage("please input the password again");
                                    error.show();
                                }
                                else{
                                    if(password.equals(edPwd.getText().toString())){
                                        String postContent = "user_name=" + edName.getText().toString() + "&user_pwd=" + edPwd.getText().toString() + "&purpose=register";
                                        TaskPostVerifyId postVerifyId = new TaskPostVerifyId(postContent);
                                        postVerifyId.execute();
                                        postVerifyId.setOnAsyncResponse(new AsyncResponse() {
                                            //通过自定义的接口回调获取AsyncTask中onPostExecute返回的结果变量
                                            @Override
                                            public void onDataReceivedSuccess(String result) {
                                                Log.e("test", "onDataReceivedSuccess");
                                                if (result.equals("OK")){
                                                    Util.setUserName(edName.getText().toString(),personLogin.this);
                                                    Toast.makeText(personLogin.this,"register successfully",Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(personLogin.this,MainActivity.class);
                                                    intent.putExtra("userloginflag", 3);
                                                    startActivity(intent);
                                                    // finish();
                                                }
                                                else if (result.equals("EXIST")){
                                                    Toast.makeText(personLogin.this,"this username exists, please change or login directly",Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                            @Override
                                            public void onDataReceivedFailed() {
                                                Log.e("test", "data received failed!");
                                            }
                                        });
                                    }
                                    else{
                                        AlertDialog.Builder error= new AlertDialog.Builder(personLogin.this);
                                        error.setMessage("password inconsistent");
                                        error.show();
                                    }
                                }
                            }
                        });
                        builder.setView(dialog);
                        builder.show();
//                        Dialog dialog = new AlertDialog.Builder(personLogin.this).setTitle("input password again")
//                                .setMessage("are you sure?")// 设置内容
//                                .setPositiveButton("delete",// 设置确定按钮
//                                        new DialogInterface.OnClickListener() {
//                                            @Override
//                                            public void onClick(DialogInterface dialog,
//                                                                int which) {
//
//                                            }
//                                        }).setNegativeButton("cancel",
//                                        new DialogInterface.OnClickListener() {
//                                            @Override
//                                            public void onClick(DialogInterface dialog, int which) {
//                                                //                                finish();
//                                            }
//                                        }).create();// 创建
//                        // 显示对话框
//                        dialog.show();




                    }
                    else{
                        Toast.makeText(personLogin.this,"please input the password",Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(personLogin.this,"please input the username",Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edName.getText()!=null){
                    if(edPwd.getText()!=null){
                        String postContent = "user_name=" + edName.getText() + "&user_pwd=" + edPwd.getText() + "&purpose=login";
                        TaskPostVerifyId postVerifyId = new TaskPostVerifyId(postContent);
                        postVerifyId.execute();
                        postVerifyId.setOnAsyncResponse(new AsyncResponse() {
                            //通过自定义的接口回调获取AsyncTask中onPostExecute返回的结果变量
                            @Override
                            public void onDataReceivedSuccess(String result) {
                                Log.e("test", "onDataReceivedSuccess");
                                if (result.equals("SUCCESS")){
                                    Util.setUserName(edName.getText().toString(),personLogin.this);
                                    Toast.makeText(personLogin.this,"login successfully",Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(personLogin.this,MainActivity.class);
                                    intent.putExtra("userloginflag", 3);
                                    startActivity(intent);
                                    // finish();
                                }
                                else if (result.equals("FAIL")){
                                    Toast.makeText(personLogin.this,"username or password is wrong",Toast.LENGTH_SHORT).show();
                                }
                            }
                            @Override
                            public void onDataReceivedFailed() {
                                Log.e("test", "data received failed!");
                            }
                        });
                    }
                    else{
                        Toast.makeText(personLogin.this,"please input the password",Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(personLogin.this,"please input the username",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }




}
