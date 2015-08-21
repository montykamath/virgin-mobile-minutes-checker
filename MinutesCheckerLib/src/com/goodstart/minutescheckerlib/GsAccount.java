package com.goodstart.minutescheckerlib;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.util.Base64;

@SuppressLint("SimpleDateFormat")
public class GsAccount
{
	public static final String ACCOUNTS_DB_FILE = "Accounts.db";
	private static ArrayList<GsAccount> allAccounts;
	
	static public GsAccount fromPersistentStringForFileVersion6(String aString)
	{
		GsAccount account = new GsAccount();
		String[] parts = aString.split("\\|\\-\\-\\-\\|");
		if(parts.length > 0)
			account.setPhoneNumber(parts[0].trim());
		if(parts.length > 1)
			account.setPin(parts[1].trim());
		if(parts.length > 2)
			account.setLastUpdateDate(parts[2].trim());
		if(parts.length > 3)
			account.setMinutesUsed(parts[3].trim());
		if(parts.length > 4)
			account.setBalance(parts[4].trim());
		if(parts.length > 5)
			account.setChargeAmount(parts[5].trim());
		if(parts.length > 6)
			account.setNewMonthStarts(parts[6].trim());
		return account;
	}
	
	static public GsAccount fromPersistentStringForFileVersion7(String aString)
	{
		GsAccount account = new GsAccount();
		String[] parts = aString.split("\\|\\-\\-\\-\\|");
		if(parts.length > 0)
			account.setPhoneNumber(parts[0].trim());
		if(parts.length > 1)
			account.setPin((new GsAccount()).decodePinValue(parts[1].trim()));
		if(parts.length > 2)
			account.setLastUpdateDate(parts[2].trim());
		if(parts.length > 3)
			account.setMinutesUsed(parts[3].trim());
		if(parts.length > 4)
			account.setBalance(parts[4].trim());
		if(parts.length > 5)
			account.setChargeAmount(parts[5].trim());
		if(parts.length > 6)
			account.setNewMonthStarts(parts[6].trim());
		if(parts.length > 7)
			account.setDataUsed(parts[7].trim());
		return account;
	}
	
	static public GsAccount fromPersistentStringWithCommas(String aString)
	{
		GsAccount account = new GsAccount();
		String[] parts = aString.split(",");
		if(parts.length > 0)
			account.setPhoneNumber(parts[0]);
		if(parts.length > 1)
			account.setPin(parts[1]);
		return account;
	}
	
	public static ArrayList<GsAccount> getAllAccounts()
	{
		return allAccounts;
	}
	
	public static void setAllAccounts(ArrayList<GsAccount> allAccounts)
	{
		GsAccount.allAccounts = allAccounts;
	}
	
	boolean isRefreshing = false;
	String phoneNumber = "";

	String pin = "";

	String minutesUsed = "";
	String dataUsed = "";
	String balance = "";
	String newMonthStarts = "";
	String chargeAmount = "";

	String lastUpdateDate = "";

	String errorMessage = null;
	public String decodePinValue(String s)
	{
		if(s == null) { return ""; }
		if(s.equals("")) { return ""; }
		return new String(Base64.decode(s.getBytes(),Base64.DEFAULT));
	}
	public String encodePinValue(String s)
	{
		if(s == null) { return " "; }
		if(s.equals("")) { return " "; }
		return Base64.encodeToString(s.getBytes(),Base64.DEFAULT);
	}
	public String getBalance()
	{
		return balance;
	}
	public String getChargeAmount()
	{
		return chargeAmount;
	}
	
	public String getDataUsed()
	{
		return dataUsed;
	}
	
	public String getErrorMessage()
	{
		return errorMessage;
	}
	
	public String getErrorMessageFor1x1Widget()
	{
		if(this.getErrorMessage() == null) { return ""; }
		return "Err: " + this.getLastUpdateDate();
	}
	
	public String getErrorMessageFor2x1Widget()
	{
		if(this.getErrorMessage() == null) { return ""; }
		return "Error: " + this.getLastUpdateDate();
	}
	
	public String getErrorMessageForApp()
	{
		if(this.getErrorMessage() == null) { return ""; }
		return "Error: "
				+ this.getErrorMessage() 
				+ ". Make sure you can login to your virgin mobile account "
				+ "at: https://www2.virginmobileusa.com";
	}
	
	String getLastUpdateDate()
	{
		return lastUpdateDate;
	}
	
	@SuppressWarnings("deprecation")
	Date getLastUpdateDateAsDate()
	{
		SimpleDateFormat format = new SimpleDateFormat("M/d h:mm a");
		Date parsed;
		try
		{
			parsed = format.parse(this.getLastUpdateDate());
		} 
		catch (ParseException e)
		{
			e.printStackTrace();
			return null;
		}
		
		Date d = new Date();
		d.setMonth(parsed.getMonth());
		d.setDate(parsed.getDate());
		d.setHours(parsed.getHours());
		d.setMinutes(parsed.getMinutes());
		d.setSeconds(parsed.getSeconds());
		
		return d;
	}
	
	public int getMinutesAvailableAsNumber()
	{
		int i = this.getMinutesUsed().lastIndexOf("/");
		if(i == -1) {return -1;}
		
		String s = this.getMinutesUsed().substring(i + 1, this.getMinutesUsed().length());
		return Integer.parseInt(s.trim());
	}
	public String getMinutesUsed()
	{
		return minutesUsed;
	}
	
	public int getMinutesUsedAsNumber()
	{
		int i = this.getMinutesUsed().lastIndexOf("/");
		if(i == -1) {return -1;}
		
		String s = this.getMinutesUsed().substring(0, i);
		return Integer.parseInt(s.trim());
	}
	
	public int getMinutesUsedAsProgress()
	{
		try
		{
			int minTotal = this.getMinutesAvailableAsNumber();
			int minUsed = this.getMinutesUsedAsNumber();
			float progress = (float) minUsed / (float) minTotal;
			progress = progress * 100;
			int result = Math.round(progress);
			return result;
		}
		catch(Exception e)
		{
			return 100;
		}
	}
	
	public String getNewMonthStarts()
	{
		return newMonthStarts;
	}
	
	public long getNewMonthStartsAsSeconds()
	{
		Pattern p = Pattern.compile("(\\d\\d)/(\\d\\d)/(\\d\\d)");
		Matcher m = p.matcher(this.getNewMonthStarts());
	    Calendar cal = null;
	    if (m.matches())
	    {
	    	cal = new GregorianCalendar(
	    		 Integer.parseInt("20" + m.group(3)),
	    		 Integer.parseInt(m.group(1)),
	    		 Integer.parseInt(m.group(2)),
	    		 23,
	    		 59);
	     }

	    if(cal != null)
	    {
	    	return cal.getTimeInMillis() / 1000;
	    }
	    return 0;
	}
	
	public int getNewMonthAsProgress()
	{
		Pattern p = Pattern.compile("(\\d\\d)/(\\d\\d)/(\\d\\d)");
		Matcher m = p.matcher(this.getNewMonthStarts());
	    if (m.matches())
	    {
	    	try
	    	{
	    		//DateFormat df = SimpleDateFormat.getDateInstance();
	            
		    	// End of the month
		    	Calendar endCal = new GregorianCalendar(
		    		 Integer.parseInt("20" + m.group(3)),
		    		 (Integer.parseInt(m.group(1)) - 1),
		    		 Integer.parseInt(m.group(2)),
		    		 23,
		    		 59);
		    	long end = endCal.getTimeInMillis();
		    	//String endString = df.format(endCal.getTime());
		    	
		    	// Start of the month
		    	Calendar startCal = (Calendar) endCal.clone();
		    	startCal.add(Calendar.MONTH, -1);
		    	startCal.set(Calendar.HOUR_OF_DAY, 0);
		    	startCal.set(Calendar.MINUTE, 0);
		    	long start = startCal.getTimeInMillis();
		    	//String startString = df.format(startCal.getTime());
		    	
		    	// Now (in the middle of the month)
		    	Calendar nowCal = new GregorianCalendar();
			    long now = nowCal.getTimeInMillis();
		    	//String nowString = df.format(nowCal.getTime());
		    	
			    long nowFromStart = now - start;
			    long endFromStart = end - start;
			    float lp = (nowFromStart / (float) endFromStart) * 100;
			    int percent = Math.round(lp);
			    return percent;
	    	}
	    	catch (Exception e)
	    	{
	    		return 100;
	    	}
	     }
	    
	    // not sure what to do if we can't parse it
	    return 100; 
	}
	

	public String getPersistentStringForFileVersion6()
	{
		return 
				this.getPersistentValue(phoneNumber) + "|---|" +
				this.getPersistentValue(pin) + "|---|" +
				this.getPersistentValue(lastUpdateDate) + "|---|" +
				this.getPersistentValue(minutesUsed) + "|---|" +
				this.getPersistentValue(balance) + "|---|" +
				this.getPersistentValue(chargeAmount) + "|---|" +
				this.getPersistentValue(newMonthStarts) + "|---|";
	}
	
	public String getPersistentStringForFileVersion7()
	{
		return 
				this.getPersistentValue(phoneNumber) + "|---|" +
				this.encodePinValue(pin) + "|---|" +
				this.getPersistentValue(lastUpdateDate) + "|---|" +
				this.getPersistentValue(minutesUsed) + "|---|" +
				this.getPersistentValue(balance) + "|---|" +
				this.getPersistentValue(chargeAmount) + "|---|" +
				this.getPersistentValue(newMonthStarts) + "|---|" +
				this.getPersistentValue(dataUsed);
	}
	
	public String getPersistentValue(String s)
	{
		if(s == null) { return " "; }
		if(s.equals("")) { return " "; }
		return s;
	}
	
	public String getPhoneEventToastString()
	{
		if(this.getErrorMessage() != null)
		{
			return this.getErrorMessage();
		}
		return "Monthly Min Used:  " + this.getMinutesUsed() +"\nBalance:  " + this.getBalance();
	}
	
	public String getPhoneNumber()
	{
		return phoneNumber;
	}

	public String getPhoneNumberFormatted()
	{
		if(phoneNumber.length() != 10)
		{
			return phoneNumber;
		}
		String formatted = String.format(
				"(%s) %s-%s", 
				phoneNumber.substring(0, 3), 
				phoneNumber.substring(3, 6), 
				phoneNumber.substring(6, 10));		
		return formatted;
	}

	public String getPin()
	{
		return pin;
	}
	
	public boolean isRefreshing()
	{
		return isRefreshing;
	}

	public void refreshFromWebsite()
	{
		this.setRefreshing(true);
		this.setErrorMessage(null);
		GsWebPageScraperApache scraper = new GsWebPageScraperApache();
		try
		{
			scraper.scrapeWebPage(this.phoneNumber, this.pin);
			this.setMinutesUsed(scraper.getMinutesUsed());
			this.setDataUsed(scraper.getDataUsed());
        	this.setBalance(scraper.getBalance());
        	this.setNewMonthStarts(scraper.getNewMonthStarts());
        	this.setChargeAmount(scraper.getChargeAmount());
        	this.setLastUpdateDateToNow();
		}
		catch (Exception e)
		{
			String message = e.getMessage();
			if(message == null)
			{ 
				//message = "Unknown error"; 
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				e.printStackTrace(pw);
				message = sw.toString();
			}
			this.setErrorMessage(message);
		}
		this.setRefreshing(false);
	}
	
	public boolean requiresRefreshForWidgets()
	{
		if(this.isRefreshing())
		{
			//Log.i(this.getClass().getSimpleName(), "NOT Refreshing because already refreshing");
			return false;
		}
		
		Date lastUpdate = this.getLastUpdateDateAsDate();
		if(lastUpdate == null)
		{
			//Log.i(this.getClass().getSimpleName(), "NOT Refreshing because null lastUpdate");
			return false;
		}
		
		Date now = new Date();
		long nowSecs = now.getTime() / 1000;
		long lastSecs = lastUpdate.getTime() / 1000;
		long secs = nowSecs - lastSecs;
		int hours = (int) (secs / 3600);
		
		// We might have updated it manually by the user opening the app
		// We might have updated it because the user made a call
		
		//Refresh account if we got an error last time and it has been 1 hr since then
		if((this.getErrorMessage() != null) && (this.getErrorMessage().equals("") == false))
		{
			//Log.i(this.getClass().getSimpleName(), "Refreshing because error message was: " + this.getErrorMessage());
			return true;
		}
		
		if(this.getMinutesUsed().equals(GsWebPageScraper.NOT_FOUND))
		{
			//Log.i(this.getClass().getSimpleName(), "Refreshing because minutes used reports as not found");
			return true;
		}
		
		// Refresh account if it has been 4 hours since the last update
		if(hours >= 6)
		{
			//Log.i(this.getClass().getSimpleName(), "Refreshing because hours was: " + hours);
			return true;
		}
		
		//Log.i(this.getClass().getSimpleName(), "NOT refreshing because hours was: " + hours);
		return false;
	}
	

	public void setBalance(String balance)
	{
		this.balance = balance;
	}

	public void setChargeAmount(String chargeAmount)
	{
		this.chargeAmount = chargeAmount;
	}

	public void setDataUsed(String dataUsed)
	{
		this.dataUsed = dataUsed;
	}

	public void setErrorMessage(String errorMessage)
	{
		this.errorMessage = errorMessage;
	}

	void setLastUpdateDate(String someDateString)
	{
		this.lastUpdateDate = someDateString;
	}

	void setLastUpdateDateToNow()
	{
		Date d = new Date();
		SimpleDateFormat format = new SimpleDateFormat("M/d h:mm a");
		this.setLastUpdateDate(format.format(d));
	}

	public void setMinutesUsed(String minutesUsed)
	{
		this.minutesUsed = minutesUsed;
	}

	public void setNewMonthStarts(String newMonthStarts)
	{
		this.newMonthStarts = newMonthStarts;
	}

	public void setPhoneNumber(String phoneNumber)
	{
		this.phoneNumber = phoneNumber;
	}
	
	public void setPin(String pin)
	{
		this.pin = pin;
	}
	
	public void setRefreshing(boolean isRefreshing)
	{
		this.isRefreshing = isRefreshing;
	}
}
