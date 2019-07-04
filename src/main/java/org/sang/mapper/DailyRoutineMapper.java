package org.sang.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.sang.bean.DailyRoutine;

import java.sql.Timestamp;
import java.util.List;

@Mapper
public interface DailyRoutineMapper {

    int addNewDailyRoutine(DailyRoutine dailyRoutine);

    int updateDailyRoutine(DailyRoutine dailyRoutine);

    int updateDailyRoutineState(@Param("ids") Long ids[], @Param("editTime") Timestamp editTime, @Param("suid") Long suid, @Param("state") Integer state);

    int deleteDailyRoutineById(@Param("ids") Long[] ids);

    int addDailyRoutine(DailyRoutine dailyRoutine);

    DailyRoutine getDailyRoutineById(@Param("id") Long id);

    List<DailyRoutine> getDailyRoutineByState(@Param("state") Integer state, @Param("start") Integer start, @Param("count") Integer count, @Param("keywords") String keywords, @Param("cid") Integer cid);

    int getDailyRoutineCountByState(@Param("state") Integer state, @Param("keywords") String keywords, @Param("cid") Integer cid);

    int DailyRoutineToFirst(@Param("id") Long id,@Param("editTime")Timestamp editTime,@Param("isTop") Integer isTop);

    void downIncrement(@Param("fid") Long fid);
}
