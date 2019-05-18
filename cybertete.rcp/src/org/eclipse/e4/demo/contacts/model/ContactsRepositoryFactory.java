/*******************************************************************************
 * Copyright (c) 2009 Siemens AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Kai Tödter - initial implementation
 ******************************************************************************/

package org.eclipse.e4.demo.contacts.model;

import java.util.List;
import java.util.ArrayList;

import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.list.WritableList;

public class ContactsRepositoryFactory 
{

	private static final IContactsRepository CONTACTS_REPOSITORY;
	
	static
	{
	
	    CONTACTS_REPOSITORY = new IContactsRepository(){

            @SuppressWarnings({"unchecked", "rawtypes"})
            @Override
            public IObservableList<Contact> getAllContacts()
            {
                List<Contact> contacts = new ArrayList<Contact>();
                contacts.add(new Contact("Alize", "Aukland"));
                contacts.add(new Contact("Sydney", "Google"));
                contacts.add(new Contact("Nadine", "Jakarta"));
                return new WritableList(contacts, null);
            }

            @Override
            public void addContact(Contact contact)
            {
            }

            @Override
            public void removeContact(Contact contact)
            {
            }};
	}

	public static IContactsRepository getContactsRepository() 
	{
		return CONTACTS_REPOSITORY;
	}
}
