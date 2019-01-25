package com.lambdatech.learncamelspringboot;


import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringBootRunner;
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
public class SimpleCamelRouteMockTest  {

    @Autowired
    CamelContext camelContext;

    @Autowired
    Environment env;

    @Autowired
    ProducerTemplate producerTemplate;

    @Autowired
    protected CamelContext createCamelContext(){
        return camelContext;
    }

    @Test
    public void testMoveFileMock() throws InterruptedException {
        String message = "type,sku#,itemdescription,price\n" +
                "ADD,100,Samsung,TV,500\n" +
                "ADD,101,Samsung,TV,300";

        MockEndpoint mockEndpoint = camelContext.getEndpoint("mock:output",MockEndpoint.class);
        mockEndpoint.expectedMessageCount(1);
        mockEndpoint.expectedBodiesReceived(message);

        producerTemplate.sendBodyAndHeader(env.getProperty("startRoute"),
                message,"env", env.getProperty("spring.environment.active"));

        mockEndpoint.assertIsSatisfied();


    }
}
