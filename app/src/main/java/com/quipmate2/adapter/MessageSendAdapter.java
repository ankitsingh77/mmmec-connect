package com.quipmate2.adapter;

import java.util.List;
import com.example.quipmate2.R;
import com.quipmate2.features.MsgInfo;
import com.quipmate2.loadwebimageandcache.ImageLoader;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MessageSendAdapter extends BaseAdapter {
	
	private List<MsgInfo> msgList;
	private ImageLoader imageDownLoader;

	public MessageSendAdapter(List<MsgInfo> msgList,Context context) {
		super();
		this.msgList = msgList;
		imageDownLoader = new ImageLoader(context);
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return msgList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return msgList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		// TODO Auto-generated method stub
		MessageViewHolder msgviewholder;
		if(view == null){
			LayoutInflater inflater = LayoutInflater.from(parent.getContext());
			view = inflater.inflate(R.layout.single_msg_content_frag, parent,
					false);
			
			msgviewholder = new MessageViewHolder();
			
			msgviewholder.msgImage = (ImageView)view.findViewById(R.id.img_msg_frag);
			//msgviewholder.msgName = (TextView)view.findViewById(R.id.name_msg);
			//msgviewholder.msgDate = (TextView)view.findViewById(R.id.time_msg);
			msgviewholder.msg = (TextView)view.findViewById(R.id.msg_frag);
			
			view.setTag(msgviewholder);
		}
		else{
			msgviewholder = (MessageViewHolder)view.getTag();
		}
		MsgInfo msg = msgList.get(position);
		if(msg != null){
			msgviewholder.msg.setText(msg.msg);
			imageDownLoader.DisplayImage(msg.msgBy.getImageURL(), msgviewholder.msgImage);
		}
		return view;
	}
	
	static class MessageViewHolder{
		ImageView msgImage;
		TextView msgName;
		TextView msgDate;
		TextView msg;
	}
}
