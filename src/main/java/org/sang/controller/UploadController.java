package org.sang.controller;


import org.apache.tomcat.util.http.fileupload.FileUploadBase;
import org.sang.bean.RespBean;
import org.sang.bean.upFile;
import org.sang.service.upFileService;
import org.sang.utils.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

//上传文件
@RestController
@RequestMapping("/file")
public class UploadController {

    @Autowired
    upFileService upfileService;

    @Value("${prop.upload-folder}")
    private String FILE_PATH_PERFIX;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    // public final static String filePath = "static/upload/docFile";


    public File getFilepath() {
        String filePath = new String("./" + FILE_PATH_PERFIX);
        ;
        File fileDir = new File(filePath);
        if (!fileDir.exists()) {
            // 递归生成文件夹
            fileDir.mkdirs();
        }
        return fileDir;
    }

    //法律法规，学习资料的新增上传功能
    @RequestMapping(value = "/uploadsingleFile", method = RequestMethod.POST)
    public Object singleFileUpload(HttpServletRequest request, @RequestParam("file") MultipartFile file,
                                   @RequestParam("cid") Long cid,
                                   @RequestParam("title") String title,
                                   @RequestParam("remark") String remark) {
        if (Objects.isNull(file) || file.isEmpty()) {
            return new RespBean("error", "文件为空，添加文件失败!");
        }
        if (title == null || title.length() == 0)
            return RespBean.error("输入文件的标题为空，请重新输入！");

        StringBuffer url = new StringBuffer();
        StringBuffer uploadFilePath = new StringBuffer();
        String filePath = new String(FILE_PATH_PERFIX + sdf.format(new Date()));
        System.out.println("filePath: " + filePath);
        // String fileFolderPath = request.getServletContext().getRealPath(filePath);
        //System.out.println("fileFolderPath: "+fileFolderPath);
        File fileFolder = new File(filePath);
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
        System.out.println("Url: " + url);
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename().replaceAll(" ", "");
        try {
            File newFile = new File(fileFolder, fileName);
            System.out.println("newFile.getAbsolutePath()" + newFile.getAbsolutePath());  //控制日志打印输出
            file.transferTo(newFile);

            url.append("/").append(fileName);
            uploadFilePath.append(filePath.substring(filePath.length() - 8)).append(File.separator).append(fileName);  //注意平台的区别  不能随意用“/”

            //上传成功后同时将路径更新进数据库   （suid、editTime）
            upFile upfile = new upFile();
            upfile.setTitle(title);
            upfile.setPath(uploadFilePath.toString());   //
            upfile.setRemark(remark);
            upfile.setCid(cid);
            upfile.setUid(Util.getCurrentUser().getId());
            upfile.setUploadTime(new Timestamp(System.currentTimeMillis()));
            upfile.setState(0);
            upfile.setIsTop(0);
            upfile.setDownNumber(0);

            int result = upfileService.addupFile(upfile);
            if (result == 1)
                return RespBean.ok("文件上传成功! 文件url=" + url.toString());
            else
                return RespBean.error("文件上传成功,更新数据库失败！");
        } catch (IOException e) {
            e.printStackTrace();
            return RespBean.error("后端异常...");
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
                    return RespBean.error("第 " + i + " 个文件上传失败 ==> " + e.getMessage());
                }
            } else {
                return RespBean.error("第 " + i + " 个文件上传失败因为文件为空");
            }
        }
        return new RespBean("success", "批量上传成功");
    }

    @RequestMapping(value = "/downloadFile", method = RequestMethod.GET)  //根据文件对应的id，在数据库取得文件存储的相对路径，再进行对应的下载
    public Object downloadFile(HttpServletRequest request, HttpServletResponse response, Long id) {
        upFile upfile = upfileService.getupFileById(id);
        String fileRoot = upfile.getPath();

        Map<String, Object> map = new HashMap<>();
        StringBuffer url = new StringBuffer();
        url.append(request.getScheme())
                .append("://")
                .append(request.getServerName())
                .append(":")
                .append(request.getServerPort())
                .append(request.getContextPath())
                .append(FILE_PATH_PERFIX)
                .append(fileRoot);
        System.out.println("url:  " + url);
        map.put("url", url);

        if (upfile != null) {
            System.out.println("fileRoot.substring(0,8): " + fileRoot.substring(0, 8));// 20190705
            System.out.println("fileRoot.substring(8): " + fileRoot.substring(8));  //  \ec9b49f1-a08a-4b5f-a09d-ef2091e5d5b5_1.txt

            String fileFolderPath = FILE_PATH_PERFIX + fileRoot.substring(0, 8); //   /d://upload/docfile/20190705
            System.out.println("fileFolderPath: " + fileFolderPath);
            String fileName = fileRoot.substring(8);

            File fileFolder = new File(fileFolderPath);
            File file = new File(fileFolder, fileName);

            System.out.println("filePath:  " + file.getAbsolutePath());
            if (file.exists()) {
                response.setContentType("application/force-download");// 设置强制下载不打开
                // response.addHeader("Content-Disposition", "attachment;fileName=" + fileName);// 设置文件名*/
                // response.setContentType("multipart/form-data");
                response.setHeader("Content-Disposition", "attachment;filename=" + fileName.substring(fileName.lastIndexOf("_") + 1)); //fileName.substring(17)
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
                    upfileService.downIncrement(id);  //更新该文件的下载次数
                    return RespBean.ok("文件下载成功", map);
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
        return RespBean.error("文件下载失败...");
    }
//测试没有起作用
 /*   @ExceptionHandler
    public RespBean doException(Exception e,HttpServletRequest request) throws Exception {
        Map<String,Object> map = new HashMap<String,Object>();
        if (e instanceof FileUploadBase.FileSizeLimitExceededException) {
            long maxSize = ((FileUploadBase.FileSizeLimitExceededException) e)
                    .getPermittedSize();
            map.put("error", "上传文件太大，不能超过" + maxSize / 1024 /1024 + "M");
        }else if(e instanceof RuntimeException){
            map.put("error", "未选中文件");
        }else{
            map.put("error", "上传失败");
        }
        return RespBean.error("上传时发生错误的信息提示",map);

    }*/

}