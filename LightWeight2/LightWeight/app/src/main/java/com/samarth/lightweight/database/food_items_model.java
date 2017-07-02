package com.samarth.lightweight.database;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Samarth on 23-Nov-16.
 */

public class food_items_model implements Parcelable {

    public String food_category;
    public int id;
    public String food_item;
    public float proteins;
    public float fat;
    public float carb;
    public float calorie;

    // Empty constructor
    public food_items_model(){

    }
    // constructor
    public food_items_model(String food_category, String food_item, float proteins, float fat, float carb, float calorie){
        this.food_category = food_category;
        this.food_item= food_item;
        this.proteins = proteins;
        this.carb=carb;
        this.calorie=calorie;
        this.fat=fat;
    }
    public food_items_model(int id, String food_category, String food_item, float proteins, float fat, float carb, float calorie){
        this.id=id;
        this.food_category = food_category;
        this.food_item= food_item;
        this.proteins = proteins;
        this.carb=carb;
        this.calorie=calorie;
        this.fat=fat;
    }

    public int gettId(){
        return this.id;
    }
    public void setId(int id)
    {
        this.id=id;
    }

    public String getfood_category(){
        return this.food_category;
    }

    public void setfood_category(String food_category){
        this.food_category = food_category;
    }

    public String getfood_item(){
        return this.food_item;
    }

    public void setfood_item(String food_item){
        this.food_item = food_item;
    }

    public float getproteins(){
        return this.proteins;
    }

    public void setproteins(float proteins){
        this.proteins = proteins;
    }
    public float getfat(){
        return this.fat;
    }

    public void setfat(float fat){
        this.fat = fat;
    }
    public float getcarb(){
        return this.carb;
    }

    public void setcarb(float carb){
        this.carb = carb;
    }
    public float getcalorie(){
        return this.calorie;
    }

    public void setcalorie(float calorie){
        this.calorie = calorie;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(proteins);
        dest.writeFloat(calorie);
        dest.writeFloat(carb);
        dest.writeFloat(fat);
        dest.writeString(food_category);
        dest.writeString(food_item);
    }
    private food_items_model(Parcel in)
    {
        food_category=in.readString();
        food_item=in.readString();
        proteins=in.readFloat();
        carb=in.readFloat();
        calorie=in.readFloat();
        fat=in.readFloat();
    }
    public  final Creator<food_items_model> CREATOR = new Creator<food_items_model>() {
        @Override
        public food_items_model createFromParcel(Parcel in) {
            return new food_items_model(in);
        }

        @Override
        public food_items_model[] newArray(int size) {
            return new food_items_model[size];
        }
    };
}
