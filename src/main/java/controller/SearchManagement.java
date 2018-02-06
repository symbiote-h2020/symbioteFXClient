package controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;

import eu.h2020.symbiote.core.ci.QueryResourceResult;
import eu.h2020.symbiote.core.ci.QueryResponse;
import eu.h2020.symbiote.core.internal.CoreQueryRequest;
import eu.h2020.symbiote.core.internal.cram.ResourceUrlsResponse;
import eu.h2020.symbiote.model.cim.Observation;
import eu.h2020.symbiote.security.commons.SecurityConstants;
import utils.Utils;

public class SearchManagement {

	public static List<QueryResourceResult>resources=null;
	
	public static void doParametricSearch(CoreQueryRequest cr) {
		
	
//		String platformId="AIT-openUwedat"; // TODO: Get this from the core
//        String platformName=null;
//        String owner=null;
//        String name=null;
//        String id=null;
//        String description=null;
//        String location_name=null;
//        Double location_lat=null;
//        Double location_long=null;
//        Integer max_distance=null;
//        String[] observed_property=null;
//        String resource_type=null;
//        Boolean should_rank=null;
//		
//        CoreQueryRequest queryRequest = new CoreQueryRequest();
//        queryRequest.setPlatform_id(platformId);
//        queryRequest.setPlatform_name(platformName);
//        queryRequest.setOwner(owner);
//        queryRequest.setName(name);
//        queryRequest.setId(id);
//        queryRequest.setDescription(description);
//        queryRequest.setLocation_name(location_name);
//        queryRequest.setLocation_lat(location_lat);
//        queryRequest.setLocation_long(location_long);
//        queryRequest.setMax_distance(max_distance);
//        queryRequest.setResource_type(resource_type);
//        queryRequest.setShould_rank(should_rank);

//        if (cr.getObserved_property() != null) {
//            cr.setObserved_property(Arrays.asList(observed_property));
//        }

        String queryUrl = cr.buildQuery(ConnectionManager.symbIoTeCoreUrl);
        System.out.println("Query ULR is "+queryUrl);
//        log.info("queryUrl = " + queryUrl);

        QueryResponse qr=(QueryResponse) Utils.sendRequestAndVerifyResponse("GET", queryUrl, ConnectionManager.homePlatformId,
                SecurityConstants.CORE_AAM_INSTANCE_ID, "search", new TypeReference<QueryResponse>(){});
        
        resources=qr.getBody();


		
		// More processing
	}
	

	public static String getResourceURL(String resourceId) {
	    String cramRequestUrl = ConnectionManager.symbIoTeCoreUrl + "/resourceUrls?id=" + resourceId;
	    
	    ResourceUrlsResponse rur=(ResourceUrlsResponse) Utils.sendRequestAndVerifyResponse("GET", cramRequestUrl, ConnectionManager.homePlatformId,
	            SecurityConstants.CORE_AAM_INSTANCE_ID, "cram", new TypeReference<ResourceUrlsResponse>(){});
	    
	    HashMap<String, String> urlList=rur.getBody();
	    
	    String url=urlList.get(resourceId);
	    
	    return url;
	}

	
	public static Observation getObservation(String url) {
		
//		TypeReference<List<Observation>> resultClass=new TypeReference<List<Observation>>(){};
		TypeReference<Observation> resultClass=new TypeReference<Observation>(){};
		
		Observation observation=(Observation)Utils.sendRequestAndVerifyResponse("GET", url, ConnectionManager.homePlatformId, ConnectionManager.homePlatformId, "rap", resultClass);
		return observation;
	}
}
