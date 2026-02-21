package net.ooder.common;

public enum Commonyesno {

    YES("YES"), 
    DEFAULT("DEFAULT"), 
    NO("NO");

    private String type;

    Commonyesno(String type){
	this.type=type;
    }
    @Override
    public String toString() {
	return type;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public static Commonyesno fromType(String typeName) {
	for (Commonyesno type : Commonyesno.values()) {
	    if (type.getType().equals(typeName)) {
		return type;
	    }
	}
	
	return null;
    }
}
