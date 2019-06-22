package org.sang.service;

import org.sang.bean.upFile;
import org.sang.mapper.upFileMapper;
import org.sang.utils.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

@Service
@Transactional
public class upFileService {

    @Autowired
    upFileMapper upfileMapper;

    public int addupFile(upFile upfile){
        upfile.setUploadTime(new Timestamp(System.currentTimeMillis()));
        upfile.setUid(Util.getCurrentUser().getId());
        return upfileMapper.addupFile(upfile);
    }

    public upFile getupFileById(long fid){
        upFile upfile = upfileMapper.getupFileById(fid);
        return upfile;
    }

    public List<upFile> getupFileByState(Integer state, Integer page, Integer count, String keywords){
        int start = (page - 1) * count;
        int uid = Util.getCurrentUser().getId().intValue();
        return upfileMapper.getupFileByState(state,start,count,uid,keywords);
    }
    public int getupFileCountByState(Integer state,int uid,String keywords){
        return upfileMapper.getupFileCountByState(state,uid,keywords);
    }

}
