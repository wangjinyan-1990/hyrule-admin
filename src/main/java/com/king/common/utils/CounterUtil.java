package com.king.common.utils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.king.framework.counter.entity.SysCounter;
import com.king.framework.counter.mapper.SysCounterMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

@Component
public class CounterUtil {

    private static final Logger logger = LoggerFactory.getLogger(CounterUtil.class);

    @Autowired
    private SysCounterMapper sysCounterMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public CounterUtil(SysCounterMapper sysCounterMapper, JdbcTemplate jdbcTemplate) {
        this.sysCounterMapper = sysCounterMapper;
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 获取指定计数器的下一个编码
     * 规则：prefix + zeroPad(nextNumber, counterLength)
     * 并发安全：使用字符串常量池锁 + 数据库行级锁（FOR UPDATE）
     */
    @Transactional(rollbackFor = Exception.class)
    public String generateNextCode(String counterName) {
        Assert.hasText(counterName, "参数不能为空");

        // 查找计数器定义
        SysCounter counter = sysCounterMapper.selectOne(new LambdaQueryWrapper<SysCounter>()
                .eq(SysCounter::getCounterName, counterName));
        Assert.notNull(counter, "找不到计数器. counterName: " + counterName);

        int nextNumber;
        synchronized (counterName.intern()) {
            // 行级锁，避免并发下重复生成
            SysCounter locked = jdbcTemplate.queryForObject(
                    "select * from t_sys_counter where COUNTER_ID = ? for update",
                    new BeanPropertyRowMapper<>(SysCounter.class),
                    counter.getCounterId()
            );
            Assert.notNull(locked, "计数器记录不存在, id=" + counter.getCounterId());

            nextNumber = (locked.getCurrentNumber() == null ? 0 : locked.getCurrentNumber()) + 1;
            try {
                jdbcTemplate.update(
                        "update t_sys_counter set CURRENT_NUMBER = ? where COUNTER_ID = ?",
                        nextNumber, counter.getCounterId()
                );
            } catch (Exception e) {
                logger.error("更新计数器失败, counterName={}", counterName, e);
                throw e;
            }
        }

        String prefix = counter.getPrefix() == null ? "" : counter.getPrefix().trim();
        int length = Integer.parseInt(counter.getCounterLength());
        return String.format(prefix + "%0" + length + "d", nextNumber);
    }
}


