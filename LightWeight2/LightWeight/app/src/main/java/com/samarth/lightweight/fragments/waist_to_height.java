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
import android.widget.Toast;

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

public class waist_to_height extends android.support.v4.app.Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private SwipeRefreshLayout swipe;

    private RadioGroup radioSexGroup;
    private RadioButton radiomale;
    private RadioButton radiofemale;
    private int selectedId;
    private EditText age;
    private EditText height_ft,height_in,height_cm;
    private EditText waist;
    private SharedPreferences sharedpreferences;
    private LinearLayout l1,l2,l3,l4,l5,l6;
    private TextView t1,t2,t3,t4,t5,t6;
    private PieChart mChart ;

    private float wth;
    private ScrollView scrollView;

    private Spinner height_spinner;
    private Spinner waist_spinner;

    private FirebaseAnalytics mFirebaseAnalytics;

    private AdView adView;

    String spinner_waist; //Height Spinner
    String spinner_height;//Weight Spinner
    String selected_item1="ft + in";
    String selected_item2="in";
    String sex_selected="Male";

    protected String[] wthcategory = new String[]
            {
                    "Extremely Slim", "Healthy Slim", "Healthy", "Overweight","Very Overweight","Morbidly Obese"
            };


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.waist_to_height, container, false);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());

        getActivity().setTitle("Waist-to-Height Ratio");
        findId(view);

        return view;
    }

    @Override
    public void onStart(){
        super.onStart();

        Log.i("On start of Wth","+");

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "3");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Wth Fragment Started");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Fragment Open");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, bundle);

    }
    public void findId(View view){

        sharedpreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        swipe=(SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        radioSexGroup = (RadioGroup) view.findViewById(R.id.radioSex);

        radiomale = (RadioButton) view.findViewById(R.id.radioMale);
        radiofemale=(RadioButton) view.findViewById(R.id.radioFemale);


//        Log.i("boolean wthr_m",sharedpreferences.getBoolean(Gender_selected_male,false)+"");
//        Log.i("boolean wthr_f",sharedpreferences.getBoolean(Gender_selected_female,false)+"");


        if(sharedpreferences.getBoolean(Gender_selected_male,false))
        {
            radiomale.setChecked(sharedpreferences.getBoolean(Gender_selected_male,false));
            sex_selected="Male";
        }
        if(sharedpreferences.getBoolean(Gender_selected_female,false))
        {
            radiofemale.setChecked(sharedpreferences.getBoolean(Gender_selected_female,false));
            sex_selected="Female";
        }

        scrollView=(ScrollView) view.findViewById(R.id.scroll_wth);

        age=(EditText) view.findViewById(R.id.age);
        age.setText(sharedpreferences.getString(Age,""));



        height_ft=(EditText) view.findViewById(R.id.height_ft);
        height_in=(EditText) view.findViewById(R.id.height_in);
        height_cm=(EditText) view.findViewById(R.id.height_cm);

        waist=(EditText) view.findViewById(R.id.waist);
//        result=(TextView) findViewById(R.id.result);
        height_spinner=(Spinner) view.findViewById(R.id.height_spinner);
        waist_spinner=(Spinner) view.findViewById(R.id.waist_spinner);
        mChart=(PieChart)view.findViewById(R.id.chart);
        l1=(LinearLayout) view.findViewById(R.id.linearlayout1);
        l2=(LinearLayout) view.findViewById(R.id.linearlayout2);
        l3=(LinearLayout) view.findViewById(R.id.linearlayout3);
        l4=(LinearLayout) view.findViewById(R.id.linearlayout4);
        l5=(LinearLayout) view.findViewById(R.id.linearlayout5);
        l6=(LinearLayout) view.findViewById(R.id.linearlayout6);
        t1=(TextView) view.findViewById(R.id.text1);
        t2=(TextView) view.findViewById(R.id.text2);
        t3=(TextView) view.findViewById(R.id.text3);
        t4=(TextView) view.findViewById(R.id.text4);
        t5=(TextView) view.findViewById(R.id.text5);
        t6=(TextView) view.findViewById(R.id.text6);
        t1.setText("<=0.00");
        age.addTextChangedListener(watch);

        height_ft.addTextChangedListener(watch1);
        height_in.addTextChangedListener(watch1);
        height_cm.addTextChangedListener(watch1);
        waist.addTextChangedListener(watch1);

        adView=(AdView)view.findViewById(R.id.adView);
        swipe.setOnRefreshListener(this);

        LoadAd();

        radioSexGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                Bundle bundle = new Bundle();

                Log.i("Selected_id:", checkedId + "");
                switch (checkedId)
                {
                    case(R.id.radioFemale):
                        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "3.1");
                        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Wth Radio button female ");
                        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Button");
                        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                    sex_selected = "Female";
                        ChangeText(sex_selected);
                        calculate();
                    break;

                    case(R.id.radioMale):
                        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "3.2");
                        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Wth Radio button male ");
                        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Button");
                        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                        sex_selected = "Male";

                        ChangeText(sex_selected);
                        calculate();


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
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "3.4");
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Wth Age edittext");
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
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "3.3");
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "WtHR height/weight edittext");
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "TextBox");
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

            setchart();
            calculate();
        }

    };


    public void ChangeText(String SexSelected){
        if(SexSelected.equals("Male"))
        {
            if(age.getText().toString().length()==0)
            {
                t1.setText("<=0.00");
                t2.setText("0.00-0.00");
                t3.setText("0.00-0.00");
                t4.setText("0.00-0.00");
                t5.setText("0.00-0.00");
                t6.setText(">=0.00");

            }
            else
            {

                if (Integer.parseInt(age.getText().toString()) <= 15 && age.getText().toString().length() > 0) {
                    t1.setText("<0.35");
                    t2.setText("0.35-0.45");
                    t3.setText("0.46-0.51");
                    t4.setText("0.52-0.63");
                    t5.setText(">=0.64");
                    t6.setText("-");

                } else if (Integer.parseInt(age.getText().toString()) > 15 && age.getText().toString().length() > 0) {
                    t1.setText("<0.35");
                    t2.setText("0.35-0.42");
                    t3.setText("0.43-0.52");
                    t4.setText("0.53-0.57");
                    t5.setText("0.58-0.62");
                    t6.setText(">=0.63");
                }
            }
        }

        if(SexSelected.equals("Female"))
        {
            if(age.getText().toString().length()==0)
            {
                t1.setText("<=0.00");
                t2.setText("0.00-0.00");
                t3.setText("0.00-0.00");
                t4.setText("0.00-0.00");
                t5.setText("0.00-0.00");
                t6.setText(">=0.00");
            }
            else {
                if (Integer.parseInt(age.getText().toString()) <= 15 && age.getText().toString().length() > 0) {
                    t1.setText("<0.35");
                    t2.setText("0.35-0.45");
                    t3.setText("0.46-0.51");
                    t4.setText("0.52-0.63");
                    t5.setText(">=0.64");
                    t6.setText("-");
                } else if (Integer.parseInt(age.getText().toString()) > 15 && age.getText().toString().length() > 0) {
                    t1.setText("<0.35");
                    t2.setText("0.35-0.41");
                    t3.setText("0.42-0.48");
                    t4.setText("0.49-0.53");
                    t5.setText("0.54-0.57");
                    t6.setText(">=0.58");
                }
            }
        }
    }

    public int SetDatasetEntries() {

        if(age.getText().toString().length()!=0) {
            if (Integer.parseInt(age.getText().toString()) >= 1 && Integer.parseInt(age.getText().toString()) <= 15)
                return 5;
            else if (Integer.parseInt(age.getText().toString()) >= 16)
                return 6;
        }
        return 6;
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
        mChart.setCenterText("Ratio:0.00");

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

        setData(SetDatasetEntries(), 80);

        mChart.setMaxAngle(270f); // HALF CHART
        mChart.setRotationAngle(135f);
        mChart.getLegend().setEnabled(false);
        mChart.invalidate();

    }

    private void setData(int count, float range) {

        ArrayList<PieEntry> values = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<>();

        PieDataSet dataSet = new PieDataSet(values, "Waist_to_height Category");
        dataSet.setSliceSpace(2f);
        dataSet.setSelectionShift(5f);

        if(count==5) {
            values.add(new PieEntry(10f, wthcategory[0]));
            values.add(new PieEntry(15f, wthcategory[1]));
            values.add(new PieEntry(8f, wthcategory[2]));
            values.add(new PieEntry(12f, wthcategory[3]));
            values.add(new PieEntry(12f, wthcategory[4]));

            // add many colors
            colors.add(Color.rgb(63, 200, 247));
            colors.add(Color.rgb(65, 189, 103));
            colors.add(Color.rgb(243, 207, 0));
            colors.add(Color.rgb(248, 154, 20));
            colors.add(Color.rgb(31, 127, 218));
        }
        else{
            values.add(new PieEntry(10f, wthcategory[0]));
            values.add(new PieEntry(15f, wthcategory[1]));
            values.add(new PieEntry(8f, wthcategory[2]));
            values.add(new PieEntry(12f, wthcategory[3]));
            values.add(new PieEntry(12f, wthcategory[4]));
            values.add(new PieEntry(12f, wthcategory[5]));

            // add many colors
            colors.add(Color.rgb(63, 200, 247));
            colors.add(Color.rgb(65, 189, 103));
            colors.add(Color.rgb(243, 207, 0));
            colors.add(Color.rgb(248, 154, 20));
            colors.add(Color.rgb(31, 127, 218));
            colors.add(Color.rgb(37, 169, 254));
        }

        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setDrawValues(false);

        mChart.setData(data);
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
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "3.5");
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Wth Height Spinner ");
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
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "3.6");
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Wth Waist Spinner ");
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


    }

    public void SetBackColor(float wth)
    {
        mChart.invalidate();
        l1.setBackgroundColor(Color.rgb(255,255,255));
        l2.setBackgroundColor(Color.rgb(255,255,255));
        l3.setBackgroundColor(Color.rgb(255,255,255));
        l4.setBackgroundColor(Color.rgb(255,255,255));
        l5.setBackgroundColor(Color.rgb(255,255,255));
        l6.setBackgroundColor(Color.rgb(255,255,255));
        if(age.getText().toString().equals("") ||  Integer.parseInt(age.getText().toString())==0 ||  wth==-1f || wth==0f)
        {
            mChart.highlightValue(0, -1, false);
        }
        if(age.getText().toString().length()!=0)
        {
        if(Integer.parseInt(age.getText().toString())>=1 && Integer.parseInt(age.getText().toString())<=15)
            {
                if(wth>0f && wth <0.35f)
                {
                    l1.setBackgroundColor(Color.rgb(220,220,220));
                    mChart.highlightValue(0, 0, false);
                }
                if(wth>=0.35f && wth<=0.45f)
                {

                    l2.setBackgroundColor(Color.rgb(220,220,220));
                    mChart.highlightValue(1, 0, false);
                }
                if(wth>=0.46f && wth<=0.51f)
                {

                    l3.setBackgroundColor(Color.rgb(220,220,220));
                    mChart.highlightValue(2, 0, false);
                }
                if(wth>=0.52f && wth<=0.63f)
                {
                    l4.setBackgroundColor(Color.rgb(220,220,220));
                    mChart.highlightValue(3, 0, false);
                }
                if(wth>=0.64f)
                {
                    l5.setBackgroundColor(Color.rgb(220,220,220));
                    mChart.highlightValue(4, 0, false);
                }
            }
            if(Integer.parseInt(age.getText().toString())>=16)
            {
                if (sex_selected.equals("Male")) {
                    if (wth < 0.35f && wth > 0f) {
                        l1.setBackgroundColor(Color.rgb(220, 220, 220));
                        mChart.highlightValue(0, 0, false);
                    }
                    if (wth >= 0.35f && wth <= 0.42f) {
                        l2.setBackgroundColor(Color.rgb(220, 220, 220));
                        mChart.highlightValue(1, 0, false);
                    }
                    if (wth >= 0.43f && wth <= 0.52f) {
                        l3.setBackgroundColor(Color.rgb(220, 220, 220));
                        mChart.highlightValue(2, 0, false);
                    }
                    if (wth >= 0.53f && wth <= 0.57f) {

                        l4.setBackgroundColor(Color.rgb(220, 220, 220));
                        mChart.highlightValue(3, 0, false);
                    }
                    if (wth >= 0.58f && wth <= 0.62f) {

                        l5.setBackgroundColor(Color.rgb(220, 220, 220));
                        mChart.highlightValue(4, 0, false);
                    }
                    if (wth >= 0.63f) {

                        l6.setBackgroundColor(Color.rgb(220, 220, 220));
                        mChart.highlightValue(5, 0, false);
                    }
                }
                if (sex_selected.equals("Female")) {
                    if (wth > 0f && wth < 0.35f) {
                        l1.setBackgroundColor(Color.rgb(220, 220, 220));
                        mChart.highlightValue(0, 0, false);
                    }
                    if (wth >= 0.35f && wth <= 0.41f) {
                        l2.setBackgroundColor(Color.rgb(220, 220, 220));
                        mChart.highlightValue(1, 0, false);
                    }
                    if (wth >= 0.42f && wth <= 0.48f) {

                        l3.setBackgroundColor(Color.rgb(220, 220, 220));
                        mChart.highlightValue(2, 0, false);
                    }
                    if (wth >= 0.49f && wth <= 0.53f) {
                        l4.setBackgroundColor(Color.rgb(220, 220, 220));
                        mChart.highlightValue(3, 0, false);
                    }
                    if (wth >= 0.54f && wth <= 0.57f) {
                        l5.setBackgroundColor(Color.rgb(220, 220, 220));
                        mChart.highlightValue(4, 0, false);
                    }
                    if (wth >= 0.58f) {
                        l6.setBackgroundColor(Color.rgb(220, 220, 220));
                        mChart.highlightValue(5, 0, false);
                    }
                }
            }

        }
    }
    public void calculate()
    {

        if((selected_item1.equals("ft + in") && selected_item2.equals("cm")) || (selected_item1.equals("cm") && selected_item2.equals("in")) )
        {
            wth=0f;
            Toast.makeText(getActivity(), "Please Select All the Values with same unit", Toast.LENGTH_SHORT).show();
            mChart.setCenterText("Ratio:");
            SetBackColor(wth);

            return;
        }
        mChart.setDrawCenterText(true);
        mChart.setCenterTextTypeface(Typeface.DEFAULT_BOLD);
        mChart.setCenterTextColor(Color.BLACK);
        mChart.setCenterTextSize(18f);

        wth=0f;
        float getHeight=0f, getwaist;
        int getage;
        DecimalFormat df = new DecimalFormat("#0.00");

        if(height_ft.isShown() && height_in.isShown())
        {
            if(height_ft.getText().toString().equals("") && height_in.getText().toString().equals(""))
                getHeight=0f;
            if(height_ft.getText().toString().equals("") && !height_in.getText().toString().equals(""))
                getHeight = Float.parseFloat(height_in.getText().toString());
            if(height_in.getText().toString().equals("")&& !height_ft.getText().toString().equals(""))
                getHeight = Float.parseFloat(height_ft.getText().toString())*12f;
            if(!height_ft.getText().toString().equals("") && !height_in.getText().toString().equals(""))
                getHeight = Float.parseFloat(height_ft.getText().toString())*12f + Float.parseFloat(height_in.getText().toString());
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
        if (age.getText().toString().equals("")) {
            getage = 0;
        } else {
            getage = Integer.parseInt(age.getText().toString());
        }

        if(getwaist!=0f && getHeight!=0f) {
            wth = getwaist / getHeight;

        }
        else wth=0f;

        wth=Float.valueOf(df.format(wth));

        Log.i("wth value",wth+"");

        mChart.setCenterText("Ratio:"+df.format(wth));
        SetBackColor(wth);
//        scrollView.scrollTo(0,l6.getBottom());

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
