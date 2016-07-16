package au.com.cybersearch2.cybertete.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ContactEntry
 * Each contact entry contains the user's JID and a locally assigned name or nickname.
 * Also included is the Contact Group to which the contact belongs. Note only the conbination
 * of user and parent is unique. There is also an "id" unique to each entry.
 * ContactEntry and ContactGroup items are linked to build a MultiGroupContactsTree which backs the ContactsView.
 * @author Andrew Bowley
 * 22 Oct 2015
 */
public class ContactEntry extends AbstractModelObject implements ContactItem
{
    /** User (bare) JID */
    protected String user;
    /** User name or nickname */
    protected String name;
    /** Owning Contact Group */
    protected ContactGroup parent;
    /** Contact status, including unavailable (offline) */
    private Presence presence;
    /** Unique index */
    private int id;
    /** The Chat window id assigned to entry while a session is in progress */
    private String windowId;

    /** Unique id generator */
    static private AtomicInteger nextId;

    static 
    {
        nextId = new AtomicInteger();
    }

    /**
     * Create ContactEntry object
     * @param name User name or nickname
     * @param user User (bare) JID
     * @param parent Owning Contact Group
     */
    public ContactEntry(String name, String user, ContactGroup parent) 
    {
        this.name = name;
        this.user = user;
        this.parent = parent;
        presence = Presence.offline;
        id = getNextId();
    }

    /**
     * Add this item to the parent's children list
     */
    public void addSelfToParent()
    {
        List<ContactEntry> entries = new ArrayList<ContactEntry>();
        entries.addAll((List<ContactEntry>) parent.getItems());
        int index = 0;
        // Remove child with matching user value, if found
        for (ContactEntry contactEntry: entries)
        {
            if (contactEntry.getUser().equals(user))
            {
                entries.remove(index);
                break; // Assume no duplicates
            }
            ++index;
        }
        entries.add(this);
        // SetItems() fires property change to update view
        parent.setItems(entries);
    }

    /**
     * Returns name
     * @see au.com.cybersearch2.cybertete.model.ContactItem#getName()
     */
    @Override
    public String getName() 
    {
        return name;
    }

    /**
     * Set name
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
     * Equals matches on parent and user
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object another)
    {
        if (another instanceof ContactEntry)
        {
            if (!parent.equals(((ContactEntry) another).getParent()))
                return false;
            return ((ContactEntry)another).getUser().equals(user);
        }
        return false;
    }

    /**
     * Returns window ID when contact is Chat participant wiht Chat window
     * @return window ID
     */
    public String getWindowId()
    {
        return windowId;
    }

    /**
     * Set window ID
     * @param windowId
     */
    public void setWindowId(String windowId)
    {
        this.windowId = windowId;
    }

    /**
     * Returns user
     * @return JID
     */
    public String getUser()
    {
        return user;
    }

    /**
     * Set user
     * @param user JID
     */
    public void setUser(String user)
    {
        firePropertyChange("user", this.user, this.user = user);
    }

    /**
     * Returns model Presence
     * @return Presence
     */
    public Presence getPresence()
    {
        return presence;
    }

    /**
     * Set presence
     * @param presence Model presence. Will be translated to XMPP presence when sent to Chat server.
     */
    public void setPresence(Presence presence)
    {
        firePropertyChange("presence", this.presence, this.presence = presence);
    }

    /**
     * Attach this entry to to a new parent
     * @param contactGroup
     */
    public void attach(ContactGroup contactGroup)
    {
        // Detach from former parent
        ((List<ContactEntry>) parent.getItems()).remove(this);
        parent = contactGroup;
        addSelfToParent();
    }

    /**
     * Returns a hash code value for the object
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        return user.hashCode() ^ parent.hashCode();
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
