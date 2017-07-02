package com.samarth.lightweight;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.ByteArrayOutputStream;

/**
 * Created by Sam on 29-Mar-17.
 */

public class intoduction_activity extends AppCompatActivity implements View.OnClickListener {

    private EditText age;
    private ImageView male,female;
    private Button bmi,bfp,bmr,wthr,calories;

    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String Age = "ageKey";
    public static final String Gender_male = "genderKey1";
    public static final String Gender_female = "genderKey2";

    public static final String Gender_selected_male="G_male";
    public static final String Gender_selected_female="G_female";

    Bitmap man,man_selected,girl,girl_selected;

    private FirebaseAnalytics mFirebaseAnalytics;


    private SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.introduction_activity);

      mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        man=BitmapFactory.decodeResource(getResources(), R.drawable.man_silhouette);
        girl=BitmapFactory.decodeResource(getResources(), R.drawable.girl);


        age=(EditText) findViewById(R.id.age_tv);
        male=(ImageView)  findViewById(R.id.male);
        female=(ImageView) findViewById(R.id.female);
        bmi=(Button) findViewById(R.id.bmi);
        bfp=(Button) findViewById(R.id.bfp);
        bmr=(Button) findViewById(R.id.bmr);
        wthr=(Button) findViewById(R.id.wthr);
        calories=(Button) findViewById(R.id.calories);

        age.setText(sharedpreferences.getString(Age,""));

        age.addTextChangedListener(watch);


      if(sharedpreferences.getBoolean(Gender_selected_male,false))
        {
            male.setImageBitmap(decodeBase64(sharedpreferences.getString(Gender_male,encodeTobase64(man))));
        }

        if(sharedpreferences.getBoolean(Gender_selected_female,false))
        {
            female.setImageBitmap(decodeBase64(sharedpreferences.getString(Gender_female,encodeTobase64(girl))));
        }

        male.setOnClickListener(this);
        female.setOnClickListener(this);
        bmi.setOnClickListener(this);
        bfp.setOnClickListener(this);
        bmr.setOnClickListener(this);
        wthr.setOnClickListener(this);
        calories.setOnClickListener(this);

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

            SharedPreferences.Editor editor = sharedpreferences.edit();
            Log.i("age",s.toString());
            editor.putString(Age,s.toString());
            editor.apply();

            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "0.0");
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Introduction age edittext");
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "TextBox");
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);



        }

    };

    public void onResume(){
        super.onResume();
    }
    public void onPause(){
        super.onPause();
    }
    public void onStop(){
        super.onStop();
    }
    public void onDestroy(){
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {

        Bundle bundle=new Bundle();
        switch (v.getId()){


            case(R.id.bmi):

               if(Check_details()) {
                   bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "0.1");
                   bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "BMI fragment");
                   bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Button");
                   mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                   BMI(v);
               }
                    break;

            case(R.id.bfp):
                if(Check_details()) {
                    bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "0.2");
                    bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "BFP fragment");
                    bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Button");
                    mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                    BFP(v);

                }
                break;

            case(R.id.wthr):
                if(Check_details()){
                    bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "0.3");
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "WTHR fragment");
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Button");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                WTHR(v);}
                break;

            case(R.id.bmr):
                if(Check_details()){
                    bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "0.4");
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "BMR fragment");
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Button");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                BMR(v);}
                break;

            case(R.id.calories):
                if(Check_details()){
                    bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "0.5");
                    bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "CALORIES fragment");
                    bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Button");
                    mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                    CALORIES(v);}
                break;

            case(R.id.male):

                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "0.6");
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Male Gender");
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "ImageView");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                if(male!=null && male.getDrawable()!=null)
                {

                    man_selected = BitmapFactory.decodeResource(getResources(), R.drawable.man_silhouette_selected);
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString(Gender_male, encodeTobase64(man_selected));
                    editor.putBoolean(Gender_selected_male,true);
                    editor.putBoolean(Gender_selected_female,false);
                    editor.apply();

//                    Toast.makeText(this,"Gender:Male",Toast.LENGTH_SHORT).show();

                    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP)
                    {
                        if(male.getDrawable().getConstantState().equals(this.getResources()
                                .getDrawable(R.drawable.man_silhouette, this.getTheme())
                                .getConstantState()))
                        {
                            male.setImageResource(R.drawable.man_silhouette_selected);
                            female.setImageResource(R.drawable.girl);
                            break;
                        }

                    }
                    else
                    {
                        if(male.getDrawable().getConstantState().equals(this.getResources()
                                .getDrawable(R.drawable.man_silhouette)
                                .getConstantState()))
                        {
                            male.setImageResource(R.drawable.man_silhouette_selected);
                            female.setImageResource(R.drawable.girl);
                            break;
                        }

                    }
                }
                break;

            case(R.id.female):

                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "0.7");
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Female Gender");
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "ImageView");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                if(female!=null && female.getDrawable()!=null)
                {

                    girl_selected = BitmapFactory.decodeResource(getResources(), R.drawable.girl_selected);
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString(Gender_female, encodeTobase64(girl_selected));
                    editor.putBoolean(Gender_selected_female,true);
                    editor.putBoolean(Gender_selected_male,false);
                    editor.apply();

//                    Toast.makeText(this,"Gender:Female",Toast.LENGTH_SHORT).show();
                    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP)
                    {
                        if(female.getDrawable().getConstantState().equals(this.getResources()
                                .getDrawable(R.drawable.girl, this.getTheme())
                                .getConstantState()))
                        {
                            female.setImageResource(R.drawable.girl_selected);
                            male.setImageResource(R.drawable.man_silhouette);
                            break;
                        }

                    }
                    else
                    {
                        if(female.getDrawable().getConstantState().equals(this.getResources()
                                .getDrawable(R.drawable.girl)
                                .getConstantState()))
                        {
                            female.setImageResource(R.drawable.girl_selected);
                            male.setImageResource(R.drawable.man_silhouette);
                            break;
                        }

                    }
                }
                break;

        }
    }

    // method for bitmap to base64
    public static String encodeTobase64(Bitmap image) {

        Bitmap immage = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immage.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);

        Log.d("Image Log:", imageEncoded);
        return imageEncoded;
    }

    // method for base64 to bitmap
    public static Bitmap decodeBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory
                .decodeByteArray(decodedByte, 0, decodedByte.length);
    }

    public boolean Check_details()
    {

        if(age.getText().toString().equals("") && ( sharedpreferences.getBoolean(Gender_selected_male,false)
                || sharedpreferences.getBoolean(Gender_selected_female,false) ) )
        {
            Toast.makeText(this,"Please Enter your age",Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(!age.getText().toString().equals("") && ( !sharedpreferences.getBoolean(Gender_selected_male,false)
                && !sharedpreferences.getBoolean(Gender_selected_female,false) ) )
        {
            Toast.makeText(this,"Please Select your Gender",Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(age.getText().toString().equals("") && ( !sharedpreferences.getBoolean(Gender_selected_male,false)
                && !sharedpreferences.getBoolean(Gender_selected_female,false) ) )
        {
            Toast.makeText(this,"Please Enter All the details",Toast.LENGTH_SHORT).show();
            return  false;
        }
        else return true;
    }

    public void BMI(View v)
    {
        Intent intent=new Intent(this, base_Activity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.putExtra("Button_clicked","BMI");
        startActivity(intent);
    }
    public void BFP(View v)
    {
        Intent intent=new Intent(this, base_Activity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.putExtra("Button_clicked","BFP");
        startActivity(intent);
    }
    public void WTHR(View v)
    {
        Intent intent=new Intent(this, base_Activity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.putExtra("Button_clicked","WTHR");
        startActivity(intent);
    }
    public void BMR(View v)
    {
        Intent intent=new Intent(this, base_Activity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.putExtra("Button_clicked","BMR");
        startActivity(intent);
    }
    public void CALORIES(View v)
    {
        Intent intent=new Intent(this, base_Activity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.putExtra("Button_clicked","CALORIES");
        startActivity(intent);
    }
}