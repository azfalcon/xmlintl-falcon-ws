/********************************************
 *
 * Copyright (c) 2003-2014 XML-INTL Ltd.
 *
 * All Rights Reserved
 *
 ********************************************/

package com.xmlintl.falcon.util;

import java.util.UUID;

/**
 * Static utility class for the Falcon WS project.
 *
 * @author $Author: azydron $
 * @version $Revision: 46553 $, $Date: 2014-09-26 10:04:20 +0100 (Fri, 26 Sep 2014) $
 */
public class FalconUtil
{
    /**
     * privatre constructor.
     */
    private FalconUtil() {}
    /**
     * Generate a UUID.
     * @return The UUID as a string.
     */
    public static String getUUID()
    {
        UUID uuid = UUID.randomUUID();
        
        return uuid.toString();
    }

}
