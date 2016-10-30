package au.com.cybersearch2.cybertete.model.service;

/**
 * Launches Logon Dialog and then performs logon while displaying progress dialog
 * LoginController
 * @author Andrew Bowley
 * 19 Apr 2016
 */
public interface LoginController
{

    /**
     * Login to Chat Server using JID entered interactively by user
     * @param serviceLoginTask ConnectLoginTask implementation to login using Chat Service
     * @return Flag set true if login successfule
     */
    boolean login(ConnectLoginTask serviceLoginTask);

}