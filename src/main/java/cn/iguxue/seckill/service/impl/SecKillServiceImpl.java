package cn.iguxue.seckill.service.impl;

import cn.iguxue.seckill.dto.Exposer;
import cn.iguxue.seckill.dto.SeckillExecution;
import cn.iguxue.seckill.entity.Seckill;
import cn.iguxue.seckill.entity.SuccessKilled;
import cn.iguxue.seckill.enums.SeckillStateEnum;
import cn.iguxue.seckill.exception.RepeatKillException;
import cn.iguxue.seckill.exception.SeckillCloseException;
import cn.iguxue.seckill.exception.SeckillException;
import cn.iguxue.seckill.dao.mapper.SeckillMapper;
import cn.iguxue.seckill.dao.mapper.SuccessKilledMapper;
import cn.iguxue.seckill.service.SecKillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.List;

@Service
public class SecKillServiceImpl implements SecKillService {

    @Autowired
    SeckillMapper seckillMapper;

    @Autowired
    SuccessKilledMapper successKilledMapper;

    @Autowired
    RedisTemplate redisTemplate;

    //md5盐值字符串，用于混淆MD5
    private final String slat = "sdasdasdascdfvea#2131223232.";

    @Override
    public List<Seckill> getSeckillList() {
        return seckillMapper.selectList(null);
    }

    @Override
    public Seckill getById(Long seckillId) {
        return seckillMapper.selectById(seckillId);
    }

    @Override
    public Exposer exportSeckillUrl(long seckillId) {
        //Seckill seckill = seckillMapper.selectById(seckillId);
        System.out.println("into exporter===============");
        Seckill seckill = (Seckill) redisTemplate.boundHashOps("seckill").get(seckillId);

        System.out.println("hhhh===" + seckillId);
        if (seckill == null) {
            //说明redis缓存中没有此key对应的value
            //查询数据库，并将数据放入缓存中
            seckill = seckillMapper.selectById(seckillId);

            if (seckill == null) {
                //说明没有查询到
                return new Exposer(false,seckillId);
            } else {
                //查询到了，存入redis缓存中。 key:秒杀表的ID值； value:秒杀表数据
                redisTemplate.boundHashOps("seckill").put(seckill.getSeckillId(), seckill);
            }
        }

        String md5 = getMd5(seckillId);

        return new Exposer(true, md5, seckillId);
    }

    private String getMd5(long seckillId) {
        String base = seckillId + "/" + slat;
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
        return md5;
    }

    @Override
    @Transactional
    public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5)
            throws SeckillException, RepeatKillException, SeckillCloseException {
        if (md5 == null || ! md5.equals(getMd5(seckillId))) {
            throw new SeckillException("seckill data rewrite");
        }

        //执行秒杀逻辑：减库存+记录秒杀行为
        Date nowTime = new Date();

        try {
            // 记录秒杀记录
            int insertCount = successKilledMapper.insertSuccessKilled(seckillId, userPhone);

            if (insertCount <= 0) {
                // 重复秒杀
                throw new RepeatKillException("seckill repeated");
            } else {
                // 减库存；热点商品竞争
                int updateCount = seckillMapper.reduceNumber(seckillId, nowTime);
                if (updateCount <= 0) {
                    // 没有更新到记录，秒杀结束
                    throw new SeckillCloseException("seckill is closed");
                } else {
                    // 秒杀成功
                    SuccessKilled successKilled = successKilledMapper.queryByIdWithSeckill(seckillId, userPhone);
                    return new SeckillExecution(seckillId, SeckillStateEnum.SUCCESS, successKilled);
                }
            }
        } catch (SeckillCloseException e1) {
            throw e1;
        } catch (RepeatKillException e2) {
            throw e2;
        } catch (Exception e) {
            //logger.error(e.getMessage(), e);
            //所有编译期异常 转化为运行期异常 可以让spring回滚
            throw new SeckillException("seckill inner error" + e.getMessage());
        }
    }
}
