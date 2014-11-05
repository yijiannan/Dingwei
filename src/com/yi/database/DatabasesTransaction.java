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
			ct=new DatabasesTransaction();//没有这句话会报空指针
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
		dataBaseHelper.cleanDb(db);// 调用dataBaseHelper的方法

	}

	private static class DataBaseHelper extends SQLiteOpenHelper {

		private static final String DATABASE_NAME = "data.db";
		private static final int DATABASE_VERSION = 1;
		private static final String CREATE_LAIDIAN_SQL = "CREATE TABLE if not exists "
				+ Constant.Caller_ID
				+ "(callerid TEXT PRIMARY KEY , "
				+ "smsbody TEXT" + ");";

		// 自动生成构造函数
		public DataBaseHelper(Context context, String name,
				CursorFactory factory, int version) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			// TODO Auto-generated constructor stub
		}

		public void cleanDb(SQLiteDatabase db) {
			// TODO Auto-generated method stub

		}
//至少实现3个方法1 构造方法，2创建数据库对数据库操作
		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			
			db.execSQL(CREATE_LAIDIAN_SQL);// 存储两个字段一个电话号码一个信息内容
		}
//3更改数据库版本操作
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			//cleanDb(db);
		}

	}
}
