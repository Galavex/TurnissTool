package com.example.model;

public class Horario {

	public int id;
	public String medico;
	public String comienza;
	public String termina;
	public String dia;
	
	public Horario(int id, String medico, String comienza, String termina, String dia) {
		super();
		this.id = id;
		this.medico = medico;
		this.comienza = comienza;
		this.termina = termina;
		this.dia = dia;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getMedico() {
		return medico;
	}

	public void setMedico(String medico) {
		this.medico = medico;
	}

	public String getComienza() {
		return comienza;
	}

	public void setComienza(String comienza) {
		this.comienza = comienza;
	}

	public String getTermina() {
		return termina;
	}

	public void setTermina(String termina) {
		this.termina = termina;
	}

	public String getDia() {
		return dia;
	}

	public void setDia(String dia) {
		this.dia = dia;
	}
	
	
	
}
