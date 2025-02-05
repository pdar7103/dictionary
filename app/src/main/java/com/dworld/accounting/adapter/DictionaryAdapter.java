package com.dworld.accounting.adapter;

import java.util.ArrayList;
import java.util.Locale;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dworld.accounting.DetailActivity;
import com.dworld.accounting.R;
import com.dworld.accounting.database.DatabaseHelper;
import com.dworld.accounting.interfaces.ClickOnSpeakButton;
import com.dworld.accounting.model.DictionaryModel;


@SuppressLint("NewApi") public class DictionaryAdapter extends BaseAdapter {
	Context mContext ;
	ArrayList<DictionaryModel> list,copyList;
	private LayoutInflater layoutInfalater;
	ClickOnSpeakButton clickOnSpeakButton;
	DatabaseHelper dHelper ;

	public DictionaryAdapter(Context mContext ,ArrayList<DictionaryModel> list,ClickOnSpeakButton clickOnSpeakButton) {
		this.mContext = mContext;
		this.list = list;
		dHelper = DatabaseHelper.getInstance(mContext);
		this.clickOnSpeakButton= clickOnSpeakButton; 
		copyList = new ArrayList<DictionaryModel>();
		copyList.addAll(list);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int paramInt) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int paramInt) {
		// TODO Auto-generated method stub
		return 0;
	}


	@SuppressLint("NewApi") @Override
	public View getView(final int paramInt, View paramView, ViewGroup paramViewGroup) {

		layoutInfalater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		paramView = layoutInfalater.inflate(R.layout.rawword, null);

		final TextView   txtengword = (TextView) paramView.findViewById(R.id.txtengword);
		final TextView   txtgujword = (TextView) paramView.findViewById(R.id.txtgujword);
		final TextView  txtwordtype  = (TextView) paramView.findViewById(R.id.txtwordtype);
		final TextView txtsynonym= (TextView) paramView.findViewById(R.id.txtsynonym);
		final TextView txttiteltype = (TextView) paramView.findViewById(R.id.txttiteltype); 

		ImageView img_speaker = (ImageView) paramView.findViewById(R.id.img_speaker);
		ImageView img_share = (ImageView) paramView.findViewById(R.id.img_share);
		ImageView img_fav = (ImageView) paramView.findViewById(R.id.img_fav);


		txtsynonym.setVisibility(View.GONE);
		txttiteltype.setVisibility(View.GONE);


		txtengword.setText(list.get(paramInt).getEngword());
		//txttiteltype.setText("("+list.get(paramInt).getType()+")");
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
			txtgujword.setText(Html.fromHtml("<html><body>"+list.get(paramInt).getMeaning()+"</body></html>",Html.FROM_HTML_MODE_LEGACY));
			txtsynonym.setText(Html.fromHtml("<html><body><b>Synonym :</b>"+list.get(paramInt).getAsynconamus()+"</body></html>",Html.FROM_HTML_MODE_LEGACY));
		} else {
			txtgujword.setText(Html.fromHtml("<html><body>"+list.get(paramInt).getMeaning()+"</body></html>"));
			txtsynonym.setText(Html.fromHtml("<html><body><b>Synonym :</b>"+list.get(paramInt).getAsynconamus()+"</body></html>"));

		}
		//		txtgujword.setText(Html.fromHtml("<html><body><b>Description :</b>"+list.get(paramInt).getMeaning()+"</body></html>"));
		//		txtwordtype.setText(list.get(paramInt).getType());
		//		txtsynonym.setText(Html.fromHtml("<html><body><b>Synonym :</b>"+list.get(paramInt).getAsynconamus()+"</body></html>"));

		if(list.get(paramInt).getFavouriteword() != null  && list.get(paramInt).getFavouriteword().equalsIgnoreCase("1")){
			img_fav.setImageResource(R.drawable.favselected);
		}else{
			img_fav.setImageResource(R.drawable.heart);
		}

		img_speaker.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				clickOnSpeakButton.onClickOnSpeakButton(txtengword.getText()
						.toString());

			}
		});
		img_share.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				PackageManager pm = mContext.getPackageManager();
				PackageInfo pInfo = null;
				String packageName="";
				try {
					pInfo =  pm.getPackageInfo(mContext.getPackageName(),0);
					packageName =mContext. getApplicationContext().getPackageName();
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
				mContext.startActivity(sendIntent);	

			}
		});
		img_fav.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(list.get(paramInt).getFavouriteword() != null  && list.get(paramInt).getFavouriteword().equalsIgnoreCase("1")){
					ContentValues cv = new ContentValues();
					cv.put(DatabaseHelper.KEY_FAVWORD, 0);
					dHelper.Updatetbl(DatabaseHelper.TBL_NAME_WORD, cv,list.get(paramInt).getId());
					list.get(paramInt).setFavouriteword("0");
				}else{
					ContentValues cv = new ContentValues();
					cv.put(DatabaseHelper.KEY_FAVWORD, 1);
					dHelper.Updatetbl(DatabaseHelper.TBL_NAME_WORD, cv,list.get(paramInt).getId());
					list.get(paramInt).setFavouriteword("1");

				}
				notifyDataSetChanged();
			}
		});

		paramView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(mContext,DetailActivity.class);
				i.putExtra("pos", paramInt);
				mContext.startActivity(i);
			}
		});
		return paramView;
	}

	public void filter(String charText) {
		charText = charText.toLowerCase(Locale.getDefault());
		list.clear();
		if (charText.length() == 0) {
			list.addAll(copyList);
		} else {

			for (DictionaryModel vo : copyList) {

				if (vo.getEngword().toLowerCase(Locale.getDefault())
						.startsWith(charText)) {
					// Log.v("matcher",vo.getSkillName());
					list.add(vo);
				}
			}
		}
		this.notifyDataSetChanged();
	}

}
