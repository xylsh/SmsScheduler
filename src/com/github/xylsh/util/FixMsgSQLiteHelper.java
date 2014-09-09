package com.github.xylsh.util;

import java.util.ArrayList;

import com.github.xylsh.bean.FixMessage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 操作数据库的helper
 *
 */
public class FixMsgSQLiteHelper extends SQLiteOpenHelper {

	/**
	 * 数据库名称
	 */
	public static final String DB_NAME = "FixMsgDB";
	/**
	 * 数据表名称
	 */
	public static final String FIX_MSG_TABLE_NAME = "MsgDataTable";

	public FixMsgSQLiteHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "create table "
				+ FIX_MSG_TABLE_NAME
				+ " ( id integer primary key autoincrement,phone_number text, msg_text text, send_time text, have_send integer )";
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// 数据库被改变时，将原先的表删除，然后建立新表
		String sql = "drop table if exists " + FIX_MSG_TABLE_NAME;
		db.execSQL(sql);
		onCreate(db);
	}


	/**
	 * 获得未发送短信
	 * @return 未发送短信列表
	 */
	public ArrayList<FixMessage> getNotSendFixMsg() {
		return getFixMsg("have_send=?", new String[] { "0" });
	}

	/**
	 * 获得已发送短信
	 * @return 已发送短信列表
	 */
	public ArrayList<FixMessage> getHaveSendFixMsg() {
		return getFixMsg("have_send=?", new String[] { "1" });
	}

	/**
	 * 获取数据库中的FixMsg
	 * 
	 * @param selection
	 *            A filter declaring which rows to return, formatted as an SQL
	 *            WHERE clause (excluding the WHERE itself). Passing null will
	 *            return all rows for the given table.
	 * @param selectionArgs
	 *            You may include ?s in selection, which will be replaced by the
	 *            values from selectionArgs, in order that they appear in the
	 *            selection. The values will be bound as Strings.
	 * @return
	 */
	public ArrayList<FixMessage> getFixMsg(String selection,
			String[] selectionArgs) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(FIX_MSG_TABLE_NAME, null, selection,
				selectionArgs, null, null, "id desc", null);
		ArrayList<FixMessage> msgList = new ArrayList<FixMessage>();
		FixMessage currMsg;
		while (cursor.moveToNext()) {
			currMsg = new FixMessage();
			currMsg.setId(cursor.getInt(0));
			currMsg.setPhoneNumber(cursor.getString(1));
			currMsg.setMsgText(cursor.getString(2));
			currMsg.setSendTime(cursor.getString(3));
			currMsg.setHaveSend(cursor.getInt(4));
			msgList.add(currMsg);
		}

		return msgList;
	}
	
	/**
	 * 插入一条定时短信记录到数据库
	 * 
	 * @param phoneNumber
	 * @param msgText
	 * @param sendTime
	 * @return the row ID of the newly inserted row, or -1 if an error occurred
	 */
	public long insertFixMsg(String phoneNumber, String msgText, String sendTime) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put("phone_number", phoneNumber);
		cv.put("msg_text", msgText);
		cv.put("send_time", sendTime);
		cv.put("have_send", 0); // 0表示未发送
		return db.insert(FIX_MSG_TABLE_NAME, null, cv);
	}

	/**
	 * 删除数据库中的指定定时短信
	 * @param msg
	 */
	public void deleteFixMsg(FixMessage msg) {
		deleteFixMsg(msg.getId());
	}

	/**
	 * 删除数据库中的指定定时短信
	 * @param idToDel
	 */
	public void deleteFixMsg(int idToDel) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(FIX_MSG_TABLE_NAME, "id=?",
				new String[] { String.valueOf(idToDel) });
	}
	
	/**
	 * 设置指定id的have_send字段为1
	 * @param id
	 */
	public void setMsgHaveSend(int id){
		if( id < 0 ){
			return;
		}
		SQLiteDatabase db = this.getWritableDatabase();
		String sql = "update "+ FIX_MSG_TABLE_NAME +" set have_send=1 where id=" + id;
		db.execSQL(sql);
	}

}
