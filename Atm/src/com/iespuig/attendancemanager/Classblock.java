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

import java.io.Serializable;
import java.util.Date;

public class Classblock implements Serializable {
	private static final long serialVersionUID = 1L;
	
  private int mId;
	private String mStart;
	private String mEnd;
	private int mIdGroup;
	private int mIdSubject;
	private String mNameSubject;
	private String mNameTerms;
	private Boolean mList;
	private Date mDate;
	private Classblock mClassblockCopy;
	private int mNumMisses;
	
	public int getId() {
		return mId;
	}
	
	public Date getDate() {
    return mDate;
  }
  public void setDate(Date mDate) {
    this.mDate = mDate;
  }
  public void setId(int mId) {
		this.mId = mId;
	}
	public String getStart() {
		return mStart;
	}
	public void setStart(String mStart) {
		this.mStart = mStart;
	}
	public String getEnd() {
		return mEnd;
	}
	public void setEnd(String mEnd) {
		this.mEnd = mEnd;
	}
	public int getIdGroup() {
		return mIdGroup;
	}
	public void setIdGroup(int mIdGroup) {
		this.mIdGroup = mIdGroup;
	}
	public int getIdSubject() {
		return mIdSubject;
	}
	public void setIdSubject(int mIdSubject) {
		this.mIdSubject = mIdSubject;
	}
	public String getNameSubject() {
		return mNameSubject;
	}
	public void setNameSubject(String mNameSubject) {
		this.mNameSubject = mNameSubject;
	}
	public String getNameTerms() {
		return mNameTerms;
	}
	public void setNameTerms(String mNameTerms) {
		this.mNameTerms = mNameTerms;
	}
	public Boolean isList() {
		return mList;
	}
	public void setList(Boolean mList) {
		this.mList = mList;
	}

  public Classblock getClassblockCopy() {
    return mClassblockCopy;
  }

  public void setClassblockCopy(Classblock classblockCopy) {
    mClassblockCopy = classblockCopy;
  }

  public int getNumMisses() {
    return mNumMisses;
  }

  public void setNumMisses(int numMisses) {
    this.mNumMisses = numMisses;
  }
  
}
