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
    private String clientName;
    
    /**
     * Constructor.
     * @param clientName The client name.
     * @throws IOException If we cannot read the properties file.
     * @throws FalconException If the rootDir property is undefined.
     */
    public ListEngines(String clientName) throws FalconException
    {
        super();
        
        this.clientName = clientName;
    }
    /**
     * Get the list of customers and engines.
     * @return The list of customers and engines.
     * @throws FalconException If the customers directory does not exist.
     */
    public ArrayList<SMTEngine> list() throws FalconException
    {
        ArrayList<SMTEngine> engineList = new ArrayList<SMTEngine>();
        
        String rootDir = properties.getProperty(SMT_ENGINES_ROOT_DIR);
        
        File clientDirs = new File(rootDir);
        
        File[] clients = clientDirs.listFiles();
        
        if (clients == null)
        {
            throw new FalconException("Empty directory");
        }
        
        File engineMapFile = new File(rootDir + "/EngineMapFile");
        
        for (File client: clients)
        {
            if (!client.isDirectory())
            {
                continue;
            }
            
            String clientName = client.getName();
            
            if ("generic".equals(clientName))
            {
                ArrayList<SMTEngine> smtEngines = findLangEngines(client);
                
                engineList.addAll(smtEngines);
            }
            else
            {
                File[] customers = client.listFiles();
                
                for (File customer: customers)
                {
                    if (customer.isDirectory())
                    {
                        String customerId = customer.getName();
                        
                        File[] customerEngines = customer.listFiles();
                        
                        for (File customerEngine: customerEngines)
                        {
                            if (customerEngine.isDirectory())
                            {
                                String customerSMTLangEngineName = customerEngine.getName();
                                
                                String engineId = clientName + "/" + customerId + "/" + customerSMTLangEngineName;
                                
                                String[] langCodes = customerSMTLangEngineName.split("_");
                                
                                if (langCodes.length != 2)
                                {
                                    continue;
                                }
                                
                                String srcLang = langCodes[0];
                                String tgtLang = langCodes[1];
                                
                                SMTEngine smt = new SMTEngine(engineId, clientName, customerId, srcLang, tgtLang, null);
                                
                                engineList.add(smt);
                            }
                        }
                    }
                }
            }
        }
        
        return engineList;
    }
    /**
     * 
     * @param topLevelDir
     * @return
     */
    private ArrayList<SMTEngine> findLangEngines(File topLevelDir)
    {
        ArrayList<SMTEngine> smtEngines = new ArrayList<SMTEngine>();
        
        File[] engines = topLevelDir.listFiles();
        
        String name = topLevelDir.getName();
        
        for (File engine: engines)
        {
            String engineName = engine.getName();
            
            String engineId = name + "/" + engineName;
            
            String[] langCodes = engineName.split("_");
            
            if (langCodes.length != 2)
            {
                continue;
            }
            
            String srcLang = langCodes[0];
            String tgtLang = langCodes[1];
            
            SMTEngine smt = new SMTEngine(engineId, name, null, srcLang, tgtLang, null);
            
            smtEngines.add(smt);
        }
        
        return smtEngines;
    }
    
    
}
