package bienergy_mst.bienergy_mst.utils.constant;

public class ChargerEVCConstant {
    //파라미터 고정 값
    public static final byte FRAME_START = (byte) 0x68;
    public static final byte FRAME_ZERO = (byte) 0x00;
    public static final byte FRAME_END = (byte) 0x16;

    // 파라미터 제어 코드 값
    public static final byte LOGIN = (byte) 0x21;
    public static final byte HEARTBEAT = (byte) 0x24;
    public static final byte ERROR_REPORT = (byte) 0x19;
    public static final byte WRITING = (byte) 0x08;
    public static final byte BACK_CONTROL = (byte) 0x0A;
    public static final byte READ_CURRENT = (byte) 0x01;
    public static final byte ROCORD_DATA = (byte) 0x05;

    // 파라미터 데이터 길이
    public static final int CHARGER_ID = 6;
    public static final int MSTA_SEQ = 2;
    public static final int DA = 2;
    public static final int DL = 2;
    public static final int MPN = 1;
    public static final int PWD = 3;
    public static final int DATA_ACCOUNT = 8;
    public static final int DATA_CARD = 8;
    public static final int TRANSACTION_SERIAL = 15;
    public static final int BALANCE = 4;

    //셋팅값
    public static final String FORMAT = "%02X";
}
