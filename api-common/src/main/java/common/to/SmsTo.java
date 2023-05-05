package common.to;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author 沐
 * 发送手机号对象
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SmsTo implements Serializable {
    /**
     * 手机号
     */
    private String mobile;

    /**
     * 验证码
     */
    private String code;
}
