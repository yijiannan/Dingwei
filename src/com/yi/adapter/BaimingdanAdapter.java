package com.yi.adapter;

import java.util.List;

import com.baidu.locTest.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;



public class BaimingdanAdapter extends BaseAdapter implements OnClickListener{
 private Context context;
 List<String> data,name;
	public BaimingdanAdapter(List<String> data,Context context,List<String> name){
	this.context=context;
		this.data=data;
		this.name=name;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		Baimingdan_1 baimingdan=null;
		if (convertView==null) {
			baimingdan=new Baimingdan_1();
			convertView=LayoutInflater.from(context).inflate(R.layout.baimingdan_1, null);
		baimingdan.textView=(TextView)convertView.findViewById(R.id.one);
		baimingdan.name=(TextView)convertView.findViewById(R.id.two);
		convertView.setTag(baimingdan);
		}else {
			baimingdan=(Baimingdan_1)convertView.getTag();
		}
		baimingdan.textView.setText(data.get(position));
		baimingdan.name.setText(name.get(position));
		return convertView;
	}
	//listview中一个的样式
public static class Baimingdan_1{
	TextView textView,name;
}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}

}
