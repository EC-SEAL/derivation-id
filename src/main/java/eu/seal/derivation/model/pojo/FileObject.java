package eu.seal.derivation.model.pojo;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * FileObject
 */
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2020-04-20T11:39:36.661Z")

public class FileObject   {
  @JsonProperty("filename")
  private String filename = null;

  @JsonProperty("fileID")
  private String fileID = null;

  @JsonProperty("contentType")
  private String contentType = null;

  @JsonProperty("fileSize")
  private Integer fileSize = null;

  @JsonProperty("content")
  private byte[] content = null;

  public FileObject filename(String filename) {
    this.filename = filename;
    return this;
  }

  /**
   * Get filename
   * @return filename
  **/


  public String getFilename() {
    return filename;
  }

  public void setFilename(String filename) {
    this.filename = filename;
  }

  public FileObject fileID(String fileID) {
    this.fileID = fileID;
    return this;
  }

  /**
   * Get fileID
   * @return fileID
  **/


  public String getFileID() {
    return fileID;
  }

  public void setFileID(String fileID) {
    this.fileID = fileID;
  }

  public FileObject contentType(String contentType) {
    this.contentType = contentType;
    return this;
  }

  /**
   * MIME type of the file content
   * @return contentType
  **/


  public String getContentType() {
    return contentType;
  }

  public void setContentType(String contentType) {
    this.contentType = contentType;
  }

  public FileObject fileSize(Integer fileSize) {
    this.fileSize = fileSize;
    return this;
  }

  /**
   * Get fileSize
   * @return fileSize
  **/


  public Integer getFileSize() {
    return fileSize;
  }

  public void setFileSize(Integer fileSize) {
    this.fileSize = fileSize;
  }

  public FileObject content(byte[] content) {
    this.content = content;
    return this;
  }

  /**
   * If not empty, the b64 encoded content of the file.
   * @return content
  **/

@Pattern(regexp="^(?:[A-Za-z0-9+/]{4})*(?:[A-Za-z0-9+/]{2}==|[A-Za-z0-9+/]{3}=)?$") 
  public byte[] getContent() {
    return content;
  }

  public void setContent(byte[] content) {
    this.content = content;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FileObject fileObject = (FileObject) o;
    return Objects.equals(this.filename, fileObject.filename) &&
        Objects.equals(this.fileID, fileObject.fileID) &&
        Objects.equals(this.contentType, fileObject.contentType) &&
        Objects.equals(this.fileSize, fileObject.fileSize) &&
        Objects.equals(this.content, fileObject.content);
  }

  @Override
  public int hashCode() {
    return Objects.hash(filename, fileID, contentType, fileSize, content);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FileObject {\n");
    
    sb.append("    filename: ").append(toIndentedString(filename)).append("\n");
    sb.append("    fileID: ").append(toIndentedString(fileID)).append("\n");
    sb.append("    contentType: ").append(toIndentedString(contentType)).append("\n");
    sb.append("    fileSize: ").append(toIndentedString(fileSize)).append("\n");
    sb.append("    content: ").append(toIndentedString(content)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

