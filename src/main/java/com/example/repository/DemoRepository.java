package com.example.repository;

import com.example.model.Demo;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DemoRepository extends CassandraRepository<Demo, String> {
    // Custom query methods can be added here if needed
}
