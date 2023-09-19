package bienergy_mst.bienergy_mst.parameter;

import bienergy_mst.bienergy_mst.utils.constant.ChargerEVCConstant;

public class ReadCurrentData {
    private String chargerId;
    private String plugNumber;
    public ReadCurrentData(String chargerId, String plugNumber) {
        this.chargerId = chargerId;
        this.plugNumber = plugNumber;
    }

    public byte [] requestReadCurrentData() {
        byte [] hexdata = new byte[19];
        boolean flag = true;
        Long [] start = new Long[2];
        Long [] charger = new Long[6];
        Long [] msta = new Long[2];
        Long control = null;
        Long DL = null;
        Long [] DA = new Long[2];
        Long [] DN = new Long[2];
        int index = 0, count = 0;
        hexdata[index] = ChargerEVCConstant.FRAME_START;
        start[0] = Long.parseLong("68", 16);
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
                hexdata[index] = ChargerEVCConstant.READ_CURRENT;
                control = Long.parseLong("01", 16);
            }else if(index == 11) {
                hexdata[index] = (byte) 0x04;
                DL = Long.parseLong("04", 16);
            }else if(index == 12) {
                hexdata[index] = ChargerEVCConstant.FRAME_ZERO;
                count = 0;
            }else if(index == 13) {
                hexdata[index] = (byte) 0x01;
                DA[0] = Long.parseLong("01", 16);
            }else if(index == 14) {
                hexdata[index] = (byte) Long.parseLong(plugNumber, 16);
                DA[1] = Long.parseLong(plugNumber, 16);
            }else if(index == 15) {
                hexdata[index] = (byte) 0x60;
                DN[0] = Long.parseLong("60", 16);
            }else if(index == 16) {
                hexdata[index] = (byte) 0xB8;
                DN[1] = Long.parseLong("B8", 16);
            }else if(index == 17) {
                Long checksum = Long.parseLong("00", 16);
                Long [] checksumList = {start[0], start[1], charger[0], charger[1], charger[2], charger[3], charger[4], 
                    charger[5], msta[0], msta[1], control, DL, DA[0], DA[1], DN[0], DN[1]};
                String hexFormat = "";
                for(int i = 0; i < checksumList.length; i++) {
                    checksum += checksumList[i];
                }
                hexFormat = String.format(ChargerEVCConstant.FORMAT, checksum).substring(1, 3);
                checksum = Long.parseLong(hexFormat, 16);
                hexdata[index] = checksum.byteValue();
            }else if(index == 18) {
                hexdata[index] = ChargerEVCConstant.FRAME_END;
            }else  {
                flag = false;
            }
        }
        return hexdata;
    }
}
