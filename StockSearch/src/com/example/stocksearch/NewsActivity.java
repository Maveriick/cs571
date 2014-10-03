package com.example.stocksearch;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class NewsActivity extends Activity {
	static final String[] FRUITS = new String[] { "Apple", "Avocado", "Banana",
		"Blueberry", "Coconut", "Durian", "Guava", "Kiwifruit",
		"Jackfruit", "Mango", "Olive", "Pear", "Sugar-apple" };
	ListView mylistview;
	ArrayAdapter<String> listAdapter;
	final Context context = this;
	private static ArrayList<String> news;
	private static ArrayList<String> links;
	private String s;
	

		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.news_layout);
			mylistview = (ListView) findViewById(R.id.listView1);
			
		
			
			Intent iin= getIntent();
	        news = iin.getExtras().getStringArrayList("News");
	            Log.i("DatainNew",news.toString());
	         links = iin.getExtras().getStringArrayList("Links");
	            
	        
	            		
	        listAdapter = new ArrayAdapter<String>(NewsActivity.this, R.layout.listitem,news);
	        mylistview.setAdapter(listAdapter);
	        Toast.makeText(getApplicationContext(), "Showing " + news.size() + " headlines", Toast.LENGTH_LONG).show();
	        
	        mylistview.setOnItemClickListener(new OnItemClickListener() {

	            @Override
	            public void onItemClick(AdapterView<?> arg0, View arg1,
	                    int position, long arg3) {
	                s = links.get(position);
	               // Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
	                
	                
	                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
	        				context);
	         
	        			// set title
	        			alertDialogBuilder.setTitle("View News");
	         
	        			// set dialog message
	        			alertDialogBuilder
	        				.setCancelable(false)
	        				.setPositiveButton("View",new DialogInterface.OnClickListener() {
	        					public void onClick(DialogInterface dialog,int id) {
	        						Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(s));
	        						startActivity(browserIntent);
	        						//NewsActivity.this.finish();
	        					}
	        				  })
	        				.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
	        					public void onClick(DialogInterface dialog,int id) {
	        						// if this button is clicked, just close
	        						// the dialog box and do nothing
	        						dialog.cancel();
	        					}
	        				});
	         
	        				// create alert dialog
	        				AlertDialog alertDialog = alertDialogBuilder.create();
	         
	        				// show it
	        				alertDialog.show();
	                
	                
	                
	            }
	        });

	        
	        
	       
		
		}

}
