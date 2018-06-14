package com.example.Turniss;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import javax.servlet.http.HttpSession;

import org.simplejavamail.email.Email;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UsuariosController {
	
	// Comodín para la coneccion a la base de datos
	@Autowired
	private Environment env;
	
	// Pantalla para ingresar paciente.
	@GetMapping("/registro-paciente")
	public String registroPaciente() {
		return "RegistroPacientes";
	}
		
	// Procesar el ingreso de nuevo paciente a la base de datos.
	@PostMapping("/insertar-paciente")
	public String insertarPaciente(@RequestParam String nombre, @RequestParam String password, @RequestParam String apellido, @RequestParam String email, @RequestParam String direccion, @RequestParam int dni, @RequestParam int telefono) throws SQLException {
		
		String id = UUID.randomUUID().toString();
		
		Connection connection;
		connection = DriverManager.getConnection(env.getProperty("DB_URL"),env.getProperty("DB_USERNAME"),env.getProperty("DB_PASSWORD"));
		
		PreparedStatement consulta1 = connection.prepareStatement("INSERT INTO usuarios(id, email, tipo ) VALUES(?, ?, ?);");
				
		consulta1.setString(1, id);
		consulta1.setString(2, email);
		consulta1.setString(3, "paciente");
		
		consulta1.executeUpdate();
		
		PreparedStatement consulta2 = 
				connection.prepareStatement("INSERT INTO pacientes(id_usuario, nombre, apellido, dni, email, telefono, direccion, password) "
						+ "VALUES(?, ?, ?, ?, ?,?, ?, ?);");
		
		consulta2.setString(1, id);
		consulta2.setString(2, nombre);
		consulta2.setString(3, apellido);
		consulta2.setInt(4, dni);
		consulta2.setString(5, email);
		consulta2.setInt(6, telefono);
		consulta2.setString(7, direccion);
		consulta2.setString(8, password);				
				
		consulta2.executeUpdate();
		
		connection.close();
		
		//Simple Java Email en conjunto con SendGrid
		// Setting --> APIkey --> crear
		/*Email emailBienvenida = EmailBuilder.startingBlank()
			    .from("Michel Baker", "m.baker@mbakery.com")
			    .to("mom", "jean.baker@hotmail.com")
			    .to("dad", "StevenOakly1963@hotmail.com")
			    .withSubject("My Bakery is finally open!")
			    .withPlainText("Mom, Dad. We did the opening ceremony of our bakery!!!")
			    .buildEmail();

			MailerBuilder
			// Password con variable de entorno, username = apikey, server = smtp.sendgrid.net
			  .withSMTPServer("server", 587, "username", "password")
			  .buildMailer()
			  .sendMail(emailBienvenida);
		*/
		
		
		
		
		return "redirect:/inicio";
				
	}
		
	// Pantalla para ingresar medico.
	@GetMapping("/registro-medico")
	public String registroMedico() {
		return "RegistroMedicos";
	}
		
	// Procesar el ingreso de nuevo médico a la base de datos.
	@GetMapping("/insertar-medico")
	public String insertarMedico(@RequestParam String nombre, @RequestParam String apellido, @RequestParam int matricula, @RequestParam String especialidad, @RequestParam String password, @RequestParam String email) throws SQLException {
		String id = UUID.randomUUID().toString();
		
		Connection connection;
		connection = DriverManager.getConnection(env.getProperty("DB_URL"),env.getProperty("DB_USERNAME"),env.getProperty("DB_PASSWORD"));
		
		PreparedStatement consulta1 = 
				connection.prepareStatement("INSERT INTO usuarios(id, email, tipo ) VALUES(?, ?, ?);");
				
		consulta1.setString(1, id);
		consulta1.setString(2, email);
		consulta1.setString(3, "medico");
		
		consulta1.executeUpdate();
				
		PreparedStatement consulta2 = 
				connection.prepareStatement("INSERT INTO medicos(id_usuario, nombre, apellido, matricula, especialidad, password, email) VALUES(?, ?, ?, ?, ?, ?, ?);");
		
		consulta2.setString(1, id);
		consulta2.setString(2, nombre);
		consulta2.setString(3, apellido);
		consulta2.setInt(4, matricula);
		consulta2.setString(5, especialidad);
		consulta2.setString(6, password);
		consulta2.setString(7, email);
							
		consulta2.executeUpdate();
			
		connection.close();
		return "redirect:/registro-medico";
	}
	
	// Eliminar usuario por ID
	@GetMapping("/eliminar-usuario/{id}")
	public String eliminarUsuario(Model template, @PathVariable String id) throws SQLException {
		Connection connection;
		connection = DriverManager.getConnection(env.getProperty("DB_URL"),env.getProperty("DB_USERNAME"),env.getProperty("DB_PASSWORD"));
		
		PreparedStatement consulta1 = connection.prepareStatement("DELETE FROM usuarios WHERE id = ?;");
		consulta1.setString(1, id);
		consulta1.executeUpdate();
		
		PreparedStatement consulta2 = connection.prepareStatement("DELETE FROM pacientes WHERE id_usuario = ?;");
		consulta2.setString(1, id);
		consulta2.executeUpdate();
		
		PreparedStatement consulta3 = connection.prepareStatement("DELETE FROM medicos WHERE id = ?;");
		consulta3.setString(1, id);
		consulta3.executeUpdate();
		
		connection.close();
		return "redirect:/";
	}
	
	// Editar paciente por id
	@GetMapping("/editar-paciente")
	public String editarPaciente(HttpSession session, Model template) throws SQLException {
		
		TurnissHelper.usuarioLogueado(session, template);
		String id_usuario = UsuariosHelper.idLogueado(session);
		
		Connection connection;
		connection = DriverManager.getConnection(env.getProperty("DB_URL"),env.getProperty("DB_USERNAME"),env.getProperty("DB_PASSWORD"));
		
		PreparedStatement consulta = 
				connection.prepareStatement("SELECT * FROM pacientes WHERE id_usuario = ?;");
		
		consulta.setString(1, id_usuario);
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
		
		return "EditarPaciente";
	}
	
	// Procesar editar paciente
	@GetMapping("/modificar-paciente")
	public String modificarPaciente(HttpSession session, @RequestParam String nombre, @RequestParam String password, @RequestParam String apellido, @RequestParam String email, @RequestParam String direccion, @RequestParam int dni, @RequestParam int telefono) throws SQLException {
		
		String id_usuario = UsuariosHelper.idLogueado(session);
		
		Connection connection;
		connection = DriverManager.getConnection(env.getProperty("DB_URL"),env.getProperty("DB_USERNAME"),env.getProperty("DB_PASSWORD"));
		
		PreparedStatement consulta1 = 
				connection.prepareStatement("UPDATE pacientes SET nombre = ?, apellido = ?, dni = ?, direccion = ?, telefono = ?,  email = ?, password = ? WHERE id_usuario = ?;");
		
		consulta1.setString(1, nombre);
		consulta1.setString(2, apellido);
		consulta1.setInt(3, dni);
		consulta1.setString(4, direccion);
		consulta1.setInt(5, telefono);
		consulta1.setString(6, email);
		consulta1.setString(7, password);
		consulta1.setString(8, id_usuario);
				
		consulta1.executeUpdate();
		
		PreparedStatement consulta2 = connection.prepareStatement("UPDATE usuarios SET email = ? WHERE id = ?;");
		consulta2.setString(1, email);
		consulta2.setString(2, id_usuario);
		
		consulta2.executeUpdate();
		
		connection.close();
		return "redirect:/detalle-paciente";
	}	
	
	// Editar medico por id
	@GetMapping("/editar-medico/{id}")
	public String editarMedico(Model template, @PathVariable String id_usuario) throws SQLException {
			
		Connection connection;
		connection = DriverManager.getConnection(env.getProperty("DB_URL"),env.getProperty("DB_USERNAME"),env.getProperty("DB_PASSWORD"));
			
		PreparedStatement consulta = 
				connection.prepareStatement("SELECT * FROM medicos WHERE id_usuario = ?;");
		
		consulta.setString(1, id_usuario);
		ResultSet resultado = consulta.executeQuery();
			
		if ( resultado.next() ) {
			String nombre = resultado.getString("nombre");
			String apellido = resultado.getString("apellido");
			String especialidad = resultado.getString("especialidad");
			String matricula = resultado.getString("matricula");
			String password = resultado.getString("password");
			String email = resultado.getString("email");
						
			template.addAttribute("nombre", nombre);
			template.addAttribute("apellido", apellido);
			template.addAttribute("especialidad", especialidad);
			template.addAttribute("matricula", matricula);
			template.addAttribute("password", password);
			template.addAttribute("email", email);
			
		}
		
		return "EditarMedico";
	}
	
	// Procesar editar medico
	@GetMapping("/modificar-medico/{id}")
	public String modificarUsuario(@PathVariable String id_usuario, @RequestParam String nombre, @RequestParam String apellido, @RequestParam String especialidad, @RequestParam int matricula, @RequestParam String password, @RequestParam String email) throws SQLException {
		Connection connection;
		connection = DriverManager.getConnection(env.getProperty("DB_URL"),env.getProperty("DB_USERNAME"),env.getProperty("DB_PASSWORD"));
		
		PreparedStatement consulta = 
		connection.prepareStatement("UPDATE medicos SET nombre = ?, apellido = ?, especialidad = ?, matricula = ?, password = ?, email = ? WHERE id_usuario = ?;");
		consulta.setString(1, nombre);
		consulta.setString(2, apellido);
		consulta.setString(3, especialidad);
		consulta.setInt(4, matricula);
		consulta.setString(5, password);
		consulta.setString(6, email);
		consulta.setString(7, id_usuario);
				
		consulta.executeUpdate();
				
		PreparedStatement consulta2 = connection.prepareStatement("UPDATE usuarios SET email = ? WHERE id = ?;");
		consulta2.setString(1, email);
		consulta2.setString(2, id_usuario);
		
		consulta2.executeUpdate();
		
		connection.close();
		return "redirect:/detalle-medico/" + id_usuario;
	}

	// formulario vacio para loguearse
		@GetMapping("/login-paciente")
		public String loginPaciente() {
		return "loginPaciente";
	}
			
	// Procesar login paciente	
	@PostMapping("/procesar-login-paciente")
	public String procesarLoginPaciente(HttpSession session, @RequestParam String email, @RequestParam String password) throws SQLException {
		boolean sePudo = UsuariosHelper.intentarLoguearPaciente(session, email, password);
			
		if (sePudo) {
			return "redirect:/inicio";
		} else {
			return "loginPacientes";
		}
	}
	
	// formulario vacio para loguearse medicos
	@GetMapping("/login-medico")
	public String loginMedico() {
		return "loginMedicos";
	}
				
	// Procesar login medicos
	@PostMapping("/procesar-login-medicos")
	public String procesarLoginMedico(HttpSession session, @RequestParam String email, @RequestParam String password) throws SQLException {
		boolean sePudo = UsuariosHelper.intentarLoguearMedico(session, email, password);
				
		if (sePudo) {
			return "redirect:/inicio";
		} else {
			// TODO: pregarcar los datos que lleno, salvo la contrasenia
			return "loginMedicos";
		}
	}
		
	// Logout usuario	
	@GetMapping("/logout")
	public String logoutUsuario(HttpSession session) throws SQLException {
		UsuariosHelper.cerrarSesion(session);
		return "redirect:/inicio";
	}
	
	
}
