package com.goodstart.minutescheckerlib;

import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;

public class GsMainActivity extends Activity
{
	public static final String PREFERENCES_FILE = "CheckMyMinutes.preferences";
	
	GsAccountList accountList;
	GsAccountListAdaptor accountListAdaptor;
	ListView accountsListView;

	void about()
	{
		GsPrompter.showMessageDialog("About", this.aboutFileContents(), this, null);
	}
	
	String aboutFileContents()
	{
		AssetManager assetManager = getAssets();
		InputStream input;
		String text = "";
        try
        {
        	input = assetManager.open("About.txt");
        	int size = input.available();
        	byte[] buffer = new byte[size];
        	input.read(buffer);
        	input.close();
        	text = new String(buffer);
        } 
        catch (IOException e) 
        {
            text = e.getMessage();
            e.printStackTrace();
        }
        return text;
	}
	
	void addAccount()
	{
		GsPrompter.showAddAccountDialog(this, this.addAccountCompletedHandler());
	}
	
	void addAccountCompleted(Message msg)
	{
		String phoneNumber = msg.getData().getString("phoneNumber");
		String pin = msg.getData().getString("pin");
		GsAccount account = new GsAccount();
		account.setPhoneNumber(phoneNumber);
		account.setPin(pin);
		this.refreshAccount(account);
		this.accountList.addAccount(account);
	}
	
	Handler addAccountCompletedHandler()
	{
		return
			new Handler()
			{
		        @Override
		        public void handleMessage(Message msg)
		        {
		        	addAccountCompleted(msg);
		        }
			};
	}
	
	void deleteAccount(GsAccount account)
	{
		this.accountList.deleteAccount(account);
	}
	
	void editAccount(GsAccount account)
	{
		GsPrompter.showEditAccountDialog(
				this, 
				this.editAccountCompletedHandler(), 
				account.getPhoneNumber(), 
				""); // blank out the pin
	}
	
	void editAccountCompleted(Message msg)
	{
		String phoneNumber = msg.getData().getString("phoneNumber");
		String pin = msg.getData().getString("pin");
		GsAccount account = this.accountList.getSelectedAccount();
		account.setPhoneNumber(phoneNumber);
		account.setPin(pin);
		this.refreshAccount(account);
		this.accountList.signalChanged();
	}
	
	Handler editAccountCompletedHandler()
	{
		return
			new Handler()
			{
		        @Override
		        public void handleMessage(Message msg)
		        {
		        	editAccountCompleted(msg);
		        }
			};
	}
	
	boolean isNewVersion()
	{
		SharedPreferences sharedPref = getSharedPreferences("VersionForWhatsNew", Context.MODE_PRIVATE);
        int currentVersionNumber = 0;
        int lastVersionNumber = sharedPref.getInt("lastVersion", 0); 
        try {
            PackageInfo pi = getPackageManager().getPackageInfo(getPackageName(), 0);
            currentVersionNumber = pi.versionCode;
        } catch (Exception e) {return false;}
 
        if (currentVersionNumber > lastVersionNumber)
        {
            Editor editor   = sharedPref.edit();
            editor.putInt("lastVersion", currentVersionNumber);
            editor.commit();
            return true;
        }
        return false;
	}
	
	void moveDown(GsAccount account)
	{
		this.accountList.moveDown(account);
	}

	void moveUp(GsAccount account)
	{
		this.accountList.moveUp(account);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item)
	{
		if (item.getItemId() == R.id.contextMenuRefreshAccount)
		{
			this.refreshAccount(this.accountList.getSelectedAccount());
		}
		else if (item.getItemId() == R.id.contextMenuItemDeleteAccount)
		{
			this.deleteAccount(this.accountList.getSelectedAccount());
		} 
		else if (item.getItemId() == R.id.contextMenuItemEditAccount)
		{
			this.editAccount(this.accountList.getSelectedAccount());
		}
		else if (item.getItemId() == R.id.contextMenuMoveUp)
		{
			this.moveUp(this.accountList.getSelectedAccount());
		} 
		else if (item.getItemId() == R.id.contextMenuMoveDown)
		{
			this.moveDown(this.accountList.getSelectedAccount());
		}
		return false;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		//Log.i("GsMainActivity", "onCreate called");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//Log.i("GsMainActivity", "About to upgrade if needed");
		// Handler upgrade from v4
		this.upgradeFromVersion4();

		// Get the saved info from disk 
		//Log.i("GsMainActivity", "About to load accounts");
		this.accountList = GsAccountList.getCurrent(this);
			
		//Log.i("GsMainActivity", "About to create list view and adaptor");
		// Create a ListView, ListAdaptor, register for context menu, refresh the view
		this.accountListAdaptor = new GsAccountListAdaptor(this.getApplicationContext(), this.accountList);
		this.accountsListView = (ListView) this.findViewById(R.id.accountsListView);
		this.registerForContextMenu(this.accountsListView);
		this.accountsListView.setAdapter(this.accountListAdaptor);
		this.accountListAdaptor.notifyDataSetChanged();
		
		//Log.i("GsMainActivity", "About to show what's new");
		this.whatsNew();
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
	{
		super.onCreateContextMenu(menu, v, menuInfo);

		if(v.getId() == R.id.accountsListView)
		{
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
			this.accountList.setSelectedIndex(info.position);
			this.getMenuInflater().inflate(R.menu.main_context_menu, menu);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_options_menu, menu);
		return true;
	}
	
	@Override
	protected void onDestroy()
	{
	    super.onDestroy();
	}

	public boolean onOptionsItemSelected(MenuItem item)
	{
		if (item.getItemId() == R.id.mainMenuItemAddAccountId)
		{
			this.addAccount();
		} 
		else if (item.getItemId() == R.id.mainMenuItemRefreshAllId)
		{
			this.refreshAllAccounts();
		} 
		else if (item.getItemId() == R.id.mainMenuItemSettingsId)
		{
			Intent settingsActivity = new Intent(getBaseContext(), GsPreferencesActivity.class);
			startActivity(settingsActivity);
		}
		else if (item.getItemId() == R.id.mainMenuItemRateThisApp)
		{
			this.rateThisApp();
		}
		else if (item.getItemId() == R.id.mainMenuItemAboutId)
		{
			this.about();
		}
		return super.onOptionsItemSelected(item);
	}

	void rateThisApp()
	{
		Intent intent = new Intent(Intent.ACTION_VIEW);
		String url = this.getString(R.string.app_market_url);
		intent.setData(Uri.parse(url));
		startActivity(intent);
	}
	
	void refreshAccount(GsAccount account)
	{
		account.setRefreshing(true);
		this.accountListAdaptor.notifyDataSetChanged();
		Dialog d = null; //GsPrompter.showProgressDialog(this);
		this.accountList.refreshAccount(account, this.refreshAccountCompletedHandler(d));
	}
	
	Handler refreshAccountCompletedHandler(final Dialog d)
	{
		return 
			new Handler()
			{
				Dialog dialog = d;
		        @Override
		        public void handleMessage(Message msg)
		        {
		        	refreshCompleted(msg, d);
		        }
			};
	}
	
	void refreshAllAccounts()
	{
		Dialog d = null; //GsPrompter.showProgressDialog(this);
		for(GsAccount a : this.accountList)
		{
			a.setRefreshing(true);
		}
		this.accountListAdaptor.notifyDataSetChanged();
		this.accountList.refreshAllAccounts(this.refreshAccountCompletedHandler(d));
	}

	void refreshCompleted(Message msg, Dialog d)
	{
		//Log.i("GsMainActivity", "refreshCompleted() was called");
		//d.dismiss();
		this.accountListAdaptor.notifyDataSetChanged();
	}
	
	void upgradeFromVersion4()
	{
		// create an account from the v4 file
		String v4PreferencesFile = "CheckMyMinutes.preferences";
		SharedPreferences settings = getSharedPreferences(v4PreferencesFile, 0);
	    String phoneNumber = settings.getString("phoneNumber", "").trim();
	    String pin = settings.getString("pin", "").trim();
	    if(phoneNumber.length() != 0)
	    {
	    	GsAccount account = new GsAccount();
		    account.setPhoneNumber(phoneNumber);
		    account.setPin(pin);
		    
		    GsAccountList list = new GsAccountList();
		    list.setContext(this);
		    list.addAccount(account); // this does a save
	    }

	    // wipe out the contents of the v4 file
		SharedPreferences.Editor editor = settings.edit();
		editor.clear();
		editor.commit();
	}
	
	void whatsNew()
	{
		if(this.isNewVersion())
		{
			GsPrompter.showMessageDialog(
					"What\'s New", 
					whatsNewFileContents(), 
					this, 
					this.whatsNewCompletedHandler());
		}
		else
		{
			this.whatsNewCompleted(null);
		}
	}
	
	public void whatsNewCompleted(Message msg)
	{
		if(this.accountList.isEmpty() == false)
		{
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
			boolean autoRefreshOnOpen = prefs.getBoolean("autoRefreshOnOpen", true);
			if(autoRefreshOnOpen)
			{
				this.refreshAllAccounts();
			}
		}
		else
		{
			this.addAccount();
		}
	}
	
	Handler whatsNewCompletedHandler()
	{
		return 
			new Handler()
			{
		        @Override
		        public void handleMessage(Message msg)
		        {
		        	whatsNewCompleted(msg);
		        }
			};
	}
	
	String whatsNewFileContents()
	{
		AssetManager assetManager = getAssets();
		InputStream input;
		String text = "";
        try
        {
        	input = assetManager.open("WhatsNew.txt");
        	int size = input.available();
        	byte[] buffer = new byte[size];
        	input.read(buffer);
        	input.close();
        	text = new String(buffer);
        } 
        catch (IOException e) 
        {
            text = e.getMessage();
            e.printStackTrace();
        }
        return text;
	}
	
	
	
	
}
