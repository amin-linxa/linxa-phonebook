package com.linxa.phonebook.service;

import com.linxa.phonebook.model.entity.Contact;
import com.linxa.phonebook.model.repository.ContactRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class ContactService {

    private final ContactRepository contactRepository;

    public List<Contact> findContacts(final String searchTerm) {
        return Objects.isNull(searchTerm) || searchTerm.isEmpty()
            ? contactRepository.findAll()
            : contactRepository.search(searchTerm);
    }

    public void saveContact(final Contact contact) {
        contactRepository.save(contact);
    }

    public void deleteContact(final Contact contact) {
        contactRepository.delete(contact);
    }

}
