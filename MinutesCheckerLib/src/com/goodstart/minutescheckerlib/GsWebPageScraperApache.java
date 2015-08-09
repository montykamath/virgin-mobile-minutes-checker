package com.goodstart.minutescheckerlib;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

public class GsWebPageScraperApache extends GsWebPageScraper
{

	@Override
	void getWebPageContents(String phoneNumber, String pin) throws ClientProtocolException, IOException
	{ 
		DefaultHttpClient client = new DefaultHttpClient();
		try
		{
			String mainContents = this.getMainPageContents(phoneNumber, pin, client);
			String dataContents = this.getDataWebPageContents(client);
			this.mainPageContents = mainContents;
			this.dataPageContents = dataContents;
		}
		finally
		{
			client.getConnectionManager().shutdown();
		}
		
	}
	
	String getMainPageContents(String phoneNumber, String pin, DefaultHttpClient client) throws ClientProtocolException, IOException
	{
		HttpPost method = new HttpPost("https://www2.virginmobileusa.com/login/login.do");
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
        nameValuePairs.add(new BasicNameValuePair("min",phoneNumber));
        nameValuePairs.add(new BasicNameValuePair("vkey",pin));
        nameValuePairs.add(new BasicNameValuePair("submit","submit"));
        nameValuePairs.add(new BasicNameValuePair("loginRoutingInfo","https://www2.virginmobileusa.com:443/myaccount/home.do"));
        method.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        
        return this.executeHttpMethod(client, method);
	}

	String executeHttpMethod(DefaultHttpClient client, HttpRequestBase method) throws IOException, ClientProtocolException
	{
		method.setHeader("Accept-Language","en-us,en;q=0.5");
        method.setHeader("Accept-Encoding","gzip,deflate");
		HttpResponse response = client.execute(method);
		InputStream inStream = (InputStream) response.getEntity().getContent();
		StringBuilder sb = new StringBuilder();	
		try
		{
		    GZIPInputStream gStream = new GZIPInputStream(inStream);
		    InputStreamReader in = new InputStreamReader(gStream);
		    BufferedReader buff = new BufferedReader(in, 1024*4);
			String line = "";
			while ((line = buff.readLine()) != null)
			{
				sb.append(line);
			}
		}
		catch (IOException ex)
		{
	        // In case of an IOException the connection will be released
	        // back to the connection manager automatically
			throw ex;
		} 
		catch (RuntimeException ex)
		{
	        // In case of an unexpected exception you may want to abort
	        // the HTTP request in order to shut down the underlying
	        // connection immediately.
			method.abort();
			throw ex;
		} 
		finally
		{
	        // Closing the input stream will trigger connection release
	        try { inStream.close(); } catch (Exception ignore) {}
		}
		String contents = sb.toString();
		return contents;
	}
	
	String getDataWebPageContents(DefaultHttpClient client) throws IllegalStateException, IOException
	{
		HttpGet method = new HttpGet("https://www2.virginmobileusa.com/myaccount/dataPlanHistory.do");
		method.setHeader("Accept-Language","en-us,en;q=0.5");
		method.setHeader("Accept-Encoding","gzip,deflate");
		
		return this.executeHttpMethod(client, method);
	}

}
