package com.etech.hdifam.activities;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.etech.hdifam.R;
import com.etech.hdifam.activities.PredictActivity.PostListener;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.AsyncFacebookRunner.RequestListener;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.URLSpan;
import android.view.View;
import android.view.Window;

public class ShareDialog<View> extends Activity {

	int[] shareimage = new int[] { R.drawable.ic_message, R.drawable.ic_mail, R.drawable.ic_facebook };
	String[] sharetext = new String[] { "SMS", "Mail", "Facebook" };
	String[] permissions = { "offline_access", "publish_stream" };
	
	SharedPreferences facebookprefernce;
	ProgressDialog pd;
	String url = "http://how-do-i-find-a-man.net";
	
	SpannableStringBuilder builder;
	String percenteage;
	String result;
	String sharemessage;
	String shareemail;
	String sharefacebook;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.sharedialog);
		facebookprefernce = this.getSharedPreferences("Facebook",Context.MODE_PRIVATE);
		
		ListView list = (ListView) findViewById(R.id.listView1);
		List<HashMap<String, String>> aList = new ArrayList<HashMap<String, String>>();
		View header = (View) getLayoutInflater().inflate(R.layout.listheader, null);
		list.addHeaderView((android.view.View) header);

		sharemessage = "How Do I Find A Man?\n" + percenteage
				+ " match with this guy!\n" + "'" + result + "'"
				+ "\nYou should try it"; //http://how-do-i-find-a-man.net"; 

		shareemail="How Do I Find A Man?<br />" + percenteage
				+ " match with this guy!<br />" + result  
				+ "<br />You should try it - <a href='http://www.how-do-i-find-a-man.net'>How Do I Find A Man</a>";

		sharefacebook ="How Do I Find A Man?\n"
				+ "I’ve found a "
				+ percenteage
				+ " match with a guy.\n"
				+ "'"
				+ result
				+ "'"
				+ "\nYou should give it a try - http://how-do-i-find-a-man.net/";
		
		for (int i = 0; i < 3; i++) {
			HashMap<String, String> hm = new HashMap<String, String>();
			hm.put("sharetext", sharetext[i]);
			hm.put("shareimage", Integer.toString(shareimage[i]));
			aList.add(hm);
		}
		
		String from[] = { "shareimage", "sharetext" };
		int[] to = new int[] { R.id.shareimage, R.id.share };
		
		SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), aList, R.layout.listitem, from, to);
		list.setAdapter(adapter);

		OnItemClickListener itemClickListener = new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0,android.view.View arg1, int position, long arg3) {
				// TODO Auto-generated method stub
				if (position == 1) {
					Intent returnIntent = new Intent();
					returnIntent.putExtra("SelectedOption","SMS");
					setResult(RESULT_OK,returnIntent);        
					finish();
				} else if (position == 2) {
					Intent returnIntent = new Intent();
					returnIntent.putExtra("SelectedOption","Mail");
					setResult(RESULT_OK,returnIntent); 
					finish();
				} else if (position == 3) {

					Intent returnIntent = new Intent();
					returnIntent.putExtra("SelectedOption","Facebook");
					setResult(RESULT_OK,returnIntent); 
					finish();
				}

			}
		};

		// Setting the item click listener for the listview
		list.setOnItemClickListener(itemClickListener);
	}
}


