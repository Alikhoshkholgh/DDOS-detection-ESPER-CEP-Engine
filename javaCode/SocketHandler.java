import java.util.Arrays;
import java.util.StringTokenizer;
import java.lang.*;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.io.*;
import java.net.*;

public class SocketHandler{

	public DataInputStream inStream = null;
	public ServerSocket sock = null;
	public Socket connection = null;
	private byte[] buff = new byte[65536];


	public boolean openReceiveSocket(int port){
		try{
			this.sock = new ServerSocket(port);
			System.out.println("\n----------------------------socket is created. waiting for new connectoin");
			this.connection = this.sock.accept();
			System.out.println("\n----------------------------new connection is found.\n");
			this.inStream = new DataInputStream(new BufferedInputStream(this.connection.getInputStream()));
		}catch(IOException e){
			System.out.println("opening of socket Connection: " + e);
			return false;
		}
		return true;
	}

	public void sendSysLog(String msg, String IP_addr, int port){
	
		try{
                        DatagramSocket ds = new DatagramSocket();
                        byte buff[] = msg.getBytes();
                        String ip_str = IP_addr;
                        byte[] ip = asBytes(ip_str);

                        DatagramPacket dPacket = new DatagramPacket(buff, buff.length, InetAddress.getByAddress(ip), port);
                        ds.send(dPacket);
                }
                catch(IOException e){
                        System.out.println(e);
                }

	}

	public JSONArray receiveJsonPacket(){
		
		String payload = "";
		int payload_length = -1;
		JSONArray jsonPacket = null;
		String payload_arr[];
		String test_buff = "";
		int frag_count = 0;

		try{
			while(true){
				test_buff = "";
				payload_length = inStream.read(this.buff, 0, 65536);

				if(payload_length == -1){
					System.out.println("\n\nthere is nothing to read in inStream");
					return null;
				}
				
				for(int i=0; i<payload_length; i++){
					payload = payload + (char)this.buff[i];
					test_buff = test_buff + (char)this.buff[i];
				}
				Arrays.fill(this.buff, (byte)0x00);
				
				//if(payload.charAt(payload.length() - 1) == 0xa)
				if(payload.charAt(payload.length() - 1) == '}')
					break;	
			}

			//payload = payload.replace("\n", "");
			payload = payload.replace("na:", "na_");
			payload_arr = payload.split("}");
			payload = "[";
			for(String x : payload_arr)
				payload = payload + x + "},";
			payload = payload + "]";
			payload = payload.replace("},]", "}]");
			jsonPacket = new JSONArray(payload);

			}
		catch(IOException e){
			System.out.println(e);
		}
		catch(JSONException e){
			System.out.println("\n\n\t class: JsonSocket: receiveJsonPacket() : parseException" + e);
		}
		return jsonPacket; 
	}


	public boolean closeSocket(){
		try{
			this.sock.close();	
			return true;
		}
		catch(IOException e){
			System.out.println(e);
			return false;
		}
	}




        private byte[] asBytes(String addr) {
              int ipInt = parseNumericAddress(addr);
              if ( ipInt == 0)
                return null;
              byte[] ipByts = new byte[4];
              ipByts[3] = (byte) (ipInt & 0xFF);
              ipByts[2] = (byte) ((ipInt >> 8) & 0xFF);
              ipByts[1] = (byte) ((ipInt >> 16) & 0xFF);
              ipByts[0] = (byte) ((ipInt >> 24) & 0xFF);
              return ipByts;
	}

        private int parseNumericAddress(String ipaddr) {
            if ( ipaddr == null || ipaddr.length() < 7 || ipaddr.length() > 15)
              return 0;
            StringTokenizer token = new StringTokenizer(ipaddr,".");
            if ( token.countTokens() != 4)
              return 0;
            int ipInt = 0;
            while ( token.hasMoreTokens()) {
              String ipNum = token.nextToken();
              try {
                int ipVal = Integer.valueOf(ipNum).intValue();
                if ( ipVal < 0 || ipVal > 255)
                  return 0;
                ipInt = (ipInt << 8) + ipVal;
              }
              catch (NumberFormatException ex) {
                return 0;
              }
            }
            return ipInt;
  	}


}
