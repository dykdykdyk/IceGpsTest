package test.icegps.rtk;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;


import com.google.gson.Gson;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.BufferedReader;
import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;

import ice.rtk.Utils.Util;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

//        assertEquals("test.icegps.icegpstest", appContext.getPackageName());
//        String lat =11.1111111111+"";
//        String lon =144.23234324+"";
//        String ele =78.4+"";
//        String descript ="20180129";
//        String name ="PDYK";
//        String str ="<wpt lat=\""+lat+""+"\"  lon=\""+lon+""+"\"\n"+
//                "<ele>"+ ele+"</ele>\n" +
//                "<time>"+descript+"</time>\n" +
//                "<name>"+name+"</name>\n" +
//                "<cmt></cmt>\n" +
//                "<desc>"+descript+"</desc>\n" +
//                "<sym>0</sym>\n" +
//                "</wpt></gpx>";
//        replaceData(str, Util.file);
    }

    public static void replaceData(String srcStr, File file) {
        // 读
//	        File file = new File("suncity.txt");
        FileReader in = null;
        BufferedReader bufIn = null;
        CharArrayWriter tempStream = null;
        String line = null;
        String srcline =null;
        RandomAccessFile raf =null;
        try {
            in = new FileReader(file);
            bufIn = new BufferedReader(in);
            tempStream = new CharArrayWriter();
            while ((line = bufIn.readLine()) != null) {
                // 替换每行中, 符合条件的字符串
                srcline =srcline+line;
            }
            // 关闭 输入流
            bufIn.close();
            srcline =srcline.trim();
            System.out.println(srcline);
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
}
