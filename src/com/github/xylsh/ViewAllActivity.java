package com.github.xylsh;

import java.util.ArrayList;

import com.beardedhen.androidbootstrap.FontAwesomeText;
import com.daimajia.swipe.SwipeAdapter;
import com.daimajia.swipe.SwipeLayout;
import com.github.xylsh.bean.FixMessage;
import com.github.xylsh.util.FixMsgSQLiteHelper;
import com.github.xylsh.util.MsgAddUtil;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.annotation.SuppressLint;
import android.content.Context;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ViewAllActivity extends ActionBarActivity {

	private ViewPager viewPager = null;
	private ListView waitSentMsgList = null;
	private ListView haveSentMsgList = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_all);

		init();
	}

	/**
	 * 一些初始化工作
	 */
	public void init() {
		viewPager = (ViewPager) this.findViewById(R.id.viewpager);

		LayoutInflater flater = LayoutInflater.from(this);
		View waitSendView = flater.inflate(R.layout.activity_view_all_item,
				null);
		View haveSendView = flater.inflate(R.layout.activity_view_all_item,
				null);

		FixMsgSQLiteHelper sqlHelper = new FixMsgSQLiteHelper(
				ViewAllActivity.this, FixMsgSQLiteHelper.DB_NAME, null, 1);
		ArrayList<FixMessage> waitSendMsgList = sqlHelper.getNotSendFixMsg();   //获取未发送短信列表
		ArrayList<FixMessage> haveSendMsgList = sqlHelper.getHaveSendFixMsg();  //获取已发送短信列表

		waitSentMsgList = (ListView) waitSendView.findViewById(R.id.msgList);
		ListViewAdapter waitSentListViewAdapter = new ListViewAdapter(
				ViewAllActivity.this, waitSendMsgList, 0);
		waitSentMsgList.setAdapter(waitSentListViewAdapter);

		haveSentMsgList = (ListView) haveSendView.findViewById(R.id.msgList);
		ListViewAdapter haveSentListViewAdapter = new ListViewAdapter(
				ViewAllActivity.this, haveSendMsgList, 1);
		haveSentMsgList.setAdapter(haveSentListViewAdapter);

		ArrayList<View> viewList = new ArrayList<View>(2);
		ArrayList<String> titleList = new ArrayList<String>(2);
		viewList.add(waitSendView);
		viewList.add(haveSendView);
		titleList.add("待发送");
		titleList.add("已发送");

		viewPager.setAdapter(new CustomPagerAdapter(viewList, titleList));
	}

	public class ListViewAdapter extends SwipeAdapter {

		private ArrayList<FixMessage> msgList = null;
		private Context mContext;
		/**
		 * 表示当前的ListViewAdapter的类型。 0表示是待发送短信的listview的adapter,
		 * 1表示是已发送短信的listview的adapter。
		 */
		private int type;

		public ListViewAdapter(Context mContext, ArrayList<FixMessage> msgList,
				int type) {
			this.mContext = mContext;
			this.msgList = msgList;
			this.type = type;
		}

		@Override
		public int getSwipeLayoutResourceId(int position) {
			return R.id.itemSwipeLayout;
		}

		@SuppressLint("InflateParams")
		@Override
		public View generateView(int position, ViewGroup parent) {
			View view = LayoutInflater.from(mContext).inflate(
					R.layout.msg_list_item, null);
			return view;
		}

		@Override
		public void fillValues(int position, View convertView) {
			TextView phoneNumberTV = (TextView) convertView
					.findViewById(R.id.phoneNumber);
			TextView msgContentTV = (TextView) convertView
					.findViewById(R.id.msgContent);
			TextView msgStatusTV = (TextView) convertView
					.findViewById(R.id.msgStatus);
			TextView msgTimeTV = (TextView) convertView
					.findViewById(R.id.msgTime);
			FontAwesomeText delMsgFAT = (FontAwesomeText) convertView
					.findViewById(R.id.delMsg);
			delMsgFAT.setTag(position);

			FixMessage currMessage = msgList.get(position);

			phoneNumberTV.setText(Html.fromHtml("<b>号码:</b> "
					+ currMessage.getPhoneNumber()));
			msgContentTV.setText(Html.fromHtml("<b>内容:</b> "
					+ currMessage.getMsgText()));
			msgTimeTV.setText(Html.fromHtml("<b>时间:</b> "
					+ currMessage.getSendTime()));
			msgStatusTV.setText(currMessage.getHaveSend() == 0 ? "状态: 待发送"
					: "状态: 已发送");

			// 为删除按钮绑定事件
			delMsgFAT.setOnClickListener(new DelMsgFATClickListener());
		}

		public class DelMsgFATClickListener implements View.OnClickListener {
			@Override
			public void onClick(View v) {
				Integer position = (Integer) v.getTag();
				FixMessage msgToDel = msgList.get(position);

				// 如果是待发送短信，取消定时短信任务
				if (type == 0) {
					MsgAddUtil.cancleFixTimeMsg(ViewAllActivity.this,
							msgToDel.getId());
				}

				// 删除数据库记录
				delItem(msgToDel);

				Toast.makeText(ViewAllActivity.this, "删除了一条记录!",
						Toast.LENGTH_SHORT).show();
			}

			private void delItem(FixMessage msgToDel) {

				FixMsgSQLiteHelper sqlHelper = new FixMsgSQLiteHelper(
						ViewAllActivity.this, FixMsgSQLiteHelper.DB_NAME, null,
						1);
				sqlHelper.deleteFixMsg(msgToDel);
				msgList = type == 0 ? sqlHelper.getNotSendFixMsg() : sqlHelper
						.getHaveSendFixMsg();
				notifyDataSetChanged();
			}

		}

		@Override
		public int getCount() {
			return msgList.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		public void unregisterDataSetObserver(DataSetObserver observer) {
			if (observer != null) {
				super.unregisterDataSetObserver(observer);
			}
		}
	}

	public class CustomPagerAdapter extends PagerAdapter {
		private ArrayList<View> viewList = null;
		private ArrayList<String> titleList = null;

		public CustomPagerAdapter(ArrayList<View> viewList,
				ArrayList<String> titleList) {
			super();
			this.viewList = viewList;
			this.titleList = titleList;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public int getCount() {
			return viewList.size();
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(viewList.get(position));
		}

		@Override
		public int getItemPosition(Object object) {
			return super.getItemPosition(object);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return titleList.get(position);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			container.addView(viewList.get(position));
			return viewList.get(position);
		}

	}
}
