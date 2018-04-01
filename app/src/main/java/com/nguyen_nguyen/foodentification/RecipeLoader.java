package com.nguyen_nguyen.foodentification;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequest;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.AnnotateImageResponse;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.WebDetection;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;
import com.google.api.services.vision.v1.model.WebEntity;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by nguyennguyen on 10/22/17.
 */

public class RecipeLoader extends AsyncTaskLoader<ArrayList<Recipe>> {
    private static final String CLOUD_VISION_API_KEY = "AIzaSyDWJF59piOcvw4NflY_290GtuXJQFO4jGk";
    private Bitmap bitmap;
    private static final String TAG = "TAG";
    private static final String ANDROID_CERT_HEADER = "X-Android-Cert";
    private static final String ANDROID_PACKAGE_HEADER = "X-Android-Package";
    private String packageName = "";
    private PackageManager packageManager;

    public RecipeLoader(Context context, Bitmap bm, String pn, PackageManager pm)
    {
        super(context);
        bitmap = bm;
        packageName = pn;
        packageManager = pm;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public ArrayList<Recipe> loadInBackground() {
        Bitmap scaledBitmap = scaleBitmapDown(bitmap, 1200);

        String URL_BASE_BEGIN = "https://api.edamam.com/search?q=";
        String URL_BASE_END = "&app_id=c7ded827&app_key=875f630a789eb81633e1e99fe272b061&from=0&to=10";
        String foodItem = "";
        final ArrayList<Recipe> recipeList = new ArrayList<>();

                try {
                    HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
                    JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

                    VisionRequestInitializer requestInitializer =
                            new VisionRequestInitializer(CLOUD_VISION_API_KEY) {
                                /**
                                 * We override this so we can inject important identifying fields into the HTTP
                                 * headers. This enables use of a restricted cloud platform API key.
                                 */
                                @Override
                                protected void initializeVisionRequest(VisionRequest<?> visionRequest)
                                        throws IOException {
                                    super.initializeVisionRequest(visionRequest);


                                    visionRequest.getRequestHeaders().set(ANDROID_PACKAGE_HEADER, packageName);

                                    String sig = PackageManagerUtils.getSignature(packageManager, packageName);

                                    visionRequest.getRequestHeaders().set(ANDROID_CERT_HEADER, sig);
                                }
                            };

                    Vision.Builder builder = new Vision.Builder(httpTransport, jsonFactory, null);
                    builder.setVisionRequestInitializer(requestInitializer);

                    Vision vision = builder.build();

                    BatchAnnotateImagesRequest batchAnnotateImagesRequest =
                            new BatchAnnotateImagesRequest();
                    batchAnnotateImagesRequest.setRequests(new ArrayList<AnnotateImageRequest>() {{
                        AnnotateImageRequest annotateImageRequest = new AnnotateImageRequest();

                        // Add the image
                        Image base64EncodedImage = new Image();
                        // Convert the bitmap to a JPEG
                        // Just in case it's a format that Android understands but Cloud Vision
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
                        byte[] imageBytes = byteArrayOutputStream.toByteArray();

                        // Base64 encode the JPEG
                        base64EncodedImage.encodeContent(imageBytes);
                        annotateImageRequest.setImage(base64EncodedImage);

                        // add the features we want
                        annotateImageRequest.setFeatures(new ArrayList<Feature>() {{
                            Feature labelDetection = new Feature();
                            labelDetection.setType("WEB_DETECTION");
                            labelDetection.setMaxResults(10);
                            add(labelDetection);
                        }});

                        // Add the list of one thing to the request
                        add(annotateImageRequest);
                    }});

                    Vision.Images.Annotate annotateRequest =
                            vision.images().annotate(batchAnnotateImagesRequest);
                    // Due to a bug: requests to Vision API containing large images fail when GZipped.
                    annotateRequest.setDisableGZipContent(true);
                    Log.d(TAG, "created Cloud Vision request object, sending request");

                    BatchAnnotateImagesResponse response = annotateRequest.execute();
                    foodItem = convertResponseToString(response);
                    Log.d("Food",foodItem);

                } catch (GoogleJsonResponseException e) {
                    Log.d(TAG, "failed to make API request because " + e.getContent());
                } catch (IOException e) {
                    Log.d(TAG, "failed to make API request because of other IOException " +
                            e.getMessage());
                }
        if (!foodItem.equals("")){
            String mUrl = URL_BASE_BEGIN + foodItem + URL_BASE_END;
            Log.d("url",mUrl);
            URL url = createURl(mUrl);
            if(url == null)
                return null;

            String jsonResponse = "";
            try {
                jsonResponse = makeHttpRequest(url);
            }catch (IOException e)
            {
                Log.e(TAG, "Error in getting a json response", e);
            }

            if(TextUtils.isEmpty(jsonResponse))
                return null;

            return QueryUtils.extractRecipe(jsonResponse);
        }

        return recipeList;


    }
    private URL createURl(String url)
    {
        if(TextUtils.isEmpty(url))
            return null;
        URL resultUrl = null;
        try{
            resultUrl = new URL(url);
        }catch(MalformedURLException e)
        {
            Log.e(TAG, "Cannot create a URL with the string" + url, e);
        }

        return resultUrl;
    }

    private String makeHttpRequest(URL url) throws IOException
    {
        String result = "";
        if(url == null)
            return result;

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try{

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if(urlConnection.getResponseCode() == 200)
            {
                inputStream = urlConnection.getInputStream();
                result = readFromStream(inputStream);
            }
            else
            {
                Log.e(TAG, "Error in connecting with code" + urlConnection.getResponseCode());
            }
        }catch (IOException e) {
            Log.e(TAG, "Error from Input/Output", e);
        }finally
        {
            if(urlConnection != null)
            {
                urlConnection.disconnect();
            }
            if(inputStream != null)
            {
                inputStream.close();
            }
        }
        return result;
    }

    private String readFromStream (InputStream inputStream) throws IOException
    {
        StringBuilder jsonResponse = new StringBuilder();
        if(inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                jsonResponse.append(line);
                line = reader.readLine();
            }
        }
        return jsonResponse.toString();

    }

    public Bitmap scaleBitmapDown(Bitmap bitmap, int maxDimension) {

        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int resizedWidth = maxDimension;
        int resizedHeight = maxDimension;

        if (originalHeight > originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = (int) (resizedHeight * (float) originalWidth / (float) originalHeight);
        } else if (originalWidth > originalHeight) {
            resizedWidth = maxDimension;
            resizedHeight = (int) (resizedWidth * (float) originalHeight / (float) originalWidth);
        } else if (originalHeight == originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = maxDimension;
        }
        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
    }

    private String convertResponseToString(BatchAnnotateImagesResponse response) {
        String message = "";
        float biggestRating = 0;
        List<AnnotateImageResponse> responses = response.getResponses();
        for(AnnotateImageResponse res : responses){
            WebDetection webDetection = res.getWebDetection();
            if (webDetection != null) {
                for(WebEntity curr : webDetection.getWebEntities()){
                    if(curr.getScore() > biggestRating){
                        biggestRating = curr.getScore();
                        message = curr.getDescription();
                    }
                }


            }
        }
        return message;
    }
}

