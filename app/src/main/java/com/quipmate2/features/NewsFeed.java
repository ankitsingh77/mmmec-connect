package com.quipmate2.features;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.quipmate2.R;
import com.quipmate2.constants.AppProperties;
import com.quipmate2.dto.ComentsDTO;
import com.quipmate2.dto.NewsFeedDTO;
import com.quipmate2.dto.Option;
import com.quipmate2.dto.Photo;
import com.quipmate2.loadwebimageandcache.MemoryCache;
import com.quipmate2.utils.CommonMethods;
import com.quipmate2.utils.CommonMethods.single_image_fetch;
import com.quipmate2.utils.FeedData;

public class NewsFeed extends Activity implements OnClickListener,
OnTouchListener {

	RelativeLayout rlPost;
	RelativeLayout rlContent, rlComment;
	LinearLayout llTime, llPostRHS, llResponse, llCommentList;
	ImageView ivPimage, ivContent;
	TextView tvName, tvCount, tvTime, tvContent, tvFile, tvExciting;
	String pageid, life_is_fun;
	Session session;
	JSONArray adata;
	EditText etComment;
	Button btnCommentPost;
	String start = "0";
	ArrayList<String> items;
	JSONObject name;
	JSONObject pimage;
	ListView scrNews;
	Boolean loading = false;
	int scrollPosition = 0;
	List<NewsFeedDTO> newsFeedDTOList = null;

	private static  ArrayList<String> Name_Title = new  ArrayList<String>();
	private static  ArrayList<String> Name_ID = new  ArrayList<String>();

	private static  Map<String, String> nameMapping = new  HashMap<String, String>();
	private static  Map<String, String> imageURLMapping = new  HashMap<String, String>();
	private static ArrayList<String> PI_Image =new ArrayList<String>();



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.news);


		getActionBar().setTitle("Post");
		getActionBar().setDisplayHomeAsUpEnabled(true);
		scrNews = (ListView) findViewById(R.id.scrNews);
		session = new Session(getApplicationContext());

		new FeedFetch().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	class SendResponse extends AsyncTask<Void, Void, Void> {

		String action = "response";

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			if (tvExciting.getText().equals("Exciting")) {
				action = "response";
				tvExciting.setText("Unexciting");
				// tvCount.setText(Integer.parseInt((String)
				// tvCount.getText())+1);
			} else {
				action = "responsed";
				tvExciting.setText("Exciting");
				// tvCount.setText(Integer.parseInt((String)
				// tvCount.getText())-1);
			}
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			try {

				ArrayList<NameValuePair> apiParams = new ArrayList<NameValuePair>();
				apiParams.add(new BasicNameValuePair(AppProperties.DATABASE,
						session.getValue(AppProperties.DATABASE)));
				apiParams.add(new BasicNameValuePair("pageid", pageid));
				apiParams.add(new BasicNameValuePair("action", action));
				apiParams.add(new BasicNameValuePair("auth", session
						.getValue(AppProperties.PROFILE_ID)));
				apiParams.add(new BasicNameValuePair("PHPSESSID", session
						.getValue(AppProperties.SESSION_ID)));
				Log.e("Chat NEW Parameters", apiParams.toString());
				JSONArray d = CommonMethods.loadJSONData(AppProperties.URL,
						AppProperties.METHOD_GET, apiParams);
				if (d != null) {

					Log.e("Response", d.toString());
				}
			} catch (Exception e) {
				System.out.println("Some Unknown exception occured");
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub

			// Log.e("Rerunred Data", data.toString());

		}
	}


	public void FeedDisplay(List<NewsFeedDTO> list){

		CompleteListAdapter adapter = new CompleteListAdapter(NewsFeed.this, list);
		scrNews.setAdapter(adapter);


	}



	public static Bitmap getBitmapFromURL(String src) {
		try {
			URL url = new URL(src);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoInput(true);
			connection.connect();
			InputStream input = connection.getInputStream();
			Bitmap myBitmap = BitmapFactory.decodeStream(input);
			return myBitmap;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	} // A

	public class CompleteListAdapter extends BaseAdapter {  

		private Activity mContext;  
		// private ArrayList<String>  mList ,pimage;  
		private LayoutInflater mLayoutInflater = null;  

		private List<NewsFeedDTO> newsListDTO = null;

		MemoryCache memoryCache;

		public CompleteListAdapter(Activity context, List<NewsFeedDTO> list ) {  
			mContext = context;  

			mLayoutInflater = (LayoutInflater) mContext  
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);  

			memoryCache =new MemoryCache();

			// mList= Name_Title;
			//pimage =PI_Image;

			newsListDTO = new ArrayList<NewsFeedDTO>();
			this.newsListDTO = list;
		}  
		@Override  
		public int getCount() {  
			return newsListDTO.size();  
		}  
		@Override  
		public Object getItem(int pos) {  
			return newsListDTO.get(pos);  
		}  
		@Override  
		public long getItemId(int position) {  
			return position;  
		}  
		@Override  
		public View getView(int position, View convertView, ViewGroup parent) {  
			View v = convertView;  
			CompleteListViewHolder viewHolder = null;  
			if (convertView == null) {  
				LayoutInflater li = (LayoutInflater) mContext .getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
				v = li.inflate(R.layout.news_feed_list_adapter, null);  
				v.setTag(viewHolder);  
			} else {  
				viewHolder = (CompleteListViewHolder) v.getTag();  
			} 

			TextView txtPostedPlace = (TextView) v.findViewById(R.id.list_adapter_posted_place);
			ImageView imgProfilePic = (ImageView) v.findViewById(R.id.list_adapter_profile_pic);
			TextView txtProfileName = (TextView) v.findViewById(R.id.list_adapter_profile_name);


			new single_image_fetch().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,imageURLMapping.get(newsListDTO.get(position).getPostby()),imgProfilePic);

			//imgProfilePic.setImageBitmap(memoryCache.get(imageURLMapping.get(Long.parseLong(newsListDTO.get(position).getPostby()))));
			//imgProfilePic.setImageURI(Uri.parse(imageURLMapping.get(Long.parseLong(newsListDTO.get(position).getPostby()))));


			RelativeLayout rl = null;
			
			if(newsListDTO.get(position).getActiontype().equals("1")){

				RelativeLayout rlAction1 = (RelativeLayout) v.findViewById(R.id.list_adapter_action_type_1);
				rlAction1.setVisibility(View.VISIBLE);

				rl = rlAction1;
				
				TextView txtStatus = (TextView) v.findViewById(R.id.list_adapter_txt_1_status);

				txtStatus.setText(newsListDTO.get(position).getPage());

				txtPostedPlace.setText(nameMapping.get(newsListDTO.get(position).getActionby()) +" posted in"+nameMapping.get(newsListDTO.get(position).getActionon())+"'s diary" );

				txtProfileName.setText(nameMapping.get(newsListDTO.get(position).getActionby()));
			} else if (newsListDTO.get(position).getActiontype().equals("6")){

				RelativeLayout rlAction6 = (RelativeLayout) v.findViewById(R.id.list_adapter_action_type_6);
				rlAction6.setVisibility(View.VISIBLE);

				rl = rlAction6;
				
				TextView txtStatus = (TextView) v.findViewById(R.id.list_adapter_txt_6_status);

				txtStatus.setText(newsListDTO.get(position).getPage());

				ImageView imgSharedPic  = (ImageView) v.findViewById(R.id.list_adapter_img_6_pic);

				new single_image_fetch().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,imageURLMapping.get(newsListDTO.get(position).getFiles()),imgSharedPic);
			} else if (newsListDTO.get(position).getActiontype().equals("2500")){

				RelativeLayout rlAction2500 = (RelativeLayout) v.findViewById(R.id.list_adapter_action_type_2500);

				rlAction2500.setVisibility(View.VISIBLE);
				
				rl = rlAction2500;
				
				TextView txtVideoStatus = (TextView) v.findViewById(R.id.list_adapter_txt_2500_status);
				txtVideoStatus.setText(newsListDTO.get(position).getPage());

				ImageView imgVideoImage = (ImageView) v.findViewById(R.id.list_adapter_img_2500_video_pic);

				new single_image_fetch().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,imageURLMapping.get(newsListDTO.get(position).getCaption()),imgVideoImage);

			} else if(newsListDTO.get(position).getActiontype().equals("1600")){
				if(newsListDTO.get(position).getVideo().equals("0")){

					RelativeLayout rlAction16001= (RelativeLayout) v.findViewById(R.id.list_adapter_action_type_1600_1);
					rlAction16001.setVisibility(View.VISIBLE);

					rl = rlAction16001;
					
					TextView txtLink = (TextView) v.findViewById(R.id.list_adapter_txt_1600_1_status);
					txtLink.setText(newsListDTO.get(position).getLink());

					TextView txtHeading = (TextView) v.findViewById(R.id.list_adapter_txt_1600_1_heading);
					txtHeading.setText(newsListDTO.get(position).getTitle());

					TextView txtWebLink = (TextView) v.findViewById(R.id.list_adapter_txt_1600_1_link);
					txtWebLink.setText(newsListDTO.get(position).getHost());

					TextView txtDescription = (TextView) v.findViewById(R.id.list_adapter_txt_1600_1_descrption);
					txtDescription.setText(newsFeedDTOList.get(position).getMeta());


				}else if(newsListDTO.get(position).getVideo().equals("1")){
					RelativeLayout rlAction16002= (RelativeLayout) v.findViewById(R.id.list_adapter_action_type_1600_2);
					rlAction16002.setVisibility(View.VISIBLE);

					rl = rlAction16002;
					
					TextView txtStatus = (TextView) v.findViewById(R.id.list_adapter_txt_1600_2_status);
					txtStatus.setText(newsListDTO.get(position).getLink());

					TextView txtHeading = (TextView) v.findViewById(R.id.list_adapter_txt_1600_2_heading);
					txtHeading.setText(newsListDTO.get(position).getTitle());

					TextView txtWebLink = (TextView) v.findViewById(R.id.list_adapter_txt_1600_2_link);
					txtWebLink.setText(newsListDTO.get(position).getHost());

					ImageView imgVideoScreen = (ImageView) v.findViewById(R.id.list_adapter_img_1600_2_video_pic);

					//new single_image_fetch().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,imageURLMapping.get(newsListDTO.get(position).getCaption()),imgVideoScreen);

					TextView txtDescription = (TextView) v.findViewById(R.id.list_adapter_txt_1600_2_description);
					txtDescription.setText(newsListDTO.get(position).getMeta());
				}
			}else if(newsListDTO.get(position).getActiontype().equals("2800")){
				LinearLayout optionsTable = (LinearLayout) findViewById(R.id.list_adapter_txt_2800_options_heading);
				// use loop 
				CheckBox feature1 = new CheckBox(mContext);
				//newsListDTO.get(position).get

				//optionsTable.addView(feature1 );
			} else if(newsListDTO.get(position).getActiontype().equals("2400")){
				RelativeLayout rlAction2400 = (RelativeLayout) v.findViewById(R.id.list_adapter_action_type_2400);

				rlAction2400.setVisibility(View.VISIBLE);
				
				rl = rlAction2400;

				TextView txtPraiseFor = (TextView) v.findViewById(R.id.list_adapter_txt_2400_for);
				txtPraiseFor.setText("For : "+newsListDTO.get(position).getLetterTitle());

				TextView txtPraise = (TextView) v.findViewById(R.id.list_adapter_txt_2400_praise_status);
				txtPraise.setText("Praised for : "+newsListDTO.get(position).getLetterContent());

				TextView txtPraiseDesc = (TextView) v.findViewById(R.id.list_adapter_ed_2400_praise_text);
				txtPraiseDesc.setText(newsListDTO.get(position).getMood());

				ImageView imgVideoImage = (ImageView) v.findViewById(R.id.list_adapter_txt_2400_image_view);

				new single_image_fetch().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,imageURLMapping.get(newsListDTO.get(position).getFiles()),imgVideoImage);

			}else if (newsListDTO.get(position).getActiontype().equals("2600")){
				RelativeLayout rlAction2600 = (RelativeLayout) v.findViewById(R.id.list_adapter_action_type_2600);

				rlAction2600.setVisibility(View.VISIBLE);

				rl = rlAction2600;
				
				TextView txtFileName = (TextView) v.findViewById(R.id.list_adapter_ed_2600_file_text);
				txtFileName.setText(newsListDTO.get(position).getCaption());

				TextView txtFileFormat = (TextView) v.findViewById(R.id.list_adapter_txt_2600_file_name);
				txtFileFormat.setText(newsListDTO.get(position).getCaption());


			}else if (newsListDTO.get(position).getActiontype().equals("1201")){
				RelativeLayout rlAction1201 = (RelativeLayout) v.findViewById(R.id.list_adapter_action_type_1201);

				rlAction1201.setVisibility(View.VISIBLE);
				
				rl = rlAction1201;

				TextView txtMoodStatus = (TextView) v.findViewById(R.id.list_adapter_txt_1201_status);
				txtMoodStatus.setText(newsListDTO.get(position).getPage());

				ImageView imgMood = (ImageView) v.findViewById(R.id.list_adapter_img_1201_pic) ;

				String uri = "@drawable/"+newsListDTO.get(position).getFiles();
				int imageResource = getResources().getIdentifier(uri, null, getPackageName());
				Drawable res = getResources().getDrawable(imageResource);
				imgMood.setImageDrawable(res);

			}

			
			LinearLayout linearLayout = (LinearLayout) v.findViewById(R.id.list_adapter_comment_exciting);
			RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
			        ViewGroup.LayoutParams.WRAP_CONTENT);

			p.addRule(RelativeLayout.BELOW,rl.getId());

			linearLayout.setLayoutParams(p);

			TextView txtExcited = (TextView) v.findViewById(R.id.list_adapter_exciting_text);
			String imageURL = null;
			String profileName = null;
			boolean found = false;
			for(int i=0;i<newsListDTO.get(position).getExcitedList().size();i++){

				if(session.getValue(AppProperties.SESSION_ID).equals(newsListDTO.get(position).getExcitedList().get(i))){
					txtExcited.setText("UnExciting");
					//imageURL = imageURLMapping.get(newsListDTO.get(position).getExcitedList().get(i));
					//profileName = nameMapping.get(newsListDTO.get(position).getExcitedList().get(i));
					found = true;
					break;
				}
			}
			if(found != true){
				txtExcited.setText("Exciting");
			}


			TextView txtPostedTime = (TextView) v.findViewById(R.id.lsit_adapter_exciting_time);
			txtPostedTime.setText(newsListDTO.get(position).getTime());

			TextView txtExcitedBy = (TextView) v.findViewById(R.id.list_adapter_commented_count);
			if(found == true){
				txtExcitedBy.setText("You and "+newsListDTO.get(position).getCommentCount()+ " more are excited at this");
			}else{
				txtExcitedBy.setText(newsListDTO.get(position).getCommentCount()+ " more are excited at this");
			}


			if(Integer.parseInt(newsListDTO.get(position).getCommentCount().toString()) != 0){

				imageURL = imageURLMapping.get(newsListDTO.get(position).getComments().get(0).getCommentBy());
				profileName = nameMapping.get(newsListDTO.get(position).getComments().get(0).getCommentBy());

				ImageView imgCommentProfilePic = (ImageView) v.findViewById(R.id.list_adapter_commented_profile_pic_1);

				new single_image_fetch().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,imageURL,imgCommentProfilePic);

				TextView txtCommentProfileName = (TextView) v.findViewById(R.id.list_adapter_commented_profile_name_1);

				txtCommentProfileName.setText(profileName);

				TextView txtComment1 = (TextView) v.findViewById(R.id.list_adapter_commented_comment_1);

				txtComment1.setText(newsListDTO.get(position).getComments().get(0).getComment());

				TextView txtCommentExciting = (TextView) v.findViewById(R.id.list_adapter_commented_comment_excite_1);

				txtCommentExciting.setText(newsListDTO.get(position).getComments().get(0).getComExcitedMine().equals("0")?"Unexciting":"Exciting");

				TextView txtCommentTime = (TextView) v.findViewById(R.id.list_adapter_commented_comment_time_1);

				txtCommentTime.setText(newsListDTO.get(position).getComments().get(0).getComTime());

				TextView txtCommentExcitedCount = (TextView) v.findViewById(R.id.list_adapter_commented_comment_excite_count_1);
				txtCommentExcitedCount.setText(newsListDTO.get(position).getComments().get(0).getComExcited() + " excited");

				if(Integer.parseInt(newsListDTO.get(position).getCommentCount().toString()) > 1){
					
					imageURL = imageURLMapping.get(newsListDTO.get(position).getComments().get(1).getCommentBy());
					profileName = nameMapping.get(newsListDTO.get(position).getComments().get(1).getCommentBy());

					ImageView imgCommentProfilePic2 = (ImageView) v.findViewById(R.id.list_adapter_commented_profile_pic_2);

					new single_image_fetch().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,imageURL,imgCommentProfilePic2);

					TextView txtCommentProfileName2 = (TextView) v.findViewById(R.id.list_adapter_commented_profile_name_2);

					txtCommentProfileName2.setText(profileName);

					TextView txtComment2 = (TextView) v.findViewById(R.id.list_adapter_commented_comment_2);

					txtComment2.setText(newsListDTO.get(position).getComments().get(1).getComment());

					TextView txtCommentExciting2 = (TextView) v.findViewById(R.id.list_adapter_commented_comment_excite_2);

					txtCommentExciting2.setText(newsListDTO.get(position).getComments().get(1).getComExcitedMine().equals("0")?"Unexciting":"Exciting");

					TextView txtCommentTime2 = (TextView) v.findViewById(R.id.list_adapter_commented_comment_time_2);

					txtCommentTime2.setText(newsListDTO.get(position).getComments().get(1).getComTime());

					TextView txtCommentExcitedCount2 = (TextView) v.findViewById(R.id.list_adapter_commented_comment_excite_count_2);
					txtCommentExcitedCount2.setText(newsListDTO.get(position).getComments().get(1).getComExcited() + " excited");

				}

			}

			/*

	           // NEED TO RETRIEVE VALUES FROM DATABASE 
	           int count = 0 ;

	           // database 
	           for(int p=0; p< count;p++){

	        	   LinearLayout layout = new LinearLayout(getApplicationContext());	

		           LinearLayout layout_Left = new LinearLayout(getApplicationContext());
		           LinearLayout layout_Right = new LinearLayout(getApplicationContext());

		           LinearLayout image = new LinearLayout(getApplicationContext());
		           LayoutParams lp1 = new LayoutParams(LayoutParams.MATCH_PARENT,40);
		           lp1.weight=4;

		           LinearLayout message = new LinearLayout(getApplicationContext());
		           LayoutParams lp2 = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
		           lp2.weight=1;

		           layout_Left.addView(image);
		           layout_Left.addView(message);

		           LinearLayout timeago = new LinearLayout(getApplicationContext());
		           LayoutParams lp21 = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
		           lp21.weight=1;

		           LinearLayout exciting = new LinearLayout(getApplicationContext());
		           LayoutParams lp22 = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
		           lp22.weight=1;

		           //=====================
		           layout_Right.addView(timeago);
		           layout_Right.addView(exciting);

		           //====================
		           layout.addView(layout_Left);
		           layout.addView(layout_Right);

		           viewHolder.comment.addView(layout);
	           }


			 */
			return v;  
		}  

		public void updateEntries(List<NewsFeedDTO> newsList){
			newsListDTO.clear();
			newsListDTO = newsList;
			notifyDataSetChanged();
		}

	}  
	class CompleteListViewHolder {  
		public TextView name;  
		public TextView message;  
		ImageView list_photo;
		LinearLayout comment;
		public CompleteListViewHolder(View base) {  
			name = (TextView) base.findViewById(R.id.list_name);  
			message = (TextView) base.findViewById(R.id.list_message);  
			comment = (LinearLayout) base.findViewById(R.id.comment);  
			list_photo  = (ImageView) base.findViewById(R.id.list_photo); 
		}  

	}  
	void feed_deploy() {



		rlPost = new RelativeLayout(this);
		ivPimage = new ImageView(this);
		llPostRHS = new LinearLayout(this);
		tvName = new TextView(this);
		rlContent = new RelativeLayout(this);
		ivContent = new ImageView(this);
		tvContent = new TextView(this);
		tvFile = new TextView(this);

		llTime = new LinearLayout(this);
		tvExciting = new TextView(this);
		tvTime = new TextView(this);

		llResponse = new LinearLayout(this);
		tvCount = new TextView(this);

		llCommentList = new LinearLayout(this);

		rlComment = new RelativeLayout(this);
		etComment = new EditText(this);
		btnCommentPost = new Button(this);

		rlContent.addView(tvContent);
		rlContent.addView(ivContent);
		rlContent.addView(tvFile);

		llTime.addView(tvExciting);
		llTime.addView(tvTime);

		llResponse.addView(tvCount);

		rlComment.addView(etComment);
		rlComment.addView(btnCommentPost);

		llPostRHS.addView(tvName);
		llPostRHS.addView(rlContent);
		llPostRHS.addView(llTime);
		llPostRHS.addView(llResponse);
		llPostRHS.addView(llCommentList);
		llPostRHS.addView(rlComment);

		rlPost.addView(ivPimage);
		rlPost.addView(llPostRHS);
		scrNews.addView(rlPost);

		tvCount.setOnClickListener(this);
		tvExciting.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Log.e("Button Clicked", "Excting Button has been clicked");
				new SendResponse()
				.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			}
		});

		if (adata != null) {

			try {

				JSONObject data = adata.getJSONObject(0);

				JSONObject name = data.getJSONObject(AppProperties.NAME);

				Iterator iteratorName = name.keys();
				while (iteratorName.hasNext()) {
					String key = (String) iteratorName.next();
					JSONObject value = name.getJSONObject(key);
					Name_Title.add(value + "");
					Name_ID.add(key);

				}

				JSONObject pimage = data
						.getJSONObject(AppProperties.PROFILE_IMAGE);
				Iterator iteratorPIImage = name.keys();
				while (iteratorPIImage.hasNext()) {
					String key = (String) iteratorPIImage.next();
					JSONObject value = name.getJSONObject(key);
					PI_Image.add( value + "");

				}

				JSONArray action = new JSONArray(data.getString("action"));
				FeedData insertdata = new FeedData(getApplicationContext());

				for (int w = 0; w < action.length(); w++) {

					JSONObject temp = new JSONObject(action.get(w).toString());

					String actiontype = temp.getString("actiontype");
					String actionby = temp.getString("actionby");
					String postby = temp.getString("postby");
					String time = temp.getString("time");
					String actionon = temp.getString("actionon");
					String excited = temp.getString("excited");
					String meta = temp.getString("meta");
					String title = temp.getString("postby");
					String life_is_fun = temp.getString("life_is_fun");
					String page = temp.getString("page");
					String files = temp.getString("file");
					String video = temp.getString("video");
					String pageid = temp.getString("pageid");

					insertdata.insertNewsFeed(actiontype, actionby, postby,
							time, actionon, excited, meta, title, life_is_fun,
							page, files, video, pageid);
				}

				/*
				 * tvName.setText(Html.fromHtml("<b>"+name.getString(postby)+"</b>"
				 * ));
				 * 
				 * 
				 * 
				 * 
				 * 
				 * 
				 * new single_image_fetch().executeOnExecutor(AsyncTask.
				 * THREAD_POOL_EXECUTOR,pimage.getString(postby),ivPimage);
				 * tvTime
				 * .setText(CommonMethods.getTime(Long.parseLong(temp.getString
				 * ("time"))));
				 * 
				 * if(actiontype.equalsIgnoreCase("1") ||
				 * actiontype.equalsIgnoreCase("301") ||
				 * actiontype.equalsIgnoreCase("403")) {
				 * if(temp.getString("page") != null) {
				 * tvContent.setText(temp.getString("page"));
				 * rlContent.removeView(ivContent); } } else
				 * if(actiontype.equalsIgnoreCase("2800") ||
				 * actiontype.equalsIgnoreCase("328") ||
				 * actiontype.equalsIgnoreCase("428")) {
				 * if(temp.getString("question") != null) {
				 * tvContent.setText(temp.getString("question"));
				 * rlContent.removeView(ivContent); } } else
				 * if(actiontype.equalsIgnoreCase("6") ||
				 * actiontype.equalsIgnoreCase("306") ||
				 * actiontype.equalsIgnoreCase("406")) {
				 * tvContent.setText(temp.getString("page"));
				 * rlContent.removeView(tvFile); final String file =
				 * temp.getString("file"); new
				 * single_image_fetch().executeOnExecutor
				 * (AsyncTask.THREAD_POOL_EXECUTOR
				 * ,temp.getString("file"),ivContent);
				 * ivContent.setOnClickListener(new OnClickListener() {
				 * 
				 * @Override public void onClick(View arg0) { try{
				 * Log.e("File Textview","File TextView clicked"); Intent intent
				 * = new Intent(Intent.ACTION_VIEW, Uri.parse(file));
				 * intent.setDataAndType(Uri.parse(file), "image/*");
				 * startActivity(intent);
				 * 
				 * } catch(Exception otherException) {
				 * otherException.printStackTrace(); } } });
				 * 
				 * } else if(actiontype.equalsIgnoreCase("2600") ||
				 * actiontype.equalsIgnoreCase("326") ||
				 * actiontype.equalsIgnoreCase("426")) {
				 * tvContent.setText(temp.getString("page"));
				 * tvFile.setText(temp.getString("caption"));
				 * tvFile.setPaintFlags(tvFile.getPaintFlags() |
				 * Paint.UNDERLINE_TEXT_FLAG); rlContent.removeView(ivContent);
				 * final String file = temp.getString("file"); //new
				 * single_image_fetch
				 * ().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR
				 * ,AppProperties.PDF_ICON,ivContent);
				 * tvFile.setOnClickListener(new OnClickListener() {
				 * 
				 * @Override public void onClick(View arg0) { try{
				 * Log.e("File Textview","File TextView clicked"); WebView
				 * webview=new WebView(NewsFeed.this);
				 * webview.getSettings().setJavaScriptEnabled(true);
				 * webview.loadUrl
				 * ("http://docs.google.com/gview?embedded=true&url=" + file);
				 * 
				 * } catch(Exception otherException) {
				 * otherException.printStackTrace(); } } }); } else
				 * if(actiontype.equalsIgnoreCase("2500") ||
				 * actiontype.equalsIgnoreCase("325") ||
				 * actiontype.equalsIgnoreCase("425")) {
				 * tvContent.setText(temp.getString("page"));
				 * tvFile.setText(temp.getString("caption"));
				 * tvFile.setPaintFlags(tvFile.getPaintFlags() |
				 * Paint.UNDERLINE_TEXT_FLAG); rlContent.removeView(tvFile);
				 * final String file = temp.getString("file"); new
				 * single_image_fetch
				 * ().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR
				 * ,temp.getString("caption"),ivContent);
				 * ivContent.setOnClickListener(new OnClickListener() {
				 * 
				 * @Override public void onClick(View arg0) { try{
				 * Log.e("File Textview","File TextView clicked"); Intent intent
				 * = new Intent(Intent.ACTION_VIEW, Uri.parse(file));
				 * intent.setDataAndType(Uri.parse(file), "video/flv");
				 * startActivity(intent);
				 * 
				 * } catch(Exception otherException) {
				 * otherException.printStackTrace(); }
				 * 
				 * } }); } else if(actiontype.equalsIgnoreCase("1600") ||
				 * actiontype.equalsIgnoreCase("316") ||
				 * actiontype.equalsIgnoreCase("416")) {
				 * tvContent.setText(temp.getString("page"));
				 * tvFile.setText(temp.getString("title"));
				 * tvFile.setPaintFlags(tvFile.getPaintFlags() |
				 * Paint.UNDERLINE_TEXT_FLAG);
				 * //rlContent.removeView(ivContent); final String link =
				 * temp.getString("link"); new
				 * single_image_fetch().executeOnExecutor
				 * (AsyncTask.THREAD_POOL_EXECUTOR
				 * ,temp.getString("file"),ivContent);
				 * tvFile.setOnClickListener(new OnClickListener() {
				 * 
				 * @Override public void onClick(View arg0) { try{
				 * Log.e("File Textview","File TextView clicked"); WebView
				 * webview=new WebView(NewsFeed.this);
				 * webview.getSettings().setJavaScriptEnabled(true);
				 * webview.loadUrl(link);
				 * 
				 * } catch(Exception otherException) {
				 * otherException.printStackTrace(); }
				 * 
				 * } }); } else { if(temp.getString("page") != null) {
				 * tvContent.setText(temp.getString("is now following you"));
				 * rlContent.removeView(ivContent);
				 * rlContent.removeView(tvFile); } }
				 * tvCount.setText(temp.getString
				 * ("excited").length()+" "+"people excited at this"); JSONArray
				 * comments = new JSONArray(temp.getString("com").toString());
				 * RelativeLayout [] hllay = new
				 * RelativeLayout[comments.length()]; TextView [] tvComment =
				 * new TextView[comments.length()]; TextView [] tvComTime = new
				 * TextView[comments.length()]; TextView [] tvComExciting = new
				 * TextView[comments.length()]; TextView [] tvComExcitingCount =
				 * new TextView[comments.length()]; ImageView [] ivComment = new
				 * ImageView[comments.length()];
				 * 
				 * RelativeLayout.LayoutParams param_com_photo = new
				 * RelativeLayout.LayoutParams(80, 80);
				 * RelativeLayout.LayoutParams param_comment = new
				 * RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,
				 * LayoutParams.WRAP_CONTENT); RelativeLayout.LayoutParams
				 * param_com_time = new
				 * RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				 * LayoutParams.WRAP_CONTENT); RelativeLayout.LayoutParams
				 * param_com_exciting = new
				 * RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				 * LayoutParams.WRAP_CONTENT); for(int
				 * j=0;j<comments.length();j++) { JSONObject com = new
				 * JSONObject(comments.get(j).toString()); hllay[j] = new
				 * RelativeLayout(this); ivComment[j] = new ImageView(this);
				 * param_com_photo.addRule(RelativeLayout.ALIGN_PARENT_TOP);
				 * ivComment[j].setLayoutParams(param_com_photo); new
				 * single_image_fetch
				 * ().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR
				 * ,pimage.get(com.getString("commentby")),ivComment[j]);
				 * hllay[j].addView(ivComment[j]); tvComment[j] = new
				 * TextView(this); tvComment[j].setText(Html.fromHtml("<b>"+
				 * name.getString(com.getString("commentby")) + "</b>" +" "
				 * +com.getString("comment")));
				 * param_comment.addRule(RelativeLayout
				 * .RIGHT_OF,ivComment[j].getId()); param_comment.setMargins(90,
				 * 0, 0, 0); tvComment[j].setLayoutParams(param_comment);
				 * 
				 * tvComTime[j] = new TextView(this);
				 * tvComTime[j].setText(CommonMethods
				 * .getTime(Long.parseLong(com.getString("com_time"))));
				 * 
				 * param_com_time.addRule(RelativeLayout.RIGHT_OF,ivComment[j].getId
				 * ());
				 * param_com_time.addRule(RelativeLayout.BELOW,tvComment[j].
				 * getId());
				 * param_com_time.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
				 * param_com_time.setMargins(90, 60, 0, 0);
				 * tvComTime[j].setLayoutParams(param_com_time);
				 * 
				 * 
				 * tvComExciting[j] = new TextView(this);
				 * tvComExciting[j].setText("Exciting");
				 * param_com_exciting.addRule
				 * (RelativeLayout.RIGHT_OF,tvComTime[j].getId());
				 * param_com_exciting
				 * .addRule(RelativeLayout.BELOW,tvComment[j].getId());
				 * param_com_exciting
				 * .addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
				 * param_com_exciting.setMargins(275, 60, 0, 0);
				 * tvComExciting[j].setLayoutParams(param_com_exciting);
				 * 
				 * 
				 * tvComExcitingCount[j] = new TextView(this);
				 * tvComExcitingCount
				 * [j].setText(com.getString("com_excited").length()+"");
				 * param_com_exciting
				 * .addRule(RelativeLayout.RIGHT_OF,tvComExciting[j].getId());
				 * param_com_exciting
				 * .addRule(RelativeLayout.BELOW,tvComment[j].getId());
				 * param_com_exciting
				 * .addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
				 * param_com_exciting.setMargins(375, 60, 0, 0);
				 * tvComExcitingCount[j].setLayoutParams(param_com_exciting);
				 * 
				 * hllay[j].addView(tvComment[j]);
				 * hllay[j].addView(tvComTime[j]);
				 * hllay[j].addView(tvComExciting[j]);
				 * hllay[j].addView(tvComExcitingCount[j]);
				 * llCommentList.addView(hllay[j]); }
				 */
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {

		}
	}

	class FeedFetch extends AsyncTask<Void, Void, List<NewsFeedDTO>> {

		// private ProgressDialog progressDialog;
		String action = "comment";

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected List<NewsFeedDTO> doInBackground(Void... params) {
			// progressDialog = ProgressDialog.show(NewsFeed.this, "Wait",
			// "Downloading...");

			try {
				ArrayList<NameValuePair> apiParams = new ArrayList<NameValuePair>();
				apiParams.add(new BasicNameValuePair(AppProperties.DATABASE,
						session.getValue(AppProperties.DATABASE)));
				apiParams.add(new BasicNameValuePair("start", start));
				apiParams.add(new BasicNameValuePair("action",
						"news_feed_mobile"));
				apiParams.add(new BasicNameValuePair("auth", session
						.getValue(AppProperties.PROFILE_ID)));
				apiParams.add(new BasicNameValuePair("PHPSESSID", session
						.getValue(AppProperties.SESSION_ID)));
				Log.e("Chat NEW Parameters", apiParams.toString());
				adata = CommonMethods.loadJSONData(AppProperties.URL,
						AppProperties.METHOD_GET, apiParams);
				if (adata != null) {
					Log.e("Response", adata.toString());
				}

			} catch (Exception e) {
				System.out.println("Some Unknown exception occured");
				e.printStackTrace();
			}

			// SEPERATING JSON 
			try {
				JSONObject data = adata.getJSONObject(0);
				JSONObject name = data.getJSONObject(AppProperties.NAME);
				Iterator iteratorName = name.keys();
				while (iteratorName.hasNext()) {
					String key = iteratorName.next().toString();
					if(!("".equalsIgnoreCase(key))) {
						String value = name.getString(key);
						if(!imageURLMapping.containsKey(key))
							nameMapping.put(key, value);
					}

				}

				JSONObject pimage = data.getJSONObject(AppProperties.PROFILE_IMAGE);
				Iterator iteratorPIImage = pimage.keys();
				while (iteratorPIImage.hasNext()) {
					String key = iteratorPIImage.next().toString();
					if(!("".equalsIgnoreCase(key))) {
						String value = pimage.getString(key);
						if(!imageURLMapping.containsKey(key))
							imageURLMapping.put(key, value);
					}

				}

				JSONArray action = new JSONArray(data.getString("action"));
				FeedData insertdata = new FeedData(getApplicationContext());

				newsFeedDTOList = new ArrayList<NewsFeedDTO>();

				for (int w = 0; w < action.length(); w++) {

					NewsFeedDTO newsFeedDTO = new NewsFeedDTO();												
					JSONObject temp = new JSONObject(action.get(w).toString());
					if(temp.has("actiontype")) 
						newsFeedDTO.setActiontype(temp.getString("actiontype"));


					if(temp.has("actionby"))
						newsFeedDTO.setActionby(temp.getString("actionby"));
					if(temp.has("postby"))
						newsFeedDTO.setPostby(temp.getString("postby"));
					if(temp.has("time"))
						newsFeedDTO.setTime(temp.getString("time"));
					if(temp.has("actionon"))
						newsFeedDTO.setActionon(temp.getString("actionon"));
					if(temp.has("excited")) {
						List<String> excitedArrayList = new ArrayList<String>();
						JSONArray excitedArray = temp.getJSONArray("excited");
						for(int excitedArrayIndex = 0; excitedArrayIndex < excitedArray.length(); excitedArrayIndex++) {
							excitedArrayList.add(excitedArray.get(excitedArrayIndex).toString());
						}
						newsFeedDTO.setExcitedList(excitedArrayList);
					}
					if(temp.has("meta"))
						newsFeedDTO.setMeta(temp.getString("meta"));
					if(temp.has("title"))
						newsFeedDTO.setTitle(temp.getString("title"));
					if(temp.has("life_is_fun"))
						newsFeedDTO.setLifeIsFun(temp.getString("life_is_fun"));
					if(temp.has("page"))
						newsFeedDTO.setPage(temp.getString("page"));
					if(temp.has("file"))
						newsFeedDTO.setFiles(temp.getString("file"));
					if(temp.has("video"))
						newsFeedDTO.setVideo(temp.getInt("video")+"");
					if(temp.has("pageid"))
						newsFeedDTO.setPageid(temp.getString("pageid"));
					if(temp.has("host"))
						newsFeedDTO.setHost(temp.getString("host"));
					if(temp.has("actionid"))
						newsFeedDTO.setActionId(temp.getString("actionid"));
					if(temp.has("visible"))
						newsFeedDTO.setVisible(temp.getString("visible"));
					if(temp.has("link"))
						newsFeedDTO.setLink(temp.getString("link"));
					if(temp.has("comment_count"))
						newsFeedDTO.setCommentCount(temp.getString("comment_count"));
					if(temp.has("mood"))
						newsFeedDTO.setLink(temp.getString("mood"));
					if(temp.has("sex"))
						newsFeedDTO.setSex(temp.getString("sex"));
					if(temp.has("mname"))
						newsFeedDTO.setmName(temp.getString("mname"));
					if(temp.has("desc"))
						newsFeedDTO.setDesc(temp.getString("desc"));
					if(temp.has("momnetid"))
						newsFeedDTO.setMomnetid(temp.getString("momnetid"));
					if(temp.has("count"))
						newsFeedDTO.setCount(temp.getString("count"));
					if(temp.has("postby"))
						newsFeedDTO.setPostBy(temp.getString("postby"));
					if(temp.has("caption"))
						newsFeedDTO.setCaption(temp.getString("caption"));
					if(temp.has("letter_title"))
						newsFeedDTO.setLetterTitle(temp.getString("letter_title"));
					if(temp.has("letter_content"))
						newsFeedDTO.setLetterContent(temp.getString("letter_content"));

					if(temp.has("com")) { 
						JSONArray comArrayObj = temp.getJSONArray("com");
						List<ComentsDTO> commentsDTOList = new ArrayList<ComentsDTO>();
						for(int comArrayIndex = 0; comArrayIndex < comArrayObj.length(); comArrayIndex++) {
							ComentsDTO commentsDTO = new ComentsDTO();
							JSONObject comObject = new JSONObject(comArrayObj.get(comArrayIndex).toString());
							if(comObject.has("com_time"))
								commentsDTO.setComTime(comObject.getString("com_time"));
							if(comObject.has("com_excited_mine"))
								commentsDTO.setComExcitedMine(comObject.getString("com_excited_mine"));
							if(comObject.has("com_excited"))
								commentsDTO.setComExcited(comObject.getString("com_excited"));
							if(comObject.has("remove"))
								commentsDTO.setRemove(comObject.getString("remove"));
							if(comObject.has("com_actionid"))
								commentsDTO.setComActionId(comObject.getString("com_actionid"));
							if(comObject.has("com_pageid"))
								commentsDTO.setComPageId(comObject.getString("com_pageid"));
							if(comObject.has("commentby"))
								commentsDTO.setCommentBy(comObject.getString("commentby"));
							if(comObject.has("comment"))
								commentsDTO.setComment(comObject.getString("comment"));

							commentsDTOList.add(commentsDTO);
						}
						newsFeedDTO.setComments(commentsDTOList);
					}


					if(temp.has("photo")) { 
						JSONArray photoArrayObj = temp.getJSONArray("photo");
						List<Photo> photoDTOList = new ArrayList<Photo>();
						for(int photoArrayIndex = 0; photoArrayIndex < photoArrayObj.length(); photoArrayIndex++) {
							Photo photoDTO = new Photo();
							JSONObject photoObject = new JSONObject(photoArrayObj.get(photoArrayIndex).toString());
							if(photoObject.has("file"))
								photoDTO.setFile(photoObject.getString("file"));
							if(photoObject.has("actionid"))
								photoDTO.setActionId(photoObject.getString("actionid"));
							if(photoObject.has("life_is_fun"))
								photoDTO.setLifeIsFun(photoObject.getString("life_is_fun"));
							if(photoObject.has("actionon"))
								photoDTO.setActionOn(photoObject.getString("actionon"));
							if(photoObject.has("actionby"))
								photoDTO.setActionBy(photoObject.getString("actionby"));
							if(photoObject.has("time"))
								photoDTO.setTime(photoObject.getString("time"));

							photoDTOList.add(photoDTO);
						}

						newsFeedDTO.setPhoto(photoDTOList);
					}

					if(temp.has("option")) { 
						JSONArray optionArrayObj = temp.getJSONArray("option");
						List<Option> optionDTOList = new ArrayList<Option>();
						for(int optionArrayIndex = 0; optionArrayIndex < optionArrayObj.length(); optionArrayIndex++) {
							Option optionDTO = new Option();
							JSONObject optionObject = new JSONObject(optionArrayObj.get(optionArrayIndex).toString());
							if(optionObject.has("opt"))
								optionDTO.setOption(optionObject.getString("opt"));
							if(optionObject.has("optid"))
								optionDTO.setOptionId(optionObject.getString("optid"));
							if(optionObject.has("mine"))
								optionDTO.setMine(optionObject.getString("mine"));
							if(optionObject.has("count"))
								optionDTO.setCount(optionObject.getString("count"));
							if(optionObject.has("percent"))
								optionDTO.setPercent(optionObject.getString("percent"));

							optionDTOList.add(optionDTO);
						}

						newsFeedDTO.setOptions(optionDTOList);
					}
					//					insertdata.insertNewsFeed(actiontype, actionby, postby,
					//							time, actionon, excited, meta, title, life_is_fun,
					//							page, files, video, pageid);
					newsFeedDTOList.add(newsFeedDTO);
				}

			} catch (Exception e) {
				e.printStackTrace();
				Log.e("error", e.getMessage());
			}

			return newsFeedDTOList;
		}

		@Override
		protected void onPostExecute(List<NewsFeedDTO> result) {

			//feed_deploy();
			// progressDialog.dismiss();

			FeedDisplay(result);

		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub

		Log.e("Svroll Position x", scrNews.getScrollX() + "");
		Log.e("Svroll Position y", scrNews.getScrollY() + "");

		Log.e("Svroll Position h", v.getHeight() + "");

		int h = scrNews.getChildAt(0).getHeight();
		if (scrNews.getScrollY() > scrollPosition && loading == false) {
			scrollPosition = scrNews.getScrollY();
			if (scrollPosition > 0.36 * h) {
				loading = true;
				Log.e("Loading more data", "Loading  more data");
				int x = Integer.parseInt(start) + 10;
				start = x + "";
				new FeedFetch()
				.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				scrollPosition = scrNews.getScrollY();
			}
		}
		return false;
	}
}