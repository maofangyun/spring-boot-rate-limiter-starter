package com.mfy.limiter.filter;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import javax.annotation.PostConstruct;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class RateLimiter implements Filter {

    @Autowired
    private StringRedisTemplate redisTemplate;

    private final AtomicInteger atomicInteger = new AtomicInteger();

    private final static String FILE_PATH = "classpath:lua/*.lua";

    protected String key;

    private final static HashMap<String,DefaultRedisScript<Long>> SCRIPT_MAP = new HashMap<>();

    @PostConstruct
    private void warmUp(){
        // 读取lua脚本传递到Redis
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        try {
            Resource[] resources = resolver.getResources(FILE_PATH);
            for (Resource resource : resources){
                DefaultRedisScript<Long> script = new DefaultRedisScript<>();
                // 指定ReturnType为Long.class，注意这里不能使用Integer.class，因为ReturnType不支持。只支持List.class, Boolean.class和Long.class
                script.setResultType(Long.class);
                script.setLocation(resource);
                String filename = resource.getFilename();
                SCRIPT_MAP.put(filename,script);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean tryAcquire(List<String> keys){
        DefaultRedisScript<Long> script = SCRIPT_MAP.get(key);
        Long result = redisTemplate.execute(script, keys);
        return result==1;
    }

    public abstract List<String> getKeys(List<String> keys);

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String url = request.getRequestURL().toString();
        List<String> keys = new ArrayList<>();
        keys.add(url);
        keys = getKeys(keys);
        if(tryAcquire(keys)){
            System.out.println("第"+atomicInteger.incrementAndGet()+"次请求: 通过");
            filterChain.doFilter(servletRequest, servletResponse);
        } else{
            System.out.println("第"+atomicInteger.incrementAndGet()+"次请求: 拒绝");
        }
    }

    public void setKey(String key) {
        this.key = key;
    }

}
