/********************************************
 *
 * Copyright (c) 2003-2014 XML-INTL Ltd.
 *
 * All Rights Reserved
 *
 ********************************************/

package com.xmlintl.falcon.util;

import static org.junit.Assert.fail;

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

    @Test
    public final void testByte2String()
    {
        byte[] buffer = new byte[] {'7', '4', '6', '1'};
        
        String portNoStr = new String(buffer);
        
        logger.info(portNoStr);

    }
    
    /**
     * Test method for {@link com.xmlintl.falcon.util.TranslateSegment#TranslateSegment(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
     */
    @Test
    public final void testTranslateSegment()
    {
        try
        {
            TranslateSegment xlate = new TranslateSegment("Falcon", "2147", "en", "es", "<g id=\"i4\"><term translation=\"XXXYYY\"><g id=\"i4\">Promoting</g><x/></term> <term translation=\"AAABBBCC\">Ireland\';s</term> interests <term translation=\"VVVZZZ\">and</term> values in the world</g>", "ce4259bd-b307-4707-accb-b6504677d94c", "./");
            
            String xlation = xlate.translate();
            
            logger.info(xlation);
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            
            fail(e.getMessage());
        }
    }
    @Test
    public final void testDetokenize()
    {
        try
        {
//            TranslateSegment xlate = new TranslateSegment("Falcon", "2147", "en", "es", "<g id=\"i4\"><term translation=\"XXXYYY\"><g id=\"i4\">Promoting</g><x/></term> <term translation=\"AAABBBCC\">Ireland\';s</term> interests <term translation=\"VVVZZZ\">and</term> values in the world</g>", null /*"ce4259bd-b307-4707-accb-b6504677d94c"*/, "./");
            TranslateSegment xlate = new TranslateSegment("Falcon", "2147", "en", "es", "OASIS DITA <term translation=\"services de traduction\">Translation</term>", null, "./");
//            String detok = xlate.detokenize("( The cat ( feline ) sat on the \" UNKBurberry \" coloured , bright red mat . )");
            
//            String detok = xlate.detokenize("bonjour les femmes de l \u2019 anarchie");
            
            String detok = xlate.detokenize("l&apos; extraction de terminologie");
            
//            String detok = xlate.detokenize("I watched the film \" The lives of Others ' \" the other night .");

            
            logger.info(detok);
        }
        catch (Exception e)
        {
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
