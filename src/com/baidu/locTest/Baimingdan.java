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
		name = new ArrayList<String>();// û��ʵ�����ͻᱨ��
		setupView();
		query_baimingdan();
		// ������������û������Լ���λ��
		ls.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				final String number = name.get(position);
				// SmsManager smsManager = SmsManager.getDefault();// ��ȡһ�����Ź�����
				// smsManager.sendTextMessage(number, null, "" +
				// Location.latitude
				// + Location.longitude + Location.dizhi, null, null);//
				// ���÷��Ͷ��Ŵ˹�����ʱע����ɺ�����
				// finish();
				new AlertDialog.Builder(Baimingdan.this).setTitle("��֪�Է�λ��")

				.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

						SmsManager smsManager = SmsManager.getDefault();// ��ȡһ�����Ź�����
						smsManager.sendTextMessage(number, null, ""
								+ Location.latitude + Location.longitude
								+ Location.dizhi, null, null);// ���÷��Ͷ��Ŵ˹�����ʱע����ɺ�����

						// System.out.println("�������绰"+str);
						Toast.makeText(getApplicationContext(), "���ͳɹ�",
								Toast.LENGTH_LONG);// ����Ŀǰû������ʾ��activity��
					}
				}).setNegativeButton("ȡ��", null).show();

			}

		});
		// ����ɾ��������������
		ls.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					final int position, long id) {
				// TODO Auto-generated method stub
				new AlertDialog.Builder(Baimingdan.this)
						.setTitle("ɾ����һ��")
						.setPositiveButton("ȷ��",
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
								}).setNegativeButton("ȡ��", null).show();
				return false;
			}

		});
	}

	private void setupView() {
		ls = (ListView) findViewById(R.id.baimingdan);
		baimingdanAdapter = new BaimingdanAdapter(adaptdata, this, name);// ���this
																			// ����ӵĻ��ᱨgetview()���п�ָ�����
		ls.setAdapter(baimingdanAdapter);
	}

	// ��ȡ���ݿ������
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
