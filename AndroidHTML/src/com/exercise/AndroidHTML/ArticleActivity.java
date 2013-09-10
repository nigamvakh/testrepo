package com.exercise.AndroidHTML;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;

public class ArticleActivity extends Activity implements AsyncTaskCompleteListener<String>  {

	int selPosition;

	WebView webArticle;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);

		try {
			//FileInputStream inp = new FileInputStream( getAssets().open("test")));

			BufferedReader inp = new BufferedReader(new InputStreamReader(getAssets().open("test"), "US-ASCII"));
			int readChar;
			StringBuffer sb = new StringBuffer();
			while ((readChar=inp.read()) != -1) {
				sb.append((char)readChar);
				//Log.e("", "" + sb.toString());
			}

			Log.e("", "" + sb.toString());

		} catch (Exception e) {
			Log.e("", "" + e);
		}

		webArticle = (WebView)findViewById(R.id.mybrowser);
		webArticle.setBackgroundColor(Color.TRANSPARENT);

		String theDetailText = "http://www.arborgate.com/ann_picks.php?id=225";

		ETechAsyncTask task=new ETechAsyncTask(this,this,"pickDetail",null);
		task.execute(theDetailText);

		//DownloadFile dTask = new DownloadFile();
		//dTask.execute(theDetailText);

		Button btnSendMsg = (Button)findViewById(R.id.sendmsg);
		btnSendMsg.setOnClickListener(new Button.OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String msgToSend = "Test";
				webArticle.loadUrl("javascript:callFromActivity(\""+msgToSend+"\")");

			}});
	}

	@Override
	public void onTaskComplete(int statusCode, String result,String webserviceCb) {
		try {
			
		}
		catch (Exception e) {
			// TODO: handle exception
		}

	}

	String responseString = "";
	private class DownloadFile extends AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... sUrl) {
			try {
				URL url = new URL(sUrl[0]);
				URLConnection connection = url.openConnection();
				connection.connect();
				int fileLength = connection.getContentLength();

				InputStream input = new BufferedInputStream(url.openStream());
				OutputStream output = new FileOutputStream("/sdcard/test.html");

				byte data[] = new byte[1024];
				long total = 0;
				int count;
				while ((count = input.read(data)) != -1) {
					total += count;
					// publishing the progress....
					publishProgress((int) (total * 100 / fileLength));
					output.write(data, 0, count);
				}

				output.flush();
				output.close();
				input.close();

			} catch (Exception e) {
			}
			return null;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected void onProgressUpdate(Integer... progress) {
			super.onProgressUpdate(progress);
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			try {
				FileInputStream fis = new FileInputStream("/sdcard/test.html");
				responseString = new Scanner(fis).useDelimiter("\\A").next();
				Log.e("", "responseString: " + responseString);
				fis.close();

				String[]theArray =responseString.split("<div>");

				String theDetailText = theArray[1];

				theArray = theDetailText.split("</h1><br>");

				String theDetailTitle = theArray[0];

				String[]imageArray = theDetailTitle.split("<img");

				String imageString; 

				if (imageArray.length > 1) {

					String tempString;

					tempString = imageArray[1];

					imageArray = tempString.split(">");

					if (imageArray.length  > 1) {

						imageString = "<img height=240"+imageArray[0]+">";

					} else {
						imageString = "";
					}
				} else {
					imageString = "";
				}

				theDetailText = theArray [1];

				theArray = theDetailText.split("</div>");

				theDetailText = theArray[0];
				// <meta http-equiv='Content-type' content='text/html;charset=utf-8'>
				// document.getElementById("mytext").innerHTML = msg;
				theDetailText = "<HTML> " +
						"<script language=\"javascript\"> function callFromActivity(msg){ document.body.innerHTML = document.body.innerHTML.replace('Pasting', 'AAAAA');} </script>" +
						"<head>" +
						"<style type='text/css'>body {font-family: 'Arial'; font-size: 18.0;}</style>" +
						"<BODY>" +
						imageString;

				theDetailText = theDetailText + theArray [0];
				//theDetailText = "Also called 'White Pearl in Red Dragon's Mouth', this ancient garden rose is tough as nails, blooms cheerfully through rain, heat and the occasional winter freeze. Being a China Rose, it is long-lived, becoming more dense over time. New growth may have touches of purple, dark red or bronze. Heat tolerant, drought-resistant Ðthis rose can fill almost any landscaping niche and do so without fertilizers, sprays or deadheading. China Roses do not go dormant in the winter Ðthey are inspired by winter rains to bloom in generous masses. China roses prefer minimal pruning. Overall size can reach 3'x3'.";
				// å
				theDetailText = theDetailText +"</BODY></head></HTML> ";

				Log.e("", "=============================>>>>>>>>theDetailText: " + theDetailText);
				webArticle.setBackgroundColor(Color.TRANSPARENT);
				//theDetailText = URLDecoder.decode(theDetailText);
				//webArticle.loadDataWithBaseURL("http://www.arborgate.com", theDetailText, "text/html","utf-8", null);
				//webArticle.loadData(theDetailText, "text/html", null);
				
				//final OutputStream os = new FileOutputStream("/sdcard/test.html");
				final OutputStream os = new FileOutputStream("/sdcard/test.html");
				final PrintStream printStream = new PrintStream(os);
				printStream.print(theDetailText);
				printStream.close();
				
				webArticle.loadUrl("file:///sdcard/test.html");
			}
			catch (Exception e) {
				// TODO: handle exception
			}
		}

	}
}

