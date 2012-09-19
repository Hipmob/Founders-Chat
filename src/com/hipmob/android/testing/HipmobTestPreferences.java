package com.hipmob.android.testing;

import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.Preference.OnPreferenceChangeListener;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceClickListener;
import android.view.KeyEvent;
import android.widget.Toast;
import com.hipmob.android.HipmobRemoteConnection;

public class HipmobTestPreferences extends PreferenceActivity implements OnPreferenceChangeListener
{
	private SharedPreferences prefs;
	private Preference name, deviceName, email, server, context;
	private boolean changed;

	public static final Pattern EMAIL_ADDRESS
    = Pattern.compile(
        "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
        "\\@" +
        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
        "(" +
            "\\." +
            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
        ")+"
    );

	@Override
	public void onDestroy()
	{
		super.onDestroy();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.prefs);
		prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		
		Preference customPref;
		String value;
		changed = false;
		customPref = (Preference) findPreference(getString(R.string.pref_version));
		try {
		    String componentName = getPackageName();
		    //ApplicationInfo ai = getPackageManager().getApplicationInfo(componentName, PackageManager.GET_META_DATA);
		    PackageInfo pi = getPackageManager().getPackageInfo(componentName, PackageManager.GET_META_DATA);
		    customPref.setSummary(pi.versionName);
		    /*
		    customPref.setOnPreferenceClickListener(new OnPreferenceClickListener(){
				public boolean onPreferenceClick(Preference preference) {
					showWhatsNew();
					return true;
				}
			});
			*/
		} catch (Exception e) {
			
		}
						
		customPref = (Preference) findPreference(getString(R.string.pref_online));
		customPref.setOnPreferenceClickListener(new OnPreferenceClickListener(){
			public boolean onPreferenceClick(Preference preference) {
				openOnline();
				return true;
			}
		});
		
		// override all the summaries
		name = (Preference) findPreference(getString(R.string.pref_name));
		if(name != null){
			value = prefs.getString(getString(R.string.pref_name), null);
			if(value != null) name.setSummary(value);
			name.setOnPreferenceChangeListener(this);
		}
		
		email = (Preference) findPreference(getString(R.string.pref_email));
		if(email != null){
			value = prefs.getString(getString(R.string.pref_email), null);
			if(value != null) email.setSummary(value);
			email.setOnPreferenceChangeListener(this);
		}
		
		context = (Preference) findPreference(getString(R.string.pref_context));
		if(context != null){
			value = prefs.getString(getString(R.string.pref_context), null);
			if(value != null) context.setSummary(value);
			context.setOnPreferenceChangeListener(this);
		}
	}
	
	void openOnline()
	{
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setData(Uri.parse(getString(R.string.label_pref_online_uri)));
		startActivity(i);
	}
	
	@Override
    protected void onResume() {
        super.onResume();
        setTitle(getString(R.string.label_preferences_title));
	}
	
	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue)
	{
		if(preference == name){
			String value = newValue.toString().trim();
			preference.setSummary(value);
			savePreference(preference, value);
			
			Toast.makeText(this, getString(R.string.message_name_updated), Toast.LENGTH_LONG).show();
			
			// update the user's name in the chat
			HipmobRemoteConnection.updateName(this, value);
		}else if(preference == email){
			// validate that it is an email address
			String value = newValue.toString().trim();
			if("".equals(value) || EMAIL_ADDRESS.matcher(value).matches()){
				preference.setSummary(value);
				savePreference(preference, value);
				Toast.makeText(this, getString(R.string.message_email_updated), Toast.LENGTH_LONG).show();
			}else{
				Toast.makeText(this, getString(R.string.error_invalid_email), Toast.LENGTH_LONG).show();
			}
		}else if(preference == context){
			String value = newValue.toString().trim();
			preference.setSummary(value);
			savePreference(preference, value);
			
			Toast.makeText(this, getString(R.string.message_context_updated), Toast.LENGTH_LONG).show();
			
			// update the user's name in the chat
			HipmobRemoteConnection.updateContext(this, value);
		}
		return false;
	}
	
	void savePreference(Preference preference, String value)
	{
		SharedPreferences.Editor edit = prefs.edit();
		edit.putString(preference.getKey(), value);
		edit.commit();
		changed = true;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
	    if (keyCode == KeyEvent.KEYCODE_BACK && 
	    		event.getRepeatCount() == 0){
	    	// do something on back.
	    	if(changed){
	    		/*
	    		Intent i = new Intent();
	    		i.setAction(App.NOTICE_PREFERENCES_CHANGED);
	    		i.putExtra(Intent.EXTRA_INTENT, App.UPDATE_PREFERENCES);
	    		sendBroadcast(i);
	    		
	    		*/
	    	}
	    }
	    return super.onKeyDown(keyCode, event);
	}
}
