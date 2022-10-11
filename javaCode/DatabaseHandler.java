import java.sql.*;
import java.io.File;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

public class DatabaseHandler{


	public String databaseName = "attack_record";
	public String programPath;

	public DatabaseHandler(String path){
		this.programPath = path;
	}

	public void createAttackerTable(String tableName){
	
		if(tableName.equals("udpFlood")){
		
			Connection conn = null;
			Statement stmt = null;

			try{
				Class.forName("org.sqlite.JDBC");
				conn = DriverManager.getConnection("jdbc:sqlite:"+this.programPath+this.databaseName+".db");
				stmt = conn.createStatement();
				String sql = 	"CREATE TABLE UDPFLOOD "+
				       		"(DSTIP TEXT NOT NULL,"+
				       		"SRCIP TEXT NOT NULL,"+
						"DSTPORTCOUNT INT NOT NULL,"+
						"TIME TEXT NOT NULL)";
				stmt.executeUpdate(sql);
				System.out.println("\n\t---------- UDPFLOOD table created specifically for udp-flood Attack");
				stmt.close();
       		                conn.close();
			}
			catch(Exception e){
				System.out.println("\n\t------------ error in DatabaseHander. createAttackerTable:: " + e);
			}
			finally{
				//System.out.println("\n\ttable exists in this place. se the program closed the statement and database connection");
			}
		}
	}



	public void putAttackRecord(String tableName, JSONArray attackDetail_list){

		if(tableName.equals("udpFlood")){
		  try{
				Connection conn = DriverManager.getConnection("jdbc:sqlite:"+this.programPath+this.databaseName+".db");
				conn.setAutoCommit(false);
	                        Statement stmt = conn.createStatement();
			for(int i=0; i < attackDetail_list.length(); i++){
				
				JSONObject attackDetail = (JSONObject)attackDetail_list.getJSONObject(i);
				String srcIP = (String)attackDetail.get("srcIP");
				String dstIP = (String)attackDetail.get("dstIP");
				String attackTime = (String)attackDetail.get("time");
				int portCount = (int)attackDetail.get("portCount");
				String sql = "INSERT INTO UDPFLOOD (DSTIP,SRCIP,TIME,DSTPORTCOUNT) VALUES (\""+dstIP+"\",\""+srcIP+"\",\""+attackTime+"\","+portCount+")";
				stmt.executeUpdate(sql);
			}
			stmt.close();
 	                conn.commit();
       	                conn.close();
		  }
		  catch(JSONException e){
		  	System.out.println("\n\t in DatabaseHandler. in putAttackRecord(): " + e);
		  }
		  catch(Exception e){
		  	System.out.println("\n\t in DatabaseHandler. in putAttackRecord(): " + e);
		  }
		}
	
	}



	public JSONArray readAttackRecord(String tableName){
	
		JSONArray attackRecord = new JSONArray();
		JSONObject attackDetail = new JSONObject();



		if(tableName.equals("udpFlood")){
		
			try{
				Connection conn = DriverManager.getConnection("jdbc:sqlite:"+this.programPath+this.databaseName+".db");
				Statement stmt = conn.createStatement();	
				ResultSet rs = stmt.executeQuery("SELECT * FROM "+tableName+";");
				while(rs.next()){
					String srcIP 	= rs.getString("SRCIP");
					String dstIP 	= rs.getString("DSTIP");
					String Time 	= rs.getString("TIME");
					int portCount 	= rs.getInt("DSTPORTCOUNT");

					attackDetail.put("srcIP", srcIP);
					attackDetail.put("dstIP", dstIP);
					attackDetail.put("time", Time);
					attackDetail.put("portCount", portCount);

					attackRecord.put(attackDetail);
				}
				stmt.close();
				conn.commit();
				conn.close();
			}
			catch(Exception e){
				System.out.println("in DatabaseHandler. readAttackRecord :: "+e);
			}
		}

		return attackRecord;
	}

}
