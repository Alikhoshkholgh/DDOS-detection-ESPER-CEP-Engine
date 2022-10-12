import org.json.JSONObject;
import org.json.JSONArray;
import java.util.*;
import java.io.File;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;


public class ConfigReader{

	public JSONObject getConfigurations(String configName){

		JSONObject jsonConfig = new JSONObject();

		try{
			File xFile = new File("./configuration.xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(xFile);
			doc.getDocumentElement().normalize();
			Element conf = (Element)doc.getElementsByTagName("custom-config").item(0);

			if(configName == "core"){
				jsonConfig = this.readCoreConfigurations(conf);
			}
			else if(configName == "database"){
				jsonConfig = this.readDatabaseConfigurations(conf);
			}
			else if(configName == "syslog"){
				jsonConfig = this.readSyslogConfigurations(conf);
			}

		}catch(Exception e){
			System.out.println("\n\tin ConfiReader. getConfigurations():: " + e);
		}


		return jsonConfig;
        }


        private JSONObject readCoreConfigurations(Element conf){
	
		JSONObject output = new JSONObject();
		try{
			Element core_conf = (Element)conf.getElementsByTagName("core").item(0);

			String programPath = core_conf.getElementsByTagName("program-path").item(0).getTextContent();
			String listenPort = core_conf.getElementsByTagName("listening-port").item(0).getTextContent();
			List<String> moduleNames = new ArrayList<>();
			output.put("program-path", programPath);
			output.put("listen-port", listenPort);
			Element EPL = (Element)core_conf.getElementsByTagName("EPL").item(0);
			NodeList moduleNodeList = EPL.getElementsByTagName("module");
			for(int i=0; i<moduleNodeList.getLength(); i++){
				String moduleName = ((Element)moduleNodeList.item(i)).getAttribute("name");
				moduleNames.add(moduleName);
			}
			JSONArray json_module_list = new JSONArray(moduleNames);

			output.put("EPL-modules", json_module_list);

		}catch(Exception e){
			System.out.println("\n\tin ConfiReader. getConfigurations():: " + e);
		}
		return output;
	}

        private JSONObject readDatabaseConfigurations(Element conf){

		JSONObject output = new JSONObject();
		try{
			Element database_conf = (Element)conf.getElementsByTagName("database").item(0);
	 
			String enableRecord = database_conf.getElementsByTagName("record").item(0).getTextContent();
			String databasePath = database_conf.getElementsByTagName("database-path").item(0).getTextContent();
			String tableName = database_conf.getElementsByTagName("table-name").item(0).getTextContent();
			String databaseName = database_conf.getElementsByTagName("database-name").item(0).getTextContent();
			output.put("record", enableRecord);
			output.put("table-name", tableName);
			output.put("database-name", databaseName);
			output.put("database-path", databasePath);	

		}catch(Exception e){
			System.out.println("\n\tin ConfiReader. getConfigurations():: " + e);
		}
		return output;
        }

        private JSONObject readSyslogConfigurations(Element conf){  
		
		JSONObject output = new JSONObject();
		try{
			Element syslog_conf = (Element)conf.getElementsByTagName("syslog").item(0);

			String sendSyslogEnabled = syslog_conf.getElementsByTagName("send-syslog").item(0).getTextContent();
			String destinationIP =     syslog_conf.getElementsByTagName("destinationIP").item(0).getTextContent();
			String destinationPort =   syslog_conf.getElementsByTagName("destinationPort").item(0).getTextContent();
			output.put("sendSyslog", sendSyslogEnabled);
			output.put("destinationIP", destinationIP);
			output.put("destinationPort", destinationPort);

		}catch(Exception e){
			System.out.println("\n\tin ConfiReader. getConfigurations():: " + e);
		}
		return output;
	}
}
