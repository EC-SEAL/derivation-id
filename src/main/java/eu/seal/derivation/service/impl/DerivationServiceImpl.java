package eu.seal.derivation.service.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;

import com.fasterxml.jackson.databind.ObjectMapper;

import eu.seal.derivation.model.pojo.AttributeType;
import eu.seal.derivation.model.pojo.DataSet;
import eu.seal.derivation.model.pojo.LinkRequest;


public class DerivationServiceImpl {
	
	private final static Logger LOG = LoggerFactory.getLogger(DerivationServiceImpl.class);

	
	private SessionManagerClientImpl sessionManagerClient;
	
	
	//// Constants ///
	
	// Seal attributes
	private static final String uuidFriendlyName = "sealUUID";
	private static final String uuidAttrName = "http://project-seal.eu/2020/id/sealUUID";	
	private final String attributeEncoding = "plain";
	private final boolean mandatory = true;
	
	//LinkRequestAttribbutes
	private final String issuerId = "https://vm.project-seal.eu/";
	private final String subjectId = "sealUUID";
	private final String derivedDatasetType = "derivedID";
	private final String issuer = "SEAL Automated Linker";
	private final String linkRequestType = "linkedID";
	private final List<String> derivedIdcategories = Arrays.asList("UUID4");
	private final String loa = "4";
	private final String language = "null";
	
	//// Variable ///
	
	private int expirationWindow;
	

	/**
	 * Constructor, creates a new derivation service 
	 * @param expirationWindow Expiration time in DAY_OF_YEAR that the linkService is expected to be received.
	 */
	public DerivationServiceImpl(int expirationWindow) {
		
		// By default, a week
		this.expirationWindow = expirationWindow;
	}
	
	public void generate(String sessionId) {
		try {
		
			// Get the current dataStore. NOT NECESSARY
			
			// Generate UUID4:
			// createNewDataSet()
			DataSet derivedDataSet = createNewDataSet();
			
			// Add the sealUUID dataSet to the dataStore		
			String objectId = getUniqueIdForDerivation(derivedDataSet); //TODO : ****TOASK
			sessionManagerClient.updateDatastore(sessionId, objectId, derivedDataSet);
			
			Object objAuthenticatedSubject = null;
			DataSet authenticatedSubject = null;
			objAuthenticatedSubject = sessionManagerClient.readVariable(sessionId, "authenticatedSubject");
			authenticatedSubject = (new ObjectMapper()).readValue(objAuthenticatedSubject.toString(),DataSet.class);
			
			// Create a new linked identity dataSet request with the sealUUID dataSet
			// Linked to the current authenticatedSubject
			
			// Add the linked Id dataSet to the dataStore
			LinkRequest linkedRequest = getLinkedRequest (derivedDataSet, authenticatedSubject); // Just linked
			sessionManagerClient.updateDatastore(sessionId, linkedRequest.getId(), linkedRequest);
			
			// Update the authenticatedSubject with the dataSet just generated
			sessionManagerClient.updateSessionVariables(sessionId, sessionId, "authenticatedSubject", derivedDataSet);
					
		} catch (Exception e){
			// TODO
			// this.returnError
			
		}
		
	}
	
	/**
	 * Returns a linkRequest given an existing dataSet
	 * @param dataSetA first dataSet to be included in the LinkRequest
	 * @param dataSetA second dataSet to be included in the LinkRequest
	 * @return LinkRequest Object linking the existing dataSet to the recently created Dataset 
	 */
	public LinkRequest getLinkedRequest(DataSet dsA, DataSet dsB) {
		LinkRequest resultLinkRequest = new LinkRequest();
		
		
		// Sort dsA and dsB
		DataSet datasetA = new DataSet();
		DataSet datasetB = new DataSet();
		if (dsA.getSubjectId().compareTo(dsB.getSubjectId()) < 0) {
			datasetA = dsA;
			datasetB = dsB;
		}
		else if (dsA.getSubjectId().compareTo(dsB.getSubjectId()) > 0) {
			datasetA = dsB;
			datasetB = dsA;
		}
		else //equals
			if (dsA.getIssuerId().compareTo(dsB.getIssuerId()) <= 0) {
				datasetA = dsA;
				datasetB = dsB;
			}
			else {
				datasetA = dsB;
				datasetB = dsA;
			}
		resultLinkRequest.setDatasetA(datasetA);
		resultLinkRequest.setDatasetB(datasetB);
		
		try {
			resultLinkRequest.setId("urn:mace:project-seal.eu:link:" + 
					URLEncoder.encode("SEAL id-boot", StandardCharsets.UTF_8.toString()) + ":" + // TO ASK
					//"LLoA" + ":" +
					URLEncoder.encode(datasetA.getSubjectId(), StandardCharsets.UTF_8.toString()) + ":" + 
					URLEncoder.encode(datasetA.getIssuerId(), StandardCharsets.UTF_8.toString())  + ":" +  
					URLEncoder.encode(datasetB.getSubjectId(), StandardCharsets.UTF_8.toString()) + ":" + 
					URLEncoder.encode(datasetB.getIssuerId(), StandardCharsets.UTF_8.toString()));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		resultLinkRequest.setIssuer(System.getenv("ISSUER") == null ? issuer : System.getenv("ISSUER"));
		resultLinkRequest.setLloa(System.getenv("LOA") == null ? loa : System.getenv("LOA"));
		resultLinkRequest.setIssued(new Date().toString());
		resultLinkRequest.setType(System.getenv("LINK_REQUEST_TYPE") == null ? linkRequestType :System.getenv("LINK_REQUEST_TYPE"));
		
		return resultLinkRequest;
	}
	
	
	
	/**
	 * Generates a new derived DataSet 
	 * @return Seal DataSet, formed by 
	 */
	public DataSet createNewDataSet() {
		DataSet derivedDataSet = new DataSet();
        Date issued = new Date();  // Current date 
        Date expiration = getExpirationDate(issued, expirationWindow);
        List<AttributeType> attributes = new ArrayList<>();
	
        derivedDataSet.setId("DRV_" + UUID.randomUUID().toString());   // TODO: random UUIDv4 generation module!!??
		derivedDataSet.setType(System.getenv("DERIVED_DATASET_TYPE") == null ? derivedDatasetType : System.getenv("DERIVED_DATASET_TYPE"));
		derivedDataSet.setCategories(System.getenv("DERIVED_ID_CATEGORIES") == null ? derivedIdcategories : Arrays.asList(System.getenv("DERIVED_ID_CATEGORIES")));
		derivedDataSet.setIssuerId(System.getenv("ISSUER_ID") == null ? issuerId : System.getenv("ISSUER_ID"));
		derivedDataSet.setSubjectId(System.getenv("SUBJECT_ID") == null ? subjectId : System.getenv("SUBJECT_ID"));
		derivedDataSet.setLoa(System.getenv("LOA")== null ? loa : System.getenv("LOA"));
		derivedDataSet.setIssued(issued.toString());
		derivedDataSet.setExpiration(expiration.toString());
		attributes.add(sealUUIDAttributeType());
		derivedDataSet.setAttributes(attributes);
		return derivedDataSet;
		
	}
	
	/**
	 * Creates an attribute Type https://github.com/EC-SEAL/derivationID/wiki/identifiers/sealUUID"
	 * @return sealUUIDAttributeType
	 */
	
	public AttributeType sealUUIDAttributeType() {
		AttributeType sealUUIDAttributeType =  new AttributeType();
		String[] values = new String[1];
		sealUUIDAttributeType.setName(System.getenv("UUID_ATTR_NAME")== null ? uuidAttrName : System.getenv("UUID_ATTR_NAME"));
		sealUUIDAttributeType.setFriendlyName(System.getenv("UUID_FRIENDLY_NAME")== null ? uuidFriendlyName : System.getenv("UUID_FRIENDLY_NAME"));
		sealUUIDAttributeType.setEncoding(System.getenv("ATTRIBUTE_ENCODING")== null ? attributeEncoding : System.getenv("ATTRIBUTE_ENCODING"));
		sealUUIDAttributeType.setLanguage(System.getenv("UUID_FRIENDLY_NAME")== null ? language : System.getenv("UUID_FRIENDLY_NAME"));
		sealUUIDAttributeType.setIsMandatory(System.getenv("MANDATORY")== null ? mandatory : (System.getenv("MANDATORY").toLowerCase().contains("true"))? true : false);
		values[0]=UUID.randomUUID().toString();
		sealUUIDAttributeType.setValues(values);
		return sealUUIDAttributeType;
		
		
	}
	
	private Date getExpirationDate(Date issuanceDate, int validityWindow) {
		Calendar c = Calendar.getInstance();
        c.setTime(issuanceDate);

        // manipulate date
        c.add(Calendar.DAY_OF_YEAR, validityWindow);
        return c.getTime();
	}
	
	private String getUniqueIdForDerivation (DataSet drvDataSet) {
		String uniqueId= "urn:mace:project-seal.eu:id:dataset:";
		try {
			String moduleId = System.getenv("SENDER_ID") == null ? "uuid_ms001": System.getenv("SENDER_ID");
			uniqueId = uniqueId + 
					URLEncoder.encode(moduleId, StandardCharsets.UTF_8.toString());
		
			String auxIssuer = drvDataSet.getIssuerId();
			String auxSubject = drvDataSet.getSubjectId();
			
			uniqueId = uniqueId + ":" + 
					URLEncoder.encode(auxIssuer, StandardCharsets.UTF_8.toString()) + ":" + 
					URLEncoder.encode(auxSubject, StandardCharsets.UTF_8.toString());
		
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		return uniqueId;
	}
	
	
	public String returnSuccess (String sessionId, Model model) throws Exception 
	{
		String callbackAddress = null;
		try
		{
			// Get the callbackAddress
			callbackAddress = (String) sessionManagerClient.readVariable(sessionId, "ClientCallbackAddr");
		
			LOG.info ("UrlToRedirect: " + callbackAddress);
			if (callbackAddress == null)
			{
				model.addAttribute("ErrorMessage","ClientCallbackAddr not found");
				return "fatalError";
			}
				
//			String tokenToX = "";
//			tokenToX = sessionManagerClient.generateToken(sessionId, msName); 
		
//			model.addAttribute("msToken", tokenToX);
			model.addAttribute("UrlToRedirect", callbackAddress);
			
			return "redirectform";
		
		}
		catch (Exception ex)
		{
			String errorMsg= ex.getMessage()+"\n";
			LOG.info ("Returning error: "+errorMsg);
			
			model.addAttribute("ErrorMessage",errorMsg);
			if (callbackAddress != null) 
	        	return "derivationError"; 
	        else
	        	return "fatalError"; // Unknown ClientCallbackAddr ...
		}
	}
	
	
}
