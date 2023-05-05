package common.Utils;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import common.constant.CookieConstant;

/**
 * cookie操作相关工具
 * @author 沐
 */
public class CookieUtils {

    /**
     * 创建一个SymmetricCrypto对象，使用AES算法进行对称加密，并指定密钥为CookieConstant.autoLoginKey。
     * 其中，SymmetricAlgorithm.AES表示使用AES算法进行加密，CookieConstant.autoLoginKey是一个字符串常量，
     * 表示加密所使用的密钥。
     */
    /**
     * AES 算法的加密和解密过程使用相同的密钥，因此也称为对称加密算法。
     * 它采用分组密码的方式对数据进行加密，将明文数据分成若干个固定长度的数据块，然后对每个数据块进行加密。
     * AES 算法支持多种加密模式，包括 ECB（电子密码本）、CBC（密码分组链接）、CFB（密码反馈模式）和 OFB（输出反馈模式）等。
     */
    SymmetricCrypto aes = new SymmetricCrypto(SymmetricAlgorithm.AES, CookieConstant.autoLoginKey);


    /**
     * 根据用户id和账号去生成记住登录的密钥
     * @param id
     * @param userAccount
     * @return
     */
    public String generateAutoLoginContent(String id , String userAccount){
        String res = id+ ":" + userAccount+ ":" + CookieConstant.autoLoginKey;
        return aes.encryptHex(res);
    }



    /**
     * 对remember-me的key进行解密
     * @param encryptHex
     * @return
     */
    public String[] decodeAutoLoginKey(String encryptHex){
        String decryptStr = aes.decryptStr(encryptHex, CharsetUtil.CHARSET_UTF_8);
        return decryptStr.split(":");
    }
}
