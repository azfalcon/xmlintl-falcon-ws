/********************************************
 *
 * Copyright (c) 2003-2014 XML-INTL Ltd.
 *
 * All Rights Reserved
 *
 ********************************************/

package com.xmlintl.falcon.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Static utility class for the Falcon WS project.
 *
 * @author $Author: azydron $
 * @version $Revision: 46553 $, $Date: 2014-09-26 10:04:20 +0100 (Fri, 26 Sep 2014) $
 */
public class FalconUtil
{
    /**
     * privatre constructor.
     */
    private FalconUtil() {}
    
    /** List of punctuation characters. */
    public static final String   PUNCTUATION             = ".,:;!¡?¿«»”‘’‛‟\"'′″´‚„˝°¨`˙˚…_¯­–‑—-(){}[]/&";
    
    public static final String   CLOSE_PUNCTUATION       = ".,:;!¡?»”‘’‛‟\"′″´˚)}]";
    
    public static final String OPEN_PUNCTUATION          = "({[\"'";
    
    public static final String[][] ENTS =
    {
        { "&lt;", "<" },
        { "&gt;", ">" },
        { "&quot;", "\"" },
        { "&apos", "'" },
        { "&amp;", "&" }
        
    };
    /**
     * Generate a UUID.
     * @return The UUID as a string.
     */
    public static String getUUID()
    {
        UUID uuid = UUID.randomUUID();
        
        return uuid.toString();
    }
    /**
     * Get the language portion of the locale code, e.g. en_US -> en.
     * @param locale The original locale code.
     * @return The language element of the locale.
     */
    public static String languageOnly(String locale)
    {
        String lang = null;
        
        if (locale.length() > 3)
        {
            lang = locale.substring(0, 2);
        }
        else if (locale.length() == 2)
        {
            lang = locale;
        }
        
        return lang;
    }
    /**
     * Load the stop word list for this language.
     * @param stopListFile The stop list file.
     * @throws IOException If any errors are encountered.
     */
    private static HashSet<String> readStopWords(File stopListFile) throws IOException
    {
        HashSet<String> stopwords = new HashSet<String>();

        BufferedReader stopListReader = new BufferedReader(new FileReader(stopListFile));

        String stopWord;

        while ((stopWord = stopListReader.readLine()) != null)
        {
            stopWord = stopWord.trim();

            if ((stopWord.length() == 0) || stopWord.startsWith("//"))
            {
                continue;
            }

            stopWord = stopWord.toLowerCase();

            stopwords.add(stopWord);
        }

        stopListReader.close();

        return stopwords;
    }

    /**
     * Strip out all inline elements except for &lt;term>.
     * @param segment The segment text.
     * @return The normalized text.
     * @throws FalconException If anything goes arwy.
     */
    public static String normalizeSegment(String segment) throws FalconException
    {
        String normalizedSegment = null;
        
        String xmlString = "<seg>" + segment + "</seg>";
        
        Document doc = readDocumentFromString(xmlString, false);
        
        String text = FalconUtil.createTextElementOnlyForm(doc.getDocumentElement());
        
        normalizedSegment = stripEntities(text);
        
        return normalizedSegment;
    }
    /**
     * Given the XML document contents as a String - read in the xml doc into a DOM
     * structure.
     * 
     * @return The Document object.
     * @param xmlContent
     *            - the actual XML contents as a String.
     * @param nonamespace
     *            Flag to indicate if we want namespace recognition or not.
     * @throws FalconException
     *             If any errors are encountered.
     */
    public static Document readDocumentFromString(String xmlContent, boolean nonamespace) throws FalconException
    {
        Document theDoc = null;
        
        try
        {
            StringReader strReader = new StringReader(xmlContent);
            InputSource inSrc = new InputSource(strReader);
            
            ParserPool pp = ParserPool.getInstance();
            DocumentBuilder builder = nonamespace ? pp.get() : pp.getNS();
            
            try
            {
                theDoc = builder.parse(inSrc);
            }
            finally
            {
                pp.free(builder);
            }
        }
        catch (IOException err)
        {
            throw new FalconException(err);
        }
        catch (SAXException err)
        {
            throw new FalconException(err);
        }
        catch (ParserConfigurationException err)
        {
            throw new FalconException(err);
        }
        catch (NullPointerException err)
        {
            throw new FalconException(err);
        }
        
        return theDoc;
    }

    /**
     * Stripp out XML entity refs.
     * @param text The original text.
     * @return Cleaned text.
     */
    public static String stripEntities(String text)
    {
        String cleanText = text;
        
        if (text != null)
        {
            for (int i = 0, len = ENTS.length; i < len; i++)
            {
                cleanText = cleanText.replaceAll(ENTS[i][0], ENTS[i][1]);
            }
        }
        
        return cleanText;
    }
    /**
     * Create a text only form of the Element.
     * 
     * @param initialElement
     *            The initial element.
     * @return The canonical form.
     */
    public static String createTextElementOnlyForm(Element initialElement)
    {
        List<String> textNodes = findAllTextNodes(initialElement, false);
        StringBuffer buff = new StringBuffer();
        
        for (String text : textNodes)
        {
            buff.append(text).append(' ');
        }
        
        String tmp = convertSpecialSpaces2Normal(buff.toString()); // Convert all special width or non-breaking spaces to a single space character
        
        tmp = tmp.replaceAll("&", "&amp;");
        
        tmp = tmp.replace("<", "&lt;");
        
        String text = tmp.replace(">", "&gt;");
        

        return text;
    }
    /**
     * Convert all special spaces to a single space character.
     * @param str The input string.
     * @return The input string with all spaces converted to \u0020.
     */
    public static String convertSpecialSpaces2Normal(String str)
    {
        String cleanStr = str.replaceAll("\\s", " ");
        
//        cleanStr = cleanStr.replaceAll("\\h", " ");
        
        cleanStr = cleanStr.replaceAll("\\s+", " ").trim();
        
        return cleanStr;
    }

    /**
     * Find all of the text strings that has some actual text within this element hierarchy, but skipping <mrk type="term"> elements.
     * 
     * @param topNode
     *            The start node.
     * @param collapseSpaces
     *            True if spaces should be collapsed, false otherwise.
     * @return found text nodes strings.
     */
    public static List<String> findAllTextNodes(Node topNode, boolean collapseSpaces)
    {
        
        //<g id="i4"><mrk id="t2_1">Promoting</mrk> <mrk id="t2_2">Ireland';s</mrk> interests <mrk id="t2_3">and</mrk> values in the world</g>
        List<String> textNodes = new ArrayList<String>();
        NodeList nodes = topNode.getChildNodes();
        
        for (int i = 0, len = nodes.getLength(); i < len; i++)
        {
            Node node = nodes.item(i);
            
            int type = node.getNodeType();
            
            if (type == Node.ELEMENT_NODE)
            {
                String name = node.getNodeName();
                
                if ("term".equals(name))
                {
                    Element element = (Element) node;
                    
                    String transAttribute = element.getAttribute("translation");
                    
                    StringBuilder xmlbuff = new StringBuilder();
                    xmlbuff.append("<term translation=\"").append(transAttribute).append("\">");
                    
                    textNodes.add(xmlbuff.toString());
                    
                    textNodes.addAll(findAllTextNodes(node, collapseSpaces));
                    
                    textNodes.add("</term>");
                }
                else
                {
                    textNodes.addAll(findAllTextNodes(node, collapseSpaces));
                }
            }
            else if (type == Node.TEXT_NODE)
            {
                String text = node.getNodeValue();
                
                if (!"".equals(text))
                {
                    textNodes.add(text);
                }
            }
        }
        
        return textNodes;
    }
    
    /**
     * Normalize the text separating out punctuation characters.
     * @param text The text to be normalized.
     * @return The normalized text.
     */
    public static String normalizeText(String text)
    {
        StringBuilder stemmedBuff = new StringBuilder();

        char lastc = ' ';
        
        boolean termElm = false;

        for (int i = 0, len = text.length(); i < len; i++)
        {
            char c = text.charAt(i);

            if (c == '<')
            {
                if (((i + "<term ".length() < len) && ("<term ".equals(text.startsWith("<term ", i)))) ||
                    ((i + "</term>".length() < len) && ("</term>".equals(text.startsWith("</term>", i)))))
                {
                    termElm = true;
                    stemmedBuff.append(c);
                }
            }
            else if (termElm)
            {
                stemmedBuff.append(c);
                if (c == '>')
                {
                    termElm = false;
                }
            }
            else if (isPunctuation(c))
            {
                stemmedBuff.append(' ').append(c).append(' ');
            }
            else if ((Character.isLetterOrDigit(c)) || (Character.isSpaceChar(c)))
            {
                stemmedBuff.append(c);
            }
            else
            {
                if ((Character.isLetterOrDigit(lastc)) || (Character.isSpaceChar(lastc)))
                {
                    stemmedBuff.append(' ');
                }
                stemmedBuff.append(c);
            }

            lastc = c;
        }

        String normalizedText = stemmedBuff.toString();

        return normalizedText;
    }
    /**
     * Is this a punctuation character.
     * @param c The character being tested.
     * @return True if this is a punctuation character, otherwise false.
     */
    public static boolean isPunctuation(char c)
    {
        boolean punct = false;

        if ((PUNCTUATION.indexOf(c) != -1) || (c >= 0x2000) && (c < 0x2070) || (c >= 0x3000) && (c < 0x303f) || (c >= 0xFF01) && (c <= 0xFFEE)) 
        {
            punct = true;
        }

        return punct;
    }
    /**
     * Is this an all upper case character string.
     * @param text The text.
     * @return True if the text is all in caps.
     */
    public static boolean allUpperCase(String text)
    {
        boolean allUpper = false;
        
        int letterCount = 0;
        int upperCount = 0;
        
        for (int i = 0, len = text.length(); i < len; i++)
        {
            char c = text.charAt(i);
            
            if (Character.isLetter(c))
            {
                letterCount++;
                
                if (Character.isUpperCase(c))
                {
                    upperCount++;
                }
            }
            
            if (letterCount != upperCount)
            {
                break;
            }
            
        }
        
        if ((letterCount > 0) && (letterCount == upperCount))
        {
            allUpper = true;
        }
        
        return allUpper;
    }
    /**
     * Is the langauge Japanes, Chinese or Korean.
     * @param tgtLang The two character language code.
     * @return True if the langauge is CJK.
     */
    public static boolean isCJKLanguage(String tgtLang)
    {
        boolean cjk = false;

        if ((tgtLang.startsWith("ja")) || (tgtLang.startsWith("ko")) || (tgtLang.startsWith("zh")))
        {
            cjk = true;
        }

        return cjk;
    }
    /**
     * Does this string contain CJK characters.
     * @param segString The string to be tested.
     * @return True if the string contains at least one CJK character, otherwise
     *         false.
     */
    public static boolean containsCJKChars(String segString)
    {
        boolean cjk = false;

        for (char c : segString.toCharArray())
        {
            if (isCJKChar(c))
            {
                cjk = true;

                break;
            }
        }

        return cjk;
    }
    /**
     * Is this string made up mainly of non Latin alphabet characters.
     * @param segString The string to be tested.
     * @return True if the string contains at least one non-Latin character, otherwise false.
     */
    public static boolean mainlyNonLatinChars(String segString)
    {
        boolean nonLatin = false;
        
        int noChars = 0;
        int noNonLatinChars = 0;

        for (char c : segString.toCharArray())
        {
            if (c == ' ')
            {
                continue;
            }
            
            noChars++;
            
            if (isNonLatin(c))
            {
                noNonLatinChars++;
            }
        }
        
        if (noNonLatinChars != 0)
        {
            double percNonLatin = calcPerc(noChars, noNonLatinChars);
            
            if (percNonLatin > 50.)
            {
                nonLatin = true;
            }
        }

        return nonLatin;
    }
    /**
     * Calculate the percentage.
     * @param v1 The first value.
     * @param v2 The second value.
     * @return The percentage that the first forms of the second.
     */
    public static double calcPerc(int v1, int v2)
    {
        double perc = ((double) v1 / (double) v2) * 100;

        return perc;
    }

    /**
     * Is this a CJK character.
     * @param c The character.
     * @return True if it is a CJK character, otherwise false.
     */
    public static boolean isCJKChar(char c)
    {
        boolean cjk = false;

        if (c >= 0x2BFF) // Covers from CJK radicals supplement to end.
        {
            cjk = true;
        }

        return cjk;
    }
    /**
     * Is this a non-laatin character.
     * @param c The character.
     * @return True if it is a non-Latin alphabet character.
     */
    public static boolean isNonLatin(char c)
    {
        boolean nonLatin = false;
        
        if ((c > 0x0370) && (!((c > 0x2000) && (c < 0x20A0)))) // > Greek but not general punctuation
        {
            nonLatin = true;
        }
        
        return nonLatin;
    }
    /**
     * Does this word contain non-Latin characters.
     * @param word The word.
     * @return True if it does, otherwise false.
     */
    public static boolean isNonLatin(String word)
    {
        boolean nonLatin = false;
        
        for (int i = 0, len = word.length(); i < len; i++)
        {
            char c = word.charAt(i);
            
            if (isNonLatin(c))
            {
                nonLatin = true;
                break;
            }
        }
        
        return nonLatin;
    }

    /**
     * Is this an RTL language.
     * @param lang The language.
     * @return True if it is, otherwise false.
     */
    public static boolean isRTLLanguage(String lang)
    {
        boolean rtl = false;

        if ((lang.startsWith("ar")) || (lang.startsWith("he")))
        {
            rtl = true;
        }

        return rtl;
    }
    /**
     * Get the list of stopwords.
     * @param locale The locale.
     * @return The list of stopwords.
     * @throws NLPException If any errors are encountered.
     */
    public static HashSet<String> getStopWords(String locale) throws FalconException
    {
        HashSet<String> stopwords = new HashSet<String>();

        try
        {
            String lang = locale.toUpperCase();

            String bilingDictsPath = "resources/stopwords/";

            String stopWordFilePath = null;

            if ("EN".equals(lang))
            {
                stopWordFilePath = bilingDictsPath + "EN.stopWords";
            }
            else
            {
                stopWordFilePath = bilingDictsPath + "/" + "EN-" + lang + "-marker.dict.stopWords";
            }

            File stopList = new File(stopWordFilePath);

            if (stopList.exists()) // Do we have a stop list for this language.
            {
                stopwords = readStopWords(stopList);
            }
         }
        catch (Exception e)
        {
            throw new FalconException(e.getMessage(), e);
        }

        return stopwords;
    }

}
