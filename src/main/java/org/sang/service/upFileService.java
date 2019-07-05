package org.sang.service;

import org.sang.bean.Article;
import org.sang.bean.upFile;
import org.sang.bean.vo.upFileBean;
import org.sang.mapper.upFileMapper;
import org.sang.utils.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class upFileService {

    @Autowired
    upFileMapper upfileMapper;

    public int addupFile(upFile upfile){
        return upfileMapper.addupFile(upfile);
    }

    public upFile getupFileById(Long id){
        upFile upfile = upfileMapper.getupFileById(id);
        return upfile;
    }

    public String updateUpFileState(Long[] ids,Integer state) {
        String result = null;
        if (state == 2) {
            if (upfileMapper.deleteUpFileById(ids) != 0)
                result = "文件资料已删除！";
        }
        if (state != 2) {
            Timestamp editTime = new Timestamp(System.currentTimeMillis());
            Long suid = Util.getCurrentUser().getId();
            if (upfileMapper.updateUpFileState(ids, editTime, suid, state) != 0) {
                result = "文件资料状态已更改！";
            }
        }
        return result;
    }

    public List<upFile> getupFileByState(Integer state, Integer page, Integer count, Integer type ,String keywords){
        int start = (page - 1) * count;
        List<upFile> list = new ArrayList<upFile>();
        if(type  == 1)
            list = upfileMapper.getupFileByState(state,start,count,keywords,20,35);
        if(type  == 2)
            list = upfileMapper.getupFileByState(state,start,count,keywords,36,40);
        return list;
    }

    public int getupFileCountByState(Integer state,Integer id ,String keywords){
        if(id  == 1)
            return upfileMapper.getupFileCountByState(state,keywords,20,35);
        if(id  == 2)
            return upfileMapper.getupFileCountByState(state,keywords,36,50);
        return 0;
    }

    public List<upFile> getupFileByStateAndcid(Integer state, Integer page, Integer count, Long cid ,String keywords){
        int start = (page - 1) * count;
        return upfileMapper.getupFileByStateAndcid(state,start,count,keywords,cid);

    }

    public int getupFileCountByStateAndcid(Integer state,Long cid ,String keywords){
        return upfileMapper.getupFileCountByStateAndcid(state,keywords,cid);
    }

    public int upFileToFirst(Long id,Integer isTop){
        Timestamp editTime = new Timestamp(System.currentTimeMillis());
        return upfileMapper.upFileToFirst(id,editTime,isTop);
    }

    public void downIncrement(Long fid) {
        upfileMapper.downIncrement(fid);
    }
    /**
     * 法律法规类别下前十下载量的文件
     * @return
     */
    public List<upFileBean> getLawStatistics(){
        return  upfileMapper.getLawStatistics();
    }

    /**
     * 学习资料类别下前十下载量的文件
     * @return
     */
    public List<upFileBean> getStudyStatistics(){
        return upfileMapper.getStudyStatistics();
    }
}
