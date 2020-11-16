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
	//private final String subjectId = System.getenv("UUID_FRIENDLY_NAME") == null ? "sealUUID" : System.getenv("UUID_FRIENDLY_NAME");
	private static final String uuidFriendlyName = System.getenv("UUID_FRIENDLY_NAME")== null ? "sealUUID" : System.getenv("UUID_FRIENDLY_NAME");
	private static final String uuidAttrName = System.getenv("UUID_ATTR_NAME")== null ? "http://project-seal.eu/2020/id/sealUUID" : System.getenv("UUID_ATTR_NAME");	
	private final String attributeEncoding = System.getenv("ATTRIBUTE_ENCODING")== null ? "plain" : System.getenv("ATTRIBUTE_ENCODING");
	private final boolean mandatory = System.getenv("MANDATORY")== null ? true : (System.getenv("MANDATORY").toLowerCase().contains("true"))? true : false;
	
	private final String senderId = System.getenv("SENDER_ID") == null ? "uuid_ms001": System.getenv("SENDER_ID");
	
	//LinkRequestAttribbutes
	private final String issuerIdContent = System.getenv("ISSUER_ID") == null ? "https://vm.project-seal.eu/" : System.getenv("ISSUER_ID");
	private final String derivedDatasetType = System.getenv("DERIVED_DATASET_TYPE") == null ? "derivedID" : System.getenv("DERIVED_DATASET_TYPE");
	private final String issuer = System.getenv("ISSUER") == null ? "SEAL Automated Linker" : System.getenv("ISSUER");
	private final String linkRequestType = System.getenv("LINK_REQUEST_TYPE") == null ? "linkedID" :System.getenv("LINK_REQUEST_TYPE");
	private final List<String> derivedIdcategories =System.getenv("DERIVED_ID_CATEGORIES") == null ?  Arrays.asList("UUID4") : Arrays.asList(System.getenv("DERIVED_ID_CATEGORIES"));
	private final String loa = System.getenv("LOA") == null ? "4" : System.getenv("LOA");
	private final String language = System.getenv("UUID_LANGUAGE")== null ? "null" : System.getenv("UUID_LANGUAGE");
	
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
			String objectId = getUniqueIdForDerivation(derivedDataSet); 
			sessionManagerClient.updateDatastore(sessionId, objectId, derivedDataSet, "dataSet");
			
			Object objAuthenticatedSubject = null;
			DataSet authenticatedSubject = null;
			objAuthenticatedSubject = sessionManagerClient.readVariable(sessionId, "authenticatedSubject");
			authenticatedSubject = (new ObjectMapper()).readValue(objAuthenticatedSubject.toString(),DataSet.class);
			
			// Create a new linked identity dataSet request with the sealUUID dataSet
			// Linked to the current authenticatedSubject
			
			//LinkRequest linkedRequest = getLinkedRequest (derivedDataSet, authenticatedSubject); // Just linked
			LinkRequest resultLinkRequest = new LinkRequest();
			// Sort dsA and dsB
			DataSet datasetA = new DataSet();
			DataSet datasetB = new DataSet();
			if (//derivedDataSet.getSubjectId().compareTo(authenticatedSubject.getSubjectId()) < 0) {
				getSubjectIdLnk(derivedDataSet.getAttributes(), derivedDataSet.getSubjectId()).
				compareTo(getSubjectIdLnk(authenticatedSubject.getAttributes(), authenticatedSubject.getSubjectId() )) < 0) {
				datasetA = derivedDataSet;
				datasetB = authenticatedSubject;
			}
			else if (//derivedDataSet.getSubjectId().compareTo(authenticatedSubject.getSubjectId()) > 0) {
					getSubjectIdLnk(derivedDataSet.getAttributes(), derivedDataSet.getSubjectId()).
					compareTo(getSubjectIdLnk(authenticatedSubject.getAttributes(), authenticatedSubject.getSubjectId() )) > 0) {
				datasetA = authenticatedSubject;
				datasetB = derivedDataSet;
			}
			else //equals
				if (//derivedDataSet.getIssuerId().compareTo(authenticatedSubject.getIssuerId()) <= 0) {
					getIssuerIdLnk(derivedDataSet.getAttributes(), derivedDataSet.getIssuerId()).
					compareTo(getIssuerIdLnk(authenticatedSubject.getAttributes(), authenticatedSubject.getIssuerId() )) <= 0) {
					datasetA = derivedDataSet;
					datasetB = authenticatedSubject;
				}
				else {
					datasetA = authenticatedSubject;
					datasetB = derivedDataSet;
				}
			resultLinkRequest.setDatasetA(datasetA);
			resultLinkRequest.setDatasetB(datasetB);
			
			resultLinkRequest.setId("LINK_" + UUID.randomUUID().toString());
			resultLinkRequest.setIssuer(issuer);
			resultLinkRequest.setLloa(loa);
			resultLinkRequest.setIssued(new Date().toString());
			resultLinkRequest.setType(linkRequestType);
			
			String storeEntryLnkId = "";
			try {
				storeEntryLnkId = "urn:mace:project-seal.eu:link:" + 
						URLEncoder.encode("SEAL id-boot", StandardCharsets.UTF_8.toString()) + ":" + // TO ASK
						//"LLoA" + ":" +
						URLEncoder.encode(getSubjectIdLnk(datasetA.getAttributes(), datasetA.getSubjectId()), StandardCharsets.UTF_8.toString()) + ":" + 
						URLEncoder.encode(getIssuerIdLnk(datasetA.getAttributes(), datasetA.getIssuerId()), StandardCharsets.UTF_8.toString())  + ":" +  
						URLEncoder.encode(getSubjectIdLnk(datasetB.getAttributes(), datasetB.getSubjectId()), StandardCharsets.UTF_8.toString()) + ":" + 
						URLEncoder.encode(getIssuerIdLnk(datasetB.getAttributes(), datasetB.getIssuerId()), StandardCharsets.UTF_8.toString());
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			LOG.info("storeEntryLnkId: " + storeEntryLnkId);
			
			// Add the linkRequest to the dataStore
			sessionManagerClient.updateDatastore(sessionId, storeEntryLnkId, resultLinkRequest, "linkRequest");
			
			// Update the authenticatedSubject with the dataSet just generated
			sessionManagerClient.updateSessionVariables(sessionId, sessionId, "authenticatedSubject", derivedDataSet);
					
		} catch (Exception e){
			e.printStackTrace();
			// TODO
			// this.returnError
			
		}
		
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
	
        derivedDataSet.setId("DRV_" + UUID.randomUUID().toString());  
		derivedDataSet.setType(derivedDatasetType);
		derivedDataSet.setCategories(derivedIdcategories);
		derivedDataSet.setIssuerId("issuerEntityId");
		derivedDataSet.setSubjectId(uuidFriendlyName);  // Pointing to sealUUIDAttributeType.
		derivedDataSet.setLoa(loa);
		derivedDataSet.setIssued(issued.toString());
		derivedDataSet.setExpiration(expiration.toString());
		
		attributes.add(sealUUIDAttributeType());
		
		AttributeType issuerAttr = new AttributeType();
		issuerAttr.setName("issuerEntityId");
		issuerAttr.setFriendlyName("issuerEntityId");
		List<String> issuerValues = new ArrayList<String>();
		issuerValues.add (issuerIdContent);
		issuerAttr.setValues(issuerValues.toArray(new String[0]));
		
		attributes.add(issuerAttr);
				
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
		sealUUIDAttributeType.setMandatory(mandatory);
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
		
			String auxIssuer = //drvDataSet.getIssuerId();
					getIssuerIdLnk(drvDataSet.getAttributes(), drvDataSet.getIssuerId());
			String auxSubject = //drvDataSet.getSubjectId();
					getSubjectIdLnk(drvDataSet.getAttributes(), drvDataSet.getSubjectId());
			
			uniqueId = uniqueId + ":" + 
					URLEncoder.encode(auxIssuer, StandardCharsets.UTF_8.toString()) + ":" + 
					URLEncoder.encode(auxSubject, StandardCharsets.UTF_8.toString());
		
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		return uniqueId;
	}
	
	  private String getSubjectIdLnk(List<AttributeType> attributes, String subjectId) {
		  String theSubjectId = null;
		  
		  for (AttributeType attr: attributes) {
			  if ((attr.getFriendlyName() != null) && 
				 (attr.getFriendlyName().contains (subjectId))){
				  
				  theSubjectId = attr.getValues()[0];
				  break;
			  }
		  }
		  
		  return (theSubjectId != null ? theSubjectId : subjectId);
	  
	  }
	  
		private String getIssuerIdLnk(List<AttributeType> attributes, String issuerId) {
		  String theIssuerId = null;
		  
		  for (AttributeType attr: attributes) {
			  if ((attr.getFriendlyName() != null) && 
				 (attr.getFriendlyName().contains (issuerId))){
				  
				  theIssuerId = attr.getValues()[0];
				  break;
			  }
		  }
		  
		  return (theIssuerId != null ? theIssuerId : issuerId);
		
		}
	
	
	
}
