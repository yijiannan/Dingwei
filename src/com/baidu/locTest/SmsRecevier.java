package com.baidu.locTest;

import java.text.SimpleDateFormat;

import com.yi.bean.BaimingbanBean;
import com.yi.database.Constant;
import com.yi.database.DatabasesTransaction;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Vibrator;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;

//广播发送短信
public class SmsRecevier extends BroadcastReceiver {
	DatabasesTransaction db;
	String body, address;
	int baimingdan;// 如果来的短信是白名单内的电话号码值为0，否则为1
 
 Context context;
 
	@SuppressLint("UnlocalizedSms")
	@Override
	public void onReceive(Context context, Intent intent) {
		
		db = DatabasesTransaction.getInstance(context);
		
		Bundle bundle = intent.getExtras();
		Object[] objects = (Object[]) bundle.get("pdus");
		for (Object obj : objects) {
			SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) obj);
			body = smsMessage.getDisplayMessageBody();
			address = smsMessage.getDisplayOriginatingAddress();
			System.out.println("---------------------------------------------");
			System.out.println("拦截短信的body+" + body);
			System.out.println("拦截短信的address+" + address);
			query_baimingdan();
			
			System.out.println("baimingdan+" + baimingdan);//
			System.out.println("dizhi" + Location.dizhi);

			long date = smsMessage.getTimestampMillis();
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			String dateStr = format.format(date);
//			if (baimingdan == 1) {// 判断来短信的用户是否是白名单内的用户， 如果是就直接发送
//				SmsManager smsManager = SmsManager.getDefault();
//				smsManager.sendTextMessage(address, null, "他（她）的地址为"
//						+ Location.latitude + "  " + Location.longitude
//						+ Location.dizhi, null, null);
//				System.out.println("拦截短信");
//				abortBroadcast();// 禁止向下传送广播
//			} else {
//
//			}

		}
		// Object[] pdus = (Object[]) intent.getExtras().get("pdus");
		// if (pdus != null && pdus.length > 0) {
		// SmsMessage[] messages = new SmsMessage[pdus.length];
		// for (int i = 0; i < pdus.length; i++) {
		// byte[] pdu = (byte[]) pdus[i];
		// messages[i] = SmsMessage.createFromPdu(pdu);
		// }
		// for (SmsMessage message : messages) {
		// String content = message.getMessageBody();// 得到短信内容
		// String sender = message.getOriginatingAddress();// 得到发信息的号码
		// if (content.equals("1")) {
		// abortBroadcast();// 中止发送
		// Log.e("TAG", sender +"想知道你的位置信息");
		// System.out.println(sender+"想知道你的位置信息");
		//
		// Date date = new Date(message.getTimestampMillis());
		// SimpleDateFormat format = new SimpleDateFormat(
		// "yyyy-MM-dd HH:mm:ss");
		// String sendContent = format.format(date) + ":" + sender + "--"
		// + content;
		// SmsManager smsManager = SmsManager.getDefault();// 发信息时需要的
		// smsManager.sendTextMessage(sender, null, ""
		// + Location.latitude + Location.longitude + Location.dizhi,
		// null, null);
		// Log.v("TAG", sendContent);
		// }
		// }
		// }
		
	}
//查询白名单匹配电话号码
	void query_baimingdan() {
		String querySql = "select smsbody from " + Constant.Caller_ID;
		Cursor cursor = db.selectsql(querySql);

		while (cursor.moveToNext()) {
			BaimingbanBean bai = new BaimingbanBean();

			bai.name = cursor
					.getString(cursor.getColumnIndexOrThrow("smsbody"));// 白名单内电话号码

			System.out.println("循环查询数据库内电话号码+" + bai.name);
			if (address.equals("+86" + bai.name) && body.equals("00")) {// 发送的内容为“hao”本地就没有拦截，且告知对方地理位置
				System.out.println("白名单内人获取你的位置");	
				// 并且是相隔大约3分钟发送一次
				SmsManager smsManager = SmsManager.getDefault();
				smsManager.sendTextMessage(address, null, "他（她）的地址为"
						+ Location.latitude + "  " + Location.longitude
						+ Location.dizhi, null, null);
				db.cleanDb();
				System.out.println("白名单发送后后关闭db");
				//baimingdan = 1;//把上边四行发送代码写在onreceive中会定时大概三分钟发送位置
				abortBroadcast();//终止广播
			}
//			else {
//				System.out.println("陌生人想获取你的位置");
//				
//				
//				new AlertDialog.Builder(context).setTitle("想知道你的位置")
//						.setPositiveButton("允许", new DialogInterface.OnClickListener() {
//							public void onClick(DialogInterface dialog, int which) {
//								SmsManager smsManager = SmsManager.getDefault();
//								smsManager.sendTextMessage(address, null, "他（她）的地址为"
//										+ Location.latitude + "  " + Location.longitude
//										+ Location.dizhi, null, null);
//								System.out.println("不是-------白名单发送后后关闭db");
//								baimingdan = 1;
//								db.cleanDb();
//								abortBroadcast();
//							
//							}
//						}).setNegativeButton("拒绝", null).show();
//				
//			}
			System.out.println("循环后关闭db");
			db.cleanDb();
		}

	}
}
