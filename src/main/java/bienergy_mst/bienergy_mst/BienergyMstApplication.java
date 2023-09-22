package bienergy_mst.bienergy_mst;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import bienergy_mst.bienergy_mst.netty.TCPServer;
import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
@Component
@SpringBootApplication
@RequiredArgsConstructor
public class BienergyMstApplication {

	public static void main(String[] args) {
		try {
			SpringApplication.run(BienergyMstApplication.class, args);
		} catch(OutOfMemoryError e) {
			System.out.println("넘어감");
		}
	}
	private final TCPServer tcpServer;

	@SuppressWarnings({"Convert2Lambda", "java:S1604"})
	@Bean
	public ApplicationListener<ApplicationReadyEvent> readyEventApplicationListener() {
		return new ApplicationListener<ApplicationReadyEvent>() {

			@Override
			public void onApplicationEvent(ApplicationReadyEvent event) {
				tcpServer.start();
			}
			
		};
	}

}
