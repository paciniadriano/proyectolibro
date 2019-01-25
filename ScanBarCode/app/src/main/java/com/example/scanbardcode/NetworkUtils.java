package com.example.scanbardcode;

import android.net.Uri;

import com.example.scanbardcode.Goodreads.GoodreadsResponse;
import com.example.scanbardcode.Goodreads.XMLParser;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


//Goodreads api key and secret
//key: fNJa78fYU1bjRJHohMJmQ
//secret: yb6KtcyKDaeRMcyOCcE1LPjRdiKzTngRFKhu8nAgvM
//https://www.goodreads.com/api/index
//Way of use: https://www.goodreads.com/search/index.xml?key=fNJa78fYU1bjRJHohMJmQ&q=9789877382259  and returns an XML  (9789877382259 is the ISBN, but it could be author name or title)

public class NetworkUtils {

    private static String GOOGLE_API_BASE_URL = "https://www.googleapis.com/books/v1/volumes?";
    private static String GOODREADS_API_BASE_URL = "https://www.goodreads.com/search/index.xml?";

    //And idea is to use jsoup to parse this HTML from Cuspide and get the Author, Title and Image of the book
    private static String CUSPIDE_BASE_URL = "https://www.cuspide.com/resultados.aspx?";


    private static String QUERY_PARAM = "q";

    private static String getBookInfoByGoogleApi(String isbn, boolean isISBNPhrase){
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String bookJSONString = null;

        String isbnQuery = ((isISBNPhrase) ? "ISBN:" : "isbn:");

        try {

            Uri builtURi = Uri.parse(GOOGLE_API_BASE_URL).buildUpon()
                    .appendQueryParameter(QUERY_PARAM, isbnQuery + isbn).build();

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

    private static String getBookInfoByGoodreadsApi(String isbn) {
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

    public static String getBookInfoByCuspideHTML(String isbn){
        String result = "{}";

        try {
            Document jsoupDOC = Jsoup.connect(CUSPIDE_BASE_URL + "c=" + isbn).get();
            Elements imgs = jsoupDOC.select("img[src*=https://www.cuspide.com/content/cover/]");
            Elements authors = jsoupDOC.select("a[href*=AutorEstricto]");
            Elements titles = jsoupDOC.select("a[href*=/Libro/" + isbn  + "]");

            if (authors.size() > 0) {
                Element imgBook = imgs.get(0);
                Element author = authors.get(0);
                Element title = titles.get(0);

                String authorTxt = author.text();
                String titleTxt = title.attr("title");
                String imgSrc = imgBook.attr("src");

                String googleBooksTemplateForCuspide = "{ \"totalItems\": 1, \"items\" : [{ \"volumeInfo\": { \"title\": \"<TITLE>\", \"authors\": [ \"<AUTHOR>\" ], \"imageLinks\": { \"thumbnail\": \"<THUMBNAIL>\"  } } }] }";

                googleBooksTemplateForCuspide = googleBooksTemplateForCuspide.replace("<AUTHOR>", authorTxt);
                googleBooksTemplateForCuspide = googleBooksTemplateForCuspide.replace("<TITLE>", titleTxt);
                googleBooksTemplateForCuspide = googleBooksTemplateForCuspide.replace("<THUMBNAIL>", imgSrc);

                result = googleBooksTemplateForCuspide;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            return result;
        }
    }


    //TODO: I have to refactor all this code to obtain independently the author, title and image, without losing any of the three
    //To do this I have to get the code from the ScannerCodeActivity.onFinishLoad and put it right here
    //And then I have to return only the three elements that I want in a json format. To parse it in the oonFinishLoad method
    public static String getBookInfo(String isbn) {
        String bookInfoByGoogleApi = getBookInfoByGoogleApi(isbn, false);
        String bookInfo = bookInfoByGoogleApi;

        String authorTxt = "";
        String titleTxt = "";
        String bookImgTxt = "";

        JSONObject jsonObject = null;

        try {
            jsonObject = new JSONObject(bookInfo);

            if (jsonObject.has("items")) {
                JSONArray itemsArray = jsonObject.getJSONArray("items");

                //I only get the first option
                JSONObject book = itemsArray.getJSONObject(0);
                JSONObject volumeInfo = book.getJSONObject("volumeInfo");

                String authorNames = "";

                if (volumeInfo.has("authors")) {
                    JSONArray authors = volumeInfo.getJSONArray("authors");

                    for (int j = 0; j < authors.length(); j++) {
                        if (j == 0) {
                            authorNames += authors.getString(j).replace("\"", "");
                        } else {
                            authorNames += ", " + authors.getString(j);
                        }
                    }
                }
                authorTxt = authorNames;

                if (volumeInfo.has("title")) {
                    titleTxt = volumeInfo.getString("title");
                }

                if (volumeInfo.has("imageLinks")) {
                    JSONObject imageLinks = volumeInfo.getJSONObject("imageLinks");
                    bookImgTxt = imageLinks.getString("thumbnail");
                }
            }

            //I have to find this info in the next API provider. In this case: Goodreads
            if (authorTxt.isEmpty() || titleTxt.isEmpty() || bookImgTxt.isEmpty()){
                bookInfo = getBookInfoByGoodreadsApi(isbn);

                InputStream xmlStream = new ByteArrayInputStream(bookInfo.getBytes());
                GoodreadsResponse goodreadsResponse = XMLParser.GetGoodreadsResonseFromXML(xmlStream);

                if (goodreadsResponse.TotalResults != 0) {
                    authorTxt = authorTxt.isEmpty() ? goodreadsResponse.Results.get(0).Authors.get(0) : authorTxt;
                    titleTxt = titleTxt.isEmpty() ? goodreadsResponse.Results.get(0).Title : titleTxt;

                    if (bookImgTxt.isEmpty() && !goodreadsResponse.Results.get(0).URLImage.contains("assets/nophoto/")) {
                        bookImgTxt = goodreadsResponse.Results.get(0).URLImage;
                    }
                }

                if (authorTxt.isEmpty() || titleTxt.equals("unknown") || titleTxt.isEmpty() || bookImgTxt.isEmpty()){
                    bookInfo = getBookInfoByCuspideHTML(isbn);

                    jsonObject = new JSONObject(bookInfo);

                    if (jsonObject.has("items")) {
                        JSONArray itemsArray = jsonObject.getJSONArray("items");

                        //I only get the first option
                        JSONObject book = itemsArray.getJSONObject(0);
                        JSONObject volumeInfo = book.getJSONObject("volumeInfo");

                        //Cuspide is better for getting the correct name. If I already called Cuspide then I seize the author field
                        String authorNames = "";

                        if (volumeInfo.has("authors")) {
                            JSONArray authors = volumeInfo.getJSONArray("authors");

                            for (int j = 0; j < authors.length(); j++) {
                                if (j == 0) {
                                    authorNames += authors.getString(j).replace("\"", "");
                                } else {
                                    authorNames += ", " + authors.getString(j);
                                }
                            }
                        }
                        authorTxt = authorNames;


                        if (titleTxt.isEmpty() || titleTxt.equals("unknown")) {
                            titleTxt = volumeInfo.getString("title");
                        }

                        if (bookImgTxt.isEmpty()) {
                            JSONObject imageLinks = volumeInfo.getJSONObject("imageLinks");
                            bookImgTxt = imageLinks.getString("thumbnail");
                        }
                    }
                }

                if (authorTxt.isEmpty() || titleTxt.isEmpty() || bookImgTxt.isEmpty()) {
                    //I try to find the book by q=ISBN:<ISBN> in GoogleBooksApi, is the ISBN as a phrase
                    bookInfo = getBookInfoByGoogleApi(isbn, true);

                    jsonObject = new JSONObject(bookInfo);

                    if (jsonObject.has("items")) {
                        JSONArray itemsArray = jsonObject.getJSONArray("items");

                        //I only get the first option
                        JSONObject book = itemsArray.getJSONObject(0);
                        JSONObject volumeInfo = book.getJSONObject("volumeInfo");

                        if (volumeInfo.has("authors") && authorTxt.isEmpty()) {
                            String authorNames = "";

                            if (volumeInfo.has("authors")) {
                                JSONArray authors = volumeInfo.getJSONArray("authors");

                                for (int j = 0; j < authors.length(); j++) {
                                    if (j == 0) {
                                        authorNames += authors.getString(j).replace("\"", "");
                                    } else {
                                        authorNames += ", " + authors.getString(j);
                                    }
                                }
                            }
                            authorTxt = authorNames;
                        }

                        if (volumeInfo.has("title") && titleTxt.isEmpty()) {
                            titleTxt = volumeInfo.getString("title");
                        }

                        if (volumeInfo.has("imageLinks") && bookImgTxt.isEmpty()) {
                            JSONObject imageLinks = volumeInfo.getJSONObject("imageLinks");
                            bookImgTxt = imageLinks.getString("thumbnail");
                        }
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        finally {
            return "{ \"author\" : \"" + authorTxt + "\", \"title\" : \"" + titleTxt + "\", \"img\" : \"" + bookImgTxt + "\" }";
        }
    }
}
