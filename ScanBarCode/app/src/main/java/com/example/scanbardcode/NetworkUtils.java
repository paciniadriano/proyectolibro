package com.example.scanbardcode;

import android.net.Uri;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


//Goodreads api key and secret
//key: fNJa78fYU1bjRJHohMJmQ
//secret: yb6KtcyKDaeRMcyOCcE1LPjRdiKzTngRFKhu8nAgvM
//https://www.goodreads.com/api/index
//Way of use: https://www.goodreads.com/search/index.xml?key=fNJa78fYU1bjRJHohMJmQ&q=9789877382259  and returns an XML  (9789877382259 is the ISBN, but it could be author name or title)

public class NetworkUtils {

    private static String GOOGLE_API_BASE_URL = "https://www.googleapis.com/books/v1/volumes?";
    private static String GOODREADS_API_BASE_URL = "https://www.goodreads.com/search/index.xml?";

    private static String QUERY_PARAM = "q";

    static String getBookInfoByGoogleApi(String isbn){
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String bookJSONString = null;

        try {
            Uri builtURi = Uri.parse(GOOGLE_API_BASE_URL).buildUpon()
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

            return bookJSONString;
        }
    }

    public static String getBookInfoByGoodreadsApi(String isbn) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String bookXMLString = null;

        try {
            Uri builtURi = Uri.parse(GOODREADS_API_BASE_URL).buildUpon()
                    .appendQueryParameter("key", "fNJa78fYU1bjRJHohMJmQ")
                    .appendQueryParameter(QUERY_PARAM, isbn).build();

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

            bookXMLString = buffer.toString();
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

            return bookXMLString;
        }
    }
}
