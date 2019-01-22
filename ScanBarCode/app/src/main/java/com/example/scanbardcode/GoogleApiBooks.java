package com.example.scanbardcode;

import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.JsonArrayRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

public class GoogleApiBooks extends AsyncTask<String, Void, String> {

    public TextView author;
    public TextView title;
    public ImageView bookImage;

     public GoogleApiBooks(TextView author, TextView title, ImageView bookImage){
        this.author = author;
        this.title = title;
        this.bookImage = bookImage;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        try {
            author.setText("");
            title.setText("");
            bookImage.setImageResource(android.R.color.transparent);

            JSONObject jsonObject = new JSONObject(s);

            String totalItemsCount = jsonObject.getString("totalItems");

            if (totalItemsCount.equals("0")){
                author.setText("No hay datos");
                return;
            }

            JSONArray itemsArray = jsonObject.getJSONArray("items");

            for (int i = 0; i < itemsArray.length(); i++) {
                JSONObject book = itemsArray.getJSONObject(i);
                JSONObject volumeInfo = book.getJSONObject("volumeInfo");

                try {
                    JSONArray authors = volumeInfo.getJSONArray("authors");
                    String authorNames = "";

                    for (int j = 0; j < authors.length(); j++) {
                        if (j == 0){
                            authorNames += authors.getString(j).replace("\"", "");
                        }
                        else{
                            authorNames += ", " + authors.getString(j);
                        }
                    }

                    author.setText(authorNames);
                    title.setText(volumeInfo.getString("title"));

                    JSONObject imageLinks = volumeInfo.getJSONObject("imageLinks");
                    Picasso.get().load(imageLinks.getString("thumbnail")).into(bookImage);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected String doInBackground(String... strings) {
        return NetworkUtils.getBookInfo(strings[0]);
    }
}
