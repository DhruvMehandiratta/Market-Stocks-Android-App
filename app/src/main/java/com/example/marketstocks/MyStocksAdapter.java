package com.example.marketstocks;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Dhruv on 12-03-2017.
 */

public class MyStocksAdapter extends ArrayAdapter<Stock> {

    Context mContext;
    ArrayList<Stock> mStocks;
    public MyStocksAdapter(Context context, ArrayList<Stock> objects){
        super(context,0,objects);
        mContext = context;
        mStocks = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       if(convertView == null){
            convertView = View.inflate(mContext,R.layout.list_item,null);
       }
        TextView BidTextView = (TextView) convertView.findViewById(R.id.bid);
        TextView symbolTextView =  (TextView) convertView.findViewById(R.id.symbol);
        TextView changeTextView = (TextView) convertView.findViewById(R.id.change);
        TextView nameTextView = (TextView) convertView.findViewById(R.id.name);
        Stock stock = mStocks.get(position);
        BidTextView.setText(stock.getBid());
        symbolTextView.setText(stock.getSymbol());
        changeTextView.setText(stock.getChange());
        nameTextView.setText(stock.getName());
        return convertView;
    }

    @Nullable
    @Override
    public Stock getItem(int position) {
        return super.getItem(position);
    }

}
