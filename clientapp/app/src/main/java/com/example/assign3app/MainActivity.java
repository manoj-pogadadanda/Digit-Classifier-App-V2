package com.example.assign3app;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.assign3app.ml.TfClassifierQ1;
import com.example.assign3app.ml.TfClassifierQ2;
import com.example.assign3app.ml.TfClassifierQ3;
import com.example.assign3app.ml.TfClassifierQ4;

import org.json.JSONArray;
import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;


public class MainActivity extends AppCompatActivity {

    private static final int STORAGE_PERMISSION_CODE = 101;
    private static final int CAMERA_REQUEST_CODE = 102;
    private static final int QUADRANT_CODE = 1;
    String imagePath;
    private TextView Predictionview = null;
    private ImageView imageview = null;
    private Bitmap Receivedimage = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button capture_image_btn = (Button) findViewById(R.id.connect_btn);
        Button exit_btn = (Button) findViewById(R.id.exit_btn);
        TextView Quadrantview = (TextView) findViewById(R.id.Quadrant_view);
        Predictionview = (TextView) findViewById(R.id.prediction_view);
        imageview = (ImageView) findViewById(R.id.imageView);

        if (QUADRANT_CODE == 1)
            Quadrantview.setText(R.string.quadrant1);
        else if (QUADRANT_CODE == 2)
            Quadrantview.setText(R.string.quadrant2);
        else if (QUADRANT_CODE == 3)
            Quadrantview.setText(R.string.quadrant3);
        else if (QUADRANT_CODE == 4)
            Quadrantview.setText(R.string.quadrant4);

        Predictionview.setText("Prediction : Unknown");
        exit_btn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    switch (which) {
                                                        case DialogInterface.BUTTON_POSITIVE:
                                                            //Yes button clicked
                                                            finish();
                                                            System.exit(0);
                                                            break;

                                                        case DialogInterface.BUTTON_NEGATIVE:
                                                            //No button clicked
                                                            break;
                                                    }
                                                }
                                            };

                                            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                                            builder.setMessage(R.string.exit_Message).setPositiveButton("Yes", dialogClickListener)
                                                    .setNegativeButton("No", dialogClickListener).show();
                                        }
                                    }
        );

        capture_image_btn.setOnClickListener(new View.OnClickListener() {
                                                 @RequiresApi(api = Build.VERSION_CODES.N)
                                                 @Override
                                                 public void onClick(View view) {
                                                     receiveImage();
                                                 }
                                             }
        );

    }

    void sendPredictions(float[] predictions) {
            //converting image to base64 string
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            String s = "";
            for (int i=0; i<10; i++) {
                if (i==9)
                    s += predictions[i] + "\n";
                else
                    s += predictions[i] + ",";
            }
            final String predictionString = s;

            String Url = getString(R.string.file_server_pred);
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Url, new Response.Listener<String>() {
                @Override
                public void onResponse(String s) {
                    if (s.equals("Success")) {
                        Toast.makeText(MainActivity.this,  " Predictions Uploaded Successfully ",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Some error occurred!", Toast.LENGTH_LONG).show();
                    }
                }
            },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            Toast.makeText(MainActivity.this, "Some error occurred -> " + volleyError, Toast.LENGTH_LONG).show();
                            ;
                        }
                    }) {
                @Nullable
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put(getString(R.string.pred_key), predictionString);
                    return params;
                }
            };
            requestQueue.add(stringRequest);





    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    float[] processImage(Bitmap image) {

        float[] predictions = null;
        try {
            //Convert to grey scale
            image = convertToGrey(image);
            //Resize as per model requirement
            image = Bitmap.createScaledBitmap(image, 28, 28, false);

            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 14, 14}, DataType.FLOAT32);
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * 14 * 14);
            byteBuffer.order(ByteOrder.nativeOrder());

            // Runs model inference and gets result.
            if (QUADRANT_CODE == 1) {
                //Cut the image
                image = Bitmap.createBitmap(image, 0,0,14,14);
                //Display the Image
                imageview.setImageBitmap(image);

                preprocessImage(image, byteBuffer);

                inputFeature0.loadBuffer(byteBuffer);

                TfClassifierQ1 modelQ1 = TfClassifierQ1.newInstance(getApplicationContext());
                TfClassifierQ1.Outputs outputs = modelQ1.process(inputFeature0);
                TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();
                predictions = outputFeature0.getFloatArray();
                // Releases model resources if no longer used.
                modelQ1.close();
            }
            else if (QUADRANT_CODE == 2) {
                //Cut the image
                image = Bitmap.createBitmap(image, 14,0,14,14);
                //Display the Image
                imageview.setImageBitmap(image);
                preprocessImage(image, byteBuffer);

                inputFeature0.loadBuffer(byteBuffer);
                TfClassifierQ2 modelQ2 = TfClassifierQ2.newInstance(getApplicationContext());
                TfClassifierQ2.Outputs outputs = modelQ2.process(inputFeature0);
                TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();
                predictions = outputFeature0.getFloatArray();
                // Releases model resources if no longer used.
                modelQ2.close();
            }
            else if (QUADRANT_CODE == 3) {
                //Cut the image
                image = Bitmap.createBitmap(image, 0,14,14,14);
                //Display the Image
                imageview.setImageBitmap(image);
                preprocessImage(image, byteBuffer);

                inputFeature0.loadBuffer(byteBuffer);
                TfClassifierQ3 modelQ3 = TfClassifierQ3.newInstance(getApplicationContext());
                TfClassifierQ3.Outputs outputs = modelQ3.process(inputFeature0);
                TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();
                predictions = outputFeature0.getFloatArray();
                // Releases model resources if no longer used.
                modelQ3.close();
            }
            else if (QUADRANT_CODE == 4) {
                //Cut the image
                image = Bitmap.createBitmap(image, 14,14,14,14);
                //Display the Image
                imageview.setImageBitmap(image);
                preprocessImage(image, byteBuffer);

                inputFeature0.loadBuffer(byteBuffer);
                TfClassifierQ4 modelQ4 = TfClassifierQ4.newInstance(getApplicationContext());
                TfClassifierQ4.Outputs outputs = modelQ4.process(inputFeature0);
                TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();
                predictions = outputFeature0.getFloatArray();
                // Releases model resources if no longer used.
                modelQ4.close();
            }

            int prediction = 0;
            float maxprob = 0;
            for (int i = 0; i < 10; i++) {
                if (predictions[i] > maxprob)
                {
                    maxprob = predictions[i];
                    prediction = i;
                }
            }
            Predictionview.setText("Prediction : " + prediction);
 //           Toast.makeText(MainActivity.this, "Image processed : " + predictions, Toast.LENGTH_LONG).show();
            //}
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return predictions;
    }


    private void preprocessImage(Bitmap image, ByteBuffer byteBuffer)
    {
        int[] intValues = new int[14 * 14];
        float[] floatValues = new float[14 * 14];
        image.getPixels(intValues, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());

        int max = intValues[0];
        int min = intValues[0];
        for (int intValue : intValues) {
            if (intValue > max) {
                max = intValue;
            }
            if (intValue < min) {
                min = intValue;
            }
        }
        int pixel = 0;
        for (int i = 0; i < 14; i++) {
            for (int j = 0; j < 14; j++) {
                float val = intValues[pixel];
                val = (val-min)/(max-min);
                floatValues[pixel] = val;
                pixel++;
            }
        }
        float maxfloat = floatValues[0];
        for (float floatValue : floatValues) {
            if (floatValue > maxfloat) {
                maxfloat = floatValue;
            }
        }

        //Reverse the image
        pixel = 0;
        float floataverage = 0;
        for (int i = 0; i < 14; i++) {
            for (int j = 0; j < 14; j++) {
                floatValues[pixel] = maxfloat - floatValues[pixel];
                floataverage += floatValues[pixel];
                pixel++;
            }
        }
        floataverage = floataverage/(14*14);

        pixel = 0;
        for (int i = 0; i < 14; i++) {
            for (int j = 0; j < 14; j++) {
                if (floatValues[pixel] < floataverage*1.25)
                    floatValues[pixel] = 0;
                else
                    floatValues[pixel] = (float) 0.28163262805318545;
                byteBuffer.putFloat(floatValues[pixel++]);
            }
        }

    }


    private Bitmap convertToGrey(Bitmap image) {
        int width, height;
        height = image.getHeight();
        width = image.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(image, 0, 0, paint);
        return bmpGrayscale;
    }



    private void receiveImage()
    {
        try {
            String Url = getString(R.string.file_server_get);
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Url, new Response.Listener<String>() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onResponse(String s) {
                    byte[] decoded_data = android.util.Base64.decode(s, android.util.Base64.DEFAULT);
                    Receivedimage = BitmapFactory.decodeByteArray(decoded_data, 0, decoded_data.length);
                    float [] predictions = processImage(Receivedimage);
                    sendPredictions(predictions);

                }
            },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            Toast.makeText(MainActivity.this, "Some error occurred -> " + volleyError, Toast.LENGTH_LONG).show();
                            ;
                        }
                    }) {
                @Nullable
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("filename", "latest.jpg");
                    return params;
                }
            };
            requestQueue.add(stringRequest);
        }
        catch (Exception e)
        {
            Toast.makeText(MainActivity.this, " Exception ", Toast.LENGTH_LONG).show();
        }
    }

}