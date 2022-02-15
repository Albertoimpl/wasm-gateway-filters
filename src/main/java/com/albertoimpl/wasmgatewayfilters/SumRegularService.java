package com.albertoimpl.wasmgatewayfilters;

import org.springframework.stereotype.Service;

@Service
class SumRegularService {

    public Integer customSum(Integer x, Integer y) {
        return x + y;
    }
}
