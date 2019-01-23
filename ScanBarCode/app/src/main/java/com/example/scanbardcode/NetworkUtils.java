package com.example.scanbardcode;

import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

public class NetworkUtils {

    private static String TAG = "NetworkUtils";
    private static String BOOK_BASE_URL = "https://www.googleapis.com/books/v1/volumes?";
    private static String QUERY_PARAM = "q";

    static  String getBookInfo(String isbn){
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String bookJSONString = null;


        try {

            Uri builtURi = Uri.parse(BOOK_BASE_URL).buildUpon()
                    .appendQueryParameter(QUERY_PARAM, "isbn:" + isbn).build();

            URL requestURL = new URL(builtURi.toString());

            urlConnection = (HttpURLConnection) requestURL.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null){
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;

            while((line = reader.readLine()) != null){
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0){
                return null;
            }

            bookJSONString = buffer.toString();

        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        finally {
            if (urlConnection != null){
                urlConnection.disconnect();
            }

            if (reader != null){
                try{
                    reader.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

            Log.d(TAG, bookJSONString);
            return bookJSONString;
        }

    }

}
