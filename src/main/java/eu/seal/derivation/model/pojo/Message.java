package eu.seal.derivation.model.pojo;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * Message
 */
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2020-04-20T11:39:36.661Z")

public class Message   {
  @JsonProperty("timestamp")
  private Integer timestamp = null;

  @JsonProperty("sender")
  private String sender = null;

  @JsonProperty("senderType")
  private String senderType = null;

  @JsonProperty("recipient")
  private String recipient = null;

  @JsonProperty("recipientType")
  private String recipientType = null;

  @JsonProperty("message")
  private String message = null;

  public Message timestamp(Integer timestamp) {
    this.timestamp = timestamp;
    return this;
  }

  /**
   * date and time when the message was sent
   * @return timestamp
  **/


  public Integer getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Integer timestamp) {
    this.timestamp = timestamp;
  }

  public Message sender(String sender) {
    this.sender = sender;
    return this;
  }

  /**
   * Identifier of the user who sent the message
   * @return sender
  **/


  public String getSender() {
    return sender;
  }

  public void setSender(String sender) {
    this.sender = sender;
  }

  public Message senderType(String senderType) {
    this.senderType = senderType;
    return this;
  }

  /**
   * Identifier of the user category who sent the message
   * @return senderType
  **/


  public String getSenderType() {
    return senderType;
  }

  public void setSenderType(String senderType) {
    this.senderType = senderType;
  }

  public Message recipient(String recipient) {
    this.recipient = recipient;
    return this;
  }

  /**
   * Identifier of the user whom the message is addressed to
   * @return recipient
  **/


  public String getRecipient() {
    return recipient;
  }

  public void setRecipient(String recipient) {
    this.recipient = recipient;
  }

  public Message recipientType(String recipientType) {
    this.recipientType = recipientType;
    return this;
  }

  /**
   * Identifier of the user category whom the message is addressed to
   * @return recipientType
  **/


  public String getRecipientType() {
    return recipientType;
  }

  public void setRecipientType(String recipientType) {
    this.recipientType = recipientType;
  }

  public Message message(String message) {
    this.message = message;
    return this;
  }

  /**
   * Content of the message
   * @return message
  **/


  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Message message = (Message) o;
    return Objects.equals(this.timestamp, message.timestamp) &&
        Objects.equals(this.sender, message.sender) &&
        Objects.equals(this.senderType, message.senderType) &&
        Objects.equals(this.recipient, message.recipient) &&
        Objects.equals(this.recipientType, message.recipientType) &&
        Objects.equals(this.message, message.message);
  }

  @Override
  public int hashCode() {
    return Objects.hash(timestamp, sender, senderType, recipient, recipientType, message);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Message {\n");
    
    sb.append("    timestamp: ").append(toIndentedString(timestamp)).append("\n");
    sb.append("    sender: ").append(toIndentedString(sender)).append("\n");
    sb.append("    senderType: ").append(toIndentedString(senderType)).append("\n");
    sb.append("    recipient: ").append(toIndentedString(recipient)).append("\n");
    sb.append("    recipientType: ").append(toIndentedString(recipientType)).append("\n");
    sb.append("    message: ").append(toIndentedString(message)).append("\n");
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

