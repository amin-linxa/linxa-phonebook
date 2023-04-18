package com.linxa.phonebook.service;

import com.linxa.phonebook.model.entity.Contact;
import com.linxa.phonebook.model.repository.ContactRepository;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public final class ContactService {

    private static final ContactService INSTANCE = new ContactService();

    private final CacheHolder cacheHolder;

    private ContactService() {
        this.cacheHolder = new CacheHolder(ContactRepository.getInstance().findAll());
    }

    public static ContactService getInstance() {
        return INSTANCE;
    }

    public List<Contact> findContacts(String searchTerm) {
        return cacheHolder.find(searchTerm);
//        ContactRepository contactRepository = ContactRepository.getInstance();
//        return Objects.isNull(searchTerm) || searchTerm.isEmpty()
//            ? contactRepository.findAll()
//            : contactRepository.search(searchTerm);
    }

    public void saveContact(Contact contact) {
        ContactRepository contactRepository = ContactRepository.getInstance();
        if (Objects.isNull(contact.getId())) {
            contactRepository.insert(contact);
            cacheHolder.add(contact);
        } else {
            cacheHolder.update(contact);
//            contactRepository.update(contact); // move to new thread
        }
    }

    public void deleteContact(Contact contact) {
        cacheHolder.delete(contact);
//        ContactRepository.getInstance().delete(contact);
    }

    private static class CacheHolder {

        private final Set<Contact> cache;

        public CacheHolder(List<Contact> initialValues) {
            this.cache = new HashSet<>(initialValues);
        }

        private final Comparator<Contact> contactComparator = (o1, o2) -> {
            Integer compare = o1.getLastName().compareTo(o2.getLastName());
            if (Objects.equals(compare, 0))
                compare = o1.getFirstName().compareTo(o2.getFirstName());
            return compare;
        };

        public List<Contact> find(String searchTerm) {
            if (Objects.isNull(searchTerm))
                return cache.stream().sorted(contactComparator).collect(Collectors.toList());

            final var term = searchTerm.toLowerCase();
            return cache.stream().filter(contact ->
                    (Objects.nonNull(contact.getFirstName()) && contact.getFirstName().toLowerCase().contains(term))
                        || (Objects.nonNull(contact.getLastName()) && contact.getLastName().toLowerCase().contains(term)))
                .sorted(contactComparator).collect(Collectors.toList());
        }

        public void add(Contact contact) {
            cache.add(contact);
        }

        public void update(Contact contact) {
            cache.remove(contact);
            cache.add(contact);
        }

        public void delete(Contact contact) {
            cache.remove(contact);
        }

    }

}
