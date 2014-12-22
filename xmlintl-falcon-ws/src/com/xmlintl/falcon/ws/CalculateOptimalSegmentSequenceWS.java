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
import com.xmlintl.falcon.util.CalculateOptimalSegmentSequence;
import com.xmlintl.falcon.util.FalconException;

/**
 * Servlet implementation class CalculateOptimalSegmentSequenceWS
 */
@WebService(targetNamespace = "http://ws.falcon.xmlintl.com/", portName = "CalculateOptimalSegmentSequenceWSPort", serviceName = "CalculateOptimalSegmentSequenceWSService")
@WebServlet("/CalculateOptimalSegmentSequenceWS")
public class CalculateOptimalSegmentSequenceWS extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CalculateOptimalSegmentSequenceWS() {
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
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Set a cookie for the user, so that the counter does not increate
        // every time the user press refresh
        HttpSession session = request.getSession(true);
        // Set the session valid for 5 secs
        session.setMaxInactiveInterval(5);
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        String engineID = request.getParameter("engineID");
        String customerID = request.getParameter("customerID");
        String projectID = request.getParameter("projectID");
        String srcLang = request.getParameter("srcLang");
        String tgtLang = request.getParameter("tgtLang");
        String textFileURL = request.getParameter("url");
        
        GsonBuilder gsonBuilder = new GsonBuilder();

//        gsonBuilder.setPrettyPrinting();

        Gson gson = gsonBuilder.create();

        JsonObject jsonObject = new JsonObject();
        
        try
        {
            CalculateOptimalSegmentSequence  optimalRouteObject = new CalculateOptimalSegmentSequence(engineID, customerID, projectID, srcLang, tgtLang, textFileURL);
            
            String uuid = optimalRouteObject.getUuid();

            jsonObject.addProperty("UUID", uuid);
            
            int[] route = optimalRouteObject.getRoute();

            String jsonText = gson.toJson(route);

            jsonObject.addProperty("route", jsonText);

            String json = gson.toJson(jsonObject);

            out.println(json);
        }
        catch (FalconException e)
        {
            getServletContext().log("An exception occurred in CalculateOptimalSegmentSequenceWS", e);
            
            e.printStackTrace();
            
            jsonObject.addProperty("error", "FAILED");

            String json = gson.toJson(jsonObject);

            out.println(json);
        }
    }

}
