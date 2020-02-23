package cn.iguxue.seckill.dao.mapper;

import cn.iguxue.seckill.entity.Seckill;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.Date;

public interface SeckillMapper extends BaseMapper<Seckill> {

    int reduceNumber(long seckillId, Date killTime);
}
