/*
 See the documentation for more options:
 https://github.com/jenkins-infra/pipeline-library/
*/
buildPlugin(useContainerAgent: true, configurations: [
  // Test the baseline on both Linux and Windows with Java 21, and the newest supported Java on Linux.
  [ platform: 'linux', jdk: '21' ],
  [ platform: 'windows', jdk: '21' ],
  [ platform: 'linux', jdk: '25' ],
])
