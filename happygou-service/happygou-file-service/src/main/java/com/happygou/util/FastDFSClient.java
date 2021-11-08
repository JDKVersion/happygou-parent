package com.happygou.util;


import com.happygou.file.FastDFSFile;
import org.csource.common.MyException;
import org.csource.fastdfs.*;
import org.springframework.core.io.ClassPathResource;

import java.io.*;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.changgou.util *
 * @since 1.0
 */
public class FastDFSClient {

    static {
        //从classpath下获取文件对象路径
        String path = new ClassPathResource("fdfs_client.conf").getPath();
        try{
            ClientGlobal.init(path);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /*
    * 文件上传
    * */
    public static String[] upload(FastDFSFile fastDFSFile) throws IOException, MyException {
        // 创建一个Tracker客户端对象TrackerClient
        TrackerClient trackerClient = new TrackerClient();
        //通过TrackerClient对象访问TrackerServer，获取链接信息
        TrackerServer trackerServer = trackerClient.getConnection();
        //通过TrackerServer,获取Storage的连接信息
        StorageClient storageClient = new StorageClient(trackerServer,null);
        /*
        * @param1:文件的字节数组
        * @param2:扩展名
        * @param3:扩展信息
        * @return1:储存文件的组的名字
        * @return2:新的文件名
        * */
         String[] uploads =storageClient.upload_file(fastDFSFile.getContent(),fastDFSFile.getExt(),null);
         return uploads;

    }
    /*文件信息获取*/
    public static FileInfo  getFile(String group_name, String remote_filename) throws IOException, MyException {
        //获取traker信息
        TrackerClient trackerClient = new TrackerClient();
        // 获取ttrackerServer信息
        TrackerServer trackerServer = trackerClient.getConnection();
        //通过trackerServer获取Storage信息
        StorageClient storageClient = new StorageClient(trackerServer,null);
        //获取文件信息
        FileInfo fileInfo=storageClient.get_file_info(group_name,remote_filename);
        return fileInfo;
    }

    /*文件下载*/
    public  static InputStream downloadFile(String group_name, String remote_filename) throws IOException, MyException {
        //创建trackerClient
        TrackerClient trackerClient = new TrackerClient();
        //创建trackerServer
        TrackerServer trackerServer = trackerClient.getConnection();
        //创建storageClient
        StorageClient storageClient = new StorageClient(trackerServer,null);
        //下载文件
       byte[] bytes= storageClient.download_file(group_name,remote_filename);
       return new ByteArrayInputStream(bytes);
    }

    /*
    * 文件删除
    * */
    public static void deleteFile(String group_name, String remote_filename) throws IOException, MyException {
        //获取tracker客户端
        TrackerClient trackerClient = new TrackerClient();
        //获取TracerServer
        TrackerServer trackerServer = trackerClient.getConnection();
        // 创建storageClient
        StorageClient storageClient = new StorageClient(trackerServer,null);
        //删除文件
        storageClient.delete_file(group_name,remote_filename);
    }

    /*
    * 获取storage服务信息
    * */
    public static StorageServer getStorage() throws IOException {
        // 创建tracker客户端
        TrackerClient trackerClient = new TrackerClient();
        // 获取trackerServer
        TrackerServer trackerServer =trackerClient.getConnection();
        // 获取storage信息
        return trackerClient.getStoreStorage(trackerServer);
    }

    /*
    * 获取storage组的信息
    * */
    public static ServerInfo[] getServerInfo(String group_name, String remote_filename) throws IOException {
        // 创建TrackerClient
        TrackerClient trackerClient = new TrackerClient();
        //获取TrackerServer
        TrackerServer trackerServer = trackerClient.getConnection();
        // 获取storage组的信息
       return trackerClient.getFetchStorages(trackerServer,group_name,remote_filename);
    }

    /*
    * 获取tracker信息
    * */
   public static String  getTrackerInfo() throws IOException {
       // 创建tracker客户端
       TrackerClient trackerClient = new TrackerClient();
       // 获取trackerServer
       TrackerServer trackerServer = trackerClient.getConnection();
       int port =ClientGlobal.getG_tracker_http_port();
       String ip = trackerServer.getInetSocketAddress().getHostString();
       return "http://"+ip+":"+port;
   }
    public static void main(String[] args) throws IOException, MyException {

        //获取文件字节输入流
        InputStream is = downloadFile("group1","M00/00/00/rBw6U2FzeV-ANM_3AAD5DbURmIs859.png");

        // 将文件信息写入本地磁盘
        FileOutputStream os = new FileOutputStream("D:/1.jpg");

        //定义文件缓冲区
        byte[] bytes = new byte[1024];
        //将文件信息读入缓冲区
        while (is.read(bytes)!=-1){
            // 冲缓冲区里写文件
            os.write(bytes);
        }
        os.flush();
        os.close();
        is.close();

    }
/*    static {
        //从classpath下获取文件对象获取路径
        String path = new ClassPathResource("fdfs_client.conf").getPath();
        try {
            ClientGlobal.init(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

  /*  //图片上传
    public static String[] upload(FastDFSFile file) {
        try {
            TrackerClient trackerClient = new TrackerClient();
            TrackerServer trackerServer = trackerClient.getConnection();
            StorageClient storageClient = new StorageClient(trackerServer, null);
            //参数1 字节数组
            //参数2 扩展名(不带点)
            //参数3 元数据( 文件的大小,文件的作者,文件的创建时间戳)
            NameValuePair[] meta_list = new NameValuePair[]{new NameValuePair(file.getAuthor()), new NameValuePair(file.getName())};

            String[] strings = storageClient.upload_file(file.getContent(), file.getExt(), meta_list);

            return strings;// strings[0]==group1  strings[1]=M00/00/00/wKjThF1aW9CAOUJGAAClQrJOYvs424.jpg
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    //图片下载
    public static InputStream downFile(String groupName, String remoteFileName) {
        ByteArrayInputStream byteArrayInputStream = null;
        try {
            //3.创建trackerclient对象
            TrackerClient trackerClient = new TrackerClient();
            //4.创建trackerserver 对象
            TrackerServer trackerServer = trackerClient.getConnection();
            //5.创建stroageserver 对象
            //6.创建storageclient 对象
            StorageClient storageClient = new StorageClient(trackerServer, null);
            //7.根据组名 和 文件名 下载图片

            //参数1:指定组名
            //参数2 :指定远程的文件名
            byte[] bytes = storageClient.download_file(groupName, remoteFileName);
            byteArrayInputStream = new ByteArrayInputStream(bytes);
            return byteArrayInputStream;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (byteArrayInputStream != null) {
                    byteArrayInputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    //图片删除

    public static void deleteFile(String groupName, String remoteFileName) {
        try {
            //3.创建trackerclient对象
            TrackerClient trackerClient = new TrackerClient();
            //4.创建trackerserver 对象
            TrackerServer trackerServer = trackerClient.getConnection();
            //5.创建stroageserver 对象
            //6.创建storageclient 对象
            StorageClient storageClient = new StorageClient(trackerServer, null);
            int i = storageClient.delete_file(groupName, remoteFileName);
            if (i == 0) {
                System.out.println("删除成功");
            } else {
                System.out.println("删除失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //根据组名获取组的信息

    public static StorageServer getStorages(String groupName) {
        try {
            TrackerClient trackerClient = new TrackerClient();
            //4.创建trackerserver 对象
            TrackerServer trackerServer = trackerClient.getConnection();

            //参数1 指定traqckerserver 对象
            //参数2 指定组名
            StorageServer group1 = trackerClient.getStoreStorage(trackerServer, groupName);
            return group1;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //根据文件名和组名获取文件的信息

    public static FileInfo getFile(String groupName, String remoteFileName) {
        try {
            TrackerClient trackerClient = new TrackerClient();
            //4.创建trackerserver 对象
            TrackerServer trackerServer = trackerClient.getConnection();

            StorageClient storageClient = new StorageClient(trackerServer, null);

            //参数1 指定组名
            //参数2 指定文件的路径
            FileInfo fileInfo = storageClient.get_file_info(groupName, remoteFileName);
            return fileInfo;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    //根据文件名和组名 获取组信息的数组信息
    public static ServerInfo[] getServerInfo(String groupName, String remoteFileName){
        try {
            //3.创建trackerclient对象
            TrackerClient trackerClient = new TrackerClient();
            //4.创建trackerserver 对象
            TrackerServer trackerServer = trackerClient.getConnection();

            ServerInfo[] group1s = trackerClient.getFetchStorages(trackerServer, groupName, remoteFileName);
            return group1s;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }

    //获取tracker 的ip和端口的信息
    //http://192.168.211.132:8080
    public static String getTrackerUrl(){
        try {
            //3.创建trackerclient对象
            TrackerClient trackerClient = new TrackerClient();
            //4.创建trackerserver 对象
            TrackerServer trackerServer = trackerClient.getConnection();
            //tracker 的ip的信息
            String hostString = trackerServer.getInetSocketAddress().getHostString();

            //http://192.168.211.132:8080/group1/M00/00/00/wKjThF1aW9CAOUJGAAClQrJOYvs424.jpg img
            int g_tracker_http_port = ClientGlobal.getG_tracker_http_port();
            return "http://" + hostString + ":" + g_tracker_http_port;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }*/
}
