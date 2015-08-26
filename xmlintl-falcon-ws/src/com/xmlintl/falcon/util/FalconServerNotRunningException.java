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
public class FalconServerNotRunningException extends Exception
{

    /**
     * @param e
     */
    public FalconServerNotRunningException(Throwable e)
    {
        super(e);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     * @param e
     */
    public FalconServerNotRunningException(String message, Throwable e)
    {
        super(message, e);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     * @param e
     */
    public FalconServerNotRunningException(String message, FalconException e)
    {
        super(message, e);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     */
    public FalconServerNotRunningException(String message)
    {
        super(message);
        // TODO Auto-generated constructor stub
    }

}
