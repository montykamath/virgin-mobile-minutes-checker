package com.goodstart.minutescheckerlib;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

public class GsServiceToRefreshAccountForWidgets extends Service
{	
	GsAccountList accountList;
	GsAccount account;
	Context context;
	
	
	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{

		Log.i(this.getClass().getSimpleName(), "onStartCommand() called");
		this.context = this.getApplicationContext();
		this.accountList = GsAccountList.getCurrent(context);
		
		// Exit if we don't have any accounts yet
		if (this.accountList.isEmpty())
		{
			return Service.START_NOT_STICKY;
		}
		
		this.account = accountList.getPrimaryAccount();
		if(this.account == null)
		{
			return Service.START_NOT_STICKY;
		}
		
		if(this.account.requiresRefreshForWidgets() == false)
		{
			return Service.START_NOT_STICKY;
		}
		
		Log.i(this.getClass().getSimpleName(), "onStartCommand() refreshing account");
		this.accountList.refreshAccount(this.account, this.refreshAccountCompletedHandler());

		return Service.START_NOT_STICKY;
	}
	
	Handler refreshAccountCompletedHandler()
	{
		return 
			new Handler()
			{
		        @Override
		        public void handleMessage(Message msg)
		        {
		        	GsWidgetProvider.updateWidgets(accountList, context);
		        }
			};
	}
}

