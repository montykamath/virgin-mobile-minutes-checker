package com.goodstart.minutescheckerlib;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.conn.ssl.AllowAllHostnameVerifier;

public class GsWebPageScraperClassic extends GsWebPageScraper
{
	@Override
	void getWebPageContents(String phoneNumber, String pin) throws MalformedURLException, IOException
	{
		this.prepareConnection();
		String url = "https://www2.virginmobileusa.com/login/login.do" +
				"?min=" + phoneNumber +
				"&vkey=" + pin +
				"&submit=submit" +
				"&loginRoutingInfo=https://www2.virginmobileusa.com:443/myaccount/home.do";
		
		HttpsURLConnection connection = (HttpsURLConnection) new URL(url).openConnection();
		//connection.addRequestProperty("Cookie", "u_cst=3");
		connection.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
		connection.addRequestProperty("Accept-Encoding", "gzip");
		//connection.addRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");
        ((HttpsURLConnection) connection).setHostnameVerifier(new AllowAllHostnameVerifier());
        connection.setInstanceFollowRedirects(true);
        InputStream iStream = (InputStream) connection.getContent();
        GZIPInputStream inStream = new GZIPInputStream(iStream);
        InputStreamReader in = new InputStreamReader(inStream);

        BufferedReader buff = new BufferedReader(in);
        StringBuilder sb = new StringBuilder();		
		String line = "";
		//String lineSep = System.getProperty("line.separator");
		while ((line = buff.readLine()) != null)
		{
			sb.append(line);
			//sb.append(lineSep);
		}
		connection.disconnect();
		this.mainPageContents = sb.toString();
	}
	
	void prepareConnection()
	{
		TrustManager[] trustAllCerts = new TrustManager[]{
				new X509TrustManager() {
					public void checkClientTrusted(
							java.security.cert.X509Certificate[] certs, String authType) {
					}
					public void checkServerTrusted(
							java.security.cert.X509Certificate[] certs, String authType) {
					}
					public java.security.cert.X509Certificate[] getAcceptedIssuers() {
						return null;
					}
				}
		};

		try {
			SSLContext sc = SSLContext.getInstance("TLS");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (Exception e) {
			e.getMessage();
		}
	}
}
