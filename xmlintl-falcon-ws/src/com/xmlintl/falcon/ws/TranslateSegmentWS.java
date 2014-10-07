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

import com.xmlintl.falcon.util.FalconException;
import com.xmlintl.falcon.util.TranslateSegment;

/**
 * Servlet implementation class TranslateSegmentWS
 */
@WebService(targetNamespace = "http://ws.falcon.xmlintl.com/", portName = "TranslateSegmentWSPort", serviceName = "TranslateSegmentWSService")
@WebServlet("/TranslateSegmentWS")
public class TranslateSegmentWS extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public TranslateSegmentWS() {
        super();
        // TODO Auto-generated constructor stub
    }
    

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Set a cookie for the user, so that the counter does not increate
        // every time the user press refresh
        HttpSession session = request.getSession(true);
        // Set the session valid for 5 secs
        session.setMaxInactiveInterval(5);
        response.setContentType("text/plain;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
		String engineID = request.getParameter("engineID");
		String customerID = request.getParameter("customerID");
		String projectID = request.getParameter("projectID");
		String srcLang = request.getParameter("srcLang");
		String tgtLang = request.getParameter("tgtLang");
		String segment = request.getParameter("segment");
		
		try
        {
		    TranslateSegment translateSegment = new TranslateSegment(engineID, customerID, projectID, srcLang, tgtLang, segment);
            
            String uuid = translateSegment.getUuid();
            
            out.println("UUID: " + uuid);
            
            String translation = translateSegment.translate();
            
            out.println("TRANSLATION FOR UUID: " + uuid + " IS: " + translation);
        }
        catch (FalconException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
