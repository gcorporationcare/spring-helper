package com.github.gcorporationcare.notest.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.gcorporationcare.web.common.ParameterKey;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.RequestParameterBuilder;
import springfox.documentation.schema.Example;
import springfox.documentation.schema.ScalarType;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ParameterType;
import springfox.documentation.service.RequestParameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Swagger configurations.
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {
	/**
	 * Create api doc scanner.
	 *
	 * @return {@link Docket}
	 */
	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2).select()
				.apis(RequestHandlerSelectors.basePackage("com.github.gcorporationcare.notest"))
				.paths(PathSelectors.any()).build().globalRequestParameters(commonParameters()).apiInfo(getApiInfo());
	}

	private ApiInfo getApiInfo() {
		return new ApiInfoBuilder().description("Documentation for Spring-Helper test API")
				.title("Spring-Helper test API").version("v1.0.0").build();
	}

	private List<RequestParameter> commonParameters() {
		List<RequestParameter> parameters = new ArrayList<>();
		parameters.add(new RequestParameterBuilder().name(ParameterKey.LANGUAGE_PARAMETER)
				.description(ParameterKey.LANGUAGE_PARAMETER_DESCRIPTION).in(ParameterType.QUERY)
				.example(new Example(ParameterKey.LANGUAGE_PARAMETER_EXAMPLE, "English", "English",
						ParameterKey.LANGUAGE_PARAMETER_EXAMPLE, null, null))
				.query(q -> q
						.model(modelSpecificationBuilder -> modelSpecificationBuilder.scalarModel(ScalarType.STRING)))
				.required(false).build());
		return parameters;
	}
}
