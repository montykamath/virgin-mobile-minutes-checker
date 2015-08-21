package com.goodstart.minutescheckerlib;

import java.util.ArrayList;
import java.util.Iterator;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;

public class GsAccountList implements Iterable<GsAccount>
{
	public static final String ACCOUNTS_FILE = "Accounts.db";
	private static GsAccountList current; 
	
	public static GsAccountList getCurrent(Context context)
	{
		if(current == null)
		{
			current = new GsAccountList();
			current.setContext(context);
			current.readFromFile();
		}
		return current;
	}
	
	ArrayList<GsAccount> accounts = new ArrayList<GsAccount>();
	private Context context;
	int selectedIndex = 0;
	
	GsAccountListAdaptor accountListAdaptor;
	
	void addAccount(GsAccount account)
	{
		this.getAccounts().add(account);
		this.signalChanged();
	}

	void deleteAccount(GsAccount account)
	{
		this.getAccounts().remove(account);
		this.signalChanged();
	}
	
	public GsAccount getAccount(int index)
	{
		return this.accounts.get(index);
	}
	
	public ArrayList<GsAccount> getAccounts()
	{
		return accounts;
	}
	
	Context getContext()
	{
		return context;
	}
	
	GsAccount getPrimaryAccount()
	{
		if(this.getAccounts().size() == 0) {return null;}
		
		return this.getAccounts().get(0);
	}
	
	GsAccount getSelectedAccount()
	{
		return this.getAccounts().get(this.getSelectedIndex());
	}
	
	public int getSelectedIndex()
	{
		return selectedIndex;
	}

	public int getSize()
	{
		return this.getAccounts().size();
	}
	
	public boolean isEmpty()
	{
		return this.getAccounts().isEmpty();
	}
	
	@Override
	public Iterator<GsAccount> iterator()
	{
		return this.accounts.iterator();
	}
	
	void moveDown(GsAccount account)
	{
		int i = this.getAccounts().indexOf(account);
		if(i != this.getAccounts().size() - 1)
		{
			this.getAccounts().remove(account);
			this.getAccounts().add(i + 1, account);
			this.signalChanged();
		}
	}
	
	void moveUp(GsAccount account)
	{
		int i = this.getAccounts().indexOf(account);
		if(i != 0)
		{
			this.getAccounts().remove(account);
			this.getAccounts().add(i - 1, account);
			this.signalChanged();
		}
	}
	
	synchronized void readFromFile()
	{
		ArrayList<GsAccount> readAccounts = new ArrayList<GsAccount>();
		
		SharedPreferences settings = context.getSharedPreferences(ACCOUNTS_FILE, 0);
		int fileVersion = settings.getInt("fileVersion", 0);
		if(fileVersion <= 5)
		{
			SharedPreferences v4Settings = this.getContext().getSharedPreferences("CheckMyMinutes.preferences", 0);
			String phone = v4Settings.getString("phoneNumber", "").trim();
			if(phone.length() != 0)
			{
				//version 4
				readAccounts = this.readFromFileForVersion4();
			}
			else
			{
				//version 5
				readAccounts = this.readFromFileForVersion5();
			}
		}
		else if(fileVersion == 6)
		{
			readAccounts = this.readFromFileForVersion6();
		}
		else if(fileVersion == 7)
		{
			readAccounts = this.readFromFileForVersion7();
		}
		
		this.setAccounts(readAccounts);
		this.writeToFile(); // re-save in most current file version
	}
	
	synchronized ArrayList<GsAccount> readFromFileForVersion4()
	{		
		// Create an account list with one account from the v4 file
		String v4PreferencesFile = "CheckMyMinutes.preferences";
		SharedPreferences settings = this.getContext().getSharedPreferences(v4PreferencesFile, 0);
		String phoneNumber = settings.getString("phoneNumber", "").trim();
		String pin = settings.getString("pin", "").trim();
		ArrayList<GsAccount> accounts = new ArrayList<GsAccount>();
		if(phoneNumber.length() != 0)
		{
			GsAccount account = new GsAccount();
			account.setPhoneNumber(phoneNumber);
			account.setPin(pin);
			accounts.add(account);
		}

		// Wipe out the contents of the v4 file
		SharedPreferences.Editor editor = settings.edit();
		editor.clear();
		editor.commit();
		
		return accounts;
	}
	
	synchronized ArrayList<GsAccount> readFromFileForVersion5()
	{		
		// Create a list of accounts from the comma file
		ArrayList<GsAccount> accounts = new ArrayList<GsAccount>();
		SharedPreferences settings = context.getSharedPreferences(ACCOUNTS_FILE, 0);
		int numberOfAccounts = settings.getInt("numberOfAccounts", 0);
		for(int i=0; i < numberOfAccounts; i++)
		{
			String line = settings.getString("account"+i, "").trim();
			if(line.indexOf(",") != 10) {return accounts;} // Don't need to upgrade
			GsAccount acct  = GsAccount.fromPersistentStringWithCommas(line);
			accounts.add(acct);
		}

		// Wipe out the old file
		SharedPreferences c = context.getSharedPreferences(ACCOUNTS_FILE, 0);
		SharedPreferences.Editor editor = c.edit();
		editor.clear();
		editor.commit();
		
		return accounts;
	}
	
	synchronized ArrayList<GsAccount> readFromFileForVersion6()
	{
		//Log.i("GsAccountList", "About to read to file");
		ArrayList<GsAccount> readAccounts = new ArrayList<GsAccount>();
		
		SharedPreferences settings = context.getSharedPreferences(ACCOUNTS_FILE, 0);
		//int fileVersion = settings.getInt("fileVersion", 0);
	    int numberOfAccounts = settings.getInt("numberOfAccounts", 0);
	    for(int i=0; i < numberOfAccounts; i++)
	    {
	    	String line = settings.getString("account"+i, "").trim();
	    	GsAccount acct  = GsAccount.fromPersistentStringForFileVersion6(line);
	    	readAccounts.add(acct);
	    }
		return readAccounts;
	}
	
	synchronized ArrayList<GsAccount> readFromFileForVersion7()
	{
		//Log.i("GsAccountList", "About to read to file");
		ArrayList<GsAccount> readAccounts = new ArrayList<GsAccount>();
		
		SharedPreferences settings = context.getSharedPreferences(ACCOUNTS_FILE, 0);
		//int fileVersion = settings.getInt("fileVersion", 0);
	    int numberOfAccounts = settings.getInt("numberOfAccounts", 0);
	    for(int i=0; i < numberOfAccounts; i++)
	    {
	    	String line = settings.getString("account"+i, "").trim();
	    	GsAccount acct  = GsAccount.fromPersistentStringForFileVersion7(line);
	    	readAccounts.add(acct);
	    }
		return readAccounts;
	}
	
	void refreshAccount(final GsAccount account, final Handler completionHandler)
	{
		Thread childThread = new Thread(
			new Runnable()
			{
				@Override
				public void run()
				{
					account.refreshFromWebsite();
					
					// Would have called signalChanged() but didn't want to update the listAdaptor
					writeToFile();
					updateWidgets();
					
					if(completionHandler != null)
					{
						completionHandler.sendEmptyMessage(0);
					}
				}
			});
		childThread.start();			
	}
	
	void refreshAllAccounts(final Handler completionHandler)
	{
		Thread mainThread = new Thread(
			new Runnable()
			{
				@Override
				public void run()
				{
					ArrayList<Thread> childThreads = new ArrayList<Thread>();
					for(final GsAccount account : getAccounts())
					{
						Thread childThread = new Thread(
							new Runnable()
							{
								@Override
								public void run()
								{
									account.refreshFromWebsite();
									
								}
							});
						childThread.start();
						childThreads.add(childThread);
					}
					for(Thread childThread : childThreads)
					{
						try
						{
							childThread.join();
						} catch (InterruptedException e)
						{
							Log.w(this.getClass().toString(), "Exception happend while waiting for minute checker threads to run "+ e.getMessage());
						}
					}
					// Would have called signalChanged() but didn't want to update the listAdaptor
					writeToFile();
					updateWidgets();
					
					if(completionHandler != null)
					{
						completionHandler.sendEmptyMessage(0);
					}
				}
			}
		
		);
		mainThread.start();
	}
	
	void setAccountListAdaptor(GsAccountListAdaptor accountListAdaptor)
	{
		this.accountListAdaptor = accountListAdaptor;
		
	}
	
	public void setAccounts(ArrayList<GsAccount> accounts)
	{
		this.accounts = accounts;
	}

	void setContext(Context context)
	{
		this.context = context;
	}

	public void setSelectedIndex(int selectedIndex)
	{
		this.selectedIndex = selectedIndex;
	}

	void signalChanged()
	{
		this.writeToFile();
		this.updateWidgets();
		
		if(this.accountListAdaptor != null)
		{
			this.accountListAdaptor.notifyDataSetChanged();
		}
	}
	
	void updateWidgets()
	{
		GsWidgetProvider.updateWidgets(this, getContext());
	}
	
	synchronized void writeToFile()
	{
		this.writeToFileForVersion7();
	}
	
	synchronized void writeToFileForVersion6()
	{
		//Log.i("GsAccountList", "About to write to file");
		SharedPreferences settings = context.getSharedPreferences(ACCOUNTS_FILE, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt("fileVersion", 6);
		editor.putInt("numberOfAccounts", this.getAccounts().size());
		for(int i=0; i < this.getAccounts().size(); i++)
		{
			GsAccount account = this.getAccount(i);
			editor.putString("account"+i, account.getPersistentStringForFileVersion6().trim());
		}
		editor.commit();
	}
	
	synchronized void writeToFileForVersion7()
	{
		//Log.i("GsAccountList", "About to write to file");
		SharedPreferences settings = context.getSharedPreferences(ACCOUNTS_FILE, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt("fileVersion", 7);
		editor.putInt("numberOfAccounts", this.getAccounts().size());
		for(int i=0; i < this.getAccounts().size(); i++)
		{
			GsAccount account = this.getAccount(i);
			editor.putString("account"+i, account.getPersistentStringForFileVersion7().trim());
		}
		editor.commit();
	}
}
