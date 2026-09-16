package com.generalcontracts.startup.config;

import com.generalcontracts.startup.util.CertDecode;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.mongodb.autoconfigure.MongoClientSettingsBuilderCustomizer;
import org.springframework.boot.mongodb.autoconfigure.MongoProperties;
import org.springframework.core.Ordered;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.TrustManagerFactory;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
public class MongoPropertiesClientSettingsBuilderCustomizer implements MongoClientSettingsBuilderCustomizer, Ordered {
    @Value("${spring.mongodb.ssl.certificate}")
    private String mongoCertFilePath;

    @Value("${spring.mongodb.ssl.enabled}")
    private Boolean sslEnabled;

    private final ResourceLoader resourceLoader;
    private final MongoProperties properties;
    private int order = 0;
    public MongoPropertiesClientSettingsBuilderCustomizer(ResourceLoader resourceLoader, MongoProperties properties) {
        this.resourceLoader = resourceLoader;
        this.properties = properties;
    }
    @Override
    public void customize(MongoClientSettings.Builder settingsBuilder) {
        applyUuidRepresentation(settingsBuilder);
        applyHostAndPort(settingsBuilder);
        applyReplicaSet(settingsBuilder);
        try {
            if (sslEnabled) {
                applyToSslSettings(settingsBuilder);
            } else {
                applyCredentials(settingsBuilder);
            }
        } catch (SSLException e) {
            throw new RuntimeException(e);
        }
    }
    private void applyToSslSettings(MongoClientSettings.Builder settingsBuilder) throws SSLException {
        SSLContext sslContext = createCustomSslContext();
        settingsBuilder
                .applyToSslSettings(builder -> builder
                        .enabled(true)
                        .invalidHostNameAllowed(true)
                        .context(sslContext))
                .build();
    }
    private SSLContext createCustomSslContext() {
        X509Certificate cert;
        SSLContext sslContext;
        Resource resource;
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            resource = resourceLoader.getResource(mongoCertFilePath);

            try (InputStream is = resource.getInputStream()) {
                cert = (X509Certificate) certificateFactory.generateCertificate(is);
            }
            TrustManagerFactory trustManagerFactory = TrustManagerFactory
                    .getInstance(TrustManagerFactory.getDefaultAlgorithm());
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null); // You don't need the KeyStore instance to come from a file.
            keyStore.setCertificateEntry("caCert", cert);
            trustManagerFactory.init(keyStore);
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustManagerFactory.getTrustManagers(), null);
        }catch (Exception e) {
            throw new IllegalStateException(e);
        }
        return sslContext;
    }
    private void applyUuidRepresentation(MongoClientSettings.Builder settingsBuilder) {
        settingsBuilder.uuidRepresentation(this.properties.getRepresentation().getUuid());
    }
    private void applyHostAndPort(MongoClientSettings.Builder settings) {
        if (this.properties.getUri() != null) {
            settings.applyConnectionString(new ConnectionString(this.properties.getUri()));
            return;
        }
        if (this.properties.getHost() != null || this.properties.getPort() != null) {
            String host = getOrDefault(this.properties.getHost(), "localhost");
            int port = getOrDefault(this.properties.getPort(), MongoProperties.DEFAULT_PORT);
            ServerAddress serverAddress = new ServerAddress(host, port);
            List<ServerAddress> serverAddressList = new ArrayList<>(List.of(serverAddress));
            applyAdditionalHosts(serverAddressList);
            settings.applyToClusterSettings((cluster) -> cluster.hosts(serverAddressList));
            return;
        }
        settings.applyConnectionString(new ConnectionString(MongoProperties.DEFAULT_URI));
    }
    private void applyAdditionalHosts(List<ServerAddress> serverAddressList) {
        if (this.properties.getAdditionalHosts() != null && !this.properties.getAdditionalHosts().isEmpty()) {
            this.properties.getAdditionalHosts()
                    .forEach((additionalHost) -> serverAddressList.add(new ServerAddress(additionalHost)));
        }
    }
    private void applyCredentials(MongoClientSettings.Builder builder) {
        if (this.properties.getUri() == null && this.properties.getUsername() != null
                && this.properties.getPassword() != null) {
            String database = (this.properties.getAuthenticationDatabase() != null)
                    ? this.properties.getAuthenticationDatabase() : this.properties.getMongoClientDatabase();
            builder.credential((MongoCredential.createCredential(this.properties.getUsername(), database,
                    this.properties.getPassword())));
        }
    }
    private void applyReplicaSet(MongoClientSettings.Builder builder) {
        if (this.properties.getReplicaSetName() != null) {
            builder.applyToClusterSettings(
                    (cluster) -> cluster.requiredReplicaSetName(this.properties.getReplicaSetName()));
        }
    }
    private <V> V getOrDefault(V value, V defaultValue) {
        return (value != null) ? value : defaultValue;
    }
    @Override
    public int getOrder() {
        return this.order;
    }
    public void setOrder(int order) {
        this.order = order;
    }
}
