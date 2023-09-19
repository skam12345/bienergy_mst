package bienergy_mst.bienergy_mst.parameter;

import bienergy_mst.bienergy_mst.utils.constant.ChargerEVCConstant;

public class HeartBeat {
    private String chargerId;
    public HeartBeat(String chargerId) {
        this.chargerId = chargerId;
    }

    public byte [] heartBeatAnswered() {
        byte [] hexdata = new byte[15];
        boolean flag = true;
        int index = 0;
        Long [] start = new Long[2];
        Long [] charger = new Long[6];
        Long [] msta = new Long[2];
        Long control = null;
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
                hexdata[index] = ChargerEVCConstant.HEARTBEAT;
                control = Long.parseLong("24", 16);            
            }
        }
        while(flag) {
            index++;      
            if(index == 11 && index == 12) {
                hexdata[index] = (byte) Long.parseLong("00", 16);
            }else if(index == 13) {
                Long checksum = Long.parseLong("00", 16);
                Long [] checksumList = {start[0], start[1], charger[0], charger[1], charger[2], charger[3], charger[4], charger[5], msta[0], msta[1], control};
                String foramtHex = "";
                for(int i = 0; i < checksumList.length; i++) {
                    // System.out.println(i + " : " + checksumList[i]);
                    checksum += checksumList[i];
                }
                foramtHex = String.format(ChargerEVCConstant.FORMAT, checksum).substring(1, 3);
                checksum = Long.parseLong(foramtHex, 16);
                hexdata[index] = checksum.byteValue();
            }else if(index == 14) {
                hexdata[index] = ChargerEVCConstant.FRAME_END;
            }else if(index == 15) {
                flag = false;
            }
        }
        return hexdata;
    }
}