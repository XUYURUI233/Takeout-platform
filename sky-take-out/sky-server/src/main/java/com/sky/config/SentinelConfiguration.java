package com.sky.config;

import com.alibaba.csp.sentinel.annotation.aspectj.SentinelResourceAspect;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Configuration
@Slf4j
public class SentinelConfiguration {

    @Bean
    public SentinelResourceAspect sentinelResourceAspect() {
        return new SentinelResourceAspect();
    }

    @PostConstruct
    public void initRules() {
        // 基础 QPS 限流（按资源名）
        List<FlowRule> rules = new ArrayList<>();
        rules.add(buildQpsRule("user_seckill_activity_active", 50));
        rules.add(buildQpsRule("user_seckill_goods_detail", 100));
        rules.add(buildQpsRule("user_seckill_order_submit", 20));
        FlowRuleManager.loadRules(rules);

        log.info("Sentinel 规则初始化完成");
    }

    private FlowRule buildQpsRule(String resource, double qps) {
        FlowRule rule = new FlowRule();
        rule.setResource(resource);
        rule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        rule.setCount(qps);
        return rule;
    }
}


