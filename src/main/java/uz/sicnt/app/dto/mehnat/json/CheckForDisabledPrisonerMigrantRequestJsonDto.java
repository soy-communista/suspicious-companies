package uz.sicnt.app.dto.mehnat.json;

public class CheckForDisabledPrisonerMigrantRequestJsonDto {
    public String jsonrpc;
    public String method;
    public Integer id;
    public CheckForDisabledPrisonerMigrantRequestJsonParamsDto params;

    public String getJsonrpc() {
        return jsonrpc;
    }

    public void setJsonrpc(String jsonrpc) {
        this.jsonrpc = jsonrpc;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public CheckForDisabledPrisonerMigrantRequestJsonParamsDto getParams() {
        return params;
    }

    public void setParams(CheckForDisabledPrisonerMigrantRequestJsonParamsDto params) {
        this.params = params;
    }
}
