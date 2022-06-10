package uz.sicnt.app.dto.mehnat.json;

import java.io.Serializable;

public class CheckForDisabledPrisonerMigrantRequestJsonParamsDto implements Serializable {
    public CheckForDisabledPrisonerMigrantRequestJsonParamsBodyDto body;

    public CheckForDisabledPrisonerMigrantRequestJsonParamsBodyDto getBody() {
        return body;
    }

    public void setBody(CheckForDisabledPrisonerMigrantRequestJsonParamsBodyDto body) {
        this.body = body;
    }
}


/*

{
    "jsonrpc": "2.0",
    "id": 123456,
    "method": "enst.citizen.fulldata",
    "params": {
        "body": {
            "pin": "30403700070024",
            "keys": [
                "migrant",
                "retiree",
                "convict"
            ]
        }
    }
}



*/