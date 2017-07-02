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

import com.google.firebase.analytics.FirebaseAnalytics;
import com.samarth.lightweight.R;
import com.samarth.lightweight.base_Activity;
import com.samarth.lightweight.fragments.food_item_description;

import java.util.List;

/**
 * Created by Samarth on 23-Nov-16.
 */

public class food_category_adapter extends
        RecyclerView.Adapter<com.samarth.lightweight.database.food_category_adapter.ViewHolder>
{
    private static final int TYPE_HEAD=0;
    private static final int TYPE_LIST=1;
    public String category;
    public Bundle mbundle;

    FirebaseAnalytics mFirebaseAnalytics;


    private Context mContext;
    private List<String> food_categories;
    private List<food_items_model> food_items_model;

    public food_category_adapter(Context context,List<String> food_categories) {
        mContext=context;
        this.food_categories=food_categories;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {



       if(viewType==TYPE_LIST) {
           View itemView = LayoutInflater.from(parent.getContext())
                   .inflate(R.layout.card, parent, false);

           return new ViewHolder(itemView,viewType);
       }
        else if(viewType==TYPE_HEAD)
       {
           View itemView = LayoutInflater.from(parent.getContext())
                   .inflate(R.layout.header_recycle_view, parent, false);

           return new ViewHolder(itemView,viewType);
       }
        return null;
    }


    public class ViewHolder extends RecyclerView.ViewHolder
    {
        int view_type;
        public TextView food_category;
        public ImageView food_image;
        public ImageView header_image;


        public ViewHolder(final View view, int viewType) {
            super(view);

            if(viewType==TYPE_LIST) {

                mFirebaseAnalytics = FirebaseAnalytics.getInstance(view.getContext());

                food_category = (TextView) view.findViewById(R.id.food_category);
                food_image=(ImageView)view.findViewById(R.id.image);
                view_type=1;

                view.setOnClickListener(new View.OnClickListener() {
                    Bundle bundle= new Bundle();
                    @Override
                    public void onClick(View v) {

                        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "6");
                        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "FoodCategory Items");
                        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Recycler view items");
                        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                        category=food_category.getText().toString();
                        food_item_fragment(category,view);
                    }
                }) ;

            }
            else if(viewType==TYPE_HEAD)
            {
                header_image=(ImageView) view.findViewById(R.id.food_main);
                view_type=0;
            }
        }
    }


    @Override
    public void onBindViewHolder(com.samarth.lightweight.database.food_category_adapter.ViewHolder viewHolder, int position)
    {
        String food_cat;
        if(viewHolder.view_type==TYPE_LIST){
            food_cat=food_categories.get(position-1);
            viewHolder.food_category.setText(food_cat);
        }
        else if(viewHolder.view_type==TYPE_HEAD)
        {
            viewHolder.header_image.setImageResource(R.drawable.food_category1);
        }

    }

    public void food_item_fragment(String foodcategory,View view)
    {
        base_Activity activity = (base_Activity) view.getContext();
        food_item_description foodItemDescription = new food_item_description();
        mbundle=new Bundle();
        mbundle.putString("category_food",foodcategory);
//        Log.i("mbundle",mbundle.toString());
        foodItemDescription.setArguments(mbundle);

        activity.fragmentManager.beginTransaction().replace(R.id.content_frame, foodItemDescription)
                .addToBackStack(null).commit();

    }
    @Override
    public int getItemCount() {

        if(food_categories!=null)
        return food_categories.size()+1;
        return 0;
    }
    @Override
    public int getItemViewType(int position)
    {
        if(position==0)
            return TYPE_HEAD;
        return TYPE_LIST;

    }

}


