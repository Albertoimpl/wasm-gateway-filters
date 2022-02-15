# Spring Cloud Gateway and WebAssembly

Creating a custom Spring Cloud Gateway filter that calls a WebAssembly function written in C from a .wasm file using [wasmtime-java](https://wasmtime.dev/).

To run the project:
```shell
./gradlew bootRun
```

To call the function:
```shell
curl localhost:8081/wasm -H"X-CustomSum-x:10" -H"X-CustomSum-y:16" -sI | grep X-CustomSum

 
X-CustomSum: 26
```

This request will call a custom filter that will sum two digits and add it to the response headers:
```yaml
spring:
  cloud:
    gateway:
      routes:
        - uri: https://albertoimpl.com
          predicates:
            - Path=/wasm
          filters:
            - StripPrefix=1
            - CustomSumWASMFilter
```

To compile the sum function:

```shell
git clone https://github.com/emscripten-core/emsdk.git
cd emsdk
./emsdk install latest
./emsdk activate latest
source ./emsdk_env.sh
```

```shell
emcc sum/sum.c -o sum/sum.js -s EXPORTED_FUNCTIONS='["_sum"]'
```

```shell
cp sum/sum.wasm src/main/resources/
```
