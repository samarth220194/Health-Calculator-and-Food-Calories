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

public class bmi extends android.support.v4.app.Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private RadioGroup radioSexGroup;
    private SwipeRefreshLayout swipe;

    private SharedPreferences sharedpreferences;
    private EditText age;
    private EditText height_ft,height_in,height_cm;
    private EditText weight;

    private TextView t1,t2,t3,t4,t5,t6,t7,t8;
    private Spinner height_spinner;
    private Spinner weight_spinner;
    private PieChart mChart;

    String spinner_height; //Height Spinner
    String spinner_weight; //Weight Spinner
    String selected_item1="ft + in";
    String selected_item2="in";
    String sex_selected="Male";

    private FirebaseAnalytics mFirebaseAnalytics;
    private AdView adView;

    private LinearLayout l1,l2,l3,l4,l5,l6,l7,l8;
    private ScrollView scrollView;
    float bmi=0f;

    private RadioButton r_male,r_female;

    protected String[] BMIcategory = new String[]
            {
                    "Very Severly Underweight", "Severly Underweight", "Underweight", "Normal",
                    "Overweight", "Obese Class I", "Obese Class II", "Obese Class III",
            };


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bmi, container, false);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());

        getActivity().setTitle("BMI Calculator");
        findId(view);

        return view;
    }

    @Override
    public void onStart(){
        super.onStart();


        Log.i("On start of BMI","+");

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "1");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "BMI Fragment Started");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Fragment Open");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, bundle);

    }
    public void findId(View view){

        sharedpreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        swipe=(SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        scrollView = ((ScrollView) view.findViewById(R.id.scrollview_main));
        radioSexGroup = (RadioGroup) view.findViewById(R.id.radioSex);
        r_male=(RadioButton) view.findViewById(R.id.radioMale);
        r_female=(RadioButton) view.findViewById(R.id.radioFemale);
        mChart=(PieChart)view.findViewById(R.id.chart);
        age=(EditText) view.findViewById(R.id.age);
        age.setText(sharedpreferences.getString(Age,""));


//        Log.i("boolean bmi_m",sharedpreferences.getBoolean(Gender_selected_male,false)+"");
//        Log.i("boolean bmi_f",sharedpreferences.getBoolean(Gender_selected_female,false)+"");


        if(sharedpreferences.getBoolean(Gender_selected_male,false))
        {
            r_male.setChecked(sharedpreferences.getBoolean(Gender_selected_male,false));
            sex_selected="Male";
        }
        if(sharedpreferences.getBoolean(Gender_selected_female,false))
        {
            r_female.setChecked(sharedpreferences.getBoolean(Gender_selected_female,false));
            sex_selected="Female";
        }



        age.addTextChangedListener(watch);

        l1=(LinearLayout) view.findViewById(R.id.linearlayout1);
        l2=(LinearLayout) view.findViewById(R.id.linearlayout2);
        l3=(LinearLayout) view.findViewById(R.id.linearlayout3);
        l4=(LinearLayout) view.findViewById(R.id.linearlayout4);
        l5=(LinearLayout) view.findViewById(R.id.linearlayout5);
        l6=(LinearLayout) view.findViewById(R.id.linearlayout6);
        l7=(LinearLayout) view.findViewById(R.id.linearlayout7);
        l8=(LinearLayout) view.findViewById(R.id.linearlayout8);

        height_ft=(EditText) view.findViewById(R.id.height_ft);
        height_in=(EditText) view.findViewById(R.id.height_in);
        height_cm=(EditText) view.findViewById(R.id.height_cm);

        weight=(EditText) view.findViewById(R.id.weight);
        height_ft.addTextChangedListener(watch1);
        height_in.addTextChangedListener(watch1);
        height_cm.addTextChangedListener(watch1);

        weight.addTextChangedListener(watch1);
//        result=(TextView) findViewById(result);
        t1=(TextView) view.findViewById(R.id.text1);
        t2=(TextView) view.findViewById(R.id.text2);
        t3=(TextView) view.findViewById(R.id.text3);
        t4=(TextView) view.findViewById(R.id.text4);
        t5=(TextView) view.findViewById(R.id.text5);
        t6=(TextView) view.findViewById(R.id.text6);
        t7=(TextView) view.findViewById(R.id.text7);
        t8=(TextView) view.findViewById(R.id.text8);

        t1.setText("<16.0");

        adView=(AdView) view.findViewById(R.id.adView);

        swipe.setOnRefreshListener(this);

        LoadAd();


        height_spinner=(Spinner) view.findViewById(R.id.height_spinner);
        weight_spinner=(Spinner) view.findViewById(R.id.weight_spinner);
//        calculate=(Button) view.findViewById(R.id.calculate_button);

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
                        ChangeText(sex_selected);

                        calculate();

                        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "1.1");
                        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "BMI Radio button female ");
                        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Button");
                        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                        break;
                    case (R.id.radioMale):
                        Log.i("gender_selected","male");

                        sex_selected="Male";
                        ChangeText(sex_selected);
                        calculate();
                        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "1.2");
                        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "BMI Radio button male");
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


        Log.i("network",NetowrkConnection.CheckConnection(getActivity())+"");
        if(NetowrkConnection.CheckConnection(getActivity())) {
            adView.setVisibility(View.VISIBLE);
            AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);
        }
        else {
            adView.setVisibility(View.GONE);
        }
    }




    TextWatcher watch = new TextWatcher() {

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
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "1.4");
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "BMI age edittext");
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "TextBox");
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
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "1.4");
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "BMI height/weight edittext");
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "TextBox");
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

            setchart();
            calculate();
        }

    };


    public void ChangeText(String selectedsex)
    {
        t1.setText("-");
        t2.setText("-");
        t3.setText("-");
        t4.setText("-");
        t5.setText("-");
        t6.setText("-");
        t7.setText("-");
        t8.setText("-");

        if(selectedsex.equals("Male"))
        {
            if(age.getText().toString().equals(""))
            {
                t1.setText("<16.0");
                t2.setText("16.0-16.9");
                t3.setText("17.0-18.4");
                t4.setText("18.5-24.9");
                t5.setText("25.0-29.9");
                t6.setText("30.0-34.9");
                t7.setText("35.0-39.9");
                t8.setText(">=40.0");
            }
            else if (Integer.parseInt(age.getText().toString()) == 7)
            {

                    t3.setText("<=13.6");
                    t4.setText("13.7-19.1");
                    t5.setText("19.2-21.0");
                    t6.setText(">=21.1");


                }
            else if (Integer.parseInt(age.getText().toString()) == 8)
            {
                    t3.setText("<=14.2");
                    t4.setText("14.3-19.2");
                    t5.setText("19.3-22.5");
                    t6.setText(">=22.6");

            }
            else if (Integer.parseInt(age.getText().toString()) == 9)
            {

                    t3.setText("<=13.7");
                    t4.setText("13.8-19.3");
                    t5.setText("19.4-21.5");
                    t6.setText(">=21.6");

            }
            else if (Integer.parseInt(age.getText().toString()) == 10)
            {

                    t3.setText("<=14.6");
                    t4.setText("14.7-21.3");
                    t5.setText("21.4-24.9");
                    t6.setText(">=25.0");

                }
            else if (Integer.parseInt(age.getText().toString()) == 11)
            {

                    t3.setText("<=14.3");
                    t4.setText("14.4-21.1");
                    t5.setText("21.2-22.9");
                    t6.setText(">=23.0");


            }
            else if (Integer.parseInt(age.getText().toString()) == 12)
            {

                    t3.setText("<=14.8");
                    t4.setText("14.9-21.9");
                    t5.setText("22.0-24.7");
                    t6.setText(">=24.8");

                }
            else if (Integer.parseInt(age.getText().toString()) == 13)
            {

                    t3.setText("<=16.2");
                    t4.setText("16.3-21.6");
                    t5.setText("21.7-24.4");
                    t6.setText(">=24.5");

            }
            else if (Integer.parseInt(age.getText().toString()) == 14)
            {

                    t3.setText("<=16.7");
                    t4.setText("16.8-22.5");
                    t5.setText("22.6-25.6");
                    t6.setText(">=25.7");


                }
            else if (Integer.parseInt(age.getText().toString()) == 15)
            {

                    t3.setText("<=17.8");
                    t4.setText("17.9-23.0");
                    t5.setText("23.1-25.8");
                    t6.setText(">=25.9");


                }
            else if (Integer.parseInt(age.getText().toString()) == 16)
            {

                    t3.setText("<=18.5");
                    t4.setText("18.6-23.6");
                    t5.setText("23.7-25.9");
                    t6.setText(">=26.0");


                }
            else if (Integer.parseInt(age.getText().toString()) == 17)
            {
                    t3.setText("<=18.6");
                    t4.setText("18.7-23.6");
                    t5.setText("23.7-25.7");
                    t6.setText(">=25.8");


                }
            else if (Integer.parseInt(age.getText().toString()) == 18)
            {

                    t3.setText("<=18.6");
                    t4.setText("18.7-23.9");
                    t5.setText("24.0-26.7");
                    t6.setText(">=26.8");

                }
            else if (Integer.parseInt(age.getText().toString()) >= 19)
            {
                    t1.setText("<16.0");
                    t2.setText("16.0-16.9");
                    t3.setText("17.0-18.4");
                    t4.setText("18.5-24.9");
                    t5.setText("25.0-29.9");
                    t6.setText("30.0-34.9");
                    t7.setText("35.0-39.9");
                    t8.setText(">=40.0");

            }
        }

        if(selectedsex.equals("Female"))
        {
            if(age.getText().toString().equals(""))
            {
                t1.setText("<16.0");
                t2.setText("16.0-16.9");
                t3.setText("17.0-18.4");
                t4.setText("18.5-24.9");
                t5.setText("25.0-29.9");
                t6.setText("30.0-34.9");
                t7.setText("35.0-39.9");
                t8.setText(">=40.0");
            }
           else if (Integer.parseInt(age.getText().toString()) == 7) {

                    t3.setText("<=13.2");
                    t4.setText("13.3-18.1");
                    t5.setText("18.2-23.0");
                    t6.setText(">=23.1");


                } else if (Integer.parseInt(age.getText().toString()) == 8) {

                    t3.setText("<=13.2");
                    t4.setText("13.3-18.7");
                    t5.setText("18.8-22.2");
                    t6.setText(">=22.3");


                } else if (Integer.parseInt(age.getText().toString()) == 9) {

                    t3.setText("<=13.7");
                    t4.setText("13.8-19.7");
                    t5.setText("19.8-23.3");
                    t6.setText(">=23.4");


                } else if (Integer.parseInt(age.getText().toString()) == 10) {

                    t3.setText("<=14.2");
                    t4.setText("14.3-20.6");
                    t5.setText("20.7-23.3");
                    t6.setText(">=23.4");


                } else if (Integer.parseInt(age.getText().toString()) == 11) {

                    t3.setText("<=14.7");
                    t4.setText("14.8-20.7");
                    t5.setText("20.8-22.8");
                    t6.setText(">=22.9");


                } else if (Integer.parseInt(age.getText().toString()) == 12) {

                    t3.setText("<=15.0");
                    t4.setText("15.1-21.4");
                    t5.setText("21.5-23.3");
                    t6.setText(">=23.4");


                } else if (Integer.parseInt(age.getText().toString()) == 13) {

                    t3.setText("<=15.6");
                    t4.setText("15.7-21.9");
                    t5.setText("22.0-24.3");
                    t6.setText(">=24.4");


                } else if (Integer.parseInt(age.getText().toString()) == 14) {

                    t3.setText("<=17.0");
                    t4.setText("17.1-23.1");
                    t5.setText("23.2-25.9");
                    t6.setText(">=26.0");

                } else if (Integer.parseInt(age.getText().toString()) == 15) {

                    t3.setText("<=17.6");
                    t4.setText("17.7-23.1");
                    t5.setText("23.2-27.5");
                    t6.setText(">=27.6");


                } else if (Integer.parseInt(age.getText().toString()) == 16) {

                    t3.setText("<=17.8");
                    t4.setText("17.9-22.7");
                    t5.setText("22.8-24.1");
                    t6.setText(">=24.2");


                } else if (Integer.parseInt(age.getText().toString()) == 17) {

                    t3.setText("<=17.8");
                    t4.setText("17.9-23.3");
                    t5.setText("23.4-25.6");
                    t6.setText(">=25.7");


                } else if (Integer.parseInt(age.getText().toString()) == 18) {

                    t3.setText("<=18.3");
                    t4.setText("18.4-23.4");
                    t5.setText("23.5-24.9");
                    t6.setText(">=25.0");

                } else if (Integer.parseInt(age.getText().toString()) >= 19) {
                    t1.setText("<16.0");
                    t2.setText("16.0-16.9");
                    t3.setText("17.0-18.4");
                    t4.setText("18.5-24.9");
                    t5.setText("25.0-29.9");
                    t6.setText("30.0-34.9");
                    t7.setText("35.0-39.9");
                    t8.setText(">=40.0");

                }
            }

    }

    public void  setchart()
    {
        mChart.setBackgroundColor(Color.rgb(255,255,255));
//        moveOffScreen();
        mChart.setUsePercentValues(false);
        mChart.getDescription().setEnabled(false);

        mChart.setDrawCenterText(true);
        mChart.setCenterTextTypeface(Typeface.DEFAULT_BOLD);

        mChart.setCenterTextColor(Color.BLACK);
        mChart.setCenterTextSize(18f);
        mChart.setCenterText("BMI:0.0");

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

        mChart.setMaxAngle(270f); // HALF CHART
        mChart.setRotationAngle(135f);

        setData(SetDatasetEntries(), 80);

        mChart.getLegend().setEnabled(false);
        mChart.invalidate();


    }
    public int SetDatasetEntries() {

        if(age.getText().toString().length()!=0) {
            if (Integer.parseInt(age.getText().toString()) >= 1 && Integer.parseInt(age.getText().toString()) <= 6)
                return 8;
            else if (Integer.parseInt(age.getText().toString()) >= 7 && Integer.parseInt(age.getText().toString()) <= 18)
                return 4;
            else return 8;
        }
        return 8;
    }

    private void setData(int count, float range) {

        ArrayList<PieEntry> values = new ArrayList<>();
        PieDataSet dataSet = new PieDataSet(values, "BMI Category");
        ArrayList<Integer> colors = new ArrayList<Integer>();

        dataSet.setSliceSpace(2f);
        dataSet.setSelectionShift(5f);

        if(count==8)
        {
            values.add(new PieEntry(10f,BMIcategory[0]));
            values.add(new PieEntry(15f,BMIcategory[1]));
            values.add(new PieEntry(8f,BMIcategory[2]));
            values.add(new PieEntry(12f,BMIcategory[3]));
            values.add(new PieEntry(5f,BMIcategory[4]));
            values.add(new PieEntry(5f,BMIcategory[5]));
            values.add(new PieEntry(15f,BMIcategory[6]));
            values.add(new PieEntry(10f,BMIcategory[7]));

            colors.add(Color.rgb(31,127,218));
            colors.add(Color.rgb(37,169,254));
            colors.add(Color.rgb(63,200,247));
            colors.add(Color.rgb(65,189,103));
            colors.add(Color.rgb(243,207,0));
            colors.add(Color.rgb(248,154,20));
            colors.add(Color.rgb(238,87,59));
            colors.add(Color.rgb(171,18,14));

        }
        else{
            values.add(new PieEntry(8f,BMIcategory[2]));
            values.add(new PieEntry(12f,BMIcategory[3]));
            values.add(new PieEntry(5f,BMIcategory[4]));
            values.add(new PieEntry(5f,BMIcategory[5]));

            colors.add(Color.rgb(63,200,247));
            colors.add(Color.rgb(65,189,103));
            colors.add(Color.rgb(243,207,0));
            colors.add(Color.rgb(248,154,20));
        }

        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setDrawValues(false);

        mChart.setData(data);
        mChart.invalidate();
    }

    public void set_spinner()
    {

        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(getActivity(), R.array.height_spinner_array, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        height_spinner.setAdapter(adapter1);

        height_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "1.5");
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "BMI Height Spinner");
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


        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(getActivity(), R.array.weight_spinner_array, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        weight_spinner.setAdapter(adapter2);
        weight_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {


                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "1.6");
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "BMI Weight Spinner");
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Button");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                int b = parent.getSelectedItemPosition();
                spinner_weight = weight_spinner.getSelectedItem().toString();
                if(b==0)
                {
                    selected_item2="kg";
                }
                if(b==1)
                {
                    selected_item2="lb";
                }
                calculate();

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {} } );


    }

    public void calculate() {

//        mChart=(PieChart)findViewById(R.id.chart);
        mChart.setDrawCenterText(true);
        mChart.setCenterTextTypeface(Typeface.DEFAULT_BOLD);
        mChart.setCenterTextColor(Color.BLACK);
        mChart.setCenterTextSize(18f);


        float getHeight=0f, getWeight;

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

        if (weight.getText().toString().equals("")) {
            getWeight = 0f;
        } else {
            getWeight = Float.parseFloat(weight.getText().toString());
        }
        if(getHeight!=0f && getWeight!=0f ) {

            if (selected_item1.equals("ft + in")) {
                if (selected_item2.equals("kg")) {

                    bmi = (getWeight * 10000f) / (2.54f * 2.54f * getHeight * getHeight);

                }
                if (selected_item2.equals("lb")) {
                    bmi = (getWeight * 0.453592f * 10000f) / (getHeight * getHeight * 2.54f * 2.54f);
                }

            }
            if(selected_item1.equals("cm"))
            {
                if(selected_item2.equals("kg"))
                {
                    bmi = (getWeight*10000f) / (getHeight * getHeight);
                }
                if(selected_item2.equals("lb"))
                {
                    bmi=(getWeight*0.453592f*10000f) / (getHeight * getHeight);
                }
            }

        }
        else {
            bmi=0f;
        }

        bmi=Float.valueOf(df.format(bmi));
        Log.i("BMI RESULT:",bmi+"");

        mChart.setCenterText("BMI:"+df.format(bmi));

        SetBackColor(bmi);

    }


    public void SetBackColor(float bmi)
    {
        mChart.invalidate();
        l1.setBackgroundColor(Color.rgb(255,255,255));
        l2.setBackgroundColor(Color.rgb(255,255,255));
        l3.setBackgroundColor(Color.rgb(255,255,255));
        l4.setBackgroundColor(Color.rgb(255,255,255));
        l5.setBackgroundColor(Color.rgb(255,255,255));
        l6.setBackgroundColor(Color.rgb(255,255,255));
        l7.setBackgroundColor(Color.rgb(255,255,255));
        l8.setBackgroundColor(Color.rgb(255,255,255));

        if(age.getText().toString().equals("") ||  Integer.parseInt(age.getText().toString())==0 ||
                (Integer.parseInt(age.getText().toString())>=1 && Integer.parseInt(age.getText().toString())<=6)  ||
                bmi==-1f || bmi==0f)
        {
            mChart.highlightValue(0,-1,false);

        }
        if(age.getText().toString().length()!=0)
        {

        if(Integer.parseInt(age.getText().toString())==7)
        {
            if(sex_selected.equals("Male"))
            {
                if(bmi<=13.6f && bmi>0f)
                {
                    mChart.highlightValue(0,0,false);

                    l3.setBackgroundColor(Color.rgb(220,220,220));

                }
                if(bmi>=13.7f && bmi<=19.1f)
                {
                    mChart.highlightValue(1,0,false);
                    l4.setBackgroundColor(Color.rgb(220,220,220));
                }
                if(bmi>=19.2f && bmi<=21.0f)
                {
                    mChart.highlightValue(2,0,false);
                    l5.setBackgroundColor(Color.rgb(220,220,220));
                }
                if(bmi>=21.1f)
                {
                    mChart.highlightValue(3,0,false);
                    l6.setBackgroundColor(Color.rgb(220,220,220));
                }
            }
            if(sex_selected.equals("Female"))
            {
                if(bmi<=13.2f && bmi>0f)
                {
                    mChart.highlightValue(0,0,false);
                    l3.setBackgroundColor(Color.rgb(220,220,220));
                }
                if(bmi>=13.3f && bmi<=18.1f)
                {
                    mChart.highlightValue(1,0,false);
                    l4.setBackgroundColor(Color.rgb(220,220,220));
                }
                if(bmi>=18.2f && bmi<=23.0f)
                {
                    mChart.highlightValue(2,0,false);
                    l5.setBackgroundColor(Color.rgb(220,220,220));
                }
                if(bmi>=23.1f)
                {
                    mChart.highlightValue(3,0,false);
                    l6.setBackgroundColor(Color.rgb(220,220,220));
                }
            }
        }
        if(Integer.parseInt(age.getText().toString())==8)
        {
            if(sex_selected.equals("Male"))
            {
                if(bmi<=14.2f && bmi>0f)
                {
                    mChart.highlightValue(0,0,false);
                    l3.setBackgroundColor(Color.rgb(220,220,220));
                }
                if(bmi>=14.3f && bmi<=19.2f)
                {
                    mChart.highlightValue(1,0,false);
                    l4.setBackgroundColor(Color.rgb(220,220,220));
                }
                if(bmi>=19.3f && bmi<=22.5f)
                {
                    mChart.highlightValue(2,0,false);
                    l5.setBackgroundColor(Color.rgb(220,220,220));
                }
                if(bmi>=22.6f)
                {
                    mChart.highlightValue(3,0,false);
                    l6.setBackgroundColor(Color.rgb(220,220,220));
                }
            }
            if(sex_selected.equals("Female"))
            {
                if(bmi<=13.2f && bmi>0f)
                {
                    mChart.highlightValue(0,0,false);
                    l3.setBackgroundColor(Color.rgb(220,220,220));
                }
                if(bmi>=13.3f && bmi<=18.7f)
                {
                    mChart.highlightValue(1,0,false);
                    l4.setBackgroundColor(Color.rgb(220,220,220));
                }
                if(bmi>=18.8f && bmi<=22.2f)
                {
                    mChart.highlightValue(2,0,false);
                    l5.setBackgroundColor(Color.rgb(220,220,220));
                }
                if(bmi>=22.3f)
                {
                    mChart.highlightValue(3,0,false);
                    l6.setBackgroundColor(Color.rgb(220,220,220));
                }
            }
        }
        if(Integer.parseInt(age.getText().toString())==9)
        {
            if(sex_selected.equals("Male"))
            {
                if(bmi<=13.7f && bmi>0f)
                {
                    mChart.highlightValue(0,0,false);
                    l3.setBackgroundColor(Color.rgb(220,220,220));
                 }
                if(bmi>=13.8f && bmi<=19.3f)
                {
                    mChart.highlightValue(1,0,false);
                    l4.setBackgroundColor(Color.rgb(220,220,220));
                }
                if(bmi>=19.4f && bmi<=21.5f)
                {
                    mChart.highlightValue(2,0,false);
                    l5.setBackgroundColor(Color.rgb(220,220,220));
                }
                if(bmi>=21.6f)
                {
                    mChart.highlightValue(3,0,false);
                    l6.setBackgroundColor(Color.rgb(220,220,220));
                }
            }
            if(sex_selected.equals("Female"))
            {
                if(bmi<=13.7f && bmi>0f)
                {
                    mChart.highlightValue(0,0,false);
                    l3.setBackgroundColor(Color.rgb(220,220,220));
                }
                if(bmi>=13.8f && bmi<=19.7f)
                {
                    mChart.highlightValue(1,0,false);
                    l4.setBackgroundColor(Color.rgb(220,220,220));
                }
                if(bmi>=19.8f && bmi<= 23.3f)
                {
                    mChart.highlightValue(2,0,false);
                    l5.setBackgroundColor(Color.rgb(220,220,220));
                }
                if(bmi>=23.4f)
                {
                    mChart.highlightValue(3,0,false);
                    l6.setBackgroundColor(Color.rgb(220,220,220));
                }
            }
        }
        if(Integer.parseInt(age.getText().toString())==10)
        {
            if(sex_selected.equals("Male"))
            {
                if(bmi<=14.6f && bmi>0f)
                {
                    mChart.highlightValue(0,0,false);
                    l3.setBackgroundColor(Color.rgb(220,220,220));
                }
                if(bmi>=14.7f && bmi<=21.3f)
                {
                    mChart.highlightValue(1,0,false);
                    l4.setBackgroundColor(Color.rgb(220,220,220));
                }
                if(bmi>=21.4f && bmi<=24.9f)
                {
                    mChart.highlightValue(2,0,false);
                    l5.setBackgroundColor(Color.rgb(220,220,220));
                }
                if(bmi>=25.0f)
                {
                    mChart.highlightValue(3,0,false);
                    l6.setBackgroundColor(Color.rgb(220,220,220));
                }
            }
            if(sex_selected.equals("Female"))
            {
                if(bmi<=14.2f && bmi>0f)
                {
                    mChart.highlightValue(0,0,false);
                    l3.setBackgroundColor(Color.rgb(220,220,220));
                }
                if(bmi>=14.3f && bmi<=20.6f)
                {
                    mChart.highlightValue(1,0,false);
                    l4.setBackgroundColor(Color.rgb(220,220,220));
                }
                if(bmi>=20.7f && bmi<=23.3f)
                {
                    mChart.highlightValue(2,0,false);
                    l5.setBackgroundColor(Color.rgb(220,220,220));
                }
                if(bmi>=23.4f)
                {
                    mChart.highlightValue(3,0,false);
                    l6.setBackgroundColor(Color.rgb(220,220,220));
                }
            }
        }
        if(Integer.parseInt(age.getText().toString())==11)
        {
            if(sex_selected.equals("Male"))
            {
                if(bmi<=14.3f && bmi>0f)
                {
                    mChart.highlightValue(0,0,false);
                    l3.setBackgroundColor(Color.rgb(220,220,220));
                }
                if(bmi>=14.4f && bmi<=21.1f)
                {
                    mChart.highlightValue(1,0,false);
                    l4.setBackgroundColor(Color.rgb(220,220,220));
                }
                if(bmi>=21.2f && bmi<=22.9f)
                {
                    mChart.highlightValue(2,0,false);
                    l5.setBackgroundColor(Color.rgb(220,220,220));
                }
                if(bmi>=23.0f)
                {
                    mChart.highlightValue(3,0,false);
                    l6.setBackgroundColor(Color.rgb(220,220,220));
                }
            }
            if(sex_selected.equals("Female"))
            {
                if(bmi<=14.7f && bmi>0f)
                {
                    mChart.highlightValue(0,0,false);
                    l3.setBackgroundColor(Color.rgb(220,220,220));
                }
                if(bmi>=14.8f && bmi<=20.7f)
                {
                    mChart.highlightValue(1,0,false);
                    l4.setBackgroundColor(Color.rgb(220,220,220));
                }
                if(bmi>=20.8f && bmi<=22.8f)
                {
                    mChart.highlightValue(2,0,false);
                    l5.setBackgroundColor(Color.rgb(220,220,220));
                }
                if(bmi>=22.9f)
                {
                    mChart.highlightValue(3,0,false);
                    l6.setBackgroundColor(Color.rgb(220,220,220));
                }
            }
        }
        if(Integer.parseInt(age.getText().toString())==12)
        {
            if(sex_selected.equals("Male"))
            {
                if(bmi<=14.8f && bmi>0f)
                {
                    mChart.highlightValue(0,0,false);
                    l3.setBackgroundColor(Color.rgb(220,220,220));
                }
                if(bmi>=14.9f && bmi<=21.9f)
                {
                    mChart.highlightValue(1,0,false);
                    l4.setBackgroundColor(Color.rgb(220,220,220));
                }
                if(bmi>=22.0f && bmi<=24.7f)
                {
                    mChart.highlightValue(2,0,false);
                    l5.setBackgroundColor(Color.rgb(220,220,220));
                }
                if(bmi>=24.8f)
                {
                    mChart.highlightValue(3,0,false);
                    l6.setBackgroundColor(Color.rgb(220,220,220));
                }
            }
            if(sex_selected.equals("Female"))
            {
                if(bmi<=15.0f && bmi>0f)
                {
                    mChart.highlightValue(0,0,false);
                    l3.setBackgroundColor(Color.rgb(220,220,220));
                }
                if(bmi>=15.1f && bmi<=21.4f)
                {
                    mChart.highlightValue(1,0,false);
                    l4.setBackgroundColor(Color.rgb(220,220,220));
                }
                if(bmi>=21.5f && bmi<=23.3f)
                {
                    mChart.highlightValue(2,0,false);
                    l5.setBackgroundColor(Color.rgb(220,220,220));
                }
                if(bmi>=23.4f)
                {
                    mChart.highlightValue(3,0,false);
                    l6.setBackgroundColor(Color.rgb(220,220,220));
                }
            }
        }
        if(Integer.parseInt(age.getText().toString())==13)
        {
            if(sex_selected.equals("Male"))
            {
                if(bmi<=16.2f && bmi>0f)
                {
                    mChart.highlightValue(0,0,false);
                    l3.setBackgroundColor(Color.rgb(220,220,220));
              }
                if(bmi>=16.3f && bmi<=21.6f)
                {
                    mChart.highlightValue(1,0,false);
                    l4.setBackgroundColor(Color.rgb(220,220,220));
                }
                if(bmi>=21.7f && bmi<=24.4f)
                {
                    mChart.highlightValue(2,0,false);
                    l5.setBackgroundColor(Color.rgb(220,220,220));
                }
                if(bmi>=24.5f)
                {
                    mChart.highlightValue(3,0,false);
                    l6.setBackgroundColor(Color.rgb(220,220,220));
                }
            }
            if(sex_selected.equals("Female"))
            {
                if(bmi<=15.6f && bmi>0f)
                {
                    mChart.highlightValue(0,0,false);
                    l3.setBackgroundColor(Color.rgb(220,220,220));
                }
                if(bmi>=15.7f && bmi<=21.9f)
                {
                    mChart.highlightValue(1,0,false);
                    l4.setBackgroundColor(Color.rgb(220,220,220));
                }
                if(bmi>=22.0f && bmi<=24.3f)
                {
                    mChart.highlightValue(2,0,false);
                    l5.setBackgroundColor(Color.rgb(220,220,220));
                }
                if(bmi>=24.4f)
                {
                    mChart.highlightValue(3,0,false);
                    l6.setBackgroundColor(Color.rgb(220,220,220));
                }
            }
        }
        if(Integer.parseInt(age.getText().toString())==14)
        {
            if(sex_selected.equals("Male"))
            {
                if(bmi<=16.7f && bmi>0f)
                {
                    mChart.highlightValue(0,0,false);
                    l3.setBackgroundColor(Color.rgb(220,220,220));
                }
                if(bmi>=16.8f && bmi<=22.5f)
                {
                    mChart.highlightValue(1,0,false);
                    l4.setBackgroundColor(Color.rgb(220,220,220));
                }
                if(bmi>=22.6f && bmi<=25.6f)
                {
                    mChart.highlightValue(2,0,false);
                    l5.setBackgroundColor(Color.rgb(220,220,220));
                }
                if(bmi>=25.7f)
                {
                    mChart.highlightValue(3,0,false);
                    l6.setBackgroundColor(Color.rgb(220,220,220));
                }
            }
            if(sex_selected.equals("Female"))
            {
                if(bmi<=17.0f && bmi>0f)
                {
                    mChart.highlightValue(0,0,false);
                    l3.setBackgroundColor(Color.rgb(220,220,220));
                }
                if(bmi>=17.1f && bmi<=23.1f)
                {
                    mChart.highlightValue(1,0,false);
                    l4.setBackgroundColor(Color.rgb(220,220,220));
                }
                if(bmi>=23.2f && bmi<=25.9f)
                {
                    mChart.highlightValue(2,0,false);
                    l5.setBackgroundColor(Color.rgb(220,220,220));
                }
                if(bmi>=26.0f)
                {
                    mChart.highlightValue(3,0,false);
                    l6.setBackgroundColor(Color.rgb(220,220,220));
                }
            }
        }
        if(Integer.parseInt(age.getText().toString())==15)
        {
            if(sex_selected.equals("Male"))
            {
                if(bmi<=17.8f && bmi>0f)
                {
                    mChart.highlightValue(0,0,false);
                    l3.setBackgroundColor(Color.rgb(220,220,220));
                }
                if(bmi>=17.9f && bmi<=23.0f)
                {
                    mChart.highlightValue(1,0,false);
                    l4.setBackgroundColor(Color.rgb(220,220,220));
                }
                if(bmi>=23.1f && bmi<=25.8f)
                {
                    mChart.highlightValue(2,0,false);
                    l5.setBackgroundColor(Color.rgb(220,220,220));
                }
                if(bmi>=25.9f)
                {
                    mChart.highlightValue(3,0,false);
                    l6.setBackgroundColor(Color.rgb(220,220,220));
                }
            }
            if(sex_selected.equals("Female"))
            {
                if(bmi<=17.6f && bmi>0f)
                {
                    mChart.highlightValue(0,0,false);
                    l3.setBackgroundColor(Color.rgb(220,220,220));
                }
                if(bmi>=17.7f && bmi<=23.1f)
                {
                    mChart.highlightValue(1,0,false);
                    l4.setBackgroundColor(Color.rgb(220,220,220));
                }
                if(bmi>=23.2f && bmi<=27.5f)
                {
                    mChart.highlightValue(2,0,false);
                    l5.setBackgroundColor(Color.rgb(220,220,220));
                }
                if(bmi>=27.6f)
                {
                    mChart.highlightValue(3,0,false);
                    l6.setBackgroundColor(Color.rgb(220,220,220));
                }
            }
        }

        if(Integer.parseInt(age.getText().toString())==16)
        {
            if(sex_selected.equals("Male"))
            {
                if(bmi<=18.5f && bmi>0f)
                {
                    mChart.highlightValue(0,0,false);
                    l3.setBackgroundColor(Color.rgb(220,220,220));
                }
                if(bmi>=18.6f && bmi<=23.6f)
                {
                    mChart.highlightValue(1,0,false);
                    l4.setBackgroundColor(Color.rgb(220,220,220));
                }
                if(bmi>=23.7f && bmi<=25.9f)
                {
                    mChart.highlightValue(2,0,false);
                    l5.setBackgroundColor(Color.rgb(220,220,220));
                }
                if(bmi>=26.0f)
                {
                    mChart.highlightValue(3,0,false);
                    l6.setBackgroundColor(Color.rgb(220,220,220));
                }
            }
            if(sex_selected.equals("Female"))
            {
                if(bmi<=17.8f && bmi>0f)
                {
                    mChart.highlightValue(0,0,false);
                    l3.setBackgroundColor(Color.rgb(220,220,220));
                }
                if(bmi>=17.9f && bmi<=22.7f)
                {
                    mChart.highlightValue(1,0,false);
                    l4.setBackgroundColor(Color.rgb(220,220,220));
                }
                if(bmi>=22.8f && bmi<=24.1f)
                {
                    mChart.highlightValue(2,0,false);
                    l5.setBackgroundColor(Color.rgb(220,220,220));
                }
                if(bmi>=24.2f)
                {
                    mChart.highlightValue(3,0,false);
                    l6.setBackgroundColor(Color.rgb(220,220,220));
                }
            }
        }
        if(Integer.parseInt(age.getText().toString())==17)
        {
            if(sex_selected.equals("Male"))
            {
                if(bmi<=18.6f && bmi>0f)
                {
                    mChart.highlightValue(0,0,false);
                    l3.setBackgroundColor(Color.rgb(220,220,220));
                }
                if(bmi>=18.7f && bmi<=23.6f)
                {
                    mChart.highlightValue(1,0,false);
                    l4.setBackgroundColor(Color.rgb(220,220,220));
                }
                if(bmi>=23.7f && bmi<=25.7f)
                {
                    mChart.highlightValue(2,0,false);
                    l5.setBackgroundColor(Color.rgb(220,220,220));
                }
                if(bmi>=25.8f)
                {
                    mChart.highlightValue(3,0,false);
                    l6.setBackgroundColor(Color.rgb(220,220,220));
                }
            }
            if(sex_selected.equals("Female"))
            {
                if(bmi<=17.8f && bmi>0f)
                {
                    mChart.highlightValue(0,0,false);
                    l3.setBackgroundColor(Color.rgb(220,220,220));
                }
                if(bmi>=17.9f && bmi<=23.3f)
                {
                    mChart.highlightValue(1,0,false);
                    l4.setBackgroundColor(Color.rgb(220,220,220));
                }
                if(bmi>=23.4f && bmi<=25.6f)
                {
                    mChart.highlightValue(2,0,false);
                    l5.setBackgroundColor(Color.rgb(220,220,220));
                }
                if(bmi>=25.7f)
                {
                    mChart.highlightValue(3,0,false);
                    l6.setBackgroundColor(Color.rgb(220,220,220));
                }
            }
        }
        if(Integer.parseInt(age.getText().toString())==18)
        {
            if(sex_selected.equals("Male"))
            {
                if(bmi<=18.6f && bmi>0f)
                {
                    mChart.highlightValue(0,0,false);
                    l3.setBackgroundColor(Color.rgb(220,220,220));
                }
                if(bmi>=18.7f && bmi<=23.9f)
                {
                    mChart.highlightValue(1,0,false);
                    l4.setBackgroundColor(Color.rgb(220,220,220));
                }
                if(bmi>=24.0f && bmi<=26.7f)
                {
                    mChart.highlightValue(2,0,false);
                    l5.setBackgroundColor(Color.rgb(220,220,220));
                }
                if(bmi>=26.8f)
                {
                    mChart.highlightValue(3,0,false);
                    l6.setBackgroundColor(Color.rgb(220,220,220));
                }
            }
            if(sex_selected.equals("Female"))
            {
                if(bmi<=18.3f && bmi>0f)
                {
                    mChart.highlightValue(0,0,false);
                    l3.setBackgroundColor(Color.rgb(220,220,220));
                }
                if(bmi>=18.4f && bmi<=23.4f)
                {
                    mChart.highlightValue(1,0,false);
                    l4.setBackgroundColor(Color.rgb(220,220,220));
                }
                if(bmi>=23.5f && bmi<=24.9f)
                {
                    mChart.highlightValue(2,0,false);
                    l5.setBackgroundColor(Color.rgb(220,220,220));
                }
                if(bmi>=25.0f)
                {
                    mChart.highlightValue(3,0,false);
                    l6.setBackgroundColor(Color.rgb(220,220,220));
                }
            }
        }
        if(Integer.parseInt(age.getText().toString())>=19)
        {
            if (bmi <= 16.0f && bmi > 0f) {
                mChart.highlightValue(0, 0, false);
                l1.setBackgroundColor(Color.rgb(220, 220, 220));
            }
            if (bmi >= 16.0f && bmi <= 16.9f) {
                mChart.highlightValue(1, 0, false);
                l2.setBackgroundColor(Color.rgb(220, 220, 220));
            }
            if (bmi >= 17.0f && bmi <= 18.4f) {
                mChart.highlightValue(2, 0, false);
                l3.setBackgroundColor(Color.rgb(220, 220, 220));
            }
            if (bmi >= 18.5f && bmi <= 24.9f) {
                mChart.highlightValue(3, 0, false);
                l4.setBackgroundColor(Color.rgb(220, 220, 220));
            }
            if (bmi >= 25.0f && bmi <= 29.9f) {
                mChart.highlightValue(4, 0, false);
                l5.setBackgroundColor(Color.rgb(220, 220, 220));
            }
            if (bmi >= 30.0f && bmi <= 34.9f) {
                mChart.highlightValue(5, 0, false);
                l6.setBackgroundColor(Color.rgb(220, 220, 220));
            }
            if (bmi >= 35.0f && bmi <= 39.9f) {
                mChart.highlightValue(6, 0, false);
                l7.setBackgroundColor(Color.rgb(220, 220, 220));
            }
            if (bmi >= 40.0f) {
                mChart.highlightValue(7, 0, false);
                l8.setBackgroundColor(Color.rgb(220, 220, 220));
            }
        }

        }
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
