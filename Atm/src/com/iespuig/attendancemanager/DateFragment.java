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

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import com.iespuig.attendancemanager.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;

@SuppressLint("InflateParams")
public class DateFragment extends DialogFragment{
  public static final String EXTRA_DATE = "com.iespuig.attendancemanager.DATE";

  private Date mDate;
  
  public static DateFragment newInstance(Date date) {
    Bundle args = new Bundle();
    args.putSerializable(EXTRA_DATE, date);
    
    DateFragment fragment = new DateFragment();
    fragment.setArguments(args);

    return fragment;
  }
  
  private void sendResult(int resultCode) {
    if (getTargetFragment() == null) 
        return;

    Intent i = new Intent();
    i.putExtra(EXTRA_DATE, mDate);

    getTargetFragment()
        .onActivityResult(getTargetRequestCode(), resultCode, i);
    
  }
  
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    mDate = (Date)getArguments().getSerializable(EXTRA_DATE);

    Calendar calendar = Calendar.getInstance();
    calendar.setTime(mDate);
    int year = calendar.get(Calendar.YEAR);
    int month = calendar.get(Calendar.MONTH);
    int day = calendar.get(Calendar.DAY_OF_MONTH);

    View v = getActivity().getLayoutInflater().inflate(R.layout.fragment_date, null);

    DatePicker datePicker = (DatePicker)v.findViewById(R.id.dialog_date_datePicker);
    datePicker.init(year, month, day, new OnDateChangedListener() {
      public void onDateChanged(DatePicker view, int year, int month, int day) {
        mDate = new GregorianCalendar(year, month, day).getTime();
        getArguments().putSerializable(EXTRA_DATE, mDate);
      }
    });

    return new AlertDialog.Builder(getActivity())
      .setView(v)
      .setTitle(R.string.dialog_date_title)
      .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int which) {
              sendResult(Activity.RESULT_OK);
          }
      })
      .create();
  }
}
