
package eu.seal.derivation.controllers;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import eu.seal.derivation.model.pojo.SessionMngrResponse;
import eu.seal.derivation.service.HttpSignatureService;
import eu.seal.derivation.service.KeyStoreService;
import eu.seal.derivation.service.NetworkService;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;	
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
		this.sessionManagerURL = System.getenv("SESSION_MANAGER_URL");
		this.sessionManagerClient = new SessionManagerClientImpl(keyServ, sessionManagerURL);
		Key signingKey = this.keyServ.getSigningKey();
		String fingerPrint = this.keyServ.getFingerPrint();
		HttpSignatureService httpSigServ = new HttpSignatureServiceImpl(fingerPrint, signingKey);
		this.netServ = new NetworkServiceImpl(httpSigServ);
		
		this.derivationService = new DerivationServiceImpl( this.sessionManagerClient);
		
	}

	
	@RequestMapping(value = {"/idboot/generate"}, method = {RequestMethod.POST, RequestMethod.GET})
	public String generate(@RequestParam(value = "msToken", required = true) String msToken, RedirectAttributes redirectAttrs, HttpServletRequest request, 
			HttpServletResponse response,
			Model model
			) throws KeyStoreException, JsonParseException, JsonMappingException, NoSuchAlgorithmException, IOException {
		
		String callbackAddress = null;
		
		try {
		
			SessionMngrResponse resp = sessionManagerClient.validateToken(null, msToken);
			
			if (resp.getCode().toString().equals("OK") && StringUtils.isEmpty(resp.getError())) {
				
				String sessionId = resp.getSessionData().getSessionId();
				derivationService.generate(sessionId);
	
				// Get the callbackAddress
				callbackAddress = (String) sessionManagerClient.readVariable(sessionId, "ClientCallbackAddr");
			
				LOG.info ("UrlToRedirect: " + callbackAddress);
				if (callbackAddress == null)
				{
					model.addAttribute("ErrorMessage","ClientCallbackAddr not found");
					return "fatalError";
				}
					
//				String tokenToX = "";
//				tokenToX = sessionManagerClient.generateToken(sessionId, msName); 
//				model.addAttribute("msToken", "tokenToX");
				model.addAttribute("UrlToRedirect", callbackAddress);
				
				LOG.info ("redirecting...");
				return "redirectformGET";
				
			} else {
				LOG.error(resp.getError());
				redirectAttrs.addFlashAttribute("errorMsg", "Error validating token! " + resp.getError()); // TODO: what is this?
				
				String errorMsg= resp.getError()+"\n";
				model.addAttribute("ErrorMessage",errorMsg);
				return "derivationError"; 
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			
			String errorMsg= e.getMessage()+"\n";
			model.addAttribute("ErrorMessage",errorMsg);
			
			return "derivationError"; 
		}

		
	}
	
}

