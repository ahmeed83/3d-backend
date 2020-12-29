package com.baghdadfocusit.webshop3d.service;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CurrencyService {

    private final AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
    private final DynamoDB dynamoDB = new DynamoDB(client);
    private final Table table = dynamoDB.getTable("3d-currency");
    private static final String PRIMARY_KEY = "currencyId";
    private static final String PRIMARY_KEY_VALUE = "1";
    private static final String DOLLAR_PRICE = "dollarPrice";
    private static final Logger LOGGER = LoggerFactory.getLogger(CurrencyService.class);

    /**
     * Change currency price in AWS dynamo DB
     *
     * @param currencyPrice currencyPrice
     */
    public void changeCurrencyPrice(final String currencyPrice) {
        try {
            Item item = new Item().withPrimaryKey(PRIMARY_KEY, PRIMARY_KEY_VALUE)
                    .withString(DOLLAR_PRICE, currencyPrice);
            table.putItem(item);
            LOGGER.info("Dollar price save successfully");
        } catch (Exception e) {
            LOGGER.error("Create item in Dynamo DB failed: {}", e.getMessage());
        }
    }

    /**
     * Retrieve currency price from AWS dynamoDB
     *
     * @return currency price
     */
    public String retrieveItem() {
        GetItemSpec spec = new GetItemSpec().withPrimaryKey(PRIMARY_KEY, PRIMARY_KEY_VALUE);
        String result = "";
        try {
            result = (String) table.getItem(spec).get(DOLLAR_PRICE);
        } catch (Exception e) {
            LOGGER.error("Getting item from Dynamo DB failed: {}", e.getMessage());
        }
        return result;
    }
}
