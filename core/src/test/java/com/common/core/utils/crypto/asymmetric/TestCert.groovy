package com.common.core.utils.crypto.asymmetric

import com.common.core.crypto.RootCA
import com.common.core.crypto.X509CSRReq
import com.common.core.crypto.X509CSRRes
import spock.lang.Specification

import java.security.KeyPair

class TestCert extends Specification {
    def "Cert가 정상적으로 실행되는지 테스트"() {
        given:
        KeyPair keyPair = ECDSAUtil.generateKey()
        RootCA rootCA = new RootCA()
        rootCA.setRootHexPrivKey(HexUtil.encodeString(keyPair.getPrivate().getEncoded()))
        rootCA.setHexPublicKey(HexUtil.encodeString(keyPair.getPublic().getEncoded()))

        X509CSRReq x509CSRReq = new X509CSRReq()
        x509CSRReq.setEccSigAlgorithm(CryptoUtil.ECCSigAlgorithm.SHA256)
        x509CSRReq.setHexPublicKey(HexUtil.encodeString(keyPair.getPublic().getEncoded()))
        x509CSRReq.setCn("www.common.com")
        x509CSRReq.setCountryCode("KR")
        x509CSRReq.setOrganization("Common")
        x509CSRReq.setOrganizationUtit("Development")
        x509CSRReq.setStartDate(DateUtil.getBeforeOrAfterDateOfHours(0, false))
        x509CSRReq.setExpiryDate(DateUtil.getBeforeOrAfterDateOfHours(24 * 365 * 10, false))

        when:
        X509CSRRes x509CSRRes = CertByECCUtil.generateX509V3(rootCA, true, x509CSRReq)

        then:
        x509CSRRes == null
    }
}
