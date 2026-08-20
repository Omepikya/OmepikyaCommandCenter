package com.omepikya.commandcenter.planning;

import java.util.ArrayDeque;
import java.util.Deque;

/** Best-effort rollback stack. Actions may register an inverse operation. */
public class ActionRollback {
    public interface Operation { boolean run(); }
    private final Deque<Operation> stack=new ArrayDeque<>();
    public void push(Operation operation){if(operation!=null)stack.push(operation);}
    public boolean rollbackLast(){return !stack.isEmpty()&&Boolean.TRUE.equals(stack.pop().run());}
    public void rollbackAll(){while(!stack.isEmpty())rollbackLast();}
    public void clear(){stack.clear();}
}
