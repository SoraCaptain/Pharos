package com.iems5722.group1.pharos;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.plus.model.people.Person;
import com.iems5722.group1.pharos.fragment.NavigationFragment;
import com.iems5722.group1.pharos.fragment.subfragment.location.LocationFragment;
import com.iems5722.group1.pharos.fragment.subfragment.location.LocationMapsActivity;
import com.iems5722.group1.pharos.module.chatrooms.ChatRoomListActivity;
import com.iems5722.group1.pharos.module.contact.ContactActivity;
import com.iems5722.group1.pharos.module.favorite.FavActivity;
import com.iems5722.group1.pharos.module.SettingsActivity;
import com.iems5722.group1.pharos.utils.Util;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;
    private NavigationView mNavigationView;
    private ActionBarDrawerToggle mDrawerToggle;
    private NavigationFragment mNavigationFragment;
    private NightModeHelper mNightModeHelper;
    int PLACE_PICKER_REQUEST = 1;
    private TextView tvHeadName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        mNightModeHelper = new NightModeHelper(this, R.style.BaseTheme);
        setContentView(R.layout.activity_main);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawer_layout_open, R.string.drawer_layout_close);
        mDrawerToggle.syncState();
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.setStatusBarBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

        View headerView = mNavigationView.getHeaderView(0);
        headerView.setOnClickListener(this);
        mNavigationView.setItemIconTintList(null);
        mNavigationView.setNavigationItemSelectedListener(this);
        mNavigationView.setItemTextColor(ContextCompat.getColorStateList(this, R.color.bg_drawer_navigation));
        mNavigationView.setItemIconTintList(ContextCompat.getColorStateList(this, R.color.bg_drawer_navigation));

        setNavigationViewChecked(0);
        setCurrentFragment();

        Util.checkToken(this);

//        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
//        startActivity(intent);
    }

    private void setNavigationViewChecked(int position) {
        mNavigationView.getMenu().getItem(position).setChecked(true);
        Log.i("Pharos", "the count of menu item is--->" + mNavigationView.getMenu().size() + "");
        for (int i = 0; i < mNavigationView.getMenu().size(); i++) {
            if (i != position) {
                mNavigationView.getMenu().getItem(i).setChecked(false);
            }
        }
    }

    private void setCurrentFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        mNavigationFragment = NavigationFragment.newInstance(getString(R.string.navigation_chat_list));
        transaction.replace(R.id.frame_content, mNavigationFragment).commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Intent intent = new Intent();
        Class context = null;
        switch (item.getItemId()) {
            case R.id.menu_chat_list:
                context = ChatRoomListActivity.class;
                setNavigationViewChecked(0);
                break;
            case R.id.menu_contact:
                context = ContactActivity.class;
                setNavigationViewChecked(1);
                break;
            case R.id.menu_fav:
                context = FavActivity.class;
                setNavigationViewChecked(2);
                break;
            case R.id.menu_setting:
                context = SettingsActivity.class;
                setNavigationViewChecked(3);
                break;

        }
        intent.setClass(MainActivity.this, context);
        startActivity(intent);
        mDrawerLayout.closeDrawers();
        transaction.commit();
        return true;
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.settings:
                Snackbar.make(mDrawerLayout, "Settings", Snackbar.LENGTH_SHORT).show();
                return true;
            case R.id.search:
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try{
                    startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
                }catch(Exception e){
                    Log.e("search",String.valueOf(e));
                }
                return true;
            case R.id.share:

                mNightModeHelper.toggle();
//                Configuration newConfig = new Configuration(getResources().getConfiguration());
//                newConfig.uiMode &= ~Configuration.UI_MODE_NIGHT_MASK;
//                newConfig.uiMode |= uiNightMode;
//                getResources().updateConfiguration(newConfig, null);
//                recreate();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("search requestCode",String.valueOf(requestCode));
        Log.e("search resultCode",String.valueOf(resultCode));
        Log.e("PLACE_PICKER_REQUEST", String.valueOf(PLACE_PICKER_REQUEST));
        if (requestCode == PLACE_PICKER_REQUEST) {
            Log.e("result_ok", String.valueOf(RESULT_OK));
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                String PlaceID = String.format("PlaceID: %s", place.getId());
                Double Latitude = place.getLatLng().latitude;
                Double Longitude = place.getLatLng().longitude;
                Toast.makeText(this, PlaceID, Toast.LENGTH_LONG).show();
                Log.e("place",String.valueOf(Latitude)+" "+String.valueOf(Longitude));
//                Util.setPlace("true",this);
//                Bundle bundle = new Bundle();
//                bundle.putDouble("Latitude", Latitude);
//                bundle.putDouble("Longitude", Longitude);
//                bundle.putBoolean("transfer", true);
//                LocationFragment fragobj = new LocationFragment();
//                fragobj.setArguments(bundle);
//                mNavigationFragment.setFragment(Latitude,Longitude,true);

                Intent intent = new Intent();
                intent.putExtra("Latitude", Latitude);
                intent.putExtra("Longitude", Longitude);
                intent.setClass(this, LocationMapsActivity.class);
                startActivity(intent);
            }
        }
    }

    public boolean isGooglePlayServicesAvailable(Activity activity) {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(activity);
        if(status != ConnectionResult.SUCCESS) {
            if(googleApiAvailability.isUserResolvableError(status)) {
                googleApiAvailability.getErrorDialog(activity, status, 2404).show();
            }
            return false;
        }
        return true;
    }
}
