pipeline {
  agent {
    dockerfile {
      filename 'Dockerfile'
      args '--net host -u 0:0 -v /var/run/docker.sock:/var/run/docker.sock'
    }

  }
  stages {
    stage('Chef') {
      steps {
        parallel(
          "Chef Cookbook Unit": {
            chef_cookbook_unit()

          },
          "Chef Cookbook Lint": {
            chef_cookbook_foodcritic()
            chef_cookbook_cookstyle()

          },
          "Chef Cookbook Functional": {
              chef_cookbook_functional()
          }
        )
      }
      post {
        always {
          warnings(canComputeNew: false, canResolveRelativePaths: false, categoriesPattern: '', consoleParsers: [[parserName: 'ChefCookbookLint']], defaultEncoding: '', excludePattern: '', healthy: '', includePattern: '', messagesPattern: '', unHealthy: '')
          junit '*_junit.xml'
          archive '*_junit.xml'

        }

      }
    }
    stage('Publish to Chef Server') {
      when {
          branch 'master'
      }
      steps {
          chef_cookbook_publish_chef()
      }
    }
  }
}
