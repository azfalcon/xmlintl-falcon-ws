/********************************************
 *
 * Copyright (c) 2003-2014 XML-INTL Ltd.
 *
 * All Rights Reserved
 *
 ********************************************/

package com.xmlintl.falcon.util;

import static com.xmlintl.falcon.util.CommonDefines.CREATE_NEW_ENGINE;
import static com.xmlintl.falcon.util.CommonDefines.GET_SERVER_PID;
import static com.xmlintl.falcon.util.CommonDefines.RESTART_SERVER;
import static com.xmlintl.falcon.util.CommonDefines.SCRIPTS_DIR;
import static com.xmlintl.falcon.util.CommonDefines.SEGMENT_DECODE;
import static com.xmlintl.falcon.util.CommonDefines.SMT_ENGINES_ROOT_DIR;
import static com.xmlintl.falcon.util.CommonDefines.START_SERVER;
import it.uniroma1.lcl.babelnet.BabelNet;
import it.uniroma1.lcl.babelnet.BabelNetConfiguration;
import it.uniroma1.lcl.babelnet.BabelSense;
import it.uniroma1.lcl.jlt.Configuration;
import it.uniroma1.lcl.jlt.util.Language;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.xmlintl.falcon.data.Segment;
import com.xmlintl.falcon.data.Word;

/**
 * Translate the segment.
 *
 * @author $Author: azydron $
 * @version $Revision: 46553 $, $Date: 2014-09-26 10:04:20 +0100 (Fri, 26 Sep 2014) $
 */
public class TranslateSegment extends FalconAbstract
{
    /** Hash map of clientName and key values so that we do not have to constantly re-read the uuid files. */
    protected static HashMap<String, String> keyMap = new HashMap<String, String>();
    
    protected String portStr;
    
    protected String clientName;
    
    protected String customerID;
    
    protected String srcLang;
    
    protected String tgtLang;
    
    protected Language babelnetSrcLang;
    
    protected Language babelnetTgtLang;
    
    protected String srcNormalizedText;
    
    protected String translation;
    
    protected String confidenceScore;
    
    protected String uuid;
    
    protected String key;
    
    protected String engineID;
    
    protected Segment srcSegment;
    
    protected Segment tgtSegment;
    
    protected static BabelNet bn;
    
    protected static HashSet<String> engineRestartTable = new HashSet<String>();
    
    public static final String FRENCH = "fr";
    
    /**
     * Constructor.
     * @param clientName The SMT engine ID. 
     * @param customerID The Customer ID
     * @param srcLang The source language.
     * @param tgtLang The target language.
     * @param srcSegmentText The source segment text.
     * @param key The token key value.
     * @param webroot 
     * @throws FalconException If we cannot initialize the Falcon properties environment correctly.
     * @throws FalconEngineRebbotingException 
     */
    public TranslateSegment(String clientName, String customerID, String srcLang, String tgtLang, String srcSegmentText, String key, String webroot) throws FalconException, FalconEngineRebbotingException
    {
        super();
        
        Path workingDirectory=Paths.get(".").toAbsolutePath();
        
        if (bn == null) // Kick off connection to BabelNet.
        {
            logger.info("#############################" + workingDirectory.toString());
            
            Configuration jltConfiguration = Configuration.getInstance();
            jltConfiguration.setConfigurationFile(new File(webroot + "config/jlt.properties"));

            BabelNetConfiguration bnconf = BabelNetConfiguration.getInstance();

            bnconf.setConfigurationFile(new File(webroot + "config/babelnet.properties"));
            
            bnconf.setBasePath(webroot);

            bn = BabelNet.getInstance();
        }
        
        this.clientName = clientName;
        this.customerID = customerID;
        this.srcLang = srcLang;
        this.tgtLang = tgtLang;
        this.srcNormalizedText = FalconUtil.normalizeSegment(srcSegmentText);
        
        this.engineID = clientName + "/" + customerID + "/" + srcLang + "_" + tgtLang;

        if (engineRestartTable.contains(engineID))
        {
            throw new FalconEngineRebbotingException("engine " + engineID + " is being rebooted");
        }
        
        String src = srcLang.toUpperCase();

        babelnetSrcLang = Language.valueOf(src);

        String tgt = tgtLang.toUpperCase();

        babelnetTgtLang = Language.valueOf(tgt);
        
        this.key = key;
        
        if (key != null)
        {
            checkKey(key);
        }
        
        uuid = FalconUtil.getUUID();
        
        srcSegment = new Segment(srcNormalizedText, null, babelnetSrcLang);
    }
    /**
     * Check XTM license key.
     * @param key The key to check.
     * @throws FalconException If wither there is no key, or the keys do not match.
     */
    private void checkKey(String key) throws FalconException
    {
        String testKey = keyMap.get(clientName);
        
        if (testKey == null)
        {
            String enginesDir = properties.getProperty(SMT_ENGINES_ROOT_DIR);
            
            String uuidFileName = enginesDir + "/" + clientName + "/uuid";
            
            File uuidFile = new File(uuidFileName);
            
            StringBuilder uuidBuilder = new StringBuilder();
            
            try
            {
                try (FileInputStream is = new FileInputStream(uuidFile))
                {
                    for (int i = 0; (i = is.read()) != -1;)
                    {
                        char c = (char) i;
                        
                        uuidBuilder.append(c);
                    }
                    
                    testKey = uuidBuilder.toString();
                    
                    keyMap.put(clientName, testKey);
                }
            }
            catch (FileNotFoundException e)
            {
                throw new FalconException("Client: " + clientName + " uuid file: " + uuidFileName + " is missing");
            }
            catch (IOException e)
            {
                throw new FalconException(e.getMessage(), e);
            }
        }
        
        if (!(key.equals(testKey)))
        {
            throw new FalconException("Client key: " + testKey + " does not match web service call key: " + key);
        }

    }
    /**
     * Is the server running.
     * @return True if it is, otherwise false.
     * @throws FalconException 
     */
    private String getServerPID() throws FalconException
    {
        String pid = null;

        String scriptsDir = properties.getProperty(SCRIPTS_DIR);

        String execScript = scriptsDir + GET_SERVER_PID;

        //ps aux | grep -w ${1}_${2}_${3}.pl | grep -v grep | awk '{print $2}

        logger.info("Invoking command: " + execScript + " " + clientName + " " + customerID + " " + srcLang + "_" + tgtLang);

        ProcessBuilder pb = new ProcessBuilder(execScript, clientName, customerID, srcLang + "_" + tgtLang);
        
        pid = executeScript(execScript, pb, 3000);
        
        if (pid != null)
        {
            if (pid.isEmpty())
            {
                pid = null;
            }
            else
            {
                int c = pid.charAt(0);
                
                logger.info("PID = " + pid + " len=" + pid.length() + " " + c );
                
                if ((pid.length() == 1) && (c == 10))
                {
                    pid = null;
                }
            }
        }

        return pid;
    }
    /**
     * Restart the engine.
     * @throws FalconException If we cannot restart the engine.
     */
    public void checkEngine() throws FalconException
    {
        logger.info("Checking if server is running");// First check if the server is running
        
        if (engineRestartTable.contains(engineID))
        {
            logger.info("engine: " + engineID + " is currently being restarted");
        }
        else
        {
            String pid = getServerPID();
            
            if (pid != null)
            {
                restartEngine(true);
            }
            else // no server running
            {
                restartEngine(false);
            }
        }
    }
    
    /**
     * Restart the engine.
     */
    public void restartEngine(boolean restart)
    {
        
        String scriptsDir = properties.getProperty(SCRIPTS_DIR);
        
        String execScript = scriptsDir + RESTART_SERVER;
        
        String restarting = "########### Restarting";
        if (restart == false)
        {
            execScript = scriptsDir + START_SERVER;
            restarting = "$$$$$$$$$$$$$ Starting";
        }
            
        logger.info(restarting + " engine for: " + clientName + " " + customerID + " " + srcLang + "_" + tgtLang + " " + portStr);
 
        // /usr/local/share/SMT/scripts/restart_moses_server.sh Falcon 2147 en_fr
        
        logger.info("Invoking: " + execScript + " " + clientName + " " + customerID + " " + srcLang + "_" + tgtLang + " " + portStr);
        
        ProcessBuilder pb = null;
        
        if (restart == false)
        {
            pb = new ProcessBuilder(execScript, clientName, customerID, srcLang + "_" + tgtLang, portStr);
        }
        else
        {
            pb = new ProcessBuilder(execScript, clientName, customerID, srcLang + "_" + tgtLang);
        }
        
        if (engineRestartTable.contains(engineID))
        {
            logger.info("engine: " + engineID + " is currently being restarted");
        }
        else
        {
            synchronized (engineRestartTable)
            {
                engineRestartTable.add(engineID);

                try
                {
                    executeScript(execScript, pb, 300000);
                }
                catch (FalconException e)
                {
                    logger.error(e.getMessage());

                    if (restart) // Failed to restart - try start
                    {
                        restartEngine(false);
                    }
                }
                finally
                {
                    engineRestartTable.remove(engineID);
                }
            }
        }

    }
    /**
     * Execute a bash shell script.
     * @param execScript The script.
     * @param pb The Process Builder object.
     * @param timeout The timeout in milliseconds.
     * @throws FalconException If something goes wrong.
     */
    public String executeScript(String execScript, ProcessBuilder pb, int timeout) throws FalconException
    {
        String shellOutput = null;
        
        InputStream is = null;
        
        try
        {
            Process shell = pb.start();

            is = shell.getInputStream();
            
            shellOutput = readInputStreamWithTimeout(is, timeout); // 5 mins timeout
            
            if (shellOutput != null)
            {
                shellOutput = shellOutput.replace("\n", "");
            }
            
            int shellExitStatus = shell.exitValue();
            
            if (shellExitStatus != 0)
            {
                throw new FalconException("Failed to run: " + execScript + " : " + shellOutput);
            }
            
            List<String> cmds = pb.command();
            
            StringBuilder buff = new StringBuilder();
            
            for (String cmd: cmds)
            {
                buff.append(cmd).append(' ');
            }
            
            logger.info("###########Script " + buff.toString().trim() + " successfully completed");
        }
        catch (IOException e)
        {
            throw new FalconException(e.getMessage(), e);
        }
        finally
        {
            if (is != null)
            {
                try
                {
                    is.close();
                }
                catch (IOException e)
                {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        
        return shellOutput;
    }
    
    private void createNewEngine() throws FalconException
    {
        String output = null;

        String scriptsDir = properties.getProperty(SCRIPTS_DIR);

        String execScript = scriptsDir + CREATE_NEW_ENGINE; 

        //ps aux | grep -w ${1}_${2}_${3}.pl | grep -v grep | awk '{print $2}

        logger.info("Invoking command: " + execScript + " " + clientName + " " + customerID + " " + srcLang + " " + tgtLang);

        ProcessBuilder pb = new ProcessBuilder(execScript, clientName, customerID, srcLang, tgtLang);
        
        synchronized (engineRestartTable)
        {
            engineRestartTable.add(engineID);
        }
        
        try
        {
            output = executeScript(execScript, pb, 30000);
        }
        catch (Exception e)
        {
            throw new FalconException(e.getMessage(), e);
        }
        
        finally
        {
            synchronized (engineRestartTable)
            {
                engineRestartTable.remove(engineID);
            }
        }
        
        log(output);

    }
    /**
     * Open the socket to the appropriate moses decoder server.
     * @return The socket.
     * @throws FalconServerNotRunningException If any errors occurr.
     * @throws FalconException If we cannot create a new engine if it is not there.
     */
    private Socket openSocket() throws FalconException, FalconServerNotRunningException
    {
        Socket socket = new Socket();
        
        String engineDir = properties.getProperty(SMT_ENGINES_ROOT_DIR);
        
        String portFileName = engineDir + engineID + "/port";
        
        File portFile = new File(portFileName);
        
        if (!portFile.exists()) // No port file - maybe no engine at all...
        {
            File parent = portFile.getParentFile();
            
            if (!parent.exists()) // Yep - must be a call to an engine that does not exist
            {
                createNewEngine();
            }
        }
        
        try (FileInputStream in =  new FileInputStream(portFile))
        {
            byte[] buffer = new byte[4];
            
            if (in.read(buffer) != -1)
            {
                portStr = new String(buffer);
                
                int port = Integer.parseInt(portStr);
                
                InetSocketAddress endpoint = new InetSocketAddress("37.187.134.20", port);
                
                for (int i = 0; i < 50; i++)
                {
                    try
                    {
                        socket.connect(endpoint, 5000); //1 secs timeout for connection.
                        
                        socket.setSoTimeout(15000); // 15 secs timeout for read
                        
                        break;
                    }
                    catch (ConnectException e)
                    {
                        if ((i < 49) && ("Connection refused".equals(e.getMessage())))
                        {
                            Thread.sleep(1000);
                            continue;
                        }
                        else
                        {
                            throw new FalconServerNotRunningException("Could not open port: " + portStr);
                        }
                    }
                }
                
                logger.info("Opened new socket on port: " + port);
            }
            else
            {
                 throw new FalconServerNotRunningException("Could not read: " + portFileName);
            }
            
         }
        catch (Exception e)
        {
            throw new FalconServerNotRunningException(e.getMessage(), e);
        }
        
        return socket;
    }
    /**
     * Do the translating.
     * @return The translated text or an empty string if none avaliable.
     * @throws FalconException If any errors are encountered.
     * @throws FalconServerNotRunningException If we time out trying to read from the decoder output.
     * @throws FalconException If we cannot create a missing engine.
     */
    public String translate() throws FalconServerNotRunningException, FalconException
    {
        // XTM: <x id="x460"/><term translation="zapiekanka_translation">zapiekanka</term> z warzyw<x id="x461"/> â»<x id="x462"/><x id="x463"/>
        
        Socket socket = openSocket();
        
        Charset cs = Charset.forName("UTF-8");
        
        try (BufferedWriter bos = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), cs)); BufferedReader is = new BufferedReader(new InputStreamReader(socket.getInputStream()));)
        {
            String tokenizedText = srcSegment.getTokenized();
            
            logger.info("Writing: " + tokenizedText + " to socket: " + socket.getPort());
            
//            byte[] bytes = tokenizedText.getBytes("UTF8");
            
            bos.write(tokenizedText);
            
            bos.newLine();
            
            bos.flush();

            logger.info("waiting for the decoder output");

            char c;

            StringBuilder buff = new StringBuilder();

            for (int i = 0; (c = (char) is.read()) != -1; i++)
            {
//                logger.info("read: " + c + " " + (int) c);
                
                if ((i == 2048) || (c == '\n'))
                {
                    break;
                }
                
                buff.append(c);
            }

            String xlate = buff.toString();
            
//            String xlate = is.readLine();

            if (xlate == null)
            {
                throw new FalconServerNotRunningException("Decoder not working");
            }

            logger.info("decoder output: " + xlate);

            translation = detokenize(xlate);

            logger.info("detokenized output: " + translation);
        }
        catch (IOException e)
        {
            throw new FalconServerNotRunningException(e.getMessage(), e);
        }

        return translation;
    }
        
    public String detokenize(String text)
    {
        Segment tgtSegment = new Segment(text, null, babelnetTgtLang);
        
        Word[] words = tgtSegment.getWords();
        
        Word firstSrcWord = srcSegment.getWords()[0];
        
        char c = firstSrcWord.getTheWord().charAt(0);
        
//        boolean startsWithCap = true;
//        
//        if (!Character.isUpperCase(c))
//        {
//            startsWithCap = false;
//        }
        
        Word lastWord = null;
        
        StringBuilder buff = new StringBuilder(text.length());
        
        char openClosePuncChar = ' ';
        
        boolean first = true;
        
        for (Word word: words) // (The cat (feline) sat on the "Burberry" coloured, bright red mat.)
        {
            String wordStr = word.getTheWord();
            
            if (word.isUnkWord())
            {
                wordStr = lookUpInBabelNet(word);
            }
            
            if (first)
            {
                first = false;
                
                c = wordStr.charAt(0);
                
                if ((Character.isLetter(c)) && (Character.isLowerCase(c)))
                {
                    c = Character.toUpperCase(c);
                    
                    wordStr = c + wordStr.substring(1);
                }
            }

            if (word.isPunctuation())
            {
                char punct = wordStr.charAt(0);

                if ((punct == '\'') || (punct == '\u2019'))
                {
                    buff.append(wordStr);
                }
                else if ((FalconUtil.CLOSE_PUNCTUATION.indexOf(punct) != -1) && (FalconUtil.OPEN_PUNCTUATION.indexOf(punct) != -1)) // e.g. "'
                {
                    if (openClosePuncChar == punct) // must be close
                    {
                        buff.append(wordStr).append(' ');

                        openClosePuncChar = ' ';
                    }
                    else
                    // must be open
                    {
                        openClosePuncChar = punct;

                        buff.append(' ').append(wordStr);
                    }
                }
                else if (FalconUtil.CLOSE_PUNCTUATION.indexOf(punct) != -1) // 'e.g. .,)]} etc.'
                {
                    buff.append(wordStr);
                }
                else
                // OPEN_PUNCTUATION, e.g. ({[
                {
                    buff.append(' ').append(wordStr);
                }
            }
            else if (lastWord != null)
            // ' cat'
            {
                if (lastWord.isPunctuation())
                {
                    char lp = lastWord.getTheWord().charAt(0);
                    
                    if (FalconUtil.OPEN_PUNCTUATION.indexOf(lp) != -1)
                    {
                        buff.append(wordStr);
                        
                        lastWord = word;
                        
                        continue;
                    }
                }
                
                buff.append(' ').append(wordStr);
            }
            else
            {
                buff.append(' ').append(wordStr);
            }

            lastWord = word;
        }
        
        String detokText = FalconUtil.stripEntities(buff.toString().trim());
        
        return detokText;
    }

    /**
     * Look up the word in BabelNet.
     * @param word The word to look up.
     * @return The BabelNet translation or the same word if none could be found.
     */
    public String lookUpInBabelNet(Word word)
    {
        String wordStr = word.getTheWord(); 

        if (!FalconUtil.allUpperCase(wordStr)) // Don't look up all Upper case words
        {
            String original = wordStr;
            
            if (!word.isPunctuation()) // not a punctuation char
            {
                List<BabelSense> senses = bn.getSenses(babelnetSrcLang, wordStr);

                for (BabelSense sense : senses)
                {
                    if (babelnetTgtLang == sense.getLanguage())
                    {
                        wordStr = sense.getLemma();
                        
                        char firstLetter = original.charAt(0);
                        
                        if (Character.isUpperCase(firstLetter))
                        {
                            char fc = wordStr.charAt(0);
                            
                            fc = Character.toUpperCase(fc);
                            
                            StringBuilder newStr = new StringBuilder(wordStr.length());
                            
                            newStr.append(fc).append(wordStr.substring(1));
                            
                            wordStr = newStr.toString();
                        }
                        
                        break;
                    }
                }
            }
        }
        
       
        wordStr = wordStr.replace('_', ' ');
        
        return wordStr;
    }
    /**
     * Do the translating.
     * @return The translated text or an empty string if none avaliable.
     * @throws FalconException If any errors are encountered.
     * @throws FalconTimeoutException If we time out trying to read from the decoder output.
     */
    public String translateUsingScript() throws FalconException, FalconTimeoutException
    {

        String scriptsDir = properties.getProperty(SCRIPTS_DIR);

        String execScript = scriptsDir + SEGMENT_DECODE;

        logger.info("Invoking: " + execScript + " " + clientName + " " + customerID + " " + srcLang + " " + tgtLang + " '" + srcNormalizedText + "' " + uuid);

        ProcessBuilder pb = new ProcessBuilder(execScript, clientName, customerID, srcLang, tgtLang, srcNormalizedText, uuid);
        
        String translation = executeScript(execScript, pb, 15000);
        
        this.translation = translation;

        return translation;
    }

    /**
     * Try and get the decoder output within a set number of miliseconds.
     * @param is The input stream.
     * @param timeoutMillis The timeout value.
     * @return The output from the decoder.
     * @throws IOException If we get an IOException trying to read the output.
     */
    public static String readInputStreamWithTimeout(InputStream is, int timeoutMillis) throws IOException  
    {
        StringBuilder buff = new StringBuilder(2048);
        
        long maxTimeMillis = System.currentTimeMillis() + timeoutMillis;
        
        while (System.currentTimeMillis() < maxTimeMillis)
        {
            byte[] b = new byte[2048];
            
            int readLength = java.lang.Math.min(is.available(), b.length);
            
            // can alternatively use bufferedReader, guarded by isReady():
            
            int readResult = is.read(b, 0, readLength);
            
            if (readResult == -1)
            {
                break;
            }
            
            String str = new String(b, 0, readLength, "UTF-8");
            
            if ((str != null) && (str.startsWith("SUCCESS")))
            {
                break;
            }
            
            buff.append(str);
            
            try
            {
                Thread.sleep(100);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
        
        return buff.toString();
    }
    /**
     * Get the uuid.
     * @return the uuid.
     */
    public String getUuid()
    {
        return uuid;
    }
    /**
     * Get the translation.
     * @return the translation.
     */
    public String getTranslation()
    {
        return translation;
    }
    /**
     * Get the bleuScore.
     * @return the bleuScore.
     */
    public String getBleuScore()
    {
        return confidenceScore;
    }
}
