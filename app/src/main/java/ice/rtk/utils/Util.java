package ice.rtk.utils;

import android.os.Environment;
import android.view.View;


import java.io.BufferedReader;
import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ice.rtk.view.coordinate_data.Point_List;
import ice.rtk.view.coordinate_data.Point_Name;

public class Util {
    public static String APP_SDCARD_PATH = "icegps/";
    public static String ADD_POINT_BUNDLE = "add_point_bundle";
    public static String ADD_POINT_NAME = "add_point_name";
    //插入点之前还是之后的标志位
    public static String INSERT_POINT_AFTER = "insert_point_after";
    public static String INSERT_POINT_PRE = "insert_point_pre";
    public static String INSERT_POINT = "insert_point";
    public static String DRAW_POINT_LAT = "draw_point_lat";
    public static String DRAW_POINT_LON = "draw_point_lon";
    public static String DRAW_POINT_ELE = "draw_point_ele";
    public static String FILESPATHPOINT = Environment.getExternalStorageDirectory().getPath() + "/" + Util.APP_SDCARD_PATH +"/points/";
    public static String FILESPATHLINES= Environment.getExternalStorageDirectory().getPath() + "/" + Util.APP_SDCARD_PATH +"/lines/";
    public static File file = new File(Environment.getExternalStorageDirectory().getPath() + "/" + Util.APP_SDCARD_PATH + "/default.gpx");
    /**
     * 字符串转换unicode
     */
    public static String string2Unicode(String string) {

        StringBuffer unicode = new StringBuffer();

        for (int i = 0; i < string.length(); i++) {

            // 取出每一个字符
            char c = string.charAt(i);

//	        int a = Integer.parseInt(Integer.toHexString(c));

            // 转换为unicode
            unicode.append("\\u" + Integer.toHexString(c));
        }

        return unicode.toString();
    }

    /**
     *
     * @return 创建航点的文件头
     */
    public static String CreatePointFileHead(){
      String str=  "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?>\n" +
                "<gpx xmlns=\"http://www.topografix.com/GPX/1/1\" creator=\"ICEGPS 2016.03.17\" version=\"1.1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
                "  <metadata>\n" +
                "    <link href=\"http://www.icegps.com\">\n" +
                "      <text>ICEGPS RTK</text>\n" +
                "    </link>\n" +
                "    <time>"+getData_normal()+"</time>\n" +
                "  </metadata></gpx>\n";
        return str;
    }

    /**
     * 创建航线的文件头
     * @return
     * @param linename 创建的航线文件航线名
     */
    public static String CreateLinesHead(String linename){
      String str=  "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?>\n" +
              "<gpx xmlns=\"http://www.topografix.com/GPX/1/1\" creator=\"ICEGPS 2017/05/27 15:09\" version=\"1.1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd\">\n" +
              "  <metadata>\n" +
              "    <link href=\"http://www.icegps.com\">\n" +
              "      <text>ICEGPS 610</text>\n" +
              "    </link>\n" +
              "    <time>"+getDataTZ()+"</time>\n" +
              "    <bounds maxlat=\"22.70731400\" maxlon=\"114.24908600\" minlat=\"22.70731400\" minlon=\"114.24084700\"/>\n" +
              "  </metadata>\n" +
              "  <rte>\n" +
              "     <name>"+linename+"</name>\n" +
              "  </rte></gpx>";
        return str;


    }
    /**
     * 小端序转大端序
     *
     * @param a
     * @return
     */
    public static int lowToInt(int a) {
        return (((a & 0xFF) << 24) | (((a >> 8) & 0xFF) << 16) | (((a >> 16) & 0xFF) << 8) | ((a >> 24) & 0xFF));
    }

    /**
     * int [] 转 byte[];
     *
     * @param data
     * @return
     */
    public static byte[] intArraysTobyteArrays(int[] data) {
        byte[] b = new byte[data.length];
        for (int i = 0; i < data.length; i++) {
            b[i] = (byte) (data[i] & 0xff);
        }
        return b;
    }

    /**
     * 转换为16进制String
     *
     * @param bytes
     * @return
     */
    public static String bytes2hex03(byte[] bytes) {
        final String HEX = "0123456789abcdef";
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            // 取出这个字节的高4位，然后与0x0f与运算，得到一个0-15之间的数据，通过HEX.charAt(0-15)即为16进制数
            sb.append(HEX.charAt((b >> 4) & 0x0f));
            // 取出这个字节的低位，与0x0f与运算，得到一个0-15之间的数据，通过HEX.charAt(0-15)即为16进制数
            sb.append(HEX.charAt(b & 0x0f));
        }
        return sb.toString();
    }

    /**
     * 转换为16进制String数组
     *
     * @param data
     * @return
     */
    public static String[] byteTo16String(byte[] data) {
        List<String> list = new ArrayList<String>();
        for (byte b : data) {
            int xx = b & 0xff;
            if (xx >= 0 & xx < 10) {
                list.add(("0x0" + Integer.toHexString(xx)));
            } else {
                list.add(("0x" + Integer.toHexString(xx)));
            }
        }
        String[] st = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            st[i] = list.get(i);
        }
        return st;
    }

    /**
     * byte数组中取int数值，本方法适用于(低位在后，高位在前)的顺序。和intToBytes2（）配套使用
     */
    public static int bytesToInt2(byte[] src, int offset) {
        int value;
        value = (int) (((src[offset] & 0xFF) << 24)
                | ((src[offset + 1] & 0xFF) << 16)
                | ((src[offset + 2] & 0xFF) << 8) | (src[offset + 3] & 0xFF));
        return value;
    }

    public static int dykbytesToInt2(byte[] src, int offset) {
        int value;
        value = (int) (((src[offset] & 0xFF) << 8)
                | ((src[offset + 1] & 0xFF)));
        return value;
    }


    /**
     * 判断字符串是否包含中文
     *
     * @param str
     * @return
     */
    public static boolean isContainChinese(String str) {

        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;
    }

    private String getSDCardPath() {
        File sdcardDir = null;
        // 判断SDCard是否存在
        boolean sdcardExist = Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
        if (sdcardExist) {
            sdcardDir = Environment.getExternalStorageDirectory();
        }
        return sdcardDir.toString();
    }

    /**
     * byte数组中取int数值，本方法适用于(低位在前，高位在后)的顺序。
     *
     * @param ary    byte数组
     * @param offset 从数组的第offset位开始
     * @return int数值
     */
    public static int bytesToInt(byte[] ary, int offset) {
        int value;
        value = (int) ((ary[offset] & 0xFF) | ((ary[offset + 1] << 8) & 0xFF00)
                | ((ary[offset + 2] << 16) & 0xFF0000) | ((ary[offset + 3] << 24) & 0xFF000000));
        return value;
    }
    /**
     * 字符串转换为byte数组
     */
    public static byte[] strToByteArray(String str) {
        byte[] byBuffer = new byte[16];
        byBuffer = str.getBytes();
        return byBuffer;

    }

    /**
     * 时间转换
     */
    public static byte[] nowTimeToBytes() {
        byte[] result = new byte[4];
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR) - 2016;
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
//		int hour = 22;

        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        result[0] = (byte) ((year << 2) | (month >> 2 & 0b11));
        result[1] = (byte) (((month & 0b0011) << 6) | (day << 1) | (hour >> 4 & 0b1));
        result[2] = (byte) (((hour & 0b01111) << 4) | (minute >> 2 & 0b1111));
        result[3] = (byte) (((minute & 0b000011) << 6) | second);
        return result;
    }

    public static byte[] intToByteArray(int i) {
        byte[] result = new byte[4];
        //由高位到低位
        result[0] = (byte) ((i >> 24) & 0xFF);
        result[1] = (byte) ((i >> 16) & 0xFF);
        result[2] = (byte) ((i >> 8) & 0xFF);
        result[3] = (byte) (i & 0xFF);
        return result;
    }

    public static byte[] longTimeToByteArray(int i) {
        byte[] result = new byte[2];
        //由高位到低位
        result[0] = (byte) ((i >> 8) & 0xFF);
        result[1] = (byte) ((i >> 0) & 0xFF);

        return result;
    }

    /*
     * 将时间戳转换为时间
     */
    public static String stampToDate(Long s) {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        long lt = s;
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }


    /**
     * 测量View的宽高
     *
     * @param view View
     */
    public static void measureWidthAndHeight(View view) {
        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * //保存到文件流中
     *
     * @param srcStr     原始字符
     * @param replaceStr 需要替换的字符
     * @param file       文件路径
     * @param flag       是否需要关闭流
     * @throws IOException
     */
    public static void replaceData(String srcStr, String replaceStr, File file, boolean flag) {
        // 读
//        File file = new File("suncity.txt");
        FileReader in = null;
        BufferedReader bufIn = null;
        CharArrayWriter tempStream = null;
        String line = null;
        try {
//            if (isInit) {
//
//            } else {
                in = new FileReader(file);
                bufIn = new BufferedReader(in);
                tempStream = new CharArrayWriter();
//            }
            while ((line = bufIn.readLine()) != null) {
                // 替换每行中, 符合条件的字符串
                line = line.replaceAll(srcStr, replaceStr);
                // 将该行写入内存
                tempStream.write(line);
                // 添加换行符
                tempStream.append(System.getProperty("line.separator"));
            }
                // 关闭 输入流
                bufIn.close();
                // 将内存中的流 写入 文件
                FileWriter out = new FileWriter(file);
                tempStream.writeTo(out);
                out.close();
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 内存流, 作为临时流
        // 替换
    }

    public static void SaveChange(Point_Name srcPN, Point_List srcPL, Point_Name newPN, Point_List newPL) {
//         int name =0,Discript=0,Mileage=0,Lat=0,Lon=0,Ele=0;
        if (!srcPN.getName().equals(newPN.getName())) {
            replaceData(srcPN.getName(), newPN.getName(), file, false);
//            name =1;
        }
        if (!srcPL.getDiscript().equals(newPL.getDiscript())) {
            replaceData(srcPL.getDiscript(), newPL.getDiscript(), file, false);
//            Discript =1;
        }
        if (!srcPL.getMileage().equals(newPL.getMileage())) {
            replaceData(srcPL.getMileage(), newPL.getMileage(), file, false);
//            Mileage=1;
        }
        if (!srcPL.getLat().equals(newPL.getLat())) {
            replaceData(srcPL.getLat(), newPL.getLat(), file, false);
//            Lat =1;
        }
        if (!srcPL.getLon().equals(newPL.getLon())) {
            replaceData(srcPL.getLon(), newPL.getLon(), file, false);
//            Lon=1;
        }
//        if(!srcPL.getEle().equals(newPL.getEle())){
        replaceData(srcPL.getEle(), newPL.getEle(), file, true);
    }


    /**
     * 向航点文件中添加点
     * @param srcStr 添加点
     * @param file
     */
    public static void addtoPointFile(String srcStr, File file) {
        // 读
//	        File file = new File("suncity.txt");
//        FileReader in = null;
//        BufferedReader bufIn = null;
//        CharArrayWriter tempStream = null;
//        String line = null;
//        String srcline =null;
        RandomAccessFile raf =null;
        try {
//            in = new FileReader(file);
//            bufIn = new BufferedReader(in);
//            tempStream = new CharArrayWriter();
//            while ((line = bufIn.readLine()) != null) {
//                // 替换每行中, 符合条件的字符串
//                srcline =srcline+line;
//            }
//            // 关闭 输入流
//            bufIn.close();
//            srcline =srcline.trim();
//            System.out.println(srcline);
            raf=new RandomAccessFile(file, "rwd");
            raf.seek(raf.length()-6);
            raf.write(srcStr.getBytes());
//	            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            if(raf != null){
                try {
                    raf.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }
    }

    /**
     *
     * @param srcStr 向航线文件中添加添加点
     * @param file 文件名
     */
    public static void addtoLinesFile(String srcStr, File file) {
        RandomAccessFile raf =null;
        try {
            raf=new RandomAccessFile(file, "rw");
            raf.seek(raf.length()-13);
            raf.write(srcStr.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            if(raf != null){
                try {
                    raf.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }
    }

    /**
     * 航线文件需要插入的点
     * @param srcStr 需要插入的内容
     * @param foundString 根据当前选择的点进行判断
     * @param flag 标志位 ==0 表示插入当前点之前  ==1  表示之后
     * @param file ~~
     */
    public static void AddData(String srcStr,String foundString,int flag, File file) {
        // 读
//	        File file = new File("suncity.txt");
        FileReader in = null;
        BufferedReader bufIn = null;
        CharArrayWriter tempStream = null;
        String line = null;
        StringBuilder srcline =new StringBuilder();
        try {
            in = new FileReader(file);
            bufIn = new BufferedReader(in);
            tempStream = new CharArrayWriter();
            boolean isFound=false;
            int counts =0;
            while ((line = bufIn.readLine()) != null) {
                // 替换每行中, 符合条件的字符串
                if(line !=null){
                    if(line.contains(foundString)){
                        if(flag ==0){
                            //插入之前
                            srcline.insert(0,srcStr.length());
                            line=srcStr+line;
                        }else if(flag ==1){
                            //插入之后
                            isFound =true;
                        }
                    }
                    if(isFound){
                        counts++;
                        if(counts==8){
                            line=line+"\n"+srcStr;
                        }
                    }
                }
                tempStream.write(line);
                // 添加换行符
                tempStream.append(System.getProperty("line.separator"));
            }
            // 关闭 输入流
            bufIn.close();
            // 将内存中的流 写入 文件
            System.out.println(srcline);
            FileWriter out = new FileWriter(file);
            tempStream.writeTo(out);
            out.close();
//	            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     *  获取当前时间
     * @return
     */
    public static String getData_normal(){
        Date date = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");
        String str = df.format(date);
        return str;
    }
    /**
     * 转化为 yyyy-MM-dd'T'HH:mm:ss.SSSZ 这种格式
     * @return
     */
   public static String getDataTZ(){
       Date date = new Date();
       SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
       String str = df.format(date);
      return str;
   }

    /**
     * 2017-05-18T10:26:10.488Z转化为yyyy-MM-dd HH:mm:ss
     */
    public static  String getData(String src) {
//        String dateStr = "2017-05-18T10:26:10.488Z";
        SimpleDateFormat dff = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.ENGLISH);//输入的被转化的时间格式
        SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//需要转化成的时间格式
//       SimpleDateFormat df2 = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date1 = null;
        try {
            date1 = dff.parse(src);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String str1 = df1.format(date1);
//       String str2 = df2.format(date1);
        System.out.println("str1 is "+str1);
//       System.out.println("str2 is "+str2);
        return str1;
    }
    public static double getfloatScale(double a,int keepnumber){
        BigDecimal bb  =   new BigDecimal(a);
        double   f1   =  bb.setScale(keepnumber, BigDecimal.ROUND_HALF_UP).floatValue();
        return f1;
    }

}
