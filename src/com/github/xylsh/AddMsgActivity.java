package com.github.xylsh;

import java.util.Calendar;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.github.xylsh.receiver.AlarmReceiver;
import com.github.xylsh.util.MsgAddUtil;

import android.support.v7.app.ActionBarActivity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

public class AddMsgActivity extends ActionBarActivity {

	private EditText phoneNumEditText = null;
	private EditText msgContentEditText = null;
	private BootstrapButton addMsgButton = null;
	private TextView dateTextView = null;
	private BootstrapButton setTimeButton = null;
	private BootstrapButton setDateButton = null;

	private DatePickerDialog datePickerDialog = null; // 设置定时发送日期
	private TimePickerDialog timePickerDialog = null; // 设置定时发送时间

	private int year, month, day, hour, minute;    //所设置的发送时间，初始为当前时间

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_msg);

		init();

	}

	/**
	 * 一些初始化工作
	 */
	private void init() {
		phoneNumEditText = (EditText) this.findViewById(R.id.phoneNumEditText);
		msgContentEditText = (EditText) this
				.findViewById(R.id.msgContentEditText);
		addMsgButton = (BootstrapButton) this.findViewById(R.id.addMsgButton);
		dateTextView = (TextView) this.findViewById(R.id.dateTextView);
		setTimeButton = (BootstrapButton) this.findViewById(R.id.setTimeButton);
		setDateButton = (BootstrapButton) this.findViewById(R.id.setDateButton);

		msgContentEditText.setMaxHeight(msgContentEditText.getHeight());
		addMsgButton.setTextGravity("center"); // 设置按钮字体居中

		// 获取当前时间，用来初始化时间
		Calendar c = Calendar.getInstance();
		year = c.get(Calendar.YEAR);
		month = c.get(Calendar.MONTH);
		day = c.get(Calendar.DAY_OF_MONTH);
		hour = c.get(Calendar.HOUR_OF_DAY);
		minute = c.get(Calendar.MINUTE);

		setDateTimeText(dateTextView, year, month, day, hour, minute);   //显示当前时间

		datePickerDialog = new DatePickerDialog(AddMsgActivity.this,
				new CustomDateSetListener(), year, month, day);
		timePickerDialog = new TimePickerDialog(AddMsgActivity.this,
				new CustomTimeSetListener(), hour, minute, true);

		setDateButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				datePickerDialog.show();
			}
		});
		setTimeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				timePickerDialog.show();
			}
		});

		addMsgButton.setOnClickListener(new AddMsgButtonClickListener());
	}

	/**
	 * 添加定时短信按钮的listener
	 *
	 */
	public class AddMsgButtonClickListener implements View.OnClickListener {

		private boolean result = false;
		@Override
		public void onClick(View v) {
			result = MsgAddUtil.addMsg(AddMsgActivity.this, phoneNumEditText.getText(),
					msgContentEditText.getText(), year, month, day, hour, minute);
			
			AlertDialog.Builder builder = new AlertDialog.Builder(AddMsgActivity.this);
			if( result ){   //如果成功
				builder.setTitle("Info");
				builder.setMessage("添加定时短信成功！");
			}else{
				builder.setTitle("Error");
				builder.setMessage("添加定时短信失败！\n请检查您输入的号码、短信内容、发送时间和存储空间。");
			}
			builder.setPositiveButton("确定", new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int which) {
					if( AddMsgButtonClickListener.this.result ){
						AddMsgActivity.this.finish();
					}
				}
			});
			builder.show();
		}
	}

	public class CustomDateSetListener implements
			DatePickerDialog.OnDateSetListener {
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			AddMsgActivity.this.year = year;
			AddMsgActivity.this.month = monthOfYear;
			AddMsgActivity.this.day = dayOfMonth;
			AddMsgActivity.this.setDateText(dateTextView, year, monthOfYear,
					dayOfMonth);
		}
	}

	public class CustomTimeSetListener implements
			TimePickerDialog.OnTimeSetListener {
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			AddMsgActivity.this.hour = hourOfDay;
			AddMsgActivity.this.minute = minute;
			AddMsgActivity.this.setTimeText(dateTextView, hourOfDay, minute);
		}
	}

	/**
	 * 设置日期显示
	 */
	private void setDateTimeText(TextView textView, int year, int month,
			int day, int hour, int minute) {
		setDateText(textView, year, month, day);
		setTimeText(textView, hour, minute);
	}

	/**
	 * 显示日期部分
	 */
	private void setDateText(TextView textView, int year, int month, int day) {
		String[] splits = textView.getText().toString().split("\n");
		String fullStr = null;
		String dateStr = new StringBuilder().append(year).append("-")
				.append((month + 1) < 10 ? "0" + (month + 1) : (month + 1))
				.append("-").append((day < 10) ? "0" + day : day).toString();
		if (splits.length == 2) {
			fullStr = dateStr + "\n" + splits[1];
		} else {
			fullStr = dateStr + "\n";
		}
		textView.setText(fullStr);
	}

	/**
	 * 显示时间部分
	 */
	private void setTimeText(TextView textView, int hour, int minute) {
		String[] splits = textView.getText().toString().split("\n");
		String fullStr = null;
		String timeStr = new StringBuilder()
				.append((hour < 10) ? "0" + hour : hour).append(" : ")
				.append((minute < 10) ? "0" + minute : minute).toString();
		if (splits.length == 2) {
			fullStr = splits[0] + "\n" + timeStr;
		} else {
			fullStr = "\n" + timeStr;
		}
		textView.setText(fullStr);
	}
}
