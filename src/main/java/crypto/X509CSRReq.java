package crypto;

import lombok.Data;
import utils.crypto.CryptoUtil;

import java.util.Date;

/**
 * 인증서 서명을 요청하기 위한 클라이언트의 값을 설정하기 위한 클래스이다.
 * CSR V3 : Certificate Signing Request (CSR)
 */
@Data
public class X509CSRReq {
    private CryptoUtil.ECCSigAlgorithm eccSigAlgorithm;

    private String hexPublicKey;

    //사용자 이름::성별::생년월일::연락처::publicKey(Hex String), Root CA의 경우 - www.common.com
    private String cn;

    //KR
    private String countryCode;
    //조직명
    private String organization;
    //Service Provider 이름 또는 도메인
    private String organizationUtit;

    private Date startDate;
    private Date expiryDate;
}
