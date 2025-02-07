package com.dworld.accounting.customeview;



import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.dworld.architecture.R;


public class JustifiedTextView extends WebView {
	private String core = "<html><head><style type=\"text/css\">@font-face {font-family: MyFont;src:url(\"file:///android_asset/fonts/LatoLight.ttf\")}body {font-family: MyFont;font-size: medium;text-align: justify;}</style></head><body font-family: 'myface', serif; style='text-align:justify;color:rgba(%s);font-size:%dpx;margin: 0px 0px 0px 0px; line-height:25px;'>%s</body></html>";
	private String text;
	private int textColor;
	private int backgroundColor;
	private int textSize;

	public JustifiedTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs);
	}

	public JustifiedTextView(Context context, AttributeSet attrs, int i) {
		super(context, attrs, i);
		init(attrs);
	}

	@SuppressLint("NewApi")
	public JustifiedTextView(Context context, AttributeSet attrs, int i,
			boolean b) {
		super(context, attrs, i, b);
		init(attrs);
	}

	private void init(AttributeSet attrs) {
		TypedArray a = getContext().obtainStyledAttributes(attrs,
				R.styleable.JustifiedTextView);

		text = a.getString(R.styleable.JustifiedTextView_Jtext);
		if (text == null)
			text = "";
		textColor = a.getColor(R.styleable.JustifiedTextView_JtextColor,
				Color.BLACK);
		backgroundColor = a.getColor(
				R.styleable.JustifiedTextView_JbackgroundColor,
				Color.TRANSPARENT);
		textSize = a.getInt(R.styleable.JustifiedTextView_JtextSize, (int) (getResources().getDimension(R.dimen.mtext)/ getResources().getDisplayMetrics().density));

		a.recycle();

		this.setWebChromeClient(new WebChromeClient() {
		});
//		this.setWillNotCacheDrawing(false);
//		this.setWebViewClient(new WebViewClient());
		reloadData();
	}

	public void setText(String s) {
		if (s == null)
			this.text = "";
		else
			this.text = s;
		reloadData();
	}

	@SuppressLint("NewApi")
	private void reloadData() {
		this.getSettings().setLoadWithOverviewMode(true);
        this.getSettings().setJavaScriptEnabled(true);
		if (text != null) {
			String data = String
					.format(core, toRgba(textColor), textSize, text);
			Log.d("test", data);
			this.loadDataWithBaseURL(null, data, "text/html", "utf-8", null);
		}

		// set WebView's background color *after* data was loaded.
		super.setBackgroundColor(backgroundColor);

		// Hardware rendering breaks background color to work as expected.
		// Need to use software renderer in that case.
		if (android.os.Build.VERSION.SDK_INT >= 11)
			this.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
	}

	public void setTextColor(int hex) {
		textColor = hex;
		reloadData();
	}

	public void setBackgroundColor(int hex) {
		backgroundColor = hex;
		reloadData();
	}

	public void setTextSize(int textSize) {
		this.textSize = textSize;
		reloadData();
	}

	private String toRgba(int hex) {
		String h = Integer.toHexString(hex);
		int a = Integer.parseInt(h.substring(0, 2), 16);
		int r = Integer.parseInt(h.substring(2, 4), 16);
		int g = Integer.parseInt(h.substring(4, 6), 16);
		int b = Integer.parseInt(h.substring(6, 8), 16);
		return String.format("%d,%d,%d,%d", r, g, b, a);
	}
}