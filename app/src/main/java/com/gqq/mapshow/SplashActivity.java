package com.gqq.mapshow;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

//        // 发送一个延时的操作：停留两秒，跳转页面
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                // 跳转页面
//                Intent intent = new Intent(SplashActivity.this,MainActivity.class);
//                startActivity(intent);
//                finish();// 跳转页面之后销毁
//            }
//        }, 2000);

        final EditText etName = (EditText) findViewById(R.id.etName);

        // 判断EditText有没有内容

        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String string = etName.getText().toString();
                if (!TextUtils.isEmpty(string)){
                    // 输入的内容不为空，跳转页面

                }else {

                    // 弹出一个吐丝
                    Toast.makeText(SplashActivity.this,"不可以为空",Toast.LENGTH_LONG).show();
                }
            }
        });

//        String string = etName.getText().toString();
//        if (!TextUtils.isEmpty(string)){
//            // 输入的内容不为空，跳转页面
//        }else {
//
//            // 弹出一个吐丝
//            Toast.makeText(SplashActivity.this,"不可以为空",Toast.LENGTH_LONG).show();
//        }


        // Toast 吐丝


    }
}
