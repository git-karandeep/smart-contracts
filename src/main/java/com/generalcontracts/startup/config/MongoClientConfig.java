package com.generalcontracts.startup.config;
import org.springframework.boot.mongodb.autoconfigure.MongoProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
@Configuration
@EnableMongoRepositories (basePackages= "com.generalcontracts.startup.repository")
public class MongoClientConfig extends AbstractMongoClientConfiguration {
    private final MongoProperties mongoProperties;
    private final ResourceLoader resourceLoader;
    public MongoClientConfig(MongoProperties mongoProperties, ResourceLoader resourceLoader) {
        this.mongoProperties = mongoProperties;
        this.resourceLoader = resourceLoader;
    }
    @Override
    protected String getDatabaseName() {
        return mongoProperties.getDatabase();
    }
    @Override
    protected void configureClientSettings (com.mongodb.MongoClientSettings.Builder builder) {
        // Configure the MongoDB client settings
        this.mongoDBDefaultSettings().customize(builder);
    }
    @Bean
    public MongoPropertiesClientSettingsBuilderCustomizer mongoDBDefaultSettings() {
        return new MongoPropertiesClientSettingsBuilderCustomizer( this.resourceLoader, this.mongoProperties);
    }
/*   @Bean
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(mongoClient(), getDatabaseName());
    }*/
}