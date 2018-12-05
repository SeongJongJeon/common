package com.common.core.crypto;

import lombok.Data;
import org.apache.commons.codec.binary.Hex;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import com.common.core.utils.crypto.asymmetric.CertByECCUtil;

import java.security.cert.X509Certificate;

@Data
public class AuthenticationReq {
    private String plainText;
    private String cipherText;
    private String pem;

    public String extractPubKeyByPem() {
        try {
            X509Certificate certificate = CertByECCUtil.generateHexStrToCert(pem, true);
            JcaX509CertificateHolder certificateHolder = new JcaX509CertificateHolder(certificate);
            return Hex.encodeHexString(certificateHolder.getSubjectPublicKeyInfo().getEncoded());
        } catch (Exception e) {

        }
        return null;
    }
}
