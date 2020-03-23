package server;

import javax.sql.DataSource;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class androidServer {
	public static void main(String[] args) {
		SpringApplication.run(androidServer.class, args);
		System.out.println("Android Server Started!!");
	}
}
