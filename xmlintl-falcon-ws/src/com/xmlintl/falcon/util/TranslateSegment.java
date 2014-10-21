/********************************************
 *
 * Copyright (c) 2003-2014 XML-INTL Ltd.
 *
 * All Rights Reserved
 *
 ********************************************/

package com.xmlintl.falcon.util;

import static com.xmlintl.falcon.util.CommonDefines.BLEU;
import static com.xmlintl.falcon.util.CommonDefines.OUTPUT;
import static com.xmlintl.falcon.util.CommonDefines.SCRIPTS_DIR;
import static com.xmlintl.falcon.util.CommonDefines.SEGMENT_DECODE;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

/**
 * Translate the segment.
 *
 * @author $Author: azydron $
 * @version $Revision: 46553 $, $Date: 2014-09-26 10:04:20 +0100 (Fri, 26 Sep 2014) $
 */
public class TranslateSegment extends FalconAbstract
{
    protected String engineID;
    
    protected String customerID;
    
    protected String projectID;
    
    protected String srcLang;
    
    protected String tgtLang;
    
    protected String srcSegment;
    
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
     * @param srcSegment The source segment.
     * @throws FalconException If we cannot initialize the Falcon properties environment correctly.
     */
    public TranslateSegment(String engineID, String customerID, String projectID, String srcLang, String tgtLang, String srcSegment) throws FalconException
    {
        super();
        
        this.engineID = engineID;
        this.customerID = customerID;
        this.projectID = projectID;
        this.srcLang = srcLang;
        this.tgtLang = tgtLang;
        this.srcSegment = srcSegment;
        
        uuid = FalconUtil.getUUID();
    }
    /**
     * Do the translating.
     * @return The translated text or an empty string if none avaliable.
     * @throws FalconException If any errors are encountered.
     */
    public String translate() throws FalconException
    {
        String scriptsDir = properties.getProperty(SCRIPTS_DIR);
        
        String execScript = scriptsDir + SEGMENT_DECODE;
        
        String engine = customerID + '/' + srcLang + '_' + tgtLang;
        
        String srcSegmentLC = srcSegment.toLowerCase();
        
        ProcessBuilder pb = new ProcessBuilder(execScript, engine, srcSegmentLC, uuid);
        
        InputStream is = null;
        
        try
        {
            Process decoder = pb.start();

            is = decoder.getInputStream();

            LineNumberReader reader = new LineNumberReader(new InputStreamReader(is, "UTF-8"));
            
            String outputLine = null;
            
            while ((outputLine = reader.readLine()) != null)
            {
                if (outputLine.startsWith(OUTPUT))
                {
                    translation = outputLine.substring(OUTPUT.length() - 1);
                }
                else if (outputLine.startsWith(BLEU))
                {
                    bleuScore = outputLine.substring(BLEU.length() - 1);
                }
                
            }
        }
        catch (Exception e)
        {
            throw new FalconException(e.getMessage(), e);
        }
        finally
        {
            if (is != null)
            {
                try
                {
                    is.close();
                }
                catch (IOException e)
                {
                    logger.error(e.getMessage(), e);
                }
            }
        }

        return translation;
    }
    /**
     * Get the uuid.
     * @return the uuid.
     */
    public String getUuid()
    {
        return uuid;
    }
}
