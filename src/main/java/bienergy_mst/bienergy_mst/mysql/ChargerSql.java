package bienergy_mst.bienergy_mst.mysql;

public class ChargerSql {
	public static final String calllistLogin = "SELECT chn_charger_id FROM charger_info WHERE chn_charger_id is NOT NULL";
	public static final String callIsLogin = "SELECT isLogin FROM charger_info WHERE chn_charger_id is NOT NULL";
	public static final String callCommand = "SELECT send_no FROM send_command WHERE send_control_yn = 'N' AND chnNo = (SELECT chn_no FROM charger_info WHERE chn_charger_id = ?)";
	public static final String callStartCommand = "SELECT send_no FROM send_command as sc WHERE cin_code = '0AH02' AND send_control_yn = 'N' AND chnNo = (SELECT chn_no FROM charger_info WHERE chn_charger_id = ?)";
	public static final String callStopCommand = "SELECT send_no FROM send_command as sc WHERE cin_code = '0AH03' AND send_control_yn = 'N' AND chnNo = (SELECT chn_no FROM charger_info WHERE chn_charger_id = ?)";
	public static final String callChargerData = "SELECT se.send_no, se.charger_plug_no, se.pyud_charge, se.pyud_no, ch.chn_charger_id FROM send_command as se"
											 + " INNER JOIN (SELECT * FROM charger_info WHERE chn_no = (SELECT chnNo FROM send_command WHERE send_control_yn = 'N'"	
                                             +" AND send_no = ?)) as ch WHERE send_no = ?";
	public static final String callPlugNumber = "SELECT charger_plug_no FROM send_command WHERE send_control_yn = 'N' AND chnNo = (SELECT chn_no FROM charger_info WHERE chn_charger_id = ?)";
	public static final String checkCommand = "SELECT cin_code FROM send_command WHERE send_control_yn = 'N' AND chnNo = (SELECT chn_no FROM charger_info WHERE chn_charger_id = ?) AND charger_plug_no = ?";
	public static final String loginUpdate = "UPDATE charger_info SET isLogin = 'Y' WHERE chn_charger_id = ?";
	public static final String checkComplete = "SELECT cin_code_name FROM receive_command WHERE pyud_no = ?";
	public static final String sendStartUpdate = "UPDATE send_command SET send_control_yn = 'Y', send_update_date = NOW() WHERE chnNo = (SELECT chn_no FROM charger_info WHERE chn_charger_id = ?) AND cin_code = '0AH02' AND send_control_yn = 'N' AND charger_plug_no = ?";
	public static final String sendStopUpdate = "UPDATE send_command SET send_control_yn = 'Y', send_update_date = NOW()  WHERE chnNo = (SELECT chn_no FROM charger_info WHERE chn_charger_id = ?) AND cin_code = '0AH03' AND send_control_yn = 'N' AND charger_plug_no = ?";
	public static final String insertReceivce = "INSERT INTO receive_command(cin_code, chnNo, charger_plug_no, pyud_charged, object_charged, receive_remain_charging, cin_code_name, receive_complete_yn, pyud_no, receive_reg_date, receive_update_date,  pyud_charging_percent) VALUES(?, (SELECT chn_no FROM charger_info WHERE chn_charger_id = ?), ?, ?, ?, ?, ?, ?, ?, SYSDATE(), SYSDATE(), ?)";
	public static final String updateCharging = "UPDATE receive_command SET pyud_charged = ?, pyud_charging_percent = ?, receive_update_date = NOW() WHERE chnNo = (SELECT chn_no FROM charger_info WHERE chn_charger_id = ?) AND receive_complete_yn = 'N'";
	public static final String stoppedReceive = "UPDATE receive_command SET receive_remain_charging = ?, cin_code = '0AH01', cin_code_name = '충전중지', receive_complete_yn = 'Y', receive_update_date = NOW() WHERE chnNo = (SELECT chn_no FROM charger_info WHERE chn_charger_id = ?) AND receive_complete_yn = 'N' AND charger_plug_no = ?";
	public static final String completePlug = "SELECT charger_plug_no FROM receive_command WHERE (object_charged - (((object_charged * 1000) / 100) * 2)) < ?";
	public static final String completeReceive = "UPDATE receive_command SET receive_remain_charging = 0, cin_code = 0AH01', icn_code_name = '충전완료', receive_complete_yn = 'Y' pyud_charging_percent = 100, receive_update_date = NOW() WHERE chnNo = (SELECT chn_no FROM charger_info WHERE chn_charger_id = ?) AND receive_complete_yn = 'N' AND charger_plug_no = ?";
}
