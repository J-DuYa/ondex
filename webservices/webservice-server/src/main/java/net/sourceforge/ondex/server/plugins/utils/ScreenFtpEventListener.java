/*
 * Based on example code from Enterprise Distributed Technologies Ltd
 * www.enterprisedt.com
 */
package net.sourceforge.ondex.server.plugins.utils;

import com.enterprisedt.net.ftp.EventListener;

/**
 *
 * @author christian
 */
public class ScreenFtpEventListener implements EventListener {

    @Override
    public void bytesTransferred(String connId, String remoteFilename, long bytes) {
        System.out.println("Bytes transferred=" + bytes);
    }

    /**
     * Log an FTP command being sent to the server. Not used for SFTP.
     *
     * @param cmd   command string
     */
    @Override
    public void commandSent(String connId, String cmd) {
        System.out.println("Command sent: " + cmd);
    }

    /**
     * Log an FTP reply being sent back to the client. Not used for
     * SFTP.
     *
     * @param reply   reply string
     */
    @Override
    public void replyReceived(String connId, String reply) {
        System.out.println("Reply received: " + reply);
    }

    /**
     * Notifies that a download has started
     *
     * @param remoteFilename   remote file name
     */
    @Override
    public void downloadStarted(String connId, String remoteFilename) {
        System.out.println("Started download: " + remoteFilename);
    }

    /**
     * Notifies that a download has completed
     *
     * @param remoteFilename   remote file name
     */
    @Override
    public void downloadCompleted(String connId, String remoteFilename) {
        System.out.println("Completed download: " + remoteFilename);
    }

    /**
     * Notifies that an upload has started
     *
     * @param remoteFilename   remote file name
     */
    @Override
    public void uploadStarted(String connId, String remoteFilename) {
        System.out.println("Started upload: " + remoteFilename);
    }

    /**
     * Notifies that an upload has completed
     *
     * @param remoteFilename   remote file name
     */
    @Override
    public void uploadCompleted(String connId, String remoteFilename) {
        System.out.println("Completed upload: " + remoteFilename);
    }
}

