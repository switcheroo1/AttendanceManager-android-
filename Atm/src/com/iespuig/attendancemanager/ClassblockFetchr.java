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
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

public class ClassblockFetchr implements MissValues {
  public static final String TAG = "ClassblockFetchr";
  private static final String ACTION = "getClassblock";
  protected Context context;

  public ClassblockFetchr(Context context) {
    this.context=context.getApplicationContext();
  }
  
  @SuppressLint("SimpleDateFormat")
  public ArrayList<Classblock> fetchClassblock(Date date) {
    ArrayList<Classblock> items = new ArrayList<Classblock>();
    
    SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(context);
    String urlServer = SP.getString("urlServer", "");
    String schoolName = SP.getString(("schoolName"), "");
    
    Format formatter = new SimpleDateFormat("ddMMyyyy");
    
    try {
      String url = Uri.parse(urlServer).buildUpon()
        .appendQueryParameter("action", ACTION)
        .appendQueryParameter("school", schoolName)
        .appendQueryParameter("login", User.getInstance().getLogin())
        .appendQueryParameter("password", User.getInstance().getPassword())
        .appendQueryParameter("date", formatter.format(date))
        .build().toString();
      
      Log.i(TAG, "url: " + url);
      
      String data = AtmNet.getUrl(url);
      Log.i(TAG, "url: " + data);
      
      JSONObject jsonObject = new JSONObject(data);
      JSONArray jsonArray = new JSONArray(jsonObject.getString("data"));
      
      for (int i = 0; i < jsonArray.length(); i++) {
        JSONObject row = jsonArray.getJSONObject(i);
        
        Classblock c = new Classblock();
        c.setId(row.getInt("id"));
        c.setIdGroup(row.getInt("idGroup"));
        c.setIdSubject(row.getInt("subject"));
        c.setList(row.getInt("list")==0 ? false : true);
        c.setStart(row.getString("start").substring(0,5));
        c.setEnd(row.getString("end").substring(0,5));
        c.setNameTerms(row.getString("name").substring(12,row.getString("name").indexOf("/")));
        c.setNameSubject(row.getString("name").substring(row.getString("name").indexOf("/")+2,
            row.getString("name").length()));
        
        items.add(c);
      }
    } catch (IOException ioe) {
      Log.e(TAG, "Failed to fetch items",ioe);
      
    } catch (JSONException je) {
      Log.e(TAG, "Failed to parse JSON",je);
    }
    
    return items;  
  }

  public Classblock fetchClassblockMissNumber(Classblock classblock) {
    ArrayList<Student> mStudents= new StudentFetchr(context).fetchStudent(classblock);
    int numMisses=0;
    for (Student s : mStudents) {
      if (s.getMissType()>0) numMisses++;
      if (s.isNotMaterial()) numMisses++;
    }
    classblock.setNumMisses(numMisses);
    return classblock;
  }

  public ArrayList<Student> fetchClassblockStudentsMiss(Classblock classblock) {
    ArrayList<Student> students= new StudentFetchr(context).fetchStudent(classblock);
    ArrayList<Student> mStudents = new ArrayList<Student>();
    
    Log.i(TAG," "+ students.size() + students.get(0).getFullname());
    for (Student s : students) {
      if (s.getMissType()> NOT_MISS || s.isNotMaterial()) {
        mStudents.add(s);
      }
    }
    return mStudents;
  }
}