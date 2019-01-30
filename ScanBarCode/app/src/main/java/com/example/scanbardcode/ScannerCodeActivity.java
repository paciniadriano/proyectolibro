package com.example.scanbardcode;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;
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
import com.example.scanbardcode.Goodreads.GoodreadsResponse;
import com.example.scanbardcode.Goodreads.XMLParser;
import com.google.zxing.Result;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class ScannerCodeActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String>, ZXingScannerView.ResultHandler{
    ZXingScannerView scannerView;
    private int CAMERA_PERMISSION_CODE = 23;
    private String authorName = "";
    private String titleName = "";
    private ProgressDialog progress = null;

    public static boolean isLoaderRunning = false;

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

        Loader<String> loader =getSupportLoaderManager().getLoader(0);
        if (loader != null){
            getSupportLoaderManager().initLoader(0, null, this);
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

            Bundle queryBundle = new Bundle();
            queryBundle.putString("queryString", isbn);
            getSupportLoaderManager().restartLoader(0, queryBundle, this);
        }
        else{
            Toast toast = Toast.makeText(getApplicationContext(), "Please, retry the scanning", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        scannerView.stopCamera();
        if (progress != null) {
            progress.dismiss();
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        scannerView.setResultHandler(this);
        scannerView.startCamera();
    }

    @NonNull
    @Override
    public Loader<String> onCreateLoader(int i, @Nullable Bundle bundle) {
        progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("Searching the book");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();

        return new BookLoader(this, bundle.getString("queryString"));
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String s) {

        MainActivity.authorTextView.setText("");
        MainActivity.titleTextView.setText("");
        MainActivity.bookImageView.setImageResource(android.R.color.transparent);

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(s);

            String author = jsonObject.getString("author");
            String title = jsonObject.getString("title");

            MainActivity.authorTextView.setText(author.equals("") ? "Not found" : author);
            MainActivity.titleTextView.setText(title.equals("") ? "Not found" : title);

            if (!jsonObject.getString("img").isEmpty()) {
                Picasso.get().load(jsonObject.getString("img")).into(MainActivity.bookImageView);
            }
            else{
                String noCoverAvailable = "https://vignette.wikia.nocookie.net/shadowhunter/images/d/dd/Muestralibro.png/revision/latest?cb=20160630172813&path-prefix=es";
                Picasso.get().load(noCoverAvailable).into(MainActivity.bookImageView);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        onBackPressed();
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {
    }
}
