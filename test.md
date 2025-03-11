## Run a simple build and test

To build, simply run

```
gradle build
```

Before you run tests, first start a local net. Build https://github.com/harmony-one/harmony and run

```
make debug
```

This starts a local net with 2 shards and 10 validators, with faucets and genesis accounts pre-funded. 

Then, you need to transfer some funds from genesis accounts to test accounts used in this project

To transfer, build https://github.com/harmony-one/go-sdk, run

```
./hmy keys import-private-key 1f84c95ac16e6a50f08d44c7bde7aff8742212fda6e4321fde48bf83bef266dc
./hmy --node=http://127.0.0.1:9500 transfer --from one155jp2y76nazx8uw5sa94fr0m4s5aj8e5xm6fu3 --to one1pdv9lrdwl0rg5vglh4xtyrv3wjk3wsqket7zxy --from-shard 0 --to-shard 0 --amount 123
```

This funds test account `one1pdv9lrdwl0rg5vglh4xtyrv3wjk3wsqket7zxy` with 123 ONE in the local net you just started. Now, to run tests, simply run

```
gradle test
```

All tests should succeed under this setup

## Tips on using Harmony Docker image for running nodes

Please make sure you turned on "EXPOSEAPIS" flag in the script (`deploy.sh`) for running local net, otherwise the node might not be accessible from host machine, since in some operating systems (such as macOS) the node binds to 127.0.0.1 (inaccessible to host) instead of 0.0.0.0 (accessible to all networks including the host)

The flag is located at: https://github.com/harmony-one/harmony/blob/afe93578992bd9c3f63dd103768841ab709377cb/test/deploy.sh#L184