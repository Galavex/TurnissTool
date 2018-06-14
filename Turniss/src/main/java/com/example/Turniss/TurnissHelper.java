package com.example.Turniss;

import java.sql.SQLException;

import javax.servlet.http.HttpSession;

import org.springframework.ui.Model;

public class TurnissHelper {
	
	public static void usuarioLogueado(HttpSession session, Model template) throws SQLException {
		
		if (UsuariosHelper.pacienteLogueado(session) == null) {
	
	        template.addAttribute("estaLogeado", false);
	
	    } else {
	
	        template.addAttribute("estaLogeado", true);
	
	    }
	}	
}
