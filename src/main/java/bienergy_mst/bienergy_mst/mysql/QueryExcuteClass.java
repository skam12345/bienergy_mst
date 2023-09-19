package bienergy_mst.bienergy_mst.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import bienergy_mst.bienergy_mst.model.ChargerDataModel;

public class QueryExcuteClass {
	private Connection conn;
	private PreparedStatement pstmt;
	private ResultSet rs;
	
	public QueryExcuteClass(Connection conn) {
		this.conn = conn;
		
	}
	
	public List<String> callLoginLsit() {
		List<String> chargerId = null;
		try {
			chargerId = new ArrayList<String>();
			pstmt = conn.prepareStatement(ChargerSql.calllistLogin.toString());
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				chargerId.add(rs.getString(1));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return chargerId;
	}
	
	public List<String> callIsLogin() {
		List<String> isLogin = null;
		try {
			isLogin = new ArrayList<String>();
			pstmt = conn.prepareStatement(ChargerSql.callIsLogin.toString());
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				isLogin.add(rs.getString(1));
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}
		return isLogin;
	}
	
	public int callCommand(String id) {
		int sendNo = 0;
		try {
			pstmt = conn.prepareStatement(ChargerSql.callCommand.toString());
			pstmt.setString(1, id);
			rs = pstmt.executeQuery();
			while(rs.next()) {
				sendNo = rs.getInt(1);
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return sendNo;
	}
	
	public List<Integer> callStartCommand(String id) {
		List<Integer> sendNo = null;
		try {
			sendNo = new ArrayList<Integer>();
			pstmt = conn.prepareStatement(ChargerSql.callStartCommand.toString());
			pstmt.setString(1, id);
			rs = pstmt.executeQuery();
			while(rs.next()) {
				sendNo.add(rs.getInt(1));
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return sendNo;
	}
	
	public List<Integer> callStopCommand(String id) {
		List<Integer> sendNo = null;
		try {
			sendNo = new ArrayList<Integer>();
			pstmt = conn.prepareStatement(ChargerSql.callStopCommand.toString());
			pstmt.setString(1, id);
			rs = pstmt.executeQuery();
			while(rs.next()) {
				sendNo.add(rs.getInt(1));
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return sendNo;
	}
	
	public ChargerDataModel callChargerData(int sendNo) {
		ChargerDataModel model = null;
		try {
			model = new ChargerDataModel();
			pstmt = conn.prepareStatement(ChargerSql.callChargerData.toString());
			pstmt.setInt(1, sendNo);
			pstmt.setInt(2, sendNo);
			
			
			rs = pstmt.executeQuery();
			while(rs.next()) {
				model.setSendNo(rs.getInt(1));
				model.setPlugNo(rs.getString(2));
				model.setCharge(rs.getInt(3));
				model.setPyudNo(rs.getString(4));
				model.setChargerId(rs.getString(5));
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}
		
		return model;
	}
	
	public String callPlugNumber(String id) {
		String plugNumber = "";
		try {
			pstmt = conn.prepareStatement(ChargerSql.callPlugNumber.toString());
			pstmt.setString(1, id);
			rs = pstmt.executeQuery();
			while(rs.next()) {
				plugNumber = rs.getString(1);
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return plugNumber;
	}
	
	
	
	public String checkCommand(String id, String plugNo) {
		String code = null;
		try {
			pstmt = conn.prepareStatement(ChargerSql.checkCommand.toString());
			pstmt.setString(1, id);
			pstmt.setString(2, plugNo);
			
			rs = pstmt.executeQuery();
			while(rs.next()) {
				code = rs.getString(1);
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}
		
		return code;
	}
	
	public void loginUpdate(String id) {
		int i = 0;
		try {
			pstmt = conn.prepareStatement(ChargerSql.loginUpdate.toString());
			pstmt.setString(1, id);
			
			i = pstmt.executeUpdate();
		}catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public String checkComplete(String pyudNo) {
		String code_name = null;
		try {
			pstmt = conn.prepareStatement(ChargerSql.checkComplete.toString());
			pstmt.setString(1, pyudNo);
			
			rs = pstmt.executeQuery();
			while(rs.next()) {
				code_name = rs.getString(1);
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}
		
		return code_name;
	}
	
	public void sendStartUpdate(String id, String plugNo) {
		int result = 0;
		try {
			pstmt = conn.prepareStatement(ChargerSql.sendStartUpdate.toString());
			pstmt.setString(1, id);
			pstmt.setString(2, plugNo);
			
			result = pstmt.executeUpdate();
		}catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void sendStopUpdate(String id, String plugNo) {
		int result = 0;
		try {
			pstmt = conn.prepareStatement(ChargerSql.sendStopUpdate.toString());
			pstmt.setString(1, id);
			pstmt.setString(2, plugNo);
			
			result = pstmt.executeUpdate();
		}catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void insertReceivce(ChargerDataModel model, String id) {
		int result = 0;
		try {
			pstmt = conn.prepareStatement(ChargerSql.insertReceivce.toString());
			pstmt.setString(1, "0AH01");
			pstmt.setString(2, id);
			pstmt.setString(3, model.getPlugNo());
			pstmt.setInt(4, 0);
			pstmt.setInt(5, model.getCharge());
			pstmt.setInt(6, 0);
			pstmt.setString(7, "충전중");
			pstmt.setString(8, "N");
			pstmt.setString(9, model.getPyudNo());
			pstmt.setInt(10, 0);
			
			result = pstmt.executeUpdate();
		}catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void updateCharging(int power, int charged) {
		double wart = ((power / (charged * 1000)) * 100);
		int percent = (int) Math.floor(wart);
		int result = 0;
		try {
			pstmt= conn.prepareStatement(ChargerSql.updateCharging.toString());
			pstmt.setInt(1, power);
			pstmt.setInt(2, percent);
			
			result = pstmt.executeUpdate();
		}catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void stoppedReceive(int remain, String id, ChargerDataModel model) {
		int result = 0;
		try {
			pstmt = conn.prepareStatement(ChargerSql.stoppedReceive.toString());
			pstmt.setInt(1, remain);
			pstmt.setString(2, id);
			pstmt.setString(3, model.getPlugNo());
			
			result = pstmt.executeUpdate();
		}catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void completeReceive(String id, ChargerDataModel model) {
		int result = 0;
		try {
			pstmt = conn.prepareStatement(ChargerSql.completeReceive.toString());
			pstmt.setString(1, id);
			pstmt.setString(2, model.getPlugNo());
			
			result = pstmt.executeUpdate();
		}catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public String completePlug(int power) {
		String plugNo = "";
		try {
			pstmt = conn.prepareStatement(ChargerSql.completePlug.toString());
			pstmt.setInt(1, power);
			
			rs = pstmt.executeQuery();
			while(rs.next()) {
				plugNo = rs.getString(1);
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}
		
		return plugNo; 
	}
}