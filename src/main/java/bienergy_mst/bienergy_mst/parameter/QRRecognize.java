package bienergy_mst.bienergy_mst.parameter;

import bienergy_mst.bienergy_mst.utils.constant.ChargerEVCConstant;

public class QRRecognize {
    private String chargerId;
    public QRRecognize(String chargerId) {
        this.chargerId = chargerId;
    }

    public byte [] QRDataRecognize(String qdata, int plugNumber) {
        byte [] hexdata = new byte[214];
        boolean flag = true;
        int index = 0;
        Long [] start = new Long[2];
        Long [] charger = new Long[6];
        Long [] msta = new Long[2];
        Long control = null;
        Long dl = null;
        Long mpn = null;
        Long auth = null;
        Long [] dn = new Long[2];
        Long [] qr = new Long[qdata.length()];
        int i = qdata.length();
        int count = 0;
        hexdata[index] = ChargerEVCConstant.FRAME_START;
        start[0] = Long.parseLong("68", 16);
        count = 0;
        System.out.println(chargerId.length());
        for(int k = 0; k < chargerId.length(); k+= 2) {
            String hex = chargerId.substring(k, k+2);
            if(k >= 0 && k < 12) {
                index++;
                hexdata[index] =(byte) Long.parseLong(hex, 16);
                charger[count] = Long.parseLong(hex, 16);
                count++;
            }
            if(k >= 12 && k < 16) {
                if(k == 12) {
                    count = 0;
                }
                index++;
                hexdata[index] = (byte)Long.parseLong(hex, 16);
                msta[count] = Long.parseLong(hex, 16);
                count++;
            }
            if(k == 16) {
                index++;
                hexdata[index] = (byte) Long.parseLong(hex, 16);
                start[1] = Long.parseLong(hex, 16);
            }if(k == 18) {
                index++;
                hexdata[index] = ChargerEVCConstant.WRITING;
                control = Long.parseLong("08", 16);            
            }
        }
        while(flag) {
            index++;      
            if(index == 11) {
                hexdata[index] = (byte) Long.parseLong("C7", 16);
                dl = Long.parseLong("C7", 16);
                count = 0;
            }else if(index == 12) {
                hexdata[index] = (byte) Long.parseLong("00", 16);
            }else if(index == 13) {
                int plug = (int) Math.pow(2, (double) plugNumber);
                hexdata[index] = (byte) Long.parseLong(Integer.toString(plug), 16);
                mpn = Long.parseLong(Integer.toString(plug), 16);
            }else if(index == 14) {
                hexdata[index] = (byte) Long.parseLong("11", 16);
                auth = Long.parseLong("11", 16);
            }else if(index >= 15 && index < 18) {
                hexdata[index] = (byte) 0x00;
            }else if(index == 18) {
                hexdata[index] = (byte) 0x41;
                dn[0] = Long.parseLong("41", 16);
            }else if(index == 19) {
                hexdata[index] = (byte) 0x89;
                dn[1] = Long.parseLong("89", 16);
            }else if(index >= 20 && index < 20 + (192 - (qdata.length()))) {
                hexdata[index] = (byte) 0x00;
            }else if(index >=  20 + (192 - qdata.length()) && index < 20 + 192) {
                char ch = qdata.charAt(i - 1);
                String hex = String.format("%02X", (int)ch);
                hexdata[index] = (byte) Long.parseLong(hex, 16);
                qr[count] = Long.parseLong(hex, 16);
                i--;
                count++;
            }else if(index == 212) {
                Long checksum = Long.parseLong("00", 16);
                Long [] checksumList = {start[0], start[1], charger[0], charger[1], charger[2], charger[3], charger[4], charger[5], msta[0], msta[1], control,
                                        dl, mpn, auth, dn[0], dn[1]};
                String foramtHex = "";
                for(int j = 0; j < checksumList.length; j++) {
                    // System.out.println(i + " : " + checksumList[i]);
                    checksum += checksumList[j];
                }
                for(int j = 0; j < qr.length; j++) {
                    checksum += qr[j];
                }
                foramtHex = String.format(ChargerEVCConstant.FORMAT, checksum).substring(1, 3);
                checksum = Long.parseLong(foramtHex, 16);
                hexdata[index] = checksum.byteValue();
            }else if(index == 213) {
                hexdata[index] = ChargerEVCConstant.FRAME_END;
            }else if(index == 214) {
                flag = false;
            }
        }
        return hexdata;
    }
}

