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

import com.iespuig.attendancemanager.R;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


public class StudentListFragment extends Fragment implements MissValues{
  
  public static final String EXTRA_CLASSBLOCK = "com.iespuig.attendancemanager.CLASSBLOCK";
  private static final String TAG = "StudentListFragment";
  
  private TextView mSubjectName;
  private TextView mTermsName;
  private TextView mDateClassblock;
  private ListView mListView;
  
  private Classblock mClassblock;
  
  
  Boolean hasNetworkTransit = false;
  
  ArrayList<Student> mStudents;
  StudentAdapter studentAdapter;
  int mPosition;
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      
    setHasOptionsMenu(true);
    
    mClassblock = (Classblock)getActivity().getIntent()
        .getSerializableExtra(EXTRA_CLASSBLOCK);
    
    new FetchStudentTask().execute(mClassblock);
    
  }
  
  @SuppressLint("SimpleDateFormat")
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup parent,
      Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.fragment_student, parent, false);
    
    SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE - dd/MM/yyyy");
    
    getActivity().setTitle(User.getInstance().getFullname());
    
    mTermsName = (TextView) v.findViewById(R.id.student_nameTerms);
    mSubjectName = (TextView) v.findViewById(R.id.student_nameSubject);
    mDateClassblock = (TextView) v.findViewById(R.id.student_date);
    
    mTermsName.setText(mClassblock.getNameTerms());
    mSubjectName.setText(mClassblock.getNameSubject());
    mDateClassblock.setText(
        mClassblock.getStart()+":" + mClassblock.getEnd() + " " +
        dateFormat.format(mClassblock.getDate())
    );
    
    mListView = (ListView) v.findViewById(R.id.student_listView);
    mListView.setOnItemClickListener(new OnItemClickListener() {
      public void onItemClick(AdapterView<?> parent, View view,
          int position, long id) {
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getActivity());
        int onClickStudent = Integer.valueOf(SP.getString("onClickStudent", "1"));
        
        Student student = mStudents.get(position);
        mPosition=position;
        
        if (!hasNetworkTransit) {
          int missTypePrev = student.getMissType();
          int missTypePost = missTypePrev;
        
          switch (onClickStudent) {
            case PREFERENCES_ONCLICK_CICLIC:
              if (++missTypePost>EXPULSION) missTypePost=NOT_MISS;
              break;
            case PREFERENCES_ONCLICK_ONLY_MISS:
              missTypePost=(missTypePost>=MISS) ? NOT_MISS : MISS;
              break;
          }
          
          manageMiss(student, missTypePrev, missTypePost);
        }
      }
    });
    setupAdapter();
    
    registerForContextMenu(mListView);
    
    return v;
  }
  
  private class AddMissTask extends AsyncTask<Miss,Void, Boolean> {
    @Override
    protected Boolean doInBackground(Miss... params) {
      return new StudentFetchr(getActivity()).addMisses(params[0],params[1]);
    }
    
    @Override
    protected void onPostExecute(Boolean result) {
      hasNetworkTransit=false;
      
      if (result) {
        mStudents.get(mPosition).setNetworkTransit(false);
        studentAdapter.notifyDataSetChanged();
      }
    }
  }
  
  void setupAdapter() {
    if (getActivity()== null || mListView == null) return;
    
    studentAdapter = new StudentAdapter(mStudents);
    
    if(mStudents!=null) {
      mListView.setAdapter(studentAdapter);
    } else {
      mListView.setAdapter(null);
    }
  }
  
  private class FetchStudentTask extends AsyncTask<Classblock,Void, ArrayList<Student>> {
    @Override
    protected ArrayList<Student> doInBackground(Classblock... params) {
      return new StudentFetchr(getActivity()).fetchStudent(params[0]);
    }
    
    @Override
    protected void onPostExecute(ArrayList<Student> items) {
      mStudents=items;
      setupAdapter();
    }
  }
  
  private class StudentAdapter extends ArrayAdapter<Student> {
    
    public StudentAdapter(ArrayList<Student> items) {
        super(getActivity(), 0, items);
    }
  
    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      // If we weren't given a view, inflate one
      if (convertView == null) {
        convertView = getActivity().getLayoutInflater()
            .inflate(R.layout.list_student, null);
      }
  
      // Configure the view for this ClassBlock
      Student student = getItem(position);
      Boolean networkTransit = student.getNetworkTransit();
  
      TextView fullnameTextView =
          (TextView)convertView.findViewById(R.id.student_item_fullname);
      fullnameTextView.setText(student.getFullname());
      
      ImageView missImageView =
          (ImageView)convertView.findViewById(R.id.student_item_miss);
      
      ImageView notMaterialImageView =
          (ImageView)convertView.findViewById(R.id.student_item_material);
      
      missImageView.setVisibility(View.VISIBLE);
      
      if (student.isNotMaterial()) 
        notMaterialImageView.setVisibility(View.VISIBLE);
      else
        notMaterialImageView.setVisibility(View.INVISIBLE);
      
      
      switch (student.getMissType()) {
        case NOT_MISS:
          missImageView.setImageResource(R.drawable.m1_not_miss);
          missImageView.setVisibility(View.INVISIBLE);
          break;
        case MISS:
          if (networkTransit)
            missImageView.setImageResource(R.drawable.m1_miss_n);
          else
            missImageView.setImageResource(R.drawable.m1_miss);
          break;
        case EXCUSED_MISS:
          if (networkTransit)
            missImageView.setImageResource(R.drawable.m2_excused_n);
          else
            missImageView.setImageResource(R.drawable.m2_excused);
          break;
        case DELAY:
          if (networkTransit)
            missImageView.setImageResource(R.drawable.m3_delay_n);
          else
            missImageView.setImageResource(R.drawable.m3_delay);
          break;
        case EXPULSION:
          if (networkTransit)
            missImageView.setImageResource(R.drawable.m4_expulsion_n);
          else
            missImageView.setImageResource(R.drawable.m4_expulsion);
          break;
      }
      
      return convertView;
    }
  }
  
  @Override
  public void onCreateContextMenu(ContextMenu menu, View v,
      ContextMenuInfo menuInfo) {
    
    AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
    Student student = studentAdapter.getItem(info.position);
    
    menu.setHeaderTitle(student.getFullname());
    
    menu.add(0,NOT_MISS ,0, R.string.student_item_no_fault);
    menu.add(0,MISS ,1 , R.string.student_item_miss);
    menu.add(0,EXCUSED_MISS , 2 ,R.string.student_item_excused_miss);
    menu.add(0,DELAY , 3 , R.string.student_item_delay);
    menu.add(0,EXPULSION , 4 , R.string.student_item_expulsion);
    
    if (student.isNotMaterial())
      menu.add(0,NOT_MATERIAL,7, R.string.student_item_material_on);
    else
      menu.add(0,NOT_MATERIAL,7,R.string.student_item_material_off);
    
    Log.i(TAG,"getMissType:" + student.getMissType());
    
    if (student.getMissType() <= EXPULSION)
      menu.getItem(student.getMissType()).setEnabled(false);
    
  }

  @Override
  public boolean onContextItemSelected(MenuItem item) {
    if (!hasNetworkTransit) {
      AdapterContextMenuInfo info =(AdapterContextMenuInfo)item.getMenuInfo();
      mPosition = info.position;
      
      Student student = studentAdapter.getItem(mPosition);
      
      int missTypeDelete = student.getMissType();
      int missTypeAdd = item.getItemId();
      
      Log.i(TAG, "missTypeAdd " + missTypeAdd);
      
      if (missTypeAdd==NOT_MATERIAL)
        if (student.isNotMaterial()) {
          missTypeDelete=NOT_MATERIAL; missTypeAdd=NOT_MISS;
        } else {
          missTypeDelete=NOT_MISS; missTypeAdd=NOT_MATERIAL;
        }
      
      Log.i(TAG,"Seleccion -> prev " + missTypeDelete + " post " + missTypeAdd );
      
      manageMiss(student, missTypeDelete, missTypeAdd);
    }
    return super.onContextItemSelected(item);
  }
  
  public void manageMiss(Student student,int missTypePrev,int missTypePost) {
    if (missTypePost==NOT_MATERIAL || missTypePrev == NOT_MATERIAL) {
       student.setNotMaterial(!student.isNotMaterial());
    }
    else 
      student.setMissType(missTypePost);
      
    student.setNetworkTransit(true);
    studentAdapter.notifyDataSetChanged();
  
    Miss mMissAdd = new Miss();
    Miss mMissDelete = new Miss();
  
    if (missTypePrev!=NOT_MISS) {
      mMissDelete.setMiss(
        student.getId(), missTypePrev, 
        mClassblock.getId(),mClassblock.getIdSubject(),
        mClassblock.getDate());
    }
  
    if (missTypePost!=NOT_MISS) {
      mMissAdd.setMiss(
        student.getId(), missTypePost, 
        mClassblock.getId(),mClassblock.getIdSubject(),
        mClassblock.getDate());
    }
    
    hasNetworkTransit=true;
    
    new AddMissTask().execute(mMissDelete,mMissAdd);
  }
  
  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.student, menu);
    if (mClassblock.isList())
      menu.getItem(0).setIcon(R.drawable.flag_on);
    else
      menu.getItem(0).setIcon(R.drawable.flag_off);
  }
  
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // handle item selection
    
    switch (item.getItemId()) {
      case R.id.action_checklist:
        if (mClassblock.isList()) {
          mClassblock.setList(false);
          item.setIcon(R.drawable.flag_off);
        } else {
          mClassblock.setList(true);
          item.setIcon(R.drawable.flag_on);
        }
        new UpdateCheckListTask().execute(mClassblock);
      default:
        return super.onOptionsItemSelected(item);
    }
  }
  
  private class UpdateCheckListTask extends AsyncTask<Classblock,Void, Boolean> {
    @Override
    protected Boolean doInBackground(Classblock... params) {
      return new StudentFetchr(getActivity()).updateCheckList(params[0]);
    }
  }
}
