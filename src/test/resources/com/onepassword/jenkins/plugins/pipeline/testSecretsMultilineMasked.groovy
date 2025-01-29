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
            echo $MULTILINE_SECRET
            echo "not sensitive data"
        '''
    }
}
