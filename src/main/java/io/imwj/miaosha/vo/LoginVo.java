package io.imwj.miaosha.vo;

import io.imwj.miaosha.validtor.IsMobile;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

/**
 * @author langao_q
 * @since 2020-11-24 17:01
 */
@Data
public class LoginVo {

    @NotNull
    @IsMobile
    private String mobile;

    @NotNull
    @Length(min=32)
    private String password;

}
