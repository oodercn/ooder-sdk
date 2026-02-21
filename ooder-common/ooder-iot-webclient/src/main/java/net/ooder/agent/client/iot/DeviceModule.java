package net.ooder.agent.client.iot;


/**
 * DeviceModule entity. @author MyEclipse Persistence Tools
 */

public class DeviceModule implements java.io.Serializable {

	// Fields

	private String moduleid;
	private Device device;
	private Integer type;
	private String name;
	private String value;
	private String ieeeaddress;
	private String chipname;

	// Constructors

	/** default constructor */
	public DeviceModule() {
	}

	/** minimal constructor */
	public DeviceModule(String moduleid) {
		this.moduleid = moduleid;
	}

	/** full constructor */
	public DeviceModule(String moduleid, Device device, Integer type,
			String name, String ieeeaddress, String chipname) {
		this.moduleid = moduleid;
		this.device = device;
		this.type = type;
		this.name = name;
		this.ieeeaddress = ieeeaddress;
		this.chipname = chipname;
	}

	// Property accessors

	public String getModuleid() {
		return this.moduleid;
	}

	public void setModuleid(String moduleid) {
		this.moduleid = moduleid;
	}

	public Device getDevice() {
		return this.device;
	}

	public void setDevice(Device device) {
		this.device = device;
	}

	public Integer getType() {
		return this.type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIeeeaddress() {
		return this.ieeeaddress;
	}

	public void setIeeeaddress(String ieeeaddress) {
		this.ieeeaddress = ieeeaddress;
	}

	public String getChipname() {
		return this.chipname;
	}

	public void setChipname(String chipname) {
		this.chipname = chipname;
	}

}