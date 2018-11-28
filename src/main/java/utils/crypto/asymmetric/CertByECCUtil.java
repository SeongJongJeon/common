package utils.crypto.asymmetric;

import crypto.RootCA;
import crypto.X509CSRReq;
import crypto.X509CSRRes;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.bouncycastle.asn1.*;
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

@Slf4j
public class CertByECCUtil {
    /**
     * 인증서의 Hex String 값을 X509Certificate로 변환
     *
     * @param certVal
     * @return
     */
    public static X509Certificate generateHexStrToCert(String certVal, boolean isPem) {
        X509Certificate cert = null;
        try {
            byte[] decoded = null;
            if (isPem) {
                BASE64Decoder decoder = new BASE64Decoder();
                decoded = decoder.decodeBuffer(
                        certVal.replaceAll(X509Factory.BEGIN_CERT, "")
                                .replaceAll(X509Factory.END_CERT, "")
                );
            } else {
                decoded = Hex.decodeHex(certVal.toCharArray());
            }

            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            cert = (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(decoded));
        } catch (Exception e) {
            log.error(e.getMessage());
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
            cert.verify(ECDSAUtil.generatePubStringToPubKey(hexPublicKey), provider);
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }

        return true;
    }

    /**
     * 사용자 인증서 및 Root 인증서 유효성 검사
     *
     * @param rootCA
     * @param clientCert
     * @param isPem
     * @return
     */
    public static boolean validateUserCert(RootCA rootCA, String clientCert, boolean isPem) {
        try {
            //클라이언트 인증서의 Root 인증서의 보관소의 정보를 넣은 후 보관소에서 Root 인증서를 조회하여 PubKey 추출
            //현재 메소드는 RootCA를 입력받음
//            X509Certificate certificate = generateHexStrToCert(clientCert, isPem);
//            JcaX509CertificateHolder certificateHolder = new JcaX509CertificateHolder(certificate);
//            String rootCertTxIdInUserCert = certificateHolder.getIssuer().getRDNs(BCStyle.UID)[0].getFirst().getValue().toString();
//            String rootCertPubKey = extractPublicKeyByCert(rootCertHex, false);
            if (!checkValidity(clientCert, rootCA.getHexPublicKey(), isPem)) {
                return false;
            }
            return checkValidity(rootCA.getRootCertHex(), rootCA.getHexPublicKey(), false);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return false;
    }

    /**
     * X509 인증서에서 public key 추출
     *
     * @param certVal
     * @param isPem
     * @return
     */
    public static String extractPublicKeyByCert(String certVal, boolean isPem) {
        String pubKey = null;
        try {
            X509Certificate cert = generateHexStrToCert(certVal, isPem);
            SubjectPublicKeyInfo pubKeyInfo = new JcaX509CertificateHolder(cert).getSubjectPublicKeyInfo();
            pubKey = Hex.encodeHexString(pubKeyInfo.getEncoded());
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return pubKey;
    }

    /**
     * X509 인증서에서 CRL(Certificate Revocation List) 추출
     *
     * @param certVal
     * @param isPem
     * @return
     */
    public static String extractCertificateRevocationList(String certVal, boolean isPem) {
        String crlTx = null;
        try {
            X509Certificate cert = generateHexStrToCert(certVal, isPem);

            byte[] crlDistributionPointDerEncodedArray = cert.getExtensionValue(Extension.cRLDistributionPoints.getId());
            ASN1InputStream oAsnInStream = new ASN1InputStream(new ByteArrayInputStream(crlDistributionPointDerEncodedArray));
            ASN1Primitive derObjCrlDP = oAsnInStream.readObject();
            DEROctetString dosCrlDP = (DEROctetString) derObjCrlDP;
            oAsnInStream.close();

            byte[] crldpExtOctets = dosCrlDP.getOctets();
            ASN1InputStream oAsnInStream2 = new ASN1InputStream(new ByteArrayInputStream(crldpExtOctets));
            ASN1Primitive derObj2 = oAsnInStream2.readObject();
            CRLDistPoint distPoint = CRLDistPoint.getInstance(derObj2);
            oAsnInStream2.close();

            for (DistributionPoint dp : distPoint.getDistributionPoints()) {
                DERSequence seq = (DERSequence) dp.getDistributionPoint().getName().toASN1Primitive();
                GeneralName gn = (GeneralName) seq.getObjectAt(0);
                crlTx = gn.getName().toString();
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return crlTx;
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
            log.error(e.getMessage());
            return null;
        }
        permString += X509Factory.END_CERT;

        return permString;
    }

    /**
     * X.509 V3 인증서 생성
     *
     * @param rootCA
     * @param isMakeRootCA
     * @param clientCSR
     * @return
     */
    public static X509CSRRes generateX509V3(RootCA rootCA, boolean isMakeRootCA, X509CSRReq clientCSR) {
        X509CSRRes x509CSRRes = null;
        X500Name rootX500Name = null;

        //Root CA 인증서 유효성 검사
        if (!isMakeRootCA) {
            if (rootCA == null) {
                return null;
            }
            if (!checkValidity(rootCA.getRootCertHex(), rootCA.getHexPublicKey(), false)) {
                return null;
            }
        }

        try {
            if (!isMakeRootCA) {
                rootX500Name = new JcaX509CertificateHolder(rootCA.getRootCert()).getIssuer();
            }
            //발급자 정보 (Root CA)
            X500NameBuilder issuerNameBuilder = new X500NameBuilder(BCStyle.INSTANCE);
            issuerNameBuilder.addRDN(BCStyle.CN, isMakeRootCA ? clientCSR.getCn() : rootX500Name.getRDNs(BCStyle.CN)[0].getFirst().getValue().toString());
            issuerNameBuilder.addRDN(BCStyle.C, isMakeRootCA ? clientCSR.getCountryCode() : rootX500Name.getRDNs(BCStyle.C)[0].getFirst().getValue().toString());
            issuerNameBuilder.addRDN(BCStyle.O, isMakeRootCA ? clientCSR.getOrganization() : rootX500Name.getRDNs(BCStyle.O)[0].getFirst().getValue().toString());
            issuerNameBuilder.addRDN(BCStyle.OU, isMakeRootCA ? clientCSR.getOrganizationUtit() : rootX500Name.getRDNs(BCStyle.OU)[0].getFirst().getValue().toString());
            //Root 인증서 조회정보. (블록체인의 경우는 Tx Hash 값)
            if (isMakeRootCA) {
//                issuerNameBuilder.addRDN(BCStyle.UID, x509CustomerCert.getRootCaTxId());
            }

            //클라이언트 정보
            X500NameBuilder subjectNameBuilder = new X500NameBuilder(BCStyle.INSTANCE);
            subjectNameBuilder.addRDN(BCStyle.CN, clientCSR.getCn());
            subjectNameBuilder.addRDN(BCStyle.C, clientCSR.getCountryCode());
            subjectNameBuilder.addRDN(BCStyle.O, clientCSR.getOrganization());
            subjectNameBuilder.addRDN(BCStyle.OU, clientCSR.getOrganizationUtit());
            PublicKey publicKey = ECDSAUtil.generatePubStringToPubKey(clientCSR.getHexPublicKey());

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
                //CRL(Certificate Revocation List) 인증서 폐기여부 확인 가능한 도메인을 입력한 후에 인증 요청이 오면 해당 서버를 통하여 폐기여부 검증.(블록체인에서는 Smart Contract 주소입력)
                GeneralName gn = new GeneralName(GeneralName.dNSName, "Certificate Revocation List");
                GeneralNames gns = new GeneralNames(gn);
                DistributionPointName dpn = new DistributionPointName(gns);

                DistributionPoint[] distPoints = new DistributionPoint[]{new DistributionPoint(dpn, null, null)};
                certBuilder.addExtension(Extension.cRLDistributionPoints, false, new CRLDistPoint(distPoints));
            }

            //Signature, Self Signed Certificate(SSC) : ROOT CA는 서명해줄 인증기관이 없으므로 본인의 개인키로 서명함.
            Provider provider = new BouncyCastleProvider();
            ContentSigner sigGen = new JcaContentSignerBuilder(clientCSR.getEccSigAlgorithm().toValue())
                    .setProvider(provider).build(ECDSAUtil.generatePrivateStringToPrivateKey(rootCA.getRootHexPrivKey()));

            X509Certificate cert = new JcaX509CertificateConverter().setProvider(provider)
                    .getCertificate(certBuilder.build(sigGen));
            cert.checkValidity(new Date());

            //Root CA의 publicKey로 검증
            cert.verify(ECDSAUtil.generatePubStringToPubKey(rootCA.getHexPublicKey()));

            x509CSRRes = new X509CSRRes();
            x509CSRRes.setHex(Hex.encodeHexString(cert.getEncoded()));
            x509CSRRes.setPem(generatePem(cert));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return x509CSRRes;
    }
}
