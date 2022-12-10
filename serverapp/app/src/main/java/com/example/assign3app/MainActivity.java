package com.example.assign3app;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
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


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//public class MainActivity extends AppCompatActivity implements WifiP2pManager.PeerListListener {
public class MainActivity extends AppCompatActivity {

    private static final int STORAGE_PERMISSION_CODE = 101;
    private static final int CAMERA_REQUEST_CODE = 102;
    private static final int ACCESS_WIFI_CODE = 103;
    String imagePath;
    private TextView Predictionview = null;
    private ImageView imageview = null;

    /*
    WifiP2pManager manager;
    WifiP2pManager.Channel channel;
    BroadcastReceiver receiver;
    IntentFilter intentFilter;
    List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();
    */


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button capture_image_btn = (Button) findViewById(R.id.capture_image_btn);
        Button exit_btn = (Button) findViewById(R.id.exit_btn);
        Button prdictions_btn = (Button) findViewById(R.id.pred_button);
        Predictionview = (TextView) findViewById(R.id.prediction_view);
        imageview = (ImageView) findViewById(R.id.imageView);

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
                                                 @Override
                                                 public void onClick(View view) {

                                                     DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                                         @Override
                                                         public void onClick(DialogInterface dialog, int which) {
                                                             switch (which) {
                                                                 case DialogInterface.BUTTON_POSITIVE:
                                                                     //Yes button clicked
                                                                     if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                                                             == PackageManager.PERMISSION_DENIED) {
                                                                         // Permission is not granted
                                                                         ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
                                                                     } else {
                                                                         captureImage();
                                                                     }
                                                                     break;

                                                                 case DialogInterface.BUTTON_NEGATIVE:
                                                                     //No button clicked
                                                                     break;
                                                             }
                                                         }
                                                     };

                                                     AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                                                     builder.setMessage(R.string.dialog_Message).setPositiveButton("Yes", dialogClickListener)
                                                             .setNegativeButton("No", dialogClickListener).show();
                                                 }
                                             }
        );

        prdictions_btn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            String Url = getString(R.string.file_server_pred);
                                            RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
                                            StringRequest stringRequest = new StringRequest(Request.Method.POST, Url, new Response.Listener<String>(){
                                                @Override
                                                public void onResponse(String s) {

                                                    Toast.makeText(MainActivity.this,  " Predictions Received Successfully ",
                                                                Toast.LENGTH_LONG).show();

                                                    String[] separated = s.split("\n");
                                                    float [] predictions = new float[10];
                                                    for (String prediction_str : separated)
                                                    {
                                                        String[] values = prediction_str.split(",");
                                                        for (int i = 0; i < 10; i++)
                                                        {
                                                            predictions[i] += Float.parseFloat(values[i]);
                                                        }
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

                                                }},
                                                    new Response.ErrorListener(){
                                                        @Override
                                                        public void onErrorResponse(VolleyError volleyError) {
                                                            Toast.makeText(MainActivity.this, "Some error occurred -> "+volleyError, Toast.LENGTH_LONG).show();;
                                                        }
                                                    }) {
                                                @Nullable
                                                @Override
                                                protected Map<String, String> getParams() throws AuthFailureError {
                                                    Map<String, String> params = new HashMap<>();
                                                    return params;
                                                }
                                            };
                                            requestQueue.add(stringRequest);

                                        }



                                    }
        );

        /*
        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);
        receiver = new WiFiDirectBroadcastReceiver(manager, channel, this);

        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_WIFI_STATE)
                == PackageManager.PERMISSION_DENIED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_WIFI_STATE}, ACCESS_WIFI_CODE);
        } else {
            manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    Toast.makeText(MainActivity.this, "Peers discovered", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(int reasonCode) {
                    Toast.makeText(MainActivity.this, "No Peers discovered", Toast.LENGTH_LONG).show();
                }
            });

        }


         */
    }
/*
    // register the broadcast receiver with the intent values to be matched
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, intentFilter);
    }

    // unregister the broadcast receiver
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    @Override
    public void onPeersAvailable(WifiP2pDeviceList peerList) {
        Toast.makeText(MainActivity.this, "Peers Available", Toast.LENGTH_LONG).show();

        List<WifiP2pDevice> peersReceived = new ArrayList<WifiP2pDevice>();

//        peers.clear();
        peersReceived.addAll(peerList.getDeviceList());

        if (peersReceived.size() == 0) {
            System.out.println("No devices found");
        } else {   //Connect to the peers
            for (WifiP2pDevice device : peersReceived) {
            WifiP2pConfig config = new WifiP2pConfig();
            config.deviceAddress = device.deviceAddress;

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            manager.connect(channel, config, new WifiP2pManager.ActionListener() {

                @Override
                public void onSuccess() {
                    Toast.makeText(MainActivity.this, "Peers Connect Success", Toast.LENGTH_LONG).show();
                    peers.add(device);
                }

                @Override
                public void onFailure(int reason) {
                    //failure logic
                    Toast.makeText(MainActivity.this, "Peers Connect Failed", Toast.LENGTH_LONG).show();
                }
            });
        }
        }
    }
*/

    void captureImage() {
        Intent captureImageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (captureImageIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File imageFile = null;
            try {
                SimpleDateFormat s = new SimpleDateFormat("ddMMyyyyhhmmss");
                String currentDate = s.format(new Date());
                imageFile = File.createTempFile("CapturedImage_" + currentDate + "_",  ".jpg",
                        getExternalFilesDir(Environment.DIRECTORY_PICTURES));
                imagePath = imageFile.getAbsolutePath();
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
            }
            if (imageFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider3",
                        imageFile);
                captureImageIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(captureImageIntent, CAMERA_REQUEST_CODE);
            }
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE)
        {
            File photoFile = new File(imagePath);
            Uri photoURI = FileProvider.getUriForFile(MainActivity.this,
                    "com.example.android.fileprovider3",
                    photoFile);
            Bitmap image = null;
            try {
                image = MediaStore.Images.Media.getBitmap(getContentResolver(), photoURI);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Matrix matrix = new Matrix();
            //matrix.postRotate(270);
            matrix.postRotate(90);
            Bitmap rotatedImg = Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), matrix, true);
            image.recycle();

            //Convert to grey scale
            rotatedImg = convertToGrey(rotatedImg);
            //Resize as per model requirement
            rotatedImg = Bitmap.createScaledBitmap(rotatedImg, 28, 28, false);
            //Display the Image
            imageview.setImageBitmap(rotatedImg);

            //converting image to base64 string
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            //bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            rotatedImg.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageBytes = baos.toByteArray();
            final String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);

            String Url = getString(R.string.file_server);
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Url, new Response.Listener<String>(){
                @Override
                public void onResponse(String s) {
                    if(s.equals("Success")){
                        Toast.makeText(MainActivity.this, photoFile.getName()
                                        + " Uploaded Successfully ",
                                Toast.LENGTH_LONG).show();
                        Predictionview.setText("Prediction : Unknown");
                    }
                    else{
                        Toast.makeText(MainActivity.this, "Some error occurred!", Toast.LENGTH_LONG).show();
                    }
                }},
                    new Response.ErrorListener(){
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            Toast.makeText(MainActivity.this, "Some error occurred -> "+volleyError, Toast.LENGTH_LONG).show();;
                        }
                    }) {
                @Nullable
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put(getString(R.string.image_key), imageString);
                    params.put("filename", photoFile.getName());
                    return params;
                }
            };
            requestQueue.add(stringRequest);

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


}