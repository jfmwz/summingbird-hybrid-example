#! /bin/bash

IP_PREFIX=33.33.33.

NUM_STORM_NODES=3
STORM_IP_OFFSET=10

NUM_ZK_NODES=1
ZK_IP_OFFSET=0

if [ $((${ZK_IP_OFFSET} + ${NUM_ZK_NODES})) -gt ${STORM_IP_OFFSET} ]; then
    echo ""
    echo "The number of zk nodes plus IP offset cannot be greater than the storm IP offset"
    echo "Be reasonable!"
    echo ""

    exit 1
fi

if [ $((${STORM_IP_OFFSET} + ${NUM_STORM_NODES})) -gt 253 ]; then
    echo ""
    echo "The number of storm nodes plus IP offset cannot be greater than 253."
    echo "Be reasonable!"
    echo ""

    exit 1
fi
