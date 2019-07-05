package org.sang.controller;

import org.sang.bean.DailyRoutine;
import org.sang.bean.RespBean;
import org.sang.service.DailyRoutineService;
import org.sang.utils.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/dailyRoutine")
public class DailyrRoutineController {

    @Autowired
    DailyRoutineService dailyRoutineService;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

    @Value("${prop.upload-dailyRoutine}")
    private String FILE_PATH_PERFIX;

    //获取日常事务资料文件
    @RequestMapping(value = "/allDr", method = RequestMethod.GET)
    public RespBean getdailyRoutineByState(@RequestParam(value = "state",defaultValue = "1") Integer state,
                                     @RequestParam(value = "page",defaultValue = "1") Integer page,
                                     @RequestParam(value = "count", defaultValue = "6") Integer count,
                                     String keywords){
        int totalCount = dailyRoutineService.getDailyRoutineCountByState(state,keywords);
        List<DailyRoutine> dailyRoutines = dailyRoutineService.getDailyRoutineByState(state, page, count,keywords);
        Map<String, Object> map = new HashMap<>();
        map.put("totalCount", totalCount);
        map.put("dailyRoutines", dailyRoutines);
        return RespBean.ok("获得日常事务相关文件成功",map);
    }

    //审核员模块
    //0代表未审核，1代表审核通过，2代表删除该文件
    @RequestMapping(value = "/updateDrState" ,method=RequestMethod.POST)
    public RespBean updateDailyRoutineStateById(Long[] ids,Integer state){
        if(ids.length == 0 || ids== null){
            return RespBean.error("传入的日常事务文件id有误，请重新检查!");
        }
        if(state==null ||state < 0 || state > 2 )
            return RespBean.error("输入日常事务相关文件的state值有误");
        if(Util.isShenhe()){
            String result = dailyRoutineService.updateDailyRoutineState(ids,state);
            if(result != null && result.length() != 0)
                return RespBean.ok(result);
            return  RespBean.error("后台没有对日常事务进行处理，请检查传入的ids是否正确！");
        }else
            return RespBean.error("你的权限不足，请联系管理员修改权限");
    }

    //置顶操作
    @RequestMapping(value = "/updateDrTop",method = RequestMethod.GET)
    public RespBean DailyRoutineToFirst(Long id,Integer isTop){
        if(isTop.intValue()<0 || isTop.intValue() >1)
            return RespBean.error("输入的isTop值有误！请检查后重新输入");
        if(Util.isShenhe()){
            int i = dailyRoutineService.DailyRoutineToFirst(id,isTop);
            if (i == 1) {
                return new RespBean("success", "日常事务设置/取消置顶操作成功!");
            }
            return new RespBean("error", "日常事务设置/取消置顶操作失败!");
        }else
            return RespBean.error("你的权限不足，请联系管理员修改权限");
    }

    @RequestMapping(value = "/getDrById", method = RequestMethod.GET)
    public RespBean getupFileById(Long id) {
        if(id != null){
            DailyRoutine dailyRoutine = dailyRoutineService.getDailyRoutineById(id);
            Map<String, Object> map = new HashMap<>();
            map.put("dailyRoutine", dailyRoutine);
            return RespBean.ok("获得相应id下的文件资料成功！",map);
        }
        return RespBean.error("获取对应类别的id有误，请检查重新输入!");
    }

 /*   public File getFilepath(){
        String filePath = new String("./"+FILE_PATH_PERFIX);;
        File fileDir = new File(filePath);
        if(!fileDir.exists()){
            // 递归生成文件夹
            fileDir.mkdirs();
        }
        return fileDir;
    }*/
    //日常事务管理的新增上传功能
    @RequestMapping(value="/uploadDr",method = RequestMethod.POST)
    public Object singleFileUpload(HttpServletRequest request, @RequestParam("file") MultipartFile file,
                                   @RequestParam("title") String title,
                                   @RequestParam("remark") String remark){
        if(Objects.isNull(file) || file.isEmpty()){
            return  RespBean.error("日常事务文件为空，添加文件失败!");
        }
        if(title == null ||title.length() ==0)
            return RespBean.error("输入日常事务文件的标题为空，请重新输入");
        StringBuffer url = new StringBuffer();
        StringBuffer uploadFilePath = new StringBuffer();
        String filePath = new String(FILE_PATH_PERFIX+ sdf.format(new Date()));
        System.out.println("filePath: "+filePath);

       /* String fileFolderPath = request.getServletContext().getRealPath(filePath);
        System.out.println("fileFolderPath: "+fileFolderPath);*/
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
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename().replaceAll(" ", "");
        try {
            File newFile = new File(fileFolder, fileName);
            System.out.println("newFile.getAbsolutePath()" + newFile.getAbsolutePath());  //控制日志打印输出
            file.transferTo(newFile);

            url.append("/").append(fileName);
            uploadFilePath.append(filePath.substring(filePath.length() - 8)).append(File.separator).append(fileName);  //注意平台的区别  不能随意用“/”

            //上传成功后同时将路径更新进数据库   （suid、editTime）
            DailyRoutine dailyRoutine = new DailyRoutine();
            dailyRoutine.setTitle(title);
            dailyRoutine.setPath(uploadFilePath.toString());   //
            dailyRoutine.setRemark(remark);
            dailyRoutine.setCid(new Long(80));
            dailyRoutine.setUid(Util.getCurrentUser().getId());
            dailyRoutine.setUploadTime(new Timestamp(System.currentTimeMillis()));
            dailyRoutine.setState(0);
            dailyRoutine.setIsTop(0);
            dailyRoutine.setDownNumber(0);

            int result = dailyRoutineService.addDailyRoutine(dailyRoutine);
            if(result==1)
                return RespBean.ok("日常事务管理文件上传成功! 文件url="+url.toString());
            else
                return RespBean.error("日常事务管理文件上传成功,更新数据库失败！");
        } catch (IOException e) {
            e.printStackTrace();
            return  RespBean.error("后端异常...");
        }
    }

    @RequestMapping(value = "/downloadDr",method = RequestMethod.GET)  //根据文件对应的id，在数据库取得文件存储的相对路径，再进行对应的下载
    public Object downloadFile(HttpServletRequest request, HttpServletResponse response, Long id) {
        DailyRoutine dailyRoutine = dailyRoutineService.getDailyRoutineById(id);
        String fileRoot = dailyRoutine.getPath();

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

        System.out.println("url:  "+url);
        map.put("url", url);

        if (dailyRoutine != null) {

            System.out.println("fileName.substring(0,8): "+fileRoot.substring(0,8));// /docfile/20190622
            System.out.println("fileName.substring(8): "+fileRoot.substring(8));  //\da840290-86dd-4f8c-a2e3-1bb8b9ee319d_1.txt

            String fileFolderPath = FILE_PATH_PERFIX + fileRoot.substring(0, 8); ;
            System.out.println("fileFolderPath: "+fileFolderPath);
            String fileName = fileRoot.substring(8);

            File fileFolder = new File(fileFolderPath);
            File file = new File(fileFolder, fileName);

            System.out.println("filePath:  "+file.getAbsolutePath());
            if (file.exists()) {
                response.setContentType("application/force-download");// 设置强制下载不打开
                // response.addHeader("Content-Disposition", "attachment;fileName=" + fileName);// 设置文件名*/
                // response.setContentType("multipart/form-data");
                response.setCharacterEncoding("UTF-8");
                response.setHeader("Content-Disposition", "attachment;filename=" + fileName.substring(fileName.lastIndexOf("_")+1));
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
                    dailyRoutineService.downIncrement(id);  //更新该文件的下载次数
                    return RespBean.ok( "文件下载成功",map);
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
        return  RespBean.error("文件下载失败...");
    }

}
