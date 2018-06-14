package com.example.Turniss;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.model.Medico;

@Controller
public class MedicosController {
	
	@Autowired
	private Environment env;
	
	// Detalle medico
	@GetMapping("/detalle-medico/{id}")
	public String detalleMedico(Model template, @PathVariable String id) throws SQLException {
			
		Connection connection;
		connection = DriverManager.getConnection(env.getProperty("DB_URL"),env.getProperty("DB_USERNAME"),env.getProperty("DB_PASSWORD"));
		
		PreparedStatement consulta = 
				connection.prepareStatement("SELECT * FROM medicos WHERE id_usuario = ?;");
			
		consulta.setString(1, id);

		ResultSet resultado = consulta.executeQuery();
		
		if ( resultado.next() ) {
			String nombre = resultado.getString("nombre");
			String apellido = resultado.getString("apellido");
			String matricula = resultado.getString("matricula");
			String especialidad = resultado.getString("especialidad");
			String password = resultado.getString("password");
			String email = resultado.getString("email");
						
			template.addAttribute("nombre", nombre);
			template.addAttribute("apellido", apellido);
			template.addAttribute("matricula", matricula);
			template.addAttribute("especialidad", especialidad);
			template.addAttribute("password", password);
			template.addAttribute("email", email);
			
		}
		return "DetalleMedico";
	}	

	// Mostrar listado con todos los medicos
	@GetMapping("/listado-medicos")
	public String listadoMedicos(Model template) throws SQLException {
			
		Connection connection;
		connection = DriverManager.getConnection(env.getProperty("DB_URL"),env.getProperty("DB_USERNAME"),env.getProperty("DB_PASSWORD"));
			
		PreparedStatement consulta = connection.prepareStatement("SELECT * FROM medicos;");
			
		ResultSet resultado = consulta.executeQuery();
			
		ArrayList<Medico> listadoMedicos = new ArrayList<Medico>();
			
		while ( resultado.next() ) {
			String id_usuario = resultado.getString("id_usuario");
			String nombre = resultado.getString("nombre");
			String apellido = resultado.getString("apellido");
			int matricula = resultado.getInt("matricula");
			String especialidad = resultado.getString("especialidad");
			String password = resultado.getString("password");
			String email = resultado.getString("email");
						
			Medico x = new Medico(id_usuario, nombre, apellido, matricula, especialidad, password, email);
			listadoMedicos.add(x);
		}
		
		template.addAttribute("listadoMedicos", listadoMedicos);
		
		return "ListadoMedicos";
	}

	// Procesa la busqueda de medicos por nombre
	@GetMapping("/procesarBusqueda-medicos")
	public String procesarBusquedaMedicos(Model template, @RequestParam String palabraBuscada) throws SQLException {
					
		Connection connection;
		connection = DriverManager.getConnection(env.getProperty("DB_URL"),env.getProperty("DB_USERNAME"),env.getProperty("DB_PASSWORD"));
				
		PreparedStatement consulta = connection.prepareStatement("SELECT * FROM medicos WHERE nombre LIKE ?;");
		consulta.setString(1, "%" + palabraBuscada +  "%");
				
		ResultSet resultado = consulta.executeQuery();
				
		ArrayList<Medico> listadoMedicos = new ArrayList<Medico>();
				
		while ( resultado.next() ) {
			String id_usuario = resultado.getString("id_usuario");
			String nombre = resultado.getString("nombre");
			String apellido = resultado.getString("apellido");
			int matricula = resultado.getInt("matricula");
			String especialidad = resultado.getString("especialidad");
			String password = resultado.getString("password");
			String email = resultado.getString("email");
						
			Medico x = new Medico(id_usuario, nombre, apellido, matricula, especialidad, password, email);
			listadoMedicos.add(x);
		}
				
		template.addAttribute("listadoMedicos", listadoMedicos);
				
		return "ListadoMedicos";
	}
	
}
