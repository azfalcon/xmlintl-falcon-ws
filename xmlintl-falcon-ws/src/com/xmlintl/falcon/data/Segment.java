/********************************************
 *
 * Copyright (c) 2003-2014 XML-INTL Ltd.
 *
 * All Rights Reserved
 *
 ********************************************/

package com.xmlintl.falcon.data;

import it.uniroma1.lcl.jlt.util.Language;

import java.util.ArrayList;
import java.util.HashSet;

import com.xmlintl.falcon.util.FalconUtil;

/**
 * The segment object.
 * 
 * @author $Author: azydron $
 * @version $Revision: 55150 $, $Date: 2015-08-14 10:40:40 +0100 (Fri, 14 Aug 2015) $
 *
 */
public class Segment implements Cloneable
{
    /** Flag to indicate that we are not quite sure of the match for this segment. */
    private boolean                 notSure;
    /** Flag to indicate that a source segment shoudl be merged with the previous source segment. */
    private boolean                 merged;
    /** The full original form of the segment. */
    private String                  full;
    /** The tokenized segment text. */
    private String                  tokenized;
    /** The full lower case form of the segment. */
    private String                  fullLc;
    /** The stemmed version of the segment. */
    private String                  stemmed;
    /** The Word array of full words. */
    private Word[]                  words;
    /** The Word array of UNK words. */
    private Word[]                  unkWords = new Word[0];
    /** The count of significant words. */
    private int                     sigWordCount;
    /** The segment number. */
    private int                     segmentNumber;
    /** THe matching segment number (alignment). */
    private int                     matchingSegNo       = -1; // -1 = not matched.
    /** List of numerics, these can be part of words as in '$8m and above' and '8 millions $ et plus' or '3.5' and '3,5' . */
    private Word[]                  numerics;
    /** The merged segment number. */
    private int                     mergedSegmentNumber = -1;
    /** The logarithmic length factor for the segment. */
    private double                  lengthFactor;
    /** The match probability. */
    private double                  probability;
    /** The langauge of the segment. */
    private Language                language;
    /** Is this segment Chinese. */
    private boolean                 cjkText;
    /** Does the segment contain non-lating characters. */
    private boolean                 nonLatinText;
    /** Flag to indicate that the text is all in upper case. */
    private boolean                 allUpperCase;
    /** Flag to indicate that this match is out of scope. */
    private boolean                 outOfScopeMatched;
    /** Flag to indicate that the segment has been printed. */
    private boolean                 printed;

    /** THe number of words in the segment. */
    private int                     noWords;

    /**
     * Constructor.
     * @param stemmer The stemmer object.
     * @param text The segment itself.
     * @param stopwords The list of stopwords.
     * @param segmentNumber The segment number.
     * @param lang The segment language.
     */
    public Segment(String text, HashSet<String> stopwords, Language lang)
    {
        this.language = lang;

        String isoLang = lang.toString().toLowerCase();

        allUpperCase = FalconUtil.allUpperCase(text);

        this.full = text;

        tokenized = FalconUtil.normalizeText(text);

        String lc = text.replace('-', ' ');

        this.fullLc = lc.toLowerCase();
        
        if ((FalconUtil.isCJKLanguage(isoLang)) && (FalconUtil.containsCJKChars(text)))
        {
            cjkText = true;
        }
        
        nonLatinText = FalconUtil.mainlyNonLatinChars(text);
        
        if (cjkText)
        {
            this.words = populateCJKWords(tokenized);
        }
        else
        {
            this.words = populateWords(stopwords, tokenized);
        }
        
        findUNKwords();

        this.numerics = findNumerics();

        if (cjkText) // CJK target files can contain Latin text.
        {
            switch (lang)
            // Calculate the length factors. For CJK + Thai use the GMX/V word count factor to work out the number of words.
            {
                case ZH:
                    noWords = (int) (full.length() / 2.5);
                    break;
                case JA:
                    noWords = (int) (full.length() / 3.0);
                    break;
                case KO:
                    noWords = (int) (full.length() / 3.3);
                    break;
                case TH:
                    noWords = (int) (full.length() / 6.0);
                    break;
                default:
                    noWords = words.length;
            }
        }
        else
        {
            noWords = words.length;
        }

        this.lengthFactor = calculateFactor(noWords);
    }
    /**
     * find unknown words.
     */
    private void findUNKwords()
    {
        ArrayList<Word> unks = new ArrayList<Word>();
        
        for (Word word: words)
        {
            if (word.isUnkWord())
            {
                unks.add(word);
            }
        }
        
        unkWords = unks.toArray(unkWords);
    }
    /**
     * Locate all of the numberic values which can be embedded within a word as per: '$4m', or 11x3.
     * @return The list of numberic values.
     */
    private Word[] findNumerics()
    {
        ArrayList<String> numericValues = new ArrayList<String>();

        for (Word word : words)
        {

            ArrayList<String> numbers = findNumbers(word.getTheLCWord());

            if (numbers.size() != 0)
            {
                numericValues.addAll(numbers);
            }
        }

        Word[] numbers = new Word[numericValues.size()];

        for (int i = 0, len = numericValues.size(); i < len; i++)
        {
            numbers[i] = new Word(numericValues.get(i));
        }

        return numbers;
    }
    /**
     * Add a numeric word to numerics array.
     * @param numericWord The new numeric wird.
     */
    public void addNumericWord(Word numericWord)
    {
        Word[] newNumerics = new Word[numerics.length + 1];
        
        for (int i = 0, len = numerics.length; i < len; i++)
        {
            newNumerics[i] = numerics[i];
        }
        
        newNumerics[numerics.length] = numericWord;
        
        numerics = newNumerics;
    }

    /**
     * Find any numbers in the word as per '11x3', or '$4m'. 
     * @param word The word as a string.
     * @return The array list of numbers.
     */
    private ArrayList<String> findNumbers(String word)
    {
        ArrayList<String> numbers = new ArrayList<String>();

        boolean numbStart = false;

        StringBuilder numbBuff = null;

        for (int i = 0, len = word.length(); i < len; i++)
        {
            char c = word.charAt(i);

            if (Character.isDigit(c))
            {
                if (numbStart == false)
                {
                    numbStart = true;
                }

                if (numbBuff == null)
                {
                    numbBuff = new StringBuilder();
                }

                numbBuff.append(c);
            }
            else
            {
                if ((FalconUtil.isPunctuation(c) == false) && (numbStart == true)) // Ignore punctuation chars - they are irrelevant for the purposes of numberic matching
                {
                    numbStart = false;

                    numbers.add(numbBuff.toString());

                    numbBuff = null;
                }
            }
        }

        if (numbBuff != null)
        {
            numbers.add(numbBuff.toString());
        }

        return numbers;
    }

    /**
     * Populate CJK words which may contain Latin words and numbers etc.
     * @param text The text string.
     * @return The array of words.
     */
    private Word[] populateCJKWords(String text)
    {
        Word[] theWords = new Word[0];
        
        ArrayList<Word> cjkWords = new ArrayList<Word>();
        
        int i = 0;
        int sw = 0;
        char prevChar = '\u0e00';
        
        for (int len = text.length(); i < len; i++)
        {
            char c = text.charAt(i);
            
            if ((((!FalconUtil.isCJKChar(c)) && (FalconUtil.isCJKChar(prevChar))) || // Change of text type
                ((FalconUtil.isCJKChar(c)) && (!FalconUtil.isCJKChar(prevChar)))) ||
                ((c == ' ' ) && (prevChar != ' ')) || // Space separator.
                ((FalconUtil.isPunctuation(c)) && (!FalconUtil.isPunctuation(prevChar))) || // Punctuation chars
                ((!FalconUtil.isPunctuation(c)) && (FalconUtil.isPunctuation(prevChar))))
            {
                storeWord(text, cjkWords, i, sw);
                
                sw = i;
            }
                
            prevChar = c;
        }
        
        storeWord(text, cjkWords, i, sw);
        
        theWords = cjkWords.toArray(theWords);
        
        return theWords;
    }

    /**
     * Store the word.
     * @param text The text containing the word.
     * @param cjkWords The array list in which to store the words.
     * @param endPos The end position.
     * @param startPos The start position.
     */
    private void storeWord(String text, ArrayList<Word> cjkWords, int endPos, int startPos)
    {
        String wordStr = text.substring(startPos, endPos).trim();
        
        if (!("".equals(wordStr)))
        {
            Word word = new Word(wordStr);
            
            cjkWords.add(word);
        }
    }
    /**
     * Populate the word array from the segment.
     * @param stopwords THe list of stopwords.
     * @param segment The segment string representation.
     * @return The array of words created from this segment string.
     */
    private Word[] populateWords(HashSet<String> stopwords, String segment)
    {
        String[] theWords = segment.split("\\s");

        sigWordCount = 0;

        Word[] words = new Word[0];

        ArrayList<Word> wordList = new ArrayList<Word>(theWords.length);

        for (int i = 0, len = theWords.length; i < len; i++)
        {
            String fullWord = theWords[i].trim();
            
            if ("".equals(fullWord))
            {
                continue;
            }

            String lcWord = fullWord.toLowerCase();

            Word theWord = new Word(fullWord);

            boolean isPunct = false;

            /*if (stopwords.contains(lcWord))
            {
                theWord.setStopWord(true);
            }
            else */ if (isPunct == false)
            {
                sigWordCount++;
            }

            wordList.add(theWord);
        }

        words = wordList.toArray(words);

        return words;
    }

    /**
     * Find the match penalty value.
     * @param wordsInSegment The words in text segment.
     * @return The match penalty value.
     */
    private double calculateFactor(int wordsInSegment)
    {
        Double penaltyMultiplier = 1.0;

        Double noOfWordsDouble = new Double(wordsInSegment);

        if (wordsInSegment < 15)
        {
            penaltyMultiplier = 0.05 + (noOfWordsDouble / 15);
        }

        return penaltyMultiplier;
    }

    /**
     * Get the full.
     * @return the full.
     */
    public String getFull()
    {
        return full;
    }

    /**
     * Get the fullLc.
     * @return the fullLc.
     */
    public String getFullLc()
    {
        return fullLc;
    }

    /**
     * Get the stemmed.
     * @return the stemmed.
     */
    public String getStemmed()
    {
        return stemmed;
    }

    /**
     * Get the lowercase fullWords.
     * @return the fullWords.
     */
    public Word[] getWords()
    {
        return words;
    }

    /**
     * Get the sigWordCount.
     * @return the sigWordCount.
     */
    public int getSigWordCount()
    {
        return sigWordCount;
    }

    /**
     * Get the segmentNumber.
     * @return the segmentNumber.
     */
    public int getSegmentNumber()
    {
        return segmentNumber;
    }

    /**
     * Get the matchingSegNo.
     * @return the matchingSegNo.
     */
    public int getMatchingSegNo()
    {
        return matchingSegNo;
    }

    /**
     * Set the matchingSegNo.
     * @param matchingSegNo the matchingSegNo to set.
     */
    public void setMatchingSegNo(int matchingSegNo)
    {
//        if (matchingSegNo == 643)
//        {
//            System.out.println("Strop");
//        }
        
        this.matchingSegNo = matchingSegNo;
    }

    /**
     * Get the notSure.
     * @return the notSure.
     */
    public boolean isNotSure()
    {
        return notSure;
    }

    /**
     * Set the notSure.
     */
    public void setNotSure()
    {
        this.notSure = true;
    }

    /**
     * Set the segmentNumber.
     * @param segmentNumber the segmentNumber to set.
     */
    public void setSegmentNumber(int segmentNumber)
    {
        this.segmentNumber = segmentNumber;
    }

    /**
     * Get the mergedSegmentNumber.
     * @return the mergedSegmentNumber.
     */
    public int getMergedSegmentNumber()
    {
        return mergedSegmentNumber;
    }

    /**
     * Set the mergedSegmentNumber.
     * @param mergedSegmentNumber the mergedSegmentNumber to set.
     */
    public void setMergedSegmentNumber(int mergedSegmentNumber)
    {
        this.mergedSegmentNumber = mergedSegmentNumber;
    }

    /**
     * Get the lengthFactor.
     * @return the lengthFactor.
     */
    public double getLengthFactor()
    {
        return lengthFactor;
    }

    /**
     * Get the length of the full segment string.
     * @return The length of the full segment string.
     */
    public int getFullLen()
    {
        return full.length();
    }

    /**
     * Get the full word count.
     * @return The full word count.
     */
    public int getFullWc()
    {
        return words.length;
    }

    /**
     * Get the probability.
     * @return the probability.
     */
    public double getProbability()
    {
        return probability;
    }

    /**
     * Set the probability.
     * @param probability the probability to set.
     */
    public void setProbability(double probability)
    {
        this.probability = probability;
    }

    /**
     * Get the mergedSrc.
     * @return the mergedSrc.
     */
    public boolean isMerged()
    {
        return merged;
    }

    /**
     * Set the mergedSrc.
     */
    public void setMerged()
    {
        this.merged = true;
    }

    /**
     * Set the full.
     * @param full the full to set.
     */
    public void setFull(String full)
    {
        this.full = full;
    }

    /**
     * Get the numerics.
     * @return the numerics.
     */
    public Word[] getNumerics()
    {
        return numerics;
    }

    /**
     * Allow cloning.
     */
    @Override
    public Object clone() throws CloneNotSupportedException
    {
        return super.clone();
    }

    /**
     * Get the noWords.
     * @return the noWords.
     */
    public int getNoWords()
    {
        return noWords;
    }

    /**
     * Get the language.
     * @return the language.
     */
    public Language getLanguage()
    {
        return language;
    }

    /**
     * Get the cjkText.
     * @return the cjkText.
     */
    public boolean isCjkText()
    {
        return cjkText;
    }

    /**
     * Set the merged.
     * @param merged the merged to set.
     */
    public void setMerged(boolean merged)
    {
        this.merged = merged;
    }
    /**
     * Does the segment contain non-latin alphabet characters. 
     * @return True if the segment contain non-latin alphabet characters.
     */
    public boolean isNonLatinText()
    {
        return nonLatinText;
    }

    /**
     * Is this segment matched out of scope.
     * @return the outOfScopeMatched value.
     */
    public boolean isOutOfScopeMatched()
    {
        return outOfScopeMatched;
    }

    /**
     * Set as out of scope match.
     */
    public void setOutOfScopeMatched()
    {
        this.outOfScopeMatched = true;
    }

    /**
     * Has the segment been output.
     * @return True if it has.
     */
    public boolean isPrinted()
    {
        return printed;
    }

    /**
     * Set the printed flag.
     */
    public void setPrinted()
    {
        this.printed = true;
    }

    /**
     * Is this an all uppercasee segment.
     * @return return true if the segment is all uppercase.
     */
    public boolean isAllUpperCase()
    {
        return allUpperCase;
    }
    /**
     * Get the unkWords.
     * @return the unkWords.
     */
    public Word[] getUnkWords()
    {
        return unkWords;
    }
    /**
     * Return the number of unknown words.
     * @return The number of unknown words.
     */
    public int noUNKwords()
    {
        return unkWords.length;
    }
    /**
     * Get the tokenized.
     * @return the tokenized.
     */
    public String getTokenized()
    {
        return tokenized;
    }

}
