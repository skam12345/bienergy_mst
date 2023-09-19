package bienergy_mst.bienergy_mst.parameter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import bienergy_mst.bienergy_mst.utils.constant.ChargerEVCConstant;


public class BackStageStart {
    private String values;
    private String plug_number;
    String chargerId;
    String reverseHex;
    public BackStageStart(String values, String plug_number) {
        this.values = values;
        this.plug_number = plug_number;
        chargerId = "";
        reverseHex = "";
    }
    // 충전 시작 sending 파라미터 조합 메서드
    public byte [] start() {
    	Boolean flag = true;
        Boolean charFlag = true;
        byte [] hexdata = new byte[61];
        int index = 0;
        Long [] start = new Long[2];
        Long [] charger = new Long[ChargerEVCConstant.CHARGER_ID * 2];
        Long [] msta = new Long[ChargerEVCConstant.MSTA_SEQ];
        Long control = null;
        Long dl = null;
        Long auth = null;
        Long command = null;
        Long [] plug = new Long[2];
        Long [] current = new Long[6];
        Long [] random = new Long[2];
        Long parameter = null;
        Long [] balance = new Long[4];
        int idx = 0;
        int count = 0;
        SimpleDateFormat format = new SimpleDateFormat("ssmmHHddMMyy");
        String currentDate = format.format(new Date());
        hexdata[index] = ChargerEVCConstant.FRAME_START;
        start[0] = Long.parseLong("68", 16);
        //충전기로 받아온 데이터 sending 파라미터 리스트 조합
        for(int i = 0; i < values.length(); i+= 2) {
            String hex = values.substring(i, i+2);
            if(i >= 0 && i < 12) {
                chargerId += hex;
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
                hexdata[index] = (byte) 0x02;
                command = Long.parseLong("02", 16);
            }else if(index >= 18 && index < 18 + ChargerEVCConstant.DATA_ACCOUNT) {
                hexdata[index] = (byte) 0x00;
            }else if(index >= 26 && index < 26 + ChargerEVCConstant.DATA_CARD) {
                hexdata[index] = (byte) 0x00;
                count = 6;
            }else if(index >= 34 && index < 34 + ChargerEVCConstant.CHARGER_ID) {
                hexdata[index] = (byte) Long.parseLong(chargerId.substring(idx, idx + 2), 16);
                charger[count] = Long.parseLong(chargerId.substring(idx, idx + 2), 16);
                idx += 2;
                count++;
            }else if(index == 40) {
                hexdata[index] = (byte) Long.parseLong(this.plug_number, 16);
                plug[0] = Long.parseLong(this.plug_number, 16);
                count = 0;
                idx = 0;
            }else if(index >= 41 && index < 47) {
                hexdata[index] = (byte) Long.parseLong(currentDate.substring(idx, idx + 2), 16);
                current[count] = Long.parseLong(currentDate.substring(idx, idx + 2), 16);
                count++;
                idx += 2;
                if(count == 6) {
                    count = 0;
                }
            }else if(index >= 47 && index < 49) {
                String number = "0";
                Random rando = new Random();
                int rand = rando.nextInt(99);
                if(rand < 10) {
                    number += Integer.toString(rand);
                }else {
                    number = Integer.toString(rand);
                }
                hexdata[index] = (byte) Long.parseLong(number, 16);
                random[count] = Long.parseLong(number, 16);
                count++;
            }else if(index == 49) {
                hexdata[index] = (byte) Long.parseLong(this.plug_number, 16); 
                plug[1] =  Long.parseLong(this.plug_number, 16);
            }else if(index == 50) {
                hexdata[index] = (byte) 0x00;
                parameter = Long.parseLong("00", 16);
                idx = 0;
                count = 0;
            }else if(index >= 51 && index < 55) {
                hexdata[index] = (byte) 0x00;
                count = 0;
            }else if(index >= 55 && index < 55 + ChargerEVCConstant.BALANCE) {
                hexdata[index] = (byte) 0x7F;
                balance[count] = Long.parseLong("7F", 16);
                count++;

            }else if(index == 59) { // checksum 구하는 부분
                Long checksum = Long.parseLong("00", 16);
                String checkCal = "";
                Long [] checksumList = {start[0], start[1], charger
                    [0], charger[1], charger[2], charger[3], charger[4], 
                                    charger[5], charger[6], charger[7], charger[8], charger[9], charger[10], charger[11], 
                                    msta[0], msta[1], control, dl, auth, command, plug[0], plug[1], current[0], current[1], 
                                    current[2], current[3], current[4], current[5], random[0], random[1],
                                    parameter, balance[0], balance[1], balance[2], balance[3]};
                for(int j = 0; j < checksumList.length; j++) {
//                     System.out.println(j + " : " + checksumList[j]);
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