package com.thekreatib.products;

import com.thekreatib.core.ProductCreatedEvent;
import com.thekreatib.products.model.ProductModel;
import com.thekreatib.products.service.ProductService;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DirtiesContext // removes test context after use (normally cached and reused for speed)
//default PER_METHOD: new class instance for every @Test method, PER_CLASS reuses the test object
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test") //looks for application-test.properties
@EmbeddedKafka(
        partitions = 3,
        count = 1, // count means no. of brokers
        controlledShutdown = true,
        // CRITICAL: This maps the random port to the property name Spring expects
        bootstrapServersProperty = "spring.kafka.bootstrap-servers"
)
@SpringBootTest(properties="spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}")
public class ProductServiceIntegrationTest {
    @Autowired
    private ProductService productService;
    @Autowired
    private  EmbeddedKafkaBroker embeddedKafkaBroker;
    @Autowired
    private  Environment environment;
    private KafkaMessageListenerContainer<String, ProductCreatedEvent> container;
    private BlockingQueue<ConsumerRecord<String, ProductCreatedEvent>> records;

    @BeforeAll
    void setUp(){
        DefaultKafkaConsumerFactory<String, ProductCreatedEvent> consumerFactory=
                new DefaultKafkaConsumerFactory<>(getConsumerProperties());
        ContainerProperties containerProperties = new ContainerProperties(environment.getProperty("product-created-events-topic-name"));
        container = new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);
        records=new LinkedBlockingQueue<>();
        container.setupMessageListener((MessageListener<String, ProductCreatedEvent>) records::add);
        container.start();
        ContainerTestUtils.waitForAssignment(container, embeddedKafkaBroker.getPartitionsPerTopic());
    }
    
    @Test
    void testCreateProduct_forValidProductDetails_sendsSuccessfulKafkaMessage() throws Exception{
        //arrange

        String title="iphone 11";
        BigDecimal price=new BigDecimal(660);
        Integer quantity=1;

        ProductModel productModel=new ProductModel();
        productModel.setPrice(price);
        productModel.setTitle(title);
        productModel.setQuantity(quantity);

        //Act
        productService.createProduct(productModel);

        //Assert
        ConsumerRecord<String, ProductCreatedEvent> message = records.poll(3000, TimeUnit.MILLISECONDS);
        assertNotNull(message);
        assertNotNull(message.key());
        ProductCreatedEvent productCreatedEvent = message.value();
        assertEquals(productModel.getQuantity(), productCreatedEvent.getQuantity());
        assertEquals(productModel.getTitle(), productCreatedEvent.getTitle());
        assertEquals(productModel.getPrice(), productCreatedEvent.getPrice());
        
    }
    private Map<String, Object> getConsumerProperties() {
        return Map.of(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, embeddedKafkaBroker.getBrokersAsString(),
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class,
                ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JacksonJsonDeserializer.class,
                JacksonJsonDeserializer.VALUE_DEFAULT_TYPE, ProductCreatedEvent.class.getName(),
                ConsumerConfig.GROUP_ID_CONFIG, environment.getProperty("spring.kafka.consumer.group-id"),
                JacksonJsonDeserializer.TRUSTED_PACKAGES, environment.getProperty("spring.kafka.consumer.properties.spring" +
                        ".json.trusted.packages"),
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, environment.getProperty("spring.kafka.consumer.auto-offset-reset")
        );
    }

    @AfterAll
    void tearDown(){
        container.stop();
    }

}
