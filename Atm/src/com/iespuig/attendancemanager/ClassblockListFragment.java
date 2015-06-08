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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.iespuig.attendancemanager.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

@SuppressLint("SimpleDateFormat")
public class ClassblockListFragment extends Fragment implements MissValues{
  private static final String TAG = "ClassblockListFragment";
  private static final String DIALOG_DATE="date";
  private static final int REQUEST_DATE = 0;
  
  private Button mDateButton;
  private Date mDate;
  private ListView mListView;
  
  private int numClassblocks;
  private int numStudentsMisses;
  private Classblock classblockToCopy;
  
  ArrayList<Classblock> mClassblocks;
  ClassblockAdapter classblockAdapter;
  SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE - dd/MM/yyyy");
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mDate = Calendar.getInstance().getTime();
    new FetchClassblockTask().execute(mDate);
  }
  
  @Override
  public void onResume() {
    super.onResume();
    new FetchClassblockTask().execute(mDate);
    setupAdapter();
  }
  
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup parent,
      Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.fragment_classblock, parent, false);
   
    getActivity().setTitle(User.getInstance().getFullname()); 
    
    mListView = (ListView) v.findViewById(R.id.class_listView);
    mListView.setOnItemClickListener(new OnItemClickListener() {
      public void onItemClick(AdapterView<?> parent, View view,
          int position, long id) {
        
        Classblock c = new Classblock();
        c = mClassblocks.get(position);
        c.setDate(mDate);
        
        Bundle bundle = new Bundle();
        bundle.putSerializable(StudentListFragment.EXTRA_CLASSBLOCK, c);
        Intent i = new Intent(getActivity(), StudentListActivity.class);
        i.putExtras(bundle);
        startActivity(i);
      }
    });
    setupAdapter();
    
    mDateButton = (Button) v.findViewById(R.id.class_date);
    mDateButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        FragmentManager fm = getFragmentManager();
        DateFragment dialog = DateFragment.newInstance(mDate);
        
        dialog.setTargetFragment(ClassblockListFragment.this, REQUEST_DATE);
        dialog.show(fm,DIALOG_DATE);
        Log.i(TAG,"Date:" + mDate.toString());
      }
    });
    updateDate();
    registerForContextMenu(mListView);
    return v;
  }
  
  void setupAdapter() {
    if (getActivity()== null || mListView == null) return;
    
    classblockAdapter = new ClassblockAdapter(mClassblocks);
    
    if(mClassblocks!=null) {
      mListView.setAdapter(classblockAdapter);
    } else {
      mListView.setAdapter(null);
    }
  }
  
  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (resultCode != Activity.RESULT_OK) return;
    
    Log.i(TAG,"RequestCode = " + requestCode);
    
    if (requestCode == REQUEST_DATE) {
      mDate = (Date)data.getSerializableExtra(DateFragment.EXTRA_DATE);
      mClassblocks.clear();
      new FetchClassblockTask().execute(mDate);
      updateDate();
    } 
  }

  private void updateDate() {
    mDateButton.setText(dateFormat.format(mDate));
  }
  
  private class FetchClassblockTask extends AsyncTask<Date,Void,ArrayList<Classblock>> {
    @Override
    protected ArrayList<Classblock> doInBackground(Date... params) {
      return new ClassblockFetchr(getActivity()).fetchClassblock(params[0]);
    }
    
    @Override
    protected void onPostExecute(ArrayList<Classblock> items) {
      mClassblocks=items;
      UpdateMissesClassblock();
    }
  }
  
  private void UpdateMissesClassblock() {
    numClassblocks = mClassblocks.size();
    
    for (Classblock c : mClassblocks) {
      c.setDate(mDate);
      new FetchClassblockMissTask().execute(c);
    }
  }
  
  private class FetchClassblockMissTask extends AsyncTask<Classblock,Void, Classblock> {
    @Override
    protected Classblock doInBackground(Classblock... params) {
      return new ClassblockFetchr(getActivity()).fetchClassblockMissNumber(params[0]);
    }
    
    @Override
    protected void onPostExecute(Classblock classblock) {
      Log.i(TAG," >num " + numClassblocks + " misses>"+ classblock.getNumMisses())  ;
      numClassblocks--;
      if (numClassblocks==0) {
        checkClassbloksForCopy();
        setupAdapter();  
      }
      
    }
  }
  
  private void checkClassbloksForCopy() {
    Classblock cc= new Classblock();
    
    for (Classblock c: mClassblocks ) {
      if (c.getIdGroup()==cc.getIdGroup() &&
          c.getNumMisses()==0 &&
          cc.getNumMisses ()>0) {
        c.setClassblockCopy(cc);
      }
      cc=c;
    }
  }
  
  private class ClassblockAdapter extends ArrayAdapter<Classblock> {
    public ClassblockAdapter(ArrayList<Classblock> items) {
        super(getActivity(), 0, items);
    }
  
    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      // If we weren't given a view, inflate one
      if (convertView == null) {
        convertView = getActivity().getLayoutInflater()
            .inflate(R.layout.list_classblock, null);
      }
  
      // Configure the view for this Classblock
      Classblock c = getItem(position);
      
      TextView nameSubjectTextView =
          (TextView)convertView.findViewById(R.id.classblock_item_name_subject);
      nameSubjectTextView.setText(c.getNameSubject() + 
          (c.getNumMisses()==0 ? "" : " (" + c.getNumMisses()+ ")"));
      
      
      TextView nameTermsTextView =
          (TextView)convertView.findViewById(R.id.classblock_item_name_term);
      nameTermsTextView.setText(c.getNameTerms());
      
      if (c.getClassblockCopy()!=null) 
        nameTermsTextView.setTextColor(Color.RED);
      else
        nameTermsTextView.setTextColor(Color.BLACK);
      
      TextView dateStartTextView =
          (TextView)convertView.findViewById(R.id.classblock_item_start);
      dateStartTextView.setText(c.getStart());
      
      TextView dateEndTextView =
          (TextView)convertView.findViewById(R.id.classblock_item_end);
      dateEndTextView.setText(c.getEnd());
      
      CheckBox listCheckBox =
          (CheckBox)convertView.findViewById(R.id.classblock_item_list);
      listCheckBox.setChecked(c.isList());
      
      return convertView;
    }
  }

  @Override
  public void onCreateContextMenu(ContextMenu menu, View v,
      ContextMenuInfo menuInfo) {
    AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
    Classblock classblock = classblockAdapter.getItem(info.position);
    if (classblock.getClassblockCopy()!=null) {
      menu.setHeaderTitle(R.string.classblock_contextual_header);
      menu.add(0,0,0, getResources().getString(R.string.classblock_contextual_copy) + " " +
          classblock.getClassblockCopy().getStart() +"-" +
          classblock.getClassblockCopy().getEnd());
    }
  }

  @Override
  public boolean onContextItemSelected(MenuItem item) {
    AdapterContextMenuInfo info =(AdapterContextMenuInfo)item.getMenuInfo();
    Classblock classblock = classblockAdapter.getItem(info.position);
      
    copyClassblockMisses(classblock);
    
    return super.onContextItemSelected(item);
  }

  private void copyClassblockMisses(Classblock classblock) {
    
    classblockToCopy = new Classblock();
    classblockToCopy = classblock;
    
    classblock.getClassblockCopy().setDate(mDate);
    
    new FetchClassblockStudentsMissTask().execute(classblock.getClassblockCopy());
  }
  
  private class FetchClassblockStudentsMissTask 
     extends AsyncTask<Classblock,Void, ArrayList<Student>> {
    @Override
    protected ArrayList<Student> doInBackground(Classblock... params) {
      return new ClassblockFetchr(getActivity()).fetchClassblockStudentsMiss(params[0]);
    }
    
    @Override
    protected void onPostExecute(ArrayList<Student> students) {
      UpdateMissesStudents(students);
    }
  }
  
  private void UpdateMissesStudents(ArrayList<Student> students) {
    numStudentsMisses = students.size();
    Miss missDelete = new Miss();
    
    for (Student s : students) {
      
      Log.i(TAG,"for->" + s.getId() + s.getFullname() + s.getMissType());
      if (s.getMissType() > NOT_MISS) {
        Miss missAdd = new Miss();
        missAdd.setMiss(s.getId(), s.getMissType(), 
          classblockToCopy.getId(),
          classblockToCopy.getIdSubject(),
          classblockToCopy.getDate());
        new FetchStudentMissTask().execute(missDelete,missAdd);  
      }
      
      if (s.isNotMaterial()) {
        Miss missAdd = new Miss();
        missAdd.setMiss(s.getId(), NOT_MATERIAL, 
          classblockToCopy.getId(),
          classblockToCopy.getIdSubject(),
          classblockToCopy.getDate());
        new FetchStudentMissTask().execute(missDelete,missAdd);
      }
    }
  }
  
  
  private class FetchStudentMissTask extends AsyncTask<Miss,Void, Boolean> {
    @Override
    protected Boolean doInBackground(Miss... params) {
      Log.i(TAG,"Miss-do"+ params[1].getIdStudent()+ params[1].getType());
      return new StudentFetchr(getActivity()).addMisses(params[0],params[1]); 
    }
    
    @Override
    protected void onPostExecute(Boolean result) {
      numStudentsMisses--;
      Log.i(TAG," numStudentsMisses >" + numStudentsMisses);
      if (numStudentsMisses==0) {
        new FetchClassblockTask().execute(mDate);
        setupAdapter(); 
      }
    }
  }
}
