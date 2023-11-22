package com.taskmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = "com.taskmanager")
@ComponentScan("com.taskmanager.util")
@SpringBootApplication


public class TaskManagerSpringApplication {

	public static void main(String[] args) {
		SpringApplication.run(TaskManagerSpringApplication.class, args);
	}

}
