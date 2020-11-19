package io.imwj.miaosha.redis;

/**
 * key头的生成策略
 * @author langao_q
 * @since 2020-11-19 14:26
 */
public abstract class BasePrefix implements KeyPrefix{

    private int expireSeconds;

    private String prefix;

    public BasePrefix(String prefix){
        this(0, prefix);
    }

    public BasePrefix(int expireSeconds, String prefix){
        this.expireSeconds = expireSeconds;
        this.prefix = prefix;
    }

    @Override
    public int expireSeconds() {//默认0代表永不过期
        return expireSeconds;
    }

    @Override
    public String getPrefix(){
        String className = getClass().getSimpleName();
        return className+":" + prefix + "-";
    }
}
