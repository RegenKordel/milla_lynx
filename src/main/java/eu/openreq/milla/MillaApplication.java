package eu.openreq.milla;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@SpringBootApplication
public class MillaApplication {

	@Value("${milla.authToken}")
	private String authToken;

	public static void main(String[] args) {
		System.setProperty("org.apache.tomcat.util.buf.UDecoder.ALLOW_ENCODED_SLASH", "true");
		SpringApplication.run(MillaApplication.class, args);
	}
	
	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		// Do any additional configuration here
		RestTemplate rt = builder
				.setReadTimeout(3000000)
				.setConnectTimeout(3000000)
				.build();

		if (!authToken.isEmpty()) {
			rt.getInterceptors().add(new ClientHttpRequestInterceptor() {
				@Override
				public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
					request.getHeaders().set("X-Requested-With", "XMLHttpRequest");
					request.getHeaders().set("Cache-Control", "no-cache");
					request.getHeaders().set("Authorization", "Bearer " + authToken);
					return execution.execute(request, body);
				}
			});
		}

		return rt;

	}
}
