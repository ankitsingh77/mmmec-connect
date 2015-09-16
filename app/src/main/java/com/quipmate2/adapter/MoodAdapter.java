package com.quipmate2.adapter;

import java.util.ArrayList;
import com.example.quipmate2.R;
import com.quipmate2.features.MoodSelect.MoodItem;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;



public class MoodAdapter extends BaseAdapter {
	Context context;
	ArrayList<MoodItem> list;
	
	public MoodAdapter(Context context,ArrayList<MoodItem> list){
		this.context=context;
		this.list=list;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView=inflater.inflate(R.layout.welcome_list_row, parent, false);
		TextView txt=(TextView)rowView.findViewById(R.id.lits_item_title);
		ImageView img=(ImageView)rowView.findViewById(R.id.list_item_image);
		img.setImageResource(list.get(position).icon);
		txt.setText(list.get(position).name);
		txt.setTextColor(Color.BLACK);
		return rowView;
	}


}
