package utils.crypto;

import crypto.RootCA;
import crypto.X509CSRReq;
import crypto.X509CSRRes;
import org.apache.commons.codec.binary.Hex;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import sun.security.provider.X509Factory;

import java.io.ByteArrayInputStream;
import java.math.BigInteger;
import java.security.Provider;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.util.Date;

public class CertByECCUtil {
    /**
     * 인증서의 Hex String 값을 X509Certificate로 변환
     *
     * @param hexCert
     * @return
     */
    public static X509Certificate generateHexStrToCert(String hexCert, boolean isPem) {
        X509Certificate cert = null;
        try {
            byte[] decoded = null;
            if (isPem) {
                BASE64Decoder decoder = new BASE64Decoder();
                decoded = decoder.decodeBuffer(
                        hexCert.replaceAll(X509Factory.BEGIN_CERT, "")
                                .replaceAll(X509Factory.END_CERT, "")
                );
            } else {
                decoded = Hex.decodeHex(hexCert.toCharArray());
            }

            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            cert = (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(decoded));
        } catch (Exception e) {
            return null;
        }

        return cert;
    }

    /**
     * X509 인증서 유효성 체크
     *
     * @param certVal
     * @param isPem
     * @return
     */
    public static boolean checkValidity(String certVal, String hexPublicKey, boolean isPem) {
        try {
            X509Certificate cert = generateHexStrToCert(certVal, isPem);
            Provider provider = new BouncyCastleProvider();

            cert.checkValidity(new Date());
            cert.verify(ECCUtil.generatePubStringToPubKey(hexPublicKey), provider);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * PEM 형식의 값으로 변환
     *
     * @param cert
     * @return
     */
    public static String generatePem(X509Certificate cert) {
        BASE64Encoder encoder = new BASE64Encoder();

        String permString = X509Factory.BEGIN_CERT + "\n";
        try {
            permString += encoder.encode(cert.getEncoded()) + "\n";
        } catch (Exception e) {
            return null;
        }
        permString += X509Factory.END_CERT;

        return permString;
    }

    public X509CSRRes generateX509V3(RootCA rootCA, boolean isMakeRootCA, X509CSRReq clientCSR) {
        X509CSRRes x509CSRRes = null;

        //Root CA 인증서 유효성 검사
        if (!isMakeRootCA) {
            if (!checkValidity(rootCA.getRootCertHex(), rootCA.getHexPublicKey(), false)) {
                return null;
            }
        }

        try {
            X500Name rootX500Name = new JcaX509CertificateHolder(rootCA.getRootCert()).getIssuer();
            //발급자 정보 (Root CA)
            X500NameBuilder issuerNameBuilder = new X500NameBuilder(BCStyle.INSTANCE);
            issuerNameBuilder.addRDN(BCStyle.CN, isMakeRootCA ? clientCSR.getCn() : rootX500Name.getRDNs(BCStyle.CN)[0].getFirst().getValue().toString());
            issuerNameBuilder.addRDN(BCStyle.C, isMakeRootCA ? clientCSR.getCountryCode() : rootX500Name.getRDNs(BCStyle.C)[0].getFirst().getValue().toString());
            issuerNameBuilder.addRDN(BCStyle.O, isMakeRootCA ? clientCSR.getOrganization() : rootX500Name.getRDNs(BCStyle.O)[0].getFirst().getValue().toString());
            issuerNameBuilder.addRDN(BCStyle.OU, isMakeRootCA ? clientCSR.getOrganizationUtit() : rootX500Name.getRDNs(BCStyle.OU)[0].getFirst().getValue().toString());
            //Root 인증서의 로그인 정보. (블록체인의 경우는 Tx Hash 값)
            if (isMakeRootCA) {
//                issuerNameBuilder.addRDN(BCStyle.UID, x509CustomerCert.getRootCaTxId());
            }

            //클라이언트 정보
            X500NameBuilder subjectNameBuilder = new X500NameBuilder(BCStyle.INSTANCE);
            subjectNameBuilder.addRDN(BCStyle.CN, clientCSR.getCn());
            subjectNameBuilder.addRDN(BCStyle.C, clientCSR.getCountryCode());
            subjectNameBuilder.addRDN(BCStyle.O, clientCSR.getOrganization());
            subjectNameBuilder.addRDN(BCStyle.OU, clientCSR.getOrganizationUtit());
            PublicKey publicKey = ECCUtil.generatePubStringToPubKey(clientCSR.getHexPublicKey());

            X509v3CertificateBuilder certBuilder = new JcaX509v3CertificateBuilder(
                    issuerNameBuilder.build(),
                    new BigInteger(String.valueOf(LocalDateTime.now().getNano())),  //Unique number
                    clientCSR.getStartDate(),
                    clientCSR.getExpiryDate(),
                    subjectNameBuilder.build(),
                    publicKey
            );

            //인증서 생성
            if (isMakeRootCA) {
                //이 인증서가 다른 인증서를 발급할 수 있는지 여부.
                certBuilder.addExtension(Extension.basicConstraints, false, new BasicConstraints(true));
                //인증서가 기입된 공개키가 사용되는 보안 서비스의 종결정 (서명, 부인방지, 전자서명, 키교환)
                certBuilder.addExtension(Extension.keyUsage, false, new KeyUsage(KeyUsage.keyCertSign));

                ASN1EncodableVector purposes = new ASN1EncodableVector();
                purposes.add(KeyPurposeId.id_kp_clientAuth);
                purposes.add(KeyPurposeId.anyExtendedKeyUsage);
                certBuilder.addExtension(Extension.extendedKeyUsage, false, new DERSequence(purposes));
            } else {
                //crlIssuer=CRL SmartContract address (인증서 폐기시 요청할 DNS - 블록체인에서는 SmartContract)
                GeneralName gn = new GeneralName(GeneralName.dNSName, "");
                GeneralNames gns = new GeneralNames(gn);
                DistributionPointName dpn = new DistributionPointName(gns);

                DistributionPoint[] distPoints = new DistributionPoint[]{new DistributionPoint(dpn, null, null)};
                certBuilder.addExtension(Extension.cRLDistributionPoints, false, new CRLDistPoint(distPoints));
            }

            //Signature, Self Signed Certificate(SSC) : ROOT CA는 서명해줄 인증기관이 없으므로 본인의 개인키로 서명함.
            Provider provider = new BouncyCastleProvider();
            ContentSigner sigGen = new JcaContentSignerBuilder(clientCSR.getEccSigAlgorithm().toValue())
                    .setProvider(provider).build(ECCUtil.generatePrivateStringToPrivateKey(rootCA.getRootHexPrivKey()));

            X509Certificate cert = new JcaX509CertificateConverter().setProvider(provider)
                    .getCertificate(certBuilder.build(sigGen));
            cert.checkValidity(new Date());

            //Root CA의 publicKey로 검증
            cert.verify(ECCUtil.generatePubStringToPubKey(clientCSR.getHexPublicKey()));

            x509CSRRes = new X509CSRRes();
            x509CSRRes.setHex(Hex.encodeHexString(cert.getEncoded()));
            x509CSRRes.setPem(generatePem(cert));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return x509CSRRes;
    }
}
