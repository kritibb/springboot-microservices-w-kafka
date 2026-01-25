package com.thekreatib.products.service;

import com.thekreatib.core.ProductCreatedEvent;
import com.thekreatib.products.model.ProductModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ProductServiceImpl implements ProductService {
  private final Logger LOG= LoggerFactory.getLogger(this.getClass());
  private final KafkaTemplate<String, ProductCreatedEvent> kafkaTemplate;

  public ProductServiceImpl(KafkaTemplate<String, ProductCreatedEvent> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  @Override
  public String createProduct(ProductModel productModel) throws Exception{
    String productId = UUID.randomUUID().toString();
    // TODO: persist Product Details into database table before publishing an Event
    ProductCreatedEvent productCreatedEvent =
        new ProductCreatedEvent(
            productId,
            productModel.getTitle(),
            productModel.getPrice(),
            productModel.getQuantity());
    LOG.info("Before publishing a ProductCreateEvent");

    SendResult<String,ProductCreatedEvent>  result = kafkaTemplate.send("product-created-events-topic", productId, productCreatedEvent).get();
//    SendResult<String,ProductCreatedEvent>  result = kafkaTemplate.send("topic2", productId, productCreatedEvent).get();
    LOG.info("Partition: "+result.getRecordMetadata().partition());
    LOG.info("Topic: "+result.getRecordMetadata().topic());
    LOG.info("Offset: "+result.getRecordMetadata().offset());

    LOG.info("******* Returning Product id");
    return productId;
  }
}
