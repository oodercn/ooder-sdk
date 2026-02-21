package net.ooder.agent.client.iot;

//import  net.ooder.common.logging.Log;
//import  net.ooder.common.logging.LogFactory;
//import  net.ooder.engine.JDSConstants;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.lang.reflect.UndeclaredThrowableException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class PYGen {

    /**
     * 转码位数 [1-8]
     */
    private static final int CODE_DIGITS = 4;

    /**
     * 时间位数
     */
    private static final int TIME_BLOCK_DIGITS = 4;

    /**
     * 初始化时间
     */
    private static final long INITIAL_TIME = 0;

    /**
     * 数子量级
     */
    private static final int[] DIGITS_POWER = {1, 10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000000};

    //private static final Log logger = LogFactory.getLog(JDSConstants.CONFIG_KEY, PYGen.class);


    public static void main(String[] args) {
        testPwd();
    }
    public static void testPwd(){
        /** 接口传入当天8点和12点时间戳*/

//				for (int i = 0; i < 24; i++) {
//					for (int j = i+1; j < 24; j++) {
//						//long  startTime=getTimeInMillis("12");
//						long  startTime=getTimeInMillis(Integer.toString(i));
//						System.out.println("08点时间戳："+startTime);
//
//						//long  endTime=getTimeInMillis("20");
//						long  endTime=getTimeInMillis(Integer.toString(j));
//						System.out.println("22点时间戳："+endTime);
//
//						//String pwd=getOfflinePwd(startTime,endTime,"00158d00026c5369",12345678);
//						String pwd=getOfflinePwd(startTime,endTime,"00158d00026C5415",777704);
//					}
//				}

                      long  startTime=getTimeInMillis("14");
                      long  endTime=getTimeInMillis("16");
                      String pwd=getOfflinePwd(startTime,endTime,"00158D00026C54B4",11111111);
    }


    /**
     * 生成离线密码
     * @author eric
     * @date  11:08 AM 2018/12/17
     *
     * @Param: startTime 开始时间
     * @Param: endTime   结束时间
     * @Param: ieee      ieee地址
     * @Param: seed      密码种子
     * @return java.lang.String
     */
    public static String getOfflinePwd( long  startTime,long  endTime,String ieee,Integer seed){

        /**将ieee处理成大写*/
        String  ieeeUpperCase = ieee.toUpperCase();
				System.out.println("MAC:" + ieeeUpperCase);

        /** 获取小时数*/
        SimpleDateFormat df = new SimpleDateFormat("HH");
        String  startHour=df.format(startTime);
        String  endHour=df.format(endTime);
        System.out.println("开始小时："+startHour);
        System.out.println("结束小时："+endHour);

        /**时间块*/
        String timeBlock=startHour+endHour;
        System.out.println("时间4位数："+timeBlock);

        /**生成加密算法*/
        /** KEY + seed */
        PYGen totp = new PYGen();

        String pwdBlock = totp.getPassword(ieeeUpperCase,seed,startTime,(Integer.parseInt(endHour)-Integer.parseInt(startHour)) * 60 * 60 * 1000);
        System.out.println("密码4位数："+pwdBlock);

        int  timeBlockInt=Integer.parseInt(timeBlock);
        int  pwdBlockInt=Integer.parseInt(pwdBlock);
        System.out.println("timeBlockInt："+timeBlockInt);
        System.out.println("pwdBlockInt："+pwdBlockInt);

        /**前4位和后4位相减取绝对值*/
        int sub=pwdBlockInt-timeBlockInt;
        int subAbs=Math.abs(sub);
        System.out.println("sub："+sub);
        System.out.println("subAbs："+subAbs);

        /**补齐时间位数*/
        String subAbsStr=fillGap(""+subAbs,TIME_BLOCK_DIGITS);
        System.out.println("subAbsStr："+subAbsStr);

        String pwd=pwdBlock +subAbsStr;
        System.out.println("生成密码："+pwd);
        //logger.info("离线密码:"+pwd+" 开始:"+startTime+" 结束:"+endTime+" ieee:"+ieeeUpperCase+" seed:"+seed);
        return  pwd;
    }

    /**
     * @author eric
     * @date  11:09 AM 2018/12/17
     *
     * @Param: ieee  ieee地址
     * @Param: seed  密码种子
     * @Param: interval 开始和结束时间间隔
     * @return java.lang.String
     */
    String getPassword(String ieee,Integer seed,long startTime,Integer interval) {
        String strSeed = Integer.toHexString(seed).toUpperCase();
        StringBuilder seedBuilder = new StringBuilder(strSeed);
        while (seedBuilder.length() < 8) {
            seedBuilder.insert(0, "0");
        }
        strSeed = seedBuilder.toString();
        //logger.info("seed :" + Integer.toString(seed) + ", HexStr is :" + strSeed);
        String totp = generateMyTOTP(ieee,strSeed.toUpperCase(), startTime, interval);
        //String totp = generateMyTOTP(ieee,strSeed.toUpperCase(), null, interval);
        return totp;

    }

    /**
     * 生成一次性密码
     *
     * @param seed     ---> "",key
     * @param now      开始时间
     * @param interval 周期 ---> seed 密码
     * @return String
     */
    public String generateMyTOTP(String ieee,String seed, Long now, Integer interval) {
        if (now == null || now == 0) {
            now = new Date().getTime();
        }
        System.out.println("now " + Long.toString(now) + "cnt:" + timeFactor(now, interval));
        String time = Long.toHexString(timeFactor(now, interval)).toUpperCase();
        return generateTOTP(seed + ieee, time); //
    }
    private static String generateTOTP(String key, String time) {
        return generateTOTP(key, time, "HmacSHA1"); // modify by au 2018/11/19
    }

    private static String generateTOTP(String key, String time, String crypto) {
        StringBuilder timeBuilder = new StringBuilder(time);
        while (timeBuilder.length() < 8) // modify by au 2018/11/19
            timeBuilder.insert(0, "0");
        time = timeBuilder.toString();
        byte[] msg = hexStr2Bytes(time); // modify by au 2018/11/19
        byte[] k = hexStr2Bytes(key); // modify by au 2018/11/19
        byte[] hash = hmac_sha(crypto, k, msg);

        System.out.println("msg :" + bytesToHexString(msg) + ",k :" + bytesToHexString(k) + ",hash :" + bytesToHexString(hash));
        return truncate(hash);
    }
    /**
     * 获取动态因子
     *
     * @param targetTime 指定时间
     * @return long
     */
    private static long timeFactor(long targetTime, Integer interval) {
        long y = (targetTime - INITIAL_TIME) / 1000 / (interval / 1000);
        return (targetTime - INITIAL_TIME) / 1000 / (interval / 1000);
    }

    /**
     * 哈希加密
     *
     * @param crypto   加密算法
     * @param keyBytes 密钥数组
     * @param text     加密内容
     * @return byte[]
     */
    private static byte[] hmac_sha(String crypto, byte[] keyBytes, byte[] text) {
        try {
            Mac hmac;
            hmac = Mac.getInstance(crypto);
            SecretKeySpec macKey = new SecretKeySpec(keyBytes, "AES");
            hmac.init(macKey);
            return hmac.doFinal(text);
        } catch (GeneralSecurityException gse) {
            throw new UndeclaredThrowableException(gse);
        }
    }

    private static byte[] hexStr2Bytes(String hex) {
        byte[] bArray = new BigInteger("10" + hex, 16).toByteArray();
        byte[] ret = new byte[bArray.length - 1];
        System.arraycopy(bArray, 1, ret, 0, ret.length);
        return ret;
    }

    private static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v).toUpperCase();
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    /**
     * 补位
     * @author eric
     * @date  11:12 AM 2018/12/17
     *
     * @Param: str 密码
     * @Param: digits 达到位数
     * @return java.lang.String
     */
    public static String fillGap(String str,int digits){
        int a=digits-str.length();
        String zero="0";
        for (int i = 0; i <a ; i++) {
            str=zero+str;
        }
        return str;
    }


    /**
     * 截断函数
     *
     * @param target 20字节的字符串
     * @return String
     */
    private static String truncate(byte[] target) {
        StringBuilder result;
        int offset = 0; // modify by au 2018/11/19
        int binary = ((target[offset] & 0x7f) << 24) | ((target[offset + 1] & 0xff) << 16) | ((target[offset + 2] & 0xff) << 8) | (target[offset + 3] & 0xff);

        int otp = binary % DIGITS_POWER[CODE_DIGITS];
        System.out.println("otp is " + Integer.toString(otp));
        result = new StringBuilder(Integer.toString(otp));
        while (result.length() < CODE_DIGITS) {
            result.insert(0, "0");
        }
        return result.toString();
    }


    /**获取当天某一时刻时间戳*/
    public static long getTimeInMillis(String  hour){
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour));
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }



}
