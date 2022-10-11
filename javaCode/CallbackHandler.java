import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPAdministrator;
import java.util.*;
import java.sql.Timestamp;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;


public class CallbackHandler{

	EPAdministrator epAdmin;
	SocketHandler sock;
	LogGenerator logGen;
	//--------------------------------------------------------------------change this IP to your log server
	String sysLogServerIP = "log-server-IP"; //like "127.0.0.1"
	//--------------------------------------------------------------------change this port to whatever your syslog server is listening to
	int sysLogServerPort = 2020;
	DatabaseHandler database;

	public CallbackHandler(EPAdministrator _epAdmin, DatabaseHandler dbHandler){
		this.epAdmin = _epAdmin;
		logGen = new LogGenerator();
		sock = new SocketHandler();
		database = dbHandler;
	}

	public void assignListener(String moduleName){
		
		
		
		if(moduleName.equals("EPL-udpFlood")){//--------------------------------------------------------------------------------------------------------------

			this.epAdmin.getStatement("udpFlood_1").addListener((newData, oldData) -> {
			       
			});


			this.epAdmin.getStatement("udpFlood_2").addListener((newData, oldData) -> {

				System.out.println("----------------- udpFlood-2 callback ");
				/*
				for(int i=0; i<newData.length; i++){

					String dstIP = (String) newData[i].get("dstIP");
					String srcIP = (String) newData[i].get("srcIP");
					Object dstPort = (int) newData[i].get("dstPort");
					Object Uports = (long) newData[0].get("uniquePorts");
					System.out.println("udp-flood module  statment-2::  dstIP = "+dstIP+"  srcIP = "+srcIP+"  dstPort = "+dstPort+ " unique ports:: ");
					sock.sendSysLog(msg, this.sysLogServerIP, this.sysLogServerPort);
				}*/
			
			});



			this.epAdmin.getStatement("udpFlood_3").addListener((newData, oldData) -> {


				System.out.println("\n----------------- udpFlood-3 callback ");

				//------------------------------------------------------------------------------------------------
				Map<String, Map<String, ArrayList<Integer> > > attacked_destinations = new HashMap<>();
				for(int i=0; i<newData.length; i++){
					String dstIP = (String) newData[i].get("dstIP");
					String srcIP = (String) newData[i].get("srcIP");
					int dstPort = (int) newData[i].get("dstPort");
					//System.out.println("udp-flood module  statment-3 event_indx["
					//		+i+"]::  dstIP = "+dstIP+"  srcIP = "+srcIP+"  dstPort = "+dstPort);
				
					if(attacked_destinations.containsKey(dstIP)){
						if(attacked_destinations.get(dstIP).containsKey(srcIP)){
							if(!attacked_destinations.get(dstIP).get(srcIP).contains(dstPort))
								attacked_destinations.get(dstIP).get(srcIP).add(dstPort);
						}
						else if(!attacked_destinations.get(dstIP).containsKey(srcIP)){
							ArrayList<Integer> portList = new ArrayList<>();
							portList.add(dstPort);
							attacked_destinations.get(dstIP).put(srcIP, portList);
						}
					}
					else if(!attacked_destinations.containsKey(dstIP)){
						ArrayList<Integer> portList = new ArrayList<>();
						portList.add(dstPort);
						Map<String, ArrayList<Integer>> srcIP_dstPort_pair = new HashMap<>();
						srcIP_dstPort_pair.put(srcIP, portList);
						attacked_destinations.put(dstIP, srcIP_dstPort_pair); 
					}
				}
				//------------------------------------------------------------------------------------------------

				try{
					Date date = new Date();
					String sqlRecord = "[";
					Timestamp timeStamp = new Timestamp(date.getTime());
					String currentTime = timeStamp.toString();	

					System.out.println("------------- records for attacks: ");
					for(int i=0; i<attacked_destinations.size(); i++){
					
						//############### dstIP
						String dstIP_finall = (String)(attacked_destinations.keySet().toArray())[i];
						System.out.println("\n__________dstIP : " + (dstIP_finall));
					
						for(int j=0;j<attacked_destinations.get((String)(attacked_destinations.keySet().toArray())[i]).size();j++)
						{
						
							//############### srcIP
							String srcIP_finall = (String)(attacked_destinations.get((String)(attacked_destinations
											.keySet().toArray())[i]).keySet().toArray())[j];

							//############### portCount
							int portCount = (attacked_destinations.get((String)(attacked_destinations.keySet().toArray())[i])
									.get((String)(attacked_destinations
											.get((String)(attacked_destinations.keySet().toArray())[i])
											.keySet().toArray())[j]).size()); 

							sqlRecord = sqlRecord + "{'dstIP':'"+dstIP_finall
								+"' , 'time':'"+currentTime+"', 'portCount':"
								+portCount+", 'srcIP': '"+srcIP_finall+"'},";

							System.out.println(" *  srcIP: " +srcIP_finall +"  Port Count: "+portCount);
							sock.sendSysLog("{'dstIP': '"+dstIP_finall+"' 'srcIP':'"+srcIP_finall+"' , 'time':'"+currentTime+"', 'portCount':"+portCount+"}"
									,this.sysLogServerIP,this.sysLogServerPort);
							System.out.println("------------------>> syslog is sent to "+this.sysLogServerIP+" on "+this.sysLogServerPort);

						}
					}
						sqlRecord = sqlRecord + "]";
						sqlRecord.replace(",]", "]");
						
						System.out.println("\n----------- in callback[3]: the overall JSONArray.string:: ");
						System.out.println(sqlRecord);

						JSONArray attack_list = new JSONArray(sqlRecord);
						database.putAttackRecord("udpFlood", attack_list);
				}
				catch(Exception e){
					System.out.println("in CallbackHandler. in callback of stmt[3] :: " + e);
				}


			});
		  }
	  
	  
	  
	  
	  
	  
	  else if(moduleName.equals("EPL-gather")){//--------------------------------------------------------------------------------------------------------------

	    this.epAdmin.getStatement("gather").addListener((newData, oldData) -> {

		    String msg;
                        for(int i=0; i<newData.length; i++){
                                String srcIP = (String) newData[i].get("srcIP");
                                String dstIP = (String) newData[i].get("dstIP");
                                Object dstPort = (int) newData[i].get("dstPort");
                                String protocol = (String) newData[i].get("protocol");
                                String startTime = (String) newData[i].get("startTime");
                                String endTime = (String) newData[i].get("endTime");
                                msg = "gather:: srcIP= "+srcIP+" dstIP= "+dstIP+" dstPort= "+dstPort+" protocol= "+protocol+" startTime= "+startTime+" endTime= "+endTime;
				sock.sendSysLog(msg, this.sysLogServerIP, this.sysLogServerPort);
                        }

                });
	  }
	


	  
	  
	  else if(moduleName.equals("ruleSet_c")){//--------------------------------------------------------------------------------------------------------------
	  
	  
	  }
	}


}
