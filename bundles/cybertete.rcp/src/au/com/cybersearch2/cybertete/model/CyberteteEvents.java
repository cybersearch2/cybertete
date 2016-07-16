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
package au.com.cybersearch2.cybertete.model;

/**
 * CyberteteEvents
 * Message types applied to Eclipse Event Broker.
 * @author Andrew Bowley
 * 12 Nov 2015
 */
public interface CyberteteEvents
{
    /** Use package name base for global uniqueness */
    public static final String  TOPIC_BASE = "au/com/cybersearch/cybertete";
    /** User presence: online, away, dnd (do not disturb) and offline */
    public static final String  PRESENCE = TOPIC_BASE + "/presence";

    /** Chat server connection status: up, establish, down, offline */
    public static final String  CONNECTION = TOPIC_BASE + "/connection";
    public static final String  COMMS_UP = CONNECTION  + "/up";
    public static final String  COMMS_ESTABLISH = CONNECTION  + "/establish";
    public static final String  COMMS_DOWN = CONNECTION  + "/down";
    public static final String  COMMS_OFFLINE = CONNECTION  + "/offline";
    /** Chat session satus: pause (communications down), resume (communications up) */
    public static final String  SESSION = TOPIC_BASE + "/session";
    public static final String  SESSION_PAUSE = SESSION  + "/pause";
    public static final String  SESSION_RESUME = SESSION  + "/resume";
    /** Network status: unavailable, connected, secure, authorised (fully available) */
    public static final String  NETWORK =TOPIC_BASE + "/network";
    public static final String  NETWORK_UNAVAILABLE = NETWORK  + "/unavailable";
    public static final String  NETWORK_CONNECTED = NETWORK  + "/connected";
    public static final String  NETWORK_SECURE = NETWORK  + "/secure";
    public static final String  NETWORK_AUTHORIZED = NETWORK  + "/authorised";
    /** Events for Application lifecycle:  */
    public static final String  LIFECYCLE = TOPIC_BASE + "/lifecycle";
    public static final String  POST_CONTEXT_CREATE = LIFECYCLE + "/postContextCreate";
    public static final String  PROCESS_ADDITIONS = LIFECYCLE + "/processAdditions";
   
    /** Events for change of Application status (incomplete): logout, login, shutdown  */
    public static final String  APPLICATION_STATE = TOPIC_BASE + "/applicationState";
    public static final String  LOGOUT = APPLICATION_STATE + "/logout";
    public static final String  LOGIN = APPLICATION_STATE + "/login";
    public static final String  SHUTDOWN = APPLICATION_STATE + "/shutdown";
    /** Events for change of perspective: default, offline */
    public static final String  PERSPECTIVE = TOPIC_BASE + "/perspective";
    public static final String  PERSPECTIVE_DEFAULT= SESSION  + "/default";
    public static final String  PERSPECTIVE_OFFLINE = SESSION  + "/offline";
    /** Client Cert */
    public static final String  CLIENT_CERT = TOPIC_BASE + "/clientCert";
    /** Save current login session */
    public static final String  SAVE_LOGIN_SESSION = TOPIC_BASE + "/saveLoginSession";
    /** Events for Login Config */
    public static final String  LOGIN_CONFIG = TOPIC_BASE + "/loginConfig";
    public static final String  UPDATE_LOGIN_CONFIG = LOGIN_CONFIG + "/update";
    public static final String  LAST_USER_CONFIG = LOGIN_CONFIG + "/lastUser";
    /** Events for Keystore Config */
    public static final String  KEYSTORE_CONFIG = TOPIC_BASE + "/keystoreConfig";
    public static final String  VALIDATE_KEYSTORE_CONFIG = KEYSTORE_CONFIG + "/validate";
    public static final String  KEYSTORE_CONFIG_DONE = KEYSTORE_CONFIG + "/done";
    public static final String  SAVE_KEYSTORE_CONFIG = KEYSTORE_CONFIG + "/save";
    
    /** Save Client Cert Auth Config */
    public static final String  SAVE_CLIENT_CERT_CONFIG = TOPIC_BASE + "/saveClientCertConfig";
    /** Events for Kerberos Config */
    public static final String  KERBEROS_CONFIG = TOPIC_BASE + "/kerberosConfig";
    public static final String  LOAD_KERBEROS_CONFIG = KERBEROS_CONFIG + "/load";
    //public static final String  KERBEROS_CONFIG_DONE = KERBEROS_CONFIG + "/done";
    /** Events for information popups */
    public static final String  INFO_POPUP = TOPIC_BASE + "/infoPopup";
    public static final String  CERT_INFO_POPUP = INFO_POPUP + "/certificate";
    /** Post Chat body */
    public static final String  POST_CHAT = TOPIC_BASE + "/postChat";
    /** Events for user cancel */
    public static final String  USER_CANCEL = TOPIC_BASE + "/userCancel";
    /** New Login Flagged */
    public static final String  NEW_LOGIN = TOPIC_BASE + "/newLogin";

}
