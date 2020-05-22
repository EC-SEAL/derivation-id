package eu.seal.derivation.service.impl;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import eu.seal.derivation.model.pojo.AttributeSet;
import eu.seal.derivation.model.pojo.AttributeSetList;
import eu.seal.derivation.model.pojo.AttributeType;

/**
 * 
 * @author carlosbuendia
 * The AttributeSetTypeResolver resolve Attribute Types from an attributeSet
 *
 */
public class AttributeSetTypeResolver {
	
	
    private final static Logger LOG = LoggerFactory.getLogger(HttpSignatureServiceImpl.class);
    private final static String modelRoot = "src/main/resources/dataTypes/";
    private static String [] allowedTypes = {"eIDAS", "eduOrg", "eduPerson", "schac"};
    
    
    
    
    /**
	 * Given a set of attributes, resolves to a String determining its type
	 * @param attributeSetList
	 * @return String specifying its type, currently {eIDAS, eduOrg, eduPerson, schac} 
	 */

	public static String resolveType(AttributeSet attributeSet) {
		for ( AttributeType aItem : attributeSet.getAttributes()) {
			System.out.println(aItem);
		}
		
		for ( String type : allowedTypes) {
			AttributeType[] dataPattern = searchPattern(type);
			if(modelEquals(attributeSet, dataPattern)) {
				return type;
			}
		}
		
		return "";
	}
	
	/**
	 * 
	 * @param attributeSet
	 * @return
	 */
	public static boolean isModel(AttributeSet dataReceived) {
    	AttributeType[] dataPattern;
	    dataPattern = searchPattern("eIDAS");
	    return modelEquals(dataReceived, dataPattern);
	}
	
	/**
	 * Transforms a given label into a set of AttributeTypes
	 * @return AttributeType, array of AttributePar
	 */
	public static AttributeType[] searchPattern(String label) {
		
		Gson gson = new Gson();
    	JsonReader reader;
    	AttributeType[] dataPattern = new AttributeType[10];
    	
		try {
	    	JsonReader readerEidasPattern = new JsonReader(new FileReader(modelRoot + label + ".json"));
	    	dataPattern = gson.fromJson(readerEidasPattern, AttributeType[].class);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
    	return dataPattern;
	}
	
	/**
	 * Checks if a given array of Attributes is Equal to a given  pattern
	 */
	
	public static boolean modelEquals(AttributeSet attributeSet, AttributeType[] pattern) {
		int target = pattern.length;
		int checksum = 0;
		for ( AttributeType aItem : attributeSet.getAttributes()) {
			for ( AttributeType bItem: pattern) {
				if (aItem.getFriendlyName().equals(bItem.getFriendlyName())) {
					checksum++;
				}
			}
		}
			
		return checksum > 0;
	}
	
	

}
