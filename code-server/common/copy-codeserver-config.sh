#!/bin/bash
sleep 5

echo "start /code-server-scripts/copy-codeserver-config.sh"

dir_src=/data/coder
dir_dest=/home
cp -r ${dir_src}/ ${dir_dest}/

echo "finish /code-server-scripts/copy-codeserver-config.sh"


# 기존에 복사해둔 복사본 (/data/coder)에서 원본 (/home/coder)로 Copy가 이루어져있다면 copy과정 x
# if [ -e ${dir_dest}/coder/check-copy ]; then
#     echo "check-copy file exist (Already Copied)"
# else
#     echo "copy codeserver-config to origin config path"
#     cp -r ${dir_src}/ ${dir_dest}/
#     echo "finish copy codeserver config files"
# fi
#sleep infinity & wait