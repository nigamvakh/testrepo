package com.etech.hdifam;

import java.io.IOException;

import com.etech.hdifam.db.DBAdapter;

import android.app.Application;
import android.content.Context;
import android.util.Log;

public class HDIFAMApp extends Application {
	private static String TAG = "HDIFAMApp";
	private static Context applicationContext = null;
	
	public static DBAdapter dbAdapter = null;
	
	public HDIFAMApp() {
		applicationContext = this;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		Log.i(TAG, "HDIFAMApp initializing...");
		
		try {
			dbAdapter = DBAdapter.getDBAdapterInstance(this);
			dbAdapter.createDataBase();
		} catch (IOException e) {
			Log.e(TAG, "dbAdapter Exception : "+e.getMessage());
		}
		catch (Exception e) {
			Log.e(TAG, "Exception : " + e);
		}
	}
	
	/***
	 * This method will return the context of the app.
	 * 
	 * @return
	 */
	public static Context getAppContext() {
		return applicationContext;
	}
}
