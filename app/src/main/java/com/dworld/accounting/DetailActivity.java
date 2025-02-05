package com.dworld.accounting;

import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowMetrics;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.viewpager.widget.ViewPager;

import com.dworld.accounting.adapter.ViewpagerAdapter;
import com.dworld.accounting.interfaces.ClickOnSpeakButton;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;


public class DetailActivity extends Activity implements ClickOnSpeakButton {

	ImageView iv_menuitem, img_right;
	TextView txtheader;
	ViewPager pagerView ;
	ViewpagerAdapter vp_adapter ;
	Context mContext;
	int pos =0;

	// Get the ad size with screen width.
	public AdSize getAdSize() {
		DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
		int adWidthPixels = displayMetrics.widthPixels;

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
			WindowMetrics windowMetrics = this.getWindowManager().getCurrentWindowMetrics();
			adWidthPixels = windowMetrics.getBounds().width();
		}

		float density = displayMetrics.density;
		int adWidth = (int) (adWidthPixels / density);
		return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth);
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);
		mContext = DetailActivity.this;




		pos = getIntent().getIntExtra("pos", 0);
		FrameLayout adViewContainer = findViewById(R.id.adViewContainer);
//		AdView mAdView = (AdView) findViewById(R.id.adView);

		AdView mAdView = new AdView(this);
		mAdView.setAdUnitId(getString(R.string.banner_ad_unit_id));
		mAdView.setAdSize(getAdSize());

// Replace ad container with new ad view.
		adViewContainer.removeAllViews();
		adViewContainer.addView(mAdView);

		AdRequest adRequest = new AdRequest.Builder().build();
		mAdView.loadAd(adRequest);

//		AdRequest adRequest = new AdRequest.Builder()
//		.addTestDevice("1DDA3DE5902F42DE3931D3E8125FB615")
//		.build();
//		mAdView.loadAd(adRequest);

		iv_menuitem = (ImageView)findViewById(R.id.iv_menuitem);
		img_right = (ImageView)findViewById(R.id.img_right);
		txtheader = (TextView)findViewById(R.id.txtheader);

		pagerView = (ViewPager) findViewById(R.id.pagerView);

		vp_adapter = new ViewpagerAdapter(mContext, MainActivity.list, this);
		pagerView.setAdapter(vp_adapter);

		pagerView.setCurrentItem(pos);


		img_right.setVisibility(View.GONE);
		txtheader.setText("Detail");
		iv_menuitem.setImageResource(R.drawable.backarrow);

		iv_menuitem.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}

	@Override
	public void onClickOnSpeakButton(String text) {
		try {

			final String convertTextToSpeech = text;
			if (MainActivity.convertToSpeech != null) {
				MainActivity.convertToSpeech.stop();
				// SplashActivity.convertToSpeech.shutdown();
				MainActivity.convertToSpeech
				.setLanguage(Locale.US);
			}
			if (MainActivity.ttsStatus != TextToSpeech.ERROR) {
				Log.e("Is Speaking", String
						.valueOf(MainActivity.convertToSpeech
								.isSpeaking()));
				// if
				// (!SplashActivity.convertToSpeech.isSpeaking())
				// {

				MainActivity.convertToSpeech.speak(
						convertTextToSpeech,
						TextToSpeech.QUEUE_ADD, null);

				// }
			} else {
				MainActivity.convertToSpeech = new TextToSpeech(mContext, new TextToSpeech.OnInitListener() {

					@Override
					public void onInit(int paramInt) {
						MainActivity.ttsStatus = paramInt;
						if (MainActivity.ttsStatus != TextToSpeech.ERROR) {
							if (!MainActivity.convertToSpeech
									.isSpeaking()) {

								// SplashActivity.convertToSpeech
								// .setLanguage(Locale.US);
								MainActivity.convertToSpeech
								.speak(convertTextToSpeech,
										TextToSpeech.QUEUE_FLUSH,
										null);

							}
						}

					}
				});
			}

		} catch (Exception e) {
			// TODO: handle exception
		}

	}


}
