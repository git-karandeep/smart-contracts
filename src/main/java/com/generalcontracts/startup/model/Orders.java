package com.generalcontracts.startup.model;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "orders")
public class Orders {

    @Id
    private String id;

    private String customer;

    private Double amount;

    private String status;
}