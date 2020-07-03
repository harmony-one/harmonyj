#!/usr/bin/env bash

solcjs contracts/Greeter.sol --bin --abi --optimize -o contracts/generated
solcjs contracts/Counter.sol --bin --abi --optimize -o contracts/generated