package bienergy_mst.bienergy_mst.parameter;

import bienergy_mst.bienergy_mst.utils.constant.ChargerEVCConstant;

public class ErrorReport {
    private String chargerId;
    public ErrorReport(String chargerId) {
        this.chargerId = chargerId;
    }

    public byte [] errorReportAnswered(String alarm_code) {
        byte [] hexdata = new byte[16];
        boolean flag = true;
        int index = 0;
        Long [] start = new Long[2];
        Long [] charger = new Long[6];
        Long [] msta = new Long[2];
        Long control = null;
        Long dl = null;
        Long code = null;
        int count = 0;
        hexdata[index] = ChargerEVCConstant.FRAME_START;
        start[0] = Long.parseLong("68", 16);
        count = 0;
        for(int i = 0; i < chargerId.length(); i+= 2) {
            String hex = chargerId.substring(i, i+2);

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
            }if(i == 18) {
                index++;
                hexdata[index] = ChargerEVCConstant.ERROR_REPORT;
                control = Long.parseLong("19", 16);            
            }
        }
        while(flag) {
            index++;      
            if(index == 11) {
                hexdata[index] = (byte) Long.parseLong("01", 16);
                dl = Long.parseLong("01", 16);
            }else if(index == 12) {
                hexdata[index] = (byte) Long.parseLong("00", 16);
            }else if(index == 13) {
                hexdata[index] = (byte) Long.parseLong(alarm_code, 16);
                code = Long.parseLong(alarm_code, 16);
            }else if(index == 14) {
                Long checksum = Long.parseLong("00", 16);
                Long [] checksumList = {start[0], start[1], charger[0], charger[1], charger[2], charger[3], charger[4], charger[5], msta[0], msta[1], control, dl, code};
                String foramtHex = "";
                for(int i = 0; i < checksumList.length; i++) {
                    // System.out.println(i + " : " + checksumList[i]);
                    checksum += checksumList[i];
                }
                foramtHex = String.format(ChargerEVCConstant.FORMAT, checksum).substring(1, 3);
                checksum = Long.parseLong(foramtHex, 16);
                hexdata[index] = checksum.byteValue();
            }else if(index == 15) {
                hexdata[index] = ChargerEVCConstant.FRAME_END;
            }else if(index == 16) {
                flag = false;
            }
        }
        return hexdata;
    }
}
