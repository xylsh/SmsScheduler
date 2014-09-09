package com.github.xylsh;

import com.beardedhen.androidbootstrap.BootstrapButton;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends ActionBarActivity {

	private BootstrapButton addButton = null;      //添加定时短信按钮
	private BootstrapButton viewButton = null;     //查看所有定时短信按钮
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		addButton = (BootstrapButton)this.findViewById(R.id.addButton);
		viewButton = (BootstrapButton)this.findViewById(R.id.viewButton);
		
		addButton.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent intent = new Intent( MainActivity.this, AddMsgActivity.class );
				MainActivity.this.startActivity(intent);
			}
		});
		
		viewButton.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent intent = new Intent( MainActivity.this, ViewAllActivity.class );
				MainActivity.this.startActivity(intent);
			}
		});
		
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
