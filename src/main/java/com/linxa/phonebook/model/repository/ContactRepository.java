package com.linxa.phonebook.model.repository;

import com.linxa.phonebook.model.entity.Contact;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactRepository extends _BaseRepository<Contact> {

    @Query(" select c from Contact c" +
        "    where lower(c.firstName) like lower(concat('%', :searchTerm, '%') )" +
        "      or lower(c.lastName) like lower(concat('%', :searchTerm, '%') )")
    List<Contact> search(@Param("searchTerm") String searchTerm);

}
