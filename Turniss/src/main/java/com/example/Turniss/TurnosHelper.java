package com.example.Turniss;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.example.Turniss.UsuariosHelper;


@Service
public class TurnosHelper {

	@Autowired
	private Environment env;
	
	@Autowired
    private UsuariosHelper UsuariosHelper;

    
	
	// Dado un horario en String devuelve el valor hora en int.
	private int toHours(String s) {
	    String[] hourMin = s.split(":");
	    int hour = Integer.parseInt(hourMin[0]);
	        
	    return hour;
	}
	
	// Dado un horario en String devuelve el valor minuto en int.
	private int toMins(String s) {
	    String[] hourMin = s.split(":");
	    int mins = Integer.parseInt(hourMin[1]);
	    
	    return mins;
	}
	
	// Dadas dos horas, crea una lista con intervalos de 15 minutos entre esos dos horarios EJ: 9:00 a 10:00 ->(9:00,9:15,9:30,9:45;10:00)
	public ArrayList turnosEnDia (String horaInicio, String horaFinaliza) throws ParseException {
				
		//int hInicio = toHours(horaInicio);
		//int mInicio = toMins(horaInicio);
		
		//int hFinaliza = toHours(horaFinaliza);
		//int mFinaliza = toMins(horaFinaliza);
		
		List<java.sql.Time> intervals = new ArrayList<>(25);
		// These constructors are deprecated and are used only for example
		//----->java.sql.Time startTime = new java.sql.Time(hInicio, mInicio, 0);
		//----->java.sql.Time endTime = new java.sql.Time(hFinaliza, mFinaliza, 0);

		DateFormat formatterInicio = new SimpleDateFormat("HH:mm");
		java.sql.Time startTime = new java.sql.Time(formatterInicio.parse(horaInicio).getTime());	
		
		DateFormat formatterFinaliza = new SimpleDateFormat("HH:mm");
		java.sql.Time endTime = new java.sql.Time(formatterFinaliza.parse(horaFinaliza).getTime());
				
		intervals.add(startTime);

		Calendar cal = Calendar.getInstance();
		cal.setTime(startTime);
		while (cal.getTime().before(endTime)) {
		    cal.add(Calendar.MINUTE, 15);
		    intervals.add(new java.sql.Time(cal.getTimeInMillis()));
		}
		ArrayList<String> listadoTurnos = new ArrayList<String>();
		
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		for (java.sql.Time time : intervals) {
		    //System.out.println(sdf.format(time));
		    listadoTurnos.add(sdf.format(time));
		}
				
		return listadoTurnos;
	}
	
	// Crea una lista de los horarios de los turnos que existen en un día
	public ArrayList turnosExistenEnDia(String fecha) throws SQLException, ParseException {
			
		SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd");
		Date date = format.parse(fecha);
		java.sql.Date sqlDate = new java.sql.Date(date.getTime());
		
		
		Connection connection;
		connection = DriverManager.getConnection(env.getProperty("DB_URL"),env.getProperty("DB_USERNAME"),env.getProperty("DB_PASSWORD"));
			
		PreparedStatement consulta = connection.prepareStatement("SELECT * FROM turnos WHERE fecha = ?;");
			
		consulta.setDate(1, sqlDate);
		
		ResultSet resultado = consulta.executeQuery();
			
		ArrayList<String> listadoTurnosExistentes = new ArrayList<String>();
				
		while ( resultado.next() ) {
				
			Time horas = resultado.getTime("hora");
			DateFormat df = new SimpleDateFormat("HH:mm");
			// Using DateFormat format method we can create a string 
			// representation of a date with the defined format.
			String reportDate = df.format(horas);				
			
			listadoTurnosExistentes.add(reportDate);
				
		}
		return listadoTurnosExistentes;
	}
	
	// Crea una lista con los horarios disponibles en un día.
	public ArrayList turnosDisponibles(String fecha, String horaInicio, String horaFinaliza) throws SQLException, ParseException {
		
		ArrayList<String> result = turnosEnDia( horaInicio, horaFinaliza);
		result.removeAll(turnosExistenEnDia(fecha));
		
		return result;
	}
	
	
}
