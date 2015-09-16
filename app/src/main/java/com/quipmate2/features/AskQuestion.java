package com.quipmate2.features;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.quipmate2.R;
import com.quipmate2.constants.AppProperties;
import com.quipmate2.loadwebimageandcache.ImageLoader;
import com.quipmate2.utils.CommonMethods;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

public class AskQuestion extends Activity implements OnClickListener {
	private ImageView profilePic;
	private TextView profileName;
	private Button btask,btaddOption;
	private EditText questionContent, optionContent;
	private Session session;
	private List<String> options;
	private ArrayAdapter<String> adapter;
	private ListView lvOptions;
	private ArrayList<NameValuePair> apiParams;
	private String question;
	private JSONObject result=null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ask_question);
		getActionBar().setTitle("Ask Question");
		getActionBar().setDisplayHomeAsUpEnabled(true);
		//hide the keyboard
				getWindow().setSoftInputMode(
					      WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		init();
		question = questionContent.getText().toString().trim();
		
		//delete an option
		lvOptions.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					final int position, long arg3) {
				// TODO Auto-generated method stub
				PopupMenu popup = new PopupMenu(AskQuestion.this,adapter.getView(position, arg1, arg0));
				popup.getMenuInflater().inflate(R.menu.popup_option_item, popup.getMenu());
				
				popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {  
		             public boolean onMenuItemClick(MenuItem item) {  
		             options.remove(position);
		             adapter.notifyDataSetChanged();
		              return true;  
		             }  
		            });  
				
				popup.show();
				return false;
			}
		});
	}

	private void init() {
		// TODO Auto-generated method stub
		lvOptions = (ListView) findViewById(R.id.lv_options);
		profilePic = (ImageView) findViewById(R.id.profilepic_question);
		profileName = (TextView) findViewById(R.id.tv_name_question);
		questionContent = (EditText) findViewById(R.id.question_content);
		optionContent = (EditText) findViewById(R.id.new_option);
		btask = (Button) findViewById(R.id.ask);
		btaddOption = (Button) findViewById(R.id.add_option);
		
		options = new ArrayList();
		session = new Session(this);
		apiParams = new ArrayList();
		adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,options);
		lvOptions.setAdapter(adapter);
		
		btaddOption.setOnClickListener(this);
		btask.setOnClickListener(this);
		
		profileName.setText(session.getValue(AppProperties.MY_PROFILE_NAME));
		Thread showPic = new Thread() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				final ImageLoader load = new ImageLoader(AskQuestion.this);
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

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int id=v.getId();
		switch(id){
		case R.id.add_option:
			String newOp= optionContent.getText().toString().trim();
			if(newOp!=null && !newOp.equals("")){
				Toast.makeText(this, "Option Added",Toast.LENGTH_SHORT ).show();
				optionContent.setText(null);
				options.add(newOp);
				//.insert(newOp,0);
				adapter.notifyDataSetChanged();
				
				//hide keyboard
				InputMethodManager imm = (InputMethodManager)getSystemService(
					      Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(optionContent.getWindowToken(), 0);
			}
			break;
		case R.id.ask:
			question = questionContent.getText().toString();
			System.out.println(question);
			if(!question.equals(null) && !question.equals(""))
			new PostQuestion().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			
		}
		
	}
	
	public class PostQuestion extends AsyncTask<Void, Void, Void>
	{

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			apiParams.add(new BasicNameValuePair(AppProperties.ACTION, "post_question"));
			apiParams.add(new BasicNameValuePair(AppProperties.PROFILE_ID, 
					session.getValue(AppProperties.PROFILE_ID)));
			apiParams.add(new BasicNameValuePair("question", question));
			for(int i=0;i<options.size();i++){
				apiParams.add(new BasicNameValuePair("option[]", options.get(i)));
			}
			try{
			result=CommonMethods.loadJSONData(AppProperties.URL, AppProperties.METHOD_GET, apiParams).getJSONObject(0);
			}
			catch(JSONException e){
				e.printStackTrace();
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
						Toast.makeText(AskQuestion.this, getResources().getString(R.string.success_question),Toast.LENGTH_LONG).show();
					}
				}
				else{
					Toast.makeText(AskQuestion.this, getResources().getString(R.string.status_error),Toast.LENGTH_LONG).show();	
				}
			}
				catch(JSONException e){
					e.printStackTrace();
				}
			
			finish();
		}
	}
}
