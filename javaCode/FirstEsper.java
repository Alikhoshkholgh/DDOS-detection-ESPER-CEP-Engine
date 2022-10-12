
import java.util.Arrays;
import org.json.JSONObject;
import org.json.JSONArray;
import java.io.*;
import java.net.*;
import java.util.*;

public class FirstEsper{

	int port  = 4545;
	public static void main(String[] args){
	

		//################### build the esper engine 
		EsperCore esper = new EsperCore();
		esper.loadEPLs();

		while(true){	
			SocketHandler jSock = new SocketHandler();

			//################### open socket connection to receive netflow packets 
			jSock.openReceiveSocket();
			
			JSONArray jsonArr;
			JSONObject jsonObj;
		
			while(true){

				//################### check if there was an interrupt to reload modules 
				if(esper.needToReDeploy){
					esper.destroyStatements();
					esper.loadEPLs();
					esper.needToReDeploy=false;
				}

				//################### receive netflow packets 
				jsonArr = jSock.receiveJsonPacket(); 
				if(jsonArr == null){
					System.out.println("empty packet or non-json packet.");
					break;
				}
				for(int i=0; i<jsonArr.length() ;i++){
					try{
						//################### send event to esper engine
						esper.sendEvent(((JSONObject)jsonArr.get(i)).toMap());
					}catch(NullPointerException e){
						System.out.println("\n\n\n------------------------ send Event NUllPointer catched");
					}
					finally{
						continue;	
					}
				}
			}
			jSock.closeSocket();
		}
	}
}

