package com.sky.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sky.service.SeckillLuaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SeckillLuaServiceImpl implements SeckillLuaService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Map<String, Object> executeStockDeduct(Long goodsId, Long userId, Integer quantity, Integer limitCount, Integer expireSeconds) {
        try {
            String stockKey = "seckill:stock:" + goodsId;
            String userKey = "seckill:user:limit:" + userId + ":" + goodsId;
            String soldKey = "seckill:sold:" + goodsId;

            List<String> keys = Arrays.asList(stockKey, userKey, soldKey);
            List<String> args = Arrays.asList(
                    String.valueOf(quantity),
                    String.valueOf(limitCount),
                    String.valueOf(userId),
                    String.valueOf(expireSeconds != null ? expireSeconds : 0)
            );

            String lua = loadLua("lua/stock_deduct.lua");
            DefaultRedisScript<String> script = new DefaultRedisScript<>(lua, String.class);
            String resultJson = stringRedisTemplate.execute(script, keys, (Object[]) args.toArray(new String[0]));

            if (resultJson == null) {
                Map<String, Object> r = new HashMap<>();
                r.put("code", 50010);
                r.put("msg", "系统繁忙，请稍后重试");
                return r;
            }

            Map<String, Object> result = objectMapper.readValue(resultJson, new TypeReference<Map<String, Object>>(){});
            log.debug("Lua执行结果: {}", result);
            return result;
        } catch (Exception e) {
            log.error("执行Lua脚本失败", e);
            Map<String, Object> r = new HashMap<>();
            r.put("code", 50010);
            r.put("msg", "系统繁忙，请稍后重试");
            return r;
        }
    }

    private String loadLua(String path) throws Exception {
        ClassPathResource resource = new ClassPathResource(path);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }
}


