package com.nguyen_nguyen.foodentification;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;




public class MainActivity extends AppCompatActivity {
    private static final int CAMERA_REQUEST = 1888;
    public static String ID = MainActivity.class.getSimpleName();
    private String mCurrentPhotoPath;
    private Button takePic;
    private Button sendPic;
    private ImageView imageView;
    private Uri photoUri;
    private boolean pictureTaken = false;
    private Bitmap rotatedBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView) findViewById(R.id.image_view);

        takePic = (Button) findViewById(R.id.btn_takepicture);
        sendPic = (Button) findViewById(R.id.btn_sendpicture);
        takePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if(cameraIntent.resolveActivity(getPackageManager()) != null) {
                    File photoFile = null;
                    try{
                        photoFile = createImageFile();
                    }catch(IOException ex)
                    {
                        Log.e("photoFile", "Error Creating Photo File");
                    }

                    if(photoFile!= null) {
                        photoUri = FileProvider.getUriForFile(getApplicationContext(),
                                        "com.nguyen_nguyen.foodentification", photoFile);
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                        startActivityForResult(cameraIntent, CAMERA_REQUEST);
                    }
                }
            }
        });

        sendPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(pictureTaken)
                {
                    Intent i = new Intent(MainActivity.this, DisplayRecipe.class);
                    i.putExtra(ID, photoUri);
                    startActivity(i);
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {

            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
            }catch(IOException ex)
            {
                Log.e("Bitmap", "Cannot Create Bitmap");
                return;
            }
            ExifInterface ei = null;
            try{
             ei = new ExifInterface(mCurrentPhotoPath);
            }
            catch (IOException ex){
                Log.e("Bitmap", "Cannot Create Bitmap ei");
                return;
            }

            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);


            switch(orientation) {

                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotatedBitmap = rotateImage(bitmap, 90);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotatedBitmap = rotateImage(bitmap, 180);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotatedBitmap = rotateImage(bitmap, 270);
                    break;

                case ExifInterface.ORIENTATION_NORMAL:
                default:
                    rotatedBitmap = bitmap;
            }
            imageView.setImageBitmap(rotatedBitmap);
            galleryAddPic();
            pictureTaken = true;

        }
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }


}