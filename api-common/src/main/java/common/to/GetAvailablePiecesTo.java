package common.to;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 沐
 */
@Data
public class GetAvailablePiecesTo implements Serializable {
    /**
     * 接口id
     */
    private Long interfaceId;
}
