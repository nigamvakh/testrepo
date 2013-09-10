package com.etech.hdifam.activities;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.etech.hdifam.Constant;
import com.etech.hdifam.HDIFAMApp;
import com.etech.hdifam.R;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.flurry.android.FlurryAgent;

public class PredictActivity extends Activity {
	private final String tag = "PredictActivity";

	public String PREFS_NAME = "carbook_setting";
	
	private final int TAG_CHANGEIMG = 101;
	private final int TAG_FADEEFFECT = 102;

	private final int[] arrImg = new int[] { R.drawable.dimond1, R.drawable.dimond2, R.drawable.dimond3, R.drawable.dimond4, R.drawable.dimond5 };
	private int curImgIndex = 0;

	private TextView txtPredictionResult = null;
	private TextView txtPredictionpercentage = null;

	private ImageView star;
	
	String starpercentage;
	String sharemessage;
    String shareemail;
	String result;
	String sharefacebook;

	Facebook facebook;
	String[] permissions = { "offline_access", "publish_stream" };
	SharedPreferences facebookprefernce;
	ProgressDialog pd;
	ConnectionDetector cd;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.predictactivity);

		//Bundle b = getIntent().getExtras();

		//int herDateId = b.getInt("HerDateId");
		//int hisDateId = b.getInt("HisDateId");

		SharedPreferences settings = this.getSharedPreferences(PREFS_NAME, MODE_WORLD_READABLE|MODE_WORLD_WRITEABLE);
		
		Boolean sholudAnimate = settings.getBoolean("shouldanimate", true);
		
		int herDateId = settings.getInt("HerDateId", 0);
		int hisDateId = settings.getInt("HisDateId", 0);
		
		Log.e(tag, "herDateId: " + herDateId + ", hisDateId: " + hisDateId);
		
		if(sholudAnimate) {
			SharedPreferences.Editor edit = settings.edit();
    		edit.putBoolean("shouldanimate", false);
			
			((LinearLayout) findViewById(R.id.linPredictingLayout)).setVisibility(View.VISIBLE);
			((LinearLayout) findViewById(R.id.linLayout)).setVisibility(View.INVISIBLE);
			((ImageView) findViewById(R.id.info)).setVisibility(View.INVISIBLE);
		}
		
        cd = new ConnectionDetector(this);
		facebook = new Facebook(Constant.App_Id);
		restore(facebook);
		facebookprefernce = this.getSharedPreferences("Facebook", Context.MODE_PRIVATE);

		txtPredictionResult = (TextView) findViewById(R.id.txtPredictionResult);
		txtPredictionpercentage = (TextView) findViewById(R.id.txtPredictionPercentage);
		star = (ImageView) findViewById(R.id.star);

		try {
			HDIFAMApp.dbAdapter.openDataBase();

			String query = String.format(Constant.query_select_result, herDateId, hisDateId);
			Log.d(tag, "query: " + query);

			ArrayList<ArrayList<String>> stringList = HDIFAMApp.dbAdapter.selectRecordsFromDBList(query, null);

			Log.e(tag, "stringList.toString : " + stringList.toString());
			
			Log.e(tag, "stringList.size : " + stringList.size());

			HDIFAMApp.dbAdapter.close();

			ArrayList<String> list = stringList.get(0);
			try {
				Log.d(tag, list.get(3));
				Log.d(tag, list.get(4));
				starpercentage = list.get(4);

				txtPredictionResult.setText("" + list.get(3).replace("\\", ""));
				txtPredictionpercentage.setText("" + list.get(4) + "%");

			} catch (Exception e) {
				Log.e(tag, "" + e.getMessage());
			}
		} catch (Exception e) {
			Log.e(tag, "Exception : " + e);
		}

		///*
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
					Log.e("SplashScreen", "Exception : " + e);
				}
			}
		};
		
		logoTimer.start();
		/**/
		int matchPercentage = Integer.parseInt(starpercentage);
		if (matchPercentage <= 60) {
			star.setImageResource(R.drawable.star1);
		} else if (matchPercentage > 60 && matchPercentage <= 70) {
			star.setImageResource(R.drawable.star2);
		} else if (matchPercentage > 70 && matchPercentage <= 80) {
			star.setImageResource(R.drawable.star3);
		} else if (matchPercentage > 80 && matchPercentage <= 90) {
			star.setImageResource(R.drawable.star4);
		} else {
			star.setImageResource(R.drawable.star5);
		}

		result = txtPredictionResult.getText().toString().replace("\\", "");

		sharemessage = "How Do I Find A Man?\n" + starpercentage + "% match with this guy!\n" + result + "\nYou should try it - http://how-do-i-find-a-man.net";
		shareemail = "How Do I Find A Man?<br />" + starpercentage + "% match with this guy!<br />" + result + "<br />You should try it - <a href=\"http://www.how-do-i-find-a-man.net\">How Do I Find A Man</a>";
		sharefacebook = "How Do I Find A Man?\n" + "I've found a " + starpercentage + "% match with a guy.\n" + result + "\nYou should give it a try - http://how-do-i-find-a-man.net/";
	}

	@Override
	protected void onStart() {
		super.onStart();
		FlurryAgent.onStartSession(this, Constant.flurry_Appid);
	}

	public void btnShareClicked(View v) {
		Log.d(tag, "btnShareClicked...");

		Intent intent = new Intent(this, ShareDialog.class);
		startActivityForResult(intent, 1);
	}

	public void btnGoagainClicked(View v) {
		finish();
	}
	
	public boolean restore(Facebook facebook){
    	SharedPreferences savedSession =
           this.getSharedPreferences("Facebook", Context.MODE_PRIVATE);
        facebook.setAccessToken(savedSession.getString("accesstoken", null));
        facebook.setAccessExpires(savedSession.getLong("accessexpire", 0));
        return facebook.isSessionValid();
    	
    }

	public void popup(View v) {
		AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.Theme_CustomDialog));
	
		builder.setCancelable(true);
		AlertDialog alert = builder.create();
		
		LayoutInflater inflater = this.getLayoutInflater();
		alert.setView(inflater.inflate(R.layout.popup, null),0,0,0,0);
		alert.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
		alert.show();
	}

	@Override
	protected void onStop() {
		super.onStop();
		FlurryAgent.onEndSession(this);
	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == TAG_CHANGEIMG) {
				((ImageView) findViewById(R.id.imgView)).setImageResource(arrImg[curImgIndex]);
				curImgIndex++;
			}
			else if (msg.what == TAG_FADEEFFECT) {
				fadeOutAnimation();
			}
		}
	};

	private void fadeOutAnimation() {
		Animation fadeoutAnim = AnimationUtils.loadAnimation(this, R.anim.fadeout);

		((LinearLayout) findViewById(R.id.linPredictingLayout)).startAnimation(fadeoutAnim);
		fadeoutAnim.setAnimationListener(new AnimationListener() {

			public void onAnimationStart(Animation animation) {}

			public void onAnimationRepeat(Animation animation) {}

			public void onAnimationEnd(Animation animation) {
				Animation fadeinAnim =AnimationUtils.loadAnimation(PredictActivity.this, R.anim.predictfadein);
				((LinearLayout) findViewById(R.id.linPredictingLayout)).setVisibility(View.INVISIBLE);
				((LinearLayout) findViewById(R.id.linLayout)).setVisibility(View.VISIBLE);
				((ImageView) findViewById(R.id.info)).setVisibility(View.VISIBLE);
				((LinearLayout)findViewById(R.id.linLayout)).startAnimation(fadeinAnim);
			}
		});
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			String option = data.getStringExtra("SelectedOption");
			
			if (option.equalsIgnoreCase("SMS")) {
				Intent sendIntent = new Intent(Intent.ACTION_VIEW);
				sendIntent.setType("vnd.android-dir/mms-sms");
				sendIntent.putExtra("sms_body", sharemessage);
				startActivity(sendIntent);
			}
			else if(option.equalsIgnoreCase("Mail")){
				Intent in = new Intent(Intent.ACTION_SEND);
				in.setType("message/rfc822");
				in.putExtra(Intent.EXTRA_SUBJECT,"My compatibility with this guy!");
				in.putExtra(Intent.EXTRA_TEXT,Html.fromHtml(shareemail));
				startActivity(Intent.createChooser(in, "Send your email in:"));
			}
			else if(option.equalsIgnoreCase("Facebook")) {
				if(!cd.isConnectingToInternet()) {
					Toast.makeText(getBaseContext(),"Please check your Internet Connnection",Toast.LENGTH_SHORT).show();
				}
				else{
					if(!facebook.isSessionValid()){
						facebook.authorize(this, permissions, Facebook.FORCE_DIALOG_AUTH, new LoginDialogListener());
					}
					else{
						wallpost();
					}
				}
			}
		}
	}

	private final class LoginDialogListener implements com.facebook.android.Facebook.DialogListener {
		public void onComplete(Bundle values) {
			SharedPreferences.Editor editor = facebookprefernce.edit();
			editor.putString("accesstoken", facebook.getAccessToken());
			editor.putLong("accessexpire", facebook.getAccessExpires());
			editor.commit();
			wallpost();
		}

		public void onCancel() {
			Toast.makeText(getBaseContext(), "Log In Cancelled", Toast.LENGTH_SHORT).show();
		}

		public void onFacebookError(com.facebook.android.FacebookError e) {
			Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
		}

		public void onError(com.facebook.android.DialogError e) {
			Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
		}
	}

	public void wallpost() {
		pd = ProgressDialog.show(this, null, "Please wait while Posting", true, true);
		Bundle params = new Bundle();
		params.putString("message", sharefacebook);
		AsyncFacebookRunner asyncRunner = new AsyncFacebookRunner(facebook);
		
		asyncRunner.request("me/feed", params, "POST", new PostListener(), null);
	}

	public class PostListener implements RequestListener {
		public void onComplete(final String response, final Object state) {
			pd.dismiss();

			runOnUiThread(new Runnable() {
				public void run() {
					Toast.makeText(PredictActivity.this,"Message posted on wall successfully",Toast.LENGTH_SHORT).show();
				}
			});
		}

		public void onIOException(IOException e, Object state) {
			Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
		}

		public void onFileNotFoundException(FileNotFoundException e,Object state) {
			Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
		}

		public void onMalformedURLException(MalformedURLException e,Object state) {
			Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
		}

		public void onFacebookError(FacebookError e, Object state) {
			Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
		}
	}
}
