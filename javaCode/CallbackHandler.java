import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPAdministrator;
import java.util.*;
import java.sql.Timestamp;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;


public class CallbackHandler{

	private EPAdministrator epAdmin;
	private SocketHandler sock;
	private String sysLogServerIP;
	private int sysLogServerPort;
	private DatabaseHandler database;
	private JSONObject db_conf;
	private JSONObject syslog_conf;
	private ConfigReader configReader;

	public CallbackHandler(EPAdministrator _epAdmin){

		this.configReader = new ConfigReader();
                this.syslog_conf = configReader.getConfigurations("syslog");
                this.db_conf = configReader.getConfigurations("database");

		this.sysLogServerIP = (String)syslog_conf.get("destinationIP");
		this.sysLogServerPort = Integer.parseInt((String)syslog_conf.get("destinationPort"));

		this.epAdmin = _epAdmin;
		sock = new SocketHandler();
		database = new DatabaseHandler();
	}


	public void assignListener(String moduleName){
		
		if(moduleName.equals("EPL-udpFlood")){//-------------------------------------------------------------------------------------

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
					sock.sendSysLog(msg);
				}*/
			
			});



			this.epAdmin.getStatement("udpFlood_3").addListener((newData, oldData) -> {


				System.out.println("\n----------------- udpFlood-3 callback ");

				//------------------------------------------------------------------------------------------------
				//Map<String, Map<String, ArrayList<Integer> > > attacked_destinations = new HashMap<>();
				Map<String, Map<String, Map<Integer, Integer> > > attacked_destinations = new HashMap<>();
				for(int i=0; i<newData.length; i++){
					String dstIP = (String) newData[i].get("dstIP");
					String srcIP = (String) newData[i].get("srcIP");
					int dstPort = (int) newData[i].get("dstPort");
					//System.out.println("udp-flood module  statment-3 event_indx["
					//		+i+"]::  dstIP = "+dstIP+"  srcIP = "+srcIP+"  dstPort = "+dstPort);
				
					if(attacked_destinations.containsKey(dstIP)){
						if(attacked_destinations.get(dstIP).containsKey(srcIP)){
							//if(!attacked_destinations.get(dstIP).get(srcIP).contains(dstPort)){
							if(!attacked_destinations.get(dstIP).get(srcIP).containsKey(dstPort)){
								//attacked_destinations.get(dstIP).get(srcIP).add(dstPort);
								attacked_destinations.get(dstIP).get(srcIP).put(dstPort, 1);
							}
							else if(attacked_destinations.get(dstIP).get(srcIP).containsKey(dstPort)){ 	//
								int count = attacked_destinations.get(dstIP).get(srcIP).get(dstPort);	//
								attacked_destinations.get(dstIP).get(srcIP).put(dstPort, count+1);	//
							}										//

						}
						else if(!attacked_destinations.get(dstIP).containsKey(srcIP)){
							//ArrayList<Integer> portList = new ArrayList<>();
							//portList.add(dstPort);
							Map<Integer, Integer> portList = new HashMap<>();		//
							portList.put(dstPort, 1);					//
							attacked_destinations.get(dstIP).put(srcIP, portList);		
						}
					}
					else if(!attacked_destinations.containsKey(dstIP)){
						//ArrayList<Integer> portList = new ArrayList<>();
						//portList.add(dstPort);
						//Map<String, ArrayList<Integer>> srcIP_dstPort_pair = new HashMap<>();
						
						Map<Integer, Integer> portList = new HashMap<>();
						portList.put(dstPort, 1);
						Map<String, Map<Integer, Integer>> srcIP_dstPort_pair = new HashMap<>();
						
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
						String dstIP_final = (String)(attacked_destinations.keySet().toArray())[i];
						System.out.println("\n__________dstIP : " + (dstIP_final));
					
						for(int j=0;j<attacked_destinations.get(dstIP_final).size();j++)
						{
						
							//############### srcIP
							String srcIP_final = (String)(attacked_destinations.get(dstIP_final).keySet().toArray())[j];


							int portCount = (attacked_destinations.get(dstIP_final).get(srcIP_final).size()); //distinct port count
							//############### calculate Entropy
								ArrayList<Integer> Entr_list = new ArrayList<>(attacked_destinations.get(dstIP_final).get(srcIP_final).values());
								int sum_of_portNumbers = 0;
									for(int n=0; n<portCount; n++)
										sum_of_portNumbers += Entr_list.get(n);
								//---------probability distribution
								float[] probability_distribution = new float[portCount];
									for(int n=0; n<portCount; n++)
										probability_distribution[n] = ((float)(Entr_list.get(n)))/((float)sum_of_portNumbers);
								
								//---------Entropy value
								float Entropy = (float)0.0;
								for(int n=0; n<portCount; n++)
									Entropy += probability_distribution[n] * Math.log(probability_distribution[n]);
								Entropy = (-1) * Entropy;

							//############### sql query string
							sqlRecord = sqlRecord + "{'dstIP':'"+dstIP_final+"' , 'time':'"+currentTime
										+"', 'portCount':"+portCount+", 'srcIP': '"+srcIP_final+"', 'Entropy':'"+String.valueOf(Entropy)+"'},";

							System.out.println(" *  srcIP: " +srcIP_final +"  Port Count: "+portCount+"  Entropy of srcIP: "+String.valueOf(Entropy));
							if( Boolean.parseBoolean((String)this.syslog_conf.get("sendSyslog")) ){
								//################ send syslog
								sock.sendSysLog("{'dstIP': '"+dstIP_final+"' 'srcIP':'"+srcIP_final
										+"' , 'time':'"+currentTime+"', 'portCount':"+portCount+", 'Entropy':'"+String.valueOf(Entropy)+ "'}");
								System.out.println("--------udpFlood log---------->> syslog is sent to "
										+this.sysLogServerIP+" on "+this.sysLogServerPort);
							}
						}
					}
						sqlRecord = sqlRecord + "]";
						sqlRecord.replace(",]", "]");
						
						System.out.println("\n----------- in callback[3]: the overall JSONArray.string:: ");
						System.out.println(sqlRecord);

						JSONArray attack_list = new JSONArray(sqlRecord);
						if( Boolean.parseBoolean((String)this.db_conf.get("record")) ){
							//################# record attack detail in database
							System.out.println("\n\t------------ put attack record in database");
							database.putAttackRecord(attack_list);
						}
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
				if(Boolean.parseBoolean((String)this.syslog_conf.get("sendSyslog"))){
					System.out.println("-------------gather log ----->> syslog is sent to "+this.sysLogServerIP+" on "+this.sysLogServerPort);
					sock.sendSysLog(msg);
				}
                        }

                });
	  }
	


	  
	  
	  else if(moduleName.equals("ruleSet_c")){//--------------------------------------------------------------------------------------------------------------
	  
	  
	  }
	}


}
