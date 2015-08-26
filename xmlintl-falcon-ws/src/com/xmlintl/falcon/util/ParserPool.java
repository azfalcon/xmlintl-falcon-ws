/********************************************
 * 
 * Copyright (c) 2003-2010 XML-INTL Ltd.
 * 
 * All Rights Reserved
 * 
 ********************************************/
package com.xmlintl.falcon.util;

import java.util.EmptyStackException;
import java.util.Stack;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.apache.xml.resolver.tools.CatalogResolver;

/**
 * Describes a Parser Pool. We don't hold used parsers since they are busy anyway. If a
 * user free()s a parses, it gets into the appropriate free parsers stack. Holding busy
 * parsers anywhere gets us into memory leak hell.
 * 
 * @author $Author: wjeczalik $
 * 
 * @version $Revision: 32797 $, $Date: 2013-06-11 13:04:53 +0100 (Tue, 11 Jun 2013) $
 */
public final class ParserPool
{
    /** Logger. */
    private static Logger logger = Logger.getLogger(ParserPool.class);
    
    /** The Singleton unique instance. */
    private static ParserPool uniqueInstance = null;
    
    /** Internal DocumentBuilderFactory. */
    private DocumentBuilderFactory dbf = null;
    
    /** Internal Stack of free namespace aware builders. */
    private Stack<DocumentBuilder> namespaceAwareBuilders = null;
    
    /** Internal Stack of free namespace unaware builders. */
    private Stack<DocumentBuilder> basicBuilders = null;
    
    /**
     * Gets the ParserPool Singleton instance. Default pool size specified inside.
     * 
     * @return an unique instance of the parser pool
     */
    public static synchronized ParserPool getInstance()
    {
        if (uniqueInstance == null)
        {
            uniqueInstance = new ParserPool(2);
        }
        return uniqueInstance;
    }
    
    /**
     * Private constructor that creates a new ParserPool.
     * 
     * @param initialPoolSize
     *            initial number of parsers included
     */
    private ParserPool(int initialPoolSize)
    {
        this.dbf = DocumentBuilderFactory.newInstance();
        this.namespaceAwareBuilders = new Stack<DocumentBuilder>();
        this.basicBuilders = new Stack<DocumentBuilder>();
        try
        {
            for (int i = 0; i < initialPoolSize; i++)
            {
                this.namespaceAwareBuilders.push(createParser(true));
                this.basicBuilders.push(createParser(false));
            }
        }
        catch (ParserConfigurationException pce)
        {
            logger.warn(pce.getMessage());
        }
    }
    
    /**
     * Gets a basic (namespace unaware) DocumentBuilder from the pool or creates a new one
     * if one's missing. NOTE: You are supposed to free() this DocumentBuilder after
     * usage.
     * 
     * @return a DocumentBuilder instance
     * @throws ParserConfigurationException
     *             when creating a new parser failed
     */
    public DocumentBuilder get() throws ParserConfigurationException
    {
        synchronized (uniqueInstance)
        {
            DocumentBuilder result = null;
            
            try
            {
                result = this.basicBuilders.pop();
            }
            catch (EmptyStackException ese)
            {
                result = createParser(false);
            }
            
            return result;
        }
    }
    
    /**
     * Gets a namespace aware DocumentBuilder from the pool or creates a new one if one's
     * missing. NOTE: You are supposed to free() this DocumentBuilder after usage.
     * 
     * @return a DocumentBuilder instance
     * @throws ParserConfigurationException
     *             when creating a new parser failed
     */
    public DocumentBuilder getNS() throws ParserConfigurationException
    {
        synchronized (uniqueInstance)
        {
            DocumentBuilder result = null;
            
            try
            {
                result = this.namespaceAwareBuilders.pop();
            }
            catch (EmptyStackException ese)
            {
                result = createParser(true);
            }
            return result;
        }
    }
    
    /**
     * Frees a DocumentBuilder object and puts it into an appropriate free parser stack.
     * 
     * @param db
     *            the DocumentBuilder to free
     */
    public void free(DocumentBuilder db)
    {
        synchronized (uniqueInstance)
        {
            if (db.isNamespaceAware())
            {
                this.namespaceAwareBuilders.push(db);
            }
            else
            {
                this.basicBuilders.push(db);
            }
        }
    }
    
    /**
     * Creates a new parser. External usage not recommended.
     * 
     * @param namespaceAware
     *            should the produced parser be namespace aware
     * @return a new instance of an XML parser
     * 
     * @throws ParserConfigurationException
     *             if producing the parser fails
     */
    DocumentBuilder createParser(boolean namespaceAware) throws ParserConfigurationException
    {
        synchronized (dbf)
        {
            dbf.setNamespaceAware(namespaceAware);
            // dbf.setValidating(false);
            // dbf.setAttribute("http://xml.org/sax/features/validation",Boolean.FALSE);
            dbf.setAttribute("http://apache.org/xml/features/nonvalidating/load-external-dtd",
                             Boolean.FALSE);
            dbf.setAttribute("http://xml.org/sax/features/external-general-entities", Boolean.FALSE);
            dbf.setAttribute("http://xml.org/sax/features/external-parameter-entities", Boolean.FALSE);
            
            dbf.setExpandEntityReferences(false);
            
            DocumentBuilder builder = dbf.newDocumentBuilder();
            CatalogResolver resolver = new CatalogResolver();
            builder.setEntityResolver(resolver);
            
            return builder;
        }
    }
}
