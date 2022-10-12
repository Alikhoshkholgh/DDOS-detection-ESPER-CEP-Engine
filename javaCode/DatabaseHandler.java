import java.sql.*;
import java.io.File;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

public class DatabaseHandler{

	private JSONObject db_config;

	public DatabaseHandler(){
		ConfigReader configReader = new ConfigReader();
		this.db_config = configReader.getConfigurations("database");
		this.createAttackerTable();
	}


	public void createAttackerTable(){

		String databaseName = (String)this.db_config.get("database-name");
		String databasePath = (String)this.db_config.get("database-path");
		String tableName = (String)this.db_config.get("table-name");

		if(tableName.equals("udpFlood") && Boolean.parseBoolean((String)this.db_config.get("record"))){
		
			Connection conn = null;
			Statement stmt = null;
			try{
				Class.forName("org.sqlite.JDBC");
				conn = DriverManager.getConnection("jdbc:sqlite:"+databasePath+databaseName+".db");
				stmt = conn.createStatement();
				String sql = 	"CREATE TABLE "+tableName+
				       		"(DSTIP TEXT NOT NULL,"+
				       		"SRCIP TEXT NOT NULL,"+
						"DSTPORTCOUNT INT NOT NULL,"+
						"Entropy TEXT NOT NULL,"+
						"TIME TEXT NOT NULL)";
				stmt.executeUpdate(sql);
				System.out.println("\n\t---------- UDPFLOOD table created specifically for udp-flood Attack");
				stmt.close();
       		                conn.close();
			}
			catch(Exception e){
				System.out.println("\n\t------------ in DatabaseHander. createAttackerTable:: " + e);
			}
		}
	}



	public void putAttackRecord(JSONArray attackDetail_list){

		String databaseName = (String)this.db_config.get("database-name");
		String databasePath = (String)this.db_config.get("database-path");
		String tableName = (String)this.db_config.get("table-name");
		if(tableName.equals("udpFlood") && Boolean.parseBoolean((String)this.db_config.get("record"))){
		  try{

				Connection conn = DriverManager.getConnection("jdbc:sqlite:"+databasePath+databaseName+".db");
				conn.setAutoCommit(false);
	                        Statement stmt = conn.createStatement();
			for(int i=0; i < attackDetail_list.length(); i++){
				
				JSONObject attackDetail = (JSONObject)attackDetail_list.getJSONObject(i);
				String srcIP = (String)attackDetail.get("srcIP");
				String dstIP = (String)attackDetail.get("dstIP");
				String attackTime = (String)attackDetail.get("time");
				int portCount = (int)attackDetail.get("portCount");
				String entropy =  (String)(attackDetail.get("Entropy"));
				String sql = "INSERT INTO "+tableName+" (DSTIP,SRCIP,TIME,DSTPORTCOUNT,ENTROPY) VALUES (\""+dstIP+"\",\""+srcIP+"\",\""+attackTime+"\","+portCount+",\""+entropy+"\")";
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



	public JSONArray readAttackRecord(){
	
		JSONArray attackRecord = new JSONArray();
		JSONObject attackDetail = new JSONObject();
		String databaseName = (String)this.db_config.get("database-name");
		String databasePath = (String)this.db_config.get("database-path");
		String tableName = (String)this.db_config.get("table-name");
		if(tableName.equals("udpFlood") && Boolean.parseBoolean((String)this.db_config.get("record")) ){
		
			try{
		

				Connection conn = DriverManager.getConnection("jdbc:sqlite:"+databasePath+databaseName+".db");
				Statement stmt = conn.createStatement();	
				ResultSet rs = stmt.executeQuery("SELECT * FROM "+tableName+";");
				while(rs.next()){
					String srcIP 	= rs.getString("SRCIP");
					String dstIP 	= rs.getString("DSTIP");
					String Time 	= rs.getString("TIME");
					int portCount 	= rs.getInt("DSTPORTCOUNT");
					String entropy	= rs.getString("ENTROPY");

					attackDetail.put("srcIP", srcIP);
					attackDetail.put("dstIP", dstIP);
					attackDetail.put("time", Time);
					attackDetail.put("portCount", portCount);
					attackDetail.put("entropy", entropy);

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
