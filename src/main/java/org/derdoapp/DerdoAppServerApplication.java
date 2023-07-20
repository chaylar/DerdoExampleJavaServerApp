package org.derdoapp;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;

@EntityScan("org.derdoapp")
@SpringBootApplication
@EnableAutoConfiguration
public class DerdoAppServerApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(DerdoAppServerApplication.class, args);

		//SOCKET
		/*try {
			AppSocketServer.getInstance().startServer();
			//appSocketServer.startServer();
		}
		catch (Exception ex) {
			System.out.println("AppSocketServer.Error : " + ex.getMessage() != null ? ex.getMessage() : "MESSAGE_WAS_NULL");
		}*/
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(DerdoAppServerApplication.class);
	}

	@Bean
	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
		return args -> {

			//TODO : Remove this after test stage
			System.out.println("Let's inspect the beans provided by Spring Boot:");

			String[] beanNames = ctx.getBeanDefinitionNames();
			Arrays.sort(beanNames);
			for (String beanName : beanNames) {
				System.out.println(beanName);
			}
		};
	}
}
