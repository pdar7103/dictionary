package com.dworld.accounting;

import android.os.Bundle;

import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.dworld.accounting.Utils.Constant;
import com.dworld.architecture.R;


public class MenuActivity extends FragmentActivity {

	static DrawerLayout drawer_layout;
	static View left_drawer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);
		left_drawer = findViewById(R.id.left_drawer);
		drawer_layout = (DrawerLayout) findViewById(R.id.drawer_layout);


		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.replace(R.id.content_frame,
				MainActivity.newInstance(Constant.FROM_HOME, ""));
		ft.commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
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

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			if(MainActivity.editTextheader != null ){
				MainActivity.editTextheader.setText("");
			}

			finish();
			//			System.exit(0);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
