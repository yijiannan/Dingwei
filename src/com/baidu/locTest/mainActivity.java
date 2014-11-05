package com.baidu.locTest;

import java.util.regex.Pattern;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Process;
import android.os.Vibrator;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater.Filter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.yi.database.Constant;
import com.yi.database.DatabasesTransaction;

public class mainActivity extends Activity {
	//private SmsRecevier recevier;
	//ע��㲥
	View  dialog;
	private DatabasesTransaction db;
	private boolean isregiset = false;
	private static final String ACTION = "android.provider.Telephony.SMS_RECEIVED";
	private TextView mTv = null;
	EditText phonenumber;// Ŀ��绰����
	PendingIntent paIntent;
	SmsManager smsManager = SmsManager.getDefault();
	private Button mStartBtn;// ��ʼ��ť
	private boolean mIsStart;
	Button sends,addrss_book;// ���Ͱ�ť
	String str, ss;
	public String numbertel;
	private static int count = 1;
	private Vibrator mVibrator01 = null;
	private LocationClient mLocClient;
	public static String TAG = "LocTestDemo";
	private SmsRecevier receiver =new SmsRecevier();
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
         db=DatabasesTransaction.getInstance(this);
         System.out.println("dbdbdbdbdbdbdbdbdbdbdbdbdbdbdbdbdbdbdbdbdb"+db);
		setContentView(R.layout.main);

		mTv = (TextView) findViewById(R.id.textview);
		mStartBtn = (Button) findViewById(R.id.StartBtn);
		sends = (Button) findViewById(R.id.send);
		addrss_book=(Button)findViewById(R.id.add_book);
		phonenumber = (EditText) findViewById(R.id.editText1);

		mLocClient = ((Location) getApplication()).mLocationClient;// �ٶȶ�λapi
		((Location) getApplication()).mTv = mTv;
		mVibrator01 = (Vibrator) getApplication().getSystemService(
				Service.VIBRATOR_SERVICE);
		((Location) getApplication()).mVibrator01 = mVibrator01;
		sends.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setPassword();// ���Ͷ��Ű�ť�¼�
			}
		});
		addrss_book.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent it=new Intent();
				it.setClass(mainActivity.this, AddressBook.class);
				startActivity(it);
			}
		});
		mStartBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				Intent it=new Intent();
				it.setClass(mainActivity.this, Baimingdan.class);  
				startActivity(it);
				
			}
		});{
			
		}
		// ��ʼ/ֹͣ��ť
		// mStartBtn.setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// if (!mIsStart) {
		// setLocationOption();
		// mLocClient.start();
		// mStartBtn.setText("ֹͣ");
		// mIsStart = true;
		// //numbertel=phonenumber.getText().toString();
		//
		// } else {
		// mLocClient.stop();
		// mIsStart = false;
		// mStartBtn.setText("��ʼ");
		// }
		// Log.d(TAG, "... mStartBtn onClick... pid=" + Process.myPid()
		// + " count=" + count++);
		// }
		// });
		kaishidingwei(); 
		//zhuce();
		IntentFilter filter=new IntentFilter();
		filter.setPriority(997);
		filter.addAction("android.provider.Telephony.SMS_RECEIVED");
		registerReceiver(receiver, filter);
	}

	public void kaishidingwei() {
		setLocationOption();
		mLocClient.start();
	}

	// ע����Ź㲥
	public void zhuce() {
		IntentFilter filter = new IntentFilter(ACTION);
		filter.setPriority(1000);// �������ȼ����
		registerReceiver(receiver, filter);
		isregiset = true;
	}

	// dialog���textע��õ���������
	 public void setPassword() {
		
		LayoutInflater inflater=LayoutInflater.from(mainActivity.this);
		dialog=inflater.inflate(R.layout.zidingyi_dialog, null);
		final EditText name=(EditText)dialog.findViewById(R.id.nameed);
		final EditText  number=(EditText)dialog.findViewById(R.id.numbered);//�˴���ȡ����dialog�е�view ����Ҫ����
		//final EditText et = new EditText(mainActivity.this);//���dialog��ֻ��Ҫһ��������д����Ҫ�Զ���ľ����ϱߵ�д
		
		//str = et.getText().toString();
		// ������Ӱ�����
		new AlertDialog.Builder(mainActivity.this).setTitle("������")
				.setView(dialog)
				.setPositiveButton("���", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
					
						//System.out.println(str);
						//����洢�������绰����
						//�����ӰѺ���������ݿ���
						// ���ȷ����ť��õ������ֵ������
						ContentValues values=new ContentValues();
						System.out.println("number"+number.getText().toString());
						System.out.println("name"+name.getText().toString());
						values.put("callerid", name.getText().toString());
						values.put("smsbody", number.getText().toString());//�õ��绰����
						db.saveSql(Constant.Caller_ID, values);//�����ݿ��д��������
					
						//System.out.println("�������绰"+str);
						Toast.makeText(getApplicationContext(), "��ӳɹ�", Toast.LENGTH_LONG);//����Ŀǰû������ʾ��activity��
					}
				}).setNegativeButton("ȡ��", null).show();
	
	} 
 

	//xml�ļ�ע�ᰴť�¼�
	public void sendMessage(View view) {
		paIntent = PendingIntent.getBroadcast(this, 0, new Intent(), 0);//
		isOpenNetwork();
	}

	boolean isOpenNetwork() {//���text������Ϊ���񱨴�
		ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);//������ͨ��
		if (connManager.getActiveNetworkInfo() != null) {//��ͨ�Բ�Ϊ�յ�����·��Ͷ���
			
			if (!phonenumber.getText().toString().equals("")) {
				smsManager.sendTextMessage(phonenumber.getText().toString(), null,
						"" + Location.latitude + Location.longitude
								+ Location.dizhi, paIntent, null);//���Ͷ��ŵ����ݡ�����������������ϱ߷�����
				Toast.makeText(getApplicationContext(), "�������", Toast.LENGTH_LONG)
						.show();
			}else {
				Toast.makeText(getApplicationContext(), "�����������ٷ���λ��", Toast.LENGTH_LONG)
				.show();
			}
			

			return connManager.getActiveNetworkInfo().isAvailable();
		} else {
			Toast.makeText(getApplicationContext(), "����ʧ��", Toast.LENGTH_LONG)
					.show();

			return false;
		}

	}

	@Override
	public void onDestroy() {
		mLocClient.stop();
		((Location) getApplication()).mTv = null;
		  unregisterReceiver(receiver); 
		super.onDestroy();
	}

	// ������ز���
	private void setLocationOption() {
		LocationClientOption option = new LocationClientOption();

		option.setServiceName("com.baidu.location.service_v2.9");
		option.setAddrType("all");
		option.setPriority(LocationClientOption.NetWorkFirst);
		option.setPoiNumber(10);
		option.disableCache(true);
		mLocClient.setLocOption(option);
	}

	protected boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		return pattern.matcher(str).matches();
	}
	// ���ؼ���̨����
	 @Override
	 public boolean onKeyDown(int keyCode, KeyEvent event) {
	 if (keyCode == KeyEvent.KEYCODE_BACK) {
	 moveTaskToBack(false);
	 return true;
	 }
		return super.onKeyDown(keyCode, event);
	}
}