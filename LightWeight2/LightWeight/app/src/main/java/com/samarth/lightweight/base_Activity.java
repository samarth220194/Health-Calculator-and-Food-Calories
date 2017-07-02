package com.samarth.lightweight;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.samarth.lightweight.database.databasehandler;
import com.samarth.lightweight.fragments.*;
import com.samarth.lightweight.fragments.BodyfatPercentage;
import com.samarth.lightweight.fragments.bmi;
import com.samarth.lightweight.fragments.bmr;
import com.samarth.lightweight.fragments.waist_to_height;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Samarth on 25-Nov-16.
 */

public class base_Activity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener {

    protected FrameLayout frameLayout;
    public NavigationView navigationView;
    private BodyfatPercentage bodyfatpercentage;
    private foodCategory foodCategory;
    private bmi bmi;
    private bmr bmr;
    private waist_to_height waist_to_height;
    private food_item_description foodItemDescription;

    FirebaseAnalytics firebaseAnalytics;


    private DrawerLayout mDrawerLayout;
    android.support.v7.app.ActionBarDrawerToggle mDrawerToggle;
    databasehandler db;

    public FragmentManager fragmentManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String fragment_name=getIntent().getStringExtra("Button_clicked");

        firebaseAnalytics=FirebaseAnalytics.getInstance(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        frameLayout = (FrameLayout) findViewById(R.id.content_frame);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        bodyfatpercentage= new BodyfatPercentage();
        bmi=new bmi();
        bmr=new bmr();
        foodCategory=new foodCategory();
        waist_to_height=new waist_to_height();
        foodItemDescription=new food_item_description();
        fragmentManager=getSupportFragmentManager();

        if(fragment_name.equals("BMI"))
            displayBMIFragment();

        if(fragment_name.equals("BFP"))
            displayBfpFragment();

        if(fragment_name.equals("WTHR"))
            displayWTHFragment();

        if(fragment_name.equals("BMR"))
            displayBMRFragment();

        if(fragment_name.equals("CALORIES"))
           displayCaloriesFragment();


        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        db = databasehandler.getInstance(getApplicationContext());

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle = new android.support.v7.app.ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, com.samarth.lightweight.R.string.navigation_drawer_open, com.samarth.lightweight.R.string.navigation_drawer_close);

        mDrawerToggle.syncState();

    }

    public void onResume(){
        super.onResume();

        Log.i("On start Base Activity","+");

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "0");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Base Activity Started");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Activity Open");
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, bundle);
    }




    private Boolean exit = false;
    @Override
    public void onBackPressed() {

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "0.1");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Back pressed");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Button");
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else
        {
            if (exit)
            {
                finish(); // finish activity
            }
            else
            {
                if(fragmentManager.getBackStackEntryCount()==0) {
                    Toast.makeText(this, "Press Back again to Exit.",
                            Toast.LENGTH_SHORT).show();
                    exit = true;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            exit = false;
                        }
                    }, 2 * 1000);
                }
                else{
                    fragmentManager.popBackStack();
                }

            }

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Bundle bundle = new Bundle();
        if (id == R.id.bmi) {

            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "0.2");
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "BMI Fragment");
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Drawer layout Item");
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

            displayBMIFragment();

        } else if (id == R.id.bfp) {

            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "0.3");
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "BFP Fragment");
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Drawer layout Item");
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

            displayBfpFragment();

        } else if (id == R.id.wth) {

            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "0.4");
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "WTH Fragment");
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Drawer layout Item");
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

            displayWTHFragment();
        }
        else if (id == R.id.bmr) {
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "0.5");
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "BMR Fragment");
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Drawer layout Item");
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

            displayBMRFragment();

        }
        else if(id==R.id.calories_in_food){

            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "0.6");
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Calories Fragment");
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Drawer layout Item");
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

            displayCaloriesFragment();

        } else if (id == R.id.nav_share) {

            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "0.7");
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Share");
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Drawer layout Item");
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);


            Intent emailIntent = new Intent();
            emailIntent.setAction(Intent.ACTION_SEND);
            emailIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.sharing_text)+getApplicationContext().getPackageName());
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
            emailIntent.setType("message/rfc822");

            PackageManager pm = getPackageManager();
            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.setType("text/plain");

            ApplicationInfo app = getApplicationContext().getApplicationInfo();
            String filePath = app.sourceDir;

            Intent openInChooser = Intent.createChooser(emailIntent,"Share via:");

            List<ResolveInfo> resInfo = pm.queryIntentActivities(sendIntent, 0);
            List<LabeledIntent> intentList = new ArrayList<LabeledIntent>();
            for (int i = 0; i < resInfo.size(); i++)
            {
                // Extract the label, append it, and repackage it in a LabeledIntent
                ResolveInfo ri = resInfo.get(i);
                String packageName = ri.activityInfo.packageName;
                if(packageName.contains("android.email"))
                {
                    emailIntent.setPackage(packageName);
                }
                else if( packageName.contains("anyshare") ||  packageName.contains("android.bluetooth")
                        || packageName.contains("hangouts") || packageName.contains("hike") ||
                        packageName.contains("twitter") || packageName.contains("facebook") ||
                        packageName.contains("mms") || packageName.contains("android.gm") ||
                        packageName.contains("whatsapp"))
                {
                    Intent intent = new Intent();
                    intent.setComponent(new ComponentName(packageName, ri.activityInfo.name));
                    intent.setAction(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TEXT,  getString(R.string.sharing_text)+getApplicationContext().getPackageName());

                   if(packageName.contains("android.gm"))
                    {
                        intent.putExtra(Intent.EXTRA_SUBJECT,  getString(R.string.app_name));
                        intent.setType("message/rfc822");
                    }
                    if ( (packageName.contains("android.bluetooth") || packageName.contains("anyshare")) && filePath!=null )
                    {
                        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(filePath)));
                        intent.setType("*/*");
                    }
                    intentList.add(new LabeledIntent(intent, packageName, ri.loadLabel(pm), ri.icon));
                }
            }

            // convert intentList to array
            LabeledIntent[] extraIntents = intentList.toArray( new LabeledIntent[ intentList.size() ]);

            openInChooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, extraIntents);
            startActivity(openInChooser);


            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            assert drawer != null;
            drawer.closeDrawer(GravityCompat.START);

        } else if (id == R.id.nav_rate) {

            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "0.8");
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Rate Us");
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Drawer layout Item");
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://play.google.com/store/apps/details?id="+getApplicationContext().getPackageName()));
            startActivity(intent);
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            assert drawer != null;
            drawer.closeDrawer(GravityCompat.START);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public void displayBfpFragment(){
        if(fragmentManager==null) {
            fragmentManager = getSupportFragmentManager();
            bodyfatpercentage= new BodyfatPercentage();
        }
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.content_frame, bodyfatpercentage,"BfpFragment");
        ft.commit();
    }

    public void displayBMIFragment(){
        if(fragmentManager==null) {
            fragmentManager=getSupportFragmentManager();
            bmi=new bmi();
        }
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.content_frame, bmi,"BMIFragment");
        ft.commit();
    }
    public void displayBMRFragment(){
        if(fragmentManager==null){
            fragmentManager=getSupportFragmentManager();
            bmr=new bmr();
        }
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.content_frame, bmr,"BMRFragment");
        ft.commit();
    }
    public void displayWTHFragment(){
        if(fragmentManager==null){
            fragmentManager=getSupportFragmentManager();
            waist_to_height=new waist_to_height();
        }
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.content_frame, waist_to_height,"WTHFragment");
        ft.commit();
    }
    public void displayCaloriesFragment(){
        if(fragmentManager==null) {
            fragmentManager=getSupportFragmentManager();
            foodCategory=new foodCategory();
        }
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.content_frame, foodCategory);
        ft.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        Bundle bundle=new Bundle();
        switch (item.getItemId()) {
            case R.id.info:

                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "0.9");
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Info");
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Action bar item");
                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

//                Toast.makeText(this, "Info selected", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(this,helpandinfo.class);
                intent.putExtra("intent_string","info");
                startActivity(intent);
                break;
            case R.id.help:

                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "1.0");
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Help");
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Action bar item");
                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

//                Toast.makeText(this, "help selected", Toast.LENGTH_SHORT).show();
                Intent intent1=new Intent(this,helpandinfo.class);
                intent1.putExtra("intent_string","help");
                startActivity(intent1);
                break;


            default:
                break;

        }
        return super.onOptionsItemSelected(item);
    }



}
