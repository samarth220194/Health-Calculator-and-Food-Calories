package com.samarth.lightweight.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.samarth.lightweight.NetowrkConnection;
import com.samarth.lightweight.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

import static com.samarth.lightweight.intoduction_activity.Age;
import static com.samarth.lightweight.intoduction_activity.Gender_selected_female;
import static com.samarth.lightweight.intoduction_activity.Gender_selected_male;
import static com.samarth.lightweight.intoduction_activity.MyPREFERENCES;

/**
 * Created by Sam on 21-Feb-17.
 */

public class BodyfatPercentage extends android.support.v4.app.Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private RadioGroup radioSexGroup;
    private RadioButton radioSexButton;

    private SharedPreferences sharedpreferences;

    private int selectedId;
    private EditText age;
    private EditText height_ft,height_in,height_cm;
    private EditText waist;
    private EditText neck;
    private EditText hip;
    private TextView hip_text;

    private SwipeRefreshLayout swipe;
    private LinearLayout hip_layout;
    private LinearLayout l1,l2,l3,l4;
    private LinearLayout main_layout;
    private TextView t1,t2,t3,t4;

    private Spinner height_spinner;
    private Spinner waist_spinner;
    private Spinner neck_spinner;
    private Spinner hip_spinner;
    private PieChart mChart;

    String spinner_waist; //Height Spinner
    String spinner_height;//Weight Spinner
    String spinner_neck;//neck spinner
    String spinner_hip;
    String selected_item1;
    String selected_item2;
    String selected_item3;
    String selected_item4;

    private AdView adView;
    private ScrollView scrollView;
    private RadioButton r_male,r_female;

    String sex_selected="Male";

    public FirebaseAnalytics mFirebaseAnalytics;

    protected String[] Fatcategory = new String[]
            {
                    "Low", "Normal", "High", "Very High"
            };
    private float fatpercent;

    public BodyfatPercentage() {

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bodyfatpercent, container, false);
         mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());

        getActivity().setTitle("Body Fat Percentage");
        findId(view);


        return view;
    }
    @Override
    public void onStart(){
        super.onStart();
        Log.i("On start of BFP","+");

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "2");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "BFP Fragment Started");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Fragment Open");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, bundle);

    }


    TextWatcher watch1 = new TextWatcher() {

        @Override
        public void afterTextChanged(Editable arg0) {

        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {

        }

        @Override
        public void onTextChanged(CharSequence s, int a, int b, int c) {

            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "2.3");
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "BFP age/height/weight edittext");
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "TextBox");
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

            setchart();
            calculate();
        }

    };

    public void findId(View view)
    {
        sharedpreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        radioSexGroup = (RadioGroup) view.findViewById(R.id.radioSex);
        selectedId = radioSexGroup.getCheckedRadioButtonId();
        radioSexButton = (RadioButton) view.findViewById(selectedId);

        r_male=(RadioButton) view.findViewById(R.id.radioMale) ;
        r_female=(RadioButton) view.findViewById(R.id.radioFemale);
        hip_text=(TextView) view.findViewById(R.id.hip_text);
        hip_layout=(LinearLayout) view.findViewById(R.id.hip_layout);

//        Log.i("boolean bfp_m",sharedpreferences.getBoolean(Gender_selected_male,false)+"");
//        Log.i("boolean bfp_f",sharedpreferences.getBoolean(Gender_selected_female,false)+"");

        if(sharedpreferences.getBoolean(Gender_selected_male,false))
        {
            r_male.setChecked(sharedpreferences.getBoolean(Gender_selected_male,false));
            sex_selected="Male";
            hip_text.setVisibility(View.GONE);
            hip_layout.setVisibility(View.GONE);

        }
        if(sharedpreferences.getBoolean(Gender_selected_female,false))
        {
            r_female.setChecked(sharedpreferences.getBoolean(Gender_selected_female,false));
            sex_selected="Female";
            hip_text.setVisibility(View.VISIBLE);
            hip_layout.setVisibility(View.VISIBLE);
        }

        swipe=(SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        scrollView=(ScrollView) view.findViewById(R.id.scroll_bfp);

        mChart=(PieChart)view.findViewById(R.id.chart);
        age=(EditText) view.findViewById(R.id.age);

        age.setText(sharedpreferences.getString(Age,""));

        height_ft=(EditText) view.findViewById(R.id.height_ft);
        height_in=(EditText) view.findViewById(R.id.height_in);
        height_cm=(EditText) view.findViewById(R.id.height_cm);

        waist=(EditText) view.findViewById(R.id.waist);
        neck=(EditText) view.findViewById(R.id.neck);
        hip=(EditText) view.findViewById(R.id.hip);


        height_spinner=(Spinner) view.findViewById(R.id.height_spinner);
        waist_spinner=(Spinner) view.findViewById(R.id.waist_spinner);
        neck_spinner=(Spinner) view.findViewById(R.id.neck_spinner);
        hip_spinner=(Spinner) view.findViewById(R.id.hip_spinner);

        l1=(LinearLayout) view.findViewById(R.id.linearlayout1);
        l2=(LinearLayout) view.findViewById(R.id.linearlayout2);
        l3=(LinearLayout) view.findViewById(R.id.linearlayout3);
        l4=(LinearLayout) view.findViewById(R.id.linearlayout4);
        main_layout=(LinearLayout) view.findViewById(R.id.super_layout);
        t1=(TextView) view.findViewById(R.id.text1);
        t2=(TextView) view.findViewById(R.id.text2);
        t3=(TextView) view.findViewById(R.id.text3);
        t4=(TextView) view.findViewById(R.id.text4);


        adView=(AdView) view.findViewById(R.id.adView);

        swipe.setOnRefreshListener(this);
        LoadAd();

        age.addTextChangedListener(watch);
        height_ft.addTextChangedListener(watch1);
        height_in.addTextChangedListener(watch1);
        height_cm.addTextChangedListener(watch1);

        waist.addTextChangedListener(watch1);
        neck.addTextChangedListener(watch1);
        hip.addTextChangedListener(watch1);

        t1.setText("<0.00%");


        radioSexGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                Bundle bundle = new Bundle();
                Log.i("Selected_id:",checkedId+"");
                switch(checkedId) {
                    case (R.id.radioFemale):
                        Log.i("gender_selected","female");
                        sex_selected = "Female";

                        hip_text.setVisibility(View.VISIBLE);
                        hip_layout.setVisibility(View.VISIBLE);

                        ChangeText(sex_selected);
                        calculate();
                        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "2.1");
                        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "BFP Radio button female ");
                        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Button");
                        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);


                        break;
                    case (R.id.radioMale):
                        Log.i("gender_selected","male");

                        sex_selected = "Male";

                        hip_text.setVisibility(View.GONE);
                        hip_layout.setVisibility(View.GONE);
                        ChangeText(sex_selected);
                        calculate();

                        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "2.2");
                        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "BFP Radio button male ");
                        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Button");
                        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                        break;

                    default:
                        break;
                }

            }
        });

        ChangeText(sex_selected);
        calculate();

        setchart();

        set_spinner();
    }


    public void LoadAd(){

        swipe.setRefreshing(false);
        Log.i("network", NetowrkConnection.CheckConnection(getActivity())+"");
        if(NetowrkConnection.CheckConnection(getActivity())) {
            adView.setVisibility(View.VISIBLE);
            AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);
        }
        else adView.setVisibility(View.GONE);

    }



    TextWatcher watch = new TextWatcher()
    {
        @Override
        public void afterTextChanged(Editable arg0) {

        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {

        }

        @Override
        public void onTextChanged(CharSequence s, int a, int b, int c) {

            Bundle bundle=new Bundle();
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "2.4");
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "BFP age Edittext");
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Button");
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

            SharedPreferences.Editor editor = sharedpreferences.edit();
            Log.i("age",s.toString());
            editor.putString(Age,s.toString());
            editor.apply();

            ChangeText(sex_selected);
            setchart();
            calculate();

        }
    };

    public void SetBackcolor(float bfp)
    {
        mChart.invalidate();
        l1.setBackgroundColor(Color.rgb(255, 255, 255));
        l2.setBackgroundColor(Color.rgb(255, 255, 255));
        l3.setBackgroundColor(Color.rgb(255, 255, 255));
        l4.setBackgroundColor(Color.rgb(255, 255, 255));

        if(age.getText().toString().equals("") || (age.getText().toString().length()>=1 && age.getText().toString().length()<=6)
                || bfp == -1f || bfp == 0f )
        {
            mChart.highlightValue(0, -1, false);

        }
        if(age.getText().toString().length()!=0)
        {
            if(Integer.parseInt(age.getText().toString())==7)
            {
                if(sex_selected.equals("Male"))
                {
                    if(fatpercent<=14.9f && fatpercent>0f)
                    {
                        mChart.highlightValue(0,0, false);
                        l1.setBackgroundColor(Color.rgb(220, 220, 220));
                    }
                    if(fatpercent<=24.9f && fatpercent>=15.0f)
                    {
                        mChart.highlightValue(1,0, false);
                        l2.setBackgroundColor(Color.rgb(220, 220, 220));
                    }
                    if(fatpercent>=25f && fatpercent<=28.9f)
                    {
                        mChart.highlightValue(2,0, false);
                        l3.setBackgroundColor(Color.rgb(220, 220, 220));
                    }
                    if(fatpercent>=29f)
                    {
                        mChart.highlightValue(3,0, false);
                        l4.setBackgroundColor(Color.rgb(220, 220, 220));
                    }

                }
                if(sex_selected.equals("Female"))
                {
                    if(fatpercent>0f && fatpercent<=12.9f)
                    {
                        mChart.highlightValue(0,0, false);
                        l1.setBackgroundColor(Color.rgb(220, 220, 220));
                    }
                    if(fatpercent>=13.0f && fatpercent<=19.9f )
                    {
                        mChart.highlightValue(1,0, false);
                        l2.setBackgroundColor(Color.rgb(220, 220, 220));
                    }
                    if(fatpercent>=20.0f && fatpercent<=24.9f)
                    {
                        mChart.highlightValue(2,0, false);
                        l3.setBackgroundColor(Color.rgb(220, 220, 220));
                    }
                    if(fatpercent>=25.0f)
                    {
                        mChart.highlightValue(3,0, false);
                        l4.setBackgroundColor(Color.rgb(220, 220, 220));
                    }

                }

            }

            if(Integer.parseInt(age.getText().toString())==8)
            {
                if(sex_selected.equals("Male"))
                {
                    if(fatpercent>0f &&  fatpercent<=14.9f )
                    {
                        mChart.highlightValue(0,0, false);
                        l1.setBackgroundColor(Color.rgb(220, 220, 220));
                    }
                    if(fatpercent>=15.0f && fatpercent<=25.9f )
                    {
                        mChart.highlightValue(1,0, false);
                        l2.setBackgroundColor(Color.rgb(220, 220, 220));
                    }
                    if(fatpercent>=26f && fatpercent<=29.9f)
                    {
                        mChart.highlightValue(2,0, false);
                        l3.setBackgroundColor(Color.rgb(220, 220, 220));
                    }
                    if(fatpercent>=30f)
                    {
                        mChart.highlightValue(3,0, false);
                        l3.setBackgroundColor(Color.rgb(255, 255, 255));
                    }

                }
                if(sex_selected.equals("Female"))
                {
                    if(fatpercent>0f && fatpercent<=12.9f)
                    {
                        mChart.highlightValue(0,0, false);
                        l1.setBackgroundColor(Color.rgb(220, 220, 220));
                    }
                    if(fatpercent>=13.0f && fatpercent<=20.9f )
                    {
                        mChart.highlightValue(1,0, false);
                        l2.setBackgroundColor(Color.rgb(220, 220, 220));
                    }
                    if(fatpercent>=21.0f && fatpercent<=25.9f)
                    {
                        mChart.highlightValue(2,0, false);
                        l3.setBackgroundColor(Color.rgb(220, 220, 220));
                    }
                    if(fatpercent>=26.0f)
                    {
                        mChart.highlightValue(3,0, false);
                        l4.setBackgroundColor(Color.rgb(220, 220, 220));
                    }

                }

            }
            if(Integer.parseInt(age.getText().toString())==9)
            {
                if(sex_selected.equals("Male"))
                {
                    if(fatpercent>0f &&  fatpercent<=15.9f )
                    {
                        mChart.highlightValue(0,0, false);
                        l1.setBackgroundColor(Color.rgb(220, 220, 220));
                    }
                    if(fatpercent>=15.0f && fatpercent<=26.9f )
                    {
                        mChart.highlightValue(1,0, false);
                        l2.setBackgroundColor(Color.rgb(220, 220, 220));
                    }
                    if(fatpercent>=27f && fatpercent<=30.9f)
                    {
                        mChart.highlightValue(2,0, false);
                        l3.setBackgroundColor(Color.rgb(220, 220, 220));
                    }
                    if(fatpercent>=31f)
                    {
                        mChart.highlightValue(3,0, false);
                        l4.setBackgroundColor(Color.rgb(220, 220, 220));
                    }

                }
                if(sex_selected.equals("Female"))
                {
                    if(fatpercent>0f && fatpercent<=12.9f)
                    {
                        mChart.highlightValue(0,0, false);
                        l1.setBackgroundColor(Color.rgb(220, 220, 220));
                    }
                    if(fatpercent>=13.0f && fatpercent<=21.9f )
                    {
                        mChart.highlightValue(1,0, false);
                        l2.setBackgroundColor(Color.rgb(220, 220, 220));
                    }
                    if(fatpercent>=22.0f && fatpercent<=26.9f)
                    {
                        mChart.highlightValue(2,0, false);
                        l3.setBackgroundColor(Color.rgb(220, 220, 220));
                    }
                    if(fatpercent>=27.0f)
                    {
                        mChart.highlightValue(3,0, false);
                        l4.setBackgroundColor(Color.rgb(220, 220, 220));
                    }

                }

            }
            if(Integer.parseInt(age.getText().toString())==10)
            {
                if(sex_selected.equals("Male"))
                {
                    if(fatpercent>0f &&  fatpercent<=15.9f )
                    {
                        mChart.highlightValue(0,0, false);
                        l1.setBackgroundColor(Color.rgb(220, 220, 220));
                    }
                    if(fatpercent>=16.0f && fatpercent<=27.9f )
                    {
                        mChart.highlightValue(1,0, false);
                        l2.setBackgroundColor(Color.rgb(220, 220, 220));
                    }
                    if(fatpercent>=28f && fatpercent<=31.9f)
                    {
                        mChart.highlightValue(2,0, false);
                        l3.setBackgroundColor(Color.rgb(220, 220, 220));
                    }
                    if(fatpercent>=32f)
                    {
                        mChart.highlightValue(3,0, false);
                        l4.setBackgroundColor(Color.rgb(220, 220, 220));
                    }

                }
                if(sex_selected.equals("Female"))
                {
                    if(fatpercent>0f && fatpercent<=12.9f)
                    {
                        mChart.highlightValue(0,0, false);
                        l1.setBackgroundColor(Color.rgb(220, 220, 220));
                    }
                    if(fatpercent>=13.0f && fatpercent<=22.9f )
                    {
                        mChart.highlightValue(1,0, false);
                        l2.setBackgroundColor(Color.rgb(220, 220, 220));
                    }
                    if(fatpercent>=23.0f && fatpercent<=27.9f)
                    {
                        mChart.highlightValue(2,0, false);
                        l3.setBackgroundColor(Color.rgb(220, 220, 220));
                    }
                    if(fatpercent>=28.0f)
                    {
                        mChart.highlightValue(3,0, false);
                        l4.setBackgroundColor(Color.rgb(220, 220, 220));
                    }

                }

            }
            if(Integer.parseInt(age.getText().toString())==11 || Integer.parseInt(age.getText().toString())==12 ||
                    Integer.parseInt(age.getText().toString())==13)
            {
                if(sex_selected.equals("Male"))
                {
                    if(fatpercent>0f &&  fatpercent<=15.9f )
                    {
                        mChart.highlightValue(0,0, false);
                        l1.setBackgroundColor(Color.rgb(220, 220, 220));
                    }
                    if(fatpercent>=16.0f && fatpercent<=28.9f )
                    {
                        mChart.highlightValue(1,0, false);
                        l2.setBackgroundColor(Color.rgb(220, 220, 220));
                    }
                    if(fatpercent>=29f && fatpercent<=32.9f)
                    {
                        mChart.highlightValue(2,0, false);
                        l3.setBackgroundColor(Color.rgb(220, 220, 220));
                    }
                    if(fatpercent>=33f)
                    {
                        mChart.highlightValue(3,0, false);
                        l4.setBackgroundColor(Color.rgb(220, 220, 220));
                    }

                }
                if(sex_selected.equals("Female"))
                {
                    if(fatpercent>0f && fatpercent<=12.9f)
                    {
                        mChart.highlightValue(0,0, false);
                        l1.setBackgroundColor(Color.rgb(220, 220, 220));
                    }
                    if(fatpercent>=13.0f && fatpercent<=22.9f )
                    {
                        mChart.highlightValue(1,0, false);
                        l2.setBackgroundColor(Color.rgb(220, 220, 220));
                    }
                    if(fatpercent>=23.0f && fatpercent<=27.9f)
                    {
                        mChart.highlightValue(2,0, false);
                        l3.setBackgroundColor(Color.rgb(220, 220, 220));
                    }
                    if(fatpercent>=28.0f)
                    {
                        mChart.highlightValue(3,0, false);
                        l4.setBackgroundColor(Color.rgb(220, 220, 220));
                    }

                }

            }
            if(Integer.parseInt(age.getText().toString())==14 || Integer.parseInt(age.getText().toString())==15
                    || Integer.parseInt(age.getText().toString())==16)
            {
                if(sex_selected.equals("Male"))
                {
                    if(fatpercent>0f &&  fatpercent<=15.9f )
                    {
                        mChart.highlightValue(0,0, false);
                        l1.setBackgroundColor(Color.rgb(220, 220, 220));
                    }
                    if(fatpercent>=16.0f && fatpercent<=29.9f )
                    {
                        mChart.highlightValue(1,0, false);
                        l2.setBackgroundColor(Color.rgb(220, 220, 220));
                    }
                    if(fatpercent>=30f && fatpercent<=33.9f)
                    {
                        mChart.highlightValue(2,0, false);
                        l3.setBackgroundColor(Color.rgb(220, 220, 220));
                    }
                    if(fatpercent>=34f)
                    {
                        mChart.highlightValue(3,0, false);
                        l4.setBackgroundColor(Color.rgb(220, 220, 220));
                    }

                }
                if(sex_selected.equals("Female"))
                {
                    if(fatpercent>0f && fatpercent<=11.9f)
                    {
                        mChart.highlightValue(0,0, false);

                        l1.setBackgroundColor(Color.rgb(220, 220, 220));
                    }
                    if(fatpercent>=12.0f && fatpercent<=20.9f )
                    {
                        mChart.highlightValue(1,0, false);
                        l2.setBackgroundColor(Color.rgb(220, 220, 220));
                    }
                    if(fatpercent>=21.0f && fatpercent<=25.9f)
                    {
                        mChart.highlightValue(2,0, false);
                        l3.setBackgroundColor(Color.rgb(220, 220, 220));
                    }
                    if(fatpercent>=26.0f)
                    {
                        mChart.highlightValue(3,0, false);
                        l4.setBackgroundColor(Color.rgb(220, 220, 220));
                    }

                }

            }
            if(Integer.parseInt(age.getText().toString())==17 || Integer.parseInt(age.getText().toString())==18)
            {
                if(sex_selected.equals("Male"))
                {
                    if(fatpercent>0f &&  fatpercent<=15.9f )
                    {
                        mChart.highlightValue(0,0, false);
                        l1.setBackgroundColor(Color.rgb(220, 220, 220));
                    }
                    if(fatpercent>=16.0f && fatpercent<=29.9f )
                    {
                        mChart.highlightValue(1,0, false);
                        l2.setBackgroundColor(Color.rgb(220, 220, 220));
                    }
                    if(fatpercent>=30f && fatpercent<=34.9f)
                    {
                        mChart.highlightValue(2,0, false);
                        l3.setBackgroundColor(Color.rgb(220, 220, 220));
                    }
                    if(fatpercent>=35f)
                    {
                        mChart.highlightValue(3,0, false);
                        l4.setBackgroundColor(Color.rgb(220, 220, 220));
                    }

                }
                if(sex_selected.equals("Female"))
                {
                    if(fatpercent>0f && fatpercent<=9.9f)
                    {
                        mChart.highlightValue(0,0, false);

                        l1.setBackgroundColor(Color.rgb(220, 220, 220));
                    }
                    if(fatpercent>=10.0f && fatpercent<=19.9f )
                    {
                        mChart.highlightValue(1,0, false);
                        l2.setBackgroundColor(Color.rgb(220, 220, 220));
                    }
                    if(fatpercent>=20.0f && fatpercent<=23.9f)
                    {
                        mChart.highlightValue(2,0, false);
                        l3.setBackgroundColor(Color.rgb(220, 220, 220));
                    }
                    if(fatpercent>=24.0f)
                    {
                        mChart.highlightValue(3,0, false);
                        l4.setBackgroundColor(Color.rgb(220, 220, 220));
                    }

                }

            }
            if(Integer.parseInt(age.getText().toString())==19)
            {
                if(sex_selected.equals("Male"))
                {
                    if(fatpercent>0f &&  fatpercent<=18.9f )
                    {
                        mChart.highlightValue(0,0, false);
                        l1.setBackgroundColor(Color.rgb(220, 220, 220));
                    }
                    if(fatpercent>=19.0f && fatpercent<=31.9f )
                    {
                        mChart.highlightValue(1,0, false);
                        l2.setBackgroundColor(Color.rgb(220, 220, 220));
                    }
                    if(fatpercent>=32f && fatpercent<=36.9f)
                    {
                        mChart.highlightValue(2,0, false);
                        l3.setBackgroundColor(Color.rgb(220, 220, 220));
                    }
                    if(fatpercent>=37f)
                    {
                        mChart.highlightValue(3,0, false);
                        l4.setBackgroundColor(Color.rgb(220, 220, 220));
                    }

                }
                if(sex_selected.equals("Female"))
                {
                    if(fatpercent>0f && fatpercent<=8.9f)
                    {
                        mChart.highlightValue(0,0, false);

                        l1.setBackgroundColor(Color.rgb(220, 220, 220));
                    }
                    if(fatpercent>=9.0f && fatpercent<=19.9f )
                    {
                        mChart.highlightValue(1,0, false);
                        l2.setBackgroundColor(Color.rgb(220, 220, 220));
                    }
                    if(fatpercent>=20.0f && fatpercent<=23.9f)
                    {
                        mChart.highlightValue(2,0, false);
                        l3.setBackgroundColor(Color.rgb(220, 220, 220));
                    }
                    if(fatpercent>=24.0f)
                    {
                        mChart.highlightValue(3,0, false);
                        l4.setBackgroundColor(Color.rgb(220, 220, 220));
                    }

                }

            }
            if(Integer.parseInt(age.getText().toString())>=20 && Integer.parseInt(age.getText().toString())<=39 )
            {
                if(sex_selected.equals("Male"))
                {
                    if(fatpercent>0f &&  fatpercent<=7.9f )
                    {
                        mChart.highlightValue(0,0, false);
                        l1.setBackgroundColor(Color.rgb(220, 220, 220));
                    }
                    if(fatpercent>=8.0f && fatpercent<=19.9f )
                    {
                        mChart.highlightValue(1,0, false);
                        l2.setBackgroundColor(Color.rgb(220, 220, 220));
                    }
                    if(fatpercent>=20f && fatpercent<=24.9f)
                    {
                        mChart.highlightValue(2,0, false);
                        l3.setBackgroundColor(Color.rgb(220, 220, 220));
                    }
                    if(fatpercent>=25f)
                    {
                        mChart.highlightValue(3,0, false);
                        l4.setBackgroundColor(Color.rgb(220, 220, 220));
                    }

                }
                if(sex_selected.equals("Female"))
                {
                    if(fatpercent>0f && fatpercent<=20.9f)
                    {
                        mChart.highlightValue(0,0, false);
                        l1.setBackgroundColor(Color.rgb(220, 220, 220));
                    }
                    if(fatpercent>=21.0f && fatpercent<=32.9f )
                    {
                        mChart.highlightValue(1,0, false);
                        l2.setBackgroundColor(Color.rgb(220, 220, 220));
                    }
                    if(fatpercent>=33.0f && fatpercent<=38.9f)
                    {
                        mChart.highlightValue(2,0, false);
                        l3.setBackgroundColor(Color.rgb(220, 220, 220));
                    }
                    if(fatpercent>=39.0f)
                    {
                        mChart.highlightValue(3,0, false);
                        l4.setBackgroundColor(Color.rgb(220, 220, 220));
                    }

                }

            }
            if(Integer.parseInt(age.getText().toString())>=40 && Integer.parseInt(age.getText().toString())<=59)
            {
                if(sex_selected.equals("Male"))
                {
                    if(fatpercent>0f &&  fatpercent<=10.9f)
                    {
                        mChart.highlightValue(0,0, false);
                        l1.setBackgroundColor(Color.rgb(220, 220, 220));
                    }
                    if(fatpercent>=11.0f && fatpercent<=21.9f )
                    {
                        mChart.highlightValue(1,0, false);
                        l2.setBackgroundColor(Color.rgb(220, 220, 220));
                    }
                    if(fatpercent>=22f && fatpercent<=27.9f)
                    {
                        mChart.highlightValue(2,0, false);
                        l3.setBackgroundColor(Color.rgb(220, 220, 220));
                    }
                    if(fatpercent>=28.0f)
                    {
                        mChart.highlightValue(3,0, false);
                        l4.setBackgroundColor(Color.rgb(220, 220, 220));
                    }

                }
                if(sex_selected.equals("Female"))
                {
                    if(fatpercent>0f && fatpercent<=22.9f)
                    {
                        mChart.highlightValue(0,0, false);

                        l1.setBackgroundColor(Color.rgb(220, 220, 220));
                    }
                    if(fatpercent>=23.0f && fatpercent<=33.9f )
                    {
                        mChart.highlightValue(1,0, false);

                        l2.setBackgroundColor(Color.rgb(220, 220, 220));
                    }
                    if(fatpercent>=34.0f && fatpercent<=39.9f)
                    {
                        mChart.highlightValue(2,0, false);
                        l3.setBackgroundColor(Color.rgb(220, 220, 220));
                    }
                    if(fatpercent>=40.0f)
                    {
                        mChart.highlightValue(3,0, false);
                        l4.setBackgroundColor(Color.rgb(220, 220, 220));
                    }

                }

            }
            if(Integer.parseInt(age.getText().toString())>=60)
            {
                if(sex_selected.equals("Male"))
                {
                    if(fatpercent>0f &&  fatpercent<=12.9f )
                    {
                        mChart.highlightValue(0,0, false);

                        l1.setBackgroundColor(Color.rgb(220, 220, 220));
                    }
                    if(fatpercent>=13.0f && fatpercent<=24.9f )
                    {
                        mChart.highlightValue(1,0, false);
                        l2.setBackgroundColor(Color.rgb(220, 220, 220));
                    }
                    if(fatpercent>=25f && fatpercent<=29.9f)
                    {
                        mChart.highlightValue(2,0, false);
                        l3.setBackgroundColor(Color.rgb(220, 220, 220));
                    }
                    if(fatpercent>=30f)
                    {
                        mChart.highlightValue(3,0, false);
                        l4.setBackgroundColor(Color.rgb(220, 220, 220));
                    }

                }
                if(sex_selected.equals("Female"))
                {
                    if(fatpercent>0f && fatpercent<=23.9f)
                    {
                        mChart.highlightValue(0,0, false);
                        l1.setBackgroundColor(Color.rgb(220, 220, 220));
                    }
                    if(fatpercent>=24.0f && fatpercent<=35.9f )
                    {
                        mChart.highlightValue(1,0, false);
                        l2.setBackgroundColor(Color.rgb(220, 220, 220));
                    }
                    if(fatpercent>=36.0f && fatpercent<=41.9f)
                    {
                        mChart.highlightValue(2,0, false);
                        l3.setBackgroundColor(Color.rgb(220, 220, 220));
                    }
                    if(fatpercent>=42.0f)
                    {
                        mChart.highlightValue(3,0, false);
                        l4.setBackgroundColor(Color.rgb(220, 220, 220));
                    }

                }

            }

        }

    }
    public void ChangeText(String selectedSex)
    {
        if(selectedSex.equals("Male"))
        {
            if(age.getText().toString().length()==0 || (Integer.parseInt(age.getText().toString())>=1 && Integer.parseInt(age.getText().toString())<=6) )
            {
                t1.setText("<=0.0");
                t2.setText("0.0-0.0");
                t3.setText("0.0-0.0");
                t4.setText(">=0.0");

            }

            else if(Integer.parseInt(age.getText().toString())==7)
            {
                t1.setText("<=14.9");
                t2.setText("15.0-24.9");
                t3.setText("25.0-28.9");
                t4.setText(">=29.0");
            }
            else if(Integer.parseInt(age.getText().toString())==8)
            {
                t1.setText("<=14.9");
                t2.setText("15.0-25.9");
                t3.setText("26.0-29.9");
                t4.setText(">30.0");

            }
            else if(Integer.parseInt(age.getText().toString())==9)
            {
                t1.setText("<=15.9");
                t2.setText("16.0-26.9");
                t3.setText("27.0-30.9");
                t4.setText(">=31.0");

            }
            else if(Integer.parseInt(age.getText().toString())==10)
            {
                t1.setText("<=15.9");
                t2.setText("16.0-27.9");
                t3.setText("28.0-31.9");
                t4.setText(">=32.0");


            }
            else if(Integer.parseInt(age.getText().toString())==11)
            {
                t1.setText("<=15.9");
                t2.setText("16.0-28.9");
                t3.setText("29.0-32.9");
                t4.setText(">=33.0");

            }
            else if(Integer.parseInt(age.getText().toString())==12)
            {
                t1.setText("<=15.9");
                t2.setText("16.0-27.9");
                t3.setText("28.0-31.9");
                t4.setText(">=32.0");

            }
            else if(Integer.parseInt(age.getText().toString())==13)
            {
                t1.setText("<=15.9");
                t2.setText("16.0-27.9");
                t3.setText("28.0-31.9");
                t4.setText(">=32.0");

            }
            else if(Integer.parseInt(age.getText().toString())==14)
            {
                t1.setText("<=15.9");
                t2.setText("16.0-29.9");
                t3.setText("30.0-33.9");
                t4.setText(">=34.0");

            }
            else if(Integer.parseInt(age.getText().toString())==15 || Integer.parseInt(age.getText().toString())==16)
            {
                t1.setText("<=15.9");
                t2.setText("16.0-27.9");
                t3.setText("28.0-31.9");
                t4.setText(">=32.0");

            }

            else if(Integer.parseInt(age.getText().toString())==17)
            {
                t1.setText("<=15.9");
                t2.setText("16.0-29.9");
                t3.setText("30.0-34.9");
                t4.setText(">=35.0");

            }
            else if(Integer.parseInt(age.getText().toString())==18)
            {
                t1.setText("<=16.9");
                t2.setText("17.0-30.9");
                t3.setText("31.0-35.9");
                t4.setText(">=36.0");

            }
            else if(Integer.parseInt(age.getText().toString())==19)
            {
                t1.setText("<=18.9");
                t2.setText("19.0-31.9");
                t3.setText("32.0-36.9");
                t4.setText(">=37.0");


            }

            else if(Integer.parseInt(age.getText().toString())>=20 && Integer.parseInt(age.getText().toString())<=39)
            {
                t1.setText("<=7.9");
                t2.setText("8.0-19.9");
                t3.setText("20.0-24.9");
                t4.setText(">=25.0");

            }
            else if(Integer.parseInt(age.getText().toString())>=40 && Integer.parseInt(age.getText().toString())<=59)
            {
                t1.setText("<=10.9");
                t2.setText("11.0-21.9");
                t3.setText("22.0-27.9");
                t4.setText(">=28.0");
            }

            else if(Integer.parseInt(age.getText().toString())>=60)
            {
                t1.setText("<=12.9");
                t2.setText("13.0-24.9");
                t3.setText("25.0-29.9");
                t4.setText(">=30.0");
            }
        }

        if(selectedSex.equals("Female"))
        {
            if(age.getText().toString().length()==0 || (Integer.parseInt(age.getText().toString())>=1 && Integer.parseInt(age.getText().toString())<=6) )
            {
                t1.setText("<=0.00");
                t2.setText("0.00-0.00");
                t3.setText("0.00-0.00");
                t4.setText(">=0.00");

            }

            else if(Integer.parseInt(age.getText().toString())==7)
            {
                t1.setText("<=12.9");
                t2.setText("13.0-19.9");
                t3.setText("20.0-24.9");
                t4.setText(">=25.0");
            }
            else if(Integer.parseInt(age.getText().toString())==8)
            {
                t1.setText("<=12.9");
                t2.setText("13.0-20.9");
                t3.setText("21.0-25.9");
                t4.setText(">26.0");

            }
            else if(Integer.parseInt(age.getText().toString())==9)
            {
                t1.setText("<=12.9");
                t2.setText("13.0-22.9");
                t3.setText("23.0-27.9");
                t4.setText(">=28.0");

            }
            else if(Integer.parseInt(age.getText().toString())==10 || Integer.parseInt(age.getText().toString())==11
                    || Integer.parseInt(age.getText().toString())==12 || Integer.parseInt(age.getText().toString())==13)
            {
                t1.setText("<=12.9");
                t2.setText("13.0-22.9");
                t3.setText("23.0-27.9");
                t4.setText(">=28.0");


            }

            else if(Integer.parseInt(age.getText().toString())==14)
            {
                t1.setText("<=11.9");
                t2.setText("12.0-20.9");
                t3.setText("21.0-25.9");
                t4.setText(">=26.0");

            }
            else if(Integer.parseInt(age.getText().toString())==15)
            {
                t1.setText("<=10.9");
                t2.setText("11.0-20.9");
                t3.setText("21.0-23.9");
                t4.setText(">=24.0");

            }
            else if(Integer.parseInt(age.getText().toString())==16)
            {
                t1.setText("<=9.9");
                t2.setText("10.0-20.9");
                t3.setText("21.0-23.9");
                t4.setText(">=24.0");
            }
            else if(Integer.parseInt(age.getText().toString())==17 && Integer.parseInt(age.getText().toString())==18)
            {
                t1.setText("<=9.9");
                t2.setText("10.0-19.9");
                t3.setText("20.0-23.9");
                t4.setText(">=24.0");

            }
            else if(Integer.parseInt(age.getText().toString())==19)
            {
                t1.setText("<=8.9");
                t2.setText("9.0-19.9");
                t3.setText("20.0-23.9");
                t4.setText(">=24.0");


            }

            else if(Integer.parseInt(age.getText().toString())>=20 && Integer.parseInt(age.getText().toString())<=39)
            {
                t1.setText("<=20.9");
                t2.setText("21.0-32.9");
                t3.setText("33.0-38.9");
                t4.setText(">=39.0");

            }
            else if(Integer.parseInt(age.getText().toString())>=40 && Integer.parseInt(age.getText().toString())<=59)
            {
                t1.setText("<=22.9");
                t2.setText("23.0-33.9");
                t3.setText("34.0-39.9");
                t4.setText(">=40.0");
            }

            else if(Integer.parseInt(age.getText().toString())>=60)
            {
                t1.setText("<=23.9");
                t2.setText("24.0-35.9");
                t3.setText("36.0-41.9");
                t4.setText(">=42.0");
            }

        }
    }

    public void setchart(){

        mChart.setBackgroundColor(Color.rgb(255,255,255));
//        moveOffScreen();
        mChart.setUsePercentValues(false);
        mChart.getDescription().setEnabled(false);

        mChart.setDrawCenterText(true);
        mChart.setCenterTextTypeface(Typeface.DEFAULT_BOLD);

        mChart.setCenterTextColor(Color.BLACK);
        mChart.setCenterTextSize(18f);
        mChart.setCenterText("Fat:0.0%");

        mChart.setDrawHoleEnabled(true);
        mChart.setHoleColor(Color.rgb(255,255,255));

        mChart.setTransparentCircleColor(Color.rgb(255,255,255));
        mChart.setTransparentCircleAlpha(110);

        mChart.setHoleRadius(65f);
        mChart.setTransparentCircleRadius(68f);
        mChart.setDrawSliceText(false);
        mChart.setDrawEntryLabels(false);

        mChart.setRotationEnabled(false);
        mChart.setHighlightPerTapEnabled(false);

        mChart.setDrawSlicesUnderHole(true);

        mChart.setMaxAngle(270f); // HALF CHART
        mChart.setRotationAngle(135f);
        setData(8, 80);
        mChart.getLegend().setEnabled(false);
        mChart.invalidate();

    }

    public void set_spinner(){

        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(getActivity(), R.array.height_spinner_array, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        height_spinner.setAdapter(adapter1);

        height_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                Bundle bundle=new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "2.5");
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "BFP Height Spinner");
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Button");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                int a = parent.getSelectedItemPosition();
                spinner_height = height_spinner.getSelectedItem().toString();

                if (a == 0) {
                    selected_item1="ft + in";
                    height_ft.setVisibility(View.VISIBLE);
                    height_in.setVisibility(View.VISIBLE);
                    height_cm.setVisibility(View.GONE);

                }
                if(a==1)
                {
                    selected_item1="cm";
                    height_ft.setVisibility(View.GONE);
                    height_in.setVisibility(View.GONE);
                    height_cm.setVisibility(View.VISIBLE);

                }
                calculate();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });


        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(getActivity(), R.array.waist_spinner_array, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        waist_spinner.setAdapter(adapter2);
        waist_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                Bundle bundle=new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "2.6");
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "BFP Weight Spinner");
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Button");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                int b = parent.getSelectedItemPosition();
                spinner_waist = waist_spinner.getSelectedItem().toString();
                if(b==0)
                {
                    selected_item2="in";
                }
                if(b==1)
                {
                    selected_item2="cm";
                }
                calculate();

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {} } );

        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(getActivity(), R.array.neck_spinner_array, android.R.layout.simple_spinner_item);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        neck_spinner.setAdapter(adapter3);
        neck_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
            {
                Bundle bundle=new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "2.7");
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "BFP Neck Spinner");
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Button");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);


                int c = parent.getSelectedItemPosition();
                spinner_neck = neck_spinner.getSelectedItem().toString();
                if(c==0)
                {
                    selected_item3="in";
                }
                if(c==1)
                {
                    selected_item3="cm";
                }
                calculate();

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {} } );

        ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(getActivity(), R.array.hip_spinner_array, android.R.layout.simple_spinner_item);
        adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        hip_spinner.setAdapter(adapter4);
        hip_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
            {
                Bundle bundle=new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "2.8");
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "BFP Hip Spinner");
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Button");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                int d = parent.getSelectedItemPosition();
                spinner_hip = hip_spinner.getSelectedItem().toString();
                if(d==0)
                {
                    selected_item4="in";
                }
                if(d==1)
                {
                    selected_item4="cm";
                }
                calculate();

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {} } );
    }

    public void calculate()
    {

        mChart.setDrawCenterText(true);
        mChart.setCenterTextTypeface(Typeface.DEFAULT_BOLD);
        mChart.setCenterTextColor(Color.BLACK);
        mChart.setCenterTextSize(18f);

        fatpercent=0f;
        float getHeight=0f, getwaist,getneck,gethip;
        double logpart1;
        double logpart2;
        float bodydensity;

        DecimalFormat df = new DecimalFormat("#0.0");

        if(height_ft.isShown() && height_in.isShown())
        {
            if(height_ft.getText().toString().equals("") && height_in.getText().toString().equals(""))
                getHeight=0f;
            if(height_ft.getText().toString().equals("") && !height_in.getText().toString().equals(""))
                getHeight = Float.parseFloat(height_in.getText().toString());
            if(height_in.getText().toString().equals("")&& !height_ft.getText().toString().equals(""))
                getHeight = Float.parseFloat(height_ft.getText().toString())*12;
            if(!height_ft.getText().toString().equals("") && !height_in.getText().toString().equals(""))
                getHeight = Float.parseFloat(height_ft.getText().toString())*12+ Float.parseFloat(height_in.getText().toString());
        }
        if(height_cm.isShown())
        {
            if(height_cm.getText().toString().equals(""))
                getHeight=0f;
            else  getHeight=Float.parseFloat(height_cm.getText().toString());
        }

        if (waist.getText().toString().equals("")) {
            getwaist = 0f;
        } else {
            getwaist = Float.parseFloat(waist.getText().toString());
        }
        if (neck.getText().toString().equals("")) {
            getneck = 0f;
        } else {
            getneck = Float.parseFloat(neck.getText().toString());
        }
        if (hip.getText().toString().equals("")) {
            gethip = 0f;
        } else {
            gethip = Float.parseFloat(hip.getText().toString());
        }
 	if(sex_selected.equals("Male"))
        {
            if(getwaist!=0f && getneck!=0f && getHeight!=0f)
            {
                if (selected_item1.equals("ft + in")) {
                    if (selected_item2.equals("cm")) {
                        if (selected_item3.equals("cm")) {
                            getwaist = getwaist / 2.54f;
                            getneck = getneck / 2.54f;
                            if (getwaist - getneck > 0) {
                                logpart1 = Math.log10(getwaist - getneck);
                                logpart2 = Math.log10(getHeight);
                                bodydensity = 1.01774f - (float) (0.19077f * logpart1) + (float) (0.15456f * logpart2);
                                fatpercent = (495 / bodydensity) - 450;
                            } else fatpercent = 0f;
                        }
                        if (selected_item3.equals("in")) {
                            getwaist = getwaist / 2.54f;
                            if (getwaist - getneck > 0) {
                                logpart1 = Math.log10(getwaist - getneck);
                                logpart2 = Math.log10(getHeight);
                                bodydensity = 1.01774f - (float) (0.19077f * logpart1) + (float) (0.15456f * logpart2);
                                fatpercent = (495 / bodydensity) - 450;
                            } else fatpercent = 0f;
                        }

                    }
                    if (selected_item2.equals("in")) {
                        if (selected_item3.equals("cm")) {
                            getneck = getneck / 2.54f;
                            if (getwaist - getneck > 0) {
                                logpart1 = Math.log10(getwaist - getneck);
                                logpart2 = Math.log10(getHeight);
                                bodydensity = 1.01774f - (float) (0.19077f * logpart1) + (float) (0.15456f * logpart2);
                                fatpercent = (495 / bodydensity) - 450;
                            } else fatpercent = 0f;
                        }
                        if (selected_item3.equals("in")) {
                            if (getwaist - getneck > 0) {
                                logpart1 = Math.log10(getwaist - getneck);
                                logpart2 = Math.log10(getHeight);
                                bodydensity = 1.01774f - (float) (0.19077f * logpart1) + (float) (0.15456f * logpart2);
                                fatpercent = (495 / bodydensity) - 450;
                            } else fatpercent = 0f;
                        }

                    }


                }

//
//                    if (getwaist - getneck > 0) {
//                        logpart1 = Math.log10(getwaist - getneck);
//                        logpart2 = Math.log10(getHeight);
//                        bodydensity = 1.01774f - (float) (0.19077f * logpart1) + (float) (0.15456f * logpart2);
//                        fatpercent = (495 / bodydensity) - 450;
//                    }
//                 else if (selected_item1.equals("cm") && selected_item2.equals("cm") && selected_item3.equals("cm")) {
//                    if (getwaist - getneck > 0) {
//                        logpart1 = Math.log10((getwaist - getneck) / 2.54);
//                        logpart2 = Math.log10(getHeight / 2.54);
//                        bodydensity = 1.01774f - (float) (0.19077f * logpart1) + (float) (0.15456f * logpart2);
//                        fatpercent = (495 / bodydensity) - 450;
//                    }
//                }
               else  if (selected_item1.equals("cm")) {
                    if (selected_item2.equals("cm")) {

                        if (selected_item3.equals("cm")) {
                            if (getwaist - getneck > 0) {
                                logpart1 = Math.log10((getwaist - getneck) / 2.54);
                                logpart2 = Math.log10(getHeight / 2.54);
                                bodydensity = 1.01774f - (float) (0.19077f * logpart1) + (float) (0.15456f * logpart2);
                                fatpercent = (495 / bodydensity) - 450;
                            }
                            else fatpercent = 0f;
                        }
                        if (selected_item3.equals("in")) {
                            getneck = getneck*2.54f;
                            if (getwaist - getneck > 0) {
                                logpart1 = Math.log10((getwaist - getneck) / 2.54);
                                logpart2 = Math.log10(getHeight / 2.54);
                                bodydensity = 1.01774f - (float) (0.19077f * logpart1) + (float) (0.15456f * logpart2);
                                fatpercent = (495 / bodydensity) - 450;
                            }
                            else fatpercent = 0f;
                        }

                    }
                    if (selected_item2.equals("in")) {
                        if (selected_item3.equals("cm")) {
                            getwaist = getwaist* 2.54f;
                            if (getwaist - getneck > 0) {
                                logpart1 = Math.log10((getwaist - getneck) / 2.54);
                                logpart2 = Math.log10(getHeight / 2.54);
                                bodydensity = 1.01774f - (float) (0.19077f * logpart1) + (float) (0.15456f * logpart2);
                                fatpercent = (495 / bodydensity) - 450;
                            }
                            else fatpercent = 0f;
                        }
                        if (selected_item3.equals("in")) {
                            getwaist=getwaist*2.54f;
                            getneck=getneck*2.54f;
                            if (getwaist - getneck > 0) {
                                logpart1 = Math.log10((getwaist - getneck) / 2.54);
                                logpart2 = Math.log10(getHeight / 2.54);
                                bodydensity = 1.01774f - (float) (0.19077f * logpart1) + (float) (0.15456f * logpart2);
                                fatpercent = (495 / bodydensity) - 450;
                            }
                            else fatpercent = 0f;
                        }

                    }

                }

            }
            else fatpercent=0f;

        }
        if(sex_selected.equals("Female"))
        {
            if(getwaist!=0f && gethip!=0f && getneck!=0f) {

                if (selected_item1.equals("ft + in"))
                {
                    if(selected_item2.equals("in"))
                    {
                        if(selected_item3.equals("cm") && selected_item4.equals("cm") )
                        {
                            getneck=getneck/2.54f;
                            gethip=gethip/2.54f;
                        }
                        if(selected_item3.equals("in") && selected_item4.equals("cm") )
                        {
                            gethip=gethip/2.54f;
                        }
                        if(selected_item3.equals("cm") && selected_item4.equals("in") )
                        {
                            getneck=getneck/2.54f;

                        }
                        if (getwaist + gethip - getneck > 0) {
                            logpart1 = Math.log10(getwaist + gethip - getneck);
                            logpart2 = Math.log10(getHeight);
                            bodydensity = 1.29579f - (float) (0.35004f * logpart1) + (float) (0.22100f * logpart2);
                            fatpercent = (519 / bodydensity) - 450;
                        }else fatpercent = 0f;

                    }
                    if(selected_item2.equals("cm"))
                    {
                        getwaist=getwaist/2.54f;
                        if(selected_item3.equals("cm") && selected_item4.equals("cm") )
                        {
                            getneck=getneck/2.54f;
                            gethip=gethip/2.54f;
                        }
                        if(selected_item3.equals("in") && selected_item4.equals("cm") )
                        {
                            gethip=gethip/2.54f;
                        }
                        if(selected_item3.equals("cm") && selected_item4.equals("in") )
                        {
                            getneck=getneck/2.54f;
                        }
                        if (getwaist + gethip - getneck > 0) {
                            logpart1 = Math.log10(getwaist + gethip - getneck);
                            logpart2 = Math.log10(getHeight);
                            bodydensity = 1.29579f - (float) (0.35004f * logpart1) + (float) (0.22100f * logpart2);
                            fatpercent = (519 / bodydensity) - 450;
                        }else fatpercent = 0f;

                    }
                }
                else if (selected_item1.equals("cm"))
                {
                    getHeight=getHeight/2.54f;

                    if(selected_item2.equals("in"))
                    {
                        if(selected_item3.equals("cm") && selected_item4.equals("cm") )
                        {
                            getneck=getneck/2.54f;
                            gethip=gethip/2.54f;
                        }
                        if(selected_item3.equals("in") && selected_item4.equals("cm") )
                        {
                            gethip=gethip/2.54f;
                        }
                        if(selected_item3.equals("cm") && selected_item4.equals("in") )
                        {
                            getneck=getneck/2.54f;

                        }
                        if (getwaist + gethip - getneck > 0) {
                            logpart1 = Math.log10(getwaist + gethip - getneck);
                            logpart2 = Math.log10(getHeight);
                            bodydensity = 1.29579f - (float) (0.35004f * logpart1) + (float) (0.22100f * logpart2);
                            fatpercent = (519 / bodydensity) - 450;
                        }else fatpercent = 0f;

                    }
                    if(selected_item2.equals("cm"))
                    {
                        getwaist=getwaist/2.54f;
                        if(selected_item3.equals("cm") && selected_item4.equals("cm") )
                        {
                            getneck=getneck/2.54f;
                            gethip=gethip/2.54f;
                        }
                        if(selected_item3.equals("in") && selected_item4.equals("cm") )
                        {
                            gethip=gethip/2.54f;
                        }
                        if(selected_item3.equals("cm") && selected_item4.equals("in") )
                        {
                            getneck=getneck/2.54f;
                        }
                        if (getwaist + gethip - getneck > 0) {
                            logpart1 = Math.log10(getwaist + gethip - getneck);
                            logpart2 = Math.log10(getHeight);
                            bodydensity = 1.29579f - (float) (0.35004f * logpart1) + (float) (0.22100f * logpart2);
                            fatpercent = (519 / bodydensity) - 450;
                        }else fatpercent = 0f;

                    }
                }
            }
            else fatpercent=0f;
        }

        fatpercent=Float.valueOf(df.format(fatpercent));
        if(fatpercent>=0f)
        mChart.setCenterText("Fat:"+df.format(fatpercent)+"%");

        else mChart.setCenterText("Fat:0.0%");

        SetBackcolor(fatpercent);

    }




    private void setData(int count, float range)
    {

        ArrayList<PieEntry> values = new ArrayList<>();

        values.add(new PieEntry(10f,Fatcategory[0]));
        values.add(new PieEntry(15f,Fatcategory[1]));
        values.add(new PieEntry(8f,Fatcategory[2]));
        values.add(new PieEntry(12f,Fatcategory[3]));

        PieDataSet dataSet = new PieDataSet(values, "Fat Category");
        dataSet.setSliceSpace(2f);
        dataSet.setSelectionShift(5f);

        // add many colors
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.rgb(63,200,247));
        colors.add(Color.rgb(65,189,103));
        colors.add(Color.rgb(243,207,0));
        colors.add(Color.rgb(248,154,20));

        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setDrawValues(false);
        mChart.setData(data);
        mChart.invalidate();
    }

    @Override
    public void onRefresh() {
        swipe.postDelayed(new Runnable() {
            @Override
            public void run() {
                swipe.setRefreshing(true);
                LoadAd();
            }
        }, 500);

    }
}
