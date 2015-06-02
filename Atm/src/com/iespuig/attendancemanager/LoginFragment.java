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
import android.widget.TextView;

public class LoginFragment extends DialogFragment {
  public static final String EXTRA_USER ="com.iespuig.attendancemanager.login";
  public static final String EXTRA_PASS ="com.iespuig.attendancemanager.pass";
  
  private TextView mLoginUser;
  private TextView mLoginPassword;
  
  private void sendResult(int resultCode) {
    if (getTargetFragment() == null)
        return;

    Intent i = new Intent();
    i.putExtra(EXTRA_USER, mLoginUser.getText().toString());
    i.putExtra(EXTRA_PASS, mLoginPassword.getText().toString());
    
    getTargetFragment()
        .onActivityResult(getTargetRequestCode(), resultCode, i);
  }
  
  @SuppressLint("InflateParams")
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    View v = getActivity().getLayoutInflater()
      .inflate(R.layout.fragment_login, null);
    
    mLoginUser = (TextView) v.findViewById(R.id.login_user);
    mLoginPassword = (TextView) v.findViewById(R.id.login_pass);
    
    return new AlertDialog.Builder(getActivity())
      .setView(v)
      .setTitle(R.string.dialog_login_title)
      .setPositiveButton(android.R.string.ok, 
        new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            sendResult(Activity.RESULT_OK);
          }
        })
      .create();
  }
}
