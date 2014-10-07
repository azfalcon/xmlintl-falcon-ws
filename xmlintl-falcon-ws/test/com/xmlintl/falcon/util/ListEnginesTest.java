package com.xmlintl.falcon.util;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;


public class ListEnginesTest
{
    Logger logger = Logger.getLogger(ListEnginesTest.class);

    @Before
    public void setUp() throws Exception
    {
    }

    @Test
    public final void test()
    {
        try
        {
            ListEngines listEngines = new ListEngines();
            
            ArrayList<String> list = listEngines.list();
            
            for (String engine: list)
            {
                logger.info(engine);
            }
            
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

}
