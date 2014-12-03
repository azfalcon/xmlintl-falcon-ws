/********************************************
 *
 * Copyright (c) 2003-2014 XML-INTL Ltd.
 *
 * All Rights Reserved
 *
 ********************************************/

package com.xmlintl.falcon.util;

/**
 * @author andrzejzydron
 *
 */
public class CalculateOptimalSegmentSequence extends FalconAbstract
{
    protected String engineID;
    
    protected String customerID;
    
    protected String projectID;
    
    protected String srcLang;
    
    protected String tgtLang;
    
    protected String segFileURL;
    
    protected String translation;
    
    protected String bleuScore;
    
    protected String uuid;

    /**
     * Constructor.
     * @param engineID The SMT engine ID. 
     * @param customerID The customer ID.
     * @param projectID The project ID. Ignored for now for future use.
     * @param srcLang The source language.
     * @param tgtLang The target language.
     * @param segFileURL The URL of the source segment file.
     * @throws FalconException If we cannot initialize the Falcon properties environment correctly.
     */
    public CalculateOptimalSegmentSequence(String engineID, String customerID, String projectID, String srcLang, String tgtLang, String segFileURL) throws FalconException
    {
        super();
        
        this.engineID = engineID;
        this.customerID = customerID;
        this.projectID = projectID;
        this.srcLang = srcLang;
        this.tgtLang = tgtLang;
        this.segFileURL = segFileURL;
        
        uuid = FalconUtil.getUUID();
    }

}
