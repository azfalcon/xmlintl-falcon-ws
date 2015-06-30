/********************************************
 *
 * Copyright (c) 2003-2014 XML-INTL Ltd.
 *
 * All Rights Reserved
 *
 ********************************************/

package com.xmlintl.falcon.util;

import static com.xmlintl.falcon.util.CommonDefines.INC_TRAIN;
import static com.xmlintl.falcon.util.CommonDefines.SCRIPTS_DIR;
import static com.xmlintl.falcon.util.CommonDefines.SMT_ENGINES_ROOT_DIR;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStreamWriter;
import java.net.URLDecoder;

/**
 * @author andrzejzydron
 * @uthor ankitks 24th JUne 2015
 *
 */
public class SendSegmentToUpdateSMT extends TranslateSegment
{
    
    private String tgtSegment;
    
    private String srcLang;
    
    private String tgtLang;
    
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
    public SendSegmentToUpdateSMT(String engineID, String customerID, String projectID, String srcLang, String tgtLang, String srcSegment, String tgtSegment) throws FalconException
    {
        super(engineID, customerID, srcSegment);

        this.tgtSegment = tgtSegment;
    }
    /**
     * Update the SMT engine by outputing the source and target ten times to affect the probability calculations.
     * @throws FalconException If any errors are encoutered.
     */
    public void update() throws FalconException
    {
        String scriptsDir = properties.getProperty(SCRIPTS_DIR);
        
        String execScript = scriptsDir + INC_TRAIN;
        
        String enginesDir = properties.getProperty(SMT_ENGINES_ROOT_DIR);
        
        String newData = enginesDir + customerID + "/" + srcLang + "_" + tgtLang + "/new_data";
        
        File nd = new File(newData);
        
        if (!nd.exists())
        {
            nd.mkdir();
        }
        
        String srcTrainFilename = uuid + "." + srcLang;
        String tgtTrainFilename = uuid + "." + tgtLang;
        
        File ndfs = new File(srcTrainFilename);
        File ndft = new File(tgtTrainFilename);
        
        InputStream is = null;
        OutputStreamWriter os = null;
        OutputStreamWriter ot = null;
        
        try
        {
            os = new OutputStreamWriter( new FileOutputStream(ndfs));
            ot = new OutputStreamWriter( new FileOutputStream(ndft));
            
            // Remove the loop, we no longer need to biase it 10 times
            // URLDecoder.decode ensures the escaped characters like %20 are converted 
            //for (int i = 0; i < 10; i++)
            //{
            os.write(URLDecoder.decode(srcSegment, "UTF-8") + "\n");
            ot.write(URLDecoder.decode(tgtSegment, "UTF-8") + "\n");
            //}
            
            if (os != null)
            {
                try
                {
                    os.close();
                }
                catch (IOException e)
                {
                    logger.error(e.getMessage(), e);
                }
                
            }
            if (ot != null)
            {
                try
                {
                    ot.close();
                }
                catch (IOException e)
                {
                    logger.error(e.getMessage(), e);
                }
                
            }
            // "Usage: `basename $0` <src_lang> <tgt_lang> <train_filename> <engine_name>"
            ProcessBuilder pb = new ProcessBuilder(execScript, srcLang, tgtLang, uuid, engineID, customerID);
            
            Process decoder = pb.start();

            is = decoder.getInputStream();

            LineNumberReader reader = new LineNumberReader(new InputStreamReader(is, "UTF-8"));
            
            String outputLine = null;
            
            while ((outputLine = reader.readLine()) != null)
            {
                logger.info(outputLine);
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
            if (os != null)
            {
                try
                {
                    os.close();
                }
                catch (IOException e)
                {
                    logger.error(e.getMessage(), e);
                }
                
            }
            if (ot != null)
            {
                try
                {
                    ot.close();
                }
                catch (IOException e)
                {
                    logger.error(e.getMessage(), e);
                }
                
            }

        }
    }
}
