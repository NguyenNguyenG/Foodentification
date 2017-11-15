package com.nguyen_nguyen.foodentification;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.microsoft.projectoxford.vision.VisionServiceClient;
import com.microsoft.projectoxford.vision.VisionServiceRestClient;
import com.microsoft.projectoxford.vision.contract.AnalysisResult;
import com.microsoft.projectoxford.vision.contract.Caption;
import com.microsoft.projectoxford.vision.contract.Category;
import com.microsoft.projectoxford.vision.rest.VisionServiceException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by nguyennguyen on 10/22/17.
 */

public class RecipeLoader extends AsyncTaskLoader<ArrayList<Recipe>> {
    public VisionServiceClient visionServiceClient = new VisionServiceRestClient("0cc9bc9208a54818a87df3e71d1d01fd", "https://westcentralus.api.cognitive.microsoft.com/vision/v1.0");
    private Bitmap bitmap;

    public RecipeLoader(Context context, Bitmap bm)
    {
        super(context);
        bitmap = bm;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public ArrayList<Recipe> loadInBackground() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        AnalysisResult result = null;
        try{
            String[] feature = {"Categories", "Description"};
            String[] detail = {};
            result = visionServiceClient.analyzeImage(inputStream,feature, detail);

        }catch (IOException e)
        {
            e.printStackTrace();
        }catch (VisionServiceException e)
        {
            e.printStackTrace();
        }

        String target = "";
        if(result != null)
        {
            for(Caption caption: result.description.captions)
                target += caption.text;
        }

        ArrayList<Recipe> n = new ArrayList<>();
        n.add(new Recipe(target, "", ""));
        return n;


    }
}
