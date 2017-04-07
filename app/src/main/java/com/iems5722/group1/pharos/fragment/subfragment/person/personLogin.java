package com.iems5722.group1.pharos.fragment.subfragment.person;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.iems5722.group1.pharos.MainActivity;
import com.iems5722.group1.pharos.R;
import com.iems5722.group1.pharos.fragment.NavigationFragment;
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
                        String postContent = "user_name=" + edName.getText() + "&user_pwd=" + edPwd.getText() + "&purpose=register";
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
                                    Toast.makeText(personLogin.this,"register successfully",Toast.LENGTH_SHORT).show();
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
