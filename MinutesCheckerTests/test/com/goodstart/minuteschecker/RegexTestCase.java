package com.goodstart.minuteschecker;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.TestCase;

public class RegexTestCase extends TestCase
{
	Pattern dataUsedPattern = Pattern.compile("MB Used:[\\s\\S]*?</th>[\\s\\S]*?<td>([\\s\\S]*?)<");
	Pattern errorPattern = Pattern.compile("<p class=\"error\">([\\s\\S]*?)<");
	Pattern chargePattern = Pattern.compile("Next Month's Charge</h3><p>([\\s\\S]*?)<");
	Pattern minUsedPattern = Pattern.compile("<p id=\"remaining_minutes\">\\s*<strong>(\\d*)</strong>\\s*/\\s*(\\d*)<");
	Pattern balancePattern = Pattern.compile("Current Balance</h3><p>([\\s\\S]*?)<");
	Pattern newMonthStartsPattern = Pattern.compile("New Month Starts([\\s\\S]*?)</span>");
	// Pattern chargeDatePattern = Pattern.compile("<h3>You will be charged on</h3><p>([\\s\\S]*?)</p>");
	// Pattern dueDatePattern = Pattern.compile("<h3>Date Due</h3><p>05/26/13</p>");

	public void testMinUsedRegex()
	{
		String s = "<div class=\"col-md-3\" id=\"account_minutes\">\n<h3>Anytime Minutes Used</h3>\n<p id=\"remaining_minutes\">\n<strong>140</strong>&nbsp;/&nbsp;300</p>\n</div>\n<div class=\"col-md-6\" id=\"account_plan\">\n<h3>Your Beyond Talk Plan</h3>\n<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\">\n<tr>\n<th>Price</th>\n<td>$35.00/mo.";
		s = s.replace("&nbsp;", " ");
		Matcher m = minUsedPattern.matcher(s);
		String usedMin = null;
		String totalMin = null;
		if (m.find())
		{
			usedMin = m.group(1);
			totalMin = m.group(2);
		}
		assertEquals(usedMin, "140");
		assertEquals(totalMin, "300");
	}
	
	public void testDataUsedRegex()
	{
		String s = "<td>Unlimited  MB</td></tr><tr><th style=\"width:120px\">MB Used: </th><td>2.65 MB                         \n       </td></tr>";
		Matcher m = dataUsedPattern.matcher(s);
		String found = null;
		if (m.find())
		{
			found = m.group(1).trim();
		}
		assertEquals(found, "2.65 MB");
	}

	public void testBalance()
	{
		String s = "some text <h3>Current Balance</h3><p>$0.00</p> some more text";
		Matcher m = balancePattern.matcher(s);
		String found = null;
		if (m.find())
		{
			found = m.group(1);
		}
		assertEquals(found, "$0.00");
	}

	public void testNewMonthStarts()
	{
		String s = "some text \nNew Month Starts\n05/19/13</span>";
		Matcher m = newMonthStartsPattern.matcher(s);
		String found = null;
		if (m.find())
		{
			found = m.group(1).trim();
		}
		assertEquals(found, "05/19/13");
	}

	public void testCharge()
	{
		String s = "some text <h3>Next Month's Charge</h3><p>\n$35.00</p> some more text";

		Matcher m = chargePattern.matcher(s);
		String found = null;
		if (m.find())
		{
			found = m.group(1).trim();
		}
		assertEquals(found, "$35.00");
	}
	
	public void testChargeWith5DollarDiscount()
	{
		String s = "some text <h3>Next Month's Charge</h3><p>\n$30.00<a style=\"22px;width:56px;background:none;text-indent:0px;\" class=\"discount-applied\" rel=\"#discounteDetails_applied\" href=\"#discountDetails_applied\"><img style=\"vertical-align:middle;\" src=\"https://www.vm.com/_img/5dollar.png\" /></a> some more text";

		Matcher m = chargePattern.matcher(s);
		String found = null;
		if (m.find())
		{
			found = m.group(1).trim();
		}
		assertEquals(found, "$30.00");
	}
	
	public void testError()
	{
		String s = "<p class=\"error\">There was an error<p>";

		Matcher m = errorPattern.matcher(s);
		String found = null;
		if (m.find())
		{
			found = m.group(1).trim();
		}
		assertEquals(found, "There was an error");
	}
}
