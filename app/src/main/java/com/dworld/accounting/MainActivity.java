package com.dworld.accounting;

import java.util.ArrayList;
import java.util.Locale;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowMetrics;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.dworld.accounting.Utils.Constant;
import com.dworld.accounting.adapter.DictionaryAdapter;
import com.dworld.accounting.database.DatabaseHelper;
import com.dworld.accounting.interfaces.ClickOnSpeakButton;
import com.dworld.accounting.model.DictionaryModel;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;



public class MainActivity extends Fragment implements ClickOnSpeakButton {

	Context mContext;
	DatabaseHelper dbHelper ;
	ListView lv_word;
	DictionaryAdapter dictionaryadapter ;

	ImageView iv_menuitem, img_right;
	TextView txtheader;
	 static AutoCompleteTextView editTextheader;
	boolean isVisible = true;
	String isFrom = Constant.FROM_HOME;
	private static final String ARG_ISFROM = "isFrom";
	private static final String ARG_PARAM2 = "param2";
	static ArrayList<DictionaryModel> list,favlist ;

	public static TextToSpeech convertToSpeech;

	public static int ttsStatus;
	public static String speed = "Slow";

	public static MainActivity newInstance(String isFrom, String param2) {
		MainActivity fragment = new MainActivity();
		Bundle args = new Bundle();
		args.putString(ARG_ISFROM, isFrom);
		args.putString(ARG_PARAM2, param2);
		fragment.setArguments(args);
		return fragment;
	}
	public AdSize getAdSize() {
		DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
		int adWidthPixels = displayMetrics.widthPixels;

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
			WindowMetrics windowMetrics = this.getActivity().getWindowManager().getCurrentWindowMetrics();
			adWidthPixels = windowMetrics.getBounds().width();
		}

		float density = displayMetrics.density;
		int adWidth = (int) (adWidthPixels / density);
		return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(getActivity(), adWidth);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreateView(inflater, container, savedInstanceState);
		mContext = getActivity();


		dbHelper = DatabaseHelper.getInstance(mContext);
		isFrom = getArguments().getString(ARG_ISFROM);

		convertToSpeech = new TextToSpeech(getActivity().getApplicationContext(),
				new TextToSpeech.OnInitListener() {

			@Override
			public void onInit(int status) {

				ttsStatus = status;

				try {
					MainActivity.convertToSpeech
					.setLanguage(Locale.US);
					setSpeed(speed);
				} catch (Exception e) {
					// TODO: handle exception
				}

			}
		});



		View v = inflater.inflate(R.layout.activity_main, container, false);

		lv_word = (ListView)v. findViewById(R.id.lv_word);
		iv_menuitem = (ImageView)v. findViewById(R.id.iv_menuitem);
		img_right = (ImageView)v.findViewById(R.id.img_right);
		txtheader = (TextView) v.findViewById(R.id.txtheader);
		editTextheader = (AutoCompleteTextView) v.findViewById(R.id.editTextheader);
		FrameLayout adViewContainer = (FrameLayout) v.findViewById(R.id.adViewContainer);
//		 MobileAds.initialize(getActivity().getApplicationContext(), "ca-app-pub-8892639331235181~8263247154");
		AdView mAdView = new AdView(getActivity());
		mAdView.setAdUnitId(getString(R.string.banner_ad_unit_id));
		mAdView.setAdSize(getAdSize());

		adViewContainer.removeAllViews();
		adViewContainer.addView(mAdView);

        AdRequest adRequest = new AdRequest.Builder()
//        .addTestDevice("1DDA3DE5902F42DE3931D3E8125FB615")
        .build();
        mAdView.loadAd(adRequest);


		//		new BackAsync().execute();

		 iv_menuitem.setOnClickListener(new View.OnClickListener() {
	            @Override
	            public void onClick(View view) {

	                if (MenuActivity.drawer_layout.isDrawerOpen(MenuActivity.left_drawer)) {
	                    MenuActivity.drawer_layout.closeDrawer(MenuActivity.left_drawer);
	                } else {
	                    MenuActivity.drawer_layout.openDrawer(MenuActivity.left_drawer);
	                }
	            }
	        });

		

		if(isFrom.equalsIgnoreCase(Constant.FROM_HOME)){
			txtheader.setText(getActivity().getResources().getString(R.string.app_name));
			if(list == null || list != null && list.size() <1){
				list = dbHelper.getALLWordFromDB(isFrom);
			}
			if(list != null && list.size() > 0 ){
				dictionaryadapter = new DictionaryAdapter(mContext, list,this);
				lv_word.setAdapter(dictionaryadapter);
			}
		}else if(isFrom.equalsIgnoreCase(Constant.FROM_FAV)){
			txtheader.setText("Favourite");
//			if(favlist == null || favlist != null &&  favlist.size() <1){
				favlist = dbHelper.getALLWordFromDB(isFrom);
//			}
			if(favlist != null && favlist.size() > 0 ){
				dictionaryadapter = new DictionaryAdapter(mContext, favlist,this);
				lv_word.setAdapter(dictionaryadapter);
			}
		}







		editTextheader.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				String text = editTextheader.getText().toString()
						.toLowerCase(Locale.getDefault());
				if(dictionaryadapter != null ){
					dictionaryadapter.filter(text);
				}
			}
		});

		img_right.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if(MainActivity.editTextheader != null ){
					MainActivity.editTextheader.setText("");
				}
				if (isVisible) {
					editTextheader.setVisibility(View.VISIBLE);
					txtheader.setVisibility(View.GONE);
					ScaleAnimation animSlide = new ScaleAnimation(0.0f, 1.0f, 1.0f, 1.0f, Animation.RELATIVE_TO_SELF,1.0f, Animation.RELATIVE_TO_SELF, 0.5f);
					animSlide.setDuration(700);
					//	                     Animation animSlide = AnimationUtils.loadAnimation(mContext.getApplicationContext(),
					//	                             R.anim.sliderighttoleft);
					editTextheader.startAnimation(animSlide);
					animSlide.setAnimationListener(new Animation.AnimationListener() {
						@Override
						public void onAnimationStart(Animation arg0) {
						}

						@Override
						public void onAnimationRepeat(Animation arg0) {
						}

						@Override
						public void onAnimationEnd(Animation arg0) {
							//set your button visibility here

						}


					});


					isVisible = false;
				} else {//if(txtheader.getVisibility() == View.GONE){

					isVisible = true;
					ScaleAnimation animSlide = new ScaleAnimation(1.0f, 0.0f, 1.0f, 1.0f, Animation.RELATIVE_TO_SELF,1.0f, Animation.RELATIVE_TO_SELF, 0.5f);
					animSlide.setDuration(700);
					//	                     Animation animSlide = AnimationUtils.loadAnimation(mContext.getApplicationContext(),
					//	                             R.anim.lefttoright);
					editTextheader.startAnimation(animSlide);

					animSlide.setAnimationListener(new Animation.AnimationListener() {
						@Override
						public void onAnimationStart(Animation arg0) {
						}

						@Override
						public void onAnimationRepeat(Animation arg0) {
						}

						@Override
						public void onAnimationEnd(Animation arg0) {
							//set your button visibility here
							editTextheader.clearAnimation();
							editTextheader.setVisibility(View.GONE);
							txtheader.setVisibility(View.VISIBLE);
						}


					});


				}
			}
		});
		return v;
	}

	private void setSpeed(String speed) {
		if (speed.equals("Very Slow")) {
			convertToSpeech.setSpeechRate(0.1f);
		}
		if (speed.equals("Slow")) {
			convertToSpeech.setSpeechRate(0.7f);
		}
		if (speed.equals("Normal")) {
			convertToSpeech.setSpeechRate(1.0f);// default 1.0
		}
		if (speed.equals("Fast")) {
			convertToSpeech.setSpeechRate(1.5f);
		}
		if (speed.equals("Very Fast")) {
			convertToSpeech.setSpeechRate(2.0f);
		}
		// for setting pitch you may call
		// tts.setPitch(1.0f);//default 1.0
	}
	public class BackAsync extends AsyncTask<Void, Void, Void>{

		@Override
		protected Void doInBackground(Void... paramVarArgs) {
			// TODO Auto-generated method stub
			dictionaryadapter = new DictionaryAdapter(mContext, dbHelper.getALLWordFromDB(isFrom),MainActivity.this);

			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			lv_word.setAdapter(dictionaryadapter);
		}

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
