package eu.seal.derivation.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import eu.seal.derivation.model.pojo.AttributeSet;
import eu.seal.derivation.model.pojo.AttributeType;
import eu.seal.derivation.model.pojo.DataSet;
import eu.seal.derivation.model.pojo.LinkRequest;

public class DerivationServiceImpl {
	
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
	
	public void generate() {
		
		// Get the current dataStore
		
		// Generate UUID4
		
		// Add the sealUUID dataSet to the dataStore	
		
		// Create a new linked identity dataSet request with the sealUUID dataSet
		// ****TOASK: linkRequest or just a linked identity? Linked identity, I think
		// ****TOASK: link to the current authenticatedSubject? Yes, I think
		
		// Add the linked Id dataSet to the dataStore
		
		// Update the authenticatedSubject with the dataSet just generated
		
		// Redirect to ClientCallbackAddr
		// ****TOASK: to confirm
		
	}
	
	/**
	 * Returns a linkRequest given an existing dataSet
	 * @param dataSetA first dataSet to be included in the LinkRequest
	 * @param dataSetA second dataSet to be included in the LinkRequest
	 * @return LinkRequest Object linking the existing dataSet to the recently created Dataset 
	 */
	public LinkRequest getLinkedRequest(DataSet ds) {
		LinkRequest resultLinkRequest = new LinkRequest();
		resultLinkRequest.setIssuer(issuer);
		resultLinkRequest.setLloa(loa);
		resultLinkRequest.setIssued(new Date().toString());
		resultLinkRequest.setType(linkRequestType);
		resultLinkRequest.setDatasetA(createNewDataSet());
		resultLinkRequest.setDatasetB(ds);
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
	
	
}
