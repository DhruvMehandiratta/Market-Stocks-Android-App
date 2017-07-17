package com.example.marketstocks;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Dhruv on 12-03-2017.
 */

public class StocksAsyncTask extends AsyncTask<String,Void,ArrayList<Stock>> {

    StockDownloadListener listener;
    boolean checkForDialog;
    void setStockDownloadListener(StockDownloadListener listener, boolean checkForDialog){
        this.listener = listener;
        this.checkForDialog = checkForDialog;
    }

    protected ArrayList<Stock> doInBackground(String... params) {


        String urlString = params[0];
        StringBuffer stringBuffer = new StringBuffer();

        try {
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            InputStream inputStream = urlConnection.getInputStream();
            if (inputStream == null) {
                return null;
            }

            Scanner s = new Scanner(inputStream);
            while (s.hasNext()) {
                stringBuffer.append(s.nextLine());
            }

        } catch (MalformedURLException e) {
            return null;
        } catch (IOException e) {

        }

        return parseCourseList(stringBuffer.toString());

    }

    private ArrayList<Stock> parseCourseList(String json) {

        try {
            JSONObject object = new JSONObject(json);
            JSONObject query = object.getJSONObject("query");
            JSONObject results = query.getJSONObject("results");
            JSONArray quote = results.getJSONArray("quote");

            ArrayList<Stock> stocks = new ArrayList<>();
            for (int i = 0; i < quote.length(); i++) {
                JSONObject firstElement = quote.getJSONObject(i);
                String symbol = firstElement.getString("symbol");
                String Bid = firstElement.getString("Bid");
                String name = firstElement.getString("Name");
                String Change_PercentChange = firstElement.getString("Change_PercentChange");
                Stock s = new Stock(Bid,symbol,Change_PercentChange,name);
                stocks.add(s);
            }
            return stocks;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;

    }

//
//    @Override
//    protected void onPostExecute(ArrayList<Course> courses) {
//        super.onPostExecute(courses);
//        if (listener != null)
//            listener.onDownloadComplete(courses);
//        // we should pass this list to CourseListActivity
//    }

    public interface StockDownloadListener {
        void onDownloadComplete(ArrayList<Stock> stocks, boolean checkForDialog);
    }
    protected void onPostExecute(ArrayList<Stock> stocks){
        super.onPostExecute(stocks);
        if(listener != null){
            listener.onDownloadComplete(stocks,checkForDialog);

        }
    }


}
