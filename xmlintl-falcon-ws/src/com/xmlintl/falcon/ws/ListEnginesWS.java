package com.xmlintl.falcon.ws;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

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
import com.xmlintl.falcon.util.ListEngines;
import com.xmlintl.falcon.util.SMTEngine;

/**
 * Servlet implementation class ListEnginesWS
 */
@WebService(targetNamespace = "http://ws.falcon.xmlintl.com/", portName = "ListEnginesWSPort", serviceName = "ListEnginesWSService")
@WebServlet("/ListEnginesWS")
public class ListEnginesWS extends HttpServlet 
{
    private static final long serialVersionUID = 1L;
    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ListEnginesWS() 
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
        // Set the session valid for 20 secs
        session.setMaxInactiveInterval(20);
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        String clientName = request.getParameter("clientName");
        
        ArrayList<SMTEngine> engines = null;
        
        GsonBuilder gsonBuilder = new GsonBuilder();

//        gsonBuilder.setPrettyPrinting();

        Gson gson = gsonBuilder.create();
        
        JsonObject jsonObject = new JsonObject();
        
        try
        {
            ListEngines listEngines = new ListEngines(clientName);
            
            engines = listEngines.list();
        }
        catch (FalconException e)
        {
            getServletContext().log("An exception occurred in ListEnginesWS", e);
            
            jsonObject.addProperty("error", "FAILED");

            String json = gson.toJson(jsonObject);

            out.println(json);
            
            throw new ServletException(e.getMessage(), e);
        }

        String jsonText = gson.toJson(engines);
        
//        String uuid = FalconUtil.getUUID();
//        
//        jsonObject.addProperty("UUID", uuid);
//        
//        jsonObject.addProperty("engines", jsonText);
//
//        String json = gson.toJson(jsonObject);

        out.println(jsonText);
    }

}
