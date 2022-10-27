def environment = [
    'OP_CONNECT_HOST=http://localhost:8081',
    'OP_CONNECT_TOKEN=connect-credential-id'
]

def config = [
    connectHost: "OP_HOST",
]

node {
    sh 'curl -sSfLo op.zip OP_CLI_URL && unzip -o op.zip && rm op.zip'
    withEnv(environment) {
        withSecrets(config: config, secrets: [[envVar: 'DOCKER_USERNAME', secretRef: 'op://acceptance-tests/test-secret/password']]) {
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
