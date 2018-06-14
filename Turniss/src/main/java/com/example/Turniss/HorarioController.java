package com.example.Turniss;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.model.Horario;
import com.example.model.Turno;

public class HorarioController {

	// Pantalla para ingresar horario.
	@GetMapping("/registro-horario")
	public String registroHorario() {
		return "RegistroHorario";
	}
		
	// Procesar el ingreso de horarios.
	@GetMapping ("/procesar-horario")
	public String procesarHorario(@RequestParam String medico, @RequestParam String comienza, @RequestParam String termina, @RequestParam String dia) throws SQLException{
		Connection connection;
		connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/Turnos","postgres","admin");
			
		PreparedStatement consulta = 
				connection.prepareStatement("INSERT INTO horarios(medico, comienza, termina, dia) VALUES(?, ?, ?, ?,?);");
			
		consulta.setString(1, medico);
		consulta.setString(2, comienza);
		consulta.setString(3, termina);
		consulta.setString(4, dia);
			
		consulta.executeUpdate();
		
		connection.close();
		return "redirect:/layout-inicio";
	}

	
	// Eliminar turno por id
	@GetMapping("/eliminar-horario/{id}")
	public String eliminarHorario(Model template, @PathVariable int id) throws SQLException {
		Connection connection;
		connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/Turnos","postgres","admin");
				
		PreparedStatement consulta = connection.prepareStatement("DELETE FROM horario WHERE id = ?;");
		consulta.setInt(1, id);
		consulta.executeUpdate();
				
		connection.close();
		return "redirect:/";
	}
	
	// Editar Horario por id
	@GetMapping("/editar-horario/{id}")
	public String editarHorario(Model template, @PathVariable int id) throws SQLException {
				
		Connection connection;
		connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/Turnos","postgres","admin");
			
		PreparedStatement consulta = 
				connection.prepareStatement("SELECT * FROM horarios WHERE id = ?;");
			
		consulta.setInt(1, id);
			ResultSet resultado = consulta.executeQuery();
			
		if ( resultado.next() ) {
			String medico = resultado.getString("medico");
			String comienza = resultado.getString("comienza");
			String termina = resultado.getString("termina");
			String dia = resultado.getString("dia");
							
			template.addAttribute("medico", medico);
			template.addAttribute("empieza", comienza);
			template.addAttribute("termina", termina);
			template.addAttribute("dia", dia);
				
		}
				
		return "EditarHorario";
	}

	// Procesar editar Horario
	@GetMapping("/modificar-horario/{id}")
	public String modificarHorario(@PathVariable int id, @RequestParam String medico, @RequestParam String comienza, @RequestParam String termina, @RequestParam String dia) throws SQLException {
		Connection connection;
		connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/Turnos","postgres","admin");
			
		PreparedStatement consulta = 
		connection.prepareStatement("UPDATE turnos SET paciente = ?, comienza = ?, termina = ?, dia = ? WHERE id = ?;");
		consulta.setString(1, medico);
		consulta.setString(2, comienza);
		consulta.setString(3, termina);
		consulta.setString(4, dia);
		consulta.setInt(5, id);
				
		consulta.executeUpdate();
				
		connection.close();
		return "redirect:/detalle-usuario/" + id;
	}

	// Mostrar detalle de datos de un horario
	@GetMapping("/detalle-horario/{id}")
	public String detalleHorario(Model template, @PathVariable int id) throws SQLException {
			
		Connection connection;
		connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/Turnos","postgres","admin");
				
		PreparedStatement consulta = 
				connection.prepareStatement("SELECT * FROM horarios WHERE id = ?;");
			
		consulta.setInt(1, id);

		ResultSet resultado = consulta.executeQuery();
				
			if ( resultado.next() ) {
				String medico = resultado.getString("medico");
				String comienza = resultado.getString("comienza");
				String termina = resultado.getString("termina");
				String dia = resultado.getString("dia");
				
				template.addAttribute("medico", medico);
				template.addAttribute("comienza", comienza);
				template.addAttribute("termina", termina);
				template.addAttribute("dia", dia);
			}
		return "DetalleTurno";
	}
			
	// Mostrar listado con todos los horarios
	@GetMapping("/listado-horarios")
	public String listadoHorarios(Model template) throws SQLException {
			
		Connection connection;
		connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/Turnos","postgres","admin");
				
		PreparedStatement consulta = connection.prepareStatement("SELECT * FROM horarios;");
				
		ResultSet resultado = consulta.executeQuery();
				
		ArrayList<Horario> listadoHorarios = new ArrayList<Horario>();
				
		while ( resultado.next() ) {
			int id = resultado.getInt("id");
			String medico = resultado.getString("medico");
			String comienza = resultado.getString("comienza");
			String termina = resultado.getString("termina");
			String dia = resultado.getString("dia");
				
			Horario x = new Horario(id, medico, comienza, termina, dia);
			listadoHorarios.add(x);
		}
			
		template.addAttribute("listadoHorarios", listadoHorarios);
			
		return "listadoHorarios";
	}	

	// Procesa la busqueda de horarios por medico
	@GetMapping("/procesarBusqueda-horarios")
	public String procesarBusquedaTurnos(Model template, @RequestParam String palabraBuscada) throws SQLException {
						
		Connection connection;
		connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/Turnos","postgres","admin");
					
		PreparedStatement consulta = connection.prepareStatement("SELECT * FROM horarios WHERE medico LIKE ?;");
		consulta.setString(1, "%" + palabraBuscada +  "%");
					
		ResultSet resultado = consulta.executeQuery();
						
		ArrayList<Horario> listadoHorarios = new ArrayList<Horario>();
						
		while ( resultado.next() ) {
			int id = resultado.getInt("id");
			String medico = resultado.getString("medico");
			String comienza = resultado.getString("comienza");
			String termina = resultado.getString("termina");
			String dia = resultado.getString("dia");
				
			Horario x = new Horario(id, medico, comienza, termina, dia);
			listadoHorarios.add(x);
		}
						
		template.addAttribute("listadoHorarios", listadoHorarios);
			
		return "listadoHorarios";
	}	
	
}