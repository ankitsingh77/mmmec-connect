package com.quipmate2.constants;

import org.apache.http.impl.client.DefaultHttpClient;

public class AppProperties {

	public static final DefaultHttpClient appUserClient = new DefaultHttpClient();
	public static final String PROD_URL = "http://www.quipmate.com/ajax/write.php";
	public static final String DEV_URL = "http://www.quipmate.com/ajax/write.php";
	public static final String URL = PROD_URL;
	public static final String DATABASE = "database";
	public static final	String ALUMNIDB = "mmmut";
	public static final String PDF_ICON = "http://372a66a66bee4b5f4c15-ab04d5978fd374d95bde5ab402b5a60b.ssl.cf2.rackcdn.com/pdf.ico";

	public static final String PARAM_EMAIL = "email";
	public static final String PARAM_PASSWORD = "password";

	public static final String PROFILE_ID = "profileid";
	public static final String SESSION_ID = "sessionid";
	public static final String MY_PROFILE_ID = "myprofileid";
	
	public static final String MY_PROFILE_NAME = "name";
	public static final String MY_PROFILE_PIC = "photo";
	
	public static final String SESSION_NAME = "session_name";
	public static final String METHOD_POST = "POST";
	public static final String METHOD_GET = "GET";
	public static final String METHOD_HEAD = "HEAD";
	public static final String LOGIN = "login";
	public static final String ACTION = "action";
	public static final String WRONG_CREDENTIAL_CODE = "37";
	public static final String ACK = "ack";
	public static final String ACK_CODE = "1";
	public static final String START="start";
	public static final String FRIEND_COUNT="count";
	public static final String PAGE="page";
	
	// json objects obtains from friend fetch
	public static final String NAME = "name";
	public static final String PROFILE_IMAGE = "pimage";
	
}
