package com.goodstart.minutescheckerlib;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GsWebPageScraper
{
	public static final String NOT_FOUND = "not found";
	String mainPageContents, dataPageContents;
	String minutesUsed, dataUsed, balance, newMonthStarts, chargeAmount, error;
	Pattern errorPattern = Pattern.compile("<p class=\"error\">([\\s\\S]*?)<");
	Pattern chargeAmountPattern = Pattern.compile("Next Month's Charge</h3><p>([\\s\\S]*?)<");
	Pattern minUsedPattern = Pattern.compile("<p id=\"remaining_minutes\">\\s*<strong>(\\d*)</strong>\\s*/\\s*(\\d*)<");
	Pattern balancePattern = Pattern.compile("Current Balance</h3><p>([\\s\\S]*?)<");
	Pattern newMonthStartsPattern = Pattern.compile("New Month Starts([\\s\\S]*?)</span>");
	Pattern dataUsedPattern = Pattern.compile("MB Used:[\\s\\S]*?</th>[\\s\\S]*?<td>([\\s\\S]*?)<");

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

	public String getError()
	{
		return error;
	}

	public String getMinutesUsed()
	{
		return minutesUsed;
	}

	public String getNewMonthStarts()
	{
		return newMonthStarts;
	}
	void getWebPageContents(String phoneNumber, String pin) throws MalformedURLException, IOException
	{
		// TODO Auto-generated method stub
		
	}
	
	void scrapeBalance()
	{
		Matcher matcher = balancePattern.matcher(mainPageContents);
		String balance = NOT_FOUND;
		if (matcher.find())
		{
			balance = matcher.group(1);
		}
		this.setBalance(balance.trim());
	}
	
	void scrapeCharge()
	{
		Matcher matcher = chargeAmountPattern.matcher(mainPageContents);
		String charge = NOT_FOUND;
		if (matcher.find())
		{
			charge = matcher.group(1);
		}
		this.setChargeAmount(charge.trim());
	}

	void scrapeDataUsed()
	{
		Matcher matcher = dataUsedPattern.matcher(dataPageContents);
		String dataUsed = NOT_FOUND;
		if (matcher.find())
		{
			dataUsed = matcher.group(1);
		}
		this.setDataUsed(dataUsed.trim());
	}

	void scrapeError() throws Exception
	{
		Matcher matcher = errorPattern.matcher(mainPageContents);
		if (matcher.find())
		{
			this.setError(matcher.group(1).trim());
			throw new Exception(this.getError());
		}
	}
	
	void scrapeMinutesUsed()
	{
		Matcher matcher = minUsedPattern.matcher(mainPageContents);
		String usedMin = NOT_FOUND;
		if (matcher.find())
		{
			usedMin = matcher.group(1) + " / " + matcher.group(2);
		}
		this.setMinutesUsed(usedMin.trim());
	}
	
	void scrapeNewMonthStarts()
	{
		Matcher matcher = newMonthStartsPattern.matcher(mainPageContents);
		String newMonthStarts = NOT_FOUND;
		if (matcher.find())
		{
			newMonthStarts = matcher.group(1);
		}
		this.setNewMonthStarts(newMonthStarts.trim());
	}
	
	
	public void scrapeWebPage(String phoneNumber, String pin) throws Exception
	{
		// Hack for testing
		if(phoneNumber.equals("0000000000"))
		{
			this.setMinutesUsed("64 / 300");
			this.setDataUsed("2.45 MB");
			this.setNewMonthStarts("05/20/13");
			this.setChargeAmount("$35");
			this.setBalance("$0.00");
			Thread.sleep(3000);
			return;
		} 
		else if(phoneNumber.equals("0008675309"))
		{
			this.setMinutesUsed("128 / 300");
			this.setDataUsed("40.4 MB");
			this.setNewMonthStarts("06/25/13");
			this.setChargeAmount("$35");
			this.setBalance("$0.00");
			Thread.sleep(3000);
			return;
		}
		else if(phoneNumber.equals("0005552368"))
		{
			this.setMinutesUsed("120 / 300");
			this.setDataUsed("102.65 MB");
			this.setNewMonthStarts("05/29/13");
			this.setChargeAmount("$35");
			this.setBalance("$0.00");
			Thread.sleep(3000);
			return;
		}

		this.getWebPageContents(phoneNumber, pin);
		
		this.mainPageContents = this.mainPageContents.replace("&nbsp;", " ");
		this.scrapeError();
		this.scrapeMinutesUsed();
		this.scrapeBalance();
		this.scrapeCharge();
		this.scrapeNewMonthStarts();
		this.scrapeDataUsed();
		
		return;
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
	
	public void setError(String error)
	{
		this.error = error;
	}
	
	public void setMinutesUsed(String minutesUsed)
	{
		this.minutesUsed = minutesUsed;
	}

	public void setNewMonthStarts(String newMonthStarts)
	{
		this.newMonthStarts = newMonthStarts;
	}
}
