package com.linxa.phonebook.views.list;

import com.linxa.phonebook.model.entity.Contact;
import com.linxa.phonebook.service.ContactService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.Objects;

@PageTitle("Contacts")
@Route("")
public class ListView extends VerticalLayout {

    private final TextField searchField = new TextField();
    private final Button addContactBtn = new Button("Add Contact");

    private final Grid<Contact> grid = new Grid<>(Contact.class);
    private final ContactForm form = new ContactForm();

    public ListView() {
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
        grid.setColumns("firstName", "lastName", "email", "phoneNumber");
        grid.getColumns().forEach(column -> column.setAutoWidth(true));
        grid.asSingleSelect().addValueChangeListener(event -> editContact(event.getValue()));

        form.setWidth("25em");
        form.addSaveListener(this::saveContact);
        form.addDeleteListener(this::deleteContact);
        form.addCloseListener(event -> {
            closeContactForm();
            grid.asSingleSelect().clear();
        });

        var content = new HorizontalLayout(grid, form);
        content.add(grid, form);
        content.setFlexGrow(2, grid);
        content.setFlexGrow(1, form);
        content.setSizeFull();
        return content;
    }

    private void updateList() {
        grid.setItems(ContactService.getInstance().findContacts(searchField.getValue()));
    }

    private void closeContactForm() {
        form.setContact(null);
        form.setVisible(false);
    }

    private void addContact() {
        grid.asSingleSelect().clear();
        editContact(new Contact());
    }

    private void editContact(Contact contact) {
        if (Objects.isNull(contact)) {
            closeContactForm();
            return;
        }

        form.setContact(contact);
        form.setVisible(true);
    }

    private void saveContact(ContactForm.SaveEvent event) {
        Contact contact = event.getContact();
        if (Objects.isNull(contact.getPhoneNumber()) || contact.getPhoneNumber().isEmpty()) {
            showErrorNotification("Phone number cannot be empty");
            return;
        }
        var phoneNumber = contact.getPhoneNumber().replaceAll("[^\\d]+", "");
        if (phoneNumber.isEmpty()) {
            showErrorNotification("Please insert a valid phone number including digits");
            return;
        }
        contact.setPhoneNumber(phoneNumber);
        if (!ContactService.getInstance().hasUniqueNumber(contact)) {
            showErrorNotification("The phone number is already registered");
            return;
        }
        ContactService.getInstance().saveContact(contact);
        updateList();
        closeContactForm();
    }

    private void deleteContact(ContactForm.DeleteEvent event) {
        ContactService.getInstance().deleteContact(event.getContact());
        updateList();
        closeContactForm();
    }

    private void showErrorNotification(String message) {
        Notification.show(message, 3000, Notification.Position.TOP_STRETCH).addThemeVariants(NotificationVariant.LUMO_ERROR);
    }

}
