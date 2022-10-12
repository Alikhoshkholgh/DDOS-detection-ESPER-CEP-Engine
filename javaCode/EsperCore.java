import sun.misc.Signal;
import sun.misc.SignalHandler;
import com.espertech.esper.client.*;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.deploy.EPDeploymentAdmin;
import com.espertech.esper.client.deploy.DeploymentResult;
import com.espertech.esper.client.deploy.UndeploymentResult;
import com.espertech.esper.client.deploy.DeploymentException;
import com.espertech.esper.client.deploy.Module;
import com.espertech.esper.client.deploy.ParseException;

import org.json.JSONObject;
import org.json.JSONArray;
import java.util.*;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

public class EsperCore implements SignalHandler{

	private EPServiceProvider engine;
	private String programPath = "";
	private EPStatement[] statements; 
	private String deploymentID = "";
	public boolean needToReDeploy = false;
	private CallbackHandler stHandler; 
	private DatabaseHandler database;
	private ConfigReader configReader;
	private JSONObject coreConfigurations;

	public EsperCore(){
		
		//############# esper Native configurations
		Configuration config = new Configuration();
		config.configure("configuration.xml");
		this.engine = EPServiceProviderManager.getDefaultProvider(config);
		EPDeploymentAdmin deployAdmin = this.engine.getEPAdministrator().getDeploymentAdmin();

		//############# Custom configurations
		this.configReader = new ConfigReader();
		this.coreConfigurations = configReader.getConfigurations("core");
		this.programPath = (String)this.coreConfigurations.get("program-path");
	
		this.stHandler = new CallbackHandler(EPServiceProviderManager.getExistingProvider("default").getEPAdministrator());
		this.initiateSignalHandler();
	}

	public void sendEvent(Map<String, Object> statistics){
		this.engine.getEPRuntime().sendEvent(statistics, "myMapEvent");
	}


	private void initiateSignalHandler(){
		Signal.handle(new Signal("USR1"), this);
	}


	public void loadEPLs(){

		try{
			JSONArray module_files= (JSONArray)((this.coreConfigurations).get("EPL-modules"));
			for(int i=0; i<module_files.length(); i++){
			
				//################ read Modules	
				com.espertech.esper.client.deploy.Module modules = EPServiceProviderManager
					.getExistingProvider("default").getEPAdministrator().getDeploymentAdmin()
					.read(new File( (this.programPath + "/EPLs/"+(String)(module_files.get(i)) )+".epl" ));

				//################ deploy Modules	
				DeploymentResult deployResult = EPServiceProviderManager.getExistingProvider("default")
					.getEPAdministrator().getDeploymentAdmin().deploy(modules, null);
		
				System.out.println("\n----------------from module: "+(String)module_files.get(i));
				for(int j=0; j<modules.getItems().size(); j++){
					System.out.println("\n EPL["+j+"] = "+modules.getItems().get(j).getExpression());
				}
				
				//################ assign handler to Modules	
				stHandler.assignListener((String)module_files.get(i));	
			}
		}	
		catch(Exception e){
			System.out.println("\n\t in EsperCore. loadEPLs() :: "+e);	
		}
	}


	public void destroyStatements(){
               EPServiceProviderManager.getExistingProvider("default").getEPAdministrator().destroyAllStatements();
	       System.out.println("\n-------------------- All statements are destroyed....");
        }



	public void handle(Signal sig){
		this.needToReDeploy = true;
		System.out.println("\n----------------- modules are re-Deployed---------------------\n");
	}
}

