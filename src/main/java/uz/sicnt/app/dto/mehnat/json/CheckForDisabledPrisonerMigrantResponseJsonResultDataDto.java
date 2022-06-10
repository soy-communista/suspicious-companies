package uz.sicnt.app.dto.mehnat.json;

import java.io.Serializable;

public class CheckForDisabledPrisonerMigrantResponseJsonResultDataDto implements Serializable {

    public String birth_date;
    public String firstname;
    public Integer gender;
    public String passport;
    public String patronymic;
    public String pin;
    public String surname;
    public CheckForDisabledPrisonerMigrantResponseJsonResultDataKeysDto keys;

}

/*
{
   "error":null,
   "id":123456,
   "jsonrpc":"2.0",
   "result":{
      "code":0,
      "data":{
         "birth_date":"1988-12-27",
         "firstname":"AKMAL",
         "gender":1,
         "keys":{
            "convict":[

            ],
            "migrant":[

            ],
            "retiree":[

            ]
         },
         "passport":"AA3864294",
         "patronymic":"ABDUNAYIMOVICH",
         "pin":"32712881070128",
         "surname":"G‘AYBULLOYEV"
      },
      "message":"ok",
      "success":true
   }
}
*/