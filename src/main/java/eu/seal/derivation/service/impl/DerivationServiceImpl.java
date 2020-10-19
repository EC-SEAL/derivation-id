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

import com.fasterxml.jackson.databind.ObjectMapper;

import eu.seal.derivation.model.pojo.AttributeType;
import eu.seal.derivation.model.pojo.DataSet;
import eu.seal.derivation.model.pojo.LinkRequest;


public class DerivationServiceImpl {
	
	private SessionManagerClientImpl sessionManagerClient;
	
	
	//// Constants ///    // ****TOASK: environment variables???
	
	// Seal attributes
	private static final String uuidFriendlyName = "sealUUID";
	private static final String uuidAttrName = "https://github.com/EC-SEAL/derivationID/wiki/identifiers/sealUUID\",";	// ****TOASK: this does not exist!!
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
		
		// ****TOASK: how to set the expirationWindow??
		this.expirationWindow = expirationWindow;
	}
	
	public void generate(String sessionId) {
		try {
		
			// Get the current dataStore. NOT NECESSARY
			
			// Generate UUID4:
			// createNewDataSet()
			DataSet derivedDataSet = createNewDataSet();
			
			// Add the sealUUID dataSet to the dataStore		
			String objectId = getUniqueIdForDerivation(); //TODO
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
			
			// Redirect to ClientCallbackAddr
			// ****TOASK: to confirm
		
		} catch (Exception e){
			
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
		
		
		//TODO: sort dsA and dsB
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
	
        derivedDataSet.setId(UUID.randomUUID().toString());   // TODO: random UUIDv4 generation module!!??
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
		sealUUIDAttributeType.setEncoding("plain");
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
	
	private String getUniqueIdForDerivation () {
		return "***this is a derived dataSet id";
	}
	
	
}
