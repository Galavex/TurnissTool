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
import org.springframework.web.bind.annotation.RequestParam;

import com.example.model.Paciente;


@Controller
public class PacientesController {
	
	// Comodín para la coneccion a la base de datos
	@Autowired
	private Environment env;
	
	// Mostrar detalle de datos de un usuario
	@GetMapping("/detalle-paciente")
	public String detalleUsuario(HttpSession session, Model template) throws SQLException {
		
		TurnissHelper.usuarioLogueado(session, template);
		
		Connection connection;
		connection = DriverManager.getConnection(env.getProperty("DB_URL"),env.getProperty("DB_USERNAME"),env.getProperty("DB_PASSWORD"));
		
		PreparedStatement consulta = 
				connection.prepareStatement("SELECT * FROM pacientes WHERE id_usuario = ?;");
		
		consulta.setString(1, UsuariosHelper.idLogueado(session));

		ResultSet resultado = consulta.executeQuery();
		
		if ( resultado.next() ) {
			String nombre = resultado.getString("nombre");
			String apellido = resultado.getString("apellido");
			String dni = resultado.getString("dni");
			String telefono = resultado.getString("telefono");
			String direccion = resultado.getString("direccion");
			String email = resultado.getString("email");
			String password = resultado.getString("password");
			
			
			template.addAttribute("nombre", nombre);
			template.addAttribute("apellido", apellido);
			template.addAttribute("dni", dni);
			template.addAttribute("telefono", telefono);
			template.addAttribute("direccion", direccion);
			template.addAttribute("mail", email);
			template.addAttribute("password", password);
		}
		return "DetallePaciente";
	}
	
	// Mostrar listado con todos los usuarios
	@GetMapping("/listado-usuarios")
	public String listado(Model template) throws SQLException {
		
		Connection connection;
		connection = DriverManager.getConnection(env.getProperty("DB_URL"),env.getProperty("DB_USERNAME"),env.getProperty("DB_PASSWORD"));
		
		PreparedStatement consulta = connection.prepareStatement("SELECT * FROM usuarios;");
		
		ResultSet resultado = consulta.executeQuery();
		
		ArrayList<Paciente> listadoUsuarios = new ArrayList<Paciente>();
		
		while ( resultado.next() ) {
			String id = resultado.getString("id");
			String nombre = resultado.getString("nombre");
			String apellido = resultado.getString("apellido");
			int dni = resultado.getInt("dni");
			String direccion = resultado.getString("direccion");
			int telefono = resultado.getInt("telefono");
			String mail = resultado.getString("mail");
			String password = resultado.getString("password");
			
			Paciente x = new Paciente(id, nombre, apellido, dni, direccion, telefono, mail, password);
			listadoUsuarios.add(x);
		}
		
		template.addAttribute("listadoUsuarios", listadoUsuarios);
		
		return "listadoUsuarios";
	}
	
	// Procesa la busqueda de usuarios por nombre
	@GetMapping("/procesarBusqueda")
	public String procesarBusquedaUsuario(Model template, @RequestParam String palabraBuscada) throws SQLException {
				
		Connection connection;
		connection = DriverManager.getConnection(env.getProperty("DB_URL"),env.getProperty("DB_USERNAME"),env.getProperty("DB_PASSWORD"));
				
		PreparedStatement consulta = connection.prepareStatement("SELECT * FROM usuarios WHERE nombre LIKE ?;");
		consulta.setString(1, "%" + palabraBuscada +  "%");
				
		ResultSet resultado = consulta.executeQuery();
				
		ArrayList<Paciente> listadoUsuarios = new ArrayList<Paciente>();
				
		while ( resultado.next() ) {
			String id = resultado.getString("id");
			String nombre = resultado.getString("nombre");
			String apellido = resultado.getString("apellido");
			int dni = resultado.getInt("dni");
			String direccion = resultado.getString("direccion");
			int telefono = resultado.getInt("telefono");
			String mail = resultado.getString("mail");
			String password = resultado.getString("password");
			
			Paciente x = new Paciente(id, nombre, apellido, dni, direccion, telefono, mail, password);
			listadoUsuarios.add(x);
		}
				
		template.addAttribute("listadoUsuarios", listadoUsuarios);
				
		return "listadoUsuarios";
	}
		
			
}
