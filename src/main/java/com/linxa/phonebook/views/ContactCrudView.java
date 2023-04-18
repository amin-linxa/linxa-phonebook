package com.linxa.phonebook.views;

import com.linxa.phonebook.data.entity.Contact;
import com.linxa.phonebook.data.provider.ContactDataProvider;
import com.vaadin.flow.component.crud.BinderCrudEditor;
import com.vaadin.flow.component.crud.Crud;
import com.vaadin.flow.component.crud.CrudEditor;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

@PageTitle("Contacts")
@Route("")
public class ContactCrudView extends Div {

    private final Crud<Contact> crud;
    private final ContactDataProvider dataProvider;

    private final AtomicLong selectedContactId = new AtomicLong(0L);

    public ContactCrudView() {
        this.crud = new Crud<>(Contact.class, createEditor());
        this.dataProvider = new ContactDataProvider();

        setupGrid();
        setupDataProvider();

        add(this.crud);
    }

    private CrudEditor<Contact> createEditor() {
        TextField firstName = new TextField("First name");
        TextField lastName = new TextField("Last name");
        EmailField email = new EmailField("Email");
        TextField phoneNumber = new TextField("Phone Number");
        TextField country = new TextField("Country");
        TextField city = new TextField("city");
        TextField street = new TextField("Street");
        FormLayout form = new FormLayout(firstName, lastName, email, phoneNumber, country, city, street);

        Binder<Contact> binder = new Binder<>(Contact.class);
        binder.forField(firstName).bind(Contact::getFirstName, Contact::setFirstName);
        binder.forField(lastName).bind(Contact::getLastName, Contact::setLastName);
        binder.forField(email)
            .withValidator(new EmailValidator("Please insert a valid email", true))
            .bind(Contact::getEmail, Contact::setEmail);
        binder.forField(phoneNumber)
            .asRequired()
            .withConverter(value -> value.replaceAll("[^\\d]+", ""), value -> value)
            .withValidator(value -> Objects.nonNull(value) && !value.isEmpty(), "Please insert a valid phone number")
            .withValidator(value -> {
                var currentContactId = Objects.equals(selectedContactId.get(), 0L) ? null : selectedContactId.get();
                return dataProvider.isUniquePhoneNumber(currentContactId, value);
            }, "The phone number is already registered in the system")
            .withNullRepresentation("").bind(Contact::getPhoneNumber, Contact::setPhoneNumber);
        binder.forField(country).bind(Contact::getCountry, Contact::setCountry);
        binder.forField(city).bind(Contact::getCity, Contact::setCity);
        binder.forField(street).bind(Contact::getStreet, Contact::setStreet);

        return new BinderCrudEditor<>(binder, form);
    }

    private void setupGrid() {
        Grid<Contact> grid = crud.getGrid();

        List<String> visibleColumns = Arrays.asList("firstName", "lastName", "email", "phoneNumber", "vaadin-crud-edit-column");
        grid.getColumns().forEach(column -> {
            String key = column.getKey();
            if (!visibleColumns.contains(key))
                grid.removeColumn(column);
        });

        grid.setColumnOrder(
            grid.getColumnByKey("firstName"),
            grid.getColumnByKey("lastName"),
            grid.getColumnByKey("email"),
            grid.getColumnByKey("phoneNumber"),
            grid.getColumnByKey("vaadin-crud-edit-column"));
    }

    private void setupDataProvider() {
        crud.setDataProvider(dataProvider);
        crud.addEditListener(event -> selectedContactId.set(event.getItem().getId()));
        crud.addCancelListener(event -> selectedContactId.set(0L));
        crud.addSaveListener(event -> {
            dataProvider.persist(event.getItem());
            selectedContactId.set(0L);
        });
        crud.addDeleteListener(event -> {
            dataProvider.delete(event.getItem());
            selectedContactId.set(0L);
        });
    }

}