package net.ooder.annotation;


public class Permissions {
	//是否只允许访问本部门的组织机构信息
	private boolean selfOrgOnly;

	public Permissions() {
	}
	/**
	 * 是否只允许访问本部门的组织机构信息
	 * 
	 * @return 返回是否只允许访问本部门的组织机构信息
	 */
	 @MethodChinaName(cname="是否只允许访问本部门的组织机构信息")
	public boolean isSelfOrgOnly() {
		return selfOrgOnly;
	}

}