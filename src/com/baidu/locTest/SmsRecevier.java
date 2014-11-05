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

//�㲥���Ͷ���
public class SmsRecevier extends BroadcastReceiver {
	DatabasesTransaction db;
	String body, address;
	int baimingdan;// ������Ķ����ǰ������ڵĵ绰����ֵΪ0������Ϊ1
 
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
			System.out.println("���ض��ŵ�body+" + body);
			System.out.println("���ض��ŵ�address+" + address);
			query_baimingdan();
			
			System.out.println("baimingdan+" + baimingdan);//
			System.out.println("dizhi" + Location.dizhi);

			long date = smsMessage.getTimestampMillis();
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			String dateStr = format.format(date);
//			if (baimingdan == 1) {// �ж������ŵ��û��Ƿ��ǰ������ڵ��û��� ����Ǿ�ֱ�ӷ���
//				SmsManager smsManager = SmsManager.getDefault();
//				smsManager.sendTextMessage(address, null, "���������ĵ�ַΪ"
//						+ Location.latitude + "  " + Location.longitude
//						+ Location.dizhi, null, null);
//				System.out.println("���ض���");
//				abortBroadcast();// ��ֹ���´��͹㲥
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
		// String content = message.getMessageBody();// �õ���������
		// String sender = message.getOriginatingAddress();// �õ�����Ϣ�ĺ���
		// if (content.equals("1")) {
		// abortBroadcast();// ��ֹ����
		// Log.e("TAG", sender +"��֪�����λ����Ϣ");
		// System.out.println(sender+"��֪�����λ����Ϣ");
		//
		// Date date = new Date(message.getTimestampMillis());
		// SimpleDateFormat format = new SimpleDateFormat(
		// "yyyy-MM-dd HH:mm:ss");
		// String sendContent = format.format(date) + ":" + sender + "--"
		// + content;
		// SmsManager smsManager = SmsManager.getDefault();// ����Ϣʱ��Ҫ��
		// smsManager.sendTextMessage(sender, null, ""
		// + Location.latitude + Location.longitude + Location.dizhi,
		// null, null);
		// Log.v("TAG", sendContent);
		// }
		// }
		// }
		
	}
//��ѯ������ƥ��绰����
	void query_baimingdan() {
		String querySql = "select smsbody from " + Constant.Caller_ID;
		Cursor cursor = db.selectsql(querySql);

		while (cursor.moveToNext()) {
			BaimingbanBean bai = new BaimingbanBean();

			bai.name = cursor
					.getString(cursor.getColumnIndexOrThrow("smsbody"));// �������ڵ绰����

			System.out.println("ѭ����ѯ���ݿ��ڵ绰����+" + bai.name);
			if (address.equals("+86" + bai.name) && body.equals("00")) {// ���͵�����Ϊ��hao�����ؾ�û�����أ��Ҹ�֪�Է�����λ��
				System.out.println("���������˻�ȡ���λ��");	
				// �����������Լ3���ӷ���һ��
				SmsManager smsManager = SmsManager.getDefault();
				smsManager.sendTextMessage(address, null, "���������ĵ�ַΪ"
						+ Location.latitude + "  " + Location.longitude
						+ Location.dizhi, null, null);
				db.cleanDb();
				System.out.println("���������ͺ��ر�db");
				//baimingdan = 1;//���ϱ����з��ʹ���д��onreceive�лᶨʱ��������ӷ���λ��
				abortBroadcast();//��ֹ�㲥
			}
//			else {
//				System.out.println("İ�������ȡ���λ��");
//				
//				
//				new AlertDialog.Builder(context).setTitle("��֪�����λ��")
//						.setPositiveButton("����", new DialogInterface.OnClickListener() {
//							public void onClick(DialogInterface dialog, int which) {
//								SmsManager smsManager = SmsManager.getDefault();
//								smsManager.sendTextMessage(address, null, "���������ĵ�ַΪ"
//										+ Location.latitude + "  " + Location.longitude
//										+ Location.dizhi, null, null);
//								System.out.println("����-------���������ͺ��ر�db");
//								baimingdan = 1;
//								db.cleanDb();
//								abortBroadcast();
//							
//							}
//						}).setNegativeButton("�ܾ�", null).show();
//				
//			}
			System.out.println("ѭ����ر�db");
			db.cleanDb();
		}

	}
}
