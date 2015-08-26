/********************************************
 *
 * Copyright (c) 2003-2014 XML-INTL Ltd.
 *
 * All Rights Reserved
 *
 ********************************************/

package com.xmlintl.falcon.data;

import static com.xmlintl.falcon.util.CommonDefines.UNK;

import java.io.Serializable;

import com.xmlintl.falcon.util.FalconUtil;


/**
 * The word object.
 * 
 * @author $Author: azydron $
 * @version $Revision: 55152 $, $Date: 2015-08-14 10:42:17 +0100 (Fri, 14 Aug 2015) $
 *
 */
public class Word implements Serializable
{
    /** The serial Version UID. */
    private static final long serialVersionUID = -3101237960701367187L;
    /** The word itself. */
    private String theWord;
    /** The UNK word. */
    private String theUNKWord;
    /** The lower case version of the word. */
    private String theLCWord;
    /** Has this word been matched. */
    private boolean matched;
    /** Is this word a stop word. */
    private boolean stopWord;
    /** Is this just a punctuation character. */
    private boolean punctuation;
    /** Is this a numeric value. */
    private boolean numeric;
    /** Is this a non-Latin alphabet word. */
    private boolean nonLatin;
    /** This a UNK OOV words. */
    private boolean unkWord;
    /** Is this a CJK word. */
    private boolean cjk;
    /** The matching target word if any. */
    private Word matchingTgtWord;
    
    /**
     * Constructor.
     * 
     * @param theWord The word.
     */
    public Word(String theWord)
    {
        this.theWord = theWord;
        
        if (theWord.startsWith(UNK))
        {
            unkWord = true;
            
            this.theWord = theWord.substring(3);
            
            this.theUNKWord = theWord;
        }
        
        theLCWord = this.theWord.toLowerCase();
        
        if (this.theWord.length() == 1)
        {
            char c = theWord.charAt(0);
            
            punctuation = FalconUtil.isPunctuation(c);
        }
    }
    /**
     * Get the matched.
     * @return the matched.
     */
    public boolean isMatched()
    {
        return matched;
    }

    /**
     * Set the matched.
     * @param matched the matched to set.
     */
    public void setMatched(boolean matched)
    {
        this.matched = matched;
    }

    /**
     * Get the stopWord.
     * @return the stopWord.
     */
    public boolean isStopWord()
    {
        return stopWord;
    }

    /**
     * Set the stopWord.
     * @param stopWord the stopWord to set.
     */
    public void setStopWord(boolean stopWord)
    {
        this.stopWord = stopWord;
    }

    /**
     * Get the theWord.
     * @return the theWord.
     */
    public String getTheWord()
    {
        return theWord;
    }

    /**
     * Get the theWordLC.
     * @return the theWordLC.
     */
    public String getTheLCWord()
    {
        return theLCWord;
    }
    /**
     * To Sttring.
     * @return THe String version of the object.
     */
    @Override
    public String toString()
    {
        return theWord;
    }

    /**
     * Get the punctuation.
     * @return the punctuation.
     */
    public boolean isPunctuation()
    {
        return punctuation;
    }

    /**
     * Get the numeric.
     * @return the numeric.
     */
    public boolean isNumeric()
    {
        return numeric;
    }
    /**
     * Get the non-Latin flag.
     * @return the nonLatin flag.
     */
    public boolean isNonLatin()
    {
        return nonLatin;
    }
    /**
     * Is this a CJK word.
     * @return the cjk flag.
     */
    public boolean isCjk()
    {
        return cjk;
    }
    /**
     * Get the other language matching word.
     * @return the matchingTgtWord.
     */
    public Word getMatchingTgtWord()
    {
        return matchingTgtWord;
    }
    /**
     * Set the other language matching word.
     * @param matchingTgtWord the matchingTgtWord to set.
     */
    public void setMatchingTgtWord(Word matchingTgtWord)
    {
        this.matchingTgtWord = matchingTgtWord;
    }
    /**
     * Set the word value.
     * @param theWord the theWord to set.
     */
    public void setTheWord(String theWord)
    {
        this.theWord = theWord;
    }
    /**
     * Set the numeric flag value.
     * @param numeric the boolean numeric flag value to set.
     */
    public void setNumeric(boolean numeric)
    {
        this.numeric = numeric;
    }
    /**
     * Get the unkWord.
     * @return the unkWord.
     */
    public boolean isUnkWord()
    {
        return unkWord;
    }
    /**
     * Set the unkWord.
     * @param unkWord the unkWord to set.
     */
    public void setUnkWord(boolean unkWord)
    {
        this.unkWord = unkWord;
    }
    
    
}
