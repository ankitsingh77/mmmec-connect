package com.quipmate2.features;


import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.quipmate2.R;
import com.quipmate2.constants.AppProperties;
import com.quipmate2.loadwebimageandcache.ImageLoader;
import com.quipmate2.utils.CommonMethods;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class StatusUpdate extends Activity implements OnClickListener {
	
	private ImageButton updateStatus , attachPhoto , attachMood, attachVideo;
	private ImageView profilePic , selectedImg;
	private TextView txtSelectedImg, profileName;
	private Session session;
	private String action="post_status";
	private EditText status;
	private JSONArray jsonTask;
	private JSONObject result;
	private String content;
	private ArrayList<NameValuePair> nameValuePairs;
	private AlertDialog dialogPhoto, dialogVideo;
	private boolean photoAttached, videoAttached;
	private String actionPhoto="photo_upload";
	private String imagepath;
	private int moodNumber;
	
	final static int RESULT_LOAD_IMAGE=0;
	final static int RESULT_SELECT_MOOD=2;
	private static final int IMAGE_PICK = 3;
	private static final int IMAGE_CAPTURE = 4;
	private static final int VIDEO_PICK = 5;
	private static final int VIDEO_CAPTURE = 6;
	
	private Bitmap setphoto;
	private String mCurrentPhotoPath;
	private static final String JPEG_FILE_PREFIX = "IMG_";
	private static final String JPEG_FILE_SUFFIX = ".jpg";
	private String videoPath;

	private AlbumStorageDirFactory mAlbumStorageDirFactory = null;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.status_update);
		getActionBar().setTitle("Update Status");
		init();
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		mAlbumStorageDirFactory = new BaseAlbumDirFactory();
		
		updateStatus.setOnClickListener(this);
		attachPhoto.setOnClickListener(this);
		attachMood.setOnClickListener(this);
		attachVideo.setOnClickListener(this);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
        case android.R.id.home:
            // app icon in action bar clicked; go home
           finish();
            return true;
        default:
            return super.onOptionsItemSelected(item);
    }
	}

	private void init() {
		// TODO Auto-generated method stub
		photoAttached = false;
		moodNumber = -1;
		videoAttached = false;
		session = new Session(StatusUpdate.this);
		updateStatus = (ImageButton) findViewById(R.id.update_status);
		attachPhoto = (ImageButton) findViewById(R.id.attach_photo);
		attachMood = (ImageButton) findViewById(R.id.mood);
		attachVideo = (ImageButton) findViewById(R.id.attach_video);
		status = (EditText) findViewById(R.id.status_content);
		profilePic = (ImageView) findViewById(R.id.profilepic);
		profileName = (TextView) findViewById(R.id.tv_name);
		selectedImg = (ImageView) findViewById(R.id.selected_image);
		txtSelectedImg = (TextView) findViewById(R.id.tv_selected_image);
		
		setDialogPhoto();
		setDialogVideo();
		
		//displaying profile pic and name of the user.
		profileName.setText(session.getValue(AppProperties.MY_PROFILE_NAME));
		Thread showPic = new Thread() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				final ImageLoader load = new ImageLoader(StatusUpdate.this);
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						load.DisplayImage(session.getValue(AppProperties.MY_PROFILE_PIC),profilePic);
					}
				});
			}
		};
		showPic.start();
		
		
	}

	//dialog to select from gallery or capture image
	private void setDialogPhoto() {
		// TODO Auto-generated method stub
		final String[] items = new String[] { "Take from camera",
        "Select from gallery" };
         ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
        android.R.layout.select_dialog_item, items);
         AlertDialog.Builder builder = new AlertDialog.Builder(this);

         builder.setTitle("Select Image");
         builder.setIcon(getResources().getDrawable(R.drawable.ic_action_photo));
         builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int item) {

        if (item == 0) {
        	Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File f = null;
			
			try {
				f = setUpPhotoFile();
				mCurrentPhotoPath = f.getAbsolutePath();
				takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
			} catch (IOException e) {
				e.printStackTrace();
				f = null;
				mCurrentPhotoPath = null;
			}
			
			startActivityForResult(takePictureIntent, IMAGE_CAPTURE);

        } else { // pick from file
            Intent intent = new Intent(
                    Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(
                    Intent.createChooser(intent, "Choose a Photo"),
                    IMAGE_PICK);
        }
    }
});

dialogPhoto = builder.create();
	}
	
	public void setDialogVideo(){
		final String[] items = new String[] { "Take from camera",
        "Select from gallery" };
         ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
        android.R.layout.select_dialog_item, items);
         AlertDialog.Builder builder = new AlertDialog.Builder(this);

         builder.setTitle("Select Video");
         builder.setIcon(getResources().getDrawable(R.drawable.ic_media_video_poster));
         builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int item) {
				// TODO Auto-generated method stub
				
				if (item == 0) {
					Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
					startActivityForResult(takeVideoIntent, VIDEO_CAPTURE);

		        } else { 
		        	Intent intent = new Intent(
		                    Intent.ACTION_PICK,
		                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		            intent.setType("video/*");
		            startActivityForResult(Intent.createChooser(intent, "Choose a Video"), VIDEO_PICK);
		        }
			}
		});
         
         dialogVideo = builder.create();
	}
	
       private File setUpPhotoFile() throws IOException {
		
		File f = createImageFile();
		mCurrentPhotoPath = f.getAbsolutePath();
		
		return f;
	}
       
       @SuppressLint("SimpleDateFormat")
	private File createImageFile() throws IOException {
   		// Create an image file name..must be unique
   		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
   		String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
   		File albumF = getAlbumDir();
   		File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);
   		return imageF;
   	}
       
       private File getAlbumDir() {
   		File storageDir = null;

   		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
   			
   			storageDir = mAlbumStorageDirFactory.getAlbumStorageDir(getAlbumName());

   			if (storageDir != null) {
   				if (! storageDir.mkdirs()) {
   					if (! storageDir.exists()){
   						Log.d("CameraSample", "failed to create directory");
   						return null;
   					}
   				}
   			}
   			
   		} else {
   			Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
   		}
   		
   		return storageDir;
   	}
       
       private String getAlbumName() {
   		return getString(R.string.album_name);
   	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int id=v.getId();
		switch(id){
		case R.id.update_status:
			content = status.getText().toString();
			
			//if no photo attached call status update task .
            if(photoAttached){
                nameValuePairs = new  ArrayList<NameValuePair>();
                System.out.println(imagepath);
               Log.e("Photo Attached","Yes");
                nameValuePairs.add(new BasicNameValuePair(AppProperties.ACTION, actionPhoto));
                nameValuePairs.add(new BasicNameValuePair("photo_box",imagepath));
                nameValuePairs.add(new BasicNameValuePair("photo_description",status.getText().toString()));
                nameValuePairs.add(new BasicNameValuePair("photo_hidden_profileid", session.getValue(AppProperties.PROFILE_ID)));
                nameValuePairs.add(new BasicNameValuePair("auth", session.getValue(AppProperties.PROFILE_ID)));
                new PhotoUpload().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                
            }
            else if(videoAttached){
            	 nameValuePairs = new  ArrayList<NameValuePair>();
                 System.out.println(videoPath);
                Log.e("Video Attached","Yes");
                 nameValuePairs.add(new BasicNameValuePair(AppProperties.ACTION, actionPhoto));
                 nameValuePairs.add(new BasicNameValuePair("photo_box",videoPath));
                 nameValuePairs.add(new BasicNameValuePair("photo_description",status.getText().toString()));
                 nameValuePairs.add(new BasicNameValuePair("photo_hidden_profileid", session.getValue(AppProperties.PROFILE_ID)));
                 nameValuePairs.add(new BasicNameValuePair("auth", session.getValue(AppProperties.PROFILE_ID)));
                 new PhotoUpload().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
            else if((!content.equals(null) && !content.equals("") && photoAttached==false) || (!content.equals(null) && !content.equals("") && videoAttached==false) || moodNumber != -1){	
            	new UpdateStatus().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
            break;
            
		case R.id.attach_photo:
			dialogPhoto.show();
			break;
			
		case R.id.mood:
			Intent moodpick= new Intent(this,MoodSelect.class);
			startActivityForResult(moodpick, RESULT_SELECT_MOOD);
			break;
		
		case R.id.attach_video :
			dialogVideo.show();
			
		}
	}
	

	public class VideoCapture extends AsyncTask<Void, Void, Void>
	{
		ProgressDialog pDialog;
		@Override
		protected void onPreExecute() {
			
		}
		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			Log.e("Status Update - video capture upload", "inside doInbackground for video capture in satus update");
			Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
		    if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
		        int REQUEST_VIDEO_CAPTURE=1;
				startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
		    }
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
		if(pDialog.isShowing())
			pDialog.dismiss();
		}
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		//super.onActivityResult(requestCode, resultCode, data);
		try{
		
		if (resultCode == RESULT_OK && requestCode == IMAGE_PICK
	            || requestCode == IMAGE_CAPTURE) {
	        switch (requestCode) {
	        case IMAGE_PICK:
	            this.imageFromGallery(resultCode, data);
	            selectedImg.setImageBitmap(null);
	            selectedImg.setImageBitmap(setphoto);
	            txtSelectedImg.setText(imagepath);
	            txtSelectedImg.setVisibility(View.VISIBLE);
	            photoAttached = true;
	            moodNumber = -1;
	            videoAttached = false;
	            break;
	        case IMAGE_CAPTURE:
	        	handlePhoto();
	            photoAttached = true;
	            moodNumber = -1;
	            videoAttached = false;
	            break;
	        default:
	            break;
	        }
	    } 
		else if(resultCode == RESULT_OK && requestCode == VIDEO_CAPTURE
	            || requestCode == VIDEO_PICK && data != null){
				
				switch(requestCode){
				case VIDEO_PICK:
					Uri selectVideo = data.getData();
					videoPath = getPath(selectVideo); 
					photoAttached = false;
					moodNumber = -1;
					videoAttached = true;
					
					Bitmap thumb = ThumbnailUtils.createVideoThumbnail(videoPath,
						    MediaStore.Images.Thumbnails.MINI_KIND);
					selectedImg.setImageBitmap(thumb);
					
					txtSelectedImg.setText("Video Path : "+videoPath);
					txtSelectedImg.setVisibility(View.VISIBLE);
					status.setHint("Write something about the video.");
					break;
					
				case VIDEO_CAPTURE:
					Uri capturedVideo = data.getData();
					videoPath = getPath(capturedVideo);
					photoAttached = false;
					moodNumber = -1;
					videoAttached = true;
					Bitmap thumbVideo = ThumbnailUtils.createVideoThumbnail(videoPath, MediaStore.Images.Thumbnails.MINI_KIND);
					selectedImg.setImageBitmap(thumbVideo);	
					txtSelectedImg.setText("Video Path : "+videoPath);
					txtSelectedImg.setVisibility(View.VISIBLE);
					status.setHint("Write something about the video.");
				    Log.e("After video capture","Video focus");
					VideoCapture.execute(new Runnable() {		
						@Override
						public void run() {
							// TODO Auto-generated method stub	
						}
					});
		            break;
				}
			}
		else
			if (requestCode == RESULT_SELECT_MOOD && resultCode == RESULT_OK && data != null){
				moodNumber = data.getIntExtra("mood_number", -1) + 1;
				//attachPhoto.setVisibility(View.INVISIBLE);
				String feeling=getResources().getStringArray(R.array.moods)[moodNumber-1];
				TypedArray moodimg = getResources().obtainTypedArray(R.array.mood_icons);
				Bitmap bitmap = BitmapFactory.decodeResource(getResources(), moodimg.getResourceId(moodNumber-1, -1));
				if(bitmap != null){
				selectedImg.setImageBitmap(bitmap);
				txtSelectedImg.setText("- feeling " + feeling);
				txtSelectedImg.setVisibility(View.VISIBLE);
				status.setHint(getString(R.string.mood_hint));
				moodimg.recycle();
				
				videoAttached = false;
				photoAttached = false;
				//new UpdateStatus().execute();
				}
			}
			
		}
		catch(Exception e){
			
		}
		
		} 
	
	private void handlePhoto() {

		if (mCurrentPhotoPath != null) {
			setPic();
			galleryAddPic();
			
		}

	}
	
	private void setPic() {

		/* There isn't enough memory to open up more than a couple camera photos */
		/* So pre-scale the target bitmap into which the file is decoded */

		/* Get the size of the ImageView */
		int targetW = selectedImg.getWidth();
		int targetH = selectedImg.getHeight();

		/* Get the size of the image */
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
		int photoW = bmOptions.outWidth;
		int photoH = bmOptions.outHeight;
		
		/* Figure out which way needs to be reduced less */
		int scaleFactor = 1;
		if ((targetW > 0) || (targetH > 0)) {
			scaleFactor = Math.min(photoW/targetW, photoH/targetH);	
		}

		/* Set bitmap options to scale the image decode target */
		bmOptions.inJustDecodeBounds = false;
		bmOptions.inSampleSize = scaleFactor;
		bmOptions.inPurgeable = true;

		/* Decode the JPEG file into a Bitmap */
		Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
		
		/* Associate the Bitmap to the ImageView */
		selectedImg.setImageBitmap(bitmap);
		txtSelectedImg.setText(mCurrentPhotoPath);
		imagepath = mCurrentPhotoPath;
		txtSelectedImg.setVisibility(View.VISIBLE);
		
	}

	private void galleryAddPic() {
		    Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
			File f = new File(mCurrentPhotoPath);
		    Uri contentUri = Uri.fromFile(f);
		    mediaScanIntent.setData(contentUri);
		    this.sendBroadcast(mediaScanIntent);
	}
	
	
	private void imageFromGallery(int resultCode, Intent data) {
	    Uri selectedImage = data.getData();
	    String[] filePathColumn = { MediaStore.Images.Media.DATA };

	    Cursor cursor = getContentResolver().query(selectedImage,
	            filePathColumn, null, null, null);
	    cursor.moveToFirst();

	    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);

	     imagepath= cursor.getString(columnIndex);
	    cursor.close();

	    setphoto = BitmapFactory.decodeFile(imagepath);

	}


	public String getPath(Uri uri) {
	    String[] projection = { MediaStore.Images.Media.DATA };
	    Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
	    int column_index = cursor
	            .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
	    cursor.moveToFirst();
	    return cursor.getString(column_index);
	}
	
	
		
	public class PhotoUpload extends AsyncTask<Void, Void, Void>
	{
		ProgressDialog pDialog;
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			pDialog = new ProgressDialog(StatusUpdate.this);
			pDialog.setMessage("Uploading image");
			pDialog.show();
		}
		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			Log.e("Status Update - photo upload", "inside doInbackground for photo upload in satus update");
			JSONArray result = CommonMethods.loadJSONData(AppProperties.URL,AppProperties.METHOD_POST,nameValuePairs);
		        System.out.println(result);
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result2) {
			// TODO Auto-generated method stub
			
			try{
				Log.e("Msg", result.toString());
				if((result.has(AppProperties.ACK))){
					String ack = result.getString(AppProperties.ACK);
					if(ack.equals("true")){
						Toast.makeText(StatusUpdate.this, getResources().getString(R.string.success_status),Toast.LENGTH_LONG).show();
					}
				}
				if(result.has(getString(R.string.error))){
					Toast.makeText(StatusUpdate.this, getResources().getString(R.string.status_error),Toast.LENGTH_LONG).show();	
				}
			}
			catch(JSONException e){
				e.printStackTrace();
			}
		if(pDialog.isShowing())
			pDialog.dismiss();
		
		finish();
		}
	}
	
  public class UpdateStatus extends AsyncTask<Void, Void, Void>
  {
	  ProgressDialog pdialog;
	  @Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		pdialog = new ProgressDialog(StatusUpdate.this);
		pdialog.setMessage("Updating Status");
		pdialog.show();
	}

	@Override
	protected Void doInBackground(Void... params) { 
		// TODO Auto-generated method stub
		
		//no mood, call status update api
		if(moodNumber == -1){ 
			try{
					List<NameValuePair> apiParams = new ArrayList<NameValuePair>(); 
					apiParams.add(new BasicNameValuePair(AppProperties.ACTION, action));
					apiParams.add(new BasicNameValuePair(AppProperties.PAGE,content));
					apiParams.add(new BasicNameValuePair(AppProperties.PROFILE_ID, session
							.getValue(AppProperties.PROFILE_ID)));
					Log.e("Recent Message", "inside doInbackground for satus update");
					jsonTask = CommonMethods.loadJSONData(AppProperties.URL, AppProperties.METHOD_GET, apiParams);
					result = jsonTask.getJSONObject(0);
					
				}
				catch(JSONException e)
				{
					e.printStackTrace();
				}
		}
		//mood attached, call mood api
		else{
			try{
					List<NameValuePair> apiParams = new ArrayList<NameValuePair>();
					apiParams.add(new BasicNameValuePair(AppProperties.ACTION, "mood"));
					apiParams.add(new BasicNameValuePair(AppProperties.PROFILE_ID,
							session.getValue(AppProperties.PROFILE_ID)));
					apiParams.add(new BasicNameValuePair("mood_desc", status.getText().toString()));
					apiParams.add(new BasicNameValuePair("mood",moodNumber+""));
					jsonTask = CommonMethods.loadJSONData(AppProperties.URL, AppProperties.METHOD_GET, apiParams );
					if(jsonTask != null)
					result = jsonTask.getJSONObject(0);
			}
			catch(JSONException e){
				e.printStackTrace();
			}
		}
		return null;
	}
	
	@Override
	protected void onPostExecute(Void result2) {
		// TODO Auto-generated method stub
		try{
			if(result.has(AppProperties.ACK)){
				String ack = result.getString(AppProperties.ACK);
				if(ack.equals("true")){
					Toast.makeText(StatusUpdate.this, getResources().getString(R.string.success_status),Toast.LENGTH_LONG).show();
				}
			}
			if(result.has(getString(R.string.error))){
				Toast.makeText(StatusUpdate.this, getResources().getString(R.string.status_error),Toast.LENGTH_LONG).show();	
			}
		}
		catch(JSONException e){
			e.printStackTrace();
		}
		if(pdialog.isShowing())
			pdialog.dismiss();
		
		finish();
	} 
  }
}
