package com.example.scanbardcode.Goodreads;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class XMLParser {

    public static GoodreadsResponse GetGoodreadsResonseFromXML(InputStream xml) {
        XmlPullParserFactory pullParserFactory;
        GoodreadsResponse goodreadsResponse = new GoodreadsResponse();

        try {
            pullParserFactory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = pullParserFactory.newPullParser();

            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(xml, null);

            goodreadsResponse =  parseXML(parser);

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            return goodreadsResponse;
        }
    }

    private static GoodreadsResponse parseXML(XmlPullParser parser) throws XmlPullParserException, IOException
    {
        GoodreadsResponse goodreadsResponse = new GoodreadsResponse();
        GoodreadsResult resultBook = null;

        boolean startReadingWork = false;
        int eventType = parser.getEventType();
        String tagName = "";

        while (eventType != XmlPullParser.END_DOCUMENT){
            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    goodreadsResponse.Results = new ArrayList();
                    break;
                case XmlPullParser.START_TAG:
                    if (!tagName.equals("Request") && !tagName.equals("authentication") && !tagName.equals("key") && !tagName.equals("method")) {
                        tagName = parser.getName();

                        if (tagName.equals("total-results")) {
                            goodreadsResponse.TotalResults = Integer.parseInt(parser.nextText());
                        } else if (tagName.equals("work")) {
                            startReadingWork = true;
                            resultBook = new GoodreadsResult();
                        } else if (tagName.equals("id") && startReadingWork) {
                            resultBook.ID = Integer.parseInt(parser.nextText());
                            startReadingWork = false;
                        } else if (tagName.equals("title")) {
                            resultBook.Title = parser.nextText();
                        } else if (tagName.equals("name")) {
                            resultBook.Authors.add(parser.nextText());
                        } else if (tagName.equals("image_url")) {
                            resultBook.URLImage = parser.nextText();
                        }
                    }
                    break;
                case XmlPullParser.END_TAG:
                    tagName = parser.getName();

                    if (tagName.equalsIgnoreCase("work") && resultBook != null) {
                        goodreadsResponse.Results.add(resultBook);
                    }
                    break;
            }
            eventType = parser.next();
        }

        return goodreadsResponse;
    }
}
