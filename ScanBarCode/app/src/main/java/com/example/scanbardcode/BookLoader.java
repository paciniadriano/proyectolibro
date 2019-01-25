package com.example.scanbardcode;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;

public class BookLoader extends AsyncTaskLoader<String> {
    private String isbn;
    private boolean isSecondCall;

    public BookLoader(@NonNull Context context, String queryString/*, boolean isSecondCall*/){
        super(context);
        this.isbn = queryString;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Nullable
    @Override
    public String loadInBackground() {
        return NetworkUtils.getBookInfo(isbn);
    }
}
