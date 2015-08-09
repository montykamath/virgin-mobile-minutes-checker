package com.goodstart.minutescheckerlib;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

public class GsAccountListAdaptor extends BaseAdapter
{
	private GsAccountList accountList;
    private Context context;
    
	public GsAccountListAdaptor(Context applicationContext, GsAccountList accountList)
	{
		this.accountList = accountList;
		this.accountList.setAccountListAdaptor(this);
		this.context = applicationContext;		
	}

	@Override
	public int getCount()
	{
		return this.accountList.getSize();
	}

	@Override
	public Object getItem(int index)
	{
		return this.accountList.getAccount(index);
	}

	@Override
	public long getItemId(int index)
	{
		return index;
	}

	@Override
	public View getView(int position, View viewToConvert, ViewGroup parent)
	{
		View view;
        if(viewToConvert == null)
        {
            view = View.inflate(this.context, R.layout.account_list_item, null);
        } 
        else
        {
            view = viewToConvert;
        }

        this.updateViewForAccount(view, this.accountList.getAccount(position));

        return view;
	}

	private void updateViewForAccount(View view, GsAccount account)
	{
		//Log.i("GsAccountListAdaptor", "About to update listview");
		TextView phoneNumberText = (TextView) view.findViewById(R.id.phoneNumberText);
		TextView statusText = (TextView) view.findViewById(R.id.statusText);
		TextView minUsedText = (TextView) view.findViewById(R.id.minUsedText);
		TextView dataUsedText = (TextView) view.findViewById(R.id.dataUsedText);
		TextView balanceText = (TextView) view.findViewById(R.id.balanceText);
		TextView newMonthStartsText = (TextView) view.findViewById(R.id.newMonthStartsText);
		TextView chargeText = (TextView) view.findViewById(R.id.chargeText);
		TextView lastUpdateText = (TextView) view.findViewById(R.id.lastUpdateText);
		ProgressBar minUsedProgressBar =(ProgressBar) view.findViewById(R.id.minUsedProgressBar);
		ProgressBar newMonthProgressBar =(ProgressBar) view.findViewById(R.id.newMonthProgressBar);
		TextView refreshingText = (TextView) view.findViewById(R.id.refreshingText);
		
		phoneNumberText.setText(account.getPhoneNumberFormatted());
		minUsedText.setText(account.getMinutesUsed());
		dataUsedText.setText(account.getDataUsed());
		balanceText.setText(account.getBalance());
		newMonthStartsText.setText(account.getNewMonthStarts());
		chargeText.setText(account.getChargeAmount());
		lastUpdateText.setText(account.getLastUpdateDate());
		statusText.setText(account.getErrorMessageForApp());
		minUsedProgressBar.setProgress(account.getMinutesUsedAsProgress());
		newMonthProgressBar.setProgress(account.getNewMonthAsProgress());
		
		if(account.isRefreshing())
		{
			Animation anim = new AlphaAnimation(1.0f, 0.0f);
			anim.setDuration(200); //You can manage the time of the blink with this parameter
			anim.setStartOffset(2500);
			anim.setRepeatMode(Animation.RESTART);
			anim.setRepeatCount(Animation.INFINITE);
			refreshingText.startAnimation(anim);
			refreshingText.setVisibility(View.VISIBLE);
		}
		else
		{
			refreshingText.setVisibility(View.GONE);
		}
		
		if(account.getErrorMessage() == null)
		{
			statusText.setVisibility(View.GONE);
		}
		else
		{
			statusText.setVisibility(View.VISIBLE);
		}
		
		this.notifyDataSetChanged();
		
	}

}
