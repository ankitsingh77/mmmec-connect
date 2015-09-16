package com.quipmate2.features;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.example.quipmate2.R;
import com.quipmate2.adapter.EventAdapter;
import com.quipmate2.constants.AppProperties;
import com.quipmate2.utils.CommonMethods;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

public class EventFragment extends Fragment {
	private ListView lvEvents;
	private List<EventInfo> eventList;
	private Session session;
	private JSONObject result, friendsName, friendsImage;
	private JSONArray birthday, aevent;
	private LinearLayout llayout;
	private EventAdapter eventadapter;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		View view = inflater.inflate(R.layout.event_fragment, null);
		llayout = (LinearLayout) view.findViewById(R.id.event_layout);
		lvEvents = (ListView) view.findViewById(R.id.lv_events);
		eventList = new ArrayList();
		session = new Session(getActivity());
		
		lvEvents.addFooterView(new View(getActivity()));
		lvEvents.addHeaderView(new View(getActivity()));
		
		new fetchevents().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		return view;
	}

	public class fetchevents extends AsyncTask<Void, Void, Void>{
		ProgressBar pBar;
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			pBar = new ProgressBar(
					getActivity(),null,android.R.attr.progressBarStyleLarge); 
			pBar.setIndeterminate(true);
			llayout.addView(pBar);
			
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			try{
			ArrayList<NameValuePair> apiParams = new ArrayList<NameValuePair>();
			apiParams.add(new BasicNameValuePair(AppProperties.ACTION, "birthday_bomb_fetch"));
			apiParams.add(new BasicNameValuePair(AppProperties.PROFILE_ID,
					session.getValue(AppProperties.PROFILE_ID)));
			result=CommonMethods.loadJSONData(AppProperties.URL, AppProperties.METHOD_GET, apiParams).getJSONObject(0);
			
			if (result != null) {
				if (!result.has(getString(R.string.error))) {
					try{
					aevent = result.getJSONArray("aevent");
					}
					catch(JSONException e){
						e.printStackTrace();
					}
					try{
					birthday = result.getJSONArray(AppProperties.ACTION);
					friendsName = result.getJSONObject(AppProperties.NAME);
					friendsImage = result.getJSONObject(AppProperties.PROFILE_IMAGE);
					}
					catch(JSONException e){
						e.printStackTrace();
					}
					
					String id=null;
					String name = null;
					String imageURL = null;
					String date = null;
					boolean eventStatus = false;
					if(aevent !=null){
					for(int i=0;i<aevent.length();i++){
						JSONObject temp = aevent.getJSONObject(i);
						name = temp.getString("name");
						System.out.println(name);
						date = temp.getString("date");
						System.out.println(date);
						imageURL = temp.getString("display_image");
						System.out.println(imageURL);
						eventList.add(new EventInfo(name, imageURL, date, eventStatus));
					}
					}
					if(birthday != null){
					for(int i=0;i<birthday.length();i++){
						JSONObject temp = birthday.getJSONObject(i);
						id = temp.getString(AppProperties.PROFILE_ID);
						eventStatus = Integer.parseInt(temp.getString("bomb_status")) == 0 ? false : true;
						date = temp.getString("b");
						
						if(id != null){
							if(friendsName != null){
								 name = friendsName.getString(id);
								 System.out.println(name);
							 }
							 
							 if(friendsImage != null){
								 imageURL = friendsImage.getString(id);
								 System.out.println(imageURL);
							 }
							 eventList.add(new EventInfo(name, imageURL, date, eventStatus));
						}
					}
					}
				}
			}
			}
			catch(JSONException e){
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			llayout.removeView(pBar);
			eventadapter = new EventAdapter(eventList , getActivity());
			lvEvents.setAdapter(eventadapter);
		}
		
	}
}
