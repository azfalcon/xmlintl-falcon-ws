/********************************************
 *
 * Copyright (c) 2003-2014 XML-INTL Ltd.
 *
 * All Rights Reserved
 *
 ********************************************/

package com.xmlintl.falcon.util;

import static org.junit.Assert.*;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

/**
 * @author andrzejzydron
 *
 */
public class TranslateSegmentTest
{
    Logger logger = Logger.getLogger(TranslateSegmentTest.class);


    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception
    {
    }

    /**
     * Test method for {@link com.xmlintl.falcon.util.TranslateSegment#TranslateSegment(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
     */
    @Test
    public final void testTranslateSegment()
    {
        try
        {
            TranslateSegment xlate = new TranslateSegment("Falcon", "2147", "en", "es", "Hello World", "ce4259bd-b307-4707-accb-b6504677d94c");
            
            String xlation = xlate.translate();
            
            logger.info(xlation);
        }
        catch (FalconException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            
            fail(e.getMessage());
        }
    }

    /**
     * Test method for {@link com.xmlintl.falcon.util.TranslateSegment#translate()}.
     */
    //@Test
    public final void testTranslate()
    {
        fail("Not yet implemented"); // TODO
    }

    /**
     * Test method for {@link com.xmlintl.falcon.util.TranslateSegment#getUuid()}.
     */
    //@Test
    public final void testGetUuid()
    {
        fail("Not yet implemented"); // TODO
    }

}
