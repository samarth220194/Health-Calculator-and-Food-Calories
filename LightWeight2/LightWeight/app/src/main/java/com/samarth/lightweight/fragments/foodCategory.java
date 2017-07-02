package com.samarth.lightweight.fragments;

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

import com.google.firebase.analytics.FirebaseAnalytics;
import com.samarth.lightweight.R;
import com.samarth.lightweight.database.databasehandler;
import com.samarth.lightweight.database.food_category_adapter;
import com.samarth.lightweight.database.food_description_adapter;
import com.samarth.lightweight.database.food_items_model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Samarth on 23-Nov-16.
 */

public class foodCategory extends Fragment implements  SearchView.OnQueryTextListener{

    public RecyclerView recyclerView;
    private food_category_adapter adapter;
    private food_description_adapter food_item_adapter;
    databasehandler db;

    List<food_items_model> food_itemseModels;
    private List<food_items_model> filtered_food_itemseModels;

    private FirebaseAnalytics mFirebaseAnalytics;


    public static boolean Is_food_category_fragment;
    public static boolean Is_food_category_fragment_search_bar=false;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.food_category, container, false);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());

        getActivity().setTitle("Calories in Food");
        setHasOptionsMenu(true);
        findId(view);

        return view;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main, menu);

        final MenuItem searchItem = menu.findItem(R.id.menu_search);
        searchItem.setVisible(true);
        final MenuItem refresh=menu.findItem(R.id.refresh);
        refresh.setVisible(false);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        MenuItemCompat.setOnActionExpandListener(searchItem,

                new MenuItemCompat.OnActionExpandListener() {
                    Bundle bundle = new Bundle();
                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        // Do something when collapsed
                        Log.i("search bar","collapsed");
                        Is_food_category_fragment_search_bar=false;

                        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "5.1");
                        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "FoodCategory Searchbar collapsed");
                        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Button");
                        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);


                        recyclerView.setHasFixedSize(true);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

                        food_item_adapter.setFilter(food_itemseModels);
                        return true; // Return true to collapse action view
                    }

                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
                        // Do something when expanded
                        Log.i("search bar","expanded");
                        Is_food_category_fragment_search_bar=true;

                        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "5.2");
                        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "FoodCategory Searchbar expanded");
                        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Button");
                        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);


                        recyclerView.setHasFixedSize(true);
                        recyclerView.setAdapter(food_item_adapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

                        return true; // Return true to expand action view
                    }
                });
        searchView.setOnQueryTextListener(this);

    }

    @Override
    public void onStart(){
        super.onStart();

        Log.i("On start of FoodCateg","+");

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "5");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "FoodCategory Fragment Started");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Fragment Open");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, bundle);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    public void findId(View view) {

        db=databasehandler.getInstance(getContext());

        Is_food_category_fragment=true;

        food_itemseModels = new ArrayList<>();
        filtered_food_itemseModels =new ArrayList<>();

        food_itemseModels.addAll(db.getAllfooditems());

        recyclerView=(RecyclerView)view.findViewById(R.id.recycler_view1);

        adapter=new food_category_adapter(getActivity(),db.getfood_category_list());

        food_item_adapter=new food_description_adapter(getContext(), filtered_food_itemseModels,this);

        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

    }

    public boolean getfragmentstatus() {
        Log.i("food categry status",Is_food_category_fragment+"");
        return Is_food_category_fragment;
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
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "5.3");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "FoodCategory Searchbar typing");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Button");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        Log.i("search bar","typing");
        final List<food_items_model> filteredModelList = filter(food_itemseModels, newText);
        food_item_adapter.setFilter(filteredModelList);
        return true;
    }
}
