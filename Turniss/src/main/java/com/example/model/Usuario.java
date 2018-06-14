package com.example.model;

public class Usuario {

	private int id;
	private String email;
	private String tipo;
	private String codigo;
	
	public Usuario(int id, String email, String tipo, String codigo) {
		super();
		this.id = id;
		this.email = email;
		this.tipo = tipo;
		this.codigo = codigo;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	
	
	
	
}
