#!/bin/bash
namespace=PROJECT_NAME-EMPLOYEE_NUMBER
pod=code-server-IMAGE_NAME_REPLACED-CATALOG_INFO_IDX-EMPLOYEE_NUMBER
result="proceed"

while [ $result = "proceed" ]
do
	kubectl get pods -n $namespace | grep $pod >> pods.txt
	#모든 Pod의 갯수 (Terminating 포함)
	podCnt=$(cat pods.txt | wc -l)
	SET=$(seq 1 $podCnt)

	# ImagePullBackOff, ErrImagePull, CrashLoopBackOff 발생 시 fail 처리
	for i in $SET
	do
		line=`sed -n "$i"p pods.txt`
		if [[ $line =~ "ImagePullBackOff" || $line =~ "ErrImagePull" || $line =~ "CrashLoopBackOff" || $line =~ "RunContainerError" ]]; then
			result="fail || $line"
		fi
	done

    # updated : 'kubectl get deploy -n $namespace' 명령의 결과 중, "UP-TO-DATE" 필드 값 (현재 업데이트된 파드)
	# available : 'kubectl get deploy -n $namespace' 명령의 결과 중, "AVAILABLE" 필드 값 (가용 파드)
	deploy=`kubectl get deploy -n $namespace | grep $pod`
	updated=`echo $deploy | cut -d ' ' -f3`
	available=`echo $deploy | cut -d ' ' -f4`

    # updated = available 조건 만족 시, 배포 성공
	if [[ updated -eq  available ]]; then
		result="success"
	fi
    # results=($result $message)

	rm pods.txt
	sleep 5
done
# echo ${results[@]}
echo $result