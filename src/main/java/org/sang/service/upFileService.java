package org.sang.service;

import org.sang.bean.upFile;
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

    public int updateUpFileState(Long[] ids,Integer state) {
        if (state == 2) {
            return upfileMapper.deleteUpFileById(ids);
        } else {
            Timestamp editTime = new Timestamp(System.currentTimeMillis());
            Long suid = Util.getCurrentUser().getId();
            return upfileMapper.updateUpFileState(ids,editTime,suid,state);//放入到回收站中
        }
    }

    public List<upFile> getupFileByState(Integer state, Integer page, Integer count, Integer id ,String keywords){
        int start = (page - 1) * count;
        List<upFile> list = new ArrayList<upFile>();
        if(id  == 1)
            list = upfileMapper.getupFileByState(state,start,count,keywords,20,35);
        if(id  == 2)
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

}
