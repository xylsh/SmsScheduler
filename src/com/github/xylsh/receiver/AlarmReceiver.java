package com.github.xylsh.receiver;

import java.util.ArrayList;

import com.github.xylsh.util.FixMsgSQLiteHelper;
import com.github.xylsh.util.MsgAddUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.widget.Toast;

/**
 * 接收定时广播，收到广播则发送短信
 *
 */
public class AlarmReceiver extends BroadcastReceiver {

	public AlarmReceiver() {
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		String phoneNumber = intent.getStringExtra("com.github.xylsh.phone_number");
		String msgText = intent.getStringExtra("com.github.xylsh.msg_text");
		int msgId = intent.getIntExtra("com.github.xylsh.msg_id", -1);
		
		//发送短信
		sendMsg(context, phoneNumber, msgText);
		
		//标记这条记录已发送
		FixMsgSQLiteHelper sqlHelper = new FixMsgSQLiteHelper(context, FixMsgSQLiteHelper.DB_NAME,
				null, 1);
		sqlHelper.setMsgHaveSend( msgId );
	}
	
	/**
	 * 发送短信
	 * @param context
	 * @param phoneNumber
	 * @param msgText
	 */
	public void sendMsg(Context context,String phoneNumber,String msgText){
		SmsManager smsManager = SmsManager.getDefault();
		ArrayList<String> msgTexts = smsManager.divideMessage(msgText);   //如果短信过长，则分多条发送
		for( String msgStr : msgTexts ){
			smsManager.sendTextMessage(phoneNumber, null, msgStr, null, null);
		}
		Toast.makeText(context, "发送了一条定时短信!", Toast.LENGTH_LONG).show();
	}
}
