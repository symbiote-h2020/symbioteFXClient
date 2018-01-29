package controller;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import eu.h2020.symbiote.security.ClientSecurityHandlerFactory;
import eu.h2020.symbiote.security.commons.exceptions.custom.SecurityHandlerException;
import eu.h2020.symbiote.security.handler.ISecurityHandler;
import events.ConfigurationChangedHandler;
import main.ClientMain;

public class ConnectionManager implements ConfigurationChangedHandler {

	
    public static ISecurityHandler securityHandler;

//    public static String symbIoTeCoreUrl="https://symbiote-dev.man.poznan.pl/coreInterface";
//    static String coreAAMAddress        ="https://symbiote-dev.man.poznan.pl/coreInterface";
    static String symbIoTeCoreUrl=ClientMain.theProperties.getProperty("core.url");
    static String coreAAMAddress =ClientMain.theProperties.getProperty("core.aamURL");

    static String keystorePath=ClientMain.theProperties.getProperty("keystore.Path");
    static String keystorePassword=ClientMain.theProperties.getProperty("keystore.Password");
    

    
    
    public static String homePlatformId=ClientMain.theProperties.getProperty("homeplatform");

    public static String appUser=ClientMain.theProperties.getProperty("appuser");
    public static String appPass=ClientMain.theProperties.getProperty("apppass");

    
    public static String clientId="fatClientDemoApp"; // each client must have an id. Currently this is only used to trace accesses.
    
    static String userId="";	// Doesn't matter yet.

	
	
	public static void init() throws NoSuchAlgorithmException, SecurityHandlerException {
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    public void checkClientTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }

                    public void checkServerTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }
                }
        };
        // Install the all-trusting trust manager
        SSLContext sc = SSLContext.getInstance("SSL");
        try {
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

        securityHandler = ClientSecurityHandlerFactory.getSecurityHandler(coreAAMAddress, keystorePath,
                keystorePassword, userId);

	}



	@Override
	public void confChanged(int changedSetting) {
		try {
			ConnectionManager.init();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (SecurityHandlerException e) {
			e.printStackTrace();
		}		
	}

}
