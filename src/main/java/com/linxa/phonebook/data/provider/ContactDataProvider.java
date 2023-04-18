package com.linxa.phonebook.data.provider;

import com.linxa.phonebook.data.entity.Contact;
import com.vaadin.flow.component.crud.CrudFilter;
import com.vaadin.flow.data.provider.AbstractBackEndDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.SortDirection;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.Comparator.naturalOrder;

public class ContactDataProvider extends AbstractBackEndDataProvider<Contact, CrudFilter> {

    private final List<Contact> DATABASE = new ArrayList<>();

    private static Predicate<Contact> predicate(CrudFilter filter) {
        return filter.getConstraints().entrySet().stream().map(constraint -> (Predicate<Contact>) contact -> {
            try {
                Object value = valueOf(constraint.getKey(), contact);
                return value != null && value.toString().toLowerCase().contains(constraint.getValue().toLowerCase());
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }).reduce(Predicate::and).orElse(e -> true);
    }

    private static Comparator<Contact> comparator(CrudFilter filter) {
        return filter.getSortOrders().entrySet().stream().map(sortClause -> {
            try {
                Comparator<Contact> comparator = Comparator.comparing(contact -> (Comparable) valueOf(sortClause.getKey(), contact));
                if (sortClause.getValue() == SortDirection.DESCENDING) {
                    comparator = comparator.reversed();
                }
                return comparator;
            } catch (Exception ex) {
                return (Comparator<Contact>) (o1, o2) -> 0;
            }
        }).reduce(Comparator::thenComparing).orElse((o1, o2) -> 0);
    }

    private static Object valueOf(String fieldName, Contact contact) {
        try {
            Field field = Contact.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(contact);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public boolean isUniquePhoneNumber(Long currentContactId, String phoneNumber) {
        Stream<Contact> stream = DATABASE.stream();
        if (Objects.nonNull(currentContactId))
            stream = stream.filter(contact -> !Objects.equals(contact.getId(), currentContactId));
        return stream.noneMatch(contact -> Objects.equals(contact.getPhoneNumber(), phoneNumber));
    }

    public void persist(Contact item) {
        if (Objects.isNull(item.getId())) item.setId(DATABASE.stream().map(Contact::getId).max(naturalOrder()).orElse(0L) + 1);

        find(item.getId()).ifPresentOrElse(contact -> {
            int position = DATABASE.indexOf(contact);
            DATABASE.remove(contact);
            DATABASE.add(position, item);
        }, () -> DATABASE.add(item));
    }

    public void delete(Contact item) {
        DATABASE.removeIf(entity -> Objects.equals(entity.getId(), item.getId()));
    }

    @Override
    protected Stream<Contact> fetchFromBackEnd(Query<Contact, CrudFilter> query) {
        int offset = query.getOffset();
        int limit = query.getLimit();

        Stream<Contact> stream = DATABASE.stream();

        if (query.getFilter().isPresent()) stream = stream.filter(predicate(query.getFilter().get())).sorted(comparator(query.getFilter().get()));

        return stream.skip(offset).limit(limit);
    }

    @Override
    protected int sizeInBackEnd(Query<Contact, CrudFilter> query) {
        return (int) fetchFromBackEnd(query).count();
    }

    private Optional<Contact> find(Long id) {
        return DATABASE.stream().filter(entity -> Objects.equals(entity.getId(), id)).findFirst();
    }

}
