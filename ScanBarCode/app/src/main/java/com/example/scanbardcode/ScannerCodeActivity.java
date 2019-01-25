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
            Toast toast = Toast.makeText(getApplicationContext(), "Please, scan valid ISBN", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        scannerView.stopCamera();
        progress.dismiss();
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
        progress.setMessage("Buscando el libro en Google Books y en Goodreads");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();

        return new BookLoader(this, bundle.getString("queryString"));
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String s) {
        try {
            MainActivity.authorTextView.setText("");
            MainActivity.titleTextView.setText("");
            MainActivity.bookImageView.setImageResource(android.R.color.transparent);

            if (!s.contains("GoodreadsResponse")) {
                JSONObject jsonObject = new JSONObject(s);
                JSONArray itemsArray = jsonObject.getJSONArray("items");

                for (int i = 0; i < itemsArray.length(); i++) {
                    JSONObject book = itemsArray.getJSONObject(i);
                    JSONObject volumeInfo = book.getJSONObject("volumeInfo");

                    try {
                        JSONArray authors = volumeInfo.getJSONArray("authors");
                        String authorNames = "";

                        for (int j = 0; j < authors.length(); j++) {
                            if (j == 0) {
                                authorNames += authors.getString(j).replace("\"", "");
                            } else {
                                authorNames += ", " + authors.getString(j);
                            }
                        }

                        MainActivity.authorTextView.setText(authorNames);
                        MainActivity.titleTextView.setText(volumeInfo.getString("title"));

                        JSONObject imageLinks = volumeInfo.getJSONObject("imageLinks");
                        Picasso.get().load(imageLinks.getString("thumbnail")).into(MainActivity.bookImageView);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            else{
                InputStream xmlStream = new ByteArrayInputStream(s.getBytes());
                GoodreadsResponse goodreadsResponse = XMLParser.GetGoodreadsResonseFromXML(xmlStream);

                if (goodreadsResponse.TotalResults != 0) {
                    MainActivity.authorTextView.setText(goodreadsResponse.Results.get(0).Authors.get(0));
                    MainActivity.titleTextView.setText(goodreadsResponse.Results.get(0).Title);
                    Picasso.get().load(goodreadsResponse.Results.get(0).URLImage).into(MainActivity.bookImageView);
                }
                else{
                    MainActivity.authorTextView.setText("No results found");
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        onBackPressed();
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {
    }
}
