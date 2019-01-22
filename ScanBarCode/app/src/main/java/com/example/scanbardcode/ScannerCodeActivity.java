package com.example.scanbardcode;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
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
import org.w3c.dom.Text;


public class ScannerCodeActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    ZXingScannerView scannerView;
    private int CAMERA_PERMISSION_CODE = 23;

    private String authorName = "";
    private String titleName = "";


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

    private boolean isValid10ISBN(String isbn) {
        if (isbn.length() != 10)
            return false;

        int sum = 0;
        for (int i = 0; i < 9; i++)
        {
            int digit = isbn.charAt(i) - '0';
            if (0 > digit || 9 < digit)
                return false;
            sum += (digit * (10 - i));
        }

        char last = isbn.charAt(9);
        if (last != 'X' && (last < '0' ||
                last > '9'))
            return false;

        sum += ((last == 'X') ? 10 : (last - '0'));

        return (sum % 11 == 0);
    }

    private boolean isValid13ISBN(String isbn) {
        if (isbn.length() != 13)
            return false;

        int sumNum = 0;
        for (int i = 0; i < isbn.length(); i++) {
            // checks if digit
            if (Character.isDigit(isbn.charAt(i))) {
                if (i % 2 == 0)
                    sumNum += Integer.parseInt("" + isbn.charAt(i));
                else
                    sumNum += 3 * Integer.parseInt("" + isbn.charAt(i));
            }
        }

        return (sumNum % 10 == 0 ? true : false);
    }

    private boolean isValidISBN(String isbn){

        isbn = isbn.replace("-", "").replace(" ", "");

        if (isbn.length() == 10 || isbn.length() == 13){
            if (isbn.length() == 10 && !isValid10ISBN(isbn)){
                return false;
            }

            if (isbn.length() == 13 && !isValid13ISBN(isbn)){
                return false;
            }

            return true;
        }
        else{
            return false;
        }
    }

    @Override
    public void handleResult(Result result){
        String isbn = result.getText();
        if (isValidISBN(isbn)){
            MainActivity.resultTextView.setText(result.getText());
            onBackPressed();

            setContentView(R.layout.activity_main);
            new GoogleApiBooks(MainActivity.authorTextView, MainActivity.titleTextView, MainActivity.bookImageView).execute(isbn);
        }
        else{
            Toast toast = Toast.makeText(getApplicationContext(), "Please, scan valid ISBN", Toast.LENGTH_SHORT);
            toast.show();

            onBackPressed();
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
