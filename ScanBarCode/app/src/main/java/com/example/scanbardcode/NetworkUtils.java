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
import java.lang.reflect.Array;
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
    private static String AMAZON_BASE_URL = "https://www.amazon.com/s/ref=nb_sb_noss?";
    private static String CUSPIDE_BASE_URL = "https://www.cuspide.com/resultados.aspx?";

    //Para buscar en AMAZON por ISBN!!
    //https://www.amazon.com/s/ref=nb_sb_noss?field-keywords=9789870407195


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


    private static String getBookInfoByAmazonHTML(String isbn){
        String result = "{}";

        try {
            Document jsoupDOC = Jsoup.connect(AMAZON_BASE_URL + "field-keywords=" + isbn).get();
            Elements liFirstBook = jsoupDOC.select("li[id=result_0]");

            if (liFirstBook.size() > 0){
                Elements imgsBook = liFirstBook.get(0).select("img[src*=https://images-na.ssl-images-amazon.com/images/]");
                Elements authors = liFirstBook.get(0).select(".a-col-right .a-row:eq(1) span.a-size-small:eq(1)");
                Elements titles = jsoupDOC.select("h2.s-access-title");

                if (authors.size() > 0) {
                    Element imgBook = imgsBook.get(0);
                    Element author = authors.get(0);
                    Element title = titles.get(0);

                    String authorTxt = author.text();
                    String titleTxt = title.text();
                    String imgSrc = imgBook.attr("src");

                    if (imgSrc.contains("no-img")){
                        imgSrc = "";
                    }

                    String googleBooksTemplateForAmazon = "{ \"totalItems\": 1, \"items\" : [{ \"volumeInfo\": { \"title\": \"<TITLE>\", \"authors\": [ \"<AUTHOR>\" ], \"imageLinks\": { \"thumbnail\": \"<THUMBNAIL>\"  } } }] }";

                    googleBooksTemplateForAmazon = googleBooksTemplateForAmazon.replace("<AUTHOR>", authorTxt);
                    googleBooksTemplateForAmazon = googleBooksTemplateForAmazon.replace("<TITLE>", titleTxt);
                    googleBooksTemplateForAmazon = googleBooksTemplateForAmazon.replace("<THUMBNAIL>", imgSrc);

                    result = googleBooksTemplateForAmazon;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            return result;
        }
    }

    private static ArrayList<String> getJSONObjectByBookInfo(String bookInfo, String authorTxt, String  titleTxt, String  bookImgTxt){
        JSONObject jsonObject = null;
        ArrayList<String> results = new ArrayList<>();

        try {
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
                    results.add(authorNames);
                }
                else{
                    results.add("");
                }

                if ((volumeInfo.has("title") || volumeInfo.has("unknown")) && titleTxt.isEmpty()) {
                    results.add(volumeInfo.getString("title"));
                }
                else{
                    results.add("");
                }

                if (volumeInfo.has("imageLinks") && bookImgTxt.isEmpty()) {
                    JSONObject imageLinks = volumeInfo.getJSONObject("imageLinks");
                    results.add(imageLinks.getString("thumbnail"));
                }
                else{
                    results.add("");
                }
            }
            else{
                results.add(authorTxt);
                results.add(titleTxt);
                results.add(bookImgTxt);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        finally {
            return results;
        }
    }

    private static boolean missingSomeDataItem(String authorTxt, String titleTxt, String bookImgTxt){
        return (authorTxt.isEmpty() || titleTxt.isEmpty() || titleTxt.equals("unknown") || bookImgTxt.isEmpty());
    }

    //TODO: I have to refactor all this code to obtain independently the author, title and image, without losing any of the three
    //To do this I have to get the code from the ScannerCodeActivity.onFinishLoad and put it right here
    //And then I have to return only the three elements that I want in a json format. To parse it in the oonFinishLoad method
    public static String getBookInfo(String isbn) {
        String bookInfo = getBookInfoByGoogleApi(isbn, false);
        String authorTxt = "";
        String titleTxt = "";
        String bookImgTxt = "";

        ArrayList<String> resultsBookInfo = getJSONObjectByBookInfo(bookInfo, authorTxt, titleTxt, bookImgTxt);
        authorTxt = resultsBookInfo.get(0);
        titleTxt = resultsBookInfo.get(1);
        bookImgTxt = resultsBookInfo.get(2);

        //I have to find this info in the next API provider or Webpage. In this case: Amazon web
        if (missingSomeDataItem(authorTxt, titleTxt, bookImgTxt)) {
            bookInfo = getBookInfoByAmazonHTML(isbn);

            resultsBookInfo = getJSONObjectByBookInfo(bookInfo, authorTxt, titleTxt, bookImgTxt);
            authorTxt = resultsBookInfo.get(0);
            titleTxt = resultsBookInfo.get(1);
            bookImgTxt = resultsBookInfo.get(2);

            //I have to find this info in the next API provider. In this case: Goodreads
            if (missingSomeDataItem(authorTxt, titleTxt, bookImgTxt)) {
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

                //I have to find this info in the next API provider or Webpage. In this case: Cuspide web
                if (missingSomeDataItem(authorTxt, titleTxt, bookImgTxt)) {
                    bookInfo = getBookInfoByCuspideHTML(isbn);

                    resultsBookInfo = getJSONObjectByBookInfo(bookInfo, authorTxt, titleTxt, bookImgTxt);
                    authorTxt = resultsBookInfo.get(0);
                    titleTxt = resultsBookInfo.get(1);
                    bookImgTxt = resultsBookInfo.get(2);
                }

                //I have to find this info in the next API provider. In this case: GoogleBooks with the ISBN as a phrase
                if (missingSomeDataItem(authorTxt, titleTxt, bookImgTxt)) {
                    bookInfo = getBookInfoByGoogleApi(isbn, true);

                    resultsBookInfo = getJSONObjectByBookInfo(bookInfo, authorTxt, titleTxt, bookImgTxt);
                    authorTxt = resultsBookInfo.get(0);
                    titleTxt = resultsBookInfo.get(1);
                    bookImgTxt = resultsBookInfo.get(2);
                }
            }
        }

        return "{ \"author\" : \"" + authorTxt + "\", \"title\" : \"" + titleTxt + "\", \"img\" : \"" + bookImgTxt + "\" }";
    }
}
