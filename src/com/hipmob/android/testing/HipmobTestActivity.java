package com.hipmob.android.testing;

import com.hipmob.android.HipmobCore;
import com.hipmob.android.HipmobPendingMessageListener;
import com.hipmob.android.HipmobRemoteConnection;

import java.util.UUID;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

public class HipmobTestActivity extends TabActivity 
{
	/*
	 * TODO: replace this key with your own to connect with your Hipmob account. The default key connects with the Hipmob founders.
	 */
	public static final String HIPMOB_KEY = "7152ce24a16d42eb8d30b5fe4c01f911";
	//public static final String HIPMOB_KEY = "488b7ecc3a764176b50717278c6a9ea0";
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		// remove the title bar
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		
		// load the content view
		setContentView(R.layout.main);
		
		// create an intent
		Intent i = new Intent(this, HipmobView.class);
		
		// REQUIRED: set the appid to the key you're provided
		i.putExtra(HipmobCore.KEY_APPID, HIPMOB_KEY);
		
		// provide a device identifier here (for use with API calls and for peer-to-peer connections)
		i.putExtra(HipmobCore.KEY_DEVICEID, getDeviceID());
		
		// 	set the user's name here (will show up in the chat status)
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		if(prefs.contains(getString(R.string.pref_name))){
			i.putExtra(HipmobCore.KEY_NAME, prefs.getString(getString(R.string.pref_name), "Jack Hawksmoor"));
		}
		
		// put the user's email here (will show up in the chat status)
		if(prefs.contains(getString(R.string.pref_email))){
			i.putExtra(HipmobCore.KEY_EMAIL, prefs.getString(getString(R.string.pref_email), "jack@theauthority.com"));
		}
		
		// put context information about the user (such as the activity that 
		// launched the chat window) here (will show up in the chat status)
		if(prefs.contains(getString(R.string.pref_context))){
			i.putExtra(HipmobCore.KEY_CONTEXT, prefs.getString(getString(R.string.pref_context), ""));
		}
		
		// you can set a location string here or geolocation co-ordinates (location string will show up in the chat status)
		//i.putExtra(HipmobCore.KEY_LOCATION, "Mountain View, CA");
		//i.putExtra(HipmobCore.KEY_LATITUDE, 37.423105);
		//i.putExtra(HipmobCore.KEY_LONGITUDE, -122.082399);
				
		// you can set the message that displays if a user connects and no admins are available
		//i.putExtra(HipmobCore.KEY_AWAY_NOTICE, "No one is home right now. Send us an email!");
		
		// you can set the message that displays if a user connects and an admin is available right now
		//i.putExtra(HipmobCore.KEY_PRESENT_NOTICE, "We're right here. Talk to us!");
		
		// and load up the tabs
		final TabHost tabHost = getTabHost();

		// the preferences window
		tabHost.addTab(tabHost.newTabSpec("tab1")
				.setIndicator(getString(R.string.indicator_settings), getResources().getDrawable(R.drawable.preferences))
                .setContent(new Intent(this, HipmobTestPreferences.class)));

		// the chat window
		tabHost.addTab(tabHost.newTabSpec("tab2")
                .setIndicator(getString(R.string.indicator_chat), getResources().getDrawable(R.drawable.chat))
                .setContent(i));
		
		tabHost.setCurrentTab(0);
		
		checkHipmobMessages();
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
	
	void checkHipmobMessages()
	{
		HipmobRemoteConnection.checkPendingMessages(this, HIPMOB_KEY, 
				getDeviceID(),  
				new HipmobPendingMessageListener(){
			@Override
			public void pendingMessageCount(int count) {
				final int cnt = count;
				getTabHost().post(new Runnable(){
					public void run(){
						updateMessageCount(cnt);
					}
				});
			}

			@Override
			public void pendingMessageLookupFailed() {
				getTabHost().post(new Runnable(){
					public void run(){
						updateMessageCountFailed();
					}
				});
			}
		});
	}
	
	private void updateMessageCountFailed() 
	{
		//Toast.makeText(this, "Failed to get message count!", Toast.LENGTH_LONG).show();
	}
	
	private void updateMessageCount(int count)
	{
		// update the tab icon
		if(count > 0){
			Drawable base = getResources().getDrawable(R.drawable.chat);
			float density = getResources().getDisplayMetrics().density;
			int h = (int)(density*base.getIntrinsicHeight()), w = (int)(density*base.getIntrinsicWidth());
			Bitmap bm = Bitmap.createBitmap(w, h, Config.ARGB_8888);
			Canvas canvas = new Canvas(bm); 
			base.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
			base.draw(canvas);
			
			// add the rounded rect
			RectF box = new RectF(w/2, 0, w, h/2);
			Paint bgpaint = new Paint();
			bgpaint.setColor(0xffFF0000);
			canvas.drawRoundRect(box, 5, 5, bgpaint);
			
			// draw the text
			String val = String.valueOf(count);
			if(count >= 100) val = "99";
			Paint textpaint = new Paint();
			textpaint.setColor(Color.WHITE);
			if(val.length() == 2) textpaint.setTextSize(box.height()-10);
			else textpaint.setTextSize(box.height()-6);
			textpaint.setAntiAlias(true);
			textpaint.setFakeBoldText(true);
			textpaint.setShadowLayer(6f, 0, 0, Color.BLACK);
			textpaint.setStyle(Paint.Style.FILL);
			textpaint.setTextAlign(Paint.Align.CENTER);
			float textheight = Math.abs(textpaint.descent()) + Math.abs(textpaint.ascent());
			float vmargin = (box.height() - textheight) / 2;
			float hmargin = 0;
			if(val.length() == 2) hmargin = -2;
			canvas.drawText(String.valueOf(count), hmargin+box.left + (box.right - box.left)/2,
					box.bottom - vmargin - textpaint.descent(),
					textpaint);
			ImageView icon = (ImageView) getTabHost().getTabWidget().getChildAt(1).findViewById(android.R.id.icon);
			icon.setImageBitmap(bm);
		}else{
			ImageView icon = (ImageView) getTabHost().getTabWidget().getChildAt(1).findViewById(android.R.id.icon);
			icon.setImageResource(R.drawable.chat);
		}
	}
}
