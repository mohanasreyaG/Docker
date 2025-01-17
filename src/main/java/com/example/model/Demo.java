package com.example.model;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Demo {
    @PrimaryKey
    private String id;
    private String name;
    private String description;
}