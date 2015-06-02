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

public interface MissValues {
    
  public static int NOT_MISS = 0;
  public static int MISS = 1;
  public static int EXCUSED_MISS = 2;
  public static int DELAY = 3;
  public static int EXPULSION = 4;
  public static int NOT_MATERIAL = 7;
  
  public static final int PREFERENCES_ONCLICK_CICLIC = 1;
  public static final int PREFERENCES_ONCLICK_ONLY_MISS = 2;
}
