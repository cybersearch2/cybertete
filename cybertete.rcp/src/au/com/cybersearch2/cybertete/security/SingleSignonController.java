package au.com.cybersearch2.cybertete.security;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;
import org.jxmpp.jid.util.JidUtil;

import au.com.cybersearch2.cybertete.handlers.ConfigNotifier;
import au.com.cybersearch2.cybertete.handlers.LoginConfigEnsemble;
import au.com.cybersearch2.cybertete.handlers.UpdateLoginConfigEvent;
import au.com.cybersearch2.cybertete.model.LoginBean;
import au.com.cybersearch2.cybertete.model.service.SessionDetails;
import au.com.cybersearch2.cybertete.preferences.UserDataStore;
import au.com.cybersearch2.cybertete.views.LoginView;

@Creatable
public class SingleSignonController implements UpdateLoginConfigEvent {

    /** Login configuration saved as preferences */
    @Inject
    UserDataStore userDataStore;
    /** E4 application model part service */
    @Inject
    EPartService partService;
    
    private boolean dirtyFlag;
    private SessionDetails currentSessionDetails;
    private SessionDetails oldSessionDetails;
    /** Notifies configuration events */
    private ConfigNotifier configNotifier;

    /**
     * Constuct SingleSignonController object
     * @param configNotifier Post events for login configuration updates 
     */
    @Inject
    public SingleSignonController(ConfigNotifier configNotifier) 
    {
    	this.configNotifier = configNotifier;
    }
    
	public String getAccountJid() {
		return userDataStore.getSingleSignonUser();
	}

    /**
     * Handle Apply button clicked
     * @param accountJidValue
     * @return flag set true if changes applied successfully
     */
    public boolean onApply(String accountJidValue)
    {
        // Save configuration when Apply button pressed
        return applyChanges(accountJidValue);
    }

    /**
     * Handle OK button clicked
     * @param accountJidValue
     */
    public void onOk(String accountJidValue)
    {
        // Save configuration and exit
        if (applyChanges(accountJidValue))
        {
        	LoginBean loginBean =  getLoginBean(currentSessionDetails);
        	LoginConfigEnsemble loginConfigEnsemble = new LoginConfigEnsemble(loginBean, this, false);
            configNotifier.applyChanges(loginConfigEnsemble);
        }
    }

    /**
     * 
     * @see au.com.cybersearch2.cybertete.handlers.UpdateLoginConfigEvent#onUpdateComplete(au.com.cybersearch2.cybertete.security.LoginStatus)
     */
    @Override   
    public void onUpdateComplete(LoginStatus loginStatus)
    {
        if (loginStatus == LoginStatus.noError)
        {
        	if (oldSessionDetails != null)
        	{
            	LoginBean loginBean =  getLoginBean(oldSessionDetails);
            	oldSessionDetails = null;
            	LoginConfigEnsemble loginConfigEnsemble = new LoginConfigEnsemble(loginBean, this, false);
                configNotifier.applyChanges(loginConfigEnsemble);
        	} else
        		activateLoginView();
        }
    }

    /**
     * Handle Cancel button clicked
     */
    public void onCancel()
    {
        // Exit if Cancel pressed
        activateLoginView();
    }

    /**
     * Activate login view, which happens on security view cancel or OK
     */
    public void activateLoginView()
    {
        MPart loginViewPart = partService.findPart(LoginView.LOGIN_VIEW_ID);
        partService.showPart(loginViewPart, PartState.ACTIVATE);
    }

	public void onAccountJidChange() {
		dirtyFlag = true;
	}
	
    boolean applyChanges(String accountJidValue)
    {
    	currentSessionDetails = null;
    	oldSessionDetails = null;
    	boolean ok = true;
    	if (dirtyFlag) 
    	{
    		if (!accountJidValue.isEmpty())
    		{
    			ok = JidUtil.isValidBareJid(accountJidValue);
    			if (ok)
    				setAccountJid(accountJidValue);
    		}
    	}
    	dirtyFlag = false;
    	return ok;
    }

    protected boolean isGssapi(SessionDetails sessionDetails) {
		String singleSignonUser = userDataStore.getSingleSignonUser();
		return !singleSignonUser.isEmpty() && singleSignonUser.equalsIgnoreCase(sessionDetails.getJid()) ;
	}

	private void setAccountJid(String accountJidValue) 
	{
		currentSessionDetails = userDataStore.getSessionDetails(accountJidValue);
		if (currentSessionDetails == null)
		{
			currentSessionDetails = new SessionDetails(accountJidValue);
		}
	}

	private LoginBean getLoginBean(SessionDetails sessionDetails) 
	{
		return new LoginBean() {

			@Override
			public String getJid() {
				return sessionDetails.getJid();
			}

			@Override
			public String getHost() {
				String host = sessionDetails.getHost();
				return host == null ? "" : host;
			}

			@Override
			public int getPort() {
				return sessionDetails.getPort();
			}

			@Override
			public String getUsername() {
				return sessionDetails.getUsername().toString();
			}

			@Override
			public String getPassword() {
				return "";
			}

			@Override
			public boolean isAutoLogin() {
				return false;
			}

			@Override
			public boolean isPlainSasl() {
				return false;
			}

			@Override
			public String getGssapiPrincipal() {
				return sessionDetails.getJid();
			}};

	}
}
