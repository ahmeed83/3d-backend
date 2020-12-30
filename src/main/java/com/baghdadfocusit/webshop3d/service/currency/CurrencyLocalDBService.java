package com.baghdadfocusit.webshop3d.service.currency;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile(value = {"local", "aws", "dev", "!prod"})
public class CurrencyLocalDBService implements CurrencyService {

    private static final String PRIMARY_KEY = "currencyId-test";
    private static final String PRIMARY_KEY_VALUE = "100";
    private static final String DOLLAR_PRICE = "dollarPrice-test";
    private static final Logger LOGGER = LoggerFactory.getLogger(CurrencyLocalDBService.class);
    private final Table table;

    public CurrencyLocalDBService(final AmazonDynamoDB amazonDynamoDB) {
        final DynamoDB dynamoDB = new DynamoDB(amazonDynamoDB);
        table = dynamoDB.getTable("3d-currency-test");
    }

    /**
     * Change currency price in AWS dynamo DB
     *
     * @param currencyPrice currencyPrice
     */
    @Override
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
    @Override
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
