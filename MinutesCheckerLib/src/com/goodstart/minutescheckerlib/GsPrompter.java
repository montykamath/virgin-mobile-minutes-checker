package com.goodstart.minutescheckerlib;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class GsPrompter
{
	public static void showMessageDialog(String title, String message, Activity activity, final Handler handler)
	{
		LayoutInflater inflater = LayoutInflater.from(activity);
        View view = inflater.inflate(R.layout.prompter_message, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		TextView titleText = (TextView)view.findViewById(R.id.titleText);
		titleText.setText(title);
		TextView messageText = (TextView)view.findViewById(R.id.messageText);
		messageText.setText(message);
        builder.setView(view);
        if(handler != null)
        {
	        builder.setOnCancelListener(new OnCancelListener() {
	            public void onCancel(final DialogInterface dialog) {
	                handler.sendEmptyMessage(0);
	            }
	        });
        }
        builder.create();
        builder.show();
	}
	
	public static void showAddAccountDialog(Activity activity, final Handler handler)
	{
		LayoutInflater inflater = LayoutInflater.from(activity);
		View dialogview = inflater.inflate(R.layout.prompter_add_account, null);
		final EditText phoneNumber = (EditText) dialogview.findViewById(R.id.dialogPhoneNumber);
		final EditText pin = (EditText) dialogview.findViewById(R.id.dialogPin);
		Button okButton = (Button) dialogview.findViewById(R.id.dialogButtonOK);
		phoneNumber.requestFocus();
		
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
		dialogBuilder.setTitle("Add Account");
		dialogBuilder.setView(dialogview);
		final AlertDialog dialog = dialogBuilder.create();

		okButton.setOnClickListener(
			new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					try { dialog.dismiss(); } catch (Exception e) { } // ignore errors
					Message msg = new Message();
					Bundle data = new Bundle();
					data.putString("phoneNumber", phoneNumber.getText().toString().trim());
					data.putString("pin", pin.getText().toString().trim());
					msg.setData(data);
					handler.sendMessage(msg);
				}
			});
		dialog.show();
	}
	
	public static void showEditAccountDialog(Activity activity, final Handler handler, String phoneNumber, String pin)
	{
		LayoutInflater inflater = LayoutInflater.from(activity);
		View dialogview = inflater.inflate(R.layout.prompter_add_account, null);
		final EditText phoneNumberText = (EditText) dialogview.findViewById(R.id.dialogPhoneNumber);
		phoneNumberText.setText(phoneNumber);
		final EditText pinText = (EditText) dialogview.findViewById(R.id.dialogPin);
		pinText.setText(pin);
		Button okButton = (Button) dialogview.findViewById(R.id.dialogButtonOK);
		phoneNumberText.requestFocus();
		
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
		dialogBuilder.setTitle("Edit Account");
		dialogBuilder.setView(dialogview);
		final AlertDialog dialog = dialogBuilder.create();

		okButton.setOnClickListener(
			new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					try { dialog.dismiss(); } catch (Exception e) { } // ignore errors
					Message msg = new Message();
					Bundle data = new Bundle();
					data.putString("phoneNumber", phoneNumberText.getText().toString().trim());
					data.putString("pin", pinText.getText().toString().trim());
					msg.setData(data);
					handler.sendMessage(msg);
				}
			});
		dialog.show();
	}
	
	public static ProgressDialog showProgressDialog(Activity activity)
	{
		ProgressDialog dialog = new ProgressDialog(activity);
		dialog.setCancelable(false);
		dialog.setMessage("Checking your minutes");
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.show();
		return dialog;
	}
	
	public static void showCustomToast(Context context, String titleString, String msgString)
	{
		Toast toast = new Toast(context);
		toast.setDuration(Toast.LENGTH_LONG);
		toast.setGravity(Gravity.TOP | Gravity.CENTER, 0, 0);
		LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View toastView = li.inflate(R.layout.prompter_toast, null);
		toast.setView(toastView);
		TextView title = (TextView) toastView.findViewById(R.id.title);
		title.setText(titleString);
		TextView text = (TextView) toastView.findViewById(R.id.text);
		text.setText(msgString);
		toast.show();
	}
}
