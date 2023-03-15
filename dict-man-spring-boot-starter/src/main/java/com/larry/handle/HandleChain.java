package com.larry.handle;

import com.larry.service.DictService;
import com.larry.spring.DictAop;
import org.noear.snack.ONode;

import java.lang.reflect.InvocationTargetException;

public abstract class HandleChain implements DictHandler{


    private HandleChain nextHandleChain;

    public HandleChain getNextHandleChain() {
        return nextHandleChain;
    }

    public HandleChain setNextHandleChain(HandleChain nextHandleChain) {
        this.nextHandleChain = nextHandleChain;
        return this.nextHandleChain;
    }

    public void nextHandle(DictAop.DictHelper dictHelper, ONode data, DictService dictService) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        HandleChain chain = this.getNextHandleChain();
        if(chain!=null){
            chain.handle(dictHelper, data, dictService);
        }

    }
}
