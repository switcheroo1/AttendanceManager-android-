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

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

public class UserFetchr {
  public static final String TAG = "UserFetchr";
  private static final String ACTION = "getTeacher";
  
  protected Context context;
  
  public UserFetchr(Context context) {
    this.context=context.getApplicationContext();
  }
  
  public Boolean fetchUser(String login, String password)  {
    SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(context);
    String urlServer = SP.getString("urlServer","");
    String schoolName = SP.getString("schoolName", "");
    
    try {
      String url = Uri.parse(urlServer).buildUpon()
        .appendQueryParameter("action", ACTION)
        .appendQueryParameter("school", schoolName)
        .appendQueryParameter("login", login)
        .appendQueryParameter("password", password)    
        .build().toString();
      
      Log.i(TAG, "url: " + url);
      
      String data = AtmNet.getUrl(url);
      
      Log.i(TAG, "Received json: " + data);
      
      JSONObject jsonObject = new JSONObject(data);
      
      if (jsonObject.has("error")) {
        return false;
      } else {
        User.getInstance().setFullname(jsonObject.getString("fullName"));
        User.getInstance().setLogin(jsonObject.getString("login"));
        User.getInstance().setPassword(password);
        User.getInstance().setId(jsonObject.getInt("id"));
      }
    } catch (IOException ioe) {
      Log.e(TAG, "Failed to fetch items", ioe);
      return false;
    } catch (JSONException je) {
      Log.e(TAG, "Failed to parse JSON", je);
      return false;
    }
    return true;
  }
}