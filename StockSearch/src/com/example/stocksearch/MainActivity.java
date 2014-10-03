package com.example.stocksearch;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;

public class MainActivity extends Activity  {
	
	public String data;
	
	private ProgressDialog pDialog;
	private AutoCompleteTextView searchView;
	private Button searchButton,newsButton,fbButton;
	private static TableLayout tv;
	String item[]={"AAPL", "GOOG", "AMZN", "FB","UHID"};
	private static String toSearch;
	private TextView companyTitle;
	private TextView ltp;
	private TextView current;
	private String input;
	private String[] rowName = {"Prev Close","Open","Bid","Ask","1st Yr Target","Day Range", "52 wk Range","Volume","Avg Vol(3m)","Market Cap"};
	private ImageView chartImage;
	private static String marketCapitalization,changeInPercent,open,lastTradePriceOnly,yearLow,bid,averageDailyVolume,changeType,ask,previousClose,
	change ,oneYearTargetPrice,daysLow,volume,yearHigh,daysHigh,chart,name,symbol;
	private static Bitmap chartbmp;
	private static ArrayList<String> newsTitle;
	private static ArrayList<String> newsLinks;
	private static String APP_ID;
	private static HashMap<String, String> pairs;
	private ImageView arrowView;
	
	private Button button;
	private ArrayAdapter<String> aAdapter;
	private List<String> suggest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	
    	try {
    	       PackageInfo info = getPackageManager().getPackageInfo(
    	               "com.facebook.samples.loginhowto",
    	               PackageManager.GET_SIGNATURES);
    	       for (Signature signature : info.signatures) {
    	           MessageDigest md = MessageDigest.getInstance("SHA");
    	           md.update(signature.toByteArray());
    	           Log.d("KeyHash:", Base64.encodeToString(md.digest(),
    	        		   	Base64.DEFAULT));
    	           }
    	 } catch (NameNotFoundException e) {

    	 } catch (NoSuchAlgorithmException e) {

    	 }
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        APP_ID =  getString(R.string.app_id);
        searchView = (AutoCompleteTextView)findViewById(R.id.searchBox);
        fbButton = (Button)findViewById(R.id.fbButton);
        newsButton = (Button)findViewById(R.id.newsButton);
        companyTitle = (TextView)findViewById(R.id.companyTitle);
        ltp = (TextView)findViewById(R.id.ltp);
        current = (TextView)findViewById(R.id.current);
        tv=(TableLayout) findViewById(R.id.resultTable);
        tv.removeAllViewsInLayout();
        chartImage = (ImageView)findViewById(R.id.chartImage);
        suggest =  new ArrayList<String>();
        button = (Button) findViewById(R.id.searchButton);
        arrowView = (ImageView)findViewById(R.id.arrowImage);
		
        
        
        searchView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    
                    String searchText = parent.getItemAtPosition(position).toString();
                    String[] splits = searchText.split(",");
                    //Toast.makeText(getBaseContext(), "Position clicked: " + splits[0], Toast.LENGTH_SHORT).show();
                    searchView.setText(splits[0]);
                    
                    
                    button.performClick();
                   
                    
                    
            }
      });
        
       // searchView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, item));
        searchView.addTextChangedListener(new TextWatcher(){
        	 
			public void afterTextChanged(Editable editable) {
				// TODO Auto-generated method stub
 
			}
 
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub
 
			}
 
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String newText = s.toString();
				new getJSON().execute(newText);
			}
 
        });
        
        
        
        
        fbButton.setOnClickListener(new OnClickListener() {
        	
        	

			@Override
			public void onClick(View view) {
				
				
				Dialog d= createPublishDialog(null);
				d.show();
			
				
			}
			
			

			private Dialog createPublishDialog(Bundle savedInstanceState) {
				final CharSequence[] items = {"Post Stock Data", "Cancel"};
			    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
			    builder.setTitle("Post to Facebook");
			    builder.setItems(items, new DialogInterface.OnClickListener() {
			               public void onClick(DialogInterface dialog, int which) {
			               // The 'which' argument contains the index position
			               // of the selected item


			    if(which==0){ 
			    	
			         com.facebook.Session.openActiveSession(MainActivity.this, true,new com.facebook.Session.StatusCallback() {

						@SuppressWarnings("deprecation")
						@Override
					public void call(com.facebook.Session session,SessionState state,Exception exception) {
					       if (session.isOpened()) {
					        com.facebook.Request.executeMeRequestAsync(session,
					        new com.facebook.Request.GraphUserCallback(){
		
					        	@Override
									public void onCompleted(GraphUser user,	Response response) {
									// TODO Auto-generated method stub
										if(user!=null){
										
											publishFeedDialog();
											//Toast.makeText(getApplicationContext(), "Publishing", Toast.LENGTH_SHORT).show();
							
										}
									}

								private void publishFeedDialog() {
									// TODO Auto-generated method stub
									Bundle params = new Bundle();
									params.putString("name", name);
									params.putString("caption", "Stock Information for " + name + "(" + symbol +")");
									params.putString("description","Last Trade Price:"+ lastTradePriceOnly + " Change:" + changeType + " " + change + "(" + changeInPercent + ")" );
									params.putString("picture",chart);
									params.putString("link", "http://finance.yahoo.com/q?s=" + symbol);
								    
								    
								    //params.putString("action","[{name: 'Look at Details' link: " + link+"}]");


								    WebDialog feedDialog = (
								        new WebDialog.FeedDialogBuilder(MainActivity.this,
								            com.facebook.Session.getActiveSession(),
								            params))
								        .setOnCompleteListener(new OnCompleteListener() {
								        	
								        	 @Override
								            public void onComplete(Bundle values,
								                FacebookException error) {
								        		
								                if (error == null) {
								                    // When the story is posted, echo the success
								                    // and the post Id.
								                    final String postId = values.getString("post_id");
								                    if (postId != null) {
								                        Toast.makeText(MainActivity.this,
								                            "Posted story, id: "+postId,
								                            Toast.LENGTH_LONG).show();
								                    } else {
								                        // User clicked the Cancel button

								Toast.makeText(MainActivity.this.getApplicationContext(),
								                            "Publish cancelled",
								                            Toast.LENGTH_LONG).show();
								                    }
								                } else if (error instanceof
								FacebookOperationCanceledException) {
								                    // User clicked the "x" button
								                    Toast.makeText(MainActivity.this.getApplicationContext(),
								                        "Publish cancelled",
								                        Toast.LENGTH_SHORT).show();
								                } else {
								                    // Generic, ex: network error
								                    Toast.makeText(MainActivity.this.getApplicationContext(),
								                        "Error posting story",
								                        Toast.LENGTH_SHORT).show();
								                }
								            }

								        })
								        .build();
								    feedDialog.show();
								}
					        });
					       }
		
					}
			   });
			   
			   
			              }
			    

			              else {
			              Toast.makeText(getApplicationContext(), "Publish cancelled", Toast.LENGTH_SHORT).show();

			              }
			           }
			    });
			    return builder.create();
				
			}

			
        });
        
        
     
        
        
        
        
        
        
       
        
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
     super.onActivityResult(requestCode, resultCode, data);
     com.facebook.Session.getActiveSession().onActivityResult(MainActivity.this,
    requestCode, resultCode, data);

    }
    
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		
		
		

		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				
				toSearch = searchView.getText().toString();
				if(toSearch != null && !toSearch.isEmpty()){
					
					
					Pattern pattern = Pattern.compile("\\s");
					Matcher matcher = pattern.matcher(toSearch);
					boolean found = matcher.find();
					if(found == true){
						AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
						builder.setMessage("Invalid Input")
						       .setCancelable(false)
						       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
						           public void onClick(DialogInterface dialog, int id) {
						                //do things
						           }
						       });
						AlertDialog alert = builder.create();
						alert.show();
						
					}else{
						List<String> inputList = Arrays.asList(toSearch.split(","));
						new fetchData().execute();
						
					}
					
					
				}else{
					AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
					builder.setMessage("Invalid Input")
					       .setCancelable(false)
					       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
					           public void onClick(DialogInterface dialog, int id) {
					                //do things
					           }
					       });
					AlertDialog alert = builder.create();
					alert.show();
				}
				
				
				
				
			}

		});
		
		
			
			
			
			
			
			
			

		
		
		
		
		newsButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				Log.i("Data",newsTitle.toString());
				Intent myIntent = new Intent(MainActivity.this, NewsActivity.class);
				myIntent.putExtra("News", newsTitle); //Optional parameters
				myIntent.putExtra("Links", newsLinks);
				
				MainActivity.this.startActivity(myIntent);
				
			}

		});
		
		
		
		return true;
	}
    
    
    
    
    //This is the class to load the data for the autocompletetextview
    //This class extends the Asynctask 
    
    
   
    
    
    
    
    //This is the class to lead the data from the Java Servlet 
    // It extends Asyntask
    class fetchData extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(MainActivity.this);
			pDialog.setMessage("Loading Stock Data");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		/**
		 * getting Inbox JSON
		 * */
		@TargetApi(Build.VERSION_CODES.KITKAT) @SuppressWarnings("deprecation")
		protected String doInBackground(String... args) {
			// Building Parameters

			input = getUpComingMovies();
			
			try {
				
				JSONObject results = new JSONObject(input);
				
				//Name and Symbol
				JSONObject result = results.getJSONObject("result");
				name = result.getString("Name");
				symbol = result.getString("Symbol");
				
				//Quotes 
				JSONObject quote = result.getJSONObject("Quote");
				
				marketCapitalization = quote.getString("MarketCapitalization");
				changeInPercent = quote.getString("ChangeInPercent");
				open = quote.getString("Open");
				lastTradePriceOnly = quote.getString("LastTradePriceOnly");
	            yearLow = quote.getString("YearLow");
	            bid = quote.getString("Bid");
	            averageDailyVolume = quote.getString("AverageDailyVolume");
	            changeType = quote.getString("ChangeType");
	            ask = quote.getString("Ask");
	            previousClose = quote.getString("PreviousClose");
	            change = quote.getString("Change");
	            oneYearTargetPrice = quote.getString("OneYearTargetPrice");
	            daysLow = quote.getString("DaysLow");
	            volume = quote.getString("Volume");
	            yearHigh = quote.getString("YearHigh");
	            daysHigh = quote.getString("DaysHigh");
	            chart = result.getString("StockChartImageUrl");
	            
	            JSONObject  news = results.getJSONObject("News");
	            JSONArray items = news.getJSONArray("Item");
	            newsTitle = new ArrayList<String>();
	            newsLinks = new ArrayList<String>();
	            
	            for(int i=0;i<items.length();i++){
	            	JSONObject item;
	            	item = items.getJSONObject(i);
	            	newsTitle.add(item.getString("Title"));
	            	newsLinks.add(item.getString("Link"));
	            }
	            
	            pairs = new HashMap<String,String>();
	            pairs.put("Prev Close", previousClose);
	            pairs.put("Open", open);
	            pairs.put("Bid", bid);
	            pairs.put("Ask", ask);
	            pairs.put("1st Yr Target", oneYearTargetPrice);
	            pairs.put("Day Range", daysLow + "-" + daysHigh);
	            pairs.put("52 wk Range", yearLow + "-" + yearHigh);
	            pairs.put("Volume", volume);
	            pairs.put("Avg Vol(3m)", averageDailyVolume );
	            pairs.put("Market Cap", marketCapitalization);
	            
	            
	            
	            
	            
	            
	            
	            
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			
			    try {
			            HttpClient client = new DefaultHttpClient();
			            URI imageURI = new URI(chart);
			            HttpGet req = new HttpGet();
			            req.setURI(imageURI);
			            HttpResponse response = client.execute(req); 
			            chartbmp =BitmapFactory.decodeStream(response.getEntity().getContent());//BitmapFactory decodes the InputStream of the HttpResponse
			       } catch (URISyntaxException e) {        //catch those exceptions
			           Log.e("ERROR",e.getMessage());
			       } catch (ClientProtocolException e) {
			           Log.e("ERROR",e.getMessage());
			       } catch (IllegalStateException e) {
			           Log.e("ERROR",e.getMessage());
			       } catch (IOException e) {
			           Log.e("ERROR",e.getMessage());
			       }
			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		@Override
		protected void onPostExecute(String file_url) {
			// dismiss the dialog after getting all products
			pDialog.dismiss();
			// updating UI from Background Thread
			
				if(change.equals("")){
					companyTitle.setText("");
					ltp.setText("");
					current.setText("");
					tv.removeAllViewsInLayout();
					newsButton.setVisibility(View.INVISIBLE);
					fbButton.setVisibility(View.INVISIBLE);
					chartImage.setImageBitmap(null);
					arrowView.setImageBitmap(null);
					
					AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
					builder.setMessage("No Stock Information Available")
					       .setCancelable(false)
					       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
					           public void onClick(DialogInterface dialog, int id) {
					                //do things
					           }
					       });
					AlertDialog alert = builder.create();
					alert.show();
					
				}else{
		            companyTitle.setText(name + "(" + symbol + ")");
		            ltp.setText(lastTradePriceOnly);
		            current.setText(change + "(" + changeInPercent +"%)");
		            
		            if(changeType.endsWith("-")){
		            	arrowView.setImageResource(R.drawable.downsame);
		            	current.setTextColor(Color.parseColor("#FF0000"));
		            	
		            }else{
		            	arrowView.setImageResource(R.drawable.upsame);
		            	current.setTextColor(Color.parseColor("#92CC47"));
		             
		            }
		            
		            
		            Log.i("Data",chart);
		            tv.removeAllViewsInLayout();
		            
	            
		            Iterator iter = pairs.keySet().iterator();
		            while(iter.hasNext()) {
		                String key = (String)iter.next();
		                String val = (String)pairs.get(key);
		                
		                TableRow tr=new TableRow(MainActivity.this);
	                    tr.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
	                    
	                    TextView b=new TextView(MainActivity.this);
	                    String str= key;
	                    b.setText(str);
	                    b.setTextColor(Color.BLACK);
	                    b.setTextSize(15);
	                    b.setTextAlignment(5);
	                    tr.addView(b);
	                   


	                    TextView b1=new TextView(MainActivity.this);
	                    b1.setPadding(10, 0, 0, 0);
	                    b1.setTextSize(15);
	                    String str1= yearLow;
	                    b1.setText(val);
	                    b1.setTextColor(Color.BLACK);
	                    b.setTextAlignment(6);
	                    tr.addView(b1);
	                    tv.addView(tr);
		               
		            }
	            
	           
	            
		            chartImage.setImageBitmap(chartbmp);
		            fbButton.setVisibility(View.VISIBLE);
					}
		            if(newsTitle.size() > 0){
		            	newsButton.setVisibility(View.VISIBLE);
		            }else{
		            	newsButton.setVisibility(View.INVISIBLE);
		            }
		            
					Log.i("Data",marketCapitalization);
			
			
			
			
		}

	}
    
    
    class getJSON extends AsyncTask<String,String,String>{
   	 
    	@Override
    	protected String doInBackground(String... key) {
    		
    		String newText = key[0];
    		newText = newText.trim();
    		newText = newText.replaceAll("\\s", "");
    		
    		StringBuilder builder = new StringBuilder();
    		HttpClient client = new DefaultHttpClient();
    		String requestUrl = "http://autoc.finance.yahoo.com/autoc?query="+newText+"&callback=YAHOO.Finance.SymbolSuggest.ssCallback";
    		HttpGet httpGet = new HttpGet(requestUrl);
    		
    		
    		try{
    			HttpResponse response = client.execute(httpGet);
    			StatusLine statusLine = response.getStatusLine();
    			int statusCode = statusLine.getStatusCode();
    			if (statusCode == 200) {
    				HttpEntity entity = response.getEntity();
    				InputStream content = entity.getContent();
    				BufferedReader reader = new BufferedReader(new InputStreamReader(content,"utf-8"));
    				String line;
    				while ((line = reader.readLine()) != null) {
    					builder.append(line);
    				}
    				
    				String input = builder.toString();
    				String json = input.substring(input.indexOf("(") + 1, input.lastIndexOf(")"));
 
    				
    				JSONObject results = new JSONObject(json);
    				JSONObject resultset = results.getJSONObject("ResultSet");
    				JSONArray resultsNames = resultset.getJSONArray("Result");
    				
    				
    				suggest.clear();
    				
    				for(int i=0;i<resultsNames.length();i++){
    					JSONObject resultObj = resultsNames.getJSONObject(i);
    					String symbol = resultObj.getString("symbol");
    					String name = resultObj.getString("name");
    					String exch = resultObj.getString("exch");
    					String item = symbol +", " + name + ", " + "(" + exch + ")";
    					suggest.add(item);
    				}
    				
    				//Log.i("yahoodata", resultsNames.toString());
    				
    			} else {
    				Log.e(MainActivity.class.toString(), "Failed to download file");
    			}
	    			
     
    		}catch(Exception e){
    			Log.w("Error", e.getMessage());
    		}
    		
    		
    		runOnUiThread(new Runnable(){
    			public void run(){
    				 aAdapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.listitem,suggest);
    		         searchView.setAdapter(aAdapter);
    				 aAdapter.notifyDataSetChanged();
    			}
    		});
    		
    		Log.i("Autocomplete",newText);
     
    		return null;
    	}
     
       }
    

    
    
    
    
    
    public String getUpComingMovies() {
		StringBuilder builder = new StringBuilder();
		HttpClient client = new DefaultHttpClient();
		String requestUrl = "http://cs-server.usc.edu:29206/examples/servlet/Hw8Servlet?q=" + toSearch;
		HttpGet httpGet = new HttpGet(requestUrl);
		Log.e(MainActivity.class.toString(), requestUrl);
		try {
			HttpResponse response = client.execute(httpGet);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			if (statusCode == 200) {
				HttpEntity entity = response.getEntity();
				InputStream content = entity.getContent();
				BufferedReader reader = new BufferedReader(new InputStreamReader(content,"utf-8"));
				String line;
				while ((line = reader.readLine()) != null) {
					builder.append(line);
				}
				
				//Log.e(MainActivity.class.toString(), builder.toString());
			} else {
				Log.e(MainActivity.class.toString(), "Failed to download file");
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return builder.toString();
	}


	


	

}
