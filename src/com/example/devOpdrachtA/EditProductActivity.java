package com.example.devOpdrachtA;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.androidhive.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class EditProductActivity extends Activity {

	TextView txtName;
	TextView txtPrice;
	TextView txtDesc;
	TextView txtUrl;
	EditText txtCreatedAt;
	Button btnSave;
	Button btnDelete;

	String pid;

	// Progress Dialog
	private ProgressDialog pDialog;

	// JSON parser class
	JSONParser jsonParser = new JSONParser();

	// single product url
	private static final String url_product_detials = "http://ultistat.heliohost.org/exclusivestyle/get_product_details.php";

	
	// JSON Node names
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_PRODUCT = "product";
	private static final String TAG_PID = "pid";
	private static final String TAG_NAME = "name";
	private static final String TAG_PRICE = "price";
	private static final String TAG_DESCRIPTION = "description";
	private static final String TAG_URL = "url";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail_items);

		// save button


		// getting product details from intent
		Intent i = getIntent();
		
		// getting product id (pid) from intent
		pid = i.getStringExtra(TAG_PID);

		// Getting complete product details in background thread
		new GetProductDetails().execute();



	
	}

	/**
	 * Background Async Task to Get complete product details
	 * */
	class GetProductDetails extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(EditProductActivity.this);
			pDialog.setMessage("Loading product details. Please wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		/**
		 * Getting product details in background thread
		 * */
		protected String doInBackground(String... params) {

			// updating UI from Background Thread
			runOnUiThread(new Runnable() {
				public void run() {
					// Check for success tag
					int success;
					try {
						// Building Parameters
						List<NameValuePair> params = new ArrayList<NameValuePair>();
						params.add(new BasicNameValuePair("pid", pid));

						// getting product details by making HTTP request
						// Note that product details url will use GET request
						JSONObject json = jsonParser.makeHttpRequest(
								url_product_detials, "GET", params);

						// check your log for json response
						Log.d("Single Product Details", json.toString());
						
						// json success tag
						success = json.getInt(TAG_SUCCESS);
						if (success == 1) {
							// successfully received product details
							JSONArray productObj = json
									.getJSONArray(TAG_PRODUCT); // JSON Array
							
							// get first product object from JSON Array
							JSONObject product = productObj.getJSONObject(0);

							// product with this pid found
							// Edit Text
							txtName = (TextView) findViewById(R.id.inputName);
							txtPrice = (TextView) findViewById(R.id.inputPrice);
							txtDesc = (TextView) findViewById(R.id.inputDesc);
							txtUrl = (EditText) findViewById(R.id.inputUrl);
						
							// display product data in EditText
							txtName.setText(product.getString(TAG_NAME));
							txtPrice.setText(product.getString(TAG_PRICE));
							txtDesc.setText(product.getString(TAG_DESCRIPTION));
							ImageView image = (ImageView) findViewById(R.id.imageView1);
							String imageUrl= "http://goo.gl/lpcy5";
							
							URL url = null;
							try {
								url = new URL(product.getString(TAG_URL));
							} catch (MalformedURLException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								Log.i("test",product.getString(TAG_URL));
							}
							HttpURLConnection connection = null;
							try {
								connection = (HttpURLConnection) url.openConnection();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							InputStream is = null;
							try {
								is = connection.getInputStream();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							Bitmap img = BitmapFactory.decodeStream(is);  

							image.setImageBitmap(img);
							
						

						}else{
							// product with pid not found
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			});

			return null;
		}


		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog once got all details
			pDialog.dismiss();
			
		}
	}

	/**
	 * Background Async Task to  Save product Details
	 * */
	

}
