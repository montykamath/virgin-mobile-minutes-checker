package com.goodstart.minutescheckerlib;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

public class GsWidgetProvider extends AppWidgetProvider
{
	public static void updateWidgets(GsAccountList accountList, Context context)
	{
		GsWidgetProvider1x1.updateWidgets(accountList, context);
		GsWidgetProvider2x1.updateWidgets(accountList, context);
		GsWidgetProvider1x1Pie.updateWidgets(accountList, context);
		GsWidgetProvider1x1OldStylePie.updateWidgets(accountList, context);
	}
	
	@Override
	public void onReceive(Context context, Intent intent)
	{
	    super.onReceive(context, intent);
	    //Log.i(this.getClass().getSimpleName(), "onReceive called so calling");
	    if (intent.getAction().equals(this.getClass().getSimpleName()+"_UPDATE"))
	    {
	    	
	    	//Log.i(this.getClass().getSimpleName(), "onUpdate called with widget ids: " + Arrays.toString(appWidgetIds));
			GsAccountList accountList = GsAccountList.getCurrent(context);
			GsAccount account = accountList.getPrimaryAccount();			
	        this.updateViews(
	        		context, 
	        		AppWidgetManager.getInstance(context) , 
	        		intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS),
	        		account);
	    }
	}
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds)
	{
		//Log.i(this.getClass().getSimpleName(), "onUpdate() called - Calling Service to refresh widgets");
		
		// Call service to refresh account info if it needs to be refreshed
		// This method is called every 30 min but we only actually update the account if 
		//   - the last update got an error or 
		//   - every 6 hrs
		Intent i= new Intent(context, GsServiceToRefreshAccountForWidgets.class);
		context.startService(i);
		
		// Refresh the widgets with the latest account info
		// This is here because:
		//   - you want new widgets to refresh right away when put on home screen
		//   - you want old widgets to update if the account info was just updated by the service above
		GsAccountList accountList = GsAccountList.getCurrent(context);
		GsAccount account = accountList.getPrimaryAccount();
		if(account != null)
		{
			this.updateViews(context, appWidgetManager, appWidgetIds, account);
		}
	}
	
	void registerOnClickHandler(Context context, RemoteViews remoteViews)
	{
		Intent intent = new Intent(context, com.goodstart.minutescheckerlib.GsMainActivity.class);
	    PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
	    remoteViews.setOnClickPendingIntent(R.id.widgetLinearLayout, pendingIntent);
	}
	
	void updateViews(Context context, AppWidgetManager appWidgetManager,int[] appWidgetIds, GsAccount account)
	{
		
	}
	
	

}
