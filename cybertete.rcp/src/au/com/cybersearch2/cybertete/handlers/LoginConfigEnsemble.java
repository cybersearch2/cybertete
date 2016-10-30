/**
    Copyright (C) 2016  www.cybersearch2.com.au

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
package au.com.cybersearch2.cybertete.handlers;

import au.com.cybersearch2.cybertete.model.LoginBean;

/**
 * LoginConfigEnsemble
 * Login configuration details to be saved along with callback method to call on completion
 * @author Andrew Bowley
 * 28 Apr 2016
 */
public class LoginConfigEnsemble
{
    /** Login configuration details */
    LoginBean loginBean;
    /**  Callback method to call on completion */
    UpdateLoginConfigEvent updateLoginConfigEvent;
    /** Flag set true if error messages to be displayed in a standard dialog */
    boolean showMessage;

    /**
     * Create LoginConfigEnsemble object
     * @param loginBean Login configuration details
     * @param updateLoginConfigEvent Callback method to call on completion
     * @param showMessage Flag set true if error messages to be displayed in a standard dialog
     */
    public LoginConfigEnsemble(LoginBean loginBean, UpdateLoginConfigEvent updateLoginConfigEvent, boolean showMessage)
    {
        this.loginBean = loginBean;
        this.updateLoginConfigEvent = updateLoginConfigEvent;
        this.showMessage = showMessage;
    }

    /**
     * @return the loginBean
     */
    public LoginBean getLoginBean()
    {
        return loginBean;
    }

    /**
     * @return the updateLoginConfigEvent
     */
    public UpdateLoginConfigEvent getUpdateLoginConfigEvent()
    {
        return updateLoginConfigEvent;
    }

    /**
     * @return the showMessage
     */
    public boolean isShowMessage()
    {
        return showMessage;
    }

}
