package com.quipmate2.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quipmate2.R;
import com.quipmate2.constants.AppProperties;
import com.quipmate2.features.Post;

// TODO: Auto-generated Javadoc
/**
 * The Class CommonMethods.
 */
public class CommonMethods {
	
	/**
	 * Gets the login url.
	 *
	 * @param password the password
	 * @param email the email
	 * @return the login url
	 */
	public static String getLoginUrl(String password, String email) {
		String URL = AppProperties.URL + "?action=login&password=" + password
				+ "&email=" + email;
		return URL;
	}

	/**
	 * Show info.
	 * 
	 * @param context
	 *            the context
	 * @param displayMessage
	 *            the display message
	 * @return the alert dialog. builder
	 */
	public static AlertDialog.Builder ShowInfo(Context context,
			String displayMessage) {

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(displayMessage)
				.setCancelable(false)
				.setPositiveButton(R.string.OK,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// Do nothing
							}
						});
		return builder;
	}

	public static String toCamelCase(String value)
	{
		String returnValue = "";
		boolean nextCharacterCapital = true;
		for(char c: value.toCharArray())
		{
			if(!Character.isSpaceChar(c))
			{
				returnValue+=nextCharacterCapital == true ? Character.toUpperCase(c) : Character.toLowerCase(c);
				nextCharacterCapital =false;
			}
			else
			{
				nextCharacterCapital = true;
				returnValue+=c;
			}
		}
		return returnValue;
	}
	/**
	 * Display toast.
	 * 
	 * @param context
	 *            the context
	 * @param text
	 *            the text
	 * @param duration
	 *            the duration
	 */
	public static void displayToast(Context context, String text, int duration) {
		Toast toast = Toast.makeText(context, text, duration);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();

	}

	/**
	 * Display toast.
	 * 
	 * @param context
	 *            the context
	 * @param text
	 *            the text
	 */
	public static void displayToast(Context context, String text) {
		Toast toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();

	}	
	
	public static JSONArray loadJSONData(String URL, String method, List<NameValuePair> postparams){
		InputStream is = null;
		JSONObject jObj = null;
		JSONArray jArray = null;

		String json = "";
		// Making HTTP request
				try {
					// Making HTTP request
					// check for request method
					if (method.equals("POST")) {

						// request method is POST
						DefaultHttpClient httpClient = AppProperties.appUserClient;
						HttpPost httpPost = new HttpPost(URL);
						MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
			        
			     Log.e("Inside Common Methods", URL);
			       for(int index=0; index < postparams.size(); index++) {
			            if(postparams.get(index).getName().equalsIgnoreCase("photo_box")) {
			                // If the key equals to "photo_box", we use FileBody to transfer the data
			                entity.addPart(postparams.get(index).getName(), new FileBody(new File (postparams.get(index).getValue())));
			            } else {
			                // Normal string data
			                entity.addPart(postparams.get(index).getName(), new StringBody(postparams.get(index).getValue()));
			            }
			        }
			       
			        httpPost.setEntity(entity);

						HttpResponse httpResponse = httpClient.execute(httpPost);
						HttpEntity httpEntity = httpResponse.getEntity();
						is = httpEntity.getContent();

					} else if (method == "GET") {
						// request method is GET
						DefaultHttpClient httpClient = AppProperties.appUserClient;
						String paramString = URLEncodedUtils
								.format(postparams, "utf-8");
						URL += "?" + paramString;
						HttpGet httpGet = new HttpGet(URL);

						HttpResponse httpResponse = httpClient.execute(httpGet);
						HttpEntity httpEntity = httpResponse.getEntity();
						
				 		is = httpEntity.getContent();
					} 
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace(); 
				} catch (Exception e) {
					e.printStackTrace();
				}

				try {
					BufferedReader reader = new BufferedReader(new InputStreamReader(
							is, "utf-8"), 8);
					StringBuilder sb = new StringBuilder();
					String line = null;
					while ((line = reader.readLine()) != null) {
						sb.append(line + "\n");
					}
					is.close();
					json = sb.toString();
					//System.out.println(json);
				} catch (Exception e) {
					Log.e("Buffer Error", "Error converting result " + e.toString());
				}

				// try parse the string to a JSON object
				try {
					if (!json.substring(0, 1).equalsIgnoreCase("[")) {
						jObj = new JSONObject(json);
					}
				} catch (JSONException e) {
					Log.e("JSON Parser", "Error parsing data " + e.toString());
				}

				// try parse the string to a JSON array
				try {
					// if string parsed as JSON object then convert it to be parsed as
					// array
					if (jObj != null) {
						json = "[" + json + "]";
					}
					jArray = new JSONArray(json);
				} catch (JSONException e) {
					Log.e("JSON Parser", "Error parsing data " + e.toString());
				}

				// return JSON String
				return jArray;
			}
	//takes a time in unix timestamp and returns the exact time.
	public static String getTime(long timestamp){
		long tc=(System.currentTimeMillis()-timestamp*1000)/1000;
		String time;
		if(tc<=60){
			time="1 seconds ago";
		}
		else{
			tc/=60;
			if(tc >= 1 && tc < 60){
				time=(tc+1)+" minutes ago";
			}
			else{
				tc/=60;
				if(tc >= 1 && tc < 24){
					time = (tc+1) +" hours ago";
				}
				else{
					tc=tc/24;
					if(tc >= 1 && tc <30){
						time = (tc+1)+ " days ago";
					}
					else{
						tc = tc/30;
						if(tc >= 1 && tc <= 12){
							time = (tc+1) +" months ago";
						}
						else{
							time = "more than a year ago";
						}
					}
				}
			}
		}
		return time;
	}
	
	public static Drawable photo_fetch(String img){
		Drawable image = null;
			try {
				System.out.println("Inside Run !");
				java.net.URL u = new java.net.URL(img);
				Object content = u.getContent();
				InputStream is = (InputStream)content;
				image = Drawable.createFromStream(is, "src");
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	System.out.println("Returning ffrom heew !");
	return image;
	}
	
	public static String getNotifDesc(String actionType){
		String desc = null;
		int actiontype = Integer.parseInt(actionType);
		switch(actiontype){
		case 1: desc="posted a status";break;
		case 2: desc="commented on your status"; break;
		case 3: desc="commented on your profile update";break;
		case 5: desc= "created an album"; break;
		case 6: desc="added a photo"; break;
		case 7: desc= "sent you a friend request"; break;
		case 8: desc="and you are now friends"; break;
		case 9: desc="unfriended you"; break;
		case 11: desc="is excited at your status"; break;
		case 12: desc= "is excited at your album"; break;
		case 13: desc="is excited at your profile update";break;
		case 15: desc="is excited at your photo";break;
		case 16: desc="new-pinched you";break;
		case 17: desc="is excited at your friendship";break;
		case 23: desc="commented on your album";break;
		case 24: desc="commented on your photo";break;
		case 25: desc="commented on your profile photo";break;
		case 26:  desc="commented on your friendship";break;
		case 63:  desc="is excited at your comment";break;
		case 91:  desc="exciting on your joining";break;
		case 92:  desc="commented on your joining";break;
		case 301: desc="posted in a group";break;
		case 302: desc="commented in a group";break;
		case 306: desc="posted a photo in a group";break;
		case 307: desc="requested you to join a group";break;
		case 308: desc="invited you to join a group";break;
		case 311: desc="any excited in group";break;
		case 316: desc= "posted link in a group"; break;
		case 326: desc="posted a document in a group"; break;
		case 328: desc="posted a question in a group"; break;
		case 329: desc="made you admin of the group"; break;
		case 330: desc="created event into a group";break;
		case 331: desc="pinned a document";break;
		case 402: desc="commented in event";break;
		case 403: desc="posted in event";break;
		case 406: desc="uploaded a photo in event";break;
		case 408: desc="joined an event";break;
		case 410: desc="cancelled an event";break;
		case 411: desc="is excited at your post in an event";break;
		case 416: desc="posted a link in an event";break;
		case 425: desc="posted a video in an event";break;
		case 426: desc="added a doc in an event";break;
		case 429: desc="made you host of an event";break;
		case 501: desc="is being missed by someone";break;
		case 502: desc="missed you back";break;
		case 503: desc="commented on your missing by someone";break;
		case 511: desc="is excited at your missing by someone";break;
		case 602:desc="commented on your blog";break;
		case 611:desc="is excited on your blog";break;
		case 702:desc="commented on your open letter to Managing Director";break;
		case 711:desc="exciting on your open letter to Managing Director";break;
		case 802: desc="commented on your tagline";break;
		case 811:desc="is excited at your tagline";break;
		case 1202:desc="commented on your mood";break;
		case 1211:desc="is excited at your mood";break;
		case 1401:desc="sent a gift to you";break;
		case 1402:desc="commented on your gift";break;
		case 1411: desc="is excited at your gift";break;
		case 1600:desc="shared a link with you";break;
		case 1602:desc="commented on your link";break;
		case 1611:desc="is excited at your link";break;
		case 1900:desc="wished birthday to you";break;
		case 1902:desc="commented on your birthday-bomb";break;
		case 1911:desc="is excited at your birthday-bomb";break;
		case 2400 : desc="praised you";break;
		case 2402:desc="commented on your praise";break;
		case 2411:desc="excited on your praise";break;
		case 2450:desc="made you star of the week";break;
		case 2500:desc="uploaded a video";break;
		case 2502:desc="commented on your video";break;
		case 2511:desc="excited on your video";break;
		case 2600:desc="uploaded a document";break;
		case 2602:desc="commented on your document";break;
		case 2611:desc="excited on your document";break;
		case 2800:desc="asked you a question";break;
		case 2801:desc="answered your question";break;
		case 2802:desc="commented on your question";break;
		case 2811:desc="excited at your question";break;
		case 2901:desc="posted in your page";break;
		case 2902:desc="commented in your page";break;
		case 2906:desc="uploaded a photo in your page";break;
		case 2911:desc="excited on page";break;
		
		
		default : desc=null;
		}
		return desc;
	}
	
	public static class single_image_fetch extends AsyncTask<Object, Void, Void>{
		 
		Drawable d;
		ImageView iv;
		@Override
		protected void onPreExecute() { 
			
		}
		@Override 
		protected Void doInBackground(Object... params) { 
					try{ 
						String photo = (String) params[0];
						iv = (ImageView) params[1];
						d = CommonMethods.photo_fetch(photo);
					}
					catch(Exception e1)
					{
						System.out.println("Background Exceptions");
						e1.printStackTrace();
					} 
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			iv.setBackgroundDrawable(d);
		} 
	}
	
		
	}

