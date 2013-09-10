package com.etech.hdifam.activities;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.etech.hdifam.Constant;
import com.etech.hdifam.HDIFAMApp;
import com.etech.hdifam.R;
import com.flurry.android.FlurryAgent;

public class SelectDateActivity extends Activity {
	private final String tag = "SelectDateActivity";
	
	public String PREFS_NAME = "carbook_setting";
	
	private String[] arrMonthName = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
	
	private final int TAG_BTNHERDATE = 101;
	private final int TAG_BTNHISDATE = 102;
	private int curDateSelection = -1;

	
	private int selDay = 1, selMonth = 0, selYear = 1985;
	private int selHerDay = 1, selHerMonth = 0, selHerYear = 1985;
	private int selHisDay = 1, selHisMonth = 0, selHisYear = 1985;
	
	private int herDateId = -1;
	private int hisDateId = -1;
	
	private Button btnHerDateDay, btnHerDateMonth, btnHerDateYear;
	private Button btnHisDateDay, btnHisDateMonth, btnHisDateYear;
	
	private TextView txtExeMatch = null;
	private TextView txtGoodMatch = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.selectdateactivity);
        
        txtExeMatch = ((TextView) findViewById(R.id.txtExeMatch));
		txtGoodMatch = ((TextView) findViewById(R.id.txtGoodMatch));
		
        btnHerDateDay = (Button) findViewById(R.id.btnHerDay); 
		btnHerDateMonth = (Button) findViewById(R.id.btnHerMonth);
		btnHerDateYear = (Button) findViewById(R.id.btnHerYear);
        
		btnHisDateDay = (Button) findViewById(R.id.btnHisDay); 
		btnHisDateMonth = (Button) findViewById(R.id.btnHisMonth);
		btnHisDateYear = (Button) findViewById(R.id.btnHisYear);
		
		if( savedInstanceState != null ) {
            Toast.makeText(this, savedInstanceState .getString("message"), Toast.LENGTH_LONG).show();
            
            savedInstanceState.putString("message", "savving previous activity state...");
            
            savedInstanceState.putString("txtExeMatch", txtExeMatch.getText().toString());
            savedInstanceState.putString("txtGoodMatch", txtGoodMatch.getText().toString());
            
            savedInstanceState.putString("btnHerDateDay", btnHerDateDay.getText().toString());
            savedInstanceState.putString("btnHerDateMonth", btnHerDateMonth.getText().toString());
            savedInstanceState.putString("btnHerDateYear", btnHerDateYear.getText().toString());
            
            savedInstanceState.putString("btnHisDateYear", btnHisDateDay.getText().toString());
            savedInstanceState.putString("btnHisDateYear", btnHisDateYear.getText().toString());
            savedInstanceState.putString("btnHisDateYear", btnHisDateYear.getText().toString());
         }
    }
    
    protected void onStart() {
		super.onStart();
		FlurryAgent.onStartSession(this, Constant.flurry_Appid);
	}
    
    protected void onStop() {
		super.onStop();
		FlurryAgent.onEndSession(this);
	}
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
       super.onSaveInstanceState(outState);
       
       outState.putString("message", "savving previous activity state...");
       
       outState.putString("txtExeMatch", txtExeMatch.getText().toString());
       outState.putString("txtGoodMatch", txtGoodMatch.getText().toString());
       
       outState.putString("btnHerDateDay", btnHerDateDay.getText().toString());
       outState.putString("btnHerDateMonth", btnHerDateMonth.getText().toString());
       outState.putString("btnHerDateYear", btnHerDateYear.getText().toString());
       
       outState.putString("btnHisDateYear", btnHisDateDay.getText().toString());
       outState.putString("btnHisDateYear", btnHisDateYear.getText().toString());
       outState.putString("btnHisDateYear", btnHisDateYear.getText().toString());
    }
    
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
    	super.onRestoreInstanceState(savedInstanceState);
    	
    	if( savedInstanceState != null ) {
    		Toast.makeText(this, savedInstanceState.getString("message"), Toast.LENGTH_LONG).show();
    		
    		txtExeMatch.setText(savedInstanceState.getString("txtExeMatch"));
    		txtGoodMatch.setText(savedInstanceState.getString("txtGoodMatch"));
    	       
    		btnHerDateDay.setText(savedInstanceState.getString("btnHerDateDay"));
    		btnHerDateMonth.setText(savedInstanceState.getString("btnHerDateMonth"));
    		btnHerDateYear.setText(savedInstanceState.getString("btnHerDateYear"));
    	       
    		btnHisDateDay.setText(savedInstanceState.getString("btnHisDateYear"));
    		btnHisDateYear.setText(savedInstanceState.getString("btnHisDateYear"));
    		btnHisDateYear.setText(savedInstanceState.getString("btnHisDateYear"));
         }
    }
    
    public void btnPredicateClicked(View v) {
    	if(herDateId > -1 && hisDateId > -1) {
    		
    		
    		Animation fadeIn = new AlphaAnimation(0, 1);
    		fadeIn.setInterpolator(new DecelerateInterpolator()); //add this
    		fadeIn.setDuration(1000);
    		
    		Animation fadeOut = new AlphaAnimation(1, 0);
    		fadeOut.setInterpolator(new AccelerateInterpolator()); //and this
    		fadeOut.setStartOffset(1000);
    		fadeOut.setDuration(1000);

    		AnimationSet animation = new AnimationSet(false); //change to false
    		animation.addAnimation(fadeIn);
    		animation.addAnimation(fadeOut);
    		
    		//this.setAnimation(animation);
    		
    		SharedPreferences settings = this.getSharedPreferences(PREFS_NAME, MODE_WORLD_READABLE|MODE_WORLD_WRITEABLE);
    		SharedPreferences.Editor edit = settings.edit();
    		
    		edit.putBoolean("shouldanimate", true);
    		edit.putInt("HerDateId", herDateId);
    		edit.putInt("HisDateId", hisDateId);
    		edit.commit();
    		
    		
    		//Intent intent = new Intent(this, PredictActivity.class);
    		Intent intent = new Intent(this, PredictionLoadingActivity.class);
    		intent.putExtra("HerDateId", herDateId);
    		intent.putExtra("HisDateId", hisDateId);
    		
    		startActivity(intent);
    		//overridePendingTransition(android.R.anim.fade_out, android.R.anim.fade_in);
    	}
    	else {
    		AlertDialog alertDialog = new AlertDialog.Builder(this).create();
    		alertDialog.setTitle(getResources().getString(R.string.app_name));
    		alertDialog.setMessage("Select your and his birthdate.");
    		
    		alertDialog.setButton2("Ok", new OnClickListener() {
			
				public void onClick(DialogInterface dialog, int which) {
					
				}
			});
    		
    		alertDialog.show();
    	}
    }
    
    public void btnSelectDateClicked(View v) {
    	if(v.getId() == R.id.btnHerMonth || v.getId() == R.id.btnHerDay || v.getId() == R.id.btnHerYear) {
    		curDateSelection = TAG_BTNHERDATE;
    		selDay = selHerDay;
    		selMonth = selHerMonth;
    		selYear = selHerYear;
    	}
    	else {
    		curDateSelection = TAG_BTNHISDATE;
    		selDay = selHisDay;
    		selMonth = selHisMonth;
    		selYear = selHisYear;
    	}
    	
    	Dialog dialog = onCreateDialog(1);
    	dialog.show();
    }
    
 	@Override
 	protected Dialog onCreateDialog(int id) 
 	{
 		DatePickerDialog dDialog = new DatePickerDialog(this, mDateSetListener, selYear, selMonth, selDay);
		return dDialog;
 	}
 	
 	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			Log.d(tag, "year: " + year + ", monthOfYear: " + monthOfYear + ", dayOfMonth: " + dayOfMonth);
			
			String date = "";
			
			if((monthOfYear  + 1)< 10)
				date = "2000" + "0" + (monthOfYear  + 1);
			else
				date = "2000" + (monthOfYear  + 1);
			
			if(dayOfMonth < 10)
				date = date + "0" + dayOfMonth;
			else
				date = date + dayOfMonth;
			
			Log.d(tag, "date: " + date);
			
			if(curDateSelection == TAG_BTNHERDATE) {
				selHerDay = dayOfMonth;
				selHerMonth = monthOfYear;
				selHerYear = year;
				
				if(dayOfMonth < 10)
					btnHerDateDay.setText("0" + String.valueOf(dayOfMonth));
				else
					btnHerDateDay.setText(String.valueOf(dayOfMonth));
				
				btnHerDateMonth.setText(arrMonthName[monthOfYear]);
				btnHerDateYear.setText(String.valueOf(year));
				
				btnHerDateDay.setTextColor(Color.WHITE);
				btnHerDateMonth.setTextColor(Color.WHITE);
				btnHerDateYear.setTextColor(Color.WHITE);
				selectMatches(date);
			}
			else {
				selHisDay = dayOfMonth;
				selHisMonth = monthOfYear;
				selHisYear = year;
				
				if(dayOfMonth < 10)
					btnHisDateDay.setText("0" + String.valueOf(dayOfMonth));
				else
					btnHisDateDay.setText(String.valueOf(dayOfMonth));
				
				btnHisDateMonth.setText(arrMonthName[monthOfYear]);
				btnHisDateYear.setText(String.valueOf(year));
				
				btnHisDateDay.setTextColor(Color.WHITE);
				btnHisDateMonth.setTextColor(Color.WHITE);
				btnHisDateYear.setTextColor(Color.WHITE);
				selectPartnerDateId(date);
			}
		}
 	};
 	
 	private void selectMatches(String date) {
 		try {
 			HDIFAMApp.dbAdapter.openDataBase();
			
			String query = String.format(Constant.query_select_her_date, date, date);
			Log.e(tag, "query: " + query);
			
			ArrayList<ArrayList<String>> stringList = HDIFAMApp.dbAdapter.selectRecordsFromDBList(query, null);
			
			Log.e(tag, "stringList.size : "+ stringList.size());
			
			HDIFAMApp.dbAdapter.close();
			
			ArrayList<String> list = stringList.get(0);
			try {
				herDateId = Integer.parseInt(list.get(0));
				
				String exeMatch = list.get(3);
				String goodMatch = list.get(5);
				
				if(exeMatch.length() < 2)
					exeMatch = "0" + exeMatch;
				
				if(goodMatch.length() < 2)
					goodMatch = "0" + goodMatch;
				
				txtExeMatch.setText(exeMatch);
				txtGoodMatch.setText(goodMatch);
			}
			catch (Exception e) {
				Log.e(tag, "" + e.getMessage());
			}
		}
		catch (Exception e) {
			Log.e(tag, "Exception : " + e);
		}
 	}
 	
 	private void selectPartnerDateId(String date) {
 		try {
 			HDIFAMApp.dbAdapter.openDataBase();
			
			String query = String.format(Constant.query_select_his_date, date, date);
			Log.e(tag, "query: " + query);
			
			ArrayList<ArrayList<String>> stringList = HDIFAMApp.dbAdapter.selectRecordsFromDBList(query, null);
			
			Log.e(tag, "stringList.size : "+ stringList.size());
			
			HDIFAMApp.dbAdapter.close();
			
			ArrayList<String> list = stringList.get(0);
			try {
				hisDateId = Integer.parseInt(list.get(0));
			}
			catch (Exception e) {
				Log.e(tag, "" + e.getMessage());
			}
		}
		catch (Exception e) {
			Log.e(tag, "Exception : " + e);
		}
 	}
}