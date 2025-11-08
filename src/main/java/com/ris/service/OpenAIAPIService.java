package com.ris.service;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class OpenAIAPIService {

	@Value("${openai.api.key}")
	private String apiKey;
	@Value("${openai.api.url}")
	private String apiUrl;

	@Autowired
	private RestTemplate restTemplate;
	@Autowired
	private ObjectMapper mapper;

	public <T> T callOpenAI(Map<String, Object> body, Class<T> responseType) {
		ResponseEntity<String> response = sendOpenAIRequest(body);
		return mapJSONResponse(response, responseType);
	}

	private ResponseEntity<String> sendOpenAIRequest(Map<String, Object> body) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBearerAuth(apiKey);

		HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
		return restTemplate.exchange(apiUrl, HttpMethod.POST, request, String.class);
	}

	private <T> T mapJSONResponse(ResponseEntity<String> response, Class<T> responseType) {
		try {
			JsonNode outputArray = mapper.readTree(response.getBody()).path("output");
			if (!outputArray.isArray() || outputArray.isEmpty()) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Model returned no output.");
			}

			for (JsonNode messageNode : outputArray) {
				JsonNode contentArray = messageNode.path("content");
				if (!contentArray.isArray())
					continue;

				for (JsonNode contentItem : contentArray) {
					String type = contentItem.path("type").asText();

					if ("refusal".equalsIgnoreCase(type)) {
						String reason = contentItem.path("refusal").asText("Model refused to respond.");
						throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
								"Model refused to generate response: " + reason);
					}

					if ("output_text".equalsIgnoreCase(type)) {
						String textJson = contentItem.path("text").asText();
						if (textJson == null || textJson.isEmpty()) {
							throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Model output text is empty.");
						}
						return mapper.readValue(textJson, responseType);
					}
				}
			}

			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Model returned no valid content.");

		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to parse OpenAI response: " + e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Unexpected error while handling OpenAI response: " + e.getMessage());
		}
	}
}