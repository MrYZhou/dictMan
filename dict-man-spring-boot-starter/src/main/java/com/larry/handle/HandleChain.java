package com.larry.handle;

public class HandleChain {
    private HandleChain nextHandleChain;

    public HandleChain getNextHandleChain() {
        return nextHandleChain;
    }

    public void setNextHandleChain(HandleChain nextHandleChain) {
        this.nextHandleChain = nextHandleChain;
    }
}
