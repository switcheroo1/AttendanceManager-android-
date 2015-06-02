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

import java.util.Date;

public class Miss {
  private int mIdStudent;
  private int mType;
  private int mIdClassblock;
  private int mIdSubject;
  private Date mDate;
  
  
  public Miss() {
    mType=0;
  }
  
  public void setMiss(int idStudent, int type, int idClassBlock, 
      int idSubject, Date date) {
    mIdStudent = idStudent;
    mType = type;
    mIdClassblock = idClassBlock;
    mIdSubject = idSubject;
    mDate = date;
  
  }

  public int getIdStudent() {
    return mIdStudent;
  }

  public int getType() {
    return mType;
  }

  public int getIdClassblock() {
    return mIdClassblock;
  }

  public int getIdSubject() {
    return mIdSubject;
  }

  public Date getDate() {
    return mDate;
  }
  
  public Boolean isEmpty() {
    return (mType==0);
  }
  
}
