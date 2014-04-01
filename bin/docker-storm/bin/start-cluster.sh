#! /bin/bash

set -e

. config.sh

if sudo docker ps | grep "storm" >/dev/null; then
  echo ""
  echo "It looks like you already have some containers running."
  echo "Please take them down before attempting to bring up another"
  echo "cluster with the following command:"
  echo ""
  echo "  make stop-cluster"
  echo ""

  exit 1
fi

# Start up Zookeeper host(s)
for index in $(seq ${NUM_ZK_NODES}); do
  CONTAINER_HOST=storm-zk${index}
  CONTAINER_IP=${IP_PREFIX}$((${ZK_IP_OFFSET} + ${index}))
  CONTAINER_ID=$(sudo docker run -d \
    -h "${CONTAINER_HOST}" \
    "paulczar/zookeeper-3.4.5")

  sleep 1

  sudo ./bin/pipework br1 ${CONTAINER_ID} "${CONTAINER_IP}/24"

  echo "Started [${CONTAINER_HOST}] and assigned it the IP [${CONTAINER_IP}]"

  if [ "$index" -eq "1" ] ; then
    sudo ifconfig br1 ${IP_PREFIX}254

    sleep 1
  fi
done

# Start up Storm host(s)
for index in $(seq ${NUM_STORM_NODES}); do
  CONTAINER_HOST=storm${index}
  CONTAINER_IP=${IP_PREFIX}$((${STORM_IP_OFFSET} + ${index}))
  CONTAINER_ID=$(sudo docker run -d -i \
    -h "${CONTAINER_HOST}" \
    -e "STORM_NODE_NAME=${CONTAINER_IP}" \
    -t "storm")

  sleep 1

  sudo ./bin/pipework br1 ${CONTAINER_ID} "${CONTAINER_IP}/24"

  echo "Started [${CONTAINER_HOST}] and assigned it the IP [${CONTAINER_IP}]"

  # First node will be the Nimbus
  if [ "$index" -eq "1" ] ; then
    sudo ifconfig br1 ${IP_PREFIX}254

    sleep 1
  fi

  until curl -s "http://${CONTAINER_IP}:8098/ping" | grep "OK" >/dev/null;
  do
    sleep 1
  done

  if [ "$index" -gt "1" ] ; then
    echo "Requesting that [storm${index}] join the cluster.."

    sshpass -p "basho" \
      ssh -o "StrictHostKeyChecking no" -o "UserKnownHostsFile /dev/null" -o "LogLevel quiet" root@33.33.33.${index}0 \
        riak-admin cluster join riak@33.33.33.10
  fi
done

sleep 1

sshpass -p "basho" \
  ssh -o "StrictHostKeyChecking no" -o "UserKnownHostsFile /dev/null" -o "LogLevel quiet" root@33.33.33.10 \
    riak-admin cluster plan

read -p "Commit these cluster changes? (y/n): " RESP
if [[ $RESP =~ ^[Yy]$ ]] ; then
  sshpass -p "basho" \
    ssh -o "StrictHostKeyChecking no" -o "UserKnownHostsFile /dev/null" -o "LogLevel quiet" root@33.33.33.10 \
      riak-admin cluster commit
else
  sshpass -p "basho" \
    ssh -o "StrictHostKeyChecking no" -o "UserKnownHostsFile /dev/null" -o "LogLevel quiet" root@33.33.33.10 \
      riak-admin cluster clear
fi
