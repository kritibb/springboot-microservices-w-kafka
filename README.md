# Springboot Microservices with Kafka

## Installation
1. Have 3 nodes (brokers) of Kafka 4.x running
2. Clone the branch and update `application.properties` based on your `Kafka` setup.
3. Run `KafkaProductsMicroserviceApplication` (producer) and `KafkaEmailNotificationMicroserviceApplication` (consumer) from your IDE.
4. Use Postman and create some products based on `ProductCreateEvent`
5. See Kafka Consumer receiving the update about the product created.