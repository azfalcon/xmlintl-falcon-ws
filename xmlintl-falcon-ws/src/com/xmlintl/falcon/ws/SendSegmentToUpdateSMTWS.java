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
import com.xmlintl.falcon.util.SendSegmentToUpdateSMT;

/**
 * Servlet implementation class SendSegmentToUpdateSMTWS
 */
@WebService(targetNamespace = "http://ws.falcon.xmlintl.com/", portName = "SendSegmentToUpdateSMTWSPort", serviceName = "SendSegmentToUpdateSMTWSService")
@WebServlet("/SendSegmentToUpdateSMTWS")
public class SendSegmentToUpdateSMTWS extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SendSegmentToUpdateSMTWS() {
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
        
        String clientName = request.getParameter("clientName");
        String customerID = request.getParameter("customerID");
        String projectID = request.getParameter("projectID");
        String srcLang = request.getParameter("srcLang");
        String tgtLang = request.getParameter("tgtLang");
        String srcSegment = request.getParameter("srcSegment");
        String tgtSegment = request.getParameter("tgtSegment");
        
        log("clientName: " + clientName);
        log("customerID: " + customerID);
        log("srcLang: " + srcLang);
        log("tgtLang: " + tgtLang);
        log("srcSegment: " + srcSegment);
        log("tgtSegment: " + tgtSegment);
               
       GsonBuilder gsonBuilder = new GsonBuilder();

//        gsonBuilder.setPrettyPrinting();

        Gson gson = gsonBuilder.create();
        
        JsonObject jsonObject = new JsonObject();
        
        try
        {
            SendSegmentToUpdateSMT updateSMT = new SendSegmentToUpdateSMT(clientName, customerID, projectID, srcLang, tgtLang, srcSegment, tgtSegment);
            
            updateSMT.update();
            
            String uuid = updateSMT.getUuid();

            jsonObject.addProperty("UUID", uuid);
            String json = gson.toJson(jsonObject);

            out.println(json);
        }
        catch (FalconException e)
        {
            getServletContext().log("An exception occurred in SendSegmentToUpdateSMTWS", e);

            jsonObject.addProperty("error", "FAILED");
            String json = gson.toJson(jsonObject);

            out.println(json);
            
            e.printStackTrace();
        }
    }

}
