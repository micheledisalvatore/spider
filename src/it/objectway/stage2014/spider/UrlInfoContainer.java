package it.objectway.stage2014.spider;

/**
 * Created by michele-macbookair on 29/04/14.
 */
public class UrlInfoContainer {
    private String status;
    private String mimType;
    private int depth;
    private boolean internal;

    public UrlInfoContainer(String status, int depth, boolean internal) {
        this.status = status;
        this.depth = depth;
        this.internal = internal;
    }

    public UrlInfoContainer(String status, int depth, boolean internal, String mimType) {
        this(status, depth, internal);
        this.mimType = mimType;
    }

    public boolean isInternal(){
        return internal;
    }

    public void setMimType(String mimType){
        this.mimType = mimType;
    }

    public void setStatus(String status){
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public String getMimType() {
        return mimType;
    }

    public int getDepth() {
        return depth;
    }

    @Override
    public String toString() {
        return "UrlInfoContainer{" +
                "\n\tstatus='" + status + '\'' +
                ",\n\t mimType='" + mimType + '\'' +
                ",\n\t depth=" + depth + "\n" +
                '}';
    }
}
