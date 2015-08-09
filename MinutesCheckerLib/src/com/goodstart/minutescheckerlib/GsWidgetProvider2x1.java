package com.goodstart.minutescheckerlib;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class GsWidgetProvider2x1 extends GsWidgetProvider
{
	public static void updateWidgets(GsAccountList accountList, Context context)
	{
		if(accountList.isEmpty()) { return; }
	
		AppWidgetManager widgetManager = AppWidgetManager.getInstance(context);
		ComponentName widgetComponent = new ComponentName(context, GsWidgetProvider2x1.class);
		int[] widgetIds = widgetManager.getAppWidgetIds(widgetComponent);
		Intent intent = new Intent();
		intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, widgetIds);
		intent.setAction("GsWidgetProvider2x1_UPDATE");
		context.sendBroadcast(intent);
	}
	
	void updateViews(Context context, AppWidgetManager appWidgetManager,int[] appWidgetIds, GsAccount account)
	{
		//Log.i(this.getClass().getSimpleName(), "About to update 2x1 widgets: "+Arrays.toString(appWidgetIds));
		
		RemoteViews remoteViews;
		if(account == null || account.getErrorMessage() != null)
		{
			remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_2x1_error);
		}
		else
		{
			remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_2x1);
		}
		
		// Setup onClick handler to them them to the app
	    this.registerOnClickHandler(context, remoteViews);
	    
	    if(account == null) {return;}
		remoteViews.setTextViewText(R.id.balanceText, account.getBalance());
		remoteViews.setTextViewText(R.id.minUsedText, account.getMinutesUsed());
		remoteViews.setTextViewText(R.id.chargeText, account.getChargeAmount());
		remoteViews.setTextViewText(R.id.newMonthStartsText, account.getNewMonthStarts());
		remoteViews.setTextViewText(R.id.statusText, account.getLastUpdateDate());
		
		appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
	}
}
