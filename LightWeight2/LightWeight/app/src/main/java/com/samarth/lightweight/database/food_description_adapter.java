package com.samarth.lightweight.database;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.samarth.lightweight.R;
import com.samarth.lightweight.base_Activity;
import com.samarth.lightweight.fragments.foodCategory;
import com.samarth.lightweight.fragments.food_item_description;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Samarth on 24-Nov-16.
 */

public class food_description_adapter extends
        RecyclerView.Adapter<com.samarth.lightweight.database.food_description_adapter.ViewHolder> {

    private List<food_items_model> food_items_model;
    private Context mContext;
    databasehandler db;
    String food_item_name;
    private foodCategory foodCategory;
    public static int  position;
    private FirebaseAnalytics firebaseAnalytics;


    public food_description_adapter(Context context, List<food_items_model> food_itemseModels) {
    mContext=context;
    food_items_model = food_itemseModels;

}
    public food_description_adapter(Context context, List<food_items_model> food_itemseModels, foodCategory fr) {
        mContext=context;
        food_items_model = food_itemseModels;
        foodCategory=fr;
    }



@Override
public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.food_description_layout, parent, false);

        return new ViewHolder(itemView);
}


public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
    public TextView food_item;
    public TextView prot_value;
    public TextView fat_value;
    public TextView carb_value;
    public TextView cal_value;
    public ImageView bk1;


    public ViewHolder(View view) {
        super(view);

        foodCategory=new foodCategory();
        db=databasehandler.getInstance(view.getContext());

        firebaseAnalytics=FirebaseAnalytics.getInstance(view.getContext());

        food_item = (TextView)view.findViewById(R.id.food_item);
        prot_value= (TextView)view.findViewById(R.id.prot_value);
        fat_value= (TextView)view.findViewById(R.id.fat_value);
        carb_value= (TextView)view.findViewById(R.id.carb_value);
        cal_value= (TextView)view.findViewById(R.id.cal_value);

            view.setOnClickListener(this);


    }

    @Override
    public void onClick(View v)
    {
        position = getAdapterPosition();

//        Log.i("clicked item pos",position+"");
        if(foodCategory.getfragmentstatus()) {

            Bundle bundle=new Bundle();
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "7");
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "FoodDescription Items");
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Recycler view items");
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);


                base_Activity activity = (base_Activity) v.getContext();
                food_item_description foodItemDescription = new food_item_description();

//                Log.i("food_item_id",db.getFoodItemId(food_item.getText().toString())+"");


            com.samarth.lightweight.database.food_items_model selected_food_item_model=new food_items_model();

                   selected_food_item_model = db.getfood_item(db.getFoodItemId(food_item.getText().toString()));

                Bundle mbundle = new Bundle();
                mbundle.putString("category_food", db.getFoodCategory(food_item.getText().toString()));
                mbundle.putParcelable("food_item_model",selected_food_item_model);

//                Log.i("mbundle recycle view ", mbundle.toString());

                foodItemDescription.setArguments(mbundle);
                activity.fragmentManager.beginTransaction().replace(R.id.content_frame, foodItemDescription)
                        .addToBackStack(null).commit();

//                Toast.makeText(v.getContext(), food_item.getText().toString(), Toast.LENGTH_SHORT).show();
        }

    }
}



    @Override
    public void onBindViewHolder(final com.samarth.lightweight.database.food_description_adapter.ViewHolder viewHolder, int position)
    {

        food_items_model food_item= food_items_model.get(position);

//        SpannableStringBuilder builder =new SpannableStringBuilder();
//
//        food_item_name=food_item.getfood_item();
//        SpannableString food_item_spannable= new SpannableString(food_item_name);
//
//        food_item_spannable.setSpan(new ForegroundColorSpan(Color.BLACK),0,food_item_name.length(),0);
//        builder.append(food_item_spannable);
//
//        StyleSpan boldspan=new StyleSpan(Typeface.BOLD);
//        String food_item_amount=" (100 g.)";
//        SpannableString food_amount_spannable=new SpannableString(food_item_amount);
//        food_amount_spannable.setSpan(boldspan,0,food_item_amount.length(),0);
//        builder.append(food_amount_spannable);


        viewHolder.food_item.setText(food_item.getfood_item());
        viewHolder.prot_value.setText(String.valueOf(food_item.getproteins())+ "g.");
        viewHolder.fat_value.setText(String.valueOf(food_item.getfat())+ "g.");
        viewHolder.carb_value.setText(String.valueOf(food_item.getcarb())+ "g.");
        viewHolder.cal_value.setText(String.valueOf(food_item.getcalorie()));

    }



    @Override
    public int getItemCount() {

        if(food_items_model !=null) {
//            Log.i("food list size", food_items_model.size() + "");
            return food_items_model.size();
        }
        return 0;
    }

    public void setFilter(List<food_items_model> foods) {
                food_items_model = new ArrayList<>();
                food_items_model.addAll(foods);
                notifyDataSetChanged();
    }
}


