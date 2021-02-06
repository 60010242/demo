package com.example.demo.entities;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

public class yml {
	@Configuration
	@EnableConfigurationProperties
	@ConfigurationProperties
	public class YAMLConfig {
	 
	    private String spring;

		public String getSpring() {
			return spring;
		}

		public void setSpring(String spring) {
			this.spring = spring;
		}
	
	}
}
