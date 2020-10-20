
package eu.seal.derivation.controllers;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import eu.seal.derivation.model.pojo.AttributeSet;
import eu.seal.derivation.model.pojo.DataSet;
import eu.seal.derivation.model.pojo.DataStore;
import eu.seal.derivation.model.pojo.LinkRequest;
import eu.seal.derivation.model.pojo.SessionMngrResponse;
import eu.seal.derivation.model.pojo.UpdateDataRequest;
import eu.seal.derivation.service.HttpSignatureService;
import eu.seal.derivation.service.KeyStoreService;
import eu.seal.derivation.service.NetworkService;
import eu.seal.derivation.service.impl.AuthSetToDataSet;
import eu.seal.derivation.service.impl.DerivationServiceImpl;
import eu.seal.derivation.service.impl.HttpSignatureServiceImpl;
import eu.seal.derivation.service.impl.NetworkServiceImpl;
import eu.seal.derivation.service.impl.SessionManagerClientImpl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.slf4j.Logger;	
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controllers managing Seal Authentication Source, used in the log in and SSO Callback
 *  */

@Controller
public class Generate {

	private final static Logger LOG = LoggerFactory.getLogger(Generate.class);

	private final NetworkService netServ;
	private final KeyStoreService keyServ;
	private final DerivationServiceImpl derivationService;
	private final SessionManagerClientImpl sessionManagerClient;
	private final String sessionManagerURL; 
	
	@Autowired
	public Generate(KeyStoreService keyServ) throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException, UnsupportedEncodingException, InvalidKeySpecException, IOException {
		this.keyServ = keyServ;
		this.derivationService = new DerivationServiceImpl(System.getenv("EXPIRATION_WINDOW") == null ? Integer.parseInt("7"):Integer.parseInt(System.getenv("EXPIRATION_WINDOW")));
		this.sessionManagerURL = System.getenv("SESSION_MANAGER_URL");
		this.sessionManagerClient = new SessionManagerClientImpl(keyServ, sessionManagerURL);
		Key signingKey = this.keyServ.getSigningKey();
		String fingerPrint = this.keyServ.getFingerPrint();
		HttpSignatureService httpSigServ = new HttpSignatureServiceImpl(fingerPrint, signingKey);
		this.netServ = new NetworkServiceImpl(httpSigServ);
	}

	
	@RequestMapping(value = {"/idboot/generate"}, method = {RequestMethod.POST, RequestMethod.GET})
	public String generate(@RequestParam(value = "msToken", required = true) String msToken, RedirectAttributes redirectAttrs, HttpServletRequest request) throws KeyStoreException, JsonParseException, JsonMappingException, NoSuchAlgorithmException, IOException {

//		List<NameValuePair> requestParams = new ArrayList<NameValuePair>();
//		requestParams.add(new NameValuePair("token", msToken));
//		ObjectMapper mapper = new ObjectMapper();
//		String rspValidate = netServ.sendGet(sessionManagerURL, "/sm/validateToken", requestParams, 1);
//		SessionMngrResponse resp = mapper.readValue(rspValidate, SessionMngrResponse.class);
		
		SessionMngrResponse resp = sessionManagerClient.validateToken(null, msToken);
		
		if (resp.getCode().toString().equals("OK") && StringUtils.isEmpty(resp.getError())) {
			
			String sessionId = resp.getSessionData().getSessionId();
			derivationService.generate(sessionId);

			/*
			SessionMngrResponse respGet = sessionManagerClient.getSingleParam("sessionId", sealSessionId);
			
			String dataStoreString = (String) respGet.getSessionData().getSessionVariables().get("dataStore");
			String authenticationSetString = (String) respGet.getSessionData().getSessionVariables().get("authenticationSet");	
			
			List <DataSet> dsArrayList = new ArrayList();
			
			DataStore datastore = new DataStore();
			AttributeSet authenticationSet = new AttributeSet();
			
			DataSet associatedDataSet = AuthSetToDataSet.resolveAttributeSet(authenticationSet);
			LinkRequest result= derivationService.getLinkedRequest(associatedDataSet);
			
			UpdateDataRequest updateReqAuthSet = new UpdateDataRequest(sealSessionId, "LinkRequest", mapper.writeValueAsString(result));
			
			requestParams.clear();
			requestParams.add(new NameValuePair("sessionId", sealSessionId));
			*/
			
			
		} else {
			LOG.error(resp.getError());
			redirectAttrs.addFlashAttribute("errorMsg", "Error validating token! " + resp.getError());
		}

		return "Hello";
	}
}

