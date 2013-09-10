package com.exercise.AndroidHTML;

public interface AsyncTaskCompleteListener<T>
{
	public void onTaskComplete(int statusCode, T result, T webserviceCb);
}
