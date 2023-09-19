package bienergy_mst.bienergy_mst;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BienergyMstApplication {

	public static void main(String[] args) {
		SpringApplication.run(BienergyMstApplication.class, args);
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
