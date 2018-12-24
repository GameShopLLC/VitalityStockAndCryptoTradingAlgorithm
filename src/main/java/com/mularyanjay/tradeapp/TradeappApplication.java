//Copyright (C) 2017 Lynden Jay Evans Jr

//I, LYNDEN JAY EVANS JR, AM THE CREATOR OF THIS
//SOURCE CODE, AND UNDER 17 U.S.C. §§ 101-810
//COPYRIGHT LAW, NO ONE OTHER THAN ME MAY VIEW, 
//EXECUTE, DISTRIBUTE, OR SELL THIS CODE. 

package com.mularyanjay.tradeapp;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
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

import java.math.BigDecimal;
import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.Arrays;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
//Needs to make "RALLYING" state.  Rally/cancel rally
//So therefore, not rallying, rally, cancel rally, rally complete
//RALLYING State done

//Needs to make docks
//Must enforce single dock per transaction
//policy to avoid manipulating nonexistent money

//Needs to make shedding functions
//Needs to do remainder shedding for large
//numbers
//Need to implement splitting (hard part)
//Implement splitting tommorow
//IMPLEMENT SPLITTING TODAY
@SpringBootApplication
@ComponentScan("com.mularyanjay.tradeapp")
public class TradeappApplication extends SpringBootServletInitializer {

//	@Autowired
//    VitalityInstanceRepository vir;
//	
//	@Autowired
//	VitalityInstance vi;
	
	public static void main(String[] args) {
		URL proximo = null;
		try {
			proximo = new URL(System.getenv("PROXIMO_URL"));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String userInfo = proximo.getUserInfo();
		String user = userInfo.substring(0, userInfo.indexOf(':'));
		String password = userInfo.substring(userInfo.indexOf(':') + 1);
		System.setProperty("socksProxyHost", proximo.getHost());
		Authenticator.setDefault(new ProxyAuthenticator(user, password));
		
		try {
		ApplicationContext ctx = SpringApplication.run(TradeappApplication.class, args);
		VitalityInstance vi = ctx.getBean(VitalityInstance.class);
		VitalityInstanceRepository vir = ctx.getBean(VitalityInstanceRepository.class);
		AlgorithmManager am = ctx.getBean(AlgorithmManager.class);
		if (vir.findAll().size() > 0) {
//			for (VitalityInstance v: vir.findAll()) {
				try {
				vi.vAll(vir.findAll().get(vir.findAll().size() - 1));
				am.setRunning(true);
				
				} catch (Throwable t) {
					t.printStackTrace();
				}
				System.out.println("Vitality Instance existing and set");
				System.out.println("Size of database is " + vir.findAll().size());
//				break;
//			}
		} else {
			System.out.println("No vi set, setting one");
			vir.save(vi);
//			Thread.sleep(100);
			System.out.println("Size of database is " + vir.findAll().size());
		}
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
	
//	@Override
//	public void run(String... args) throws Exception {
//
////		try {
////			
////		} catch (Throwable ex) {
////			ex.printStackTrace();
////		}
//	}
	
	@Bean(name="current")
	User currentUser(){
		return new User("Mula", "Mula");
	}
	
	@Bean
	HashingAlgorithm hashingAlgorithm(){
		return new HashingAlgorithm();
	}
	
	@Bean(name="main")
	HttpEntityBean httpEntityBean() {
		return new HttpEntityBean();
	}
	
	@Bean(name="local")
	HttpEntityBean localHttpEntityBean() {
		return new HttpEntityBean();
	}
	
	@Bean
	AlgorithmManager algorithmManager() {
		return new AlgorithmManager();
	}
	
	@Bean 
	SimulationManager simulationManager() {
		return new SimulationManager();
	}
	
	@Bean
	CarrotHistory carrotHistory() {
		return new CarrotHistory();
	}
	
	//Make bean for vi but with better constructor
	//public TradeGroup(String whatName, int whatAmountThreads, BigDecimal initialUSD, int timeSpan, int ccn, float bto, float sto) {
	@Bean
	VitalityInstance vitalityInstance() {
		return new VitalityInstance(SimulationMode.REALTIME, new BigDecimal("64"), //Original 25000000
				new TradeGroup(SimulationMode.REALTIME, "One-1", "NONE", 32, new BigDecimal("64"), 1, 3, 8L * 60L * 1000L, 1L * 24L * 60L * 60L * 1000L, 1L * 24L * 60L * 60L * 1000L, new BigDecimal(".0001")));//forceLossTimeout),
				//new TradeGroup("Five-1", 20, new BigDecimal("10000"), 5, 3, 40L * 60L * 1000L, 24L * 60L * 60L * 1000L),
				//new TradeGroup("Ten-1", 20, new BigDecimal("10000"), 10, 3, 80L * 60L * 1000L, 24L * 60L * 60L * 1000L),
				//new TradeGroup("Fifteen-1", 20, new BigDecimal("10000"), 15, 3, 120L * 60L * 1000L, 24L * 60L * 60L * 1000L),
				//new TradeGroup("Thirty-1", 20, new BigDecimal("10000"), 30, 3, 240L * 60L * 1000L, 24L * 60L * 60L * 1000L),
				//new TradeGroup("Hour-1", 20, new BigDecimal("10000"), 60, 3, 480L * 60L * 1000L, 24L * 60L * 60L * 1000L));

	}
	
//	private class ProxyAuthenticator extends Authenticator {
//		  
//		}
//	@Bean
//	ApplicationPropertiesValues applicationPropertiesValues() {
//		return new ApplicationPropertiesValues();
//		
//	}
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
