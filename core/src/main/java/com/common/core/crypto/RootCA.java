package com.common.core.crypto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.security.cert.X509Certificate;

@Data
@EqualsAndHashCode(callSuper = false)
public class RootCA extends X509CSRReq {
    private X509Certificate rootCert;
    private String rootCertHex;
    private String rootHexPrivKey;
}
