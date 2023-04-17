package com.linxa.phonebook.views.list;

import com.linxa.phonebook.model.entity.Contact;
import com.linxa.phonebook.service.ContactService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.Objects;

@PageTitle("Contacts")
@Route(value = "")
public class ListView extends VerticalLayout {

    private final ContactService contactService;

    private final TextField searchField = new TextField();
    private final Button addContactBtn = new Button("Add Contact");

    private final Grid<Contact> grid = new Grid<>(Contact.class);
    private final ContactForm form = new ContactForm();

    public ListView(final ContactService contactService) {
        this.contactService = contactService;

        setSizeFull();

        add(
            getToolbar(),
            getContent()
        );

        updateList();
        closeContactForm();
    }

    private Component getToolbar() {
        searchField.setPlaceholder("Search contacts");
        searchField.setClearButtonVisible(true);
        searchField.setValueChangeMode(ValueChangeMode.LAZY);
        searchField.addValueChangeListener(event -> updateList());

        addContactBtn.addClickListener(event -> addContact());

        return new HorizontalLayout(searchField, addContactBtn);
    }

    private Component getContent() {
        grid.setSizeFull();
        grid.setColumns("firstName", "lastName", "email");
        grid.getColumns().forEach(column -> column.setAutoWidth(true));
        grid.asSingleSelect().addValueChangeListener(event -> editContact(event.getValue()));

        form.setWidth("25em");
        form.addSaveListener(this::saveContact);
        form.addDeleteListener(this::deleteContact);
        form.addCloseListener(event -> {
            closeContactForm();
            grid.asSingleSelect().clear();
        });

        final var content = new HorizontalLayout(grid, form);
        content.add(grid, form);
        content.setFlexGrow(2, grid);
        content.setFlexGrow(1, form);
        content.setSizeFull();
        return content;
    }

    private void updateList() {
        grid.setItems(contactService.findContacts(searchField.getValue()));
    }

    private void closeContactForm() {
        form.setContact(null);
        form.setVisible(false);
    }

    private void addContact() {
        grid.asSingleSelect().clear();
        editContact(new Contact());
    }

    private void editContact(final Contact contact) {
        if (Objects.isNull(contact)) {
            closeContactForm();
            return;
        }

        form.setContact(contact);
        form.setVisible(true);
    }

    private void saveContact(final ContactForm.SaveEvent event) {
        contactService.saveContact(event.getContact());
        updateList();
        closeContactForm();
    }

    private void deleteContact(final ContactForm.DeleteEvent event) {
        contactService.deleteContact(event.getContact());
        updateList();
        closeContactForm();
    }

}
