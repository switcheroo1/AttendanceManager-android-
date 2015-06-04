package org.coopux.attendancemanager.httpservlet;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.coopux.attendancemanager.core.Classblock;
import org.coopux.attendancemanager.core.Miss;
import org.coopux.attendancemanager.core.Student;
import org.coopux.attendancemanager.core.Teacher;
import org.coopux.attendancemanager.entrypoint.AttendancemanagerApplication;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


/**
 * Servlet implementation class RestServlet
 * 
 * ACTION:
 * 
 * getStudentOfGroup
 * getStudentOfGroupMisses
 * getTeacher
 * getClassBlock
 * addMiss
 * getMissOfStudent
 * deleteMiss
 * insertCheckList
 * deleteCheckList
 */

@WebServlet("/AMServlet/*")
public class AMServlet extends HttpServlet {
  private static final long serialVersionUID = 1L;
  private static final int ERROR_VALIDATION_USER = 99;
  private static final int ERROR_ACTION_NO_AVAILABLE = 98;
  private static final int ERROR_ACTION_NOT_FOUND = 97;

  private static AttendancemanagerApplication am = AttendancemanagerApplication.getCurrentAM();
  /**
   * @see HttpServlet#HttpServlet()
   */
  public AMServlet() {
    super();
  }

  /**
   * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
   */
  @SuppressWarnings("unchecked")
  protected void doGet(HttpServletRequest request, HttpServletResponse response) 
      throws ServletException, IOException {

    String action = request.getParameter("action");
    String school = request.getParameter("school");
    String login = request.getParameter("login");
    String password = request.getParameter("password");

    SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyy");


    am.setBundlesAndResources(school);

    if(action==null){
      response.getOutputStream().println("{\"error\":" + ERROR_ACTION_NOT_FOUND + "}");
      return;
    }

    Teacher t = am.storage.loginUser(login, password);

    if (t==null) {
      response.getOutputStream().println("{\"error\":"+ ERROR_VALIDATION_USER + "}");
      return;
    }

    JSONObject objParent,obj;
    JSONArray objArray;
    Date date;

    switch (action) {
    /********************************************************************************
     * ACTION getStudentOfGroupMisses
     * 
     * REQUEST:
     * http://localhost:8080/AMServlet?school=newplantada&login=ebayan&password=poiu
     *    &action=getStudentsOfGroupMisses&idGroup=5&idClassBlock=123&data=02052015
     * 
     * RESPONSE: json
     * {"data":[
     *  {"surname1":"aaa","name":"aaa","surname2":"aaa","id":1959,"fullname":"aaa aaa, aaa",
     *   "misses":[1,2]},
     *  {"surname1":"El Amri","name":"Aicha","surname2":"","id":1483,"fullname":"El Amri, Aicha"
     *   }
     * ]}
     * 
     * MISS:
     * 
     * 1=FALTA 2=JUSTIFICADA 3=RETRASO 4=EXPULSION
     *  
     *******************************************************************************/
    case "getStudentsOfGroupMisses":
      int idGroupMisses = Integer.parseInt(request.getParameter("idGroup"));
      int idClassBlock = Integer.parseInt(request.getParameter("idClassBlock"));
      date = new Date();
      try {
        date = formatter.parse(request.getParameter("date"));
      } catch (ParseException e) {
        e.printStackTrace();
      }

      Collection<Student> collection = am.storage.studentsOfGroup(idGroupMisses);

      objParent = new JSONObject();
      objArray = new JSONArray();

      if (collection!=null) {
        for(Iterator<Student> it = collection.iterator(); it.hasNext(); ) {
          Student student = it.next();
          obj = new JSONObject();

          obj.put("id", student.getID());
          obj.put("fullname", student.getFullName());
          obj.put("surname1", student.getSurname1());
          obj.put("surname2", student.getSurname2());
          obj.put("name", student.getName());

          // Check Misses of Student

          ArrayList<Miss> misses = am.storage.getStudentMisses(student.getID(), 
              idClassBlock, date);

          if(misses!=null){
            JSONArray objMisses = new JSONArray();
            for(Iterator<Miss> itm = misses.iterator(); itm.hasNext(); ) {
              Miss miss = itm.next();
              objMisses.add(miss.getType());
            }
            if (!objMisses.isEmpty()) obj.put("misses", objMisses);
          }
          objArray.add(obj);
        }
        objParent.put("data",objArray);
        response.getOutputStream().println(objParent.toJSONString());
      }
      break;

      /********************************************************************************
       * ACTION getStudentOfGroup
       * 
       * REQUEST:
       * http://localhost:8080/AMServlet?school=newplantada&login=ebayan&password=poiu
       *    &action=getStudentsOfGroup&idGroup=5
       * 
       * RESPONSE: json
       * {"data":[
       *  {"surname1":"aaa","name":"aaa","surname2":"aaa","id":1959,"fullname":"aaa aaa, aaa"},
       *  {"surname1":"El Amri","name":"Aicha","surname2":"","id":1483,"fullname":"El Amri, Aicha"}
       * ]}
       *  
       *******************************************************************************/
    case "getStudentsOfGroup":
      String idGroup = request.getParameter("idGroup");

      Collection<Student> students = am.storage.studentsOfGroup(Integer.parseInt(idGroup));

      objParent = new JSONObject();
      objArray = new JSONArray();

      if (students!=null) {
        for(Iterator<Student> it = students.iterator(); it.hasNext(); ) {
          Student student = it.next();
          obj = new JSONObject();

          obj.put("id", student.getID());
          obj.put("fullname", student.getFullName());
          obj.put("surname1", student.getSurname1());
          obj.put("surname2", student.getSurname2());
          obj.put("name", student.getName());

          objArray.add(obj);
        }
        objParent.put("data",objArray);
        response.getOutputStream().println(objParent.toJSONString());
      }
      break;

      /********************************************************************************
       * ACTION getTeacher
       *
       * REQUEST:
       * http://localhost:8080/AMServlet?school=newplantada&login=ebayan&password=poiu
       *   &action=getTeacher
       * 
       * RESPONSE: json
       * {"password":"44e42e...9","fullName":"Esther Bayan","id":16,"login":"ebayan"}
       * 
       * 
       *******************************************************************************/
    case "getTeacher":
      obj = new JSONObject();
      obj.put("id", t.getID());
      obj.put("login", t.getLogin());
      obj.put("password", t.getPassword());
      obj.put("fullName", t.getFullname());
      response.getOutputStream().println(obj.toJSONString());
      break;

      /********************************************************************************
       * ACTION getClassblock
       * 
       * REQUEST:
       * http://localhost:8080/AMServlet?school=plantada&login=ebayan&password=poiu
       *   &action=getClassblock&date=12052015
       * 
       * RESPONSE: json
       * {"data":
       *  [
       *   {"idGroup":103,"subject":119,"start":"15:00:00",
       *    "name":"15:00-16:00 CFGS 2n Agències Viatges \/ M7-Viatges 2n",
       *    "end":"16:00:00","id":1963,"list":0,"day":3},
       *   {"idGroup":103,"subject":119,"start":"17:00:00",
       *    "name":"17:00-18:00 CFGS 2n Agències Viatges \/ M7-Viatges 2n",
       *    "end":"18:00:00","id":1972,"list":0,"day":3} 
       *  ]
       * }
       * 
       * 
       *******************************************************************************/
    case "getClassblock":
      date = new Date();
      try {
        date = formatter.parse(request.getParameter("date"));
      } catch (ParseException e) {
        e.printStackTrace();
      }

      Collection<Classblock> cbs = am.storage.classblocksOfTeacherOnDate(t.getID(), date);

      objParent = new JSONObject();
      objArray = new JSONArray();

      if(cbs!= null){
        for(Iterator<Classblock> it = cbs.iterator(); it.hasNext(); ) {
          Classblock cb = it.next();
          obj = new JSONObject();

          obj.put("id", cb.getID());
          obj.put("idGroup", cb.getIDGroup());
          obj.put("subject", cb.getIDSubject());
          obj.put("start", cb.getStart().toString());
          obj.put("name", cb.getName());
          obj.put("day", cb.getDay_of_week());
          obj.put("end", cb.getEnd().toString());
          obj.put("list", cb.getListChecked());

          objArray.add(obj);
        }
        objParent.put("data",objArray);
        response.getOutputStream().println(objParent.toJSONString());
      }
      break;

      /********************************************************************************
       * ACTION addMiss
       * 
       * REQUEST:
       * http://localhost:8080/AMServlet?school=newplantada&login=o&password=o&
       * action=addMiss&type=3&date=2015-05-08&idStudent=1530&idSubject=1&idClassblock=1237
       * 
       * RESPONSE: json
       * {}
       * 
       * 
       *******************************************************************************/
    case "addMiss":
      String type = request.getParameter("type");
      String dateMiss = request.getParameter("date");
      String idStudentMiss = request.getParameter("idStudent");
      String idSubjectMiss = request.getParameter("idSubject");
      String idClassblockMiss = request.getParameter("idClassblock");

      obj = new JSONObject();

      try {
        Date dateMissf = formatter.parse(dateMiss);
        Miss miss = new Miss(0, Integer.parseInt(type), dateMissf, "", 
            Integer.parseInt(idStudentMiss), t.getID(), 
            Integer.parseInt(idSubjectMiss), Integer.parseInt(idClassblockMiss));

        if (am.storage.addMiss(miss)!=0) 
          obj.put("result", "ok");
        else
          obj.put("error", "Fallo al añadir falta");

      } catch (ParseException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

      response.getOutputStream().println(obj.toJSONString());

      break;

    /********************************************************************************
    * ACTION getMissOfStudent
    * 
    * REQUEST:
    * http://localhost:8080/AMServlet?school=newplantada&login=o&password=o
    *   &action=getMissOfStudent&idStudent=1530&idClassblock=1237&date=2015-05-08
    * 
    * RESPONSE: json
    * {}
    * 
    * 
    *******************************************************************************/
    case "getMissOfStudent":
      int idStudent = Integer.parseInt(request.getParameter("idStudent"));
      int idClassblock = Integer.parseInt(request.getParameter("idClassblock"));

      Date dateM = new Date();
      try {
        dateM = formatter.parse(request.getParameter("date"));
      } catch (ParseException e) {
        e.printStackTrace();
      }
      objArray = new JSONArray();
      //Comprueba si el grupo existe.
      ArrayList<Miss> misses = am.storage.getStudentMisses(idStudent, idClassblock, dateM);

      //Esta condición comprueba si el grupo es null
      if(misses!=null){
        // Coleccion donde se almacenan todos los datos recibidos
        // ArrayList<Miss> misses = am.storage.getStudentMisses(idStudent, idClassblock, dateM);
        // iterador para ordenar y convertir en JSON

        for(Iterator<Miss> it = misses.iterator(); it.hasNext(); ) {
          obj = new JSONObject();

          Miss miss = it.next();
          obj.put("id", miss.getID());
          obj.put("type", miss.getType());
          obj.put("classblock", miss.getIDClassblock());
          obj.put("idSubject", miss.getIDSubject());
          obj.put("idTeacher", miss.getIDTeacher());
          obj.put("date", miss.getDate());

          objArray.add(obj);
        }
        //Muestra JSON
        response.getOutputStream().println(objArray.toJSONString());
      }else{
        //Mensaje en caso de que grupo se null
        obj = new JSONObject();
        obj.put("message","El grupo no existe.");
        response.getOutputStream().println(obj.toJSONString());
      }
      break;

      /********************************************************************************
       * ACTION deleteMiss
       * 
       * REQUEST:
       * http://localhost:8080/AMServlet?school=newplantada&login=o&password=o
       *   &action=deleteMiss&type=1&date=2015-05-12&idStudent=1266&idClassblock=1963
       *    
       * RESPONSE: json
       * {}
       * 
       * 
       *******************************************************************************/
    case "deleteMiss":
      int typeMD = Integer.parseInt(request.getParameter("type"));
      String dateMD = request.getParameter("date");
      int idStudentMD = Integer.parseInt(request.getParameter("idStudent"));
      int idClassblockMD = Integer.parseInt(request.getParameter("idClassblock"));

      Date dateDM = new Date();
      obj = new JSONObject();

      try {
        dateDM = formatter.parse(dateMD);
        am.storage.deleteMiss(typeMD, dateDM, idStudentMD, idClassblockMD);
      } catch (ParseException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      obj.put("result","ok");
      response.getOutputStream().println(obj.toJSONString());
      break;
      
      /********************************************************************************
       * ACTION checkedList
       * 
       * REQUEST:
       * http://localhost:8080/AMServlet?school=newplantada&login=o&password=o
       *   &action=insertCheckList&date=12052015&idClassblock=1963
       *    
       * RESPONSE: json
       * {}
       * 
       * 
       *******************************************************************************/
    case "checkedList":
      String dateCL = request.getParameter("date");
      int idClassblockCL = Integer.parseInt(request.getParameter("idClassblock"));

      date = new Date();
      obj = new JSONObject();
      

      try {
        date = formatter.parse(dateCL);
        Classblock classblock = am.storage.getClassblock(idClassblockCL);
        am.storage.updateListControlToChecked(classblock, date);
      } catch (ParseException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      obj.put("result","ok");
      response.getOutputStream().println(obj.toJSONString());
      break;
      /********************************************************************************
       * ACTION uncheckedList
       * 
       * REQUEST:
       * http://localhost:8080/AMServlet?school=newplantada&login=o&password=o
       *   &action=insertCheckList&date=12052015&idClassblock=1963
       *    
       * RESPONSE: json
       * {}
       * 
       * 
       *******************************************************************************/
    case "uncheckedList":
      String dateCLD = request.getParameter("date");
      int idClassblockCLD = Integer.parseInt(request.getParameter("idClassblock"));

      date = new Date();
      obj = new JSONObject();
      

      try {
        date = formatter.parse(dateCLD);
        Classblock classblock = am.storage.getClassblock(idClassblockCLD);
        am.storage.updateListControlToUnChecked(classblock, date);
      } catch (ParseException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      obj.put("result","ok");
      response.getOutputStream().println(obj.toJSONString());
      break;

      /********************************************************************************
       * ACTION NO AVAILABLE
       *******************************************************************************/
    default:
      response.getOutputStream().println("{\"error\":"+ ERROR_ACTION_NO_AVAILABLE + "}");
      break;
    }
  }

  /**
   * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
   */
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    // TODO Auto-generated method stub
  }

}