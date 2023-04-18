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
    }

    public void saveContact(Contact contact) {
        ContactRepository contactRepository = ContactRepository.getInstance();
        if (Objects.isNull(contact.getId())) {
            contactRepository.insert(contact);
            cacheHolder.add(contact);
        } else {
            cacheHolder.update(contact);
            new Thread(() -> {
                var old = contactRepository.findOne(contact.getId());
                NotificationService.getInstance().sendNotification(old);
                contactRepository.update(contact);
            }).start();
        }
    }

    public void deleteContact(Contact contact) {
        cacheHolder.delete(contact);
        new Thread(() -> ContactRepository.getInstance().delete(contact)).start();
    }

    public boolean hasUniqueNumber(Contact contact) {
        return Objects.isNull(contact.getId())
            ? !cacheHolder.containsWithoutId(contact.getPhoneNumber())
            : !cacheHolder.containsWithId(contact.getId(), contact.getPhoneNumber());
    }

    private static class CacheHolder {

        private final Set<Contact> cache;
        private final Comparator<Contact> contactComparator = (o1, o2) -> {
            Integer compare = o1.getLastName().compareTo(o2.getLastName());
            if (Objects.equals(compare, 0))
                compare = o1.getFirstName().compareTo(o2.getFirstName());
            return compare;
        };

        CacheHolder(List<Contact> initialValues) {
            this.cache = new HashSet<>(initialValues);
        }

        List<Contact> find(String searchTerm) {
            if (Objects.isNull(searchTerm))
                return cache.stream().sorted(contactComparator).collect(Collectors.toList());

            final var term = searchTerm.toLowerCase();
            return cache.stream().filter(contact ->
                    (Objects.nonNull(contact.getFirstName()) && contact.getFirstName().toLowerCase().contains(term))
                        || (Objects.nonNull(contact.getLastName()) && contact.getLastName().toLowerCase().contains(term)))
                .sorted(contactComparator).collect(Collectors.toList());
        }

        void add(Contact contact) {
            cache.add(contact);
        }

        void update(Contact contact) {
            cache.remove(contact);
            cache.add(contact);
        }

        void delete(Contact contact) {
            cache.remove(contact);
        }

        boolean containsWithoutId(String phoneNumber) {
            return cache.stream().anyMatch(contact -> Objects.equals(contact.getPhoneNumber(), phoneNumber));
        }

        boolean containsWithId(Long id, String phoneNumber) {
            return cache.stream().filter(contact -> {
                System.out.println("ID: " + contact.getId());
                return !Objects.equals(contact.getId(), id);
            }).anyMatch(contact -> {
                System.out.println("PN: " + contact.getPhoneNumber());
                return Objects.equals(contact.getPhoneNumber(), phoneNumber);
            });
        }

    }

}
