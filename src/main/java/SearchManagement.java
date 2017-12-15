import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import eu.h2020.symbiote.core.ci.QueryResourceResult;
import eu.h2020.symbiote.core.ci.QueryResponse;
import eu.h2020.symbiote.core.internal.CoreQueryRequest;
import eu.h2020.symbiote.enabler.messaging.model.rap.access.ResourceAccessMessage;
import eu.h2020.symbiote.security.commons.SecurityConstants;
import eu.h2020.symbiote.security.commons.Token;
import eu.h2020.symbiote.security.commons.credentials.AuthorizationCredentials;
import eu.h2020.symbiote.security.commons.credentials.HomeCredentials;
import eu.h2020.symbiote.security.commons.exceptions.custom.SecurityHandlerException;
import eu.h2020.symbiote.security.commons.exceptions.custom.ValidationException;
import eu.h2020.symbiote.security.communication.payloads.AAM;
import eu.h2020.symbiote.security.communication.payloads.SecurityRequest;
import eu.h2020.symbiote.security.helpers.MutualAuthenticationHelper;

public class SearchManagement {

	static List<QueryResourceResult>resources=null;
	
	public static void doParametricSearch() {
		
	
		String platformId="AIT-openUwedat";
        String platformName=null;
        String owner=null;
        String name=null;
        String id=null;
        String description=null;
        String location_name=null;
        Double location_lat=null;
        Double location_long=null;
        Integer max_distance=null;
        String[] observed_property=null;
        String resource_type=null;
        Boolean should_rank=null;
        String homePlatformId="AIT-openUwedat";
		
        CoreQueryRequest queryRequest = new CoreQueryRequest();
        queryRequest.setPlatform_id(platformId);
        queryRequest.setPlatform_name(platformName);
        queryRequest.setOwner(owner);
        queryRequest.setName(name);
        queryRequest.setId(id);
        queryRequest.setDescription(description);
        queryRequest.setLocation_name(location_name);
        queryRequest.setLocation_lat(location_lat);
        queryRequest.setLocation_long(location_long);
        queryRequest.setMax_distance(max_distance);
        queryRequest.setResource_type(resource_type);
        queryRequest.setShould_rank(should_rank);

        if (observed_property != null) {
            queryRequest.setObserved_property(Arrays.asList(observed_property));
        }

        String queryUrl = queryRequest.buildQuery(ConnectionManagement.symbIoTeCoreUrl);
//        log.info("queryUrl = " + queryUrl);

        resources=sendRequestAndVerifyResponse("GET", queryUrl, homePlatformId,
                SecurityConstants.CORE_AAM_INSTANCE_ID, "search");


		
		// More processing
	}
	
	
	
	private static List<QueryResourceResult> sendRequestAndVerifyResponse(String httpMethod, String strUrl, String homePlatformId,
			String targetPlatformId, String componentId) {

		Map<String, String> securityRequestHeaders=null;
//		HttpHeaders httpHeaders = new HttpHeaders();
//		httpHeaders.set("Accept", MediaType.APPLICATION_JSON_VALUE);
//		httpHeaders.setContentType(MediaType.APPLICATION_JSON);

		HttpURLConnection urlConnection=null;
		
		// Insert Security Request into the headers
		try {

			URL url=new URL(strUrl);
			urlConnection=(HttpURLConnection) url.openConnection();
			urlConnection.setRequestMethod(httpMethod);

			
			Set<AuthorizationCredentials> authorizationCredentialsSet = new HashSet<>();
			Map<String, AAM> availableAAMs = ConnectionManagement.securityHandler.getAvailableAAMs();

//			log.info("Getting certificate for " + availableAAMs.get(homePlatformId).getAamInstanceId());
			ConnectionManagement.securityHandler.getCertificate(availableAAMs.get(homePlatformId), "demo", "demo", ConnectionManagement.clientId);

//			log.info("Getting token from " + availableAAMs.get(homePlatformId).getAamInstanceId());
			Token homeToken = ConnectionManagement.securityHandler.login(availableAAMs.get(homePlatformId));

			HomeCredentials homeCredentials = ConnectionManagement.securityHandler.getAcquiredCredentials()
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
//			return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (Map.Entry<String, String> entry : securityRequestHeaders.entrySet()) {
			urlConnection.setRequestProperty(entry.getKey(), entry.getValue());
		}
//		log.info("request headers: " + httpHeaders);

//		HttpEntity<String> httpEntity = new HttpEntity<>(httpHeaders);

//		ResponseEntity<?> responseEntity = null;
		try {
			urlConnection.connect();
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
	            QueryResponse msg = mapper.readValue(response, QueryResponse.class);
	            
	            System.out.println(msg);
	            List<QueryResourceResult> resources=msg.getBody();
	            
	            return resources;

			} else {
				is=urlConnection.getErrorStream();
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
