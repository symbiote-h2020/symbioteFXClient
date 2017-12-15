import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import eu.h2020.symbiote.security.ClientSecurityHandlerFactory;
import eu.h2020.symbiote.security.commons.exceptions.custom.SecurityHandlerException;
import eu.h2020.symbiote.security.handler.ISecurityHandler;

public class ConnectionManagement {

	
    public static ISecurityHandler securityHandler;
//    public static String symbIoTeCoreUrl="https://symbiote-dev.man.poznan.pl:8100/coreInterface/v1";
    public static String symbIoTeCoreUrl="https://symbiote-dev.man.poznan.pl/coreInterface";
    static String coreAAMAddress        ="https://symbiote-dev.man.poznan.pl/coreInterface";
    static String keystorePath="ks.jks";
    static String keystorePassword="1234";
    static String userId="";
//    static String clientId="backendDemoApp";
    static String clientId="fatClientDemoApp";

    static String password="Catberta";
    
    

	
	
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

}
