package com.lambdatech.learncamelspringboot;

import com.lambdatech.learncamelspringboot.process.HealthCheckProcessor;
import com.lambdatech.learncamelspringboot.route.HealthCheckRoute;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("mock")
@RunWith(CamelSpringBootRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest
public class HealthCheckMockTest extends CamelTestSupport {

    @Override
    public RouteBuilder createRouteBuilder(){
        return new HealthCheckRoute();
    }

    @Autowired
    CamelContext context;

    @Autowired
    ProducerTemplate producerTemplate;

    @Autowired
    Environment environment;

    @Autowired
    protected CamelContext createCamelContext(){
        return context;
    }

    @Autowired
    HealthCheckProcessor healthCheckProcessor;

    @Before
    public void setUp(){}

    @Test
    public void healthRouteTest(){
        String input = "{status=UP}";
        String response  = (String) producerTemplate.requestBodyAndHeader(environment.getProperty("healthRoute"),
                                                                            input, "env",
                                                                            environment.getProperty("spring.profiles.active"));
        String expectedResult = "UP";
        assertEquals(expectedResult, response);
    }
}
