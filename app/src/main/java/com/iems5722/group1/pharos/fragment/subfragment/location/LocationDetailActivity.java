package com.iems5722.group1.pharos.fragment.subfragment.location;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.hannesdorfmann.swipeback.Position;
import com.hannesdorfmann.swipeback.SwipeBack;
import com.iems5722.group1.pharos.R;
import com.iems5722.group1.pharos.utils.Util;

/**
 * Created by Sora on 22/4/17.
 */

public class LocationDetailActivity extends AppCompatActivity {
    ImageView ivPic;
    ImageButton btnFav;
    TextView tvName;
    TextView tvPhoneNum;
    TextView tvBusHour;
    TextView tvContact;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_maps_detail);

        SwipeBack.attach(this, Position.LEFT)
                .setSwipeBackView(R.layout.swipeback_default);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ivPic = (ImageView)findViewById(R.id.ivPic);
        btnFav = (ImageButton)findViewById(R.id.ibFav);
        tvName = (TextView)findViewById(R.id.tvName);
        tvPhoneNum = (TextView)findViewById(R.id.tvPhoneNum);
        tvBusHour = (TextView)findViewById(R.id.tvBusHour);
        tvContact = (TextView)findViewById(R.id.tvContact);

        String userName = Util.getUsername(this);
        if(!userName.equals("null")){

        }

        btnFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
