package com.ts.banking.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;

@Configuration
@EnableSwagger2
public class SwaggerConfiguration {
	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2).select()
				.apis(RequestHandlerSelectors.basePackage("com.ts.banking")).paths(PathSelectors.any()).build()
				.pathMapping("/").apiInfo(metaData());
	}

	private ApiInfo metaData() {
		return new ApiInfo("Banking Test App", "Spring Boot REST API for Banking", "1.0", null,
				new Contact("Team Banking", "", "ralph19.morales@gmail.com"), "Spring Boot 2.4.1",
				null, new ArrayList<>());
	}
}
