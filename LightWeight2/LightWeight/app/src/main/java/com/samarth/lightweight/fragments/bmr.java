package com.samarth.lightweight.fragments;

import android.content.Context;
import android.content.SharedPreferences;
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

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.samarth.lightweight.NetowrkConnection;
import com.samarth.lightweight.R;

import java.text.DecimalFormat;

import static com.samarth.lightweight.intoduction_activity.Age;
import static com.samarth.lightweight.intoduction_activity.Gender_selected_female;
import static com.samarth.lightweight.intoduction_activity.Gender_selected_male;
import static com.samarth.lightweight.intoduction_activity.MyPREFERENCES;

/**
 * Created by Sam on 21-Feb-17.
 */

public class bmr extends android.support.v4.app.Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private RadioGroup radioSexGroup;
    private SwipeRefreshLayout swipe;

    private SharedPreferences sharedpreferences;

    private EditText age;
    private EditText height_ft,height_in,height_cm;
    private EditText weight;
    private TextView result;
    private TextView result1;
    private TextView result2;
    private TextView goal;
    private LinearLayout main_layout;

    float bmr=0f;
    float calories_activity=0f;
    float calories_goal=0f;

    private AdView adView;

    private Spinner height_spinner;
    private Spinner weight_spinner;
    private Spinner activity_spinner;
    private Spinner goal_spinner;

    private FirebaseAnalytics mFirebaseAnalytics;

    String spinner_weight;
    String spinner_height;
    String spinner_activity;
    String spinner_goal;
    String selected_item1="";
    String selected_item2="";
    String selected_item3="";
    String selected_item4="";
    private ScrollView scrollView;
    private RadioButton r_male,r_female;
    String sex_selected="Male";


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bmr, container, false);
        getActivity().setTitle("Basal Metabolic Rate");
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());

        findId(view);
//        setHasOptionsMenu(true);


        return view;
    }
    @Override
    public void onStart(){
        super.onStart();

        Log.i("On start of BMR","+");

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "4");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "BMR Fragment Started");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Fragment Open");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, bundle);

    }

    public void findId(View view){

        sharedpreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        radioSexGroup = (RadioGroup) view.findViewById(R.id.radioSex);
        swipe=(SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        r_male=(RadioButton) view.findViewById(R.id.radioMale) ;
        r_female=(RadioButton) view.findViewById(R.id.radioFemale);

//        Log.i("boolean bmr_m",sharedpreferences.getBoolean(Gender_selected_male,false)+"");
//        Log.i("boolean bmr_f",sharedpreferences.getBoolean(Gender_selected_female,false)+"");

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

        scrollView=(ScrollView) view.findViewById(R.id.scroll_bmr);

        age=(EditText) view.findViewById(R.id.age);
        age.setText(sharedpreferences.getString(Age,""));


        height_ft=(EditText) view.findViewById(R.id.height_ft);
        height_in=(EditText) view.findViewById(R.id.height_in);
        height_cm=(EditText) view.findViewById(R.id.height_cm);

        weight=(EditText) view.findViewById(R.id.weight);

        age.addTextChangedListener(watch1);
        height_ft.addTextChangedListener(watch1);
        height_in.addTextChangedListener(watch1);
        height_cm.addTextChangedListener(watch1);

        weight.addTextChangedListener(watch1);

        result=(TextView) view.findViewById(R.id.result);
        result1=(TextView) view.findViewById(R.id.result1);
        result2=(TextView) view.findViewById(R.id.result2);
        goal=(TextView) view.findViewById(R.id.goal);
        main_layout=(LinearLayout) view.findViewById(R.id.super_layout);

        height_spinner=(Spinner) view.findViewById(R.id.height_spinner);
        weight_spinner=(Spinner) view.findViewById(R.id.weight_spinner);
        goal_spinner=(Spinner) view.findViewById(R.id.goal_spinner);
        activity_spinner=(Spinner) view.findViewById(R.id.activity_spinner);

        adView=(AdView)view.findViewById(R.id.adView);
        swipe.setOnRefreshListener(this);

        LoadAd();


        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);


        radioSexGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
//                radioSexButton=(RadioButton) findViewById(checkedId);

                Bundle bundle = new Bundle();
                Log.i("Selected_id:",checkedId+"");
                switch(checkedId)
                {
                    case(R.id.radioFemale):

                        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "4.1");
                        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "BMR Radio button female ");
                        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Button");
                        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                        sex_selected = "Female";
                        calculate();
                        break;

                    case(R.id.radioMale):

                        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "4.2");
                        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "BMR Radio button male ");
                        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Button");
                        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                        sex_selected = "Male";
                        calculate();
                        break;


                }

            }
        });

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
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "4.3");
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "BMR age/height/weight edittext");
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "TextBox");
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

            SharedPreferences.Editor editor = sharedpreferences.edit();
            Log.i("age",s.toString());
            editor.putString(Age,s.toString());
            editor.apply();

            calculate();
        }

    };

    public void set_spinner(){

        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(getActivity(), R.array.height_spinner_array, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        height_spinner.setAdapter(adapter1);

        height_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {


            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

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

        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(getActivity(), R.array.activity_spinner_array, android.R.layout.simple_spinner_item);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        activity_spinner.setAdapter(adapter3);
        activity_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
            {
                int c = parent.getSelectedItemPosition();
                spinner_activity = activity_spinner.getSelectedItem().toString();
                if(c==0)
                {
                    selected_item3="Little to no exercise";
                }
                if(c==1)
                {
                    selected_item3="Light exercise (1–3 days per week)";
                }
                if(c==2)
                {
                    selected_item3="Moderate exercise (3–5 days per week)";
                }
                if(c==3)
                {
                    selected_item3="Heavy exercise (6–7 days per week)";
                }
                if(c==4)
                {
                    selected_item3="Very heavy exercise (twice per day)";
                }
                calculate();

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {} } );

        ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(getActivity(), R.array.goal_spinner_array, android.R.layout.simple_spinner_item);
        adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        goal_spinner.setAdapter(adapter4);
        goal_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
            {
                int d = parent.getSelectedItemPosition();
                spinner_goal = goal_spinner.getSelectedItem().toString();
                if(d==0)
                {
                    selected_item4="Maintain current weight";
                }
                if(d==1)
                {
                    selected_item4="Lose 0.5kg per week";
                }
                if(d==2)
                {
                    selected_item4="Lose 1kg per week";
                }
                if(d==3)
                {
                    selected_item4="Gain 0.5kg per week";
                }
                if(d==4)
                {
                    selected_item4="Gain 1kg per week";
                }
                calculate();

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {} } );

    }

    public void calculate()
    {
        float getHeight=0f, getWeight,getage;
        DecimalFormat df = new DecimalFormat("#0.0");

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

        if (weight.getText().toString().equals("")) {
            getWeight = 0f;
        } else {
            getWeight = Float.parseFloat(weight.getText().toString());
        }
        if (age.getText().toString().equals("")) {
            getage = 0f;
        } else {
            getage=Float.parseFloat(age.getText().toString());
        }

        if(getHeight!=0f && getWeight!=0f && getage!=0f) {

            if (sex_selected.equals("Male")) {
                if (selected_item1.equals("ft + in")) {
                    if (selected_item2.equals("lb")) {
                        bmr = 10 * getWeight * 0.453592f + 6.25f * getHeight * 2.54f - 5 * getage + 5;
                    }
                    if (selected_item2.equals("kg")) {
                        bmr = 10 * getWeight + 6.25f * getHeight * 2.54f - 5 * getage + 5;
                    }
                }

                if (selected_item1.equals("cm")) {
                    if (selected_item2.equals("lb")) {
                        bmr = 10 * getWeight * 0.453592f + 6.25f * getHeight - 5 * getage + 5;
                    }
                    if (selected_item2.equals("kg")) {
                        bmr = 10 * getWeight + 6.25f * getHeight - 5 * getage + 5;
                    }
                }
            }

            if (sex_selected.equals("Female")) {
                if (selected_item1.equals("ft + in")) {
                    if (selected_item2.equals("lb")) {
                        bmr = 10 * getWeight * 0.453592f + 6.25f * getHeight * 2.54f - 5 * getage - 161;
                    }
                    if (selected_item2.equals("kg")) {
                        bmr = 10 * getWeight + 6.25f * getHeight * 2.54f - 5 * getage - 161;
                    }
                }

                if (selected_item1.equals("cm")) {
                    if (selected_item2.equals("lb")) {
                        bmr = 10 * getWeight * 0.453592f + 6.25f * getHeight - 5 * getage - 161;
                    }
                    if (selected_item2.equals("kg")) {
                        bmr = 10 * getWeight + 6.25f * getHeight - 5 * getage - 161;
                    }
                }
            }

        }
        else bmr=0f;

            bmr=(int) bmr;

        if(sex_selected.equals("Female") && bmr>0) bmr=bmr-166;
        if(sex_selected.equals("Male") && bmr>0) bmr=bmr+166;

            if(selected_item3.equals("Little to no exercise"))
            {
                calories_activity=bmr*1.2f;
            }
            if(selected_item3.equals("Light exercise (1–3 days per week)"))
            {
                calories_activity=bmr*1.375f;
            }
            if(selected_item3.equals("Moderate exercise (3–5 days per week)"))
            {
                calories_activity=bmr*1.55f;
            }
            if(selected_item3.equals("Heavy exercise (6–7 days per week)"))
            {
                calories_activity=bmr*1.725f;
            }
            if(selected_item3.equals("Very heavy exercise (twice per day)"))
            {
                calories_activity=bmr*1.9f;
            }
            if(selected_item4.equals("Maintain current weight"))
            {
                calories_goal=calories_activity;
            }
            if(selected_item4.equals("Lose 0.5kg per week"))
            {
                calories_goal=calories_activity-500;
            }
            if(selected_item4.equals("Lose 1kg per week"))
            {
                calories_goal=calories_activity-1000;
            }
            if(selected_item4.equals("Gain 0.5kg per week"))
            {
                calories_goal=calories_activity+500;
            }
            if(selected_item4.equals("Gain 1kg per week"))
            {
                calories_goal=calories_activity+1000;
            }

            calories_activity=(int) calories_activity;
            calories_goal=(int) calories_goal;
            result.setText(bmr+" kcal");
            result1.setText(df.format(calories_activity)+" kcal");
            result2.setText(df.format(calories_goal)+" kcal");


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
