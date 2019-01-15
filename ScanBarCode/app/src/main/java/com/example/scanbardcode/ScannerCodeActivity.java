package com.example.scanbardcode;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.Result;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

import org.json.JSONArray;
import org.json.JSONObject;


public class ScannerCodeActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    ZXingScannerView scannerView;
    private int CAMERA_PERMISSION_CODE = 23;

    private boolean isCameraAccessAllowed() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
    }


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        scannerView = new ZXingScannerView(this);
        setContentView(scannerView);

        if (!isCameraAccessAllowed()) {
            requestCameraPermission();
        }
    }

    private boolean isValidISBN(String isbn){
        if (isbn.length() >= 10 && isbn.length() <= 13){
            return true;
        }
        else{
            return false;
        }
    }

    @Override
    public void handleResult(Result result){
        if (isValidISBN(result.getText())){
            MainActivity.resultTextView.setText(result.getText());
            onBackPressed();

            RequestQueue queue = Volley.newRequestQueue(this);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, "https://www.googleapis.com/books/v1/volumes?q=" + result.getText(), null, new Response.Listener<JSONObject>()
            {
                @Override
                public void onResponse(JSONObject response)
                {
                    try
                    {
                        JSONArray items = response.getJSONArray("items");
                        JSONObject firstItem = (JSONObject)items.get(0);

                        Toast toast = Toast.makeText(getApplicationContext(),firstItem.getString("title") + firstItem.getString(("authors")), Toast.LENGTH_LONG);
                        toast.show();
                    }
                    catch(Exception e)
                    {
                        Toast toast = Toast.makeText(getApplicationContext(),"Failed to load URL with that code", Toast.LENGTH_LONG);
                        toast.show();
                        finish();
                    }
                }
            }, new Response.ErrorListener()
            {
                @Override
                public void onErrorResponse(VolleyError error)
                {
                    Toast toast = Toast.makeText(getApplicationContext(),"Error to load URL with that code", Toast.LENGTH_LONG);
                    toast.show();
                }
            });

            queue.add(jsonObjectRequest);
        }
        else{
            Toast toast = Toast.makeText(getApplicationContext(), "Please, scan again", Toast.LENGTH_SHORT);
            toast.show();
        }
    }


    @Override
    public void onPause(){
        super.onPause();
        scannerView.stopCamera();
    }

    @Override
    public void onResume(){
        super.onResume();
        scannerView.setResultHandler(this);
        scannerView.startCamera();
    }
}
