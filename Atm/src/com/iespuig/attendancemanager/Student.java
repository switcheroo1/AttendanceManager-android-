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

public class Student {
  private int mId;
  private String mName;
  private String mSurname1;
  private String mSurname2;
  private String mFullname;
  private int mMissType;
  private Boolean mNotMaterial;
  private Boolean mNetworkTransit;
  
  public int getId() {
    return mId;
  }
  public void setId(int id) {
    mId = id;
  }
  
  public Boolean isNotMaterial() {
    return mNotMaterial;
  }
  
  public void setNotMaterial(Boolean notMaterial) {
    mNotMaterial = notMaterial;
  }
  
  public int getMissType() {
    return mMissType;
  }
  
  public void setMissType(int id) {
    mMissType = id;
  }
  
  public String getName() {
    return mName;
  }
  public void setName(String name) {
    mName = name;
  }
  public String getSurname1() {
    return mSurname1;
  }
  public void setSurname1(String surname1) {
    mSurname1 = surname1;
  }
  public String getSurname2() {
    return mSurname2;
  }
  public void setSurname2(String surname2) {
    mSurname2 = surname2;
  }
  
  public String getFullname() {
    return mFullname;
  }
  
  public void setFullname(String fullname) {
    mFullname = fullname;
  }
  public Boolean getNetworkTransit() {
    return mNetworkTransit;
  }
  public void setNetworkTransit(Boolean networkTransit) {
    mNetworkTransit = networkTransit;
  }
}
