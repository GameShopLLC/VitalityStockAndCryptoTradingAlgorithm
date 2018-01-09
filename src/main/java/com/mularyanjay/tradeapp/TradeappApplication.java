//Copyright (C) 2017 Lynden Jay Evans Jr

//I, LYNDEN JAY EVANS JR, AM THE CREATOR OF THIS
//SOURCE CODE, AND UNDER 17 U.S.C. §§ 101-810
//COPYRIGHT LAW, NO ONE OTHER THAN ME MAY VIEW, 
//EXECUTE, DISTRIBUTE, OR SELL THIS CODE. 

package com.mularyanjay.tradeapp;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.AbstractConfigurableTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

import java.util.Arrays;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

@SpringBootApplication
@ComponentScan("com.mularyanjay.tradeapp")
public class TradeappApplication extends SpringBootServletInitializer  {

	public static void main(String[] args) {
		try {
		ApplicationContext ctx = SpringApplication.run(TradeappApplication.class, args);
		} catch (Throwable t) {
			t.printStackTrace();
		}
		System.out.println("We're live");
		//ApplicationPropertiesValues apv = ctx.getBean(ApplicationPropertiesValues.class);
		
		//System.out.println(apv.getKeyStore());
//		String[] beanNames = ctx.getBeanDefinitionNames();
//		Arrays.sort(beanNames);
//		for (String beanName : beanNames){
//		System.out.println(beanName);
//		}
		
//		System.out.println("*******************************************************");
//		ThymeleafViewResolver tvr = ctx.getBean(ThymeleafViewResolver.class);
//		System.out.println(tvr.getTemplateEngine().getConfiguration().getTemplateResolvers());
//		for (ITemplateResolver tr :  tvr.getTemplateEngine().getConfiguration().getTemplateResolvers()) {
//	//System.out.println(tr.toString());
//			AbstractConfigurableTemplateResolver ac = (AbstractConfigurableTemplateResolver)tr;
//			System.out.println(ac.getPrefix());
//			//ac.setPrefix("/templates/");
//			System.out.println(ac.getPrefix());
//			System.out.println(ac.getSuffix());
//			//ac.setPrefix("claspath:/resources/templates/");
//			//System.out.println(ac.getPrefix());
//			
//		}
	}
	@Bean(name="current")
	User currentUser(){
		return new User("Mula", "Mula");
	}
	
	@Bean
	HashingAlgorithm hashingAlgorithm(){
		return new HashingAlgorithm();
	}
	
	@Bean
	HttpEntityBean httpEntityBean() {
		return new HttpEntityBean();
	}
	
	@Bean
	ApplicationPropertiesValues applicationPropertiesValues() {
		return new ApplicationPropertiesValues();
		
	}
//	  @Bean
//	    public WebMvcConfigurer corsConfigurer() {
//	        return new WebMvcConfigurerAdapter() {
//	            @Override
//	            public void addCorsMappings(CorsRegistry registry) {
//	                registry.addMapping("/main").allowedOrigins("http://localhost:8080");
//	            }
//	        };
//	    }
	
}
