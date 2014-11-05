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
	//注册广播
	View  dialog;
	private DatabasesTransaction db;
	private boolean isregiset = false;
	private static final String ACTION = "android.provider.Telephony.SMS_RECEIVED";
	private TextView mTv = null;
	EditText phonenumber;// 目标电话号码
	PendingIntent paIntent;
	SmsManager smsManager = SmsManager.getDefault();
	private Button mStartBtn;// 开始按钮
	private boolean mIsStart;
	Button sends,addrss_book;// 发送按钮
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

		mLocClient = ((Location) getApplication()).mLocationClient;// 百度定位api
		((Location) getApplication()).mTv = mTv;
		mVibrator01 = (Vibrator) getApplication().getSystemService(
				Service.VIBRATOR_SERVICE);
		((Location) getApplication()).mVibrator01 = mVibrator01;
		sends.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setPassword();// 发送短信按钮事件
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
		// 开始/停止按钮
		// mStartBtn.setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// if (!mIsStart) {
		// setLocationOption();
		// mLocClient.start();
		// mStartBtn.setText("停止");
		// mIsStart = true;
		// //numbertel=phonenumber.getText().toString();
		//
		// } else {
		// mLocClient.stop();
		// mIsStart = false;
		// mStartBtn.setText("开始");
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

	// 注册短信广播
	public void zhuce() {
		IntentFilter filter = new IntentFilter(ACTION);
		filter.setPriority(1000);// 设置优先级最大
		registerReceiver(receiver, filter);
		isregiset = true;
	}

	// dialog里的text注册得到输入数字
	 public void setPassword() {
		
		LayoutInflater inflater=LayoutInflater.from(mainActivity.this);
		dialog=inflater.inflate(R.layout.zidingyi_dialog, null);
		final EditText name=(EditText)dialog.findViewById(R.id.nameed);
		final EditText  number=(EditText)dialog.findViewById(R.id.numbered);//此处获取的是dialog中的view 必须要加上
		//final EditText et = new EditText(mainActivity.this);//如果dialog中只需要一个就这样写，需要自定义的就照上边的写
		
		//str = et.getText().toString();
		// 设置添加白名单
		new AlertDialog.Builder(mainActivity.this).setTitle("白名单")
				.setView(dialog)
				.setPositiveButton("添加", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
					
						//System.out.println(str);
						//建表存储白名单电话号码
						//点击添加把号码存入数据库中
						// 点击确定按钮后得到输入的值，保存
						ContentValues values=new ContentValues();
						System.out.println("number"+number.getText().toString());
						System.out.println("name"+name.getText().toString());
						values.put("callerid", name.getText().toString());
						values.put("smsbody", number.getText().toString());//得到电话号码
						db.saveSql(Constant.Caller_ID, values);//往数据库中存号码内容
					
						//System.out.println("白名单电话"+str);
						Toast.makeText(getApplicationContext(), "添加成功", Toast.LENGTH_LONG);//这里目前没看到显示在activity中
					}
				}).setNegativeButton("取消", null).show();
	
	} 
 

	//xml文件注册按钮事件
	public void sendMessage(View view) {
		paIntent = PendingIntent.getBroadcast(this, 0, new Intent(), 0);//
		isOpenNetwork();
	}

	boolean isOpenNetwork() {//如果text中内容为空择报错
		ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);//服务连通性
		if (connManager.getActiveNetworkInfo() != null) {//连通性不为空的情况下发送短信
			
			if (!phonenumber.getText().toString().equals("")) {
				smsManager.sendTextMessage(phonenumber.getText().toString(), null,
						"" + Location.latitude + Location.longitude
								+ Location.dizhi, paIntent, null);//发送短信的内容。最后两个参数就是上边方法的
				Toast.makeText(getApplicationContext(), "发送完毕", Toast.LENGTH_LONG)
						.show();
			}else {
				Toast.makeText(getApplicationContext(), "请输入号码后再发送位置", Toast.LENGTH_LONG)
				.show();
			}
			

			return connManager.getActiveNetworkInfo().isAvailable();
		} else {
			Toast.makeText(getApplicationContext(), "发送失败", Toast.LENGTH_LONG)
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

	// 设置相关参数
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
	// 返回键后台运行
	 @Override
	 public boolean onKeyDown(int keyCode, KeyEvent event) {
	 if (keyCode == KeyEvent.KEYCODE_BACK) {
	 moveTaskToBack(false);
	 return true;
	 }
		return super.onKeyDown(keyCode, event);
	}
}