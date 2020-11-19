package io.imwj.miaosha.redis;

/**
 * User - Prefix的标识
 * @author langao_q
 * @since 2020-11-19 14:30
 */
public class UserKey extends BasePrefix {

    public UserKey(String prefix) {
        super(prefix);
    }

    public UserKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static UserKey getById = new UserKey("id");

    public static UserKey getByName = new UserKey("name");


    public static UserKey getByIdEx(int expireSeconds){
        return new UserKey(expireSeconds, "id");
    }

    public static UserKey getByNameEx(int expireSeconds){
        return new UserKey( expireSeconds, "name");
    }
}
