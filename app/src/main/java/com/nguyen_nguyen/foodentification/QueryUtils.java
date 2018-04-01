package com.nguyen_nguyen.foodentification;

/**
 * Created by nguyennguyen on 4/1/18.
 */


import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Helper methods related to requesting and receiving earthquake data from USGS.
 */
public final class QueryUtils {

    /**
     * Sample JSON response for a USGS query
     */
    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }


    public static ArrayList<Recipe> extractRecipe(String jsonResponse) {

        // Create an empty ArrayList that we can start adding earthquakes to
        ArrayList<Recipe> recipes = new ArrayList<>();

        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);

            JSONArray hits = jsonObject.getJSONArray("hits");
            for(int i = 0; i < hits.length(); i++)
            {
                JSONObject currObject = hits.getJSONObject(i).getJSONObject("recipe");
                String name = currObject.getString("label");
                String url = currObject.getString("url");
                JSONArray ingredients = currObject.getJSONArray("ingredients");
                ArrayList<String> ingredientString = new ArrayList<>();
                for(int j = 0; j < ingredients.length(); j++){
                    ingredientString.add(ingredients.getJSONObject(j).getString("text"));
                }
                recipes.add(new Recipe(name,ingredientString, url));
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }

        // Return the list of earthquakes
        return recipes;
    }
}
