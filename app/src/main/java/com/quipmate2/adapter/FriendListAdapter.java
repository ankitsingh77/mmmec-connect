package com.quipmate2.adapter;


import java.util.List;

import com.example.quipmate2.R;
import com.quipmate2.features.FriendInfo;
import com.quipmate2.loadwebimageandcache.ImageLoader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FriendListAdapter extends BaseAdapter {
	private List<FriendInfo> friendDataList;
	private ImageLoader imageDownLoader;
	
	public FriendListAdapter(List<FriendInfo> friendDataList,Context context) {
		super();
		this.friendDataList = friendDataList;
		imageDownLoader = new ImageLoader(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return friendDataList != null ? friendDataList.size() : 0;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return friendDataList != null ? friendDataList.get(position) : null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		// TODO Auto-generated method stub
		FriendViewHolder friendViewHolder;
		if(view == null){
			LayoutInflater inflater = LayoutInflater.from(parent.getContext());
			view = inflater.inflate(R.layout.friend_info, parent,
					false);
			
			friendViewHolder = new FriendViewHolder();
			friendViewHolder.pimage = (ImageView) view.findViewById(R.id.pimage);
			friendViewHolder.name = (TextView) view.findViewById(R.id.name);
			
			view.setTag(friendViewHolder);
		} else {
			friendViewHolder = (FriendViewHolder) view.getTag();
		}
		
		final FriendInfo friend = friendDataList.get(position);
		if(friend != null){
			friendViewHolder.name.setText(friend.getName());
			imageDownLoader.DisplayImage(friend.getImageURL(), friendViewHolder.pimage);
			
		}
		return view;
	}
	static class FriendViewHolder{
		ImageView pimage;
		TextView name;
	}
}
