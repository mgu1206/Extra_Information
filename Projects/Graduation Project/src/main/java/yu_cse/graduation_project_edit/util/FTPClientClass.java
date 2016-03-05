package yu_cse.graduation_project_edit.util;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * Created by gyeunguckmin on 11/6/15.
 *
 * ftp서버로부터 맵 파일 리스트를 받고
 * 맵 파일을 다운 받을수 있는 클래스
 */
public class FTPClientClass {

    private String serverDomain = "minsdatanetwork.iptime.org";
    private int serverPort = 21;
    private String id = "common_user";
    private String pw = "common123_User";

    FTPClient ftpClient = null;

    public FTPClientClass()
    {
        this.ftpClient = new FTPClient();
        connect();
        login(id, pw);
        changeTargetFolder();
    }

    public void changeTargetFolder()
    {
        cd("/Web/iptsPhp/iptsMaps/");//input u r directory
    }


    public boolean DownloadContents(String filename){

        FTPFile[] files = list();
        if(files==null){
            return false;
        }
        ArrayList<String> ImageIds_tmp = new ArrayList<String>();
        for(int i =0 ;i<files.length;i++){
            String fileName = files[i].getName();
            String extension = fileName.substring(fileName.lastIndexOf(".") + 1);
            String desFileName = filename.substring(0,filename.indexOf("."));
            long size = files[i].getSize();
            extension=extension.toUpperCase();
            if (size > 0) {

                if(desFileName.equalsIgnoreCase(fileName.substring(0, fileName.indexOf(".")))){
                    StringBuffer furl = new StringBuffer("/storage/sdcard0/indoorMapData/");
                    furl.append(fileName);
                    ImageIds_tmp.add(furl.toString());
                    get(fileName, fileName);
                    return true;

                }
            }
        }
        logout();
        disconnect();
        return false;
    }

    public boolean login(String user, String password) {
        try {
            this.connect();
            return this.ftpClient.login(user, password);
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return false;
    }


    public boolean logout() {
        try {
            return this.ftpClient.logout();
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return false;
    }

    public void connect() {

        try {
            this.ftpClient.connect(serverDomain, serverPort);
            int reply;
            reply = this.ftpClient.getReplyCode();
            if(!FTPReply.isPositiveCompletion(reply)) {
                this.ftpClient.disconnect();
            }
        }
        catch (IOException ioe) {
            if(this.ftpClient.isConnected()) {
                try {
                    this.ftpClient.disconnect();
                } catch(IOException f) {;}
            }
        }
    }

    public FTPFile[] list() {
        FTPFile[] files = null;
        //changeTargetFolder();
        try {
            files = this.ftpClient.listFiles();
            return files;
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return null;
    }

    public File get(String source, String target) {
        OutputStream output = null;
        try {
            StringBuffer furl = new StringBuffer("/storage/sdcard0/indoorMapData/");
            File path = new File(furl.toString());
            if(! path.isDirectory()) {
                path.mkdirs();
            }

            furl.append(target);
            File local = new File(furl.toString());
            if(local.isFile()){
                return null;
            }
            output = new FileOutputStream(local);
        }
        catch (FileNotFoundException fnfe) {;}
        File file = new File(source);
        try {
            if (this.ftpClient.retrieveFile(source, output)) {
                return file;
            }
        }
        catch (IOException ioe) {;}
        return null;
    }

    public void cd(String path) {
        try {
            this.ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            this.ftpClient.enterLocalPassiveMode();
            this.ftpClient.changeWorkingDirectory(path);
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            this.ftpClient.disconnect();
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }


}
