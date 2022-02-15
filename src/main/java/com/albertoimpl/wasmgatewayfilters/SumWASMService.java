package com.albertoimpl.wasmgatewayfilters;

import io.github.kawamuray.wasmtime.Func;
import io.github.kawamuray.wasmtime.Instance;
import io.github.kawamuray.wasmtime.Module;
import io.github.kawamuray.wasmtime.Store;
import io.github.kawamuray.wasmtime.WasmFunctions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;

import static io.github.kawamuray.wasmtime.WasmValType.I32;
import static java.util.Collections.emptyList;

@Service
class SumWASMService {

    @Value("classpath:sum.wasm")
    private Resource resourceFile;

    public Integer customSum(Integer x, Integer y) {
        try {
            Store<Void> store = Store.withoutData();
            Path wasmPath = resourceFile.getFile().toPath();
            byte[] wasmBytes = Files.readAllBytes(wasmPath);
            Module module = Module.fromBinary(store.engine(), wasmBytes);
            Instance instance = new Instance(store, module, emptyList());

            Func sumFunction = instance.getFunc(store, "sum").get();
            WasmFunctions.Function2<Integer, Integer, Integer> sum =
                    WasmFunctions.func(store, sumFunction, I32, I32, I32);
            return sum.call(x, y);
        } catch (Exception e) {
            return 0;
        }
    }
}
