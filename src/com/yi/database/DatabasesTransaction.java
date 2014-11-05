package com.yi.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabasesTransaction {
	private static DataBaseHelper dataBaseHelper = null;
	private static DatabasesTransaction ct = null;
	private SQLiteDatabase db;

	private DatabasesTransaction() {
		db = dataBaseHelper.getWritableDatabase();
	}

	public static DatabasesTransaction getInstance(Context context) {

		if (ct == null) {
			dataBaseHelper = new DataBaseHelper(
					context.getApplicationContext(),
					dataBaseHelper.DATABASE_NAME, null,
					dataBaseHelper.DATABASE_VERSION);
			ct=new DatabasesTransaction();//û����仰�ᱨ��ָ��
		}
		return ct;

	}

	public Cursor selectsql(String sql) {
		Cursor cursor = db.rawQuery(sql, null);

		return cursor;

	}

	public long saveSql(String table, ContentValues values) {
		return db.insert(table, null, values);

	}

	public void deleteData(String tableName, String whereSql, String[] whereArgs) {
		db.delete(tableName, whereSql, whereArgs);
	}

	public int updataeSql(String table, ContentValues values, String whereSql) {
		return db.update(table, values, whereSql, null);

	}

	public void execSql(String sql) {
		db.execSQL(sql);
	}

	public void cleanDb() {
		dataBaseHelper.cleanDb(db);// ����dataBaseHelper�ķ���

	}

	private static class DataBaseHelper extends SQLiteOpenHelper {

		private static final String DATABASE_NAME = "data.db";
		private static final int DATABASE_VERSION = 1;
		private static final String CREATE_LAIDIAN_SQL = "CREATE TABLE if not exists "
				+ Constant.Caller_ID
				+ "(callerid TEXT PRIMARY KEY , "
				+ "smsbody TEXT" + ");";

		// �Զ����ɹ��캯��
		public DataBaseHelper(Context context, String name,
				CursorFactory factory, int version) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			// TODO Auto-generated constructor stub
		}

		public void cleanDb(SQLiteDatabase db) {
			// TODO Auto-generated method stub

		}
//����ʵ��3������1 ���췽����2�������ݿ�����ݿ����
		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			
			db.execSQL(CREATE_LAIDIAN_SQL);// �洢�����ֶ�һ���绰����һ����Ϣ����
		}
//3�������ݿ�汾����
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			//cleanDb(db);
		}

	}
}
