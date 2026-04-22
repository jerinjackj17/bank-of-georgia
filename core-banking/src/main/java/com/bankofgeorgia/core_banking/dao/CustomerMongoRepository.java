package com.bankofgeorgia.core_banking.dao;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;
import com.bankofgeorgia.core_banking.entity.Customer;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import java.util.logging.Logger;

@Repository
public class CustomerMongoRepository implements CustomerDao {

    Logger logger = Logger.getLogger(CustomerMongoRepository.class.getName());

    private final BCryptPasswordEncoder passwordEncoder;
    private final MongoClient mongoClient;
    private final MongoDatabase mongoDatabase;

    public CustomerMongoRepository(
            @Value("${bcrypt.strength:10}") int bcryptStrength, 
            @Value("${spring.mongodb.database}") String mongoDatabase,
            @Value("${spring.mongodb.uri}") String mongoUri,  
            MongoClient mongoClient) {
        this.passwordEncoder = new BCryptPasswordEncoder(bcryptStrength);
        this.mongoClient = mongoClient;
        this.mongoDatabase = mongoClient.getDatabase(mongoDatabase);

    }

    @Override
    public Customer save(Customer customer) {
        try{
            logger.info("Saving customer with email: " + customer.getEmail());

            MongoCollection<Document> collection = mongoDatabase.getCollection("customers");

            Document doc = new Document()
                    .append("firstName", customer.getFirstName())
                    .append("lastName", customer.getLastName())
                    .append("email", customer.getEmail())
                    .append("username", customer.getUsername())
                    .append("phone", customer.getPhone())
                    .append("password", passwordEncoder.encode(customer.getPassword())) 
                    .append("dateOfBirth", customer.getDateOfBirth());

            collection.insertOne(doc);

            logger.info("Customer saved successfully with email: " + customer.getEmail());

            return customer;
            
        } catch (Exception e) {
            logger.severe("Error saving customer: " + e.getMessage());
        }
        return null;
    }

}
