package utils;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import controller.ConnectionManager;
import eu.h2020.symbiote.security.commons.Token;
import eu.h2020.symbiote.security.commons.credentials.AuthorizationCredentials;
import eu.h2020.symbiote.security.commons.credentials.HomeCredentials;
import eu.h2020.symbiote.security.commons.exceptions.custom.SecurityHandlerException;
import eu.h2020.symbiote.security.commons.exceptions.custom.ValidationException;
import eu.h2020.symbiote.security.communication.payloads.AAM;
import eu.h2020.symbiote.security.communication.payloads.SecurityRequest;
import eu.h2020.symbiote.security.helpers.MutualAuthenticationHelper;

public class Utils {

	public static Object sendRequestAndVerifyResponse(String httpMethod, String strUrl, String payload, String homePlatformId,
			String targetPlatformId, String componentId, TypeReference<?> expectedType) {

		Map<String, String> securityRequestHeaders=null;
//		HttpHeaders httpHeaders = new HttpHeaders();
//		httpHeaders.set("Accept", MediaType.APPLICATION_JSON_VALUE);
//		httpHeaders.setContentType(MediaType.APPLICATION_JSON);

		HttpURLConnection urlConnection=null;
		
		// Insert Security Request into the headers
		try {

			Set<AuthorizationCredentials> authorizationCredentialsSet = new HashSet<>();
			Map<String, AAM> availableAAMs = ConnectionManager.securityHandler.getAvailableAAMs();

//			log.info("Getting certificate for " + availableAAMs.get(homePlatformId).getAamInstanceId());
			ConnectionManager.securityHandler.getCertificate(
					availableAAMs.get(homePlatformId), 
					ConnectionManager.appUser, 
					ConnectionManager.appPass, 
					ConnectionManager.clientId
					);

//			log.info("Getting token from " + availableAAMs.get(homePlatformId).getAamInstanceId());
			Token homeToken = ConnectionManager.securityHandler.login(availableAAMs.get(homePlatformId));

			HomeCredentials homeCredentials = ConnectionManager.securityHandler.getAcquiredCredentials()
					.get(homePlatformId).homeCredentials;
			authorizationCredentialsSet
					.add(new AuthorizationCredentials(homeToken, homeCredentials.homeAAM, homeCredentials));

			SecurityRequest securityRequest = MutualAuthenticationHelper.getSecurityRequest(authorizationCredentialsSet,
					false);
			securityRequestHeaders = securityRequest.getSecurityRequestHeaderParams();

		} catch (SecurityHandlerException | ValidationException | JsonProcessingException
				| NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

//		log.info("request headers: " + httpHeaders);

//		HttpEntity<String> httpEntity = new HttpEntity<>(httpHeaders);

//		ResponseEntity<?> responseEntity = null;
		try {
			
			URL url=new URL(strUrl);
			urlConnection=(HttpURLConnection) url.openConnection();
			urlConnection.setRequestMethod(httpMethod);
			urlConnection.setRequestProperty("Accept", "application/json");
						
			for (Map.Entry<String, String> entry : securityRequestHeaders.entrySet()) {
				String key=entry.getKey();
				String value=entry.getValue();
				System.out.println("Copying header "+key+" with a value of "+value);
				urlConnection.setRequestProperty(key, value);
			}

			
			if ("PUT".equals(httpMethod)) {	// Might need to be extended to also include POST and such
				urlConnection.setDoOutput(true);
				urlConnection.setRequestProperty("Content-Type", "application/json");
		        OutputStreamWriter osw = new OutputStreamWriter(urlConnection.getOutputStream());
		        osw.write(payload);
		        osw.flush();
		        osw.close();

			} else {
				urlConnection.connect();
			}
			
			int error=urlConnection.getResponseCode();
			InputStream is;
			ByteArrayOutputStream bos=new ByteArrayOutputStream();
			if (error==200) {
				is=urlConnection.getInputStream();
				int n;
				while((n = is.read()) > -1) {
				   bos.write(n);   // Don't allow any extra bytes to creep in, final write
				}

				String response=bos.toString("UTF-8");
				System.out.println(response);
				
				
	            ObjectMapper mapper = new ObjectMapper();
	            Object decodedMsg = mapper.readValue(response, expectedType);
	            
	            System.out.println(decodedMsg);
	            
	            return decodedMsg;

			} else {
				System.out.println("Problems accessing "+strUrl);
				System.out.println("Response code was "+error);
				is=urlConnection.getErrorStream();
				if (is==null) {
					System.out.println("No error message in response");
				} else {
					int n;
					while((n = is.read()) > -1) {
					   bos.write(n);   // Don't allow any extra bytes to creep in, final write
					}
	
					String response=bos.toString("UTF-8");
					System.out.println(response);
				}
			}
//			responseEntity = restTemplate.exchange(url, httpMethod, httpEntity, Object.class);
		} catch (IOException e) {
			e.printStackTrace();
//			return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
			return null;
		}

//		log.info("response = " + responseEntity);
//		log.info("headers = " + responseEntity.getHeaders());
//		log.info("body = " + responseEntity.getBody());

//		String serviceResponse = responseEntity.getHeaders().get(SecurityConstants.SECURITY_RESPONSE_HEADER).get(0);

//		if (serviceResponse == null)
//			return new ResponseEntity<>("The receiver was not authenticated", new HttpHeaders(),
//					HttpStatus.INTERNAL_SERVER_ERROR);

		boolean isServiceResponseVerified;
		try {
//			isServiceResponseVerified = MutualAuthenticationHelper.isServiceResponseVerified(serviceResponse,
//					securityHandler.getComponentCertificate(componentId, targetPlatformId));
//		} catch (CertificateException | NoSuchAlgorithmException | SecurityHandlerException e) {
		} catch (Exception e) {
//			log.warn("Exception during verifying service response", e);
//			return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
			return null;
		}

//		if (isServiceResponseVerified) {
//			return new ResponseEntity<>(responseEntity.getBody(), new HttpHeaders(), responseEntity.getStatusCode());
//		} else {
//			return new ResponseEntity<>("The service response is not verified", new HttpHeaders(),
//					HttpStatus.INTERNAL_SERVER_ERROR);
//		}
		
		return null;
	}

}
