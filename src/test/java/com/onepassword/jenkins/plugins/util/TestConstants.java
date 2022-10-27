package com.onepassword.jenkins.plugins.util;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TestConstants {
    public static final String TEST_SUCCESS = "Strings are equal.";
    public static final String TEST_FAILURE = "Strings are not equal.";

    public static final String TEST_CONNECT_HOST = "https://9ace-2001-1c00-b00-600-dc16-4ccd-d24d-4c9d.eu.ngrok.io";
    public static final String OP_CLI_URL = "https://cache.agilebits.com/dist/1P/op2/pkg/v2.7.1-beta.01/op_linux_amd64_v2.7.1-beta.01.zip";
    public static final String TEST_SECRET = "RGVhciBzZWN1cml0eSByZXNlYXJjaGVyLCB0aGlzIGlzIGp1c3QgYSBkdW1teSBzZWNyZXQuIFBsZWFzZSBkb24ndCByZXBvcnQgaXQu";

    public static String readFile(String path, Charset encoding)
            throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }
}
