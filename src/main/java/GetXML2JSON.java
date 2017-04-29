
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Base64;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.json.JSONObject;
import org.json.XML;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Servlet implementation class GetXML2JSON
 */
@WebServlet("/GetXML2JSON")
public class GetXML2JSON extends HttpServlet {

	private static final long serialVersionUID = 1L;
	public static int PRETTY_PRINT_INDENT_FACTOR = 4;
	public static String TEST_XML_STRING;



	protected void processRequest(HttpServletRequest request, HttpServletResponse response) {

		String xmlURL = getPropertyByName("url");

		//check authorization
		Boolean supportBasicAuthorization = Boolean.valueOf(getPropertyByName("basicAuthorization"));
		Boolean authorizated = false;
		if (supportBasicAuthorization) {
			
			String header = request.getHeader("authorization");
			if (header != null) {
	            String base64 = header.substring(6, header.length());
	            byte[] bytes = Base64.getDecoder().decode(base64);
	            String userNameAndPasswd = new String(bytes, 0, bytes.length);
	            if (userNameAndPasswd.equals("KdhSe8:iy4F4PnHuygeuvfDbM4")) {
	            	authorizated = true;
	            } else {
	            	authorizated = false;
	            }
	        } else {
	        	authorizated = false;
	        }

		}else{
			authorizated = true;
		}

		// get data to print out
		PrintWriter out = null;
		try {
			out = response.getWriter();
			String result;
			if(authorizated){
				
				Document doc;
				TransformerFactory tf = TransformerFactory.newInstance();
				Transformer transformer;
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder;
				response.setCharacterEncoding("UTF-8");
				response.setContentType("application/json");
				
				//Getting XML from URL
				builder = factory.newDocumentBuilder();
				doc = builder.parse(xmlURL);
				//XML to String
				transformer = tf.newTransformer();
				transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
				StringWriter writer = new StringWriter();
				transformer.transform(new DOMSource(doc), new StreamResult(writer));
				TEST_XML_STRING = writer.getBuffer().toString().replaceAll("\n|\r", "");
				//XML to JSON
				JSONObject xmlJSONObj = XML.toJSONObject(TEST_XML_STRING);
				result = xmlJSONObj.toString(PRETTY_PRINT_INDENT_FACTOR);
				
			}else{
				result="Access Denied";
			}
			//output the result
			out.print(result);
			
		} catch (IOException | ParserConfigurationException | SAXException | TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		processRequest(request,response);
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	private String getPropertyByName(String name){
		String result = null;
		// Load config.properties
		Properties prop = new Properties();
		InputStream input = null;
		try {
			input = getClass().getClassLoader().getResourceAsStream("config.properties");
			prop.load(input);
			result = prop.getProperty(name);
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}
	
}
