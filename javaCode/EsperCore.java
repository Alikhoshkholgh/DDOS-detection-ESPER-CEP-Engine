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

import java.util.*;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;

public class EsperCore implements SignalHandler{

	private EPServiceProvider engine;
	private String pathToEpls;
	private String programPath = "";
	private EPStatement[] statements; 
	private String deploymentID = "";
	public boolean needToReDeploy = false;
	private CallbackHandler stHandler; 
	private DatabaseHandler database;

	public EsperCore(String attackTableName, String programPath){

		this.programPath = programPath;
		pathToEpls = programPath + "EPLs/EPL-udpFlood.epl";
		Configuration config = new Configuration();
		config.configure("configuration.xml");
		this.engine = EPServiceProviderManager.getDefaultProvider(config);
		this.initiateSignalHandler();
		this.database = new DatabaseHandler(programPath);
		this.stHandler = new CallbackHandler(EPServiceProviderManager.getExistingProvider("default").getEPAdministrator(), database);
		EPDeploymentAdmin deployAdmin = this.engine.getEPAdministrator().getDeploymentAdmin();
		database.createAttackerTable(attackTableName);
	}



	public void sendEvent(Map<String, Object> statistics){
		this.engine.getEPRuntime().sendEvent(statistics, "myMapEvent");
	}


	private void initiateSignalHandler(){
		Signal.handle(new Signal("USR1"), this);
	}


	public void loadEPLs(){
		try{

			File file = new File(this.programPath+"EPLs/target-EPL-names");
			BufferedReader br = new BufferedReader(new FileReader(file));
			List<String> module_files = new ArrayList<>();
			String st;
			while ((st = br.readLine()) != null)
				module_files.add(st);


			for(int i=0; i<module_files.size(); i++){
				com.espertech.esper.client.deploy.Module modules = EPServiceProviderManager.getExistingProvider("default").getEPAdministrator().getDeploymentAdmin().read(new File( this.programPath + "/EPLs/"+module_files.get(i)));
				DeploymentResult deployResult = EPServiceProviderManager.getExistingProvider("default").getEPAdministrator().getDeploymentAdmin().deploy(modules, null);
		
				System.out.println("\n----------------from module: " + module_files.get(i).substring(0, module_files.get(i).indexOf(".")));
				for(int j=0; j<modules.getItems().size(); j++){
					System.out.println("\n EPL["+j+"] = "+modules.getItems().get(j).getExpression());
				}


				stHandler.assignListener(module_files.get(i).substring(0, module_files.get(i).indexOf(".")));	
			}


		}	
		catch(DeploymentException e){
		System.out.println(e);	
		}
		catch(IOException e){
			System.out.println(e);	
		}
		catch(ParseException e){
			System.out.println(e);	
		}
		catch(InterruptedException e){
			System.out.println(e);	
		}
		catch(IndexOutOfBoundsException e){
			System.out.println(e);	
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




/*

		this.engine.getEPAdministrator().getStatement("udpFlood_1").addListener((newData, oldData) -> {
			String dstIP =   (String) newData[0].get("dstIP");
			String srcIP = (String) newData[0].get("srcIP");
			Object dstPort = (int)newData[0].get("dstPort");
			String protocolIdentifier = (String) newData[0].get("iana_protocolIdentifier");
			System.out.println("\n------------------- statement[0]  protocol_ident="+protocolIdentifier+"  dstIP="+dstIP+"  dstPort="+dstPort+"  srcIP="+srcIP);
		});

		
		this.engine.getEPAdministrator().getStatement("udpFlood_2").addListener((newData, oldData) -> {
			String dstIP = (String) newData[0].get("dstIP");
			Object dstPort = (int) newData[0].get("dstPort");
			Object Uports = (long) newData[0].get("uniquePorts");
			System.out.println("------------------- statement[1] callback: dstIP=  "+dstIP+"  uniquePorts=  "+Uports+"  >>> Message:  UDP-Flood Detected");	
		});


		
		this.engine.getEPAdministrator().getStatement("udpFlood_3").addListener((newData, oldData) -> {

			ArrayList<Integer> dstPortList = new ArrayList<Integer>();
			Map<String, ArrayList<Integer>> sourceIPList = new HashMap<String, ArrayList<Integer>>();
			
			for(int i=0; i<newData.length; i++){
				String srcIP = (String) newData[i].get("srcIP");
				Object dstPort = (int) newData[i].get("dstPort");
				System.out.println("-------------- statement[2] callback: srcIP=  "+srcIP+"  dstPort=  "+dstPort);
			}
			
			Object Uports = (long)newData[0].get("uniquePorts");
			System.out.println("------------------- statement[2] callback:  uniquePorts=  "+Uports);
		});


*/
