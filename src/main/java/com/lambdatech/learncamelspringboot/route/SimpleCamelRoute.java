package com.lambdatech.learncamelspringboot.route;

import com.lambdatech.learncamelspringboot.alert.MailProcessor;
import com.lambdatech.learncamelspringboot.domain.Item;
import com.lambdatech.learncamelspringboot.exception.DataException;
import com.lambdatech.learncamelspringboot.process.BuildSQLProcessor;
import com.lambdatech.learncamelspringboot.process.SuccessProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dataformat.bindy.csv.BindyCsvDataFormat;
import org.apache.camel.spi.DataFormat;
import org.postgresql.util.PSQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
@Slf4j
public class SimpleCamelRoute extends RouteBuilder {

    @Autowired
    private Environment env;

    @Qualifier("dataSource")
    @Autowired
    DataSource dataSource;

    @Autowired
    BuildSQLProcessor buildSQLProcessor;

    @Autowired
    SuccessProcessor successProcessor;

    @Autowired
    MailProcessor mailProcessor;

    @Override
    public void configure() {

        log.info("Starting the Camel Route");

        DataFormat bindy = new BindyCsvDataFormat(Item.class);

        errorHandler(
                deadLetterChannel("log:errorInRoute?level=ERROR&showProperties=true")
                        .maximumRedeliveries(3)
                        .redeliveryDelay(300)
                        .backOffMultiplier(2)
                        .retryAttemptedLogLevel(LoggingLevel.ERROR));

        //if DB is down, it raises PSQLException
        onException(PSQLException.class).log(LoggingLevel.ERROR, "PSQLException in the route ${body}");

        onException(DataException.class)
                .log(LoggingLevel.ERROR, "Exception in the route ${body}")
                .process(mailProcessor);

        from("{{startRoute}}")
                .log("Times Invoked and the body" + env.getProperty("message"))
                .choice()
                    .when((header("env").isNotEqualTo("mock")))
                        .pollEnrich("{{fromRoute}}")
                    .otherwise()
                        .log("mock env flow and the body is ${body}")
                .end()
                .to("{{toRoute1}}")
                .unmarshal(bindy)
                .log("The unmarshal object is ${body}")
                .split(body())
                    .log("Record is ${body}")
                    .process(buildSQLProcessor)
                    .to("jdbc:dataSource")
                .end()
            .process(successProcessor)
            .to("{{toRoute3}}");

        log.info("Ending the Camel Route");
    }
}
