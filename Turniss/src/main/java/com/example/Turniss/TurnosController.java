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
import java.util.Date;

import javax.servlet.http.HttpSession;

import org.simplejavamail.email.Email;
import org.simplejavamail.email.EmailBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.model.Medico;
import com.example.model.Turno;
import com.example.model.TurnoBasico;

@Controller
public class TurnosController {

	@Autowired
	private Environment env;
	
	@Autowired
    private TurnosHelper TurnosHelper;
	
	// Pantalla para ingresar turnos.
	@GetMapping("/registro-turno")
	public String registroTurno(HttpSession session, Model template) throws SQLException {
		
		TurnissHelper.usuarioLogueado(session, template);
		
		if (UsuariosHelper.pacienteLogueado(session) == null) {
			return "redirect:/login-paciente";
		} else {
			return "RegistroTurnos";
		}
	}
	
	// Procesa la busqueda de usuarios por datos del usuario
	@GetMapping("/procesarBusqueda-turnos")
	public String procesarBusquedaTurnos( HttpSession session, Model template, @RequestParam String medico,  @RequestParam String fecha) throws SQLException, ParseException {
		
		TurnissHelper.usuarioLogueado(session, template);
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date date1 = format.parse(fecha);
		java.sql.Date sqlDate = new java.sql.Date(date1.getTime());
			
		
		SimpleDateFormat newDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date2 = newDateFormat.parse(fecha);
		newDateFormat.applyPattern("EEEE");
		String diaSemana = newDateFormat.format(date2);
				
		//String horario = hora;
		//DateFormat formatter = new SimpleDateFormat("HH:mm");
		//java.sql.Time timeValue = new java.sql.Time(formatter.parse(horario).getTime());	
		
		Connection connection;
		connection = DriverManager.getConnection(env.getProperty("DB_URL"),env.getProperty("DB_USERNAME"),env.getProperty("DB_PASSWORD"));
		
		// Se fija si el médico atiende el día (L,M,M,J,V,S) ingresado.
		PreparedStatement consultaDiaSemana = connection.prepareStatement("SELECT * FROM horarios JOIN medicos ON horarios.medico = medicos.id_usuario WHERE dia = ? AND medico = ?;");
		
		consultaDiaSemana.setString(1, diaSemana);
		consultaDiaSemana.setString(2, medico);
		
		ResultSet resultadoDiaSemana = consultaDiaSemana.executeQuery();
		
			
		if ( resultadoDiaSemana.next()) {
						
			Time localHoraComienza = resultadoDiaSemana.getTime("comienza"); // Toma el horario que comienza a atender el medico 
			DateFormat dFormat = new SimpleDateFormat("HH:mm");
			// Using DateFormat format method we can create a string 
			// representation of a date with the defined format.
			String horaComienza = dFormat.format(localHoraComienza);
			
			Time localHoraTermina = resultadoDiaSemana.getTime("termina"); // Toma el horario que termina de atender el medico 
			
			String horaTermina = dFormat.format(localHoraTermina);
			
			// Crea una Array list con los horarios disponibles en la fecha ingresada.
			ArrayList<String> listadoHorariosDisponibles = TurnosHelper.turnosDisponibles(fecha, horaComienza, horaTermina);	
			
			ArrayList<Turno> listadoTurnos = new ArrayList<Turno>();
			
			String id_usuario = resultadoDiaSemana.getString("id_usuario");
			String nombre = resultadoDiaSemana.getString("nombre");
			String apellido = resultadoDiaSemana.getString("apellido");
			int matricula = resultadoDiaSemana.getInt("matricula");
			String especialidad = resultadoDiaSemana.getString("especialidad");
			String password = resultadoDiaSemana.getString("password");
			String email = resultadoDiaSemana.getString("email");
			
			Medico localMedico = new Medico(id_usuario, nombre, apellido, matricula, especialidad, password, email);
			
			
			for(int i=0; i < listadoHorariosDisponibles.size(); i++) {
				
				int id = 1;
				String paciente = UsuariosHelper.idLogueado(session);
				String fechaElegida = fecha;
				String horarioElegido = listadoHorariosDisponibles.get(i);
								
				
				Turno x = new Turno(id, paciente, localMedico, fechaElegida, horarioElegido);
				listadoTurnos.add(x);
					
			}
				
			template.addAttribute("listadoTurnos", listadoTurnos);
			
			return "RegistroTurnos";
			
		} else {
			
			template.addAttribute("alerta", "No hay turnos disponibles para esta fecha, intentelo nuevamente.");
			
			return "RegistroTurnos";
		}
		
	}
	
	
	// Procesar el ingreso de turnos.
	@GetMapping ("/procesar-turno")
	public String procesarTurno( HttpSession session, Model template, @RequestParam String fecha, @RequestParam String hora, @RequestParam String id) throws SQLException, ParseException {
				
		Connection connection;
		connection = DriverManager.getConnection(env.getProperty("DB_URL"),env.getProperty("DB_USERNAME"),env.getProperty("DB_PASSWORD"));
			
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MMO-dd");
		Date date = format.parse(fecha);
		java.sql.Date sqlDate = new java.sql.Date(date.getTime());
					
		String horario = hora;
		DateFormat formatter = new SimpleDateFormat("HH:mm");
		java.sql.Time timeValue = new java.sql.Time(formatter.parse(horario).getTime());
			
		PreparedStatement consulta3 = 
			connection.prepareStatement("INSERT INTO turnos(id_medico, id_paciente, hora, fecha) VALUES(?, ?, ?,?);");
				
			consulta3.setString(1, id);
			consulta3.setString(2, UsuariosHelper.idLogueado(session)); // idLogueado devuelve el id del usuario logueado.
			consulta3.setTime(3, timeValue);
			consulta3.setDate(4, sqlDate);
							
			consulta3.executeUpdate();
				
			connection.close();
			
						
			return "redirect:/inicio";
	}
				
	
	
	// Eliminar turno por id
	@GetMapping("/eliminar-turno")
	public String eliminarTurno(Model template, @RequestParam int id) throws SQLException {
		Connection connection;
		connection = DriverManager.getConnection(env.getProperty("DB_URL"),env.getProperty("DB_USERNAME"),env.getProperty("DB_PASSWORD"));
			
		PreparedStatement consulta = connection.prepareStatement("DELETE FROM turnos WHERE id = ?;");
		consulta.setInt(1, id);
		consulta.executeUpdate();
			
		connection.close();
		return "redirect:/listado-turnos-paciente";
	}

	
	// Mostrar listado con todos los turnos de un paciente
	@GetMapping("/listado-turnos-paciente")
	public String listado(HttpSession session, Model template) throws SQLException {
		
		TurnissHelper.usuarioLogueado(session, template);
		
		Connection connection;
		connection = DriverManager.getConnection(env.getProperty("DB_URL"),env.getProperty("DB_USERNAME"),env.getProperty("DB_PASSWORD"));
			
		PreparedStatement consulta = connection.prepareStatement("SELECT * FROM turnos WHERE id_paciente = ? ORDER BY fecha DESC;;");
		
		consulta.setString(1, UsuariosHelper.idLogueado(session));
		
		ResultSet resultado = consulta.executeQuery();
		
		
		ArrayList<TurnoBasico> turnos = new ArrayList<TurnoBasico>();
		
		while (resultado.next()) {
			
			int id = resultado.getInt("id");
			String medico = resultado.getString("id_medico");
			String fecha = resultado.getString("fecha");
			String hora = resultado.getString("hora");
			
			TurnoBasico x = new TurnoBasico(id,UsuariosHelper.nombreMedicoSegunId(medico), fecha, hora);
			turnos.add(x);
			
		} 
		
		template.addAttribute("listadoTurnos", turnos);
		
			
		return "ListadoTurnos";
	}
	
	  // Editar Turno por id
	@GetMapping("/editar-turno")
	public String editarTurno(Model template, @RequestParam int id) throws SQLException {
			
		Connection connection;
		connection = DriverManager.getConnection(env.getProperty("DB_URL"),env.getProperty("DB_USERNAME"),env.getProperty("DB_PASSWORD"));
		
		PreparedStatement consulta = 
				connection.prepareStatement("SELECT * FROM turnos WHERE id = ?;");
		
		consulta.setInt(1, id);
		ResultSet resultado = consulta.executeQuery();
		
		if ( resultado.next() ) {
			
			String medico = resultado.getString("id_medico");
			String fecha = resultado.getString("fecha");
			String horario = resultado.getString("hora");
						
			
			template.addAttribute("medico", medico);
			template.addAttribute("fecha", fecha);
			template.addAttribute("horario", horario);
			
		}
			
		return "EditarTurno";
	}

	// Procesar editar Turno
	@GetMapping("/modificar-turno")
	public String modificarTurno(@RequestParam int id, @RequestParam String medico, @RequestParam String especialidad, @RequestParam String fecha, @RequestParam String hora) throws SQLException, ParseException {
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd");
		Date date = format.parse(fecha);
		java.sql.Date sqlDate = new java.sql.Date(date.getTime());
					
		String horario = hora;
		DateFormat formatter = new SimpleDateFormat("HH:mm");
		java.sql.Time timeValue = new java.sql.Time(formatter.parse(horario).getTime());
		
		Connection connection;
		connection = DriverManager.getConnection(env.getProperty("DB_URL"),env.getProperty("DB_USERNAME"),env.getProperty("DB_PASSWORD"));
		
		PreparedStatement consulta = 
		connection.prepareStatement("UPDATE turnos SET id_medico = ?, fecha = ?, hora = ? WHERE id = ?;");
		
		consulta.setString(1, medico);
		consulta.setDate(2, sqlDate);
		consulta.setTime(3, timeValue);
		consulta.setInt(4, id);
				
		consulta.executeUpdate();
				
		connection.close();
		return "redirect:/detalle-usuario" ;
	}

	// Mostrar detalle de datos de un turno
	@GetMapping("/detalle-turno/{id}")
	public String detalleTurno(Model template, @PathVariable int id) throws SQLException {
		
		Connection connection;
		connection = DriverManager.getConnection(env.getProperty("DB_URL"),env.getProperty("DB_USERNAME"),env.getProperty("DB_PASSWORD"));
			
		PreparedStatement consulta = 
				connection.prepareStatement("SELECT * FROM turnos WHERE id = ?;");
			
		consulta.setInt(1, id);

		ResultSet resultado = consulta.executeQuery();
			
		if ( resultado.next() ) {
			String paciente = resultado.getString("paciente");
			String medico = resultado.getString("medico");
			String especialidad = resultado.getString("especialidad");
			String fecha = resultado.getString("fecha");
			String horario = resultado.getString("horario");
			
			
				
			template.addAttribute("paciente", paciente);
			template.addAttribute("medico", medico);
			template.addAttribute("especialidad", especialidad);
			template.addAttribute("fecha", fecha);
			template.addAttribute("horario", horario);
		}
		return "DetalleUsuario";
	}
		
		
}	
