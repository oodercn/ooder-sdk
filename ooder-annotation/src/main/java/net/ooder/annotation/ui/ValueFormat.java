package net.ooder.annotation.ui;


import net.ooder.annotation.Enumstype;

public enum ValueFormat implements Enumstype {
    phone("^1[3456789]\\d{9}$"),
    numpassword("^\\d{6}$"),
    required("[^.*]"),
    email("^[\\w\\.=-]+@[\\w\\.-]+\\.[\\w\\.-]{2,4}$"),
    charOnly("^[a-zA-Z]*$"),
    words("^[\\w ]*$"),
    size("^(([1-9]\\d*\\.\\d+)|([1-9]\\d*)|(\\.\\d\\d*))\\s*(px|em)?$"),
    integer("^-?\\d\\d*$"),
    positiveInteger("^\\d\\d*$"),
    filepath("([\\/]?[\\w_]+)+\\.\\w{1,9}$"),
    number("^-?(\\d\\d*\\.\\d*$)|(^-?\\d\\d*$)|(^-?\\.\\d\\d*$)"),
    color("^\\#[0-9A-Fa-f]{6}$"),
    URL("^(http|https|ftp)\\:\\/\\/[\\w\\-\\_\\.]+[\\w\\-\\_](:[\\w]*)?\\/?([\\w\\-\\._\\?\\,\\'\\/\\\\\\+&amp;%\\$#\\=~])*$"),
    HH_MM("^\\(\\([0-1][0-9]\\)|\\([2][0-3])\\)\\:\\([0-5][0-9]\\)$"),
    HH_MM_SS("^\\(\\([0-1][0-9]\\)|\\([2][0-3])\\)\\:\\([0-5][0-9]\\)\\:\\([0-5][0-9]\\)$"),
    YYYY_MM_DD("^\\([0-9]{4}\\)\\-\\(\\([0][0-9]\\)|\\([1][0-2]\\)\\)\\-\\([0-3][0-9]\\)$");

    private final String name;


    ValueFormat(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public String getType() {
        return name();
    }

    @Override
    public String getName() {
        return name;
    }

}
