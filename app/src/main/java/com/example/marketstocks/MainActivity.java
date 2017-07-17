package com.example.marketstocks;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements StocksAsyncTask.StockDownloadListener {

    ListView stockItemsList;
    MyStocksAdapter adapter;
    ArrayList<Stock> stocks;
    ArrayList<String> symbols;
    SharedPreferences sp1, sp2;
    EditText editText;

    //navigation bar variables
    DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle mToggle;

    //dialog2 texts
    TextView dialogName, dialogBID, dialogPerncentage;
    // this boolean is just for checking whether we are obtaining result for dialog or whole list
    Boolean checkForDialog = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //navigation work
        mDrawerLayout = (DrawerLayout) findViewById(R.id.activity_main);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);

        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //navigation work end

        stockItemsList = (ListView) findViewById(R.id.stocks_list);
        stocks = new ArrayList<Stock>();
        adapter = new MyStocksAdapter(this,stocks);
        stockItemsList.setAdapter(adapter);

        stockItemsList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long l) {
                AlertDialog.Builder b = new AlertDialog.Builder(MainActivity.this);
                b.setTitle("Remove?");
                b.setCancelable(false);
                b.setIcon(R.mipmap.ic_launcher);
                b.setMessage("Are you sure to delete this stock ?");
                b.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                b.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        stocks.remove(position - 1);
                        fetchData();
                        //because arraylist index starts from 0 and it was showing out of bound error
                    }
                });
                b.create().show();
                return true;
            }
        });
        // taken 4 predefined symbols if app is run for first time
        symbols = new ArrayList<String>();
        sp1 = getSharedPreferences("firstLogin",MODE_PRIVATE);
        boolean check = sp1.getBoolean("first_time",true);
        if(check){
            symbols.add("YHOO");
            symbols.add("AAPL");
            symbols.add("GOOG");
            symbols.add("MSFT");
            Toast.makeText(this,"first time!!",Toast.LENGTH_SHORT).show();
        }else{
            //fetching symbols from database if app is not run first time
            Toast.makeText(this,"not the first time!!",Toast.LENGTH_SHORT).show();
            sp1.edit().putBoolean("first_time",false);
            sp2 = getSharedPreferences("stocksList",MODE_PRIVATE);
            String stocksString = sp2.getString("stocksString","");
            String[] a = stocksString.split(":");
            for(int i = 0 ; i < a.length ; i++){
                symbols.add(a[i]);
            }
        }
        fetchData();
    }

    //navigation work

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //navigation work
        if(mToggle.onOptionsItemSelected(item)){
            return true;
        }
        //navigation work ends here

        if (item.getItemId() == R.id.menu_custom_stock) {
            AlertDialog.Builder b = new AlertDialog.Builder(this);
            b.setTitle("Enter Corporation");
            View v = getLayoutInflater().inflate(R.layout.dialog_view, null);
            editText = (EditText) v.findViewById(R.id.dialog_edit_text);
            b.setView(v);
            b.setCancelable(false);
            b.setPositiveButton("Go", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int which) {
                    if (editText.getText().toString().length() == 0) {
                        Toast.makeText(MainActivity.this, "Enter a Corporation", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    final String newItem = editText.getText().toString();

                    //dialog2
                    AlertDialog.Builder x = new AlertDialog.Builder(MainActivity.this);
                    x.setTitle(newItem);
                    View v1 = getLayoutInflater().inflate(R.layout.dialog_2_view,null);
                    x.setView(v1);
                    dialogName =(TextView) v1.findViewById(R.id.dialog2_name);
                    dialogBID =(TextView) v1.findViewById(R.id.dialog2_bid);
                    dialogPerncentage =(TextView) v1.findViewById(R.id.dialog2_percentage);

                    String urlStringShort = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.quotes%20where%20symbol%20in%20(%22"+newItem+ "%22)&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=";

                    //specifying the function that call for fetching data is done by dialog so that no change the existing list should be done
                    checkForDialog = true;
                    StocksAsyncTask task = new StocksAsyncTask();
                    task.setStockDownloadListener(MainActivity.this,checkForDialog);
                    task.execute(urlStringShort);

                    x.setCancelable(false);
                    x.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            symbols.add(newItem);
                            fetchData();
                        }
                    });
                    x.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    x.create().show();
                }
            });
            b.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int which) {
                    dialogInterface.cancel();
                }
            });
            b.create().show();
        }
        else if(item.getItemId() == R.id.menu_refresh){
            Toast.makeText(this,"refreshing list...",Toast.LENGTH_SHORT).show();
            fetchData();
        }
        return true;
    }

    private void fetchData() {
        // % 20 = space, %22 = "", %2Cis comma
        String preUrl = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.quotes%20where%20symbol%20in%20";
        String postUrl = "&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=";
        StringBuffer helper = new StringBuffer();
        helper.append("(");
        for(int i = 0 ; i < symbols.size() ; i++){
            helper.append("%22" + symbols.get(i) + "%22");
            if(i != symbols.size() - 1){
                helper.append("%2C");
            }
        }
        helper.append(")");
        String midUrl = helper.toString();
        //String midUrl = "(%22YHOO%22%2C%22AAPL%22%2C%22GOOG%22%2C%22MSFT%22)";
        //String urlString = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.quotes%20where%20symbol%20in%20(%22YHOO%22%2C%22AAPL%22%2C%22GOOG%22%2C%22MSFT%22)&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=";
        StocksAsyncTask task = new StocksAsyncTask();
        task.setStockDownloadListener(this,false);
        String urlString = preUrl + midUrl + postUrl;
        task.execute(urlString);
    }
    public void onDownloadComplete(ArrayList<Stock> stocksAnswer, boolean checkForDialog) {
        if (stocksAnswer == null) {
            return;
        }
        if (checkForDialog) {
            dialogName.setText(stocksAnswer.get(0).getName());
            dialogPerncentage.setText(stocksAnswer.get(0).getChange());
            dialogBID.setText(stocksAnswer.get(0).getBid());
        } else {
            stocks.clear();
            stocks.addAll(stocksAnswer);
            adapter.notifyDataSetChanged();
        }
    }
}
