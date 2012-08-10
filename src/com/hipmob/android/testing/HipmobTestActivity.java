package com.hipmob.android.testing;

import com.hipmob.android.HipmobCore;

import java.util.UUID;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class HipmobTestActivity extends Activity 
{
	/*
	 * TODO: replace this key with your own to connect with your Hipmob account. The default key connects with the Hipmob founders.
	 */
	public static final String HIPMOB_KEY = "7152ce24a16d42eb8d30b5fe4c01f911";
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		// create an intent
		Intent i = new Intent(this, HipmobCore.class);
		
		// REQUIRED: set the appid to the key you're provided
		i.putExtra(HipmobCore.KEY_APPID, HIPMOB_KEY);
		
		// provide a device identifier here (for use with API calls and for peer-to-peer connections)
		i.putExtra(HipmobCore.KEY_DEVICEID, getDeviceID());
		
		// set the user's name here (will show up in the chat status)
		//i.putExtra(HipmobCore.KEY_NAME, "Stranger");
		
		// put the user's email here (will show up in the chat status)
		//i.putExtra(HipmobCore.KEY_EMAIL, "usersemail@somehost.com");
		
		// you can set a location string here or geolocation co-ordinates (location string will show up in the chat status)
		//i.putExtra(HipmobCore.KEY_LOCATION, "Mountain View, CA");
		//i.putExtra(HipmobCore.KEY_LATITUDE, 37.423105);
		//i.putExtra(HipmobCore.KEY_LONGITUDE, -122.082399);
		
		// you can add extra context, such as the place where the user came from (will show up in the chat status) 
		//i.putExtra(HipmobCore.KEY_CONTEXT, "Reaching out from the test app!");
		
		// you can set the message that displays if a user connects and no admins are available
		//i.putExtra(HipmobCore.KEY_AWAY_NOTICE, "No one is home right now. Send us an email!");
		
		// you can set the message that displays if a user connects and an admin is available right now
		//i.putExtra(HipmobCore.KEY_PRESENT_NOTICE, "We're right here. Talk to us!");
		
		// launch the chat window
		startActivity(i);
		
		// in the test app, we finish immediately: we don't really do anything else!
		finish();
	}
	
	private String getDeviceID()
	{
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		
		if(prefs.contains("deviceid")) return prefs.getString("deviceid", "");
		
		// create one from scratch
		String deviceid = UUID.randomUUID().toString();
		
		// save it
		SharedPreferences.Editor edit = prefs.edit();
		edit.putString("deviceid", deviceid);
		edit.commit();
		
		// return it
		return deviceid;
	}
}
