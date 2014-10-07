/********************************************
 *
 * Copyright (c) 2003-2014 XML-INTL Ltd.
 *
 * All Rights Reserved
 *
 ********************************************/

package com.xmlintl.falcon.util;

import org.apache.log4j.Logger;


/**
 * @author andrzejzydron
 *
 */
public abstract class FalconAbstract
{
    protected Logger logger = Logger.getLogger(this.getClass());

    protected static FalconProperties properties;
    
    /**
     * Initialize the class.
     * 
     * @throws FalconException If we are unable to initialize the Falcon properties.
     * 
     */
    public void init() throws FalconException
    {
        properties = new FalconProperties();
    }

    public FalconAbstract() throws FalconException
    {
        if (properties == null)
        {
            init();
        }

    }
}
