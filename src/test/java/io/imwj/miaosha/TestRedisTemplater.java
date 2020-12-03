package io.imwj.miaosha;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author langao_q
 * @since 2020-11-18 17:35
 */
@Slf4j
@SpringBootTest
public class TestRedisTemplater {

    public static void main(String[] args) {
        String str = "<!DOCTYPE HTML>\\r\\n<html>\\r\\n<head>\\r\\n  ";
        String replace = str.replace("\\r\\n", "");
        log.info(str);
        log.info(replace);
    }
}
