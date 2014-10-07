/********************************************
 *
 * Copyright (c) 2003-2014 XML-INTL Ltd.
 *
 * All Rights Reserved
 *
 ********************************************/

package com.xmlintl.falcon.util;


/**
 * @author andrzejzydron
 *
 */
public class FalconException extends Exception
{

    /**
     * Generated Serial Version ID.
     */
    private static final long serialVersionUID = -7258667574604494813L;


    /**
     * Default constructor.
     * 
     * @param message
     *            the message that describes this error
     * @param e
     *            the previous exception in the stack.
     */
    public FalconException(String message, Throwable e)
    {
        super(message, e);
    }
    /**
     * Default constructor.
     * 
     * @param message
     *            the message that describes this error
     * @param e
     *            the previous exception in the stack (extractexception to obtain error).
     */
    public FalconException(String message, FalconException e)
    {
        super(message, e);
    }
    
    
    /**
     * Constructor with message.
     * @param message message describing error
     */
    public FalconException(String message)
    {
        super(message);
    }
    
}
