package com.example.repository;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.example.model.Demo;
import org.springframework.stereotype.Repository;

@Repository
public class CassandraRepository {
    private final CqlSession session;

    public CassandraRepository(CqlSession session) {
        this.session = session;
    }

    public void save(Demo demo) {
        SimpleStatement statement = SimpleStatement.builder(
                        "INSERT INTO demo (id, name, description) VALUES (?, ?, ?)")
                .addPositionalValues(demo.getId(), demo.getName(), demo.getDescription())
                .build();
        session.execute(statement);
    }

    public Demo findById(String id) {
        SimpleStatement statement = SimpleStatement.builder(
                        "SELECT * FROM demo WHERE id = ?")
                .addPositionalValue(id)
                .build();
        var row = session.execute(statement).one();
        if (row != null) {
            return new Demo(
                    row.getString("id"),
                    row.getString("name"),
                    row.getString("description")
            );
        }
        return null;
    }
}
