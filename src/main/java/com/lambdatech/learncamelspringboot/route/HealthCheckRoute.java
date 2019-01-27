package com.lambdatech.learncamelspringboot.route;

import com.lambdatech.learncamelspringboot.alert.MailProcessor;
import com.lambdatech.learncamelspringboot.process.HealthCheckProcessor;
import org.apache.camel.Predicate;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HealthCheckRoute extends RouteBuilder {

    @Autowired
    HealthCheckProcessor healthCheckProcessor;

    @Autowired
    MailProcessor mailProcessor;

    Predicate isNotMock = header("env").isNotEqualTo("mock");

    @Override
    public void configure() throws Exception {
        from("{{healthRoute}}").routeId("healthRoute")
                .choice()
                    .when(isNotMock)
                        .pollEnrich("http://localhost:8080/actuator/health")
                    .end()
                .process(healthCheckProcessor)
                .choice()
                    .when(header("error").isEqualTo(true))
                    .choice()
                        .when(isNotMock)
                            .process(mailProcessor)
                        .end()
                    .end();
    }
}
