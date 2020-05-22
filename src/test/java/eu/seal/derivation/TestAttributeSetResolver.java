/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.seal.derivation;



import java.io.FileNotFoundException;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.opensaml.saml2.core.Attribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import eu.seal.derivation.controllers.Generate;
import eu.seal.derivation.model.pojo.AttributeSet;
import eu.seal.derivation.model.pojo.AttributeSetList;
import eu.seal.derivation.model.pojo.AttributeType;
import eu.seal.derivation.service.impl.AttributeSetTypeResolver;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;


@RunWith(SpringRunner.class)
public class TestAttributeSetResolver {
	
	private final static Logger LOG = LoggerFactory.getLogger(TestAttributeSetResolver.class);
	private final static String authSetMockURL = "src/main/resources/dataTypes/authSetEidas.json";
	
	
	
	// Should Resolve schac when input is schac
	// Should rerolve eduPerson when input is eduPerson

	// Given a JSON, returns the correct set of AttributeTypes
	
	
	// Should resolve eidas when input is eidas

    
    
    @Test
    public void testSearch() throws FileNotFoundException {
    	
    	Gson gson = new Gson();
    	JsonReader reader = new JsonReader(new FileReader(authSetMockURL));
    	AttributeSet dataReceived = gson.fromJson(reader, AttributeSet.class);
    	Boolean isModel = AttributeSetTypeResolver.isModel(dataReceived);
    	
    	assertEquals(true, isModel);
    }
    
    @Test
    public void testResolve() throws FileNotFoundException {
    	
    	Gson gson = new Gson();
    	JsonReader reader = new JsonReader(new FileReader(authSetMockURL));
    	AttributeSet dataReceived = gson.fromJson(reader, AttributeSet.class);
    	String typeResolved = AttributeSetTypeResolver.resolveType(dataReceived);
    	
    	assertEquals("eIDAS", typeResolved);
    }

}