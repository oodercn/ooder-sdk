/**
 * $RCSfile: Constants.java,v $
 * $Revision: 1.1 $
 * $Date: 2025/07/08 00:25:57 $
 *
 * Copyright (C) 2003 spk, Inc. All rights reserved.
 *
 * This software is the proprietary information of spk, Inc.
 * Use is subject to license terms.
 */
/**
 * $RCSfile: Constants.java,v $
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

import net.ooder.common.CommonConfig;

/**
 * <p>Title: 常用代码打包</p>
 * <p>Description: Contains constant values representing various objects. </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: raddev.cn</p>
 * @author wenzhang li
 * @version 1.0
 */
public class Constants {

    public static final long SECOND = 1000;
    public static final long MINUTE = 60 * SECOND;
    public static final long HOUR = 60 * MINUTE;
    
    public static final String CONFIG_KEY = "JDS";
    
    public static final String ORG_CONFIG_KEY = "org";

    public static final String FILE_SEPERATOR = System.getProperty("file.separator");
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");
    public static final String PATH_SEPARATOR = System.getProperty("path.separator");

    public static final String JAVA_IO_TMPDIR = System.getProperty("java.io.tmpdir");
    public static final String USER_HOME = System.getProperty("user.home");
    
    public static final String COMMAND_CONFIGKEY = "COMMAND";

    public static final String COMMON_CONFIGKEY = CommonConfig.getValue("commonConfigKey");
    
	
    
}