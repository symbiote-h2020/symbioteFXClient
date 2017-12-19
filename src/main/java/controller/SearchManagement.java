package controller;
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
import utils.Utils;

public class SearchManagement {

	public static List<QueryResourceResult>resources=null;
	
	public static void doParametricSearch() {
		
	
		String platformId="AIT-openUwedat"; // TODO: Get this from the core
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

        resources=Utils.sendRequestAndVerifyResponse("GET", queryUrl, ConnectionManagement.homePlatformId,
                SecurityConstants.CORE_AAM_INSTANCE_ID, "search");


		
		// More processing
	}
	
	

}
