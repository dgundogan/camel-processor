package com.lambdatech.learncamelspringboot.domain;


import lombok.Data;
import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;

import java.math.BigDecimal;

@CsvRecord( separator = ",", skipFirstLine = true)
@Data
public class Item {

    @DataField(pos=1)
    private String transactionType;

    @DataField(pos=2)
    private String sku;

    @DataField(pos=3)
    private String itemDescription;

    @DataField(pos=4)
    private BigDecimal price;

}
