/********************************************
 *
 * Copyright (c) 2003-2014 XML-INTL Ltd.
 *
 * All Rights Reserved
 *
 ********************************************/

package com.xmlintl.falcon.util;

import java.io.Serializable;

/**
 * SMT Engine POJO.
 * 
 * @author $Author: azydron $
 * @version $Revision: 46553 $, $Date: 2014-09-26 10:04:20 +0100 (Fri, 26 Sep 2014) $
 */
public class SMTEngine implements Serializable
{
    /** Serial ID. */
    private static final long serialVersionUID = -154223599930157513L;

    private String engineId;
    
    private String client;
    
    private String customerId;
    
    private String srcLangCode;
    
    private String tgtLangCode;
    
    private String domain;
    /**
     * Constructor.
     * @param engineId The engine ID.
     * @param client The client name.
     * @param customerId The customer ID.
     * @param srcLangCode The source langauge code.
     * @param tgtLangCode The target language code.
     * @param domain The engine domain.
     */
    public SMTEngine(String engineId, String client, String customerId, String srcLangCode, String tgtLangCode, String domain)
    {
        this.engineId = engineId;
        this.client = client;
        this.customerId = customerId;
        this.srcLangCode = srcLangCode;
        this.tgtLangCode = tgtLangCode;
        this.domain = domain;
    }

    /**
     * Get the engineId.
     * @return the engineId.
     */
    public String getEngineId()
    {
        return engineId;
    }

    /**
     * Get the client.
     * @return the client.
     */
    public String getClient()
    {
        return client;
    }

    /**
     * Get the customerId.
     * @return the customerId.
     */
    public String getCustomerId()
    {
        return customerId;
    }

    /**
     * Get the srcLangCode.
     * @return the srcLangCode.
     */
    public String getSrcLangCode()
    {
        return srcLangCode;
    }

    /**
     * Get the tgtLangCode.
     * @return the tgtLangCode.
     */
    public String getTgtLangCode()
    {
        return tgtLangCode;
    }

    /**
     * Get the domain.
     * @return the domain.
     */
    public String getDomain()
    {
        return domain;
    }
    
    @Override
    public String toString()
    {
        StringBuilder buff = new StringBuilder();
        
        buff
        .append("\tengineId: " + engineId + "\n")
        .append("\tclient: " + client + "\n")
        .append("\tcustomerId: " + customerId + "\n")
        .append("\tsrcLangCode: " + srcLangCode + "\n")
        .append("\ttgtLangCode: " + tgtLangCode + "\n")
        .append("\tdomain: " + domain + "\n");
                    
        
        return buff.toString();
    }
}
