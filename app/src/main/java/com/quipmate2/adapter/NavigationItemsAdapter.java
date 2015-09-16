package com.quipmate2.adapter;

import java.util.ArrayList;

import com.example.quipmate2.R;
import com.quipmate2.features.NavigationListItem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;



public class NavigationItemsAdapter extends BaseAdapter {
	Context context;
	ArrayList<NavigationListItem> list;
	
	public NavigationItemsAdapter(Context context,ArrayList<NavigationListItem> list){
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
	public View getView(int position, View view, ViewGroup parent) {
		// TODO Auto-generated method stub
		NavigateViewHolder navigateviewholder;
		if(view == null){
		LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view=inflater.inflate(R.layout.welcome_list_row, parent, false);
		
		navigateviewholder = new NavigateViewHolder();
		navigateviewholder.txt=(TextView)view.findViewById(R.id.lits_item_title);
		navigateviewholder.img=(ImageView)view.findViewById(R.id.list_item_image);
		view.setTag(navigateviewholder);
		}
		else{
			navigateviewholder = (NavigateViewHolder)view.getTag();
		}
		navigateviewholder.img.setImageResource(list.get(position).icon);
		navigateviewholder.txt.setText(list.get(position).name);
		return view;
	}
	
	static class NavigateViewHolder{
		ImageView img;
		TextView txt;
	}


}
