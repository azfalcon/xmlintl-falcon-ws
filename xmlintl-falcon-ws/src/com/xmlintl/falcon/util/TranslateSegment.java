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
import static com.xmlintl.falcon.util.CommonDefines.SMT_ENGINES_ROOT_DIR;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.HashMap;

/**
 * Translate the segment.
 *
 * @author $Author: azydron $
 * @version $Revision: 46553 $, $Date: 2014-09-26 10:04:20 +0100 (Fri, 26 Sep 2014) $
 */
public class TranslateSegment extends FalconAbstract
{
    /** Hash map of clientName and key values so that we do not have to constantly re-read the uuid files. */
    protected static HashMap<String, String> keyMap = new HashMap<String, String>();
    
    protected String clientName;
    
    protected String customerID;
    
    protected String srcLang;
    
    protected String tgtLang;
    
    protected String srcSegment;
    
    protected String translation;
    
    protected String confidenceScore;
    
    protected String uuid;
    
    protected String key;
    
    /**
     * Constructor.
     * @param clientName The SMT engine ID. 
     * @param customerID The Customer ID
     * @param srcLang The source language.
     * @param tgtLang The target language.
     * @param srcSegment The source segment.
     * @param key The token key value.
     * @throws FalconException If we cannot initialize the Falcon properties environment correctly.
     */
    public TranslateSegment(String clientName, String customerID, String srcLang, String tgtLang, String srcSegment, String key) throws FalconException
    {
        super();
        
        this.clientName = clientName;
        this.customerID = customerID;
        this.srcLang = srcLang;
        this.tgtLang = tgtLang;
        this.srcSegment = srcSegment;
        
        this.key = key;
        
        if (key != null)
        {
            checkKey(key);
        }
        
        uuid = FalconUtil.getUUID();
    }
    
    private void checkKey(String key) throws FalconException
    {
        String testKey = keyMap.get(clientName);
        
        if (testKey == null)
        {
            String enginesDir = properties.getProperty(SMT_ENGINES_ROOT_DIR);
            
            String uuidFileName = enginesDir + "/" + clientName + "/uuid";
            
            File uuidFile = new File(uuidFileName);
            
            StringBuilder uuidBuilder = new StringBuilder();
            
            try
            {
                try (FileInputStream is = new FileInputStream(uuidFile))
                {
                    for (int i = 0; (i = is.read()) != -1;)
                    {
                        char c = (char) i;
                        
                        uuidBuilder.append(c);
                    }
                    
                    testKey = uuidBuilder.toString();
                    
                    keyMap.put(clientName, testKey);
                }
            }
            catch (FileNotFoundException e)
            {
                throw new FalconException("Client: " + clientName + " uuid file: " + uuidFileName + " is missing");
            }
            catch (IOException e)
            {
                throw new FalconException(e.getMessage(), e);
            }
        }
        
        if (!(key.equals(testKey)))
        {
            throw new FalconException("Client key: " + testKey + " does not match web service call key: " + key);
        }

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
        
        // XTM: <x id="x460"/><term translation="zapiekanka_translation">zapiekanka</term> z warzyw<x id="x461"/> â»<x id="x462"/><x id="x463"/>
        
        log("Invoking: " + execScript + " " + clientName + " " + customerID + " " + srcLang + " " + tgtLang + " " + srcSegment + " " + uuid );

        ProcessBuilder pb = new ProcessBuilder(execScript, clientName, customerID, srcLang, tgtLang, srcSegment, uuid);
        
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
                    if (outputLine.length() > OUTPUT.length() + 1)
                    {
                        translation = outputLine.substring(OUTPUT.length() + 1);
                    }
                    else
                    {
                        reader.close();
                        
                        throw new FalconException("Translation segment decode failed");
                    }
                }
                else if (outputLine.startsWith(BLEU))
                {
                    confidenceScore = outputLine.substring(BLEU.length());
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
    /**
     * Get the translation.
     * @return the translation.
     */
    public String getTranslation()
    {
        return translation;
    }
    /**
     * Get the bleuScore.
     * @return the bleuScore.
     */
    public String getBleuScore()
    {
        return confidenceScore;
    }
}
