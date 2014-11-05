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

	/** ��ȡ��Phone���ֶ� **/
	private static final String[] PHONES_PROJECTION = new String[] {
			Phone.DISPLAY_NAME, Phone.NUMBER, Photo.PHOTO_ID, Phone.CONTACT_ID };

	/** ��ϵ����ʾ���� **/
	private static final int PHONES_DISPLAY_NAME_INDEX = 0;

	/** �绰���� **/
	private static final int PHONES_NUMBER_INDEX = 1;

	/** ͷ��ID **/
	private static final int PHONES_PHOTO_ID_INDEX = 2;

	/** ��ϵ�˵�ID **/
	private static final int PHONES_CONTACT_ID_INDEX = 3;

	/** ��ϵ������ **/
	private ArrayList<String> mContactsName = new ArrayList<String>();

	/** ��ϵ�˵绰 **/
	private ArrayList<String> mContactsNumber = new ArrayList<String>();

	/** ��ϵ��ͷ�� **/
	private ArrayList<Bitmap> mContactsPhonto = new ArrayList<Bitmap>();

	ListView mListView = null;
	MyListAdapter myAdapter = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		mContext = this;
		mListView = this.getListView();
		/** �õ��ֻ�ͨѶ¼��ϵ����Ϣ **/
		getPhoneContacts();
		db = DatabasesTransaction.getInstance(this);
		myAdapter = new MyListAdapter(this);
		setListAdapter(myAdapter);

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					final int position, long id) {
				// ����ϵͳ��������绰
				// Intent dialIntent = new Intent(Intent.ACTION_CALL, Uri
				// .parse("tel:" + mContactsNumber.get(position)));
				// startActivity(dialIntent);
				// ���÷����ŵķ���
//				System.out.println("tel:" + mContactsNumber.get(position));
//				SmsManager smsManager = SmsManager.getDefault();// ��ȡһ�����Ź�����
//				// smsManager.sendTextMessage(mContactsNumber.get(position),
//				// null,
//				// "", null, null);// ���÷��Ͷ��Ŵ˹�����ʱע����ɺ�����
//				finish();
				new AlertDialog.Builder(AddressBook.this)
				.setTitle("����Է�λ��")
				
				.setPositiveButton("ȷ��",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {

								SmsManager smsManager = SmsManager.getDefault();// ��ȡһ�����Ź�����
								 smsManager.sendTextMessage(mContactsNumber.get(position),
								 null,"00", null, null);// ���÷��Ͷ��Ŵ˹�����ʱע����ɺ�����
							

								// System.out.println("�������绰"+str);
								Toast.makeText(getApplicationContext(),
										"���ͳɹ�", Toast.LENGTH_LONG);// ����Ŀǰû������ʾ��activity��
							}
						}).setNegativeButton("ȡ��", null).show();
				
			}
		});
		mListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					final int position, long id) {
				// TODO Auto-generated method stub
				final EditText et = new EditText(AddressBook.this);
				String str = et.getText().toString();
				// ������Ӱ�����
				new AlertDialog.Builder(AddressBook.this)
						.setTitle("��ӵ�������")
						.setPositiveButton("ȷ��",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {

										ContentValues values = new ContentValues();
										values.put("callerid",
												mContactsName.get(position));// �õ��绰����
										values.put("smsbody",
												mContactsNumber.get(position));
										db.saveSql(Constant.Caller_ID, values);// �����ݿ��д��������

										// System.out.println("�������绰"+str);
										Toast.makeText(getApplicationContext(),
												"��ӳɹ�", Toast.LENGTH_LONG);// ����Ŀǰû������ʾ��activity��
									}
								}).setNegativeButton("ȡ��", null).show();
				return false;
			}
		});

		super.onCreate(savedInstanceState);
	}

	/** �õ��ֻ�ͨѶ¼��ϵ����Ϣ **/
	private void getPhoneContacts() {
		ContentResolver resolver = mContext.getContentResolver();

		// ��ȡ�ֻ���ϵ��
		Cursor phoneCursor = resolver.query(Phone.CONTENT_URI,
				PHONES_PROJECTION, null, null, null);

		if (phoneCursor != null) {
			while (phoneCursor.moveToNext()) {

				// �õ��ֻ�����
				String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
				// ���ֻ�����Ϊ�յĻ���Ϊ���ֶ� ������ǰѭ��
				if (TextUtils.isEmpty(phoneNumber))
					continue;

				// �õ���ϵ������
				String contactName = phoneCursor
						.getString(PHONES_DISPLAY_NAME_INDEX);

				// �õ���ϵ��ID
				Long contactid = phoneCursor.getLong(PHONES_CONTACT_ID_INDEX);

				// �õ���ϵ��ͷ��ID
				Long photoid = phoneCursor.getLong(PHONES_PHOTO_ID_INDEX);

				// �õ���ϵ��ͷ��Bitamp
				Bitmap contactPhoto = null;

				// photoid ����0 ��ʾ��ϵ����ͷ�� ���û�и���������ͷ�������һ��Ĭ�ϵ�
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

	/** �õ��ֻ�SIM����ϵ������Ϣ **/
	private void getSIMContacts() {
		ContentResolver resolver = mContext.getContentResolver();
		// ��ȡSims����ϵ��
		Uri uri = Uri.parse("content://icc/adn");
		Cursor phoneCursor = resolver.query(uri, PHONES_PROJECTION, null, null,
				null);

		if (phoneCursor != null) {
			while (phoneCursor.moveToNext()) {

				// �õ��ֻ�����
				String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
				// ���ֻ�����Ϊ�յĻ���Ϊ���ֶ� ������ǰѭ��
				if (TextUtils.isEmpty(phoneNumber))
					continue;
				// �õ���ϵ������
				String contactName = phoneCursor
						.getString(PHONES_DISPLAY_NAME_INDEX);

				// Sim����û����ϵ��ͷ��

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
			// ���û�������
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
			// ������ϵ������
			contacts.title.setText(mContactsName.get(position));
			// ������ϵ�˺���
			contacts.text.setText(mContactsNumber.get(position));
			// ������ϵ��ͷ��
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