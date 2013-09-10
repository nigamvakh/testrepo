package com.exercise.AndroidHTML;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class ETechAsyncTask extends AsyncTask<String, Void, String> 
{
	private String TAG = "ETechAsyncTask";
	private AsyncTaskCompleteListener<String> callback = null;
	private ProgressDialog progressDialog = null;
	
	private HttpClient httpClient;
	
	private HttpGet httpget;
	private HttpPost httpPost;
	private HttpResponse response;
	
	private String responseString = "";
	private String webserviceCb = "";
	private HashMap<String, Object> paramValues = null;
	
	public static final int REQUEST_METHOD_GET = 1;
	public static final int REQUEST_METHOD_POST = 2;
	
	public static final int CANCELED = 101;
	public static final int COMPLETED = 102;
	public static final int ERROR = 103;
	public int status = ERROR;
	
	public boolean isMultiPartData=false;
	private static final String twoHyphens = "--";
	private static final String boundary = "---------------------------";
	private static final String CRLF = "\r\n";
	private ConnectionDetector checkConnection;
	private Boolean isNetAvailable=false;
	public static Boolean showProgressDialog=true;
	
	private int requestMethod = REQUEST_METHOD_GET;

	public ETechAsyncTask(Context context, AsyncTaskCompleteListener<String> cb, String webserviceCb, HashMap<String, Object> paramValues) {
		this(context, cb, webserviceCb, paramValues, REQUEST_METHOD_GET,false);
	}
	
	public ETechAsyncTask(Context context, AsyncTaskCompleteListener<String> cb, String webserviceCb, HashMap<String, Object> paramValues, int requestMethod) {
		this(context, cb, webserviceCb, paramValues, requestMethod,false);
	}
	
	public ETechAsyncTask(Context context, AsyncTaskCompleteListener<String> cb, String webserviceCb, HashMap<String, Object> paramValues, int requestMethod, boolean isMultiPartData) {
		callback = cb;
		this.webserviceCb = webserviceCb;
		if(paramValues != null)
			this.paramValues = paramValues;
		this.requestMethod=requestMethod;
		this.isMultiPartData=isMultiPartData;
		if(showProgressDialog){
		progressDialog = new ProgressDialog(context);
		progressDialog.setCancelable(false);
		}
		checkConnection=new ConnectionDetector(context);
	}
	
	@Override
	protected void onPreExecute() {
		if (progressDialog != null) {
			progressDialog.setMessage("Loading....");
			progressDialog.show();
			progressDialog.setOnDismissListener(onDismissListener);
		}
		isNetAvailable=checkConnection.isConnectingToInternet();
		
	}

	protected String doInBackground(String... url) {
		if(isNetAvailable){
		
		try {
			String strUrl = url[0];
			httpClient = new DefaultHttpClient();
			
			if(requestMethod==REQUEST_METHOD_GET) {
				if(paramValues != null) {
					ArrayList<String> arrKey = new ArrayList<String>(paramValues.keySet());
					
					for (String string : arrKey) {
						strUrl = strUrl + string + "=" + paramValues.get(string) + "&";
					}
				}
				Log.d(TAG, "strUrl: " + strUrl);
				
				httpget = new HttpGet(strUrl);
				response = httpClient.execute(httpget);
			}else{
				//String strOnlyURL=strUrl.split("\\?")[0];
				String strOnlyURL=strUrl;
				if(!isMultiPartData){
					List<NameValuePair> pairs = new ArrayList<NameValuePair>();
					Iterator<String> iterParamKeys = paramValues.keySet().iterator();
					String strKey=null;String strValue=null;
					while(iterParamKeys.hasNext()){
						strKey=strValue=null;
						strKey=iterParamKeys.next();
						strValue=paramValues.get(strKey).toString();
						if(strKey !=null){
							pairs.add(new BasicNameValuePair(strKey,strValue));
						}			
					}
					httpPost = new HttpPost(strOnlyURL);
					
					if(pairs.size() > 0) {
						UrlEncodedFormEntity entity = new UrlEncodedFormEntity(pairs,"utf-8");
						
						httpPost.setEntity(entity);
					}
				}else{
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					for(String key : paramValues.keySet())
					{
						Log.d(TAG, "==============-------->key: " + key);
						Object objValue=paramValues.get(key);
						if(objValue instanceof FileObject){
							writeFileField(baos, key,((FileObject)objValue).getFileName(), ((FileObject)objValue).getContentType(), ((FileObject)objValue).getByteData());	
						}else{
							writeFormField(baos, key, paramValues.get(key).toString());	
						}
					}							
					baos.write((twoHyphens + boundary + twoHyphens + CRLF).getBytes());
					httpPost = new HttpPost(strOnlyURL);
					httpPost.setHeader("Content-Type", "multipart/form-data; boundary=" + boundary);
					httpPost.setEntity(new ByteArrayEntity(baos.toByteArray()));
				}				
				response = httpClient.execute(httpPost);
			}

			HttpEntity entity = response.getEntity();
			if(entity != null) {
				responseString = EntityUtils.toString(entity);
            }
			
			/*
			if (entity != null) {
				
				InputStream instream = entity.getContent();
				InputStreamReader inp = new InputStreamReader(instream);
				Log.e("", "inp.getEncoding: " + inp.getEncoding());
				BufferedReader reader = new BufferedReader(inp);
				StringBuilder sb = new StringBuilder();
				String line = null;
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
				
				status = COMPLETED;
				responseString = sb.toString();
				instream.close();
			}
			/**/
		} 
		catch (UnknownHostException e) {
			Log.e(TAG, " doInBackground > UnknownHostException : " + e,e);
			responseString = e.getMessage();
			status = ERROR;
			//handler.sendMessage(handler.obtainMessage(0));
		}
		catch (Exception e) {
			Log.e(TAG, " doInBackground > Exception : " + e,e);
			responseString = e.getMessage();
			status = ERROR;
			//handler.sendMessage(handler.obtainMessage(1));
		}
		
		return responseString;
	}
		
	else{
		    status=CANCELED;
			ETechAsyncTask.this.cancel(isNetAvailable);
		}
		return null;
	}
	protected void onProgressUpdate() {
		
	}
	
	protected void onProgressUpdate(Integer... progress) {
		
    }

	
	protected void onPostExecute(String responseString) {
		super.onPostExecute(responseString);
		try {
			if (progressDialog != null)
				progressDialog.dismiss();
		} 
		catch (Exception e) {}
		
		callback.onTaskComplete(status, responseString,webserviceCb);
	}
	
	protected void onCancelled() {
		super.onCancelled();
		try {
			if (progressDialog != null)
				progressDialog.dismiss();
		} 
		catch (Exception e) {}
		
		callback.onTaskComplete(status,"No Internet Connection is available.Please Check your Internet Connection.",webserviceCb);
	}	
	
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if(msg.what == 0) // network message
				callback.onTaskComplete(ERROR, responseString,webserviceCb);
			else
				callback.onTaskComplete(ERROR, responseString,webserviceCb);
			
			if(!ETechAsyncTask.this.isCancelled())
				ETechAsyncTask.this.cancel(false);
		}
	};
	
	private OnDismissListener onDismissListener = new OnDismissListener() {
		public void onDismiss(DialogInterface dialog) {
			if(!ETechAsyncTask.this.isCancelled())
				if(ETechAsyncTask.this.cancel(false)) {
					responseString = "Loading canced.";
					//callback.onTaskComplete(CANCELED, responseString, webserviceCb);
				}
		}
	};
	
	private void writeFormField(ByteArrayOutputStream baos, String fieldName, String fieldValue) throws IOException
	{
		baos.write((twoHyphens + boundary + CRLF).getBytes());
		baos.write(("Content-Disposition: form-data;name=\"" + fieldName + "\"" + CRLF).getBytes("UTF-8"));
		baos.write((CRLF + fieldValue + CRLF).getBytes());
	}

	private void writeFileField(ByteArrayOutputStream baos, String fieldName, String fileName, String contentType, byte [] buf) throws IOException
	{
		baos.write((twoHyphens + boundary + CRLF).getBytes());
		baos.write(("Content-Disposition: form-data;name=\"" + fieldName + "\";filename=\"" + fileName + "\"" + CRLF).getBytes("UTF-8"));
		baos.write(("Content-Type: " + contentType + CRLF + CRLF).getBytes());
		baos.write(buf);
		baos.write(CRLF.getBytes());
	}
}
