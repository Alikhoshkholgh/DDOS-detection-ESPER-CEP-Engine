
import java.util.Arrays;
import org.json.JSONObject;
import org.json.JSONArray;
import java.io.*;
import java.net.*;
import java.util.*;

public class FirstEsper{

	int port  = 4545;
	public static void main(String[] args){
	
		System.out.println("\n\n you must insert a path to specify the root path of the your program: \n");
		String programPath = "";
		String attackTableName = "udpFlood";
		//------------------------------------------------------------ change this to port for listening to netflow traffic
		int listeningPort = 4545;

		try{
			programPath = args[0];
		}catch(ArrayIndexOutOfBoundsException e){
			System.out.println("\n\n\t  you should specify the program path in the your system. you just left it empty\n\n");
			System.exit(1);
		}

		EsperCore esper = new EsperCore(attackTableName, programPath);
		esper.loadEPLs();

		while(true){	
			SocketHandler jSock = new SocketHandler();

			jSock.openReceiveSocket(listeningPort);
			
			JSONArray jsonArr;
			JSONObject jsonObj;
		
			while(true){

				if(esper.needToReDeploy){
					esper.destroyStatements();
					esper.loadEPLs();
					esper.needToReDeploy=false;
				}

				jsonArr = jSock.receiveJsonPacket(); 
				if(jsonArr == null){
					System.out.println("empty packet or non-json packet.");
					break;
				}
				for(int i=0; i<jsonArr.length() ;i++){
					try{
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

