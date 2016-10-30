/**
    Copyright (C) 2015  www.cybersearch2.com.au

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/> */
 
package au.com.cybersearch2.cybertete.views;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.xml.ws.WebServiceException;

import org.eclipse.e4.core.services.log.ILoggerProvider;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;

import au.com.cybersearch2.cybertete.model.ContactEntry;
import au.com.cybersearch2.cybertete.model.service.ChatSession;
import au.com.cybersearch2.dialogs.SyncErrorDialog;
import au.com.cybersearch2.e4.JobScheduler;

/**
 * ChatSessionView
 * Window split into transcript of chat and text entry field.
 * Supports both local and remote initiated chats.
 * @author Andrew Bowley
 * 6 Mar 2016
 */
public class ChatSessionView
{
    /** Model id of this view */
    public static final String ID = "au.com.cybersearch2.cybertete.part.chat";
    /** Id of Stack containing all ChatSessionView windows */
    public static final String STACK_ID = "au.com.cybersearch2.cybertete.partstack.chat";

    /** ContactEntry of currently selected remote user or null if none selected */
    ContactEntry contact;
    /** Chat session operator */
    ChatSession chatSession;
    /** Logger */
    Logger logger;

    /** The view contents */
    @Inject
    ChatSessionControl chatSessionControl;
    
    /** Display error message */
    @Inject 
    SyncErrorDialog errorDialog;
    /** Job scheduler */
    @Inject 
    JobScheduler jobScheduler;
 
    /**
     * Post construct. Set window layout and controls. 
     * Add listener for new Chat connection established.
     * @param loggerProvider ILoggerProvider object
     */
    @PostConstruct
    public void postConstruct(
        ILoggerProvider loggerProvider) 
    {
        logger = loggerProvider.getClassLogger(ChatSessionView.class);
        // Add key listener to send line when Enter key pressed
        chatSessionControl.setKeyListener(new KeyAdapter() {
            
            public void keyPressed(KeyEvent event) 
            {
                if (event.character == SWT.CR)
                {
                    sendLine();
                    event.doit = false;
                }
            }
        });
    }

    /**
     * Predestroy.
     * Close this window. Closes chat session of current contact.
     */
	@PreDestroy
	public void preDestroy() 
	{
	    if (chatSession != null)
	        chatSession.close("Chat window closed");
	}

	/**
	 * On focus event, set entry focus so user can type
	 */
	@Focus
	public void onFocus() 
	{
	    chatSessionControl.onFocus();
	}

    public void setChatSession(ChatSession chatSession)
    {
        this.chatSession = chatSession;
    }

	/**
	 * Set remote user contact entry item. Intiaties start of chat session.
	 * @param contact ContactEntry
	 */
    public void setSelection(ContactEntry contact) 
    {
        if (contact != null) 
            this.contact = contact;
    }

    public void displayMessage(String info)
    {
        chatSessionControl.displayMessage(info);
    }

    /**
     * Display chat text in transcript control
     * @param from Sender's contact entry item
     * @param body Text to be sent
     */
    public void displayMessage(ContactEntry from, String body)
    {
        // Skip if no text to display
        if (body.length() == 0)
            return;
        // Write formatted line of text
        chatSessionControl.displayMessage(renderMessage(from.getUser(), body));
    }

    /**
     * Enable view
     * @return Flag set true if view changed state
     */
    public boolean disable()
    {
        return chatSessionControl.setEnabled(false);
    }

    /**
     * Disable view
     * @return Flag set true if view changed state
     */
    public boolean enable()
    {
        return chatSessionControl.setEnabled(true);
    }

    public void clear()
    {
        chatSessionControl.clear();
    }

    /**
     * Send one line of text entered by local user to remote user
     */
    private void sendLine()
    {
        if (chatSession != null)
        {
            String body = chatSessionControl.getText();
            sendChatMessage(body);
            displayMessage(chatSession.getSessionOwner(), body);
        }
    }

    /**
     * Send text message to remote user
     * @param body Message text
     */
    private void sendChatMessage(final String body)
    {
        jobScheduler.schedule("Send chat message", new Runnable(){
            
            @Override
            public void run()
            {
                try
                {
                    chatSession.sendMessage(body);
                }
                catch (final WebServiceException e)
                {
                    logger.error(e.getCause(), e.getMessage());
                    errorDialog.showError("Communications Error", e.getMessage());
                }
            }
       });
    }
    
    /**
     * Format line of text for display
     * @param from Sender's JID
     * @param body Text content
     * @return Formatted text &lt;from&gt; body
     */
    private String renderMessage(String from, String body) 
    {
        if (from == null)
            return body;
        int j = from.indexOf('@');
        if (j > 0)
            from = from.substring(0, j);
        return "<" + from + ">  " + body;
    }


}