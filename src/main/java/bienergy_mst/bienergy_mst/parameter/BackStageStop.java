package bienergy_mst.bienergy_mst.parameter;

import bienergy_mst.bienergy_mst.utils.constant.ChargerEVCConstant;

public class BackStageStop {
	private String values;
    private String plug_number;
    private String serialCode;
    String reverseHex;
	public BackStageStop(String values, String plug_number, String serialCode) {
		this.values = values;
        this.plug_number = plug_number;
        this.serialCode = serialCode;
        reverseHex = "";
    }
	public byte [] stop() {
		System.out.println(serialCode + " ");
        Boolean flag = true;
        Boolean charFlag = true;
        byte [] hexdata = new byte[61];
        int index = 0;
        Long [] start = new Long[2];
        Long [] charger = new Long[ChargerEVCConstant.CHARGER_ID];
        Long [] msta = new Long[ChargerEVCConstant.MSTA_SEQ];
        Long control = null;
        Long dl = null;
        Long auth = null;
        Long command = null;
        Long [] serial = new Long[15];
        Long [] plug = new Long[1];
        Long parameter = null;
        Long [] balance = new Long[4];
        int idx = 0;
        int count = 0;
        hexdata[index] = ChargerEVCConstant.FRAME_START;
        start[0] = Long.parseLong("68", 16);
        for(int i = 0; i < values.length(); i+= 2) {
            String hex = values.substring(i, i+2);
            if(i >= 0 && i < 12) {
                index++;
                hexdata[index] =(byte) Long.parseLong(hex, 16);
                charger[count] = Long.parseLong(hex, 16);
                count++;
            }
            if(i >= 12 && i < 16) {
                if(i == 12) {
                    count = 0;
                }
                index++;
                hexdata[index] = (byte)Long.parseLong(hex, 16);
                msta[count] = Long.parseLong(hex, 16);
                count++;
            }
            if(i == 16) {
                index++;
                hexdata[index] = (byte) Long.parseLong(hex, 16);
                start[1] = Long.parseLong(hex, 16);
            }
        }
        while(flag) {
            index++;
            if(index == 7) {
                hexdata[index] = (byte) 0xC1;
                msta[0] = Long.parseLong("C1", 16);
            }else if(index == 8) {
                hexdata[index] = (byte) 0x06;
                msta[1] = Long.parseLong("06", 16);
            }else if(index == 9) {
                hexdata[index] = ChargerEVCConstant.FRAME_START;
                start[1] = Long.parseLong("68", 16);
            }else if(index == 10) {
                hexdata[index] = ChargerEVCConstant.BACK_CONTROL;
                control = Long.parseLong("0A", 16);
            }else if(index == 11) {
                hexdata[index] = (byte )0x2E;
                dl = Long.parseLong("2E", 16);
            }else if(index == 12) {
                hexdata[index] = (byte) 0x00;
            }else if(index == 13) {
                hexdata[index] = (byte) 0x11;
                auth = Long.parseLong("11", 16);
            }else if(index >= 14 && index < (14 + ChargerEVCConstant.PWD)) {
                hexdata[index] = (byte) 0x00;
            }else if(index == 17) {
                hexdata[index] = (byte) 0x03;
                command = Long.parseLong("03", 16);
            }else if(index >= 18 && index < 18 + ChargerEVCConstant.DATA_ACCOUNT) {
                hexdata[index] = (byte) 0x00;
            }else if(index >= 26 && index < 26 + ChargerEVCConstant.DATA_CARD) {
                hexdata[index] = (byte) 0x00;
                count = 0;
            }else if(index >= 34 && index < 49) {
                hexdata[index] = (byte) Long.parseLong(serialCode.substring(idx, idx + 2), 16);
                serial[count] = Long.parseLong(serialCode.substring(idx, idx + 2), 16);
                idx += 2;
                count++;
            }else if(index == 49) {
                hexdata[index] = (byte) Long.parseLong(this.plug_number, 16); 
                plug[0] =  Long.parseLong(this.plug_number, 16);
            }else if(index >= 50 && index < 55) {
                hexdata[index] = (byte) 0x00;
                parameter = Long.parseLong("00", 16);
                idx = 0;
                count = 0;
            }else if(index >= 55 && index < 55 + ChargerEVCConstant.BALANCE) {
                hexdata[index] = (byte) 0x99;
                balance[count] = Long.parseLong("99", 16);
                count++;
            }else if(index == 59) {
                Long checksum = Long.parseLong("00", 16);
                String checkCal = "";
                Long [] checksumList = {start[0], start[1], charger[0], charger[1], charger[2], charger[3], charger[4], 
                                    charger[5], msta[0], msta[1], control, dl, auth, command, serial[0], serial[1], serial[2], 
                                    serial[3], serial[4], serial[5], serial[6], serial[7], serial[8], serial[9], serial[10],
                                    serial[11], serial[12], serial[13], serial[14], plug[0], parameter, balance[0], balance[1], balance[2], balance[3]};
                for(int j = 0; j < checksumList.length; j++) {
                    // System.out.println(j + " :" + checksumList[j]);
                    checksum += checksumList[j];
                }
                checkCal = String.format("%02X", checksum).substring(1, 3);
	                hexdata[index] = (byte) Long.parseLong(checkCal, 16);
	            }else if(index == 60) {
	                hexdata[index] = ChargerEVCConstant.FRAME_END;
	            }
	            if(index == 61) {
	                flag = false;
	            }

	        }
	        return hexdata;
	    }
	}
