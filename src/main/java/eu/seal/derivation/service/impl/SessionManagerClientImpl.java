package eu.seal.derivation.service.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.Key;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.apache.commons.httpclient.NameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

import eu.seal.derivation.enums.ResponseCode;
import eu.seal.derivation.controllers.Generate;
import eu.seal.derivation.model.pojo.NewUpdateDataRequest;
import eu.seal.derivation.model.pojo.SessionMngrResponse;
import eu.seal.derivation.service.HttpSignatureService;
import eu.seal.derivation.service.KeyStoreService;
import eu.seal.derivation.service.NetworkService;
import eu.seal.derivation.model.pojo.UpdateDataRequest;

public class SessionManagerClientImpl {
	
	private final static Logger LOG = LoggerFactory.getLogger(Generate.class);
	private final NetworkService netServ;
	private final KeyStoreService keyServ;
	private final String sessionMngrURL;
	private final String sender;
	ObjectMapper mapper = new ObjectMapper();
	
	private final String URIUPDATENEWSESSION = "/sm/new/add";
	private final String URIUPDATESESSION = "/sm/updateSessionData";
	
	public SessionManagerClientImpl(KeyStoreService keyServ, String sessionMngrURL) throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, InvalidKeySpecException, IOException {
		this.keyServ = keyServ;
		this.sessionMngrURL = sessionMngrURL;
		Key signingKey = this.keyServ.getSigningKey();
		String fingerPrint = this.keyServ.getFingerPrint();
		HttpSignatureService httpSigServ = new HttpSignatureServiceImpl(fingerPrint, signingKey);
		this.netServ = new NetworkServiceImpl(httpSigServ);
		this.sender = System.getenv("SENDER_ID");
	}
	
	public SessionMngrResponse validateToken(String param, String msToken) throws NoSuchAlgorithmException, IOException {
		List<NameValuePair> requestParams = new ArrayList<NameValuePair>();
		requestParams.add(new NameValuePair("token", msToken));
		ObjectMapper mapper = new ObjectMapper();
		String rspValidate = netServ.sendGet(sessionMngrURL, "/sm/validateToken", requestParams, 1);
		SessionMngrResponse resp = mapper.readValue(rspValidate, SessionMngrResponse.class);
		return resp;
	}
	
	
	public SessionMngrResponse getSingleParam(String key, String value) {
		try {
			String newUUID = UUID.randomUUID().toString();
			List<NameValuePair> requestParamsGet = new ArrayList<NameValuePair>();
			requestParamsGet.add(new NameValuePair(key, value));
			String clearRespGet = netServ.sendGet(sessionMngrURL, "/sm/getSessionData", requestParamsGet, 1);
			SessionMngrResponse respGet;
			respGet = mapper.readValue(clearRespGet, SessionMngrResponse.class);
			return respGet;
		} catch (Exception e) {
			e.printStackTrace();
			return new SessionMngrResponse();
		}
	}
	
	public Object readDS(String sessionId) throws UnrecoverableKeyException, KeyStoreException, FileNotFoundException, NoSuchAlgorithmException, CertificateException, InvalidKeySpecException, IOException
	{
		String service = "/sm/new/get";
		//HashMap<String, Object> sessionVbles = new HashMap<String, Object>();
		Object sessionVble = new Object();
		
		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
	    urlParameters.add(new NameValuePair("sessionId",sessionId));
	    
	    SessionMngrResponse smResponse = null;
	    try {
	    	LOG.info("Sending new/get ...");
	    	//response = network.sendGet(hostURL, service, urlParameters);
	    	smResponse = netServ.sendGetSMResponse(sessionMngrURL, service, urlParameters, 1);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    //log.info("Response new/getSessionData:<"+smResponse.toString()+">");
	    if (smResponse.getCode()==ResponseCode.OK)
	    {
	    	//sessionVbles = (HashMap<String, Object>) smResponse.getSessionData().getSessionVariables();
	    	sessionVble = smResponse.getAdditionalData();
	    	
	    	LOG.info("sessionVble: "+ sessionVble.toString());
	    	return sessionVble;
	    }
	    else return null;
	}
	
	public Object getDataSet(String sessionId, String dataSetId) throws UnrecoverableKeyException, KeyStoreException, FileNotFoundException, NoSuchAlgorithmException, CertificateException, InvalidKeySpecException, IOException
	{
		String service = "/sm/new/get";
		//HashMap<String, Object> sessionVbles = new HashMap<String, Object>();
		Object sessionVble = new Object();
		
		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
	    urlParameters.add(new NameValuePair("sessionId",sessionId));
	    urlParameters.add(new NameValuePair("id",dataSetId));
	    
	    SessionMngrResponse smResponse = null;
	    try {
	    	LOG.info("Sending new/get DataSet ...");
	    	//response = network.sendGet(hostURL, service, urlParameters);
	    	smResponse = netServ.sendGetSMResponse(sessionMngrURL, service, urlParameters, 1);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    //log.info("Response new/getSessionData:<"+smResponse.toString()+">");
	    if (smResponse.getCode()==ResponseCode.OK)
	    {
	    	//sessionVbles = (HashMap<String, Object>) smResponse.getSessionData().getSessionVariables();
	    	sessionVble = smResponse.getAdditionalData();
	    	
	    	LOG.info("sessionVble: "+ sessionVble.toString());
	    	return sessionVble;
	    }
	    else return null;
	    
	}
	
	/**
	 * Updates the dataStore session Variable
	 */	
	
	public String updateDatastore(String sessionId, String objectId, Object updateObject, String type) throws IOException, NoSuchAlgorithmException {
        ObjectMapper mapper = new ObjectMapper();
        String stringifiedObject = mapper.writeValueAsString(updateObject);

            NewUpdateDataRequest newReq = new NewUpdateDataRequest();
            newReq.setId(objectId);
            newReq.setSessionId(sessionId);
            newReq.setType(type);  // dataSet or linkRequest
            newReq.setData(stringifiedObject);
            String result = netServ.sendNewPostBody(sessionMngrURL, URIUPDATENEWSESSION, newReq, "application/json", 1);
            
            LOG.info("Result" + result);          
            LOG.info("session " + sessionId + " updated NEW API Session succesfully  with objectID" + objectId + "  with user attributes " + stringifiedObject);

        return "ok";
    }
	
	public String updateSessionVariables(String sessionId, String objectId, String variableName, Object updateObject) throws IOException, NoSuchAlgorithmException {
        ObjectMapper mapper = new ObjectMapper();
        String stringifiedObject = mapper.writeValueAsString(updateObject);

        UpdateDataRequest updateReq = new UpdateDataRequest(sessionId, variableName, stringifiedObject);
        SessionMngrResponse resp = mapper.readValue(netServ.sendPostBody(sessionMngrURL, URIUPDATESESSION, updateReq, "application/json", 1), SessionMngrResponse.class);
        LOG.info("updateSessionData " + resp.getCode().toString());
        if (!resp.getCode().toString().equals("OK")) {
            LOG.error("ERROR: " + resp.getError());
            return "error";
        }
        
        return "ok";
    }
	
	public Object readVariable(String sessionId, String variableName) throws UnrecoverableKeyException, KeyStoreException, FileNotFoundException, NoSuchAlgorithmException, CertificateException, InvalidKeySpecException, IOException
	{
		String service = "/sm/getSessionData";
		HashMap<String, Object> sessionVbles = new HashMap<String, Object>();

		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
	    urlParameters.add(new NameValuePair("sessionId",sessionId));
	    urlParameters.add(new NameValuePair("variableName",variableName));
	     
	    SessionMngrResponse smResponse = null;
	    try {
	    	smResponse = netServ.sendGetSMResponse(sessionMngrURL, service, urlParameters, 1);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    System.out.println("Response getSessionData:<"+smResponse.toString()+">");
	    if (smResponse.getCode()== ResponseCode.OK)
	    {
	    	sessionVbles = (HashMap<String, Object>) smResponse.getSessionData().getSessionVariables();
	    	return sessionVbles.get(variableName);
	    }
	    else return null;
	    
	}
	
	public String generateToken(String sessionId, String receiver)
	//(String sessionId, String sender, String receiver)
			throws UnrecoverableKeyException, KeyStoreException, FileNotFoundException, NoSuchAlgorithmException,
			CertificateException, InvalidKeySpecException, IOException
	{
		String service = "/sm/generateToken";
		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
        urlParameters.add(new NameValuePair("sessionId",sessionId));
        urlParameters.add(new NameValuePair("sender", sender)); 
        urlParameters.add(new NameValuePair("receiver", receiver));
        urlParameters.add(new NameValuePair("data", "extraData"));
        
        SessionMngrResponse smResponse = netServ.sendGetSMResponse(sessionMngrURL, service, urlParameters,1);
        
        String additionalData="";
        //System.out.println("SMresponse(generateToken):" +smResponse.toString());
        if ( smResponse.getCode()== ResponseCode.NEW)
        {
	        System.out.println( "addDAta:"+ smResponse.getAdditionalData());
	        additionalData = smResponse.getAdditionalData();
	    }
        return additionalData; // Returns a token
	}

}
