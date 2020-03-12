package com.cesar.decrypt.resource;

import java.io.File;
import java.io.FileInputStream;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.cesar.decrypt.model.AnswerDTO;
import com.google.gson.Gson;

import reactor.core.publisher.Flux;

@RestController
@RequestMapping(value = "/cesar")
public class Decrypter {

	static final String alphabet = "abcdefghijklmnopqrstuvxwyz";
	File answer = new File("C:\\Users\\neemi\\Documents\\workspace-sts-3.9.9.RELEASE\\CesarsDecoder\\answer.json");
	static Gson gson = new Gson();

	@GetMapping
	public void getStarterJson() throws Exception {
		
		AnswerDTO answer = WebClient.create()
        .get().uri("https://api.codenation.dev/v1/challenge/dev-ps/generate-data?token=8eab8d5fdd5b05e5b1411dd532aa2aa29f2ec0ce")
        .retrieve().bodyToMono(AnswerDTO.class)
        .block();
	
		save(this.answer, answer);
		
		//Descriptografando
		String decryptedText = this.decrypter(answer.getCifrado(), answer.getNumero_casas());
		answer.setDecifrado(decryptedText);
		
		//Resumo Criptografico
		answer.setResumo_criptografico(setCryptographicResume(answer.getDecifrado()));
			
		save(this.answer, answer);
		
		submistAnswer();

	}
	
	@SuppressWarnings("deprecation")
	public void save(File file, AnswerDTO answer) throws Exception {
		String json = gson.toJson(answer); // Convertendo resposta para string em json
		FileUtils.writeStringToFile(file, json); //Salvando o arquivo
	}

	public String decrypter(String encryptedText, Integer numCasas) throws Exception {

		String decoded = "";
		for (int i = 0; i < encryptedText.length(); i++) {

			char charToBeUncrypted = encryptedText.toCharArray()[i];

			// Se for espaço adiciona um espaço na resposta
			if (charToBeUncrypted == ' ') {
				decoded += " ";
			}
			// Qlqr coisa que não for uma letra do afabeto sera repetida como está
			else if (!alphabet.contains(charToBeUncrypted + "")) {
				decoded += charToBeUncrypted + "";
			}

			else if (alphabet.indexOf(charToBeUncrypted) - numCasas < 0) {
				decoded += alphabet.toCharArray()[alphabet.length()
				                                  + ((alphabet.indexOf(charToBeUncrypted) - numCasas))] + "";
			} else {

				if (charToBeUncrypted != ' ') {
					decoded += alphabet.toCharArray()[alphabet.toString()
					                                  .indexOf((charToBeUncrypted) - numCasas)] + "";
				}
			}
		}

		return decoded; // Setando o texto frase descriptografada
	
	}
	
	public String setCryptographicResume(String decryptedText) throws Exception {
		
		MessageDigest digest =  MessageDigest.getInstance("SHA-1"); 
		byte[] byteArray = digest.digest(decryptedText.getBytes(StandardCharsets.UTF_8));
		

		StringBuilder cryptographicResume = new StringBuilder();
		
		for (int i = 0; i < byteArray.length; i++) {
			String hex = Integer.toHexString(0xff & byteArray[i]);
			
			hex = hex.length() == 1 ? "0" : hex;
			cryptographicResume.append(hex);
		}
		
		return cryptographicResume.toString();
	}
	
	private void submistAnswer() throws Exception {
		
		WebClient.create().post().contentType(MediaType.MULTIPART_FORM_DATA).retrieve();

		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpPost uploadFile = new HttpPost("https://api.codenation.dev/v1/challenge/dev-ps/submit-solution?token=8eab8d5fdd5b05e5b1411dd532aa2aa29f2ec0ce");
		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		builder.addTextBody("answer", "answer", ContentType.MULTIPART_FORM_DATA);

		
		// Anexando o arquivo ao post:
		builder.addBinaryBody( "answer", new FileInputStream(answer), ContentType.MULTIPART_FORM_DATA, answer.getName());

		HttpEntity multipart = builder.build();
		uploadFile.setEntity(multipart);
		
		CloseableHttpResponse response = httpClient.execute(uploadFile);
		HttpEntity responseEntity = response.getEntity();
		responseEntity.getContent();
	
	}
	
	public Flux<String> submitAnswer() throws MalformedURLException {
	    
		final UrlResource resource = new UrlResource(
				"file:///C:\\\\Users\\\\neemi\\\\Documents\\\\workspace-sts-3.9.9.RELEASE\\\\CesarsDecoder\\\\answer.json");

	    MultiValueMap<String, UrlResource> data = new LinkedMultiValueMap<>();
	    data.add("answer", resource);

	    return WebClient.create().post()
	            .uri("https://api.codenation.dev/v1/challenge/dev-ps/submit-solution?token=8eab8d5fdd5b05e5b1411dd532aa2aa29f2ec0ce")
	            .contentType(MediaType.MULTIPART_FORM_DATA)
	            .body(BodyInserters.fromMultipartData(data))
	            .exchange()
	            .flatMap(response -> response.bodyToMono(String.class))
	            .flux();
	}
	
	
}
