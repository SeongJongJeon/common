package utils.crypto;

import crypto.X509CSRReq;
import org.junit.Test;
import utils.DateUtil;

import java.security.KeyPair;

import static org.junit.Assert.assertEquals;

public class TestECCUtil {
    @Test
    public void signatureWithVerification() {
        KeyPair keyPair = ECCUtil.generateKey(CryptoUtil.ECCAlgorithm.MEDIUM);
        String hexPrivKey = ECCUtil.generateHexString(keyPair.getPrivate().getEncoded());
        String hexPubKey = ECCUtil.generateHexString(keyPair.getPublic().getEncoded());

        String sig = ECCUtil.signature(CryptoUtil.ECCSigAlgorithm.SHA256, "아이고~", hexPrivKey);
        boolean isSuccess = ECCUtil.verifySignature(CryptoUtil.ECCSigAlgorithm.SHA256, "아이고~", sig, hexPubKey);
        assertEquals(true, isSuccess);
    }

    @Test
    public void requestCSR() {
        KeyPair keyPair = ECCUtil.generateKey(CryptoUtil.ECCAlgorithm.MEDIUM);
        X509CSRReq x509CSRReq = new X509CSRReq();
        x509CSRReq.setHexPublicKey(ECCUtil.generateHexString(keyPair.getPublic().getEncoded()));
        x509CSRReq.setCn("www.common.com");
        x509CSRReq.setCountryCode("KR");
        x509CSRReq.setOrganization("Common Company");
        x509CSRReq.setOrganizationUtit("Development");
        x509CSRReq.setStartDate(DateUtil.getBeforeOrAfterDateOfMinutes(0, false));
        x509CSRReq.setExpiryDate(DateUtil.getBeforeOrAfterDateOfHours(24 * 365 * 10, false));
    }
}
