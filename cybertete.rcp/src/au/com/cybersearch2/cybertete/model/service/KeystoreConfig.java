package au.com.cybersearch2.cybertete.model.service;

/**
 * Keystore configuration information used to obtain SSL context and client certificate chain
 * KeystoreConfig
 * @author Andrew Bowley
 * 19 Apr 2016
 */
public interface KeystoreConfig
{
    public static final String SSL_PROTOCOL = "TLSv1";
    public static final String[] KEYSTORE_TYPES =
    {
            "PKCS12",
            "JKS",
            "JCEKS"
    };

    /**
     * @return the keystore file
     */
    String getKeystoreFile();

    /**
     * @return the keystore type
     */
    String getKeystoreType();

    /**
     * @return the keystore password
     */
    String getKeystorePassword();

}