package com.dworld.accounting.adapter;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;



import com.dworld.accounting.model.DictionaryModel;

import com.dworld.accounting.database.DatabaseHelper;
import com.dworld.accounting.interfaces.ClickOnSpeakButton;
import com.dworld.architecture.R;


public class ViewpagerAdapter extends PagerAdapter {
	Context context;
	public static ArrayList<DictionaryModel> list;

	ClickOnSpeakButton clickOnSpeakButton;
	DatabaseHelper dHelper ;

	public ViewpagerAdapter(Context context, ArrayList<DictionaryModel> list,
			ClickOnSpeakButton clickOnSpeakButton) {
		this.context = context;
		this.list = list;
		this.clickOnSpeakButton = clickOnSpeakButton;
		dHelper = DatabaseHelper.getInstance(context);


	}



	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}


	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		// TODO Auto-generated method stub
		container.removeView((View) object);
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		// TODO Auto-generated method stub
		return arg0 == arg1;
	}

	@SuppressLint("NewApi") @TargetApi(24) public Object instantiateItem(final ViewGroup collection, final int position) {

		LayoutInflater inflater = LayoutInflater.from(context);
		ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.viewpagerview,
				collection, false);

		final TextView   txtengword = (TextView) layout.findViewById(R.id.txtengword);
		final TextView   txtgujword = (TextView) layout.findViewById(R.id.txtgujword);
		final TextView  txtwordtype  = (TextView) layout.findViewById(R.id.txtwordtype);
		final TextView txtsynonym= (TextView) layout.findViewById(R.id.txtsynonym);
		final TextView txttiteltype = (TextView) layout.findViewById(R.id.txttiteltype);
		
		ImageView img_speaker = (ImageView) layout.findViewById(R.id.img_speaker);
		ImageView img_share = (ImageView) layout.findViewById(R.id.img_share);
		final ImageView img_fav = (ImageView) layout.findViewById(R.id.img_fav);

		WebView wv_desc = (WebView) layout.findViewById(R.id.wv_detail);

		wv_desc.getSettings().setBuiltInZoomControls(true);
		wv_desc.getSettings().setSupportZoom(true); 
		wv_desc.getSettings().setJavaScriptEnabled(true);
		wv_desc.setWebViewClient(new WebViewClient());
//		wv_desc.getSettings().setUseWideViewPort(true);
//		wv_desc.getSettings().setLoadWithOverviewMode(true);

		txtsynonym.setVisibility(View.GONE);
		txttiteltype.setVisibility(View.GONE);



		wv_desc.loadUrl("https://en.m.wikipedia.org/wiki/"+list.get(position).getEngword().replace(" ", "_"));

		txtengword.setText(list.get(position).getEngword());
		txttiteltype.setText("("+list.get(position).getType()+")");
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
			txtgujword.setText(Html.fromHtml("<html><body><b>Description :</b>"+list.get(position).getMeaning()+"</body></html>",Html.FROM_HTML_MODE_LEGACY));
			txtsynonym.setText(Html.fromHtml("<html><body><b>Synonym :</b>"+list.get(position).getAsynconamus()+"</body></html>",Html.FROM_HTML_MODE_LEGACY));
		} else {
			txtgujword.setText(Html.fromHtml("<html><body><b>Description :</b>"+list.get(position).getMeaning()+"</body></html>"));
			txtsynonym.setText(Html.fromHtml("<html><body><b>Synonym :</b>"+list.get(position).getAsynconamus()+"</body></html>"));

		}
		
		
		if(list.get(position).getFavouriteword() != null  && list.get(position).getFavouriteword().equalsIgnoreCase("1")){
			img_fav.setImageResource(R.drawable.favselected);
		}else{
			img_fav.setImageResource(R.drawable.heart);
		}

		img_speaker.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				clickOnSpeakButton.onClickOnSpeakButton(txtengword.getText()
						.toString());

			}
		});
		img_share.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				PackageManager pm = context.getPackageManager();
				PackageInfo pInfo = null;
				String packageName="";
				try {
					pInfo =  pm.getPackageInfo(context.getPackageName(),0);
					packageName =context. getApplicationContext().getPackageName();
				} catch (PackageManager.NameNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Intent sendIntent = new Intent();
				sendIntent.setAction(Intent.ACTION_SEND);
				sendIntent.putExtra(Intent.EXTRA_TEXT, txtengword.getText().toString()+"\n    "+
						txtgujword.getText().toString()+
						" \n\nYou can Download this Dictionary from here --"+
						"https://play.google.com/store/apps/details?id="+packageName);
				sendIntent.setType("text/plain");
				context.startActivity(sendIntent);	

			}
		});
		img_fav.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(list.get(position).getFavouriteword() != null  && list.get(position).getFavouriteword().equalsIgnoreCase("1")){
					img_fav.setImageResource(R.drawable.favselected);
					ContentValues cv = new ContentValues();
					cv.put(DatabaseHelper.KEY_FAVWORD, 0);
					dHelper.Updatetbl(DatabaseHelper.TBL_NAME_WORD, cv,list.get(position).getId());
					list.get(position).setFavouriteword("0");
				}else{
					ContentValues cv = new ContentValues();
					cv.put(DatabaseHelper.KEY_FAVWORD, 1);
					dHelper.Updatetbl(DatabaseHelper.TBL_NAME_WORD, cv,list.get(position).getId());
					list.get(position).setFavouriteword("1");
					img_fav.setImageResource(R.drawable.heart);

				}
				notifyDataSetChanged();
			}
		});










		collection.addView(layout);
		return layout;

	}

}
