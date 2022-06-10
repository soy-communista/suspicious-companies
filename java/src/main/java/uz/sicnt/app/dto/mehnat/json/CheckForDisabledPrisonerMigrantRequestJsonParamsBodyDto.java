package uz.sicnt.app.dto.mehnat.json;

import java.io.Serializable;
import java.util.List;

public class CheckForDisabledPrisonerMigrantRequestJsonParamsBodyDto implements Serializable {
    public String pin;
    public List<String> keys;

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public List<String> getKeys() {
        return keys;
    }

    public void setKeys(List<String> keys) {
        this.keys = keys;
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