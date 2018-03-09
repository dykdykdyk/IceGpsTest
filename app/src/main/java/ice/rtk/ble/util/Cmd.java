package ice.rtk.ble.util;

/**
 * Created by 111 on 2017/11/22.
 */

public class Cmd {
    public static String CONNECT = "$ICEGPS,CONNECT,1";
    public static String DIS_CONNECT = "$ICEGPS,CONNECT,0";
    public static String TEST = "$ICEGPS,TESTMODE,1";
    public static String DIS_TEST = "$ICEGPS,TESTMODE,0";
    public static String SN = "$ICEGPS,FACTORY,SN,";
    public static String GET_DATA = "$ICEGPS,RAWDATA,1,";
    public static String DIS_DATA = "$ICEGPS,RAWDATA,0,";
    public static String GET_STATUS = "$ICEGPS,GETSTATUS";
    public static String GET_VER = "$ICEGPS,GETVER";
    public static String GET_RADIO = "$ICEGPS,GETRADIO";
    public static String SET_RADIO = "$ICEGPS,SETRADIO,";
    public static String SET_XYZ = "$ICEGPS,SETPOS,";
    public static String ERROR = "$ICEGPS,ERROR*7F";
    public static String OK = "$ICEGPS,OK*23";

    public static String currentCMD;

    //计算校验值
    public static String addCheckSum(String s) {
        byte sum = 0;
        for (int i = 1; i < s.length(); i++) {
            sum ^= s.charAt(i);
        }
        return s + "*" + Integer.toHexString(sum) + "\r\n";
    }

//    public static void write(String cmd) {
//        currentCMD = cmd;
//        byte[] bytes = addCheckSum(cmd).getBytes();
//        RxBleHelper.getInstance().writeData(bytes);
//    }
    public static byte[] write(String cmd) {
        currentCMD = cmd;
        byte[] bytes = addCheckSum(cmd).getBytes();
//        RxBleHelper.getInstance().writeData(bytes);
        return bytes;
    }
}
