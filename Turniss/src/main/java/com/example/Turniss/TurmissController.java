package com.example.Turniss;

import java.sql.SQLException;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TurmissController {
	
	@GetMapping("/inicio")
	public String inicio(HttpSession session, Model template) throws SQLException {
		
		TurnissHelper.usuarioLogueado(session, template);		
		
		return "inicio";
	}
	
	@GetMapping("/mapa")
	public String mapa(HttpSession session, Model template) throws SQLException {
		
		TurnissHelper.usuarioLogueado(session, template);
		
		return "MapaGoogle";
	}
	
	@GetMapping("/contacto")
	public String contacto(HttpSession session, Model template) throws SQLException {
		
		TurnissHelper.usuarioLogueado(session, template);
		
		return "Contacto";
	}
	
}
