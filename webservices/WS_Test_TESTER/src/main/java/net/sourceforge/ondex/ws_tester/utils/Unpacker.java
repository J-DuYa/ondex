/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sourceforge.ondex.ws_tester.utils;

import com.enterprisedt.net.ftp.FTPConnectMode;
import com.enterprisedt.net.ftp.FTPException;
import com.enterprisedt.net.ftp.FTPFile;
import com.enterprisedt.net.ftp.FTPReply;
import com.enterprisedt.net.ftp.FTPTransferType;
import com.enterprisedt.net.ftp.FileTransferClient;
import com.ice.tar.TarEntry;
import com.ice.tar.TarInputStream;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.util.Date;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import net.sourceforge.ondex.tools.DirUtils;

/**
 *
 * @author christian
 */
public class Unpacker {

    private File tempDir;
    
    public Unpacker() throws IOException{
        if (System.getProperty("webapp.root") == null){
            tempDir = new File("/Ondex/tempData/");
        } else {
            tempDir = new File(System.getProperty("webapp.root")+File.separator+"temp");
        }
        DirUtils.makeDirs(tempDir);
    }

    public File toDirectory(String name, File root) throws IOException, FTPException, ParseException{
        File exists = new File(name);
        if (exists.isDirectory()){
            return exists;
        }
        if (exists.isFile()){
            if ((name.toLowerCase().endsWith(".zip")) || (name.toLowerCase().endsWith(".tar")) ||
                    (name.toLowerCase().endsWith(".tar.gz"))) {
                InputStream fis = new FileInputStream(exists);
                File parent = createTempDirectory(root);
                toDirectory (fis, name, parent);
                return parent;
            }
            throw new IOException ("File " + name + " is a file not a directory or known zip format.");
        }
        if (name.toLowerCase().startsWith("ftp")){
            return fromFtpDirectory(name, root);
        }
        try {
            //Ugly way to check if string is a URL
            URL url = new URL(name);
            URLConnection connection = url.openConnection();
            long date = connection.getDate();
            //ystem.out.println(connection.toString());
            //ystem.out.println(connection.getDate());
            //ystem.out.println(new Date(connection.getDate()).toString());
            //ystem.out.println(connection.getPermission());
            //ystem.out.println(connection.getContentLength());
            InputStream uis = url.openStream();
            if ((name.toLowerCase().endsWith(".zip")) || (name.toLowerCase().endsWith(".tar")) ||
                    (name.toLowerCase().endsWith(".tar.gz"))) {
                File parent = createTempDirectory(root);
                toDirectory(uis, name, parent);
                return parent;
            }
            throw new IOException ("Url " + name + " does not appear to point to known zip format.");
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new IOException("The arguement " + name + " of type String, could not be converted "+
                  "to either a URL or an existing file.");
        }
        //throw new FileNotFoundException(name);
    }

    public void toDirectory(InputStream input, String name, File root) throws IOException{
        if (name.toLowerCase().endsWith(".zip")) {
             unzip (input, root);
        } else if (name.toLowerCase().endsWith(".tar")) {
             untar (input, root);
        } else if (name.toLowerCase().endsWith(".gz")) {
            InputStream ungz = new GZIPInputStream(input);
            toDirectory(ungz,name.substring(0, name.length()-3),root);
        } else {
            throw new IOException ("File " + name + " is a file not a directory");
        }
    }

    private void unzip (InputStream input, File root) throws IOException{
        final int BUFFER = 2048;
        BufferedOutputStream dest = null;
        ZipInputStream zis = new ZipInputStream(new BufferedInputStream(input));
        //JarInputStream jis = new JarInputStream(new BufferedInputStream(fis));
        ZipEntry entry;
        String rootPath = root.getAbsolutePath() + File.separator;
        while((entry = zis.getNextEntry()) != null) {
            System.out.println("Extracting: " +entry);
            int count;
            byte data[] = new byte[BUFFER];
            // write the files to the disk
            String tempName = rootPath + entry.getName();
            if (tempName.endsWith("/")) {
                File output = new File(tempName);
                System.out.println("new directory "+output.getAbsoluteFile());
                DirUtils.makeDirs(output);
            } else {
                System.out.println("ouput file is " + tempName);
                FileOutputStream fos = new FileOutputStream(tempName);
                dest = new BufferedOutputStream(fos, BUFFER);
                while ((count = zis.read(data, 0, BUFFER)) != -1) {
                    dest.write(data, 0, count);
                }
                dest.flush();
                dest.close();
            }
         }
         zis.close();
    }

    private void untar (InputStream input, File root) throws IOException{
        final int BUFFER = 2048;
        BufferedOutputStream dest = null;
        TarInputStream tis = new TarInputStream(new BufferedInputStream(input));
        //JarInputStream jis = new JarInputStream(new BufferedInputStream(fis));
        TarEntry entry;
        String rootPath = root.getAbsolutePath() + File.separator;
        while((entry = tis.getNextEntry()) != null) {
            System.out.println("Extracting: " +entry);
            int count;
            byte data[] = new byte[BUFFER];
            // write the files to the disk
            String tempName = rootPath + entry.getName();
            if (tempName.endsWith("/")) {
                File output = new File(tempName);
                System.out.println("new directory "+output.getAbsoluteFile());
                DirUtils.makeDirs(output);
            } else {
                System.out.println("ouput file is " + tempName);
                FileOutputStream fos = new FileOutputStream(tempName);
                dest = new BufferedOutputStream(fos, BUFFER);
                while ((count = tis.read(data, 0, BUFFER)) != -1) {
                    dest.write(data, 0, count);
                }
                dest.flush();
                dest.close();
            }
         }
         tis.close();
    }

    private File createTempDirectory(File parent) throws IOException
    {
        final File temp;
        temp = File.createTempFile("temp", "", parent);
        if (temp.exists()){
            DirUtils.deleteTree(temp);
        }
        if(!(temp.delete())){
            throw new IOException("Could not delete temp file: " + temp.getAbsolutePath());
        }
        DirUtils.makeDirs(temp);
        //temp.deleteOnExit();
        return (temp);
    }

    private File fromFtpDirectory(String name, File root) throws FTPException, IOException, ParseException{
        //Split name into host, directory and name;
        String localName = name.toLowerCase().replace('\\', '/');
        if (localName.startsWith("ftp://")){
            localName = localName.substring(6,localName.length());
        }
        int hostCutOff = localName.indexOf('/');
        int nameCutOff = localName.lastIndexOf('/');
        String host; //ftp.plantcyc.org or ftp://ftp.plantcyc.org
        //ystem.out.println(name);
        //ystem.out.println(hostCutOff);
        host = localName.substring(0,hostCutOff);
        String directory; // "/tmp/private/plantcyc/";
        directory = localName.substring(hostCutOff, nameCutOff);
        String fileName; // "aracyc.tar.gz";
        fileName = localName.substring(nameCutOff+1, localName.length());
        System.out.println(host);
        System.out.println(directory);
        System.out.println(fileName);

        //Set up target
        File target = new File (root,host + directory);
        DirUtils.makeDirs(target);

        //Log on to FTP server
        String username = "anonymous";
        String password = "";
        FileTransferClient ftp = new FileTransferClient();
//        ftp.setEventListener(new ScreenFtpEventListener());
        ftp.setRemoteHost(host);
        ftp.setUserName(username);
        ftp.setPassword(password);
        ftp.connect();
        ftp.getAdvancedFTPSettings().setConnectMode(FTPConnectMode.PASV);
        ftp.setContentType(FTPTransferType.BINARY);

        //Find the file or directory refered to
        ftp.changeDirectory(directory);
        //Check if names exists and if it points to a directory or file
        FTPFile[] files = ftp.directoryList();
        FTPFile file = null;

        for (int i = 0; i<files.length; i++){
            System.out.println(files[i].getName());
            if (files[i].getName().equalsIgnoreCase(fileName)){
                file = files[i];
            }
        }
        if (file == null){
            ftp.disconnect();
            throw new IOException("Unable to find "+name);
        }

        //Download file(s)
        fromFptFile(ftp, file, target);
        ftp.disconnect();
        return target;
    }

    private FTPFile[] getDirectoryList(FileTransferClient ftp) throws FTPException, IOException, ParseException{
        int count = 0;
        while (true){
            try {
                return ftp.directoryList();
            } catch (ConnectException e){
                count ++;
                if (count > 10){
                    throw e;
                }
            }
        }
    }

    private void downloadFile(FileTransferClient ftp, String target, String source) throws FTPException, IOException{
        int count = 0;
        while (true){
            try {
                ftp.downloadFile(target, source);
                return;
            } catch (ConnectException e){
                count ++;
                if (count > 10){
                    throw e;
                }
            }
        }
    }


    private void fromFptFile(FileTransferClient ftp, FTPFile ftpFile, File root)
            throws FTPException, IOException, ParseException{
        if (ftpFile.isDir()){
            System.out.println("Directory: "+ftpFile.getName());
            File directory = new File(root, ftpFile.getName());
            DirUtils.makeDirs(directory);
            ftp.changeDirectory(ftpFile.getName());
            FTPFile[] files = getDirectoryList(ftp);
            //for (int i = 0; i<files.length; i++){
                //ystem.out.println("sub: "+files[i].getName());
            //}
            for (int i = 0; i<files.length; i++){
                fromFptFile(ftp, files[i], directory);
            }
            ftp.changeDirectory("..");
        } else if (ftpFile.isFile()){
            String fileName = ftpFile.getName().toLowerCase();
            if (fileName.endsWith(".gz") || fileName.endsWith(".tar") || fileName.endsWith(".zip"))  {
                InputStream inputStream = ftp.downloadStream(ftpFile.getName());
                toDirectory(inputStream, fileName, root);
            } else {
                File target = new File(root, ftpFile.getName());
                if (target.exists()){
                    if (ftpFile.lastModified().before(new Date(target.lastModified()))){
                        System.out.println("Skipping "+ftpFile.getName());
                        return;
                    } else {
                        //System.out.println(ftpFile.lastModified());
                        //System.out.println(new Date(target.lastModified()));
                        System.out.println("Overwriting "+ftpFile.getName());
                    }
                } else {
                    System.out.println(ftpFile.getPath()+"/"+ftpFile.getName() + " to " + target.getAbsolutePath());
                }
                downloadFile(ftp, target.getAbsolutePath(), fileName);
            }
        }
    }

    private InputStream toFtpStream(String name) throws FTPException, IOException, ParseException{
        String username = "anonymous";
        String password = "";
        String localName = name.toLowerCase().replace('\\', '/');
        if (localName.startsWith("ftp://")){
            localName = localName.substring(6,localName.length());
        }
        int hostCutOff = localName.indexOf('/');
        int nameCutOff = localName.lastIndexOf('/');
        //String filePath = "/tmp/private/plantcyc/aracyc.tar.gz";
        String host; //ftp.plantcyc.org or ftp://ftp.plantcyc.org
        //ystem.out.println(name);
        //ystem.out.println(hostCutOff);
        host = localName.substring(0,hostCutOff);
        String directory; // "/tmp/private/plantcyc/";
        directory = localName.substring(hostCutOff, nameCutOff);
        String fileName; // "aracyc.tar.gz";
        fileName = localName.substring(nameCutOff+1, localName.length());
        //ystem.out.println(host);
        //ystem.out.println(directory);
        //ystem.out.println(fileName);

        FileTransferClient ftp = new FileTransferClient();
        //ftp.setEventListener(new ScreenFtpEventListener());
        ftp.setRemoteHost(host);
        ftp.setUserName(username);
        ftp.setPassword(password);
        ftp.connect();
        ftp.getAdvancedFTPSettings().setConnectMode(FTPConnectMode.PASV);
        ftp.setContentType(FTPTransferType.BINARY);
        ftp.changeDirectory(directory);

        //Check if names exists and if it points to a directory or file
        FTPFile[] files = ftp.directoryList();
        FTPFile file = null;
        for (int i = 0; i<files.length; i++){
            if (files[i].getName().equalsIgnoreCase(fileName)){
                file = files[i];
            }
        }
        if (file == null){
            ftp.disconnect();
            throw new IOException("Unable to find "+name);
        }
        if (file.isDir()){
            System.out.println("Directory");
            ftp.disconnect();
            return null;
        } else if (file.isFile()){
            //ystem.out.println("File");
            ftp.downloadFile("c:/tmp/aracyc.tar.gz","aracyc.tar.gz");

            InputStream is = ftp.downloadStream(fileName);
            return is;
        }
        ftp.disconnect();
        throw new IOException("Unable to download "+name);
    }

    private void showFTPFile(FTPFile file){
        System.out.println(file.getName());
        System.out.println("\t Created: " + file.created());
        System.out.println("\t isDir: " + file.isDir());
        System.out.println("\t isFile: " + file.isFile());
        System.out.println("\t isLink: " + file.isLink());
        System.out.println("\t lastModified: " + file.lastModified());
        System.out.println("\t size: " + file.size());
        System.out.println("\t group: " + file.getGroup());
        System.out.println("\t LinkedName: " + file.getLinkedName());
        System.out.println("\t Owner: " + file.getOwner());
        System.out.println("\t Path: " + file.getPath());
        System.out.println("\t Permissions: " + file.getPermissions());
        System.out.println("\t raw: " + file.getRaw());
        System.out.println("\t toString: " + file.toString());
    }

    private void Test() throws MalformedURLException, IOException, ParseException, FTPException{
        //String host = "ftp.genome.jp";
        String host = "ftp.plantcyc.org";
        String filePath = "/tmp/private/plantcyc/aracyc.tar.gz";
        String username = "anonymous";
        String password = "";
        String path = "/tmp/private/plantcyc/";
        //String path = "tmp";

        FileTransferClient ftp = null;

       // set up client
        ftp = new FileTransferClient();
        //ftp.setEventListener(new ScreenFtpEventListener());
        ftp.setRemoteHost(host);
        ftp.setUserName(username);
        ftp.setPassword(password);
        //FTPMessageCollector listener = new FTPMessageCollector();
        //ftp.setMessageListener(listener);

        // connect
        System.out.println("Connecting");
        ftp.connect();
        FTPReply reply = ftp.getLastReply();
        System.out.println(reply.getRawReply());
        System.out.println(reply.getReplyCode());
        System.out.println(reply.getReplyText());

        System.out.println("Setting Binary Transfer mode");
        ftp.setContentType(FTPTransferType.BINARY);
        // login
        //System.out.println("Logging in");
        //ftp.login(username, password);

        // set up passive ASCII transfers
        //System.out.println("Setting up passive, ASCII transfers");
        //ftp.setConnectMode(FTPConnectMode.PASV);
        //ftp.setType(FTPTransferType.ASCII);

        // get directory and print it to console
        System.out.println("Directory before chdir:");
        FTPFile[] files = ftp.directoryList();
        for (int i = 0; i < files.length; i++) {
            //showFTPFile(files[i]);
            System.out.println(files[i]);
        }

        System.out.println("Changing dir");
        ftp.changeDirectory(path);
        reply = ftp.getLastReply();
        System.out.println(reply.getRawReply());
        System.out.println(reply.getReplyCode());
        System.out.println(reply.getReplyText());

        System.out.println("Directory after chdir:");
        files = ftp.directoryList();
        for (int i = 0; i < files.length; i++) {
            //showFTPFile(files[i]);
            System.out.println(files[i]);
        }
        reply = ftp.getLastReply();
        System.out.println(reply.getRawReply());
        System.out.println(reply.getReplyCode());
        System.out.println(reply.getReplyText());

        //upload a file
        //ftp.uploadFile("C:/users/Christian/test.txt", "test.txt");

        //Download a file
        System.out.println("Downloading file");
        File file = new File("c:/tmp/aracyc.tar.gz");
        
        ftp.downloadFile("c:/tmp/aracyc.tar.gz","aracyc.tar.gz");
        //String messages = listener.getLog();
        //System.out.println("Listener log:");
        //System.out.println(messages);

        // Shut down client
        System.out.println("Quitting client");
        ftp.disconnect();;

        //upload a file should fail
        System.out.println("Test complete");
    }/**/

    public static void main(String[] args) throws Exception{
        Unpacker unpacker = new Unpacker();
    
        //File test = unpacker.toDirectory("ftp://ftp.plantcyc.org/tmp/private/plantcyc/aracyc.tar.gz", unpacker.tempDir);
        File test = unpacker.toDirectory("ftp://ftp.genome.jp/pub/kegg", unpacker.tempDir);
        //File test = unpacker.toDirectory("http://www.enterprisedt.com/products/edtftpj/download/edtftpj.zip", unpacker.tempDir);
        //File test = unpacker.toDirectory("c:/Users/Christian/Ondex/webservices/WS_Test_TESTER/data/inputs/aracyc.tar.gz");
        System.out.println(test.getAbsolutePath());
    }

}
