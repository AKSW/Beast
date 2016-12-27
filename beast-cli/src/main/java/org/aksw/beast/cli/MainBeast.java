package org.aksw.beast.cli;

import org.springframework.boot.SpringApplication;

public class MainBeast {
	public static void main(String[] args) throws Exception {
		// ApplicationContext ctx =  
		SpringApplication.run(new Object[]{"file:test.groovy", /* ConfigIguanaCore.class */}, args);
	}
}
