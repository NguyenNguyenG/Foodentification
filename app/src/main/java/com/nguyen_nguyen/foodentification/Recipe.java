package com.nguyen_nguyen.foodentification;

/**
 * Created by nguyennguyen on 10/21/17.
 */

public class Recipe {
    private String name;
    private String ingredient;
    private String url;
    public Recipe(String n, String in, String u)
    {
        name = n;
        ingredient = in;
        url = u;
    }
    public String getName()
    {
        return name;
    }
    public String getIngredient()
    {
        return ingredient;
    }
    public String getUrl()
    {
        return url;
    }
}
