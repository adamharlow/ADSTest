import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.InputMismatchException;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.json.JSONObject;
import org.json.XML;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class App {

	public static void main(String[] args) {
		initMenu();
	}
	
	private static void initMenu() {
		File file = new File("./src/main/resources/ab.xml");
		App app = new App();
		Scanner scan = new Scanner(System.in);
		boolean isRunning = true;
		
		try {
			while (isRunning) {
				System.out.println("\nEnter the number for the format you wish to convert to, or press 0 to exit.");
				System.out.println("1. JSON");
				System.out.println("2. CSV");
				System.out.println("\n>");
				
				int input = scan.nextInt();
				
				switch(input) {
				case 0:
					isRunning = false;
					break;
				case 1:
					app.getJSON(file);
					break;
				case 2:
					app.getCSV(file);
					break;
				default:
					System.out.println("Invalid selection.\n");
				}
			}
		} catch (InputMismatchException ime) {
			System.out.println("Invalid input. Must be an integer.\n");
		} catch (IOException ioe) {
			System.err.println("Error reading file.");
			ioe.printStackTrace();
		} finally {
			System.out.println("Program terminated.");
			scan.close();
		}
	}
	
	private String getJSON(File file) throws IOException {
		String fileContents = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
		JSONObject obj = XML.toJSONObject(fileContents);
		String jsonPrettyPrintString = obj.toString(4);
		System.out.println(jsonPrettyPrintString);
		
		return jsonPrettyPrintString;
	}
	
	private String getCSV(File file) throws IOException {
		File stylesheet = new File("./src/main/resources/style.xsl");
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		StringWriter writer = new StringWriter();
		
		try {
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(file);
			StreamSource styleSource = new StreamSource(stylesheet);
			Transformer transformer = TransformerFactory.newInstance().newTransformer(styleSource);
			Source source = new DOMSource(doc);
			Result outputTarget = new StreamResult(writer);
			transformer.transform(source, outputTarget);
		} catch (TransformerFactoryConfigurationError tfce) {
			tfce.printStackTrace();
		} catch (TransformerException te) {
			te.printStackTrace();
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (SAXException saxe) {
			saxe.printStackTrace();
		}
		
		System.out.println(writer.toString());
		return writer.toString();
	}
	
}
