/********************************************
 *
 * Copyright (c) 2003-2014 XML-INTL Ltd.
 *
 * All Rights Reserved
 *
 ********************************************/

package com.xmlintl.falcon.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

/**
 * @author andrzejzydron
 *
 */
public class CalculateOptimalSegmentSequence extends FalconAbstract
{
    protected String            engineID;

    protected String            customerID;

    protected String            projectID;

    protected String            srcLang;

    protected String            tgtLang;

    protected String            segFileURL;

    protected String            translation;

    protected String            bleuScore;

    protected String            uuid;

    protected ArrayList<String> optimalSequence;

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

        optimalSequence = new ArrayList<String>();
    }

    /**
     * Get the optimal route.
     * @return The optimal sequence of segments as an integer array.
     * @throws FalconException If anything goes wrong.
     */
    public int[] getRoute() throws FalconException
    {
        int[] route = null;
        
        int counter = 0;

        ArrayList<Integer> lines = new ArrayList<Integer>();

        try
        {
            URL url = new URL(segFileURL);

            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

            String inputLine;
            
            while ((inputLine = in.readLine()) != null)
            {
                lines.add(new Integer(counter));
//                System.out.println(inputLine);
                counter++;
            }
            
            Collections.shuffle(lines);
            
            in.close();
        }
        catch (Exception e)
        {
            throw new FalconException(e.getMessage(), e);
        }
        
        route = new int[counter + 1];
        
        for (int i = 0; i < counter; i++)
        {
            route[i] = lines.get(i);
        }

        return route;
    }

    /**
     * Get the engineID.
     * @return the engineID.
     */
    public String getEngineID()
    {
        return engineID;
    }

    /**
     * Get the customerID.
     * @return the customerID.
     */
    public String getCustomerID()
    {
        return customerID;
    }

    /**
     * Get the projectID.
     * @return the projectID.
     */
    public String getProjectID()
    {
        return projectID;
    }

    /**
     * Get the srcLang.
     * @return the srcLang.
     */
    public String getSrcLang()
    {
        return srcLang;
    }

    /**
     * Get the tgtLang.
     * @return the tgtLang.
     */
    public String getTgtLang()
    {
        return tgtLang;
    }

    /**
     * Get the segFileURL.
     * @return the segFileURL.
     */
    public String getSegFileURL()
    {
        return segFileURL;
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
        return bleuScore;
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
