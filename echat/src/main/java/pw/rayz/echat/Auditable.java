package pw.rayz.echat;

public interface Auditable {

    /**
     * Log this to the audit channel.
     */
    void sendAudit();

}
