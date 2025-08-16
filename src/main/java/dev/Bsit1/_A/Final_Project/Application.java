package dev.Bsit1._A.Final_Project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}


	@Configuration
	public static class WebConfig implements WebMvcConfigurer {
		@Override
		public void addResourceHandlers(org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry registry) {
			registry.addResourceHandler("/css/**").addResourceLocations("classpath:/static/css/");
			registry.addResourceHandler("/js/**").addResourceLocations("classpath:/static/js/");
			registry.addResourceHandler("/images/**").addResourceLocations("classpath:/static/images/");
		}


	}

}
