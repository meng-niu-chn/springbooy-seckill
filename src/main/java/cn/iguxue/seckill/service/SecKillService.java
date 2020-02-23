package cn.iguxue.seckill.service;

import cn.iguxue.seckill.dto.Exposer;
import cn.iguxue.seckill.dto.SeckillExecution;
import cn.iguxue.seckill.entity.Seckill;
import cn.iguxue.seckill.exception.RepeatKillException;
import cn.iguxue.seckill.exception.SeckillCloseException;
import cn.iguxue.seckill.exception.SeckillException;

import java.util.List;

/**
 * 业务接口：站在“使用者”角度设计接口
 * 三个方面：方法定义粒度，参数，返回类型（return类型友好 / 异常 ）
 */
public interface SecKillService {
    /**
     * 查找 所有 秒杀
     * @return
     */
    List<Seckill> getSeckillList();

    /**
     * 根据id查找单个秒杀
     * @param seckillId
     * @return
     */
    Seckill getById(Long seckillId);

    /**
     * 秒杀开启时输出秒杀地址，
     * 否则输出系统时间和秒杀时间
     * @param seckillId
     * @return
     */
    Exposer exportSeckillUrl(long seckillId);

    /**
     * 执行秒杀操作
     * @param seckillId
     * @param userPhone
     * @param md5
     * @return
     */
    SeckillExecution executeSeckill(long seckillId, long userPhone, String md5)
        throws SeckillException, RepeatKillException, SeckillCloseException;

}
