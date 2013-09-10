package com.etech.hdifam.activities;


import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings.Secure;
import android.util.Log;

import com.android.vending.licensing.AESObfuscator;
import com.android.vending.licensing.LicenseChecker;
import com.android.vending.licensing.LicenseCheckerCallback;
import com.android.vending.licensing.ServerManagedPolicy;
import com.etech.hdifam.R;

public class Splash extends Activity {

	private final String tag = "Splash";
	public static String PREFS_NAME = "hdifam_setting";
	
	// needed for license verification
	private static final String BASE64_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAkGI3QiOX4dae3gCATFUMld5tppnUvgprFdqfWmrxJOuVBfVMd9AEsSALHCHyjDOUvJg2RJpqj2SnTi+cLyIPCVV6bVd5KgfCO+oHW8+JuAy4g9ILyIQ8e9OJ1clt92VjKKgrt8LxYgAHvtk5OY8+fjhh1jcTQrGtbB/7sBYAjSEE1+1FJi9yn4MeT7D9qTjgZXFaAQCooZ2qO9kVAbAZpiijKmA5pZ0k87fD4oe0w+dIIH1ZeIKJ+6ZT71ZZeUG2pEyzqUpASJZZrwj5TOSHBiFkytABgcTZwrqLh0yFK9J5pqf/bcBivloVX4hNqdCWUwQNSmnJrZjdOGlmkQbsKQIDAQAB";
	
	// Generate your own 20 random bytes, and put them here.
    private static final byte[] SALT = new byte[] { -46, 65, 30, -128, -103, -57, 74, -64, 51, 88, -95, -45, 77, -117, -36, -113, -11, 32, -64, 89 };
    
    private LicenseCheckerCallback mLicenseCheckerCallback;
    private LicenseChecker mChecker;
    private Handler mHandler;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);

		mHandler = new Handler();

        // Try to use more data here. ANDROID_ID is a single point of attack.
        String deviceId = Secure.getString(getContentResolver(), Secure.ANDROID_ID);

        Log.d(tag, "onCreate > deviceId: " + deviceId);
        
        // Library calls this when it's done.
        mLicenseCheckerCallback = new EtechLicenseCheckerCallback();
        // Construct the LicenseChecker with a policy.
        mChecker = new LicenseChecker( this, new ServerManagedPolicy(this, new AESObfuscator(SALT, getPackageName(), deviceId)), BASE64_PUBLIC_KEY);
        
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				
				SharedPreferences settings = Splash.this.getSharedPreferences(PREFS_NAME, MODE_WORLD_READABLE|MODE_WORLD_WRITEABLE);
				boolean isLicenseCheck = settings.getBoolean("isLicenseCheck", false);
				
				isLicenseCheck = true; 
						
				if(!isLicenseCheck) {
					handler.sendMessage(handler.obtainMessage(1));
				}
				else {
					Intent intent = new Intent().setClass(Splash.this, SelectDateActivity.class);
					startActivity(intent);
					Splash.this.finish();
					overridePendingTransition(R.anim.fadein, R.anim.fadeout);
				}
			}
		};

		Timer timer = new Timer();
		timer.schedule(task, 3000);
	}
	
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			doCheck();
		}
	};
	
	
	private void doCheck() {
		Log.d(tag, "checking license status.");
        setProgressBarIndeterminateVisibility(true);
        mChecker.checkAccess(mLicenseCheckerCallback);
    }
	
	private class EtechLicenseCheckerCallback implements LicenseCheckerCallback {
        public void allow() {
            if (isFinishing()) {
                // Don't update UI if Activity is finishing.
                return;
            }
            // Should allow user access.
            //displayResult(getString(R.string.allow));
            
            SharedPreferences settings = Splash.this.getSharedPreferences(PREFS_NAME, MODE_WORLD_READABLE|MODE_WORLD_WRITEABLE);
            SharedPreferences.Editor edit = settings.edit();
    		edit.putBoolean("isLicenseCheck", true);
    		edit.commit();
    		
    		Intent intent = new Intent().setClass(Splash.this, SelectDateActivity.class);
			startActivity(intent);
			Splash.this.finish();
			overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        }

        public void dontAllow() {
            if (isFinishing()) {
                // Don't update UI if Activity is finishing.
                return;
            }
            
            //displayResult(getString(R.string.dont_allow));
            
            // Should not allow access. In most cases, the app should assume
            // the user has access unless it encounters this. If it does,
            // the app should inform the user of their unlicensed ways
            // and then either shut down the app or limit the user to a
            // restricted set of features.
            // In this example, we show a dialog that takes the user to Market.
            showDialog(0);
        }

        public void applicationError(ApplicationErrorCode errorCode) {
            if (isFinishing()) {
                // Don't update UI if Activity is finishing.
                return;
            }
            // This is a polite way of saying the developer made a mistake
            // while setting up or calling the license checker library.
            // Please examine the error code and fix the error.
            
            Log.d(tag, "applicationError > errorCode: " + errorCode);
            
            String result = String.format(getString(R.string.application_error), errorCode);
            //displayResult(result);
            
            new AlertDialog.Builder(Splash.this)
            .setTitle(R.string.error_dialog_title)
            .setMessage(result)
            .setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            })
            .create().show();
        }
    }
	
	protected Dialog onCreateDialog(int id) {
        // We have only one dialog.
        return new AlertDialog.Builder(this)
            .setTitle(R.string.unlicensed_dialog_title)
            .setMessage(R.string.unlicensed_dialog_body)
            .setPositiveButton(R.string.buy_button, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent marketIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://market.android.com/details?id=" + getPackageName()));
                    startActivity(marketIntent);
                }
            })
            .setNegativeButton(R.string.quit_button, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            })
            .create();
    }
	
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mChecker.onDestroy();
    }
}


