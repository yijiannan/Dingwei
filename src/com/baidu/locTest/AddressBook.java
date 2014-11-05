package com.baidu.locTest;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.yi.database.Constant;
import com.yi.database.DatabasesTransaction;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class AddressBook extends ListActivity {

	private DatabasesTransaction db;
	Context mContext = null;

	/** 获取库Phone表字段 **/
	private static final String[] PHONES_PROJECTION = new String[] {
			Phone.DISPLAY_NAME, Phone.NUMBER, Photo.PHOTO_ID, Phone.CONTACT_ID };

	/** 联系人显示名称 **/
	private static final int PHONES_DISPLAY_NAME_INDEX = 0;

	/** 电话号码 **/
	private static final int PHONES_NUMBER_INDEX = 1;

	/** 头像ID **/
	private static final int PHONES_PHOTO_ID_INDEX = 2;

	/** 联系人的ID **/
	private static final int PHONES_CONTACT_ID_INDEX = 3;

	/** 联系人名称 **/
	private ArrayList<String> mContactsName = new ArrayList<String>();

	/** 联系人电话 **/
	private ArrayList<String> mContactsNumber = new ArrayList<String>();

	/** 联系人头像 **/
	private ArrayList<Bitmap> mContactsPhonto = new ArrayList<Bitmap>();

	ListView mListView = null;
	MyListAdapter myAdapter = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		mContext = this;
		mListView = this.getListView();
		/** 得到手机通讯录联系人信息 **/
		getPhoneContacts();
		db = DatabasesTransaction.getInstance(this);
		myAdapter = new MyListAdapter(this);
		setListAdapter(myAdapter);

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					final int position, long id) {
				// 调用系统方法拨打电话
				// Intent dialIntent = new Intent(Intent.ACTION_CALL, Uri
				// .parse("tel:" + mContactsNumber.get(position)));
				// startActivity(dialIntent);
				// 调用发短信的方法
//				System.out.println("tel:" + mContactsNumber.get(position));
//				SmsManager smsManager = SmsManager.getDefault();// 获取一个短信管理器
//				// smsManager.sendTextMessage(mContactsNumber.get(position),
//				// null,
//				// "", null, null);// 调用发送短信此功能暂时注销完成后再上
//				finish();
				new AlertDialog.Builder(AddressBook.this)
				.setTitle("请求对方位置")
				
				.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {

								SmsManager smsManager = SmsManager.getDefault();// 获取一个短信管理器
								 smsManager.sendTextMessage(mContactsNumber.get(position),
								 null,"00", null, null);// 调用发送短信此功能暂时注销完成后再上
							

								// System.out.println("白名单电话"+str);
								Toast.makeText(getApplicationContext(),
										"发送成功", Toast.LENGTH_LONG);// 这里目前没看到显示在activity中
							}
						}).setNegativeButton("取消", null).show();
				
			}
		});
		mListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					final int position, long id) {
				// TODO Auto-generated method stub
				final EditText et = new EditText(AddressBook.this);
				String str = et.getText().toString();
				// 设置添加白名单
				new AlertDialog.Builder(AddressBook.this)
						.setTitle("添加到白名单")
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {

										ContentValues values = new ContentValues();
										values.put("callerid",
												mContactsName.get(position));// 得到电话号码
										values.put("smsbody",
												mContactsNumber.get(position));
										db.saveSql(Constant.Caller_ID, values);// 往数据库中存号码内容

										// System.out.println("白名单电话"+str);
										Toast.makeText(getApplicationContext(),
												"添加成功", Toast.LENGTH_LONG);// 这里目前没看到显示在activity中
									}
								}).setNegativeButton("取消", null).show();
				return false;
			}
		});

		super.onCreate(savedInstanceState);
	}

	/** 得到手机通讯录联系人信息 **/
	private void getPhoneContacts() {
		ContentResolver resolver = mContext.getContentResolver();

		// 获取手机联系人
		Cursor phoneCursor = resolver.query(Phone.CONTENT_URI,
				PHONES_PROJECTION, null, null, null);

		if (phoneCursor != null) {
			while (phoneCursor.moveToNext()) {

				// 得到手机号码
				String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
				// 当手机号码为空的或者为空字段 跳过当前循环
				if (TextUtils.isEmpty(phoneNumber))
					continue;

				// 得到联系人名称
				String contactName = phoneCursor
						.getString(PHONES_DISPLAY_NAME_INDEX);

				// 得到联系人ID
				Long contactid = phoneCursor.getLong(PHONES_CONTACT_ID_INDEX);

				// 得到联系人头像ID
				Long photoid = phoneCursor.getLong(PHONES_PHOTO_ID_INDEX);

				// 得到联系人头像Bitamp
				Bitmap contactPhoto = null;

				// photoid 大于0 表示联系人有头像 如果没有给此人设置头像则给他一个默认的
				if (photoid > 0) {
					Uri uri = ContentUris.withAppendedId(
							ContactsContract.Contacts.CONTENT_URI, contactid);
					InputStream input = ContactsContract.Contacts
							.openContactPhotoInputStream(resolver, uri);
					contactPhoto = BitmapFactory.decodeStream(input);
				} else {
					contactPhoto = BitmapFactory.decodeResource(getResources(),
							R.drawable.ic_launcher);
				}

				mContactsName.add(contactName);
				mContactsNumber.add(phoneNumber);
				mContactsPhonto.add(contactPhoto);
			}

			phoneCursor.close();
		}
	}

	/** 得到手机SIM卡联系人人信息 **/
	private void getSIMContacts() {
		ContentResolver resolver = mContext.getContentResolver();
		// 获取Sims卡联系人
		Uri uri = Uri.parse("content://icc/adn");
		Cursor phoneCursor = resolver.query(uri, PHONES_PROJECTION, null, null,
				null);

		if (phoneCursor != null) {
			while (phoneCursor.moveToNext()) {

				// 得到手机号码
				String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
				// 当手机号码为空的或者为空字段 跳过当前循环
				if (TextUtils.isEmpty(phoneNumber))
					continue;
				// 得到联系人名称
				String contactName = phoneCursor
						.getString(PHONES_DISPLAY_NAME_INDEX);

				// Sim卡中没有联系人头像

				mContactsName.add(contactName);
				mContactsNumber.add(phoneNumber);
			}

			phoneCursor.close();
		}
	}

	class MyListAdapter extends BaseAdapter {
		public MyListAdapter(Context context) {
			mContext = context;
		}

		public int getCount() {
			// 设置绘制数量
			return mContactsName.size();
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			Contacts contacts = null;
			if (convertView == null) {
				contacts = new Contacts();
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.addressbook, null);
				contacts.iamge = (ImageView) convertView
						.findViewById(R.id.color_image);
				contacts.title = (TextView) convertView
						.findViewById(R.id.color_title);
				contacts.text = (TextView) convertView
						.findViewById(R.id.color_text);
				convertView.setTag(contacts);
			} else {
				contacts = (Contacts) convertView.getTag();
			}
			// 绘制联系人名称
			contacts.title.setText(mContactsName.get(position));
			// 绘制联系人号码
			contacts.text.setText(mContactsNumber.get(position));
			// 绘制联系人头像
			contacts.iamge.setImageBitmap(mContactsPhonto.get(position));
			return convertView;
		}

		class Contacts {
			ImageView iamge;
			TextView title;
			TextView text;
		}
	}

}