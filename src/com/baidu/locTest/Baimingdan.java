package com.baidu.locTest;

import java.util.ArrayList;
import java.util.List;

import com.yi.adapter.BaimingdanAdapter;
import com.yi.bean.BaimingbanBean;
import com.yi.database.Constant;
import com.yi.database.DatabasesTransaction;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

public class Baimingdan extends Activity {
	private ListView ls;
	private BaimingdanAdapter baimingdanAdapter;
	private DatabasesTransaction db;
	// private List<Baimingdan_1> dlist;
	private List<String> adaptdata, name;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		db = DatabasesTransaction.getInstance(this);
		setContentView(R.layout.baimingdan);
		adaptdata = new ArrayList<String>();
		name = new ArrayList<String>();// 没有实例化就会报空
		setupView();
		query_baimingdan();
		// 单击向白名单用户发送自己的位置
		ls.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				final String number = name.get(position);
				// SmsManager smsManager = SmsManager.getDefault();// 获取一个短信管理器
				// smsManager.sendTextMessage(number, null, "" +
				// Location.latitude
				// + Location.longitude + Location.dizhi, null, null);//
				// 调用发送短信此功能暂时注销完成后再上
				// finish();
				new AlertDialog.Builder(Baimingdan.this).setTitle("告知对方位置")

				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

						SmsManager smsManager = SmsManager.getDefault();// 获取一个短信管理器
						smsManager.sendTextMessage(number, null, ""
								+ Location.latitude + Location.longitude
								+ Location.dizhi, null, null);// 调用发送短信此功能暂时注销完成后再上

						// System.out.println("白名单电话"+str);
						Toast.makeText(getApplicationContext(), "发送成功",
								Toast.LENGTH_LONG);// 这里目前没看到显示在activity中
					}
				}).setNegativeButton("取消", null).show();

			}

		});
		// 长按删除白名单内内容
		ls.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					final int position, long id) {
				// TODO Auto-generated method stub
				new AlertDialog.Builder(Baimingdan.this)
						.setTitle("删除这一列")
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub

										String number = name.get(position);
										db.deleteData(Constant.Caller_ID,
												"smsbody=?",
												new String[] { number });
										query_baimingdan();
										baimingdanAdapter
												.notifyDataSetChanged();
									}
								}).setNegativeButton("取消", null).show();
				return false;
			}

		});
	}

	private void setupView() {
		ls = (ListView) findViewById(R.id.baimingdan);
		baimingdanAdapter = new BaimingdanAdapter(adaptdata, this, name);// 这个this
																			// 不添加的话会报getview()这行空指针错误
		ls.setAdapter(baimingdanAdapter);
	}

	// 获取数据库表内容
	private void query_baimingdan() {
		String querySql = "select * from " + Constant.Caller_ID;
		Cursor cursor = db.selectsql(querySql);
		adaptdata.clear();
		name.clear();
		while (cursor.moveToNext()) {
			BaimingbanBean bai = new BaimingbanBean();
			bai.content = cursor.getString(cursor
					.getColumnIndexOrThrow("callerid"));
			bai.name = cursor
					.getString(cursor.getColumnIndexOrThrow("smsbody"));
			System.out.println("call" + bai.content);
			System.out.println("smsbody" + bai.name);

			// try {
			// JSONObject json=new JSONObject(bai.content);
			// } catch (Exception e) {
			// // TODO: handle exception
			// }
			adaptdata.add(bai.content);
			name.add(bai.name);
		}

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		baimingdanAdapter.notifyDataSetChanged();
		super.onResume();
	}
}
