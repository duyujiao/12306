package com.jiawa.train.business.service;

import cn.hutool.core.date.DateUtil;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.fastjson.JSON;
import com.jiawa.train.business.enums.RedisKeyPreEnum;
import com.jiawa.train.business.enums.RocketMQTopicEnum;
import com.jiawa.train.business.mapper.ConfirmOrderMapper;
import com.jiawa.train.business.req.ConfirmOrderDoReq;
import com.jiawa.train.common.context.LoginMemberContext;
import com.jiawa.train.common.exception.BusinessException;
import com.jiawa.train.common.exception.BusinessExceptionEnum;
import jakarta.annotation.Resource;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class BeforeConfirmOrderService {

    private static final Logger LOG = LoggerFactory.getLogger(BeforeConfirmOrderService.class);

    @Resource
    private ConfirmOrderMapper confirmOrderMapper;

    @Autowired
    private SkTokenService skTokenService;
    @Autowired
    private StringRedisTemplate redisTemplate;

     @Resource
     //public RocketMQTemplate rocket
     public RocketMQTemplate rocketMQTemplate;

    @Resource
    private ConfirmOrderService confirmOrderService;

    @SentinelResource(value = "beforeDoConfirm", blockHandler = "beforeDoConfirmBlock")
    public void beforeDoConfirm(ConfirmOrderDoReq req) {
        //Long id = null;
        // 根据前端传值，加入排队人数
//        for (int i = 0; i < req.getLineNumber() + 1; i++) {
//            req.setMemberId(LoginMemberContext.getId());
            // 校验令牌余量
            boolean validSkToken = skTokenService.validSkToken(req.getDate(), req.getTrainCode(), LoginMemberContext.getId());
            if (validSkToken) {
                LOG.info("令牌校验通过");
            } else {
                LOG.info("令牌校验不通过");
                throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_SK_TOKEN_FAIL);
            }


        // 获取分布式锁
        String lockKey = RedisKeyPreEnum.CONFIRM_ORDER+ "-" + DateUtil.formatDate(req.getDate() )+ "-" + req.getTrainCode();
        // setIfAbsent就是对应redis的setnx
        Boolean setIfAbsent = redisTemplate.opsForValue().setIfAbsent(lockKey, lockKey, 10, TimeUnit.SECONDS);
        if (Boolean.TRUE.equals(setIfAbsent)) {
            LOG.info("恭喜，抢到锁了！lockKey：{}", lockKey);
        } else {
            //只是没抢到锁，并不知道票抢完了没，所以提示稍候再试
            LOG.info("很遗憾，没抢到锁！lockKey：{}", lockKey);
            throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_LOCK_FAIL);
        }

//            Date date = req.getDate();
//            String trainCode = req.getTrainCode();
//            String start = req.getStart();
//            String end = req.getEnd();
//            List<ConfirmOrderTicketReq> tickets = req.getTickets();
//
//            // 保存确认订单表，状态初始
//            DateTime now = DateTime.now();
//            ConfirmOrder confirmOrder = new ConfirmOrder();
//            confirmOrder.setId(SnowUtil.getSnowflakeNextId());
//            confirmOrder.setCreateTime(now);
//            confirmOrder.setUpdateTime(now);
//            confirmOrder.setMemberId(req.getMemberId());
//            confirmOrder.setDate(date);
//            confirmOrder.setTrainCode(trainCode);
//            confirmOrder.setStart(start);
//            confirmOrder.setEnd(end);
//            confirmOrder.setDailyTrainTicketId(req.getDailyTrainTicketId());
//            confirmOrder.setStatus(ConfirmOrderStatusEnum.INIT.getCode());
//            confirmOrder.setTickets(JSON.toJSONString(tickets));
//            confirmOrderMapper.insert(confirmOrder);

            // 发送MQ排队购票
//            ConfirmOrderMQDto confirmOrderMQDto = new ConfirmOrderMQDto();
//            confirmOrderMQDto.setDate(req.getDate());
//            confirmOrderMQDto.setTrainCode(req.getTrainCode());
//            confirmOrderMQDto.setLogId(MDC.get("LOG_ID"));
//            String reqJson = JSON.toJSONString(confirmOrderMQDto);
                String reqJson = JSON.toJSONString(req);
                 LOG.info("排队购票，发送mq开始，消息：{}", reqJson);
//            // rocketMQTemplate.convertAndSend(RocketMQTopicEnum.CONFIRM_ORDER.getCode(), reqJson);
                rocketMQTemplate.convertAndSend(RocketMQTopicEnum.CONFIRM_ORDER.getCode(), reqJson);
                LOG.info("排队购票，发送mq结束");
//            confirmOrderService.doConfirm(confirmOrderMQDto);
//            id = confirmOrder.getId();
//        }
//        return id;
    }

    /**
     * 降级方法，需包含限流方法的所有参数和BlockException参数
     * @param req
     * @param e
     */
    public void beforeDoConfirmBlock(ConfirmOrderDoReq req, BlockException e) {
        LOG.info("购票请求被限流：{}", req);
        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_FLOW_EXCEPTION);
    }
}
