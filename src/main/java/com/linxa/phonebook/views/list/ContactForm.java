package com.linxa.phonebook.views.list;

import com.linxa.phonebook.model.entity.Contact;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;


public class ContactForm extends FormLayout {

    private final TextField firstName = new TextField("First name");
    private final TextField lastName = new TextField("Last name");
    private final TextField email = new TextField("Email");
    private final TextField phoneNumber = new TextField("Phone number");
    private final TextField country = new TextField("Country");
    private final TextField city = new TextField("city");
    private final TextField street = new TextField("Street");

    private final Button save = new Button("Save");
    private final Button delete = new Button("Delete");
    private final Button cancel = new Button("cancel");

    private final Binder<Contact> binder = new Binder<>(Contact.class);

    private Contact contact;

    public ContactForm() {
        binder.bindInstanceFields(this);

        add(
            firstName,
            lastName,
            email,
            phoneNumber,
            country,
            city,
            street,
            getButtons()
        );
    }

    public void setContact(final Contact contact) {
        this.contact = contact;
        binder.setBean(contact);
    }

    private Component getButtons() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.addClickShortcut(Key.ENTER);
        save.addClickListener(event -> fireEvent(new SaveEvent(this, contact)));

        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        delete.addClickListener(event -> fireEvent(new DeleteEvent(this, contact)));

        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        cancel.addClickShortcut(Key.ESCAPE);
        cancel.addClickListener(event -> fireEvent(new CloseEvent(this)));

        return new HorizontalLayout(save, delete, cancel);
    }

    public Registration addDeleteListener(final ComponentEventListener<DeleteEvent> listener) {
        return addListener(DeleteEvent.class, listener);
    }

    public Registration addSaveListener(final ComponentEventListener<SaveEvent> listener) {
        return addListener(SaveEvent.class, listener);
    }

    public Registration addCloseListener(final ComponentEventListener<CloseEvent> listener) {
        return addListener(CloseEvent.class, listener);
    }

    // Events
    public static abstract class ContactFormEvent extends ComponentEvent<ContactForm> {

        private final Contact contact;

        protected ContactFormEvent(final ContactForm source, final Contact contact) {
            super(source, false);
            this.contact = contact;
        }

        public Contact getContact() {
            return contact;
        }

    }

    public static class SaveEvent extends ContactFormEvent {
        SaveEvent(final ContactForm source, final Contact contact) {
            super(source, contact);
        }
    }

    public static class DeleteEvent extends ContactFormEvent {
        DeleteEvent(final ContactForm source, final Contact contact) {
            super(source, contact);
        }

    }

    public static class CloseEvent extends ContactFormEvent {
        CloseEvent(final ContactForm source) {
            super(source, null);
        }
    }

}
