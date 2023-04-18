package com.linxa.phonebook.service;

import com.linxa.phonebook.model.entity.Contact;
import com.linxa.phonebook.model.repository.ContactRepository;

import java.util.List;
import java.util.Objects;

public final class ContactService {

    private static final ContactService INSTANCE = new ContactService();

    private ContactService() {
    }

    public static ContactService getInstance() {
        return INSTANCE;
    }

    public List<Contact> findContacts(String searchTerm) {
        ContactRepository contactRepository = ContactRepository.getInstance();
        return Objects.isNull(searchTerm) || searchTerm.isEmpty()
            ? contactRepository.findAll()
            : contactRepository.search(searchTerm);
    }

    public void saveContact(Contact contact) {
        ContactRepository.getInstance().insert(contact);
    }

    public void deleteContact(Contact contact) {
//        ContactRepository.getInstance().delete(contact);
    }

}
