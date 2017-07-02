package com.samarth.lightweight;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.text.DecimalFormat;

/**
 * Created by Samarth on 23-Nov-16.
 */

public class helpandinfo extends AppCompatActivity
{

    private TextView bmi;
    private TextView bmi_text;
    private TextView bfp;
    private TextView bfp_text;
    private TextView bmr;
    private TextView bmr_text;
    private TextView wth;
    private TextView wth_text;
    private TextView converter_1;
    private TextView converter_2;
    private TextView length_result;
    private TextView mass_result;
    private LinearLayout l1;
    private LinearLayout l2;
    String selected_button;
    private Spinner length_spinner;
    private Spinner mass_spinner;
    private Spinner length_spinner_result;
    private Spinner mass_spinner_result;
    private String selected_item1="in";
    private String selected_item1_1="in";
    private String selected_item2="kg";
    private  String selected_item2_2="kg";
    private EditText length;
    private EditText mass;

    private AdView adview;
    float getHeight, getWeight;
    float result1;
    float result2;
    DecimalFormat df = new DecimalFormat("#0.0");


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.helpandinfo);

        bmi=(TextView) findViewById(R.id.bmi);
        bmi_text=(TextView) findViewById(R.id.bmi_text);
        bfp=(TextView) findViewById(R.id.bfp);
        bfp_text=(TextView) findViewById(R.id.bfp_text);
        bmr=(TextView) findViewById(R.id.bmr);
        bmr_text=(TextView) findViewById(R.id.bmr_text);
        wth=(TextView) findViewById(R.id.wth);
        wth_text=(TextView) findViewById(R.id.wth_text);
        converter_1=(TextView) findViewById(R.id.converter1);
        converter_2=(TextView) findViewById(R.id.converter2);
        length_result=(TextView) findViewById(R.id.height_result);
        mass_result=(TextView) findViewById(R.id.weight_result);
        l1=(LinearLayout) findViewById(R.id.l1);
        l2=(LinearLayout) findViewById(R.id.l2);
        length_spinner=(Spinner) findViewById(R.id.height_spinner);
        mass_spinner=(Spinner) findViewById(R.id.weight_spinner);
        length_spinner_result=(Spinner) findViewById(R.id.height_spinner_result);
        mass_spinner_result=(Spinner) findViewById(R.id.weight_spinner_result);
        length=(EditText) findViewById(R.id.height);
        mass=(EditText) findViewById(R.id.weight);

        length.addTextChangedListener(watch);
        mass.addTextChangedListener(watch);

        adview=(AdView) findViewById(R.id.adView);


        selected_button=getIntent().getStringExtra("intent_string");
        if(selected_button.equals("info"))
        {
            setTitle("FAQ");
            bmi.setVisibility(View.VISIBLE);
            bmi_text.setVisibility(View.VISIBLE);
            bfp.setVisibility(View.VISIBLE);
            bfp_text.setVisibility(View.VISIBLE);
            bmr.setVisibility(View.VISIBLE);
            bmr_text.setVisibility(View.VISIBLE);
            wth.setVisibility(View.VISIBLE);
            wth_text.setVisibility(View.VISIBLE);
            converter_1.setVisibility(View.GONE);
            converter_2.setVisibility(View.GONE);
            l1.setVisibility(View.GONE);
            l2.setVisibility(View.GONE);
            settext();
            LoadAd();


        }
        if(selected_button.equals("help"))
        {
            setTitle("Converter");
            bmi.setVisibility(View.GONE);
            bmi_text.setVisibility(View.GONE);
            bfp.setVisibility(View.GONE);
            bfp_text.setVisibility(View.GONE);
            bmr.setVisibility(View.GONE);
            bmr_text.setVisibility(View.GONE);
            wth.setVisibility(View.GONE);
            wth_text.setVisibility(View.GONE);
            converter_1.setVisibility(View.VISIBLE);
            converter_2.setVisibility(View.VISIBLE);
            l1.setVisibility(View.VISIBLE);
            l2.setVisibility(View.VISIBLE);
            adview.setVisibility(View.GONE);
//            calculate();
            setspinner();

        }

    }

    public void LoadAd(){

        Log.i("network",NetowrkConnection.CheckConnection(this)+"");
        if(NetowrkConnection.CheckConnection(this)) {
            adview.setVisibility(View.VISIBLE);
            AdRequest adRequest = new AdRequest.Builder().build();
            adview.loadAd(adRequest);
        }
        else adview.setVisibility(View.GONE);

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

         if(length.getText().toString().length()==0 )
         {
             length_result.setText("");
         }
            if(mass.getText().toString().length()==0)
            {
                mass_result.setText("");
            }
            if(length.getText().toString().length()!=0)
            {
                length_result();
            }
            if(mass.getText().toString().length()!=0)
            {
                mass_result();
            }

        }
    };

    public void settext()
    {
        bmi_text.setText(getString(R.string.bmi_text));
        bfp_text.setText(getString(R.string.bfp_text));
        bmr_text.setText(getString(R.string.bmr_text));
        wth_text.setText(getString(R.string.wth_text));
    }
    public void setspinner(){

        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this, R.array.length_spinner_array, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
       length_spinner.setAdapter(adapter1);

        length_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                int a = parent.getSelectedItemPosition();

                if (a == 0) {
                    selected_item1="in";
                    length_result();
                }
                if(a==1)
                {
                    selected_item1="cm";
                    length_result();
                }
                if(a==2)
                {
                    selected_item1="ft";
                    length_result();
                }






            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });

        length_spinner_result.setAdapter(adapter1);
        length_spinner_result.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                int b = parent.getSelectedItemPosition();

                if (b == 0) {
                    selected_item1_1="in";
                    length_result();
                }
                if(b==1)
                {
                    selected_item1_1="cm";
                    length_result();
                }
                if(b==2)
                {
                    selected_item1_1="ft";
                    length_result();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });


        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this, R.array.weight_spinner_array, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mass_spinner.setAdapter(adapter2);
        mass_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                int c = parent.getSelectedItemPosition();
                if(c==0)
                {
                    selected_item2="kg";
                    mass_result();
                }
                if(c==1)
                {
                    selected_item2="lb";
                    mass_result();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {} } );

        mass_spinner_result.setAdapter(adapter2);
        mass_spinner_result.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                int d = parent.getSelectedItemPosition();
               if(d==0)
                {
                    selected_item2_2="kg";
                    mass_result();
                }
                if(d==1)
                {
                    selected_item2_2="lb";
                    mass_result();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {} } );

    }


    public void length_result()
    {
        if(length.getText().toString().length()==0)
        {
            length_result.setText("");
        }
        if(length.getText().toString().length()!=0) {

            getHeight = Float.parseFloat(length.getText().toString());
            if (selected_item1.equals("in")) {
                if (selected_item1_1.equals("cm")) {
                    result1 = getHeight * 2.54f;
                    length_result.setText(df.format(result1));
                }
                if (selected_item1_1.equals("in")) {
                    result1 = getHeight;
                    length_result.setText(df.format(result1));
                }
                if (selected_item1_1.equals("ft")) {
                    result1 = getHeight/12f;
                    length_result.setText(df.format(result1));
                }
            }
            if (selected_item1.equals("cm")) {
                if (selected_item1_1.equals("in")) {
                    result1 = getHeight / 2.54f;
                    length_result.setText(df.format(result1));
                }
                if (selected_item1_1.equals("cm")) {
                    result1 = getHeight;
                    length_result.setText(df.format(result1));
                }
                if (selected_item1_1.equals("ft")) {
                    result1 = getHeight/30.48f;
                    length_result.setText(df.format(result1));
                }
            }
            if(selected_item1.equals("ft"))
            {
                if (selected_item1_1.equals("in")) {
                    result1 = getHeight *12f;
                    length_result.setText(df.format(result1));
                }
                if (selected_item1_1.equals("cm")) {
                    result1 = getHeight*30.48f;
                    length_result.setText(df.format(result1));
                }
                if (selected_item1_1.equals("ft")) {
                    result1 = getHeight;
                    length_result.setText(df.format(result1));
                }
            }
        }

    }
    public void mass_result()
    {

        if(mass.getText().toString().length()==0)
        {
            mass_result.setText("");
        }
        if(mass.getText().toString().length()!=0) {

            getWeight = Float.parseFloat(mass.getText().toString());
            if (selected_item2.equals("lb")) {
                if (selected_item2_2.equals("kg")) {
                    result2 = getWeight * 0.453592f;
                    mass_result.setText(df.format(result2));
                }
                if (selected_item2_2.equals("lb")) {
                    result2 = getWeight;
                    mass_result.setText(df.format(result2));
                }
            }
            if (selected_item2.equals("kg")) {
                if (selected_item2_2.equals("lb")) {
                    result2 = getWeight / 0.453592f;
                    mass_result.setText(df.format(result2));
                }
                if (selected_item2_2.equals("kg")) {
                    result2 = getWeight;
                    mass_result.setText(df.format(result2));
                }
            }
        }

    }

}
