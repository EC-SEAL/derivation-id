package eu.seal.derivation.service;

import java.io.IOException;
import java.security.KeyStoreException;

import eu.seal.derivation.model.pojo.EntityMetadata;


public interface SealMetadataService {
    
    public EntityMetadata getMetadata() throws IOException, KeyStoreException;

}
