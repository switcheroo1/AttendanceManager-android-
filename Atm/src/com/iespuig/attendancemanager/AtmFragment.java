/*
 *    This file is part of AM_Android.
 *    
 *    Copyright (C) 2015 Alex Visiedo
 *    Copyright (C) 2015 Gonzalo Collado
 *
 *    AM_Android is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 +    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    AM_Android is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details. 
 *
 *    You should have received a copy of the GNU General Public License
 *    along with AM_Android.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.iespuig.attendancemanager;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.iespuig.attendancemanager.R;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.XmlResourceParser;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;


public class AtmFragment extends Fragment {
  private static final String TAG ="AtmFragment";
  private static final String DIALOG_LOGIN ="login";
  private static final int REQUEST_LOGIN = 0;
  
  private ImageButton mLoginButton;
  private TextView mLoginTxt;
  private ProgressBar mProgressBar;
  private SavedUser mSavedUser ;
  
  private class SavedUser{
    String login;
    String password;
    String fullName;

    SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = sharedPref.edit();

    SavedUser(){
      if(sharedPref.getString("savedLogin", "").isEmpty()){
        deleteSavedUser();
      }
      login = sharedPref.getString("savedLogin", "");
      password = sharedPref.getString("savedPassword", "");
      fullName = sharedPref.getString("savedFullname", "");
    }

    public boolean isEmpty(){
      return login.isEmpty();
    }

    public String getLogin() {
      return login;
    }

    public String getPassword() {
      return password;
    }
    
    public String getFullname() {
      return fullName;
    }

    public void setSavedUser() {
      this.password = User.getInstance().getPassword();
      this.login = User.getInstance().getLogin();
      this.fullName = User.getInstance().getFullname();
      
      editor.putString("savedLogin", login);
      editor.putString("savedFullname", fullName);
      editor.putString("savedPassword", password);
      editor.commit();
    }
    
    public void deleteSavedUser() {
      this.password = "";
      this.login    = "";
      this.fullName = "";
        
      editor.putString("savedLogin", "");
      editor.putString("savedFullname", "");
      editor.putString("savedPassword", "");
      editor.commit();
    }
  }
  
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
    setHasOptionsMenu(true);
    mSavedUser = new SavedUser();
    
    SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getActivity());
    if (SP.getString("urlServer", "").isEmpty()) {
      checkDeploymentValues();
    }
  }
    
  private void checkDeploymentValues()  {
    XmlResourceParser xpp = getActivity().getResources().getXml(R.xml.deployment);
    String deploymentURL = "";
    String deploymentSchool = "";
      
    try {
      xpp.next();
      int eventType = xpp.getEventType();
      
      while (eventType != XmlPullParser.END_DOCUMENT) {
        if (eventType == XmlResourceParser.START_TAG) {
          String s = xpp.getName();
          if (s.equals("deployment_url")) {
            eventType = xpp.next();
            deploymentURL=xpp.getText();
          }
          if (s.equals("deployment_school")) {
            eventType = xpp.next();
            deploymentSchool=xpp.getText();
          }
        }
        eventType = xpp.next();
      }
    } catch (XmlPullParserException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getActivity());
    SharedPreferences.Editor editor = SP.edit();
    editor.putString("urlServer", deploymentURL);
    editor.putString("schoolName", deploymentSchool);
    editor.commit();
  }  
  
  @Override
  public void onResume() {
    super.onResume();
    
    SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getActivity());
    boolean savedUser = SP.getBoolean(("saveUser"), false);
  
    if (!mSavedUser.isEmpty() && savedUser)
      mLoginTxt.setText(mSavedUser.getFullname());
    else
      mLoginTxt.setText(R.string.login_txt);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup parent,
      Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.fragment_atm, parent, false);
  
    SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getActivity());
    boolean savedUser = SP.getBoolean(("saveUser"), false);
  
    mLoginButton =(ImageButton) v.findViewById(R.id.imageBotonLogin);
    mLoginTxt = (TextView) v.findViewById(R.id.txtUsuari);
    
    if (!mSavedUser.isEmpty() && savedUser)
      mLoginTxt.setText(mSavedUser.getFullname());
    else
      mLoginTxt.setText(R.string.login_txt);
  
    mProgressBar = (ProgressBar) v.findViewById(R.id.progressBarLogin);
    mProgressBar.setVisibility(View.INVISIBLE);
    
    mLoginButton.setOnClickListener(new View.OnClickListener() {
      @Override
     
      public void onClick(View v) {
	    SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean savedUser = SP.getBoolean(("saveUser"), false);
    	
        if(mSavedUser.isEmpty() || savedUser == false){
        	FragmentManager fm = getFragmentManager();
            LoginFragment dialog = new LoginFragment();
            dialog.setTargetFragment(AtmFragment.this, REQUEST_LOGIN);
            dialog.show(fm,DIALOG_LOGIN);
        }else{
        	mProgressBar.setVisibility(View.VISIBLE);
            new FetchUserTask().execute(mSavedUser.getLogin(), mSavedUser.getPassword());
        }
      }
    });
    
    return v;
  }
  
  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (resultCode != Activity.RESULT_OK) return;
    if (requestCode == REQUEST_LOGIN) {
      String user = (String) data.getSerializableExtra(LoginFragment.EXTRA_USER);
      String pass = (String) data.getSerializableExtra(LoginFragment.EXTRA_PASS);
      mProgressBar.setVisibility(View.VISIBLE);
      new FetchUserTask().execute(user,pass);
    }
  }
  
  private class FetchUserTask extends AsyncTask<String, Void, Boolean> {
    @Override
    protected Boolean doInBackground(String... params) {
      return new UserFetchr(getActivity()).fetchUser(params[0], params[1]);
    }
    
    @Override
   	protected void onProgressUpdate(Void... values) {
   		mProgressBar.setVisibility(View.VISIBLE);
   		super.onProgressUpdate(values);
   	}
    
    protected void onPostExecute(Boolean result) {
      Log.i(TAG, "Validate user " + result);
      mProgressBar.setVisibility(View.INVISIBLE);
      if (result) {
    	SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean savedUser = SP.getBoolean(("saveUser"), false);
      	
        if(savedUser)
      		mSavedUser.setSavedUser();
      	else{
          mSavedUser.deleteSavedUser();
        }
        
        Intent i = new Intent(getActivity(), ClassblockListActivity.class);
        startActivity(i);
      }
      else
        mLoginTxt.setText(R.string.login_error); 
    }
  }
  
  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.atm, menu);
  }
  
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // handle item selection
    switch (item.getItemId()) {
      case R.id.action_settings:
         Intent i = new Intent(getActivity(), PreferencesActivity.class);
         startActivity(i);
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  } 
}
