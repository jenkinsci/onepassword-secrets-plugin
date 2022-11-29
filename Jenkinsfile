/*
 See the documentation for more options:
 https://github.com/jenkins-infra/pipeline-library/
*/
buildPlugin(useContainerAgent: true, configurations: [
  // Test the common case (i.e., a recent LTS release) on both Linux and Windows.
  [ platform: 'linux', jdk: '11', jenkins: '2.361.1' ],
  [ platform: 'windows', jdk: '11', jenkins: '2.361.1' ],
])
