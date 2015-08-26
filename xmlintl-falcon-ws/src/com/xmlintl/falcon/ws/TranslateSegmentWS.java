package com.xmlintl.falcon.ws;

import java.io.IOException;
import java.io.PrintWriter;

import javax.jws.WebService;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.hamcrest.core.IsInstanceOf;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.xmlintl.falcon.util.FalconException;
import com.xmlintl.falcon.util.FalconServerNotRunningException;
import com.xmlintl.falcon.util.FalconTimeoutException;
import com.xmlintl.falcon.util.TranslateSegment;

/**
 * Servlet implementation class TranslateSegmentWS
 */
@WebService(targetNamespace = "http://ws.falcon.xmlintl.com/", portName = "TranslateSegmentWSPort", serviceName = "TranslateSegmentWSService")
@WebServlet("/TranslateSegmentWS")
public class TranslateSegmentWS extends HttpServlet
{
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public TranslateSegmentWS()
    {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {

        // Set a cookie for the user, so that the counter does not increate
        // every time the user press refresh
        HttpSession session = request.getSession(true);
        // Set the session valid for 5 secs
        session.setMaxInactiveInterval(5);
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String engineID = request.getParameter("engineID");
        String clientName = request.getParameter("clientName");
        String segment = request.getParameter("segment");
        String customerID = request.getParameter("customerID");
        String srcLang = request.getParameter("srcLang");
        String tgtLang = request.getParameter("tgtLang");
        String key = request.getParameter("key");

        log("clientName: " + clientName);
        log("engineID: " + engineID);
        log("customerID: " + customerID);
        log("srcLang: " + srcLang);
        log("tgtLang: " + tgtLang);
        log("segment: " + segment);
        
        if (segment == null)
        {
            log("######################## WARNING: segment is null - nothing to send to SMT");
        }

        JsonObject jsonObject = new JsonObject();

        GsonBuilder gsonBuilder = new GsonBuilder();

//      gsonBuilder.setPrettyPrinting();

        Gson gson = gsonBuilder.create();
        
        TranslateSegment translateSegment = null;

        if (segment != null)
        {
            try
            {
                String webroot = getServletContext().getRealPath("/") + "WEB-INF/";
                
                log("WEBROOT set to: " + webroot);
                
                translateSegment = new TranslateSegment(clientName, customerID, srcLang, tgtLang, segment, key, webroot);

                gson = gsonBuilder.create();

                jsonObject = new JsonObject();

                String uuid = translateSegment.getUuid();

                jsonObject.addProperty("UUID", uuid);
                
                String translation = "";
                
                if (segment != null)
                {
                    
                    translation = translateSegment.translate();
                    
                    log("translation: " + translation);
                }

                jsonObject.addProperty("translation", translation);
                
                if ((translation.isEmpty()) && (!segment.isEmpty())) // We are getting nothing back: need to restart the server.
                {
                    jsonObject.addProperty("error", "FAILED: No output restarting engine");
                    
                    String json = gson.toJson(jsonObject);

                    out.println(json);
                    
                    translateSegment.checkEngine();
                }
                else
                {
                    String json = gson.toJson(jsonObject);

                    out.println(json);
                }
            }
            catch (Exception e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
                
                if (e instanceof FalconServerNotRunningException)
                {
                    getServletContext().log("FalconTimeoutException occurred", e);
                    
                    jsonObject.addProperty("error", "FAILED: Timeout");
                    
                }
                else
                {
                    getServletContext().log("An exception occurred in TranslateSegmentWS", e);
                    
                    jsonObject.addProperty("error", "FAILED");
                }
                
                String json = gson.toJson(jsonObject);

                out.println(json);
                
                if (e instanceof FalconServerNotRunningException)
                {
                    try
                    {
                        translateSegment.checkEngine();
                    }
                    catch (FalconException e1) // Not much we can do about this unfortunately except complain....
                    {
                        log(e1.getMessage());
                        
                        e1.printStackTrace();
                    }
                }
            }
        }
    }

}
