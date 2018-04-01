package com.nguyen_nguyen.foodentification;

import android.app.LoaderManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;


import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by nguyennguyen on 10/21/17.
 */

public class DisplayRecipe extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<Recipe>>{

    public static final String LOG_TAG = DisplayRecipe.class.getName();
    private final int LoaderID = 0;
    private Uri uri;
    private RecipeAdapter recipeAdapter;
    private TextView emptyView;
    private ProgressBar progressBar;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipe_display_activity);
        Uri photoUri = getIntent().getParcelableExtra(MainActivity.ID);
        try{
        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);}
        catch (IOException e)
        {
            e.printStackTrace();
        }

        // Find a reference to the {@link ListView} in the layout
        ListView recipeListView = (ListView) findViewById(R.id.list);

        emptyView = (TextView) findViewById(R.id.emptyView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        // Create a new {@link ArrayAdapter} of earthquakes
        recipeAdapter = new RecipeAdapter(this, new ArrayList<Recipe>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        recipeListView.setAdapter(recipeAdapter);
        recipeListView.setEmptyView(emptyView);

        recipeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try{
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(recipeAdapter.getItem(position).getUrl()));
                    startActivity(intent);
                }catch (ActivityNotFoundException e)
                {
                    Log.d(LOG_TAG, "Cannot Open the Url");
                    e.printStackTrace();
                }
            }
        });

        Context context = getApplicationContext();
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if(activeNetwork  != null && activeNetwork.isConnectedOrConnecting())
            getLoaderManager().initLoader(LoaderID, null, this);
        else {
            progressBar.setVisibility(View.GONE);
            emptyView.setText("No Internet Connection");
        }

    }

    @Override
    public Loader<ArrayList<Recipe>> onCreateLoader(int i, Bundle bundle) {

        return new RecipeLoader(getApplicationContext(), bitmap, getPackageName(),getPackageManager());
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Recipe>> loader, ArrayList<Recipe> recipes) {
        recipeAdapter.clear();

        progressBar.setVisibility(View.GONE);
        if(recipes != null && !recipes.isEmpty())
            recipeAdapter.addAll(recipes);
        emptyView.setText("No Recipe Found");
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Recipe>> loader) {
        recipeAdapter.clear();
        emptyView.setText("");
    }
}
