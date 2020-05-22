package eu.seal.derivation.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.seal.derivation.model.pojo.AttributeSet;
import eu.seal.derivation.model.pojo.DataSet;

public class AuthSetToDataSet {
	private static final Logger LOG = LoggerFactory.getLogger(AuthSetToDataSet.class);
	
	public static DataSet resolveAttributeSet(AttributeSet inputAttributeSet) {
		LOG.info("AuthSet received the following request attributeSet" + inputAttributeSet);
		LOG.info("Attributes: " + inputAttributeSet.getAttributes().toString());
		DataSet result = new DataSet();
		result.setId(inputAttributeSet.getId());
		result.setLoa(inputAttributeSet.getLoa());
		result.setType(AttributeSetTypeResolver.resolveType(inputAttributeSet));
		result.setIssued("??");
		result.setExpiration("??");
		return result;
	}

}
