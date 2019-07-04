package org.sang.service;

import org.sang.bean.DailyRoutine;
import org.sang.mapper.DailyRoutineMapper;
import org.sang.utils.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class DailyRoutineService {

    @Autowired
    DailyRoutineMapper dailyRoutineMapper;

    public int addDailyRoutine(DailyRoutine dailyRoutine){
        return dailyRoutineMapper.addDailyRoutine(dailyRoutine);
    }

    public DailyRoutine getDailyRoutineById(Long id){
        DailyRoutine dailyRoutine = dailyRoutineMapper.getDailyRoutineById(id);
        return dailyRoutine;
    }

    public String updateDailyRoutineState(Long[] ids,Integer state) {
        String result = null;
        if (state == 2) {
            if (dailyRoutineMapper.deleteDailyRoutineById(ids) != 0)
                result = "文件资料已删除！";
        }
        if (state != 2) {
            Timestamp editTime = new Timestamp(System.currentTimeMillis());
            Long suid = Util.getCurrentUser().getId();
            if (dailyRoutineMapper.updateDailyRoutineState(ids, editTime, suid, state) != 0) {
                result = "文件资料状态已更改！";
            }
        }
        return result;
    }

    public List<DailyRoutine> getDailyRoutineByState(Integer state, Integer page, Integer count, String keywords){
        int start = (page - 1) * count;
        return dailyRoutineMapper.getDailyRoutineByState(state,start,count,keywords,80);
    }

    public int getDailyRoutineCountByState(Integer state,String keywords){
        return dailyRoutineMapper.getDailyRoutineCountByState(state,keywords,80);
    }

    public int DailyRoutineToFirst(Long id,Integer isTop){
        Timestamp editTime = new Timestamp(System.currentTimeMillis());
        return dailyRoutineMapper.DailyRoutineToFirst(id,editTime,isTop);
    }

    public void downIncrement(Long fid) {
        dailyRoutineMapper.downIncrement(fid);
    }
}
