package com.goodstart.minutescheckerlib;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;

public class GsPhoneEventReceiver extends BroadcastReceiver
{
	@Override
	public void onReceive(Context context, Intent intent)
	{
		Bundle extras = intent.getExtras();
		if (extras != null)
		{
			String phoneState = extras.getString(TelephonyManager.EXTRA_STATE);
			Intent i= new Intent(context, GsServiceToRefreshAccountAfterPhoneEvent.class);
			i.putExtra("PhoneState", phoneState);
			context.startService(i);
		}
	}
}
