package com.etech.hdifam.activities;

import com.etech.hdifam.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

public class PredictionLoadingActivity extends Activity {
	
	private String tag = "PredictionLoadingActivity";
	
	private final int TAG_CHANGEIMG = 101;
	private final int TAG_FADEEFFECT = 102;
	
	private final int[] arrImg = new int[] { R.drawable.dimond1, R.drawable.dimond2, R.drawable.dimond3, R.drawable.dimond4, R.drawable.dimond5 };
	private int curImgIndex = 0;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		setContentView(R.layout.predictloading);
	
		Thread logoTimer = new Thread() {
			public void run() {
				try {
					int logoTimer = 0;
					while (logoTimer < 4001) {
						sleep(1000);
						logoTimer = logoTimer + 1000;
						handler.sendMessage(handler.obtainMessage(TAG_CHANGEIMG));
					}

					handler.sendMessage(handler.obtainMessage(TAG_FADEEFFECT));
				} catch (Exception e) {
					Log.e(tag, "Exception : " + e);
				}
			}
		};
		
		logoTimer.start();
	}
	
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == TAG_CHANGEIMG) {
				((ImageView) findViewById(R.id.imgView)).setImageResource(arrImg[curImgIndex]);
				curImgIndex++;
			}
			else if (msg.what == TAG_FADEEFFECT) {
				Intent intent = new Intent(PredictionLoadingActivity.this, PredictResultActivity.class);
				intent.putExtras(getIntent());
	    		startActivity(intent);
	    		//finish();
			}
		}
	};
}
