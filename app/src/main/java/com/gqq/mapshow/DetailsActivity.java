package com.gqq.mapshow;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class DetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        TextView tvMarker = (TextView) findViewById(R.id.tvMarker);

        // 拿到跳转的Intent
        Intent intent = getIntent();
        String latlng = intent.getStringExtra("latlng");

        tvMarker.setTextSize(25f);
//        tvMarker.setTextColor(Color.GREEN);
        tvMarker.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        tvMarker.setText("这个Marker的经纬度是："+latlng);

    }
}
