package com.cesar.decrypt.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AnswerDTO {

	@JsonProperty("numero_casas")
	private Integer numero_casas;
	private String token;
	private String cifrado;
	private String decifrado;
	@JsonProperty("resumo_criptografico")
	private String resumo_criptografico;
	
	public AnswerDTO() {}
	
	public AnswerDTO(Integer numero_casas, String token, String cifrado, String decifrado,
			String resumo_criptografico) {
		this.numero_casas = numero_casas;
		this.token = token;
		this.cifrado = cifrado;
		this.decifrado = decifrado;
		this.resumo_criptografico = resumo_criptografico;
	}
	public Integer getNumero_casas() {
		return numero_casas;
	}
	public void setNumero_casas(Integer numero_casas) {
		this.numero_casas = numero_casas;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getCifrado() {
		return cifrado;
	}
	public void setCifrado(String cifrado) {
		this.cifrado = cifrado;
	}
	public String getDecifrado() {
		return decifrado;
	}
	public void setDecifrado(String decifrado) {
		this.decifrado = decifrado;
	}
	public String getResumo_criptografico() {
		return resumo_criptografico;
	}
	public void setResumo_criptografico(String resumo_criptografico) {
		this.resumo_criptografico = resumo_criptografico;
	}
	
	
	
}
