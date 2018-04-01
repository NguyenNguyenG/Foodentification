package com.nguyen_nguyen.foodentification;

import java.util.ArrayList;

/**
 * Created by nguyennguyen on 10/21/17.
 */

public class Recipe {
    private String name;
    private ArrayList<String> ingredient;
    private String url;
    public Recipe(String n, ArrayList<String> in, String u)
    {
        name = n;
        ingredient = in;
        url = u;
    }
    public String getName()
    {
        return name;
    }
    public ArrayList<String> getIngredient()
    {
        return ingredient;
    }
    public String getUrl()
    {
        return url;
    }
}
