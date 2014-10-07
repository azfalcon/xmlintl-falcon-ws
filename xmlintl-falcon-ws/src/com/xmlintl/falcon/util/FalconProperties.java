/********************************************
 *
 * Copyright (c) 2003-2014 XML-INTL Ltd.
 *
 * All Rights Reserved
 *
 ********************************************/

package com.xmlintl.falcon.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Falcon utility class.
 *
 * @author $Author: azydron $
 * @version $Revision: 46553 $, $Date: 2014-09-26 10:04:20 +0100 (Fri, 26 Sep 2014) $
 */
public class FalconProperties extends Properties
{
    /**
     * Constructor.
     * @throws FalconException If we are unable to load the properties.
     */
    public FalconProperties() throws FalconException
    {
        if (this.isEmpty())
        {
            init();
        }
    }
    /**
     * Load the properties from the classpath.
     * @throws FalconException If we encounter any errors trying to load the properties file.
     */
    private void init() throws FalconException
    {
        InputStream in = this.getClass().getResourceAsStream("/smt.properties");

        try
        {
            load(in);
        }
        catch (IOException e)
        {
            throw new FalconException(e.getMessage(), e);
        }

    }
}
