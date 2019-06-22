package org.sang.controller;

import org.sang.bean.RespBean;
import org.sang.bean.upFile;
import org.sang.service.upFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

//上传文件
@RestController
@RequestMapping("/upload")
public class UploadController {

    @Autowired
    upFileService upfileService;

    @Value("${prop.upload-folder}")
    private String FILE_PATH_PERFIX;



    private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
   // public final static String filePath = "static/upload/docFile";

    public File getFilepath(){
        String filePath = new String("./"+FILE_PATH_PERFIX);;
        File fileDir = new File(filePath);
        if(!fileDir.exists()){
            // 递归生成文件夹
            fileDir.mkdirs();
        }
        return fileDir;
    }
    @RequestMapping("/singlefile")
    public Object singleFileUpload(HttpServletRequest request,@RequestParam("file") MultipartFile file){
        if(Objects.isNull(file) || file.isEmpty()){
            return new RespBean("error", "文件为空，添加文件失败!");
        }

        StringBuffer url = new StringBuffer();
        StringBuffer uploadFilePath = new StringBuffer();
        String filePath = new String(FILE_PATH_PERFIX+ sdf.format(new Date()));
        System.out.println("filePath: "+filePath);
        String fileFolderPath = request.getServletContext().getRealPath(filePath);
        System.out.println("fileFolderPath: "+fileFolderPath);
        File fileFolder = new File(fileFolderPath);
        if (!fileFolder.exists()) {
            fileFolder.mkdirs();
        }
        url.append(request.getScheme())
                .append("://")
                .append(request.getServerName())
                .append(":")
                .append(request.getServerPort())
                .append(request.getContextPath())
                .append(filePath);
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename().replaceAll(" ", "");
        try {
            File newFile = new File(fileFolder, fileName);
            System.out.println(newFile.getAbsolutePath());  //控制日志打印输出
            file.transferTo(newFile);

            url.append("/").append(fileName);
            uploadFilePath.append(filePath).append(File.separator).append(fileName);  //注意平台的区别  不能随意用“/”


            //上传成功后同时将路径更新进数据库
            upFile upfile = new upFile();
            upfile.setTitle("测试上传文件1");
            upfile.setRemark("测试上传");
            upfile.setState(0);
            upfile.setPath(uploadFilePath.toString());
            int result = upfileService.addupFile(upfile);
            if(result==1)
                return new RespBean("success", "文件上传成功! 文件url="+url.toString());
            else
                return new RespBean("error", "文件上传成功,更新数据库失败！");
        } catch (IOException e) {
            e.printStackTrace();
            return  new RespBean("error","后端异常...");
        }
    }
/*
    @RequestMapping("/singlefile")
    public Object singleFileUpload(@RequestParam("file") MultipartFile file){
        if(Objects.isNull(file) || file.isEmpty()){
            return new RespBean("error", "文件为空，添加文件失败!");
        }
        try {
            String fileName = file.getOriginalFilename();
            uploadFilePath =  fileName;

            //文件写入指定路径
            File fileDir = getFilepath();
            File newFile = new File(fileDir.getAbsolutePath() +File.separator + fileName);
            System.out.println(newFile.getAbsolutePath());
            file.transferTo(newFile);

            //上传成功后同时将路径更新进数据库
            upFile upfile = new upFile();
            upfile.setTitle("测试上传文件1");
            upfile.setRemark("测试上传");
            upfile.setState(0);
            upfile.setPath(uploadFilePath);
            int result = upfileService.addupFile(upfile);
            if(result==1)
                return new RespBean("success", "文件上传成功");
            else
                return new RespBean("error", "文件上传成功,更新数据库失败！");
        } catch (IOException e) {
            e.printStackTrace();
            return  new RespBean("error","后端异常...");
        }
    }*/


    @PostMapping("/batch")  //批量上传文件  (功能如果需要扩展，需同时将文件路径保存进数据库中)
    public Object handleFileUpload(HttpServletRequest request) {
        List<MultipartFile> files = ((MultipartHttpServletRequest) request).getFiles("file");
        MultipartFile file = null;
        BufferedOutputStream stream = null;
        for (int i = 0; i < files.size(); ++i) {
            file = files.get(i);
           // String filePath = "/Users/dalaoyang/Downloads/";

            String fileName = file.getOriginalFilename();
            File fileDir = getFilepath();
            File newFile = new File(fileDir.getAbsolutePath() + File.separator + fileName);
            if (!file.isEmpty()) {
                try {
                    byte[] bytes = file.getBytes();
                    /*stream = new BufferedOutputStream(new FileOutputStream(
                            new File(filePath + file.getOriginalFilename())));//设置文件路径及名字*/
                    stream = new BufferedOutputStream(new FileOutputStream(
                            newFile));//设置文件路径及名字
                    stream.write(bytes);// 写入
                    stream.close();
                } catch (Exception e) {
                    stream = null;
                    return new RespBean("error", "第 " + i + " 个文件上传失败 ==> " + e.getMessage());
                }
            } else {
                return new RespBean("error","第 " + i + " 个文件上传失败因为文件为空");
            }
        }
        return new RespBean("success", "批量上传成功");
    }

    @RequestMapping("/downloadFile")  //根据文件对应的id，在数据库取得文件存储的相对路径，再进行对应的下载
    public Object downloadFile(HttpServletRequest request, HttpServletResponse response) {
        upFile upfile = upfileService.getupFileById(1);

        String fileName = upfile.getPath();  // "1.txt";// 文件名
        if (upfile != null) {
            //设置文件路径
            /*File fileDir = getFilepath();
            File file = new File(fileDir.getAbsolutePath()+File.separator +upfile.getPath());*/

            System.out.println("fileName.substring(0,17): "+fileName.substring(0,17));///docfile/20190622
            System.out.println("fileName.substring(17): "+fileName.substring(17));  //\da840290-86dd-4f8c-a2e3-1bb8b9ee319d_1.txt

            String fileFolderPath = request.getServletContext().getRealPath(fileName.substring(0,17));
            System.out.println("fileFolderPath: "+fileFolderPath);

            File fileFolder = new File(fileFolderPath);
            File file = new File(fileFolder, fileName.substring(17));

            System.out.println("filePath:  "+file.getAbsolutePath());
            if (file.exists()) {
               /* response.setContentType("application/force-download");// 设置强制下载不打开
                response.addHeader("Content-Disposition", "attachment;fileName=" + fileName);// 设置文件名*/
                response.setContentType("multipart/form-data");
                response.setHeader("Content-Disposition", "attachment;filename=" + fileName.substring(17)); //fileName.substring(17)
                byte[] buffer = new byte[1024];
                FileInputStream fis = null;
                BufferedInputStream bis = null;
                try {
                    fis = new FileInputStream(file);
                    bis = new BufferedInputStream(fis);
                    OutputStream os = response.getOutputStream();
                    int i = bis.read(buffer);
                    while (i != -1) {
                        os.write(buffer, 0, i);
                        i = bis.read(buffer);
                    }
                    return new RespBean("success", "文件下载成功");
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (bis != null) {
                        try {
                            bis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (fis != null) {
                        try {
                            fis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return  new RespBean("error","文件下载失败...");
    }
}
