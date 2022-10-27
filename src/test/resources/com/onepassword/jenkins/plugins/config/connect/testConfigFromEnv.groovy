def environment = [
    'OP_CONNECT_HOST=OP_HOST',
    'OP_CONNECT_TOKEN=connect-credential-id',
]

def secrets = [
    [envVar: 'DOCKER_USERNAME', secretRef: 'op://acceptance-tests/test-secret/password']
]

node {
    sh 'curl -sSfLo op.zip OP_CLI_URL && unzip -o op.zip && rm op.zip'
    withEnv(environment) {
        withSecrets(secrets: secrets) {
            sh '''
                if [ "$DOCKER_USERNAME" = "RGVhciBzZWN1cml0eSByZXNlYXJjaGVyLCB0aGlzIGlzIGp1c3QgYSBkdW1teSBzZWNyZXQuIFBsZWFzZSBkb24ndCByZXBvcnQgaXQu" ]; then
                    echo "Strings are equal."
                else
                    echo "Strings are not equal."
                fi
            '''
        }
    }
}
