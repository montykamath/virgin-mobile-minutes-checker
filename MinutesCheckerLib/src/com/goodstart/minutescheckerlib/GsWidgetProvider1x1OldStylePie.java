package com.goodstart.minutescheckerlib;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.widget.RemoteViews;

public class GsWidgetProvider1x1OldStylePie extends GsWidgetProvider
{
	public static void updateWidgets(GsAccountList accountList, Context context)
	{
		if(accountList.isEmpty()) { return; }
	
		AppWidgetManager widgetManager = AppWidgetManager.getInstance(context);
		ComponentName widgetComponent = new ComponentName(context, GsWidgetProvider1x1OldStylePie.class);
		int[] widgetIds = widgetManager.getAppWidgetIds(widgetComponent);
		Intent intent = new Intent();
		intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, widgetIds);
		intent.setAction("GsWidgetProvider1x1OldStylePie_UPDATE");
		context.sendBroadcast(intent);
	}
	
	void updateViews(Context context, AppWidgetManager appWidgetManager,int[] appWidgetIds, GsAccount account)
	{
		//Log.i(this.getClass().getSimpleName(), "About to update 1x1 Pie widgets: " + Arrays.toString(appWidgetIds));
		
		RemoteViews remoteViews;
		if(account == null || account.getErrorMessage() != null)
		{
			remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_1x1_pie_error);
		}
		else
		{
			remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_1x1_pie);
		}
		
		// Setup onClick handler to them them to the app
	    this.registerOnClickHandler(context, remoteViews);
	    
	    if(account == null) {return;} 
	    
	    Bitmap bitmap = new GsPieChart().getPieChartBitmap(context, account, true);
	    
	    remoteViews.setImageViewBitmap(R.id.chart, bitmap);
		remoteViews.setTextViewText(R.id.minUsedText, account.getMinutesUsed());
		//remoteViews.setTextViewText(R.id.statusText, account.getLastUpdateDate());
		
		appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
	}
}
