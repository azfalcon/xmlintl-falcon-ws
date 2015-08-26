/********************************************
 *
 * Copyright (c) 2003-2015 XML-INTL Ltd.
 *
 * All Rights Reserved
 *
 ********************************************/

package com.xmlintl.falcon.util;

/**
 * @author andrzejzydron
 *
 */
public class FalconTimeoutException extends Exception
{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * @param e
     */
    public FalconTimeoutException(Throwable e)
    {
        super(e);
    }

    /**
     * @param message
     * @param e
     */
    public FalconTimeoutException(String message, Throwable e)
    {
        super(message, e);
    }

    /**
     * @param message
     * @param e
     */
    public FalconTimeoutException(String message, FalconException e)
    {
        super(message, e);
    }

    /**
     * @param message
     */
    public FalconTimeoutException(String message)
    {
        super(message);
    }

}
