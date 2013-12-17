package net.sourceforge.ondex.server.executor;

/**
 *
 * @author Christian Brenninkmeijer
 */
public enum Status{
    DONE("DONE"),
    ERROR("ERROR"),
    NOT_FOUND("NOT_FOUND"),
    PENDING("PENDING"),
    RUNNING("RUNNING");

    private String text;

    private Status(String t){
        text = t;
    }

    @Override
    public String toString(){
        return text;
    }

}