package com.example.scanbardcode;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

public class BookLoader extends AsyncTaskLoader<String> {
    private String isbn;
    private boolean isSecondCall;

    public BookLoader(Context context, String queryString, boolean isSecondCall){
        super(context);
        this.isbn = queryString;
        this.isSecondCall = isSecondCall;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public String loadInBackground() {
        if (isSecondCall){
            return NetworkUtils.getBookInfoByGoodreadsApi(isbn);
        }
        else {
            return NetworkUtils.getBookInfoByGoogleApi(isbn);
        }
    }
}
