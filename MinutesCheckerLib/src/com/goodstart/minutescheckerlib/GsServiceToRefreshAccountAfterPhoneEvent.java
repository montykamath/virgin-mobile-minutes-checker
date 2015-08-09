package com.goodstart.minutescheckerlib;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;

public class GsServiceToRefreshAccountAfterPhoneEvent extends Service
{	
	GsAccountList accountList;
	GsAccount account;
	Context context;
	boolean popupBeforeCall;
	boolean popupAfterCall;
	
	
	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		Bundle extras = intent.getExtras();
		this.context = this.getApplicationContext();
		this.accountList = GsAccountList.getCurrent(context);
		// Exit if we can't get phone state from the intent
		if (extras == null)
		{
			return Service.START_NOT_STICKY;
		}

		String phoneState = extras.getString("PhoneState");
		
		// Exit if we don't have any accounts yet
		if (this.accountList.isEmpty())
		{
			return Service.START_NOT_STICKY;
		}
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		this.popupBeforeCall = prefs.getBoolean("popupBeforeCall", true);
		this.popupAfterCall = prefs.getBoolean("popupAfterCall", true);
		this.account = accountList.getPrimaryAccount();
		
		// If the user just dialed then show them their minutes (without checking the website)
		if(phoneState.equals("OFFHOOK"))
		{
			if(this.popupBeforeCall)
			{
				GsPrompter.showCustomToast(
						this.context, 
						this.account.getPhoneNumberFormatted(), 
						this.account.getPhoneEventToastString());
			}
			GsWidgetProvider.updateWidgets(this.accountList, this.context);
		}
		
		// If the user just hung up then check the website and then show them their minutes
		if(phoneState.equals("IDLE"))
		{
			//new RefreshAccountAsyncTask().execute(account);
			this.accountList.refreshAccount(this.account, this.refreshAccountCompletedHandler());
		}

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
		        	if(popupAfterCall)
					{
						GsPrompter.showCustomToast(
								context, 
								account.getPhoneNumberFormatted(), 
								account.getPhoneEventToastString());
					}
					GsWidgetProvider.updateWidgets(accountList, context);
		        }
			};
	}
}

