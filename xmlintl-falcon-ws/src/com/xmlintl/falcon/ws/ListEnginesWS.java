package com.xmlintl.falcon.ws;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

import com.xmlintl.falcon.util.FalconException;
import com.xmlintl.falcon.util.ListEngines;

/**
 * Servlet implementation class ListEnginesWS
 */
@WebService(targetNamespace = "http://ws.falcon.xmlintl.com/", portName = "ListEnginesWSPort", serviceName = "ListEnginesWSService")
@WebServlet("/ListEnginesWS")
public class ListEnginesWS extends HttpServlet 
{
    private static final long serialVersionUID = 1L;

    private ListEngines listEngines;
    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ListEnginesWS() 
    {
        super();
        // TODO Auto-generated constructor stub
    }

    @WebMethod(operationName = "init", action = "urn:Init")
    @RequestWrapper(className = "com.xmlintl.falcon.ws.jaxws.Init", localName = "init", targetNamespace = "http://ws.falcon.xmlintl.com/")
    @ResponseWrapper(className = "com.xmlintl.falcon.ws.jaxws.InitResponse", localName = "initResponse", targetNamespace = "http://ws.falcon.xmlintl.com/")
    @Override
    public void init() throws ServletException 
    {
        try
        {
            listEngines = new ListEngines();
            
        }
        catch (Exception e)
        {
           getServletContext().log("An exception occurred in ListEnginesWS", e);
           
           throw new ServletException(e.getMessage(), e);
        }
        
        
    }
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
	    // Set a cookie for the user, so that the counter does not increate
	    // every time the user press refresh
	    HttpSession session = request.getSession(true);
	    // Set the session valid for 5 secs
	    session.setMaxInactiveInterval(5);
	    response.setContentType("text/plain");
	    PrintWriter out = response.getWriter();
	    
        ArrayList<String> engines = null;
        try
        {
            engines = listEngines.list();
        }
        catch (FalconException e)
        {
            getServletContext().log("An exception occurred in FileCounter", e);
            
            throw new ServletException(e.getMessage(), e);
        }
        
        for (String engine: engines)
        {
            out.println(engine);
        }
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		// TODO Auto-generated method stub
	}

}
