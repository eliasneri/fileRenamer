package com.betaservicos.filerenamer.util;

import com.betaservicos.filerenamer.domain.Person;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PersonRowMapper implements RowMapper<Person> {
    @Override
    public Person mapRow(ResultSet rs, int rowNum) throws SQLException {
        Person person = new Person();
        person.setPersonId(rs.getInt("person_id"));
        person.setPersonName(rs.getString("name"));
        return person;
    }
}
