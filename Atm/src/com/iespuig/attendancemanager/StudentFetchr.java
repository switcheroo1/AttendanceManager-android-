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
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

@SuppressLint("SimpleDateFormat")
public class StudentFetchr implements MissValues {
  public static final String TAG = "StudentFetch";
  private static final String ACTION_GET_STUDENTS = "getStudentsOfGroupMisses";
  private static final String ACTION_ADD_MISS = "addMiss";
  private static final String ACTION_DELETE_MISS = "deleteMiss";
  private static final String ACTION_CHECKED_CHECKLIST = "checkedList";
  private static final String ACTION_UNCHECKED__CHECKLIST = "uncheckedList";
  protected Context context;
  
  public StudentFetchr(Context context) {
    this.context=context.getApplicationContext();
  }
  
  public ArrayList<Student> fetchStudent(Classblock classBlock) {
    ArrayList<Student> items = new ArrayList<Student>();
    
    SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(context);
    String schoolName =SP.getString(("schoolName"), "");
    String urlServer = SP.getString("urlServer", "");
    
    Format formatter = new SimpleDateFormat("ddMMyyyy");
    
    try {
      String url = Uri.parse(urlServer).buildUpon()
        .appendQueryParameter("action", ACTION_GET_STUDENTS)
        .appendQueryParameter("school", schoolName)
        .appendQueryParameter("login", User.getInstance().getLogin())
        .appendQueryParameter("password", User.getInstance().getPassword())
        .appendQueryParameter("idGroup", String.valueOf(classBlock.getIdGroup()))
        .appendQueryParameter("idClassBlock", String.valueOf(classBlock.getId()))
        .appendQueryParameter("date", formatter.format(classBlock.getDate()))
        .build().toString();
      
      Log.i(TAG, "url: " + url);
      
      String data = AtmNet.getUrl(url);
      Log.i(TAG, "url: " + data);
      
      JSONObject jsonObject = new JSONObject(data);
      JSONArray jsonArray = new JSONArray(jsonObject.getString("data"));
      
      for (int i = 0; i < jsonArray.length(); i++) {
        JSONObject row = jsonArray.getJSONObject(i);
        
        Student item = new Student();
        
        item.setId(row.getInt("id"));
        item.setFullname(row.getString("fullname"));
        item.setName(row.getString("name"));
        item.setSurname1(row.getString("surname1"));
        item.setSurname2(row.getString("surname2"));
        item.setMissType(0);
        item.setNotMaterial(false);
        item.setNetworkTransit(false);
        
        if (row.has("misses")) {
          JSONArray misses = row.getJSONArray("misses");
          for (int j = 0; j < misses.length(); j++) {
            int miss = misses.getInt(j);
            if (miss > NOT_MISS && miss <= EXPULSION) {
              item.setMissType(miss);
            }
            if (miss==NOT_MATERIAL) item.setNotMaterial(true);
          }
        }
        
        items.add(item);
      }
    } catch (IOException ioe) {
      Log.e(TAG,"Failed to fetch items",ioe);
      
    } catch (JSONException je) {
      Log.e(TAG,"Failed to parse JSON",je);
    }
    return items;
  }
  
  public Boolean addMisses(Miss missDelete, Miss missAdd) {
    
    SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(context);
    String schoolName =SP.getString(("schoolName"), "");
    String urlServer = SP.getString("urlServer", "");
    
    Format formatter = new SimpleDateFormat("ddMMyyyy");
    
    try {
      if (!missDelete.isEmpty()) {
        String url = Uri.parse(urlServer).buildUpon()
          .appendQueryParameter("action", ACTION_DELETE_MISS)
          .appendQueryParameter("school", schoolName)
          .appendQueryParameter("login", User.getInstance().getLogin())
          .appendQueryParameter("password", User.getInstance().getPassword())
          .appendQueryParameter("idStudent", String.valueOf(missDelete.getIdStudent()))
          .appendQueryParameter("type", String.valueOf(missDelete.getType()))
          .appendQueryParameter("idClassblock", String.valueOf(missDelete.getIdClassblock()))
          .appendQueryParameter("date",formatter.format(missDelete.getDate()))
          .build().toString();
        
        Log.i(TAG, "url delete: " + url);
        
        String data = AtmNet.getUrl(url);
        Log.i(TAG, "url delete: " + data);
        JSONObject jsonObject = new JSONObject(data);
        
        if (!jsonObject.has("result")) {
          return false;  
        }
      }
      
      if (!missAdd.isEmpty()) {
        String url = Uri.parse(urlServer).buildUpon()
            .appendQueryParameter("action", ACTION_ADD_MISS)
            .appendQueryParameter("school", schoolName)
            .appendQueryParameter("login", User.getInstance().getLogin())
            .appendQueryParameter("password", User.getInstance().getPassword())
            .appendQueryParameter("idStudent", String.valueOf(missAdd.getIdStudent()))
            .appendQueryParameter("type", String.valueOf(missAdd.getType()))
            .appendQueryParameter("idClassblock", String.valueOf(missAdd.getIdClassblock()))
            .appendQueryParameter("date",formatter.format(missAdd.getDate()))
            .appendQueryParameter("idSubject",String.valueOf(missAdd.getIdSubject()))
            .build().toString();
          
        Log.i(TAG, "url add: " + url);
          
        String data = AtmNet.getUrl(url);
        Log.i(TAG, "url add: " + data);
        JSONObject jsonObject = new JSONObject(data);
          
        if (!jsonObject.has("result")) {
          return false;  
        }
      }
    } catch (IOException ioe) {
      Log.e(TAG,"Failed to fetch items",ioe);
      
    } catch (JSONException je) {
      Log.e(TAG,"Failed to parse JSON",je);
    }
    
    return true;
  }
  
  public Boolean updateCheckList(Classblock classblock) {
    
    SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(context);
    String schoolName = SP.getString(("schoolName"), "");
    String urlServer = SP.getString("urlServer", "");
    
    Format formatter = new SimpleDateFormat("ddMMyyyy");
    String action_checklist; 
    
    try {
      
      if (classblock.isList()) 
        action_checklist = ACTION_CHECKED_CHECKLIST;
      else
        action_checklist = ACTION_UNCHECKED__CHECKLIST;
       
      
      String url = Uri.parse(urlServer).buildUpon()
        .appendQueryParameter("action", action_checklist)
        .appendQueryParameter("school", schoolName)
        .appendQueryParameter("login", User.getInstance().getLogin())
        .appendQueryParameter("password", User.getInstance().getPassword())
        .appendQueryParameter("idClassblock", String.valueOf(classblock.getId()))
        .appendQueryParameter("date",formatter.format(classblock.getDate()))
        .build().toString();
      
      Log.i(TAG, "url checklist: " + url);
      
      String data = AtmNet.getUrl(url);
      Log.i(TAG, "url checklist: " + data);
      JSONObject jsonObject = new JSONObject(data);
      
      if (!jsonObject.has("result")) {
        return false;  
      }
    } catch (IOException ioe) {
      Log.e(TAG,"Failed to fetch items",ioe);
      
    } catch (JSONException je) {
      Log.e(TAG,"Failed to parse JSON",je);
    }
    
    return true;
  }
}
