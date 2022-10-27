def config = [
    connectHost: "OP_HOST",
    connectCredentialId: "connect-credential-id"
]

def secrets = [
    [envVar: 'DOCKER_USERNAME', secretRef: 'op://acceptance-tests/test-secret/password'],
    [envVar: 'FOO_BAR', secretRef: 'op://acceptance-tests/test-secret/test-section/password']
]

node {
    sh 'curl -sSfLo op.zip OP_CLI_URL && unzip -o op.zip && rm op.zip'
    withSecrets(config: config, secrets: secrets) {
        sh '''
            if [ "$DOCKER_USERNAME" = "RGVhciBzZWN1cml0eSByZXNlYXJjaGVyLCB0aGlzIGlzIGp1c3QgYSBkdW1teSBzZWNyZXQuIFBsZWFzZSBkb24ndCByZXBvcnQgaXQu" ]; then
                echo "Strings are equal."
            else
                echo "Strings are not equal."
            fi
            if [ "$FOO_BAR" = "RGVhciBzZWN1cml0eSByZXNlYXJjaGVyLCB0aGlzIGlzIGp1c3QgYSBkdW1teSBzZWNyZXQuIFBsZWFzZSBkb24ndCByZXBvcnQgaXQu" ]; then
                echo "Strings are equal."
            else
                echo "Strings are not equal."
            fi
        '''
    }
}
