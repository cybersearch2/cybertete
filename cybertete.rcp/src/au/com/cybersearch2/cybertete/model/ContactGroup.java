package au.com.cybersearch2.cybertete.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ContactGroup
 * Contains a collection of Contact Entries.
 * It may contain a collection of Contact Groups, but such nesting is expected
 * only for the root of a Roster. 
 * Each Contact Group has a name and a unique id.
 * @author Andrew Bowley
 * 1 Dec 2015
 */
public class ContactGroup extends AbstractModelObject implements ContactItem
{
    /** Name of group */
    private String name;
    /** Unique index */
    private int id;
    /** Child groups (only root of contacts tree should have children) */
    private List<ContactGroup> groups = new ArrayList<ContactGroup>();
    /** Child contact entries */
    private List<ContactEntry> items = new ArrayList<ContactEntry>();
    /** Parent group or null if root */
    private ContactGroup parent;
    
    /** Unique id generator */
    static private AtomicInteger nextId;

    static 
    {
        nextId = new AtomicInteger();
    }

    /**
     * Create root ContactGroup object (no parent)
     * @param name Group name
     */
    public ContactGroup(String name)
    {
        this.name = name;
        id = getNextId();
    }

    /**
     * Create nested ContactGroup object
     * @param name Group name
     * @param parent Parent group
     */
    public ContactGroup(String name, ContactGroup parent)
    {
        this(name);
        this.parent = parent;
    }
 
    /**
     * Returns group name 
     * @see au.com.cybersearch2.cybertete.model.ContactItem#getName()
     */
    @Override
    public String getName() 
    {
        return name;
    }

    /**
     * Set group name 
     * @see au.com.cybersearch2.cybertete.model.ContactItem#setName(java.lang.String)
     */
    @Override
    public void setName(String name) 
    {
        firePropertyChange("name", this.name, this.name = name);
    }
 
    /**
     * Returns ID
     * @see au.com.cybersearch2.cybertete.model.ContactItem#getId()
     */
    @Override
    public int getId()
    {
        return id;
    }

    /**
     * Returns parent 
     * @see au.com.cybersearch2.cybertete.model.ContactItem#getParent()
     */
    @Override
    public ContactItem getParent()
    {
        return parent;
    }

    /**
     * Returns child groups
     * @return List of ContactGroup objects 
     */
    public List<ContactGroup> getGroups() 
    {
        return groups;
    }

    /**
     * Set child groups
     * @param groups List of ContactGroup objects 
     */
    public void setGroups(List<ContactGroup> groups) 
    {
        firePropertyChange(GROUP_LIST_NAME, this.groups,
                this.groups = groups);
    }

    /**
     * Returns contact entry children
     * @return List of ContactEntry objects
     */
    public List<ContactEntry> getItems() 
    {
        return items;
    }

    /**
     * Returns contact entry specified by user JID
     * @param user JID
     * @return ContactEntry object or null if not found
     */
    public ContactEntry getContact(String user)
    {
        for (ContactEntry contactEntry: items)
            if (contactEntry.getUser().equals(user))
                return contactEntry;
        return null;
    }
    
    /**
     * Set contact entry children 
     * @param items List of ContactEntry objects
     */
    public void setItems(List<ContactEntry> items) 
    {
        firePropertyChange(ENTRY_LIST_NAME, this.items, this.items = items);
    }

    /**
     * equals
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object another)
    {
        if (another instanceof ContactGroup)
            return ((ContactGroup)another).getName().equals(name);
        return false;
    }

    /**
     * hash code
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        return name.hashCode();
    }

    /**
     * Returns next unique ID
     * @return int
     */
    protected static int getNextId()
    {
        return nextId.incrementAndGet();
    }

}
