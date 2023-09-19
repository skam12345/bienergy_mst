package bienergy_mst.bienergy_mst.parameter;

import bienergy_mst.bienergy_mst.utils.constant.ChargerEVCConstant;

public class DataRecord {
    private String chargerId;
    public DataRecord(String chargerId) {
        this.chargerId = chargerId;
    }

    public byte [] dataRecordAnswered(String transaction) {
        byte [] hexdata = new byte[30];
        Boolean flag = true;
        int index = 0;
        Long [] start = new Long[2];
        Long [] charger = new Long[ChargerEVCConstant.CHARGER_ID];
        Long [] msta = new Long[ChargerEVCConstant.MSTA_SEQ];
        Long control = null;
        Long [] serialCode = new Long[15];
        Long dl = null;
        int count = 0;
        hexdata[index] = ChargerEVCConstant.FRAME_START;
        int k = 0;
        start[0] = Long.parseLong("68", 16);
        //충전기로 받아온 데이터 sending 파라미터 리스트 조합
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
            }
        }
        while(flag) {
            index++;
            if(index == 10) {
                hexdata[index] = (byte)0x05;
                control = Long.parseLong("05", 16);
            }else if(index == 11) {
                hexdata[index] = (byte )0x0F;
                dl = Long.parseLong("0F", 16);
            }else if(index == 12) {
                hexdata[index] = (byte)0x00;
                count = 0;
            }else if(index >= 13  && index < 28) {

                    // System.out.println(combine.substring(k, k+2));
                    hexdata[index] = (byte) Long.parseLong(transaction.substring(k, k+2), 16);
                    serialCode[count] = Long.parseLong(transaction.substring(k, k+2), 16);
                    count++;
                    k+=2;
            }else if(index == 28) { // checksum 구하는 부분
                Long checksum = Long.parseLong("00", 16);
                String checkCal = "";
                Long [] checksumList = {start[0], start[1], charger
                    [0], charger[1], charger[2], charger[3], charger[4], 
                                    charger[5], 
                                    msta[0], msta[1], control, dl, serialCode[0], serialCode[1], serialCode[2], serialCode[3], serialCode[4],
                                    serialCode[5], serialCode[6], serialCode[7], serialCode[8], serialCode[9], serialCode[10], serialCode[11],
                                    serialCode[12], serialCode[13], serialCode[14]};
                for(int j = 0; j < checksumList.length; j++) {
                    // System.out.println(j + " : " + checksumList[j]);
                    checksum += checksumList[j];
                }
                checkCal = String.format("%02X", checksum).substring(1, 3);
                hexdata[index] = (byte) Long.parseLong(checkCal, 16);
            }else if(index == 29) {
                hexdata[index] = ChargerEVCConstant.FRAME_END;
            }
            if(index == 30) {
                flag = false;
            }
        }
        return hexdata;
    }
}