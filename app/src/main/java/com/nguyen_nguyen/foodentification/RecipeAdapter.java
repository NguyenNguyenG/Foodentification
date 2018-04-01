package com.nguyen_nguyen.foodentification;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by nguyennguyen on 10/21/17.
 */

public class RecipeAdapter extends ArrayAdapter<Recipe>{
    public RecipeAdapter(Context context, ArrayList<Recipe> recipeList)
    {

        //need context to inflate the list_item xml later
        super(context, 0, recipeList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View listViewItem = convertView;

        //create view if there is no view yet
        if(listViewItem == null)
        {
            listViewItem = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        Recipe currRecipe = getItem(position);
        TextView name = (TextView) listViewItem.findViewById(R.id.name);
        TextView ingredient = (TextView) listViewItem.findViewById(R.id.ingredient);

        name.setText(currRecipe.getName());
        String ingredientText = "";
        for(int i = 0; i < currRecipe.getIngredient().size(); i++){
            ingredientText = ingredientText + currRecipe.getIngredient().get(i) + "\n";
        }
        ingredient.setText(ingredientText);
        return listViewItem;
    }
}
