package com.quipmate2.adapter;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.example.quipmate2.R;
import com.quipmate2.features.EventInfo;
import com.quipmate2.loadwebimageandcache.ImageLoader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class EventAdapter extends BaseAdapter {
	private List<EventInfo> eventList;
	private ImageLoader imageDownLoader;
	
	public EventAdapter(List<EventInfo> eventList,Context context) {
		super();
		this.eventList = eventList;
		imageDownLoader = new ImageLoader(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return eventList != null ? eventList.size() : 0;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return eventList != null ? eventList.get(arg0) : null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		// TODO Auto-generated method stub
		EventViewHolder eventviewholder;
		if(view == null){
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		view = inflater.inflate(R.layout.single_event, parent,
				false);
		eventviewholder = new EventViewHolder();
		
		eventviewholder.eventImage = (ImageView) view.findViewById(R.id.event_pimage);
		eventviewholder.eventName = (TextView) view.findViewById(R.id.event_profile_name);
		eventviewholder.eventDate = (TextView) view.findViewById(R.id.event_date);
		
		view.setTag(eventviewholder);
		}
		else{
			eventviewholder = (EventViewHolder) view.getTag();
		}
		
		final EventInfo event = eventList.get(position);
		if(event != null){
			eventviewholder.eventName.setText(event.name);
			imageDownLoader.DisplayImage(event.imageUrl, eventviewholder.eventImage);
			DateNew birthdate = new DateNew();
			birthdate = getDate(event.birthdate);
			if(birthdate != null){
				Date date = new Date(); // your date
			    Calendar cal = Calendar.getInstance();
			    cal.setTime(date);
			    int day = cal.get(Calendar.DAY_OF_MONTH);
			    String month = null;
			    switch(cal.get(Calendar.MONTH)){
			    case 0:month = "January";break;
				case 1:month = "February";break;
				case 2:month = "March";break;
				case 3:month = "April";break;
				case 4:month = "May";break;
				case 5:month = "June";break;
				case 6:month = "July";break;
				case 7:month = "August";break;
				case 8:month = "September";break;
				case 9:month = "October";break;
				case 10:month = "November";break;
				case 11:month = "December";
			    }
			   if(birthdate.day == day && birthdate.month.equals(month)){
				   eventviewholder.eventDate.setText("Today");
			   }
			   else
				eventviewholder.eventDate.setText(birthdate.day+" "+birthdate.month);
			}
		}
		return view;
	}
	
	static class EventViewHolder{
		ImageView eventImage;
		TextView eventName;
		TextView eventDate;
	}
	
	
	public DateNew getDate(String date){
		
		if(date != null){
		int i = date.indexOf('-');
		DateNew newdate = new DateNew();
		newdate.year = Integer.parseInt(date.substring(0,i));
		switch(Integer.parseInt(date.substring(i+1,date.lastIndexOf('-')))){
			case 1:newdate.month = "January";break;
			case 2:newdate.month = "February";break;
			case 3:newdate.month = "March";break;
			case 4:newdate.month = "April";break;
			case 5:newdate.month = "May";break;
			case 6:newdate.month = "June";break;
			case 7:newdate.month = "July";break;
			case 8:newdate.month = "August";break;
			case 9:newdate.month = "September";break;
			case 10:newdate.month = "October";break;
			case 11:newdate.month = "November";break;
			case 12:newdate.month = "December";
			}
		
		newdate.day = Integer.parseInt(date.substring(date.lastIndexOf('-')+1, date.length()));
		return newdate;
		}
		return null;
	}

	public class DateNew{
		int day;
		String month;
		int year;
		}
	}

