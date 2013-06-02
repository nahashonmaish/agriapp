package lipwapoa;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;


public class DBHandler {
	
	Connection con;
	Statement st;

	public void getConnection(){
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			con = DriverManager.getConnection("jdbc:mysql://localhost/mshamba", "root", "");//connecting to database
			System.out.println("Connection Established!");
			
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {//catch statement in case connection fails
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//closing database after transaction like registering new member
	public void closeConnection(){
		try {
			if(con != null){
				con.close();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//registering a new member
	public void registerMember(String number, String name){
		try {
			getConnection();
			st = con.createStatement();
			st.execute("insert into member values ('"+number+"','"+name+"')");
			st.close();
			closeConnection();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//selling product
	public void addCrop(String cropName, String price, String unit, String location){
		try {
			getConnection();
			st = con.createStatement();
			st.execute("insert into crops values ('"+cropName.toLowerCase()+"','"+price+"','"+unit+"','"+location.toLowerCase()+"','"+new GregorianCalendar().getTime()+"')");
			st.close();
			closeConnection();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//validating registration avoiding double registration
	public boolean validateRegistration(String number){
		try {
			getConnection();
			st = con.createStatement();
			ResultSet rs = st.executeQuery("select * from member where phonenumber = '"+number+"'");
			int count = 0;
			while(rs.next()){
				count++;
			}
			if(count > 0){
				st.close();
				closeConnection();
				return false;
			}else{
				st.close();
				closeConnection();
				return true;
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean getIfUserExist(String number){
		try {
			getConnection();
			st = con.createStatement();
			ResultSet rs = st.executeQuery("select * from member where phonenumber = '"+number+"'");
			int count = 0;
			while(rs.next()){
				count++;
			}
			if(count > 0){
				st.close();
				closeConnection();
				return true;
			}else{
				st.close();
				closeConnection();
				return false;
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	//querying the database
	public String[] replyToUser(String crop, String location){
		List<String> list = new ArrayList<String>();
		List<String> unitsList = new ArrayList<String>();
		String []result = new String[2];
		System.out.println("Querying database..");
		
		try {
			getConnection();
			st = con.createStatement();
			
			ResultSet rs = st.executeQuery("select * from products");//accessing the table with variety of names
			while(rs.next()){
				if(crop.toLowerCase().equalsIgnoreCase(rs.getString("Name").toLowerCase())){
					ResultSet rs1 = st.executeQuery("select * from crops where cropname = '"+rs.getString("Name").toLowerCase()+"' " +
							"or cropname = '"+rs.getString("kisw").toLowerCase()+"' or cropname = '"+rs.getString("kamba").toLowerCase()+"' " +
									"and location = '"+location.toLowerCase()+"'");//querying for english product
					int count = 0;
					while(rs1.next()){
						list.add(rs1.getString("price"));
						result[1] = rs1.getString("units");
						unitsList.add(rs1.getString("units"));
						count++;
					}
					
					if(count > 0){
						double avgPrice = 0;
						for(int i = 0; i < list.size(); i++){
							avgPrice += (Double.parseDouble(list.get(i)) * Integer.parseInt(unitsList.get(i)));
						}
						avgPrice = avgPrice / unitsList.size();
						result[0] = avgPrice + "";
						return result;
					}else{
						return result;
					}
				}
				//querying in kiswahili
				else if(crop.toLowerCase().equalsIgnoreCase(rs.getString("kisw").toLowerCase())){
					System.out.println("Kiswahili");
					ResultSet rs1 = st.executeQuery("select * from crops where cropname = '"+rs.getString("Name").toLowerCase()+"' " +
							"or cropname = '"+rs.getString("kisw").toLowerCase()+"' " +
									"or cropname = '"+rs.getString("kamba").toLowerCase()+"' and location = '"+location.toLowerCase()+"'");
					
					int count = 0;
					
					while(rs1.next()){
						list.add(rs1.getString("price"));
						result[1] = rs1.getString("units");
						unitsList.add(rs1.getString("units"));
						count++;
					}
					
					if(count > 0){
						double avgPrice = 0;
						for(int i = 0; i < list.size(); i++){
							avgPrice += (Double.parseDouble(list.get(i)) * Integer.parseInt(unitsList.get(i)));
						}
						avgPrice = avgPrice / unitsList.size();
						result[0] = avgPrice + "";
						return result;
					}else{
						return result;
					}
				}
				//querying in kamba
				else if(crop.toLowerCase().equalsIgnoreCase(rs.getString("kamba").toLowerCase())){
					ResultSet rs1 = st.executeQuery("select * from crops where cropname = '"+rs.getString("Name").toLowerCase()+"' " +
							"or cropname = '"+rs.getString("kisw").toLowerCase()+"' or cropname = '"+rs.getString("kamba").toLowerCase()+"'" +
									" and location = '"+location.toLowerCase()+"'");
					int count = 0;
					while(rs1.next()){
						list.add(rs1.getString("price"));
						result[1] = rs1.getString("units");
						unitsList.add(rs1.getString("units"));
						count++;
					}
					
					if(count > 0){
						double avgPrice = 0;
						for(int i = 0; i < list.size(); i++){
							avgPrice += (Double.parseDouble(list.get(i)) * Integer.parseInt(unitsList.get(i)));
						}
						avgPrice = avgPrice / unitsList.size();
						result[0] = avgPrice + "";
						return result;
					}else{
						return result;
					}
				}
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return result;
		}
		return result;
	}
	
	public void detProductToDelete(String date){
		try {
			getConnection();
			st = con.createStatement();
			ResultSet rs = st.executeQuery("select * from crops");
			while(rs.next()){
				if(date.equals(rs.getString("dateinserted") + 14)){
					con.createStatement().execute("delete from crops where dateinserted = '"+rs.getString("dateinserted")+"'");
				}
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
