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
        //this.isSecondCall = isSecondCall;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Nullable
    @Override
    public String loadInBackground() {

        String jsonOrXMLResult = "";
//
//        if (isSecondCall){
//            return NetworkUtils.getBookInfoByGoodreadsApi(isbn);
//        }
//        else {
//            return NetworkUtils.getBookInfoByGoogleApi(isbn);
//        }

        return NetworkUtils.getBookInfo(isbn);

            //return jsonOrXMLResult.toString();
        //}

//        return "{\n" +
//                " \"kind\": \"books#volumes\",\n" +
//                " \"totalItems\": 1,\n" +
//                " \"items\": [\n" +
//                "  {\n" +
//                "   \"kind\": \"books#volume\",\n" +
//                "   \"id\": \"hcUoAQAAIAAJ\",\n" +
//                "   \"etag\": \"ME+4Ss2KxI0\",\n" +
//                "   \"selfLink\": \"https://www.googleapis.com/books/v1/volumes/hcUoAQAAIAAJ\",\n" +
//                "   \"volumeInfo\": {\n" +
//                "    \"title\": \"Poesía (1955-1972)\",\n" +
//                "    \"authors\": [\n" +
//                "     \"Alejandra Pizarnik\"\n" +
//                "    ],\n" +
//                "    \"publisher\": \"Lumen Editorial\",\n" +
//                "    \"publishedDate\": \"2003\",\n" +
//                "    \"description\": \"\\\"Presentamos la obra poetica completa de una de las escritoras argentinas mas emblematicas de la segunda mitad de siglo: la controvertida, polemica y malograda Alejandra Pizarnik, una leyenda de las letras hispanas, figura de culto en vida, un autentico mito.\\\"\",\n" +
//                "    \"industryIdentifiers\": [\n" +
//                "     {\n" +
//                "      \"type\": \"OTHER\",\n" +
//                "      \"identifier\": \"UCSC:32106011244735\"\n" +
//                "     }\n" +
//                "    ],\n" +
//                "    \"readingModes\": {\n" +
//                "     \"text\": false,\n" +
//                "     \"image\": false\n" +
//                "    },\n" +
//                "    \"pageCount\": 470,\n" +
//                "    \"printType\": \"BOOK\",\n" +
//                "    \"categories\": [\n" +
//                "     \"Literary Collections\"\n" +
//                "    ],\n" +
//                "    \"maturityRating\": \"NOT_MATURE\",\n" +
//                "    \"allowAnonLogging\": false,\n" +
//                "    \"contentVersion\": \"1.2.2.0.preview.0\",\n" +
//                "    \"imageLinks\": {\n" +
//                "     \"smallThumbnail\": \"http://books.google.com/books/content?id=hcUoAQAAIAAJ&printsec=frontcover&img=1&zoom=5&source=gbs_api\",\n" +
//                "     \"thumbnail\": \"http://books.google.com/books/content?id=hcUoAQAAIAAJ&printsec=frontcover&img=1&zoom=1&source=gbs_api\"\n" +
//                "    },\n" +
//                "    \"language\": \"es\",\n" +
//                "    \"previewLink\": \"http://books.google.com.ar/books?id=hcUoAQAAIAAJ&dq=isbn:9788426428257&hl=&cd=1&source=gbs_api\",\n" +
//                "    \"infoLink\": \"http://books.google.com.ar/books?id=hcUoAQAAIAAJ&dq=isbn:9788426428257&hl=&source=gbs_api\",\n" +
//                "    \"canonicalVolumeLink\": \"https://books.google.com/books/about/Poes%C3%ADa_1955_1972.html?hl=&id=hcUoAQAAIAAJ\"\n" +
//                "   },\n" +
//                "   \"saleInfo\": {\n" +
//                "    \"country\": \"AR\",\n" +
//                "    \"saleability\": \"NOT_FOR_SALE\",\n" +
//                "    \"isEbook\": false\n" +
//                "   },\n" +
//                "   \"accessInfo\": {\n" +
//                "    \"country\": \"AR\",\n" +
//                "    \"viewability\": \"NO_PAGES\",\n" +
//                "    \"embeddable\": false,\n" +
//                "    \"publicDomain\": false,\n" +
//                "    \"textToSpeechPermission\": \"ALLOWED\",\n" +
//                "    \"epub\": {\n" +
//                "     \"isAvailable\": false\n" +
//                "    },\n" +
//                "    \"pdf\": {\n" +
//                "     \"isAvailable\": false\n" +
//                "    },\n" +
//                "    \"webReaderLink\": \"http://play.google.com/books/reader?id=hcUoAQAAIAAJ&hl=&printsec=frontcover&source=gbs_api\",\n" +
//                "    \"accessViewStatus\": \"NONE\",\n" +
//                "    \"quoteSharingAllowed\": false\n" +
//                "   },\n" +
//                "   \"searchInfo\": {\n" +
//                "    \"textSnippet\": \"&quot;Presentamos la obra poetica completa de una de las escritoras argentinas mas emblematicas de la segunda mitad de siglo: la controvertida, polemica y malograda Alejandra Pizarnik, una leyenda de las letras hispanas, figura de culto en vida, ...\"\n" +
//                "   }\n" +
//                "  }\n" +
//                " ]\n" +
//                "}\n";
    }
}
