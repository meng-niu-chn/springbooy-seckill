package cn.iguxue.seckill.dao.mapper;

import cn.iguxue.seckill.entity.SuccessKilled;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

public interface SuccessKilledMapper extends BaseMapper<SuccessKilled> {

    //插入购买明细，可过滤重复
    int insertSuccessKilled(long seckillId, long userPhone);

    //根据id查询SuccessKilled并携带秒杀产品对象实体
    SuccessKilled queryByIdWithSeckill(long seckillId, long userPhone);
}
