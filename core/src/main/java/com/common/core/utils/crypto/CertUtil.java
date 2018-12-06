package com.common.core.utils.crypto;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.io.output.ByteArrayOutputStream;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by alex.
 * Date: 2018-12-06
 */
public class CertUtil {
    public static String generateCert(String certPem) {
//        openssl x509 -in acs.qacafe.com.pem -text
        try {
            String tempPemPath = "/tmp/tempPem";
            Files.write(Paths.get(tempPemPath), certPem.getBytes());

            DefaultExecutor executor = new DefaultExecutor();
            CommandLine cmdLine = CommandLine.parse("openssl");
            cmdLine.addArgument("x509");
            cmdLine.addArgument("-in");
            cmdLine.addArgument(tempPemPath);
            cmdLine.addArgument("-text");

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PumpStreamHandler streamHandler = new PumpStreamHandler(baos);
            executor.setStreamHandler(streamHandler);

            executor.execute(cmdLine);
            return baos.toString("UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
