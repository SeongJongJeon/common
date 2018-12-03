package utils.crypto.asymmetric;

import crypto.AuthenticationReq;
import crypto.RootCA;
import crypto.X509CSRReq;
import crypto.X509CSRRes;
import org.junit.Test;
import utils.DateUtil;
import utils.crypto.CryptoUtil;
import utils.crypto.HexUtil;

import java.security.KeyPair;

import static org.junit.Assert.assertNotNull;

public class TestCertByECDSAUtil {
    @Test
    public void crs() {
        //Root CA 생성
        KeyPair keyPair = ECDSAUtil.generateKey();

        RootCA rootCA = new RootCA();
        rootCA.setRootHexPrivKey(HexUtil.encodeString(keyPair.getPrivate().getEncoded()));
        rootCA.setHexPublicKey(HexUtil.encodeString(keyPair.getPublic().getEncoded()));

        X509CSRReq x509CSRReq = new X509CSRReq();
        x509CSRReq.setEccSigAlgorithm(CryptoUtil.ECCSigAlgorithm.SHA256);
        x509CSRReq.setHexPublicKey(HexUtil.encodeString(keyPair.getPublic().getEncoded()));
        x509CSRReq.setCn("www.common.com");
        x509CSRReq.setCountryCode("KR");
        x509CSRReq.setOrganization("Common");
        x509CSRReq.setOrganizationUtit("Development");
        x509CSRReq.setStartDate(DateUtil.getBeforeOrAfterDateOfHours(0, false));
        x509CSRReq.setExpiryDate(DateUtil.getBeforeOrAfterDateOfHours(24 * 365 * 10, false));

        X509CSRRes x509CSRRes = CertByECCUtil.generateX509V3(rootCA, true, x509CSRReq);
        assertNotNull(x509CSRRes);

        rootCA.setRootCert(CertByECCUtil.generateHexStrToCert(x509CSRRes.getHex(), false));
        rootCA.setRootCertHex(x509CSRRes.getHex());

        //Client 인증서 생성
        keyPair = ECDSAUtil.generateKey();

        x509CSRReq = new X509CSRReq();
        x509CSRReq.setEccSigAlgorithm(CryptoUtil.ECCSigAlgorithm.SHA256);
        x509CSRReq.setHexPublicKey(HexUtil.encodeString(keyPair.getPublic().getEncoded()));
        x509CSRReq.setCn("전성종");
        x509CSRReq.setCountryCode("KR");
        x509CSRReq.setOrganization("Soul");
        x509CSRReq.setOrganizationUtit("soul");
        x509CSRReq.setStartDate(DateUtil.getBeforeOrAfterDateOfHours(0, false));
        x509CSRReq.setExpiryDate(DateUtil.getBeforeOrAfterDateOfHours(24 * 365 * 10, false));

        x509CSRRes = CertByECCUtil.generateX509V3(rootCA, false, x509CSRReq);
        assertNotNull(x509CSRRes);

        //Client 인증서로 서명 검증
        String plainText = "아이고야!";
        AuthenticationReq authenticationReq = new AuthenticationReq();
        authenticationReq.setPlainText(plainText);
        authenticationReq.setCipherText(ECDSAUtil.signature(CryptoUtil.ECCSigAlgorithm.SHA256, plainText, HexUtil.encodeString(keyPair.getPrivate().getEncoded())));   //서명
        authenticationReq.setPem(x509CSRRes.getPem());

        String crl = CertByECCUtil.extractCertificateRevocationList(authenticationReq.getPem(), true);  //인증서 폐기목록 추출 (해당 정보를 통하여 인증서 폐기여부를 조회해야 함.)

        boolean isSuccess = CertByECCUtil.validateUserCert(rootCA, authenticationReq.getPem(), true);
        boolean isSuccessSig = ECDSAUtil.verifySignature(CryptoUtil.ECCSigAlgorithm.SHA256, authenticationReq.getPlainText(), authenticationReq.getCipherText(), authenticationReq.extractPubKeyByPem());
        System.out.println(isSuccess + " : " + isSuccessSig);
    }
}
