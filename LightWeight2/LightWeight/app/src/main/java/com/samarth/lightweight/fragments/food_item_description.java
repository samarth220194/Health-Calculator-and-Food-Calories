package com.samarth.lightweight.fragments;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.samarth.lightweight.R;
import com.samarth.lightweight.database.databasehandler;
import com.samarth.lightweight.database.food_description_adapter;
import com.samarth.lightweight.database.food_items_model;
import com.samarth.lightweight.database.recycler_view_divider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Samarth on 24-Nov-16.
 */

public class food_item_description extends Fragment implements SearchView.OnQueryTextListener,
        View.OnClickListener{

    private food_description_adapter adapter;
    private RecyclerView recyclerView;
    databasehandler db;
    private String Food_category;
    private food_items_model selected_food_item_model;
    private List<food_items_model> food_itemseModels;
    private List<food_items_model> filtered_food_itemseModels;
    private LinearLayout namell,caloriell;
    private ImageView  name_sorting,calorie_sorting;
    private TextView nametv,calroietv,measure_dict;
    LinearLayoutManager linearLayoutManager;

    private FirebaseAnalytics firebaseAnalytics;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.food_description, container, false);

        firebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());


        findId(view);
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onStart(){
        super.onStart();

        Log.i("On start FoodDescr","+");

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "8");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "FoodDescription Fragment Started");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Fragment Open");
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, bundle);

    }
    public void findId(View view) {

        foodCategory.Is_food_category_fragment = false;

        db = databasehandler.getInstance(getContext());
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view2);

        measure_dict = (TextView) view.findViewById(R.id.measure_dict);
        namell = (LinearLayout) view.findViewById(R.id.namell);
        caloriell = (LinearLayout) view.findViewById(R.id.caloriell);
        nametv = (TextView) view.findViewById(R.id.nametv);
        calroietv = (TextView) view.findViewById(R.id.calorietv);
        name_sorting = (ImageView) view.findViewById(R.id.name_sorting);
        calorie_sorting = (ImageView) view.findViewById(R.id.calorie_sorting);

        namell.setOnClickListener(this);
        caloriell.setOnClickListener(this);
        measure_dict.setOnClickListener(this);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            Food_category = bundle.getString("category_food");
            selected_food_item_model = bundle.getParcelable("food_item_model");
        }
        getActivity().setTitle(Food_category);

        food_itemseModels = new ArrayList<>();
        filtered_food_itemseModels = new ArrayList<>();

        food_itemseModels.addAll(db.getAllfood_items(Food_category));

        if(foodCategory.Is_food_category_fragment_search_bar)
        {
        for (int i = 0; i < food_itemseModels.size(); i++)
        {
            if (food_itemseModels.get(i).food_item.equals(selected_food_item_model.food_item))
            {
                int deleting_pos = i;
                Log.i("position", deleting_pos + "");
                food_itemseModels.remove(deleting_pos);
                food_itemseModels.add(0, selected_food_item_model);
                break;
            }
        }
    }
        foodCategory.Is_food_category_fragment_search_bar=false;

        filtered_food_itemseModels.addAll(db.getAllfood_items(Food_category));

        adapter = new food_description_adapter(getActivity(), food_itemseModels);

        recyclerView.addItemDecoration(new recycler_view_divider(getActivity(), LinearLayoutManager.VERTICAL));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        linearLayoutManager=new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main, menu);

        final MenuItem searchItem = menu.findItem(R.id.menu_search);
        searchItem.setVisible(true);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        final MenuItem refresh=menu.findItem(R.id.refresh);
        refresh.setVisible(false);

        MenuItemCompat.setOnActionExpandListener(searchItem,

                        new MenuItemCompat.OnActionExpandListener() {
                            Bundle bundle = new Bundle();
                            @Override
                            public boolean onMenuItemActionCollapse(MenuItem item) {

                                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "8.1");
                                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "FoodDescription Searchbar collapsed");
                                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Button");
                                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                                // Do something when collapsed
                                Log.i("search bar","collapsed");
                                adapter.setFilter(food_itemseModels);
                                return true; // Return true to collapse action view
                            }

                            @Override
                            public boolean onMenuItemActionExpand(MenuItem item) {
                                // Do something when expanded

                                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "8.2");
                                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "FoodDescription Searchbar Expanded");
                                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Button");
                                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                                Log.i("search bar","expanded");
                                return true; // Return true to expand action view
                            }
                        });
        searchView.setOnQueryTextListener(this);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
}

    private List<food_items_model> filter(List<food_items_model> models, String query) {
        query = query.toLowerCase();

        final List<food_items_model> filteredModelList = new ArrayList<>();
        for (food_items_model model : models) {
            final String text = model.getfood_item().toLowerCase();
            if (text.contains(query)) {
                filteredModelList.add(model);
            }
        }
        filtered_food_itemseModels =filteredModelList;
        return filteredModelList;
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        Bundle bundle=new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "8.3");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "FoodDescription Searchbar Typing");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Button");
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
        Log.i("Text on search bar","is changed");
        final List<food_items_model> filteredModelList = filter(food_itemseModels, newText);
        adapter.setFilter(filteredModelList);
        return true;
    }

    @Override
    public void onClick(View v) {
        Bundle bundle=new Bundle();

        switch(v.getId()){

            case(R.id.namell):

                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "8.4");
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "FoodDescription name sorting");
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Button");
                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                calorie_sorting.setVisibility(View.GONE);
                name_sorting.setVisibility(View.VISIBLE);
                nametv.setTextColor(getResources().getColor(R.color.colorPrimary));
                calroietv.setTextColor(Color.rgb(255,255,255));
                if(getContext()!=null && name_sorting!=null && name_sorting.getDrawable()!=null)
                {
                    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP)
                    {
                        if(name_sorting.getDrawable().getConstantState().equals(getContext().getResources()
                            .getDrawable(R.drawable.ic_arrow_drop_up_white_24dp, getContext().getTheme())
                            .getConstantState()))
                        {
                            name_sorting.setImageResource(R.drawable.ic_arrow_drop_down_white_24dp);
                                name_descending();
                            break;
                        }
                        if(name_sorting.getDrawable().getConstantState().equals(getContext().getResources()
                                .getDrawable(R.drawable.ic_arrow_drop_down_white_24dp, getContext().getTheme())
                                .getConstantState()))
                        {
                            name_sorting.setImageResource(R.drawable.ic_arrow_drop_up_white_24dp);
                            name_ascending();
                            break;
                        }
                    }
                    else
                    {
                        if(name_sorting.getDrawable().getConstantState().equals(getContext().getResources()
                                .getDrawable(R.drawable.ic_arrow_drop_up_white_24dp)
                                .getConstantState()))
                        {
                            name_sorting.setImageResource(R.drawable.ic_arrow_drop_down_white_24dp);
                            name_descending();
                            break;
                        }
                        if(name_sorting.getDrawable().getConstantState().equals(getContext().getResources()
                                .getDrawable(R.drawable.ic_arrow_drop_down_white_24dp)
                                .getConstantState()))
                        {
                            name_sorting.setImageResource(R.drawable.ic_arrow_drop_up_white_24dp);
                            name_ascending();
                            break;
                        }
                    }
                }
////                if(name_sorting.getDrawable().getConstantState()
////                        .equals(getResources().getDrawable(R.drawable.ic_arrow_drop_up_white_24dp).getConstantState()))
////                {
//                    name_sorting.setImageResource(R.drawable.ic_arrow_drop_down_white_24dp);
//
//                    name_descending();
//                    break;
////                }
////                if(name_sorting.getDrawable().getConstantState()
////                        .equals(getResources().getDrawable(R.drawable.ic_arrow_drop_down_white_24dp).getConstantState()))
////                {
////                    name_sorting.setImageResource(R.drawable.ic_arrow_drop_up_white_24dp);
////
////                    name_ascending();
////                    break;
////                }
////                break;
            case(R.id.caloriell):

                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "8.5");
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "FoodDescription calorie sorting");
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Button");
                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                name_sorting.setVisibility(View.GONE);
                calorie_sorting.setVisibility(View.VISIBLE);
                calroietv.setTextColor(getResources().getColor(R.color.colorPrimary));
                nametv.setTextColor(Color.rgb(255,255,255));

////                if(calorie_sorting.getDrawable().getConstantState()
////                        .equals(getResources().getDrawable(R.drawable.ic_arrow_drop_up_white_24dp).getConstantState()))
////                {
//                    calorie_sorting.setImageResource(R.drawable.ic_arrow_drop_down_white_24dp);
//                    calorie_descending();
//                    break;
////                }
////                if(calorie_sorting.getDrawable().getConstantState()
////                        .equals(getResources().getDrawable(R.drawable.ic_arrow_drop_down_white_24dp).getConstantState()))
////                {
////                    calorie_sorting.setImageResource(R.drawable.ic_arrow_drop_up_white_24dp);
////                    calroie_ascending();
////                    break;
////                }
////                break;

                if(getContext()!=null && name_sorting!=null && name_sorting.getDrawable()!=null)
                {
                    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP)
                    {
                        if(calorie_sorting.getDrawable().getConstantState().equals(getContext().getResources()
                                .getDrawable(R.drawable.ic_arrow_drop_up_white_24dp, getContext().getTheme())
                                .getConstantState()))
                        {
                            calorie_sorting.setImageResource(R.drawable.ic_arrow_drop_down_white_24dp);
                            calorie_descending();
                            break;
                        }
                        if(calorie_sorting.getDrawable().getConstantState().equals(getContext().getResources()
                                .getDrawable(R.drawable.ic_arrow_drop_down_white_24dp, getContext().getTheme())
                                .getConstantState()))
                        {
                            calorie_sorting.setImageResource(R.drawable.ic_arrow_drop_up_white_24dp);
                            calroie_ascending();
                            break;
                        }
                    }
                    else
                    {
                        if(calorie_sorting.getDrawable().getConstantState().equals(getContext().getResources()
                                .getDrawable(R.drawable.ic_arrow_drop_up_white_24dp)
                                .getConstantState()))
                        {
                            calorie_sorting.setImageResource(R.drawable.ic_arrow_drop_down_white_24dp);
                            calorie_descending();
                            break;
                        }
                        if(calorie_sorting.getDrawable().getConstantState().equals(getContext().getResources()
                                .getDrawable(R.drawable.ic_arrow_drop_down_white_24dp)
                                .getConstantState()))
                        {
                            calorie_sorting.setImageResource(R.drawable.ic_arrow_drop_up_white_24dp);
                            calroie_ascending();
                            break;
                        }
                    }
                }

            case(R.id.measure_dict):

                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "8.6");
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Measurement Dictionary");
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Button");
                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);


                measurement_dictionary measurementDictionary = new measurement_dictionary();
                measurementDictionary.show(getActivity().getSupportFragmentManager(), "MEASUREMENT DICTIONARY");

                break;

            default:
                break;
        }
    }

    public void name_descending(){
        Log.i("name","descending");
        Collections.sort(filtered_food_itemseModels, new Comparator<food_items_model>() {
            @Override
            public int compare(food_items_model lhs, food_items_model rhs) {
                return  rhs.food_item.compareTo(lhs.food_item);
            }
        });
        adapter.setFilter(filtered_food_itemseModels);
        adapter.notifyDataSetChanged();
    }
    public void name_ascending(){
        Log.i("name","ascending");
        Collections.sort(filtered_food_itemseModels, new Comparator<food_items_model>() {
            @Override
            public int compare(food_items_model lhs, food_items_model rhs) {
                return  lhs.food_item.compareTo(rhs.food_item);
            }
        });
        adapter.setFilter(filtered_food_itemseModels);
        adapter.notifyDataSetChanged();
    }
    public void calorie_descending(){
        Log.i("calorie","descending");
        Collections.sort(filtered_food_itemseModels, new Comparator<food_items_model>() {
            @Override
            public int compare(food_items_model lhs, food_items_model rhs) {
                return (int) (rhs.calorie-lhs.calorie);
            }
        });
        adapter.setFilter(filtered_food_itemseModels);
        adapter.notifyDataSetChanged();
    }
    public void calroie_ascending(){
        Log.i("calorie","ascending");
        Collections.sort(filtered_food_itemseModels, new Comparator<food_items_model>() {
            @Override
            public int compare(food_items_model lhs, food_items_model rhs) {
                return (int) (lhs.calorie-rhs.calorie);
            }
        });
        adapter.setFilter(filtered_food_itemseModels);
        adapter.notifyDataSetChanged();


    }

}
