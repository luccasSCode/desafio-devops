#!/bin/bash
set -euo pipefail

if [[ -S /var/run/docker.sock ]]; then
  sock_gid="$(stat -c '%g' /var/run/docker.sock)"
  if ! getent group "${sock_gid}" >/dev/null; then
    groupadd -g "${sock_gid}" dockerhost
  fi
  usermod -aG "${sock_gid}" jenkins
fi

exec gosu jenkins /usr/local/bin/jenkins.sh "$@"
