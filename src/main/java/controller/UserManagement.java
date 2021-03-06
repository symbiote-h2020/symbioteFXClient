package controller;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import eu.h2020.symbiote.security.ClientSecurityHandlerFactory;
import eu.h2020.symbiote.security.commons.enums.AccountStatus;
import eu.h2020.symbiote.security.commons.enums.OperationType;
import eu.h2020.symbiote.security.commons.enums.UserRole;
import eu.h2020.symbiote.security.commons.exceptions.custom.AAMException;
import eu.h2020.symbiote.security.commons.exceptions.custom.SecurityHandlerException;
import eu.h2020.symbiote.security.communication.AAMClient;
import eu.h2020.symbiote.security.communication.IAAMClient;
import eu.h2020.symbiote.security.communication.payloads.AAM;
import eu.h2020.symbiote.security.communication.payloads.Credentials;
import eu.h2020.symbiote.security.communication.payloads.UserDetails;
import eu.h2020.symbiote.security.communication.payloads.UserManagementRequest;
import eu.h2020.symbiote.security.handler.ISecurityHandler;

public class UserManagement {

		
	public static void init() throws NoSuchAlgorithmException, SecurityHandlerException {
	}
	
	
	
	public static void addUser(Properties theProperties) {
		
		
//        log.info("Registering to PAAM: " + platformId);
		String platformId=ConnectionManager.homePlatformId;

		String paamOwnerUsername=theProperties.getProperty("platform.owner.name");
		String paamOwnerPassword=theProperties.getProperty("platform.owner.pass");
		
		String username=theProperties.getProperty("appuser");
		String password=theProperties.getProperty("apppass");
		
        try {

             Map<String, AAM> availableAAMs = ConnectionManager.securityHandler.getAvailableAAMs();
             AAMClient aamClient = new AAMClient(availableAAMs.get(platformId).getAamAddress());
//             log.info("Registering to PAAM: " + platformId + " with url " + availableAAMs.get(platformId).getAamAddress());

             
             Credentials paamOwnerCredentials=new Credentials(paamOwnerUsername, paamOwnerPassword);
             Credentials newUserCredentials=new Credentials(username, password);
             UserDetails newUserDetails
             			=new UserDetails(
             					newUserCredentials, 
             					"icom@icom.com",
             					UserRole.USER, 
             					AccountStatus.ACTIVE,
             					new HashMap<>(), 
             					new HashMap<>(),
             					true,
             					false
             				);
             
            UserManagementRequest userManagementRequest 
            		= new UserManagementRequest(
            				paamOwnerCredentials,
            				newUserCredentials,
            				newUserDetails,
            				OperationType.CREATE);

            try {
                aamClient.manageUser(userManagementRequest);
 //               log.info("User registration done");
            } catch (AAMException e) {
//                log.error(e);
            	e.printStackTrace();
                return;
            }
        } catch (SecurityHandlerException e) {
            e.printStackTrace();
            return;
        }

		
		
	}

}
