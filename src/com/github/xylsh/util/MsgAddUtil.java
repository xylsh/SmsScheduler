package com.github.xylsh.util;

import java.util.Calendar;
import java.util.regex.Pattern;

import com.github.xylsh.receiver.AlarmReceiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;

public class MsgAddUtil {

	/**
	 * 添加定时短信
	 * 
	 * @param context
	 *            上下文
	 * @param phone
	 *            号码
	 * @param msgText
	 *            短信内容
	 * @param year
	 *            发送时间,年
	 * @param month
	 *            发送时间,月
	 * @param day
	 *            发送时间,日
	 * @param hour
	 *            发送时间,小时
	 * @param minute
	 *            发送时间,分钟
	 * @return 成功返回true,失败返回false
	 */
	public static boolean addMsg(Context context, Editable phone,
			Editable msgText, int year, int month, int day, int hour, int minute) {
		return addMsg(context, phone.toString(), msgText.toString(), year,
				month, day, hour, minute);
	}

	/**
	 * 添加定时短信
	 * 
	 * @param context
	 *            上下文
	 * @param phone
	 *            号码
	 * @param msgText
	 *            短信内容
	 * @param year
	 *            发送时间,年
	 * @param month
	 *            发送时间,月
	 * @param day
	 *            发送时间,日
	 * @param hour
	 *            发送时间,小时
	 * @param minute
	 *            发送时间,分钟
	 * @return 成功返回true,失败返回false
	 */
	public static boolean addMsg(Context context, String phone, String msgText,
			int year, int month, int day, int hour, int minute) {
		// 去掉两端空格
		phone = phone.trim();
		msgText = msgText.trim();
		if (!isValid(phone, msgText, year, month, day, hour, minute)) {
			return false;
		}

		int id = (int) addToDB(context, phone, msgText, year, month, day, hour,
				minute);
		if (id < 0) {
			return false;
		}
		addFixTimeMsg(context, id, phone, msgText, year, month, day, hour,
				minute);

		return true;
	}

	/**
	 * 添加定时短信到数据库
	 * 
	 * @param context
	 *            上下文
	 * @param phone
	 *            号码
	 * @param msgText
	 *            短信内容
	 * @param year
	 *            发送时间,年
	 * @param month
	 *            发送时间,月
	 * @param day
	 *            发送时间,日
	 * @param hour
	 *            发送时间,小时
	 * @param minute
	 *            发送时间,分钟
	 * @return the row ID of the newly inserted row, or -1 if an error occurred
	 */
	private static long addToDB(Context context, String phone, String msgText,
			int year, int month, int day, int hour, int minute) {
		FixMsgSQLiteHelper sqlHelper = new FixMsgSQLiteHelper(context,
				FixMsgSQLiteHelper.DB_NAME, null, 1);
		String sendTime = year + "-" + (month + 1) + "-" + day + " " + hour
				+ ":" + minute; // month从0开始算的，所以存储时+1
		long num = sqlHelper.insertFixMsg(phone, msgText, sendTime);
		return num;
	}

	/**
	 * 添加定时短信到系统
	 * 
	 * @param context
	 *            上下文
	 * @param requestCode
	 *            Private request code for the sender (currently not used).
	 *            在设定系统定时发送和取消定时发送时要保证requestCode一致才行
	 * @param phone
	 *            号码
	 * @param msgText
	 *            短信内容
	 * @param year
	 *            发送时间,年
	 * @param month
	 *            发送时间,月
	 * @param day
	 *            发送时间,日
	 * @param hour
	 *            发送时间,小时
	 * @param minute
	 *            发送时间,分钟
	 */
	public static void addFixTimeMsg(Context context, int requestCode,
			String phone, String msgText, int year, int month, int day,
			int hour, int minute) {
		AlarmManager aManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);

		Intent intent = new Intent(context, AlarmReceiver.class);
		intent.setAction("com.github.xylsh.receiver.send_msg");
		intent.putExtra("com.github.xylsh.phone_number", phone);
		intent.putExtra("com.github.xylsh.msg_text", msgText);
		intent.putExtra("com.github.xylsh.msg_id", requestCode);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
				requestCode, intent, 0);

		Calendar c = Calendar.getInstance();
		c.set(year, month, day, hour, minute, 0);

		// Beginning in API 19,考虑使用setExact(...)
		aManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(),
				pendingIntent);
	}

	/**
	 * 取消某条定时短信
	 * 
	 * @param context
	 *            上下文
	 * @param requestCode
	 *            在设定系统定时发送时的requestCode
	 */
	public static void cancleFixTimeMsg(Context context, int requestCode) {
		AlarmManager aManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);

		Intent intent = new Intent(context, AlarmReceiver.class);
		intent.setAction("com.github.xylsh.receiver.send_msg");
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
				requestCode, intent, 0);
		aManager.cancel(pendingIntent);
	}

	/**
	 * 验证参数是否合法
	 * 
	 * @return 合法返回true,非法返回false
	 */
	public static boolean isValid(String phone, String msgText, int year,
			int month, int day, int hour, int minute) {
		// 首先验证长度
		if (phone.length() == 0 || msgText.length() == 0) {
			return false;
		}

		// 号码只能是数字，暂不考虑+86形式的号码
		if (!phone.matches("^[0-9]+$")) {
			return false;
		}

		// 获得当前时间
		Calendar c = Calendar.getInstance();
		int currYear = c.get(Calendar.YEAR);
		int currMonth = c.get(Calendar.MONTH);
		int currDay = c.get(Calendar.DAY_OF_MONTH);
		int currHour = c.get(Calendar.HOUR_OF_DAY);
		int currMinute = c.get(Calendar.MINUTE) + 0; // 时间至少是当前时间的3分钟之后,所以+3

		// 时间至少是当前时间的3分钟之后
		try {
			// 不能是过去的日期
			if (year < currYear
					|| (year == currYear && month < currMonth)
					|| (year == currYear && month == currMonth && day < currDay)) {
				return false;
			}
			// 如果日期是今天，那么不能是过去的时间
			if (year == currYear || month == currMonth || day == currDay) {
				if (hour < currHour
						|| (hour == currHour && minute < currMinute)) {
					return false;
				}
			}

		} catch (Exception e) {
			return false;
			// throw new IllegalArgumentException();
		}

		return true;
	}

}
