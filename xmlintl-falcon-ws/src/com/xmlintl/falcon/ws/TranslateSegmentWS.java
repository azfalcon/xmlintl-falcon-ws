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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.xmlintl.falcon.util.FalconException;
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
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
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
        String segment = request.getParameter("segment");

        System.out.println("engineID: " + engineID);
        System.out.println("segment: " + segment);

        try
        {
            TranslateSegment translateSegment = new TranslateSegment(engineID, segment);

            GsonBuilder gsonBuilder = new GsonBuilder();

//            gsonBuilder.setPrettyPrinting();

            Gson gson = gsonBuilder.create();

            JsonObject jsonObject = new JsonObject();

            String uuid = translateSegment.getUuid();

            jsonObject.addProperty("UUID", uuid);

            translateSegment.translate();

            String translation = translateSegment.getTranslation();

            jsonObject.addProperty("translation", translation);

            String confidenceScore = translateSegment.getBleuScore();

            if (confidenceScore != null)
            {
                jsonObject.addProperty("confidenceScore", confidenceScore);
            }

            String json = gson.toJson(jsonObject);

            out.println(json);
        }
        catch (FalconException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
