package common.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * echarts需要返回的数据
 * @author 沐
 */
@Data
public class EchartsVo implements Serializable {
    private Long count;

    private String date;
}
