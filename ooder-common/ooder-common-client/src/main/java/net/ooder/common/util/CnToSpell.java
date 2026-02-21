/**
 * $RCSfile: CnToSpell.java,v $
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

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

public class CnToSpell {

    public static String getFirstSpell(String chinese) {
	return getShortSpell(chinese);
    }

    /**
     * get short spell of chinese
     * 
     * @param chinese
     * @return
     */
    public static String getShortSpell(String chinese) {
	StringBuffer pybf = new StringBuffer();
	char[] arr = chinese.toCharArray();
	HanyuPinyinOutputFormat defaultFormat = getOutputFormat();
	for (int i = 0; i < arr.length; i++) {
	    // do not convert ascii character
	    if (arr[i] > 128) {
		try {
		    String[] _t = PinyinHelper.toHanyuPinyinStringArray(arr[i], defaultFormat);
		    if (_t != null && _t.length != 0) {
			pybf.append((_t[0] != null && _t[0].trim().length() > 1) ? _t[0].charAt(0) : "");
		    } else {
			pybf.append(arr[i]);
		    }
		} catch (BadHanyuPinyinOutputFormatCombination e) {
		    e.printStackTrace();
		}
	    } else {
		pybf.append(arr[i]);
	    }
	}
	return pybf.toString();

    }

    /**
     * get full spell of chinese
     * 
     * @param chinese
     * @return
     */
    public static String getFullSpell(String chinese) {
	StringBuffer pybf = new StringBuffer();
	char[] arr = chinese.toCharArray();
	HanyuPinyinOutputFormat defaultFormat = getOutputFormat();
	for (int i = 0; i < arr.length; i++) {
	    // do not convert ascii character
	    if (arr[i] > 128) {
		try {
		    String[] strArray = PinyinHelper.toHanyuPinyinStringArray(arr[i], defaultFormat);
		    if (strArray != null && strArray.length != 0) {
			pybf.append(strArray[0]);
		    } else {
			pybf.append(arr[i]);
		    }
		} catch (BadHanyuPinyinOutputFormatCombination e) {
		    e.printStackTrace();
		}
	    } else {
		pybf.append(arr[i]);
	    }
	}
	return pybf.toString();

    }

    private static HanyuPinyinOutputFormat getOutputFormat() {
	HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
	defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
	defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
	defaultFormat.setVCharType(HanyuPinyinVCharType.WITH_V);
	return defaultFormat;
    }
}
