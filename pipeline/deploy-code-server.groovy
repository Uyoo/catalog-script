#!groovy
pipeline {
    agent {
        label 'jenkins-slave'
    }
    stages{
        stage('Initialize the variables') {
            steps {
                script {

                    // Jenkins에서 PARAMS로 전달받은 data
                    params = readJSON text: "${PARAMS}"
                    EMPLOYEE_NUMBER = "${params.EMPLOYEE_NUMBER}"
                    USER_TOKEN = "${params.USER_TOKEN}"
                    PROJECT_NAME = "${params.PROJECT_NAME}"
                    CLUSTER_NAME = "${params.CLUSTER_NAME}"
                    IMAGE_NAME = "${params.IMAGE_NAME}"
                    REQUIRED_MEMORY = "${params.REQUIRED_MEMORY}"
                    REQUIRED_CPU = "${params.REQUIRED_CPU}"
                    REQUIRED_PV = "${params.REQUIRED_PV}"
                    CATALOG_INFO_IDX = "${params.CATALOG_INFO_IDX}"

                    // Jenkins의 전역변수
                    CATALOG_URL = "${CATALOG_URL}"
                    CATALOG_URL_EXT = "${CATALOG_URL_EXT}"
                    IMAGE_NGINX = "${IMAGE_NGINX}"

                    sh "echo 'params => ${params}'"
                    sh "echo 'EMPLOYEE_NUMBER => ${EMPLOYEE_NUMBER}'"
                    sh "echo 'USER_TOKEN => ${USER_TOKEN}'"
                    sh "echo 'PROJECT_NAME => ${PROJECT_NAME}'"
                    sh "echo 'CLUSTER_NAME => ${CLUSTER_NAME}'"
                    sh "echo 'IMAGE_NAME => ${IMAGE_NAME}'"                
                    sh "echo 'REQUIRED_MEMORY => ${REQUIRED_MEMORY}'"
                    sh "echo 'REQUIRED_CPU => ${REQUIRED_CPU}'"
                    sh "echo 'REQUIRED_PV => ${REQUIRED_PV}'"
                    sh "echo 'CATALOG_INFO_IDX => ${CATALOG_INFO_IDX}'"
                    sh "echo 'PORT_INFOS => ${params.PORT_INFOS}'"

                    sh "echo 'CATALOG_URL => ${CATALOG_URL}'"
                    sh "echo 'CATALOG_URL_EXT => ${CATALOG_URL_EXT}'"
                    sh "echo 'IMAGE_NGINX => ${IMAGE_NGINX}'"

                    portInfos = writeJSON returnText: true, json: params.PORT_INFOS
                    sh "echo 'portInfos => ${portInfos}'"
                }
            }
        }
        
        stage('Create Namespace') {
            steps{
                script{
                    // secret.sh
                    create_secret="/home/jenkins/agent/workspace/deploy-code-server/create-secret.sh"
                    withCredentials([string(credentialsId: 'bitbucket_token', variable: 'TOKEN')]) {
                        sh "curl -H 'Authorization: Bearer ${TOKEN}' '<create-secret bitbucket url>'"
                        sh "cat ${create_secret}"
                    }
                    
                    // namespace
                    withCredentials([string(credentialsId: 'catalog_portal_token', variable: 'TOKEN')]) {
                        sh "curl -H 'X-AUTH-TOKEN: ${TOKEN}' '${CATALOG_URL}/api_v1/kubernetes/code-server/namespace/${CATALOG_INFO_IDX}.yml' | kubectl apply -f -"
                    }

                    sh "sed -i s/NAMESPACE/${PROJECT_NAME}-${EMPLOYEE_NUMBER}/g ${create_secret}"
                    sh "chmod 755 ${create_secret}"

                    // nexus를 사용한다면 create-secret.sh 내용 수정 후 주석 해제
                    try {
                        sh "sh ${create_secret}"
                    } catch (err) {}
                }
            }
        }

        stage('Deploy Container') {
            steps{
                script {
                    withCredentials([string(credentialsId: 'catalog_portal_token', variable: 'TOKEN')]) {
                        sh "curl -H 'X-AUTH-TOKEN: ${TOKEN}' '${CATALOG_URL}/api_v1/kubernetes/code-server/namespace/${CATALOG_INFO_IDX}.yml' | kubectl apply -f -"

                        // RBAC
                        sh "curl -H 'X-AUTH-TOKEN: ${TOKEN}' '${CATALOG_URL}/api_v1/kubernetes/code-server/service-account/${CATALOG_INFO_IDX}.yml' | kubectl apply -f -"
                        sh "curl -H 'X-AUTH-TOKEN: ${TOKEN}' '${CATALOG_URL}/api_v1/kubernetes/code-server/cluster-role-bind/${CATALOG_INFO_IDX}.yml' | kubectl apply -f -"
                        sh "curl -H 'X-AUTH-TOKEN: ${TOKEN}' '${CATALOG_URL}/api_v1/kubernetes/code-server/cluster-role-bind2/${CATALOG_INFO_IDX}.yml' | kubectl apply -f -"

                        // pvc, deployment, service, ingress
                        sh "curl -H 'X-AUTH-TOKEN: ${TOKEN}' '${CATALOG_URL}/api_v1/kubernetes/code-server/pvc/${CATALOG_INFO_IDX}.yml' | kubectl apply -f -"
                        sh "curl -H 'X-AUTH-TOKEN: ${TOKEN}' '${CATALOG_URL}/api_v1/kubernetes/code-server/deployment/${CATALOG_INFO_IDX}?userToken=${USER_TOKEN}&imageNginx=${IMAGE_NGINX}&catalogUrl=${CATALOG_URL}.yml' | kubectl apply -f -"
                        sh "curl -H 'X-AUTH-TOKEN: ${TOKEN}' '${CATALOG_URL}/api_v1/kubernetes/code-server/service/${CATALOG_INFO_IDX}.yml' | kubectl apply -f -"
                        sh "curl -H 'X-AUTH-TOKEN: ${TOKEN}' '${CATALOG_URL}/api_v1/kubernetes/code-server/ingress/${CATALOG_INFO_IDX}.yml' | kubectl apply -f -"

                    }
                }
            }
        }

        stage('Container Health-Check') {
            // options {
            //     timeout(time: 300, unit: "SECONDS")
            // }
            steps{
                script {
                    health_check="/home/jenkins/agent/workspace/deploy-code-server/health-check.sh"

                    withCredentials([string(credentialsId: 'bitbucket_token', variable: 'TOKEN')]) {
                        sh "echo 'bitbucket token: ${TOKEN}'"
                        sh "curl -H 'Authorization: Bearer ${TOKEN}' '<healthcheck.sh bitbucket url>'"
                        
                        // replace values from healthcheck.sh
                        IMAGE_NAME_REPLACED = IMAGE_NAME.replace(":", "-")
                        IMAGE_NAME_REPLACED = IMAGE_NAME_REPLACED.replace(".", "-")
                        sh "sed -i 's/PROJECT_NAME/${PROJECT_NAME}/g' ${health_check}"
                        sh "sed -i 's/EMPLOYEE_NUMBER/${EMPLOYEE_NUMBER}/g' ${health_check}"
                        sh "sed -i 's/IMAGE_NAME_REPLACED/${IMAGE_NAME_REPLACED}/g' ${health_check}"
                        sh "sed -i 's/CATALOG_INFO_IDX/${CATALOG_INFO_IDX}/g' ${health_check}"

                        sh "chmod 755 ${health_check}"

                        def output = sh (script: "${health_check}", returnStdout: true).trim()
                        DEPLOY_MESSAGE = output
                        sh "echo 'DEPLOY_MESSAGE => ${DEPLOY_MESSAGE}'"

                        // fail인 경우 stop
                        if ( output =~ "fail" ) {
                            sh 'exit 1'
                        }
                    }
                }
            }
        }
    }

    post {
        success {
            sh "echo 'success'"
            withCredentials([string(credentialsId: 'catalog_portal_token', variable: 'TOKEN')]) {
                sh "curl -X POST -H 'X-AUTH-TOKEN: ${TOKEN}' -H 'Content-Type: application/json' '${CATALOG_URL}/api_v1/catalog/deployment/external/status' -d '{\"catalogInfoId\": \"${CATALOG_INFO_IDX}\", \"catalogInformationStatus\": \"ACTIVE\", \"message\": \"${DEPLOY_MESSAGE}\"}'"
                // sh "curl -X POST -H 'X-AUTH-TOKEN: ${TOKEN}' -H 'Content-Type: application/json' '${CATALOG_URL}/api_v1/kubernetes/code-server/service' -d '{\"portInfos\": ${portInfos}}'"
            }
        }
        failure {
            // health check 중 ImagePullBackOff, ErrImagePull, CrashLoopBackOff 발생 시 fail
            sh "echo 'failure'"
            withCredentials([string(credentialsId: 'catalog_portal_token', variable: 'TOKEN')]) {
                sh "curl -X POST -H 'X-AUTH-TOKEN: ${TOKEN}' -H 'Content-Type: application/json' '${CATALOG_URL}/api_v1/catalog/deployment/external/status' -d '{\"catalogInfoId\": \"${CATALOG_INFO_IDX}\", \"catalogInformationStatus\": \"FAILURE\", \"message\": \"${DEPLOY_MESSAGE}\"}'"
            }
        }
        aborted {
            // 자원이 없는 경우 aborted
            sh "echo 'aborted'"
            withCredentials([string(credentialsId: 'catalog_portal_token', variable: 'TOKEN')]) {
                sh "curl -X POST -H 'X-AUTH-TOKEN: ${TOKEN}' -H 'Content-Type: application/json' '${CATALOG_URL}/api_v1/catalog/deployment/external/status' -d '{\"catalogInfoId\": \"${CATALOG_INFO_IDX}\", \"catalogInformationStatus\": \"FAILURE\", \"message\": \"Lack of Resources\"}'"
            }
        }
    }
}