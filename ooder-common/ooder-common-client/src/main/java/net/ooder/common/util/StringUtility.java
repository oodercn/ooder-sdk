/**
 * $RCSfile: StringUtility.java,v $
 * $Revision: 1.1 $
 * $Date: 2025/07/08 00:25:55 $
 * <p>
 * Copyright (C) 2003 spk, Inc. All rights reserved.
 * <p>
 * This software is the proprietary information of spk, Inc.
 * Use is subject to license terms.
 */
/**
 * $RCSfile: StringUtility.java,v $
 * $Revision: 1.0 $
 * $Date: 2025/08/25 $
 * <p>
 * Copyright (c) 2025 ooder.net
 * </p>
 * <p>
 * Company: ooder.net
 * </p>
 * <p>
 * License: MIT License
 * </p>
 */
package net.ooder.common.util;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * <p>
 * Title: 常用代码打包
 * </p>
 * <p>
 * Description: 字符串操作常用的方法
 * </p>
 * <p>
 * Copyright: Copyright (c) 2002-12-5
 * </p>
 * <p>
 * Company: raddev.cn
 * </p>
 *
 * @author wenzhang li
 * @version 1.0
 */
public final class StringUtility {

    private static final char[] newLine = new char[2];

    static {
        newLine[0] = 13;
        newLine[1] = 10;
    }

    private static final String newLineString = new String(newLine);

    private static final String[] htmlSpecial = {"\u0026", "\u00A9", "\u00AE", "\u2122", "\"", "\u003C", "\u003E", newLineString};
    private static final String[] htmlESC = {"&amp;", "&copy;", "&reg;", "&trade;", "&quot;", "&lt;", "&gt;", "<br>"};

    private static final String[] jsSpecial = {"\\", "'", "\"", newLineString};
    private static final String[] jsESC = {"\\\\", "\\'", "\\\"", "\\n"};

    private static final String[] sqlSpecial = {"'"};
    private static final String[] sqlESC = {"''"};

    /**
     * Private constructor to prevent instantiation.
     */
    private StringUtility() {
    }


    /**
     * 转义单引号 Creation date: (2000-12-20 14:43:29)
     *
     * @param src java.lang.String
     * @return java.lang.String
     */
    public static String convertSingleQuot(String src) {
        if (src == null || src.equals("")) {
            return src;
        }
        int length = src.length();
        StringBuffer tmp = new StringBuffer();
        for (int i = 0; i < length; i++) {
            if (src.charAt(i) == '\'') {
                tmp.append("\'\'");
            } else {
                tmp.append(src.charAt(i));
            }
        }
        return tmp.toString();
    }


    public static String formatUrl(String requestUrl) {
        if (requestUrl.toUpperCase().startsWith("GET")) {
            requestUrl = requestUrl.substring(3);
        }
        requestUrl = formatJavaName(requestUrl, true);
        return requestUrl;
    }


    /**
     * 创建指定数量的随机字符串
     *
     * @param numberFlag 是否是数字
     * @param length
     * @return
     */
    public static String createRandom(boolean numberFlag, int length) {
        String retStr = "";
        String strTable = numberFlag ? "1234567890" : "1234567890abcdefghijkmnpqrstuvwxyz";
        int len = strTable.length();
        boolean bDone = true;
        do {
            retStr = "";
            int count = 0;
            for (int i = 0; i < length; i++) {
                double dblR = Math.random() * len;
                int intR = (int) Math.floor(dblR);
                char c = strTable.charAt(intR);
                if (('0' <= c) && (c <= '9')) {
                    count++;
                }
                retStr += strTable.charAt(intR);
            }
            if (count >= 2) {
                bDone = false;
            }
        } while (bDone);

        return retStr;
    }

    /**
     * 转义数据库通配字符'%','_' Creation date: (2000-12-20 14:49:55)
     *
     * @param src java.lang.String
     * @return java.lang.String
     */
    public static String convertCastChar(String src) {
        if (src == null || src.equals("")) {
            return src;
        }
        int length = src.length();
        StringBuffer tmp = new StringBuffer();
        for (int i = 0; i < length; i++) {
            switch (src.charAt(i)) {
                case '%':
                case '_':
                case '\\':
                    tmp.append("\\");
                    break;
            }
            tmp.append(src.charAt(i));
        }
        return tmp.toString();
    }

    /**
     * 转换常见的Html符号 Creation date: (2000-12-20 14:58:19)
     *
     * @param src java.lang.String
     * @return java.lang.String
     */
    public static String convertForHtml(String src) {
        if (src == null || src.equals("")) {
            return src;
        }
        int length = src.length();
        StringBuffer tmp = new StringBuffer();
        for (int i = 0; i < length; i++) {
            switch (src.charAt(i)) {
                case '<':
                    tmp.append("&lt;");
                    break;
                case '>':
                    tmp.append("&gt;");
                    break;
                case '"':
                    tmp.append("&quot;");
                    break;
                case ' ': {
                    int spaceCount = 0;
                    for (; src.charAt(i) == ' '; i++, spaceCount++)
                        ;
                    for (int j = 0; j < spaceCount / 2; j++) {
                        tmp.append("　");
                    }
                    if (spaceCount % 2 != 0) {
                        tmp.append("&#160;");
                    }
                    --i;
                    break;
                }
                case '\n':
                    tmp.append("<br/>");
                    break;
                case '&':
                    tmp.append("&amp;");
                    break;
                case '\r':
                    break;
                default:
                    tmp.append(src.charAt(i));
                    break;
            }
        }
        return tmp.toString();
    }

    /**
     * Insert the method's description here. Creation date: (2001-6-6 8:43:25)
     *
     * @return java.lang.String
     */
    public static String convertForXml(String src) {
        if (src == null || src.equals("")) {
            return src;
        }
        int length = src.length();
        StringBuffer tmp = new StringBuffer();
        for (int i = 0; i < length; i++) {
            switch (src.charAt(i)) {
                case '<':
                    tmp.append("&lt;");
                    break;
                case '>':
                    tmp.append("&gt;");
                    break;
                case '"':
                    tmp.append("&quot;");
                    break;
                case ' ': {
                    int spaceCount = 0;
                    for (; src.charAt(i) == ' '; i++, spaceCount++)
                        ;
                    for (int j = 0; j < spaceCount / 2; j++) {
                        tmp.append("　");
                    }
                    if (spaceCount % 2 != 0) {
                        tmp.append("&#160;");
                    }
                    --i;
                    break;
                }
                case '&':
                    tmp.append("&amp;");
                    break;
                case '\r':
                    break;
                default:
                    tmp.append(src.charAt(i));
                    break;
            }
        }
        return tmp.toString();
    }

    /**
     * 如果是null字符串, 则返回""字符串
     *
     * @param str String
     * @return String
     */
    public static String filterNull(String str) {
        if (str == null) {
            return new String();
        } else {
            return str;
        }
    }

    /**
     * 如果是null对象, 则返回""字符串
     *
     * @param obj Object
     * @return String
     */
    public static String filterNullObject(Object obj) {
        if (obj == null) {
            return new String();
        } else {
            return obj.toString();
        }
    }

    /**
     * Replace substrings of one string with another string and return altered string.
     *
     * @param original  input string
     * @param oldString the substring section to replace
     * @param newString the new substring replacing old substring section
     * @return converted string
     */
    public static String replace(final String original, final String oldString, final String newString) {
        if (oldString == null || newString == null || oldString.equals(newString)) {
            return original;
        }
        return replace(original, oldString, newString, 0);
    }

    /**
     * Replace substrings of one string with another string and return altered string.
     *
     * @param original  input string
     * @param oldString the substring section to replace
     * @param newString the new substring replacing old substring section
     * @param counts    how many times the replace happen, 0 for all.
     * @return converted string
     */
    public static String replace(final String original, final String oldString, final String newString, final int counts) {
        if (original == null || oldString == null || newString == null) {
            return "";
        }
        if (counts < 0)
            throw new IllegalArgumentException("parameter counts can not be negative");

        final StringBuffer sb = new StringBuffer();

        int end = original.indexOf(oldString);
        int start = 0;
        final int stringSize = oldString.length();

        int currentCount = 0;
        while (end != -1) {
            if (counts == 0 || currentCount < counts) {
                sb.append(original.substring(start, end));
                sb.append(newString);
                start = end + stringSize;
                end = original.indexOf(oldString, start);
                currentCount++;
            } else
                break;
        }

        end = original.length();
        sb.append(original.substring(start, end));

        return sb.toString();
    }

    /**
     * 将一个字符串中带有的HTML特殊字符转换为HTML的转义字符。例如：& --> "amp;" > --> "gt;"
     *
     * @param str 要进行HTML特殊字符串转义的字符串
     * @return 转义后的字符串
     */
    public static String escapeHTMLSpecial(final String str) {
        return escapeSpecial(str, htmlSpecial, htmlESC);
    }

    /**
     * 将一个字符串中带有的JS特殊字符转换为JS的转义字符。例如：\ --> "\\" ' --> "\'" " --> "\""
     *
     * @param str 要进行JS特殊字符串转义的字符串
     * @return 转义后的字符串
     */
    public static String escapeJSSpecial(String str) {
        return escapeSpecial(str, jsSpecial, jsESC);
    }

    /**
     * 将一个字符串中带有的SQL特殊字符转换为转义字符。例如：' --> "''"
     *
     * @param str 要进行SQL特殊字符串转义的字符串
     * @return 转义后的字符串
     */
    public static String escapeSQLSpecial(String str) {
        return escapeSpecial(str, sqlSpecial, sqlESC);
    }

    /**
     * 获得截取到指定字节长度后的字符串，多出部分用指定字符串代替。
     *
     * @param str           需要限定长度的字符串
     * @param maxByteLength 最大字节数
     * @param more          超长情况的替换字符串
     * @return 结果字符串
     */
    public static String getMoreString(final String str, final int maxByteLength, final String more) {
        if (str == null)
            return "";

        int len = 0;
        StringBuffer buf = new StringBuffer();
        char c;
        boolean isSingleChar;
        for (int i = 0; i < str.length(); i++) {
            c = str.charAt(i);
            byte[] bytes = charToByte(c);
            if (bytes[0] == 0) {
                len++;
            } else {
                len += 2;
            }
            if (len > maxByteLength) {
                buf.append(more == null ? "..." : more);
                break;
            } else {
                buf.append(c);
            }
        }

        return buf.toString();
    }

    /**
     * 把由delim分割的字符串分裂并形成字符串数组。例如：String sourceString = "string1;string2;string3"; String[] result =
     * StringUtility.split(sourceString, ";"); 则result是由字符串"string1","string2","string3"组成的数组。
     *
     * @param sourceString 要分裂的字符串
     * @param delim        分隔符
     * @return 分裂并组合后的字符串数组
     */
    public static String[] split(String sourceString, String delim) {
        if (sourceString == null || delim == null) {
            return new String[0];
        }


        StringTokenizer st = new StringTokenizer(sourceString, delim);
        List stringList = new ArrayList();
        for (; st.hasMoreTokens(); stringList.add(st.nextToken()))
            ;
        return (String[]) (stringList.toArray(new String[stringList.size()]));
    }

    // 整数到字节数组的转换
    public static byte[] intToByte(int number) {
        int temp = number;
        byte[] b = new byte[4];
        for (int i = b.length - 1; i > -1; i--) {
            b[i] = new Integer(temp & 0xff).byteValue(); // 将最高位保存在最低位
            temp = temp >> 8; // 向右移8位
        }
        return b;
    }

    // 字节数组到整数的转换
    public static int byteToInt(byte[] b) {
        int s = 0;
        for (int i = 0; i < 3; i++) {
            if (b[i] >= 0)
                s = s + b[i];
            else
                s = s + 256 + b[i];
            s = s * 256;
        }
        if (b[3] >= 0) // 最后一个之所以不乘，是因为可能会溢出
            s = s + b[3];
        else
            s = s + 256 + b[3];
        return s;
    }

    // 字符到字节转换
    public static byte[] charToByte(char ch) {
        int temp = (int) ch;
        byte[] b = new byte[2];
        for (int i = b.length - 1; i > -1; i--) {
            b[i] = new Integer(temp & 0xff).byteValue(); // 将最高位保存在最低位
            temp = temp >> 8; // 向右移8位
        }
        return b;
    }

    // 字节到字符转换
    public static char byteToChar(byte[] b) {
        int s = 0;
        if (b[0] > 0)
            s += b[0];
        else
            s += 256 + b[0];
        s *= 256;
        if (b[1] > 0)
            s += b[1];
        else
            s += 256 + b[1];
        char ch = (char) s;
        return ch;
    }

    // 浮点到字节转换
    public static byte[] doubleToByte(double d) {
        byte[] b = new byte[8];
        long l = Double.doubleToLongBits(d);
        for (int i = 0; i < b.length; i++) {
            b[i] = new Long(l).byteValue();
            l = l >> 8;

        }
        return b;
    }

    // 字节到浮点转换
    public static double byteToDouble(byte[] b) {
        long l;

        l = b[0];
        l &= 0xff;
        l |= ((long) b[1] << 8);
        l &= 0xffff;
        l |= ((long) b[2] << 16);
        l &= 0xffffff;
        l |= ((long) b[3] << 24);
        l &= 0xffffffffl;
        l |= ((long) b[4] << 32);
        l &= 0xffffffffffl;

        l |= ((long) b[5] << 40);
        l &= 0xffffffffffffl;
        l |= ((long) b[6] << 48);

        l |= ((long) b[7] << 56);
        return Double.longBitsToDouble(l);
    }

    private static String escapeSpecial(String str, String[] special, String[] esc) {
        String result = str;
        for (int i = 0; i < special.length; i++) {
            result = replace(result, special[i], esc[i]);
        }
        return result;
    }

    private static final int base64Int(char c) {
        if (c >= 'a')
            if (c <= 'z')
                return (c - 97) + 26;
            else
                return -1;
        if (c >= 'A')
            if (c <= 'Z')
                return (c - 65) + 0;
            else
                return -1;
        if (c >= '0') {
            if (c <= '9')
                return (c - 48) + 52;
            return c != '=' ? -1 : 0;
        }
        if (c == '+')
            return 62;
        return c != '/' ? -1 : 63;
    }

    private static final char base64Char(int i) {
        if (i >= 52) {
            if (i < 62)
                return (char) ((i - 52) + 48);
            if (i == 62)
                return '+';
            return i != 63 ? '?' : '/';
        }
        if (i >= 26)
            return (char) ((i - 26) + 97);
        if (i >= 0)
            return (char) ((i - 0) + 65);
        else
            return '?';
    }

    public static final byte[] base64ToBytes(String s) {
        int k = 0;
        char ac[] = s.toCharArray();
        int i = ac.length;
        int j = 0;
        if (ac[i - 1] == '=')
            j++;
        if (ac[i - 2] == '=')
            j++;
        i = (i / 4) * 3 - j;
        byte abyte0[] = new byte[i];
        int l = 0;
        int i1 = 0;
        while (l + 3 < ac.length) {
            k = base64Int(ac[l + 0]) << 18 | base64Int(ac[l + 1]) << 12 | base64Int(ac[l + 2]) << 6 | base64Int(ac[l + 3]);
            l += 4;
            if (i1 >= i - 2)
                break;
            abyte0[i1++] = (byte) (k >> 16 & 0xff);
            abyte0[i1++] = (byte) (k >> 8 & 0xff);
            abyte0[i1++] = (byte) (k & 0xff);
        }
        if (i1 < abyte0.length)
            abyte0[i1++] = (byte) (k >> 16 & 0xff);
        if (i1 < abyte0.length)
            abyte0[i1++] = (byte) (k >> 8 & 0xff);
        return abyte0;
    }

    public static final String bytesToBase64(byte abyte0[]) {
        int j1 = 0;
        int k1 = 0;
        int i = (((abyte0.length + 3) - 1) / 3) * 4;
        char ac[] = new char[i];
        for (int j = abyte0.length - 2; j1 < j; ) {
            int k = (abyte0[j1 + 0] & 0xff) << 16 | (abyte0[j1 + 1] & 0xff) << 8 | abyte0[j1 + 2] & 0xff;
            j1 += 3;
            ac[k1++] = base64Char(k >> 18 & 0x3f);
            ac[k1++] = base64Char(k >> 12 & 0x3f);
            ac[k1++] = base64Char(k >> 6 & 0x3f);
            ac[k1++] = base64Char(k & 0x3f);
        }

        if (j1 < abyte0.length - 1) {
            int l = (abyte0[j1 + 0] & 0xff) << 16 | (abyte0[j1 + 1] & 0xff) << 8;
            ac[k1++] = base64Char(l >> 18 & 0x3f);
            ac[k1++] = base64Char(l >> 12 & 0x3f);
            ac[k1++] = base64Char(l >> 6 & 0x3f);
            ac[k1++] = '=';
        } else if (j1 < abyte0.length) {
            int i1 = (abyte0[j1 + 0] & 0xff) << 16;
            ac[k1++] = base64Char(i1 >> 18 & 0x3f);
            ac[k1++] = base64Char(i1 >> 12 & 0x3f);
            ac[k1++] = '=';
            ac[k1++] = '=';
        }
        return new String(ac, 0, k1);
    }

    /**
     * 格式化double类型数字 保留decimalDigits位小数
     *
     * @param number        要格式化的double数字
     * @param decimalDigits 保留小数位数
     * @return java.lang.String
     */
    public static String formatNumber(double number, int decimalDigits) {
        String result;
        if (decimalDigits < 0)
            throw new IllegalArgumentException("小数位数decimalDigits不能为负数");
        if (decimalDigits == 0) {
            result = String.valueOf(Math.round(number));
        } else {
            StringBuffer format = new StringBuffer("0.");
            long l = 1;
            for (int i = 0; i < decimalDigits; i++) {
                format.append("0");
                l *= 10;
            }

            StringBuffer d = new StringBuffer();
            d.append(l).append(".");
            for (int i = 0; i < decimalDigits; i++) {
                d.append("0");
            }
            d.append("d");

            DecimalFormat decimalFormat = new DecimalFormat(format.toString());
            result = decimalFormat.format(Math.round(number * l) / Double.parseDouble(d.toString()));
        }

        return result;
    }

    /**
     * 格式化float类型数字 保留decimalDigits位小数
     *
     * @param number        要格式化的float数字
     * @param decimalDigits 保留小数位数
     * @return java.lang.String
     */
    public static String formatNumber(float number, int decimalDigits) {
        String result;
        if (decimalDigits < 0)
            throw new IllegalArgumentException("小数位数decimalDigits不能为负数");
        if (decimalDigits == 0) {
            result = String.valueOf(Math.round(number));
        } else {
            StringBuffer format = new StringBuffer("0.");
            long l = 1;
            for (int i = 0; i < decimalDigits; i++) {
                format.append("0");
                l *= 10;
            }

            StringBuffer d = new StringBuffer();
            d.append(l).append(".");
            for (int i = 0; i < decimalDigits; i++) {
                d.append("0");
            }
            d.append("d");

            DecimalFormat decimalFormat = new DecimalFormat(format.toString());
            result = decimalFormat.format(Math.round(number * l) / Double.parseDouble(d.toString()));
        }

        return result;

    }

    /**
     * 格式化String类型数字 保留decimalDigits位小数
     *
     * @param number        要格式化的String数字
     * @param decimalDigits 保留小数位数
     * @return java.lang.String
     */
    public static String formatNumber(String number, int decimalDigits) {
        return formatNumber(Double.parseDouble(number), decimalDigits);
    }

    /**
     * 格式化以下划线'_'或减号'-'隔开的字符串，如：
     * "user-name", 可以格式化为"userName"
     *
     * @param name               java.lang.String
     * @param firstCharUpperCase boolean 如果为true，那么返回的首字母大写，反之，小写
     * @return java.lang.String
     */
    public static final String formatJavaName(String name, boolean firstCharUpperCase) {
        if (name == null || name.length() <= 1) return name;
        StringTokenizer tokenizer = new StringTokenizer(name, "-_");
        StringBuffer tmp = new StringBuffer();
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            tmp.append(Character.toUpperCase(token.charAt(0))).append(token.substring(1));
        }
        if (!firstCharUpperCase) {
            String ch = String.valueOf(Character.toLowerCase(tmp.charAt(0)));
            tmp.replace(0, 1, ch);
        }
        return tmp.toString();
    }

    public static final String getFieldName(String methodName) {
        String fieldName = methodName;
        if (fieldName.toLowerCase().startsWith("get")) {
            fieldName = fieldName.substring("get".length());
        }
        fieldName = formatJavaName(fieldName, false);
        return fieldName;
    }

    public static final String getGetMethodName(String name) {
        String methodName = name;
        if (methodName != null && !methodName.startsWith("get")) {
            methodName = "get" + formatJavaName(name, true);
        }
        return methodName;
    }

    public static final String getIsMethodName(String name) {
        String methodName = name;
        if (methodName != null && !methodName.startsWith("is")) {
            methodName = "is" + formatJavaName(name, true);
        }
        return methodName;
    }

    public static final String getSetMethodName(String name) {
        String methodName = name;
        if (methodName != null && !methodName.startsWith("set")) {
            methodName = "set" + formatJavaName(name, true);
        }
        return methodName;
    }

}