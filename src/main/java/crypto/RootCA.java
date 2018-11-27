package crypto;

import lombok.Data;

import java.security.cert.X509Certificate;

@Data
public class RootCA extends X509CSRReq{
    private X509Certificate rootCert;
    private String rootCertHex;
    private String rootHexPrivKey;
}
