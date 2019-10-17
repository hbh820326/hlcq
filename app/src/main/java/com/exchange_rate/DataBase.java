package com.exchange_rate;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class DataBase extends SQLiteOpenHelper {
	/**
	   * context 上下文
	   * name 数据库文件名
	   * factory 结果集
	   * version 数据库版本
	   * */
	public DataBase(Context context) {
		super(context,"Currency.db",null,1);//创建一个数据库

	}
	/**
	 * 当数据库文件被创建完成后 执行
	 * */
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table currency(keyname TEXT primary key,msg_json TEXT)");//msg的json串      String
//		//商品信息
//		db.execSQL("create table Storage (" +
//				"commodity_code TEXT primary key ," +//商品编码       String
//				"commodity_name TEXT, " +//商品名      String
//				"prix REAL," +//价格       double
//				"tel TEXT," +//供应商电话   String
//				"sales_volume INT,"+//总销量      int
//				"remarks TEXT)");//备注      String
//
//		//商品生产日期
//		db.execSQL("create table data(" +
//				"id INT primary key," +//id
//				"commodity_code TEXT," +//商品编码        String
//				"date0 TEXT,"+//生产日期      String
//				"count INT)");//数量      int
//		//销售信息（提交给服务器用来删减数量的）
//		db.execSQL("create table market(" +
//				"id INT primary key," +//id
//				"commodity_code TEXT," +//商品编码        String
//				"date0 TEXT,"+//生产日期      String
//				"count INT)");//数量      int

	}
	/**
	 * 当版本号有变时执行
	 * */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//		db.execSQL("DROP TABLE MSG.db.msg;");//删除表


//		db.execSQL("create table config(" +
//				"configKey TEXT primary key ," +//      String
//				"configValue TEXT)");//msg的json串      String
	}


    public static <T> T reanCurrencyBean(Context context, String keyName){
		SQLiteDatabase db = new DataBase(context).getWritableDatabase();
		Cursor cursor = db.rawQuery("select * from currency where keyname=?",new String[]{keyName} );
		if(cursor.getCount()==0){
			cursor.close();
			db.close();
			return null;
		}
		cursor.moveToNext();
		String  result = cursor.getString(cursor.getColumnIndex("msg_json"));
		cursor.close();
		db.close();
		return  new Gson().fromJson(result, new TypeToken<List<T>>(){}.getType());
	}
	public static String reanString(Context context, String keyName){
		SQLiteDatabase db = new DataBase(context).getWritableDatabase();
		Cursor cursor = db.rawQuery("select * from currency where keyname=?",new String[]{keyName} );
		if(cursor.getCount()==0){
			cursor.close();
			db.close();
			return null;
		}
		cursor.moveToNext();
		String  result = cursor.getString(cursor.getColumnIndex("msg_json"));
		cursor.close();
		db.close();
		return  result;
	}
    /**
     * 缓存
     */
    public static <T> void writeCurrencyBean(Context context,T data,String keyName){
            String json = new Gson().toJson(data);
            SQLiteDatabase db = new DataBase(context).getWritableDatabase();
            Cursor cursor = db.rawQuery("select * from currency where keyname=?", new String[]{keyName});
            if (cursor.getCount() == 0) {//没有数据插入
                db.execSQL("insert into currency(keyname,msg_json)values(?,?)", new String[]{keyName, json});
            } else {                 //有数据更新
                db.execSQL("update currency set msg_json=? where keyname=?", new String[]{json, keyName});
            }
            cursor.close();
            db.close();
    }
}
