package net.ooder.scene.skill.knowledge;

/**
 * 文件解析异常
 *
 * @author ooder
 * @since 4.1.0
 */
public class FileParseException extends Exception {

    private final String fileName;
    private final String fileType;

    public FileParseException(String message) {
        super(message);
        this.fileName = null;
        this.fileType = null;
    }

    public FileParseException(String message, Throwable cause) {
        super(message, cause);
        this.fileName = null;
        this.fileType = null;
    }

    public FileParseException(String fileName, String message) {
        super(message);
        this.fileName = fileName;
        this.fileType = null;
    }

    public FileParseException(String fileName, String fileType, String message) {
        super(message);
        this.fileName = fileName;
        this.fileType = fileType;
    }

    public FileParseException(String fileName, String fileType, String message, Throwable cause) {
        super(message, cause);
        this.fileName = fileName;
        this.fileType = fileType;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileType() {
        return fileType;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("FileParseException{");
        if (fileName != null) {
            sb.append("fileName='").append(fileName).append("', ");
        }
        if (fileType != null) {
            sb.append("fileType='").append(fileType).append("', ");
        }
        sb.append("message='").append(getMessage()).append("'}");
        return sb.toString();
    }
}
