package com.example.scanbardcode;

import android.net.Uri;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
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
        String result = "";

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
        JSONObject jsonObject = null;

        try {
            jsonObject = new JSONObject(bookInfo);
            String totalItemsCount = jsonObject.getString("totalItems");

            //TODO: There are better ways of doing it, parsin the JSON.
            boolean hasImage =  bookInfo.contains("\"imageLinks\":");
            boolean hasAuthor = bookInfo.contains("\"authors\":");
            boolean hastTitle = bookInfo.contains("\"title\":");

            boolean foundInGoogleBooksByISBN = !totalItemsCount.equals("0");
            boolean foundInGoodReadsApiByISBN = false;

            if (!foundInGoogleBooksByISBN || (!hasImage || !hasAuthor || !hastTitle)){
                bookInfo = getBookInfoByGoodreadsApi(isbn);
                foundInGoodReadsApiByISBN = !bookInfo.contains("<total-results>0</total-results>");

                if (foundInGoodReadsApiByISBN) {
                    hasImage = !bookInfo.contains("assets/nophoto/");
                    hasAuthor = bookInfo.contains("<author>");
                    hastTitle = bookInfo.contains("<title>");
                }

                if (!foundInGoogleBooksByISBN && !foundInGoodReadsApiByISBN) {
                    bookInfo = getBookInfoByCuspideHTML(isbn);

                    if (bookInfo.isEmpty()) {
                        //I try to find the book by q=ISBN:<ISBN> in GoogleBooksApi, is the ISBN as a phrase
                        bookInfo = getBookInfoByGoogleApi(isbn, true);
                    }
                }
                else if (foundInGoodReadsApiByISBN && (!hasImage || !hasAuthor || !hastTitle)){
                    bookInfo = getBookInfoByCuspideHTML(isbn);

                    if (bookInfo.isEmpty()) {
                        //I try to find the book by q=ISBN:<ISBN> in GoogleBooksApi, is the ISBN as a phrase
                        bookInfo = getBookInfoByGoogleApi(isbn, true);
                    }
                }
                else if (foundInGoogleBooksByISBN && !foundInGoodReadsApiByISBN && (!hasImage || !hasAuthor || !hastTitle)){
                    bookInfo = getBookInfoByCuspideHTML(isbn);

                    if (bookInfo.isEmpty()) {
                        //I try to find the book by q=ISBN:<ISBN> in GoogleBooksApi, is the ISBN as a phrase
                        bookInfo = getBookInfoByGoogleApi(isbn, true);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        finally{



            return bookInfo;
        }
    }
}
