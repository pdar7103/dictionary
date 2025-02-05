package com.dworld.accounting;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.dworld.accounting.Utils.Constant;


public class MenuFragment extends Fragment {

	LinearLayout ll_home,ll_fav,ll_share;
	static boolean isHome=true,isFav,isShare;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		View  v = inflater.inflate(R.layout.side_menu_fragment, container, false);


		ll_home = (LinearLayout) v.findViewById(R.id.ll_home);
		ll_fav = (LinearLayout) v.findViewById(R.id.ll_fav);
		ll_share = (LinearLayout) v.findViewById(R.id.ll_share);

		ll_home.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				MenuActivity.drawer_layout.closeDrawer(MenuActivity.left_drawer);
				if(!isHome){
					isHome = true;
					isFav = false;
					isShare= false;
					if(MainActivity.editTextheader != null ){
						MainActivity.editTextheader.setText("");
					}
					FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
					ft.replace(R.id.content_frame,
							MainActivity.newInstance(Constant.FROM_HOME, ""));
					ft.commit();
				}
			}
		});


		ll_fav.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				MenuActivity.drawer_layout.closeDrawer(MenuActivity.left_drawer);
				if(!isFav){
					isHome = false;
					isFav = true;
					isShare= false;
					if(MainActivity.editTextheader != null ){
						MainActivity.editTextheader.setText("");
					}
					FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
					ft.replace(R.id.content_frame,
							MainActivity.newInstance(Constant.FROM_FAV, ""));
					ft.commit();
				}

			}
		});

		ll_share.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				isHome = false;
				isFav = false;
				isShare= false;

				MenuActivity.drawer_layout.closeDrawer(MenuActivity.left_drawer);
				PackageManager pm = getActivity().getPackageManager();
				PackageInfo pInfo = null;
				String packageName="";
				try {
					pInfo =  pm.getPackageInfo(getActivity().getPackageName(),0);
					packageName =getActivity(). getApplicationContext().getPackageName();
				} catch (PackageManager.NameNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Intent sendIntent = new Intent();
				sendIntent.setAction(Intent.ACTION_SEND);
				sendIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id="+packageName);
				sendIntent.setType("text/plain");
				startActivity(sendIntent);
			}
		});

		return v;
	}
}
