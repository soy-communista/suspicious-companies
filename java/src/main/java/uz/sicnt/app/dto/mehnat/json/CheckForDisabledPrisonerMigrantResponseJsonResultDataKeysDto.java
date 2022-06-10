package uz.sicnt.app.dto.mehnat.json;

import java.io.Serializable;

public class CheckForDisabledPrisonerMigrantResponseJsonResultDataKeysDto implements Serializable {

    public Object convict;
    public Object retiree;
    public Object migrant;

    public Object getConvict() {
        return convict;
    }

    public void setConvict(Object convict) {
        this.convict = convict;
    }

    public Object getRetiree() {
        return retiree;
    }

    public void setRetiree(Object retiree) {
        this.retiree = retiree;
    }

    public Object getMigrant() {
        return migrant;
    }

    public void setMigrant(Object migrant) {
        this.migrant = migrant;
    }


}