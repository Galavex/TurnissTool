package com.example.model;

public class TurnoBasico {

	private int id;
	private String medico;
	private String fecha;
	private String hora;
	
	public TurnoBasico(int id, String medico, String fecha, String hora) {
		super();
		
		this.id = id;
		this.medico = medico;
		this.fecha = fecha;
		this.hora = hora;
	}

	public String getMedico() {
		return medico;
	}

	public void setMedico(String medico) {
		this.medico = medico;
	}

	public String getFecha() {
		return fecha;
	}

	public void setFecha(String fecha) {
		this.fecha = fecha;
	}

	public String getHora() {
		return hora;
	}

	public void setHora(String hora) {
		this.hora = hora;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	
	
	
	
	
	
}
