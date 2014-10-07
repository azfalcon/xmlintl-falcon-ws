/********************************************
 *
 * Copyright (c) 2003-2014 XML-INTL Ltd.
 *
 * All Rights Reserved
 *
 ********************************************/

package com.xmlintl.falcon.util;

import static com.xmlintl.falcon.util.CommonDefines.SMT_ENGINES_ROOT_DIR;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * List the available SMT emgnies.
 *
 * @author $Author: azydron $
 * @version $Revision: 46553 $, $Date: 2014-09-26 10:04:20 +0100 (Fri, 26 Sep 2014) $
 */
public class ListEngines extends FalconAbstract
{
    /**
     * Constructor.
     * @throws IOException If we cannot read the properties file.
     * @throws FalconException If the rootDir property is undefined.
     */
    public ListEngines() throws FalconException
    {
        super();
    }
    /**
     * Get the list of customers and engines.
     * @return The list of customers and engines.
     * @throws FalconException If the customers directory does not exist.
     */
    public ArrayList<String> list() throws FalconException
    {
        ArrayList<String> engineList = new ArrayList<String>();
        
        String rootDir = properties.getProperty(SMT_ENGINES_ROOT_DIR);
        
        File customersDir = new File(rootDir);
        
        File[] customers = customersDir.listFiles();
        
        if (customers == null)
        {
            throw new FalconException("Empty directory");
        }
        
        for (File customer: customers)
        {
            String customerName = customer.getName();
            
            File[] engines = customer.listFiles();
            
            for (File engine: engines)
            {
                String engineName = engine.getName();
                
                engineList.add(customerName + "_" + engineName);
            }
        }
        
        return engineList;
    }
}
