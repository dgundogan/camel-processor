package com.lambdatech.learncamelspringboot.process;

import com.lambdatech.learncamelspringboot.domain.Item;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BuildSQLProcessor implements org.apache.camel.Processor {
    @Override
    public void process(Exchange exchange) throws Exception {
        Item item  = (Item) exchange.getIn().getBody();

        log.info("Item in Processor is : "+item);
        String tableName = "ITEMS";

        StringBuilder query = new StringBuilder();
        if(item.getTransactionType().equals("ADD")){
            query.append("INSERT INTO "+tableName+"(SKU, ITEM_DESCRIPTION, PRICE) VALUES('");
            query.append(item.getSku()+"','"+item.getItemDescription()+"',"+item.getPrice()+")");
        }else if(item.getTransactionType().equals("UPDATE")){
            query.append("UPDATE "+tableName+" SET PRICE=");
            query.append(item.getPrice()+" where SKU='"+item.getSku()+"'");
        }else if(item.getTransactionType().equals("DELETE")){
            query.append("DELETE FROM "+tableName+" where SKU='"+item.getSku()+"'");
        }

        log.info("Final Query is "+query);
        exchange.getIn().setBody(query.toString());
    }
}
