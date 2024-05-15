def config = [
        connectHost: "OP_HOST",
        connectCredentialId: "connect-credential-id"
]

def secrets = [
        [envVar: 'MULTILINE_SECRET', secretRef: 'op://acceptance-tests/multiline-secret/notesPlain'],
]

node {
    sh 'curl -sSfLo op.zip OP_CLI_URL && unzip -o op.zip && rm op.zip'
    withSecrets(config: config, secrets: secrets) {
        sh '''
            if [ "$MULTILINE_SECRET" = "$(cat << EOF
-----BEGIN PRIVATE KEY-----
RGVhciBzZWN1cml0eSByZXNlYXJjaGVyLApXaGls
ZSB3ZSBkZWVwbHkgYXBwcmVjaWF0ZSB5b3VyIHZp
Z2lsYW5jZSBhbmQgZWZmb3J0cyB0byBtYWtlIHRo
ZSB3b3JsZCBtb3JlIHNlY3VyZSwgSSdtIGFmcmFp
ZCBJIG11c3QgdGVsbCB5b3UgdGhhdCB0aGlzIHZh
bHVlIGlzIG5vdCBhIGFjdHVhbCBwcml2YXRlIGtl
eS4gCkl0J3MgYSBqdXN0IGEgZHVtbXkgc2VjcmV0
IHRoYXQgd2UgdXNlIHRvIHRlc3QgdmFyaW91cyAx
UGFzc3dvcmQgc2VjcmV0cyBpbnRlZ3JhdGlvbnMu
IApTbyBwbGVhc2UgZG9uJ3QgcmVwb3J0IGl0IQo=
-----END PRIVATE KEY-----
EOF
)" ]; then
                echo "Strings are equal."
            else
                echo "Strings are not equal."
            fi
        '''
    }
}