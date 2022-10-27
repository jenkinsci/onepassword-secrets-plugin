def environment = [
    'OP_CONNECT_HOST=OP_HOST',
    'OP_CONNECT_TOKEN=connect-credential-id',
    'DOCKER_USERNAME=op://acceptance-tests/test-secret/password',
    'FOO_BAR=op://acceptance-tests/test-secret/test-section/password'
]

node {
    sh 'curl -sSfLo op.zip OP_CLI_URL && unzip -o op.zip && rm op.zip'
    withEnv(environment) {
        withSecrets() {
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
}
