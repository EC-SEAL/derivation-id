package eu.seal.derivation.service.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.httpclient.NameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import eu.seal.derivation.enums.ResponseCode;
import eu.seal.derivation.controllers.Generate;
import eu.seal.derivation.model.pojo.SessionMngrResponse;
import eu.seal.derivation.service.HttpSignatureService;
import eu.seal.derivation.service.KeyStoreService;
import eu.seal.derivation.service.NetworkService;

public class SessionManagerClientImpl {
	
	private final static Logger LOG = LoggerFactory.getLogger(Generate.class);
	private final NetworkService netServ;
	private final KeyStoreService keyServ;
	private final String sessionMngrURL;
	ObjectMapper mapper = new ObjectMapper();
	
	public SessionManagerClientImpl(KeyStoreService keyServ, String sessionMngrURL) throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, InvalidKeySpecException, IOException {
		this.keyServ = keyServ;
		this.sessionMngrURL = sessionMngrURL;
		Key signingKey = this.keyServ.getSigningKey();
		String fingerPrint = this.keyServ.getFingerPrint();
		HttpSignatureService httpSigServ = new HttpSignatureServiceImpl(fingerPrint, signingKey);
		this.netServ = new NetworkServiceImpl(httpSigServ);
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
	    }
	    
	    
	    //return sessionVbles.get(variableName);
	    return sessionVble;
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
	    }
	    
	    
	    //return sessionVbles.get(variableName);
	    return sessionVble;
	}

}
