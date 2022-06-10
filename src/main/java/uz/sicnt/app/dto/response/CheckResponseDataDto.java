package uz.sicnt.app.dto.response;

public class CheckResponseDataDto {
    
    String criteria_type;
    Boolean result;
    String message;
    
    public String getCriteria_type() {
        return criteria_type;
    }

    public void setCriteria_type(String criteria_type) {
        this.criteria_type = criteria_type;
    }

    public Boolean getResult() {
        return result;
    }

    public void setResult(Boolean result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}