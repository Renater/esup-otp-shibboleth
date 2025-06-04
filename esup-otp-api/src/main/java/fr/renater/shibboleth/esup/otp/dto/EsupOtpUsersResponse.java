package fr.renater.shibboleth.esup.otp.dto;

import java.util.List;

/**
 * Esup otp users uid response.
 */
public class EsupOtpUsersResponse extends EsupOtpResponse {

    /** uids. */
    private List<String> uids;

    /**
     * {@inheritDoc}
     */
    public List<String> getUids() {
        return uids;
    }

    /**
     * {@inheritDoc}
     */
    public void setUids(final List<String> uidsList) {
        this.uids = uidsList;
    }

    /** {@inheritDoc} */
    public String toString() {
        return "EsupOtpUsersResponse [code="+ getCode() +", uids=" + uids + "]";
    }
    
    
    
}
