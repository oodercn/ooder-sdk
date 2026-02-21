/**
 * $RCSfile: VFSIndex.java,v $
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
package net.ooder.vfs.index;

import net.ooder.annotation.JLuceneIndex;

public interface VFSIndex extends JLuceneIndex {


    public String getDocpath();
    public void setDocpath(String docpath);
;

    public String getName();
    public void setName(String name);
    public StringBuffer getText();
    public void setText(StringBuffer text);
    public String getDesc();
    public void setDesc(String desc);
    public String getRight();

    public void setRight(String right);
    public Long getCreatetime();
    public void setCreatetime(Long createtime);
;


}


