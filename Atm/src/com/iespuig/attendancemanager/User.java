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

public class User {
  private static User sUser = null;
  private String mLogin;
  private String mPassword;
  private String mFullname;
  private int mId;

  private User() {
  }

  public static User getInstance() {
    if (sUser == null) {
      sUser = new User();
    }
    return sUser;
  }

  public int getId() {
    return mId;
  }

  public void setId(int id) {
    mId = id;
  }
  public String getLogin() {
    return mLogin;
  }

  public void setLogin(String login) {
    mLogin = login;
  }

  public String getPassword() {
    return mPassword;
  }

  public void setPassword(String password) {
    mPassword = password;
  }

  public String getFullname() {
    return mFullname;
  }

  public void setFullname(String fullname) {
    mFullname = fullname;
  }
}
