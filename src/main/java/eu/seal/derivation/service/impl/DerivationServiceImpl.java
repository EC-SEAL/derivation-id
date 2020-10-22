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
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;

import eu.seal.derivation.model.pojo.AttributeType;
import eu.seal.derivation.model.pojo.DataSet;
import eu.seal.derivation.model.pojo.LinkRequest;

public class DerivationServiceImpl {
	
	private final static Logger LOG = LoggerFactory.getLogger(DerivationServiceImpl.class);

	
	//// Constants ///
	
	private int expirationWindow = System.getenv("EXPIRATION_WINDOW") == null ? Integer.parseInt("7"):Integer.parseInt(System.getenv("EXPIRATION_WINDOW"));
	
	// Seal attributes
	private static final String uuidFriendlyName = System.getenv("UUID_FRIENDLY_NAME")== null ? "sealUUID" : System.getenv("UUID_FRIENDLY_NAME");
	private static final String uuidAttrName = System.getenv("UUID_ATTR_NAME")== null ? "http://project-seal.eu/2020/id/sealUUID" : System.getenv("UUID_ATTR_NAME");	
	private final String attributeEncoding = System.getenv("ATTRIBUTE_ENCODING")== null ? "plain" : System.getenv("ATTRIBUTE_ENCODING");
	private final boolean mandatory = System.getenv("MANDATORY")== null ? true : (System.getenv("MANDATORY").toLowerCase().contains("true"))? true : false;
	
	private final String senderId = System.getenv("SENDER_ID") == null ? "uuid_ms001": System.getenv("SENDER_ID");
	
	//LinkRequestAttribbutes
	private final String issuerId = System.getenv("ISSUER_ID") == null ? "https://vm.project-seal.eu/" : System.getenv("ISSUER_ID");
	private final String subjectId = System.getenv("SUBJECT_ID") == null ? "sealUUID" : System.getenv("SUBJECT_ID");
	private final String derivedDatasetType = System.getenv("DERIVED_DATASET_TYPE") == null ? "derivedID" : System.getenv("DERIVED_DATASET_TYPE");
	private final String issuer = System.getenv("ISSUER") == null ? "SEAL Automated Linker" : System.getenv("ISSUER");
	private final String linkRequestType = System.getenv("LINK_REQUEST_TYPE") == null ? "linkedID" :System.getenv("LINK_REQUEST_TYPE");
	private final List<String> derivedIdcategories =System.getenv("DERIVED_ID_CATEGORIES") == null ?  Arrays.asList("UUID4") : Arrays.asList(System.getenv("DERIVED_ID_CATEGORIES"));
	private final String loa = System.getenv("LOA") == null ? "4" : System.getenv("LOA");
	private final String language = System.getenv("UUID_FRIENDLY_NAME")== null ? "null" : System.getenv("UUID_FRIENDLY_NAME");
	
	//// Variable ///
	
	
	private SessionManagerClientImpl sessionManagerClient;
	

	/**
	 * Constructor, creates a new derivation service 
	 * @param expirationWindow Expiration time in DAY_OF_YEAR that the linkService is expected to be received.
	 */
	@Autowired
	public DerivationServiceImpl(SessionManagerClientImpl sessionManagerClient) {
		this.sessionManagerClient = sessionManagerClient;
	}
	
	public void generate(String sessionId) {
		try {
		
			// Get the current dataStore. UNNECESSARY
			
			// Generate UUID4:
			// createNewDataSet()
			DataSet derivedDataSet = createNewDataSet();
			
			// Add the sealUUID dataSet to the dataStore		
			String objectId = getUniqueIdForDerivation(derivedDataSet); //TODO : ****TOASK
			sessionManagerClient.updateDatastore(sessionId, objectId, derivedDataSet, "dataSet");
			
			Object objAuthenticatedSubject = null;
			DataSet authenticatedSubject = null;
			objAuthenticatedSubject = sessionManagerClient.readVariable(sessionId, "authenticatedSubject");
			authenticatedSubject = (new ObjectMapper()).readValue(objAuthenticatedSubject.toString(),DataSet.class);
			
			// Create a new linked identity dataSet request with the sealUUID dataSet
			// Linked to the current authenticatedSubject
			
			// Add the linked Id dataSet to the dataStore
			LinkRequest linkedRequest = getLinkedRequest (derivedDataSet, authenticatedSubject); // Just linked
			sessionManagerClient.updateDatastore(sessionId, linkedRequest.getId(), linkedRequest, "linkRequest");
			
			// Update the authenticatedSubject with the dataSet just generated
			sessionManagerClient.updateSessionVariables(sessionId, sessionId, "authenticatedSubject", derivedDataSet);
					
		} catch (Exception e){
			e.printStackTrace();
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
		
//		try {
//			resultLinkRequest.setId("urn:mace:project-seal.eu:link:" + 
//					URLEncoder.encode("SEAL id-boot", StandardCharsets.UTF_8.toString()) + ":" + // TO ASK
//					//"LLoA" + ":" +
//					URLEncoder.encode(datasetA.getSubjectId(), StandardCharsets.UTF_8.toString()) + ":" + 
//					URLEncoder.encode(datasetA.getIssuerId(), StandardCharsets.UTF_8.toString())  + ":" +  
//					URLEncoder.encode(datasetB.getSubjectId(), StandardCharsets.UTF_8.toString()) + ":" + 
//					URLEncoder.encode(datasetB.getIssuerId(), StandardCharsets.UTF_8.toString()));
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		
		resultLinkRequest.setId("LINK_" + UUID.randomUUID().toString());
		resultLinkRequest.setIssuer(issuer);
		resultLinkRequest.setLloa(loa);
		resultLinkRequest.setIssued(new Date().toString());
		resultLinkRequest.setType(linkRequestType);
		
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
		derivedDataSet.setType(derivedDatasetType);
		derivedDataSet.setCategories(derivedIdcategories);
		derivedDataSet.setIssuerId(issuerId);
		derivedDataSet.setSubjectId(subjectId);
		derivedDataSet.setLoa(loa);
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
		sealUUIDAttributeType.setName(uuidAttrName);
		sealUUIDAttributeType.setFriendlyName(uuidFriendlyName);
		sealUUIDAttributeType.setEncoding(attributeEncoding);
		sealUUIDAttributeType.setLanguage(language);
		sealUUIDAttributeType.setIsMandatory(mandatory);
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
			String moduleId = senderId;
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
	
	

	
	
}
