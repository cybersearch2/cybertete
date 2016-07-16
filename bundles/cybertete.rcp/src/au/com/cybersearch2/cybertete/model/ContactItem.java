package au.com.cybersearch2.cybertete.model;

/**
 * ContactItem
 * Common interface for Contact nodes used to build a tree.
 * Each node has a name, unique id and parent, though the root 
 * of a tree will have a null parent. Note that the name field
 * for ContactGroups must be unique.
 * @author Andrew Bowley
 * 1 Dec 2015
 */
public interface ContactItem
{
    /** BeanProperties name for list of GroupContact objects */
    public static final String GROUP_LIST_NAME = "groups";
    /** BeanProperties name for list of EntryContact objects */
    public static final String ENTRY_LIST_NAME = "items";
    /** Name of group at the tree of contact items */
    public static final String ROOT_GROUP_NAME = "Groups";
    /** Tag for ungrouped entries which are placed under the root group */
    public static final String UNGROUPED_NAME = "<Ungrouped>";

    /** 
     * Returns name of item
     * @return the name
     */
    public String getName();

    /**
     * Set name of item (must be unique in ContactGroup case)
     * @param name The name
     */
    public void setName(String name);

    /**
     * Returns unique ID of item
     * @return ID
     */
    public int getId();

    /**
     * Returns parent of item
     * @return ContactItem ojbect
     */
    public ContactItem getParent();
}