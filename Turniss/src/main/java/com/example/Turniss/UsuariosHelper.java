package com.example.Turniss;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.example.model.Medico;
import com.example.model.Paciente;

@Service
public class UsuariosHelper {
		
	private Environment env;
	
	public static boolean intentarLoguearPaciente(HttpSession session, String email, String password) throws SQLException{
		Connection connection;
		connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/Turnos","postgres","CDU31A");
		
		PreparedStatement consulta = 
				connection.prepareStatement("SELECT * FROM pacientes WHERE email = ? AND password = ?;");

		consulta.setString(1, email);
		consulta.setString(2, password);
		
		ResultSet resultado = consulta.executeQuery();
		
		if ( resultado.next() ) {
			String codigo = UUID.randomUUID().toString();
			session.setAttribute("codigo", codigo);

			consulta = connection.prepareStatement("UPDATE usuarios SET codigo = ? WHERE email = ?;");
			consulta.setString(1, codigo);
			consulta.setString(2, email);

			consulta.executeUpdate();
			
			return true;
		} else {
			return false;
		}
	}	

	public static Paciente pacienteLogueado(HttpSession session) throws SQLException{
		String codigo = (String)session.getAttribute("codigo");

		if (codigo != null) {
			// OBTENER EL USUARIO DE LA BASE
			Connection connection;
			connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/Turnos","postgres","CDU31A");
			
			PreparedStatement consulta = 
					connection.prepareStatement("SELECT * FROM usuarios WHERE tipo = ? AND codigo = ?;");
			
			consulta.setString(1, "paciente");
			consulta.setString(2, codigo);
			
			ResultSet resultado = consulta.executeQuery();
			
			if ( resultado.next() ) {
				// ARMAR Y DEVOLVE ESE USUARIO
				String localId = resultado.getString("id");
				
				PreparedStatement consulta2 = 
						connection.prepareStatement("SELECT * FROM pacientes WHERE id_usuario = ?;");
				
				consulta2.setString(1, localId);
				ResultSet resultado2 = consulta2.executeQuery();
				if ( resultado2.next() ) {
					Paciente logueado = new Paciente( resultado2.getString("id_usuario"), resultado2.getString("nombre"), resultado2.getString("apellido"), resultado2.getInt("dni"), resultado2.getString("direccion"), resultado2.getInt("telefono"), resultado2.getString("email"), resultado2.getString("password") );
					return logueado;
				} else {
					return null;
				}
				
				
			} else {
				return null;
			}
			
			
		} else {
			return null;
		}
	}

	public static boolean intentarLoguearMedico(HttpSession session, String email, String password) throws SQLException{
		Connection connection;
		connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/Turnos","postgres","CDU31A");
		
		PreparedStatement consulta = 
				connection.prepareStatement("SELECT * FROM medicos WHERE email = ? AND password = ?;");

		consulta.setString(1, email);
		consulta.setString(2, password);
		
		ResultSet resultado = consulta.executeQuery();
		
		if ( resultado.next() ) {
			String codigo = UUID.randomUUID().toString();
			session.setAttribute("codigo-autorizacion", codigo);

			consulta = connection.prepareStatement("UPDATE usuarios SET codigo = ? WHERE email = ?;");
			consulta.setString(1, codigo);
			consulta.setString(2, email);

			consulta.executeUpdate();
			
			return true;
		} else {
			return false;
		}
	}
	
	public static Medico medicoLogueado(HttpSession session) throws SQLException{
		String codigo = (String)session.getAttribute("codigo-autorizacion");

		if (codigo != null) {
			// OBTENER EL USUARIO DE LA BASE
			Connection connection;
			connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/Turnos","postgres","CDU31A");
			
			PreparedStatement consulta = 
					connection.prepareStatement("SELECT * FROM usuarios WHERE tipo = ? AND codigo = ?;");
			
			consulta.setString(1, "medico");
			consulta.setString(2, codigo);
			
			ResultSet resultado = consulta.executeQuery();
			
			if ( resultado.next() ) {
				// ARMAR Y DEVOLVE ESE USUARIO
				Medico logueado = new Medico( resultado.getString("id_usuario"), resultado.getString("nombre"), resultado.getString("apellido"), resultado.getInt("matricula"), resultado.getString("especialidad"), resultado.getString("password"), resultado.getString("email"));
				return logueado;
			} else {
				return null;
			}
			
			
		} else {
			return null;
		}
	}

	public static void cerrarSesion(HttpSession session) throws SQLException{

		String codigo = (String)session.getAttribute("codigo");
		
		session.removeAttribute("codigo");
		
		Connection connection;
		connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/Turnos","postgres","CDU31A");
		
		PreparedStatement consulta = 
				connection.prepareStatement("Update usuarios SET codigo = ? WHERE codigo = ?;");
		
		consulta.setString(1, "");
		consulta.setString(2, codigo);
		
		consulta.executeUpdate();
		connection.close();
		
	}
	
	// Devuelve el id del usuario logueado.
	public static String idLogueado(HttpSession session) throws SQLException {
		Connection connection;
		connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/Turnos","postgres","CDU31A");
		
		String codigo = (String)session.getAttribute("codigo");
		
		
		PreparedStatement consulta = 
				connection.prepareStatement("SELECT id FROM usuarios WHERE codigo = ?;");
		
		consulta.setString(1, codigo);
		ResultSet resultado = consulta.executeQuery();
		if ( resultado.next() ) {
		String localId = resultado.getString("id");
		
		return localId;
		} else {
			return null;
		}
	}
	
	public static String emailLogueado(HttpSession session)throws SQLException{
		Connection connection;
		connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/Turnos","postgres","CDU31A");
		
		String codigo = (String)session.getAttribute("codigo");
		
		PreparedStatement consulta = 
				connection.prepareStatement("SELECT id FROM usuarios WHERE codigo = ?;");
		consulta.setString(1, codigo);
		
		ResultSet resultado = consulta.executeQuery();
		if ( resultado.next() ) {
		String localEmail = resultado.getString("email");
		
		return localEmail;
		} else {
			return null;
		}
	}
		
	
	
	
	public static String nombreMedicoSegunId(String id) throws SQLException {
		
		Connection connection;
		connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/Turnos","postgres","CDU31A");
		
		PreparedStatement consulta = connection.prepareStatement("SELECT * FROM medicos WHERE id_usuario = ?;");
		
		consulta.setString(1, id);
		ResultSet resultado = consulta.executeQuery();
		if ( resultado.next() ) {
			String nombre = resultado.getString("nombre");
			String apellido = resultado.getString("apellido");
			
			return nombre + " " + apellido;
			
		} else { 
			return null;
		}
		
	}
	
	
	
}
