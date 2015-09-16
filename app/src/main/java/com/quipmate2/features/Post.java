package com.quipmate2.features;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.quipmate2.R;
import com.quipmate2.constants.AppProperties;
import com.quipmate2.utils.CommonMethods; 

import android.app.Activity;
import android.app.ActionBar.LayoutParams;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import com.quipmate2.utils.CommonMethods.single_image_fetch;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Post extends Activity implements OnClickListener{

	RelativeLayout llPost;
	RelativeLayout llContent;
	LinearLayout llTime, llResponse, llCommentList;
	ImageView ivPimage, ivContent;
	TextView tvName, tvCount,tvTime,tvContent,tvFile,tvExciting;
	String pageid, life_is_fun;
	Session session;
	JSONArray adata;
	EditText etComment;
	Button btnCommentPost; 
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.post);
		getActionBar().setTitle("Post");
		getActionBar().setDisplayHomeAsUpEnabled(true);
		llPost = (RelativeLayout) findViewById(R.id.llPost);
		llContent = (RelativeLayout) findViewById(R.id.llContent);
		llTime = (LinearLayout) findViewById(R.id.llTime);
		llResponse = (LinearLayout) findViewById(R.id.llResponse);
		llCommentList = (LinearLayout) findViewById(R.id.llCommentList);
		ivPimage = (ImageView) findViewById(R.id.ivPimage);
		ivContent = (ImageView) findViewById(R.id.ivContent1);
		tvContent = (TextView)findViewById(R.id.tvContent);
		tvFile = (TextView)findViewById(R.id.tvFile);
		tvName = (TextView)findViewById(R.id.tvName);
		tvCount = (TextView)findViewById(R.id.tvCount);
		tvTime = (TextView)findViewById(R.id.tvTime);
		tvExciting = (TextView)findViewById(R.id.tvExciting);
		etComment = (EditText) findViewById(R.id.etComment);
		btnCommentPost = (Button) findViewById(R.id.btnCommentPost);
		
		session = new Session(getApplicationContext()); 
		Log.e("Startted Post","Post class started");
		Intent intent = getIntent(); 
		Bundle idata = intent.getExtras();
		pageid = idata.getString("actionid");
		life_is_fun = idata.getString("life_is_fun");

		tvCount.setOnClickListener(this);
		tvExciting.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				Log.e("Button Clicked", "Excting Button has been clicked");
				new SendResponse().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				
			}
		});
		
		
		
etComment.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub 
				System.out.println("Working !!!");
				if (event.getAction() == KeyEvent.ACTION_DOWN)
		        {
		            switch (keyCode)
		            {
		                case KeyEvent.KEYCODE_DPAD_CENTER:
		                case KeyEvent.KEYCODE_ENTER:
		                    Log.e("Seding Chat", "Sending CHat");
		                    if(!etComment.getText().toString().trim().equalsIgnoreCase(""))
		                    {	
		                    	new SendComment().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		                    }	
		                    return true;
		                default: 
		                    break;
		            }
		        }
				return false;
			}
		});
		
		
		btnCommentPost.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(!etComment.getText().toString().trim().equalsIgnoreCase(""))
                {	
                	new SendComment().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
			}
		});
		
		
		
		
		new PostFetch().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		
	}
	
	void feed_deploy()
	{

		if(adata!=null){
			try{
				JSONObject data = adata.getJSONObject(0);
				JSONArray action = new JSONArray(data.getString("action"));
				JSONObject name = data.getJSONObject(AppProperties.NAME);
				JSONObject pimage = data.getJSONObject(AppProperties.PROFILE_IMAGE);
				JSONObject temp = new JSONObject(action.get(0).toString());
						
				String actiontype = temp.getString("actiontype");
						
				String actionby = temp.getString("actionby");
				String postby = temp.getString("postby");
				tvName.setText(Html.fromHtml("<b>"+name.getString(postby)+"</b>")); 
				new single_image_fetch().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,pimage.getString(postby),ivPimage);
				tvTime.setText(CommonMethods.getTime(Long.parseLong(temp.getString("time")))); 
				
				if(actiontype.equalsIgnoreCase("1") || actiontype.equalsIgnoreCase("301") || actiontype.equalsIgnoreCase("403"))
				{
					if(temp.getString("page") != null)
					{
						tvContent.setText(temp.getString("page"));
						llContent.removeView(ivContent); 
					}
				}
				else if(actiontype.equalsIgnoreCase("2800") || actiontype.equalsIgnoreCase("328") || actiontype.equalsIgnoreCase("428"))
				{
					if(temp.getString("question") != null)
					{
						tvContent.setText(temp.getString("question"));
						llContent.removeView(ivContent);
					}
				}
				else if(actiontype.equalsIgnoreCase("6") || actiontype.equalsIgnoreCase("306") || actiontype.equalsIgnoreCase("406"))
				{
					tvContent.setText(temp.getString("page"));
					llContent.removeView(tvFile);
					final String file = temp.getString("file");
					new single_image_fetch().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,temp.getString("file"),ivContent);
					ivContent.setOnClickListener(new OnClickListener() { 
						
						@Override
						public void onClick(View arg0) {
							try{			
									Log.e("File Textview","File TextView clicked");
									Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(file));
									intent.setDataAndType(Uri.parse(file), "image/*");
									startActivity(intent);
									
								} 
								catch(Exception otherException) 
							    {
							         otherException.printStackTrace();
							    }
				
						}
					});
					
				}
	 			else if(actiontype.equalsIgnoreCase("2600") || actiontype.equalsIgnoreCase("326") || actiontype.equalsIgnoreCase("426"))
				{ 
					tvContent.setText(temp.getString("page")); 
	 				tvFile.setText(temp.getString("caption"));
	 				tvFile.setPaintFlags(tvFile.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
	 				llContent.removeView(ivContent);
					final String file = temp.getString("file");
					//new single_image_fetch().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,AppProperties.PDF_ICON,ivContent);
					tvFile.setOnClickListener(new OnClickListener() { 
											
						@Override
						public void onClick(View arg0) {
							try{			
									Log.e("File Textview","File TextView clicked");
									WebView webview=new WebView(Post.this);
			                        webview.getSettings().setJavaScriptEnabled(true); 
			                        webview.loadUrl("http://docs.google.com/gview?embedded=true&url=" + file);
									
								} 
								catch(Exception otherException) 
							    {
							         otherException.printStackTrace();
							    }
				
						}
					});
				}
	 			else if(actiontype.equalsIgnoreCase("2500") || actiontype.equalsIgnoreCase("325") || actiontype.equalsIgnoreCase("425"))
				{ 
					tvContent.setText(temp.getString("page")); 
	 				tvFile.setText(temp.getString("caption"));
	 				tvFile.setPaintFlags(tvFile.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
	 				llContent.removeView(tvFile);
					final String file = temp.getString("file");  
					new single_image_fetch().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,temp.getString("caption"),ivContent);
					ivContent.setOnClickListener(new OnClickListener() { 
											
						@Override
						public void onClick(View arg0) {
							try{			
									Log.e("File Textview","File TextView clicked");
									Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(file));
									intent.setDataAndType(Uri.parse(file), "video/flv");
									startActivity(intent);
									
								} 
								catch(Exception otherException) 
							    {
							         otherException.printStackTrace();
							    }
				
						}
					});
				}
	 			else if(actiontype.equalsIgnoreCase("1600") || actiontype.equalsIgnoreCase("316") || actiontype.equalsIgnoreCase("416"))
				{ 
					tvContent.setText(temp.getString("page")); 
	 				tvFile.setText(temp.getString("title"));
	 				tvFile.setPaintFlags(tvFile.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
	 				//llContent.removeView(ivContent);
					final String link = temp.getString("link");
					new single_image_fetch().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,temp.getString("file"),ivContent);
					tvFile.setOnClickListener(new OnClickListener() { 
											
						@Override
						public void onClick(View arg0) {
							try{			
									Log.e("File Textview","File TextView clicked");
									WebView webview=new WebView(Post.this);
			                        webview.getSettings().setJavaScriptEnabled(true); 
			                        webview.loadUrl(link);
									
								} 
								catch(Exception otherException) 
							    {
							         otherException.printStackTrace();
							    }
				
						}
					});
				}
				else 
				{
					if(temp.getString("page") != null)
					{
						tvContent.setText(temp.getString("is now following you"));
						llContent.removeView(ivContent);
						llContent.removeView(tvFile);
					}
				} 
				tvCount.setText(temp.getString("excited").length()+" "+"people excited at this");
				JSONArray comments = new JSONArray(temp.getString("com").toString());
				RelativeLayout [] hllay = new RelativeLayout[comments.length()]; 
				TextView [] tvComment = new TextView[comments.length()];
				TextView [] tvComTime = new TextView[comments.length()];
				TextView [] tvComExciting = new TextView[comments.length()];
				TextView [] tvComExcitingCount = new TextView[comments.length()];
				ImageView [] ivComment = new ImageView[comments.length()];
				
				RelativeLayout.LayoutParams param_com_photo = new RelativeLayout.LayoutParams(80, 80);
				RelativeLayout.LayoutParams param_comment = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				RelativeLayout.LayoutParams param_com_time = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				RelativeLayout.LayoutParams param_com_exciting = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				for(int j=0;j<comments.length();j++)
				{
					JSONObject com = new JSONObject(comments.get(j).toString());
					hllay[j] = new RelativeLayout(Post.this);
					ivComment[j] = new ImageView(Post.this);
					param_com_photo.addRule(RelativeLayout.ALIGN_PARENT_TOP);
					ivComment[j].setLayoutParams(param_com_photo);
					new single_image_fetch().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,pimage.get(com.getString("commentby")),ivComment[j]);
					hllay[j].addView(ivComment[j]);
					tvComment[j] = new TextView(Post.this); 
					tvComment[j].setText(Html.fromHtml("<b>"+ name.getString(com.getString("commentby")) + "</b>" +" " +com.getString("comment")));
					param_comment.addRule(RelativeLayout.RIGHT_OF,ivComment[j].getId());
					param_comment.setMargins(90, 0, 0, 0);
					tvComment[j].setLayoutParams(param_comment);
		 			
					tvComTime[j] = new TextView(Post.this);
					tvComTime[j].setText(CommonMethods.getTime(Long.parseLong(com.getString("com_time"))));
					
					param_com_time.addRule(RelativeLayout.RIGHT_OF,ivComment[j].getId());
					param_com_time.addRule(RelativeLayout.BELOW,tvComment[j].getId());
					param_com_time.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
					param_com_time.setMargins(90, 60, 0, 0);
					tvComTime[j].setLayoutParams(param_com_time);
					
					
	 				tvComExciting[j] = new TextView(Post.this);
		 	 		tvComExciting[j].setText("Exciting");
		 	 		param_com_exciting.addRule(RelativeLayout.RIGHT_OF,tvComTime[j].getId());
					param_com_exciting.addRule(RelativeLayout.BELOW,tvComment[j].getId());
					param_com_exciting.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
					param_com_exciting.setMargins(275, 60, 0, 0);
		 	 		tvComExciting[j].setLayoutParams(param_com_exciting);
	 				

	 				tvComExcitingCount[j] = new TextView(Post.this);
		 	 		tvComExcitingCount[j].setText(com.getString("com_excited").length()+"");
		 	 		param_com_exciting.addRule(RelativeLayout.RIGHT_OF,tvComExciting[j].getId());
					param_com_exciting.addRule(RelativeLayout.BELOW,tvComment[j].getId());
					param_com_exciting.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
					param_com_exciting.setMargins(375, 60, 0, 0);
		 	 		tvComExcitingCount[j].setLayoutParams(param_com_exciting);
		 	 		
					hllay[j].addView(tvComment[j]);
					hllay[j].addView(tvComTime[j]);
					hllay[j].addView(tvComExciting[j]);
					hllay[j].addView(tvComExcitingCount[j]);
					llCommentList.addView(hllay[j]);
				}

			}
			catch(JSONException e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			
		}
	}
	
	
	class PostFetch extends AsyncTask<Void, Void, Void>
	{

		@Override
		protected Void doInBackground(Void... params) {
			List<NameValuePair> apiParams = new ArrayList<NameValuePair>();
			apiParams.add(new BasicNameValuePair(AppProperties.ACTION, "action_fetch"));
			apiParams.add(new BasicNameValuePair("actionid", pageid));
			apiParams.add(new BasicNameValuePair("auth", session.getValue(AppProperties.PROFILE_ID)));
			apiParams.add(new BasicNameValuePair("life_is_fun", life_is_fun)); 
			Log.e("Previous Chat Parameters", apiParams.toString());
			try{ 
				adata = CommonMethods.loadJSONData(AppProperties.URL, AppProperties.METHOD_GET, apiParams);		
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
			super.onPostExecute(result);
			Log.e("post data", adata.toString());
			feed_deploy();
		}
	}

	
	
	class SendComment extends AsyncTask<Void, Void, Void>{
		
		 String action = "comment", comment = etComment.getText().toString();
			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				
				etComment.setText("");
				RelativeLayout.LayoutParams param_com_photo = new RelativeLayout.LayoutParams(80, 80);
				RelativeLayout.LayoutParams param_comment = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				RelativeLayout.LayoutParams param_com_time = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				RelativeLayout.LayoutParams param_com_exciting = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				RelativeLayout hllay = new RelativeLayout(Post.this);
				ImageView ivComment = new ImageView(Post.this);
				param_com_photo.addRule(RelativeLayout.ALIGN_PARENT_TOP);
				ivComment.setLayoutParams(param_com_photo);
				new single_image_fetch().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,session.getValue(AppProperties.MY_PROFILE_PIC),ivComment);
				hllay.addView(ivComment);
				TextView tvComment = new TextView(Post.this);
				tvComment.setText(Html.fromHtml("<b>"+ session.getValue(AppProperties.NAME) + "</b>" +" " + comment));
				param_comment.addRule(RelativeLayout.RIGHT_OF,ivComment.getId());
				param_comment.setMargins(90, 0, 0, 0);
				tvComment.setLayoutParams(param_comment);
	 			
				TextView tvComTime = new TextView(Post.this);
				tvComTime.setText("Just Now");
				param_com_time.addRule(RelativeLayout.RIGHT_OF,ivComment.getId());
				param_com_time.addRule(RelativeLayout.BELOW,tvComment.getId());
				param_com_time.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
				param_com_time.setMargins(90, 60, 0, 0);
				tvComTime.setLayoutParams(param_com_time);
				
				
 				TextView tvComExciting = new TextView(Post.this);
	 	 		tvComExciting.setText("Exciting");
	 	 		param_com_exciting.addRule(RelativeLayout.RIGHT_OF,tvComTime.getId());
				param_com_exciting.addRule(RelativeLayout.BELOW,tvComment.getId());
				param_com_exciting.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
				param_com_exciting.setMargins(275, 60, 0, 0);
	 	 		tvComExciting.setLayoutParams(param_com_exciting);
	 	 		
	 	 		TextView tvComExcitingCount = new TextView(Post.this);
	 	 		tvComExcitingCount.setText("");
	 	 		param_com_exciting.addRule(RelativeLayout.RIGHT_OF,tvComTime.getId());
				param_com_exciting.addRule(RelativeLayout.BELOW,tvComment.getId());
				param_com_exciting.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
				param_com_exciting.setMargins(375, 60, 0, 0);
	 	 		tvComExcitingCount.setLayoutParams(param_com_exciting);
 				
				hllay.addView(tvComment);
				hllay.addView(tvComTime);
				hllay.addView(tvComExciting);
				llCommentList.addView(hllay);
				
			}
			
			
			@Override
			protected Void doInBackground(Void... params) {
				// TODO Auto-generated method stub
				try{
						
						ArrayList<NameValuePair> apiParams = new ArrayList<NameValuePair>();
						apiParams.add(new BasicNameValuePair(AppProperties.DATABASE, session.getValue(AppProperties.DATABASE)));
						apiParams.add(new BasicNameValuePair("pageid", pageid));  
						apiParams.add(new BasicNameValuePair("action", action));
						apiParams.add(new BasicNameValuePair("comment", comment));
						apiParams.add(new BasicNameValuePair("comment_time", "100"));
						apiParams.add(new BasicNameValuePair("auth", session.getValue(AppProperties.PROFILE_ID)));
						apiParams.add(new BasicNameValuePair("PHPSESSID", session.getValue(AppProperties.SESSION_ID)));
						Log.e("Chat NEW Parameters", apiParams.toString());
						JSONArray d = CommonMethods.loadJSONData(AppProperties.URL, AppProperties.METHOD_GET, apiParams);
							if(d != null){
									
									Log.e("Response", d.toString());
							} 
						}	
				 catch (Exception e) {
						System.out.println("Some Unknown exception occured");
						e.printStackTrace();
					}
				return null;
			}
			
			@Override
			protected void onPostExecute(Void result) {
				// TODO Auto-generated method stub
				
				//Log.e("Rerunred Data", data.toString());
				
			}
		}

	

	 class SendResponse extends AsyncTask<Void, Void, Void>{
			
		 String action = "response";
			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				if(tvExciting.getText().equals("Exciting"))
				{
					action = "response";
					tvExciting.setText("Unexciting");
					//tvCount.setText(Integer.parseInt((String) tvCount.getText())+1);
				}
				else
				{
					action = "responsed";
					tvExciting.setText("Exciting");
					//tvCount.setText(Integer.parseInt((String) tvCount.getText())-1);
				}
			}
			
			
			@Override
			protected Void doInBackground(Void... params) {
				// TODO Auto-generated method stub
				try{
						
						ArrayList<NameValuePair> apiParams = new ArrayList<NameValuePair>();
						apiParams.add(new BasicNameValuePair(AppProperties.DATABASE, session.getValue(AppProperties.DATABASE)));
						apiParams.add(new BasicNameValuePair("pageid", pageid));  
						apiParams.add(new BasicNameValuePair("action", action));
						apiParams.add(new BasicNameValuePair("auth", session.getValue(AppProperties.PROFILE_ID)));
						apiParams.add(new BasicNameValuePair("PHPSESSID", session.getValue(AppProperties.SESSION_ID)));
						Log.e("Chat NEW Parameters", apiParams.toString());
						JSONArray d = CommonMethods.loadJSONData(AppProperties.URL, AppProperties.METHOD_GET, apiParams);
							if(d != null){
									
									Log.e("Response", d.toString());
							}
						}	
				 catch (Exception e) {
						System.out.println("Some Unknown exception occured");
						e.printStackTrace();
					}
				return null;
			}
			
			@Override
			protected void onPostExecute(Void result) {
				// TODO Auto-generated method stub
				
				//Log.e("Rerunred Data", data.toString());
				
			}
		}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
			// TODO Auto-generated method stub
			switch (item.getItemId()) {
	        case android.R.id.home:
	            // application icon in action bar clicked; go home
	           finish();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
	}


	@Override
	public void onClick(View v) {

		Log.e("Clicked On TextView","Clicked On TextView");
	}	

}